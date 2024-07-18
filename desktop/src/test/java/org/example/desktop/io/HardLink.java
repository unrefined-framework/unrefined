package org.example.desktop.io;

import unrefined.Lifecycle;
import unrefined.app.Log;
import unrefined.io.file.FileSystem;

import java.io.File;
import java.io.IOException;

public class HardLink {

    public static void main(String[] args) throws IOException {
        Lifecycle.onMain(args);

        FileSystem fs = FileSystem.getInstance();
        Log log = Log.defaultInstance();

        File link = new File("link");
        File target = new File("target");
        fs.deleteOnExit(link);
        fs.deleteOnExit(target);

        fs.createFile(target);
        fs.createHardLink(link, target);

        log.info("Unrefined FS", "HardLinks: " + fs.getHardLinkCount(link));
    }

}
