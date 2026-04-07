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

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * URL-safe encoding of composite incident keys.
 */
public final class IncidentIdCodec {

    private IncidentIdCodec() {
    }

    public static String encode(String service, String environment) {
        String raw = service + "|" + environment;
        return Base64.getUrlEncoder().withoutPadding().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }

    public static String[] decode(String id) {
        byte[] decoded = Base64.getUrlDecoder().decode(id);
        String raw = new String(decoded, StandardCharsets.UTF_8);
        int pipe = raw.indexOf('|');
        if (pipe < 0) {
            return new String[] { raw, "prod" };
        }
        return new String[] { raw.substring(0, pipe), raw.substring(pipe + 1) };
    }
}
