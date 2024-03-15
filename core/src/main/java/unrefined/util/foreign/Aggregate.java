package unrefined.util.foreign;

import unrefined.nio.Pointer;
import unrefined.util.reflect.Reflection;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public abstract class Aggregate implements Comparable<Aggregate>, Closeable {

    private static final Descriptor EMPTY = new Descriptor(Collections.emptyList(), 0);

    protected static Descriptor declareEmpty() {
        return EMPTY;
    }

    protected static Descriptor declare(Class<?>[] memberTypes, long[] memberOffsets, long[] memberRepetitions, long padding) {
        if (memberOffsets.length != memberTypes.length) throw new IndexOutOfBoundsException("Array length mismatch");
        int length = memberOffsets.length;
        if (length == 0) return new Descriptor(Collections.emptyList(), padding);
        else {
            List<Member> list = new ArrayList<>(length);
            for (int i = 0; i < length; i ++) {
                list.add(new Member(memberTypes[i], memberOffsets[i], memberRepetitions[i]));
            }
            return new Descriptor(Collections.unmodifiableList(list), list.getLast().getOffset() + list.getLast().getSize() + padding);
        }
    }

    protected static Descriptor declare(Class<?>[] memberTypes, long[] memberOffsets, long padding) {
        long[] memberRepetitions = new long[memberTypes.length];
        Arrays.fill(memberRepetitions, 1);
        return declare(memberTypes, memberOffsets, memberRepetitions, padding);
    }

    protected static Descriptor declare(Class<?>[] memberTypes, int memberTypesOffset,
                                     long[] memberOffsets, int memberOffsetsOffset,
                                     long[] memberRepetitions, int memberRepetitionsOffset,
                                     int length, long padding) {
        if (length == 0) return new Descriptor(Collections.emptyList(), padding);
        else {
            List<Member> list = new ArrayList<>(memberTypes.length);
            for (int i = 0; i < length; i ++) {
                list.add(new Member(memberTypes[memberTypesOffset + i], memberOffsets[memberOffsetsOffset + i], memberRepetitions[memberRepetitionsOffset + i]));
            }
            return new Descriptor(Collections.unmodifiableList(list), list.getLast().getOffset() + list.getLast().getSize() + padding);
        }
    }

    protected static Descriptor declare(Class<?>[] memberTypes, int memberTypesOffset,
                                        long[] memberOffsets, int memberOffsetsOffset,
                                        int length, long padding) {
        long[] memberRepetitions = new long[length];
        Arrays.fill(memberRepetitions, 1);
        return declare(memberTypes, memberTypesOffset, memberOffsets, memberOffsetsOffset, memberRepetitions, 0, length, padding);
    }

    protected static Descriptor declareStruct(Class<?>[] memberTypes, int memberTypesOffset,
                                           long[] memberRepetitions, int memberRepetitionsOffset,
                                           int length) {
        if (length == 0) return new Descriptor(Collections.emptyList(), 0);
        else {
            List<Member> list = new ArrayList<>(length);
            long offset = 0;
            Class<?> memberType = memberTypes[memberTypesOffset];
            Member first = new Member(memberType, offset, memberRepetitions[memberRepetitionsOffset]);
            long alignment = first.getTypeSize();
            list.add(first);
            offset += first.getSize();
            for (int i = 1; i < length; i ++) {
                long tmpTypeSize = Math.min(Foreign.getInstance().addressSize(), sizeOf(memberTypes[i + memberTypesOffset]));
                long left = offset % tmpTypeSize;
                if (left > 0) offset += tmpTypeSize - left;
                Member tmpMember = new Member(memberTypes[i + memberTypesOffset], offset, memberRepetitions[i + memberRepetitionsOffset]);
                alignment = Math.max(alignment, tmpTypeSize);
                list.add(tmpMember);
                offset += tmpMember.getSize();
            }
            if (length - 1 != 0) {
                long left = list.getLast().getTypeSize() % alignment;
                return new Descriptor(Collections.unmodifiableList(list), offset + (left == 0 ? 0 : alignment - left));
            }
            else return new Descriptor(Collections.unmodifiableList(list), offset);
        }
    }

    protected static Descriptor declareStruct(Class<?>[] memberTypes, int memberTypesOffset, int length) {
        long[] memberRepetitions = new long[length];
        Arrays.fill(memberRepetitions, 1);
        return declareStruct(memberTypes, memberTypesOffset, memberRepetitions, 0, length);
    }

    protected static Descriptor declareStruct(Class<?>[] memberTypes, long[] memberRepetitions) {
        if (memberTypes.length != memberRepetitions.length) throw new IndexOutOfBoundsException("Array length mismatch");
        return declareStruct(memberTypes, 0, memberRepetitions, 0, memberTypes.length);
    }

    protected static Descriptor declareStruct(Class<?>... memberTypes) {
        long[] memberRepetitions = new long[memberTypes.length];
        Arrays.fill(memberRepetitions, 1);
        return declareStruct(memberTypes, memberRepetitions);
    }

    protected static Descriptor declareUnion(Class<?>[] memberTypes, int memberTypesOffset,
                                             long[] memberRepetitions, int memberRepetitionsOffset,
                                             int length) {
        long alignment = 0;
        long size = 0;
        List<Member> list = new ArrayList<>();
        for (int i = 0; i < length; i ++) {
            Member tmpMember = new Member(memberTypes[i + memberTypesOffset], 0, memberRepetitions[i + memberRepetitionsOffset]);
            list.add(tmpMember);
            alignment = Math.max(alignment, tmpMember.getTypeSize());
            size = Math.max(size, tmpMember.getSize());
        }
        return new Descriptor(Collections.unmodifiableList(list), size % alignment == 0 ? size : (size / alignment + 1) * alignment);
    }

    protected static Descriptor declareUnion(Class<?>[] memberTypes, int memberTypesOffset, int length) {
        long[] memberRepetitions = new long[length];
        Arrays.fill(memberRepetitions, 1);
        return declareUnion(memberTypes, memberTypesOffset, memberRepetitions, 0, length);
    }

    protected static Descriptor declareUnion(Class<?>[] memberTypes, long[] memberRepetitions) {
        if (memberTypes.length != memberRepetitions.length) throw new IndexOutOfBoundsException("Array length mismatch");
        return declareUnion(memberTypes, 0, memberRepetitions, 0, memberTypes.length);
    }

    protected static Descriptor declareUnion(Class<?>... memberTypes) {
        long[] memberRepetitions = new long[memberTypes.length];
        Arrays.fill(memberRepetitions, 1);
        return declareUnion(memberTypes, memberRepetitions);
    }

    private final AtomicReference<Pointer> memory = new AtomicReference<>();

    protected Aggregate(Pointer memory) {
        this.memory.set(Objects.requireNonNull(memory));
    }

    public static <T extends Aggregate> T newInstance(Class<T> clazz, Pointer memory) {
        return Foreign.getInstance().newAggregateInstance(clazz, memory);
    }

    public static <T extends Aggregate> T allocate(Class<T> clazz) throws IOException {
        return Foreign.getInstance().newAggregateInstance(clazz, Pointer.allocate(sizeOfType(clazz)));
    }

    public static <T extends Aggregate> T allocateDirect(Class<T> clazz) throws IOException {
        return Foreign.getInstance().newAggregateInstance(clazz, Pointer.allocateDirect(sizeOfType(clazz)));
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
        return descriptorOf(clazz).getSize();
    }
    public static long sizeOf(Aggregate aggregate) {
        return aggregate == null ? 0 : sizeOfType(aggregate.getClass());
    }

    public static Descriptor descriptorOf(Class<? extends Aggregate> clazz) {
        if (clazz == null) return null;
        try {
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

    public final Descriptor getDescriptor() {
        return descriptorOf(getClass());
    }

    @SuppressWarnings("unchecked")
    private static long sizeOf(Class<?> memberType) {
        if (memberType == boolean.class || memberType == byte.class) return 1;
        else if (memberType == short.class || memberType == char.class) return 2;
        else if (memberType == int.class || memberType == float.class) return 4;
        else if (memberType == long.class || memberType == double.class) return 8;
        else if (Aggregate.class.isAssignableFrom(memberType)) return descriptorOf((Class<? extends Aggregate>) memberType).getSize();
        else throw new IllegalArgumentException("Illegal member type: " + memberType);
    }

    private static Class<?> checkMemberType(Class<?> memberType) {
        if (memberType == boolean.class || memberType == byte.class || memberType == char.class ||
                memberType == short.class || memberType == int.class || memberType == long.class ||
                memberType == float.class || memberType == double.class ||
                Aggregate.class.isAssignableFrom(memberType))
            return memberType;
        else throw new IllegalArgumentException("Illegal member type: " + memberType);
    }

    private static long checkMemberOffset(long memberOffset) {
        if (memberOffset < 0) throw new IllegalArgumentException("Negative member offset");
        else return memberOffset;
    }

    public final static class Member {

        private final Class<?> type;
        private final long offset;
        private final long repetition;
        private final long size;
        private final long typeSize;

        private Member(Class<?> type, long offset, long repetition) {
            this.type = checkMemberType(type);
            this.offset = checkMemberOffset(offset);
            this.repetition = repetition;
            this.typeSize = sizeOf(type);
            this.size = this.typeSize * this.repetition;
        }

        public Class<?> getType() {
            return type;
        }

        public long getOffset() {
            return offset;
        }

        public long getTypeSize() {
            return typeSize;
        }

        public long getSize() {
            return size;
        }

        public long getRepetition() {
            return repetition;
        }

    }

    public final static class Descriptor {

        private final List<Member> members;
        private final long size;

        private Descriptor(List<Member> members, long size) {
            this.members = members;
            this.size = size;
        }

        public List<Member> getMembers() {
            return members;
        }

        public long getSize() {
            return size;
        }

    }

    @Override
    public int compareTo(Aggregate other) {
        return memory().compareTo(other.memory(), 0, getDescriptor().getSize());
    }

}