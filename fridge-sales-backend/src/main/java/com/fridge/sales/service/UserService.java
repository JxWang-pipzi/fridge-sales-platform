package com.fridge.sales.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fridge.sales.dto.ForgotPasswordDTO;
import com.fridge.sales.dto.PasswordUpdateDTO;
import com.fridge.sales.dto.UserLoginDTO;
import com.fridge.sales.dto.UserRegisterDTO;
import com.fridge.sales.dto.UserUpdateDTO;
import com.fridge.sales.dto.UserVO;

import java.util.Map;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 用户注册
     *
     * @param registerDTO 注册请求DTO
     * @return 用户视图对象
     */
    UserVO register(UserRegisterDTO registerDTO);

    /**
     * 用户登录
     *
     * @param loginDTO 登录请求DTO
     * @return JWT Token
     */
    String login(UserLoginDTO loginDTO);

    /**
     * 用户登录（返回token和用户信息）
     *
     * @param loginDTO 登录请求DTO
     * @return 包含token和用户信息的Map
     */
    Map<String, Object> loginWithUserInfo(UserLoginDTO loginDTO);

    /**
     * 根据ID查询用户
     *
     * @param id 用户ID
     * @return 用户视图对象
     */
    UserVO getById(Long id);

    /**
     * 更新用户信息
     *
     * @param id 用户ID
     * @param updateDTO 更新请求DTO
     * @return 用户视图对象
     */
    UserVO updateInfo(Long id, UserUpdateDTO updateDTO);

    /**
     * 管理员查询用户列表
     *
     * @param page 页码
     * @param size 每页数量
     * @param username 用户名（模糊查询，可选）
     * @param email 邮箱（模糊查询，可选）
     * @param phone 手机号（模糊查询，可选）
     * @param role 角色（精确查询，可选）
     * @param status 状态（可选）
     * @return 分页结果
     */
    Page<UserVO> listUsers(Integer page, Integer size, String username, String email, String phone, String role, Integer status);

    /**
     * 管理员更新用户状态
     *
     * @param id 用户ID
     * @param status 状态（0-禁用，1-正常）
     */
    void updateStatus(Long id, Integer status);

    /**
     * 管理员重置用户密码
     *
     * @param id 用户ID
     */
    void resetPassword(Long id);

    /**
     * 用户修改密码
     *
     * @param userId 用户ID
     * @param passwordUpdateDTO 修改密码请求DTO
     */
    void updatePassword(Long userId, PasswordUpdateDTO passwordUpdateDTO);

    /**
     * 忘记密码
     *
     * @param forgotPasswordDTO 忘记密码请求DTO
     */
    void forgotPassword(ForgotPasswordDTO forgotPasswordDTO);

    /**
     * 获取用户角色（用于缓存）
     *
     * @param userId 用户ID
     * @return 角色名称
     */
    String getUserRole(Long userId);

    /**
     * 发送验证码
     *
     * @param email 邮箱地址
     */
    void sendVerificationCode(String email);
}
