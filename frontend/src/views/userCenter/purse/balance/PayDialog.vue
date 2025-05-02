<script setup>
import {nextTick, ref} from "vue";
import {ElNotification} from "element-plus";
import {buyCoins} from "@/request/normal/coin.js";

const payDialogVisible = ref(false), currentCoins = ref(0);
const alipayForm = ref(""), alipayPage = ref();

const showDialog = (coins) => {
  alipayForm.value = "";
  payDialogVisible.value = true;
  currentCoins.value = coins;
}

defineExpose({
  showDialog,
});

const clickWechatPay = () => {
  ElNotification({type: 'info', title: '暂不支持微信支付', message: '敬请谅解'});
}

const clickAlipay = async () => {
  const data = await buyCoins(currentCoins.value);
  if (data !== null) {
    alipayForm.value = data["pageRedirectionData"];
    await nextTick(() => {
      alipayPage.value.children[0].submit();
      setTimeout(() => {
      }, 500)
    });
  }
}
</script>

<template>
  <el-dialog v-model="payDialogVisible" title="请选择支付方式" width="400" align-center destroy-on-close>
    <div style="width: 100%; text-align: center">
      <div>
        <span>RMB</span>
        <span style="font-size: xx-large; margin-left: 5px; font-weight: bold">{{ parseInt(currentCoins / 100) }}</span>
        <span style="font-size: large; font-weight: bold">.{{ currentCoins % 100 }}</span>
      </div>
      <div style="margin-top: 20px">
        <!--suppress CheckImageSize -->
        <img class="pay-img" src="@/assets/alipay.png" height="30px" alt="支付宝" @click="clickAlipay"/>
        <!--suppress CheckImageSize -->
        <img class="pay-img" src="@/assets/wechatpay.png" height="38px" alt="微信支付" style="margin-left: 40px"
             @click="clickWechatPay"/>
      </div>
    </div>
  </el-dialog>
  <div ref="alipayPage" v-html="alipayForm"/>
</template>

<style scoped>
.pay-img {
  cursor: pointer;
}
</style>