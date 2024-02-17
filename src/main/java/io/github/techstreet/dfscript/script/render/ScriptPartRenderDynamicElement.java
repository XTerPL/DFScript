package io.github.techstreet.dfscript.script.render;

import io.github.techstreet.dfscript.screen.widget.*;
import io.github.techstreet.dfscript.script.Script;
import io.github.techstreet.dfscript.script.event.ScriptHeader;

import java.util.function.Consumer;
import java.util.function.Function;

public class ScriptPartRenderDynamicElement implements ScriptPartRenderElement {
    private Consumer<ScriptPartRenderArgs> onRender;
    private Function<ScriptPartRenderArgs, ScriptPartRender.ScriptButtonPos> getButtonPos;

    private int width, height;

    public ScriptPartRenderDynamicElement(Consumer<ScriptPartRenderArgs> onRender, Function<ScriptPartRenderArgs, ScriptPartRender.ScriptButtonPos> getButtonPos, int width, int height) {
        this.onRender = onRender;
        this.getButtonPos = getButtonPos;
        this.width = width;
        this.height = height;
    }

    @Override
    public void render(CWidgetContainer panel, int x, int y, Script script, ScriptHeader header) {
        onRender.accept(new ScriptPartRenderArgs(panel, x, y, script));
    }

    @Override
    public ScriptPartRender.ScriptButtonPos getButtonPos(CWidgetContainer panel, int x, int y, Script script, ScriptHeader header) {
        return getButtonPos.apply(new ScriptPartRenderArgs(panel, x, y, script));
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight(Script script) {
        return height;
    }

    public record ScriptPartRenderArgs(CWidgetContainer container, int x, int y, Script script) {
        public int y() {
            return y;
        }

        public int x() {
            return x;
        }

        public CWidgetContainer container() {
            return container;
        }

        public Script script() {
            return script;
        }
    }
}