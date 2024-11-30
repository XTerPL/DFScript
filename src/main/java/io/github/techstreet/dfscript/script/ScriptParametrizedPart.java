package io.github.techstreet.dfscript.script;

import io.github.techstreet.dfscript.script.action.ScriptActionArgumentList;
import io.github.techstreet.dfscript.script.action.ScriptActionTag;
import io.github.techstreet.dfscript.script.argument.*;
import io.github.techstreet.dfscript.script.event.ScriptHeader;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class ScriptParametrizedPart extends ScriptPart implements ScriptRunnable {

    List<ScriptArgument> arguments;
    List<ScriptTag> tags;

    public ScriptParametrizedPart(List<ScriptArgument> arguments, List<ScriptTag> tags) {
        this.arguments = arguments;
        this.tags = tags;
    }

    public List<ScriptArgument> getArguments() {
        return arguments;
    }

    public List<ScriptTag> getTags() {
        return tags;
    }

    @Override
    public ArrayList<ScriptNotice> getNotices() {
        var notices = super.getNotices();

        if(blocked()) {
            notices.add(new ScriptNotice(ScriptNoticeLevel.UNUSABLE_ARGUMENTS));
        }

        return notices;
    }

    public abstract ScriptActionArgumentList getActionArgumentList();

    public void updateTags() {
        ScriptActionArgumentList argList = getActionArgumentList();

        int i = 0;
        while(i < getTags().size()) {
            ScriptTag tag = getTags().get(i);
            ScriptActionTag actionTag = tag.getTag(argList);
            if(actionTag == null) {
                getTags().remove(i);
                continue;
            }
            i++;
        }

        for(ScriptActionTag actionTag : argList.getTags()) {
            boolean has = false;
            for(ScriptTag tag : getTags()) {
                if(tag.getTag(argList) == actionTag) {
                    has = true;
                    break;
                }
            }
            if(!has) {
                tags.add(new ScriptTag(actionTag));
            }
        }
    }

    public void updateScriptReferences(Script script, ScriptHeader header) {
        for(ScriptArgument arg : getArguments()) {
            if (arg instanceof ScriptConfigArgument carg) {
                carg.setScript(script);
            }
            if (arg instanceof ScriptFunctionArgument farg) {
                farg.setHeader(header);
            }
        }
        for(ScriptTag tag : getTags()) {
            if(tag.getArgument() == null) {
                continue;
            }

            if (tag.getArgument() instanceof ScriptConfigArgument carg) {
                carg.setScript(script);
            }
            if (tag.getArgument() instanceof ScriptFunctionArgument farg) {
                farg.setHeader(header);
            }
        }
    }

    public void updateConfigArguments(String oldOption, String newOption) {
        for(ScriptArgument arg : getArguments()) {
            if (arg instanceof ScriptConfigArgument carg) {
                if(Objects.equals(carg.getName(), oldOption))
                {
                    carg.setOption(newOption);
                }
            }
        }
        for(ScriptTag tag : getTags()) {
            if(tag.getArgument() == null) {
                continue;
            }

            if (tag.getArgument() instanceof ScriptConfigArgument carg) {
                if(Objects.equals(carg.getName(), oldOption))
                {
                    carg.setOption(newOption);
                }
            }
        }
    }

    public void removeConfigArguments(String option) {
        int index = 0;

        List<ScriptArgument> argList = getArguments();

        while(index < argList.size()) {
            if (argList.get(index) instanceof ScriptConfigArgument carg) {
                if(Objects.equals(carg.getName(), option))
                {
                    argList.remove(index);
                    continue;
                }
            }
            index++;
        }

        for(ScriptTag tag : getTags()) {
            if(tag.getArgument() == null) {
                continue;
            }

            if (tag.getArgument() instanceof ScriptConfigArgument carg) {
                if(Objects.equals(carg.getName(), option))
                {
                    tag.setArgument(null);
                }
            }
        }
    }

    public void replaceFunctionArgument(String oldArg, String newArg) {
        for(ScriptArgument arg : getArguments()) {
            if (arg instanceof ScriptFunctionArgument carg) {
                if(Objects.equals(carg.getName(), oldArg))
                {
                    carg.setFunctionArg(newArg);
                }
            }
        }

        for(ScriptTag tag : getTags()) {
            if(tag.getArgument() == null) {
                continue;
            }

            if (tag.getArgument() instanceof ScriptFunctionArgument carg) {
                if(Objects.equals(carg.getName(), oldArg))
                {
                    carg.setFunctionArg(newArg);
                }
            }
        }
    }

    public void replaceClientValue(ScriptClientValueType oldClientValue, ScriptClientValueType newClientValue) {
        for(int i = 0; i < getArguments().size(); i++) {
            if (getArguments().get(i) instanceof ScriptClientValueArgument clientValue) {
                if(clientValue.getClientValueType() == oldClientValue)
                {
                    clientValue.setClientValueType(newClientValue);
                }
            }
        }

        for(ScriptTag tag : getTags()) {
            if(tag.getArgument() == null) {
                continue;
            }

            if (tag.getArgument() instanceof ScriptClientValueArgument clientValue) {
                if(clientValue.getClientValueType() == oldClientValue)
                {
                    clientValue.setClientValueType(newClientValue);
                }
            }
        }
    }

    public void removeFunctionArgument(String arg) {
        int index = 0;

        List<ScriptArgument> argList = getArguments();

        while(index < argList.size()) {
            if (argList.get(index) instanceof ScriptFunctionArgument carg) {
                if(Objects.equals(carg.getName(), arg))
                {
                    argList.remove(index);
                    continue;
                }
            }
            index++;
        }

        for(ScriptTag tag : getTags()) {
            if(tag.getArgument() == null) {
                continue;
            }

            if (tag.getArgument() instanceof ScriptFunctionArgument carg) {
                if(Objects.equals(carg.getName(), arg))
                {
                    tag.setArgument(null);
                }
            }
        }
    }

    public boolean blocked() {
        for(ScriptArgument arg : arguments) {
            if(arg.getNotice().disablesScript()) {
                return true;
            }
        }
        for(ScriptTag tag : tags) {
            if(tag.getArgument() != null) {
                if(tag.getArgument().getNotice().disablesScript()) {
                    return true;
                }
            }
        }
        return false;
    }

    public void setTag(ScriptActionTag tag, String tagValue) {
        for(ScriptTag atag : tags) {
            if(Objects.equals(atag.getTagName(), tag.getName())) {
                atag.select(tagValue);
                return;
            }
        }

        tags.add(new ScriptTag(tag, tagValue, null));
    }

    public void setClientValueMode(ScriptClientValueType type, String mode) {
        for(int i = 0; i < getArguments().size(); i++) {
            if (getArguments().get(i) instanceof ScriptClientValueArgument clientValue) {
                if(clientValue.getClientValueType() == type)
                {
                    clientValue.setMode(mode);
                }
            }
        }

        for(ScriptTag tag : getTags()) {
            if(tag.getArgument() == null) {
                continue;
            }

            if (tag.getArgument() instanceof ScriptClientValueArgument clientValue) {
                if(clientValue.getClientValueType() == type)
                {
                    clientValue.setMode(mode);
                }
            }
        }
    }
}
