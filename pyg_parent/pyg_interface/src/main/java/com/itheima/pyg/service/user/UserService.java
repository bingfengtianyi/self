package com.itheima.pyg.service.user;

import com.itheima.pyg.entity.PageResult;
import com.itheima.pyg.pojo.user.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface UserService {
    void addUser(User user);

    void sendCode(String phone);

    boolean checkSmsCode(String phone,String smscode);

    List<User>  getUserList();

    PageResult<User> getUserListByPage(Integer pageNum, Integer pageSize);

    void save(User user);


}
