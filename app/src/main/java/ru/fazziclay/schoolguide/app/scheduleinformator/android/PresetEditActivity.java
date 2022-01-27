package ru.fazziclay.schoolguide.app.scheduleinformator.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import ru.fazziclay.schoolguide.R;
import ru.fazziclay.schoolguide.app.SchoolGuideApp;
import ru.fazziclay.schoolguide.app.Settings;
import ru.fazziclay.schoolguide.app.scheduleinformator.ScheduleInformatorApp;
import ru.fazziclay.schoolguide.app.scheduleinformator.appschedule.CompressedEvent;
import ru.fazziclay.schoolguide.app.scheduleinformator.appschedule.Event;
import ru.fazziclay.schoolguide.app.scheduleinformator.appschedule.Preset;
import ru.fazziclay.schoolguide.databinding.ActivityPresetEditBinding;
import ru.fazziclay.schoolguide.util.ColorUtil;
import ru.fazziclay.schoolguide.util.time.ConvertMode;
import ru.fazziclay.schoolguide.util.time.TimeUtil;

public class PresetEditActivity extends AppCompatActivity {
    private static final int SECONDS_IN_DAY = 24 * 60 * 60;
    private static final String EXTRA_PRESET_UUID = "uuid";

    public static Intent getLaunchIntent(Context context, UUID uuid) {
        return new Intent(context, PresetEditActivity.class)
                .putExtra(EXTRA_PRESET_UUID, uuid.toString());
    }

    private SchoolGuideApp app;
    private Settings settings;
    private ActivityPresetEditBinding binding;
    private DateFormatSymbols dateFormatSymbols;

    private UUID presetUUID;
    private Preset preset;

    private boolean isFirstMonday = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = SchoolGuideApp.get(this);
        settings = app.getSettings();
        ScheduleInformatorApp informatorApp = app.getScheduleInformatorApp();
        dateFormatSymbols = new DateFormatSymbols();

        isFirstMonday = app.getSettings().isFirstMonday;

        presetUUID = UUID.fromString(getIntent().getExtras().getString(EXTRA_PRESET_UUID));
        preset = informatorApp.getSchedule().getPreset(presetUUID);

        if (preset == null) {
            Toast.makeText(this, "Error: Preset not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (settings.presetEditColorScheme == null) {
            settings.presetEditColorScheme = ColorScheme.DEFAULT;
            app.saveSettings();
        }

        binding = ActivityPresetEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setTitle(getString(R.string.presetEdit_activityTitle, preset.getName()));

        updateEventList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateEventList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_preset_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.colorScheme) {
            showColorSchemeDialog();

        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    /**
     * Показывает диолог смены цветовой схемы
     * **/
    private void showColorSchemeDialog() {
        String[] schemesNames = new String[]{
                getString(R.string.presetEdit_colorScheme_default),
                getString(R.string.presetEdit_colorScheme_yesterday)
        };
        String[] schemesDescriptions = new String[]{
                getString(R.string.presetEdit_colorScheme_default_description),
                getString(R.string.presetEdit_colorScheme_yesterday_description)
        };
        ColorScheme[] schemes = new ColorScheme[]{
                ColorScheme.DEFAULT,
                ColorScheme.YESTERDAY
        };
        int selected = 0;
        for (ColorScheme scheme : schemes) {
            if (scheme == settings.presetEditColorScheme) break;
            selected++;
        }

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        TextView colorSchemeDescription = new TextView(this);
        colorSchemeDescription.setPadding(30, 3, 30, 3);
        colorSchemeDescription.setTextSize(16);
        colorSchemeDescription.setTextColor(Color.GREEN);
        colorSchemeDescription.setText(schemesDescriptions[selected]);
        colorSchemeDescription.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));


        Spinner spinner = new Spinner(this);
        spinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, schemesNames));
        spinner.setSelection(selected);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                colorSchemeDescription.setText(schemesDescriptions[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        spinner.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        layout.addView(colorSchemeDescription);
        layout.addView(spinner);

        String message = getString(R.string.presetEdit_colorScheme_message);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.presetEdit_colorScheme_title)
                .setMessage(message.isEmpty() ? null : message)
                .setView(layout)
                .setPositiveButton(R.string.presetEdit_colorScheme_apply, (dialogInterface, which) -> {
                    int selectedPosition = spinner.getSelectedItemPosition();
                    settings.presetEditColorScheme = schemes[selectedPosition];
                    app.saveSettings();
                    updateEventList();
                })
                .setNegativeButton(R.string.presetEdit_colorScheme_cancel, null)
                .create();

        dialog.show();
    }

    /**
     * Выдаёт список со всеми евентами которые начинаются в неделе week
     * @param week неделя в формате {@link Calendar#SUNDAY}
     * **/
    private CompressedEvent[] getEventsInWeek(int week) {
        List<CompressedEvent> e = new ArrayList<>();
        week--;

        for (Event event : preset.eventsPositions) {
            int w = (int) Math.floor(event.getStart() / (double) (24 * 60 * 60));
            if (week == w) {
                e.add(preset.compressEvent(event));
            }
            Log.d("getEventsInWeek", "eventName="+preset.eventsInfos.get(event.getEventInfo()).getName()+"; w="+w);
        }
        return e.toArray(new CompressedEvent[0]);
    }

    /**
     * Обновляет список
     * **/
    private void updateEventList() {
        binding.eventList.deferNotifyDataSetChanged();
        binding.eventList.setAdapter(getListAdapter());
        binding.eventList.setGroupIndicator(null);
    }

    /**
     * Выдаёт адаптер списка
     * **/
    private BaseExpandableListAdapter getListAdapter() {
        return new BaseExpandableListAdapter() {
            @Override
            public int getGroupCount() {
                return 7;
            }

            @Override
            public int getChildrenCount(int groupPosition) {
                int i = getEventsInWeek(posToWeek(isFirstMonday, groupPosition)).length;
                return Math.max(i, 1); // 1 for <empty> text
            }

            @Override
            public Object getGroup(int groupPosition) {
                return null;
            }

            @Override
            public Object getChild(int groupPosition, int childPosition) {
                return null;
            }

            @Override
            public long getGroupId(int groupPosition) {
                return groupPosition;
            }

            @Override
            public long getChildId(int groupPosition, int childPosition) {
                return childPosition;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }

            @Override
            public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
                binding.eventList.expandGroup(groupPosition, false);
                return getWeekView(groupPosition);
            }

            @Override
            public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
                return getWeekEventView(groupPosition, childPosition);
            }

            @Override
            public boolean isChildSelectable(int groupPosition, int childPosition) {
                return true;
            }
        };
    }


    /**
     * Выдаёт View евента
     * **/
    @SuppressLint("SetTextI18n")
    private View getWeekEventView(int groupPosition, int childPosition) {
        final int week = posToWeek(isFirstMonday, groupPosition);

        int eventsInDay = getEventsInWeek(posToWeek(isFirstMonday, groupPosition)).length;
        if (eventsInDay == 0) {
            TextView textView = new TextView(this);
            textView.setTextSize(21);
            textView.setText(R.string.presetEdit_emptyDay);
            return textView;
        }

        final int timeDeleteCoff = SECONDS_IN_DAY * (week-1);
        final CompressedEvent compressedEvent = getEventsInWeek(week)[childPosition];
        if (compressedEvent == null) {
            Log.e("getWeekEventView", "compressedEvent is null");
            TextView textView = new TextView(this);
            textView.setText("(null)");
            return textView;
        }

        TextView textView = new TextView(this);
        textView.setTextSize(21);
        int textColor = Color.RED;

        // text color
        {
            if (settings.presetEditColorScheme == ColorScheme.YESTERDAY) {
                textColor = Color.GREEN;
                CompressedEvent[] previousEvents = getEventsInWeek(week-1 <= 0 ? Calendar.SATURDAY : week-1);

                for (CompressedEvent prev : previousEvents) {
                    if (prev.getEventInfoUUID() == null) {
                        textColor = Color.RED;
                        break;
                    }
                    if (prev.getEventInfoUUID().equals(compressedEvent.getEventInfoUUID())) {
                        textColor = Color.GRAY;
                        break;
                    }
                }
            } else if (settings.presetEditColorScheme == ColorScheme.DEFAULT) {
                textColor = Color.GREEN;
            }
        }

        final String INFO_COLOR = "#aaffaa";
        textView.setTextColor(textColor);
        String startTime = TimeUtil.convertToHumanTime(compressedEvent.getStart() - timeDeleteCoff, ConvertMode.HHMM);
        String endTime = TimeUtil.convertToHumanTime(compressedEvent.getEnd() - timeDeleteCoff, ConvertMode.HHMM);
        SpannableString text = ColorUtil.colorize(String.format("$[-%s]%s %s-%s $[-reset]%s", INFO_COLOR, childPosition+1, startTime, endTime, compressedEvent.getName()),
                textColor,
                Color.TRANSPARENT,
                Typeface.NORMAL);
        textView.setText(text);
        return textView;
    }


    /**
     * Выдаёт View заголовка недели
     * **/
    private View getWeekView(int groupPosition) {
        final int week = posToWeek(isFirstMonday, groupPosition);
        String localeWeekName = dateFormatSymbols.getWeekdays()[week].toLowerCase();
        String firstChar = localeWeekName.substring(0, 1).toUpperCase();
        localeWeekName = localeWeekName.substring(1);
        localeWeekName = firstChar + localeWeekName;

        TextView textView = new TextView(this);
        textView.setTextSize(33);
        textView.setTextColor(Color.WHITE);
        textView.setText(localeWeekName);
        return textView;
    }

    /**
     * Выдаёт неделю в формате {@link Calendar#SUNDAY} по позиции
     * **/
    public static int posToWeek(boolean firstMonday, int pos) {
        pos++;
        if (firstMonday) {
            pos++;
            if (pos > 7) pos = Calendar.SUNDAY;
        }
        return pos;
    }

    /**
     * Цветовая схема страницы
     * **/
    public enum ColorScheme {
        DEFAULT,
        YESTERDAY
    }
}