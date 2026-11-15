<template>
  <router-view />
</template>

<script setup>
import { onMounted } from 'vue'
import { useThemeStore } from '@/stores/theme'

const themeStore = useThemeStore()

onMounted(() => {
  themeStore.initTheme()
})
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

:root {
  --primary-color: #1677ff;
  --primary-hover: #4096ff;
  --bg-dark: #001529;
  --bg-dark-light: #0a1a2f;
  --bg-card: #0f2744;
  --text-primary: #ffffff;
  --text-secondary: #a8b4c4;
  --border-color: #1a3a5c;
  --success-color: #52c41a;
  --warning-color: #faad14;
  --error-color: #f5222d;
  --price-color: #ff6b6b;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  background-color: var(--bg-dark);
  color: var(--text-primary);
  line-height: 1.6;
  overflow-x: hidden;
}

/* 滚动条样式 */
::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

::-webkit-scrollbar-track {
  background: var(--bg-dark);
}

::-webkit-scrollbar-thumb {
  background: var(--border-color);
  border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
  background: var(--primary-color);
}

/* 通用样式类 */
.container {
  max-width: 1440px;
  margin: 0 auto;
  padding: 0 24px;
}

.text-primary {
  color: var(--text-primary);
}

.text-secondary {
  color: var(--text-secondary);
}

.text-price {
  color: var(--price-color);
  font-weight: 600;
}

.bg-card {
  background: var(--bg-card);
  border-radius: 8px;
  border: 1px solid var(--border-color);
}

.hover-lift {
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.hover-lift:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.3);
}

/* 按钮样式 */
.btn-primary {
  background: linear-gradient(135deg, var(--primary-color), var(--primary-hover));
  border: none;
  color: white;
  padding: 8px 20px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-weight: 500;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(22, 119, 255, 0.4);
}

/* 响应式布局 */
@media (max-width: 1200px) {
  .container {
    padding: 0 16px;
  }
}

@media (max-width: 768px) {
  .container {
    padding: 0 12px;
  }
}

/* 动画效果 */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.7;
  }
}

.animate-fade-in {
  animation: fadeIn 0.5s ease-out;
}

.animate-pulse {
  animation: pulse 2s ease-in-out infinite;
}
</style>
