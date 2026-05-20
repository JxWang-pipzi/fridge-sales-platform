package com.fridge.sales.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fridge.sales.annotation.Log;
import com.fridge.sales.common.Result;
import com.fridge.sales.dto.ForgotPasswordDTO;
import com.fridge.sales.dto.PasswordUpdateDTO;
import com.fridge.sales.dto.UserLoginDTO;
import com.fridge.sales.dto.UserRegisterDTO;
import com.fridge.sales.dto.UserUpdateDTO;
import com.fridge.sales.dto.UserVO;
import com.fridge.sales.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import static com.fridge.sales.annotation.Log.BusinessType.*;

/**
 * 用户控制器
 */
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Log(title = "用户管理", businessType = INSERT)
    @PostMapping("/user/register")
    public Result<UserVO> register(@Valid @RequestBody UserRegisterDTO registerDTO) {
        UserVO userVO = userService.register(registerDTO);
        return Result.success(userVO);
    }

    @Log(title = "用户管理", businessType = OTHER)
    @PostMapping("/user/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody UserLoginDTO loginDTO) {
        Map<String, Object> result = userService.loginWithUserInfo(loginDTO);
        return Result.success(result);
    }

    @GetMapping("/user/info")
    public Result<UserVO> getUserInfo(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        UserVO userVO = userService.getById(userId);
        return Result.success(userVO);
    }

    @Log(title = "用户管理", businessType = UPDATE)
    @PutMapping("/user/info")
    public Result<UserVO> updateUserInfo(HttpServletRequest request, @Valid @RequestBody UserUpdateDTO updateDTO) {
        Long userId = (Long) request.getAttribute("userId");
        UserVO userVO = userService.updateInfo(userId, updateDTO);
        return Result.success(userVO);
    }

    @Log(title = "用户管理", businessType = UPDATE)
    @PutMapping("/user/password")
    public Result<Void> updatePassword(HttpServletRequest request, @Valid @RequestBody PasswordUpdateDTO passwordUpdateDTO) {
        Long userId = (Long) request.getAttribute("userId");
        userService.updatePassword(userId, passwordUpdateDTO);
        return Result.success();
    }

    /**
     * 忘记密码
     */
    @PostMapping("/user/forgot-password")
    public Result<Void> forgotPassword(@Valid @RequestBody ForgotPasswordDTO forgotPasswordDTO) {
        userService.forgotPassword(forgotPasswordDTO);
        return Result.success();
    }

    /**
     * 发送验证码
     */
    @PostMapping("/user/send-code")
    public Result<Void> sendCode(@RequestBody Map<String, String> params) {
        String email = params.get("email");
        userService.sendVerificationCode(email);
        return Result.success();
    }
}
