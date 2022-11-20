package io.github.techstreet.dfscript.script;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.event.system.Event;
import io.github.techstreet.dfscript.script.action.ScriptAction;
import io.github.techstreet.dfscript.script.action.ScriptActionType;
import io.github.techstreet.dfscript.script.action.ScriptRunnablePart;
import io.github.techstreet.dfscript.script.argument.ScriptArgument;
import io.github.techstreet.dfscript.script.argument.ScriptUnknownArgument;
import io.github.techstreet.dfscript.script.function.ScriptCallFunction;
import io.github.techstreet.dfscript.script.function.ScriptFunction;
import io.github.techstreet.dfscript.script.options.ScriptNamedOption;
import io.github.techstreet.dfscript.script.event.ScriptEvent;
import io.github.techstreet.dfscript.script.execution.ScriptContext;
import io.github.techstreet.dfscript.script.execution.ScriptPosStack;
import io.github.techstreet.dfscript.script.execution.ScriptScopeVariables;
import io.github.techstreet.dfscript.script.execution.ScriptTask;
import io.github.techstreet.dfscript.script.values.ScriptUnknownValue;
import io.github.techstreet.dfscript.script.values.ScriptValue;
import io.github.techstreet.dfscript.util.chat.ChatType;
import io.github.techstreet.dfscript.util.chat.ChatUtil;
import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Script {
    public static int scriptVersion = 4;

    private String name;
    private String owner;
    private String description = "N/A";
    private int version = 0;
    private String server;
    private final List<ScriptPart> parts;

    private final List<ScriptNamedOption> options;
    private final Logger LOGGER;
    private final ScriptContext context = new ScriptContext(this);
    private File file;
    private boolean disabled;

    public Script(String name, String owner, String server, List<ScriptPart> parts, boolean disabled, int version) {
        this.name = name;
        this.owner = owner;
        this.server = server;
        this.parts = parts;
        this.disabled = disabled;
        this.version = version;
        this.options = new ArrayList<>();

        LOGGER = LogManager.getLogger("Script." + name);
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void invoke(Event event) {
        int pos = 0;
        for (ScriptPart part : parts) {
            if (part instanceof ScriptEvent se) {
                if (se.getType().getCodeutilitiesEvent().equals(event.getClass())) {
                    try {
                        this.execute(new ScriptTask(new ScriptPosStack(pos+1), event,this));
                    } catch (Exception err) {
                        ChatUtil.sendMessage("Error while invoking event " + se.getType().getName() + " in script " + name + ": " + err.getMessage(), ChatType.FAIL);
                        LOGGER.error("Error while invoking event " + se.getType().getName(), err);
                        err.printStackTrace();
                    }
                }
            }
            pos++;
        }
    }

    public void execute(ScriptTask task) {
        if (disabled) { // don't run the code if it's disabled obviously
            return;
        }

        while (task.stack().peek() < parts.size()) { // check if there is still code to be run
            ScriptPart part = parts.get(task.stack().peek()); // get the script part (action or event who cares)
            if (part instanceof ScriptEvent || part instanceof ScriptFunction) { // well maybe we do care?
                if(task.stack().peekElement().isFunction()) {
                    if(endScope(task)) {
                        return;
                    }
                }
                else {
                    return;
                }
            } else if (part instanceof ScriptRunnablePart srp) { // only run ScriptActions (possibly being able to implement comments?)
                Consumer<ScriptScopeVariables> inner = null;
                if (srp.hasChildren()) {
                    int posCopy = task.stack().peek(); // get the current position for later
                    inner = (scriptScopeVariables) -> task.schedule(posCopy, scriptScopeVariables); // schedule the configurable code
                    int depth = 0;
                    while (task.stack().peek() < parts.size()) { // loop through all the script parts
                        ScriptPart nextPart = parts.get(task.stack().peek());
                        if (nextPart instanceof ScriptEvent || nextPart instanceof ScriptFunction) { // so we can see whether it's an event or an action
                            task.stack().clear();
                            return;
                        } else if (nextPart instanceof ScriptRunnablePart srp2) {
                            if (srp2.hasChildren()) { // we increase the depth if it has children
                                depth++;
                            } else if (srp2 instanceof ScriptAction sa2 && sa2.getType() == ScriptActionType.CLOSE_BRACKET) { // or we decrease it if we get to the end of the inner code
                                depth--;
                                if (depth == 0) { // stop when we reach the same depth as the original script action
                                    break;
                                }
                            }
                        } else if (nextPart instanceof ScriptComment) {
                            // ignore comments
                        } else {
                            throw new IllegalStateException("Unexpected script part type: " + nextPart.getClass().getName());
                        }
                        if (!task.stack().isEmpty()) { // are we done with code yet?
                            task.stack().increase();
                        } else {
                            return;
                        }
                    }
                }
                if(srp.getGroup() == ScriptGroup.CONDITION) { // if it's a condition
                    if(srp instanceof ScriptAction sa && sa.getType() != ScriptActionType.ELSE) { // and not an else
                        task.stack().peekElement().setVariable("lastIfResult", false); // set the last result to false
                    }
                }
                else {
                    task.stack().peekElement().setVariable("lastIfResult", true); //does this detect close brackets or no (no it doesn't, good)
                }
                srp.invoke(task.event(), context, inner,task, this); // execute the script action
                if (!task.isRunning()) { // is the script still running?
                    return;
                }
                if(srp.getGroup() == ScriptGroup.CONDITION) { // if it's a condition
                    if(task.stack().peekElement().getVariable("lastIfResult").equals(true)) { // and it's last if result worked
                        inner.accept(null);
                    }
                }
                if (srp instanceof ScriptAction sa && sa.getType() == ScriptActionType.CLOSE_BRACKET) { // is this the end of the scope?
                    if(task.stack().peekElement().isFunction()) {
                        return;
                    }

                    if(endScope(task))
                    {
                        return;
                    }
                }
                while(context.isForcedToEndScope()) { // are we forced to end the scope? (aka was skip iteration used?)
                    context.forceEndScope(-1);
                    if(endScope(task))
                    {
                        return;
                    }
                }
                if(context.isLoopBroken()) { // are we forced to break the loop? (aka was stop repetition used?)
                    context.breakLoop(-1);
                    task.stack().pop(); // don't use endScope() because of the fact that endScope runs the condition to see if it is false before ending the scope
                }
            } else if (part instanceof ScriptComment) {
                // ignore the comment lol
            } else {
                throw new IllegalArgumentException("Invalid script part");
            }
            if (!task.stack().isEmpty()) { // did we finish executing the code?
                task.stack().increase();
            } else {
                return;
            }
        }
    }

    private boolean endScope(ScriptTask task) {
        if(task.stack().peekElement().checkCondition()) {
            if(!task.stack().peekElement().hasVariable("LagslayerCounter")) {
                task.stack().peekElement().setVariable("LagslayerCounter", 0);
            }

            int lagslayerCounter = (Integer)task.stack().peekElement().getVariable("LagslayerCounter")+1;

            task.stack().peekElement().setVariable("LagslayerCounter", lagslayerCounter);

            if(lagslayerCounter >= 100000) {
                task.stack().peekElement().setVariable("LagslayerCounter", 0);
                task.stop();//Lagslayer be like:
            }

            task.stack().peekElement().setPos(task.stack().peekElement().getOriginalPos());
            return false;
        }

        if (task.stack().isEmpty()) {
            return true;
        } else {
            task.stack().pop();
        }

        return false;
    }
    public List<ScriptPart> getParts() {
        return parts;
    }

    public String getName() {
        return name;
    }

    public String getOwner() {
        return owner;
    }

    public int getVersion() {
        return version;
    }

    public String getDescription() {
        return description;
    }

    public String getServer() {
        return server;
    }

    public boolean disabled() {
        return disabled;
    }

    public void setDisabled(boolean b) {
        disabled = b;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public ScriptContext getContext() {
        return context;
    }

    public void replaceAction(ScriptActionType oldAction, ScriptActionType newAction) {
        for(int i = 0; i < parts.size(); i++)
        {
            if(parts.get(i) instanceof ScriptAction)
            {
                if(((ScriptAction) parts.get(i)).getType() == oldAction)
                {
                    parts.set(i, ((ScriptAction) parts.get(i)).setType(newAction));
                }
            }
        }
    }

    public List<ScriptNamedOption> getOptions() {
        return options;
    }

    public void addOption(int pos, ScriptNamedOption option) {
        options.add(pos, option);
    }

    public boolean optionExists(String option) {
        for(ScriptNamedOption o : getOptions()) {
            if(Objects.equals(o.getName(), option)) return true;
        }

        return false;
    }

    public ScriptValue getOption(String option) {
        for(ScriptNamedOption o : getOptions()) {
            if(Objects.equals(o.getName(), option)) return o.getValue();
        }

        return new ScriptUnknownValue();
    }

    public String getUnnamedOption() {
        for(int i = 1; ; i++) {

            String name = "Option";

            if(i != 1) {
                name = name + " " + i;
            }

            if(!optionExists(name)) {
                return name;
            }
        }
    }

    public ScriptNamedOption getNamedOption(String option) {
        for(ScriptNamedOption o : getOptions()) {
            if(Objects.equals(o.getName(), option)) return o;
        }

        return null;
    }

    public HashMap<String, ScriptFunction> getFunctions() {
        HashMap<String, ScriptFunction> funcs = new HashMap<>();
        for(ScriptPart part : getParts()) {
            if(part instanceof ScriptFunction f && !Objects.equals(f.getFunctionName(), "")) {
                funcs.put(f.getFunctionName(), f);
            }
        }
        return funcs;
    }

    private void updateScriptReferences() {
        for(ScriptPart part : getParts()) {
            if(part instanceof ScriptAction a) {
                a.updateScriptReferences(this);
            }
        }
    }

    public void replaceOption(String oldOption, String newOption) {
        for(ScriptPart part : getParts()) {
            if(part instanceof ScriptRunnablePart a) {
                a.updateConfigArguments(oldOption, newOption);
            }
        }
    }

    public void replaceFunction(String oldFunction, String newFunction) {
        if(Objects.equals(oldFunction, "")) {
            return;
        }

        for(ScriptPart part : getParts()) {
            if(part instanceof ScriptCallFunction cf) {
                if(Objects.equals(cf.getFunctionName(), oldFunction)) {
                    cf.setFunctionName(newFunction);
                }
            }
        }
    }

    public void removeFunction(String function) {
        if(Objects.equals(function, "")) {
            return;
        }

        for(ScriptPart part : getParts()) {
            if(part instanceof ScriptCallFunction cf) {
                if(Objects.equals(cf.getFunctionName(), function)) {
                    cf.setFunctionName("");
                }
            }
        }
    }

    public void removeOption(String option) {
        for(ScriptPart part : getParts()) {
            if(part instanceof ScriptRunnablePart a) {
                a.removeConfigArguments(option);
            }
        }
    }

    public static class Serializer implements JsonSerializer<Script>, JsonDeserializer<Script> {
        @Override
        public Script deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject object = json.getAsJsonObject();
            String name = object.get("name").getAsString();

            String owner = DFScript.PLAYER_UUID;
            if (object.get("owner") != null) owner = object.get("owner").getAsString();

            String serverId = "None";
            if (object.get("server") != null) serverId = object.get("server").getAsString();

            String description = "N/A";
            if (object.get("description") != null) description = object.get("description").getAsString();

            List<ScriptPart> parts = new ArrayList<>();
            for (JsonElement element : object.get("actions").getAsJsonArray()) {
                ScriptPart part = context.deserialize(element, ScriptPart.class);
                parts.add(part);
            }

            boolean disabled = object.has("disabled") && object.get("disabled").getAsBoolean();

            int version = 0;
            if (object.get("version") != null) version = object.get("version").getAsInt();

            Script script = new Script(name, owner, serverId, parts, disabled, version);
            script.setDescription(description);

            if (object.get("config") != null) for (JsonElement element : object.get("config").getAsJsonArray()) {
                ScriptNamedOption option = context.deserialize(element, ScriptNamedOption.class);
                script.addOption(script.getOptions().size(), option);
            }

            script.updateScriptReferences();

            return script;
        }

        @Override
        public JsonElement serialize(Script src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            object.addProperty("name", src.name);
            object.addProperty("owner", src.owner);
            object.addProperty("server", src.server);
            object.addProperty("description", src.description);

            JsonArray array = new JsonArray();
            for (ScriptPart part : src.getParts()) {
                array.add(context.serialize(part));
            }

            JsonArray config = new JsonArray();
            for (ScriptNamedOption option : src.getOptions()) {
                config.add(context.serialize(option));
            }

            object.add("actions", array);
            object.add("config", config);
            object.addProperty("disabled", src.disabled);
            object.addProperty("version", src.version);
            return object;
        }
    }
}
