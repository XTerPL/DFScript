package io.github.techstreet.dfscript.util;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.joml.Matrix4f;

public class RenderUtil {
    public static void renderGuiItem(DrawContext context, ItemStack item) {
        MatrixStack stack = context.getMatrices();
        stack.push();
        stack.scale(0.5F,0.5F,1F);
        context.drawItem(item, 0, 0);
        stack.pop();
    }

    public static void sendToaster(String title, String description, SystemToast.Type type) {
        sendToaster(Text.literal(title), Text.literal(description), type);
    }

    public static void sendToaster(MutableText title, MutableText description, SystemToast.Type type) {
        MinecraftClient.getInstance().getToastManager().add(new SystemToast(type, title, description));
    }

    static int renderSteps = 100;

    public static void renderLine(MatrixStack stack, float x1, float y1, float x2, float y2, int color, float size) {
        float stepX = (x2-x1)/renderSteps;
        float stepY = (y2-y1)/renderSteps;

        for(int i = 0; i <= renderSteps; i++) {
            fill(stack, (x1-(size/2)), (y1-(size/2)), (x1+(size/2)), (y1+(size/2)), color);

            x1 += stepX;
            y1 += stepY;
        }
    }

    // these functions exist because fabric api dumb and used an integer for positions in the DrawableHelper class...
    public static void fill(DrawContext context, float x1, float y1, float x2, float y2, int color) {
        fill(context.getMatrices(), x1, y1, x2, y2, color);
    }

    public static void fill(MatrixStack stack, float x1, float y1, float x2, float y2, int color) {
        fill(stack.peek().getPositionMatrix(), x1, y1, x2, y2, color);
    }
    public static void fill(Matrix4f matrix, float x1, float y1, float x2, float y2, int color) {
        float i;
        if (x1 < x2) {
            i = x1;
            x1 = x2;
            x2 = i;
        }
        if (y1 < y2) {
            i = y1;
            y1 = y2;
            y2 = i;
        }
        float f = (float)(color >> 24 & 0xFF) / 255.0f;
        float g = (float)(color >> 16 & 0xFF) / 255.0f;
        float h = (float)(color >> 8 & 0xFF) / 255.0f;
        float j = (float)(color & 0xFF) / 255.0f;
        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        RenderSystem.enableBlend();
//        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
        bufferBuilder.vertex(matrix, x1, y2, 0.0f).color(g, h, j, f);
        bufferBuilder.vertex(matrix, x2, y2, 0.0f).color(g, h, j, f);
        bufferBuilder.vertex(matrix, x2, y1, 0.0f).color(g, h, j, f);
        bufferBuilder.vertex(matrix, x1, y1, 0.0f).color(g, h, j, f);
//        BufferRenderer.drawWithShader(bufferBuilder.end());
        BufferRenderer.draw(bufferBuilder.end());
//        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }
}