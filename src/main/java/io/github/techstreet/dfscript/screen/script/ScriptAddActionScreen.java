package io.github.techstreet.dfscript.screen.script;

import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.screen.CScreen;
import io.github.techstreet.dfscript.screen.widget.CItem;
import io.github.techstreet.dfscript.script.Script;
import io.github.techstreet.dfscript.script.action.ScriptAction;
import io.github.techstreet.dfscript.script.action.ScriptActionCategory;
import io.github.techstreet.dfscript.script.action.ScriptActionCategoryExtra;
import io.github.techstreet.dfscript.script.action.ScriptActionType;
import io.github.techstreet.dfscript.script.event.ScriptEvent;
import io.github.techstreet.dfscript.script.event.ScriptEventType;
import java.util.ArrayList;
import java.util.List;

public class ScriptAddActionScreen extends CScreen {

    private final Script script;
    private final int insertIndex;

    public ScriptAddActionScreen(Script script, int insertIndex, ScriptActionCategory category) {
        super(size(category, script), size(category, script));
        int size = size(category, script);
        this.script = script;
        this.insertIndex = insertIndex;

        int x = 3;
        int y = 3;

        if (category == null) {
            for (ScriptEventType type : ScriptEventType.values()) {
                CItem item = new CItem(x, y, type.getIcon());
                item.setClickListener((btn) -> {
                    ScriptEvent event = new ScriptEvent(type);
                    script.getParts().add(insertIndex, event);
                    DFScript.MC.setScreen(new ScriptEditScreen(script));
                });
                widgets.add(item);
                x += 10;
                if (x >= size - 10) {
                    x = 3;
                    y += 10;
                }
            }
        }

        if (category != null)
        {
            for(ScriptActionCategoryExtra extra : category.getExtras(script)) {
                CItem item = new CItem(x, y, extra.getIcon());
                item.setClickListener((btn) -> {
                    script.getParts().add(insertIndex, extra.getPart());
                    DFScript.MC.setScreen(new ScriptEditScreen(script));
                });
                widgets.add(item);
                x += 10;
                if (x >= size-10) {
                    x = 3;
                    y += 10;
                }
            }
        }

        for (ScriptActionType type : ScriptActionType.values()) {
            if (type.getCategory() != category) continue;
            if (type.isDeprecated()) continue;

            CItem item = new CItem(x, y, type.getIcon());
            item.setClickListener((btn) -> {
                ScriptAction action = new ScriptAction(type, new ArrayList<>());
                script.getParts().add(insertIndex, action);
                if (action.getType().hasChildren()) {
                    script.getParts().add(insertIndex + 1, new ScriptAction(ScriptActionType.CLOSE_BRACKET, List.of()));
                }
                DFScript.MC.setScreen(new ScriptEditScreen(script));
            });
            widgets.add(item);
            x += 10;
            if (x >= size-10) {
                x = 3;
                y += 10;
            }
        }
    }

    private static int size(ScriptActionCategory category, Script script) {
        int amount = 0;
        if (category == null) {
            amount = ScriptEventType.values().length;
        } else {
            amount += category.getExtras(script).size();
            for (ScriptActionType type : ScriptActionType.values()) {
                if (type.getCategory() == category) {
                    amount++;
                }
            }
        }
        return (int) (Math.ceil(Math.sqrt(amount))*10)+4;
    }

    @Override
    public void close() {
        DFScript.MC.setScreen(new ScriptActionCategoryScreen(script, insertIndex));
    }
}
