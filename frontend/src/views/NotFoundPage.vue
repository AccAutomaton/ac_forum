<script setup>
import {Discount} from "@element-plus/icons-vue";
import router from "@/router/index.js";

const goBackToHome = () => {
  router.push("/home");
}
</script>

<template>
  <div class="x-y-center">
    <el-row style="width: 300px" align="middle" justify="center">
      <el-icon :size="200">
        <svg xmlns="http://www.w3.org/2000/svg" width="256" height="256" viewBox="0 0 32 32">
          <g fill="currentColor">
            <path
                d="M7.293 9.293a1 1 0 0 1 1.414 0l1.793 1.793l1.793-1.793a1 1 0 1 1 1.414 1.414L11.914 12.5l1.793 1.793a1 1 0 0 1-1.414 1.414L10.5 13.914l-1.793 1.793a1 1 0 0 1-1.414-1.414L9.086 12.5l-1.793-1.793a1 1 0 0 1 0-1.414m16 0a1 1 0 1 1 1.414 1.414L22.914 12.5l1.793 1.793a1 1 0 0 1-1.414 1.414L21.5 13.914l-1.793 1.793a1 1 0 0 1-1.414-1.414l1.793-1.793l-1.793-1.793a1 1 0 0 1 1.414-1.414l1.793 1.793zM12 23a4 4 0 1 1 8 0a4 4 0 0 1-8 0"></path>
            <path
                d="M15.999 1C11.095 1 7.313 2.654 4.763 5.423C2.226 8.178 1 11.938 1 15.999c0 4.061 1.226 7.82 3.763 10.576c2.55 2.769 6.332 4.423 11.236 4.423c4.904 0 8.686-1.654 11.235-4.423c2.537-2.755 3.764-6.515 3.764-10.576c0-4.061-1.227-7.821-3.764-10.576C24.684 2.654 20.903 1 16 1M3 15.999c0-3.67 1.106-6.91 3.235-9.221C8.35 4.48 11.568 3 15.999 3c4.43 0 7.648 1.48 9.764 3.778c2.129 2.311 3.235 5.55 3.235 9.22c0 3.671-1.106 6.91-3.235 9.222c-2.116 2.298-5.333 3.778-9.764 3.778c-4.43 0-7.648-1.48-9.764-3.778C4.105 22.91 3 19.67 3 16"></path>
          </g>
        </svg>
      </el-icon>
    </el-row>
    <el-row style="width: 300px" align="middle" justify="center">
      <el-col :span="12" style="font-weight: bolder; font-size: 75px">
        404
      </el-col>
      <el-col :span="12">
        <el-row style="font-weight: bolder; font-size: 30px">Not Found</el-row>
        <el-row>网页走丢了 ~</el-row>
      </el-col>
    </el-row>
    <el-row style="width: 300px; margin-top: 20px" align="middle" justify="center">
      <el-button :icon="Discount" @click="goBackToHome">返回首页</el-button>
    </el-row>
  </div>
</template>

<style scoped>

</style>