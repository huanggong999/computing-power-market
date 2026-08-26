package com.lingyang.cloud.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lingyang.cloud.client.GpuPodApiClient;
import com.lingyang.cloud.client.GpuPodTenantProvider;
import com.lingyang.cloud.client.dto.BatchRenewRequest;
import com.lingyang.cloud.client.dto.CreateInstanceRequest;
import com.lingyang.cloud.client.dto.InstanceListResponse;
import com.lingyang.cloud.client.dto.InstanceResponse;
import com.lingyang.cloud.client.dto.RenewRequest;
import com.lingyang.cloud.client.dto.ResetPasswordRequest;
import com.lingyang.cloud.client.dto.SetInstanceNameRequest;
import com.lingyang.cloud.client.dto.ShutdownScheduleRequest;
import com.lingyang.cloud.client.dto.SshInfoResponse;
import com.lingyang.cloud.entity.SysCustomerGpuPodEntity;
import com.lingyang.cloud.mapper.SysCustomerGpuPodMapper;
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
import com.lingyang.cloud.service.GpuPodService;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.security.SecurityContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * GPU Pod 服务实现
 */
@Slf4j
@Service
public class GpuPodServiceImpl implements GpuPodService {

    @Autowired
    private SysCustomerGpuPodMapper sysCustomerGpuPodMapper;

    @Autowired
    private GpuPodApiClient gpuPodApiClient;

    @Autowired
    private GpuPodTenantProvider tenantProvider;

    /**
     * 获取当前租户ID
     */
    private String getTenantId() {
        return tenantProvider.getCurrentTenant().tenantId();
    }

    /**
     * 获取当前租户名称
     */
    private String getTenantName() {
        return tenantProvider.getCurrentTenant().tenantName();
    }

    /**
     * 获取当前客户ID
     */
    private Long getCustomerId() {
        return SecurityContext.getUserInfo().getUserId();
    }

    @Override
    public PageResult<GpuPodInstanceVO> listInstances(GpuPodInstanceQuery query) {
        Long customerId = getCustomerId();
        String tenantId = getTenantId();

        // 构建查询参数
        Map<String, Object> params = new HashMap<>();
        if (ObjectUtils.isNotEmpty(query.getName())) {
            params.put("name", query.getName());
        }
        if (ObjectUtils.isNotEmpty(query.getStatus())) {
            params.put("status", query.getStatus());
        }
        if (ObjectUtils.isNotEmpty(query.getRegionCode())) {
            params.put("region", query.getRegionCode());
        }
        if (ObjectUtils.isNotEmpty(query.getGpuModel())) {
            params.put("gpuModel", query.getGpuModel());
        }
        if (ObjectUtils.isNotEmpty(query.getBillingMode())) {
            params.put("billingMode", query.getBillingMode());
        }
        params.put("pageNum", query.getPageNo());
        params.put("pageSize", query.getPageSize());

        // 调用外部API获取列表
        InstanceListResponse response = gpuPodApiClient.listInstances(tenantId, params);

        List<GpuPodInstanceVO> voList = new ArrayList<>();
        long total = 0L;

        if (response != null && response.isSuccess() && response.getInstances() != null) {
            total = response.getTotal() != null ? response.getTotal() : response.getInstances().size();
            for (InstanceListResponse.InstanceItem item : response.getInstances()) {
                voList.add(convertItemToVO(item));
            }
        }

        PageResult<GpuPodInstanceVO> result = new PageResult<>();
        result.setList(voList);
        result.setDataTotal(total);
        result.setPageNo(query.getPageNo());
        result.setPageSize(query.getPageSize());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public GpuPodInstanceVO createInstance(CreateGpuPodDTO dto) {
        Long customerId = getCustomerId();
        String tenantId = getTenantId();

        // 构建创建请求
        CreateInstanceRequest request = new CreateInstanceRequest();
        request.setTenantId(tenantId);
        request.setTenantName(getTenantName());
        request.setRegion(dto.getRegion());
        request.setImage(dto.getImage());
        request.setPodName(dto.getPodName());

        if (dto.getGpuSpec() != null) {
            CreateInstanceRequest.GpuSpec gpuSpec = new CreateInstanceRequest.GpuSpec();
            gpuSpec.setModel(dto.getGpuSpec().getModel());
            gpuSpec.setCount(dto.getGpuSpec().getCount());
            request.setGpuSpec(gpuSpec);
        }

        if (dto.getBilling() != null) {
            CreateInstanceRequest.Billing billing = new CreateInstanceRequest.Billing();
            billing.setMode(dto.getBilling().getMode());
            billing.setDuration(dto.getBilling().getDuration());
            request.setBilling(billing);
        }

        if (dto.getResource() != null) {
            CreateInstanceRequest.Resource resource = new CreateInstanceRequest.Resource();
            resource.setCpu(dto.getResource().getCpu());
            resource.setMemory(dto.getResource().getMemory());
            resource.setSystemDisk(dto.getResource().getSystemDisk());
            resource.setDataDisk(dto.getResource().getDataDisk());
            request.setResource(resource);
        }

        // 调用API创建实例
        InstanceResponse response = gpuPodApiClient.createInstance(request);
        if (response == null || !response.isSuccess()) {
            String msg = response != null ? response.getMessage() : "创建实例失败";
            throw new RuntimeException(msg);
        }

        // 保存到本地数据库
        SysCustomerGpuPodEntity entity = new SysCustomerGpuPodEntity();
        entity.setCustomerId(customerId);
        entity.setInstanceId(response.getInstanceId());
        entity.setInstanceName(response.getInstanceName() != null ? response.getInstanceName() : dto.getPodName());
        entity.setStatus(response.getStatus());
        entity.setRegionCode(dto.getRegion());
        entity.setZoneCode(dto.getZone());
        if (dto.getGpuSpec() != null) {
            entity.setGpuModel(dto.getGpuSpec().getModel());
            entity.setGpuCount(dto.getGpuSpec().getCount());
        }
        if (dto.getResource() != null) {
            entity.setCpuCores(parseCpuCores(dto.getResource().getCpu()));
            entity.setMemorySize(dto.getResource().getMemory());
            entity.setSystemDiskSize(parseDiskSize(dto.getResource().getSystemDisk()));
            entity.setDataDiskSize(parseDiskSize(dto.getResource().getDataDisk()));
        }
        entity.setImageUrl(dto.getImage());
        if (dto.getBilling() != null) {
            entity.setBillingMode(dto.getBilling().getMode());
        }
        entity.setDelFlag(0);

        sysCustomerGpuPodMapper.insert(entity);

        return getInstance(response.getInstanceId());
    }

    @Override
    public GpuPodInstanceVO getInstance(String id) {
        String tenantId = getTenantId();

        // 优先从外部API获取最新状态
        InstanceResponse response = gpuPodApiClient.getInstance(id, tenantId);
        if (response != null && response.isSuccess()) {
            return convertResponseToVO(response);
        }

        // 回退到本地数据库查询
        LambdaQueryWrapper<SysCustomerGpuPodEntity> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(SysCustomerGpuPodEntity::getInstanceId, id)
                .eq(SysCustomerGpuPodEntity::getCustomerId, getCustomerId())
                .eq(SysCustomerGpuPodEntity::getDelFlag, 0);
        SysCustomerGpuPodEntity entity = sysCustomerGpuPodMapper.selectOne(wrapper);
        if (entity == null) {
            throw new RuntimeException("实例不存在");
        }
        return convertEntityToVO(entity);
    }

    @Override
    public void startInstance(String id) {
        String tenantId = getTenantId();
        gpuPodApiClient.startInstance(id, tenantId);

        // 更新本地状态
        updateLocalStatus(id, "running");
    }

    @Override
    public void stopInstance(String id) {
        String tenantId = getTenantId();
        gpuPodApiClient.stopInstance(id, tenantId);

        // 更新本地状态
        updateLocalStatus(id, "stopped");
    }

    @Override
    public void restartInstance(String id) {
        String tenantId = getTenantId();
        gpuPodApiClient.restartInstance(id, tenantId);

        // 更新本地状态
        updateLocalStatus(id, "running");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void releaseInstance(String id) {
        String tenantId = getTenantId();
        gpuPodApiClient.releaseInstance(id, tenantId);

        // 本地逻辑删除
        LambdaQueryWrapper<SysCustomerGpuPodEntity> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(SysCustomerGpuPodEntity::getInstanceId, id)
                .eq(SysCustomerGpuPodEntity::getCustomerId, getCustomerId());
        SysCustomerGpuPodEntity entity = sysCustomerGpuPodMapper.selectOne(wrapper);
        if (entity != null) {
            entity.setDelFlag(1);
            entity.setReleaseTime(new Date());
            sysCustomerGpuPodMapper.updateById(entity);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String resetPassword(String id, String newPassword) {
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setNewPassword(newPassword);
        InstanceResponse response = gpuPodApiClient.resetPassword(id, getTenantId(), request);
        if (response == null || !response.isSuccess()) {
            throw new RuntimeException(response == null ? "重置密码失败" : response.getMessage());
        }

        LambdaQueryWrapper<SysCustomerGpuPodEntity> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(SysCustomerGpuPodEntity::getInstanceId, id)
                .eq(SysCustomerGpuPodEntity::getCustomerId, getCustomerId());
        SysCustomerGpuPodEntity entity = sysCustomerGpuPodMapper.selectOne(wrapper);
        if (entity != null) {
            entity.setSshPassword(newPassword);
            sysCustomerGpuPodMapper.updateById(entity);
        }
        return newPassword;
    }

    @Override
    public void renewInstance(String id, RenewDTO dto) {
        RenewRequest request = new RenewRequest();
        request.setBillingMode(dto.getBillingMode());
        request.setDuration(dto.getDuration());
        gpuPodApiClient.renewInstance(id, getTenantId(), request);
    }

    @Override
    public void batchRenewInstances(BatchRenewDTO dto) {
        BatchRenewRequest request = new BatchRenewRequest();
        request.setInstanceIds(dto.getInstanceIds());
        request.setBillingMode(dto.getBillingMode());
        request.setDuration(dto.getDuration());
        gpuPodApiClient.batchRenewInstances(getTenantId(), request);
    }

    @Override
    public Object getInstanceTools(String id) {
        return gpuPodApiClient.getInstanceTools(id, getTenantId());
    }

    @Override
    public Object getInstanceMonitor(String id, Map<String, Object> params) {
        return gpuPodApiClient.getInstanceMonitor(id, getTenantId(), params);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setInstanceName(String id, SetNameDTO dto) {
        SetInstanceNameRequest request = new SetInstanceNameRequest();
        request.setName(dto.getName());
        gpuPodApiClient.setInstanceNameExternal(id, getTenantId(), request);

        // 更新本地数据库
        LambdaQueryWrapper<SysCustomerGpuPodEntity> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(SysCustomerGpuPodEntity::getInstanceId, id)
                .eq(SysCustomerGpuPodEntity::getCustomerId, getCustomerId());
        SysCustomerGpuPodEntity entity = sysCustomerGpuPodMapper.selectOne(wrapper);
        if (entity == null) {
            throw new RuntimeException("实例不存在");
        }
        entity.setInstanceName(dto.getName());
        sysCustomerGpuPodMapper.updateById(entity);
    }

    @Override
    public void setShutdownSchedule(String id, SetShutdownScheduleDTO dto) {
        String tenantId = getTenantId();

        ShutdownScheduleRequest request = new ShutdownScheduleRequest();
        request.setShutdownTime(ObjectUtils.isEmpty(dto.getShutdownTime()) ? null : dto.getShutdownTime());

        gpuPodApiClient.setShutdownSchedule(id, tenantId, request);

        // 更新本地数据库
        LambdaQueryWrapper<SysCustomerGpuPodEntity> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(SysCustomerGpuPodEntity::getInstanceId, id)
                .eq(SysCustomerGpuPodEntity::getCustomerId, getCustomerId());
        SysCustomerGpuPodEntity entity = sysCustomerGpuPodMapper.selectOne(wrapper);
        if (entity != null) {
            if (ObjectUtils.isNotEmpty(dto.getShutdownTime())) {
                entity.setScheduledShutdownTime(Date.from(
                        LocalDateTime.parse(dto.getShutdownTime(), DateTimeFormatter.ISO_DATE_TIME)
                                .atZone(ZoneId.systemDefault()).toInstant()));
            } else {
                entity.setScheduledShutdownTime(null);
            }
            sysCustomerGpuPodMapper.updateById(entity);
        }
    }

    @Override
    public ShutdownScheduleVO getShutdownSchedule(String id) {
        ShutdownScheduleVO vo = new ShutdownScheduleVO();
        InstanceResponse response = gpuPodApiClient.getInstance(id, getTenantId());
        if (response != null && response.isSuccess() && response.getShutdownSchedule() != null) {
            InstanceResponse.ShutdownScheduleInfo schedule = response.getShutdownSchedule();
            vo.setHasSchedule(Boolean.TRUE.equals(schedule.getEnabled()));
            if (Boolean.TRUE.equals(schedule.getEnabled()) && schedule.getScheduledTime() != null) {
                vo.setShutdownTime(schedule.getScheduledTime());
                try {
                    LocalDateTime shutdownTime = LocalDateTime.parse(schedule.getScheduledTime(), DateTimeFormatter.ISO_DATE_TIME);
                    long remaining = ChronoUnit.SECONDS.between(LocalDateTime.now(), shutdownTime);
                    vo.setRemainingTime(Math.max(remaining, 0L));
                } catch (Exception e) {
                    log.warn("解析关机时间失败: {}", schedule.getScheduledTime());
                    vo.setRemainingTime(0L);
                }
            }
        } else {
            vo.setHasSchedule(false);
        }
        return vo;
    }

    @Override
    public SshInfoVO getSshInfo(String id) {
        try {
            SshInfoResponse response = gpuPodApiClient.getSshInfo(id, getTenantId());
            if (response != null && response.isSuccess() && response.getSshInfo() != null) {
                SshInfoVO vo = new SshInfoVO();
                vo.setHost(response.getSshInfo().getHost());
                vo.setPort(response.getSshInfo().getPort());
                vo.setUsername(response.getSshInfo().getUsername());
                vo.setPassword(response.getSshInfo().getPassword());
                if (ObjectUtils.isNotEmpty(vo.getHost()) && vo.getPort() != null && ObjectUtils.isNotEmpty(vo.getUsername())) {
                    vo.setCommand(String.format("ssh -p %d %s@%s", vo.getPort(), vo.getUsername(), vo.getHost()));
                }
                return vo;
            }
        } catch (Exception e) {
            log.warn("通过调度器获取SSH连接信息失败，回退到本地数据: {}", e.getMessage());
        }

        // 回退到本地数据库
        LambdaQueryWrapper<SysCustomerGpuPodEntity> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(SysCustomerGpuPodEntity::getInstanceId, id)
                .eq(SysCustomerGpuPodEntity::getCustomerId, getCustomerId());
        SysCustomerGpuPodEntity entity = sysCustomerGpuPodMapper.selectOne(wrapper);
        if (entity == null) {
            throw new RuntimeException("实例不存在");
        }

        SshInfoVO vo = new SshInfoVO();
        vo.setHost(entity.getSshHost());
        vo.setPort(entity.getSshPort());
        vo.setUsername(entity.getSshUsername());
        vo.setPassword(entity.getSshPassword());
        if (ObjectUtils.isNotEmpty(vo.getHost()) && vo.getPort() != null && ObjectUtils.isNotEmpty(vo.getUsername())) {
            vo.setCommand(String.format("ssh -p %d %s@%s", vo.getPort(), vo.getUsername(), vo.getHost()));
        }
        return vo;
    }

    @Override
    public VncInfoVO getVncInfo(String id) {
        return gpuPodApiClient.getVncInfo(id, getTenantId());
    }

    @Override
    public String getInstanceLogs(String id, int tailLines) {
        String tenantId = getTenantId();
        return gpuPodApiClient.getInstanceLogs(id, tenantId, tailLines);
    }

    /**
     * 更新本地实例状态
     */
    private void updateLocalStatus(String instanceId, String status) {
        LambdaQueryWrapper<SysCustomerGpuPodEntity> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(SysCustomerGpuPodEntity::getInstanceId, instanceId)
                .eq(SysCustomerGpuPodEntity::getCustomerId, getCustomerId());
        SysCustomerGpuPodEntity entity = sysCustomerGpuPodMapper.selectOne(wrapper);
        if (entity != null) {
            entity.setStatus(status);
            if ("running".equals(status)) {
                entity.setStartTime(new Date());
            } else if ("stopped".equals(status)) {
                entity.setStopTime(new Date());
            }
            sysCustomerGpuPodMapper.updateById(entity);
        }
    }

    /**
     * 将API响应转换为VO
     */
    private GpuPodInstanceVO convertResponseToVO(InstanceResponse response) {
        GpuPodInstanceVO vo = new GpuPodInstanceVO();
        vo.setId(response.getInstanceId());
        vo.setName(response.getInstanceName());
        vo.setRegion(response.getRegion());
        vo.setZone(response.getZone());
        vo.setStatus(response.getStatus());
        vo.setMemory(response.getMemory());
        vo.setImageUrl(response.getImage());
        vo.setBillingType(response.getBillingMode());
        vo.setBillingStatus(response.getBillingStatus());
        vo.setPricePerHour(response.getPricePerHour());

        if (response.getGpuSpec() != null) {
            vo.setGpuType(response.getGpuSpec().getModel());
            vo.setGpuCount(response.getGpuSpec().getCount());
            vo.setGpuMemory(response.getGpuSpec().getMemory());
        }

        if (response.getCpu() != null) {
            vo.setCpuCores(response.getCpu().getCores());
            if (response.getCpu().getModel() != null) {
                vo.setCpu(response.getCpu().getCores() + "核, " + response.getCpu().getModel());
            }
        }

        if (response.getCreateTime() != null) {
            try {
                vo.setCreateTime(LocalDateTime.parse(response.getCreateTime(), DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            } catch (Exception e) {
                log.warn("解析创建时间失败: {}", response.getCreateTime());
            }
        }
        if (response.getStartTime() != null) {
            try {
                vo.setStartTime(LocalDateTime.parse(response.getStartTime(), DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            } catch (Exception e) {
                log.warn("解析启动时间失败: {}", response.getStartTime());
            }
        }
        if (response.getReleaseTime() != null) {
            try {
                vo.setReleaseTime(LocalDateTime.parse(response.getReleaseTime(), DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            } catch (Exception e) {
                log.warn("解析释放时间失败: {}", response.getReleaseTime());
            }
        }
        if (response.getExpireTime() != null) {
            try {
                vo.setExpireTime(LocalDateTime.parse(response.getExpireTime(), DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            } catch (Exception e) {
                log.warn("解析到期时间失败: {}", response.getExpireTime());
            }
        }

        if (response.getSshInfo() != null) {
            SshInfoVO sshVo = new SshInfoVO();
            sshVo.setHost(response.getSshInfo().getHost());
            sshVo.setPort(response.getSshInfo().getPort());
            sshVo.setUsername(response.getSshInfo().getUsername());
            sshVo.setPassword(response.getSshInfo().getPassword());
            sshVo.setCommand(response.getSshInfo().getCommand());
            vo.setSshInfo(sshVo);
        }

        if (response.getHealth() != null) {
            GpuPodInstanceVO.HealthStatus health = new GpuPodInstanceVO.HealthStatus();
            health.setStatus(response.getHealth().getStatus());
            if (response.getHealth().getCpuUsage() != null) {
                health.setCpuUsage(response.getHealth().getCpuUsage().doubleValue());
            }
            if (response.getHealth().getMemoryUsage() != null) {
                health.setMemoryUsage(response.getHealth().getMemoryUsage().doubleValue());
            }
            if (response.getHealth().getGpuUsage() != null) {
                health.setGpuUsage(response.getHealth().getGpuUsage().doubleValue());
            }
            vo.setHealthStatus(health);
        }

        if (response.getDisk() != null) {
            GpuPodInstanceVO.DiskUsage diskUsage = new GpuPodInstanceVO.DiskUsage();
            diskUsage.setSystemDiskUsage(response.getDisk().getSystemDiskUsage());
            diskUsage.setDataDiskUsage(response.getDisk().getDataDiskUsage());
            vo.setDiskUsage(diskUsage);
        }

        if (response.getTools() != null) {
            vo.setTools(response.getTools().stream()
                    .map(t -> {
                        GpuPodInstanceVO.ToolInfo tool = new GpuPodInstanceVO.ToolInfo();
                        tool.setName(t.getName());
                        tool.setUrl(t.getUrl());
                        tool.setIcon(t.getIcon());
                        return tool;
                    })
                    .toList());
        }

        return vo;
    }

    /**
     * 将列表项转换为VO
     */
    private GpuPodInstanceVO convertItemToVO(InstanceListResponse.InstanceItem item) {
        GpuPodInstanceVO vo = new GpuPodInstanceVO();
        vo.setId(item.getInstanceId());
        vo.setName(item.getInstanceName());
        vo.setStatus(item.getStatus());
        vo.setRegion(item.getRegion());
        vo.setZone(item.getZone());
        vo.setBillingType(item.getBillingMode());
        vo.setBillingStatus(item.getBillingStatus());
        vo.setGpuType(item.getGpuSpec());
        vo.setGpuCount(item.getGpuCount());
        vo.setCpu(item.getCpu());
        vo.setCpuCores(parseCpuCores(item.getCpu()));
        vo.setMemory(item.getMemory());

        if (item.getCreateTime() != null) {
            try {
                vo.setCreateTime(LocalDateTime.parse(item.getCreateTime(), DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            } catch (Exception e) {
                log.warn("解析创建时间失败: {}", item.getCreateTime());
            }
        }
        if (item.getExpireTime() != null) {
            try {
                vo.setExpireTime(LocalDateTime.parse(item.getExpireTime(), DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            } catch (Exception e) {
                log.warn("解析到期时间失败: {}", item.getExpireTime());
            }
        }
        if (item.getReleaseTime() != null) {
            try {
                vo.setReleaseTime(LocalDateTime.parse(item.getReleaseTime(), DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            } catch (Exception e) {
                log.warn("解析释放时间失败: {}", item.getReleaseTime());
            }
        }

        if (item.getSshInfo() != null) {
            SshInfoVO sshVo = new SshInfoVO();
            sshVo.setHost(item.getSshInfo().getHost());
            sshVo.setPort(item.getSshInfo().getPort());
            sshVo.setCommand(item.getSshInfo().getCommand());
            vo.setSshInfo(sshVo);
        }

        if (item.getHealth() != null) {
            GpuPodInstanceVO.HealthStatus health = new GpuPodInstanceVO.HealthStatus();
            health.setStatus(item.getHealth().getStatus());
            if (item.getHealth().getCpuUsage() != null) {
                health.setCpuUsage(item.getHealth().getCpuUsage().doubleValue());
            }
            if (item.getHealth().getMemoryUsage() != null) {
                health.setMemoryUsage(item.getHealth().getMemoryUsage().doubleValue());
            }
            if (item.getHealth().getGpuUsage() != null) {
                health.setGpuUsage(item.getHealth().getGpuUsage().doubleValue());
            }
            vo.setHealthStatus(health);
        }

        if (item.getDisk() != null) {
            GpuPodInstanceVO.DiskUsage diskUsage = new GpuPodInstanceVO.DiskUsage();
            diskUsage.setSystemDiskUsage(item.getDisk().getSystemDiskUsage());
            diskUsage.setDataDiskUsage(item.getDisk().getDataDiskUsage());
            vo.setDiskUsage(diskUsage);
        }

        if (item.getTools() != null) {
            vo.setTools(item.getTools().stream()
                    .map(t -> {
                        GpuPodInstanceVO.ToolInfo tool = new GpuPodInstanceVO.ToolInfo();
                        tool.setName(t.getName());
                        tool.setUrl(t.getUrl());
                        tool.setIcon(t.getIcon());
                        return tool;
                    })
                    .toList());
        }

        return vo;
    }

    /**
     * 将实体转换为VO
     */
    private GpuPodInstanceVO convertEntityToVO(SysCustomerGpuPodEntity entity) {
        GpuPodInstanceVO vo = new GpuPodInstanceVO();
        vo.setId(entity.getInstanceId());
        vo.setName(entity.getInstanceName());
        vo.setRegion(entity.getRegionCode());
        vo.setRegionName(entity.getRegionName());
        vo.setZone(entity.getZoneCode());
        vo.setZoneName(entity.getZoneName());
        vo.setStatus(entity.getStatus());
        vo.setGpuType(entity.getGpuModel());
        vo.setGpuCount(entity.getGpuCount());
        vo.setGpuMemory(entity.getGpuMemory());
        vo.setCpuCores(entity.getCpuCores());
        vo.setMemory(entity.getMemorySize());
        vo.setSystemDisk(entity.getSystemDiskSize());
        vo.setDataDisk(entity.getDataDiskSize());
        vo.setImageUrl(entity.getImageUrl());
        vo.setBillingType(entity.getBillingMode());
        vo.setPricePerHour(entity.getPricePerHour());

        if (entity.getStartTime() != null) {
            vo.setStartTime(LocalDateTime.ofInstant(entity.getStartTime().toInstant(), ZoneId.systemDefault()));
        }
        if (entity.getStopTime() != null) {
            vo.setStopTime(LocalDateTime.ofInstant(entity.getStopTime().toInstant(), ZoneId.systemDefault()));
        }
        if (entity.getExpireTime() != null) {
            vo.setExpireTime(LocalDateTime.ofInstant(entity.getExpireTime().toInstant(), ZoneId.systemDefault()));
        }
        if (entity.getScheduledShutdownTime() != null) {
            vo.setScheduledShutdownTime(LocalDateTime.ofInstant(entity.getScheduledShutdownTime().toInstant(), ZoneId.systemDefault()));
        }

        if (ObjectUtils.isNotEmpty(entity.getSshHost())) {
            SshInfoVO sshVo = new SshInfoVO();
            sshVo.setHost(entity.getSshHost());
            sshVo.setPort(entity.getSshPort());
            sshVo.setUsername(entity.getSshUsername());
            sshVo.setPassword(entity.getSshPassword());
            vo.setSshInfo(sshVo);
        }

        return vo;
    }

    /**
     * 解析CPU核心数
     */
    private Integer parseCpuCores(String cpu) {
        if (ObjectUtils.isEmpty(cpu)) {
            return null;
        }
        try {
            return Integer.parseInt(cpu.replaceAll("[^0-9]", ""));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 解析磁盘大小（GB）
     */
    private Integer parseDiskSize(String disk) {
        if (ObjectUtils.isEmpty(disk)) {
            return null;
        }
        try {
            String num = disk.replaceAll("[^0-9]", "");
            return Integer.parseInt(num);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
