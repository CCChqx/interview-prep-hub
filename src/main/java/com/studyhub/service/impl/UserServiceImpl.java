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
import org.springframework.transaction.annotation.Transactional;

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

    /**
     * 注册用户。
     *
     * 1. 先查重，给普通重复请求一个友好的错误
     * 2. BCrypt 编码密码
     * 3. 写入数据库
     * 4. 数据库唯一索引作为并发场景下的最终防线
     */
    @Transactional
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


    /**
     * 校验用户名和密码。
     *
     * 这里返回 User 是给 AuthService 内部使用的，
     * 不能直接把这个 User 返回给 Controller 或前端。
     */
    @Transactional
    @Override
    public User authenticate(String username, String rawPassword) {
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getUsername, username)
        );
        if (user == null) {
            throw new BusinessException(401,"用户名或密码错误");
        }
        if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            throw new BusinessException(401,"用户名或密码错误");
        }
        if (Integer.valueOf(0).equals(user.getStatus())) {
            throw new BusinessException(403,"该用户已被冻结");
        }
        user.setLastLoginTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
        return user;
    }

    @Override
    public UserVO getCurrentUser(Long userId) {
        User user = getUserById(userId);

        if (user == null ||  user.getStatus() == 0) {
            throw new BusinessException(401,"用户不存在或已失效");
        }
        return toVO(user);
    }

    @Override
    public User getUserById(Long userId) {
        return userMapper.selectById(userId);
    }
}
