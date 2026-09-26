package com.studyhub.controller;

import com.studyhub.common.Result;
import com.studyhub.pojo.vo.UserVO;
import com.studyhub.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public Result<UserVO> getCurrentUser(
            @RequestAttribute("userId") Long userId) {

        return Result.success(userService.getCurrentUser(userId));
    }
}
