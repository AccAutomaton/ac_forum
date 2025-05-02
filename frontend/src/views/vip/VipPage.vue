<script setup>
import {ref} from "vue";
import {CaretTop, QuestionFilled, Warning} from "@element-plus/icons-vue";
import {getVip, getVipPrice, refreshPayingStatus} from "@/request/normal/vip.js";
import PayDialog from "@/views/vip/PayDialog.vue";
import PaySuccessDialog from "@/views/vip/PaySuccessDialog.vue";
import {ElNotification} from "element-plus";

const vipType = ref([
  {
    span: 4,
    cardStyle: {
      "background-color": "rgba(211,211,211,0.3)"
    },
    shadow: "hover",
    vipIndex: 0,
    vipTypeName: "大众会员",
    coinsPerDay: 8,
    price: 0,
  },
  {
    span: 5,
    cardStyle: {
      "background-color": "rgba(255,215,0,0.2)"
    },
    shadow: "hover",
    vipIndex: 1,
    vipTypeName: "周度会员",
    coinsPerDay: 16,
    price: 68,
  },
  {
    span: 5,
    cardStyle: {
      "background-color": "rgba(255,215,0,0.4)"
    },
    shadow: "hover",
    vipIndex: 2,
    vipTypeName: "月度会员",
    coinsPerDay: 32,
    price: 168,
  },
  {
    span: 5,
    cardStyle: {
      "background-color": "rgba(255,215,0,0.6)"
    },
    shadow: "hover",
    vipIndex: 3,
    vipTypeName: "季度会员",
    coinsPerDay: 64,
    price: 328,
  },
  {
    span: 5,
    cardStyle: {
      "background-color": "rgba(255,215,0,0.8)"
    },
    shadow: "hover",
    vipIndex: 4,
    vipTypeName: "年度会员",
    coinsPerDay: 128,
    price: 648,
  }
]);
const currentVipType = ref(0), currentExpirationTime = ref(""), currentSelectedVipType = ref(0);
const priceWithCoins = ref(0), priceWithoutCoins = ref(0), targetExpireDate = ref("");
const ownCoins = ref(0), payCoins = ref(0);

const onClickVipCard = (index) => {
  if (index === 0 || index < currentVipType.value) {
    return;
  }
  currentSelectedVipType.value = index;
  for (let i = 0; i < vipType.value.length; i++) {
    if (i === index) {
      vipType.value[i].cardStyle["border-color"] = "#409eff";
    } else {
      vipType.value[i].cardStyle["border-color"] = "#e4e7ed";
    }
  }
  refreshPrice();
}

const getUserVipInformation = async () => {
  const data = await getVip();
  if (data !== null) {
    currentVipType.value = data["vipType"]["index"];
    currentSelectedVipType.value = currentVipType.value === 0 ? 1 : currentVipType.value;
    currentExpirationTime.value = data["expirationTime"];
    await refreshPrice();
  }
}

const refreshPrice = async () => {
  const data = await getVipPrice(currentSelectedVipType.value);
  if (data !== null) {
    priceWithCoins.value = data["priceWithCoins"];
    priceWithoutCoins.value = data["priceWithoutCoins"];
    targetExpireDate.value = data["targetExpireDate"];
    ownCoins.value = data["ownCoins"];
    payCoins.value = data["payCoins"];
  }
}

getUserVipInformation();

const payDialog = ref();
const pay = (mode) => {
  if (mode === 0) {
    payDialog.value.showDialog(priceWithCoins.value, 0, currentSelectedVipType.value);
  } else if (mode === 1) {
    payDialog.value.showDialog(priceWithoutCoins.value, 1, currentSelectedVipType.value);
  }
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
  <el-row>
    <el-col :span="3"/>
    <el-col :span="18">
      <el-container>
        <el-header>
          <div>
            <span style="font-size: xx-large; font-weight: bolder">会员中心</span>
            <el-text><el-icon style="font-size: 11px; margin-left: 20px; color: gray"><QuestionFilled /></el-icon></el-text>
            <el-text style="font-size: small; color: gray">充值没到账? 点击</el-text>
            <el-button text style="font-size: small; padding: 0" type="primary" @click="clickRefreshPayingStatusButton">此处</el-button>
            <el-text style="font-size: small; color: gray">刷新</el-text>
          </div>
          <el-divider/>
        </el-header>
        <el-main style="margin-top: 20px">
          <el-row :gutter="20" style="width: 100%">
            <el-col v-for="(type, index) in vipType" :key="type.vipIndex" :span="type.span">
              <el-card class="vip-card" :style="type.cardStyle" shadow="hover" @click="onClickVipCard(index)">
                <div style="font-weight: bold; font-size: large">{{ type.vipTypeName }}</div>
                <div style="margin-top: 20px; font-size: small; color: gray">每日签到可获取</div>
                <div style="font-size: small; color: gray">
                  <span>{{ type.coinsPerDay }}</span>
                  <span> 经验</span>
                </div>
                <div style="float: right; margin-top: 40px">
                  <span>RMB</span>
                  <span style="font-size: xx-large; margin-left: 10px">{{ type.price }}</span>
                </div>
              </el-card>
              <div v-if="type.vipIndex === currentVipType" style="width: 100%">
                <div style="text-align: center; width: 100%; margin-top: 10px">
                  <el-icon>
                    <CaretTop/>
                  </el-icon>
                </div>
                <div style="text-align: center; width: 100%">
                  当前会员
                </div>
                <div v-if="type.vipIndex !== 0" style="text-align: center; width: 100%; color: gray; font-size: small">
                  将于 {{ currentExpirationTime }} 到期
                </div>
              </div>
              <div v-else-if="type.vipIndex === currentSelectedVipType"
                   style="color: #409eff; text-align: center; width: 100%; margin-top: 10px">
                <el-icon>
                  <CaretTop/>
                </el-icon>
              </div>
            </el-col>
          </el-row>
          <el-divider/>
          <div style="width: 100%; text-align: center">
            <div v-if="currentSelectedVipType !== 0 && currentSelectedVipType === currentVipType">
              <span>续费 </span>
              <span style="font-weight: bold">{{ vipType[currentSelectedVipType].vipTypeName }}</span>
            </div>
            <div v-else>
              <span>升级到 </span>
              <span style="font-weight: bold">{{ vipType[currentSelectedVipType].vipTypeName }}</span>
              <el-tooltip placement="bottom" effect="light">
                <el-icon style="color: gray; margin-left: 1px" size="small">
                  <Warning/>
                </el-icon>
                <template #content>
                  <div style="font-size: small">当前会员的剩余天数将自动折算并抵扣</div>
                </template>
              </el-tooltip>
            </div>
          </div>
          <div style="width: 100%; text-align: center; color: gray; font-size: small">
            至 {{ targetExpireDate }}
          </div>
          <div style="width: 100%; text-align: center; margin-top: 10px">
            <el-button style="width: 150px; height: 100px" round plain color="rgba(219,78,252,1)" @click="pay(0)">
              <div style="float: revert">
                <div>
                  <span>RMB</span>
                  <span style="font-size: xx-large; margin-left: 5px">{{ parseInt(priceWithCoins / 100) }}</span>
                  <span v-if="priceWithCoins % 100 !== 0" style="font-size: large">.{{ priceWithCoins % 100 }}</span>
                </div>
                <div style="font-weight: bolder; margin-top: 10px">
                  <span>使用AC币抵扣</span>
                  <el-tooltip placement="bottom" effect="light">
                    <el-icon style="margin-left: 1px" size="small">
                      <Warning/>
                    </el-icon>
                    <template #content>
                      <div style="font-size: small">您当前拥有的AC币: {{ ownCoins }}</div>
                      <div style="font-size: small">用于抵扣的AC币: {{ payCoins }}</div>
                      <div style="font-size: small">100 AC币可抵扣 1 RMB</div>
                    </template>
                  </el-tooltip>
                </div>
              </div>
            </el-button>
            <el-button style="width: 150px; height: 100px" round plain color="rgba(88,106,254,1)" @click="pay(1)">
              <div style="float: revert">
                <div>
                  <span>RMB</span>
                  <span style="font-size: xx-large; margin-left: 5px">{{ parseInt(priceWithoutCoins / 100) }}</span>
                  <span v-if="priceWithoutCoins % 100 !== 0" style="font-size: large">.{{
                      priceWithoutCoins % 100
                    }}</span>
                </div>
                <div style="font-weight: bolder; margin-top: 10px">直接支付</div>
              </div>
            </el-button>
          </div>
        </el-main>
      </el-container>
    </el-col>
    <el-col :span="3"/>
  </el-row>
  <PayDialog ref="payDialog"/>
  <PaySuccessDialog/>
</template>

<style scoped>
.vip-card {
  height: 200px;
  cursor: pointer;
}
</style>