package io.github.techstreet.dfscript.screen.script;

import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.screen.CReloadableScreen;
import io.github.techstreet.dfscript.screen.CScreen;
import io.github.techstreet.dfscript.screen.ContextMenuButton;
import io.github.techstreet.dfscript.screen.widget.*;
import io.github.techstreet.dfscript.script.Script;
import io.github.techstreet.dfscript.script.ScriptManager;

import io.github.techstreet.dfscript.script.event.ScriptFunction;
import io.github.techstreet.dfscript.script.event.ScriptHeader;
import io.github.techstreet.dfscript.script.render.ScriptPartRender;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
public class ScriptEditScreen extends CReloadableScreen {
    private final Identifier identifier_main = new Identifier(DFScript.MOD_ID + ":wrench.png");

    private final Script script;
    private static double offsetX = 0;
    private static double offsetY = 0;

    public final static int width = 250;
    private CDragPanel panel;
    private final List<CWidget> contextMenu = new ArrayList<>();

    public ScriptEditScreen(Script script) {
        super(width, 100);
        this.script = script;

        reload();
    }

    public void reload()
    {
        clearContextMenu();
        widgets.clear();

        if(panel != null)
        {
            offsetX = panel.getOffsetCenterX();
            offsetY = panel.getOffsetCenterY();
        }
        else
        {
            panel = new CDragPanel(0, 3, 120, 94);
            widgets.add(panel);
        }

        panel.setOffset(offsetX, offsetY);

        reloadDragPanel();

        int y = 0;


        CText name = new CText(5,y+2,Text.literal(script.getName()),0,1,false,false);
        panel.add(name);

        CButton settings = new CTexturedButton(120-8, y, 8, 8, DFScript.MOD_ID + ":settings.png", DFScript.MOD_ID + ":settings_highlight.png", () -> {
            DFScript.MC.setScreen(new ScriptSettingsScreen(this.script, true));
        });
        panel.add(settings);
    }

    private void reloadDragPanel() {
        panel.clear();

        int index = 0;
        for(ScriptHeader header : script.getHeaders()) {
            ScriptPartRender render = new ScriptPartRender();
            header.create(render, script);
            render.create(panel, header.getX(), header.getY(), script, header);
            int currentIndex = index;

            for (var buttonPos : render.getButtonPositions()) {
                panel.add(new CButton(buttonPos.getX(), buttonPos.getY(), buttonPos.getWidth(), buttonPos.getHeight(), "",() -> {}) {
                    @Override
                    public void render(DrawContext context, int mouseX, int mouseY, float tickDelta) {
                        Rectangle b = getBounds();
                        int color = 0;
                        boolean drawFill = false;
                        if (b.contains(mouseX, mouseY)) {
                            drawFill = true;
                            color = 0x33000000;
                        }

                        if(drawFill) {
                            for(var renderButtonPos : render.getButtonPositions()) {
                                context.fill(renderButtonPos.getX(), renderButtonPos.getY(), renderButtonPos.getX() + renderButtonPos.getWidth(), renderButtonPos.getY() + renderButtonPos.getHeight(), color);
                            }
                        }
                    }

                    @Override
                    public boolean mouseClicked(double x, double y, int button) {
                        if (getBounds().contains(x, y)) {
                            DFScript.MC.getSoundManager().play(PositionedSoundInstance.ambient(SoundEvents.UI_BUTTON_CLICK.value(), 1f,1f));

                            if (button != 0) {
                                CButton delete = new CButton((int) x, (int) y+16, 40, 8, "Delete", () -> {
                                    script.getHeaders().remove(currentIndex);
                                    if(header instanceof ScriptFunction f) {
                                        script.removeFunction(f.getName());
                                    }
                                    reload();
                                });
                                DFScript.MC.send(() -> {
                                    widgets.add(delete);
                                    contextMenu.add(delete);
                                });
                            }
                            else {
                                if(header instanceof ScriptFunction f) {
                                    DFScript.MC.setScreen(new ScriptEditFunctionScreen(f, script));
                                }
                            }
                            return true;
                        }
                        return false;
                    }
                });
            }
            index++;
        }
    }

    @Override
    public void close() {
        offsetX = panel.getOffsetCenterX();
        offsetY = panel.getOffsetCenterY();
        ScriptManager.getInstance().saveScript(script);
        DFScript.MC.setScreen(new ScriptListScreen(true));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean b = super.mouseClicked(mouseX, mouseY, button);
        clearContextMenu();
        return b;
    }

    private void clearContextMenu() {
        for (CWidget w : contextMenu) {
            widgets.remove(w);
        }
        contextMenu.clear();
    }

    public void contextMenu(int x, int y, List<ContextMenuButton> contextMenuButtons) {
        clearContextMenu();

        int maxWidth = 0;

        for(ContextMenuButton w : contextMenuButtons)
        {
            TextRenderer t = DFScript.MC.textRenderer;
            int width = t.getWidth(w.getName())/2 + 4;

            if(width > maxWidth) maxWidth = width;
        }

        for(ContextMenuButton w : contextMenuButtons)
        {
            CButton button = new CButton(x, y, maxWidth, 8, w.getName(), w.getOnClick());
            y += 8;

            widgets.add(button);
            contextMenu.add(button);
        }
    }
}
