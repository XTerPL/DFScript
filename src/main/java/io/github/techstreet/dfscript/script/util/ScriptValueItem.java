package io.github.techstreet.dfscript.script.util;

import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.script.values.ScriptDictionaryValue;
import io.github.techstreet.dfscript.script.values.ScriptListValue;
import io.github.techstreet.dfscript.script.values.ScriptNumberValue;
import io.github.techstreet.dfscript.script.values.ScriptTextValue;
import io.github.techstreet.dfscript.script.values.ScriptUnknownValue;
import io.github.techstreet.dfscript.script.values.ScriptValue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.AbstractNbtNumber;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;

public class ScriptValueItem {

    public static ItemStack itemFromValue(ScriptValue value) {
        if(DFScript.MC.player == null || DFScript.MC.player.clientWorld == null) {
            throw new UnsupportedOperationException("Dictionary to Item Conversion only available when in a world!");
        }

        return ItemStack.fromNbtOrEmpty(DFScript.MC.player.getRegistryManager(), (NbtCompound) nbtFromValue(value));
    }

    public static ScriptValue valueFromItem(ItemStack item) {
        if(DFScript.MC.player == null || DFScript.MC.player.clientWorld == null) {
            throw new UnsupportedOperationException("Item to Dictionary Conversion only available when in a world!");
        }

        NbtElement nbt = item.toNbtAllowEmpty(DFScript.MC.player.clientWorld.getRegistryManager());
        return valueFromNbt(nbt);
    }

    public static NbtElement nbtFromValue(ScriptValue value) {
        if (value instanceof ScriptTextValue) {
            return NbtString.of(value.asText());
        } else if (value instanceof ScriptNumberValue) {
            return NbtDouble.of(value.asNumber());
        } else if (value instanceof ScriptListValue) {
            NbtList list = new NbtList();
            for (ScriptValue item : value.asList()) {
                list.add(nbtFromValue(item));
            }
            return list;
        } else if (value instanceof ScriptDictionaryValue) {
            NbtCompound compound = new NbtCompound();
            for (String key : value.asDictionary().keySet()) {
                compound.put(key, nbtFromValue(value.asDictionary().get(key)));
            }
            return compound;
        } else {
            return null;
        }
    }

    public static ScriptValue valueFromNbt(NbtElement nbt) {
        if (nbt instanceof NbtString nbts) {
            return new ScriptTextValue(nbts.asString());
        } else if (nbt instanceof AbstractNbtNumber nbtn) {
            return new ScriptNumberValue(nbtn.doubleValue());
        } else if (nbt instanceof NbtList nbtl) {
            List<ScriptValue> list = new ArrayList<>();
            for (NbtElement item : nbtl) {
                list.add(valueFromNbt(item));
            }
            return new ScriptListValue(list);
        } else if (nbt instanceof NbtCompound nbtc) {
            HashMap<String, ScriptValue> dictionary = new HashMap<>();
            for (String key : nbtc.getKeys()) {
                dictionary.put(key, valueFromNbt(nbtc.get(key)));
            }
            return new ScriptDictionaryValue(dictionary);
        } else {
            return new ScriptUnknownValue();
        }
    }

}
