package com.itheima.mp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.mp.domain.dto.PageDTO;
import com.itheima.mp.domain.dto.UserFormDTO;
import com.itheima.mp.domain.po.User;
import com.itheima.mp.domain.query.UserQuery;
import com.itheima.mp.domain.vo.UserVO;

import java.util.List;

public interface IUserService extends IService<User> {

    void deductBalance(Long id, Integer money);

    UserVO queryUserById(Long userId);

    List<UserVO> queryUserByIds(List<Long> ids);

    PageDTO<UserVO> queryUsersPage(UserQuery query);
}
