package com.muy.constant;

import lombok.Data;

/**
 * @Author jiyanghuang
 * @Date 2022/6/24 00:58
 */
public interface SequenceConstant {
    String ANONYMOUS_CLASS_NAME = "Anonymous";
    String CONSTRUCTOR_METHOD_NAME = "new";
    String Lambda_Invoke = "λ→";
    String TOP_LEVEL_FUN = "Global";

    public static final String PLUGIN_ID = "SequenceOutline";
    public static final String RUNNER_ID = "Runner-so-Agent-Project";
    public static final String DEBUGGER_ID = "Debugger-so-Agent-Project";

    public static final String DEBUGGER_ACTION_ID = "Debugger-Action-so-Agent-Project";
    public static final String RUNNER_ACTION_ID = "Runner-Action-so-Agent-Project";

    public static final String RUNNER_DESC = "Run with so-agent runner";
    public static final String DEBUGGER_DESC = "Run with so-agent debugger";

    public static final String SR_RUN_ENV = "unnamedEnv";

    public static final String SR_APP_NAME = "unnamedAppName";

    public static final String BODY_JSON = "bodyJson";

    /**
     * 临时存放 query_key
     */
    public static final String PARAM_QUERY_KEY = "param_query_key";

    /**
     * 临时存放 body_key
     */
    public static final String PARAM_BODY_KEY = "param_body_key";
}
