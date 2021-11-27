package ru.fazziclay.schoolguide.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.net.UnknownHostException;
import java.util.Locale;

import ru.fazziclay.schoolguide.BuildConfig;
import ru.fazziclay.schoolguide.CrashReport;
import ru.fazziclay.schoolguide.DownloadThread;
import ru.fazziclay.schoolguide.R;
import ru.fazziclay.schoolguide.SchoolGuide;
import ru.fazziclay.schoolguide.SharedConstrains;
import ru.fazziclay.schoolguide.data.manifest.AppVersion;
import ru.fazziclay.schoolguide.data.manifest.ManifestProvider;
import ru.fazziclay.schoolguide.data.manifest.VersionState;
import ru.fazziclay.schoolguide.databinding.ActivityUpdateCheckerBinding;

public class UpdateCheckerActivity extends AppCompatActivity {
    ActivityUpdateCheckerBinding binding;

    ManifestProvider manifestProvider = null;
    State state = State.ERROR_GENERIC;
    Exception exception = new Exception("Error generic!");
    DownloadThread.DownloadThreadInterface downloadThreadInterface = new DownloadThread.DownloadThreadInterface() {
        @Override
        public void onChangeProgress(int progress, int max) {
            runOnUiThread(() -> {
                binding.downloadingProgress.setProgress((int) ((((float) progress) / ((float) max)) * 100));
                binding.downloadingTitle.setText(R.string.updateCenter_downloading_title);
            });
        }

        @Override
        public void onEnded(Exception exception, String filePath) {
            runOnUiThread(() -> {
                if (exception != null) {
                    binding.downloadingProgress.setProgress(0);
                    binding.downloadButton.setEnabled(true);
                    if (exception.getClass().equals(DownloadThread.UserDownloadingInterrupted.class)) {
                        binding.downloading.setVisibility(View.GONE);
                    } else {
                        binding.downloadingTitle.setText(getString(R.string.updateCenter_downloading_title_error));
                        binding.downloadingCancel.setEnabled(false);
                    }
                    return;
                }
                binding.downloadingTitle.setText(R.string.updateCenter_downloading_title_success);
                binding.downloadingInstall.setVisibility(View.VISIBLE);
                binding.downloadingCancel.setVisibility(View.GONE);
                installApk(tempDownloadedFilePath);
            });
        }
    };
    DownloadThread downloadThread = null;
    String tempDownloadedFilePath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            binding = ActivityUpdateCheckerBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());
            setTitle(R.string.activityTitle_updateChecker);

            SchoolGuide.fixInstance(getApplicationContext());
            manifestProvider = SchoolGuide.getInstance().getManifestProvider();

            preInit();

            manifestProvider.updateForGlobal((e, manifestProvider) -> {
                exception = e;
                try {
                    initState();
                } catch (Throwable throwable) {
                    new CrashReport(this, throwable);
                    finish();
                }
                runOnUiThread(() -> {
                    try {
                        initLayout();
                    } catch (Throwable throwable) {
                        new CrashReport(this, throwable);
                        finish();
                    }
                });
            });

        } catch (Throwable throwable) {
            new CrashReport(this, throwable);
            finish();
        }
    }

    private void preInit() {
        binding.downloadingProgress.setMax(100);
        binding.downloadingCancel.setOnClickListener(ignore -> downloadThread.cancel());
    }

    private void initState() {
        if (manifestProvider.isTechnicalWorks()) {
            state = State.TECHNICAL_WORKS;
            return;
        }

        if (exception != null) {
            if (exception.getClass().equals(UnknownHostException.class)) {
                state = State.ERROR_NOT_NETWORK_CONNECTION;
            } else {
                state = State.ERROR_GENERIC;
            }
            return;
        }

        VersionState versionState = manifestProvider.getAppVersionState();
        if (versionState == VersionState.LATEST) state = State.VERSION_LATEST;
        if (versionState == VersionState.OUTDATED) state = State.VERSION_OUTDATED;
        if (versionState == VersionState.UNKNOWN) state = State.VERSION_UNKNOWN;
    }

    private void initLayout() {
        binding.loadingGroup.setVisibility(View.GONE);
        binding.mainGroup.setVisibility(View.VISIBLE);

        if (state == State.TECHNICAL_WORKS) {
            binding.title.setText(R.string.updateCenter_title_technicalWorks);
            binding.text.setText(getString(R.string.updateCenter_text_technicalWorks));

        } else if (state.isError()) {
            binding.title.setText(R.string.updateCenter_title_error);
            if (state == State.ERROR_GENERIC) binding.text.setText(getString(R.string.updateCenter_text_error_generic, exception.toString()));
            if (state == State.ERROR_NOT_NETWORK_CONNECTION) binding.text.setText(R.string.updateCenter_text_error_noNetworkConnection);

        } else if (state == State.VERSION_UNKNOWN) {
            binding.title.setText(R.string.abc_error);
            binding.text.setText(null);

        } else if (state == State.VERSION_LATEST) {
            binding.title.setText(R.string.updateCenter_title_latest);
            binding.text.setText(R.string.updateCenter_text_latest);

        } else if (state.isUpdateAvailable()) {
            AppVersion latestVersion = manifestProvider.getLatestAppVersion();
            tempDownloadedFilePath = getExternalCacheDir().getAbsolutePath() + "/downloads/update_v"+latestVersion.getCode()+".apk";
            binding.downloadingInstall.setOnClickListener(ignore -> installApk(tempDownloadedFilePath));

            binding.title.setText(R.string.updateCenter_title_updateAvailable);
            binding.text.setText(latestVersion.getChangeLog(Locale.getDefault().getLanguage())
                    .replace("%CLIENT_VERSION_CODE%", String.valueOf(SharedConstrains.APPLICATION_VERSION_CODE))
                    .replace("%CLIENT_VERSION_NAME%", SharedConstrains.APPLICATION_VERSION_NAME)
            );
            if (latestVersion.getDownloadUrl() != null) {
                binding.downloadButton.setVisibility(View.VISIBLE);
                binding.downloadButton.setOnClickListener(button -> {
                    button.setEnabled(false);
                    binding.downloadingCancel.setEnabled(true);
                    binding.downloading.setVisibility(View.VISIBLE);

                    downloadThread = new DownloadThread(
                            latestVersion.getDownloadUrl(),
                            tempDownloadedFilePath,
                            downloadThreadInterface
                    );

                    downloadThread.start();
                });
            }
        }
    }

    private void installApk(String filePath) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", new File(filePath)), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);

        } catch (Throwable e) {
            Toast.makeText(this, getString(R.string.abc_error), Toast.LENGTH_SHORT).show();
            new CrashReport(this, e);
        }
    }

    private enum State {
        ERROR_GENERIC,
        ERROR_NOT_NETWORK_CONNECTION,
        TECHNICAL_WORKS,
        VERSION_LATEST,
        VERSION_UNKNOWN,
        VERSION_OUTDATED;

        public boolean isError() {
            return (this == ERROR_GENERIC || this == ERROR_NOT_NETWORK_CONNECTION);
        }

        public boolean isUpdateAvailable() {
            return (this == VERSION_OUTDATED);
        }
    }
}