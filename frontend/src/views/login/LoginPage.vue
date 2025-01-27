<script setup>
import {ref} from "vue";
import LoginTab from "@/views/login/LoginTab.vue";
import RegisterTab from "@/views/login/RegisterTab.vue";

const enabledTab = ref("login");
const setEnabledTab = (newTab) => {
  enabledTab.value = newTab;
}

</script>

<template>
  <el-card class="x-y-center" style="width: 480px" shadow="always">
    <el-tabs v-model="enabledTab" stretch type="border-card" style="border-style: none">
      <el-tab-pane name="login" label="登录" style="height: 400px">
        <LoginTab @setEnabledTab="setEnabledTab"/>
      </el-tab-pane>
      <el-tab-pane name="register" label="注册" style="height: 400px">
        <RegisterTab @setEnabledTab="setEnabledTab"/>
      </el-tab-pane>
    </el-tabs>
  </el-card>
</template>

<style scoped>
:deep() .el-card__body {
  padding: 0;
}

h1 {
  text-align: center;
}
</style>