package io.github.techstreet.dfscript.script.repetitions;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import io.github.techstreet.dfscript.screen.ContextMenuButton;
import io.github.techstreet.dfscript.script.Script;
import io.github.techstreet.dfscript.script.ScriptNotice;
import io.github.techstreet.dfscript.script.ScriptNoticeLevel;
import io.github.techstreet.dfscript.script.argument.ScriptArgument;
import io.github.techstreet.dfscript.script.conditions.ScriptCondition;
import io.github.techstreet.dfscript.script.execution.ScriptActionContext;
import io.github.techstreet.dfscript.script.execution.ScriptTask;
import io.github.techstreet.dfscript.script.render.ScriptPartRender;
import io.github.techstreet.dfscript.script.render.ScriptPartRenderIconElement;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ScriptWhile extends ScriptRepetition {

    public static String whileName = "Repeat While";
    public static ItemStack whileIcon = new ItemStack(Items.GOLD_NUGGET);

    static {
        whileIcon.set(DataComponentTypes.CUSTOM_NAME, Text.literal(whileName)
                .fillStyle(Style.EMPTY
                        .withColor(Formatting.WHITE)
                        .withItalic(false)));

        List<Text> lore = new ArrayList<>();

        lore.add(Text.literal("Repeats while a condition is true.").setStyle(Style.EMPTY.withColor(Formatting.GRAY).withItalic(false)));

        whileIcon.set(DataComponentTypes.LORE, new LoreComponent(lore));
    }
    private final ScriptCondition condition;

    public ScriptWhile(List<ScriptArgument> arguments, ScriptCondition condition) {
        super(arguments);
        this.condition = condition;
    }

    @Override
    public void create(ScriptPartRender render, Script script) {
        render.addElement(new ScriptPartRenderIconElement(getName(), putNotices(getIcon())));

        super.create(render, script);
    }

    @Override
    public ItemStack getIcon() {
        ItemStack icon = whileIcon.copy();

        icon.set(DataComponentTypes.CUSTOM_NAME, Text.literal(getName()).setStyle(Style.EMPTY.withColor(Formatting.WHITE).withItalic(false)));

        List<Text> lore = new ArrayList<>();

        lore.add(Text.literal("Repeats while a condition is true.").setStyle(Style.EMPTY.withColor(Formatting.GRAY).withItalic(false)));
        lore.add(Text.literal(""));

        lore.addAll(condition.getLore());

        icon.set(DataComponentTypes.LORE, new LoreComponent(lore));

        return icon;
    }

    @Override
    public String getName() {
        return whileName + ":" + condition.getName("", " NOT");
    }

    @Override
    public ArrayList<ScriptNotice> getNotices() {
        var notices = super.getNotices();

        ScriptNotice typeNotice = condition.getNotice(whileName + ": ");

        if(typeNotice.getLevel() != ScriptNoticeLevel.NORMAL) {
            notices.add(typeNotice);
        }

        return notices;
    }

    @Override
    public List<ContextMenuButton> getContextMenu() {
        List<ContextMenuButton> extra = new ArrayList<>();
        extra.add(new ContextMenuButton("Invert", condition::invert));
        return extra;
    }

    @Override
    public boolean checkCondition(ScriptTask task) {
        ScriptActionContext ctx = new ScriptActionContext(task, getArguments());
        return condition.run(ctx);
    }

    public static class Serializer implements JsonSerializer<ScriptWhile> {

        @Override
        public JsonElement serialize(ScriptWhile src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "while");
            obj.add("condition", context.serialize(src.condition));
            obj.add("arguments", context.serialize(src.getArguments()));
            obj.add("snippet", context.serialize(src.container().getSnippet(0)));
            return obj;
        }
    }
}
