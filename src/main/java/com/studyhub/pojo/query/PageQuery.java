package com.studyhub.pojo.query;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.studyhub.exception.BusinessException;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;

/*
* 通用分页参数，所有分页查询复用
* */
@Data
public class PageQuery {
    private Integer page = 1;
    private Integer size = 10;
    private String sortField;  // 排序字段
    private Boolean isAsc = false; // true: asc/ false: desc


    public  <T>Page<T> toMpPage(Set<String> allowedSortFields, OrderItem ... items){
        //  分页
        int pageNo = page == null ? 1 : Math.max(page,1);
        int pageSize = size == null ? 10 : Math.min(Math.max(size,10),100);

        Page<T> result = Page.of(pageNo,pageSize);

        //  如果请求携带了需要指定的排序字段
        if (StringUtils.isNotBlank(sortField)) {
            //  安全检查
            if (allowedSortFields == null || !allowedSortFields.contains(sortField)) {
                throw new BusinessException(400,"不支持的排序字段:" + sortField);
            }
            OrderItem primaryOrder = Boolean.TRUE.equals(isAsc) ? OrderItem.asc(sortField) : OrderItem.desc(sortField);
            result.addOrder(primaryOrder);

            if (!"id".equals(sortField)) {
                result.addOrder(OrderItem.desc("id"));
            }

        } else if (items != null && items.length > 0) {
            result.addOrder(items);
        }
        return result;
    }


}
