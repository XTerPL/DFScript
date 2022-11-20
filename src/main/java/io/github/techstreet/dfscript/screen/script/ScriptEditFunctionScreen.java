package io.github.techstreet.dfscript.screen.script;

import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.screen.CScreen;
import io.github.techstreet.dfscript.screen.widget.CText;
import io.github.techstreet.dfscript.screen.widget.CTextField;
import io.github.techstreet.dfscript.script.Script;
import io.github.techstreet.dfscript.script.ScriptPart;
import io.github.techstreet.dfscript.script.function.ScriptFunction;

import java.util.Objects;

public class ScriptEditFunctionScreen extends CScreen {
    private final Script script;
    private final ScriptFunction function;

    public ScriptEditFunctionScreen(Script script, ScriptFunction function) {
        super(90, 100);
        this.script = script;
        this.function = function;

        CTextField nameField = new CTextField(function.getFunctionName(), 5, 5, 80, 20, true);
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
    }

    @Override
    public void close() {
        DFScript.MC.setScreen(new ScriptEditScreen(script));
    }
}
