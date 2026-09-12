<template>
  <div class="table-box">
    <!-- OR 连接符 -->
    <div class="tags">
      <el-tag> {{ mode }} </el-tag>
    </div>
    <!-- 条件主体 -->
    <div class="condition-row mt20" v-for="(item, index) in group" :key="index">
      <SelectHidden
        v-model="item.field"
        :options="props.options"
        :label="'title'"
        :value="'field'"
        class="select"
      />
      <SelectHidden
        :options="judgmentConditionsList"
        v-model="item.condition"
        class="select ml24"
      />

      <SelectHidden
        v-if="getSelectItem(item.field)?.type === 'select'"
        :options="getSelectItem(item.field)?.options"
        v-model="item.value"
        class="select ml24"
      />
      <el-input
        v-if="getSelectItem(item.field)?.type === 'input'"
        v-model="item.value"
        placeholder="请输入"
        class="select ml24"
      />
      <el-button
        v-if="index !== 0"
        class="ml24"
        type="danger"
        icon="Delete"
        @click="removeCondition(index)"
        circle
      />
    </div>
    <!-- 操作按钮区域 -->
    <div class="mt20">
      <el-button type="primary" icon="CirclePlus" @click="addCondition">
        添加条件
      </el-button>
      <el-button @click="toggleLogicMode">
        切换逻辑 ：{{ mode === "OR" ? "AND" : "OR" }}
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts" name="ConditionBuilder">
import SelectHidden from "./SelectHidden.vue";

const props = defineProps<{
  options: TKeyValue[];
  dataForm: TKeyValue;
}>();

const emit = defineEmits(["update:modelValue"]);

const mode = ref("OR");

watch(
  () => props.dataForm,
  (newVal) => (mode.value = newVal.mode),
  { immediate: true }
);

const groupItem = { condition: "==", field: "", value: "" };
//
const group = ref(props.dataForm.group);

// 定义操作符类型
interface Operator {
  label: string;
  value: string;
}

const judgmentConditionsList: Operator[] = [
  { label: "等于", value: "==" },
  { label: "不等于", value: "!=" },
  { label: "包含", value: "on" },
  { label: "不包含", value: "notOn" },
  { label: "为空", value: "empty" },
  { label: "不为空", value: "notEmpty" },
];

const getSelectItem = (id: string) =>
  props.options.find((item) => item.field === id);

// 添加条件
const addCondition = () => group.value.push({ ...groupItem });

// 删除条件
const removeCondition = (index: number) => group.value.splice(index, 1);

// 切换逻辑
const toggleLogicMode = () => {
  nextTick(() => (mode.value = mode.value === "OR" ? "AND" : "OR"));
};

defineExpose({
  group: computed(() => group.value),
  mode: computed(() => mode.value),
});
</script>
<style lang="scss" scoped>
.select {
  width: 240px;
}
</style>
