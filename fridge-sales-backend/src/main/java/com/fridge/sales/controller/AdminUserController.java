package com.fridge.sales.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fridge.sales.annotation.Log;
import com.fridge.sales.common.Result;
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
 * 管理员用户控制器
 */
@RestController
@RequestMapping("/admin")
public class AdminUserController {

    @Autowired
    private UserService userService;

    @GetMapping("/user/list")
    public Result<Page<UserVO>> listUsers(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Integer status) {
        Page<UserVO> userPage = userService.listUsers(current, size, username, email, phone, role, status);
        return Result.success(userPage);
    }

    @Log(title = "用户管理", businessType = UPDATE)
    @PutMapping("/user/status")
    public Result<Void> updateStatus(@RequestBody Map<String, Object> params) {
        Long id = Long.valueOf(params.get("id").toString());
        Integer status = Integer.valueOf(params.get("status").toString());
        userService.updateStatus(id, status);
        return Result.success();
    }

    @GetMapping("/user/{id}")
    public Result<UserVO> getUserDetail(@PathVariable Long id) {
        UserVO userVO = userService.getById(id);
        return Result.success(userVO);
    }

    @Log(title = "用户管理", businessType = UPDATE)
    @PutMapping("/user/{id}")
    public Result<Void> updateUser(@PathVariable Long id, @RequestBody UserUpdateDTO updateDTO) {
        userService.updateInfo(id, updateDTO);
        return Result.success();
    }

    @Log(title = "用户管理", businessType = UPDATE)
    @PutMapping("/user/reset-password/{id}")
    public Result<Void> resetPassword(@PathVariable Long id) {
        userService.resetPassword(id);
        return Result.success();
    }
}
