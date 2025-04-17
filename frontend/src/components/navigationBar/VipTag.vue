<script setup>
const {vipType} = defineProps({
  vipType: {
    index: Number,
    value: String,
  }
})

const getTagColor = (index) => {
  switch (index) {
    case 0:
      return "rgba(112,111,111,0.5)";
    case 1:
      return "rgba(255,174,0,0.7)";
    case 2:
      return "rgba(255,174,0,0.8)";
    case 3:
      return "rgba(255,174,0,0.9)";
    case 4:
      return "rgba(255,174,0,1)";
    default:
      return "";
  }
}
</script>

<template>
  <el-tag effect="dark" :color="getTagColor(vipType.index)" round style="border-color: transparent">
    {{ vipType.value }}
  </el-tag>
</template>

<style scoped>

</style>