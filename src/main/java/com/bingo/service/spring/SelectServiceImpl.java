package com.bingo.service.spring;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.bingo.service.SelectService;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SelectServiceImpl implements SelectService {

    public final static String KEY = "sentinel";

    @Override
    public Map select1() {
        Map map = new HashMap();
        map.put("result1", "select1 is success");
        return map;
    }

    @PostConstruct
    public void initFlowQpsRule() {
        List<FlowRule> rules = new ArrayList<FlowRule>();
        FlowRule rule1 = new FlowRule();
        rule1.setResource(KEY);
        // QPS控制在2以内
        rule1.setCount(2);
        // QPS限流
        rule1.setGrade(RuleConstant.FLOW_GRADE_QPS);
        rule1.setLimitApp("default");
        rules.add(rule1);
        FlowRuleManager.loadRules(rules);
    }

    @SentinelResource(value = KEY, blockHandler = "handleFlowQpsException",
            fallback = "queryOrderInfo2Fallback")
    @Override
    public Map select2() {
        Map map = new HashMap();
        map.put("result2", "select2 is success");
        if (map.get("a") == null) {
            throw new NullPointerException();
        }
        return map;
    }

    /**
     * 接口抛出限流或降级时的处理逻辑
     * 用来处理Sentinel 限流/熔断等错误；
     * 注意: 方法参数、返回值要与原函数保持一致
     *
     * @return
     */
    public Map handleFlowQpsException(BlockException e) {
        Map map = new HashMap();
        map.put("result2", "select2 is limit");
        return map;
    }

    /**
     * 接口运行时抛出的异常提供fallback处理
     * 用来处理接口中业务代码所有异常(如业务代码异常、sentinel限流熔断异常等)
     * <p>
     * 注意: 方法参数、返回值要与原函数保持一致
     *
     * @return
     */
    public Map queryOrderInfo2Fallback(Throwable e) {
        Map map = new HashMap();
        map.put("result2", "select2 is runtime exception");
        return map;
    }

}
