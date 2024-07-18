package unrefined.media.graphics;

import unrefined.util.Cacheable;
import unrefined.util.NotInstantiableError;
import unrefined.util.Objects;
import unrefined.util.PhantomString;
import unrefined.util.function.Assert;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class Text implements CharSequence, Cloneable {

    private final PhantomString string;
    private final List<SpanMark> spanMarks = new CopyOnWriteArrayList<>();

    public Text(char[] array, int offset, int length) {
        this.string = new PhantomString(array, offset, length);
    }

    public Text(char[] array) {
        this.string = new PhantomString(array);
    }

    public Text(CharSequence sequence, int offset, int length) {
        this.string = new PhantomString(sequence, offset, length);
    }

    public Text(CharSequence sequence) {
        this.string = new PhantomString(sequence);
    }

    public Text(char[] array, int offset, int length, List<SpanMark> spanMarks) {
        this.string = new PhantomString(array, offset, length);
        if (spanMarks != null) this.spanMarks.addAll(spanMarks);
    }

    public Text(char[] array, List<SpanMark> spanMarks) {
        this.string = new PhantomString(array);
        if (spanMarks != null) this.spanMarks.addAll(spanMarks);
    }

    public Text(CharSequence sequence, int offset, int length, List<SpanMark> spanMarks) {
        this.string = new PhantomString(sequence, offset, length);
        if (spanMarks != null) this.spanMarks.addAll(spanMarks);
    }

    public Text(CharSequence sequence, List<SpanMark> spanMarks) {
        this.string = new PhantomString(sequence);
        if (spanMarks != null) this.spanMarks.addAll(spanMarks);
    }

    public List<SpanMark> spanMarks() {
        return spanMarks;
    }

    @Override
    public int length() {
        return string.length();
    }

    @Override
    public char charAt(int index) {
        return string.charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return new Text(string, start, end);
    }

    @Override
    public Text clone() {
        Text clone;
        try {
            clone = (Text) super.clone();
        }
        catch (CloneNotSupportedException e) {
            clone = new Text(string);
        }
        clone.spanMarks().addAll(spanMarks);
        return clone;
    }

    public static class SpanMark {

        private final int start, end;
        private final Map<Attribute, Object> attributeMap;

        public SpanMark(int start, int end, Map<Attribute, Object> attributes) {
            this.start = start;
            this.end = end;
            this.attributeMap = Collections.unmodifiableMap(new HashMap<>(attributes));
        }

        public int getStart() {
            return start;
        }

        public int getEnd() {
            return end;
        }

        public Map<Attribute, Object> getAttributes() {
            return attributeMap;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            SpanMark spanMark = (SpanMark) o;
            return start == spanMark.start && end == spanMark.end && attributeMap.equals(spanMark.attributeMap);
        }

        @Override
        public int hashCode() {
            int result = start;
            result = 31 * result + end;
            result = 31 * result + attributeMap.hashCode();
            return result;
        }

        @Override
        public String toString() {
            return getClass().getName()
                    + '{' +
                    "start=" + start +
                    ", end=" + end +
                    '}';
        }

    }

    public enum Attribute implements unrefined.beans.Attribute {

        BRUSH(Font.class),
        //STYLE(Integer.class),
        //STROKE_WIDTH(Float.class),
        FONT(Font.class),
        BACKGROUND(Brush.class),
        FOREGROUND(Brush.class),
        SIZE(Float.class),
        DIRECTION(Integer.class),
        //ALIGNMENT(Integer.class),
        //SUBPIXEL(Boolean.class),
        UNDERLINE(Boolean.class),
        STRIKETHROUGH(Boolean.class),
        KERNING(Boolean.class),
        VARIANT_LIGATURES(Boolean.class),
        SUPERSCRIPT(Integer.class),
        LETTER_SPACING(Float.class),
        SCALE_X(Float.class),
        SCALE_Y(Float.class),
        SKEW_X(Float.class),
        SKEW_Y(Float.class),
        //SHADOW_BLUR(Float.class),
        //SHADOW_OFFSET_X(Float.class),
        //SHADOW_OFFSET_Y(Float.class),
        //SHADOW_COLOR(Integer.class),

        REPLACEMENT(Bitmap.class),
        REPLACEMENT_BASELINE(Integer.class);

        private final Class<?> type;
        private final Assert<Object> predicate;
        Attribute(Class<?> type) {
            this.type = Objects.requireNonNull(type);
            this.predicate = null;
        }
        Attribute(Class<?> type, Assert<Object> predicate) {
            this.type = Objects.requireNonNull(type);
            this.predicate = predicate;
        }
        @Override
        public Class<?> getType() {
            return type;
        }
        @Override
        public boolean isValid(Object value) {
            return predicate == null ? unrefined.beans.Attribute.super.isValid(value) : predicate.test(value);
        }
    }

    public static class Metrics implements Cacheable {

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

    public static class HitInfo implements Cacheable {

        private int index;
        private int insertion;
        private boolean leadingEdge = true;

        public HitInfo(int index, int insertion, boolean leadingEdge) {
            this.index = index;
            this.insertion = insertion;
            this.leadingEdge = leadingEdge;
        }

        public HitInfo(HitInfo info) {
            setHitInfo(info.getIndex(), info.getInsertion(), info.isLeadingEdge());
        }

        public HitInfo() {
        }

        public void setHitInfo(int index, int insertion, boolean edge) {
            this.index = index;
            this.insertion = insertion;
            this.leadingEdge = edge;
        }

        public void setHitInfo(HitInfo info) {
            setHitInfo(info.getIndex(), info.getInsertion(), info.isLeadingEdge());
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public int getInsertion() {
            return insertion;
        }

        public void setInsertion(int insertion) {
            this.insertion = insertion;
        }

        public boolean isLeadingEdge() {
            return leadingEdge;
        }

        public void setLeadingEdge(boolean leadingEdge) {
            this.leadingEdge = leadingEdge;
        }

        @Override
        public void to(Object dst) {
            ((HitInfo) dst).setHitInfo(this);
        }

        @Override
        public void from(Object src) {
            setHitInfo((HitInfo) src);
        }

        @Override
        public void swap(Object o) {
            HitInfo that = (HitInfo) o;
            int index = that.index;
            int insertion = that.insertion;
            boolean leadingEdge = that.leadingEdge;
            that.setHitInfo(this);
            setHitInfo(index, insertion, leadingEdge);
        }

        @Override
        public HitInfo clone() {
            try {
                return (HitInfo) super.clone();
            }
            catch (CloneNotSupportedException e) {
                return copy();
            }
        }

        @Override
        public HitInfo copy() {
            return new HitInfo(this);
        }

        @Override
        public void reset() {
            index = insertion = 0;
            leadingEdge = true;
        }

        @Override
        public boolean isIdentity() {
            return index == 0 && insertion == 0 && leadingEdge;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            HitInfo hitInfo = (HitInfo) o;

            if (index != hitInfo.index) return false;
            if (insertion != hitInfo.insertion) return false;
            return leadingEdge == hitInfo.leadingEdge;
        }

        @Override
        public int hashCode() {
            int result = index;
            result = 31 * result + insertion;
            result = 31 * result + (leadingEdge ? 1 : 0);
            return result;
        }

        @Override
        public String toString() {
            return getClass().getName()
                    + '{' +
                    "index=" + index +
                    ", insertion=" + insertion +
                    ", leadingEdge=" + leadingEdge +
                    '}';
        }

    }

    public static final class Baseline {
        private Baseline() {
            throw new NotInstantiableError(Baseline.class);
        }
        public static final int ALPHABETIC  = 0;
        public static final int IDEOGRAPHIC = 1;
        public static final int HANGING     = 2;
        public static final int BOTTOM      = 3;
        public static final int CENTER      = 4;
        public static final int TOP         = 5;
        public static int checkValid(int baseline) {
            if (baseline < ALPHABETIC || baseline > TOP) throw new IllegalArgumentException("Illegal text baseline: " + baseline);
            else return baseline;
        }
        public static boolean isValid(int baseline) {
            return baseline >= ALPHABETIC && baseline <= TOP;
        }
        public static String toString(int baseline) {
            switch (baseline) {
                case ALPHABETIC: return "ALPHABETIC";
                case IDEOGRAPHIC: return "IDEOGRAPHIC";
                case HANGING: return "HANGING";
                case BOTTOM: return "BOTTOM";
                case CENTER: return "CENTER";
                case TOP: return "TOP";
                default: throw new IllegalArgumentException("Illegal text baseline: " + baseline);
            }
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
        public static final int START  = 0;
        public static final int MIDDLE = 1;
        public static final int END    = 2;

        public static int checkValid(int alignment) {
            if (alignment < START || alignment > END) throw new IllegalArgumentException("Illegal text alignment: " + alignment);
            else return alignment;
        }
        public static boolean isValid(int alignment) {
            return alignment >= START && alignment <= END;
        }
        public static String toString(int alignment) {
            switch (alignment) {
                case START: return "START";
                case MIDDLE: return "MIDDLE";
                case END: return "END";
                default: throw new IllegalArgumentException("Illegal text alignment: " + alignment);
            }
        }
    }

    public static final class Superscript {
        private Superscript() {
            throw new NotInstantiableError(Alignment.class);
        }
        public static final int SUB  = -1;
        public static final int NONE = 0;
        public static final int SUP  = 1;

        public static int checkValid(int superscript) {
            if (superscript < SUB || superscript > SUP) throw new IllegalArgumentException("Illegal text superscript: " + superscript);
            else return superscript;
        }
        public static boolean isValid(int superscript) {
            return superscript >= SUB && superscript <= SUP;
        }
        public static String toString(int superscript) {
            switch (superscript) {
                case SUB: return "SUB";
                case NONE: return "NONE";
                case SUP: return "SUP";
                default: throw new IllegalArgumentException("Illegal text superscript: " + superscript);
            }
        }
    }

    @Override
    public String toString() {
        return string.toString();
    }

}
