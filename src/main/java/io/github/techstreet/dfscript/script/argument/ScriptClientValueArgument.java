package io.github.techstreet.dfscript.script.argument;

import com.google.gson.*;
import io.github.techstreet.dfscript.screen.ContextMenuButton;
import io.github.techstreet.dfscript.script.ScriptNotice;
import io.github.techstreet.dfscript.script.action.ScriptActionArgument.ScriptActionArgumentType;
import io.github.techstreet.dfscript.script.execution.ScriptTask;
import io.github.techstreet.dfscript.script.values.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ScriptClientValueArgument implements ScriptArgument {
    ScriptClientValueType type;
    String mode;

    public ScriptClientValueArgument(ScriptClientValueType type, String mode) {
        this.type = type;
        this.mode = mode;
    }

    public ScriptClientValueArgument(ScriptClientValueType type) {
        this.type = type;

        List<ScriptClientValueType.ScriptClientValueMode> modes = type.getModes();

        this.mode = null;
        if(!modes.isEmpty()) {
            this.mode = modes.getFirst().getValue();
        }
    }

    public void updateMode() {
        if(this.type.getModes().isEmpty()) {
            this.mode = null;
            return;
        }

        for(ScriptClientValueType.ScriptClientValueMode mode : this.type.getModes()) {
            if(Objects.equals(mode.getValue(), this.mode)) {
                return;
            }
        }

        this.mode = this.type.getModes().getFirst().getValue();
    }

    public ItemStack getIcon() {
        return type.getIcon();
    }

    public String getName() {
        return type.getName();
    }

    public ScriptNotice getNotice() {
        return type.getNotice();
    }

    public String getNoticeDescriptor() {
        return "client value";
    }

    @Override
    public List<ContextMenuButton> getContextMenu() {
        List<ContextMenuButton> context = new ArrayList<>();

        for(ScriptClientValueType.ScriptClientValueMode mode : this.type.getModes()) {
            context.add(new ContextMenuButton(
                    mode.getValue(), () -> setMode(mode.getValue())
            ));
        }

        return context;
    }

    @Override
    public ScriptValue getValue(ScriptTask task) {
        return type.getValue(task, mode);
    }

    @Override
    public boolean convertableTo(ScriptActionArgumentType type) {
        ScriptActionArgumentType argType = this.type.getArgumentType();
        if(argType == null) {
            return false;
        }
        return argType.convertableTo(type);
    }

    public ScriptClientValueType getClientValueType() {
        return type;
    }

    public void setClientValueType(ScriptClientValueType type) {
        this.type = type;
    }

    public void setMode(String mode) {
        this.mode = mode;
        updateMode();
    }

    public static class Serializer implements JsonSerializer<ScriptClientValueArgument>, JsonDeserializer<ScriptClientValueArgument> {

        @Override
        public JsonElement serialize(ScriptClientValueArgument src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            object.addProperty("type","CLIENT_VALUE");
            object.addProperty("value", src.type.name());
            if(src.mode != null) {
                object.addProperty("mode", src.mode);
            }
            return object;
        }

        @Override
        public ScriptClientValueArgument deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject obj = json.getAsJsonObject();

            String mode = null;
            if(obj.has("mode")) {
                mode = obj.get("mode").getAsString();
            }

            return new ScriptClientValueArgument(ScriptClientValueType.valueOf(obj.get("value").getAsString()), mode);
        }
    }

    @Override
    public ItemStack getArgIcon() {
        ItemStack result = new ItemStack(Items.NAME_TAG);
        result.set(DataComponentTypes.CUSTOM_NAME, Text.literal("Client Value").setStyle(Style.EMPTY.withColor(Formatting.WHITE).withItalic(false)));
        return result;
    }

    @Override
    public String getArgText() {
        return getName();
    }

    @Override
    public Text getArgIconText() {
        if(this.mode == null) return null;

        for(ScriptClientValueType.ScriptClientValueMode mode : this.type.getModes()) {
            if(Objects.equals(mode.getValue(), this.mode)) {
                return mode.getShortText();
            }
        }

        return null;
    }

    @Override
    public String getOverwrite() {
        return getName();
    }
}
