package io.github.techstreet.dfscript.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.script.action.ScriptActionTag;
import io.github.techstreet.dfscript.util.chat.ChatType;
import net.kyori.adventure.platform.modcommon.MinecraftClientAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.item.Items;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

public class ComponentUtil {

    public static ScriptActionTag componentMode = new ScriptActionTag(
            "Component Mode",
            new ScriptActionTag.ScriptActionTagOption("MiniMessage", Items.KNOWLEDGE_BOOK, ""),
            new ScriptActionTag.ScriptActionTagOption("Ampersand Color Codes", Items.TORCH, ""),
            new ScriptActionTag.ScriptActionTagOption("Section Sign Color Codes", Items.REDSTONE_TORCH, ""),
            new ScriptActionTag.ScriptActionTagOption("Hex Color", Items.SOUL_TORCH, ""),
            new ScriptActionTag.ScriptActionTagOption("Plain", Items.STRING, "")
    );

    public static Text fromString(String message, String componentMode) {
        return switch(componentMode) {
            case "MiniMessage" -> {
                Component adventureComp = MiniMessage.miniMessage().deserialize(message);
                yield MinecraftClientAudiences.of().asNative(adventureComp);
            }
            case "Ampersand Color Codes", "Section Sign Color Codes" -> {
                String colorCodeChar = switch (componentMode) {
                    case "Ampersand Color Codes" -> "&";
                    case "Section Sign Color Codes" -> "§";
                    default -> throw new IllegalArgumentException("Unexpected Component Mode: " + componentMode);
                };
                MutableText result = Text.literal("");

                try {
                    Regex pattern = Regex.of("("+colorCodeChar+"[a-f0-9lonmkrA-FLONMRK]|"+colorCodeChar+
                                                    "x("+colorCodeChar+"[a-f0-9A-F]){6})");
                    Matcher matcher = pattern.getMatcher(message);

                    Style s = Style.EMPTY;

                    int lastIndex = 0;
                    while (matcher.find()) {
                        int start = matcher.start();
                        String text = message.substring(lastIndex, start);
                        if (!text.isEmpty()) {
                            MutableText t = Text.literal(text);
                            t.setStyle(s);
                            result.append(t);
                        }
                        String col = matcher.group();

                        if (col.length() == 2) {
                            s = s.withFormatting(Formatting.byCode(col.charAt(1)));
                        } else {
                            s = Style.EMPTY.withColor(
                                    TextColor.parse(
                                            "#" + col.replaceAll(colorCodeChar, "").substring(1)
                                    ).getOrThrow((h) -> {throw new IllegalArgumentException(h);}));
                        }
                        lastIndex = matcher.end();
                    }
                    String text = message.substring(lastIndex);
                    if (!text.isEmpty()) {
                        MutableText t = Text.literal(text);
                        t.setStyle(s);
                        result.append(t);
                    }
                } catch (Exception err) {
                    err.printStackTrace();
                    yield Text.literal("DFScript Text Error");
                }

                yield result;
            }
            case "Hex Color" -> {
                MutableText result = Text.literal("");

                try {
                    Regex pattern = Regex.of("#[a-f0-9A-F]{6}");
                    Matcher matcher = pattern.getMatcher(message);

                    Style s = Style.EMPTY;

                    int lastIndex = 0;
                    while (matcher.find()) {
                        int start = matcher.start();
                        String text = message.substring(lastIndex, start);
                        if (!text.isEmpty()) {
                            MutableText t = Text.literal(text);
                            t.setStyle(s);
                            result.append(t);
                        }
                        String col = matcher.group();

                        s = Style.EMPTY.withColor(
                                TextColor.parse(col).getOrThrow((h) -> {throw new IllegalArgumentException(h);}));
                        lastIndex = matcher.end();
                    }
                    String text = message.substring(lastIndex);
                    if (!text.isEmpty()) {
                        MutableText t = Text.literal(text);
                        t.setStyle(s);
                        result.append(t);
                    }
                } catch (Exception err) {
                    err.printStackTrace();
                    yield Text.literal("DFScript Text Error");
                }

                yield result;
            }
            case "Plain" -> Text.literal(message);
            default -> throw new IllegalArgumentException("Unknown Component Mode: " + componentMode);
        };
    }

    public static String toFormattedString(Text message, String componentMode) {
        return switch(componentMode) {
            case "MiniMessage" -> {
                Component adventureComp = MinecraftClientAudiences.of().asAdventure(message);
                yield MiniMessage.miniMessage().serialize(adventureComp);
            }
            case "Ampersand Color Codes", "Section Sign Color Codes" -> {
                String colorCodeChar = switch (componentMode) {
                    case "Ampersand Color Codes" -> "&";
                    case "Section Sign Color Codes" -> "§";
                    default -> throw new IllegalArgumentException("Unexpected Component Mode: " + componentMode);
                };

                StringBuilder result = new StringBuilder();

                parseComponentsToColorCodes(result, message, colorCodeChar, null, Style.EMPTY);

                yield result.toString();
            }
            case "Hex Color" -> {
                StringBuilder result = new StringBuilder();

                parseComponentsToHexColor(result, message, null, null);

                yield result.toString();
            }
            case "Plain" -> message.getString();
            default -> throw new IllegalArgumentException("Unknown Component Mode: " + componentMode);
        };
    }

    private static Style parseComponentsToColorCodes(StringBuilder result, Text message, String colorCodeChar, Style parent, Style previous) {
        Style style = message.getStyle();

        if(parent != null) {
            style = style.withParent(parent);
        }

        if(!style.equals(previous)) {
            String format = "";

            if (style.getColor() != null) {
                format += colorCodeChar + "x" + colorCodeChar + String.join(colorCodeChar, String.format("%06X", style.getColor().getRgb()).split(""));
            }

            if (style.isBold()) {
                format += colorCodeChar+"l";
            }
            if (style.isItalic()) {
                format += colorCodeChar+"o";
            }
            if (style.isUnderlined()) {
                format += colorCodeChar+"n";
            }
            if (style.isStrikethrough()) {
                format += colorCodeChar+"m";
            }
            if (style.isObfuscated()) {
                format += colorCodeChar+"k";
            }

            result.append(format);
        }

        result.append(message.copyContentOnly().getString());

        previous = style;

        if(!message.getSiblings().isEmpty()) {
            for (Text sibling : message.getSiblings()) {
                previous = parseComponentsToColorCodes(result, sibling, colorCodeChar, style, previous);
            }
        }

        return previous;
    }

    private static TextColor parseComponentsToHexColor(StringBuilder result, Text message, TextColor parent, TextColor previous) {
        Style style = message.getStyle();
        TextColor color = style.getColor();

        if(parent != null && color == null) {
            color = parent;
        }

        if (color != null) {
            if(!color.equals(previous)) {
                result.append("#").append(String.format("%06X", color.getRgb()));
            }
        }

        result.append(message.copyContentOnly().getString());

        previous = color;

        if(!message.getSiblings().isEmpty()) {
            for (Text sibling : message.getSiblings()) {
                previous = parseComponentsToHexColor(result, sibling, color, previous);
            }
        }

        return previous;
    }

    public static Text fromString(String message) {
        return fromString(message, "Section Sign Color Codes");
    }

    public static String toFormattedString(Text message) {
        return toFormattedString(message, "Section Sign Color Codes");
    }

    public static String sectionSignsToAnds(String msg) {
        StringBuilder result = new StringBuilder();

        Pattern p = Regex.of("(§[a-f0-9lonmkrA-FLONMRK]|§x(§[a-f0-9A-F]){6})").getPattern();
        Matcher m = p.matcher(msg);

        int lastIndex = 0;
        while (m.find()) {
            int start = m.start();
            String between = msg.substring(lastIndex, start);
            if (between.length() != 0) {
                result.append(between);
            }
            String replace = m.group().replaceAll("§", "&");
            result.append(replace);
            lastIndex = m.end();
        }

        String between = msg.substring(lastIndex);
        if (between.length() != 0) {
            result.append(between);
        }

        return result.toString();
    }

    public static String andsToSectionSigns(String msg) {
        StringBuilder result = new StringBuilder();

        Pattern p = Regex.of("(&[a-f0-9lonmkrA-FLONMRK]|&x(&[a-f0-9A-F]){6})").getPattern();
        Matcher m = p.matcher(msg);

        int lastIndex = 0;
        while (m.find()) {
            int start = m.start();
            String between = msg.substring(lastIndex, start);
            if (between.length() != 0) {
                result.append(between);
            }
            String replace = m.group().replaceAll("&", "§");
            result.append(replace);
            lastIndex = m.end();
        }

        String between = msg.substring(lastIndex);
        if (between.length() != 0) {
            result.append(between);
        }

        return result.toString();
    }
}
