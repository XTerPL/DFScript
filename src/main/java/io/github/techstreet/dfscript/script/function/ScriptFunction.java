package io.github.techstreet.dfscript.script.function;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import io.github.techstreet.dfscript.script.ScriptGroup;
import io.github.techstreet.dfscript.script.ScriptPart;
import io.github.techstreet.dfscript.script.event.ScriptEventType;

import java.lang.reflect.Type;

public class ScriptFunction implements ScriptPart {

    private String name;

    public ScriptFunction(String name) {
        this.name = name;
    }

    public void setFunctionName(String name) {
        this.name = name;
    }

    public String getFunctionName() {
        return name;
    }

    @Override
    public ScriptGroup getGroup() {
        return ScriptGroup.EVENT;
    }

    public static class Serializer implements JsonSerializer<ScriptFunction> {

        @Override
        public JsonElement serialize(ScriptFunction src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "function");
            obj.addProperty("function", src.getFunctionName());
            return obj;
        }
    }

}
