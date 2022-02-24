package ru.fazziclay.schoolguide.app.scheduleinformator.android;

import android.annotation.SuppressLint;
import android.app.Dialog;
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
import android.widget.Button;
import android.widget.EditText;
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
import ru.fazziclay.schoolguide.app.MilkLog;
import ru.fazziclay.schoolguide.app.SharedConstrains;
import ru.fazziclay.schoolguide.app.PresetEditEventEditDialogStateCache;
import ru.fazziclay.schoolguide.app.SchoolGuideApp;
import ru.fazziclay.schoolguide.app.Settings;
import ru.fazziclay.schoolguide.app.scheduleinformator.ScheduleInformatorApp;
import ru.fazziclay.schoolguide.app.scheduleinformator.appschedule.CompressedEvent;
import ru.fazziclay.schoolguide.app.scheduleinformator.appschedule.Event;
import ru.fazziclay.schoolguide.app.scheduleinformator.appschedule.EventInfo;
import ru.fazziclay.schoolguide.app.scheduleinformator.appschedule.Preset;
import ru.fazziclay.schoolguide.databinding.ActivityPresetEditBinding;
import ru.fazziclay.schoolguide.util.ColorUtil;
import ru.fazziclay.schoolguide.util.UUIDUtil;
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
    /*private*/ ScheduleInformatorApp informatorApp;
    private ActivityPresetEditBinding binding;
    private DateFormatSymbols dateFormatSymbols;

    /*private*/ UUID presetUUID;
    private Preset preset;

    private boolean isFirstMonday = true;

    private MenuItem enableOneDayModeItem;
    private boolean isOneDayMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = SchoolGuideApp.get(this);
        if (app == null) {
            setContentView(SharedConstrains.getAppNullView(this));
            return;
        }
        settings = app.getSettings();
        informatorApp = app.getScheduleInformatorApp();
        dateFormatSymbols = new DateFormatSymbols();

        isFirstMonday = app.getSettings().isFirstMonday();

        presetUUID = UUID.fromString(getIntent().getExtras().getString(EXTRA_PRESET_UUID));
        preset = informatorApp.getAppPresetList().getPreset(presetUUID);

        if (preset == null) {
            Toast.makeText(this, R.string.presetEdit_error_presetNotFound, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (settings.getPresetEditColorScheme() == null) {
            settings.setPresetEditColorScheme(ColorScheme.DEFAULT);
            app.saveSettings();
        }

        binding = ActivityPresetEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.addEvent.setVisibility(preset.isSyncedByGlobal() ? View.GONE : View.VISIBLE);
        if (!preset.isSyncedByGlobal()) {
            binding.addEvent.setOnClickListener(ignore -> showEventDialog(null));
        }
        setTitle(getString(R.string.presetEdit_activityTitle, preset.getName()));
        isOneDayMode = preset.isOneDayMode();
        if (isOneDayMode) {
            _cloneSundayToAll();
            isFirstMonday = false;
        }
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
        enableOneDayModeItem = menu.findItem(R.id.onDayMode);
        enableOneDayModeItem.setVisible(!isOneDayMode && settings.isDeveloperFeatures() && !preset.isSyncedByGlobal());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.colorScheme) {
            showColorSchemeDialog();

            // CRUTCH
        } else if (item.getItemId() == R.id.onDayMode) {
            showEnableOneDayCrutchDialog();

        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private int[] getWeekDaysArray() {
        if (isFirstMonday) {
            return new int[]{
                    Calendar.MONDAY,
                    Calendar.TUESDAY,
                    Calendar.WEDNESDAY,
                    Calendar.THURSDAY,
                    Calendar.FRIDAY,
                    Calendar.SATURDAY,
                    Calendar.SUNDAY,
            };
        } else {
            return new int[]{
                    Calendar.SUNDAY,
                    Calendar.MONDAY,
                    Calendar.TUESDAY,
                    Calendar.WEDNESDAY,
                    Calendar.THURSDAY,
                    Calendar.FRIDAY,
                    Calendar.SATURDAY,
            };
        }
    }

    private void showEventDialog(Event event1) {
        boolean create = event1 == null;
        PresetEditEventEditDialogStateCache cache = app.getPresetEditEventEditDialogStateCache();
        if (create) {
            event1 = new Event(null, cache.latestStartSelected, cache.latestEndSelected);
        }
        Event event = event1;

        // CRUTCH
        if (isOneDayMode && event.getStart() >= (24*60*60) && !create) {
            Toast.makeText(this, "NoNoNo use sunday!", Toast.LENGTH_SHORT).show();
            return;
        }


        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_preset_edit_event_edit);
        Button cancelButton = dialog.findViewById(R.id.cancel);
        Button applyButton = dialog.findViewById(R.id.apply);
        EditText startTimeInput = dialog.findViewById(R.id.startTime);
        Spinner startTimeWeekSpinner = dialog.findViewById(R.id.startTimeWeek);
        EditText endTimeInput = dialog.findViewById(R.id.endTime);
        Spinner endTimeWeekSpinner = dialog.findViewById(R.id.endTimeWeek);
        Button deleteButton = dialog.findViewById(R.id.delete);
        Button deleteApplyButton = dialog.findViewById(R.id.deleteApply);
        Spinner eventInfoSpinner = dialog.findViewById(R.id.eventInfos);
        EditText eventInfoNameInput = dialog.findViewById(R.id.eventInfoName);
        TextView dialogTitle = dialog.findViewById(R.id.dialogTitle);
        if (create) dialogTitle.setText(R.string.presetEdit_eventDialog_title_add);

        final int SECONDS_IN_DAY = 24 * 60 * 60;
        int startCoff = (int) Math.floor(((double) event.getStart()) / ((double) SECONDS_IN_DAY));
        int endCoff = (int) Math.floor(((double) event.getEnd()) / ((double) SECONDS_IN_DAY));

        int[] timeWeekValues = getWeekDaysArray();
        String[] timeWeekTitles = new String[timeWeekValues.length /*7*/];
        String[] localedWeekNames = new DateFormatSymbols().getWeekdays();
        int i = 0;
        for (int week : timeWeekValues) {
            String localeWeekName = localedWeekNames[week].toLowerCase();
            timeWeekTitles[i] = localeWeekName.substring(0, 1).toUpperCase() + localeWeekName.substring(1);
            i++;
        }

        SpinnerSetup<UUID> spinnerSetup = applyEventInfosToSpinner(eventInfoSpinner, event);

        deleteButton.setVisibility(create ? View.GONE : View.VISIBLE);
        deleteButton.setOnClickListener(ignore -> {
            if (deleteApplyButton.getVisibility() == View.VISIBLE) {
                deleteButton.setText(R.string.presetEdit_eventDialog_delete);
                deleteApplyButton.setVisibility(View.GONE);
            } else {
                deleteButton.setText(R.string.presetEdit_eventDialog_delete_cancel);
                deleteApplyButton.setVisibility(View.VISIBLE);
            }
        });

        deleteApplyButton.setOnClickListener(ignore -> {
            preset.eventsPositions.remove(event);
            informatorApp.saveAppSchedule();
            updateEventList();
            dialog.cancel();
        });

        cancelButton.setOnClickListener(ignore -> dialog.cancel());
        applyButton.setOnClickListener(ignore -> {
            try {
                String[] startTimeSplit = startTimeInput.getText().toString().split(":");
                String[] endTimeSplit = endTimeInput.getText().toString().split(":");

                int start = (Integer.parseInt(startTimeSplit[0]) * 60 * 60) + (Integer.parseInt(startTimeSplit[1]) * 60) + (Integer.parseInt(startTimeSplit[2]));
                int end = (Integer.parseInt(endTimeSplit[0]) * 60 * 60) + (Integer.parseInt(endTimeSplit[1]) * 60) + (Integer.parseInt(endTimeSplit[2]));

                int startWeek = timeWeekValues[startTimeWeekSpinner.getSelectedItemPosition()];
                int endWeek = timeWeekValues[endTimeWeekSpinner.getSelectedItemPosition()];

                if (!isOneDayMode) {
                    start += (startWeek - 1) * SECONDS_IN_DAY;
                    end += (endWeek - 1) * SECONDS_IN_DAY;
                }

                String newInfoName = eventInfoNameInput.getText().toString();
                if (newInfoName.isEmpty()) {
                    throw new IllegalArgumentException(getString(R.string.presetEdit_eventDialog_error_nameEmpty));
                }
                UUID eventInfoUUID = spinnerSetup.values[eventInfoSpinner.getSelectedItemPosition()];

                if (eventInfoUUID == null) {
                    eventInfoUUID = UUIDUtil.generateUUID(preset.eventsInfos.keySet().toArray(new UUID[0]));
                    preset.eventsInfos.put(eventInfoUUID, new EventInfo(newInfoName));
                } else {
                    preset.eventsInfos.get(eventInfoUUID).setName(newInfoName);
                }
                event.setStart(start);
                event.setEnd(end);
                event.setEventInfo(eventInfoUUID);

                cache.latestStartSelected = start;
                cache.latestEndSelected = end;

                if (create) {
                    preset.eventsPositions.add(event);
                }

                if (isOneDayMode) {
                    _cloneSundayToAll();
                }

                informatorApp.saveAppSchedule();
                updateEventList();
                dialog.cancel();
            } catch (Exception e) {
                String message = e.getLocalizedMessage();
                if (e instanceof IndexOutOfBoundsException) {
                    message = getString(R.string.presetEdit_eventDialog_error_time);
                }
                Toast.makeText(this, getString(R.string.presetEdit_eventDialog_error_prefix) + message, Toast.LENGTH_SHORT).show();
            }
        });

        int selectedStart = 0;
        int selectedEnd = 0;
        int i1 = 0;
        for (int week : timeWeekValues) {
            if (week == startCoff+1) selectedStart = i1;
            if (week == endCoff+1) selectedEnd = i1;
            i1++;
        }

        eventInfoNameInput.setText(spinnerSetup.selectedName);
        eventInfoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    eventInfoNameInput.setText("");
                } else {
                    eventInfoNameInput.setText(spinnerSetup.names[i]);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        startTimeWeekSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, timeWeekTitles));
        endTimeWeekSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, timeWeekTitles));
        startTimeInput.setText(TimeUtil.convertToHumanTime(event.getStart() - (SECONDS_IN_DAY * startCoff), ConvertMode.HHMMSS));
        startTimeWeekSpinner.setSelection(selectedStart);
        endTimeInput.setText(TimeUtil.convertToHumanTime(event.getEnd() - (SECONDS_IN_DAY * endCoff), ConvertMode.HHMMSS));
        endTimeWeekSpinner.setSelection(selectedEnd);

        if (isOneDayMode) {
            startTimeWeekSpinner.setVisibility(View.GONE);
            endTimeWeekSpinner.setVisibility(View.GONE);
        }

        dialog.show();
    }

    private void showEnableOneDayCrutchDialog() {
        new AlertDialog.Builder(this)
                .setTitle("ОЧЕНЬ ВАЖНО!")
                .setMessage("Это не обратимое действие!\nВаше расписание станет однодневным! Это значит что каждый день будет с ондим и тем же положением событий и их временем. За основу будет взято воскресенье")
                .setNegativeButton(R.string.presetEdit_colorScheme_cancel, null)
                .setPositiveButton(R.string.presetEdit_colorScheme_apply, (i, e) -> {
                    preset.setOneDayMode(true);
                    informatorApp.saveAppSchedule();
                    startActivity(getLaunchIntent(this, presetUUID));
                    finish();
                })
        .show();
    }

    private SpinnerSetup<UUID> applyEventInfosToSpinner(Spinner eventsInfos, Event event) {
        SpinnerSetup<UUID> spinnerSetup = new SpinnerSetup<>();

        UUID[] eventsInfosUUIDs = preset.eventsInfos.keySet().toArray(new UUID[0]);
        UUID[] eventInfosUUIDs = new UUID[eventsInfosUUIDs.length + 1];
        System.arraycopy(eventsInfosUUIDs, 0, eventInfosUUIDs, 1, eventsInfosUUIDs.length);

        String[] eventInfosNames = new String[eventInfosUUIDs.length];
        int selectedInfos = 0;
        int i = 0;
        for (UUID uuid : eventInfosUUIDs) {
            if (uuid == null) {
                eventInfosNames[i] = getString(R.string.presetEdit_eventDialog_eventsInfos_createNew);
                i++;
                continue;
            }
            EventInfo eventInfo = preset.getEventInfo(uuid);
            eventInfosNames[i] = eventInfo.getName();
            if (uuid.equals(event.getEventInfo())) { // isSelected
                selectedInfos = i;
                spinnerSetup.selectedPosition = selectedInfos;
                spinnerSetup.selectedName = eventInfosNames[i];
                spinnerSetup.selectedValue = uuid;
            }
            i++;
        }
        eventsInfos.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, eventInfosNames));
        eventsInfos.setSelection(selectedInfos);

        spinnerSetup.names = eventInfosNames;
        spinnerSetup.values = eventInfosUUIDs;

        return spinnerSetup;
    }

    public static class SpinnerSetup<V> {
        V[] values;
        String[] names;
        V selectedValue;
        String selectedName;
        int selectedPosition;
    }

    /**
     * Показывает диолог смены цветовой схемы
     * **/
    private void showColorSchemeDialog() {
        final String[] schemesNames = new String[]{
                getString(R.string.presetEdit_colorScheme_default),
                getString(R.string.presetEdit_colorScheme_yesterday)
        };
        final String[] schemesDescriptions = new String[]{
                getString(R.string.presetEdit_colorScheme_default_description),
                getString(R.string.presetEdit_colorScheme_yesterday_description)
        };
        final ColorScheme[] schemesValues = new ColorScheme[]{
                ColorScheme.DEFAULT,
                ColorScheme.YESTERDAY
        };
        int selected = 0;
        for (ColorScheme scheme : schemesValues) {
            if (scheme == settings.getPresetEditColorScheme()) break;
            selected++;
        }

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        TextView colorSchemeDescription = new TextView(this);
        colorSchemeDescription.setPadding(30, 3, 30, 3);
        colorSchemeDescription.setTextSize(16);
        colorSchemeDescription.setTextColor(Color.WHITE);
        colorSchemeDescription.setText(ColorUtil.colorize(schemesDescriptions[selected], Color.WHITE, Color.TRANSPARENT, Typeface.NORMAL));
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
                    settings.setPresetEditColorScheme(schemesValues[selectedPosition]);
                    app.saveSettings();
                    updateEventList();
                })
                .setNegativeButton(R.string.presetEdit_colorScheme_cancel, null)
                .create();

        dialog.show();
    }

    /**
     * Сжимает все евенты
     * **/
    private CompressedEvent[] compressAll(Event[] events) {
        List<CompressedEvent> e = new ArrayList<>();

        for (Event event : events) {
            e.add(preset.compressEvent(event));
        }
        return e.toArray(new CompressedEvent[0]);
    }

    /**
     * Выдаёт список со всеми евентами которые начинаются в неделе week
     * @param week неделя в формате {@link Calendar#SUNDAY}
     * **/
    private Event[] getEventsInWeek(int week) {
        List<Event> e = new ArrayList<>();
        week--;

        for (Event event : preset.eventsPositions) {
            int w = (int) Math.floor(event.getStart() / (double) (24 * 60 * 60));
            if (week == w) {
                e.add(event);
            }
            Log.d("getEventsInWeek", "eventName="+preset.eventsInfos.get(event.getEventInfo()).getName()+"; w="+w);
        }
        return e.toArray(new Event[0]);
    }

    /**
     * Обновляет список
     * **/
    private void updateEventList() {
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
                return isOneDayMode ? 1 : 7;
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
        // Calendar.SUNDAY
        final int viewWeek = posToWeek(isFirstMonday, groupPosition);

        // Count of events in current week day
        final int eventsInDay = getEventsInWeek(viewWeek).length;

        // Empty view
        if (eventsInDay == 0) return getEmptyView();

        // Секунд всех прошлых недель (для вычитания из времени евента и получении сдвига от начала дня)
        final int shiftSeconds = SECONDS_IN_DAY * (viewWeek - 1);
        final Event event;
        final CompressedEvent compressedEvent;
        try {
            event = getEventsInWeek(viewWeek)[childPosition];
            compressedEvent = preset.compressEvent(event);
        } catch (Exception e) {
            app.getAppTrace().point("exception while get event", e);
            TextView textView = new TextView(this);
            textView.setText("(null)");
            return textView;
        }

        if (event == null || compressedEvent == null) {
            app.getAppTrace().point("error! event == null | compressedEvent == null");
            TextView textView = new TextView(this);
            textView.setText("(null)");
            return textView;
        }

        // Calendar.SUNDAY. неделя окончания события
        final int eventEndWeekShift = (int) Math.floor(((double) event.getEnd()) / (24 * 60 * 60));
        final int eventEndWeek = posToWeek(false, eventEndWeekShift);
        final boolean isEventEndWeekOtherStart = viewWeek != eventEndWeek;

        // text color
        int textColor = Color.RED;
        ColorScheme colorScheme = settings.getPresetEditColorScheme();
        if (colorScheme == ColorScheme.DEFAULT || colorScheme == null) {
            textColor = Color.GREEN;

        } else if (colorScheme == ColorScheme.YESTERDAY) {
            textColor = Color.GREEN;
            int previousWeek = viewWeek - 1 <= 0 ? Calendar.SATURDAY : viewWeek - 1;
            CompressedEvent[] previousEvents = compressAll(getEventsInWeek(previousWeek));
            for (CompressedEvent previous : previousEvents) {
                if (compressedEvent.getEventInfoUUID().equals(previous.getEventInfoUUID())) {
                    textColor = Color.GRAY;
                    break;
                }
            }
        }

        // Text
        final ConvertMode startStyle = ConvertMode.HHMM;
        final ConvertMode endStyle = ConvertMode.HHMM;
        String startTimeText = TimeUtil.convertToHumanTime(compressedEvent.getStart() - shiftSeconds, startStyle);
        String endTime;
        if (isEventEndWeekOtherStart) {
            final String END_WEEK_COLOR = "#FF00c6a8";
            DateFormatSymbols dateFormatSymbols = new DateFormatSymbols();
            endTime = TimeUtil.convertToHumanTime(compressedEvent.getEnd() - (eventEndWeekShift * SECONDS_IN_DAY), endStyle);
            endTime += "$[-" + END_WEEK_COLOR + "]" + dateFormatSymbols.getShortWeekdays()[eventEndWeek];
        } else {
            endTime = TimeUtil.convertToHumanTime(compressedEvent.getEnd() - shiftSeconds, endStyle);
        }

        SpannableString spannableText = ColorUtil.colorize("$[-_INFO_COLOR_]_DAY_POSITION_ _START_TIME_-_END_TIME_$[-reset]_INFO_NAME_LINE_SEPARATOR__EVENT_NAME_"
                        .replace("_INFO_COLOR_", "#99ffdd")
                        .replace("_DAY_POSITION_", String.valueOf(childPosition + 1))
                        .replace("_START_TIME_", startTimeText)
                        .replace("_END_TIME_",  endTime)
                        .replace("_INFO_NAME_LINE_SEPARATOR_", settings.isPresetEditEventNameInNextLine() ? "\n\t\t\t" : " ")
                        .replace("_EVENT_NAME_", compressedEvent.getName()),
                textColor,
                Color.TRANSPARENT,
                Typeface.NORMAL);

        TextView textView = new TextView(this);
        textView.setText(spannableText);
        textView.setTextSize(21);
        textView.setTextColor(textColor);
        if (!preset.isSyncedByGlobal()) {
            textView.setOnClickListener(ignore -> showEventDialog(event));
        }
        return textView;
    }

    private View getEmptyView() {
        TextView textView = new TextView(this);
        textView.setTextSize(21);
        textView.setText(R.string.presetEdit_emptyDay);
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

        if (isOneDayMode) localeWeekName = "Everyday";

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


    // CRUTCH ZONE
    private void _cleanupAllNoSunday() {
        if (!isOneDayMode) {
            MilkLog.g("WARN вызов функции при выключеном режиме костыля");
            return;
        }

        List<Event> noDel = new ArrayList<>();
        // search > SUNDAY
        for (Event e : preset.eventsPositions) {
            if (e.getStart() <= ((24 * 60 * 60)-10)) {
                noDel.add(e);
                MilkLog.g("wow! noDel "+ e);
            }
        }

        // delete
        preset.eventsPositions = new ArrayList<>();
        preset.eventsPositions.addAll(noDel);
        informatorApp.saveAppSchedule();
    }

    /**
     * удаляет все что после воскресения (пн. вт ср. чт. пт. сб)
     * и копирует в каждый день данные из воскресенья
     * **/
    private void _cloneSundayToAll() {
        if (!isOneDayMode) {
            MilkLog.g("WARN вызов функции при выключеном режиме костыля");
            return;
        }
        _cleanupAllNoSunday();

        final int SECONDS_IN_DAY = 24 * 60 * 60;
        final int[] coefficients = {
                SECONDS_IN_DAY, // понидельник
                SECONDS_IN_DAY * 2,
                SECONDS_IN_DAY * 3,
                SECONDS_IN_DAY * 4,
                SECONDS_IN_DAY * 5,
                SECONDS_IN_DAY * 6
        };

        List<Event> temp = new ArrayList<>(preset.eventsPositions);
        for (Event e : temp) {
            for (int coff : coefficients) {
                preset.eventsPositions.add(new Event(e.getEventInfo(), e.getStart() + coff, e.getEnd() + coff));
            }
        }

        informatorApp.saveAppSchedule();
    }
}