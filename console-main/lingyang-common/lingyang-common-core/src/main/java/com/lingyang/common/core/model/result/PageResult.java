package com.lingyang.common.core.model.result;

import com.alibaba.fastjson2.annotation.JSONField;
import com.alibaba.ttl.TransmittableThreadLocal;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.lingyang.common.core.model.FileVO;
import com.lingyang.common.core.model.page.BasePage;
import com.lingyang.common.core.utils.BeanUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2023/8/9 14:09
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "统一分页")
public class PageResult<T> extends BasePage {
    @Serial
    private static final long serialVersionUID = 1019344730532442738L;

    @JsonIgnore
    @JSONField(serialize = false, deserialize = false)
    private final static ThreadLocal<Boolean> IS_PAGE = new TransmittableThreadLocal<>();

    @Schema(description = "总页数")
    private long pageTotal;

    @Schema(description = "总数量")
    private long dataTotal;

    @Getter
    @Schema(description = "数据列表")
    private List<T> list;

    private List<FileVO> fileList;

    public static <T> PageResult<T> of(List<T> data) {
        try {
            Page<Object> localPage = PageHelper.getLocalPage();
            if (data == null) {
                data = new ArrayList<>();
            }
            if (data.isEmpty()) {
                localPage.setTotal(0L);
                localPage.setPages(0);
            }
            PageResult<T> pageResult = new PageResult<>();
            pageResult.setPageNo(localPage.getPageNum());
            pageResult.setPageSize(localPage.getPageSize());
            pageResult.setPageTotal(localPage.getPages());
            pageResult.setDataTotal(localPage.getTotal());
            pageResult.setList(data);
            return pageResult;
        } finally {
            IS_PAGE.remove();
            PageHelper.clearPage();
        }
    }

    public static <S, T> PageResult<T> of(List<S> data, Class<T> toClass) {
        return of(BeanUtils.copyList(data, toClass));
    }

    public long getPageTotal() {
        if (dataTotal == 0) {
            return 0;
        }
        int pageSize = getPageSize();
        return dataTotal % pageSize == 0 ? dataTotal / pageSize : dataTotal / pageSize + 1;
    }

    @JsonIgnore
    @JSONField(serialize = false, deserialize = false)
    public static void onPage() {
        IS_PAGE.set(true);
    }

    @JsonIgnore
    @JSONField(serialize = false, deserialize = false)
    public static boolean isPage() {
        Boolean flag = IS_PAGE.get();
        return flag != null && flag;
    }
}
