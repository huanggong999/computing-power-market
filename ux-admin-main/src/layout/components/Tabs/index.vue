<template>
  <div class="tabs-box">
    <div class="tabs-menu">
      <el-tabs
        v-model="tabsMenuValue"
        type="card"
        @tab-click="tabClick"
        @tab-remove="tabRemove"
      >
        <el-tab-pane
          v-for="item in tabsMenuList"
          :key="item.path"
          :label="item.title"
          :name="item.path"
          :closable="item.close"
        >
          <template #label>
            <el-icon class="tabs-icon" v-show="item.icon && tabsIcon">
              <component :is="item.icon" />
            </el-icon>
            {{ item.title }}
          </template>
        </el-tab-pane>
      </el-tabs>
      <MoreButton />
    </div>
  </div>
</template>

<script setup lang="ts" name="Tabs">
import { useAuth, useGlobalStore, useKeepAlive, useTabs } from "@/store";
import { TabPaneName, TabsPaneContext } from "element-plus";
import MoreButton from "./components/MoreButton.vue";

const route = useRoute();
const router = useRouter();
const Tabs = useTabs();
const Auth = useAuth();
const keepAlive = useKeepAlive();
const Global = useGlobalStore();

const tabsMenuValue = ref(route.fullPath);
const tabsMenuList = computed(() => Tabs.tabsMenuList);
const tabsIcon = computed(() => Global.tabsIcon);

onMounted(() => {
  initTabs();
}),
  watch(
    () => route.fullPath,
    () => {
      tabsMenuValue.value = route.fullPath;
      tabsMenuValue.value = route.fullPath;
      const tabsParams = {
        icon: route.meta.icon as string,
        title: route.meta.title as string,
        path: route.fullPath,
        name: route.name as string,
        close: !route.meta.isAffix,
      };

      Tabs.addTabs(tabsParams);
      if (!Auth.keepAliveNameGet.includes(route.name as string)) return;
      keepAlive.addKeepAliveName(route.name as string);
    },
    { immediate: true }
  );

const initTabs = () => {
  Auth.flatMenuListGet.forEach((item) => {
    if (item.meta.isAffix) {
      const tabsParams = {
        icon: item.meta.icon,
        title: item.meta.title,
        path: item.path,
        name: item.name,
        close: !item.meta.isAffix,
      };
      Tabs.addTabs(tabsParams);
    }
  });
};

const tabClick = (tabItem: TabsPaneContext) => {
  const fullPath = tabItem.props.name as string;
  router.push(fullPath);
};
const tabRemove = (fullPath: TabPaneName) => {
  const name =
    Tabs.tabsMenuList.filter((item) => item.path == fullPath)[0].name || "";
  keepAlive.removeKeepAliveName(name);
  Tabs.removeTabs(fullPath as string, fullPath == route.fullPath);
};
</script>
<style scoped lang="scss">
@use "./index.scss";
</style>
