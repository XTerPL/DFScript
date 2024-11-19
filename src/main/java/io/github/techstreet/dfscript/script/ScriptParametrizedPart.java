package io.github.techstreet.dfscript.script;

import io.github.techstreet.dfscript.script.argument.ScriptArgument;
import io.github.techstreet.dfscript.script.argument.ScriptClientValueArgument;
import io.github.techstreet.dfscript.script.argument.ScriptConfigArgument;
import io.github.techstreet.dfscript.script.argument.ScriptFunctionArgument;
import io.github.techstreet.dfscript.script.event.ScriptHeader;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class ScriptParametrizedPart extends ScriptPart implements ScriptRunnable {

    List<ScriptArgument> arguments;

    public ScriptParametrizedPart(List<ScriptArgument> arguments) {
        this.arguments = arguments;
    }

    public List<ScriptArgument> getArguments() {
        return arguments;
    }

    @Override
    public ArrayList<ScriptNotice> getNotices() {
        var notices = super.getNotices();

        if(blocked()) {
            notices.add(new ScriptNotice(ScriptNoticeLevel.UNUSABLE_ARGUMENTS));
        }

        return notices;
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
    }

    public void replaceClientValue(ScriptClientValueArgument oldClientValue, ScriptClientValueArgument newClientValue) {
        for(int i = 0; i < getArguments().size(); i++) {
            if (getArguments().get(i) instanceof ScriptClientValueArgument clientValue) {
                if(clientValue == oldClientValue)
                {
                    arguments.set(i, newClientValue);
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
    }

    public boolean blocked() {
        for(ScriptArgument arg : arguments) {
            if(arg.getNotice().disablesScript()) {
                return true;
            }
        }
        return false;
    }
}
