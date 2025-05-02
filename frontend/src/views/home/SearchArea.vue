<script setup>
import icon from "@/assets/favicon.png"
import {ref} from "vue";
import {Search} from "@element-plus/icons-vue";
import {homeSearchOptions} from "@/utils/options.js";
import router from "@/router/index.js";

const keywords = ref(""), selectSearchType = ref(0);

const doSearch = () => {
  if (selectSearchType.value === 0) {
    router.push({path: "/article", query: {keywords: keywords.value}});
  } else if (selectSearchType.value === 1) {
    router.push({path: "/topic", query: {keywords: keywords.value}});
  }
}
</script>

<template>
  <el-row style="display: flex; flex-direction: column" justify="center" align="middle">
    <el-row justify="center" align="middle">
      <el-image style="width: 80px; margin-right: 6px" :src="icon"/>
      <el-text
          style="font-size: 48px; font-weight: bolder; color: black; font-family: 方正小标宋简体, '小米兰亭', 微软雅黑, system-ui">
        论坛
      </el-text>
    </el-row>
    <el-row justify="center" align="middle">
      <el-input v-model="keywords" @keyup.enter="doSearch"
                style="border-radius: 25px; width: 40vw; margin-top: 20px; height: 48px; font-size: 14px"
                placeholder="请输入你想搜索的内容">
        <template #prepend>
          <span>
            <el-select v-model="selectSearchType" placeholder="搜索范围" style="width: 100px; border: none">
              <el-option v-for="option in homeSearchOptions" :key="option.value" :label="option.label"
                         :value="option.value"/>
            </el-select>
          </span>
        </template>
        <template #append>
          <el-button :icon="Search" @click="doSearch"/>
        </template>
      </el-input>
    </el-row>
  </el-row>
</template>

<style scoped>
:deep() .el-select {
  --el-input-border-color: transparent
}

:deep(.el-input-group__prepend) {
  padding-left: 32px;
  border-top-left-radius: 25px;
  border-bottom-left-radius: 25px;
}

:deep(.el-input-group__append) {
  color: dodgerblue;
  padding-right: 25px;
  border-top-right-radius: 25px;
  border-bottom-right-radius: 25px;
}
</style>