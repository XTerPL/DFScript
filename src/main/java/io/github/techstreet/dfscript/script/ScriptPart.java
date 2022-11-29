package io.github.techstreet.dfscript.script;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import io.github.techstreet.dfscript.script.action.ScriptAction;
import io.github.techstreet.dfscript.script.action.ScriptActionType;
import io.github.techstreet.dfscript.script.argument.ScriptArgument;
import io.github.techstreet.dfscript.script.event.ScriptEvent;
import io.github.techstreet.dfscript.script.event.ScriptEventType;
import io.github.techstreet.dfscript.script.function.ScriptCallFunction;
import io.github.techstreet.dfscript.script.function.ScriptFunction;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public interface ScriptPart {

    ScriptGroup getGroup();

    class Serializer implements JsonDeserializer<ScriptPart> {

        @Override
        public ScriptPart deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject obj = json.getAsJsonObject();
            String type = obj.get("type").getAsString();
            switch (type) {
                case "action" -> {
                    String action = obj.get("action").getAsString();
                    List<ScriptArgument> args = new ArrayList<>();
                    for (JsonElement arg : obj.get("arguments").getAsJsonArray()) {
                        args.add(context.deserialize(arg, ScriptArgument.class));
                    }
                    return new ScriptAction(ScriptActionType.valueOf(action), args);
                }
                case "event" -> {
                    String event = obj.get("event").getAsString();
                    return new ScriptEvent(ScriptEventType.valueOf(event));
                }
                case "comment" -> {
                    String comment = obj.get("comment").getAsString();
                    return new ScriptComment(comment);
                }
                case "function" -> {
                    String name = obj.get("function").getAsString();
                    String icon = obj.has("icon") ? obj.get("icon").getAsString() : "lapis_lazuli";
                    return new ScriptFunction(name, icon);
                }
                case "callFunction" -> {
                    String name = obj.get("function").getAsString();
                    List<ScriptArgument> args = new ArrayList<>();
                    for (JsonElement arg : obj.get("arguments").getAsJsonArray()) {
                        args.add(context.deserialize(arg, ScriptArgument.class));
                    }
                    return new ScriptCallFunction(name, args, null);
                }
                default -> throw new JsonParseException("Unknown script part type: " + type);
            }
        }
    }
}
