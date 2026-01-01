package com.itheima.mp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.itheima.mp.domain.dto.PageDTO;
import com.itheima.mp.domain.po.Address;
import com.itheima.mp.domain.po.User;
import com.itheima.mp.domain.query.UserQuery;
import com.itheima.mp.domain.vo.AddressVO;
import com.itheima.mp.domain.vo.UserVO;
import com.itheima.mp.enums.UserStatus;
import com.itheima.mp.mapper.UserMapper;
import com.itheima.mp.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    private final UserMapper userMapper;

    @Override
    @Transactional
    public void deductBalance(Long id, Integer money) {
        User user = getById(id);
        // 2.校验用户状态
        if (user == null || user.getStatus() == UserStatus.FREEZE) {
            throw new RuntimeException("用户状态异常！");
        }
        // 3.校验余额是否充足
        if (user.getBalance() < money) {
            throw new RuntimeException("用户余额不足！");
        }

        // 4.扣减余额 update tb_user set balance = balance - ?
        int remainBalance = user.getBalance() - money;
        lambdaUpdate()
                .set(User::getBalance, remainBalance) // 更新余额
                .set(remainBalance == 0, User::getStatus, 2) // 动态判断，是否更新status
                .eq(User::getId, id)
                .eq(User::getBalance, user.getBalance()) // 乐观锁
                .update();

    }

    @Override
    public UserVO queryUserById(Long userId) {
       
        User user = getById(userId);
        List<Address> list = Db.lambdaQuery(Address.class)
                .eq(Address::getUserId, userId)
                .list();
        UserVO userVO = new UserVO();
        BeanUtil.copyProperties(user,userVO);
        userVO.setAddresses(BeanUtil.copyToList(list,AddressVO.class));

        return userVO;
    }

    @Override
    public List<UserVO> queryUserByIds(List<Long> ids) {
        List<User> users = listByIds(ids);

        if (CollUtil.isEmpty(users)){
            return Collections.emptyList();
        }

        List<Long> userIdList = users.stream().map(User::getId).collect(Collectors.toList());

        List<Address> addressList = Db.lambdaQuery(Address.class)
                .in(Address::getUserId, ids)
                .list();
        List<AddressVO> addressVOS = BeanUtil.copyToList(addressList, AddressVO.class);
        Map<Long,List<AddressVO>> map = new HashMap<>();
        if (CollUtil.isNotEmpty(addressVOS)){
            map = addressVOS.stream().collect(Collectors.groupingBy(AddressVO::getUserId));
        }

        List<UserVO> userVOS = new ArrayList<>();

        for (User user : users){
            UserVO userVO = BeanUtil.copyProperties(user, UserVO.class);
            userVO.setAddresses(map.get(user.getId()));
            userVOS.add(userVO);
        }

        return userVOS;
    }

    @Override
    public PageDTO<UserVO> queryUsersPage(UserQuery query) {
        Page<User> page = query.toMpPageDefaultSortByCreateTimeDesc();
        page(page);
        PageDTO<UserVO> userVOPageDTO = PageDTO.of(page, UserVO.class);

        PageDTO<UserVO> userVOPageDTO1 = PageDTO.of(page, user -> {
            // 拷贝属性到VO
            UserVO vo = BeanUtil.copyProperties(user, UserVO.class);
            // 用户名脱敏
            String username = vo.getUsername();
            vo.setUsername(username.substring(0, username.length() - 2) + "**");
            return vo;
        });

        return userVOPageDTO;
    }
}
