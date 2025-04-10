/*
 * Copyright © 2017-2019 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 */
package io.cdap.wrangler.api.parser;

import com.google.gson.JsonObject;

public class TimeDuration implements Token {
    private final String originalValue;
    private final long nanoseconds;

    public TimeDuration(String value) {
        this.originalValue = value;
        this.nanoseconds = parseNanoseconds(value);
    }

    private long parseNanoseconds(String value) {
        // Parse the input string (e.g., "5ms", "30s") and convert to nanoseconds
        double number = Double.parseDouble(value.replaceAll("[^0-9.]", ""));
        String unit = value.replaceAll("[0-9.]", "").toLowerCase();

        switch (unit) {
            case "ms": return (long) (number * 1_000_000);
            case "s": return (long) (number * 1_000_000_000);
            case "m": return (long) (number * 60 * 1_000_000_000);
            case "h": return (long) (number * 60 * 60 * 1_000_000_000);
            case "d": return (long) (number * 24 * 60 * 60 * 1_000_000_000);
            default: throw new IllegalArgumentException("Unsupported unit: " + unit);
        }
    }

    public long getNanoseconds() {
        return nanoseconds;
    }

    @Override
    public Object value() {
        return originalValue; // Return the original string representation
    }

    @Override
    public TokenType type() {
        return TokenType.TIME_DURATION;
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("type", "TIME_DURATION");
        json.addProperty("value", originalValue);
        json.addProperty("nanoseconds", nanoseconds);
        return json;
    }
}