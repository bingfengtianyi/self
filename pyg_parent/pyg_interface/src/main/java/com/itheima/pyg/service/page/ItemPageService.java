package com.itheima.pyg.service.page;

public interface ItemPageService {

    /**
     * 生成静态页面的方法
     * @param goodsId
     * @throws Exception
     */
    boolean genHtml(long goodsId);

    /**
     * 删除静态页面的方法
     * @param goodsId
     * @throws Exception
     */
    boolean deleteItemPage(Long[] goodsIds);
}
