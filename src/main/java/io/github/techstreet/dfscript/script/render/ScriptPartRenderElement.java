package io.github.techstreet.dfscript.script.render;

import io.github.techstreet.dfscript.screen.widget.CWidgetContainer;
import io.github.techstreet.dfscript.script.Script;
import io.github.techstreet.dfscript.script.event.ScriptHeader;

public interface ScriptPartRenderElement {
    void render(CWidgetContainer panel, int x, int y, Script script, ScriptHeader header);

    ScriptPartRender.ScriptButtonPos getButtonPos(CWidgetContainer panel, int x, int y, Script script, ScriptHeader header);

    default boolean canGenerateButton() {
        return true;
    }

    int getWidth();

    int getHeight(Script script);
}
