package io.github.techstreet.dfscript.script.conditions;

import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.script.ScriptNotice;
import io.github.techstreet.dfscript.script.ScriptNoticeLevel;
import io.github.techstreet.dfscript.script.action.*;
import io.github.techstreet.dfscript.script.action.ScriptActionArgument.ScriptActionArgumentType;
import io.github.techstreet.dfscript.script.execution.ScriptActionContext;
import io.github.techstreet.dfscript.script.values.*;
import io.github.techstreet.dfscript.util.*;
import io.github.techstreet.dfscript.util.chat.ChatUtil;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import javax.xml.crypto.Data;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public enum ScriptConditionType {

    IF_EQUALS(builder -> builder.name("Equals")
        .description("Checks if one value is equal to another.")
        .icon(Items.IRON_INGOT)
        .category(ScriptActionCategory.VARIABLES)
        .arg("Value", ScriptActionArgumentType.ANY)
        .arg("Other", ScriptActionArgumentType.ANY)
        .action(ctx -> ctx.value("Value").valueEquals(ctx.value("Other")))),

    IF_NOT_EQUALS(builder -> builder.name("Not Equals")
        .description("Checks if one value is not equal to another.")
        .icon(Items.BARRIER)
        .category(ScriptActionCategory.VARIABLES)
        .arg("Value", ScriptActionArgumentType.ANY)
        .arg("Other", ScriptActionArgumentType.ANY)
        .deprecate().proposeAlternative(IF_EQUALS)
        .action(ctx -> !ctx.value("Value").valueEquals(ctx.value("Other")))),

    IF_GREATER(builder -> builder.name("Greater")
        .description("Checks if one number is greater than another.")
        .icon(Items.BRICK)
        .category(ScriptActionCategory.NUMBERS)
        .arg("Value", ScriptActionArgumentType.NUMBER)
        .arg("Other", ScriptActionArgumentType.NUMBER)
        .action(ctx -> ctx.value("Value").asNumber() > ctx.value("Other").asNumber())),

    IF_GREATER_EQUALS(builder -> builder.name("Greater Equals")
        .description("Checks if one number is greater than or equal to another.")
        .icon(Items.BRICKS)
        .category(ScriptActionCategory.NUMBERS)
        .arg("Value", ScriptActionArgumentType.NUMBER)
        .arg("Other", ScriptActionArgumentType.NUMBER)
        .action(ctx -> ctx.value("Value").asNumber() >= ctx.value("Other").asNumber())),

    IF_LESS(builder -> builder.name("Less")
        .description("Checks if one number is less than another.")
        .icon(Items.NETHER_BRICK)
        .category(ScriptActionCategory.NUMBERS)
        .arg("Value", ScriptActionArgumentType.NUMBER)
        .arg("Other", ScriptActionArgumentType.NUMBER)
        .action(ctx -> ctx.value("Value").asNumber() < ctx.value("Other").asNumber())),

    IF_LESS_EQUALS(builder -> builder.name("If Less Equals")
        .description("Checks if one number is less than or equal to another.")
        .icon(Items.NETHER_BRICKS)
        .category(ScriptActionCategory.NUMBERS)
        .arg("Value", ScriptActionArgumentType.NUMBER)
        .arg("Other", ScriptActionArgumentType.NUMBER)
        .action(ctx -> ctx.value("Value").asNumber() <= ctx.value("Other").asNumber())),

    IF_WITHIN_RANGE(builder -> builder.name("Number Within Range")
            .description("Checks if a number is between\n2 different numbers (inclusive).")
            .icon(Items.CHEST)
            .category(ScriptActionCategory.NUMBERS)
            .arg("Value", ScriptActionArgumentType.NUMBER)
            .arg("Minimum", ScriptActionArgumentType.NUMBER)
            .arg("Maximum", ScriptActionArgumentType.NUMBER)
            .action(ctx -> {
                double value = ctx.value("Value").asNumber();

                if (value >= ctx.value("Minimum").asNumber()) {
                    if (value <= ctx.value("Maximum").asNumber()) {
                        return true;
                    }
                }
                return false;
            })),

    IF_NOT_WITHIN_RANGE(builder -> builder.name("Number Not Within Range")
            .description("Checks if a number isn't between\n2 different numbers (inclusive).")
            .icon(Items.TRAPPED_CHEST)
            .category(ScriptActionCategory.NUMBERS)
            .arg("Value", ScriptActionArgumentType.NUMBER)
            .arg("Minimum", ScriptActionArgumentType.NUMBER)
            .arg("Maximum", ScriptActionArgumentType.NUMBER)
            .deprecate().proposeAlternative(IF_WITHIN_RANGE)
            .action(ctx -> {
                double value = ctx.value("Value").asNumber();

                if (value >= ctx.value("Minimum").asNumber()) {
                    if (value <= ctx.value("Maximum").asNumber()) {
                        return false;
                    }
                }

                return true;
            })),

    IF_LIST_CONTAINS(builder -> builder.name("List Contains")
        .description("Checks if a list contains a value.")
        .icon(Items.BOOKSHELF)
        .category(ScriptActionCategory.LISTS)
        .arg("List", ScriptActionArgumentType.LIST)
        .arg("Value", ScriptActionArgumentType.ANY)
        .action(ctx -> {
            List<ScriptValue> list = ctx.value("List").asList();
            return list.stream().anyMatch(value -> value.valueEquals(ctx.value("Value")));
        })),

    IF_TEXT_CONTAINS(builder -> builder.name("Text Contains")
        .description("Checks if a text contains a value.")
        .icon(Items.NAME_TAG)
        .category(ScriptActionCategory.TEXTS)
        .arg("Text", ScriptActionArgumentType.TEXT)
        .arg("Subtext", ScriptActionArgumentType.TEXT)
        .action(ctx -> {
            String text = ctx.value("Text").asText();
            String subtext = ctx.value("Subtext").asText();
            return text.contains(subtext);
        })),

    IF_MATCHES_REGEX(builder -> builder.name("Matches Regex")
        .description("Checks if a text matches a regex.")
        .icon(Items.ANVIL)
        .category(ScriptActionCategory.TEXTS)
        .arg("Text", ScriptActionArgumentType.TEXT)
        .arg("Regex", ScriptActionArgumentType.TEXT)
        .action(ctx -> {
            String text = ctx.value("Text").asText();
            String regex = ctx.value("Regex").asText();
            return text.matches(regex);
        })),

    IF_STARTS_WITH(builder -> builder.name("Starts With")
        .description("Checks if a text starts with an other.")
        .icon(Items.FEATHER)
        .category(ScriptActionCategory.TEXTS)
        .arg("Text", ScriptActionArgumentType.TEXT)
        .arg("Subtext", ScriptActionArgumentType.TEXT)
        .action(ctx -> {
            String text = ctx.value("Text").asText();
            String subtext = ctx.value("Subtext").asText();
            return text.startsWith(subtext);
        })),

    IF_LIST_DOESNT_CONTAIN(builder -> builder.name("List Doesnt Contain")
        .description("Checks if a list doesnt contain a value.")
        .icon(Items.BOOKSHELF)
        .category(ScriptActionCategory.LISTS)
        .arg("List", ScriptActionArgumentType.LIST)
        .arg("Value", ScriptActionArgumentType.ANY)
        .deprecate().proposeAlternative(IF_LIST_CONTAINS)
        .action(ctx -> {
            List<ScriptValue> list = ctx.value("List").asList();
            return list.stream().noneMatch(value -> value.valueEquals(ctx.value("Value")));
        })),

    IF_TEXT_DOESNT_CONTAIN(builder -> builder.name("Text Doesnt Contain")
        .description("Checks if a text doesnt contain a value.")
        .icon(Items.NAME_TAG)
        .category(ScriptActionCategory.TEXTS)
        .arg("Text", ScriptActionArgumentType.TEXT)
        .arg("Subtext", ScriptActionArgumentType.TEXT)
        .deprecate().proposeAlternative(IF_TEXT_CONTAINS)
        .action(ctx -> {
            String text = ctx.value("Text").asText();
            String subtext = ctx.value("Subtext").asText();
            return !text.contains(subtext);
        })),

    IF_DOESNT_START_WITH(builder -> builder.name("Doesnt Start With")
        .description("Checks if a text doesnt start with an other.")
        .icon(Items.FEATHER)
        .category(ScriptActionCategory.TEXTS)
        .arg("Text", ScriptActionArgumentType.TEXT)
        .arg("Subtext", ScriptActionArgumentType.TEXT)
        .deprecate().proposeAlternative(IF_STARTS_WITH)
        .action(ctx -> {
            String text = ctx.value("Text").asText();
            String subtext = ctx.value("Subtext").asText();
            return !text.startsWith(subtext);
        })),

    IF_DOESNT_MATCH_REGEX(builder -> builder.name("Doesnt Match Regex")
        .description("Checks if a text doesnt match a regex.")
        .icon(Items.ANVIL)
        .category(ScriptActionCategory.TEXTS)
        .arg("Text", ScriptActionArgumentType.TEXT)
        .arg("Regex", ScriptActionArgumentType.TEXT)
        .deprecate().proposeAlternative(IF_MATCHES_REGEX)
        .action(ctx -> {
            String text = ctx.value("Text").asText();
            String regex = ctx.value("Regex").asText();
            return !text.matches(regex);
        })),

    IF_DICT_KEY_EXISTS(builder -> builder.name("Dictionary Key Exists")
        .description("Checks if a key exists in a dictionary.")
        .icon(Items.NAME_TAG)
        .category(ScriptActionCategory.DICTIONARIES)
        .arg("Dictionary", ScriptActionArgumentType.DICTIONARY)
        .arg("Key", ScriptActionArgumentType.TEXT)
        .action(ctx -> {
            HashMap<String, ScriptValue> dict = ctx.value("Dictionary").asDictionary();
            String key = ctx.value("Key").asText();
            return dict.containsKey(key);
        })),

    IF_DICT_KEY_DOESNT_EXIST(builder -> builder.name("Dictionary Key Doesnt Exist")
        .description("Checks if a key doesnt exist in a dictionary.")
        .icon(Items.NAME_TAG)
        .category(ScriptActionCategory.DICTIONARIES)
        .arg("Dictionary", ScriptActionArgumentType.DICTIONARY)
        .arg("Key", ScriptActionArgumentType.TEXT)
        .deprecate().proposeAlternative(IF_DICT_KEY_EXISTS)
        .action(ctx -> {
            HashMap<String, ScriptValue> dict = ctx.value("Dictionary").asDictionary();
            String key = ctx.value("Key").asText();
            return !dict.containsKey(key);
        })),

    IF_GUI_OPEN(builder -> builder.name("GUI Open")
        .description("Executes if a gui is open.")
        .icon(Items.BOOK)
        .category(ScriptActionCategory.MISC)
        .action(ctx -> {
            return DFScript.MC.currentScreen != null;
        })),

    IF_GUI_CLOSED(builder -> builder.name("GUI Not Open")
        .description("Executes if no gui is open.")
        .icon(Items.BOOK)
        .deprecate().proposeAlternative(IF_GUI_OPEN)
        .category(ScriptActionCategory.MISC)
        .action(ctx -> {
            return DFScript.MC.currentScreen == null;
        })),

    IF_FILE_EXISTS(builder -> builder.name("File Exists")
        .description("Executes if the specified file exists.")
        .icon(Items.BOOK)
        .category(ScriptActionCategory.MISC)
        .arg("Filename", ScriptActionArgumentType.TEXT)
        .action(ctx -> {
            String filename = ctx.value("Filename").asText();
            if (filename.matches("^[a-zA-Z\\d_\\-. ]+$")) {
                Path f = FileUtil.folder("Scripts").resolve(ctx.task().context().script().getFile().getName()+"-files").resolve(filename);
                if (Files.exists(f)) {
                    return true;
                }
            } else {
                ChatUtil.error("Illegal filename: " + filename);
            }
            return false;
        })),

    IF_FILE_DOESNT_EXIST(builder -> builder.name("File Doesnt Exist")
        .description("Executes if the specified file doesnt exist.")
        .icon(Items.BOOK)
        .category(ScriptActionCategory.MISC)
        .arg("Filename", ScriptActionArgumentType.TEXT)
        .deprecate().proposeAlternative(IF_FILE_EXISTS)
        .action(ctx -> {
            String filename = ctx.value("Filename").asText();
            if (filename.matches("^[a-zA-Z\\d_\\-. ]+$")) {
                Path f = FileUtil.folder("Scripts").resolve(ctx.task().context().script().getFile().getName()+"-files").resolve(filename);
                if (!Files.exists(f)) {
                    return true;
                }
            } else {
                ChatUtil.error("Illegal filename: " + filename);
            }
            return false;
        })),

    IF_UNKNOWN(builder -> builder.name("Unknown")
            .description("Checks if a value is of the unknown type.")
            .icon(Items.IRON_NUGGET)
            .category(ScriptActionCategory.VARIABLES)
            .arg("Value", ScriptActionArgumentType.ANY)
            .action(ctx -> (ctx.value("Value") instanceof ScriptUnknownValue))),

    IF_BOOLEAN_TRUE(builder -> builder.name("Boolean Is True")
            .description("Checks if a boolean is true.")
            .icon(Items.YELLOW_WOOL)
            .category(ScriptActionCategory.CONDITIONS)
            .arg("Value", ScriptActionArgumentType.BOOL)
            .action(ctx -> ctx.value("Value").asBoolean())),

    IF_ITEM_EQUALS(builder -> builder.name("Item Equals")
            .description("Checks if two items are equal.\n" +
                         "Has a few extra comparison modes.")
            .icon(Items.ITEM_FRAME)
            .category(ScriptActionCategory.ITEMS)
            .arg("Item to check", ScriptActionArgumentType.ITEM)
            .arg("Item to compare to", ScriptActionArgumentType.ITEM, b -> b.optional(true))
            .tag(new ScriptActionTag("Comparison Mode",
                    new ScriptActionTag.ScriptActionTagOption("Exactly Equals", Items.GOLD_INGOT),
                    new ScriptActionTag.ScriptActionTagOption("Ignore Stack Size", Items.CHEST),
                    new ScriptActionTag.ScriptActionTagOption("Ignore Stack Size and Durability", Items.DIAMOND_SWORD),
                    new ScriptActionTag.ScriptActionTagOption("Material Only", Items.CHISELED_STONE_BRICKS)
            ))
            .action(ctx -> {
                ItemStack item1 = ctx.value("Item to check").asItem();

                if(!ctx.argMap().containsKey("Item to compare to")) {
                    return item1.isEmpty();
                }

                ItemStack item2 = ctx.value("Item to compare to").asItem();

                switch(ctx.tagValue("Comparison Mode")) {
                    case "Exactly Equals" -> {
                        return item1.equals(item2);
                    }
                    case "Ignore Stack Size" -> {
                        item1.setCount(1);
                        item2.setCount(1);
                    }
                    case "Ignore Stack Size and Durability" -> {
                        item1.setCount(1);
                        item2.setCount(1);

                        if(item1.contains(DataComponentTypes.DAMAGE)) {
                            item1.set(DataComponentTypes.DAMAGE, 0);
                        }
                        if(item2.contains(DataComponentTypes.DAMAGE)) {
                            item1.set(DataComponentTypes.DAMAGE, 0);
                        }
                    }
                    case "Material Only" -> {
                        return item1.isOf(item2.getItem());
                    }
                }

                return item1.equals(item2);
            })),

    IF_ITEM_HAS_CUSTOM_DATA(builder -> builder.name("Item Has Custom Data")
            .description("Checks if an item has custom data.")
            .icon(Items.CHEST_MINECART)
            .category(ScriptActionCategory.ITEMS)
            .arg("Item to check", ScriptActionArgumentType.ITEM)
            .action(ctx -> {
                ItemStack item = ctx.value("Item to check").asItem();

                return item.contains(DataComponentTypes.CUSTOM_DATA);
            })),

    IF_ITEM_HAS_CUSTOM_NAME(builder -> builder.name("Item Has Custom Name")
            .description("Checks if an item has a custom name.")
            .icon(Items.NAME_TAG)
            .category(ScriptActionCategory.ITEM_DISPLAY)
            .arg("Item to check", ScriptActionArgumentType.ITEM)
            .action(ctx -> {
                ItemStack item = ctx.value("Item to check").asItem();

                return item.contains(DataComponentTypes.CUSTOM_NAME);
            })),

    IF_ITEM_HAS_LORE(builder -> builder.name("Item Has Lore")
            .description("Checks if an item has lore.")
            .icon(Items.BOOK)
            .category(ScriptActionCategory.ITEM_DISPLAY)
            .arg("Item to check", ScriptActionArgumentType.ITEM)
            .action(ctx -> {
                ItemStack item = ctx.value("Item to check").asItem();

                return !item.get(DataComponentTypes.LORE).lines().isEmpty();
            })),

    IF_ITEM_HAS_DURABILITY(builder -> builder.name("Item Has Durabiity")
            .description("Checks if an item has durability or max durability.")
            .icon(Items.DIAMOND_PICKAXE)
            .category(ScriptActionCategory.ITEM_DURABILITY)
            .arg("Item to check", ScriptActionArgumentType.ITEM)
            .tag(new ScriptActionTag("Required Durability Components",
                    new ScriptActionTag.ScriptActionTagOption("Damage", Items.DIAMOND_PICKAXE),
                    new ScriptActionTag.ScriptActionTagOption("Maximum Damage", Items.NETHERITE_PICKAXE),
                    new ScriptActionTag.ScriptActionTagOption("Both", Items.GOLDEN_PICKAXE)
            ))
            .action(ctx -> {
                ItemStack item = ctx.value("Item to check").asItem();

                boolean damage = item.contains(DataComponentTypes.DAMAGE);
                boolean maxDamage = item.contains(DataComponentTypes.MAX_DAMAGE);

                return switch(ctx.tagValue("Required Durability Components")) {
                    case "Damage" -> damage;
                    case "Maximum Damage" -> maxDamage;
                    case "Both" -> damage && maxDamage;
                    default -> false;
                };
            })),

    IF_ITEM_IS_UNBREAKABLE(builder -> builder.name("Item Is Unbreakable")
            .description("Checks if an item is unbreakable.")
            .icon(Items.CHIPPED_ANVIL)
            .category(ScriptActionCategory.ITEM_DURABILITY)
            .arg("Item to check", ScriptActionArgumentType.ITEM)
            .tag(new ScriptActionTag("Required Hidden State",
                    new ScriptActionTag.ScriptActionTagOption("None", Items.RED_DYE),
                    new ScriptActionTag.ScriptActionTagOption("Shown", Items.GREEN_DYE),
                    new ScriptActionTag.ScriptActionTagOption("Hidden", Items.LIGHT_BLUE_DYE)
            ))
            .action(ctx -> {
                ItemStack item = ctx.value("Item to check").asItem();

                if(!item.contains(DataComponentTypes.UNBREAKABLE)) return false;

                boolean shown = item.get(DataComponentTypes.UNBREAKABLE).showInTooltip();

                return switch(ctx.tagValue("Required Hidden State")) {
                    case "Shown" -> shown;
                    case "Hidden" -> !shown;
                    default -> true;
                };
            })),

    IF_ITEM_CAN_BE_REPAIRED(builder -> builder.name("Item Can Be Repaired")
            .description("Checks if an can be repaired.")
            .icon(Items.ANVIL)
            .category(ScriptActionCategory.ITEM_DURABILITY)
            .arg("Item to check", ScriptActionArgumentType.ITEM)
            .arg("Item to check against", ScriptActionArgumentType.ITEM, b -> b.optional(true))
            .action(ctx -> {
                ItemStack item = ctx.value("Item to check").asItem();

                if(!item.contains(DataComponentTypes.REPAIRABLE)) return false;

                if(ctx.argMap().containsKey("Item to check against")) {
                    ItemStack check = ctx.value("Item to check against").asItem();

                    if(!item.get(DataComponentTypes.REPAIRABLE).matches(check)) {
                        return false;
                    }
                }

                return true;
            })),

    TRUE(builder -> builder.name("True")
            .description("Always executes.\nLiterally the only reason for this is so the\nlegacy deserializer code doesn't have to discard ELSEs\nthat aren't tied to a CONDITION...")
            .icon(Items.LIME_WOOL)
            .category(null)
            .action(ctx -> true));

    private Function<ScriptActionContext, Boolean> action = (ctx) -> false;

    private boolean glow = false;
    private Item icon = Items.STONE;
    private String name = "Unnamed Condition";
    private ScriptActionCategory category = ScriptActionCategory.MISC;
    private final List<String> description = new ArrayList<>();

    private ScriptNoticeLevel noticeLevel = ScriptNoticeLevel.NORMAL;
    private ScriptConditionType alternative = null;
    private final ScriptActionArgumentList arguments = new ScriptActionArgumentList();
    ScriptConditionType(Consumer<ScriptConditionType> builder) {
        description.add("No description provided.");
        builder.accept(this);
    }
    public ItemStack getIcon(String prefix) {
        ItemStack item = new ItemStack(icon);

        item.set(DataComponentTypes.CUSTOM_NAME, Text.literal(prefix + (prefix.isEmpty() ? "" : " ") + name)
            .fillStyle(Style.EMPTY
                .withColor(Formatting.WHITE)
                .withItalic(false)));

        List<Text> lore = new ArrayList<>(getLore());

        item.set(DataComponentTypes.LORE, new LoreComponent(lore));

        item.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, glow);

        return item;
    }

    public String getName() {
        return name;
    }

    public List<Text> getLore() {
        List<Text> lore = new ArrayList<>();

        for (String descriptionLine: description) {
            lore.add(Text.literal(descriptionLine)
                    .fillStyle(Style.EMPTY
                            .withColor(Formatting.GRAY)
                            .withItalic(false)));
        }

        lore.add(Text.literal(""));

        for (ScriptActionArgument arg : arguments) {
            lore.addAll(arg.text());
        }

        for (ScriptActionTag tag : arguments.getTags()) {
            lore.addAll(tag.text());
        }

        return lore;
    }

    public ScriptNotice getNotice(String prefix) {
        if(alternative != null) {
            return new ScriptNotice(noticeLevel, "Consider using '" + prefix + alternative.getName() + "' instead.");
        }

        return new ScriptNotice(noticeLevel);
    }

    public ScriptActionCategory getCategory() {
        return category;
    }

    private ScriptConditionType action(Function<ScriptActionContext, Boolean> action) {
        this.action = action;
        return this;
    }

    private ScriptConditionType icon(Item icon, boolean glow) {
        this.icon = icon;
        this.glow = glow;
        return this;
    }

    private ScriptConditionType icon(Item icon) {
        icon(icon, false);
        return this;
    }

    private ScriptConditionType name(String name) {
        this.name = name;
        return this;
    }

    private ScriptConditionType category(ScriptActionCategory category) {
        this.category = category;
        return this;
    }

    private ScriptConditionType description(String description) {
        this.description.clear();
        this.description.addAll(Arrays.asList(description.split("\n", -1)));
        return this;
    }

    public ScriptConditionType arg(String name, ScriptActionArgumentType type, Consumer<ScriptActionArgument> builder) {
        ScriptActionArgument arg = new ScriptActionArgument(name, type);
        builder.accept(arg);
        arguments.add(arg);
        return this;
    }

    public ScriptConditionType arg(String name, ScriptActionArgumentType type) {
        return arg(name, type, (arg) -> {
        });
    }

    public ScriptConditionType tag(ScriptActionTag tag) {
        arguments.getTags().add(tag);
        return this;
    }

    public ScriptConditionType deprecate() {
        noticeLevel = ScriptNoticeLevel.DEPRECATION;
        return this;
    }

    public ScriptConditionType removeUsability() {
        noticeLevel = ScriptNoticeLevel.UNUSABILITY;
        return this;
    }

    public ScriptConditionType proposeAlternative(ScriptConditionType alternative) {
        this.alternative = alternative;

        return this;
    }

    public boolean run(ScriptActionContext ctx) {
        try
        {
            arguments.getArgMap(ctx);
            return action.apply(ctx);
        }
        catch(IllegalArgumentException e)
        {
            ChatUtil.error("Invalid arguments for " + name + ".");
            return false;
        }
    }

    public ItemStack getIcon() {
        return getIcon("");
    }

    public ScriptActionArgumentList getArgumentList() {
        return arguments;
    }
}
