<script setup>
import CashRecords from "@/views/userCenter/purse/consumeRecord/CashRecords.vue";
import CoinRecords from "@/views/userCenter/purse/consumeRecord/CoinRecords.vue";
import {ref} from "vue";
import {useRoute} from "vue-router";
import router from "@/router/index.js";

const enableTab = ref(useRoute().query.tab === undefined ? "cash" : useRoute().query.tab);

const onTabClick = (pane) => {
  router.push({query: {tab: pane.paneName}});
}
</script>

<template>
  <el-container>
    <el-header>
      <h2 style="margin-top: 10px">消费记录</h2>
    </el-header>
    <el-divider style="margin: 0"/>
    <el-main style="padding-bottom: 0">
      <el-scrollbar style="height: 70vh">
        <el-tabs type="border-card" v-model="enableTab" stretch @tab-click="onTabClick">
          <el-tab-pane label="现金" name="cash" lazy>
            <CashRecords/>
          </el-tab-pane>
          <el-tab-pane label="AC币" name="coin" lazy>
            <CoinRecords/>
          </el-tab-pane>
        </el-tabs>
      </el-scrollbar>
    </el-main>
  </el-container>
</template>

<style scoped>

</style>