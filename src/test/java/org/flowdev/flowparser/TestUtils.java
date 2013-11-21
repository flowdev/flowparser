package org.flowdev.flowparser;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

public class TestUtils {
    public static final String UTF8 = "UTF8";

    public static String readFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(getPath(path));
        return new String(bytes, UTF8);
    }

    public static void writeFile(String path, String content)
            throws IOException {
        Files.write(getPath(path), content.getBytes(UTF8));
    }

    public static void copyFile(String src, String dst) throws IOException {
        Files.copy(getPath(src), getPath(dst));
    }

    public static void deleteFile(String path) throws IOException {
        Files.deleteIfExists(getPath(path));
    }

    private static Path getPath(String path) {
        return FileSystems.getDefault().getPath(path);
    }
}
