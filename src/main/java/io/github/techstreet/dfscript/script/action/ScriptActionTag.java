package io.github.techstreet.dfscript.script.action;

import com.google.gson.*;
import io.github.techstreet.dfscript.script.Script;
import io.github.techstreet.dfscript.script.values.ScriptUnknownValue;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ScriptActionTag {
    private final String name;
    private final ArrayList<ScriptActionTagOption> options = new ArrayList<>();
    private final String defaultOption;

    public ScriptActionTag(String name, String defaultOption, List<ScriptActionTagOption> options) {
        if(options.isEmpty()) {
            throw new IllegalArgumentException("There must be at least 1 tag option!");
        }

        this.name = name;
        this.options.addAll(options);
        this.defaultOption = defaultOption;
    }

    public ScriptActionTag(String name, String defaultOption, ScriptActionTagOption... options) {
        if(options.length == 0) {
            throw new IllegalArgumentException("There must be at least 1 tag option!");
        }

        this.name = name;
        this.options.addAll(List.of(options));
        this.defaultOption = defaultOption;
    }

    public ScriptActionTag(String name, ScriptActionTagOption... options) {
        if(options.length == 0) {
            throw new IllegalArgumentException("There must be at least 1 tag option!");
        }

        this.name = name;
        this.options.addAll(List.of(options));
        this.defaultOption = this.options.getFirst().name;
    }

    public String getDefaultOptionName() {
        return defaultOption;
    }

    public ScriptActionTagOption getOrDefault(String optionName) {
        ScriptActionTagOption result = get(optionName);

        if(result == null) {
            result = get(this.defaultOption);

            if(result == null) {
                result = this.options.getFirst();
            }
        }

        return result;
    }

    public ScriptActionTagOption get(String optionName) {
        for(ScriptActionTagOption option : options) {
            if(option.name.equalsIgnoreCase(optionName)) {
                return option;
            }
        }
        return null;
    }

    public boolean has(String optionName) {
        return get(optionName) != null;
    }

    public String getName() {
        return name;
    }

    public List<String> getOptionNames() {
        ArrayList<String> names = new ArrayList<>();

        for(ScriptActionTagOption option : options) {
            names.add(option.getName());
        }

        return names;
    }

    public List<Text> text() {
        MutableText t = Text.literal("Tag").fillStyle(Style.EMPTY.withItalic(false).withColor(Formatting.WHITE));

        t.append(Text.literal(" - ").fillStyle(Style.EMPTY.withItalic(false).withColor(Formatting.GRAY)))
                .append(Text.literal(name).fillStyle(Style.EMPTY.withItalic(false).withColor(Formatting.WHITE)));

        List<Text> argText = new ArrayList<>();
        argText.add(t);
        return argText;
    }

    public static class ScriptActionTagOption {
        String name;
        Item icon;
        String description = "";

        public ScriptActionTagOption(String name, Item icon, String description) {
            this.name = name;
            this.icon = icon;
            this.description = description;
        }

        public ScriptActionTagOption(String name, Item icon) {
            this(name, icon, "");
        }

        public String getName() {
            return name;
        }

        public ItemStack getIcon(ScriptActionTag parentTag, boolean isVariable) {
            ItemStack item = new ItemStack(icon);

            item.set(DataComponentTypes.CUSTOM_NAME, Text.literal(parentTag.name)
                    .fillStyle(Style.EMPTY
                            .withColor(Formatting.WHITE)
                            .withItalic(false)));

            List<Text> lore = new ArrayList<>();

            lore.add(Text.literal((isVariable ? "Default Value: " : "Current Value: ") + name)
                    .fillStyle(Style.EMPTY
                            .withColor(Formatting.GRAY)
                            .withItalic(false)));

            if(!description.isBlank()) {
                lore.add(Text.literal(""));

                for (String descriptionLine : description.split("\n", -1)) {
                    lore.add(Text.literal(descriptionLine)
                            .fillStyle(Style.EMPTY
                                    .withColor(Formatting.GRAY)
                                    .withItalic(false)));
                }
            }

            item.set(DataComponentTypes.LORE, new LoreComponent(lore));

            return item;
        }
    }

    public static class Serializer implements JsonSerializer<ScriptActionTag>, JsonDeserializer<ScriptActionTag> {

        @Override
        public JsonElement serialize(ScriptActionTag src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject obj = new JsonObject();

            obj.addProperty("name", src.name);
            obj.addProperty("default", src.defaultOption);

            JsonArray options = new JsonArray();

            for(ScriptActionTagOption option : src.options) {
                JsonObject optionObj = new JsonObject();

                optionObj.addProperty("name", option.name);
                optionObj.addProperty("icon", Registries.ITEM.getId(option.icon).toString());
                optionObj.addProperty("description", option.description);

                options.add(optionObj);
            }

            obj.add("options", options);

            return obj;
        }

        @Override
        public ScriptActionTag deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            JsonObject obj = json.getAsJsonObject();

            String name = obj.get("name").getAsString();
            String defaultOption = obj.get("default").getAsString();

            JsonArray optionArray = obj.getAsJsonArray("options");
            ArrayList<ScriptActionTagOption> options = new ArrayList<>();

            for(JsonElement element : optionArray) {
                JsonObject optionObj = element.getAsJsonObject();

                String optionName = optionObj.get("name").getAsString();
                Identifier iconType = Identifier.of(optionObj.get("icon").getAsString());
                String description = optionObj.get("description").getAsString();

                options.add(new ScriptActionTagOption(optionName, Registries.ITEM.get(iconType), description));
            }

            return new ScriptActionTag(name, defaultOption, options);
        }
    }
}
