package com.lingyang.cloud.api.controller.system;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.lingyang.cloud.entity.SysNetworkValueEntity;
import com.lingyang.cloud.entity.SysOrderEntity;
import com.lingyang.cloud.mapper.SysNetworkProductMapper;
import com.lingyang.cloud.mapper.SysNetworkValueMapper;
import com.lingyang.cloud.mapper.SysOrderMapper;
import com.lingyang.cloud.model.dto.SysNetworkProductValueDTO;
import com.lingyang.cloud.model.dto.feilian.VpnInfoDTO;
import com.lingyang.cloud.model.query.home.SysNetworkValueQuery;
import com.lingyang.cloud.model.vo.feilian.AddVpnIpVO;
import com.lingyang.cloud.model.vo.product.SysOpenProductUserPwdVO;
import com.lingyang.cloud.model.vo.product.SysOpenProductVO;
import com.lingyang.cloud.service.SysNetworkValueService;
import com.lingyang.cloud.utils.FeiLianUtils;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static com.lingyang.cloud.enums.source.SourceChargeTypeEnum.*;

@RestController
@RequestMapping("/system/network-value")
@Tag(name = "后台系统-产品咨询记录")
@Slf4j
public class SysNetworkValueController {

    @Autowired
    private SysNetworkValueService sysNetworkValueService;

    @Autowired
    private SysNetworkValueMapper sysNetworkValueMapper;


    @Autowired
    private SysNetworkProductMapper sysNetworkProductMapper;

    @Autowired
    private SysOrderMapper sysOrderMapper;

    @Resource
    private FeiLianUtils flashLianUtils;

    @Operation(summary = "删除")
    @GetMapping("/delete")
    public Result<Void> delete(@RequestParam(value = "id") Long id) {
        sysNetworkValueMapper.deleteById(id);
        return Result.success();
    }

    @Operation(summary = "获取分页列表", parameters = {
            @Parameter(name = PageQuery.PAGE_NO_NAME, description = "当前页，默认为 = 1", in = ParameterIn.QUERY),
            @Parameter(name = PageQuery.PAGE_SIZE_NAME, description = "当前页数量，默认为 = 10", in = ParameterIn.QUERY)
    })
    @GetMapping("/page")
    public Result<PageResult<SysNetworkValueEntity>> page(SysNetworkValueQuery query) {
        return sysNetworkValueService.getPage(PageQuery.build(query));
    }

    @Operation(summary = "产品详情")
    @GetMapping("/product/detail")
    public Result<SysNetworkProductValueDTO> getProductDetail(@RequestParam Long id) {
        SysNetworkProductValueDTO entity = sysNetworkValueService.getProductDetail(id);
        return Result.success(entity);
    }

    @PostMapping("/product/open")
    @Operation(summary = "开通产品")
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> openProduct(@RequestBody SysOpenProductVO vo) {
        SysNetworkValueEntity valueEntity = sysNetworkValueMapper.selectById(vo.getId());
        if (valueEntity.getActualStatus() == null) {
            return Result.error("开通状态错误");
        }
        if (valueEntity.getActualStatus().equals(3)) {
            return Result.error("产品已过期");
        }
        List<SysOpenProductUserPwdVO> userPwdList = vo.getUserPwdList();
        if (valueEntity.getActualStatus().equals(1)) {
            Date date = new Date();
            String jsonString = JSONObject.toJSONString(userPwdList);
            LocalDate currentDate = LocalDate.now();
            long days = 0;
            if (valueEntity.getChargeType() == POSTPAID_BY_HOUR){
                sysNetworkValueMapper.update(null,
                        new LambdaUpdateWrapper<SysNetworkValueEntity>()
                                .set(SysNetworkValueEntity::getActualStatus, 2)
                                .set(SysNetworkValueEntity::getActualAgiOpenTime, date)
                                .set(SysNetworkValueEntity::getUserpwdList, jsonString)
                                .set(SysNetworkValueEntity::getIpAddress, vo.getIpAddress())
                                .eq(SysNetworkValueEntity::getId, vo.getId())

                );
                sysOrderMapper.update(null,
                        new LambdaUpdateWrapper<SysOrderEntity>()
                                .set(SysOrderEntity::getActualStatus, 2)
                                .set(SysOrderEntity::getActualAgiOpenTime, date)
                                .eq(SysOrderEntity::getNetworkValueId, valueEntity.getId())

                );
            }else {
                LocalDateTime openDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                LocalDateTime expireDateTime;
                if (valueEntity.getChargeType() == POSTPAID_BY_MONTH) {
                    expireDateTime = openDateTime.plusMonths(valueEntity.getDuration());
                } else if (valueEntity.getChargeType() == POSTPAID_BY_YEAR) {
                    expireDateTime = openDateTime.plusYears(valueEntity.getDuration());
                } else {
                    expireDateTime = openDateTime;
                }
                expireDateTime = expireDateTime.withHour(23).withMinute(59).withSecond(59).withNano(0);
                Date expireDate = Date.from(expireDateTime.atZone(ZoneId.systemDefault()).toInstant());

                sysNetworkValueMapper.update(null,
                        new LambdaUpdateWrapper<SysNetworkValueEntity>()
                                .set(SysNetworkValueEntity::getActualStatus, 2)
                                .set(SysNetworkValueEntity::getActualAgiOpenTime, date)
                                .set(SysNetworkValueEntity::getUserpwdList, jsonString)
                                .set(SysNetworkValueEntity::getActualAgiExpireTime, expireDate)
                                .set(SysNetworkValueEntity::getIpAddress, vo.getIpAddress())
                                .eq(SysNetworkValueEntity::getId, vo.getId())

                );
                sysOrderMapper.update(null,
                        new LambdaUpdateWrapper<SysOrderEntity>()
                                .set(SysOrderEntity::getActualStatus, 2)
                                .set(SysOrderEntity::getActualAgiOpenTime, date)
                                .set(SysOrderEntity::getActualAgiExpireTime, expireDate)
                                .eq(SysOrderEntity::getNetworkValueId, valueEntity.getId())

                );
            }
        }

        if (valueEntity.getActualStatus().equals(2)) {
            JSONArray userpwdList = valueEntity.getUserpwdList();
            List<SysOpenProductUserPwdVO> userPwdList2 = JSON.parseArray(userpwdList.toJSONString(), SysOpenProductUserPwdVO.class);
            List<SysOpenProductUserPwdVO> filteredList = new java.util.ArrayList<>(userPwdList2.stream()
                    .filter(vo2 -> vo2.getIp() != null && !vo2.getIp().isEmpty())
                    .toList());
            filteredList.addAll(userPwdList);
            String jsonString = JSONObject.toJSONString(filteredList);
            sysNetworkValueMapper.update(null,
                    new LambdaUpdateWrapper<SysNetworkValueEntity>()
                            .set(SysNetworkValueEntity::getUserpwdList, jsonString)
                            .eq(SysNetworkValueEntity::getId, vo.getId())

            );
        }
        //给飞连用户添加预留IP
        //获取VpnId
        List<VpnInfoDTO> vpnList = flashLianUtils.getVpnList();
        VpnInfoDTO vpnInfoDTO = vpnList.get(0);
        log.info("userPwdList：{}",userPwdList);
        for (SysOpenProductUserPwdVO sysOpenProductUserPwdVO : userPwdList) {
            //获取飞连用户id
            JSONObject userInfo = flashLianUtils.getUserInfo(sysOpenProductUserPwdVO.getEmail());
            String userId = userInfo.get("id").toString();
            AddVpnIpVO vo2 = new AddVpnIpVO();
            vo2.setVpnId(vpnInfoDTO.getId());//需要去查询当前节点的vpnId
            vo2.setFixedIps(new String[]{sysOpenProductUserPwdVO.getIp()});
            vo2.setUserIds(new String[]{userId});
            log.info("AddVpnIpVO:{}", vo2);
            flashLianUtils.addVpnIp(vo2);
            log.info("添加IP成功:{}",  sysOpenProductUserPwdVO.getIp());
        }
        return Result.success();
    }
}
