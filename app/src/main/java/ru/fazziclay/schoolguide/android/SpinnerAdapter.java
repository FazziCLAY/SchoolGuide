package ru.fazziclay.schoolguide.android;

import java.util.ArrayList;
import java.util.List;

public class SpinnerAdapter {
    List<String> names;
    List<Object> values;
    int selected = 0;

    public SpinnerAdapter(List<SpinnerAdapterElement> elements, Object selected) {
        init(elements.toArray(new SpinnerAdapterElement[0]));
        setSelected(selected);
    }

    public void init(SpinnerAdapterElement[] elements) {
        names = new ArrayList<>();
        values = new ArrayList<>();

        for (SpinnerAdapterElement element : elements) {
            names.add(element.name);
            values.add(element.value);
        }
    }

    public List<String> getNames() {
        return names;
    }

    public List<Object> getValues() {
        return values;
    }

    public Object getValue(int i) {
        return getValues().get(i);
    }

    public void setSelected(Object selected) {
        this.selected = values.indexOf(selected);
    }

    public int getSelected() {
        return selected;
    }

    public static class SpinnerAdapterElement {
        String name;
        Object value;

        public SpinnerAdapterElement(String name, Object value) {
            this.name = name;
            this.value = value;
        }
    }
}
