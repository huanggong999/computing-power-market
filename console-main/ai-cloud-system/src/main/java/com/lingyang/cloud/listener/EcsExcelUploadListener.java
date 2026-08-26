package com.lingyang.cloud.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.ListUtils;
import com.alibaba.fastjson2.JSON;
import com.lingyang.cloud.entity.SysEcsEntity;
import com.lingyang.cloud.enums.source.EcsTypeEnum;
import com.lingyang.cloud.mapper.SysEcsMapper;
import com.lingyang.cloud.model.edit.ecs.EcsExcelUploadEdit;
import com.lingyang.common.core.utils.BeanUtils;
import com.lingyang.common.core.utils.SpringUtils;

import java.util.List;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/11/18 17:05
 */
public class EcsExcelUploadListener implements ReadListener<EcsExcelUploadEdit> {
    public static final int BATCH_COUNT = 200;

    private final EcsTypeEnum ecsTypeEnum;

    private static final SysEcsMapper SYS_ECS_MAPPER;

    private List<SysEcsEntity> cachedDataList;

    static {
        SYS_ECS_MAPPER = SpringUtils.getBean(SysEcsMapper.class);
    }
    public EcsExcelUploadListener(EcsTypeEnum ecsTypeEnum) {
        this.ecsTypeEnum = ecsTypeEnum;
        this.cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
    }

    @Override
    public void invoke(EcsExcelUploadEdit data, AnalysisContext context) {
        System.out.println("上传数据:" + JSON.toJSONString(data));
        SysEcsEntity sysEcsEntity = BeanUtils.copyBean(data, SysEcsEntity.class);
        sysEcsEntity.setEcsType(ecsTypeEnum);
        cachedDataList.add(sysEcsEntity);
        if (cachedDataList.size() == BATCH_COUNT) {
            handlerDate();
            cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        handlerDate();
    }


    private void handlerDate() {
        if (cachedDataList.isEmpty()) {
            return;
        }
        SYS_ECS_MAPPER.batchInsert(cachedDataList, BATCH_COUNT);
    }
}
