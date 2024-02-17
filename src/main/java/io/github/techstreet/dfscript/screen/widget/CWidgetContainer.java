package io.github.techstreet.dfscript.screen.widget;

import java.util.ArrayList;
import java.util.List;

public interface CWidgetContainer {
    List<CWidget> children = new ArrayList<>();

    default void add(CWidget child) {
        children.add(child);
    }

    default void clear() { children.clear(); }

    default CWidget[] getAll() {
        return children.toArray(new CWidget[0]);
    }

    default void remove(CWidget w) {
        children.remove(w);
    }
}
