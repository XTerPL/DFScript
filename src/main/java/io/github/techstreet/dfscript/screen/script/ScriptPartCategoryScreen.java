package io.github.techstreet.dfscript.screen.script;

import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.screen.CScreen;
import io.github.techstreet.dfscript.screen.widget.CItem;
import io.github.techstreet.dfscript.script.Script;
import io.github.techstreet.dfscript.script.ScriptSnippet;
import io.github.techstreet.dfscript.script.action.ScriptActionCategory;
import io.github.techstreet.dfscript.script.action.ScriptActionType;
import io.github.techstreet.dfscript.script.conditions.ScriptConditionType;
import io.github.techstreet.dfscript.script.repetitions.ScriptRepetitionType;

public class ScriptPartCategoryScreen extends CScreen {

    private static int size() {
        int amount = 0;
        for (ScriptActionCategory child : ScriptActionCategory.values()) {
            if (child.getParent() == null) {
                amount++;
            }
        }
        return (int) (Math.ceil(Math.sqrt(amount))*10)+4;
    }

    private final Script script;

    public ScriptPartCategoryScreen(Script script, ScriptSnippet snippet, int insertIndex) {
        super(size(), size());
        int size = size();
        this.script = script;

        int x = 3;
        int y = 3;

        for (ScriptActionCategory category : ScriptActionCategory.values()) {
            if(category.getParent() != null) continue;

            CItem actionItem = new CItem(x, y, category.getIcon());
            widgets.add(actionItem);

            actionItem.setClickListener(btn -> DFScript.MC.setScreen(new ScriptAddPartScreen(script, snippet, insertIndex, category)));

            x += 10;
            if (x >= size - 10) {
                x = 3;
                y += 10;
            }
        }
    }

    @Override
    public void close() {
        DFScript.MC.setScreen(new ScriptEditScreen(script));
    }
}
