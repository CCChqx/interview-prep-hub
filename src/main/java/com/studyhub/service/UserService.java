package com.studyhub.service;


import com.studyhub.pojo.dto.RegisterRequest;
import com.studyhub.pojo.entity.User;
import com.studyhub.pojo.vo.UserVO;

public interface UserService {

    UserVO register (RegisterRequest request);

    User authenticate(String username,String rawPassword);

    UserVO getCurrentUser(Long userId);
}
