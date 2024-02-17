package io.github.techstreet.dfscript.screen.widget;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;

public class COffset implements CWidgetContainer, CWidget {
    protected final int x, y;

    public COffset(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float tickDelta) {
        MatrixStack stack = context.getMatrices();
        stack.push();

        stack.translate(x, y, 0);
        mouseX -= x;
        mouseY -= y;

        for (CWidget child : getAll()) {
            child.render(context, mouseX, mouseY, tickDelta);
        }

        stack.pop();
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        x -= this.x;
        y -= this.y;

        for (int i = getAll().length - 1; i >= 0; i--) {
            if (getAll()[i].mouseClicked(x, y, button)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double x, double y, int button) {
        x -= this.x;
        y -= this.y;

        for (int i = getAll().length - 1; i >= 0; i--) {
            if (getAll()[i].mouseReleased(x, y, button)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double x, double y, int button, double dx, double dy) {
        x -= this.x;
        y -= this.y;

        for (int i = getAll().length - 1; i >= 0; i--) {
            if (getAll()[i].mouseDragged(x, y, button, dx, dy)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void charTyped(char ch, int keyCode) {
        for (CWidget child : getAll()) {
            child.charTyped(ch, keyCode);
        }
    }

    @Override
    public void keyPressed(int keyCode, int scanCode, int modifiers) {
        for (CWidget child : getAll()) {
            child.keyPressed(keyCode, scanCode, modifiers);
        }
    }

    @Override
    public void keyReleased(int keyCode, int scanCode, int modifiers) {
        for (CWidget child : getAll()) {
            child.keyReleased(keyCode, scanCode, modifiers);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double vertical, double horizontal) {
        mouseX -= this.x;
        mouseY -= this.y;

        for (CWidget child : getAll()) {
            if(child.mouseScrolled(mouseX, mouseY, vertical, horizontal)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void renderOverlay(DrawContext context, int mouseX, int mouseY, float tickDelta) {
        MatrixStack stack = context.getMatrices();
        stack.push();
        stack.translate(x, y, 0);
        mouseX -= x;
        mouseY -= y;
        for (CWidget child : getAll()) {
            child.renderOverlay(context, mouseX, mouseY, tickDelta);
        }
        stack.pop();
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, 0, 0);
    }

    @Override
    public boolean enableClosingOnEsc() {
        for(CWidget widget : getAll()) {
            if(!widget.enableClosingOnEsc())
            {
                return false;
            }
        }

        return CWidget.super.enableClosingOnEsc();
    }
}
