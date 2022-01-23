package ru.fazziclay.schoolguide;

import static android.view.View.GONE;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.net.UnknownHostException;
import java.util.HashMap;

import ru.fazziclay.schoolguide.app.Manifest;
import ru.fazziclay.schoolguide.app.SchoolGuideApp;
import ru.fazziclay.schoolguide.databinding.ActivityUpdateCenterBinding;
import ru.fazziclay.schoolguide.util.ColorUtil;
import ru.fazziclay.schoolguide.util.NetworkUtil;

public class UpdateCenterActivity extends AppCompatActivity {
    public static final int NOTIFICATION_ID = 2000;
    public static final String NOTIFICATION_CHANNEL_ID = "updatecenter";

    private ActivityUpdateCenterBinding binding;
    private Gson gson;

    public static Intent getLaunchIntent(@NonNull Context context) {
        return new Intent(context, UpdateCenterActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SchoolGuideApp.get(this);
        gson = new Gson();

        try {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } catch (Exception ignored) {}

        binding = ActivityUpdateCenterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setTitle(R.string.updatecenter_activityTitle);

        new Thread(() -> {
            Status status = load();
            runOnUiThread(() -> updateUI(status));
        }).start();
    }

    public void updateUI(Status status) {
        binding.loadingGroup.setVisibility(GONE);
        binding.mainGroup.setVisibility(View.VISIBLE);
        binding.actionButton.setVisibility(GONE);

        if (status.latestVersion == null && status == Status.OUTDATED) {
            status = Status.ERROR.setException(new IllegalStateException("latestVersion is null"));
        }
        if (status == Status.ERROR) {
            binding.title.setText(R.string.updatecenter_error_title);
            if (status.exception instanceof UnknownHostException) {
                binding.text.setText(R.string.updatecenter_error_unknownHost_text);
                binding.actionButton.setVisibility(View.VISIBLE);
                binding.actionButton.setText(R.string.updatecenter_error_unknownHost_reload);
                binding.actionButton.setOnClickListener(ignore -> {
                    startActivity(new Intent(this, UpdateCenterActivity.class));
                    finish();
                });
            } else if (status.exception instanceof FileNotFoundException) {
                binding.text.setText(R.string.updatecenter_error_fileNotFound_text);
            } else {
                binding.text.setText(getString(R.string.updatecenter_error_generic_text, status.exception.toString()));
            }
            setupColorizeText();
            setupLinkableText();
            return;
        }

        if (status == Status.UPDATED) {
            binding.title.setText(R.string.updatecenter_updated_title);
            binding.title.setTextColor(Color.GREEN);
            binding.text.setText(R.string.updatecenter_updated_text);
            setupColorizeText();
            return;
        }

        if (status == Status.OUTDATED) {
            binding.title.setText(getString(R.string.updatecenter_outdated_title, status.latestVersion.getName()));
            binding.text.setText(status.latestVersion.getChangelog() == null ? getString(R.string.updatecenter_outdated_changelogEmpty_text) : status.latestVersion.getChangelog()
                    .replace("$(CLIENT_VERSION_CODE)", SharedConstrains.APPLICATION_VERSION_CODE+"")
                    .replace("$(CLIENT_VERSION_NAME)", SharedConstrains.APPLICATION_VERSION_NAME)
            );
            setupColorizeText();
            setupLinkableText();
            binding.actionButton.setVisibility(View.VISIBLE);
            binding.actionButton.setText(R.string.updatecenter_outdated_openDownloadLink);

            String downloadUrl = status.latestVersion.getDownloadUrl();
            binding.actionButton.setOnClickListener(ignore -> {
                try {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(downloadUrl));
                    startActivity(browserIntent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(this, R.string.updatecenter_outdated_downloadError_activityNotFound, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void setupLinkableText() {
        binding.text.setLinksClickable(true);
        binding.text.setLinkTextColor(Color.CYAN);
        Linkify.addLinks(binding.text, Linkify.ALL);
    }

    public void setupColorizeText() {
        binding.text.setText(ColorUtil.colorize(binding.text.getText().toString(), Color.LTGRAY, Color.TRANSPARENT, Typeface.NORMAL));
    }

    public Status load() {
        final int debug = -1;
        if (debug != 0) {
            if (debug == 1) {
                return Status.ERROR.setException(new UnknownHostException());
            } else if (debug == 2) {
                return Status.ERROR.setException(new FileNotFoundException());
            } else if (debug == 3) {
                return Status.ERROR.setException(new RuntimeException());
            } else if (debug == 4) {
                return Status.UPDATED;
            } else if (debug == 5) {
                HashMap<String, String> change = new HashMap<>();
                change.put("default", "Пофикшены баги:\nКорова прилипала к стене\n$[-#ff0000]Вылет из за краша$[-reset]\n\nПодпишись: https://youtube.com/\n\nПривет $[@italic;-#ff0000;=#00cccc]owoPeef  $[@reset;-reset;=reset]& $[@italic;-#00ff00;=#cc00cc]_Dane4ka_");
                HashMap<String, String> download = new HashMap<>();
                download.put("release", "https://google.com");
                download.put("debug", "https://yandex.ru");
                Manifest.ManifestVersion v = new Manifest.ManifestVersion(20, "0.6 - ReWriTTen ", change, download);
                return Status.OUTDATED.setLatestVersion(v);
            }
        }
        try {
            String fileContent = NetworkUtil.parseTextPage(SharedConstrains.VERSION_MANIFEST_V2);
            Manifest manifest = gson.fromJson(fileContent, Manifest.class);

            if (manifest.latest.release.getCode() > SharedConstrains.APPLICATION_VERSION_CODE) {
                return Status.OUTDATED
                        .setLatestVersion(manifest.latest.release);
            } else {
                return Status.UPDATED;
            }
        } catch (Exception e) {
            return Status.ERROR
                    .setException(e);
        }
    }

    enum Status {
        ERROR,
        UPDATED,
        OUTDATED;

        public Manifest.ManifestVersion latestVersion;
        public Exception exception;

        public Status setException(Exception e) {
            this.exception = e;
            return this;
        }

        public Status setLatestVersion(Manifest.ManifestVersion s) {
            this.latestVersion = s;
            return this;
        }
    }
}