type LayoutType = "vertical" | "classic" | "transverse" | "columns";

type AssemblySizeType = "large" | "default" | "small";
type LanguageType = "zh" | "en" | null;

interface IGlobalState {
  drawerVisible: boolean;
  layout: LayoutType;
  assemblySize: AssemblySizeType;
  language: LanguageType;
  maximize: boolean;
  primary: string;
  isDark: boolean;
  isGrey: boolean;
  isWeak: boolean;
  asideInverted: boolean;
  isCollapse: boolean;
  breadcrumb: boolean;
  breadcrumbIcon: boolean;
  tabs: boolean;
  tabsIcon: boolean;
  footer: boolean;
}

type ObjToKeyValArray<T> = {
  [K in keyof T]: [K, T[K]];
}[keyof T];

type GreyOrWeakType = "grey" | "weak";
