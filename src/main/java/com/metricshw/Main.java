package com.metricshw;

import com.codahale.metrics.*;
import lombok.Data;
import lombok.Getter;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {

        final Report report  = new Report();

        try {
            Random r = new Random();
            report.startTimer();
            int num = r.nextInt(4)+1;
            while (num + report.getSum() > 0)
            {
                report.getCounter().inc();
                report.getHistogram().update(num);
                report.add(num);
                num = r.nextInt(4)+1;
            }
        } catch (Exception ex) {
            System.out.println();
        }
        finally {
            report.stopTimer();
        }
    }

    private static class Report {
        Report(){
            sum = 0;
            MetricRegistry metrics = new MetricRegistry();
            counter = metrics.counter("report.counter");
            histogram = metrics.histogram("report.histogram");
            timer = metrics.timer("report.timer");
            metrics.register(MetricRegistry.name(Report.class, "report.sum"), (Gauge<Integer>) () -> getSum());
            context = null;
            initReporters(metrics);
        }

        private void initReporters(MetricRegistry metrics) {
            initConsoleReporter(metrics);
            initJmxReporter(metrics);
        }

        private void initConsoleReporter(MetricRegistry metrics) {
            final ConsoleReporter reporter = ConsoleReporter.forRegistry(metrics)
                    .build();
            reporter.start(1, TimeUnit.SECONDS);
        }

        private void initJmxReporter(MetricRegistry metrics) {
            final JmxReporter reporter = JmxReporter.forRegistry(metrics).build();
            reporter.start();
        }

        public void add(Integer value)
        {
            sum+=value;
        }

        @Getter
        private Integer sum;
        @Getter
        private Counter counter;
        @Getter
        private Histogram histogram;
        private Timer timer;
        private Timer.Context context;

        public void startTimer() {
            if (context != null)
                timer.time();
        }

        public void stopTimer(){
            if (context!= null)
                context.stop();
            context = null;
        }

    }
}
