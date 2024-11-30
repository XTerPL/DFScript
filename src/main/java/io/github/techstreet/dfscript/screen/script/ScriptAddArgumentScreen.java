package io.github.techstreet.dfscript.screen.script;

import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.screen.CScreen;
import io.github.techstreet.dfscript.screen.widget.CItem;
import io.github.techstreet.dfscript.screen.widget.CTextField;
import io.github.techstreet.dfscript.script.Script;
import io.github.techstreet.dfscript.script.ScriptParametrizedPart;
import io.github.techstreet.dfscript.script.argument.*;
import io.github.techstreet.dfscript.script.event.ScriptFunction;
import io.github.techstreet.dfscript.script.event.ScriptHeader;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.function.Consumer;

public class ScriptAddArgumentScreen extends CScreen {

    private final Script script;

    private final ScriptHeader header;
    private final ScriptParametrizedPart action;

    public ScriptAddArgumentScreen(Script script, ScriptParametrizedPart action, Consumer<ScriptArgument> consumer, ScriptHeader header) {
        this(script,action,consumer, header,null);
    }
    public ScriptAddArgumentScreen(Script script, ScriptParametrizedPart action, Consumer<ScriptArgument> consumer, ScriptHeader header, String overwrite) {
        super(100, 50);
        this.script = script;
        this.action = action;
        this.header = header;

        CTextField input = new CTextField("Input", 2, 2, 96, 35, true);
        if(overwrite != null) input.setText(overwrite);

        ItemStack textIcon = new ItemStack(Items.BOOK);
        textIcon.set(DataComponentTypes.CUSTOM_NAME, Text.literal("Text")
            .fillStyle(Style.EMPTY.withItalic(false)));

        ItemStack numberIcon = new ItemStack(Items.SLIME_BALL);
        numberIcon.set(DataComponentTypes.CUSTOM_NAME, Text.literal("Number")
            .fillStyle(Style.EMPTY.withItalic(false)));

        ItemStack variableIcon = new ItemStack(Items.MAGMA_CREAM);
        variableIcon.set(DataComponentTypes.CUSTOM_NAME, Text.literal("Variable")
            .fillStyle(Style.EMPTY.withItalic(false)));

        ItemStack clientValueIcon = new ItemStack(Items.NAME_TAG);
        clientValueIcon.set(DataComponentTypes.CUSTOM_NAME, Text.literal("Client Value")
            .fillStyle(Style.EMPTY.withItalic(false)));

        ItemStack configValueIcon = new ItemStack(Items.INK_SAC);
        configValueIcon.set(DataComponentTypes.CUSTOM_NAME, Text.literal("Config Value")
            .fillStyle(Style.EMPTY.withItalic(false)));

        ItemStack functionArgumentIcon = new ItemStack(Items.BLUE_DYE);
        functionArgumentIcon.set(DataComponentTypes.CUSTOM_NAME, Text.literal("Function Argument")
                .fillStyle(Style.EMPTY.withItalic(false)));

        ItemStack trueIcon = new ItemStack(Items.LIME_DYE);
        trueIcon.set(DataComponentTypes.CUSTOM_NAME, Text.literal("True")
                .fillStyle(Style.EMPTY.withItalic(false)));

        ItemStack falseIcon = new ItemStack(Items.RED_DYE);
        falseIcon.set(DataComponentTypes.CUSTOM_NAME, Text.literal("False")
                .fillStyle(Style.EMPTY.withItalic(false)));

        CItem addNumber = new CItem(2, 40, numberIcon);
        CItem addText = new CItem(12, 40, textIcon);
        CItem addTrue = new CItem(22, 40, trueIcon);
        CItem addFalse = new CItem(32, 40, falseIcon);
        CItem addVariable = new CItem(42, 40, variableIcon);
        CItem addClientValue = new CItem(52, 40, clientValueIcon);
        CItem addConfigValue = new CItem(62, 40, configValueIcon);
        CItem addFunctionArgument = new CItem(72, 40, functionArgumentIcon);

        input.setChangedListener(() -> input.textColor = 0xFFFFFF);

        addText.setClickListener((btn) -> {
            consumer.accept(new ScriptTextArgument(input.getText()));
            close();
        });

        addNumber.setClickListener((btn) -> {
            try {
                double number = Double.parseDouble(input.getText());
                consumer.accept(new ScriptNumberArgument(number));
                close();
            } catch (Exception err) {
                input.textColor = 0xFF3333;
            }
        });

        addVariable.setClickListener((btn) -> {
            consumer.accept(new ScriptVariableArgument(input.getText(), ScriptVariableScope.SCRIPT));
            close();
        });

        addClientValue.setClickListener((btn) -> {
            DFScript.MC.setScreen(new ScriptAddClientValueScreen(action, script, consumer, header, overwrite));
        });

        addConfigValue.setClickListener((btn) -> {
            DFScript.MC.setScreen(new ScriptAddConfigValueScreen(action, script, consumer, header, overwrite));
        });

        addFunctionArgument.setClickListener((btn) -> {
            DFScript.MC.setScreen(new ScriptAddFunctionArgValueScreen(action, script, consumer, header, overwrite));
        });

        addTrue.setClickListener((btn) -> {
            consumer.accept(new ScriptBoolArgument(true));
            close();
        });

        addFalse.setClickListener((btn) -> {
            consumer.accept(new ScriptBoolArgument(false));
            close();
        });

        widgets.add(input);
        widgets.add(addNumber);
        widgets.add(addText);
        widgets.add(addTrue);
        widgets.add(addFalse);
        widgets.add(addVariable);
        widgets.add(addClientValue);
        widgets.add(addConfigValue);
        if(header instanceof ScriptFunction) {
            widgets.add(addFunctionArgument);
        }
    }

    @Override
    public void close() {
        DFScript.MC.setScreen(new ScriptEditPartScreen(action, script, header));
    }
}
