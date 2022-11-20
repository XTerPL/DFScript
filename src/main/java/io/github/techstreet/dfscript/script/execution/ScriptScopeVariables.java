package io.github.techstreet.dfscript.script.execution;

import io.github.techstreet.dfscript.script.function.ScriptFunction;

import java.util.function.Consumer;

public class ScriptScopeVariables {

    Runnable preTask;
    Consumer<ScriptActionContext> condition;
    ScriptActionContext ctx;

    ScriptFunction function = null;

    ScriptScopeVariables(Runnable run, Consumer<ScriptActionContext> cond, ScriptActionContext ctx) {
        preTask = run;
        condition = cond;
        this.ctx = ctx;
    }

    public ScriptScopeVariables function(ScriptFunction scriptFunction) {
        function = scriptFunction;
        return this;
    }
}
