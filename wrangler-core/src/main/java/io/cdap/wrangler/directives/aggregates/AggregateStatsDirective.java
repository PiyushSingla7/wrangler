/*
 *  Copyright © 2019 Cask Data, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not
 *  use this file except in compliance with the License. You may obtain a copy of
 *  the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  License for the specific language governing permissions and limitations under
 *  the License.
 */

package io.cdap.wrangler.directives.aggregates;

import java.util.List;

import io.cdap.wrangler.api.Arguments;
import io.cdap.wrangler.api.Directive;
import io.cdap.wrangler.api.ExecutorContext;
import io.cdap.wrangler.api.Row;
import io.cdap.wrangler.api.parser.ByteSize;
import io.cdap.wrangler.api.parser.TimeDuration;
import io.cdap.wrangler.api.parser.UsageDefinition;

/**
 * Aggregates byte sizes and time durations from input rows.
 *
 * Example usage:
 * aggregate-stats sizeColumn timeColumn totalSizeColumn totalTimeColumn
 */
public class AggregateStatsDirective implements Directive {

    private String sizeColumn;
    private String timeColumn;
    private String totalSizeColumn;
    private String totalTimeColumn;

    private long totalBytes = 0;
    private long totalNanoseconds = 0;

    @Override
    public void initialize(Arguments args) {
        sizeColumn = args.value("sizeColumn");
        timeColumn = args.value("timeColumn");
        totalSizeColumn = args.value("totalSizeColumn");
        totalTimeColumn = args.value("totalTimeColumn");
    }

    @Override
    public List<Row> execute(List<Row> rows, ExecutorContext context) {
        for (Row row : rows) {
            Object sizeObj = row.getValue(sizeColumn);
            Object timeObj = row.getValue(timeColumn);

            if (sizeObj instanceof ByteSize) {
                totalBytes += ((ByteSize) sizeObj).getBytes();
            }

            if (timeObj instanceof TimeDuration) {
                totalNanoseconds += ((TimeDuration) timeObj).getNanoseconds();
            }

            // Optional: add running totals to each row
            row.add(totalSizeColumn, totalBytes / (1024.0 * 1024)); // MB
            row.add(totalTimeColumn, totalNanoseconds / 1_000_000_000.0); // seconds
        }

        return rows;
    }

    @Override
    public void destroy() {
        // No cleanup needed
    }

    @Override
    public UsageDefinition define() {
        return null; // Skip defining usage if unsupported in your version
    }
}
