<script setup>

import {ref, watch} from "vue";
import {getCoins} from "@/request/coin.js";
import {useTransition} from "@vueuse/core";
import {CaretTop, Coin, QuestionFilled} from "@element-plus/icons-vue";
import PayDialog from "@/views/userCenter/purse/balance/PayDialog.vue";
import PaySuccessDialog from "@/views/userCenter/purse/balance/PaySuccessDialog.vue";
import {refreshPayingStatus} from "@/request/coin.js";
import {ElNotification} from "element-plus";

const rechargeCoinType = [
  {
    index: 0,
    value: 100,
    price: 1,
  },
  {
    index: 1,
    value: 600,
    price: 6,
  },
  {
    index: 2,
    value: 3000,
    price: 30,
  },
  {
    index: 3,
    value: 6800,
    price: 68,
  },
  {
    index: 4,
    value: 12800,
    price: 128,
  },
  {
    index: 5,
    value: 32800,
    price: 328,
  },
  {
    index: 6,
    value: 64800,
    price: 648,
  },
]

const coins = ref(0);
const coinsOutput = useTransition(coins, {
  duration: 1000,
})

const currentSelectedIndex = ref(0), currentRechargeValue = ref(rechargeCoinType[0].value),
    customizeRechargeValue = ref(0);

const getCoinBalance = async () => {
  const data = await getCoins();
  if (data !== null) {
    coins.value = data["coins"];
  }
}
getCoinBalance();

const enableSelectedClass = (index) => {
  if (currentSelectedIndex.value === index) {
    return "active-card";
  }
}

const onClickCard = (index) => {
  currentSelectedIndex.value = index;
  if (index !== 7) {
    currentRechargeValue.value = rechargeCoinType[index].value;
  } else {
    currentRechargeValue.value = customizeRechargeValue.value;
  }
}

watch(customizeRechargeValue, (newValue, oldValue) => {
  onClickCard(7);
  if (newValue < 0 || newValue > 99999999) {
    customizeRechargeValue.value = oldValue;
  } else if (newValue.toString().includes(".") || newValue.toString().includes("e")) {
    customizeRechargeValue.value = oldValue;
  } else {
    customizeRechargeValue.value = newValue;
    currentRechargeValue.value = customizeRechargeValue.value;
  }
})

const payDialog = ref();
const pay = () => {
  payDialog.value.showDialog(currentRechargeValue.value);
}

const clickRefreshPayingStatusButton = async () => {
  const data = await refreshPayingStatus();
  if (data !== null) {
    if (data["hasNewStatus"]) {
      window.location.reload();
    } else {
      ElNotification({title: '已是最新状态', type: 'success'})
    }
  }
}
</script>

<template>
  <el-container>
    <el-header>
      <h2 style="margin-top: 10px">余额</h2>
    </el-header>
    <el-divider style="margin: 0"/>
    <el-main style="padding-bottom: 0; margin-top: -20px">
      <el-scrollbar style="height: 75.5vh">
        <el-row align="middle">
          <el-icon :size=40 color="#edbe00">
            <el-icon><Coin /></el-icon>
          </el-icon>
          <el-text style="font-size: xxx-large; color: #edbe00; margin-left: 20px; font-weight: bold;">
            {{ Intl.NumberFormat('en', {maximumFractionDigits: 0}).format(coinsOutput) }}
          </el-text>
        </el-row>
        <el-row style="margin-bottom: -10px">
          <el-text>
            <el-icon style="font-size: 11px; margin-left: 20px; color: gray">
              <QuestionFilled/>
            </el-icon>
          </el-text>
          <el-text style="font-size: small; color: gray">充值没到账? 点击</el-text>
          <el-button text style="font-size: small; padding: 0" type="primary" @click="clickRefreshPayingStatusButton">此处</el-button>
          <el-text style="font-size: small; color: gray">刷新</el-text>
        </el-row>
        <el-row style="margin-top: 20px; width: 90%">
          <el-col v-for="(type, index) in rechargeCoinType" :key="type.vipIndex" :span="6" style="margin-bottom: 20px">
            <el-card :class="enableSelectedClass(index)" shadow="hover" @click="onClickCard(index)"
                     style="border-radius: 25px; margin-left: 10px; margin-right: 10px; cursor: pointer">
              <el-row style="font-weight: bold; font-size: large">
                <el-icon :size=25>
                  <el-icon><Coin /></el-icon>
                </el-icon>
                <span style="margin-left: 5px">
                  {{ type.value }}
                </span>
              </el-row>
              <div style="float: right; margin-top: 50px; margin-bottom: 10px">
                <span>RMB</span>
                <span style="font-size: xx-large; margin-left: 10px">{{ type.price }}</span>
              </div>
            </el-card>
            <div style="color: #409eff; text-align: center; width: 100%; margin-top: 10px">
              <el-icon v-if="type.index === currentSelectedIndex">
                <CaretTop/>
              </el-icon>
              <el-icon v-else style="color: transparent">
                <CaretTop/>
              </el-icon>
            </div>
          </el-col>
          <el-col :span="6">
            <el-card :class="enableSelectedClass(7)" shadow="hover" @click="onClickCard(7)"
                     style="border-radius: 25px; margin-left: 10px; margin-right: 10px; cursor: pointer">
              <el-row style="font-weight: bold; font-size: large">
                <el-icon :size=25>
                  <el-icon><Coin /></el-icon>
                </el-icon>
                <el-input type="number" v-model="customizeRechargeValue" style="width: 80%; margin-left: 5px"
                          size="small"
                          placeholder="自定义充值数量"/>
              </el-row>
              <div style="float: right; margin-top: 50px; margin-bottom: 10px">
                <span>RMB</span>
                <span v-if="customizeRechargeValue" style="font-size: xx-large; margin-left: 10px">
                  {{ customizeRechargeValue / 100 }}
                </span>
                <span v-else style="font-size: xx-large">
                  --
                </span>
              </div>
            </el-card>
            <div v-if="currentSelectedIndex === 7"
                 style="color: #409eff; text-align: center; width: 100%; margin-top: 10px">
              <el-icon>
                <CaretTop/>
              </el-icon>
            </div>
          </el-col>
        </el-row>
        <el-row align="middle" justify="center" style="width: 90%; margin-top: -30px; margin-bottom: 10px">
          <el-text>充值</el-text>
          <el-text style="color: black; font-size: large; font-weight: bold; margin-left: 5px; margin-right: 5px">
            <span v-if="currentRechargeValue">
                  {{ currentRechargeValue }}
                </span>
            <span v-else>
                  0
            </span>
          </el-text>
          <el-text>AC币</el-text>
        </el-row>
        <el-row align="middle" justify="center" style="width: 90%">
          <el-button style="height: 75px; min-width: 150px" round plain color="rgba(88,106,254,1)" @click="pay">
            <div style="float: revert">
              <div>
                <span>RMB</span>
                <span style="font-size: xx-large; margin-left: 5px">{{ currentRechargeValue / 100 }}</span>
              </div>
              <div style="font-weight: bolder; margin-top: 10px">直接支付</div>
            </div>
          </el-button>
        </el-row>
      </el-scrollbar>
    </el-main>
  </el-container>
  <PayDialog ref="payDialog"/>
  <PaySuccessDialog/>
</template>

<style scoped>
.active-card {
  border-color: #409eff;
}
</style>