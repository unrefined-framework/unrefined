package unrefined.internal;

import unrefined.util.NotInstantiableError;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public final class FileUtils {

    private FileUtils() {
        throw new NotInstantiableError(FileUtils.class);
    }

    /**
     * Deletes the contents of {@code directory}. Throws an IOException if any file
     * could not be deleted, or if {@code directory} is not a readable directory.
     */
    public static void deleteContents(File directory) throws IOException {
        File[] files = directory.listFiles();
        if (files == null) throw new IOException("not a readable directory: " + directory);
        for (File file : files) {
            if (file.isDirectory()) deleteContents(file);
            if (!file.delete()) throw new IOException("failed to delete file: " + file);
        }
    }

    public static boolean createDirectoryIfNotExists(File directory) {
        if (directory.exists()) return directory.isDirectory();
        else return directory.mkdirs();
    }

    public static boolean createFileIfNotExists(File file) {
        try {
            if (file.exists() && file.isFile()) return true;
            else {
                File parentFile = file.getParentFile();
                if (!parentFile.exists()) {
                    if (!parentFile.mkdirs()) return false;
                }
                return file.createNewFile();
            }
        }
        catch (IOException e) {
            return false;
        }
    }

    public static String removeStartSeparator(String text) {
        return text.charAt(0) == File.separatorChar ? text.substring(1) : text;
    }

    public static List<URI> fileListToURIList(List<File> fileList) {
        URI[] uriArray = new URI[fileList.size()];
        for (int i = 0; i < uriArray.length; i ++) {
            uriArray[i] = fileList.get(i).toURI();
        }
        return Arrays.asList(uriArray);
    }

    public static String normalizeToUNIXStyle(String pathname) {
        if (pathname == null) return null;
        return pathname.toLowerCase(Locale.ENGLISH).replaceAll(" ", "-");
    }

}
