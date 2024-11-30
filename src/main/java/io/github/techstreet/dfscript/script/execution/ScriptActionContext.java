package io.github.techstreet.dfscript.script.execution;

import io.github.techstreet.dfscript.script.action.ScriptActionArgument;
import io.github.techstreet.dfscript.script.action.ScriptActionArgumentList;
import io.github.techstreet.dfscript.script.action.ScriptActionTag;
import io.github.techstreet.dfscript.script.argument.ScriptArgument;
import io.github.techstreet.dfscript.script.argument.ScriptTag;
import io.github.techstreet.dfscript.script.argument.ScriptVariableArgument;
import io.github.techstreet.dfscript.script.values.ScriptValue;
import io.github.techstreet.dfscript.script.values.ScriptVariable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class ScriptActionContext {
    private final ScriptTask task;
    private final List<ScriptArgument> arguments;
    private final List<ScriptTag> tags;
    private final HashMap<String, List<ScriptArgument>> argMap;
    private final HashMap<String, ScriptActionArgument> actionArgMap;
    private final HashMap<String, ScriptTag> tagMap;
    private final HashMap<String, ScriptActionTag> actionTagMap;

    public ScriptActionContext(ScriptTask task, List<ScriptArgument> arguments, List<ScriptTag> tags) {
        this.task = task;
        this.arguments = arguments;
        this.tags = tags;
        this.argMap = new HashMap<>();
        this.actionArgMap = new HashMap<>();
        this.actionTagMap = new HashMap<>();
        this.tagMap = new HashMap<>();
    }

    public void setArg(ScriptActionArgument actionArg, List<ScriptArgument> args) {
        argMap.put(actionArg.name(), args);
    }

    public void setTag(ScriptActionTag actionTag, ScriptTag tag) {
        tagMap.put(actionTag.getName(), tag);
    }

    public void putActionArg(ScriptActionArgument actionArg) {
        actionArgMap.put(actionArg.name(), actionArg);
    }

    public void putActionTag(ScriptActionTag tag) {
        actionTagMap.put(tag.getName(), tag);
    }

    public List<ScriptArgument> pluralArg(String messages) {
        return argMap.get(messages);
    }

    public ScriptArgument arg(String name) {
        return argMap.get(name).get(0);
    }

    public ScriptTag tag(String name) {
        return tagMap.get(name);
    }

    public ScriptValue value(String name) {
        if(!argMap.containsKey(name)) {
            return actionArgMap.get(name).defaultValue();
        }

        return arg(name).getValue(task).get();
    }

    public List<ScriptValue> pluralValue(String name) {
        return pluralArg(name).stream().map(arg -> arg.getValue(task).get()).collect(Collectors.toList());
    }

    public String tagValue(String name) {
        ScriptActionTag actionTag = actionTagMap.get(name);

        if(!tagMap.containsKey(name)) {
            return actionTag.getDefaultOptionName();
        }

        ScriptTag tag = tag(name);

        if(tag.getArgument() != null) {
            String optionName = tag.getArgument().getValue(task).asText();

            ScriptActionTag.ScriptActionTagOption option = actionTag.get(optionName);

            if(option != null) {
                return option.getName();
            }
        }

        return actionTag.getOrDefault(tag.getSelectedName()).getName();
    }

    public ScriptVariable variable(String name) {
        if(arg(name).getValue(task) instanceof ScriptVariable var)
        {
            return var;
        }

        throw new UnsupportedOperationException("Tried to get a variable from a constant.");
    }

    public void setVariable(String name, ScriptValue value) {
        variable(name).set(value);
    }

    public void setScopeVariable(String name, Object object) {
        task().stack().peek(0).setVariable(name, object);
    }

    public Object getScopeVariable(String name) {
        return task().stack().peek(0).getVariable(name);
    }

    public boolean hasScopeVariable(String name) {
        return task().stack().peek(0).hasVariable(name);
    }

    public ScriptTask task() {
        return task;
    }

    public List<ScriptArgument> arguments() {
        return arguments;
    }

    public List<ScriptTag> tags() {
        return tags;
    }

    public HashMap<String, List<ScriptArgument>> argMap() {
        return argMap;
    }

    public HashMap<String, ScriptActionArgument> actionArgMap() {
        return actionArgMap;
    }

    public HashMap<String, ScriptTag> tagMap() {
        return tagMap;
    }

    public HashMap<String, ScriptActionTag> actionTagMap() {
        return actionTagMap;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ScriptActionContext) obj;
        return Objects.equals(this.task, that.task) &&
                Objects.equals(this.arguments, that.arguments) &&
                Objects.equals(this.argMap, that.argMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(task, arguments, argMap);
    }

    @Override
    public String toString() {
        return "ScriptActionContext[" +
                "task=" + task + ", " +
                "arguments=" + arguments + ", " +
                "argMap=" + argMap + ']';
    }
}
