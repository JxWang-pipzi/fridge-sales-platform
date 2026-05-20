package com.fridge.sales.service;

import com.fridge.sales.entity.User;
import com.fridge.sales.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.Mockito.*;

@SpringBootTest
public class UserServiceCacheTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserMapper userMapper;

    @Test
    public void testGetUserRoleCache() {
        Long userId = 1L;
        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setRole("admin");

        when(userMapper.selectById(userId)).thenReturn(mockUser);

        // First call - should hit database (mapper)
        userService.getUserRole(userId);

        // Second call - should hit cache
        userService.getUserRole(userId);

        // Verify mapper is called only once
        // Expecting FAILURE initially because cache is not enabled yet
        verify(userMapper, times(1)).selectById(userId);
    }
}
