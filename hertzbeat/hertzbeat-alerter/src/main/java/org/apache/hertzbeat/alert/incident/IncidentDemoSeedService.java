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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.hertzbeat.alert.dao.GroupAlertDao;
import org.apache.hertzbeat.alert.dao.SingleAlertDao;
import org.apache.hertzbeat.common.constants.CommonConstants;
import org.apache.hertzbeat.common.entity.alerter.GroupAlert;
import org.apache.hertzbeat.common.entity.alerter.SingleAlert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Deterministic demo data for the live Cursor incident command center.
 * Scenario: payment-service degradation in prod; separate staging disk warning.
 */
@Service
public class IncidentDemoSeedService {

    /** Prefix for synthetic single-alarm fingerprints (cleanup). */
    public static final String DEMO_FP_PREFIX = "demo-incident-fp-";

    /** Prefix for synthetic group keys (cleanup). */
    public static final String DEMO_GK_PREFIX = "demo-incident-";

    @Autowired
    private SingleAlertDao singleAlertDao;

    @Autowired
    private GroupAlertDao groupAlertDao;

    /**
     * Remove prior demo rows and insert the canonical payment-service scenario.
     */
    @Transactional(rollbackFor = Exception.class)
    public void seedDemo() {
        cleanup();
        long base = System.currentTimeMillis();
        long t1 = base - 240_000;
        long t2 = base - 180_000;
        long t3 = base - 120_000;
        long t4 = base - 60_000;

        // Prod: latency spike
        insertPair(
                DEMO_GK_PREFIX + "pay-prod-latency",
                DEMO_FP_PREFIX + "pay-prod-latency",
                t1,
                mapOf(
                        "alertname", "HighLatency",
                        "service", "payment-service",
                        "environment", "prod",
                        "severity", "critical"),
                mapOf("summary", "p95 latency exceeded 2s on checkout path"),
                "High latency on payment-service checkout");

        // Prod: error rate
        insertPair(
                DEMO_GK_PREFIX + "pay-prod-errors",
                DEMO_FP_PREFIX + "pay-prod-errors",
                t2,
                mapOf(
                        "alertname", "HighErrorRate",
                        "service", "payment-service",
                        "environment", "prod",
                        "severity", "critical"),
                mapOf("summary", "5xx rate above 5% for 5 minutes"),
                "Elevated 5xx errors on payment-service");

        // Prod: deployment (correlates with degradation for narrative)
        insertPair(
                DEMO_GK_PREFIX + "pay-prod-deploy",
                DEMO_FP_PREFIX + "pay-prod-deploy",
                t3,
                mapOf(
                        "alertname", "DeploymentRolledOut",
                        "service", "payment-service",
                        "environment", "prod",
                        "severity", "info"),
                mapOf("summary", "Version v2.4.1 rolled out to payment-service"),
                "Deployment rollout recorded for payment-service");

        // Staging: separate incident (noise if grouped by service only)
        insertPair(
                DEMO_GK_PREFIX + "pay-staging-disk",
                DEMO_FP_PREFIX + "pay-staging-disk",
                t4,
                mapOf(
                        "alertname", "DiskSpaceLow",
                        "service", "payment-service",
                        "environment", "staging",
                        "severity", "warning"),
                mapOf("summary", "Disk usage above 80% on staging node"),
                "Disk space warning on staging (non-prod)");
    }

    private void cleanup() {
        singleAlertDao.deleteByFingerprintStartingWith(DEMO_FP_PREFIX);
        groupAlertDao.deleteByGroupKeyStartingWith(DEMO_GK_PREFIX);
    }

    private void insertPair(
            String groupKey,
            String fingerprint,
            long startAt,
            Map<String, String> labels,
            Map<String, String> annotations,
            String content) {
        SingleAlert single = SingleAlert.builder()
                .fingerprint(fingerprint)
                .labels(labels)
                .annotations(annotations)
                .content(content)
                .status(CommonConstants.ALERT_STATUS_FIRING)
                .triggerTimes(1)
                .startAt(startAt)
                .activeAt(startAt)
                .build();
        singleAlertDao.save(single);

        GroupAlert group = GroupAlert.builder()
                .groupKey(groupKey)
                .status(CommonConstants.ALERT_STATUS_FIRING)
                .groupLabels(new HashMap<>(labels))
                .commonLabels(new HashMap<>(labels))
                .commonAnnotations(new HashMap<>(annotations))
                .alertFingerprints(List.of(fingerprint))
                .build();
        groupAlertDao.save(group);
    }

    private static Map<String, String> mapOf(String... kv) {
        Map<String, String> m = new HashMap<>();
        for (int i = 0; i + 1 < kv.length; i += 2) {
            m.put(kv[i], kv[i + 1]);
        }
        return m;
    }
}
