package io.github.techstreet.dfscript.screen.script;

import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.screen.CScreen;
import io.github.techstreet.dfscript.screen.misc.ItemMaterialSelectionScreen;
import io.github.techstreet.dfscript.screen.widget.CItem;
import io.github.techstreet.dfscript.screen.widget.CTextField;
import io.github.techstreet.dfscript.script.Script;
import io.github.techstreet.dfscript.script.ScriptPart;
import io.github.techstreet.dfscript.script.function.ScriptFunction;

import java.util.List;
import java.util.Objects;

public class ScriptEditFunctionScreen extends CScreen {
    private final Script script;
    private final ScriptFunction function;

    CItem functionIcon;

    public ScriptEditFunctionScreen(Script script, ScriptFunction function) {
        super(90, 100);
        this.script = script;
        this.function = function;

        CTextField nameField = new CTextField(function.getFunctionName(), 15, 5, 70, 20, true);
        nameField.setChangedListener(() -> {
            for (ScriptPart part : script.getParts()) {
                if(part instanceof ScriptFunction f) {
                    if(function != f) {
                        if(Objects.equals(nameField.getText(), f.getFunctionName())) {
                            nameField.textColor = 0xFF0000;
                            return;
                        }
                    }
                }
            }

            if(Objects.equals(nameField.getText(), ""))
            {
                return;
            }

            script.replaceFunction(function.getFunctionName(), nameField.getText());

            nameField.textColor = 0xFFFFFF;
            function.setFunctionName(nameField.getText());
        });

        widgets.add(nameField);

        refresh();
    }

    public void refresh() {
        widgets.remove(functionIcon);

        functionIcon = new CItem(5,5, function.getFullIcon());
        functionIcon.setClickListener((mouse) -> {
            DFScript.MC.setScreen(new ItemMaterialSelectionScreen(
                    (mat) -> {
                        function.setIcon(mat);
                        DFScript.MC.setScreen(new ScriptEditFunctionScreen(script, function));
                    },
                    function.getIcon()
            ));
        });

        widgets.add(functionIcon);
    }

    @Override
    public void close() {
        DFScript.MC.setScreen(new ScriptEditScreen(script));
    }
}
