package unrefined.util.foreign;

import unrefined.nio.Pointer;
import unrefined.util.UnexpectedError;
import unrefined.util.reflect.Reflection;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public abstract class Aggregate implements Comparable<Aggregate>, Closeable {

    private static final Descriptor EMPTY = new Descriptor(Collections.emptyList(), 0);

    public static Descriptor declareEmpty() {
        return EMPTY;
    }

    public static Descriptor declare(Object[] memberTypes, long[] memberOffsets, long[] memberRepetitions, long padding) {
        if (memberOffsets.length != memberTypes.length) throw new IndexOutOfBoundsException("Array length mismatch");
        int length = memberOffsets.length;
        if (length == 0) return new Descriptor(Collections.emptyList(), padding);
        else {
            List<Member> list = new ArrayList<>(length);
            for (int i = 0; i < length; i ++) {
                list.add(new Member(memberTypes[i], memberOffsets[i], memberRepetitions[i]));
            }
            return new Descriptor(Collections.unmodifiableList(list), list.getLast().getOffset() + list.getLast().size() + padding);
        }
    }

    public static Descriptor declare(Object[] memberTypes, long[] memberOffsets, long padding) {
        long[] memberRepetitions = new long[memberTypes.length];
        Arrays.fill(memberRepetitions, 1);
        return declare(memberTypes, memberOffsets, memberRepetitions, padding);
    }

    public static Descriptor declare(Object[] memberTypes, int memberTypesOffset,
                                     long[] memberOffsets, int memberOffsetsOffset,
                                     long[] memberRepetitions, int memberRepetitionsOffset,
                                     int length, long padding) {
        if (length == 0) return new Descriptor(Collections.emptyList(), padding);
        else {
            List<Member> list = new ArrayList<>(memberTypes.length);
            for (int i = 0; i < length; i ++) {
                list.add(new Member(memberTypes[memberTypesOffset + i], memberOffsets[memberOffsetsOffset + i], memberRepetitions[memberRepetitionsOffset + i]));
            }
            return new Descriptor(Collections.unmodifiableList(list), list.getLast().getOffset() + list.getLast().size() + padding);
        }
    }

    public static Descriptor declare(Object[] memberTypes, int memberTypesOffset,
                                     long[] memberOffsets, int memberOffsetsOffset,
                                     int length, long padding) {
        long[] memberRepetitions = new long[length];
        Arrays.fill(memberRepetitions, 1);
        return declare(memberTypes, memberTypesOffset, memberOffsets, memberOffsetsOffset, memberRepetitions, 0, length, padding);
    }

    public static Descriptor declareStruct(Object[] memberTypes, int memberTypesOffset,
                                           long[] memberRepetitions, int memberRepetitionsOffset,
                                           int length) {
        if (length == 0) return new Descriptor(Collections.emptyList(), 0);
        else {
            List<Member> list = new ArrayList<>(length);
            long offset = 0;
            Object memberType = memberTypes[memberTypesOffset];
            Member first = new Member(memberType, offset, memberRepetitions[memberRepetitionsOffset]);
            long alignment = first.typeSize();
            list.add(first);
            offset += first.size();
            for (int i = 1; i < length; i ++) {
                long tmpTypeSize = Math.min(Foreign.getInstance().addressSize(), sizeOf(memberTypes[i + memberTypesOffset]));
                long left = offset % tmpTypeSize;
                if (left > 0) offset += tmpTypeSize - left;
                Member tmpMember = new Member(memberTypes[i + memberTypesOffset], offset, memberRepetitions[i + memberRepetitionsOffset]);
                alignment = Math.max(alignment, tmpTypeSize);
                list.add(tmpMember);
                offset += tmpMember.size();
            }
            if (length - 1 != 0) {
                long left = list.getLast().typeSize() % alignment;
                return new Descriptor(Collections.unmodifiableList(list), offset + (left == 0 ? 0 : alignment - left));
            }
            else return new Descriptor(Collections.unmodifiableList(list), offset);
        }
    }

    public static Descriptor declareStruct(Object[] memberTypes, int memberTypesOffset, int length) {
        long[] memberRepetitions = new long[length];
        Arrays.fill(memberRepetitions, 1);
        return declareStruct(memberTypes, memberTypesOffset, memberRepetitions, 0, length);
    }

    public static Descriptor declareStruct(Object[] memberTypes, long[] memberRepetitions) {
        if (memberTypes.length != memberRepetitions.length) throw new IndexOutOfBoundsException("Array length mismatch");
        return declareStruct(memberTypes, 0, memberRepetitions, 0, memberTypes.length);
    }

    public static Descriptor declareStruct(Object... memberTypes) {
        long[] memberRepetitions = new long[memberTypes.length];
        Arrays.fill(memberRepetitions, 1);
        return declareStruct(memberTypes, memberRepetitions);
    }

    public static Descriptor declareUnion(Object[] memberTypes, int memberTypesOffset,
                                          long[] memberRepetitions, int memberRepetitionsOffset,
                                          int length) {
        long alignment = 0;
        long size = 0;
        List<Member> list = new ArrayList<>();
        for (int i = 0; i < length; i ++) {
            Member tmpMember = new Member(memberTypes[i + memberTypesOffset], 0, memberRepetitions[i + memberRepetitionsOffset]);
            list.add(tmpMember);
            alignment = Math.max(alignment, tmpMember.typeSize());
            size = Math.max(size, tmpMember.size());
        }
        return new Descriptor(Collections.unmodifiableList(list), size % alignment == 0 ? size : (size / alignment + 1) * alignment);
    }

    public static Descriptor declareUnion(Object[] memberTypes, int memberTypesOffset, int length) {
        long[] memberRepetitions = new long[length];
        Arrays.fill(memberRepetitions, 1);
        return declareUnion(memberTypes, memberTypesOffset, memberRepetitions, 0, length);
    }

    public static Descriptor declareUnion(Object[] memberTypes, long[] memberRepetitions) {
        if (memberTypes.length != memberRepetitions.length) throw new IndexOutOfBoundsException("Array length mismatch");
        return declareUnion(memberTypes, 0, memberRepetitions, 0, memberTypes.length);
    }

    public static Descriptor declareUnion(Object... memberTypes) {
        long[] memberRepetitions = new long[memberTypes.length];
        Arrays.fill(memberRepetitions, 1);
        return declareUnion(memberTypes, memberRepetitions);
    }

    private final AtomicReference<Pointer> memory = new AtomicReference<>();

    protected Aggregate(Pointer memory) {
        this.memory.set(Objects.requireNonNull(memory));
    }

    public static Aggregate newProxyInstance(Descriptor descriptor, Pointer memory) {
        return new Proxy(memory, descriptor);
    }

    public static <T extends Aggregate> T newInstance(Class<T> clazz, Pointer memory) {
        try {
            return Reflection.getInstance().newInstance(clazz.getDeclaredConstructor(Pointer.class), memory);
        } catch (InstantiationException | InvocationTargetException | NoSuchMethodException e) {
            throw new UnexpectedError(e);
        }
    }

    public static <T extends Aggregate> T allocate(Class<T> clazz) throws IOException {
        return newInstance(clazz, Pointer.allocate(sizeOfType(clazz)));
    }

    public static <T extends Aggregate> T allocateDirect(Class<T> clazz) throws IOException {
        return newInstance(clazz, Pointer.allocateDirect(sizeOfType(clazz)));
    }

    public static Pointer allocate(Descriptor descriptor) throws IOException {
        return Pointer.allocate(descriptor.size());
    }

    public static Pointer allocateDirect(Descriptor descriptor) throws IOException {
        return Pointer.allocateDirect(descriptor.size());
    }

    public void useMemory(Pointer memory) {
        this.memory.set(Objects.requireNonNull(memory));
    }

    public Pointer memory() {
        return memory.get();
    }

    @Override
    public void close() throws IOException {
        memory.get().close();
    }

    public static long sizeOfType(Class<? extends Aggregate> clazz) {
        return descriptorOf(clazz).size();
    }
    public static long sizeOf(Aggregate aggregate) {
        return aggregate instanceof Proxy ? ((Proxy) aggregate).descriptor.size() : (aggregate == null ? 0 : sizeOfType(aggregate.getClass()));
    }

    public static Descriptor descriptorOf(Class<? extends Aggregate> clazz) {
        if (clazz == null) return null;
        else try {
            Field field = clazz.getDeclaredField("descriptor");
            if (field.getType() != Descriptor.class) throw new IllegalStateException("Illegal descriptor type: " + field.getType());
            int modifiers = field.getModifiers();
            if (!Modifier.isPublic(modifiers) || !Modifier.isFinal(modifiers) || !Modifier.isStatic(modifiers))
                throw new IllegalStateException("Illegal modifiers; expected public static final");
            return (Descriptor) Reflection.getInstance().getObjectField(null, field);
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException("Incomplete aggregate type");
        }
    }

    public static boolean isIncompleteObject(Aggregate object) {
        return isIncompleteClass(object.getClass());
    }

    public static boolean isIncompleteClass(Class<? extends Aggregate> clazz) {
        try {
            Field field = clazz.getDeclaredField("descriptor");
            if (field.getType() != Descriptor.class) throw new IllegalStateException("Illegal descriptor type: " + field.getType());
            int modifiers = field.getModifiers();
            return !Modifier.isPublic(modifiers) || !Modifier.isFinal(modifiers) || !Modifier.isStatic(modifiers);
        } catch (NoSuchFieldException e) {
            return true;
        }
    }

    public static boolean isProxyObject(Aggregate object) {
        return isProxyClass(object.getClass());
    }

    public static boolean isProxyClass(Class<? extends Aggregate> clazz) {
        return Proxy.class.isAssignableFrom(clazz);
    }

    public final Descriptor getDescriptor() {
        return this instanceof Proxy ? ((Proxy) this).descriptor : descriptorOf(getClass());
    }

    @SuppressWarnings("unchecked")
    private static long sizeOf(Object memberType) {
        if (memberType instanceof Descriptor) return ((Descriptor) memberType).size();
        else if (memberType instanceof Class) {
            if (memberType == boolean.class || memberType == byte.class) return 1;
            else if (memberType == short.class || memberType == char.class) return 2;
            else if (memberType == int.class || memberType == float.class) return 4;
            else if (memberType == long.class || memberType == double.class) return 8;
            else if (Aggregate.class.isAssignableFrom((Class<?>) memberType)) return
                    descriptorOf((Class<? extends Aggregate>) memberType).size();
        }
        throw new IllegalArgumentException("Illegal member type: " + memberType);
    }

    private static Object checkMemberType(Object memberType) {
        if (memberType instanceof Class && (memberType == boolean.class || memberType == byte.class || memberType == char.class ||
                memberType == short.class || memberType == int.class || memberType == long.class ||
                memberType == float.class || memberType == double.class ||
                Aggregate.class.isAssignableFrom((Class<?>) memberType)))
            return memberType;
        else if (memberType instanceof Descriptor) return memberType;
        else throw new IllegalArgumentException("Illegal member type: " + memberType);
    }

    private static long checkMemberOffset(long memberOffset) {
        if (memberOffset < 0) throw new IllegalArgumentException("Negative member offset");
        else return memberOffset;
    }

    public final static class Member {

        private final Object type;
        private final long offset;
        private final long repetition;
        private final long size;
        private final long typeSize;

        private Member(Object type, long offset, long repetition) {
            this.type = checkMemberType(type);
            this.offset = checkMemberOffset(offset);
            this.repetition = repetition;
            this.typeSize = sizeOf(type);
            this.size = this.typeSize * this.repetition;
        }

        public Object getType() {
            return type;
        }

        public long getOffset() {
            return offset;
        }

        public long typeSize() {
            return typeSize;
        }

        public long size() {
            return size;
        }

        public long getRepetition() {
            return repetition;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) return true;
            if (object == null || getClass() != object.getClass()) return false;

            Member that = (Member) object;

            return size == that.size;
        }

        @Override
        public int hashCode() {
            return (int) (size ^ (size >>> 32));
        }

    }

    public final static class Descriptor {

        private final List<Member> members;
        private final long size;

        private Descriptor(List<Member> members, long size) {
            this.members = members;
            this.size = size;
        }

        public List<Member> members() {
            return members;
        }

        public long size() {
            return size;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) return true;
            if (object == null || getClass() != object.getClass()) return false;

            Descriptor that = (Descriptor) object;

            if (size != that.size) return false;
            return members.equals(that.members);
        }

        @Override
        public int hashCode() {
            int result = members.hashCode();
            result = 31 * result + (int) (size ^ (size >>> 32));
            return result;
        }

    }

    private final static class Proxy extends Aggregate {
        public final Descriptor descriptor;
        public Proxy(Pointer memory, Descriptor descriptor) {
            super(memory);
            this.descriptor = Objects.requireNonNull(descriptor);
        }
    }

    @Override
    public int compareTo(Aggregate other) {
        return memory().compareTo(other.memory(), 0, getDescriptor().size());
    }

}