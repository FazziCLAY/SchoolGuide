package ru.fazziclay.schoolguide.android.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.format.DateFormat;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.annotations.SerializedName;

import org.w3c.dom.Text;

import java.util.Date;
import java.util.List;

import ru.fazziclay.schoolguide.R;
import ru.fazziclay.schoolguide.android.service.ForegroundService;
import ru.fazziclay.schoolguide.data.restore_point.RestorePoint;
import ru.fazziclay.schoolguide.data.restore_point.RestorePointProvider;
import ru.fazziclay.schoolguide.data.schedule.Schedule;
import ru.fazziclay.schoolguide.data.schedule.ScheduleProvider;
import ru.fazziclay.schoolguide.databinding.ActivityRestorePointBinding;

public class RestorePointActivity extends AppCompatActivity {
    ActivityRestorePointBinding binding;
    Handler loopHandler = null;
    Runnable loopRunnable = null;
    ScheduleProvider scheduleProvider;
    RestorePointProvider restorePointProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRestorePointBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        scheduleProvider = ForegroundService.getInstance().getScheduleProvider();
        restorePointProvider = ForegroundService.getInstance().getRestorePointProvider();

        loopHandler = new Handler(Looper.getMainLooper());
        loopRunnable = new Runnable() {
            @Override
            public void run() {
                if (isFinishing()) return;
                loop();
                loopHandler.postDelayed(this, 1000);
            }
        };
        loopHandler.post(loopRunnable);
    }

    public void loop() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(5, 5, 5, 13);

        binding.root.removeAllViews();

        List<RestorePoint> list = restorePointProvider.getRestorePoints();
        int i = 0;
        while (i < list.size()) {
            RestorePoint restorePoint = list.get(i);

            String text = String.format("==== Точка востановления #%s ====\nИмя: %s\nДата создания: %s",
                    (i+1),
                    String.format("%s (%s)", restorePoint.name, restorePoint.fileName),
                    DateFormat.format("yyyy-MM-dd HH:mm:ss z", new Date(restorePoint.createdTime))
            );

            TextView restorePointButton = new TextView(this);
            restorePointButton.setText(text);
            restorePointButton.setTextSize(18f);
            restorePointButton.setTextColor(Color.CYAN);
            restorePointButton.setBackgroundColor(Color.DKGRAY);
            restorePointButton.setLayoutParams(params);
            int finalI = i;
            restorePointButton.setOnClickListener(v -> {
                LinearLayout dialogLinearLayout = new LinearLayout(this);
                EditText name = new EditText(this);
                Button deleteButton = new Button(this);
                Button restoreButton = new Button(this);

                AlertDialog a = new AlertDialog.Builder(this)
                        .setTitle("Точка востановления #"+(finalI +1))
                        .setMessage(String.format("Учителей: %s\nУроков: %s",
                                restorePoint.schedule.teachers.size(),
                                restorePoint.schedule.lessons.size()
                        ))
                        .setView(dialogLinearLayout)
                        .setPositiveButton(R.string.apply, (dialogInterface, i1) -> {
                            restorePoint.name = name.getText().toString();
                            restorePointProvider.save(this);
                        })
                        .create();

                name.setHint("Имя точки востановления");
                name.setText(restorePoint.name);
                deleteButton.setText("Удалить");
                deleteButton.setOnClickListener(vvv -> {
                    a.dismiss();
                    restorePointProvider.delete(this, restorePoint);
                    restorePointProvider.save(this);
                });
                restoreButton.setText("Востановить");
                restoreButton.setOnClickListener(vv -> {
                    a.dismiss();
                    AlertDialog aa = new AlertDialog.Builder(this)
                            .setTitle("ВНИМАНИЕ")
                            .setMessage("Востановление точки востановления приведёт к удалению текущего расписания. Рекумондуется перед этом сделать дополниельную точку востановления.")
                            .setPositiveButton(R.string.apply, (dialogInterface, i1) -> {
                                restorePointProvider.restore(scheduleProvider, restorePoint);
                                scheduleProvider.save(Schedule.getScheduleFilePath(this));
                                //..Toast.makeText(this, String.format("Click!\n%s", restorePoint), Toast.LENGTH_SHORT).show();
                                //..Toast.makeText(this, String.format("scheduleProvider.s= = %s", scheduleProvider.getSchedule()), Toast.LENGTH_SHORT).show();
                            })
                            .setNegativeButton("ОТМЕНА", null)
                            .create();
                    aa.show();
                });

                dialogLinearLayout.setOrientation(LinearLayout.VERTICAL);
                dialogLinearLayout.addView(name);
                dialogLinearLayout.addView(deleteButton);
                dialogLinearLayout.addView(restoreButton);

                a.show();
            });
            binding.root.addView(restorePointButton);

            i++;
        }

        if (i == 0) {
            TextView restorePointButton = new TextView(this);
            restorePointButton.setText("Точек востановления не обнаружено, вы можете создать их ниже. Точки востановления нужны если вы хотите изменить текущее расписание с расчётом на то что-бы вернуть его назад. Так же вы можете делиться своими точками востановления с другими людьми, а они могут их установить и опробывать.");
            restorePointButton.setLayoutParams(params);
            binding.root.addView(restorePointButton);
        }

        Button createButton = new Button(this);
        createButton.setText("Создать точку востановления");
        createButton.setOnClickListener(v -> {
            LinearLayout dialogLinearLayout = new LinearLayout(this);
            EditText name = new EditText(this);

            AlertDialog a = new AlertDialog.Builder(this)
                    .setTitle("Создать точку востановления")
                    .setMessage("Точка востановления будет создана из текущего заданого засписания")
                    .setView(dialogLinearLayout)
                    .setPositiveButton(R.string.apply, (dialogInterface, i1) -> {
                        if (name.getText().toString().equals("")) {
                            Toast.makeText(this, "Ошибка в имени!", Toast.LENGTH_LONG).show();
                            return;
                        }
                        restorePointProvider.create(name.getText().toString(), scheduleProvider.getSchedule());
                        restorePointProvider.save(this);
                    })
                    .create();

            name.setHint("Имя точки востановления");

            dialogLinearLayout.setOrientation(LinearLayout.VERTICAL);
            dialogLinearLayout.addView(name);

            a.show();
        });
        binding.root.addView(createButton);

        Button reloadButton = new Button(this);
        reloadButton.setText("Перезагрузить");
        reloadButton.setOnClickListener(v -> restorePointProvider.reload(this));
        binding.root.addView(reloadButton);

        TextView textView = new TextView(this);
        textView.setText(String.format("Вы можете делится и принимать точки востановления! Они хранятся в виде файлов и находятся по пути %s", RestorePointProvider.getRestorePointsFolderPath(this)));
        binding.root.addView(textView);
    }
}