package com.itheima.pyg.controller.user;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.pyg.entity.PageResult;
import com.itheima.pyg.pojo.user.User;
import com.itheima.pyg.service.user.UserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("user")
public class UserController {

    @Reference
    private UserService userService;

    @RequestMapping("findUserList")
    public PageResult<User> findUserList(Integer pageNum, Integer pageSize){
        return userService.getUserListByPage(pageNum,pageSize);
    }

}
