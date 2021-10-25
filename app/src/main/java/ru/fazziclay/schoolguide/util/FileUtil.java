package ru.fazziclay.schoolguide.util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtil {
    public static void createDirIfNotExists(String path) {
        File file = new File(fixPathSeparator(path));
        if (!file.exists()) {
            //noinspection ResultOfMethodCallIgnored
            file.mkdirs();
        }
    }

    private static void createNew(String path) {
        int lastSep = fixPathSeparator(path).lastIndexOf(File.separator);
        if (lastSep > 0) {
            String dirPath = fixPathSeparator(path).substring(0, lastSep);
            createDirIfNotExists(dirPath);
            File folder = new File(dirPath);
            folder.mkdirs();
        }

        File file = new File(fixPathSeparator(path));

        try {
            if (!file.exists())
                //noinspection ResultOfMethodCallIgnored
                file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String read(String path, String defaultValue) {
        String content = read(path);
        if (content != null && content.equals("")) {
            return defaultValue;
        }
        return content;
    }

    public static String read(String path) {
        try {
            createNew(fixPathSeparator(path));

            StringBuilder stringBuilder = new StringBuilder();
            FileReader fileReader = null;

            try {
                fileReader = new FileReader(fixPathSeparator(path));

                char[] buff = new char[1024];
                int length;

                while ((length = fileReader.read(buff)) > 0) {
                    stringBuilder.append(new String(buff, 0, length));
                }

            } catch (IOException e) {
                e.printStackTrace();

            } finally {
                if (fileReader != null) {
                    try {
                        fileReader.close();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            return stringBuilder.toString();
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean write(String path, String content) {
        try {
            createNew(fixPathSeparator(path));
            FileWriter fileWriter = new FileWriter(fixPathSeparator(path), false);
            fileWriter.write(content);
            fileWriter.flush();
            fileWriter.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isExist(String path) {
        return new File(path.replace("/", File.separator)).isFile();
    }

    public static String fixPathSeparator(String path) {
        return path.replace("/", File.separator).replace("\\", File.separator);
    }

    public static File[] getFilesList(String path) {
        createDirIfNotExists(path);
        File file = new File(path);
        return file.listFiles();
    }
}
