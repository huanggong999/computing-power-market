package com.lingyang.cloud.api.controller.pc;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lingyang.cloud.entity.SysDocument;
import com.lingyang.cloud.entity.SysDocumentType;
import com.lingyang.cloud.enums.system.StatusEnum;
import com.lingyang.cloud.mapper.SysDocumentMapper;
import com.lingyang.cloud.mapper.SysDocumentTypeMapper;
import com.lingyang.cloud.service.SysDocumentService;
import com.lingyang.common.core.model.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@Tag(name = "pc端-文档")
@RequestMapping("/pc/document")
public class PcDocumentController {

    @Autowired
    private SysDocumentService sysDocumentService;

    @Autowired
    private SysDocumentMapper sysDocumentMapper;


    @Autowired
    private SysDocumentTypeMapper sysDocumentTypeMapper;

    @Operation(summary = "文档分类列表")
    @GetMapping("/type-list")
    public Result<List<SysDocumentType>> typeList(@RequestParam(required = false) String name) {
        List<SysDocumentType> documentTypes = sysDocumentTypeMapper.selectList(
                new LambdaQueryWrapper<SysDocumentType>()
                        .eq(SysDocumentType::getStatus, StatusEnum.OK)
                        .orderByAsc(SysDocumentType::getSort)
        );
        if (StringUtils.isNotBlank(name)) {
            List<SysDocumentType> ps = sysDocumentTypeMapper.selectList(
                    new LambdaQueryWrapper<SysDocumentType>()
                            .eq(SysDocumentType::getStatus, StatusEnum.OK)
                            .like(SysDocumentType::getName, name)
                            .orderByAsc(SysDocumentType::getSort)
            );
            if (CollectionUtils.isNotEmpty(ps)) {
                Set<Long> ids = new HashSet<>();
                for (SysDocumentType p : ps) {
                    ids.add(p.getId());
                    // 查询当前分类下所有的子分类
                    if (p.getLevel().equals(1)) {
                        List<SysDocumentType> types2 = sysDocumentTypeMapper.selectList(
                                new LambdaQueryWrapper<SysDocumentType>()
                                        .eq(SysDocumentType::getStatus, StatusEnum.OK)
                                        .eq(SysDocumentType::getParentId, p.getId())
                                        .orderByAsc(SysDocumentType::getSort)
                        );

                        if ( CollectionUtils.isNotEmpty(types2)) {
                            for (SysDocumentType type2 : types2) {
                                ids.add(type2.getId());
                                List<SysDocumentType> types3 = sysDocumentTypeMapper.selectList(
                                        new LambdaQueryWrapper<SysDocumentType>()
                                                .eq(SysDocumentType::getStatus, StatusEnum.OK)
                                                .eq(SysDocumentType::getParentId, type2.getId())
                                                .orderByAsc(SysDocumentType::getSort)
                                );
                                if (CollectionUtils.isNotEmpty(types3)) {
                                    for (SysDocumentType type3 : types3) {
                                        ids.add(type3.getId());
                                        List<SysDocumentType> types4 = sysDocumentTypeMapper.selectList(
                                                new LambdaQueryWrapper<SysDocumentType>()
                                                        .eq(SysDocumentType::getStatus, StatusEnum.OK)
                                                        .eq(SysDocumentType::getParentId, type3.getId())
                                                        .orderByAsc(SysDocumentType::getSort)
                                        );
                                        if (CollectionUtils.isNotEmpty(types4)) {
                                            for (SysDocumentType type4 : types4) {
                                                ids.add(type4.getId());
                                            }
                                        }
                                    }
                                }
                            }
                        }

                    } else if (p.getLevel().equals(2)) {
                        ids.add(p.getParentId());

                        List<SysDocumentType> types3 = sysDocumentTypeMapper.selectList(
                                new LambdaQueryWrapper<SysDocumentType>()
                                        .eq(SysDocumentType::getStatus, StatusEnum.OK)
                                        .eq(SysDocumentType::getParentId, p.getId())
                                        .orderByAsc(SysDocumentType::getSort)
                        );
                        if (CollectionUtils.isNotEmpty(types3)) {
                            for (SysDocumentType type3 : types3) {
                                ids.add(type3.getId());
                                List<SysDocumentType> types4 = sysDocumentTypeMapper.selectList(
                                        new LambdaQueryWrapper<SysDocumentType>()
                                                .eq(SysDocumentType::getStatus, StatusEnum.OK)
                                                .eq(SysDocumentType::getParentId, type3.getId())
                                                .orderByAsc(SysDocumentType::getSort)
                                );
                                if (CollectionUtils.isNotEmpty(types4)) {
                                    for (SysDocumentType type4 : types4) {
                                        ids.add(type4.getId());
                                    }
                                }
                            }
                        }

                    } else if (p.getLevel().equals(3)) {
                        ids.add(p.getParentId());
                        SysDocumentType type2 = sysDocumentTypeMapper.selectById(p.getParentId());
                        SysDocumentType type1 = sysDocumentTypeMapper.selectById(type2.getParentId());

                        ids.add(type1.getId());

                        List<SysDocumentType> types4 = sysDocumentTypeMapper.selectList(
                                new LambdaQueryWrapper<SysDocumentType>()
                                        .eq(SysDocumentType::getStatus, StatusEnum.OK)
                                        .eq(SysDocumentType::getParentId, p.getId())
                                        .orderByAsc(SysDocumentType::getSort)
                        );
                        if (CollectionUtils.isNotEmpty(types4)) {
                            for (SysDocumentType type4 : types4) {
                                ids.add(type4.getId());
                            }
                        }

                    } else if (p.getLevel().equals(4)) {
                        ids.add(p.getParentId());
                        SysDocumentType type3 = sysDocumentTypeMapper.selectById(p.getParentId());
                        SysDocumentType type2 = sysDocumentTypeMapper.selectById(type3.getParentId());
                        SysDocumentType type1 = sysDocumentTypeMapper.selectById(type2.getParentId());

                        ids.add(type2.getId());
                        ids.add(type1.getId());

                    }
                }

                List<SysDocumentType> typeList = sysDocumentTypeMapper.selectList(
                        new LambdaQueryWrapper<SysDocumentType>()
                                .in(SysDocumentType::getId, ids)
                                .eq(SysDocumentType::getStatus, StatusEnum.OK)
                                .orderByAsc(SysDocumentType::getSort)
                );
                return Result.success(ls(typeList));
            }
            return Result.success(new ArrayList<>());
        } else {
            return Result.success(ls(documentTypes));
        }

    }


    public static List<SysDocumentType> ls (List<SysDocumentType> documentTypes) {
        List<SysDocumentType> d = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(documentTypes)) {

            List<SysDocumentType> types1 = documentTypes.stream().filter(s -> {
                return s.getLevel().equals(1);
            }).collect(Collectors.toList());
            d = types1;
            d.forEach(s -> {
                List<SysDocumentType> types2 = documentTypes.stream().filter(s1 -> {
                    return s1.getParentId().equals(s.getId());
                }).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(types2)) {
                    s.setChildren(types2);
                    types2.forEach(s1 -> {
                        List<SysDocumentType> types3 = documentTypes.stream().filter(s2 -> {
                            return s2.getParentId().equals(s1.getId());
                        }).collect(Collectors.toList());
                        if (!CollectionUtils.isEmpty(types3)) {
                            s1.setChildren(types3);
                            types3.forEach(s2 -> {
                                List<SysDocumentType> types4 = documentTypes.stream().filter(s3 -> {
                                    return s3.getParentId().equals(s2.getId());
                                }).collect(Collectors.toList());
                                if (!CollectionUtils.isEmpty(types4)) {
                                    s2.setChildren(types4);
                                    types4.forEach(s3 -> {
                                        List<SysDocumentType> types5 = documentTypes.stream().filter(s4 -> {
                                            return s4.getParentId().equals(s3.getId());
                                        }).collect(Collectors.toList());
                                        if (!CollectionUtils.isEmpty(types5)) {
                                            s3.setChildren(types5);
                                        }
                                    });
                                }
                            });
                        }
                    });
                }

            });
        }
        return d;
    }

    @Operation(summary = "查询文档分类id下所属的文档")
    @GetMapping("/type/{id}")
    public Result<SysDocument> detailType(@PathVariable("id") Long id) {
        List<SysDocument> documents = sysDocumentMapper.selectList(new LambdaQueryWrapper<SysDocument>()
                .eq(SysDocument::getStatus, StatusEnum.OK)
                .eq(SysDocument::getTypeId, id)
        );
        if (CollectionUtils.isNotEmpty(documents)) {
            return Result.success(documents.get(0));
        }else {
            return Result.success();
        }

    }

    @Operation(summary = "查询文档详情")
    @GetMapping("/{id}")
    public Result<SysDocument> detail(@PathVariable("id") Long id) {
        return Result.success(sysDocumentMapper.selectById(id));
    }



}
