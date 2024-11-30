package io.github.techstreet.dfscript.script.repetitions;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import io.github.techstreet.dfscript.script.Script;
import io.github.techstreet.dfscript.script.ScriptNotice;
import io.github.techstreet.dfscript.script.ScriptNoticeLevel;
import io.github.techstreet.dfscript.script.action.ScriptActionArgumentList;
import io.github.techstreet.dfscript.script.argument.ScriptArgument;
import io.github.techstreet.dfscript.script.argument.ScriptTag;
import io.github.techstreet.dfscript.script.execution.ScriptActionContext;
import io.github.techstreet.dfscript.script.execution.ScriptTask;
import io.github.techstreet.dfscript.script.render.ScriptPartRender;
import io.github.techstreet.dfscript.script.render.ScriptPartRenderIconElement;
import net.minecraft.item.ItemStack;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ScriptBuiltinRepetition extends ScriptRepetition {

    private ScriptRepetitionType type;

    public ScriptBuiltinRepetition(List<ScriptArgument> arguments, List<ScriptTag> tags, ScriptRepetitionType type) {
        super(arguments, tags);
        this.type = type;
    }

    @Override
    public void create(ScriptPartRender render, Script script) {
        render.addElement(new ScriptPartRenderIconElement(getName(), putNotices(getIcon())));

        super.create(render, script);
    }

    public ScriptBuiltinRepetition setType(ScriptRepetitionType newType) {
        type = newType;

        return this;
    }

    public ScriptRepetitionType getType() {
        return type;
    }

    @Override
    public ArrayList<ScriptNotice> getNotices() {
        var notices = super.getNotices();

        ScriptNotice typeNotice = type.getNotice();

        if(typeNotice.getLevel() != ScriptNoticeLevel.NORMAL) {
            notices.add(typeNotice);
        }

        return notices;
    }

    @Override
    public ScriptActionArgumentList getActionArgumentList() {
        return type.getArgumentList();
    }

    @Override
    public ItemStack getIcon() {
        return type.getIcon();
    }

    @Override
    public String getName() {
        return type.getName();
    }

    @Override
    public boolean checkCondition(ScriptTask task) {
        ScriptActionContext ctx = new ScriptActionContext(task, getArguments(), getTags());
        return type.run(ctx);
    }

    public static class Serializer implements JsonSerializer<ScriptBuiltinRepetition> {

        @Override
        public JsonElement serialize(ScriptBuiltinRepetition src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "repetition");
            obj.addProperty("repetition", src.getType().name());
            obj.add("arguments", context.serialize(src.getArguments()));
            obj.add("tags", context.serialize(src.getTags()));
            obj.add("snippet", context.serialize(src.container().getSnippet(0)));
            return obj;
        }
    }
}
