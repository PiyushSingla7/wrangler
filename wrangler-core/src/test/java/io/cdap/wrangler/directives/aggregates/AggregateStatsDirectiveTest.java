package io.cdap.wrangler.directives.aggregates;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import io.cdap.wrangler.TestingRig;
import io.cdap.wrangler.api.Row;

public class AggregateStatsDirectiveTest {

    @Test
    public void testAggregateStatsDirective() throws Exception {
        String[] recipe = {
            "aggregate-stats :data_transfer_size :response_time total_size_mb total_time_sec"
        };

        List<Row> rows = Arrays.asList(
            new Row().add("data_transfer_size", new ByteSize("10MB")).add("response_time", new TimeDuration("5s")),
            new Row().add("data_transfer_size", new ByteSize("5MB")).add("response_time", new TimeDuration("3s"))
        );

        List<Row> results = TestingRig.execute(recipe, rows);

        Assert.assertEquals(1, results.size());
        Assert.assertEquals(15.0, (Double) results.get(0).getValue("total_size_mb"), 0.001);
        Assert.assertEquals(8.0, (Double) results.get(0).getValue("total_time_sec"), 0.001);
    }

    // Simple mock of TimeDuration class for testing
    static class TimeDuration {
        private final double seconds;

        public TimeDuration(String input) {
            if (input.endsWith("s")) {
                seconds = Double.parseDouble(input.replace("s", ""));
            } else {
                throw new IllegalArgumentException("Unsupported duration format: " + input);
            }
        }

        public double getSeconds() {
            return seconds;
        }

        @Override
        public String toString() {
            return seconds + "s";
        }
    }

    // Simple mock of ByteSize class for testing
    static class ByteSize {
        private final double megabytes;

        public ByteSize(String input) {
            if (input.endsWith("MB")) {
                megabytes = Double.parseDouble(input.replace("MB", ""));
            } else {
                throw new IllegalArgumentException("Unsupported byte size format: " + input);
            }
        }

        public double getMegabytes() {
            return megabytes;
        }

        @Override
        public String toString() {
            return megabytes + "MB";
        }
    }
}
