package slisenko.samples;

import java.util.concurrent.atomic.AtomicReference;

public class LockFreeStatsCollector {

    // Immutable class to change all values in one transaction
    private class Data {
        final double sum;
        final long count;
        final double min;
        final double max;

        private Data(double sum, long count, double min, double max) {
            this.sum = sum;
            this.count = count;
            this.min = min;
            this.max = max;
        }

        public Data add(double value) {
            return new Data(
                sum + value,
                count + 1,
                Math.min(value, min),
                Math.max(value, max)
            );
        }

        public double avg() {
            if (count > 0) {
                return sum / count;
            } else {
                return -1;
            }
        }
    }

    private final AtomicReference<Data> data = new AtomicReference<>(new Data(
        0.0, 0,  Double.MAX_VALUE, Double.MIN_VALUE
    ));

//    private double sum;
//    private long count;
//    private double min = Double.MAX_VALUE;
//    private double max = Double.MIN_VALUE;

    public void add(double value) {
        Data oldData = data.get();
        while (!data.compareAndSet(oldData, oldData.add(value))) {
            oldData = data.get();
        }
//
//        if (value < min) {
//            min = value;
//        }
//
//        if (value > max) {
//            max = value;
//        }
//
//        sum += value;
//        count++;
    }

    public double sum() {
        return data.get().sum;
    }

    public long count() {
        return data.get().count;
    }

    public double avg() {
        return data.get().avg();
    }

    public double min() {
        return data.get().min;
    }

    public double max() {
        return data.get().max;
    }
}
