package io.github.techstreet.dfscript.screen.script;

import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.screen.CReloadableScreen;
import io.github.techstreet.dfscript.screen.ContextMenuButton;
import io.github.techstreet.dfscript.screen.widget.*;
import io.github.techstreet.dfscript.script.Script;
import io.github.techstreet.dfscript.script.ScriptNotice;
import io.github.techstreet.dfscript.script.ScriptNoticeLevel;
import io.github.techstreet.dfscript.script.ScriptParametrizedPart;
import io.github.techstreet.dfscript.script.action.ScriptActionTag;
import io.github.techstreet.dfscript.script.argument.*;
import io.github.techstreet.dfscript.script.event.ScriptHeader;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ScriptEditPartScreen extends CReloadableScreen {
    private final Script script;

    private final ScriptHeader header;
    private final ScriptParametrizedPart action;
    private final CScrollPanel panel;
    private final List<CWidget> contextMenu = new ArrayList<>();

    public ScriptEditPartScreen(ScriptParametrizedPart action, Script script, ScriptHeader header) {
        super(90, 100);
        panel = new CScrollPanel(0, 0, 90, 100);

        widgets.add(panel);

        this.script = script;
        this.action = action;
        this.header = header;

        reload();
    }

    @Override
    public void close() {
        DFScript.MC.setScreen(new ScriptEditScreen(script));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean b = super.mouseClicked(mouseX, mouseY, button);
        clearContextMenu();
        return b;
    }

    private void clearContextMenu() {
        for (CWidget w : contextMenu) {
            panel.remove(w);
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

            panel.add(button);
            contextMenu.add(button);
        }
    }

    @Override
    public void reload() {
        clearContextMenu();
        panel.clear();

        panel.add(new CItem(5, 3, action.putNotices(action.getIcon())));
        panel.add(new CText(15, 5, Text.of(action.getName())));

        int y = 15;
        int index = 0;
        for (ScriptArgument arg : action.getArguments()) {
            ItemStack icon = arg.putNotices(arg.getArgIcon());
            Text countText = arg.getArgIconText();
            String text = arg.getArgText();

            panel.add(new CItem(5, y, icon, countText));
            panel.add(new CText(15, y + 2, Text.literal(text)));

            int currentIndex = index;

            ScriptNotice notice = arg.getNotice();

            panel.add(new CButton(5, y-1, 80, 10, "",() -> {}) {
                @Override
                public void render(DrawContext context, int mouseX, int mouseY, float tickDelta) {
                    Rectangle b = getBounds();
                    int color = notice.getPartColor(b.contains(mouseX, mouseY));
                    context.fill(b.x, b.y, b.x + b.width, b.y + b.height, color);
                }

                @Override
                public boolean mouseClicked(double x, double y, int button) {
                    if (getBounds().contains(x, y)) {
                        DFScript.MC.getSoundManager().play(PositionedSoundInstance.ambient(SoundEvents.UI_BUTTON_CLICK.value(), 1f,1f));

                        if (button == 0) {
                            ScriptArgument argument = action.getArguments().get(currentIndex);
                            String value = argument.getOverwrite();
                            DFScript.MC.setScreen(new ScriptAddArgumentScreen(script, action, (newArg) -> {
                                action.getArguments().set(currentIndex, newArg);
                            }, header, value));
                        }

                        if (button != 0) {
                            List<ContextMenuButton> contextMenuButtons = new ArrayList<>();
                            contextMenuButtons.add(new ContextMenuButton("Insert Before", () -> DFScript.MC.setScreen(new ScriptAddArgumentScreen(script, action, (newArg) -> {
                                action.getArguments().add(currentIndex, newArg);
                            }, header)), false));
                            contextMenuButtons.add(new ContextMenuButton("Insert After", () -> DFScript.MC.setScreen(new ScriptAddArgumentScreen(script, action, (newArg) -> {
                                action.getArguments().add(currentIndex+1, newArg);
                            }, header)), false));
                            contextMenuButtons.add(new ContextMenuButton("Delete", () -> action.getArguments().remove(currentIndex)));
                            contextMenuButtons.addAll(action.getArguments().get(currentIndex).getContextMenu());
                            DFScript.MC.send(() -> {
                                if(DFScript.MC.currentScreen instanceof ScriptEditPartScreen screen) {
                                    screen.contextMenu((int) x, (int) y, contextMenuButtons);
                                }
                            });
                        }
                        return true;
                    }
                    return false;
                }
            });

            y += 10;
            index++;

        }

        CButton add = new CButton(25, y, 40, 8, "Add", () ->
                DFScript.MC.setScreen(new ScriptAddArgumentScreen(script, action, (newArg) -> {
                    action.getArguments().add(newArg);
                }, header)));
        panel.add(add);

        y += 10;

        for(ScriptActionTag actionTag : action.getActionArgumentList().getTags()) {
            for(ScriptTag tag : action.getTags()) {
                if(Objects.equals(tag.getTagName(), actionTag.getName())) {
                    ItemStack icon = tag.getIcon(actionTag);
                    String tagName = actionTag.getName();
                    String optionName = tag.getName(actionTag);

                    panel.add(new CItem(5, y, icon));
                    panel.add(new CText(15, y + 2, Text.literal(tagName)));

                    if(tag.getArgument() == null) {
                        panel.add(new CTexturedButton(5+80-8, y, 8, 8, DFScript.MOD_ID + ":add", () -> {
                            DFScript.MC.setScreen(new ScriptAddArgumentScreen(script, action, tag::setArgument, header));
                        }));
                    }
                    else {
                        panel.add(new CTexturedButton(5+80-8, y, 8, 8, DFScript.MOD_ID + ":off_button", () -> {
                            tag.setArgument(null);
                            reload();
                        }));

                        ScriptArgument arg = tag.getArgument();

                        ItemStack argIcon = arg.putNotices(arg.getArgIcon());
                        Text countText = arg.getArgIconText();
                        String text = arg.getArgText();

                        panel.add(new CItem(5, y + 10, argIcon, countText));
                        panel.add(new CText(15, y + 12, Text.literal(text)));

                        ScriptNotice notice = arg.getNotice();

                        panel.add(new CButton(5, y + 9, 80, 10, "", () -> {}) {
                            @Override
                            public void render(DrawContext context, int mouseX, int mouseY, float tickDelta) {
                                Rectangle b = getBounds();
                                int color = notice.getPartColor(b.contains(mouseX, mouseY));
                                context.fill(b.x, b.y, b.x + b.width, b.y + b.height, color);
                            }

                            @Override
                            public boolean mouseClicked(double x, double y, int button) {
                                if (getBounds().contains(x, y)) {
                                    DFScript.MC.getSoundManager().play(PositionedSoundInstance.ambient(SoundEvents.UI_BUTTON_CLICK.value(), 1f,1f));

                                    if (button == 0) {
                                        ScriptArgument argument = tag.getArgument();
                                        String value = argument.getOverwrite();
                                        DFScript.MC.setScreen(new ScriptAddArgumentScreen(script, action, tag::setArgument, header, value));
                                    }

                                    if (button != 0) {
                                        List<ContextMenuButton> contextMenuButtons = new ArrayList<>();
                                        contextMenuButtons.add(new ContextMenuButton("Delete", () -> tag.setArgument(null)));
                                        contextMenuButtons.addAll(tag.getArgument().getContextMenu());
                                        DFScript.MC.send(() -> {
                                            if(DFScript.MC.currentScreen instanceof ScriptEditPartScreen screen) {
                                                screen.contextMenu((int) x, (int) y, contextMenuButtons);
                                            }
                                        });
                                    }
                                    return true;
                                }
                                return false;
                            }
                        });
                    }

                    CSelectField tagOptions = new CSelectField(13, y + (tag.getArgument() == null ? 9 : 19), 80-13-2, 10, optionName, actionTag.getOptionNames());

                    tagOptions.setChangedListener(() -> {
                        tag.select(tagOptions.getSelectedOptionName());
                        reload();
                    });

                    panel.add(tagOptions);

                    panel.add(new CButton(5, y-1, 80, tag.getArgument() == null ? 20 : 30, "",() -> {}) {
                        @Override
                        public void render(DrawContext context, int mouseX, int mouseY, float tickDelta) {
                            Rectangle b = getBounds();
                            int color = ScriptNoticeLevel.NORMAL.getPartColor(b.contains(mouseX, mouseY));
                            context.fill(b.x, b.y, b.x + b.width, b.y + b.height, color);
                        }

                        @Override
                        public boolean mouseClicked(double x, double y, int button) {
                            return false;
                        }
                    });

                    y += tag.getArgument() == null ? 20 : 30;
                    index++;
                }
            }
        }
    }
}
