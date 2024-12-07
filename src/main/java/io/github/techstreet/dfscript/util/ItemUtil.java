package io.github.techstreet.dfscript.util;

import io.github.techstreet.dfscript.script.action.ScriptActionTag;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

public class ItemUtil {
    public static ScriptActionTag itemNameType = new ScriptActionTag("Name Type",
                new ScriptActionTag.ScriptActionTagOption("Item Name",   Items.NAME_TAG),
                new ScriptActionTag.ScriptActionTagOption("Custom Name", Items.ANVIL)
            );

    public static ComponentType<Text> getItemNameComponent(String name) {
        return switch(name) {
            case "Item Name" -> DataComponentTypes.ITEM_NAME;
            case "Custom Name" -> DataComponentTypes.CUSTOM_NAME;
            default -> throw new IllegalArgumentException("Unknown component: " + name);
        };
    }
}
