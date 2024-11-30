package io.github.techstreet.dfscript.screen.widget;

import io.github.techstreet.dfscript.DFScript;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Vector4f;

import java.awt.*;
import java.util.List;

public class CSelectField implements CWidget {
    final int x, y, width, height;

    boolean selected;
    String selectedOption;
    List<String> options;
    Runnable changedListener;
    int scroll = 0;

    public CSelectField(int x, int y, int width, int height, String selectedOption, List<String> options) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.selectedOption = selectedOption;
        this.options = options;
    }

    public CSelectField(int x, int y, int width, int height, String selectedOption, String... options) {
        this(x, y, width, height, selectedOption, List.of(options));
    }

    public void setChangedListener(Runnable r) {
        changedListener = r;
    }

    public String getSelectedOptionName() {
        return selectedOption;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float tickDelta) {
        MatrixStack stack = context.getMatrices();
        stack.push();
        stack.translate(x, y, 0);

        stack.push();
        stack.scale(0.5f, 0.5f, 0.5f);

        Rectangle rect = new Rectangle(x, y, width, height);

        String image = "widget/button";
        if (rect.contains(mouseX, mouseY)) {
            image = "widget/button_highlighted";
        }

        context.drawGuiTexture(RenderLayer::getGuiTextured, Identifier.of(image), 0, 0, width*2, height*2);

        stack.pop();

        TextRenderer f = DFScript.MC.textRenderer;

        stack.translate(rect.width / 2f, rect.height / 2f, 0);
        stack.scale(0.5f, 0.5f, 0.5f);
        stack.translate(-f.getWidth(selectedOption) / 2f, -f.fontHeight / 2f, 0);

        context.drawText(f, selectedOption, 0, 0, 0xFFFFFF, true);

        stack.pop();
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        if (button == 0) {

            if(selected) {
                int yy = this.y + this.height - scroll + 1;

                TextRenderer f = DFScript.MC.textRenderer;
                int fontHeight = (f.fontHeight + 3) / 2;
                int visibleCount = options.size();
                if(visibleCount > 5) {
                    visibleCount = 5;
                }

                if(new Rectangle(this.x, this.y + this.height, this.width, fontHeight*visibleCount).contains(x, y)) {
                    for(String option : options) {
                        if(new Rectangle(this.x, yy, this.width, fontHeight).contains(x, y)) {
                            DFScript.MC.getSoundManager().play(PositionedSoundInstance.ambient(SoundEvents.UI_BUTTON_CLICK.value(), 1f,1f));
                            selectedOption = option;
                            selected = false;
                            changedListener.run();
                            return true;
                        }
                        yy += fontHeight;
                    }
                }
            }

            if(new Rectangle(this.x, this.y, this.width, this.height).contains(x, y)) {
                DFScript.MC.getSoundManager().play(PositionedSoundInstance.ambient(SoundEvents.UI_BUTTON_CLICK.value(), 1f,1f));
                this.selected = !this.selected;
            }
            else {
                this.selected = false;
            }
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double vertical, double horizontal) {
        if(selected) {
            TextRenderer f = DFScript.MC.textRenderer;
            int visibleCount = options.size();
            if (visibleCount > 5) {
                visibleCount = 5;
            }

            scroll += (int) (vertical * -5);

            if (scroll < 0) {
                scroll = 0;
            }

            int maxScroll = (f.fontHeight + 3) / 2 * (options.size() - visibleCount);
            if (scroll > maxScroll) {
                scroll = maxScroll;
            }

            return true;
        }

        return false;
    }

    @Override
    public void renderOverlay(DrawContext context, int mouseX, int mouseY, float tickDelta) {
        if(!selected) return;

        MatrixStack stack = context.getMatrices();
        stack.push();
        stack.translate(x, y, 0);
        stack.translate(0, height, 0);

        stack.scale(0.5f, 0.5f, 0.5f);

        String image = "widget/button_highlighted";

        TextRenderer f = DFScript.MC.textRenderer;
        int visibleCount = options.size();
        if(visibleCount > 5) {
            visibleCount = 5;
        }

        int fontHeight = f.fontHeight + 3;

        context.drawGuiTexture(RenderLayer::getGuiTextured, Identifier.of(image), 0, 0, width*2, fontHeight * visibleCount + 4);

        float xPos = stack.peek().getPositionMatrix().m30();
        float yPos = stack.peek().getPositionMatrix().m31();

        Vector4f begin = new Vector4f(xPos, yPos + 2, 1, 1);
        Vector4f end = new Vector4f((xPos + (width * 2)), (yPos + (fontHeight * visibleCount) + 2), 1, 1);

        context.enableScissor(
                (int) begin.x(),
                (int) begin.y(),
                (int) end.x(),
                (int) end.y()
        );

        stack.translate(0, 2, 0);

        stack.translate(0, -scroll * 2, 0);

        int index = 0;
        for(String option : options) {
            stack.push();
            stack.translate(-f.getWidth(option) / 2f, -f.fontHeight / 2F, 0);
            stack.translate(width, fontHeight / 2F, 0);
            context.drawText(f, option, 0, 0, 0xFFFFFF, true);
            stack.pop();
            stack.push();
            stack.scale(2F, 2F, 2F);

            Rectangle entryBounds = new Rectangle(x, y + height - scroll + 1 + fontHeight/2 * index, width, fontHeight/2);
            if(entryBounds.contains(mouseX, mouseY)) {
                context.fill(0, 0, width, fontHeight/2, 0x33000000);
            }

            stack.pop();
            stack.translate(0, fontHeight, 0);
            index++;
        }

        context.disableScissor();

        stack.pop();
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}