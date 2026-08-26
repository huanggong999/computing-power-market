package com.lingyang.cloud.api.controller.pc;

import com.lingyang.cloud.entity.SysApplyTypeEntity;
import com.lingyang.cloud.model.vo.apply.SysApplyTypeListVO;
import com.lingyang.cloud.service.SysApplyTypeService;
import com.lingyang.common.core.model.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "pc端-应用分类")
@RestController
@RequestMapping("/pc/apply-type")
public class PcApplyTypeController {

    @Autowired
    private SysApplyTypeService sysApplyTypeService;


    @Operation(summary = "所有分类")
    @GetMapping("/getAll")
    public Result<List<SysApplyTypeListVO>> getAll() {
        return Result.success(sysApplyTypeService.getAll());
    }

}
