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

import unrefined.util.NotInstantiableError;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.openmbean.CompositeData;

/**
 * Detects and represents JVM-specific properties that relate to the memory
 * data model for java objects that are useful for size of calculations.
 *
 * @author jhouse
 * @author Chris Dennis
 * @author Karstian Lee
 */
public final class VMInfo {

    private VMInfo() {
        throw new NotInstantiableError(VMInfo.class);
    }

    private static final long TWENTY_FIVE_GIB = 25L * 1024L * 1024L * 1024L;
    private static final long FIFTY_SEVEN_GIB = 57L * 1024L * 1024L * 1024L;

    public static final String VM_NAME = System.getProperty("java.vm.name");
    public static final String VM_VENDOR = System.getProperty("java.vm.vendor");

    public static final boolean IS_JROCKIT;
    public static final boolean IS_HOTSPOT;
    public static final boolean IS_OPENJDK;
    public static final boolean IS_IBM;
    public static final boolean IS_APPLE;

    public static final boolean IS_IBM_COMPRESSED_REFS;
    public static final boolean IS_HOTSPOT_COMPRESSED_OOPS;
    public static final boolean IS_JROCKIT_64GB_COMPRESSION;
    public static final boolean IS_HOTSPOT_CONCURRENT_MARK_SWEEP_GC;

    static {
        String vm = VM_NAME.toLowerCase();
        IS_JROCKIT = System.getProperty("jrockit.version") != null || vm.contains("jrockit");
        IS_HOTSPOT = vm.contains("hotspot");
        IS_APPLE = VM_VENDOR.startsWith("Apple");
        IS_OPENJDK = vm.contains("openjdk");
        IS_IBM = VM_NAME.contains("IBM") && VM_VENDOR.contains("IBM");

        IS_IBM_COMPRESSED_REFS = IS_IBM && System.getProperty("com.ibm.oti.vm.bootstrap.library.path", "").contains("compressedrefs");

        if (IS_HOTSPOT) {
            boolean isHotspotCompressedOops;
            try {
                MBeanServer server = ManagementFactory.getPlatformMBeanServer();
                ObjectName beanName = ObjectName.getInstance("com.sun.management:type=HotSpotDiagnostic");
                Object vmOption = server.invoke(beanName, "getVMOption",
                        new Object[] { "UseCompressedOops" },
                        new String[] { "java.lang.String" });
                isHotspotCompressedOops = Boolean.parseBoolean((String) ((CompositeData) vmOption).get("value"));
            } catch (ReflectionException | MalformedObjectNameException | InstanceNotFoundException | MBeanException e) {
                isHotspotCompressedOops = false;
            }
            IS_HOTSPOT_COMPRESSED_OOPS = isHotspotCompressedOops;
            boolean isHotSpotConcurrentMarkSweepGC = false;
            for (GarbageCollectorMXBean bean : ManagementFactory.getGarbageCollectorMXBeans()) {
                if ("ConcurrentMarkSweep".equals(bean.getName())) {
                    isHotSpotConcurrentMarkSweepGC = true;
                }
            }
            IS_HOTSPOT_CONCURRENT_MARK_SWEEP_GC = isHotSpotConcurrentMarkSweepGC;
        }
        else {
            IS_HOTSPOT_COMPRESSED_OOPS = false;
            IS_HOTSPOT_CONCURRENT_MARK_SWEEP_GC = false;
        }

        if (IS_JROCKIT) {
            String vmArgs;
            try {
                MBeanServer server = ManagementFactory.getPlatformMBeanServer();
                ObjectName name = ObjectName.getInstance("oracle.jrockit.management:type=PerfCounters");
                Object attr = server.getAttribute(name, "java.rt.vmArgs");
                vmArgs = attr == null ? null : attr.toString();
            }
            catch (ReflectionException | MalformedObjectNameException | AttributeNotFoundException |
                     InstanceNotFoundException | MBeanException e) {
                vmArgs = null;
            }
            if (vmArgs == null) IS_JROCKIT_64GB_COMPRESSION = false;
            else if (vmArgs.contains("-XXcompressedRefs:enable=false")) {
                IS_JROCKIT_64GB_COMPRESSION = false;
            }
            else if (vmArgs.contains("-XXcompressedRefs:size=4GB") || vmArgs.contains("-XXcompressedRefs:size=32GB")) {
                IS_JROCKIT_64GB_COMPRESSION = false;
            }
            else if (vmArgs.contains("-XXcompressedRefs:size=64GB")) {
                IS_JROCKIT_64GB_COMPRESSION = true;
            }
            else if (vmArgs.contains("-XXcompressedRefs:enable=true")) {
                long maxMemory = Runtime.getRuntime().maxMemory();
                IS_JROCKIT_64GB_COMPRESSION = maxMemory > TWENTY_FIVE_GIB && maxMemory <= FIFTY_SEVEN_GIB;
            }
            else IS_JROCKIT_64GB_COMPRESSION = false;
        }
        else IS_JROCKIT_64GB_COMPRESSION = false;
    }

    public static final boolean IS_64_BIT;
    static {
        String property;
        property = System.getProperty("com.ibm.vm.bitmode", System.getProperty("sun.arch.data.model"));
        if (property != null) {
            IS_64_BIT = property.equals("64");
        }
        else {
            property = System.getProperty("java.vm.version");
            if (property != null) {
                IS_64_BIT = property.contains("_64");
            }
            else IS_64_BIT = ABI.P == 8;
        }
    }

    public static final int REFERENCE_SIZE;
    public static final int MINIMUM_OBJECT_SIZE;
    public static final int OBJECT_ALIGNMENT;
    public static final int FIELD_OFFSET_ADJUSTMENT;
    public static final int OBJECT_HEADER_SIZE;

    static {
        if (IS_HOTSPOT) {
            if (IS_64_BIT) {
                if (IS_HOTSPOT_COMPRESSED_OOPS && IS_HOTSPOT_CONCURRENT_MARK_SWEEP_GC) {
                    REFERENCE_SIZE = 4;
                    OBJECT_ALIGNMENT = 8;
                    MINIMUM_OBJECT_SIZE = 24;
                    FIELD_OFFSET_ADJUSTMENT = 0;
                    OBJECT_HEADER_SIZE = 12;
                } else if (IS_HOTSPOT_COMPRESSED_OOPS) {
                    REFERENCE_SIZE = 4;
                    OBJECT_ALIGNMENT = 8;
                    MINIMUM_OBJECT_SIZE = 8;
                    FIELD_OFFSET_ADJUSTMENT = 0;
                    OBJECT_HEADER_SIZE = 12;
                } else if (IS_HOTSPOT_CONCURRENT_MARK_SWEEP_GC) {
                    REFERENCE_SIZE = 8;
                    OBJECT_ALIGNMENT = 8;
                    MINIMUM_OBJECT_SIZE = 24;
                    FIELD_OFFSET_ADJUSTMENT = 0;
                    OBJECT_HEADER_SIZE = 16;
                } else {
                    REFERENCE_SIZE = 8;
                    OBJECT_ALIGNMENT = 8;
                    MINIMUM_OBJECT_SIZE = 8;
                    FIELD_OFFSET_ADJUSTMENT = 0;
                    OBJECT_HEADER_SIZE = 16;
                }
            }
            else {
                if (IS_HOTSPOT_CONCURRENT_MARK_SWEEP_GC) {
                    REFERENCE_SIZE = 4;
                    OBJECT_ALIGNMENT = 8;
                    MINIMUM_OBJECT_SIZE = 16;
                    FIELD_OFFSET_ADJUSTMENT = 0;
                    OBJECT_HEADER_SIZE = 8;
                } else {
                    REFERENCE_SIZE = 4;
                    OBJECT_ALIGNMENT = 8;
                    MINIMUM_OBJECT_SIZE = 8;
                    FIELD_OFFSET_ADJUSTMENT = 0;
                    OBJECT_HEADER_SIZE = 8;
                }
            }
        }
        else if (IS_IBM) {
            if (IS_64_BIT) {
                if (IS_IBM_COMPRESSED_REFS) {
                    REFERENCE_SIZE = 4;
                    OBJECT_ALIGNMENT = 8;
                    MINIMUM_OBJECT_SIZE = 8;
                    FIELD_OFFSET_ADJUSTMENT = 0;
                    OBJECT_HEADER_SIZE = 16;
                } else {
                    REFERENCE_SIZE = 8;
                    OBJECT_ALIGNMENT = 8;
                    MINIMUM_OBJECT_SIZE = 8;
                    FIELD_OFFSET_ADJUSTMENT = 0;
                    OBJECT_HEADER_SIZE = 24;
                }
            } else {
                REFERENCE_SIZE = 4;
                OBJECT_ALIGNMENT = 8;
                MINIMUM_OBJECT_SIZE = 8;
                FIELD_OFFSET_ADJUSTMENT = 0;
                OBJECT_HEADER_SIZE = 16;
            }
        }
        else if (IS_64_BIT) {
            REFERENCE_SIZE = 8;
            OBJECT_ALIGNMENT = 8;
            MINIMUM_OBJECT_SIZE = 8;
            FIELD_OFFSET_ADJUSTMENT = 0;
            OBJECT_HEADER_SIZE = 16;
        }
        else {
            REFERENCE_SIZE = 4;
            OBJECT_ALIGNMENT = 8;
            MINIMUM_OBJECT_SIZE = 8;
            FIELD_OFFSET_ADJUSTMENT = 0;
            OBJECT_HEADER_SIZE = 8;
        }
    }

}
