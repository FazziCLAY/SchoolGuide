package ru.fazziclay.schoolguide;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import ru.fazziclay.schoolguide.util.FileUtil;

public class DownloadThread extends Thread {
    String downloadUrl = null;
    String savePath = null;
    boolean isCanceled = false;

    URL url = null;
    URLConnection connection = null;
    InputStream inputStream = null;
    OutputStream outputStream = null;

    DownloadThreadInterface downloadThreadInterface = null;

    public DownloadThread(String downloadUrl, String savePath, DownloadThreadInterface downloadThreadInterface) {
        this.downloadUrl = downloadUrl;
        this.savePath = savePath;
        this.downloadThreadInterface = downloadThreadInterface;
    }

    public void cancel() {
        isCanceled = true;
    }

    @Override
    public void run() {
        try {
            FileUtil.write(savePath, "");

            url = new URL(downloadUrl);
            connection = url.openConnection();
            connection.connect();

            int lengthOfFile = connection.getContentLength();
            int downloaded = 0;
            int tmp = 0;

            inputStream = new BufferedInputStream(url.openStream(), 8192);
            outputStream = new FileOutputStream(savePath);

            byte[] data = new byte[1024];

            while (!isCanceled && (tmp = inputStream.read(data)) != -1) {
                downloaded += tmp;
                downloadThreadInterface.onChangeProgress(downloaded, lengthOfFile);
                outputStream.write(data, 0, tmp);
            }

            outputStream.close();
            inputStream.close();

            downloadThreadInterface.onEnded(isCanceled ? new UserDownloadingInterrupted() : null, savePath);
        } catch (Exception e) {
            downloadThreadInterface.onEnded(e, savePath);
        }
    }

    public interface DownloadThreadInterface {
        void onChangeProgress(int progress, int max);
        void onEnded(Exception exception, String filePath);
    }

    public static class UserDownloadingInterrupted extends Exception {}
}
