package io.github.techstreet.dfscript.script.action;

import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.screen.script.ScriptEditScreen;
import io.github.techstreet.dfscript.script.ScriptParametrizedPart;
import io.github.techstreet.dfscript.script.ScriptPart;
import net.minecraft.item.ItemStack;

import java.util.Objects;
import java.util.function.Supplier;

public class ScriptActionCategoryExtraPartCreator extends ScriptActionCategoryExtra {
    private final ItemStack icon;
    private final Supplier<ScriptPart> createPartFunction;

    public ScriptActionCategoryExtraPartCreator(ItemStack icon, Supplier<ScriptPart> createPartFunction) {
        super(icon, (sc, sn, ii) -> {
            ScriptPart part = createPartFunction.get();

            if(part instanceof ScriptParametrizedPart ppart) {
                ppart.updateTags();
            }

            sn.add(ii, part);
            DFScript.MC.setScreen(new ScriptEditScreen(sc));
        });
        this.icon = icon;
        this.createPartFunction = createPartFunction;
    }

    public ItemStack getIcon() {
        return icon;
    }

    public ScriptPart getPart() {
        ScriptPart part = createPartFunction.get();

        if(part instanceof ScriptParametrizedPart ppart) {
            ppart.updateTags();
        }

        return part;
    }

    public ItemStack icon() {
        return icon;
    }

    public Supplier<ScriptPart> createPartFunction() {
        return createPartFunction;
    }
}
