package com.itheima.pyg.service.user;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.pyg.dao.address.AddressDao;
import com.itheima.pyg.pojo.address.Address;
import com.itheima.pyg.pojo.address.AddressQuery;
import com.itheima.pyg.service.user.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AddressServiceImpl implements AddressService {
    @Autowired
    private AddressDao addressDao;
    /**
     * 根据用户id查询地址列表
     * @param userId
     * @return
     */
    @Override
    public List<Address> findListByLoginUser(String userId) {
        AddressQuery query = new AddressQuery();
        AddressQuery.Criteria criteria = query.createCriteria();
        criteria.andUserIdEqualTo(userId);
        List<Address> addressList = addressDao.selectByExample(query);
        return addressList;
    }
}
