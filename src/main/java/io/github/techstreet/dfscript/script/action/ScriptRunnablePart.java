package io.github.techstreet.dfscript.script.action;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import io.github.techstreet.dfscript.event.system.Event;
import io.github.techstreet.dfscript.script.Script;
import io.github.techstreet.dfscript.script.ScriptPart;
import io.github.techstreet.dfscript.script.argument.ScriptArgument;
import io.github.techstreet.dfscript.script.argument.ScriptConfigArgument;
import io.github.techstreet.dfscript.script.execution.ScriptActionContext;
import io.github.techstreet.dfscript.script.execution.ScriptContext;
import io.github.techstreet.dfscript.script.execution.ScriptScopeVariables;
import io.github.techstreet.dfscript.script.execution.ScriptTask;
import net.minecraft.item.ItemStack;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public abstract class ScriptRunnablePart implements ScriptPart {
    protected final List<ScriptArgument> arguments;

    public ScriptRunnablePart(List<ScriptArgument> arguments) {
        this.arguments = arguments;
    }

    public List<ScriptArgument> getArguments() {
        return arguments;
    }

    public void updateScriptReferences(Script script) {
        for(ScriptArgument arg : getArguments()) {
            if (arg instanceof ScriptConfigArgument carg) {
                carg.setScript(script);
            }
        }
    }

    public void updateConfigArguments(String oldOption, String newOption) {
        for(ScriptArgument arg : getArguments()) {
            if (arg instanceof ScriptConfigArgument carg) {
                if(carg.getName() == oldOption)
                {
                    carg.setOption(newOption);
                }
            }
        }
    }

    public void removeConfigArguments(String option) {
        int index = 0;

        List<ScriptArgument> argList = getArguments();

        while(index < argList.size()) {
            if (argList.get(index) instanceof ScriptConfigArgument carg) {
                if(Objects.equals(carg.getName(), option))
                {
                    argList.remove(index);
                    continue;
                }
            }
            index++;
        }
    }

    public abstract void invoke(Event event, ScriptContext context, Consumer<ScriptScopeVariables> inner, ScriptTask task, Script script);

    public Boolean hasChildren() {
        return false;
    }

    abstract public ItemStack getIcon();
    abstract public String getName();
}
