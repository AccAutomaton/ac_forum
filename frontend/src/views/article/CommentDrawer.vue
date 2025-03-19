<script setup>
import {ref} from "vue";

const drawerVisible = ref(false);

const showDrawer = () => {
  drawerVisible.value = true;
}
defineExpose({
  showDrawer,
})
</script>

<template>
  <el-drawer v-model="drawerVisible" title="评论">
    <span>Hi there!</span>
  </el-drawer>
</template>

<style scoped>

</style>