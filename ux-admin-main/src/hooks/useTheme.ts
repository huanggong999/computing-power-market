import { DEFAULT_PRIMARY } from "@/config";
import { storeToRefs, useGlobalStore } from "@/store";
import { asideTheme, AsideThemeType } from "@/styles/theme/aside";
import { getDarkColor, getLightColor } from "@/utils/color";

export const useTheme = () => {
  const Global = useGlobalStore();

  const { primary, isDark, isGrey, isWeak, asideInverted, layout } =
    storeToRefs(Global);

  const changePrimary = (val: string | null) => {
    if (!val) {
      val = DEFAULT_PRIMARY;
      ElMessage.success(`主题颜色已重置为 ${DEFAULT_PRIMARY}`);
    }
    document.documentElement.style.setProperty("--el-color-primary", val);

    document.documentElement.style.setProperty(
      "--el-color-primary-dark-2",
      isDark.value ? `${getLightColor(val, 0.2)}` : `${getDarkColor(val, 0.3)}`
    );
    for (let i = 1; i <= 9; i++) {
      const primaryColor = isDark.value
        ? `${getDarkColor(val, i / 10)}`
        : `${getLightColor(val, i / 10)}`;
      document.documentElement.style.setProperty(
        `--el-color-primary-light-${i}`,
        primaryColor
      );
    }
    Global.setGlobalState("primary", val);
  };
  // 切换暗黑模式 ==> 并带修改主题颜色、侧边栏颜色
  const switchDark = () => {
    const html = document.documentElement as HTMLElement;
    if (isDark.value) html.setAttribute("class", "dark");
    else html.setAttribute("class", "");
    changePrimary(primary.value);
    setAsideTheme();
  };

  // 灰色和弱色切换
  const changeGreyOrWeak = (type: GreyOrWeakType, value: boolean) => {
    const body = document.body as HTMLElement;
    if (!value) return body.removeAttribute("style");
    const styles: Record<GreyOrWeakType, string> = {
      grey: "filter: grayscale(1)",
      weak: "filter: invert(80%)",
    };
    body.setAttribute("style", styles[type]);
    const propName = type === "grey" ? "isWeak" : "isGrey";
    Global.setGlobalState(propName, false);
  };

  // 设置侧边栏样式 ==> light、inverted、dark
  const setAsideTheme = () => {
    // 默认所有侧边栏为 light 模式
    let type: AsideThemeType = "light";
    // transverse 布局下菜单栏为 inverted 模式
    if (layout.value == "transverse") type = "inverted";
    // 侧边栏反转色目前只支持在 vertical 布局模式下生效
    if (layout.value == "vertical" && asideInverted.value) type = "inverted";
    // 侧边栏 dark 模式
    if (isDark.value) type = "dark";
    const theme = asideTheme[type!];
    for (const [key, value] of Object.entries(theme)) {
      document.documentElement.style.setProperty(key, value);
    }
  };

  // init theme
  const initTheme = () => {
    switchDark();
    if (isGrey.value) changeGreyOrWeak("grey", true);
    if (isWeak.value) changeGreyOrWeak("weak", true);
    document.documentElement.style.setProperty(
      `--el-menu-item-font-size`,
      "16px"
    );
  };

  return {
    initTheme,
    switchDark,
    changePrimary,
    changeGreyOrWeak,
    setAsideTheme,
  };
};
