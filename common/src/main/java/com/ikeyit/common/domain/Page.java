package com.ikeyit.common.domain;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Page<T> {
    private List<T> content;
    private int page;
    private int pageSize;
    private long total;
    private int totalPages;

    public Page(List<T> content, PageParam pageParam, long total) {
        this(content, pageParam.getPage(), pageParam.getPageSize(), total);
    }

    public Page(List<T> content, int page, int pageSize, long total) {
        this.total = total;
        this.page = page;
        this.pageSize = pageSize;
        this.content = content;
        this.totalPages = (int) Math.ceil((double) this.total / (double) this.pageSize);
    }

    public List<T> getContent() {
        return content;
    }

    public int getPage() {
        return page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public long getTotal() {
        return total;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public boolean hasNext() {
        return page < totalPages;
    }


    public boolean isLast() {
        return !hasNext();
    }

    public boolean hasPrevious() {
        return page > 1;
    }


    public boolean isFirst() {
        return !hasPrevious();
    }

    public static <A, R> Page<R> map(Page<A> page, Function<? super A, ? extends R> mapper) {
        List<R>  content = page.getContent().stream().map(mapper).collect(Collectors.toList());
        return new Page<R>(content, page.page, page.pageSize, page.total);
    }
}
