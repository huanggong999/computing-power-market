package com.lingyang.cloud.common.constant;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/11/18 20:51
 */
public interface CacheQueueConstant {

    /**
     * 订单到期未支付取消队列
     */
    String ORDER_EXPIRE_QUEUE_TYPE = "order_expire_queue";
    /**
     * 客户优惠卷到期队列
      */
    String CUSTOMER_COUPON_EXPIRE_QUEUE_TYPE = "customer_coupon_expire_queue";

    /**
     * 客户优惠卷待使用队列
     */
    String CUSTOMER_COUPON_UNUSED_QUEUE_TYPE = "customer_coupon_unused_queue";

    /**
     * 客户服务器创建队列
     */
    String CUSTOMER_INSTANCE_CREATE_QUERY_QUEUE_TYPE = "customer_instance_create_query_queue_type";

    /**
     * 资源结算队列
     */
    String SOURCE_SETTLEMENT_QUEUE_TYPE = "source_settlement_queue";


    /**
     * 客户账单处理
     */
    String CUSTOMER_BILL_HANDLER_QUEUE_TYPE = "customer_bill_handler_queue";

    /**
     * 客户容器集群创建队列
     */
    String CUSTOMER_CONTAINER_CREATE_QUERY_QUEUE_TYPE = "customer_container_create_query_queue_type";

    /**
     * 客户镜像仓库创建队列
     */
    String CUSTOMER_IMAGE_REPOSITORY_QUERY_QUEUE_TYPE = "customer_image_repository_query_queue_type";
    /**
     * 新增nat网关id和负载均衡id的延迟队列
     */
    String NAT_GATEWAY_ID_QUEUE_TYPE = "nat_gateway_id_queue";

    /**
     * 客户产品账单处理
     */
    String CUSTOMER_BILL_PRODUCT_QUEUE_TYPE = "customer_bill_product_queue_type";

    /**
     * 客户产品按天计费账单处理
     */
    String CUSTOMER_DAY_BILL_PRODUCT_QUEUE_TYPE = "customer_day_bill_product_queue_type";

    /**
     * 客户产品按天计费账单处理
     */
    String CUSTOMER_NEW_DAY_BILL_PRODUCT_QUEUE_TYPE = "customer_new_day_bill_product_queue_type";

    /**
     * 客户自建服务器计费账单处理
     */
    String CUSTOMER_BILL_SELF_BUILD_QUEUE_TYPE = "customer_bill_self_build_queue_type";

    /**
     * AGI-C撤线后，IP处理
     */
    String AGI_C_IP_QUEUE_TYPE = "agi_c_ip_queue";

    /**
     * 自建服务器按时计费，每小时进行余额校验，余额不足则暂停服务
     */
    String CUSTOMER_SELF_BUILD_SERVER_HOURLY_CHECK_QUEUE_TYPE = "customer_self_build_server_hourly_check_queue_type";
}
