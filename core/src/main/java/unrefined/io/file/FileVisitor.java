package unrefined.io.file;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public interface FileVisitor {

    enum Result {
        CONTINUE,
        TERMINATE,
        SKIP_SUBTREE,
        SKIP_SIBLINGS
    }

    abstract class Adapter implements FileVisitor {
        @Override
        public Result preVisitDirectory(File directory) throws IOException {
            Objects.requireNonNull(directory);
            return Result.CONTINUE;
        }
        @Override
        public Result visitFile(File file, IOException exception) throws IOException {
            Objects.requireNonNull(file);
            if (exception != null) throw exception;
            else return Result.CONTINUE;
        }
        @Override
        public Result postVisitDirectory(File directory, IOException exception) throws IOException {
            Objects.requireNonNull(directory);
            if (exception != null) throw exception;
            else return Result.CONTINUE;
        }
    }

    Result preVisitDirectory(File directory) throws IOException;
    Result visitFile(File file, IOException exception) throws IOException;
    Result postVisitDirectory(File directory, IOException exception) throws IOException;

}