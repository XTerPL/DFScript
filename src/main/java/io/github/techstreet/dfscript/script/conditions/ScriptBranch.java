package io.github.techstreet.dfscript.script.conditions;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import io.github.techstreet.dfscript.screen.ContextMenuButton;
import io.github.techstreet.dfscript.script.*;
import io.github.techstreet.dfscript.script.action.ScriptActionArgumentList;
import io.github.techstreet.dfscript.script.argument.ScriptArgument;
import io.github.techstreet.dfscript.script.argument.ScriptTag;
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
import java.util.function.Consumer;

public class ScriptBranch extends ScriptParametrizedPart implements ScriptScopeParent {

    public static String closeBracketName = "Close Bracket";
    public static ItemStack closeBracketIcon = new ItemStack(Items.PISTON);
    static String elseName = "Else";
    static ItemStack elseIcon = new ItemStack(Items.END_STONE);

    static {
        closeBracketIcon.set(DataComponentTypes.CUSTOM_NAME, Text.literal(closeBracketName)
                .fillStyle(Style.EMPTY
                        .withColor(Formatting.WHITE)
                        .withItalic(false)));

        List<Text> lore = new ArrayList<>();

        lore.add(Text.literal("Closes the current code block.").setStyle(Style.EMPTY.withColor(Formatting.GRAY).withItalic(false)));

        closeBracketIcon.set(DataComponentTypes.LORE, new LoreComponent(lore));

        elseIcon.set(DataComponentTypes.CUSTOM_NAME, Text.literal(elseName)
                .fillStyle(Style.EMPTY
                        .withColor(Formatting.WHITE)
                        .withItalic(false)));

        lore = new ArrayList<>();

        lore.add(Text.literal("Executes if the last IF condition failed.").setStyle(Style.EMPTY.withColor(Formatting.GRAY).withItalic(false)));

        elseIcon.set(DataComponentTypes.LORE, new LoreComponent(lore));
    }
    boolean hasElse = false;

    ScriptCondition condition;
    ScriptContainer container;

    public ScriptBranch(List<ScriptArgument> arguments, List<ScriptTag> tags, ScriptCondition condition) {
        super(arguments, tags);
        this.condition = condition;
        container = new ScriptContainer(2);
    }

    @Override
    public void create(ScriptPartRender render, Script script) {
        condition.create(render, script, "If", "Unless", this);

        render.addElement(container.createSnippet(0));

        render.addElement(new ScriptPartRenderIconElement(closeBracketName, closeBracketIcon));

        if(hasElse)
        {
            render.addElement(new ScriptPartRenderIconElement(elseName, elseIcon));

            render.addElement(container.createSnippet(1));

            render.addElement(new ScriptPartRenderIconElement(closeBracketName, closeBracketIcon));
        }
    }

    public ScriptBranch setHasElse() {
        hasElse = !hasElse;
        return this;
    }

    public boolean hasElse() {
        return hasElse;
    }

    @Override
    public void run(ScriptTask task) {
        ScriptActionContext actionCtx = new ScriptActionContext(task, getArguments(), getTags());
        boolean result = condition.run(actionCtx);

        if(!result && !hasElse) return;

        container.runSnippet(task, result ? 0 : 1, this);
    }

    public ScriptCondition getCondition() {
        return condition;
    }

    @Override
    public ArrayList<ScriptNotice> getNotices() {
        var notices = super.getNotices();

        ScriptNotice typeNotice = condition.getNotice("If ");

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
    public void forEach(Consumer<ScriptSnippet> consumer) {
        container.forEach(consumer);
    }

    @Override
    public ScriptContainer container() {
        return container;
    }

    @Override
    public List<ContextMenuButton> getContextMenu() {
        List<ContextMenuButton> extra = new ArrayList<>();
        extra.add(new ContextMenuButton("Invert", () -> condition.invert()));
        extra.add(new ContextMenuButton(!hasElse ? "Add Else Statement" : "Remove Else Statement", this::setHasElse));
        return extra;
    }

    @Override
    public ItemStack getIcon() {
        return condition.getIcon("If", "Unless");
    }

    @Override
    public String getName() {
        return condition.getName("If", "Unless");
    }

    public static class Serializer implements JsonSerializer<ScriptBranch> {

        @Override
        public JsonElement serialize(ScriptBranch src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "branch");
            obj.add("condition", context.serialize(src.condition));
            obj.add("arguments", context.serialize(src.getArguments()));
            obj.add("tags", context.serialize(src.getTags()));
            obj.addProperty("hasElse", src.hasElse);
            obj.add("true", context.serialize(src.container().getSnippet(0)));
            obj.add("false", context.serialize(src.container().getSnippet(1)));
            return obj;
        }
    }
}
