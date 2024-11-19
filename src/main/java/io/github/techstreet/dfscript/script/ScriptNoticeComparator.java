package io.github.techstreet.dfscript.script;

import java.util.Comparator;

public class ScriptNoticeComparator implements Comparator<ScriptNotice> {
    @Override
    public int compare(ScriptNotice o1, ScriptNotice o2) {
        return o1.getSeverity() - o2.getSeverity();
    }
}