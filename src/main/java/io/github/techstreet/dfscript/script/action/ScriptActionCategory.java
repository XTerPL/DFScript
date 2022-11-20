package io.github.techstreet.dfscript.script.action;

import io.github.techstreet.dfscript.script.Script;
import io.github.techstreet.dfscript.script.ScriptComment;
import io.github.techstreet.dfscript.script.event.ScriptEvent;
import io.github.techstreet.dfscript.script.event.ScriptEventType;
import io.github.techstreet.dfscript.script.function.ScriptCallFunction;
import io.github.techstreet.dfscript.script.function.ScriptFunction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.slf4j.helpers.FormattingTuple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

public enum ScriptActionCategory {

    EVENTS("Events", Items.DIAMOND,
            Arrays.stream(ScriptEventType.values()).map((eventType) -> new ScriptActionCategoryExtra(eventType.getIcon(), (script) -> new ScriptEvent(eventType))).toList()),

    FUNCTIONS("Functions", Items.LAPIS_BLOCK, (script) -> {
        List<ScriptActionCategoryExtra> extras = new ArrayList<>();

        extras.add(new ScriptActionCategoryExtra(new ItemStack(Items.LAPIS_BLOCK).setCustomName(Text.literal("Function Definition").setStyle(Style.EMPTY.withItalic(false))), (a) -> new ScriptFunction("")));

        HashMap<String, ScriptFunction> funcs = script.getFunctions();

        List<String> funcNames = new ArrayList<>(funcs.keySet().stream().toList());

        funcNames.sort(String::compareTo);

        for(String funcName : funcNames) {
            ScriptFunction func = funcs.get(funcName);

            ItemStack icon = new ItemStack(Items.LAPIS_LAZULI);
            icon.setCustomName(Text.literal(func.getFunctionName()).setStyle(Style.EMPTY.withItalic(false)));
            extras.add(new ScriptActionCategoryExtra(icon, (Void) -> new ScriptCallFunction(funcName, new ArrayList<>())));
        }

        return extras;
    }),

    VISUALS("Visuals", Items.ENDER_EYE),
    ACTIONS("Actions", Items.PLAYER_HEAD),
    MISC("Misc", Items.COMPASS, List.of(
            new ScriptActionCategoryExtra(new ItemStack(Items.MAP).setCustomName(Text.literal("Comment").setStyle(Style.EMPTY.withItalic(false))), (Void) -> new ScriptComment(""))
    )),
    VARIABLES("Variables", Items.IRON_INGOT),
    NUMBERS("Numbers", Items.SLIME_BALL),
    LISTS("Lists", Items.BOOKSHELF),
    TEXTS("Texts", Items.BOOK),
    DICTIONARIES("Dictionaries", Items.ENDER_CHEST),

    MENUS("Menus", Items.PAINTING),
    ;

    private final ItemStack icon;

    private Function<Script,List<ScriptActionCategoryExtra>> extras;

    ScriptActionCategory(String name, Item icon) {
        this.icon = new ItemStack(icon);
        this.icon.setCustomName(Text.literal(name).fillStyle(Style.EMPTY.withItalic(false)));
        this.extras = (script) -> new ArrayList<>();
    }

    ScriptActionCategory(String name, Item icon, List<ScriptActionCategoryExtra> extras) {
        this.icon = new ItemStack(icon);
        this.icon.setCustomName(Text.literal(name).fillStyle(Style.EMPTY.withItalic(false)));
        this.extras = (script) -> extras;
    }

    ScriptActionCategory(String name, Item icon, Function<Script,List<ScriptActionCategoryExtra>> extras) {
        this.icon = new ItemStack(icon);
        this.icon.setCustomName(Text.literal(name).fillStyle(Style.EMPTY.withItalic(false)));
        this.extras = extras;
    }

    public ItemStack getIcon() {
        return icon;
    }
    public List<ScriptActionCategoryExtra> getExtras(Script script)
    {
        return extras.apply(script);
    }
}
