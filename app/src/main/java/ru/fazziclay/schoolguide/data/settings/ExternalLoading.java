package ru.fazziclay.schoolguide.data.settings;

import android.app.Activity;

import com.google.gson.annotations.SerializedName;

import ru.fazziclay.schoolguide.android.activity.developer.DeveloperManifestUtilsActivity;
import ru.fazziclay.schoolguide.android.activity.schedule.ScheduleLessonEditActivity;

public enum ExternalLoading {
    @SerializedName("activity_developer_manifest_utils")
    ACTIVITY_DEVELOPER_MANIFEST_UTILS(DeveloperManifestUtilsActivity.class),

    @SerializedName("activity_schedule_lesson_edit")
    ACTIVITY_SCHEDULE_LESSON_EDIT(ScheduleLessonEditActivity.class, true);

    Class<? extends Activity> activity;
    boolean isCodedFeatures = false;
    ExternalLoading(Class<? extends Activity> activity) {
        this.activity = activity;
    }
    ExternalLoading(Class<? extends Activity> activity, boolean isCodedFeatures) {
        this.activity = activity;
        this.isCodedFeatures = isCodedFeatures;
    }

    public Class<? extends Activity> getActivity() {
        return activity;
    }

    public boolean isCodedFeatures() {
        return isCodedFeatures;
    }
}
