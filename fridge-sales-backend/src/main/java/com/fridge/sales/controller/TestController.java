package com.fridge.sales.controller;

import com.fridge.sales.common.Result;
import com.fridge.sales.dto.SalesTrendVO;
import com.fridge.sales.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 测试控制器
 */
@RestController
public class TestController {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private StatisticsService statisticsService;

    /**
     * 测试密码加密
     */
    @GetMapping("/test/encode")
    public Map<String, Object> encodePassword(@RequestParam String password) {
        Map<String, Object> result = new HashMap<>();
        result.put("original", password);
        result.put("encoded", passwordEncoder.encode(password));
        result.put("matches", passwordEncoder.matches(password, passwordEncoder.encode(password)));
        return result;
    }

    /**
     * 测试密码匹配
     */
    @GetMapping("/test/matches")
    public Map<String, Object> matchesPassword(@RequestParam String rawPassword, @RequestParam String encodedPassword) {
        Map<String, Object> result = new HashMap<>();
        result.put("rawPassword", rawPassword);
        result.put("encodedPassword", encodedPassword);
        result.put("matches", passwordEncoder.matches(rawPassword, encodedPassword));
        return result;
    }

    /**
     * 测试销售趋势API（无需登录）
     */
    @GetMapping("/test/statistics/trend")
    public Result<List<SalesTrendVO>> testSalesTrend(@RequestParam(defaultValue = "day") String type) {
        List<SalesTrendVO> trend = statisticsService.getSalesTrend(type);
        return Result.success(trend);
    }
}
