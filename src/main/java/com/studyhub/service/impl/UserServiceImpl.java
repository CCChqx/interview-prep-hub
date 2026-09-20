package com.studyhub.service.impl;

import com.studyhub.mapper.UserMapper;
import com.studyhub.pojo.dto.RegisterRequest;
import com.studyhub.pojo.entity.User;
import com.studyhub.pojo.vo.UserVO;
import com.studyhub.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserVO register(RegisterRequest request) {
        return null;
    }

    @Override
    public User authenticate(String username, String rawPassword) {
        return null;
    }

    @Override
    public UserVO getCurrentUser(Long userId) {
        return null;
    }
}
