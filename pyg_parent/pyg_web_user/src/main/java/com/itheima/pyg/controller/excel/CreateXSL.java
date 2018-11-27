package com.itheima.pyg.controller.excel;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.pyg.pojo.user.User;
import com.itheima.pyg.service.user.UserService;
import com.itheima.pyg.util.ExcelUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("createXSL")
public class CreateXSL {

    @Reference
    private UserService userService;

    @RequestMapping("exportexcel")
    public void exportexcel(HttpServletResponse response) {
        List<User> userList = userService.getUserList();
        String[] columnNames = {
                "用户名","密码","手机号","注册邮箱","创建时间","更新时间","会员来源","昵称","真实姓名","使用状态",
                "头像地址","QQ号码","账户余额","手机是否验证","邮箱是否检测","性别","会员等级","积分","经验值",
                "生日","最后登录时间"
        };
        String[] columns = {
                "username","password","phone","email","created","updated","sourceType","nickName","name",
                "status","headPic","qq","accountBalance","isMobileCheck","isEmailCheck","sex","userLevel",
                "points","experienceValue","birthday","lastLoginTime"
        };
        ExcelUtils.exportExcel(response,userList,columnNames,columns,"用户表","E:\\test.html");
    }

    @RequestMapping("importexcel")
    public Map<String, Object> importexcel(MultipartFile filePath){
        Map<String,Object> map = new HashMap<>();
        try {
            //用工具类
            String[][] data = ExcelUtils.readexcell("E:\\test.xls",1);
            SimpleDateFormat format = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
            for(int i=0;i<data.length;i++){
                User user = new User();
                user.setUsername(data[i][0]);
                user.setPassword(data[i][1]);
                user.setPhone(data[i][2]);
                user.setEmail(data[i][3]);
                user.setCreated(format.parse(data[i][4]));
                user.setUpdated(format.parse(data[i][5]));
                user.setSourceType(data[i][6]);
                user.setNickName(data[i][7]);
                user.setName(data[i][8]);
                user.setStatus(data[i][9]);
                user.setHeadPic(data[i][10]);
                user.setQq(data[i][11]);
                user.setAccountBalance(Long.valueOf(data[i][12]));
                user.setIsMobileCheck(data[i][13]);
                user.setIsEmailCheck(data[i][14]);
                user.setSex(data[i][15]);
                user.setUserLevel(Integer.valueOf(data[i][16]));
                user.setPoints(Integer.valueOf(data[i][17]));
                user.setExperienceValue(Integer.valueOf(data[i][18]));
                user.setBirthday(format.parse(data[i][19]));
                user.setLastLoginTime(format.parse(data[i][20]));
                userService.save(user);//这是一个添加方法，dao层写入sql语句即可
            }
            map.put("success", true);

        }catch (Exception e) {
            e.printStackTrace();
            map.put("success", false);
            map.put("errmsg", e.getMessage());
        }

        return map;
    }
}
