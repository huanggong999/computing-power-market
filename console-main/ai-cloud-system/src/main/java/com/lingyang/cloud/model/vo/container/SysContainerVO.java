package com.lingyang.cloud.model.vo.container;

import com.lingyang.cloud.entity.SysCustomerSecurityGroupEntity;
import com.lingyang.cloud.entity.SysCustomerSubnetEntity;
import lombok.Data;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/2/11 15:57
 */
@Data
public class SysContainerVO {

    private SysCustomerSubnetEntity customerSubnet;

    private SysCustomerSecurityGroupEntity securityGroupEntity;
}
