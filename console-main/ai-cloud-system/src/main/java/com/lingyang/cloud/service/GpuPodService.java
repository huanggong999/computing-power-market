package com.lingyang.cloud.service;

import com.lingyang.cloud.model.dto.BatchRenewDTO;
import com.lingyang.cloud.model.dto.CreateGpuPodDTO;
import com.lingyang.cloud.model.dto.RenewDTO;
import com.lingyang.cloud.model.dto.SetNameDTO;
import com.lingyang.cloud.model.dto.SetShutdownScheduleDTO;
import com.lingyang.cloud.model.query.GpuPodInstanceQuery;
import com.lingyang.cloud.model.vo.GpuPodInstanceVO;
import com.lingyang.cloud.model.vo.ShutdownScheduleVO;
import com.lingyang.cloud.model.vo.SshInfoVO;
import com.lingyang.cloud.model.vo.VncInfoVO;
import com.lingyang.common.core.model.result.PageResult;

import java.util.Map;

/**
 * GPU Pod 服务接口
 */
public interface GpuPodService {

    /**
     * 获取实例列表
     */
    PageResult<GpuPodInstanceVO> listInstances(GpuPodInstanceQuery query);

    /**
     * 创建实例
     */
    GpuPodInstanceVO createInstance(CreateGpuPodDTO dto);

    /**
     * 获取实例详情
     */
    GpuPodInstanceVO getInstance(String id);

    /**
     * 启动实例
     */
    void startInstance(String id);

    /**
     * 停止实例
     */
    void stopInstance(String id);

    /**
     * 重启实例
     */
    void restartInstance(String id);

    /**
     * 释放实例
     */
    void releaseInstance(String id);

    /**
     * 重置密码
     */
    String resetPassword(String id, String newPassword);

    /**
     * 续费实例
     */
    void renewInstance(String id, RenewDTO dto);

    /**
     * 批量续费实例
     */
    void batchRenewInstances(BatchRenewDTO dto);

    /**
     * 获取实例快捷工具
     */
    Object getInstanceTools(String id);

    /**
     * 获取实例监控数据
     */
    Object getInstanceMonitor(String id, Map<String, Object> params);

    /**
     * 设置实例名称
     */
    void setInstanceName(String id, SetNameDTO dto);

    /**
     * 设置定时关机
     */
    void setShutdownSchedule(String id, SetShutdownScheduleDTO dto);

    /**
     * 获取定时关机状态
     */
    ShutdownScheduleVO getShutdownSchedule(String id);

    /**
     * 获取SSH信息
     */
    SshInfoVO getSshInfo(String id);

    /**
     * 获取VNC连接信息
     */
    VncInfoVO getVncInfo(String id);

    /**
     * 获取实例日志
     */
    String getInstanceLogs(String id, int tailLines);
}
