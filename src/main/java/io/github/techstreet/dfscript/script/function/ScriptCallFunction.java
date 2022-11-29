package io.github.techstreet.dfscript.script.function;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import io.github.techstreet.dfscript.event.system.Event;
import io.github.techstreet.dfscript.script.Script;
import io.github.techstreet.dfscript.script.ScriptGroup;
import io.github.techstreet.dfscript.script.action.ScriptAction;
import io.github.techstreet.dfscript.script.action.ScriptRunnablePart;
import io.github.techstreet.dfscript.script.argument.ScriptArgument;
import io.github.techstreet.dfscript.script.execution.ScriptActionContext;
import io.github.techstreet.dfscript.script.execution.ScriptContext;
import io.github.techstreet.dfscript.script.execution.ScriptScopeVariables;
import io.github.techstreet.dfscript.script.execution.ScriptTask;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class ScriptCallFunction extends ScriptRunnablePart {
    String function;
    transient Script script;

    public ScriptCallFunction(String function, List<ScriptArgument> arguments, Script script) {
        super(arguments);
        this.function = function;
        this.script = script;
    }

    public String getFunctionName() {
        return function;
    }

    public void setFunctionName(String function) {
        this.function = function;
    }

    @Override
    public ScriptGroup getGroup() {
        return ScriptGroup.ACTION;
    }

    @Override
    public void invoke(Event event, ScriptContext context, Consumer<ScriptScopeVariables> inner, ScriptTask task, Script script) {
        ScriptActionContext ctx = new ScriptActionContext(
                context, arguments, event, inner, task, new HashMap<>(), script);
        ctx.scheduleFunction(getFunctionName(), null, null);
    }

    @Override
    public ItemStack getIcon() {
        ScriptFunction func = getFunction();

        if(func == null) {
            ItemStack icon = new ItemStack(Items.RED_DYE);
            icon.setCustomName(Text.literal(getFunctionName()).setStyle(Style.EMPTY.withItalic(false)));
            NbtList lore = new NbtList();

            lore.add(NbtString.of(Text.Serializer.toJson(Text.literal("Missing Function Definition")
                    .fillStyle(Style.EMPTY
                            .withColor(Formatting.RED)
                            .withItalic(false)))));

            icon.getSubNbt("display")
                    .put("Lore", lore);

            return icon;
        }

        return func.getFullIcon();
    }

    public ScriptFunction getFunction() {
        if(getScript().getFunctions().containsKey(function))
            return getScript().getFunctions().get(function);
        return null;
    }

    public Script getScript() {
        return script;
    }

    public void setScript(Script script) {
        this.script = script;
    }

    @Override
    public String getName() {
        return getFunctionName();
    }

    public static class Serializer implements JsonSerializer<ScriptCallFunction> {

        @Override
        public JsonElement serialize(ScriptCallFunction src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "callFunction");
            obj.addProperty("function", src.getFunctionName());
            obj.add("arguments", context.serialize(src.getArguments()));
            return obj;
        }
    }
}
