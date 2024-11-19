package io.github.techstreet.dfscript.script;

import net.minecraft.text.Style;
import net.minecraft.util.Formatting;

import java.util.function.Function;

public enum ScriptNoticeLevel {
    NORMAL(0,null, null, 0, 0x33000000, false, false),
    DEPRECATION(1, (descriptor) -> "This " + descriptor + " is deprecated!",
                    Style.EMPTY.withColor(Formatting.YELLOW).withItalic(false),
            0x33FFFF00, 0x88FFFF00, false, true),
    UNUSABLE_ARGUMENTS(2, (descriptor) -> "This " + descriptor + " has arguments that were removed from use!",
                        Style.EMPTY.withColor(Formatting.GOLD).withItalic(false),
                        0x33FFAA00, 0x88FFAA00, true, true),
    UNUSABILITY(3, (descriptor) -> "This " + descriptor + " was removed from use!",
            Style.EMPTY.withColor(Formatting.RED).withItalic(false),
            0x33FF0000, 0x88FF0000, true, true);

    private final Function<String, String> messageSupplier;
    private final Style textStyle;
    private final int severity;
    private final int partColor;
    private final int hoveredPartColor;
    private final boolean disablesScript;
    private final boolean hidden;

    ScriptNoticeLevel(int severity, Function<String, String> messageSupplier,
                      Style textStyle, int partColor, int hoveredPartColor,
                      boolean disablesScript, boolean hidden) {

        this.messageSupplier = messageSupplier;
        this.textStyle = textStyle;
        this.partColor = partColor;
        this.hoveredPartColor = hoveredPartColor;
        this.disablesScript = disablesScript;
        this.hidden = hidden;
        this.severity = severity;
    }

    public int getPartColor(boolean hovered) {
        return hovered ? hoveredPartColor : partColor;
    }

    public Style getTextStyle() {
        return textStyle;
    }

    public String getMessage(String descriptor) {
        if(!hasMessage()) {
            return "";
        }
        return messageSupplier.apply(descriptor);
    }

    public boolean disablesScript() {
        return disablesScript;
    }
    public boolean isHidden() {
        return hidden;
    }

    public boolean hasMessage() {
        return messageSupplier != null && textStyle != null;
    }

    public int getSeverity() {
        return this.severity;
    }
}
