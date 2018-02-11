/**
 * 
 */
package com.pay.card.model;

import java.util.List;

/**
 * @author qiaohui
 * 
 *         带分页统计的结果页
 */
public class PagedResultModel<T> {

    private List<T> list;

    private int totalCount;

    private int countPerPage;

    private int currentPage;

    public List<T> getList() {
        return list;
    }

    public PagedResultModel<T> setList(List<T> list) {
        this.list = list;
        return this;
    }

    public int getTotalPage() {
        return totalCount / countPerPage;
    }

    public PagedResultModel<T> setTotalCountAndCountPerPage(int totalCount, int countPerPage) {
        this.totalCount = totalCount;
        this.countPerPage = countPerPage;
        return this;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public PagedResultModel<T> setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
        return this;
    }

}
