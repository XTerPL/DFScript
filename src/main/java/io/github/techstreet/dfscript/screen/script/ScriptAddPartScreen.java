package io.github.techstreet.dfscript.screen.script;

import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.screen.CScreen;
import io.github.techstreet.dfscript.screen.widget.CItem;
import io.github.techstreet.dfscript.script.Script;
import io.github.techstreet.dfscript.script.ScriptSnippet;
import io.github.techstreet.dfscript.script.action.*;
import io.github.techstreet.dfscript.script.conditions.ScriptBranch;
import io.github.techstreet.dfscript.script.conditions.ScriptBuiltinCondition;
import io.github.techstreet.dfscript.script.conditions.ScriptConditionType;
import io.github.techstreet.dfscript.script.repetitions.ScriptBuiltinRepetition;
import io.github.techstreet.dfscript.script.repetitions.ScriptRepetitionType;

import java.util.ArrayList;

public class ScriptAddPartScreen extends CScreen {

    private final Script script;

    private final ScriptSnippet snippet;
    private final int insertIndex;
    private final ScriptActionCategory category;

    public ScriptAddPartScreen(Script script, ScriptSnippet snippet, int insertIndex, ScriptActionCategory category) {
        super(size(category, script), size(category, script));
        this.category = category;
        int size = size(category, script);
        this.script = script;
        this.insertIndex = insertIndex;
        this.snippet = snippet;

        int x = 3;
        int y = 3;

        /*if (category == null) {
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
        }*/

        if (category != null)
        {
            for(ScriptActionCategoryExtra extra : category.getExtras(script)) {
                CItem item = new CItem(x, y, extra.getIcon());
                item.setClickListener((btn) -> extra.click(script, snippet, insertIndex));
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
            if (type.getNotice().isHidden()) continue;

            CItem item = new CItem(x, y, type.getIcon());
            item.setClickListener((btn) -> {
                ScriptAction action = new ScriptBuiltinAction(type, new ArrayList<>(), new ArrayList<>());
                action.updateTags();
                snippet.add(insertIndex, action);
                if (type.getNotice().disablesScript()) script.block();
                DFScript.MC.setScreen(new ScriptEditScreen(script));
            });
            widgets.add(item);
            x += 10;
            if (x >= size-10) {
                x = 3;
                y += 10;
            }
        }

        for (ScriptConditionType type : ScriptConditionType.values()) {
            if (type.getCategory() != category) continue;
            if (type.getNotice("").isHidden()) continue;

            CItem item = new CItem(x, y, type.getIcon("If"));
            item.setClickListener((btn) -> {
                ScriptBranch action = new ScriptBranch(new ArrayList<>(), new ArrayList<>(), new ScriptBuiltinCondition(type));
                action.updateTags();
                snippet.add(insertIndex, action);
                if (type.getNotice("").disablesScript()) script.block();
                DFScript.MC.setScreen(new ScriptEditScreen(script));
            });
            widgets.add(item);
            x += 10;
            if (x >= size-10) {
                x = 3;
                y += 10;
            }
        }

        for (ScriptRepetitionType type : ScriptRepetitionType.values()) {
            if (type.getCategory() != category) continue;
            if (type.getNotice().isHidden()) continue;

            CItem item = new CItem(x, y, type.getIcon());
            item.setClickListener((btn) -> {
                ScriptBuiltinRepetition action = new ScriptBuiltinRepetition(new ArrayList<>(), new ArrayList<>(), type);
                action.updateTags();
                snippet.add(insertIndex, action);
                if (type.getNotice().disablesScript()) script.block();
                DFScript.MC.setScreen(new ScriptEditScreen(script));
            });
            widgets.add(item);
            x += 10;
            if (x >= size-10) {
                x = 3;
                y += 10;
            }
        }

        for (ScriptActionCategory child : ScriptActionCategory.values()) {
            if(child.getParent() != category) continue;

            CItem actionItem = new CItem(x, y, child.getIcon());
            widgets.add(actionItem);

            actionItem.setClickListener(btn -> DFScript.MC.setScreen(new ScriptAddPartScreen(script, snippet, insertIndex, child)));

            x += 10;
            if (x >= size-10) {
                x = 3;
                y += 10;
            }
        }
    }

    private static int size(ScriptActionCategory category, Script script) {
        int amount = 0;
        amount += category.getExtras(script).size();
        for (ScriptActionType type : ScriptActionType.values()) {
            if (type.getCategory() == category) {
                amount++;
            }
        }
        for (ScriptConditionType type : ScriptConditionType.values()) {
            if (type.getCategory() == category) {
                amount++;
            }
        }
        for (ScriptRepetitionType type : ScriptRepetitionType.values()) {
            if (type.getCategory() == category) {
                amount++;
            }
        }
        for (ScriptActionCategory child : ScriptActionCategory.values()) {
            if (child.getParent() == category) {
                amount++;
            }
        }
        return (int) (Math.ceil(Math.sqrt(amount))*10)+4;
    }

    @Override
    public void close() {
        if(category.getParent() == null) {
            DFScript.MC.setScreen(new ScriptPartCategoryScreen(script, snippet, insertIndex));
        }
        else {
            DFScript.MC.setScreen(new ScriptAddPartScreen(script, snippet, insertIndex, category.getParent()));
        }
    }
}
