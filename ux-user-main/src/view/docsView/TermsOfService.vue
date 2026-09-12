<template>
  <iframe
    style="width: 100%; height: 100%"
    :src="'/terms_of_service.pdf?_t=' + Date.now()"
  ></iframe>
</template>

<script setup lang="ts" name="docsView"></script>
<style lang="scss" scoped></style>
