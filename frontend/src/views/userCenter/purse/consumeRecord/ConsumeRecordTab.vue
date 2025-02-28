<script setup>
import CashRecords from "@/views/userCenter/purse/consumeRecord/CashRecords.vue";

// TODO: 根据 Url params 跳转不同 tab
</script>

<template>
  <el-container>
    <el-header>
      <h2 style="margin-top: 10px">消费记录</h2>
    </el-header>
    <el-divider style="margin: 0"/>
    <el-main style="padding-bottom: 0">
      <el-scrollbar style="height: 70vh">
        <el-tabs type="border-card">
          <el-tab-pane label="现金" lazy>
            <CashRecords/>
          </el-tab-pane>
          <el-tab-pane label="AC币" lazy>
            <CashRecords/>
          </el-tab-pane>
        </el-tabs>
      </el-scrollbar>
    </el-main>
  </el-container>
</template>

<style scoped>

</style>