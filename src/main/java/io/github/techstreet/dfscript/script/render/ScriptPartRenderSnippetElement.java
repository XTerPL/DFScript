package io.github.techstreet.dfscript.script.render;

import io.github.techstreet.dfscript.screen.widget.CWidgetContainer;
import io.github.techstreet.dfscript.script.Script;
import io.github.techstreet.dfscript.script.ScriptSnippet;
import io.github.techstreet.dfscript.script.event.ScriptHeader;

public class ScriptPartRenderSnippetElement implements ScriptPartRenderElement {
    ScriptSnippet snippet;

    public ScriptPartRenderSnippetElement(ScriptSnippet snippet) {
        this.snippet = snippet;
    }

    @Override
    public void render(CWidgetContainer panel, int x, int y, Script script, ScriptHeader header) {
        snippet.create(panel, x + 7, y, script, header);
    }

    @Override
    public ScriptPartRender.ScriptButtonPos getButtonPos(CWidgetContainer panel, int x, int y, Script script, ScriptHeader header) {
        return new ScriptPartRender.ScriptButtonPos(x, y, getWidth(), getHeight(script));
    }

    @Override
    public boolean canGenerateButton() {
        return false;
    }

    @Override
    public int getWidth() {
        return 5;
    }

    @Override
    public int getHeight(Script script) {
        return snippet.getHeight(script);
    }
}
