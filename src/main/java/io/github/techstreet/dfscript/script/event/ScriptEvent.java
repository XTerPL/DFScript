package io.github.techstreet.dfscript.script.event;

import com.google.gson.*;
import io.github.techstreet.dfscript.screen.widget.CItem;
import io.github.techstreet.dfscript.screen.widget.CScrollPanel;
import io.github.techstreet.dfscript.screen.widget.CText;
import io.github.techstreet.dfscript.script.Script;
import io.github.techstreet.dfscript.script.ScriptPart;
import io.github.techstreet.dfscript.script.render.ScriptPartRender;
import io.github.techstreet.dfscript.script.render.ScriptPartRenderIconElement;
import net.minecraft.text.Text;

import java.lang.reflect.Type;

public class ScriptEvent extends ScriptHeader {

    private final ScriptEventType type;

    public ScriptEvent(ScriptEventType type) {
        this.type = type;
    }

    public ScriptEventType getType() {
        return type;
    }

    public static class Serializer implements JsonSerializer<ScriptEvent> {

        @Override
        public JsonElement serialize(ScriptEvent src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "event");
            obj.addProperty("event", src.getType().name());
            obj.add("snippet", context.serialize(src.container().getSnippet(0)));
            return obj;
        }
    }

    @Override
    public void create(ScriptPartRender render, Script script) {
        render.addElement(new ScriptPartRenderIconElement(type.getName(), type.getIcon()));

        super.create(render, script);
    }
}
