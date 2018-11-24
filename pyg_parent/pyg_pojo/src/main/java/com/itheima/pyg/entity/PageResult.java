package com.itheima.pyg.entity;

import java.io.Serializable;
import java.util.List;


/**
 * 用于封装分页查询条件的对象
 * @param <T>
 */
public class PageResult<T> implements Serializable {
    private long    total;//总条数
    private List<T> rows;//结果集

    public PageResult() {
    }

    public PageResult(long total, List<T> rows) {

        this.total = total;
        this.rows = rows;
    }

    public long getTotal() {

        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }
}
