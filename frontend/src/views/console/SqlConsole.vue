<script setup>
import {ref} from "vue";
import {doSelectSql, doUpdateSql} from "@/request/hacker/sql.js";

const isLoading = ref(false);
const searchType = ref(0);
const sql = ref(""), result = ref("");

const doSql = async () => {
  isLoading.value = true;
  if (searchType.value === 0) {
    const data = await doSelectSql(sql.value);
    if (data !== null) {
      result.value = data;
      isLoading.value = false;
    }
  } else if (searchType.value === 1) {
    const data = await doUpdateSql(sql.value);
    if (data !== null) {
      result.value = data;
      isLoading.value = false;
    }
  }
}
</script>

<template>
  <div v-loading="isLoading">
    <el-row align="middle">
      <el-text>SQL语句</el-text>
      <el-radio-group v-model="searchType" style="margin: 0 40px">
        <el-radio :value="0">查询</el-radio>
        <el-radio :value="1">更新</el-radio>
      </el-radio-group>
      <el-button style="width: 100px" plain @click="doSql">执行</el-button>
    </el-row>
    <el-row>
      <el-input v-model="sql" style="margin: 10px 0 20px 0; width: 600px" :rows="12" type="textarea" resize="none"/>
    </el-row>
    <el-row>
      <el-text>执行结果</el-text>
    </el-row>
    <el-row>
      <el-input v-model="result" style="margin: 10px 0 20px 0; width: 600px" :rows="12" type="textarea" resize="none"
                readonly/>
    </el-row>
  </div>
</template>

<style scoped>

</style>