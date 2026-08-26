package com.lingyang.cloud.model.dto.feilian;

import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/6/11 17:04
 */
@Data
public class DepartmentInfoDTO {
    private String id;
    private String name;
    private Integer type;
    private String parent_id;
    private Integer seq;
    private List<DepartmentInfoDTO> sub_departments;
    private String third_party_department_id;
}
