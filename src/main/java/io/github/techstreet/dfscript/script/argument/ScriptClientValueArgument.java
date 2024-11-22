package io.github.techstreet.dfscript.script.argument;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.event.KeyPressEvent;
import io.github.techstreet.dfscript.event.ReceiveChatEvent;
import io.github.techstreet.dfscript.event.RecieveSoundEvent;
import io.github.techstreet.dfscript.event.SendChatEvent;
import io.github.techstreet.dfscript.script.ScriptNotice;
import io.github.techstreet.dfscript.script.ScriptNoticeLevel;
import io.github.techstreet.dfscript.script.action.ScriptActionArgument.ScriptActionArgumentType;
import io.github.techstreet.dfscript.script.execution.ScriptTask;
import io.github.techstreet.dfscript.script.menu.ScriptMenuClickButtonEvent;
import io.github.techstreet.dfscript.script.util.ScriptValueItem;
import io.github.techstreet.dfscript.script.values.*;
import io.github.techstreet.dfscript.util.ComponentUtil;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public enum ScriptClientValueArgument implements ScriptArgument {

    EVENT_KEY("KeyPressed","The key code of the key pressed. (KeyPressEvent)", Items.STONE_BUTTON, ScriptActionArgumentType.NUMBER, (task) -> {
        if (task.event() instanceof KeyPressEvent e) {
            return new ScriptNumberValue(e.getKey().getCode());
        } else {
            throw new IllegalStateException("Event is not a KeyPressEvent");
        }
    }),

    EVENT_KEY_ACTION("KeyAction","The code of the key action performed. (KeyPressEvent)", Items.OAK_BUTTON, ScriptActionArgumentType.NUMBER, (task) -> {
        if (task.event() instanceof KeyPressEvent e) {
            return new ScriptNumberValue(e.getAction());
        } else {
            throw new IllegalStateException("Event is not a KeyPressEvent");
        }
    }),

    EVENT_MESSAGE("ReceivedMessage","The message received. (ReceiveChatEvent)", Items.WRITTEN_BOOK, ScriptActionArgumentType.TEXT, (task) -> {
        if (task.event() instanceof ReceiveChatEvent e) {
            return new ScriptTextValue(ComponentUtil.sectionSignsToAnds(ComponentUtil.toFormattedString(e.getMessage())));
        } else {
            throw new IllegalStateException("Event is not a ReceiveChatEvent");
        }
    }),

    ENTERED_MESSAGE("EnteredMessage","The message entered. (SendChatEvent)", Items.WRITABLE_BOOK, ScriptActionArgumentType.TEXT, (task) -> {
        if (task.event() instanceof SendChatEvent e) {
            return new ScriptTextValue(e.getMessage());
        } else {
            throw new IllegalStateException("Event is not a SendChatEvent");
        }
    }),

    TIMESTAMP("Timestamp","The current timestamp in milliseconds.", Items.CLOCK, ScriptActionArgumentType.NUMBER, (task) -> new ScriptNumberValue(System.currentTimeMillis())),

    CLIPBOARD("Clipboard", "The current text on the clipboard", Items.PAPER, ScriptActionArgumentType.TEXT, (task) -> new ScriptTextValue(DFScript.MC.keyboard.getClipboard())),

    MAIN_HAND_ITEM("MainHandItem","The item in the players main hand.", Items.STONE_BUTTON, ScriptActionArgumentType.ITEM,
            (task) -> new ScriptItemValue(DFScript.MC.player.getMainHandStack())),

    MAIN_HAND_ITEM_OLD(builder -> builder
            .name("MainHandItem (Old)")
            .description("The item in the players main hand.")
            .icon(Items.STONE_BUTTON)
            .removeUsability().proposeAlternative(MAIN_HAND_ITEM)
    ),

    OFF_HAND_ITEM("OffHandItem","The item in the players off hand.", Items.OAK_BUTTON, ScriptActionArgumentType.ITEM,
            (task) -> new ScriptItemValue(DFScript.MC.player.getOffHandStack())
    ),

    OFF_HAND_ITEM_OLD(builder -> builder
            .name("OffHandItem (Old)")
            .description("The item in the players off hand.")
            .icon(Items.OAK_BUTTON)
            .removeUsability().proposeAlternative(OFF_HAND_ITEM)
    ),

    FULL_INVENTORY("FullInventory","The entire inventory items of the player.", Items.OAK_PLANKS, ScriptActionArgumentType.LIST, (task) -> {
        List<ScriptValue> list = new ArrayList<>();
        for (int i = 0; i < DFScript.MC.player.getInventory().size(); i++) {
            list.add(new ScriptItemValue(DFScript.MC.player.getInventory().getStack(i)));
        }
        return new ScriptListValue(list);
    }),

    FULL_INVENTORY_OLD(builder -> builder
            .name("FullInventory (Old)")
            .description("The entire inventory items of the player.")
            .icon(Items.OAK_PLANKS)
            .removeUsability().proposeAlternative(FULL_INVENTORY)
    ),

    MAIN_INVENTORY("MainInventory", "The main inventory items of the player.", Items.BIRCH_PLANKS, ScriptActionArgumentType.LIST, (task) -> {
        List<ScriptValue> list = new ArrayList<>();
        for (ItemStack item : DFScript.MC.player.getInventory().main) {
            list.add(new ScriptItemValue(item));
        }
        return new ScriptListValue(list);
    }),

    MAIN_INVENTORY_OLD(builder -> builder
            .name("MainInventory (Old)")
            .description("The main inventory items of the player.")
            .icon(Items.BIRCH_PLANKS)
            .removeUsability().proposeAlternative(MAIN_INVENTORY)
    ),

    ARMOR("Armor", "The armor items of the player.", Items.IRON_CHESTPLATE, ScriptActionArgumentType.LIST, (task) -> {
        List<ScriptValue> list = new ArrayList<>();
        for (ItemStack item : DFScript.MC.player.getInventory().armor) {
            list.add(new ScriptItemValue(item));
        }
        return new ScriptListValue(list);
    }),

    ARMOR_OLD(builder -> builder
            .name("Armor (Old)")
            .description("The armor items of the player.")
            .icon(Items.IRON_CHESTPLATE)
            .removeUsability().proposeAlternative(ARMOR)
    ),

    HOTBAR_ITEMS("Hotbar Items", "The hotbar items of the player.", Items.IRON_AXE, ScriptActionArgumentType.LIST, (task) -> {
        List<ScriptValue> list = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            list.add(new ScriptItemValue(DFScript.MC.player.getInventory().getStack(i)));
        }
        return new ScriptListValue(list);
    }),

    HOTBAR_ITEMS_OLD(builder -> builder
            .name("Hotbar Items (Old)")
            .description("The hotbar items of the player.")
            .icon(Items.IRON_AXE)
            .removeUsability().proposeAlternative(HOTBAR_ITEMS)
    ),

    SELECTED_SLOT("Selected Slot", "The selected hotbar slot.", Items.LIME_DYE, ScriptActionArgumentType.NUMBER,
            (task) -> new ScriptNumberValue(DFScript.MC.player.getInventory().selectedSlot)
    ),

    GAME_MODE("Game Mode", "The gamemode the player is in.", Items.BEDROCK, ScriptActionArgumentType.TEXT,
            (task) -> new ScriptTextValue(DFScript.MC.interactionManager.getCurrentGameMode().getName())
    ),

    WINDOW_WIDTH("Window Width", "The width of the current window.", Items.STICK, ScriptActionArgumentType.NUMBER,
            (task) -> new ScriptNumberValue(DFScript.MC.getWindow().getScaledWidth())
    ),

    WINDOW_HEIGHT("Window Height", "The height of the current window.", Items.STICK, ScriptActionArgumentType.NUMBER,
            (task) -> new ScriptNumberValue(DFScript.MC.getWindow().getScaledHeight())
    ),

    MENU_ELEMENT_IDENTIFIER("Menu Element Identifier", "The identifier of the menu element that triggered the event.", Items.NAME_TAG, ScriptActionArgumentType.TEXT,(task) -> {
        if (task.event() instanceof ScriptMenuClickButtonEvent e) {
            return new ScriptTextValue(e.identifier());
        } else {
            throw new IllegalStateException("The event is not a menu click event.");
        }
    }),
    
    PLAYER_UUID("Player UUID", "The UUID of the player.", Items.PLAYER_HEAD, ScriptActionArgumentType.TEXT,
            (task) -> new ScriptTextValue(DFScript.PLAYER_UUID)),

    PLAYER_NAME("Player Name", "The name of the player.", Items.PLAYER_HEAD, ScriptActionArgumentType.TEXT,
            (task) -> new ScriptTextValue(DFScript.PLAYER_NAME)),

    EVENT_SOUND("ReceivedSound", "The ID of the sound. (OnReceiveSound)", Items.NAUTILUS_SHELL, ScriptActionArgumentType.TEXT, (task) -> {
        if(task.event() instanceof RecieveSoundEvent e) {
            return new ScriptTextValue(e.getSoundId().toString().replaceAll("^minecraft:", ""));
        } else {
            throw new IllegalStateException("The event is not a receive sound event.");
        }
    }),

    EVENT_VOLUME("ReceivedSoundVolume", "The volume of the sound received. (OnReceiveSound)", Items.NOTE_BLOCK, ScriptActionArgumentType.NUMBER, (task) -> {
        if(task.event() instanceof RecieveSoundEvent e) {
            return new ScriptNumberValue(e.getVolume());
        } else {
            throw new IllegalStateException("The event is not a receive sound event.");
        }
    }),

    EVENT_PITCH("ReceivedSoundPitch", "The pitch of the sound received. (OnReceiveSound)", Items.JUKEBOX, ScriptActionArgumentType.NUMBER, (task) -> {
        if(task.event() instanceof RecieveSoundEvent e) {
            return new ScriptNumberValue(e.getPitch());
        } else {
            throw new IllegalStateException("The event is not a receive sound event.");
        }
    });

    private Function<ScriptTask, ScriptValue> valueGetter = (task) -> new ScriptUnknownValue();
    private ScriptActionArgumentType type = null;

    private boolean glow = false;
    private Item icon = Items.STONE;
    private String name = "Unnamed Action";
    private final List<String> description = new ArrayList<>();

    private ScriptClientValueArgument alternative = null;
    private ScriptNoticeLevel noticeLevel = ScriptNoticeLevel.NORMAL;

    ScriptClientValueArgument(Consumer<ScriptClientValueArgument> builder) {
        description.add("No description provided.");
        builder.accept(this);
    }

    ScriptClientValueArgument(String name, String description, Item type, ScriptActionArgumentType varType, Function<ScriptTask, ScriptValue> consumer) {
        name(name).description(description).icon(type).valueGetter(varType, consumer);
    }

    ScriptClientValueArgument(String name, String description, Item type, ScriptActionArgumentType varType, Function<ScriptTask, ScriptValue> consumer, Consumer<ScriptClientValueArgument> builder) {
        this(name, description, type, varType, consumer);
        builder.accept(this);
    }

    public ItemStack getIcon() {
        ItemStack item = new ItemStack(icon);

        item.set(DataComponentTypes.CUSTOM_NAME, Text.literal(name)
                .fillStyle(Style.EMPTY
                        .withColor(Formatting.WHITE)
                        .withItalic(false)));

        List<Text> lore = new ArrayList<>();

        for (String descriptionLine: description) {
            lore.add(Text.literal(descriptionLine)
                    .fillStyle(Style.EMPTY
                            .withColor(Formatting.GRAY)
                            .withItalic(false)));
        }

        item.set(DataComponentTypes.LORE, new LoreComponent(lore));

        item.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, glow);

        return item;
    }

    public String getName() {
        return name;
    }

    public ScriptNotice getNotice() {
        if(alternative != null) {
            return new ScriptNotice(noticeLevel, "Consider using '" + alternative.getName() + "' instead.");
        }

        return new ScriptNotice(noticeLevel);
    }

    public String getNoticeDescriptor() {
        return "client value";
    }

    private ScriptClientValueArgument valueGetter(ScriptActionArgumentType type, Function<ScriptTask, ScriptValue> valueGetter) {
        this.type = type;
        this.valueGetter = valueGetter;
        return this;
    }

    private ScriptClientValueArgument icon(Item icon, boolean glow) {
        this.icon = icon;
        this.glow = glow;
        return this;
    }

    private ScriptClientValueArgument icon(Item icon) {
        icon(icon, false);
        return this;
    }

    private ScriptClientValueArgument name(String name) {
        this.name = name;
        return this;
    }

    private ScriptClientValueArgument description(String description) {
        this.description.clear();
        this.description.addAll(Arrays.asList(description.split("\n", -1)));
        return this;
    }

    public ScriptClientValueArgument deprecate() {
        noticeLevel = ScriptNoticeLevel.DEPRECATION;
        return this;
    }

    public ScriptClientValueArgument removeUsability() {
        noticeLevel = ScriptNoticeLevel.UNUSABILITY;
        return this;
    }

    public ScriptClientValueArgument proposeAlternative(ScriptClientValueArgument alternative) {
        this.alternative = alternative;

        return this;
    }

    @Override
    public ScriptValue getValue(ScriptTask task) {
        return valueGetter.apply(task);
    }

    @Override
    public boolean convertableTo(ScriptActionArgumentType type) {
        if(this.type == null) {
            return false;
        }
        return this.type.convertableTo(type);
    }

    public static class Serializer implements JsonSerializer<ScriptClientValueArgument> {

        @Override
        public JsonElement serialize(ScriptClientValueArgument src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            object.addProperty("type","CLIENT_VALUE");
            object.addProperty("value",src.name());
            return object;
        }
    }

    @Override
    public ItemStack getArgIcon() {
        ItemStack result = new ItemStack(Items.NAME_TAG);
        result.set(DataComponentTypes.CUSTOM_NAME, Text.literal("Client Value").setStyle(Style.EMPTY.withColor(Formatting.WHITE).withItalic(false)));
        return result;
    }

    @Override
    public String getArgText() {
        return name;
    }
}
