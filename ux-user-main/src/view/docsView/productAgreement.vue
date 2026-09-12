<template>
  <iframe
    style="width: 100%; height: 100%"
    :src="'/product_Agreement.pdf?_t=' + Date.now()"
  ></iframe>
</template>

<script setup lang="ts" name="docsView"></script>
<style lang="scss" scoped></style>
