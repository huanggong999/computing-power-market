<template>
  <el-dialog
    v-model="visible"
    title="更多操作"
    width="500px"
    :close-on-click-modal="false"
  >
    <div class="more-actions-dialog">
      <!-- 操作列表 -->
      <div class="action-list">
        <!-- 重置密码 -->
        <div class="action-item" @click="handleAction('resetPassword')">
          <div class="action-icon">
            <el-icon><Lock /></el-icon>
          </div>
          <div class="action-info">
            <div class="action-title">重置密码</div>
            <div class="action-desc">重置实例的SSH登录密码</div>
          </div>
          <div class="action-arrow">
            <el-icon><ArrowRight /></el-icon>
          </div>
        </div>

        <!-- 设置名称 -->
        <div class="action-item" @click="handleAction('setName')">
          <div class="action-icon">
            <el-icon><Edit /></el-icon>
          </div>
          <div class="action-info">
            <div class="action-title">设置名称</div>
            <div class="action-desc">设置实例的显示名称</div>
          </div>
          <div class="action-arrow">
            <el-icon><ArrowRight /></el-icon>
          </div>
        </div>
      </div>
    </div>
  </el-dialog>

  <!-- 重置密码对话框 -->
  <ResetPasswordDialog
    v-model="resetPasswordVisible"
    :instance="instance"
    @success="handleActionSuccess"
  />

  <!-- 设置名称对话框 -->
  <SetNameDialog
    v-model="setNameVisible"
    :instance="instance"
    @success="handleActionSuccess"
  />
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import {
  Lock,
  Edit,
  ArrowRight
} from '@element-plus/icons-vue'
import type { Instance } from '@/types/instance'

// 导入子对话框组件
import ResetPasswordDialog from './ResetPasswordDialog.vue'
import SetNameDialog from './SetNameDialog.vue'

interface Props {
  modelValue: boolean
  instance: Instance | null
}

const props = defineProps<Props>()

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  success: []
}>()

const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

// 各对话框显示状态
const resetPasswordVisible = ref(false)
const setNameVisible = ref(false)

// 处理操作选择
const handleAction = (action: string) => {
  visible.value = false

  switch (action) {
    case 'resetPassword':
      resetPasswordVisible.value = true
      break
    case 'setName':
      setNameVisible.value = true
      break
  }
}

// 操作成功回调
const handleActionSuccess = () => {
  emit('success')
}
</script>

<style scoped lang="scss">
.more-actions-dialog {
  .action-list {
    .action-item {
      display: flex;
      align-items: center;
      padding: 13px 16px;
      border-radius: 8px;
      cursor: pointer;
      transition: all 0.3s;
      margin-bottom: 8px;
      border: 1px solid #e4e7ed;

      &:hover {
        background: #f5f7fa;
        border-color: #409eff;
      }

      .action-icon {
        width: 37px;
        height: 37px;
        border-radius: 7px;
        background: #ecf5ff;
        color: #409eff;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 19px;
        margin-right: 13px;
      }

      .action-info {
        flex: 1;

        .action-title {
          font-size: 17px;
          font-weight: 500;
          color: #303133;
          margin-bottom: 4px;
        }

        .action-desc {
          font-size: 14px;
          color: #909399;
        }
      }

      .action-arrow {
        color: #c0c4cc;
        font-size: 15px;
      }
    }
  }
}
</style>
