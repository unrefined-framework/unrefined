/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package unrefined.util;

import unrefined.util.event.Event;
import unrefined.util.event.EventSlot;
import unrefined.util.signal.Signal;
import unrefined.util.signal.SignalSlot;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class SharedTimer {

    public static Builder build() {
        return new Builder();
    }

    public final static class Builder {
        private final SharedTimer timer;
        private Builder() {
            this.timer = new SharedTimer();
        }
        public Builder period(long period) {
            timer.setPeriod(period);
            return this;
        }
        public Builder delay(long delay) {
            timer.setDelay(delay);
            return this;
        }
        public Builder repeat(boolean repeat) {
            timer.setRepeat(repeat);
            return this;
        }
        public Builder coalesce(boolean coalesce) {
            timer.setCoalesce(coalesce);
            return this;
        }
        public Builder timeUnit(TimeUnit timeUnit) {
            timer.setTimeUnit(timeUnit);
            return this;
        }
        public Builder onPerform(SignalSlot<EventSlot<PerformEvent>> consumer) {
            consumer.accept(timer.onPerform());
            return this;
        }
        public SharedTimer start() {
            timer.start();
            return timer;
        }
        public SharedTimer unstarted() {
            return timer;
        }
    }

    private long period;
    private long delay;
    private boolean repeat;
    private boolean coalesce;
    private TimeUnit timeUnit;

    private ScheduledFuture<?> scheduledFuture;

    private static final ScheduledExecutorService EXECUTOR = Executors.newSingleThreadScheduledExecutor(runnable -> {
        Thread thread = new Thread(runnable, "SharedTimer");
        thread.setDaemon(true);
        return thread;
    });

    public SharedTimer() {
        this(0);
    }

    public SharedTimer(long delay) {
        this(delay, 0);
    }

    public SharedTimer(long delay, long period) {
        this(delay, period, true);
    }

    public SharedTimer(long delay, long period, boolean repeat) {
        this(delay, period, repeat, true);
    }

    public SharedTimer(long delay, long period, boolean repeat, boolean coalesce) {
        this(delay, period, repeat, coalesce, null);
    }

    public SharedTimer(long delay, long period, boolean repeat, boolean coalesce, TimeUnit timeUnit) {
        setDelay(delay);
        setPeriod(period);
        this.repeat = repeat;
        this.coalesce = coalesce;
        setTimeUnit(timeUnit);
    }

    private final Signal<EventSlot<PerformEvent>> onPerform = Signal.ofSlot();
    public Signal<EventSlot<PerformEvent>> onPerform() {
        return onPerform;
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }

    public boolean isRepeat() {
        return repeat;
    }

    public void setCoalesce(boolean coalesce) {
        this.coalesce = coalesce;
    }

    public boolean isCoalesce() {
        return coalesce;
    }

    public void setDelay(long delay) {
        if (delay < 0) throw new IllegalArgumentException("Negative delay: " + delay);
        this.delay = delay;
    }

    public long getDelay() {
        return delay;
    }

    public void setPeriod(long period) {
        if (period < 0) throw new IllegalArgumentException("Negative period: " + period);
        this.period = period;
    }

    public long getPeriod() {
        return period;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit == null ? TimeUnit.MILLISECONDS : timeUnit;
    }

    public void start() {
        if (isStarted()) scheduledFuture.cancel(false);
        scheduledFuture = EXECUTOR.scheduleAtFixedRate(new Runnable() {
            private int queuedCounter = 0;
            @Override
            public void run() {
                if (!coalesce || queuedCounter <= 0) {
                    queuedCounter ++;
                    onPerform().emit(new PerformEvent(SharedTimer.this, System.currentTimeMillis()));
                    if (repeat) queuedCounter --;
                    else stop();
                }
            }
        }, delay, period, timeUnit);
    }

    public void stop() {
        if (scheduledFuture != null && scheduledFuture.cancel(false)) {
            scheduledFuture = null;
        }
    }

    public boolean isStarted() {
        return scheduledFuture != null;
    }

    public static final class PerformEvent extends Event<SharedTimer> {

        private final long scheduledTime;

        public PerformEvent(SharedTimer source, long scheduledTime) {
            super(source);
            this.scheduledTime = scheduledTime;
        }

        public long getScheduledTime() {
            return scheduledTime;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;

            PerformEvent that = (PerformEvent) o;

            return scheduledTime == that.scheduledTime;
        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + (int) (scheduledTime ^ (scheduledTime >>> 32));
            return result;
        }

        @Override
        public String toString() {
            return getClass().getName()
                    + '{' +
                    "source=" + getSource() +
                    ", scheduledTime=" + scheduledTime +
                    '}';
        }

    }

}
