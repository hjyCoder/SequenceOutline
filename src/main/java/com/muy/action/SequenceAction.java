package com.muy.action;

import com.muy.domain.bean.invoketree.TreeInvokeModel;
import com.muy.utils.ClipboardUtils;
import com.muy.utils.JacksonUtils;

/**
 * @Author jiyanghuang
 * @Date 2022/6/29 22:47
 */
public class SequenceAction extends SequenceActionAbstract {

    @Override
    protected void genAfter(TreeInvokeModel root) {
        ClipboardUtils.fillStringToClip(JacksonUtils.toJSONString(root));
    }
}
