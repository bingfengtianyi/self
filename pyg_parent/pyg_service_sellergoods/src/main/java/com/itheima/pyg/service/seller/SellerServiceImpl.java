package com.itheima.pyg.service.seller;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.pyg.dao.seller.SellerDao;
import com.itheima.pyg.entity.PageResult;
import com.itheima.pyg.entity.Result;
import com.itheima.pyg.pojo.seller.Seller;
import com.itheima.pyg.pojo.seller.SellerQuery;
import com.itheima.pyg.service.seller.SellerService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

@Service
@Transactional
public class SellerServiceImpl  implements SellerService {

    @Resource
    private SellerDao sellerDao;

    /**
     * 商家入驻
     * @param seller
     * @return
     */
    @Override
    public Result add(Seller seller) {
        Result result = null;
        try {
            seller.setStatus("0");
            seller.setCreateTime(new Date());
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            String encode = bCryptPasswordEncoder.encode(seller.getPassword());
            seller.setPassword(encode);
            sellerDao.insertSelective(seller);
            result = new Result(true,"注册成功");
        } catch (Exception e) {
            e.printStackTrace();
            result = new Result(false,"注册失败");
        } finally {
            return result;
        }
    }

    /**
     * 查询待审核商家
     * @param pageNum
     * @param pageSize
     * @param seller
     * @return
     */
    @Override
    public PageResult search(Integer pageNum, Integer pageSize, Seller seller) {
        PageHelper.startPage(pageNum,pageSize);
        SellerQuery sellerQuery = new SellerQuery();
        if (seller.getStatus()!=null&&!"".equals(seller.getStatus().trim())){
            sellerQuery.createCriteria().andStatusEqualTo(seller.getStatus().trim());
        }
        Page<Seller> page = (Page<Seller>) sellerDao.selectByExample(sellerQuery);
        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 查询待审核商家实体对象
     * @param id
     * @return
     */
    @Override
    public Seller findOne(String id) {
        return sellerDao.selectByPrimaryKey(id);
    }

    /**
     * 更新商家审核状态
     * @param sellerId
     * @param status
     * @return
     */
    @Override
    public Result updateStatus(String sellerId, String status) {
        Result result = null;
        try {
            Seller seller = new Seller();
            seller.setSellerId(sellerId);
            seller.setStatus(status);
            sellerDao.updateByPrimaryKeySelective(seller);
            result = new Result(true,"更新审核状态成功");
        } catch (Exception e) {
            e.printStackTrace();
            result = new Result(false,"更新审核状态失败");
        } finally {
            return result;
        }
    }
}
