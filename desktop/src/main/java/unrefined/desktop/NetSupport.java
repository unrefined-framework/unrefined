package unrefined.desktop;

import unrefined.runtime.DesktopClientSocketChannel;
import unrefined.runtime.DesktopDatagramSocketChannel;
import unrefined.runtime.DesktopInet4Address;
import unrefined.runtime.DesktopInet6Address;
import unrefined.runtime.DesktopServerSocketChannel;
import unrefined.util.NotInstantiableError;
import unrefined.util.UnexpectedError;

import java.io.FileDescriptor;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.Proxy;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.DatagramChannel;
import java.nio.channels.Pipe;
import java.nio.channels.SelectableChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

@SuppressWarnings("DiscouragedPrivateApi")
public final class NetSupport {

    private NetSupport() {
        throw new NotInstantiableError(NetSupport.class);
    }

    private static final Method openConnection;
    static {
        Method method;
        try {
            method = URL.class.getDeclaredMethod("openConnection", Proxy.class);
        } catch (NoSuchMethodException e) {
            method = null;
        }
        openConnection = method;
    }

    public static URLConnection openConnection(URL url, Proxy proxy) throws IOException {
        if (openConnection == null || proxy == null) return url.openConnection();
        else {
            try {
                return (URLConnection) ReflectionSupport.invokeObjectMethod(url, openConnection, proxy);
            } catch (InvocationTargetException e) {
                Throwable target = e.getTargetException();
                if (target instanceof IOException) throw (IOException) target;
                else if (target instanceof RuntimeException) throw (RuntimeException) target;
                else if (target instanceof Error) throw (Error) target;
                else throw new UnexpectedError(target);
            }
        }
    }

    public static unrefined.net.InetAddress toUnrefinedInetAddress(InetAddress address) {
        if (address instanceof Inet4Address) return new DesktopInet4Address((Inet4Address) address);
        else if (address instanceof Inet6Address) return new DesktopInet6Address((Inet6Address) address);
        else throw new IllegalArgumentException("Illegal internet address: " + address);
    }

    public static InetAddress toInetAddress(Object address) {
        if (address instanceof unrefined.net.Inet4Address) return ((DesktopInet4Address) address).getInet4Address();
        else if (address instanceof unrefined.net.Inet6Address) return ((DesktopInet6Address) address).getInet6Address();
        else throw new IllegalArgumentException("Illegal internet address: " + address);
    }

    public static unrefined.nio.channels.SelectableChannel toUnrefinedSelectableChannel(SelectableChannel channel) {
        if (channel instanceof SocketChannel) return new DesktopClientSocketChannel((SocketChannel) channel);
        else if (channel instanceof ServerSocketChannel) return new DesktopServerSocketChannel((ServerSocketChannel) channel);
        else if (channel instanceof DatagramChannel) return new DesktopDatagramSocketChannel((DatagramChannel) channel);
        else throw new IllegalArgumentException("Illegal selectable channel: " + channel);
    }

    private static final Field implFieldSocket;
    private static final Field implFieldServerSocket;
    private static final Field implFieldDatagramSocket;
    private static final Field delegateField;
    static {
        Field field;
        try {
            field = Socket.class.getDeclaredField("impl");
        } catch (NoSuchFieldException e) {
            field = null;
        }
        implFieldSocket = field;
        try {
            field = ServerSocket.class.getDeclaredField("impl");
        } catch (NoSuchFieldException e) {
            field = null;
        }
        implFieldServerSocket = field;
        try {
            field = DatagramSocket.class.getDeclaredField("impl");
        } catch (NoSuchFieldException e) {
            field = null;
        }
        implFieldDatagramSocket = field;
        if (implFieldDatagramSocket == null) {
            try {
                field = DatagramSocket.class.getDeclaredField("delegate");
            } catch (NoSuchFieldException ignored) {
            }
        }
        else field = null;
        delegateField = field;
    }

    public static FileDescriptor getFD(Socket socket) throws IOException {
        SocketChannel channel = socket.getChannel();
        if (channel != null) return getFD(channel);
        else {
            if (implFieldSocket == null) throw new IOException();
            else {
                Object impl = ReflectionSupport.getObjectField(socket, implFieldSocket);
                if (impl == null) throw new IOException();
                else {
                    try {
                        return (FileDescriptor) ReflectionSupport.getObjectField(impl, impl.getClass().getDeclaredField("fd"));
                    } catch (NoSuchFieldException e) {
                        throw new IOException();
                    }
                }
            }
        }
    }

    public static FileDescriptor getFD(ServerSocket socket) throws IOException {
        ServerSocketChannel channel = socket.getChannel();
        if (channel != null) return getFD(channel);
        else {
            if (implFieldServerSocket == null) throw new IOException();
            else {
                Object impl = ReflectionSupport.getObjectField(socket, implFieldServerSocket);
                if (impl == null) throw new IOException();
                else {
                    try {
                        return (FileDescriptor) ReflectionSupport.getObjectField(impl, impl.getClass().getDeclaredField("fd"));
                    } catch (NoSuchFieldException e) {
                        throw new IOException();
                    }
                }
            }
        }
    }

    public static FileDescriptor getFD(DatagramSocket socket) throws IOException {
        DatagramChannel channel = socket.getChannel();
        if (channel != null) return getFD(channel);
        else {
            if (implFieldServerSocket == null) {
                if (delegateField == null) throw new IOException();
                else {
                    Object delegate = ReflectionSupport.getObjectField(socket, delegateField);
                    if (delegate == null) throw new IOException();
                    else {
                        try {
                            Object impl = ReflectionSupport.getObjectField(socket, delegate.getClass().getDeclaredField("impl"));
                            if (impl == null) throw new IOException();
                            else return (FileDescriptor) ReflectionSupport.getObjectField(impl, impl.getClass().getDeclaredField("fd"));
                        } catch (NoSuchFieldException e) {
                            throw new IOException();
                        }
                    }
                }
            }
            else {
                Object impl = ReflectionSupport.getObjectField(socket, implFieldServerSocket);
                if (impl == null) throw new IOException();
                else {
                    try {
                        return (FileDescriptor) ReflectionSupport.getObjectField(impl, impl.getClass().getDeclaredField("fd"));
                    } catch (NoSuchFieldException e) {
                        throw new IOException();
                    }
                }
            }
        }
    }

    public static FileDescriptor getFD(SocketChannel channel) throws IOException {
        try {
            return (FileDescriptor) ReflectionSupport.getObjectField(channel, channel.getClass().getDeclaredField("fd"));
        } catch (NoSuchFieldException e) {
            throw new IOException();
        }
    }

    public static FileDescriptor getFD(ServerSocketChannel channel) throws IOException {
        try {
            return (FileDescriptor) ReflectionSupport.getObjectField(channel, channel.getClass().getDeclaredField("fd"));
        } catch (NoSuchFieldException e) {
            throw new IOException();
        }
    }

    public static FileDescriptor getFD(DatagramChannel channel) throws IOException {
        try {
            return (FileDescriptor) ReflectionSupport.getObjectField(channel, channel.getClass().getDeclaredField("fd"));
        } catch (NoSuchFieldException e) {
            throw new IOException();
        }
    }

    public static FileDescriptor getFD(Pipe.SourceChannel channel) throws IOException {
        try {
            return (FileDescriptor) ReflectionSupport.getObjectField(channel, channel.getClass().getDeclaredField("fd"));
        } catch (NoSuchFieldException e) {
            throw new IOException();
        }
    }

    public static FileDescriptor getFD(Pipe.SinkChannel channel) throws IOException {
        try {
            return (FileDescriptor) ReflectionSupport.getObjectField(channel, channel.getClass().getDeclaredField("fd"));
        } catch (NoSuchFieldException e) {
            throw new IOException();
        }
    }

}
