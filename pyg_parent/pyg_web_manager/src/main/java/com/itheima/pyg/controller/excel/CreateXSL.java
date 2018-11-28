package com.itheima.pyg.controller.excel;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.pyg.pojo.good.Brand;
import com.itheima.pyg.pojo.good.Goods;
import com.itheima.pyg.pojo.item.ItemCat;
import com.itheima.pyg.pojo.order.Order;
import com.itheima.pyg.pojo.specification.Specification;
import com.itheima.pyg.pojo.template.TypeTemplate;
import com.itheima.pyg.pojo.user.User;
import com.itheima.pyg.service.brand.BrandService;
import com.itheima.pyg.service.goods.GoodsService;
import com.itheima.pyg.service.itemcat.ItemCatService;
import com.itheima.pyg.service.order.OrderService;
import com.itheima.pyg.service.spec.SpecificationService;
import com.itheima.pyg.service.template.TypeTemplateService;
import com.itheima.pyg.service.user.UserService;
import com.itheima.pyg.util.ExcelUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("createXSL")
public class CreateXSL {

    @Reference
    private GoodsService goodsService;

    @Reference
    private OrderService orderService;

    @Reference
    private UserService userService;

    @Reference
    private BrandService brandService;

    @Reference
    private SpecificationService specificationService;

    @Reference
    private TypeTemplateService typeTemplateService;

    @Reference
    private ItemCatService itemCatService;

    /**
     * 运营商后台 商品列表导出为excel
     * @param response
     */
    @RequestMapping("exportexcelForGoods")
    public void exportexcelForGoods(HttpServletResponse response) {
        List<Goods> userList = goodsService.getGoodList();
        String[] columnNames = {
                "商家ID","SPU名","默认SKU","状态","是否上架","品牌","副标题","一级类目","二级类目",
                "三级类目","小图","商城价","分类模板ID","是否启用规格","是否删除"
        };
        String[] columns = {
                "sellerId","goodsName","defaultItemId","auditStatus","isMarketable","brandId",
                "caption","category1Id","category2Id","category3Id","smallPic","price","typeTemplateId",
                "isEnableSpec","isDelete"
        };
        ExcelUtils.exportExcel(response,userList,columnNames,columns,"商品表","E:\\Goods.html");
    }

    /**
     * 运营商后台 订单数据导出为excel
     * @param response
     */
    @RequestMapping("exportexcelForOrders")
    public void exportexcelForOrders(HttpServletResponse response) {
        List<Order> userList = orderService.getOrderList();
        String[] columnNames = {
                "订单id","实付金额","支付类型","邮费","状态","订单创建时间","订单更新时间","付款时间",
                "发货时间","交易完成时间","交易关闭时间","物流名称","物流单号","用户id","买家留言",
                "买家昵称","买家是否已经评价","收货人地区名称","收货人手机","收货人邮编","收货人",
                "过期时间","发票类型","订单来源","商家ID","支付订单号"
        };
        String[] columns = {
                "orderId","payment","paymentType","postFee","status","createTime","updateTime",
                "paymentTime","consignTime","endTime","closeTime","shippingName","shippingCode",
                "userId","buyerMessage","buyerNick","buyerRate","receiverAreaName","receiverMobile",
                "receiverZipCode","receiver","expire","invoiceType","sourceType","sellerId","outTradeNo"
        };
        ExcelUtils.exportExcel(response,userList,columnNames,columns,"订单表","E:\\Orders.html");
    }

    @RequestMapping("exportexcel")
    public void exportexcel(HttpServletResponse response) {
        List<User> userList = userService.getUserList();
        String[] columnNames = {
                "用户名", "密码", "手机号", "注册邮箱", "创建时间", "更新时间", "会员来源", "昵称", "真实姓名", "使用状态",
                "头像地址", "QQ号码", "账户余额", "手机是否验证", "邮箱是否检测", "性别", "会员等级", "积分", "经验值",
                "生日", "最后登录时间"
        };
        String[] columns = {
                "username", "password", "phone", "email", "created", "updated", "sourceType", "nickName", "name",
                "status", "headPic", "qq", "accountBalance", "isMobileCheck", "isEmailCheck", "sex", "userLevel",
                "points", "experienceValue", "birthday", "lastLoginTime"
        };
        ExcelUtils.exportExcel(response, userList, columnNames, columns, "用户表", "E:\\test.html");
    }

    @RequestMapping("importexcel")
    public Map<String, Object> importexcel(MultipartFile file) {
        Map<String, Object> map = new HashMap<>();
        try {
            //用工具类
            String[][] data = ExcelUtils.readexcell("E:\\user.xls", 1);
            SimpleDateFormat format = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
            for (int i = 0; i < data.length; i++) {
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

        } catch (Exception e) {
            e.printStackTrace();
            map.put("success", false);
            map.put("errmsg", e.getMessage());
        }

        return map;
    }

    @RequestMapping("importexcelForBrand")
    public Map<String, Object> importexcelForBrand(MultipartFile file) {
        Map<String, Object> map = new HashMap<>();
        try {
            //用工具类
            String[][] data = ExcelUtils.readexcell("E:\\brand.xls", 1);
            for (int i = 0; i < data.length; i++) {
                Brand brand = new Brand();
                brand.setName(data[i][0]);
                brand.setFirstChar(data[i][1]);
                brandService.addBrand(brand);//这是一个添加方法，dao层写入sql语句即可
            }
            map.put("success", true);

        } catch (Exception e) {
            e.printStackTrace();
            map.put("success", false);
            map.put("errmsg", e.getMessage());
        }

        return map;
    }

    @RequestMapping("importexcelForSpec")
    public Map<String, Object> importexcelForSpec(MultipartFile file) {
        Map<String, Object> map = new HashMap<>();
        try {
            //用工具类
            String[][] data = ExcelUtils.readexcell("E:\\spec.xls", 1);
            for (int i = 0; i < data.length; i++) {
                Specification specification = new Specification();
                specification.setSpecName(data[i][0]);
                specificationService.save(specification);
                //这是一个添加方法，dao层写入sql语句即可
            }
            map.put("success", true);

        } catch (Exception e) {
            e.printStackTrace();
            map.put("success", false);
            map.put("errmsg", e.getMessage());
        }

        return map;
    }

    @RequestMapping("importexcelForTemplate")
    public Map<String, Object> importexcelForTemplate(MultipartFile file) {
        Map<String, Object> map = new HashMap<>();
        try {
            //用工具类
            String[][] data = ExcelUtils.readexcell("E:\\template.xls", 1);
            for (int i = 0; i < data.length; i++) {
                TypeTemplate typeTemplate = new TypeTemplate();
                typeTemplate.setName(data[i][0]);
                typeTemplate.setSpecIds(data[i][1]);
                typeTemplate.setBrandIds(data[i][2]);
                typeTemplate.setCustomAttributeItems(data[i][3]);
                typeTemplateService.add(typeTemplate);
                //这是一个添加方法，dao层写入sql语句即可
            }
            map.put("success", true);

        } catch (Exception e) {
            e.printStackTrace();
            map.put("success", false);
            map.put("errmsg", e.getMessage());
        }

        return map;
    }

    @RequestMapping("importexcelForItemCat")
    public Map<String, Object> importexcelForItemCat(MultipartFile file) {
        Map<String, Object> map = new HashMap<>();
        try {
            //用工具类
            String[][] data = ExcelUtils.readexcell("E:\\itemCat.xls", 1);
            for (int i = 0; i < data.length; i++) {
                ItemCat itemCat = new ItemCat();
                itemCat.setParentId(Long.valueOf(data[i][0]));
                itemCat.setName(data[i][1]);
                itemCat.setTypeId(Long.valueOf(data[i][2]));
                itemCatService.save(itemCat);
                //这是一个添加方法，dao层写入sql语句即可
            }
            map.put("success", true);

        } catch (Exception e) {
            e.printStackTrace();
            map.put("success", false);
            map.put("errmsg", e.getMessage());
        }

        return map;
    }

}
