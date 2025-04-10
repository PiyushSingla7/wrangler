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

public class ByteSize implements Token {
    private final String originalValue;
    private final long bytes;

    public ByteSize(String value) {
        this.originalValue = value;
        this.bytes = parseBytes(value);
    }

    private long parseBytes(String value) {
        // Parse the input string (e.g., "10KB", "1.5MB") and convert to bytes
        double number = Double.parseDouble(value.replaceAll("[^0-9.]", ""));
        String unit = value.replaceAll("[0-9.]", "").toUpperCase();

        switch (unit) {
            case "B": return (long) number;
            case "KB": return (long) (number * 1024);
            case "MB": return (long) (number * 1024 * 1024);
            case "GB": return (long) (number * 1024 * 1024 * 1024);
            case "TB": return (long) (number * 1024L * 1024L * 1024L * 1024L);
            default: throw new IllegalArgumentException("Unsupported unit: " + unit);
        }
    }

    public long getBytes() {
        return bytes;
    }

    @Override
    public Object value() {
        return originalValue; // Return the original string representation
    }

    @Override
    public TokenType type() {
        return TokenType.BYTE_SIZE;
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("type", "BYTE_SIZE");
        json.addProperty("value", originalValue);
        json.addProperty("bytes", bytes);
        return json;
    }
}