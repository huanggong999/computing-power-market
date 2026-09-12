<template>
  <div class="typeArea"><slot /></div>
</template>

<script setup lang="ts" name="TypeArea"></script>
<style lang="scss" scoped>
@media (max-width: 1440px) {
  .typeArea {
    width: 100%;
    height: 100%;
    margin: auto;
  }
}
@media (min-width: 1440px) {
  .typeArea {
    width: 1440px;
    height: 100%;
    margin: auto;
  }
}
</style>
