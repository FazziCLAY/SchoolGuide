package ru.fazziclay.schoolguide;

import static android.view.View.GONE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.annotation.SuppressLint;
import android.app.Activity;
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
    ActivityUpdateCenterBinding binding;
    SchoolGuideApp app;
    Gson gson;
    Manifest manifest;

    public static void open(@NonNull Activity activity) {
        activity.startActivity(new Intent(activity, UpdateCenterActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = SchoolGuideApp.get(this);
        gson = new Gson();

        try {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } catch (Exception ignored) {}

        binding = ActivityUpdateCenterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setTitle("SchoolGuide - Update Center"); // TODO: 2022-01-21 make translatable

        new Thread(() -> {
            Status status = load();
            runOnUiThread(() -> updateUI(status));
        }).start();
    }

    @SuppressLint("SetTextI18n")
    public void updateUI(Status status) {
        binding.loadingGroup.setVisibility(GONE);
        binding.mainGroup.setVisibility(View.VISIBLE);
        binding.actionButton.setVisibility(GONE);

        if (status == Status.ERROR) {
            // TODO: 2022-01-21 make translatable
            binding.title.setText("Ошибка :(");
            if (status.exception instanceof UnknownHostException) {
                binding.text.setText("$[-#ff2222]Отсутствует подключение к $[@bold]интернету$[@reset]!");
                binding.actionButton.setVisibility(View.VISIBLE);
                binding.actionButton.setText("Перезагрузить");
                binding.actionButton.setOnClickListener(ignore -> {
                    startActivity(new Intent(this, UpdateCenterActivity.class));
                    finish();
                });
            } else if (status.exception instanceof FileNotFoundException) {
                binding.text.setText("$[@bold;-#ff0000]Сервис закрыт :($[@reset;-reset]\nПриложение не смогло найти последнюю версию, $[@bold;-#dddddd]очень рекомендуем зайти на страницу проекта$[@reset;-reset]\nhttps://github.com/FazziCLAY/SchoolGuide/");
            } else {
                binding.text.setText(String.format("$[-#ffffff;@bold]Неизвестная ошибка.$[@reset;-reset]\nПриложение не смогло распознать ошибку, скорее всего сейчас технические неполадки\n\nОшибка: $[=#080607;-#cc0000;=#000000] %s", status.exception.toString()));
            }
            setupColorizeText();
            setupLinkableText();
            return;
        }

        if (status == Status.UPDATED) {
            binding.title.setText("Вы крутой!");
            binding.title.setTextColor(Color.GREEN);
            binding.text.setText("$[-#a0a0ff]Сейчас у вас установлена $[@bold;-#7070ff]самая последняя$[@reset;-#a0a0ff] версия, так держать!");
            setupColorizeText();
            return;
        }

        if (status == Status.OUTDATED) {
            binding.title.setText(String.format("Доступно обновление\n%s", status.latestVersion.getName()));
            binding.text.setText(status.latestVersion.getChangelog() == null ? "" : status.latestVersion.getChangelog()
                    .replace("$(CLIENT_VERSION_CODE)", SharedConstrains.APPLICATION_VERSION_CODE+"")
                    .replace("$(CLIENT_VERSION_NAME)", SharedConstrains.APPLICATION_VERSION_NAME)
            );
            setupColorizeText();
            setupLinkableText();
            binding.actionButton.setVisibility(View.VISIBLE);
            binding.actionButton.setText("Скачать и установить"); // TODO: 2022-01-21 make translatable
            binding.actionButton.setOnClickListener(ignore -> {
                try {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(status.latestVersion.getDownloadUrl()));
                    startActivity(browserIntent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(this, "Пожалуйста установите браузер (например FireFox)", Toast.LENGTH_SHORT).show();
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
        if (debug == 0) {
        } else if (debug == 1) {
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
        try {
            String fileContent = NetworkUtil.parseTextPage(SharedConstrains.VERSION_MANIFEST_V2);
            manifest = gson.fromJson(fileContent, Manifest.class);

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