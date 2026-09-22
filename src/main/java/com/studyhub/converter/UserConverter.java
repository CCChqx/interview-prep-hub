package com.studyhub.converter;

import com.studyhub.pojo.entity.User;
import com.studyhub.pojo.vo.UserVO;

public final class UserConverter {

    private UserConverter() {
    }

    public static UserVO toVO(User user) {
        if (user == null) {
            return null;
        }

        UserVO userVO = new UserVO();
        userVO.setId(user.getId());
        userVO.setNickname(user.getNickname());
        userVO.setUsername(user.getUsername());
        userVO.setStatus(user.getStatus());
        userVO.setLastLoginTime(user.getLastLoginTime());
        userVO.setCreateTime(user.getCreateTime());
        userVO.setUpdateTime(user.getUpdateTime());

        return userVO;
    }
}
