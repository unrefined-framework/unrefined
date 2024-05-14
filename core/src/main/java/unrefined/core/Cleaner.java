/* Copyright (c) 2021, Matthias Bläsing, All Rights Reserved
 *
 * The contents of this file is dual-licensed under 2
 * alternative Open Source/Free licenses: LGPL 2.1 or later and
 * Apache License 2.0. (starting with JNA version 4.0.0).
 *
 * You can freely decide which license you want to apply to
 * the project.
 *
 * You may obtain a copy of the LGPL License at:
 *
 * http://www.gnu.org/licenses/licenses.html
 *
 * A copy is also included in the downloadable source code package
 * containing JNA, in file "LGPL2.1".
 *
 * You may obtain a copy of the Apache License at:
 *
 * http://www.apache.org/licenses/
 *
 * A copy is also included in the downloadable source code package
 * containing JNA, in file "AL2.0".
 */

package unrefined.core;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;

/**
 * Implement ReferenceQueue based cleanup of resources associated with GCed
 * objects. It replaces the {@code Object#finalize} based resource deallocation
 * that is deprecated for removal from the JDK.
 */
public class Cleaner {

    private static final Cleaner INSTANCE = new Cleaner();
    public static Cleaner getCleaner() {
        return INSTANCE;
    }

    private final ReferenceQueue<Object> referenceQueue;
    private Thread cleanerThread;
    private CleanerReference firstCleanable;

    private Cleaner() {
        referenceQueue = new ReferenceQueue<>();
    }

    public synchronized Cleanable register(Object obj, Runnable cleanupTask) {
        // The important side effect is the PhantomReference, that is yielded
        // after the referent is GCed
        return add(new CleanerReference(this, obj, referenceQueue, cleanupTask));
    }

    private synchronized CleanerReference add(CleanerReference ref) {
        synchronized (referenceQueue) {
            if (firstCleanable == null) {
                firstCleanable = ref;
            } else {
                ref.setNext(firstCleanable);
                firstCleanable.setPrevious(ref);
                firstCleanable = ref;
            }
            if (cleanerThread == null) {
                //Logger.getLogger(Cleaner.class.getName()).log(Level.FINE, "Starting CleanerThread");
                cleanerThread = new CleanerThread();
                cleanerThread.start();
            }
            return ref;
        }
    }

    private synchronized boolean remove(CleanerReference ref) {
        synchronized (referenceQueue) {
            boolean inChain = false;
            if (ref == firstCleanable) {
                firstCleanable = ref.getNext();
                inChain = true;
            }
            if (ref.getPrevious() != null) {
                ref.getPrevious().setNext(ref.getNext());
            }
            if (ref.getNext() != null) {
                ref.getNext().setPrevious(ref.getPrevious());
            }
            if (ref.getPrevious() != null || ref.getNext() != null) {
                inChain = true;
            }
            ref.setNext(null);
            ref.setPrevious(null);
            return inChain;
        }
    }

    private static class CleanerReference extends PhantomReference<Object> implements Cleanable {
        private final Cleaner cleaner;
        private final Runnable cleanupTask;
        private CleanerReference previous;
        private CleanerReference next;

        public CleanerReference(Cleaner cleaner, Object referent, ReferenceQueue<? super Object> q, Runnable cleanupTask) {
            super(referent, q);
            this.cleaner = cleaner;
            this.cleanupTask = cleanupTask;
        }

        @Override
        public void clean() {
            if (cleaner.remove(this)) {
                cleanupTask.run();
            }
        }

        CleanerReference getPrevious() {
            return previous;
        }

        void setPrevious(CleanerReference previous) {
            this.previous = previous;
        }

        CleanerReference getNext() {
            return next;
        }

        void setNext(CleanerReference next) {
            this.next = next;
        }
    }

    public interface Cleanable {
        void clean();
    }

    private class CleanerThread extends Thread {

        private static final long CLEANER_LINGER_TIME = 30000;

        public CleanerThread() {
            super("Unrefined Cleaner");
            setDaemon(true);
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Reference<?> ref = referenceQueue.remove(CLEANER_LINGER_TIME);
                    if (ref instanceof CleanerReference) {
                        ((CleanerReference) ref).clean();
                    } else if (ref == null) {
                        synchronized (referenceQueue) {
                            //Logger logger = Logger.getLogger(Cleaner.class.getName());
                            if (firstCleanable == null) {
                                cleanerThread = null;
                                //logger.log(Level.FINE, "Shutting down CleanerThread");
                                break;
                            }/* else if (logger.isLoggable(Level.FINER)) {
                                StringBuilder registeredCleaners = new StringBuilder();
                                for(CleanerReference cleanerRef = firstCleanable; cleanerRef != null; cleanerRef = cleanerRef.next) {
                                    if(registeredCleaners.length() != 0) {
                                        registeredCleaners.append(", ");
                                    }
                                    registeredCleaners.append(cleanerRef.cleanupTask.toString());
                                }
                                logger.log(Level.FINER, "Registered Cleaners: {0}", registeredCleaners.toString());
                            }*/
                        }
                    }
                } catch (InterruptedException ex) {
                    // Can be raised on shutdown. If anyone else messes with
                    // our reference queue, well, there is no way to separate
                    // the two cases.
                    // https://groups.google.com/g/jna-users/c/j0fw96PlOpM/m/vbwNIb2pBQAJ
                    break;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    //Logger.getLogger(Cleaner.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

}
