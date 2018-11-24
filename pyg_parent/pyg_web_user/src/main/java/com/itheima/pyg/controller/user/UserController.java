package com.itheima.pyg.controller.user;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.pyg.entity.Result;
import com.itheima.pyg.pojo.user.User;
import com.itheima.pyg.service.user.UserService;
import com.itheima.pyg.util.PhoneFormatCheckUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("user")
public class UserController {
    @Reference
    private UserService userService;

    @RequestMapping("add")
    public Result add(@RequestBody User user,String smscode){
        if (!userService.checkSmsCode(user.getPhone(),smscode)){
            return new Result(false,"验证码错误");
        }
        try {
            userService.addUser(user);
            return  new Result(true,"注册成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"注册失败");
        }
    }

    @RequestMapping("sendCode")
    public Result sendCode(String phone){
        if (!PhoneFormatCheckUtils.isPhoneLegal(phone)){
            return new Result(false,"手机号码不合法");
        }
        try {
            userService.sendCode(phone);
            return new Result(true,"发送验证码成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"发送验证码失败");
        }
    }
}
