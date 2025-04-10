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
