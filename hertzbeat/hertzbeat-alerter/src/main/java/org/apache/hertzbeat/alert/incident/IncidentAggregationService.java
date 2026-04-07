/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.hertzbeat.alert.incident;

import jakarta.persistence.criteria.Predicate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.hertzbeat.alert.dao.GroupAlertDao;
import org.apache.hertzbeat.alert.dao.SingleAlertDao;
import org.apache.hertzbeat.alert.dto.incident.IncidentDetailDto;
import org.apache.hertzbeat.alert.dto.incident.IncidentSummaryDto;
import org.apache.hertzbeat.alert.dto.incident.IncidentTimelineEventDto;
import org.apache.hertzbeat.common.constants.CommonConstants;
import org.apache.hertzbeat.common.entity.alerter.GroupAlert;
import org.apache.hertzbeat.common.entity.alerter.SingleAlert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Builds incident views from stored group alarms.
 */
@Service
@Transactional(readOnly = true)
public class IncidentAggregationService {

    @Autowired
    private GroupAlertDao groupAlertDao;

    @Autowired
    private SingleAlertDao singleAlertDao;

    public List<IncidentSummaryDto> listIncidents() {
        List<GroupAlert> firing = groupAlertDao.findAll(firingSpec());
        return buildSummaries(groupByServiceEnv(firing));
    }

    public IncidentDetailDto getIncident(String incidentId) {
        String[] parts = IncidentIdCodec.decode(incidentId);
        String service = parts[0];
        String env = parts[1];
        List<GroupAlert> firing = groupAlertDao.findAll(firingSpec());
        List<GroupAlert> mine = firing.stream()
                .filter(g -> service.equals(serviceFrom(g)) && env.equals(envFrom(g)))
                .collect(Collectors.toList());
        for (GroupAlert g : mine) {
            attachSingles(g);
        }
        IncidentSummaryDto summary = buildSummary(service, env, mine);
        IncidentDetailDto detail = new IncidentDetailDto();
        copySummary(summary, detail);
        detail.setTimeline(buildTimeline(mine));
        detail.setGroupAlerts(mine);
        return detail;
    }

    private void copySummary(IncidentSummaryDto from, IncidentSummaryDto to) {
        to.setId(from.getId());
        to.setService(from.getService());
        to.setEnvironment(from.getEnvironment());
        to.setSeverity(from.getSeverity());
        to.setSeverityScore(from.getSeverityScore());
        to.setHeadline(from.getHeadline());
        to.setProbableCause(from.getProbableCause());
        to.setProbableCauseConfidence(from.getProbableCauseConfidence());
        to.setBlastRadius(from.getBlastRadius());
        to.setAlertCount(from.getAlertCount());
        to.setRecommendedResponse(from.getRecommendedResponse());
    }

    private Specification<GroupAlert> firingSpec() {
        return (root, query, cb) -> {
            Predicate status = cb.equal(root.get("status"), CommonConstants.ALERT_STATUS_FIRING);
            return query.where(status).getRestriction();
        };
    }

    private Map<String, List<GroupAlert>> groupByServiceEnv(List<GroupAlert> alerts) {
        Map<String, List<GroupAlert>> map = new HashMap<>();
        for (GroupAlert g : alerts) {
            String s = serviceFrom(g);
            String e = envFrom(g);
            String key = s + "|" + e;
            map.computeIfAbsent(key, k -> new ArrayList<>()).add(g);
        }
        return map;
    }

    private List<IncidentSummaryDto> buildSummaries(Map<String, List<GroupAlert>> grouped) {
        List<IncidentSummaryDto> out = new ArrayList<>();
        for (Map.Entry<String, List<GroupAlert>> e : grouped.entrySet()) {
            String[] parts = e.getKey().split("\\|", 2);
            String service = parts[0];
            String environment = parts.length > 1 ? parts[1] : "prod";
            out.add(buildSummary(service, environment, e.getValue()));
        }
        out.sort(Comparator.comparingInt(IncidentSummaryDto::getSeverityScore).reversed());
        return out;
    }

    private IncidentSummaryDto buildSummary(String service, String environment, List<GroupAlert> groups) {
        boolean hasDeploy = groups.stream().anyMatch(g -> IncidentHeuristics.matchesAlertName(g, "deploy"));
        boolean hasLatency = groups.stream().anyMatch(g -> IncidentHeuristics.matchesAlertName(g, "latency"));
        boolean hasErrors = groups.stream().anyMatch(g -> IncidentHeuristics.matchesAlertName(g, "error"));
        int rank = IncidentHeuristics.worstSeverityRank(groups);
        String severity = IncidentHeuristics.severityLabelFromRank(rank);
        int score = IncidentHeuristics.severityScoreFromRank(rank, groups.size());
        double conf = IncidentHeuristics.probableCauseConfidence(groups, hasDeploy, hasLatency || hasErrors);
        String cause = IncidentHeuristics.probableCause(hasDeploy, hasLatency, hasErrors);
        String headline = String.format(Locale.ROOT, "%s — %d active signal(s) in %s", service, groups.size(), environment);
        return IncidentSummaryDto.builder()
                .id(IncidentIdCodec.encode(service, environment))
                .service(service)
                .environment(environment)
                .severity(severity)
                .severityScore(score)
                .headline(headline)
                .probableCause(cause)
                .probableCauseConfidence(conf)
                .blastRadius(IncidentHeuristics.blastRadius(service))
                .alertCount(groups.size())
                .recommendedResponse(IncidentHeuristics.recommendedResponse(rank >= 4, hasDeploy))
                .build();
    }

    private List<IncidentTimelineEventDto> buildTimeline(List<GroupAlert> groups) {
        List<GroupAlert> sorted = new ArrayList<>(groups);
        sorted.sort(Comparator.comparing(this::timeMsForGroup));
        List<IncidentTimelineEventDto> events = new ArrayList<>();
        for (GroupAlert g : sorted) {
            String title = IncidentHeuristics.label(g.getCommonLabels(), "alertname",
                    IncidentHeuristics.label(g.getGroupLabels(), "alertname", "alarm"));
            String detail = "";
            if (g.getCommonAnnotations() != null && g.getCommonAnnotations().get("summary") != null) {
                detail = g.getCommonAnnotations().get("summary");
            }
            events.add(IncidentTimelineEventDto.builder()
                    .timestampMs(timeMsForGroup(g))
                    .title(title)
                    .detail(detail != null ? detail : "")
                    .groupAlertId(g.getId())
                    .build());
        }
        return events;
    }

    private long timeMsForGroup(GroupAlert g) {
        if (g.getAlerts() != null) {
            for (SingleAlert s : g.getAlerts()) {
                if (s.getStartAt() != null) {
                    return s.getStartAt();
                }
            }
        }
        if (g.getGmtCreate() != null) {
            return g.getGmtCreate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        }
        return System.currentTimeMillis();
    }

    private void attachSingles(GroupAlert g) {
        List<String> fps = g.getAlertFingerprints();
        if (fps == null || fps.isEmpty()) {
            g.setAlerts(List.of());
            return;
        }
        List<SingleAlert> singles = singleAlertDao.findSingleAlertsByFingerprintIn(fps);
        g.setAlerts(singles);
    }

    private String serviceFrom(GroupAlert g) {
        String s = IncidentHeuristics.label(g.getCommonLabels(), "service", null);
        if (s == null) {
            s = IncidentHeuristics.label(g.getGroupLabels(), "service", "unknown");
        }
        return s;
    }

    private String envFrom(GroupAlert g) {
        String e = IncidentHeuristics.label(g.getCommonLabels(), "environment", null);
        if (e == null) {
            e = IncidentHeuristics.label(g.getGroupLabels(), "environment", "prod");
        }
        return e;
    }
}
