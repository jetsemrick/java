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
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.hertzbeat.common.entity.alerter.GroupAlert;

/**
 * Incident detail including timeline and raw group alerts for drill-down.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Schema(description = "Incident detail with timeline")
public class IncidentDetailDto extends IncidentSummaryDto {

    @Schema(description = "Ordered timeline built from group alarm timestamps")
    private List<IncidentTimelineEventDto> timeline;

    @Schema(description = "Underlying group alarms (includes nested single alerts when loaded)")
    private List<GroupAlert> groupAlerts;
}
