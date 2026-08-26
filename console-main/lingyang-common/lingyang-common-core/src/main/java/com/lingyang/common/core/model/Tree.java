package com.lingyang.common.core.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2023/8/11 18:13
 */
@Data
public abstract class Tree<T extends Tree<T>> {
    /**
     * id
     */
    @Schema(description = "id")
    private Long id;

    /**
     * 父级id
     */
    @Schema(description = "父级id")
    private Long parentId;

    /**
     * 下级列表
     */
    @Schema(description = "下级列表")
    private List<T> children;
    /**
     * 显示顺序
     */
    @Schema(description = "显示顺序")
    private Long orderNum;

    public static <T extends Tree<T>> List<T> buildTree(List<T> list) {
        if (ObjectUtils.isEmpty(list)) {
            return new ArrayList<>(0);
        }
        Map<Long, T> treeMap = list.stream().collect(Collectors.toMap(Tree::getId, t -> t));
        List<Long> removeTreeList = new ArrayList<>();
        for (T tree : list) {
            if (treeMap.containsKey(tree.getParentId())) {
                Long parentId = tree.getParentId();
                T parentTree = treeMap.get(parentId);
                List<T> children = parentTree.getChildren();
                if (ObjectUtils.isEmpty(children)) {
                    children = new ArrayList<>();
                }
                children.add(tree);
                children.sort((o1, o2) -> (int) (o1.getOrderNum() - o2.getOrderNum()));
                parentTree.setChildren(children);
                treeMap.put(parentId, parentTree);
                removeTreeList.add(tree.getId());
            }
        }
        if (ObjectUtils.isNotEmpty(removeTreeList)) {
            for (Long key : removeTreeList) {
                treeMap.remove(key);
            }
        }
        return treeMap.values().stream().sorted((o1, o2) -> (int) (o1.getOrderNum() - o2.getOrderNum())).toList();
    }
}