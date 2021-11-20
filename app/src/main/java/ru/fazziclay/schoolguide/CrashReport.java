package ru.fazziclay.schoolguide;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import ru.fazziclay.schoolguide.util.FileUtil;

public class CrashReport {
    public static final String CRASH_REPORT_FOLDER = "/crash_reports/";
    public static final int CRASH_REPORT_VERSION = 3;
    public static final String CRASH_REPORT_PATTERN = "Crash Report! Version=%CRASH_REPORT_VERSION%\n\nThread: %ERROR_THREAD_NAME%\n\nInit StackTrace:\n%INIT_STACK_TRACE%\n\nError: %ERROR_TEXT%\nStackTrace:\n%ERROR_STACK_TRACE%\n\nSystem Information: %SYSTEM_INFORMATION%";

    String crashReportsFolded = "";
    StackTraceElement[] initStackTrace;
    StackTraceElement[] errorStackTrace;

    String finalReport = "";

    public CrashReport(Context context, Throwable throwable) {
        this.crashReportsFolded = getFolder(context);
        try {
            throw new GetInitException();
        } catch (Throwable e) {
            initStackTrace = e.getStackTrace();
        }
        error(throwable);
        notifyUser(context);
    }

    public static String getFolder(Context context) {
        return context.getExternalCacheDir().getAbsolutePath() + "/" + CRASH_REPORT_FOLDER;
    }

    private String stackTraceToString(StackTraceElement[] stackTrace) {
        StringBuilder stringBuilder = new StringBuilder();

        int i = 0;
        for (StackTraceElement e : stackTrace) {
            stringBuilder.append(
                    String.format(" * [%s] at %s.%s(%s:%s)\n",
                            i,
                            e.getClassName(),
                            e.getMethodName(),
                            e.getFileName(),
                            e.getLineNumber()
                    )
            );
            i++;
        }

        return stringBuilder.toString();
    }

    public void error(Throwable throwable) {
        errorStackTrace = throwable.getStackTrace();
        String details
                ="\n * VERSION.RELEASE : "+Build.VERSION.RELEASE
                +"\n * VERSION.INCREMENTAL : "+Build.VERSION.INCREMENTAL
                +"\n * VERSION.SDK.NUMBER : "+Build.VERSION.SDK_INT
                +"\n * BOARD : "+Build.BOARD
                +"\n * BOOTLOADER : "+Build.BOOTLOADER
                +"\n * BRAND : "+Build.BRAND
                +"\n * CPU_ABI : "+Build.CPU_ABI
                +"\n * CPU_ABI2 : "+Build.CPU_ABI2
                +"\n * DISPLAY : "+Build.DISPLAY
                +"\n * FINGERPRINT : "+Build.FINGERPRINT
                +"\n * HARDWARE : "+Build.HARDWARE
                +"\n * HOST : "+Build.HOST
                +"\n * ID : "+Build.ID
                +"\n * MANUFACTURER : "+Build.MANUFACTURER
                +"\n * MODEL : "+Build.MODEL
                +"\n * PRODUCT : "+Build.PRODUCT
                +"\n * TAGS : "+Build.TAGS
                +"\n * TIME : "+Build.TIME
                +"\n * TYPE : "+Build.TYPE
                +"\n * UNKNOWN : "+Build.UNKNOWN
                +"\n * USER : "+Build.USER;

        finalReport = CRASH_REPORT_PATTERN
                .replace("%INIT_STACK_TRACE%", stackTraceToString(initStackTrace))
                .replace("%ERROR_TEXT%", throwable.toString())
                .replace("%ERROR_STACK_TRACE%", stackTraceToString(errorStackTrace))
                .replace("%ERROR_THREAD_NAME%", Thread.currentThread().getName())
                .replace("%CRASH_REPORT_VERSION%", CRASH_REPORT_VERSION+"")
                .replace("%SYSTEM_INFORMATION%", details);

        FileUtil.write(crashReportsFolded + "/crash_report_"+(System.currentTimeMillis() / 1000)+".txt", finalReport);
        Log.e("Crash Report", finalReport);
    }

    public void notifyUser(Context context) {
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationManager notificationManager = context.getSystemService(NotificationManager.class);

                NotificationChannel channel = new NotificationChannel(SharedConstrains.CRASHREPORT_NOTIFICATION_CHANNEL_ID, "Crash Report", NotificationManager.IMPORTANCE_HIGH);
                channel.setDescription("Crash Reporter");
                notificationManager.createNotificationChannel(channel);
            }

            NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
            bigTextStyle.bigText(finalReport);
            bigTextStyle.setSummaryText("App Crash!");

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, SharedConstrains.CRASHREPORT_NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setStyle(bigTextStyle)
                    .setContentTitle("Slide down notification!")
                    .setContentTitle("Crash Report");

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            notificationManagerCompat.notify(SharedConstrains.CRASHREPORT_NOTIFICATION_ID, builder.build());
        } catch (Throwable ignored) {}
    }

    public static class GetInitException extends Exception {
        public GetInitException() {
            super("This is exception for check init crashReport stackTrace. CRASH_REPORT_VERSION="+CRASH_REPORT_VERSION);
        }
    }
}
