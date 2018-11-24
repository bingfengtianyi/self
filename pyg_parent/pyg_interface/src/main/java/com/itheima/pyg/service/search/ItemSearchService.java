package com.itheima.pyg.service.search;

import java.util.Map;

public interface ItemSearchService {
    Map<String,Object> search(Map<String,String> searchMap);
}
