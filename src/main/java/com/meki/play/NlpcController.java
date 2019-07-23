/*
 * Copyright (C) 2019 Baidu, Inc. All Rights Reserved.
 */
package com.meki.play;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * NLPC查询控制器
 * Created by xujinchao on 18/1/22.
 */
@Controller
public class NlpcController {

    private static final Logger LOG = LoggerFactory.getLogger(NlpcController.class);

    @RequestMapping("/nlpc/get")
    public void getNlpcParseResult(HttpServletRequest request, HttpServletResponse response) {

        String question = request.getParameter("question");
        String answer = request.getParameter("answer");
        String type = request.getParameter("type");
        if (StringUtils.isBlank(answer) || !NumberUtils.isNumber(type)) {
            AbstractJsonActionController.outJson(response, JsonResultUtils.getParamFailedJsonResult());
            return;
        }

        LOG.info("params:{},{},{}", question, answer, type);
        try {

            String result = NlpUtil.getNlpcLexTagNew(answer, Integer.valueOf(type));

            AbstractJsonActionController.outJson(response, JsonResultUtils.getSuccessJsonResult(result));
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            AbstractJsonActionController.outJson(response, JsonResultUtils.getParamFailedJsonResult());
        }

    }

}
