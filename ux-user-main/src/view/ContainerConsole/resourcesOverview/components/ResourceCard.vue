<template>
  <div class="resource card">
    <div class="flx-justify-between">
      <div class="title sle mr15">
        集群名称cloudpods集群名称cloudpods集群名称cloudpods
        集群名称cloudpods集群名称cloudpods集群名称cloudpods集群名称cloudpods
      </div>
      <el-tag :type="tagType('运行中')">运行中</el-tag>
    </div>
    <div class="tip mt16">工作负载数量<span class="number"> 9</span></div>
    <p class="mb20 mt30">云服务器ECS资源已用量</p>
    <div class="tip mb12">CPU（Core）</div>
    <el-progress :percentage="50" />
    <div class="tip mb12">内存</div>
    <el-progress :percentage="58" />
    <div class="btn flx-center mt30 cup">
      节点数量 <span class="num ml15">2</span>
    </div>
  </div>
</template>

<script setup lang="ts" name="ResourceCard">
// defineProps<{}>();
const typeEnum = {
  运行中: { text: "运行中", type: "success" },
  创建失败: { text: "创建失败", type: "danger" },
  异常: { text: "异常", type: "danger" },
  欠费关停: { text: "欠费关停", type: "warning" },
  删除失败: { text: "删除失败", type: "danger" },
};

const tagType = (type: keyof typeof typeEnum): any => typeEnum[type].type;
</script>
<style lang="scss" scoped>
.resource {
  font-size: 20px;
  width: calc((100% - 30px) / 3);
  margin-bottom: 15px;
  margin-right: 15px;
  &:nth-child(3n) {
    margin-right: 0;
  }
  .title {
    font-size: 28px;
  }
  .tip {
    color: #83889d;
  }
  .number {
    color: #fba201;
  }
  .btn {
    padding: 15px 0;
    background: #d7e2f6;
    border-radius: 10px;
    .num {
      color: #3972fd;
    }
  }
}
</style>
