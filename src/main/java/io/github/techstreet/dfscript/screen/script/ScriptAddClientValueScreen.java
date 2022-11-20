package io.github.techstreet.dfscript.screen.script;

import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.screen.CScreen;
import io.github.techstreet.dfscript.screen.widget.CItem;
import io.github.techstreet.dfscript.script.Script;
import io.github.techstreet.dfscript.script.action.ScriptRunnablePart;
import io.github.techstreet.dfscript.script.argument.ScriptClientValueArgument;

public class ScriptAddClientValueScreen extends CScreen {

    private final Script script;
    private final ScriptRunnablePart action;
    private final int insertIndex;
    private static final int WIDTH = 55;

    public ScriptAddClientValueScreen(ScriptRunnablePart action, Script script, int insertIndex) {
        super(WIDTH, 52);
        this.script = script;
        this.action = action;
        this.insertIndex = insertIndex;

        int x = 5;
        int y = 5;
        for (ScriptClientValueArgument arg : ScriptClientValueArgument.values()) {
            CItem item = new CItem(x, y, arg.getIcon());
            item.setClickListener((btn) -> {
                action.getArguments().add(insertIndex, arg);
                DFScript.MC.setScreen(new ScriptEditActionScreen(action, script));
            });
            widgets.add(item);
            x += 10;
            if (x > WIDTH-10) {
                x = 5;
                y += 10;
            }
        }
    }

    @Override
    public void close() {
        DFScript.MC.setScreen(new ScriptAddArgumentScreen(script, action, insertIndex));
    }
}
