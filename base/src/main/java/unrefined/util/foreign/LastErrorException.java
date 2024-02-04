package unrefined.util.foreign;

public class LastErrorException extends RuntimeException {

    private static final long serialVersionUID = -5788617330996733844L;

    private final int errno;

    public LastErrorException(int errno) {
        super(Foreign.getInstance().getErrorString(errno));
        this.errno = errno;
    }

    public int getErrno() {
        return errno;
    }

}
