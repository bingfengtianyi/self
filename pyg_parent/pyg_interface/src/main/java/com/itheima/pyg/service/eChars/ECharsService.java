package com.itheima.pyg.service.eChars;

import java.util.Map;

public interface ECharsService {

    /**
     * 商家后台,统计某一段时间每天的销售额
     * @param startTime
     * @param endTime
     * @return
     */
    Map<String,Double>  findSaleroomBySellerAndDate(String startTime,String endTime);

    /**运营商后台,销售折线图
     * @param startTime
     * @param endTime
     * @return
     */
    Map<String,Double>  findSaleroomByDate(String startTime,String endTime);

    /**
     * 运营商后台,销售饼状图
     * @param sellerId
     * @return
     */
    Map<String,Double>  findSaleroomBySeller(String sellerId);
}
