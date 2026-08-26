package com.lingyang.cloud.api.service.applets;

import com.lingyang.cloud.model.dto.AppletsConsoleListDTO;
import com.lingyang.cloud.model.dto.AppletsIncomeDTO;
import com.lingyang.cloud.model.vo.applets.AppletsIncomeListVO;
import com.lingyang.cloud.model.vo.applets.AppletsLoginBindVO;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/3/7 10:42
 */
public interface AppletsConsoleService {
    AppletsConsoleListDTO list();

    boolean bindPhone(AppletsLoginBindVO vo);

    AppletsIncomeDTO incomeList(AppletsIncomeListVO vo);
}
