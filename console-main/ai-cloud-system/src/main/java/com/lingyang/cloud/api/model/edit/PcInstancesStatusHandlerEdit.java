package com.lingyang.cloud.api.model.edit;

import com.lingyang.cloud.enums.source.EcsStatusEnum;
import com.lingyang.cloud.enums.source.SourceRegionsEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/11/26 14:49
 */
@Data
public class PcInstancesStatusHandlerEdit {

    @Schema(description = "实列id集合")
    @NotNull(message = "实列为空")
    private List<Long> idList;

    @Schema(description = "状态枚举")
    private EcsStatusEnum statusEnum;

    /**
     * KeepCharging：普通停机模式。停机后实例及其相关资源仍被保留且持续计费，费用和停机前一致。
     * StopCharging：节省停机模式。停机后实例的计算资源（vCPU、GPU和内存）将被回收且停止计费，所挂载的云盘、镜像、公网IP仍被保留且持续计费。
     */
    @Schema(description = " KeepCharging：普通停机模式。停机后实例及其相关资源仍被保留且持续计费，费用和停机前一致。" +
            "StopCharging：节省停机模式。停机后实例的计算资源（vCPU、GPU和内存）将被回收且停止计费，所挂载的云盘、镜像、公网IP仍被保留且持续计费。")
    private String stoppedMode;

    @Schema(hidden = true)
    private SourceRegionsEnum regionsEnum;

}
