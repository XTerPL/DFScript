package io.github.techstreet.dfscript.script.render;

import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.screen.widget.*;
import io.github.techstreet.dfscript.script.Script;
import io.github.techstreet.dfscript.script.event.ScriptHeader;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class ScriptPartRenderIconElement implements ScriptPartRenderElement {
    private String name;
    private ItemStack icon;

    public ScriptPartRenderIconElement(String name, ItemStack icon) {
        this.name = name;
        this.icon = icon;
    }

    @Override
    public void render(CWidgetContainer panel, int x, int y, Script script, ScriptHeader header) {
        panel.add(new CItem(x, y, icon));
        panel.add(new CText(x + 10, y + 2, Text.literal(name)));
    }

    @Override
    public ScriptPartRender.ScriptButtonPos getButtonPos(CWidgetContainer panel, int x, int y, Script script, ScriptHeader header) {
        return new ScriptPartRender.ScriptButtonPos(x, y, getWidth(), getHeight(script));
    }

    @Override
    public int getWidth() {
        TextRenderer t = DFScript.MC.textRenderer;
        return 12+t.getWidth(name);
    }

    @Override
    public int getHeight(Script script) {
        return 10;
    }
}
