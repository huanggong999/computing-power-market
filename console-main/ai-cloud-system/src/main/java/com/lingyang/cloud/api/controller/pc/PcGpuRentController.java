package com.lingyang.cloud.api.controller.pc;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lingyang.cloud.client.GpuPodApiClient;
import com.lingyang.cloud.client.GpuPodTenantProvider;
import com.lingyang.cloud.client.GpuSchedulerApiClient;
import com.lingyang.cloud.client.config.GpuPodProperties;
import com.lingyang.cloud.client.dto.GpuPodCreateRequest;
import com.lingyang.cloud.client.dto.GpuPodCreateResponse;
import com.lingyang.cloud.entity.GpuComponentEntity;
import com.lingyang.cloud.entity.GpuResourcePriceEntity;
import com.lingyang.cloud.entity.GpuResourceStockEntity;
import com.lingyang.cloud.entity.SysCustomerEntity;
import com.lingyang.cloud.entity.SysOrderEntity;
import com.lingyang.cloud.entity.SysOrderSourceEntity;
import com.lingyang.cloud.enums.customer.SysTransactionType;
import com.lingyang.cloud.enums.order.OrderStatusEnum;
import com.lingyang.cloud.enums.order.OrderTypeEnum;
import com.lingyang.cloud.enums.source.SourceChargeTypeEnum;
import com.lingyang.cloud.enums.source.SourceChargeUnitEnum;
import com.lingyang.cloud.enums.source.SourceTypeEnum;
import com.lingyang.cloud.mapper.GpuComponentMapper;
import com.lingyang.cloud.mapper.GpuResourcePriceMapper;
import com.lingyang.cloud.mapper.GpuResourceStockMapper;
import com.lingyang.cloud.mapper.SysCustomerMapper;
import com.lingyang.cloud.mapper.SysOrderMapper;
import com.lingyang.cloud.mapper.SysOrderSourceMapper;
import com.lingyang.cloud.service.SysCustomerService;
import com.lingyang.cloud.model.dto.GpuRentCalculateDTO;
import com.lingyang.cloud.model.dto.GpuRentOrderDTO;
import com.lingyang.cloud.model.vo.pc.GpuMirrorItemVO;
import com.lingyang.cloud.model.vo.pc.GpuMirrorVersionVO;
import com.lingyang.cloud.model.vo.pc.GpuRentFeeVO;
import com.lingyang.cloud.model.vo.pc.GpuRentOrderVO;
import com.lingyang.common.core.model.result.Result;
import com.lingyang.common.core.security.SecurityContext;
import com.lingyang.common.core.utils.IdUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/pc/gpu")
@Tag(name = "PC端-GPU租用")
public class PcGpuRentController {

    @Resource
    private GpuResourcePriceMapper gpuResourcePriceMapper;

    @Resource
    private GpuResourceStockMapper gpuResourceStockMapper;

    @Resource
    private GpuComponentMapper gpuComponentMapper;

    @Resource
    private GpuPodApiClient gpuPodApiClient;

    @Resource
    private GpuSchedulerApiClient gpuSchedulerApiClient;

    @Resource
    private GpuPodTenantProvider gpuPodTenantProvider;

    @Resource
    private GpuPodProperties gpuPodProperties;

    @Resource
    private SysOrderMapper sysOrderMapper;

    @Resource
    private SysOrderSourceMapper sysOrderSourceMapper;

    @Resource
    private SysCustomerMapper sysCustomerMapper;

    @Resource
    private SysCustomerService sysCustomerService;

    @GetMapping("/mirror/list")
    @Operation(summary = "GPU镜像列表")
    @PreAuthorize("permitAll()")
    public Result<List<GpuMirrorItemVO>> mirrorList(@RequestParam(required = false) String gpuModel) {
        List<GpuComponentEntity> components = gpuComponentMapper.selectList(Wrappers.lambdaQuery(GpuComponentEntity.class)
                .eq(GpuComponentEntity::getDelFlag, 0)
                .eq(GpuComponentEntity::getStatus, 1)
                .orderByAsc(GpuComponentEntity::getSortOrder)
                .orderByAsc(GpuComponentEntity::getId));
        return Result.success(components.stream().map(this::toMirrorItem).toList());
    }

    @GetMapping("/mirror/{mirrorId}/versions")
    @Operation(summary = "GPU镜像版本")
    @PreAuthorize("permitAll()")
    public Result<List<GpuMirrorVersionVO>> mirrorVersions(@PathVariable String mirrorId) {
        Long componentId;
        try {
            componentId = Long.valueOf(mirrorId);
        } catch (NumberFormatException e) {
            return Result.success(List.of());
        }
        GpuComponentEntity component = gpuComponentMapper.selectOne(Wrappers.lambdaQuery(GpuComponentEntity.class)
                .eq(GpuComponentEntity::getId, componentId)
                .eq(GpuComponentEntity::getDelFlag, 0)
                .eq(GpuComponentEntity::getStatus, 1)
                .last("limit 1"));
        if (component == null) {
            return Result.success(List.of());
        }
        return Result.success(List.of(toMirrorVersion(component)));
    }

    @PostMapping("/rent/calculate")
    @Operation(summary = "GPU租用费用计算")
    public Result<GpuRentFeeVO> calculate(@RequestBody GpuRentCalculateDTO dto) {
        GpuResourceStockEntity stock = gpuResourceStockMapper.selectByResourceId(dto.getResourceId());
        if (stock == null || stock.getAvailableCount() == null || dto.getQuantity() == null || dto.getQuantity() < 1 || stock.getAvailableCount() < dto.getQuantity()) {
            return Result.error("库存不足");
        }

        GpuResourcePriceEntity price = selectPrice(dto.getResourceId(), dto.getBillingType());
        if (price == null) {
            return Result.error("价格不存在");
        }

        int duration = dto.getDuration() == null || dto.getDuration() < 1 ? 1 : dto.getDuration();
        BigDecimal unitPrice = getEffectiveUnitPrice(price);
        BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(dto.getQuantity())).multiply(BigDecimal.valueOf(duration));

        GpuRentFeeVO vo = new GpuRentFeeVO();
        vo.setUnitPrice(unitPrice);
        vo.setQuantity(dto.getQuantity());
        vo.setDuration(duration);
        vo.setSubtotal(subtotal);
        vo.setDiscount(BigDecimal.ZERO);
        vo.setTotal(subtotal);
        return Result.success(vo);
    }

    @PostMapping("/rent/order")
    @Operation(summary = "创建GPU租用订单")
    @Transactional(rollbackFor = Exception.class)
    public Result<GpuRentOrderVO> order(@RequestBody GpuRentOrderDTO dto) {
        //dto = buildMockRentOrderDTO();
        GpuPodCreateRequest request = dto.getPodCreateRequest();
        Result<GpuRentFeeVO> feeResult = dto.getResourceId() != null && dto.getResourceId() == 0
                ? calculateExternal(dto, request)
                : calculate(dto);
        if (feeResult.getData() == null) {
            return Result.error("创建订单失败");
        }

        if (request == null) {
            return Result.error("GPU Pod 创建参数不能为空");
        }
        // Payable amounts must come from the server-side resource price calculation.
        GpuRentFeeVO orderFee = feeResult.getData();
        Long userId = SecurityContext.getUserInfo().getUserId();
        BigDecimal payAmount = orderFee.getTotal() == null ? BigDecimal.ZERO : orderFee.getTotal();
        SysCustomerEntity customer = sysCustomerMapper.selectById(userId);
        BigDecimal balance = customer == null || customer.getBalance() == null ? BigDecimal.ZERO : customer.getBalance();
        if (balance.compareTo(payAmount) < 0) {
            return Result.error("余额不足");
        }
        GpuPodTenantProvider.Tenant tenant = gpuPodTenantProvider.getCurrentTenant();
        request.setPodName(null);
        request.setTenantId(tenant.tenantId());
        request.setTenantName(tenant.tenantName());

        GpuPodCreateResponse podResponse;
        if (gpuPodProperties.isMockCreateEnabled()) {
            podResponse = new GpuPodCreateResponse();
            podResponse.setSuccess(true);
            podResponse.setPodName(request.getPodName());
            podResponse.setMessage("mock success");
        } else {
            podResponse = gpuPodApiClient.createGpuPod(request);
        }
        if (podResponse == null || !podResponse.isSuccess()) {
            return Result.error(toCreateOrderErrorMessage(podResponse));
        }

        LocalDateTime now = LocalDateTime.now();
        GpuRentOrderVO vo = new GpuRentOrderVO();
        String podName = podResponse.getPodName();
        String instanceId = StringUtils.defaultIfBlank(podResponse.getInstanceId(), podName);
        SysOrderEntity order = createPaidGpuOrder(dto, orderFee, request, instanceId, podName, podResponse);
        vo.setOrderId(String.valueOf(order.getId()));
        vo.setOrderNo(order.getOrderNo());
        vo.setStatus("processing");
        vo.setTotalAmount(orderFee.getTotal());
        vo.setPaidAmount(orderFee.getTotal());
        vo.setPricing(toOrderPricing(orderFee, podResponse));
        vo.setCreateTime(now);

        GpuRentOrderVO.InstanceInfo instanceInfo = new GpuRentOrderVO.InstanceInfo();
        instanceInfo.setInstanceId(instanceId);
        instanceInfo.setInstanceName(StringUtils.defaultIfBlank(podName, instanceId));
        instanceInfo.setPodName(podName);
        instanceInfo.setTenantId(tenant.tenantId());
        instanceInfo.setStatus("creating");
        vo.setInstanceInfo(instanceInfo);
        return Result.success(vo);
    }

    private Result<GpuRentFeeVO> calculateExternal(GpuRentOrderDTO dto, GpuPodCreateRequest request) {
        if (request == null || request.getGpuSpec() == null) return Result.error("GPU规格参数不能为空");
        String billingType = StringUtils.defaultIfBlank(dto.getBillingType(), "hourly");
        String instanceTypeId = request.getMachineId();
        if (StringUtils.isBlank(request.getZone())) {
            request.setZone(StringUtils.defaultIfBlank(request.getRegion(), "default"));
        }
        BigDecimal unitPrice = gpuSchedulerApiClient.findGpuPrice(
                request.getRegion(), request.getGpuSpec().getModel(), request.getGpuSpec().getGpuMemory(),
                instanceTypeId, billingType);
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) <= 0) return Result.error("价格不存在");
        int quantity = dto.getQuantity() == null || dto.getQuantity() < 1 ? 1 : dto.getQuantity();
        int duration = dto.getDuration() == null || dto.getDuration() < 1 ? 1 : dto.getDuration();
        BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity)).multiply(BigDecimal.valueOf(duration));
        GpuRentFeeVO vo = new GpuRentFeeVO();
        vo.setUnitPrice(unitPrice);
        vo.setQuantity(quantity);
        vo.setDuration(duration);
        vo.setSubtotal(subtotal);
        vo.setDiscount(BigDecimal.ZERO);
        vo.setTotal(subtotal);
        return Result.success(vo);
    }

    private GpuRentOrderVO.Pricing toOrderPricing(GpuRentFeeVO fee, GpuPodCreateResponse response) {
        GpuRentOrderVO.Pricing pricing = new GpuRentOrderVO.Pricing();
        GpuPodCreateResponse.BillingInfo billing = response == null ? null : response.getBillingInfo();
        BigDecimal unitPrice = billing == null || billing.getUnitPrice() <= 0
                ? fee.getUnitPrice() : BigDecimal.valueOf(billing.getUnitPrice());
        BigDecimal discountUnitPrice = billing == null || billing.getDiscountUnitPrice() <= 0
                ? unitPrice : BigDecimal.valueOf(billing.getDiscountUnitPrice());
        BigDecimal totalCost = billing == null || billing.getTotalCost() <= 0
                ? fee.getSubtotal() : BigDecimal.valueOf(billing.getTotalCost());
        BigDecimal discountTotalCost = billing == null || billing.getDiscountTotalCost() <= 0
                ? fee.getTotal() : BigDecimal.valueOf(billing.getDiscountTotalCost());
        pricing.setUnitPrice(unitPrice);
        pricing.setDiscountUnitPrice(discountUnitPrice);
        pricing.setTotalCost(totalCost);
        pricing.setDiscountTotalCost(discountTotalCost);
        pricing.setCurrency(billing == null ? "CNY" : billing.getCurrency());
        pricing.setUnit(billing == null ? null : billing.getUnit());
        return pricing;
    }

    private GpuRentOrderDTO buildMockRentOrderDTO() {
        GpuRentOrderDTO dto = new GpuRentOrderDTO();
        dto.setResourceId(6L);
        dto.setMirrorId("2");
        dto.setMirrorVersionId("2");
        dto.setBillingType("hourly");
        dto.setQuantity(1);
        dto.setDuration(1);
        dto.setAgreeProtocol(true);

        GpuPodCreateRequest request = new GpuPodCreateRequest();

        GpuPodCreateRequest.GpuSpec gpuSpec = new GpuPodCreateRequest.GpuSpec();
        gpuSpec.setModel("RTX 3090");
        gpuSpec.setCount(1);
        gpuSpec.setGpuMemory("24 GB");
        request.setGpuSpec(gpuSpec);

        request.setImage("acs-sjkuu-cn-beijing.cr.volces.com/gpu/base/pytorch-2.8.0-cu12.8:python3.12");

        GpuPodCreateRequest.Billing billing = new GpuPodCreateRequest.Billing();
        billing.setMode("on_demand");
        billing.setDuration(1);
        request.setBilling(billing);

        GpuPodCreateRequest.Resource resource = new GpuPodCreateRequest.Resource();
        resource.setCpu("8");
        resource.setCpuModel("AMD EPYC 7742");
        resource.setMemory("32GB");
        resource.setSystemDisk("500 GB SSD");
        resource.setDataDisk("2 TB NVMe");
        resource.setDataDiskExpandable("4 TB");
        request.setResource(resource);

        GpuPodCreateRequest.Pricing pricing = new GpuPodCreateRequest.Pricing();
        pricing.setUnitPrice(new BigDecimal("5"));
        pricing.setDiscountUnitPrice(new BigDecimal("4.25"));
        pricing.setTotalCost(new BigDecimal("5"));
        pricing.setDiscountTotalCost(new BigDecimal("4.25"));
        pricing.setCurrency("CNY");
        pricing.setUnit("小时");
        pricing.setPricePerHour(new BigDecimal("5"));
        pricing.setDiscountPrice(new BigDecimal("4.25"));
        request.setPricing(pricing);

        request.setRegion("foshan");
        request.setZone("v100");
        request.setMachineId("H07机");
        request.setGpuDriver("535.104.05");
        request.setCudaVersion("12.2");
        dto.setPodCreateRequest(request);
        return dto;
    }

    @GetMapping("/rent/pod/{podName}/status")
    @Operation(summary = "获取GPU Pod创建阶段状态")
    public Result<JSONObject> podStatus(@PathVariable String podName) {
        if (StringUtils.isBlank(podName)) {
            return Result.error("Pod名称不能为空");
        }
        GpuPodTenantProvider.Tenant tenant = gpuPodTenantProvider.getCurrentTenant();
        return Result.success(gpuPodApiClient.getPodStatus(podName, tenant.tenantId()));
    }

    private String toCreateOrderErrorMessage(GpuPodCreateResponse podResponse) {
        if (podResponse == null || StringUtils.isBlank(podResponse.getMessage())) {
            return "实例创建服务暂时不可用，请稍后重试或联系管理员处理";
        }
        String message = podResponse.getMessage();
        if (isTechnicalCreateError(message)) {
            return "实例创建服务暂时不可用，请稍后重试或联系管理员处理";
        }
        // Business failures from the scheduler contain actionable details
        // (for example the currently available GPU models); show them as-is.
        return message;
    }

    private boolean isTechnicalCreateError(String message) {
        String lowerMessage = message.toLowerCase(Locale.ROOT);
        return lowerMessage.contains("http://")
                || lowerMessage.contains("https://")
                || lowerMessage.contains("authkey=")
                || lowerMessage.contains("i/o error")
                || lowerMessage.contains("post request")
                || lowerMessage.contains("get request")
                || lowerMessage.contains("exception")
                || lowerMessage.contains("connection")
                || lowerMessage.contains("timeout")
                || lowerMessage.contains("timed out")
                || lowerMessage.contains("refused")
                || lowerMessage.contains("socket");
    }

    private SysOrderEntity createPaidGpuOrder(GpuRentOrderDTO dto, GpuRentFeeVO fee, GpuPodCreateRequest podRequest, String instanceId,
            String podName, GpuPodCreateResponse podResponse) {
        Long userId = SecurityContext.getUserInfo().getUserId();
        String username = SecurityContext.getUserInfo().getUsername();
        Long orderId = IdUtils.nextId();
        String orderNo = "GPU" + System.currentTimeMillis();
        Date now = new Date();

        BigDecimal unitPrice = fee.getUnitPrice() == null ? BigDecimal.ZERO : fee.getUnitPrice();
        BigDecimal discountUnitPrice = unitPrice;
        BigDecimal originalPrice = fee.getSubtotal() == null ? BigDecimal.ZERO : fee.getSubtotal();
        BigDecimal finalPayAmount = fee.getTotal() == null ? BigDecimal.ZERO : fee.getTotal();
        BigDecimal discount = originalPrice.subtract(finalPayAmount).max(BigDecimal.ZERO);

        SysOrderEntity order = new SysOrderEntity();
        order.setId(orderId);
        order.setOrderNo(orderNo);
        order.setOrderType(OrderTypeEnum.NEW_RESOURCE);
        order.setOriginalPrice(originalPrice);
        order.setPremiumPrice(originalPrice);
        order.setUserDiscountAmount(discount);
        order.setFinalPayAmount(finalPayAmount);
        order.setBalancePayAmount(finalPayAmount);
        order.setOnlinePayAmount(BigDecimal.ZERO);
        order.setCouponAmount(BigDecimal.ZERO);
        order.setVoucherAmount(BigDecimal.ZERO);
        order.setOrderStatus(OrderStatusEnum.PAID);
        order.setPayTime(now);
        order.setCreateById(userId);
        order.setCreateBy(username);
        order.setUpdateById(userId);
        order.setUpdateBy(username);
        order.setDelFlag(0);
        sysOrderMapper.insert(order);

        JSONObject configDetail = JSONObject.from(podRequest);
        configDetail.put("resourceId", dto.getResourceId());
        configDetail.put("mirrorId", dto.getMirrorId());
        configDetail.put("mirrorVersionId", dto.getMirrorVersionId());
        configDetail.put("instanceId", instanceId);
        configDetail.put("podName", podName);
        configDetail.put("podNamespace", podResponse.getPodNamespace());
        configDetail.put("createMessage", podResponse.getMessage());
        JSONObject pricingSnapshot = new JSONObject();
        pricingSnapshot.put("unit_price", unitPrice);
        pricingSnapshot.put("discount_unit_price", discountUnitPrice);
        pricingSnapshot.put("total_cost", originalPrice);
        pricingSnapshot.put("discount_total_cost", finalPayAmount);
        pricingSnapshot.put("currency", "CNY");
        pricingSnapshot.put("unit", getDurationUnit(dto.getBillingType()).name());
        configDetail.put("pricing", pricingSnapshot);

        String gpuModel = podRequest.getGpuSpec() == null ? "" : podRequest.getGpuSpec().getModel();
        SysOrderSourceEntity source = new SysOrderSourceEntity();
        source.setOrderId(orderId);
        source.setOrderNo(orderNo);
        source.setSourceId(instanceId);
        source.setSourceType(SourceTypeEnum.GPU_SERVER);
        source.setSourceName(StringUtils.defaultIfBlank(podName, instanceId));
        source.setProductName("GPU Pod - " + gpuModel);
        source.setProductType(2);
        source.setConfigDetail(configDetail);
        source.setChargeType(getChargeType(dto.getBillingType()));
        source.setDuration(getSourceDuration(dto));
        source.setDurationUnit(getDurationUnit(dto.getBillingType()));
        source.setNumber(dto.getQuantity() == null ? 1 : dto.getQuantity());
        source.setUnitPrice(unitPrice);
        source.setPremiumPrice(unitPrice);
        source.setUserDiscountAmount(unitPrice.subtract(discountUnitPrice).max(BigDecimal.ZERO));
        source.setFinalUnitPrice(discountUnitPrice);
        source.setCouponDiscountAmount(BigDecimal.ZERO);
        source.setVoucherDiscountAmount(BigDecimal.ZERO);
        source.setCreateById(userId);
        source.setCreateBy(username);
        source.setUpdateById(userId);
        source.setUpdateBy(username);
        source.setDelFlag(0);
        sysOrderSourceMapper.insert(source);
        sysCustomerService.updateCustomerBalance(orderId, orderNo, userId, finalPayAmount, SysTransactionType.PAY_DISCOUNT);
        return order;
    }

    private SourceChargeTypeEnum getChargeType(String billingType) {
        return ("on_demand".equals(billingType) || "hourly".equals(billingType))
                ? SourceChargeTypeEnum.POSTPAID_BY_HOUR
                : SourceChargeTypeEnum.POSTPAID_BY_MONTH;
    }

    private SourceChargeUnitEnum getDurationUnit(String billingType) {
        return switch (billingType) {
            case "daily" -> SourceChargeUnitEnum.DAY;
            case "weekly" -> SourceChargeUnitEnum.DAY;
            case "monthly" -> SourceChargeUnitEnum.MONTH;
            default -> SourceChargeUnitEnum.HOUR;
        };
    }

    private int getSourceDuration(GpuRentOrderDTO dto) {
        int duration = dto.getDuration() == null || dto.getDuration() < 1 ? 1 : dto.getDuration();
        return "weekly".equals(dto.getBillingType()) ? duration * 7 : duration;
    }

    private GpuResourcePriceEntity selectPrice(Long resourceId, String billingType) {
        GpuResourcePriceEntity price = gpuResourcePriceMapper.selectByResourceAndBilling(resourceId, billingType);
        if (price == null && "on_demand".equals(billingType)) {
            return gpuResourcePriceMapper.selectByResourceAndBilling(resourceId, "hourly");
        }
        return price;
    }

    private BigDecimal getEffectiveUnitPrice(GpuResourcePriceEntity price) {
        BigDecimal unitPrice = price.getUnitPrice() == null ? BigDecimal.ZERO : price.getUnitPrice();
        BigDecimal discountPrice = price.getDiscountPrice();
        if (discountPrice != null && discountPrice.compareTo(BigDecimal.ZERO) > 0 && discountPrice.compareTo(unitPrice) < 0) {
            return discountPrice;
        }
        return unitPrice;
    }

    private GpuMirrorItemVO toMirrorItem(GpuComponentEntity entity) {
        String type = resolveMirrorType(entity.getComponentName());
        GpuMirrorItemVO vo = new GpuMirrorItemVO();
        vo.setId(String.valueOf(entity.getId()));
        vo.setName(StringUtils.defaultIfBlank(entity.getComponentName(), entity.getBaseImage()));
        vo.setType(type);
        vo.setIcon(type);
        vo.setDescription(StringUtils.defaultIfBlank(entity.getDescription(), entity.getBaseImage()));
        vo.setBaseImage(entity.getBaseImage());
        vo.setImageAddress(entity.getImageAddress());
        vo.setSupportedGpuModels(List.of());
        return vo;
    }

    private GpuMirrorVersionVO toMirrorVersion(GpuComponentEntity entity) {
        String componentId = String.valueOf(entity.getId());
        String baseImage = StringUtils.defaultIfBlank(entity.getBaseImage(), entity.getComponentName());
        GpuMirrorVersionVO vo = new GpuMirrorVersionVO();
        vo.setId(componentId);
        vo.setMirrorId(componentId);
        vo.setVersion(baseImage);
        vo.setCudaVersion(StringUtils.defaultIfBlank(entity.getCudaVersion(), extractVersion(baseImage, "cuda")));
        vo.setPythonVersion(StringUtils.defaultIfBlank(entity.getPythonVersion(), extractVersion(baseImage, "python")));
        vo.setDescription(StringUtils.defaultIfBlank(entity.getDescription(), baseImage));
        vo.setImage(entity.getImageAddress());
        vo.setFrameworks(Map.of(resolveMirrorType(entity.getComponentName()), baseImage));
        return vo;
    }

    private String resolveMirrorType(String componentName) {
        String name = StringUtils.defaultString(componentName).toLowerCase(Locale.ROOT);
        if (name.contains("pytorch")) return "pytorch";
        if (name.contains("tensorflow")) return "tensorflow";
        if (name.contains("miniconda")) return "miniconda";
        if (name.contains("triton")) return "tritonserver";
        if (name.contains("jax")) return "jax";
        return "component";
    }

    private String extractVersion(String text, String prefix) {
        if (StringUtils.isBlank(text)) {
            return "";
        }
        String lower = text.toLowerCase(Locale.ROOT);
        int index = lower.indexOf(prefix);
        if (index < 0) {
            return "";
        }
        String suffix = text.substring(index + prefix.length()).trim();
        if (suffix.startsWith(":") || suffix.startsWith("-") || suffix.startsWith("_")) {
            suffix = suffix.substring(1).trim();
        }
        String[] parts = suffix.split("[^0-9.]+");
        return parts.length > 0 ? parts[0] : "";
    }
}
