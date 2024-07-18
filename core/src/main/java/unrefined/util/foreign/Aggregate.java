package unrefined.util.foreign;

import unrefined.nio.AddressArrayHandle;
import unrefined.nio.AddressHandle;
import unrefined.nio.BooleanArrayHandle;
import unrefined.nio.BooleanHandle;
import unrefined.nio.ByteArrayHandle;
import unrefined.nio.ByteHandle;
import unrefined.nio.CharArrayHandle;
import unrefined.nio.CharHandle;
import unrefined.nio.DoubleArrayHandle;
import unrefined.nio.DoubleHandle;
import unrefined.nio.FloatArrayHandle;
import unrefined.nio.FloatHandle;
import unrefined.nio.IntArrayHandle;
import unrefined.nio.IntHandle;
import unrefined.nio.LongArrayHandle;
import unrefined.nio.LongHandle;
import unrefined.nio.NativeIntArrayHandle;
import unrefined.nio.NativeIntHandle;
import unrefined.nio.NativeLongArrayHandle;
import unrefined.nio.NativeLongHandle;
import unrefined.nio.Pointer;
import unrefined.nio.ShortArrayHandle;
import unrefined.nio.ShortHandle;
import unrefined.util.UnexpectedError;
import unrefined.util.reflect.Reflection;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public abstract class Aggregate extends Pointer.Handle implements Comparable<Aggregate> {

    private static final Descriptor EMPTY = new Descriptor(Collections.emptyList(), 0);

    public static Descriptor declareEmpty() {
        return EMPTY;
    }

    public static Descriptor declare(Object[] memberTypes, long[] memberOffsets, long[] memberRepetitions, long padding) {
        if (memberTypes.length != memberOffsets.length) throw new IndexOutOfBoundsException("Array length mismatch");
        if (memberOffsets.length != memberRepetitions.length) throw new IndexOutOfBoundsException("Array length mismatch");
        int length = memberOffsets.length;
        if (length == 0) return new Descriptor(Collections.emptyList(), padding);
        else {
            List<Member> list = new ArrayList<>(length);
            for (int i = 0; i < length; i ++) {
                list.add(new Member(memberTypes[i], memberOffsets[i], memberRepetitions[i]));
            }
            Member last = list.get(length - 1);
            return new Descriptor(Collections.unmodifiableList(list), last.getOffset() + last.size() + padding);
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
            List<Member> list = new ArrayList<>(length);
            for (int i = 0; i < length; i ++) {
                list.add(new Member(memberTypes[memberTypesOffset + i], memberOffsets[memberOffsetsOffset + i], memberRepetitions[memberRepetitionsOffset + i]));
            }
            Member last = list.get(length - 1);
            return new Descriptor(Collections.unmodifiableList(list), last.getOffset() + last.size() + padding);
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
                long tmpTypeSize = Math.min(Foreign.getInstance().addressSize(), alignSizeOf(memberTypes[i + memberTypesOffset]));
                long left = offset % tmpTypeSize;
                if (left > 0) offset += tmpTypeSize - left;
                Member tmpMember = new Member(memberTypes[i + memberTypesOffset], offset, memberRepetitions[i + memberRepetitionsOffset]);
                alignment = Math.max(alignment, tmpTypeSize);
                list.add(tmpMember);
                offset += tmpMember.size();
            }
            if (length - 1 != 0) {
                long left = list.get(list.size() - 1).typeSize() % alignment;
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

    protected Aggregate(Pointer memory) {
        super(memory);
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

    private static long alignSizeOf(Object memberType) {
        if (memberType instanceof Descriptor) {
            long size = 0;
            for (Member member : ((Descriptor) memberType).getMembers()) {
                size = Math.max(size, alignSizeOf(member.getType()));
            }
            return size;
        }
        else if (memberType instanceof Class) {
            if (memberType == boolean.class || memberType == byte.class) return 1;
            else if (memberType == short.class || memberType == char.class) return 2;
            else if (memberType == int.class || memberType == float.class) return 4;
            else if (memberType == long.class || memberType == double.class) return 8;
            else if (Aggregate.class.isAssignableFrom((Class<?>) memberType)) return
                    alignSizeOf(descriptorOf((Class<? extends Aggregate>) memberType));
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

        @Override
        public String toString() {
            return getClass().getName()
                    + '{' +
                    "type=" + type +
                    ", offset=" + offset +
                    ", repetition=" + repetition +
                    ", size=" + size +
                    ", typeSize=" + typeSize +
                    '}';
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
            return new ArrayList<>(members);
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

        @Override
        public String toString() {
            return getClass().getName()
                    + '{' +
                    "size=" + size +
                    '}';
        }

    }

    private final static class Proxy extends Aggregate {
        public final Descriptor descriptor;
        public Proxy(Pointer memory, Descriptor descriptor) {
            super(memory);
            this.descriptor = Objects.requireNonNull(descriptor);
        }
        @Override
        public void set(Pointer memory, long offset) {
            payload().transferFrom(0, memory, offset, descriptor.size());
        }
        @Override
        public void get(Pointer memory, long offset) {
            payload().transferTo(0, memory, offset, descriptor.size());
        }
    }

    @Override
    public void set(Pointer memory, long offset) {
        payload().transferFrom(0, memory, offset, getDescriptor().size());
    }

    @Override
    public void get(Pointer memory, long offset) {
        payload().transferTo(0, memory, offset, getDescriptor().size());
    }

    @Override
    public int compareTo(Aggregate other) {
        return payload().compareTo(other.payload(), 0, getDescriptor().size());
    }

    @Override
    public String toString() {
        return getClass().getName()
                + '{' +
                "payload=" + payload() +
                ", descriptor=" + getDescriptor() +
                '}';
    }

    public static class ArrayHandle<T extends Aggregate> extends Pointer.Handle implements Comparable<ArrayHandle<? extends Aggregate>> {

        public static <T extends Aggregate> ArrayHandle<T> wrap(Pointer memory, Class<T> clazz, int length) {
            return new ArrayHandle<>(memory, descriptorOf(clazz).size(), length);
        }

        public static <T extends Aggregate> ArrayHandle<T> allocate(Class<T> clazz, int length) throws IOException {
            long typeSize = descriptorOf(clazz).size();
            return new ArrayHandle<>(Pointer.allocate((long) length * typeSize), typeSize, length);
        }

        public static <T extends Aggregate> ArrayHandle<T> allocateDirect(Class<T> clazz, int length) throws IOException {
            long typeSize = descriptorOf(clazz).size();
            return new ArrayHandle<>(Pointer.allocateDirect((long) length * typeSize), typeSize, length);
        }

        public static ArrayHandle<?> wrap(Pointer memory, Descriptor descriptor, int length) {
            return new ArrayHandle<>(memory, descriptor.size(), length);
        }

        public static ArrayHandle<?> allocate(Descriptor descriptor, int length) throws IOException {
            long typeSize = descriptor.size();
            return new ArrayHandle<>(Pointer.allocate((long) length * typeSize), typeSize, length);
        }

        public static ArrayHandle<?> allocateDirect(Descriptor descriptor, int length) throws IOException {
            long typeSize = descriptor.size();
            return new ArrayHandle<>(Pointer.allocateDirect((long) length * typeSize), typeSize, length);
        }

        private final int length;
        private final long typeSize;

        protected ArrayHandle(Pointer memory, long typeSize, int length) {
            super(memory);
            this.typeSize = typeSize;
            this.length = length;
        }

        public int length() {
            return length;
        }

        public void get(T[] array, int index) {
            long offset = 0;
            for (int i = 0; i < length; i ++) {
                payload().transferTo(offset, array[index + i].payload(), 0, typeSize);
                offset += typeSize;
            }
        }

        public void get(T[] array) {
            long offset = 0;
            for (int i = 0; i < length; i ++) {
                payload().transferTo(offset, array[i].payload(), 0, typeSize);
                offset += typeSize;
            }
        }

        public void set(T[] array, int index) {
            long offset = 0;
            for (int i = 0; i < length; i ++) {
                payload().transferFrom(offset, array[index + i].payload(), 0, typeSize);
                offset += typeSize;
            }
        }

        public void set(T[] array) {
            long offset = 0;
            for (int i = 0; i < length; i ++) {
                payload().transferFrom(offset, array[i].payload(), 0, typeSize);
                offset += typeSize;
            }
        }

        @Override
        public int compareTo(ArrayHandle<? extends Aggregate> other) {
            return payload().compareTo(other.payload(), 0, (long) length * typeSize);
        }

        @Override
        public void set(Pointer memory, long offset) {
            payload().transferFrom(0, memory, offset, (long) length * typeSize);
        }
        @Override
        public void get(Pointer memory, long offset) {
            payload().transferTo(0, memory, offset, (long) length * typeSize);
        }

    }
    
    public BooleanHandle declareBoolean(int index) {
        return BooleanHandle.wrap(payload().slice(getDescriptor().getMembers().get(index).getOffset(), 1));
    }

    public ByteHandle declareByte(int index) {
        return ByteHandle.wrap(payload().slice(getDescriptor().getMembers().get(index).getOffset(), 1));
    }

    public CharHandle declareChar(int index) {
        return CharHandle.wrap(payload().slice(getDescriptor().getMembers().get(index).getOffset(), 2));
    }

    public ShortHandle declareShort(int index) {
        return ShortHandle.wrap(payload().slice(getDescriptor().getMembers().get(index).getOffset(), 2));
    }

    public IntHandle declareInt(int index) {
        return IntHandle.wrap(payload().slice(getDescriptor().getMembers().get(index).getOffset(), 4));
    }

    public LongHandle declareLong(int index) {
        return LongHandle.wrap(payload().slice(getDescriptor().getMembers().get(index).getOffset(), 8));
    }

    public FloatHandle declareFloat(int index) {
        return FloatHandle.wrap(payload().slice(getDescriptor().getMembers().get(index).getOffset(), 4));
    }

    public DoubleHandle declareDouble(int index) {
        return DoubleHandle.wrap(payload().slice(getDescriptor().getMembers().get(index).getOffset(), 8));
    }

    public NativeIntHandle declareNativeInt(int index) {
        return NativeIntHandle.wrap(payload().slice(getDescriptor().getMembers().get(index).getOffset(), Foreign.getInstance().nativeIntSize()));
    }

    public NativeLongHandle declareNativeLong(int index) {
        return NativeLongHandle.wrap(payload().slice(getDescriptor().getMembers().get(index).getOffset(), Foreign.getInstance().nativeLongSize()));
    }

    public AddressHandle declareAddress(int index) {
        return AddressHandle.wrap(payload().slice(getDescriptor().getMembers().get(index).getOffset(), Foreign.getInstance().addressSize()));
    }

    public <T extends Aggregate> T declareAggregate(Class<T> clazz, int index) {
        return newInstance(clazz, payload().slice(getDescriptor().getMembers().get(index).getOffset(), descriptorOf(clazz).size()));
    }
    
    public BooleanArrayHandle declareBooleanArray(int index, int length) {
        return BooleanArrayHandle.wrap(payload().slice(getDescriptor().getMembers().get(index).getOffset(), length), length);
    }

    public ByteArrayHandle declareByteArray(int index, int length) {
        return ByteArrayHandle.wrap(payload().slice(getDescriptor().getMembers().get(index).getOffset(), length), length);
    }

    public CharArrayHandle declareCharArray(int index, int length) {
        return CharArrayHandle.wrap(payload().slice(getDescriptor().getMembers().get(index).getOffset(), (long) length << 1), length);
    }

    public ShortArrayHandle declareShortArray(int index, int length) {
        return ShortArrayHandle.wrap(payload().slice(getDescriptor().getMembers().get(index).getOffset(), (long) length << 1), length);
    }

    public IntArrayHandle declareIntArray(int index, int length) {
        return IntArrayHandle.wrap(payload().slice(getDescriptor().getMembers().get(index).getOffset(), (long) length << 2), length);
    }

    public LongArrayHandle declareLongArray(int index, int length) {
        return LongArrayHandle.wrap(payload().slice(getDescriptor().getMembers().get(index).getOffset(), (long) length << 3), length);
    }

    public FloatArrayHandle declareFloatArray(int index, int length) {
        return FloatArrayHandle.wrap(payload().slice(getDescriptor().getMembers().get(index).getOffset(), (long) length << 2), length);
    }

    public DoubleArrayHandle declareDoubleArray(int index, int length) {
        return DoubleArrayHandle.wrap(payload().slice(getDescriptor().getMembers().get(index).getOffset(), (long) length << 3), length);
    }

    public NativeIntArrayHandle declareNativeIntArray(int index, int length) {
        return NativeIntArrayHandle.wrap(payload().slice(getDescriptor().getMembers().get(index).getOffset(), (long) length * Foreign.getInstance().nativeIntSize()), length);
    }

    public NativeLongArrayHandle declareNativeLongArray(int index, int length) {
        return NativeLongArrayHandle.wrap(payload().slice(getDescriptor().getMembers().get(index).getOffset(), (long) length * Foreign.getInstance().nativeLongSize()), length);
    }

    public AddressArrayHandle declareAddressArray(int index, int length) {
        return AddressArrayHandle.wrap(payload().slice(getDescriptor().getMembers().get(index).getOffset(), (long) length * Foreign.getInstance().addressSize()), length);
    }

    public <T extends Aggregate> ArrayHandle<T> declareAggregateArray(Class<T> clazz, int index, int length) {
        return ArrayHandle.wrap(payload().slice(getDescriptor().getMembers().get(index).getOffset(), (long) length * descriptorOf(clazz).size()), clazz, length);
    }

}