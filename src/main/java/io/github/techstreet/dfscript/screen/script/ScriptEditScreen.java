package io.github.techstreet.dfscript.screen.script;

import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.screen.CScreen;
import io.github.techstreet.dfscript.screen.widget.*;
import io.github.techstreet.dfscript.script.Script;
import io.github.techstreet.dfscript.script.ScriptComment;
import io.github.techstreet.dfscript.script.ScriptManager;
import io.github.techstreet.dfscript.script.ScriptPart;
import io.github.techstreet.dfscript.script.action.ScriptAction;
import io.github.techstreet.dfscript.script.action.ScriptActionType;
import io.github.techstreet.dfscript.script.action.ScriptRunnablePart;
import io.github.techstreet.dfscript.script.event.ScriptEvent;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import io.github.techstreet.dfscript.script.function.ScriptCallFunction;
import io.github.techstreet.dfscript.script.function.ScriptFunction;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class ScriptEditScreen extends CScreen {
    private final Identifier identifier_main = new Identifier(DFScript.MOD_ID + ":wrench.png");

    private final Script script;
    private static int scroll = 0;

    private final static int width = 125;
    private final CScrollPanel panel;
    private final List<CWidget> contextMenu = new ArrayList<>();

    public ScriptEditScreen(Script script) {
        super(width, 100);
        this.script = script;
        panel = new CScrollPanel(0, 3, 120, 94);

        //CTextField description = new CTextField(script.getDescription(), 3, 3, 115, 20, true);
        //description.setChangedListener(() -> script.setDescription(description.getText()));
        //panel.add(description);

        widgets.add(panel);

        int y = 0;
        int index = 0;
        int indent = 0;

        CButton settings = new CButton(37, y, 46, 8, "Settings", () -> {
            DFScript.MC.setScreen(new ScriptSettingsScreen(this.script, true));
        });

        panel.add(settings);

        y += 10;

        for (ScriptPart part : script.getParts()) {
            if (part instanceof ScriptEvent se) {
                panel.add(new CItem(5, y, se.getType().getIcon()));
                panel.add(new CText(15, y + 2, Text.literal(se.getType().getName())));
                indent = 5;

                int currentIndex = index;
                panel.add(new CButton(5, y-1, 115, 10, "",() -> {}) {
                    @Override
                    public void render(MatrixStack stack, int mouseX, int mouseY, float tickDelta) {
                        Rectangle b = getBounds();

                        if (b.contains(mouseX, mouseY)) {
                            int color = 0x33000000;

                            DrawableHelper.fill(stack, b.x, b.y, b.x + b.width, b.y + b.height, color);
                        }
                    }

                    @Override
                    public boolean mouseClicked(double x, double y, int button) {
                        if (getBounds().contains(x, y)) {
                            DFScript.MC.getSoundManager().play(PositionedSoundInstance.ambient(SoundEvents.UI_BUTTON_CLICK, 1f,1f));

                            if (button != 0) {
                                CButton insertBefore = new CButton((int) x, (int) y, 40, 8, "Insert Before", () -> {
                                    DFScript.MC.setScreen(new ScriptActionCategoryScreen(script, currentIndex));
                                });
                                CButton insertAfter = new CButton((int) x, (int) y+8, 40, 8, "Insert After", () -> {
                                    DFScript.MC.setScreen(new ScriptActionCategoryScreen(script, currentIndex + 1));
                                });
                                CButton delete = new CButton((int) x, (int) y+16, 40, 8, "Delete", () -> {
                                    script.getParts().remove(currentIndex);
                                    scroll = panel.getScroll();
                                    DFScript.MC.setScreen(new ScriptEditScreen(script));
                                });
                                DFScript.MC.send(() -> {
                                    panel.add(insertBefore);
                                    panel.add(insertAfter);
                                    panel.add(delete);
                                    contextMenu.add(insertBefore);
                                    contextMenu.add(insertAfter);
                                    contextMenu.add(delete);
                                });
                            }
                            return true;
                        }
                        return false;
                    }
                });
            } else if (part instanceof ScriptRunnablePart srp) {
                if (part instanceof ScriptAction sa && sa.getType() == ScriptActionType.CLOSE_BRACKET) {
                    indent -= 5;
                }

                panel.add(new CItem(5 + indent, y, srp.getIcon()));
                panel.add(new CText(15 + indent, y + 2, Text.literal(srp.getName())));

                createIndent(indent, y);

                int currentIndex = index;
                panel.add(new CButton(5, y - 1, 115, 10, "", () -> {
                }) {
                    @Override
                    public void render(MatrixStack stack, int mouseX, int mouseY, float tickDelta) {
                        Rectangle b = getBounds();
                        if (b.contains(mouseX, mouseY)) {
                            int color = 0x33000000;

                            if (srp instanceof ScriptAction sa && sa.getType().isDeprecated()) {
                                color = 0x80FF0000;
                            }

                            DrawableHelper.fill(stack, b.x, b.y, b.x + b.width, b.y + b.height, color);
                        } else {
                            if (srp instanceof ScriptAction sa && sa.getType().isDeprecated()) {
                                DrawableHelper.fill(stack, b.x, b.y, b.x + b.width, b.y + b.height, 0x33FF0000);
                            }
                        }
                    }

                    @Override
                    public boolean mouseClicked(double x, double y, int button) {
                        if (getBounds().contains(x, y)) {
                            DFScript.MC.getSoundManager().play(PositionedSoundInstance.ambient(SoundEvents.UI_BUTTON_CLICK, 1f, 1f));

                            if (button == 0) {
                                if (srp instanceof ScriptCallFunction || (srp instanceof ScriptAction sa && sa.getType() != ScriptActionType.CLOSE_BRACKET)) {
                                    scroll = panel.getScroll();
                                    DFScript.MC.setScreen(new ScriptEditActionScreen(srp, script));
                                }
                            } else {
                                CButton insertBefore = new CButton((int) x, (int) y, 40, 8, "Insert Before", () -> {
                                    DFScript.MC.setScreen(new ScriptActionCategoryScreen(script, currentIndex));
                                });
                                CButton insertAfter = new CButton((int) x, (int) y + 8, 40, 8, "Insert After", () -> {
                                    DFScript.MC.setScreen(new ScriptActionCategoryScreen(script, currentIndex + 1));
                                });
                                CButton delete = new CButton((int) x, (int) y + 16, 40, 8, "Delete", () -> {
                                    script.getParts().remove(currentIndex);
                                    scroll = panel.getScroll();
                                    DFScript.MC.setScreen(new ScriptEditScreen(script));
                                });
                                DFScript.MC.send(() -> {
                                    panel.add(insertBefore);
                                    panel.add(insertAfter);
                                    panel.add(delete);
                                    contextMenu.add(insertBefore);
                                    contextMenu.add(insertAfter);
                                    contextMenu.add(delete);
                                });
                            }
                            return true;
                        }
                        return false;
                    }
                });

                if (srp.hasChildren()) {
                    indent += 5;
                }
            } else if (part instanceof ScriptComment sc) {
                panel.add(new CItem(5 + indent, y, new ItemStack(Items.MAP).setCustomName(Text.literal("Comment").setStyle(Style.EMPTY.withItalic(false)))));

                CTextField cTextField = new CTextField(sc.getComment(),15+indent, y-1, width-(15+indent)-5, 10, true);

                cTextField.setChangedListener(() -> sc.setComment(cTextField.getText()));

                panel.add(cTextField);

                int currentIndex = index;

                panel.add(new CButton(5, y-1, 115, 10, "",() -> {}) {
                    @Override
                    public void render(MatrixStack stack, int mouseX, int mouseY, float tickDelta) {
                        Rectangle b = getBounds();

                        if (b.contains(mouseX, mouseY)) {
                            int color = 0x33000000;

                            DrawableHelper.fill(stack, b.x, b.y, b.x + b.width, b.y + b.height, color);
                        }
                    }

                    @Override
                    public boolean mouseClicked(double x, double y, int button) {
                        if (getBounds().contains(x, y)) {
                            DFScript.MC.getSoundManager().play(PositionedSoundInstance.ambient(SoundEvents.UI_BUTTON_CLICK, 1f,1f));

                            if (button != 0) {
                                CButton insertBefore = new CButton((int) x, (int) y, 40, 8, "Insert Before", () -> {
                                    DFScript.MC.setScreen(new ScriptActionCategoryScreen(script, currentIndex));
                                });
                                CButton insertAfter = new CButton((int) x, (int) y+8, 40, 8, "Insert After", () -> {
                                    DFScript.MC.setScreen(new ScriptActionCategoryScreen(script, currentIndex + 1));
                                });
                                CButton delete = new CButton((int) x, (int) y+16, 40, 8, "Delete", () -> {
                                    script.getParts().remove(currentIndex);
                                    scroll = panel.getScroll();
                                    DFScript.MC.setScreen(new ScriptEditScreen(script));
                                });
                                DFScript.MC.send(() -> {
                                    panel.add(insertBefore);
                                    panel.add(insertAfter);
                                    panel.add(delete);
                                    contextMenu.add(insertBefore);
                                    contextMenu.add(insertAfter);
                                    contextMenu.add(delete);
                                });

                                return true;
                            }
                        }
                        return false;
                    }
                });

                createIndent(indent, y);
            }
            else if (part instanceof ScriptFunction sf) {
                panel.add(new CItem(5, y, new ItemStack(Items.LAPIS_BLOCK).setCustomName(Text.literal("Function Definition").setStyle(Style.EMPTY.withItalic(false)))));
                panel.add(new CText(15, y + 2, Text.literal(sf.getFunctionName())));
                indent = 5;

                int currentIndex = index;
                panel.add(new CButton(5, y-1, 115, 10, "",() -> {}) {
                    @Override
                    public void render(MatrixStack stack, int mouseX, int mouseY, float tickDelta) {
                        Rectangle b = getBounds();

                        if (b.contains(mouseX, mouseY)) {
                            int color = 0x33000000;

                            DrawableHelper.fill(stack, b.x, b.y, b.x + b.width, b.y + b.height, color);
                        }
                    }

                    @Override
                    public boolean mouseClicked(double x, double y, int button) {
                        if (getBounds().contains(x, y)) {
                            DFScript.MC.getSoundManager().play(PositionedSoundInstance.ambient(SoundEvents.UI_BUTTON_CLICK, 1f,1f));

                            if (button != 0) {
                                CButton insertBefore = new CButton((int) x, (int) y, 40, 8, "Insert Before", () -> {
                                    DFScript.MC.setScreen(new ScriptActionCategoryScreen(script, currentIndex));
                                });
                                CButton insertAfter = new CButton((int) x, (int) y+8, 40, 8, "Insert After", () -> {
                                    DFScript.MC.setScreen(new ScriptActionCategoryScreen(script, currentIndex + 1));
                                });
                                CButton delete = new CButton((int) x, (int) y+16, 40, 8, "Delete", () -> {
                                    script.getParts().remove(currentIndex);
                                    scroll = panel.getScroll();
                                    DFScript.MC.setScreen(new ScriptEditScreen(script));
                                });
                                DFScript.MC.send(() -> {
                                    panel.add(insertBefore);
                                    panel.add(insertAfter);
                                    panel.add(delete);
                                    contextMenu.add(insertBefore);
                                    contextMenu.add(insertAfter);
                                    contextMenu.add(delete);
                                });
                            }
                            else
                            {
                                DFScript.MC.setScreen(new ScriptEditFunctionScreen(script, sf));
                            }

                            return true;
                        }
                        return false;
                    }
                });
            } else {
                throw new IllegalArgumentException("Unknown script part type");
            }

            y += 10;
            index++;
        }

        CButton add = new CButton(37, y, 46, 8, "Add", () -> {
            DFScript.MC.setScreen(new ScriptActionCategoryScreen(script, script.getParts().size()));
        });

        panel.add(add);
        panel.setScroll(scroll);
    }

    public void createIndent(int indent, int y)
    {
        for (int i = 0; i < indent; i += 5) {
            int xpos = 8 + i;
            int ypos = y;
            panel.add(new CWidget() {
                @Override
                public void render(MatrixStack stack, int mouseX, int mouseY, float tickDelta) {
                    DrawableHelper.fill(stack, xpos, ypos, xpos + 1, ypos + 8, 0xFF333333);
                }

                @Override
                public Rectangle getBounds() {
                    return new Rectangle(0, 0, 0, 0);
                }
            });
        }
    }

    @Override
    public void close() {
        scroll = panel.getScroll();
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
            panel.remove(w);
        }
        contextMenu.clear();
    }
}
