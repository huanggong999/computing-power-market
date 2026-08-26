package com.lingyang.cloud.model.vo.menu;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.lingyang.cloud.common.constant.UserConstants;
import com.lingyang.common.core.utils.StringUtils;
import lombok.Data;
import org.apache.commons.lang3.ObjectUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 路由配置信息
 *
 * @author scrm
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RouterVo {
    /**
     * 路由名字
     */
    private String name;

    /**
     * 路由地址
     */
    private String path;

    /**
     * 是否隐藏路由，当设置 true 的时候该路由不会再侧边栏出现
     */
    private boolean hidden;

    /**
     * 重定向地址，当设置 noRedirect 的时候该路由在面包屑导航中不可被点击
     */
    private String redirect;

    /**
     * 组件地址
     */
    private String component;

    /**
     * 路由参数：如 {"id": 1, "name": "ry"}
     */
    private String query;

    /**
     * 当你一个路由下面的 children 声明的路由大于1个时，自动会变成嵌套的模式--如组件页面
     */
    private Boolean alwaysShow;

    /**
     * 其他元素
     */
    private MetaVo meta;

    /**
     * 子路由
     */
    private List<RouterVo> children;

    /**
     * 获取vue路由信息
     *
     * @param sysMenus 菜单列表
     * @return 菜单路由
     */
    @JsonIgnore
    public static List<RouterVo> getVueRouterVo(List<SysMenuTree> sysMenus) {
        if (ObjectUtils.isEmpty(sysMenus)) {
            return List.of();
        }
        List<RouterVo> routers = new LinkedList<>();
        for (SysMenuTree menu : sysMenus) {
            if (menu.getMenuType().equalsIgnoreCase("F")) {
                continue;
            }
            RouterVo router = new RouterVo();
            router.setHidden(Integer.valueOf(1).equals(menu.getVisible()));
            router.setName(getRouteName(menu));
            router.setPath(getRouterPath(menu));
            router.setComponent(getComponent(menu));
            router.setQuery(menu.getQuery());
            router.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon(), Integer.valueOf(1).equals(menu.getIsCache()), menu.getPath()));
            List<SysMenuTree> cMenus = menu.getChildren();
            if (ObjectUtils.isNotEmpty(cMenus) && UserConstants.TYPE_DIR.equals(menu.getMenuType())) {
                router.setAlwaysShow(true);
                router.setRedirect("noRedirect");
                router.setChildren(getVueRouterVo(cMenus));
            } else if (isMenuFrame(menu)) {
                router.setMeta(new MetaVo());
                List<RouterVo> childrenList = new ArrayList<>();
                RouterVo children = new RouterVo();
                children.setPath(menu.getPath());
                children.setComponent(menu.getComponent());
                children.setName(StringUtils.capitalize(menu.getPath()));
                children.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon(), Integer.valueOf(1).equals(menu.getIsCache()), menu.getPath()));
                children.setQuery(menu.getQuery());
                childrenList.add(children);
                router.setChildren(childrenList);
            } else if (menu.getParentId().intValue() == 0 && isInnerLink(menu)) {
                router.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon()));
                router.setPath("/");
                List<RouterVo> childrenList = new ArrayList<>();
                RouterVo children = new RouterVo();
                String routerPath = innerLinkReplaceEach(menu.getPath());
                children.setPath(routerPath);
                children.setComponent(UserConstants.INNER_LINK);
                children.setName(StringUtils.capitalize(routerPath));
                children.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon(), menu.getPath()));
                childrenList.add(children);
                router.setChildren(childrenList);
            }
            routers.add(router);
        }
        return routers;
    }

    /**
     * 获取路由名称
     *
     * @param menu 菜单信息
     * @return 路由名称
     */
    @JsonIgnore
    public static String getRouteName(SysMenuTree menu) {
        String routerName = StringUtils.capitalize(menu.getPath());
        // 非外链并且是一级目录（类型为目录）
        if (isMenuFrame(menu)) {
            routerName = StringUtils.EMPTY;
        }
        return routerName;
    }


    /**
     * 获取路由地址
     *
     * @param menu 菜单信息
     * @return 路由地址
     */
    @JsonIgnore
    public static String getRouterPath(SysMenuTree menu) {
        String routerPath = menu.getPath();
        // 内链打开外网方式
        if (menu.getParentId().intValue() != 0 && isInnerLink(menu)) {
            routerPath = innerLinkReplaceEach(routerPath);
        }
        // 非外链并且是一级目录（类型为目录）
        if (isMenuFrame(menu)) {
            routerPath = "/";
        }
        return routerPath;
    }

    /**
     * 获取组件信息
     *
     * @param menu 菜单信息
     * @return 组件信息
     */
    @JsonIgnore
    private static String getComponent(SysMenuTree menu) {
        String component = UserConstants.LAYOUT;
        if (StringUtils.isNotEmpty(menu.getComponent()) && !isMenuFrame(menu)) {
            component = menu.getComponent();
        } else if (StringUtils.isEmpty(menu.getComponent()) && menu.getParentId().intValue() != 0 && isInnerLink(menu)) {
            component = UserConstants.INNER_LINK;
        } else if (StringUtils.isEmpty(menu.getComponent()) && isParentView(menu)) {
            component = UserConstants.PARENT_VIEW;
        }
        return component;
    }

    /**
     * 是否为菜单内部跳转
     *
     * @param menu 菜单信息
     * @return 结果
     */
    @JsonIgnore
    private static boolean isMenuFrame(SysMenuTree menu) {
        return menu.getParentId().equals(0L) && UserConstants.TYPE_MENU.equals(menu.getMenuType()) && menu.getIsFrame().equals(UserConstants.NO_FRAME);
    }

    /**
     * 是否为内链组件
     *
     * @param menu 菜单信息
     * @return 结果
     */
    @JsonIgnore
    private static boolean isInnerLink(SysMenuTree menu) {
        return menu.getIsFrame().equals(UserConstants.NO_FRAME) && StringUtils.isHttp(menu.getPath());
    }

    /**
     * 是否为parent_view组件
     *
     * @param menu 菜单信息
     * @return 结果
     */
    @JsonIgnore
    private static boolean isParentView(SysMenuTree menu) {
        return menu.getParentId().intValue() != 0 && UserConstants.TYPE_DIR.equals(menu.getMenuType());
    }


    /**
     * 内链域名特殊字符替换
     */
    @JsonIgnore
    private static String innerLinkReplaceEach(String path) {
        return StringUtils.replaceEach(path, new String[]{StringUtils.HTTP, StringUtils.HTTPS, StringUtils.WWW, "."},
                new String[]{"", "", "", "/"});
    }

}
