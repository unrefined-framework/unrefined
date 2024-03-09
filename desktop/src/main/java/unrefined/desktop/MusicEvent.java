package unrefined.desktop;

import java.util.EventObject;

public class MusicEvent extends EventObject {

    private static final long serialVersionUID = 6030326245104016472L;

    private final Type type;
    private final long position;

    public MusicEvent(MusicPlayer source, Type type, long position) {
        super(source);
        this.type = type;
        this.position = position;
    }

    @Override
    public final MusicPlayer getSource() {
        return (MusicPlayer) super.getSource();
    }

    public final Type getType() {
        return type;
    }

    public final long getFramePosition() {
        return position;
    }

    @Override
    public String toString() {
        return String.format("%s event from %s", type, getSource());
    }

    public static class Type {

        private final String name;

        protected Type(String name) {
            this.name = name;
        }

        @Override
        public final boolean equals(Object obj) {
            return super.equals(obj);
        }

        @Override
        public final int hashCode() {
            return super.hashCode();
        }

        @Override
        public String toString() {
            return name;
        }

        public static final Type PREPARE = new Type("Prepare");
        public static final Type START = new Type("Start");
        public static final Type STOP = new Type("Stop");

    }

}
