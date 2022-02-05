package ru.fazziclay.schoolguide.util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Набор утилит для работы с файлами
 * **/
public class FileUtil {
    public static void createDirIfNotExists(String path) {
        File file = new File(fixPathSeparator(path));
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    private static void createNew(String path) {
        path = fixPathSeparator(path);
        int lastSep = path.lastIndexOf(File.separator);
        if (lastSep > 0) {
            String dirPath = path.substring(0, lastSep);
            createDirIfNotExists(dirPath);
            File folder = new File(dirPath);
            folder.mkdirs();
        }

        File file = new File(fixPathSeparator(path));

        try {
            if (!file.exists()) file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String read(File file, String defaultValue) {
        return read(file.getAbsolutePath(), defaultValue);
    }

    public static String read(File file) {
        return read(file.getAbsolutePath());
    }

    public static String read(String path, String defaultValue) {
        String content = read(path);
        if (content != null && content.equals("")) {
            return defaultValue;
        }
        return content;
    }

    public static String read(String path) {
        path = fixPathSeparator(path);
        try {
            createNew(path);

            StringBuilder stringBuilder = new StringBuilder();
            FileReader fileReader = new FileReader(path);

            char[] buff = new char[1024];
            int length;

            while ((length = fileReader.read(buff)) > 0) {
                stringBuilder.append(new String(buff, 0, length));
            }

            try {
                fileReader.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void write(File file, String content) {
        write(file.getAbsolutePath(), content);
    }

    public static void write(String path, String content) {
        path = fixPathSeparator(path);
        try {
            createNew(path);
            FileWriter fileWriter = new FileWriter(path, false);
            fileWriter.write(content);
            fileWriter.flush();
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isExist(String path) {
        return new File(fixPathSeparator(path)).isFile();
    }

    public static String fixPathSeparator(String path) {
        return path.replace("/", File.separator).replace("\\", File.separator);
    }

    public static File[] getFilesList(String path) {
        createDirIfNotExists(fixPathSeparator(path));
        File file = new File(fixPathSeparator(path));
        return file.listFiles();
    }

    public static void deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String child : children) {
                deleteDir(new File(dir, child));
            }
            dir.delete();

        } else if (dir != null && dir.isFile()) {
            dir.delete();
        }
    }
}
