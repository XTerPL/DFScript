package io.github.techstreet.dfscript.script.render;

import io.github.techstreet.dfscript.screen.widget.CPanel;
import io.github.techstreet.dfscript.screen.widget.CScrollPanel;
import io.github.techstreet.dfscript.screen.widget.CWidget;
import io.github.techstreet.dfscript.screen.widget.CWidgetContainer;
import io.github.techstreet.dfscript.script.Script;
import io.github.techstreet.dfscript.script.event.ScriptHeader;
import net.minecraft.client.gui.DrawContext;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ScriptPartRender {
    List<ScriptPartRenderElement> elements = new ArrayList<>();
    List<ScriptButtonPos> buttonPos = new ArrayList<>();

    public ScriptPartRender() {

    }

    public ScriptPartRender addElement(ScriptPartRenderElement element) {
        elements.add(element);
        return this;
    }

    public List<ScriptButtonPos> getButtonPositions() {
        return buttonPos;
    }

    public int create(CWidgetContainer panel, int x, int y, Script script, ScriptHeader header) {
        buttonPos.clear();
        for (ScriptPartRenderElement element : elements) {
            element.render(panel, x, y, script, header);
            if(element.canGenerateButton()) {
                buttonPos.add(element.getButtonPos(panel, x, y, script, header));
            }
            y += element.getHeight(script);
        }
        return y;
    }

    public static void createIndent(CPanel panel, int indent, int y, int height)
    {
        for (int i = 0; i < indent; i ++) {
            int xpos = 8 + i*5;
            int ypos = y;
            panel.add(new CWidget() {
                @Override
                public void render(DrawContext context, int mouseX, int mouseY, float tickDelta) {
                    context.fill(xpos, ypos, xpos + 1, ypos + height, 0xFF333333);
                }

                @Override
                public Rectangle getBounds() {
                    return new Rectangle(0, 0, 0, 0);
                }
            });
        }
    }

    public int getHeight(Script script) {
        int height = 0;
        for (ScriptPartRenderElement element : elements) {
            height += element.getHeight(script);
        }
        return height;
    }

    public record ScriptButtonPos(int x, int y, int width, int height) {
        public int getX() {
            return x;
        }

        public int getWidth() {
            return width;
        }

        public int getY() {
            return y;
        }

        public int getHeight() {
            return height;
        }
    }
}