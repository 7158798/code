/**
 * 
 */
package com.pay.card.model;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author qiaohui
 * 
 *         列表结果，含下一页的开始 id
 *
 */
public final class ResultModel<E, K> {

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static final ResultModel EMPTY = new ResultModel(Collections.emptyList(), null);

    private List<E> list;

    private K nextCursor;

    @SuppressWarnings("unchecked")
    public static <T1, T2> ResultModel<T1, T2> emptyResultView() {
        return EMPTY;
    }

    public ResultModel(List<E> list, K nextCursor) {
        this.list = list;
        this.nextCursor = nextCursor;
    }

    public List<E> getList() {
        return list;
    }

    public K getNextCursor() {
        return nextCursor;
    }

    public boolean isHasNext() {
        return nextCursor != null;
    }

    @Override
    public String toString() {
        return "ResultView [list=" + list + ", nextCursor=" + nextCursor + "]";
    }

    public Stream<E> stream() {
        return this.list.stream();
    }

    public boolean isEmpty() {
        return this.list.isEmpty();
    }

}
