package com.lingyang.cloud.mapper;

import com.lingyang.cloud.entity.SysCustomerEntity;
import com.lingyang.common.datasource.model.CustomMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/10/21 15:54
 */
public interface SysCustomerMapper extends CustomMapper<SysCustomerEntity> {

    @Update("update sys_customer set balance = balance + #{balance} where id = #{id}")
    void returnCustomerBalance(SysCustomerEntity customer);

    @Select("SELECT MAX(MOD(account_id, 10000)) FROM sys_customer WHERE account_id LIKE CONCAT(#{yearMonth}, '%')")
    Long selectMaxSeqByYearMonth(@Param("yearMonth") String yearMonth);
}
