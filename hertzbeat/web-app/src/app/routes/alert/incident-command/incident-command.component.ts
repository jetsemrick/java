/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { Component, OnInit } from '@angular/core';
import { NzMessageService } from 'ng-zorro-antd/message';

import { IncidentDetail, IncidentSummary } from '../../../pojo/IncidentSummary';
import { IncidentService } from '../../../service/incident.service';

@Component({
  selector: 'app-incident-command',
  templateUrl: './incident-command.component.html',
  styleUrl: './incident-command.component.less'
})
export class IncidentCommandComponent implements OnInit {
  loading = false;
  seeding = false;
  incidents: IncidentSummary[] = [];
  selected: IncidentDetail | null = null;
  detailLoading = false;

  constructor(
    private incidentSvc: IncidentService,
    private msg: NzMessageService
  ) {}

  ngOnInit(): void {
    this.refresh();
  }

  refresh(): void {
    this.loading = true;
    this.incidentSvc.loadIncidents().subscribe({
      next: body => {
        this.incidents = body.data ?? [];
        this.loading = false;
      },
      error: () => {
        this.loading = false;
        this.msg.error('Failed to load incidents');
      }
    });
  }

  seedDemo(): void {
    this.seeding = true;
    this.incidentSvc.seedDemo().subscribe({
      next: () => {
        this.seeding = false;
        this.msg.success('Demo data seeded');
        this.refresh();
      },
      error: () => {
        this.seeding = false;
        this.msg.error('Failed to seed demo');
      }
    });
  }

  selectIncident(inc: IncidentSummary): void {
    this.detailLoading = true;
    this.selected = null;
    this.incidentSvc.loadIncidentDetail(inc.id).subscribe({
      next: body => {
        this.selected = body.data ?? null;
        this.detailLoading = false;
      },
      error: () => {
        this.detailLoading = false;
        this.msg.error('Failed to load incident detail');
      }
    });
  }

  severityColor(sev: string): string {
    switch (sev) {
      case 'critical':
        return 'red';
      case 'high':
        return 'orange';
      case 'medium':
        return 'gold';
      default:
        return 'blue';
    }
  }
}
