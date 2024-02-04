/**
 * Copyright Terracotta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package unrefined.desktop;

import sun.misc.Unsafe;
import unrefined.util.NotInstantiableError;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static unrefined.desktop.UnsafeSupport.UNSAFE;

/**
 * {@link Unsafe} based sizeOf measurement.
 *
 * @author Chris Dennis
 * @author Karstian Lee
 */
public final class SizeOfSupport {


    private SizeOfSupport() {
        throw new NotInstantiableError(SizeOfSupport.class);
    }

    /**
     * Calculates the size in memory (heap) of the instance passed in, not navigating the down graph
     *
     * @param object the object to measure the size of
     * @return the object size in memory in bytes
     */
    public static long sizeOf(Object object) {
        if (object == null) return 0;
        else if (object.getClass().isArray()) {
            Class<?> clazz = object.getClass();
            int base = UNSAFE.arrayBaseOffset(clazz);
            int scale = UNSAFE.arrayIndexScale(clazz);
            long size = base + (long) scale * Array.getLength(object);
            size += VMInfo.FIELD_OFFSET_ADJUSTMENT;
            if ((size % VMInfo.OBJECT_ALIGNMENT) != 0) {
                size += VMInfo.OBJECT_ALIGNMENT - (size % VMInfo.OBJECT_ALIGNMENT);
            }
            return Math.max(VMInfo.MINIMUM_OBJECT_SIZE, size);
        } else return sizeOfType(object.getClass());
    }

    private static long sizeOfType0(Class<?> clazz) {
        for (; clazz != null; clazz = clazz.getSuperclass()) {
            long lastFieldOffset = -1;
            for (Field f : clazz.getDeclaredFields()) {
                if (!Modifier.isStatic(f.getModifiers())) {
                    lastFieldOffset = Math.max(lastFieldOffset, UNSAFE.objectFieldOffset(f));
                }
            }
            if (lastFieldOffset > 0) {
                lastFieldOffset += VMInfo.FIELD_OFFSET_ADJUSTMENT;
                lastFieldOffset += 1;
                if ((lastFieldOffset % VMInfo.OBJECT_ALIGNMENT) != 0) {
                    lastFieldOffset += VMInfo.OBJECT_ALIGNMENT -
                            (lastFieldOffset % VMInfo.OBJECT_ALIGNMENT);
                }
                return Math.max(VMInfo.MINIMUM_OBJECT_SIZE, lastFieldOffset);
            }
        }

        long size = VMInfo.OBJECT_HEADER_SIZE;
        if ((size % VMInfo.OBJECT_ALIGNMENT) != 0) {
            size += VMInfo.OBJECT_ALIGNMENT - (size % VMInfo.OBJECT_ALIGNMENT);
        }
        return Math.max(VMInfo.MINIMUM_OBJECT_SIZE, size);
    }

    public static long sizeOfType(Class<?> clazz) {
        if (clazz == null || clazz.isArray()) return VMInfo.REFERENCE_SIZE;
        else if (clazz == boolean.class || clazz == int.class || clazz == float.class) return 4;
        else if (clazz == byte.class || clazz == void.class) return 1;
        else if (clazz == char.class || clazz == short.class) return 2;
        else if (clazz == long.class || clazz == double.class) return 8;
        else return sizeOfType0(clazz);
    }

}
