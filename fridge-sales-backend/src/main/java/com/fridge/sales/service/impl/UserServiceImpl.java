package com.fridge.sales.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fridge.sales.common.ResultCode;
import com.fridge.sales.common.exception.BusinessException;
import com.fridge.sales.dto.ForgotPasswordDTO;
import com.fridge.sales.dto.PasswordUpdateDTO;
import com.fridge.sales.dto.UserLoginDTO;
import com.fridge.sales.dto.UserRegisterDTO;
import com.fridge.sales.dto.UserUpdateDTO;
import com.fridge.sales.dto.UserVO;
import com.fridge.sales.entity.User;
import com.fridge.sales.mapper.UserMapper;
import com.fridge.sales.service.UserService;
import com.fridge.sales.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.Random;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    // 简单的验证码存储（实际项目中应该使用Redis）
    private static final Map<String, String> VERIFICATION_CODES = new HashMap<>();

    private static final Random RANDOM = new Random();

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserVO register(UserRegisterDTO registerDTO) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, registerDTO.getUsername());
        User existUser = userMapper.selectOne(queryWrapper);
        if (existUser != null) {
            throw new BusinessException(ResultCode.USERNAME_EXISTS);
        }

        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setPhone(registerDTO.getPhone());
        user.setEmail(registerDTO.getEmail());
        user.setRole("user");
        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        userMapper.insert(user);

        return convertToVO(user);
    }

    @Override
    public String login(UserLoginDTO loginDTO) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, loginDTO.getUsername());
        User user = userMapper.selectOne(queryWrapper);

        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.PASSWORD_ERROR);
        }

        if (user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用");
        }

        return jwtUtil.generateToken(user.getId(), user.getUsername());
    }

    @Override
    public Map<String, Object> loginWithUserInfo(UserLoginDTO loginDTO) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, loginDTO.getUsername());
        User user = userMapper.selectOne(queryWrapper);

        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.PASSWORD_ERROR);
        }

        if (user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", convertToVO(user));
        return result;
    }

    @Override
    public UserVO getById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        return convertToVO(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserVO updateInfo(Long id, UserUpdateDTO updateDTO) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        if (StrUtil.isNotBlank(updateDTO.getUsername())) {
            if (!updateDTO.getUsername().equals(user.getUsername())) {
                LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(User::getUsername, updateDTO.getUsername());
                User existUser = userMapper.selectOne(queryWrapper);
                if (existUser != null) {
                    throw new BusinessException(ResultCode.USERNAME_EXISTS);
                }
            }
            user.setUsername(updateDTO.getUsername());
        }
        if (StrUtil.isNotBlank(updateDTO.getPhone())) {
            user.setPhone(updateDTO.getPhone());
        }
        if (StrUtil.isNotBlank(updateDTO.getEmail())) {
            user.setEmail(updateDTO.getEmail());
        }
        if (StrUtil.isNotBlank(updateDTO.getAvatar())) {
            user.setAvatar(updateDTO.getAvatar());
        }
        user.setUpdateTime(LocalDateTime.now());

        userMapper.updateById(user);

        return convertToVO(user);
    }

    @Override
    public Page<UserVO> listUsers(Integer page, Integer size, String username, String email, String phone, String role, Integer status) {
        Page<User> pageParam = new Page<>(page, size);

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(username)) {
            queryWrapper.like(User::getUsername, username);
        }
        if (StrUtil.isNotBlank(email)) {
            queryWrapper.like(User::getEmail, email);
        }
        if (StrUtil.isNotBlank(phone)) {
            queryWrapper.like(User::getPhone, phone);
        }
        if (StrUtil.isNotBlank(role)) {
            queryWrapper.eq(User::getRole, role);
        }
        if (status != null) {
            queryWrapper.eq(User::getStatus, status);
        }
        queryWrapper.orderByDesc(User::getCreateTime);

        Page<User> userPage = userMapper.selectPage(pageParam, queryWrapper);

        Page<UserVO> voPage = new Page<>();
        voPage.setCurrent(userPage.getCurrent());
        voPage.setSize(userPage.getSize());
        voPage.setTotal(userPage.getTotal());
        voPage.setPages(userPage.getPages());
        voPage.setRecords(userPage.getRecords().stream()
                .map(this::convertToVO)
                .toList());

        return voPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        user.setStatus(status);
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        user.setPassword(passwordEncoder.encode("123456"));
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePassword(Long userId, PasswordUpdateDTO passwordUpdateDTO) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        if (!passwordEncoder.matches(passwordUpdateDTO.getOldPassword(), user.getPassword())) {
            throw new BusinessException("旧密码错误");
        }

        user.setPassword(passwordEncoder.encode(passwordUpdateDTO.getNewPassword()));
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
    }

    @Override
    public void sendVerificationCode(String email) {
        // 生成6位数字验证码
        String code = String.format("%06d", RANDOM.nextInt(1000000));

        // 存储验证码
        VERIFICATION_CODES.put(email, code);

        // 演示模式：打印验证码到控制台
        System.out.println("【验证码】邮箱：" + email + "，验证码：" + code);
    }

    @Override
    public void forgotPassword(ForgotPasswordDTO forgotPasswordDTO) {
        // 校验邮箱是否存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getEmail, forgotPasswordDTO.getEmail());
        User user = userMapper.selectOne(queryWrapper);

        if (user == null) {
            throw new BusinessException("邮箱不存在，请检查邮箱地址");
        }

        // 校验验证码
        String cachedCode = VERIFICATION_CODES.get(forgotPasswordDTO.getEmail());
        if (cachedCode == null || !cachedCode.equals(forgotPasswordDTO.getVerificationCode())) {
            throw new BusinessException("验证码错误或已过期，请重新获取验证码");
        }

        // 校验密码
        if (!forgotPasswordDTO.getNewPassword().equals(forgotPasswordDTO.getConfirmPassword())) {
            throw new BusinessException("两次输入的密码不一致");
        }

        // 更新密码
        user.setPassword(passwordEncoder.encode(forgotPasswordDTO.getNewPassword()));
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);

        // 删除验证码
        VERIFICATION_CODES.remove(forgotPasswordDTO.getEmail());
    }

    /**
     * 将实体转换为视图对象
     */
    private UserVO convertToVO(User user) {
        UserVO vo = new UserVO();
        BeanUtil.copyProperties(user, vo);
        return vo;
    }

    @Override
    @Cacheable(value = "userRoles", key = "#userId", unless = "#result == null")
    public String getUserRole(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return null;
        }
        return user.getRole();
    }
}
