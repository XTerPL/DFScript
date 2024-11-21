package io.github.techstreet.dfscript.script;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    public static ItemStack putNotices(ItemStack current, String noticeDescriptor, ScriptNotice... notices) {
        return putNotices(current, noticeDescriptor, List.of(notices));
    }

    public static ItemStack putNotices(ItemStack current, String noticeDescriptor, List<ScriptNotice> notices) {
        ArrayList<Text> lore = new ArrayList<>();

        if (current.getComponents().contains(DataComponentTypes.LORE)) {
            lore = new ArrayList<>(Objects.requireNonNull(current.getComponents().get(DataComponentTypes.LORE)).lines());
        }

        ArrayList<ScriptNotice> sorted = new ArrayList<>(notices);
        sorted.sort(new ScriptNoticeComparator());

        for(ScriptNotice notice : notices) {
            for(String line : notice.getMessage(noticeDescriptor).reversed()) {
                lore.addFirst(Text.literal(line).setStyle(notice.getTextStyle()));
            }
        }

        ItemStack result = current.copy();
        result.set(DataComponentTypes.LORE, new LoreComponent(lore));

        return result;
    }
}
