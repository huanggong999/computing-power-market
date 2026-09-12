div
<template>
  <div class="search">
    <div class="title">
      请问要搜索什么<span class="key">{{ searchTitle }}呢?</span>
    </div>
    <el-input
      v-model="model"
      :placeholder="placeholder"
      :prefix-icon="Search"
      @keyup.enter="() => emit('search')"
    />
    <!-- <div class="search-right" @click="() => emit('search')">
      {{ $t("banner.search") }}
    </div> -->
  </div>
</template>

<script setup lang="ts" name="Search">
import { useVModel } from '../../utils/useVModel'
import { Search } from '@element-plus/icons-vue'
const props = defineProps<{ modelValue: string; searchTitle?: string }>()
const emit = defineEmits(['update:modelValue', 'search'])
const model = useVModel(props, 'modelValue', emit)
const placeholder = `请输入关键词,搜索你想要的${props.searchTitle}`
</script>
<style lang="scss" scoped>
.search {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  .title {
    font-weight: bold;
    font-size: 56px;
    color: #000000;
    margin-bottom: 30px;
    .key {
      color: #3972fd;
    }
  }
  .el-input {
    width: 1053px;
    height: 90px;

    ::v-deep .el-input__wrapper {
      padding: 0;
      border: solid 2px transparent;
      border-radius: 50px;
      background-image: linear-gradient(#fff, #fff),
        linear-gradient(to right, #f1e452, #3972fd);
      background-origin: border-box;
      background-clip: content-box, border-box;
      .el-input__prefix {
        padding-left: 350px;
      }
      .el-input__inner {
        width: 330px;
        height: 30px;
        font-weight: 400;
        font-size: 22px;
        color: #83889d;
      }
    }
  }
}
</style>
