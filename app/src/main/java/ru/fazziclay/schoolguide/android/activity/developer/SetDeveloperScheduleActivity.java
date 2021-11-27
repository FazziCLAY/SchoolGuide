package ru.fazziclay.schoolguide.android.activity.developer;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import ru.fazziclay.schoolguide.R;
import ru.fazziclay.schoolguide.SchoolGuide;
import ru.fazziclay.schoolguide.data.manifest.ManifestProvider;
import ru.fazziclay.schoolguide.data.schedule.Schedule;
import ru.fazziclay.schoolguide.data.schedule.ScheduleProvider;

public class SetDeveloperScheduleActivity extends AppCompatActivity {
    ManifestProvider manifestProvider;
    ScheduleProvider scheduleProvider;

    LinearLayout background;
    TextView loadingText;
    TextView errorText;
    TextView mainText;
    Button setButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setButton = new Button(this);
        background = new LinearLayout(this);
        loadingText = new TextView(this);
        errorText = new TextView(this);
        mainText = new TextView(this);

        setContentView(background);


        loadingText.setText("Загрузка...");
        errorText.setText("Ошибка. (default)");

        background.setOrientation(LinearLayout.VERTICAL);
        background.addView(loadingText);

        manifestProvider = SchoolGuide.getInstance().getManifestProvider();
        scheduleProvider = SchoolGuide.getInstance().getScheduleProvider();


        manifestProvider.updateForGlobal((exception, manifestProvider) -> runOnUiThread(() -> {
            if (manifestProvider.isTechnicalWorks()) {
                errorText.setText(R.string.updateCenter_title_technicalWorks);
                mainText.setText(R.string.updateCenter_text_technicalWorks);
                errorText.setTextSize(27f);
                mainText.setTextSize(20f);

                background.addView(errorText);
                background.addView(mainText);
                loadingText.setVisibility(View.GONE);
                return;
            }

            if (exception != null || manifestProvider.getDeveloperSchedule() == null) {
                if (exception != null) {
                    errorText.setText(exception.toString());
                } else {
                    errorText.setText("На стороне сервера расписание пустое!");
                }

                background.addView(errorText);
                loadingText.setVisibility(View.GONE);
                return;
            }
            loadingText.setVisibility(View.GONE);

            Schedule developerSchedule = manifestProvider.getDeveloperSchedule();
            mainText.setText("Расписание найдено, при установке расписания разработчика всё текущее расписание удалится безвозвратно! А на его место станет расписание разработчика!");
            setButton.setText("Я знаю что делаю!");
            background.addView(mainText);
            background.addView(setButton);

            setButton.setOnClickListener(ignore -> {
                Gson gson = new Gson();
                scheduleProvider.setSchedule(gson.fromJson(gson.toJson(developerSchedule, Schedule.class), Schedule.class));
                if (SchoolGuide.getInstance().getScheduleProvider().getAllSchedules().length > 0) SchoolGuide.getInstance().getSettingsProvider().setSelectedLocalSchedule(SchoolGuide.getInstance().getScheduleProvider().getAllSchedules()[0]);
                finish();
            });
        }));
    }
}