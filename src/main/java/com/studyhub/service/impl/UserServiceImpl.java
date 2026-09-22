package com.studyhub.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.studyhub.exception.BusinessException;
import com.studyhub.mapper.UserMapper;
import com.studyhub.pojo.dto.RegisterRequest;
import com.studyhub.pojo.entity.User;
import com.studyhub.pojo.vo.UserVO;
import com.studyhub.service.UserService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.studyhub.converter.UserConverter.toVO;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserVO register(RegisterRequest request) {
        // 获取用户传入的用户名称
        String username = request.getUsername().trim();

        // 校验用户名是否与已存在的重复
        Long count = userMapper.selectCount(
                new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username)
        );
        if (count != null && count > 0) {
            throw new BusinessException(409,"用户名已经存在");
        }

        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname());
        user.setStatus(1);

        LocalDateTime now = LocalDateTime.now();
        user.setCreateTime(now);
        user.setUpdateTime(now);

        try {
            userMapper.insert(user);
        }catch (DuplicateKeyException e){
            throw new BusinessException(409,"用户名已存在");
        }
        return toVO(user);
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
