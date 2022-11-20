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
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class ScriptCallFunction extends ScriptRunnablePart {
    String function;

    public ScriptCallFunction(String function, List<ScriptArgument> arguments) {
        super(arguments);
        this.function = function;
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
        ItemStack icon = new ItemStack(Items.LAPIS_LAZULI);
        icon.setCustomName(Text.literal(getFunctionName()).setStyle(Style.EMPTY.withItalic(false)));
        return icon;
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
