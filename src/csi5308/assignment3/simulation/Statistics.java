package csi5308.assignment3.simulation;

import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Optional;

public class Statistics {

    private List<Double> data;
    private DoubleSummaryStatistics stats;

    public Statistics(List<Double> data) {
        this.data = data;
        this.stats = this.data.stream().mapToDouble(d -> d).summaryStatistics();
    }

    List<Double> getData() {
        return this.data;
    }

    public Optional<Double> getVariance() {
        if (stats.getCount() == 0) {
            return Optional.empty();
        } else {
            return data.stream()
                    .reduce((s, n) -> s + Math.pow((n-stats.getAverage()), 2))
                    .map(s -> Math.sqrt(s/(stats.getCount()-1)));
        }
    }

    public double getStdDev() {
        return Math.sqrt(this.getVariance().orElse(0d));
    }

    @Override
    public String toString() {
        return "Count: " + this.stats.getCount() + "\n"
                + "Average: " + this.stats.getAverage() + "\n"
                + "StdDev: " + this.getStdDev() + "\n"
                + "Minimum: " + this.stats.getMin() + "\n"
                + "Maximum: " + this.stats.getMax() + "\n"
                + "Raw: " + this.stats.getSum();
    }
}
