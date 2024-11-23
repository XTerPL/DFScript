package io.github.techstreet.dfscript.screen.widget;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class CTexturedButton extends CButton {

    private String texture;

    public CTexturedButton(int x, int y, int width, int height, String texture, Runnable onClick) {
        super(x, y, width, height, 0, "", onClick);
        this.texture = texture;
    }

    public void setTexture(String newTexture) {
        texture = newTexture;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float tickDelta) {
        super.render(context, mouseX, mouseY, tickDelta);

        MatrixStack stack = context.getMatrices();
        stack.push();
        stack.translate(x, y, 0);
        stack.scale(0.5f, 0.5f, 0.5f);

        context.drawGuiTexture(RenderLayer::getGuiTextured, Identifier.of(texture), 0, 0, width*2, height*2);
        stack.pop();
    }
}
