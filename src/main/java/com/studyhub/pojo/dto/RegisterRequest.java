package com.studyhub.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 3,max = 50,message = "用户名长度必须在3-50个字符之间")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 8,max = 72,message = "密码长度必须在8-72个字符之间")
    private String password;

    @Size(max = 50,message = "昵称最长50个字符")
    private String nickname;
}
