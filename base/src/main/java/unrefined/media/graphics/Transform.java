package unrefined.media.graphics;

import unrefined.io.Portable;
import unrefined.util.Copyable;
import unrefined.io.Disposable;
import unrefined.util.NotInstantiableError;
import unrefined.util.Resettable;
import unrefined.util.Swappable;

import static unrefined.media.graphics.Transform.Index.*;

public abstract class Transform implements Portable, Disposable, Copyable, Swappable, Resettable {

    public static final class Index {
        private Index() {
            throw new NotInstantiableError(Index.class);
        }
        public static final int SCALE_X = 0;
        public static final int SKEW_X = 1;
        public static final int TRANSLATE_X = 2;
        public static final int SKEW_Y = 3;
        public static final int SCALE_Y = 4;
        public static final int TRANSLATE_Y = 5;
        public static boolean isValid(int index) {
            return index >= SCALE_X && index <= TRANSLATE_Y;
        }
        public static int checkValid(int index) {
            if (index < SCALE_X || index > TRANSLATE_Y) throw new IndexOutOfBoundsException("Index out of range: " + index);
            else return index;
        }
        public static String toString(int index) {
            switch (index) {
                case SCALE_X: return "SCALE_X";
                case SKEW_X: return "SKEW_X";
                case TRANSLATE_X: return "TRANSLATE_X";
                case SKEW_Y: return "SKEW_Y";
                case SCALE_Y: return "SCALE_Y";
                case TRANSLATE_Y: return "TRANSLATE_Y";
                default: throw new IndexOutOfBoundsException("Index out of range: " + index);
            }
        }
    }

    public void setTransform(float sx, float kx, float dx, float ky, float sy, float dy) {
        setScale(sx, sy);
        setSkew(kx, ky);
        setTranslate(dx, dy);
    }

    public abstract void setTransform(Transform transform);
    
    public void preTransform(float sx, float kx, float dx, float ky, float sy, float dy) {
        preScale(sx, sy);
        preSkew(kx, ky);
        preTranslate(dx, dy);
    }
    
    public void rTransform(float sx, float kx, float dx, float ky, float sy, float dy) {
        rScale(sx, sy);
        rSkew(kx, ky);
        rTranslate(dx, dy);
    }
    
    public void postTransform(float sx, float kx, float dx, float ky, float sy, float dy) {
        postScale(sx, sy);
        postSkew(kx, ky);
        postTranslate(dx, dy);
    }

    public void setValues(float... values) {
        setTransform(values[SCALE_X], values[SKEW_X], values[TRANSLATE_X], values[SKEW_Y], values[SCALE_Y], values[TRANSLATE_Y]);
    }
    
    public void preValues(float... values) {
        preTransform(values[SCALE_X], values[SKEW_X], values[TRANSLATE_X], values[SKEW_Y], values[SCALE_Y], values[TRANSLATE_Y]);
    }
    
    public void rValues(float... values) {
        rTransform(values[SCALE_X], values[SKEW_X], values[TRANSLATE_X], values[SKEW_Y], values[SCALE_Y], values[TRANSLATE_Y]);
    }
    
    public void postValues(float... values) {
        postTransform(values[SCALE_X], values[SKEW_X], values[TRANSLATE_X], values[SKEW_Y], values[SCALE_Y], values[TRANSLATE_Y]);
    }

    public abstract boolean invert();

    public abstract void concatenate(Transform a, Transform b);
    public abstract void preConcatenate(Transform other);
    public abstract void postConcatenate(Transform other);

    public abstract void setScale(float sx, float sy);
    public abstract void preScale(float sx, float sy);
    public abstract void rScale(float sx, float sy);
    public abstract void postScale(float sx, float sy);
    
    public abstract void setSkew(float kx, float ky);
    public abstract void preSkew(float kx, float ky);
    public abstract void rSkew(float kx, float ky);
    public abstract void postSkew(float kx, float ky);

    public abstract void setTranslate(float dx, float dy);
    public abstract void preTranslate(float dx, float dy);
    public abstract void rTranslate(float dx, float dy);
    public abstract void postTranslate(float dx, float dy);

    public abstract void setRotate(float degrees, float px, float py);
    public abstract void setRotate(float degrees);
    public abstract void preRotate(float degrees, float px, float py);
    public abstract void preRotate(float degrees);
    public abstract void rRotate(float degrees, float px, float py);
    public abstract void rRotate(float degrees);
    public abstract void postRotate(float degrees, float px, float py);
    public abstract void postRotate(float degrees);

    public abstract void getValues(float[] values, int offset);
    public void getValues(float[] values) {
        getValues(values, 0);
    }
    public abstract void getScale(float[] scale, int offset);
    public void getScale(float[] scale) {
        getScale(scale, 0);
    }
    public abstract void getSkew(float[] skew, int offset);
    public void getSkew(float[] skew) {
        getSkew(skew, 0);
    }
    public abstract void getTranslate(float[] translate, int offset);
    public void getTranslate(float[] translate) {
        getTranslate(translate, 0);
    }

    @Override
    public Transform clone() {
        try {
            return (Transform) super.clone();
        }
        catch (CloneNotSupportedException e) {
            return null;
        }
    }

    @Override
    public abstract Transform copy();

    @Override
    public String toString() {
        if (isDisposed()) return getClass().getName() + "@" + Integer.toHexString(hashCode())
                + '{' +
                "disposed=true" +
                '}';
        else {
            StringBuilder builder = new StringBuilder();
            float[] values = new float[6];
            getValues(values);
            builder.append("[[").append(values[0]).append(", ").append(values[1]).append(", ").append(values[2]).append("], ");
            builder.append('[').append(values[3]).append(", ").append(values[4]).append(", ").append(values[5]).append("]]");
            return getClass().getName()
                    + '{' +
                    "disposed=false" +
                    ", values=" + builder +
                    '}';
        }
    }

}
