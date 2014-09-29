package org.flowdev.flowparser;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

public class TestUtils {
    private static final String UTF8 = "UTF8";
    private static final int BUF_SIZE = 8192;

    public static String readResource(String name) throws IOException {
        InputStream stream = TestUtils.class.getClassLoader().getResourceAsStream(name);
        return readStream(stream);
    }

    public static String readStream(InputStream stream) throws IOException {
        if (stream == null) {
            return null;
        }
        byte[] buf = new byte[BUF_SIZE];
        StringBuilder sb = new StringBuilder(BUF_SIZE);

        for (int num = 0; num >= 0; num = stream.read(buf, 0, BUF_SIZE)) {
            sb.append(new String(buf, 0, num, UTF8));
        }

        return sb.toString();
    }

    public static String readFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(getPath(path));
        return new String(bytes, UTF8);
    }

    public static void writeFile(String path, String content)
            throws IOException {
        Files.write(getPath(path), content.getBytes(UTF8));
    }

    public static void deleteFile(String path) throws IOException {
        Files.deleteIfExists(getPath(path));
    }

    private static Path getPath(String path) {
        return FileSystems.getDefault().getPath(path);
    }
}
