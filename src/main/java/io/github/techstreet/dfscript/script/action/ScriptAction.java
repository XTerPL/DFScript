package io.github.techstreet.dfscript.script.action;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import io.github.techstreet.dfscript.event.system.Event;
import io.github.techstreet.dfscript.script.Script;
import io.github.techstreet.dfscript.script.ScriptGroup;
import io.github.techstreet.dfscript.script.argument.ScriptArgument;
import io.github.techstreet.dfscript.script.execution.ScriptActionContext;
import io.github.techstreet.dfscript.script.execution.ScriptContext;
import io.github.techstreet.dfscript.script.execution.ScriptScopeVariables;
import io.github.techstreet.dfscript.script.execution.ScriptTask;
import net.minecraft.item.ItemStack;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class ScriptAction extends ScriptRunnablePart {

    private ScriptActionType type;

    public ScriptAction(ScriptActionType type, List<ScriptArgument> arguments) {
        super(arguments);
        this.type = type;
    }

    public ScriptAction setType(ScriptActionType newType) {
        type = newType;

        return this;
    }

    @Override
    public void invoke(Event event, ScriptContext context, Consumer<ScriptScopeVariables> inner, ScriptTask task, Script script) {
        type.run(new ScriptActionContext(
            context, arguments, event, inner, task, new HashMap<>(), script
        ));
    }

    public ScriptActionType getType() {
        return type;
    }

    @Override
    public ScriptGroup getGroup() {
        return getType().getGroup();
    }

    @Override
    public Boolean hasChildren() {
        return getType().hasChildren();
    }

    @Override
    public ItemStack getIcon() {
        return getType().getIcon();
    }

    @Override
    public String getName() {
        return getType().getName();
    }

    public static class Serializer implements JsonSerializer<ScriptAction> {

        @Override
        public JsonElement serialize(ScriptAction src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "action");
            obj.addProperty("action", src.getType().name());
            obj.add("arguments", context.serialize(src.getArguments()));
            return obj;
        }
    }
}