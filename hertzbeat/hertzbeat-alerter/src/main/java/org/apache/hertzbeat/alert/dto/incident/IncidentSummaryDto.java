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

package org.apache.hertzbeat.alert.dto.incident;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Aggregated incident view for list cards.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Incident summary for command center")
public class IncidentSummaryDto {

    @Schema(description = "Stable incident id (URL-safe encoding of service|environment)")
    private String id;

    @Schema(description = "Logical service name from alert labels")
    private String service;

    @Schema(description = "Environment label, default prod when absent")
    private String environment;

    @Schema(description = "Aggregated severity: critical|high|medium|low")
    private String severity;

    @Schema(description = "Numeric score 1-100 for sorting and badges")
    private int severityScore;

    @Schema(description = "Short headline for the incident")
    private String headline;

    @Schema(description = "Heuristic probable root cause")
    private String probableCause;

    @Schema(description = "Confidence 0.0-1.0 for probable cause")
    private double probableCauseConfidence;

    @Schema(description = "Blast radius description (e.g. payments path)")
    private String blastRadius;

    @Schema(description = "Number of grouped alarms in this incident")
    private int alertCount;

    @Schema(description = "Recommended response steps")
    private List<String> recommendedResponse;
}
