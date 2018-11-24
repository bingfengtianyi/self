package com.itheima.pyg.controller.excel;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.pyg.pojo.user.User;
import com.itheima.pyg.service.user.UserService;
import com.itheima.pyg.util.ExcelUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
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
        String[] columnNames = {"用户名","密码","手机号","创建时间"};
        String[] columns = {"username","password","phone","created"};
        ExcelUtils.exportExcel(response,userList,columnNames,columns,"用户表","E:\\test.html");
    }

    @RequestMapping("importexcel")
    public Map<String, Object> importexcel(MultipartFile file){
        Map<String,Object> map = new HashMap<>();
        try {
            //用工具类
            String[][] data = ExcelUtils.readexcell("E:\\test.xls",1);
            SimpleDateFormat format = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
            for(int i=0;i<data.length;i++){
                User user = new User();
                user.setUsername(data[i][0]+"02");
                user.setPassword(data[i][1]);
                user.setPhone(data[i][2]);
                user.setCreated(format.parse(data[i][3]));
                user.setUpdated(format.parse(data[i][4]));
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
