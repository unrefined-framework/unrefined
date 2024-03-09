package unrefined.desktop;

import java.util.EventObject;

public class SoundEvent extends EventObject {

	private static final long serialVersionUID = -2858220172096261678L;

	private final Type type;
	private final double position;
	public final int instanceID;

	public SoundEvent(SoundClip source, Type type, int instanceID, double position) {
		super(source);
		this.type = type;
		this.instanceID = instanceID;
		this.position = position;
	}

	@Override
	public SoundClip getSource() {
		return (SoundClip) super.getSource();
	}

	public final Type getType() {
		return type;
	}

	public final double getFramePosition() {
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

		public static final Type OPEN = new Type("Open");
		public static final Type CLOSE = new Type("Close");
		public static final Type OBTAIN_INSTANCE = new Type("ObtainInstance");
		public static final Type RELEASE_INSTANCE = new Type("ReleaseInstance");
		public static final Type START_INSTANCE = new Type("StartInstance");
		public static final Type STOP_INSTANCE = new Type("StopInstance");
		public static final Type LOOP_INSTANCE = new Type("LoopInstance");

	}

}

