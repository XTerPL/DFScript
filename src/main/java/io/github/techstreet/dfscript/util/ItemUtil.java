package io.github.techstreet.dfscript.util;

import io.github.techstreet.dfscript.script.action.ScriptActionTag;
import io.github.techstreet.dfscript.script.values.ScriptNumberValue;
import io.github.techstreet.dfscript.script.values.ScriptUnknownValue;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

import java.util.Optional;

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

    public static ScriptActionTag durabilityType = new ScriptActionTag("Durability Type",
            new ScriptActionTag.ScriptActionTagOption("Damage",   Items.BRICK),
            new ScriptActionTag.ScriptActionTagOption("Damage Percentage", Items.STICK),
            new ScriptActionTag.ScriptActionTagOption("Remaining", Items.GOLD_INGOT),
            new ScriptActionTag.ScriptActionTagOption("Remaining Percentage", Items.BLAZE_ROD)
    );

    public static ScriptActionTag keptDurabilityType = new ScriptActionTag("Kept Durability Type",
            new ScriptActionTag.ScriptActionTagOption("Damage",   Items.BRICK),
            new ScriptActionTag.ScriptActionTagOption("Damage Percentage", Items.STICK),
            new ScriptActionTag.ScriptActionTagOption("Remaining", Items.GOLD_INGOT),
            new ScriptActionTag.ScriptActionTagOption("Remaining Percentage", Items.BLAZE_ROD)
    );

    public static Optional<Double> getDurability(ItemStack item, String type) {
        if(!item.contains(DataComponentTypes.DAMAGE)) {
            return Optional.empty();
        }

        double damage = item.get(DataComponentTypes.DAMAGE);

        if(type.equals("Damage")) {
            return Optional.of(damage);
        }

        if(!item.contains(DataComponentTypes.MAX_DAMAGE)) {
            return Optional.empty();
        }

        double maxDamage = item.get(DataComponentTypes.MAX_DAMAGE);

        return Optional.of(switch(type) {
            case "Remaining" -> maxDamage - damage;
            case "Damage Percentage" -> damage * 100 / maxDamage;
            case "Remaining Percentage" -> (maxDamage - damage) * 100 / maxDamage;
            default -> throw new IllegalArgumentException("Unknown Durability Type: " + type);
        });
    }

    public static void setDurability(ItemStack item, double durability, String type) {
        if(type.equals("Damage")) {
            item.set(DataComponentTypes.DAMAGE, (int) durability);
            return;
        }

        if(!item.contains(DataComponentTypes.MAX_DAMAGE)) {
            return;
        }

        double maxDamage = item.get(DataComponentTypes.MAX_DAMAGE);

        int damage = (int) switch(type) {
            case "Remaining" -> maxDamage - durability;
            case "Damage Percentage" -> durability / 100 * maxDamage;
            case "Remaining Percentage" -> maxDamage - (durability / 100 * maxDamage);
            default -> throw new IllegalArgumentException("Unknown Durability Type: " + type);
        };

        item.set(DataComponentTypes.DAMAGE, damage);
    }
}
