<script setup>
import {ref} from "vue";
import {useRoute} from "vue-router";
import router from "@/router/index.js";
import {afterPaying} from "@/request/normal/coin.js";

const paySuccessDialogVisible = ref(false);
if (useRoute().query.method === "alipay.trade.page.pay.return") {
  afterPaying(useRoute().query.trade_no);
  paySuccessDialogVisible.value = true;
}

const clickCloseDialogButton = () => {
  paySuccessDialogVisible.value = false;
  router.push({path: '/userCenter/purse/balance', query: ''}).then(() => {
    window.location.reload();
  });
}
</script>

<template>
  <el-dialog v-model="paySuccessDialogVisible" width="400" align-center destroy-on-close :show-close="false">
    <el-result icon="success" title="支付成功" sub-title="感谢您的支持">
      <template #extra>
        <el-button type="primary" plain @click="clickCloseDialogButton">返回</el-button>
      </template>
    </el-result>
  </el-dialog>
</template>

<style scoped>

</style>