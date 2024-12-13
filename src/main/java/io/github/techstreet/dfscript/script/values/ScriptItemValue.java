package io.github.techstreet.dfscript.script.values;

import com.google.gson.*;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.script.util.ScriptValueItem;
import net.minecraft.SharedConstants;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.InvalidNbtException;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;

import java.lang.reflect.Type;
import java.util.HashMap;

public class ScriptItemValue extends ScriptValue {
    private final NbtElement value;

    public ScriptItemValue(NbtElement value) {
        this.value = value.copy();
    }

    public ScriptItemValue(ItemStack item) {
        if(DFScript.MC.player == null || DFScript.MC.player.clientWorld == null) {
            throw new UnsupportedOperationException("Item Script Value initialization from ItemStack only available in a world!");
        }

        this.value = item.toNbtAllowEmpty(DFScript.MC.player.clientWorld.getRegistryManager());
    }

    @Override
    String typeName() {
        return "Item";
    }

    @Override
    public String asText() {
        return asItem().getItem().toString();
    }

    @Override
    public boolean asBoolean() {
        return !asItem().isEmpty();
    }

    @Override
    public ItemStack asItem() {
        if(DFScript.MC.player == null || DFScript.MC.player.clientWorld == null) {
            throw new UnsupportedOperationException("Item Value to ItemStack conversion only available while in a world!");
        }

        if(value instanceof NbtCompound comp)
        {
            return ItemStack.fromNbtOrEmpty(DFScript.MC.player.getRegistryManager(), comp);
        }

        throw new InvalidNbtException("ItemStack NBT is not a Compound!");
    }

    @Override
    public HashMap<String,ScriptValue> asDictionary() {
        return ScriptValueItem.valueFromNbt(value).asDictionary();
    }

    @Override
    public boolean valueEquals(ScriptValue other) {
        if(other instanceof ScriptItemValue)
        {
            return asItem().equals(other.asItem());
        }
        try
        {
            return Registries.ITEM.getId(asItem().getItem()).equals(Identifier.of(other.asText()));
        }
        catch(InvalidIdentifierException e)
        {
            return false;
        }
    }

    @Override
    public String formatAsText() {
        return asText();
    }

    static int getSaveVersionId() {
        return SharedConstants.getGameVersion().getSaveVersion().getId();
    }

    public static class Serializer implements JsonSerializer<ScriptItemValue>, JsonDeserializer<ScriptItemValue> {
        @Override
        public ScriptItemValue deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
            if (!jsonElement.isJsonObject()) {
                throw new JsonParseException("Script Item Value JSON is not an object!");
            }

            JsonObject obj = jsonElement.getAsJsonObject();

            if (!obj.has("item")) {
                throw new JsonParseException("Missing item data in script item value json!");
            }

            if (!obj.has("version")) {
                throw new JsonParseException("Missing save data version in script item value json!");
            }

            JsonElement itemData = obj.get("item");

            if (!itemData.isJsonObject()) {
                throw new JsonParseException("Item Data is not an object!");
            }

            JsonElement updated = DFScript.MC.getDataFixer().update(TypeReferences.ITEM_STACK,
                    new Dynamic<>(JsonOps.INSTANCE, itemData),
                    obj.get("version").getAsInt(), getSaveVersionId()).getValue();

            NbtElement nbt = JsonOps.INSTANCE.convertTo(NbtOps.INSTANCE, updated);

            return new ScriptItemValue(nbt);
        }

        @Override
        public JsonElement serialize(ScriptItemValue scriptValue, Type type, JsonSerializationContext context) {
            JsonObject obj = new JsonObject();
            JsonElement element = NbtOps.INSTANCE.convertTo(JsonOps.COMPRESSED, scriptValue.value);

            obj.addProperty("___objectType", "item");
            obj.addProperty("version", getSaveVersionId());
            obj.add("item", element);

            return obj;
        }
    }
}
