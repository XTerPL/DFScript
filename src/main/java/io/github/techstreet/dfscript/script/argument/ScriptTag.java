package io.github.techstreet.dfscript.script.argument;

import com.google.gson.*;
import io.github.techstreet.dfscript.script.action.ScriptActionArgumentList;
import io.github.techstreet.dfscript.script.action.ScriptActionTag;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Objects;

public class ScriptTag {
    ScriptArgument argument;
    String selectedOption;
    String tagName;

    public ScriptTag(ScriptActionTag tag) {
        this(tag, tag.getDefaultOptionName(), null);
    }

    public ScriptTag(ScriptActionTag tag, String selectedOption, ScriptArgument argument) {
        this(tag.getName(), selectedOption, argument);
    }

    public ScriptTag(String tagName, String selectedOption, ScriptArgument argument) {
        this.argument = argument;
        this.tagName = tagName;
        this.selectedOption = selectedOption;
    }

    public ScriptActionTag getTag(ScriptActionArgumentList argList) {
        for(ScriptActionTag tag : argList.getTags()) {
            if(Objects.equals(tag.getName(), tagName)) {
                return tag;
            }
        }

        return null;
    }

    public void update(ScriptActionArgumentList argList) {
        if(!getTag(argList).has(selectedOption)) {
            selectedOption = getTag(argList).getDefaultOptionName();
        }
    }

    public void select(String optionName) {
        selectedOption = optionName;
    }

    public void setArgument(ScriptArgument argument) {
        this.argument = argument;
    }

    public ScriptArgument getArgument() {
        return argument;
    }

    public ScriptActionTag.ScriptActionTagOption getOption(ScriptActionArgumentList argList) {
        return getTag(argList).getOrDefault(selectedOption);
    }

    public ScriptActionTag.ScriptActionTagOption getOption(ScriptActionTag tag) {
        return tag.getOrDefault(selectedOption);
    }

    public ItemStack getIcon(ScriptActionArgumentList argList) {
        return getOption(argList).getIcon(getTag(argList), argument != null);
    }

    public String getName(ScriptActionArgumentList argList) {
        return getOption(argList).getName();
    }

    public ItemStack getIcon(ScriptActionTag tag) {
        return getOption(tag).getIcon(tag, argument != null);
    }

    public String getName(ScriptActionTag tag) {
        return getOption(tag).getName();
    }

    public String getTagName() {
        return tagName;
    }

    public String getSelectedName() {
        return selectedOption;
    }

    public static class Serializer implements JsonSerializer<ScriptTag>, JsonDeserializer<ScriptTag> {

        @Override
        public JsonElement serialize(ScriptTag src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject obj = new JsonObject();

            obj.addProperty("tag", src.tagName);
            obj.addProperty("selected", src.selectedOption);

            if(src.argument != null) {
                obj.add("argument", context.serialize(src.argument));
            }

            return obj;
        }

        @Override
        public ScriptTag deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            JsonObject obj = json.getAsJsonObject();

            String tagName = obj.get("tag").getAsString();
            String selected = obj.get("selected").getAsString();

            ScriptArgument argument = null;

            if(obj.has("argument")) {
                argument = context.deserialize(obj.get("argument"), ScriptArgument.class);
            }

            return new ScriptTag(tagName, selected, argument);
        }
    }
}
