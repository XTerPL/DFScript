package io.github.techstreet.dfscript.script.conditions;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import io.github.techstreet.dfscript.screen.ContextMenuButton;
import io.github.techstreet.dfscript.script.*;
import io.github.techstreet.dfscript.script.action.ScriptActionArgument;
import io.github.techstreet.dfscript.script.action.ScriptActionArgumentList;
import io.github.techstreet.dfscript.script.argument.ScriptArgument;
import io.github.techstreet.dfscript.script.argument.ScriptTag;
import io.github.techstreet.dfscript.script.argument.ScriptVariableArgument;
import io.github.techstreet.dfscript.script.execution.ScriptActionContext;
import io.github.techstreet.dfscript.script.execution.ScriptTask;
import io.github.techstreet.dfscript.script.render.ScriptPartRender;
import io.github.techstreet.dfscript.script.render.ScriptPartRenderIconElement;
import io.github.techstreet.dfscript.script.values.ScriptBoolValue;
import io.github.techstreet.dfscript.util.chat.ChatUtil;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ScriptBooleanSet extends ScriptParametrizedPart {

    public static String booleanSetName = "Set to Condition";
    public static ItemStack booleanSetIcon = new ItemStack(Items.PISTON);

    static {
        booleanSetIcon.set(DataComponentTypes.CUSTOM_NAME, Text.literal(booleanSetName)
                .fillStyle(Style.EMPTY
                        .withColor(Formatting.WHITE)
                        .withItalic(false)));

        List<Text> lore = new ArrayList<>();

        lore.add(Text.literal("Sets a variable to the result of a condition.").setStyle(Style.EMPTY.withColor(Formatting.GRAY).withItalic(false)));
        lore.add(Text.literal(""));

        MutableText t = ScriptActionArgument.ScriptActionArgumentType.VARIABLE.text();
        t.append(Text.literal(" - ").fillStyle(Style.EMPTY.withItalic(false).withColor(Formatting.GRAY)))
                .append(Text.literal("Result").fillStyle(Style.EMPTY.withItalic(false).withColor(Formatting.WHITE)));

        lore.add(t);

        booleanSetIcon.set(DataComponentTypes.LORE, new LoreComponent(lore));
    }

    ScriptCondition condition;

    public ScriptBooleanSet(List<ScriptArgument> arguments, List<ScriptTag> tags, ScriptCondition condition) {
        super(arguments, tags);
        this.condition = condition;
    }

    @Override
    public void create(ScriptPartRender render, Script script) {
        render.addElement(new ScriptPartRenderIconElement(getName(), putNotices(getIcon())));
    }

    @Override
    public void run(ScriptTask task) {
        if(getArguments().isEmpty()) {
            ChatUtil.error("You need to add a variable argument to Set to Condition.");
            return;
        }

        ScriptArgument variableArg = getArguments().getFirst();

        if(!(variableArg instanceof ScriptVariableArgument variable)) {
            ChatUtil.error("You need to add a VARIABLE argument to Set to Condition.");
            return;
        }

        List<ScriptArgument> conditionArguments = new ArrayList<>();
        for(int i = 1; i < getArguments().size(); i++) {
            conditionArguments.add(getArguments().get(i));
        }

        ScriptActionContext actionCtx = new ScriptActionContext(task, conditionArguments, getTags());
        boolean result = condition.run(actionCtx);

        variable.getVariable(task).set(new ScriptBoolValue(result));
    }

    public ScriptCondition getCondition() {
        return condition;
    }

    @Override
    public List<ContextMenuButton> getContextMenu() {
        List<ContextMenuButton> extra = new ArrayList<>();
        extra.add(new ContextMenuButton("Invert", () -> condition.invert()));
        return extra;
    }

    @Override
    public ItemStack getIcon() {
        ItemStack icon = booleanSetIcon.copy();

        icon.set(DataComponentTypes.CUSTOM_NAME, Text.literal(getName()).setStyle(Style.EMPTY.withColor(Formatting.WHITE).withItalic(false)));

        List<Text> lore = new ArrayList<>();

        lore.add(Text.literal("Sets a variable to the result of a condition.").setStyle(Style.EMPTY.withColor(Formatting.GRAY).withItalic(false)));
        lore.add(Text.literal(""));

        MutableText t = ScriptActionArgument.ScriptActionArgumentType.VARIABLE.text();
        t.append(Text.literal(" - ").fillStyle(Style.EMPTY.withItalic(false).withColor(Formatting.GRAY)))
         .append(Text.literal("Result").fillStyle(Style.EMPTY.withItalic(false).withColor(Formatting.WHITE)));

        lore.add(t);
        lore.add(Text.literal(""));

        lore.addAll(condition.getLore());

        icon.set(DataComponentTypes.LORE, new LoreComponent(lore));

        return icon;
    }

    @Override
    public ArrayList<ScriptNotice> getNotices() {
        var notices = super.getNotices();

        ScriptNotice typeNotice = condition.getNotice(booleanSetName + ": ");

        if(typeNotice.getLevel() != ScriptNoticeLevel.NORMAL) {
            notices.add(typeNotice);
        }

        return notices;
    }

    @Override
    public ScriptActionArgumentList getActionArgumentList() {
        return condition.getArgumentList();
    }

    @Override
    public String getName() {
        return booleanSetName + ":" + condition.getName("", " NOT");
    }

    public static class Serializer implements JsonSerializer<ScriptBooleanSet> {

        @Override
        public JsonElement serialize(ScriptBooleanSet src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "booleanSet");
            obj.add("condition", context.serialize(src.condition));
            obj.add("arguments", context.serialize(src.getArguments()));
            obj.add("tags", context.serialize(src.getTags()));
            return obj;
        }
    }
}
