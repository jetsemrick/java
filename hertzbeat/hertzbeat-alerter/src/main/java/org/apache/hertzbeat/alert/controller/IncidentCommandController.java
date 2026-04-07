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

package org.apache.hertzbeat.alert.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.apache.hertzbeat.alert.dto.incident.IncidentDetailDto;
import org.apache.hertzbeat.alert.dto.incident.IncidentSummaryDto;
import org.apache.hertzbeat.alert.incident.IncidentAggregationService;
import org.apache.hertzbeat.common.entity.dto.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Incident Command Center API (aggregates existing group alarms into incidents).
 */
@Tag(name = "Incident Command Center API")
@RestController
@RequestMapping(path = "/api/incidents", produces = {APPLICATION_JSON_VALUE})
public class IncidentCommandController {

    @Autowired
    private IncidentAggregationService incidentAggregationService;

    @GetMapping
    @Operation(summary = "List active incidents derived from firing group alarms")
    public ResponseEntity<Message<List<IncidentSummaryDto>>> list() {
        return ResponseEntity.ok(Message.success(incidentAggregationService.listIncidents()));
    }

    @GetMapping("/{incidentId}")
    @Operation(summary = "Incident detail with timeline and underlying group alarms")
    public ResponseEntity<Message<IncidentDetailDto>> detail(
            @Parameter(description = "Incident id from list endpoint") @PathVariable String incidentId) {
        return ResponseEntity.ok(Message.success(incidentAggregationService.getIncident(incidentId)));
    }
}
