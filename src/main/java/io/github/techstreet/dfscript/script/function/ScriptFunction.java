package io.github.techstreet.dfscript.script.function;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import io.github.techstreet.dfscript.script.ScriptGroup;
import io.github.techstreet.dfscript.script.ScriptPart;
import io.github.techstreet.dfscript.script.event.ScriptEventType;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.lang.reflect.Type;

public class ScriptFunction implements ScriptPart {

    private String name;
    private String icon;

    public ScriptFunction(String name) {
        this(name, "lapis_lazuli");
    }
    public ScriptFunction(String name, String icon) {
        this.name = name;
        this.icon = icon;
    }

    public void setFunctionName(String name) {
        this.name = name;
    }

    public String getFunctionName() {
        return name;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Override
    public ScriptGroup getGroup() {
        return ScriptGroup.EVENT;
    }

    public String getIcon() {
        return icon;
    }

    public ItemStack getFullIcon() {
        ItemStack icon = new ItemStack(Registry.ITEM.get(new Identifier(getIcon())));
        icon.setCustomName(Text.literal(getFunctionName()).setStyle(Style.EMPTY.withItalic(false)));
        return icon;
    }

    public static class Serializer implements JsonSerializer<ScriptFunction> {

        @Override
        public JsonElement serialize(ScriptFunction src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "function");
            obj.addProperty("function", src.getFunctionName());
            obj.addProperty("icon", src.getIcon());
            return obj;
        }
    }
}
