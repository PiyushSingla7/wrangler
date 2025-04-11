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

package io.cdap.wrangler.api;

import java.security.SecureRandom;
import java.util.Random;

/**
 * This class <code>SUID</code> creates a unique 64-bit ID with the capacity to
 * generate around 65,536 unique IDs within a millisecond.
 */
public final class SUID {
    /**
     * The maximum value for the counter, which is 65,536 (2^16).
     */
    private static final int SHORT_MAX = 65536;

    /**
     * A counter used to generate unique IDs. Initialized to -1 to indicate it has not been initialized yet.
     */
    private static int counter = -1;

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private SUID() {
    }

    /**
     * Creates a unique 64-bit ID by aggregating the current time in milliseconds since epoch
     * (Jan. 1, 1970) and using a 16-bit counter. The counter is initialized at a random number.
     * This generator can create up to 65,536 different IDs per millisecond.
     *
     * @return A new unique 64-bit ID.
     */
    public static synchronized long nextId() {
        if (counter == -1) {
            final Random rnd = new SecureRandom();
            counter = rnd.nextInt(SHORT_MAX);
        }

        final long now = System.currentTimeMillis();
        final long id = (now << 16) | counter;
        counter = (counter + 1) % SHORT_MAX;
        return id;
    }
}
