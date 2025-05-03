<script setup>
import {getOnlineUserCount} from "@/request/administrator/user.js";
import {ref} from "vue";

const total = ref(0), online = ref(0)

const init = async () => {
  const data = await getOnlineUserCount();
  if (data !== null) {
    total.value = data["total"];
    online.value = data["online"];
  }
}
init();
</script>

<template>
  <el-row>
    <el-statistic title="总用户数" :value="total"/>
  </el-row>
  <el-row style="margin-top: 20px">
    <el-statistic title="在线用户数" :value="online"/>
  </el-row>
</template>

<style scoped>

</style>