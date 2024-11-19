package io.github.techstreet.dfscript.script;

import net.minecraft.text.Style;
import java.util.List;

public class ScriptNotice {
    private final ScriptNoticeLevel level;
    private final String extraMessage;

    public ScriptNotice(ScriptNoticeLevel level, String extraMessage) {
        this.level = level;
        this.extraMessage = extraMessage;
    }

    public ScriptNotice(ScriptNoticeLevel level) {
        this.level = level;
        this.extraMessage = null;
    }

    public int getPartColor(boolean hovered) {
        return level.getPartColor(hovered);
    }

    public Style getTextStyle() {
        return level.getTextStyle();
    }

    public List<String> getMessage(String descriptor) {
        if(level.hasMessage()) {
            if(extraMessage != null) {
                return List.of(level.getMessage(descriptor), extraMessage);
            }
            return List.of(level.getMessage(descriptor));
        }
        return List.of();
    }

    public boolean disablesScript() {
        return level.disablesScript();
    }
    public boolean isHidden() {
        return level.isHidden();
    }

    public ScriptNoticeLevel getLevel() {
        return level;
    }

    public int getSeverity() {
        return level.getSeverity();
    }
}
