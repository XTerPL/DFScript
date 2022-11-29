package io.github.techstreet.dfscript.screen.misc;

import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.screen.CScreen;
import io.github.techstreet.dfscript.screen.script.ScriptListScreen;
import io.github.techstreet.dfscript.screen.widget.CItem;
import io.github.techstreet.dfscript.screen.widget.CScrollPanel;
import io.github.techstreet.dfscript.screen.widget.CTextField;
import io.github.techstreet.dfscript.screen.widget.CTexturedButton;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.registry.Registry;

import java.awt.*;
import java.util.Objects;
import java.util.function.Consumer;

public class ItemMaterialSelectionScreen extends CScreen {

    static int WIDTH = 104;
    static int HEIGHT = 100;
    final CScrollPanel panel;
    CItem selectedItem;
    CTextField field;
    final Consumer<String> save;
    String current;

    public ItemMaterialSelectionScreen(Consumer<String> save) {
        this(save, "");
    }

    public ItemMaterialSelectionScreen(Consumer<String> save, String current) {
        super(WIDTH, HEIGHT);

        this.save = save;
        this.current = current;

        panel = new CScrollPanel(2,12, WIDTH-4,HEIGHT-12);
        widgets.add(panel);

        CTexturedButton button = new CTexturedButton(WIDTH - 10, 4, 8, 8, DFScript.MOD_ID + ":on_button.png", this::close, 0,0,1,0.5f,0,0.5f);

        field = new CTextField("", 16, 4, WIDTH - 16 - 10, 8, true);
        field.setChangedListener(
                () -> DFScript.MC.send(this::refreshItems)
        );

        widgets.add(button);
        widgets.add(field);

        refreshItems();
    }

    public void refreshItems()
    {
        widgets.remove(selectedItem);

        panel.clear();

        int x = 2;
        int y = 2;

        for (Item item : Registry.ITEM) {
            if(Objects.equals(item, Items.AIR)) {
                continue;
            }

            String name = String.valueOf(item.getName());

            if(current.equals(item.toString())) {
                selectedItem = new CItem(4, 4, new ItemStack(item));
                widgets.add(selectedItem);
            }

            if(!name.contains(field.getText())) {
                continue;
            }

            ItemStack itemStack = new ItemStack(item);

            CItem citem;

            if(current.equals(item.toString())) {
                citem = new CItem(x, y, itemStack) {
                    @Override
                    public void render(MatrixStack stack, int mouseX, int mouseY, float tickDelta) {
                        super.render(stack, mouseX, mouseY, tickDelta);
                        Rectangle b = getBounds();
                        DrawableHelper.fill(stack, b.x, b.y, b.x + b.width, b.y + b.height, 0x3300ff00);
                    }
                };
            } else {
                citem = new CItem(x, y, itemStack);
            }

            citem.setClickListener((mouse) -> {
                current = item.toString();
                refreshItems();
            });

            panel.add(citem);

            x += 10;
            if(x >= WIDTH-4) {
                y += 10;
                x = 2;
            }
        }
    }

    @Override
    public void close() {
        save.accept(current);
    }
}
