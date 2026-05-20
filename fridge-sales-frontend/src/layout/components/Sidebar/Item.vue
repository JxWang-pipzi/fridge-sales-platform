<template>
  <el-icon v-if="icon" class="sub-el-icon">
    <component :is="icon" />
  </el-icon>
  <span v-if="title">{{ title }}</span>
</template>

<script setup>
defineProps({
  icon: {
    type: String,
    default: ''
  },
  title: {
    type: String,
    default: ''
  }
})
</script>
