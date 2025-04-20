<script setup>
import NavigationBar from "@/components/navigationBar/NavigationBar.vue"
import {nextTick, ref, provide} from "vue";

const isRefreshFlag = ref(true)
const reloadPage = () => {
  isRefreshFlag.value = false
  nextTick(() => {
    isRefreshFlag.value = true
  })
}
provide("reloadPage", reloadPage);
</script>

<template>
  <NavigationBar>
    <router-view v-if="isRefreshFlag"/>
  </NavigationBar>
</template>

<style>
@font-face {
  font-family: 小米兰亭;
  src: url('assets/css/MI_LanTing_Regular.ttf');
}

body {
  font-family: 小米兰亭, 微软雅黑, system-ui;
}

.x-center {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
}

.y-center {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
}

.x-y-center {
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
}

.el-menu--collapse .el-menu .el-submenu, .el-menu--popup{
  min-width: 120px!important;
}

</style>