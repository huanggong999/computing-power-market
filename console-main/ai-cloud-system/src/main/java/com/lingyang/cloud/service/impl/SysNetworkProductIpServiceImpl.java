package com.lingyang.cloud.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lingyang.cloud.entity.SysNetworkProductIpEntity;
import com.lingyang.cloud.entity.SysNetworkValueEntity;
import com.lingyang.cloud.mapper.SysCustomerMapper;
import com.lingyang.cloud.mapper.SysNetworkProductIpMapper;
import com.lingyang.cloud.mapper.SysNetworkValueMapper;
import com.lingyang.cloud.model.dto.excelDto.SysProductIpExportDto;
import com.lingyang.cloud.model.dto.feilian.VpnFixedInfoDTO;
import com.lingyang.cloud.model.dto.feilian.VpnInfoDTO;
import com.lingyang.cloud.model.query.home.SysProductIpQuery;
import com.lingyang.cloud.model.vo.feilian.AddVpnIpVO;
import com.lingyang.cloud.model.vo.feilian.VpnIpVO;
import com.lingyang.cloud.model.vo.product.SysOpenProductUserPwdVO;
import com.lingyang.cloud.model.vo.product.SysProductIpExportVO;
import com.lingyang.cloud.service.SysNetworkProductIpService;
import com.lingyang.cloud.utils.FeiLianUtils;
import com.lingyang.common.cache.CacheQueueService;
import com.lingyang.common.cache.queue.QueueMessageBody;
import com.lingyang.common.core.exception.http.HttpServiceException;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;
import com.lingyang.common.web.excel.model.ExcelSheetModel;
import com.lingyang.common.web.excel.utils.ExcelUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.lingyang.cloud.common.constant.CacheQueueConstant.AGI_C_IP_QUEUE_TYPE;

/**
 * @author Administrator
 */
@Service
@Slf4j
public class SysNetworkProductIpServiceImpl implements SysNetworkProductIpService {

    @Resource
    private SysNetworkProductIpMapper systemNetworkProductIpMapper;

    @Resource
    private SysCustomerMapper sysCustomerMapper;

    @Resource
    private FeiLianUtils feiLianUtils;

    @Resource
    private CacheQueueService cacheQueueService;

    @Resource
    private SysNetworkValueMapper sysNetworkValueMapper;

    @Override
    public Result<PageResult<SysNetworkProductIpEntity>> getPage(PageQuery<SysProductIpQuery> pageQuery) {
        SysProductIpQuery query = pageQuery.getQuery();
        pageQuery.startPage();
        List<SysNetworkProductIpEntity> value = systemNetworkProductIpMapper.getPage(query);
        log.info("查询IP列表结果: {}", value);
        return Result.success(PageResult.of(value));
    }

    @Override
    public void save(SysNetworkProductIpEntity entity) {
        SysNetworkProductIpEntity sysNetworkProductIpEntity = systemNetworkProductIpMapper.selectOne(Wrappers.lambdaQuery(SysNetworkProductIpEntity.class)
                .eq(SysNetworkProductIpEntity::getIp, entity.getIp())
                .eq(SysNetworkProductIpEntity::getProductId, entity.getProductId()));
        if (sysNetworkProductIpEntity != null) {
            throw new HttpServiceException("IP已存在，请重新输入");
        }
        entity.setCreateTime(new Date());
        systemNetworkProductIpMapper.insert(entity);
    }

    @Override
    public void update(SysNetworkProductIpEntity entity) {
        SysNetworkProductIpEntity sysNetworkProductIpEntity = systemNetworkProductIpMapper.selectById(entity.getId());
        if (sysNetworkProductIpEntity.getStatus() != 0 && entity.getStatus() == 0){
            log.info("进来了3---------");
            if (sysNetworkProductIpEntity.getStatus() == 2){
                log.info("进来了2---------");
                for (Object queue : cacheQueueService.getAllQueue()) {
                    if (queue instanceof QueueMessageBody messageBody) {
                        if (messageBody.getMessageType().equals(AGI_C_IP_QUEUE_TYPE) && messageBody.getBody().equals(sysNetworkProductIpEntity.getIp())) {
                            cacheQueueService.remove(messageBody);
                        }
                    }
                }
            }else if (sysNetworkProductIpEntity.getStatus() == 1){
                log.info("进来了1---------");
                deleteIp(sysNetworkProductIpEntity);
            }
            systemNetworkProductIpMapper.update(null,
                    Wrappers.lambdaUpdate(SysNetworkProductIpEntity.class)
                            .set(SysNetworkProductIpEntity::getUserId, null)
                            .set(SysNetworkProductIpEntity::getEmail, null)
                            .eq(SysNetworkProductIpEntity::getId, entity.getId())
            );
        }else if (sysNetworkProductIpEntity.getStatus() == 1 && entity.getStatus() == 1){
            if (!sysNetworkProductIpEntity.getIp().equals(entity.getIp()) || !sysNetworkProductIpEntity.getPublicIp().equals(entity.getPublicIp())){
                SysNetworkValueEntity sysNetworkValueEntity = sysNetworkValueMapper.selectOne(Wrappers.lambdaQuery(SysNetworkValueEntity.class)
                        .like(SysNetworkValueEntity::getEmail, sysNetworkProductIpEntity.getEmail())
                        .eq(SysNetworkValueEntity::getActualStatus, 2)
                        .eq(SysNetworkValueEntity::getPayStatus ,1));
                if (sysNetworkValueEntity != null){
                    List<SysOpenProductUserPwdVO> userPwdList = JSON.parseArray(sysNetworkValueEntity.getUserpwdList().toJSONString(), SysOpenProductUserPwdVO.class);
                    for (SysOpenProductUserPwdVO sysOpenProductUserPwdVO : userPwdList) {
                        if (sysOpenProductUserPwdVO.getEmail().equals(sysNetworkProductIpEntity.getEmail())){
                            sysOpenProductUserPwdVO.setIp(entity.getIp());
                            sysOpenProductUserPwdVO.setPublicIp(entity.getPublicIp());

                            if (!sysNetworkProductIpEntity.getIp().equals(entity.getIp())){

                                Long aLong = systemNetworkProductIpMapper.selectCount(Wrappers.lambdaQuery(SysNetworkProductIpEntity.class)
                                        .eq(SysNetworkProductIpEntity::getIp, entity.getIp()));
                                if (aLong > 0){
                                    throw new HttpServiceException("IP已存在，请重新输入");
                                }

                                //替换飞连预留IP
                                //先删除旧IP
                                deleteIp(sysNetworkProductIpEntity);
                                //添加新IP
                                //获取VpnId
                                List<VpnInfoDTO> vpnList = feiLianUtils.getVpnList();
                                VpnInfoDTO vpnInfoDTO = vpnList.get(0);

                                //查询用户的部门
                                JSONObject userInfo = feiLianUtils.getUserInfo(sysNetworkProductIpEntity.getEmail());
                                Object departments = userInfo.get("departments");
                                if (departments instanceof JSONArray deptArray && !deptArray.isEmpty()) {
                                    // 优先选择主部门
                                    JSONObject targetDept = deptArray.stream()
                                            .filter(obj -> obj instanceof JSONObject)
                                            .map(obj -> (JSONObject) obj)
                                            .filter(dept -> dept.getBooleanValue("main_department"))
                                            .findFirst()
                                            .orElse(deptArray.getJSONObject(0)); // 没有主部门则取第一个

                                    String departmentId = targetDept.getString("department_id");

                                    AddVpnIpVO vo3 = new AddVpnIpVO();
                                    vo3.setVpnId(vpnInfoDTO.getId());
                                    vo3.setFixedIps(new String[]{entity.getIp()});
                                    vo3.setDepartmentIds(new String[]{departmentId});
                                    log.info("AddVpnIpVO:{}", vo3);
                                    feiLianUtils.addVpnIp(vo3);
                                    log.info("添加IP成功:{}", entity.getIp());
                                } else {
                                    log.warn("departments 字段格式异常或为空");
                                }
                            }
                        }
                    }
                    String jsonString = JSONObject.toJSONString(userPwdList);
                    sysNetworkValueMapper.update(null,
                            new LambdaUpdateWrapper<SysNetworkValueEntity>()
                                    .set(SysNetworkValueEntity::getUserpwdList, jsonString)
                                    .eq(SysNetworkValueEntity::getId, sysNetworkValueEntity.getId())
                    );
                }
            }

        }
        systemNetworkProductIpMapper.update(null,
                Wrappers.lambdaUpdate(SysNetworkProductIpEntity.class)
                        .set(SysNetworkProductIpEntity::getIp,entity.getIp())
                        .set(SysNetworkProductIpEntity::getPublicIp,entity.getPublicIp())
                        .set(SysNetworkProductIpEntity::getStatus,entity.getStatus())
                        .set(SysNetworkProductIpEntity::getUpdateTime, new Date())
                        .eq(SysNetworkProductIpEntity::getId, entity.getId())
        );
    }

    private void deleteIp(SysNetworkProductIpEntity sysNetworkProductIpEntity) {
        //删除飞连VPN节点预留IP
        List<Integer> vpnIps = new ArrayList<>();
        //获取VpnId
        List<VpnInfoDTO> vpnList = feiLianUtils.getVpnList();
        VpnInfoDTO vpnInfoDTO = vpnList.get(0);
        VpnIpVO vo =  new VpnIpVO();
        vo.setVpnId(vpnInfoDTO.getId());//需要填写VPN ID
        String userId = feiLianUtils.getUserInfo(sysNetworkProductIpEntity.getEmail()).get("id").toString();
        vo.setUserId(userId);
        List<VpnFixedInfoDTO> vpnIps1 = feiLianUtils.getVpnIps(vo);
        if (vpnIps1.size() > 0){
            List<Integer> list = vpnIps1.stream()
                    .filter(i -> Arrays.stream(i.getFix_ip()).anyMatch(ip -> ip.equals(sysNetworkProductIpEntity.getIp())))
                    .map(VpnFixedInfoDTO::getId)
                    .toList();
            vpnIps.addAll(list);
        }
        if (CollectionUtils.isNotEmpty(vpnIps)){
            Integer[] array = vpnIps.toArray(new Integer[0]);
            feiLianUtils.deleteVpnIp(array);
            log.info("删除飞连VPN节点预留IP: {} ", vpnIps);
        }
    }

    @Override
    public void handlerCustomerAgicIp(String ip) {
        log.info("处理客户等待中的AGIC IP: {}", ip);
        systemNetworkProductIpMapper.update(null, Wrappers.lambdaUpdate(SysNetworkProductIpEntity.class)
                .set(SysNetworkProductIpEntity::getStatus, 0)
                .set(SysNetworkProductIpEntity::getUpdateTime, new Date())
                .set(SysNetworkProductIpEntity::getUserId, null)
                .set(SysNetworkProductIpEntity::getEmail, null)
                .eq(SysNetworkProductIpEntity::getIp, ip));
    }

    @Override
    public void export(SysProductIpExportVO vo) {
        List<SysProductIpExportDto> ips = systemNetworkProductIpMapper.getIpList(vo);
        // 创建 ExcelSheetModel
        ExcelSheetModel<SysProductIpExportDto> sheetModel = new ExcelSheetModel<>("ip列表", ips);

        // 创建包含 sheetModel 的列表
        List<ExcelSheetModel<SysProductIpExportDto>> sheetModels = new ArrayList<>();
        sheetModels.add(sheetModel);

        // 调用 exportExcel 方法
        ExcelUtils.exportExcel("ip.xlsx", sheetModels, SysProductIpExportDto.class);
    }
}
