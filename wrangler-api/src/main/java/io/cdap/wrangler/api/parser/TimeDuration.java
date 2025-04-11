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
 * the License.
 */

package io.cdap.wrangler.api.parser;

import com.google.gson.JsonObject;

/**
 * Represents a time duration value parsed from a directive argument.
 * This class provides methods to retrieve the value in nanoseconds and its original string representation.
 */
public final class TimeDuration implements Token {
    /**
     * The number of nanoseconds in a millisecond.
     */
    private static final long NANOSECONDS_IN_MILLISECOND = 1_000_000L;

    /**
     * The number of nanoseconds in a second.
     */
    private static final long NANOSECONDS_IN_SECOND = 1_000_000_000L;

    /**
     * The number of seconds in a minute.
     */
    private static final long SECONDS_IN_MINUTE = 60L;

    /**
     * The number of minutes in an hour.
     */
    private static final long MINUTES_IN_HOUR = 60L;

    /**
     * The number of hours in a day.
     */
    private static final long HOURS_IN_DAY = 24L;

    /**
     * The original string representation of the time duration value.
     */
    private final String originalValue;

    /**
     * The time duration value converted to nanoseconds.
     */
    private final long nanoseconds;

    /**
     * Constructs a new {@code TimeDuration} object by parsing the input string.
     *
     * @param value The input string representing the time duration (e.g., "5ms", "30s").
     */
    public TimeDuration(final String value) {
        this.originalValue = value;
        this.nanoseconds = parseNanoseconds(value);
    }

    /**
     * Parses the input string and converts it to nanoseconds.
     *
     * @param value The input string representing the time duration (e.g., "5ms", "30s").
     * @return The time duration value converted to nanoseconds.
     * @throws IllegalArgumentException If the unit in the input string is unsupported.
     */
    private long parseNanoseconds(final String value) {
        // Parse the numeric part of the input string.
        final double number = Double.parseDouble(value.replaceAll("[^0-9.]", ""));
        // Extract the unit part of the input string.
        final String unit = value.replaceAll("[0-9.]", "").toLowerCase();

        switch (unit) {
            case "ms":
                return (long) (number * NANOSECONDS_IN_MILLISECOND);
            case "s":
                return (long) (number * NANOSECONDS_IN_SECOND);
            case "m":
                return (long) (number * SECONDS_IN_MINUTE * NANOSECONDS_IN_SECOND);
            case "h":
                return (long) (number * MINUTES_IN_HOUR * SECONDS_IN_MINUTE * NANOSECONDS_IN_SECOND);
            case "d":
                return (long) (number * HOURS_IN_DAY * MINUTES_IN_HOUR * SECONDS_IN_MINUTE * NANOSECONDS_IN_SECOND);
            default:
                throw new IllegalArgumentException("Unsupported unit: " + unit);
        }
    }

    /**
     * Returns the time duration value in nanoseconds.
     *
     * @return The time duration value in nanoseconds.
     */
    public final long getNanoseconds() {
        return nanoseconds;
    }

    /**
     * Returns the original string representation of the time duration value.
     *
     * @return The original string representation of the time duration value.
     */
    @Override
    public final Object value() {
        return originalValue;
    }

    /**
     * Returns the type of this token.
     *
     * @return The token type, which is {@link TokenType#TIME_DURATION}.
     */
    @Override
    public final TokenType type() {
        return TokenType.TIME_DURATION;
    }

    /**
     * Converts this {@code TimeDuration} object to a JSON representation.
     *
     * @return A JSON object containing the type, original value, and nanoseconds.
     */
    @Override
    public final JsonObject toJson() {
        final JsonObject json = new JsonObject();
        json.addProperty("type", "TIME_DURATION");
        json.addProperty("value", originalValue);
        json.addProperty("nanoseconds", nanoseconds);
        return json;
    }
}
