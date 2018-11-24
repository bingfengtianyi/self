package com.itheima.pyg.controller.user;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.pyg.pojo.address.Address;
import com.itheima.pyg.service.user.AddressService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("address")
public class AddressController {
    @Reference
    private AddressService addressService;

    /**
     * 根据用户id查询地址列表
     * @return
     */
    @RequestMapping("findListByLoginUser")
    public List<Address>    findListByLoginUser(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return addressService.findListByLoginUser(username);
    }
}
