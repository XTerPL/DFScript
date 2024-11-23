package io.github.techstreet.dfscript.screen.widget;

import java.awt.Rectangle;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

public class CImage implements CWidget {

    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final String img;

    public CImage(int x, int y, int width, int height, String img) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.img = img;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float tickDelta) {
        context.drawGuiTexture(RenderLayer::getGuiTextured, Identifier.of(img), x, y, width*2, height*2);
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}
