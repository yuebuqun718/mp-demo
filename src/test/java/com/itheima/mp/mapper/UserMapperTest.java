package com.itheima.mp.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.mp.domain.po.User;
import com.itheima.mp.service.IUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private IUserService userService;

    @Test
    void testInsert() {
        User user = new User();
        user.setId(5L);
        user.setUsername("Lucy");
        user.setPassword("123");
        user.setPhone("18688990011");
        user.setBalance(200);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        userMapper.insert(user);
    }

    @Test
    void testSelectById() {
        User user = userMapper.selectById(5L);
        System.out.println("user = " + user);
    }


    @Test
    void testQueryByIds() {
        List<User> users = userMapper.selectBatchIds(List.of(1L, 2L, 3L, 4L));
        users.forEach(System.out::println);
    }

    @Test
    void testUpdateById() {
        User user = new User();
        user.setId(5L);
        user.setBalance(20000);
        userMapper.updateById(user);
    }

    @Test
    void testDeleteUser() {
        userMapper.deleteById(5L);
    }

    @Test
    void testQuery() {
        User user = userMapper.selectById(1L);
        System.out.println("user = " + user);
    }

    @Test
    void testQueryWrapper(){
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.lambda()
                .select(User::getId,User::getUsername)
                .eq(User::getUsername,"Jack")
                .eq(User::getId,1);

        List<User> users = userMapper.selectList(wrapper);
        users.forEach(System.out::println);


    }

    @Test
    void testUpdateWrapper(){
        UpdateWrapper<User> wrapper = new UpdateWrapper<User>()
                .setSql("balance = balance - 1")
                .eq("id",1);
        userMapper.update(null,wrapper);
    }

    @Test
    void testCustomJoinWrapper() {
        // 1.准备自定义查询条件
        QueryWrapper<User> wrapper = new QueryWrapper<User>()
                .in("u.id", List.of(1L, 2L, 4L))
                .eq("a.city", "北京");

        // 2.调用mapper的自定义方法
        List<User> users = userMapper.queryUserByWrapper(wrapper);

        users.forEach(System.out::println);
    }

    @Test
    void testDeductBalance(){

        userService.deductBalance(1L,1000);
    }

    @Test
    void testPageQuery() {
        Page<User> page = Page.of(1, 5);
        page.addOrder(new OrderItem("balance",false));
        // 1.分页查询，new Page()的两个参数分别是：页码、每页大小
        userService.page(page);
        // 2.总条数
        System.out.println("total = " + page.getTotal());
        // 3.总页数
        System.out.println("pages = " + page.getPages());
        // 4.数据
        List<User> records = page.getRecords();
        records.forEach(System.out::println);
    }

}