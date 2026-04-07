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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import org.apache.hertzbeat.common.entity.alerter.GroupAlert;

/**
 * Rule-based severity and narrative for incidents (no ML).
 */
public final class IncidentHeuristics {

    private IncidentHeuristics() {
    }

    public static String label(Map<String, String> labels, String key, String defaultValue) {
        if (labels == null || key == null) {
            return defaultValue;
        }
        return Objects.requireNonNullElse(labels.get(key), defaultValue);
    }

    /**
     * Rank severities across all group alerts. Uses max rank (worst wins).
     */
    public static int worstSeverityRank(List<GroupAlert> groups) {
        int worst = 0;
        for (GroupAlert g : groups) {
            int r = rankOneGroup(g);
            worst = Math.max(worst, r);
        }
        return worst;
    }

    public static int rankOneGroup(GroupAlert g) {
        Map<String, String> common = g.getCommonLabels();
        if (common != null) {
            String sev = common.get("severity");
            if (sev != null) {
                return rankFromLabel(sev);
            }
        }
        Map<String, String> groupLabels = g.getGroupLabels();
        if (groupLabels != null) {
            String sev = groupLabels.get("severity");
            if (sev != null) {
                return rankFromLabel(sev);
            }
        }
        return 2;
    }

    public static int rankFromLabel(String severity) {
        if (severity == null) {
            return 2;
        }
        String s = severity.toLowerCase(Locale.ROOT).trim();
        return switch (s) {
            case "critical", "emergency" -> 4;
            case "warning", "warn" -> 3;
            case "medium" -> 2;
            case "info", "informational", "none" -> 1;
            default -> 2;
        };
    }

    public static String severityLabelFromRank(int rank) {
        if (rank >= 4) {
            return "critical";
        }
        if (rank == 3) {
            return "high";
        }
        if (rank == 2) {
            return "medium";
        }
        return "low";
    }

    public static int severityScoreFromRank(int rank, int alertCount) {
        int base = switch (rank) {
            case 4 -> 95;
            case 3 -> 75;
            case 2 -> 50;
            default -> 30;
        };
        return Math.min(100, base + Math.min(10, alertCount * 2));
    }

    public static double probableCauseConfidence(List<GroupAlert> groups, boolean hasDeploy, boolean hasSloBreach) {
        double c = 0.45;
        if (hasSloBreach) {
            c += 0.25;
        }
        if (hasDeploy) {
            c += 0.2;
        }
        if (groups.size() >= 3) {
            c += 0.1;
        }
        return Math.min(0.95, c);
    }

    public static String probableCause(boolean hasDeploy, boolean hasLatency, boolean hasErrors) {
        if (hasDeploy && (hasLatency || hasErrors)) {
            return "Correlated service degradation with a recent deployment; investigate rollout and dependency health.";
        }
        if (hasLatency && hasErrors) {
            return "Elevated latency and errors suggest overload, dependency failure, or resource exhaustion.";
        }
        if (hasErrors) {
            return "Error rate elevated; check downstream dependencies and recent configuration changes.";
        }
        if (hasLatency) {
            return "Latency elevated; check saturation, GC, pools, and upstream timeouts.";
        }
        if (hasDeploy) {
            return "Deployment signal present; validate canary health and rollback criteria.";
        }
        return "Review grouped alarms and recent changes for this service.";
    }

    public static String blastRadius(String service) {
        if (service == null || service.isEmpty()) {
            return "Unknown blast radius";
        }
        return "User-facing traffic for " + service;
    }

    public static List<String> recommendedResponse(boolean critical, boolean hasDeploy) {
        List<String> steps = new ArrayList<>();
        steps.add("Confirm scope in dashboards and traces for the affected service.");
        if (critical) {
            steps.add("Page or escalate on-call for the owning team.");
        }
        if (hasDeploy) {
            steps.add("Check deployment timeline, diffs, and feature flags tied to this release.");
            steps.add("Prepare rollback or traffic shift if health checks fail.");
        } else {
            steps.add("Check autoscaling limits, quotas, and dependency SLOs.");
        }
        steps.add("Post a short status update with customer impact and ETA.");
        return steps;
    }

    public static boolean matchesAlertName(GroupAlert g, String fragment) {
        Map<String, String> common = g.getCommonLabels();
        if (common != null) {
            String name = common.get("alertname");
            if (name != null && name.toLowerCase(Locale.ROOT).contains(fragment)) {
                return true;
            }
        }
        Map<String, String> gl = g.getGroupLabels();
        if (gl != null) {
            String name = gl.get("alertname");
            return name != null && name.toLowerCase(Locale.ROOT).contains(fragment);
        }
        return false;
    }
}
