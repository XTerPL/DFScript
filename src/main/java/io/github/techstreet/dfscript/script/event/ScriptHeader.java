package io.github.techstreet.dfscript.script.event;

import com.google.gson.*;
import io.github.techstreet.dfscript.screen.widget.CScrollPanel;
import io.github.techstreet.dfscript.script.*;
import io.github.techstreet.dfscript.script.action.*;
import io.github.techstreet.dfscript.script.argument.ScriptArgument;
import io.github.techstreet.dfscript.script.execution.ScriptTask;
import io.github.techstreet.dfscript.script.render.ScriptPartRender;
import io.github.techstreet.dfscript.script.render.ScriptPartRenderSnippetElement;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class ScriptHeader implements ScriptRunnable, ScriptScopeParent {
    int x = 0;
    int y = 0;
    ScriptContainer container;
    ScriptHeader()
    {
        container = new ScriptContainer(1);
    }

    @Override
    public void run(ScriptTask task)
    {
        container.runSnippet(task, 0, this);
    }

    public void create(ScriptPartRender render, Script script) {
        render.addElement(new ScriptPartRenderSnippetElement(container().getSnippet(0)));
    }

    @Override
    public void forEach(Consumer<ScriptSnippet> consumer) {
        container.forEach(consumer);
    }

    @Override
    public ScriptContainer container() {
        return container;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public static class Serializer implements JsonDeserializer<ScriptHeader> {

        @Override
        public ScriptHeader deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject obj = json.getAsJsonObject();
            String type = obj.get("type").getAsString();
            ScriptHeader header;
            switch (type) {
                case "empty" -> {
                    header = new ScriptEmptyHeader();
                }
                case "event" -> {
                    String event = obj.get("event").getAsString();
                    header = new ScriptEvent(ScriptEventType.valueOf(event));
                }
                case "function" -> {
                    String function = obj.get("name").getAsString();
                    String icon = obj.get("icon").getAsString();
                    String description = obj.get("description").getAsString();
                    ScriptActionArgumentList list = context.deserialize(obj.getAsJsonObject("args"), ScriptActionArgumentList.class);
                    header = new ScriptFunction(function, Registries.ITEM.get(new Identifier(icon)), list, description);
                }
                default -> throw new JsonParseException("Unknown script header type: " + type);
            }
            header.container().setSnippet(0, context.deserialize(obj.getAsJsonObject("snippet"), ScriptSnippet.class));

            return header;
        }
    }
}
