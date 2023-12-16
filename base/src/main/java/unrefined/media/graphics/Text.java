package unrefined.media.graphics;

import unrefined.util.Copyable;
import unrefined.util.NotInstantiableError;
import unrefined.util.Resettable;
import unrefined.util.Swappable;

public final class Text {
    
    private Text() {
        throw new NotInstantiableError(Text.class);
    }

    public static class Metrics implements Copyable, Swappable, Resettable {

        private float baseline;
        private float ascent;
        private float descent;
        private float leading;
        private float maxAscent;
        private float maxDescent;
        private float advance;

        public Metrics() {
        }

        public Metrics(float baseline, float ascent, float descent, float leading, float maxAscent, float maxDescent, float advance) {
            this.baseline = baseline;
            this.ascent = ascent;
            this.descent = descent;
            this.leading = leading;
            this.maxAscent = maxAscent;
            this.maxDescent = maxDescent;
            this.advance = advance;
        }

        public Metrics(Metrics metrics) {
            this(metrics.getBaseline(), metrics.getAscent(), metrics.getDescent(), metrics.getLeading(),
                    metrics.getMaxAscent(), metrics.getMaxDescent(), metrics.getAdvance());
        }

        public float getBaseline() {
            return baseline;
        }

        public void setBaseline(float baseline) {
            this.baseline = baseline;
        }

        public float getAscent() {
            return ascent;
        }

        public void setAscent(float ascent) {
            this.ascent = ascent;
        }

        public float getDescent() {
            return descent;
        }

        public void setDescent(float descent) {
            this.descent = descent;
        }

        public float getLeading() {
            return leading;
        }

        public void setLeading(float leading) {
            this.leading = leading;
        }

        public float getMaxAscent() {
            return maxAscent;
        }

        public void setMaxAscent(float maxAscent) {
            this.maxAscent = maxAscent;
        }

        public float getMaxDescent() {
            return maxDescent;
        }

        public void setMaxDescent(float maxDescent) {
            this.maxDescent = maxDescent;
        }

        public float getAdvance() {
            return advance;
        }

        public void setAdvance(float advance) {
            this.advance = advance;
        }

        public void setMetrics(float baseline, float ascent, float descent, float leading, float maxAscent, float maxDescent, float advance) {
            this.baseline = baseline;
            this.ascent = ascent;
            this.descent = descent;
            this.leading = leading;
            this.maxAscent = maxAscent;
            this.maxDescent = maxDescent;
            this.advance = advance;
        }

        public void setMetrics(Metrics metrics) {
            setMetrics(metrics.getBaseline(), metrics.getAscent(), metrics.getDescent(), metrics.getLeading(),
                    metrics.getMaxAscent(), metrics.getMaxDescent(), metrics.getAdvance());
        }

        @Override
        public Metrics clone() {
            try {
                return (Metrics) super.clone();
            }
            catch (CloneNotSupportedException e) {
                return copy();
            }
        }

        @Override
        public Metrics copy() {
            return new Metrics(this);
        }

        @Override
        public void reset() {
            baseline = ascent = descent = leading = maxAscent = maxDescent = advance = 0;
        }

        @Override
        public boolean isIdentity() {
            return baseline == 0 && ascent == 0 && descent == 0 && leading == 0 && maxAscent == 0 && maxDescent == 0 && advance == 0;
        }

        @Override
        public void to(Object dst) {
            ((Metrics) dst).setMetrics(this);
        }

        @Override
        public void from(Object src) {
            setMetrics((Metrics) src);
        }

        @Override
        public void swap(Object o) {
            Metrics that = (Metrics) o;
            baseline = that.baseline;
            ascent = that.ascent;
            descent = that.descent;
            leading = that.leading;
            maxAscent = that.maxAscent;
            maxDescent = that.maxDescent;
            advance = that.advance;
            that.setMetrics(this);
            setMetrics(baseline, ascent, descent, leading, maxAscent, maxDescent, advance);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Metrics metrics = (Metrics) o;

            if (Float.compare(metrics.baseline, baseline) != 0) return false;
            if (Float.compare(metrics.ascent, ascent) != 0) return false;
            if (Float.compare(metrics.descent, descent) != 0) return false;
            if (Float.compare(metrics.leading, leading) != 0) return false;
            if (Float.compare(metrics.maxAscent, maxAscent) != 0) return false;
            if (Float.compare(metrics.maxDescent, maxDescent) != 0) return false;
            return Float.compare(metrics.advance, advance) == 0;
        }

        @Override
        public int hashCode() {
            int result = (baseline != +0.0f ? Float.floatToIntBits(baseline) : 0);
            result = 31 * result + (ascent != +0.0f ? Float.floatToIntBits(ascent) : 0);
            result = 31 * result + (descent != +0.0f ? Float.floatToIntBits(descent) : 0);
            result = 31 * result + (leading != +0.0f ? Float.floatToIntBits(leading) : 0);
            result = 31 * result + (maxAscent != +0.0f ? Float.floatToIntBits(maxAscent) : 0);
            result = 31 * result + (maxDescent != +0.0f ? Float.floatToIntBits(maxDescent) : 0);
            result = 31 * result + (advance != +0.0f ? Float.floatToIntBits(advance) : 0);
            return result;
        }

        @Override
        public String toString() {
            return getClass().getName()
                    + '{' +
                    "baseline=" + baseline +
                    ", ascent=" + ascent +
                    ", descent=" + descent +
                    ", leading=" + leading +
                    ", maxAscent=" + maxAscent +
                    ", maxDescent=" + maxDescent +
                    ", advance=" + advance +
                    '}';
        }

    }

    public static final class Direction {
        private Direction() {
            throw new NotInstantiableError(Direction.class);
        }
        public static final int AUTO = 0;
        public static final int LTR  = 1;
        public static final int RTL  = 2;
        public static int checkValid(int direction) {
            if (direction < AUTO || direction > RTL) throw new IllegalArgumentException("Illegal text direction: " + direction);
            else return direction;
        }
        public static boolean isValid(int direction) {
            return direction >= AUTO && direction <= RTL;
        }
        public static String toString(int direction) {
            switch (direction) {
                case AUTO: return "AUTO";
                case LTR: return "LTR";
                case RTL: return "RTL";
                default: throw new IllegalArgumentException("Illegal text direction: " + direction);
            }
        }
    }

    public static final class Alignment {
        private Alignment() {
            throw new NotInstantiableError(Alignment.class);
        }
        public static final int LEFT  = 0;
        public static final int CENTER = 1;
        public static final int RIGHT  = 2;

        public static int checkValid(int alignment) {
            if (alignment < LEFT || alignment > RIGHT) throw new IllegalArgumentException("Illegal text alignment: " + alignment);
            else return alignment;
        }
        public static boolean isValid(int align) {
            return align >= LEFT && align <= RIGHT;
        }
        public static String toString(int alignment) {
            switch (alignment) {
                case LEFT: return "LEFT";
                case CENTER: return "CENTER";
                case RIGHT: return "RIGHT";
                default: throw new IllegalArgumentException("Illegal text alignment: " + alignment);
            }
        }
    }

}