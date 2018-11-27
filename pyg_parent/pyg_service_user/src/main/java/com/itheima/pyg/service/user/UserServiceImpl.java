package com.itheima.pyg.service.user;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.pyg.dao.user.UserDao;
import com.itheima.pyg.entity.PageResult;
import com.itheima.pyg.pojo.user.User;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import javax.jms.Destination;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    /**
     * Excel 文件要存放的位置，假定在D盘下
     */
    private static String outputFile = "D:\\test.xls";

    @Autowired
    private UserDao userDao;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private Destination smscodeDestination;

    /**
     * 用户注册,添加新用户
     *
     * @param user
     */
    @Override
    public void addUser(User user) {
        user.setCreated(new Date());
        user.setUpdated(new Date());
        String newPassword = DigestUtils.md5Hex(user.getPassword());
        user.setPassword(newPassword);
        userDao.insertSelective(user);
    }

    /**
     * 随机生成短信验证码
     *
     * @param phone
     */
    @Override
    public void sendCode(String phone) {
        long smscode = (long) (Math.random() * 899999 + 100000);
        System.out.println("验证码为:" + smscode);
        redisTemplate.boundValueOps("smscode_" + phone).set(smscode, 5, TimeUnit.MINUTES);
        Map map = new HashMap();
        map.put("phone", phone);
        map.put("smscode", smscode + "");
        jmsTemplate.convertAndSend(smscodeDestination, map);
    }

    /**
     * 校验验证码是否正确
     *
     * @param smscode
     * @return
     */
    @Override
    public boolean checkSmsCode(String phone, String smscode) {
        long smscode1 = (long) redisTemplate.boundValueOps("smscode_" + phone).get();
        if (smscode != null && (smscode1 + "").equals(smscode)) {
            return true;
        }
        return false;
    }

    /**
     * 查询全部用户列表
     * @return
     */
    @Override
    public List<User> getUserList() {
        return userDao.selectByExample(null);
    }

    /**
     * 分页获得用户列表
     * @return
     */
    @Override
    public PageResult<User> getUserListByPage(Integer pageNum, Integer pageSize) {
        //设置分页查询条件
        PageHelper.startPage(pageNum,pageSize);
        //进行查询
        Page<User> page = (Page<User>) userDao.selectByExample(null);
        //封装PageResult对象
        PageResult<User>   pageResult = new PageResult<>(page.getTotal(),page.getResult());
        return pageResult;
    }

    /**
     * 从excel读取数据添加到数据库
     * @param user
     * */
    @Override
    public void save(User user){
        userDao.insert(user);
    }
}
