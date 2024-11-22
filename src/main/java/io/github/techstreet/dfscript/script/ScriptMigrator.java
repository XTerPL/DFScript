package io.github.techstreet.dfscript.script;

import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.script.action.ScriptActionType;
import io.github.techstreet.dfscript.script.argument.ScriptClientValueArgument;
import io.github.techstreet.dfscript.script.options.ScriptOptionEnum;

public class ScriptMigrator {
    public static void migrate(Script script) {
        int previousVer = script.getVersion();

        if (script.getVersion() == 0) {
            script.setServer("None");
            script.setOwner(DFScript.PLAYER_UUID);
            script.setVersion(1);
        }

        if (script.getVersion() == 1) {
            script.setDescription("N/A");
            script.setVersion(2);
        }

        if (script.getVersion() == 2) {
            script.replaceAction(ScriptActionType.TEXT_SUBTEXT, ScriptActionType.TEXT_SUBTEXT_V1);
            script.setVersion(3);
        }

        if (script.getVersion() == 3) {
            script.replaceAction(ScriptActionType.SPLIT_TEXT, ScriptActionType.REGEX_SPLIT_TEXT);
            script.replaceAction(ScriptActionType.RANDOM_NUMBER, ScriptActionType.RANDOM_DOUBLE);

            script.setVersion(4);
        }

        if (script.getVersion() == 4) {
            script.setVersion(5);
        }

        if (script.getVersion() == 5) {
            script.replaceAction(ScriptActionType.DISPLAY_TITLE, ScriptActionType.DISPLAY_TITLE_OLD);
            script.replaceAction(ScriptActionType.PLAY_SOUND, ScriptActionType.PLAY_SOUND_OLD);
            script.replaceAction(ScriptActionType.READ_FILE, ScriptActionType.READ_FILE_OLD);
            script.replaceAction(ScriptActionType.WRITE_FILE, ScriptActionType.WRITE_FILE_OLD);

            script.setVersion(6);
        }

        if(script.getVersion() == 6) {
            script.setVersion(7);
        }

        if(script.getVersion() == 7) {
            script.replaceAction(ScriptActionType.ADD_MENU_ITEM, ScriptActionType.ADD_MENU_ITEM_OLD);
            script.replaceAction(ScriptActionType.GIVE_ITEM, ScriptActionType.GIVE_ITEM_OLD);
            script.replaceAction(ScriptActionType.SET_HOTBAR_ITEM, ScriptActionType.SET_HOTBAR_ITEM_OLD);

            script.replaceClientValue(ScriptClientValueArgument.ARMOR, ScriptClientValueArgument.ARMOR_OLD);
            script.replaceClientValue(ScriptClientValueArgument.HOTBAR_ITEMS, ScriptClientValueArgument.HOTBAR_ITEMS_OLD);
            script.replaceClientValue(ScriptClientValueArgument.MAIN_INVENTORY, ScriptClientValueArgument.MAIN_INVENTORY_OLD);
            script.replaceClientValue(ScriptClientValueArgument.FULL_INVENTORY, ScriptClientValueArgument.FULL_INVENTORY_OLD);
            script.replaceClientValue(ScriptClientValueArgument.MAIN_HAND_ITEM, ScriptClientValueArgument.MAIN_HAND_ITEM_OLD);
            script.replaceClientValue(ScriptClientValueArgument.OFF_HAND_ITEM, ScriptClientValueArgument.OFF_HAND_ITEM_OLD);

            script.setVersion(8);
        }

        if (previousVer != script.getVersion()) {
            ScriptManager.LOGGER.info("Migrated script '" + script.getName() + "' from version " + previousVer + " to version " + script.getVersion() + "!");
            ScriptManager.getInstance().saveScript(script);
        }
    }
}
