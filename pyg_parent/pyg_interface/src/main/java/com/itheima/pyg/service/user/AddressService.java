package com.itheima.pyg.service.user;

import com.itheima.pyg.pojo.address.Address;

import java.util.List;

public interface AddressService {
    List<Address>   findListByLoginUser(String userId);
}
