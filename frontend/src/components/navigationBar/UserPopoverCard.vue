<script setup>
import store from "@/store/index.js";
import {ArrowRight, Coin, EditPen, User} from "@element-plus/icons-vue";
import {computed} from "vue";
import router from "@/router/index.js";
import {useStorage} from "@vueuse/core";

const {userInfomation} = defineProps({
  userInfomation: {
    coins: Number,
    points: Number,
    vip: {
      vipType: {
        index: Number,
        value: String,
      },
      expirationTime: String,
    },
    follows: Number,
    collections: Number,
    articles: Number,
  },
})

const levels = [
  {
    value: "黑铁",
    maxPoint: 1024,
    disableColor: "rgba(37,37,37,0.1)",
    disableFontColor: "rgba(37,37,37,0.3)",
    enableColor: "rgba(37,37,37,1)",
  },
  {
    value: "青铜",
    maxPoint: 2048,
    disableColor: "rgba(80,56,7,0.1)",
    disableFontColor: "rgba(80,56,7,0.3)",
    enableColor: "rgba(80,56,7,1)",
  },
  {
    value: "白银",
    maxPoint: 4096,
    disableColor: "rgba(142,140,138,0.1)",
    disableFontColor: "rgba(142,140,138,0.5)",
    enableColor: "rgba(142,140,138,1)",
  },
  {
    value: "黄金",
    maxPoint: 8192,
    disableColor: "rgba(221,140,32,0.1)",
    disableFontColor: "rgba(221,140,32,0.4)",
    enableColor: "rgba(221,140,32,1)",
  },
  {
    value: "铂金",
    maxPoint: 16384,
    disableColor: "rgba(47,150,170,0.1)",
    disableFontColor: "rgba(47,150,170,0.3)",
    enableColor: "rgba(47,150,170,1)",
  },
  {
    value: "钻石",
    maxPoint: 32768,
    disableColor: "rgba(191,109,218,0.1)",
    disableFontColor: "rgba(191,109,218,0.4)",
    enableColor: "rgba(191,109,218,1)",
  },
  {
    value: "超凡",
    maxPoint: 65536,
    disableColor: "rgba(33,133,78,0.1)",
    disableFontColor: "rgba(33,133,78,0.3)",
    enableColor: "rgba(33,133,78,1)",
  },
  {
    value: "神话",
    maxPoint: 131072,
    disableColor: "rgba(157,29,65,0.1)",
    disableFontColor: "rgba(157,29,65,0.3)",
    enableColor: "rgba(157,29,65,1)",
  },
  {
    value: "辐能",
    maxPoint: 999999999,
    disableColor: "rgba(242,246,6,0.1)",
    disableFontColor: "rgba(169,174,11,0.4)",
    enableColor: "rgba(169,174,11,1)",
  },
];

const currentLeveIndex = computed(() => {
  for (let i = 0; i < levels.length; i++) {
    if (userInfomation.points < levels[i].maxPoint) {
      return i;
    }
  }
  return levels.length - 1;
})

const authorization = useStorage("Authorization", "");
const logout = () => {
  authorization.value = "";
  store.commit('clearLoginInformation');
  store.commit('clearUserInformation');
  router.push("/login");
}
</script>

<template>
  <div style="width: 275px; padding: 5px 10px">
    <el-row align="middle" justify="center">
      <el-avatar style="background-color: transparent" :size="75"
                 :src="store.getters.getAvatar"/>
    </el-row>
    <el-row align="middle" justify="center" style="margin-top: 5px">
      <el-text style="font-weight: bolder; color: black">{{ store.getters.getNickname }}</el-text>
    </el-row>
    <el-row align="middle" justify="center" style="margin-top: 5px; cursor: pointer" @click="router.push('/vip')">
      <el-tag v-if="userInfomation.vip.vipType.index === 0" effect="dark" color="rgba(112,111,111,0.5)" round
              style="border-color: transparent">
        大众会员
      </el-tag>
      <el-tag v-else-if="userInfomation.vip.vipType.index === 1" effect="dark" color="rgba(255,174,0,0.7)" round
              style="border-color: transparent">
        周度会员
      </el-tag>
      <el-tag v-else-if="userInfomation.vip.vipType.index === 2" effect="dark" color="rgba(255,174,0,0.8)" round
              style="border-color: transparent">
        月度会员
      </el-tag>
      <el-tag v-else-if="userInfomation.vip.vipType.index === 3" effect="dark" color="rgba(255,174,0,0.9)" round
              style="border-color: transparent">
        季度会员
      </el-tag>
      <el-tag v-else-if="userInfomation.vip.vipType.index === 4" effect="dark" color="rgba(255,174,0,1)" round
              style="border-color: transparent">
        年度会员
      </el-tag>
    </el-row>
    <el-row align="middle" justify="center" style="margin-top: 5px; cursor: pointer" @click="router.push('/userCenter/purse/balance')">
      <el-icon style="margin-right: 5px">
        <Coin/>
      </el-icon>
      <el-text>{{ userInfomation.coins }}</el-text>
    </el-row>
    <el-row align="middle" justify="center" style="text-align: center; margin-top: 5px; cursor: pointer" @click="router.push('/userCenter/account/level')">
      <el-col :span="3" :style="'font-weight: bold; color: ' + levels[currentLeveIndex].enableColor">
        {{ levels[currentLeveIndex].value }}
      </el-col>
      <el-col :span="18">
        <el-progress
            :text-inside="true"
            :stroke-width="15"
            :percentage="userInfomation.points * 100 / levels[currentLeveIndex].maxPoint"
            :color="levels[currentLeveIndex].enableColor"
        >
          <span>{{ userInfomation.points + " / " + levels[currentLeveIndex].maxPoint }}</span>
        </el-progress>
      </el-col>
      <el-col :span="3"
              :style="'font-weight: bold; color: ' + levels[currentLeveIndex === levels.length - 1 ? currentLeveIndex : currentLeveIndex + 1].disableFontColor">
        {{ levels[currentLeveIndex === levels.length - 1 ? currentLeveIndex : currentLeveIndex + 1].value }}
      </el-col>
    </el-row>
    <el-row align="middle" justify="center" style="margin-top: 10px; text-align: center">
      <el-col :span="8" class="line-button" style="border-radius: 5px">
        <el-statistic style="margin-bottom: 5px" title="关注" :value="userInfomation.follows"/>
      </el-col>
      <el-col :span="8" class="line-button">
        <el-statistic style="margin-bottom: 5px" title="收藏" :value="userInfomation.collections"/>
      </el-col>
      <el-col :span="8" class="line-button">
        <el-statistic style="margin-bottom: 5px" title="文章" :value="userInfomation.articles"/>
      </el-col>
    </el-row>
    <el-divider style="margin: 10px 0 5px 0"/>
    <el-row class="line-button" align="middle" justify="center" style="height: 40px; border-radius: 5px" @click="router.push('/userCenter')">
      <el-col :span="6" style="text-align: center; font-size: 16px; height: 16px">
        <el-icon>
          <User/>
        </el-icon>
      </el-col>
      <el-col :span="12" style="font-size: 14px">个人中心</el-col>
      <el-col :span="6" style="text-align: center; font-size: 16px; height: 16px">
        <el-icon>
          <ArrowRight/>
        </el-icon>
      </el-col>
    </el-row>
    <el-row class="line-button" align="middle" justify="center" @click="router.push('/creation')"
            style="height: 40px; border-radius: 5px; margin-top: 5px">
      <el-col :span="6" style="text-align: center; font-size: 16px; height: 16px">
        <el-icon>
          <EditPen/>
        </el-icon>
      </el-col>
      <el-col :span="12" style="font-size: 14px">创作中心</el-col>
      <el-col :span="6" style="text-align: center; font-size: 16px; height: 16px">
        <el-icon>
          <ArrowRight/>
        </el-icon>
      </el-col>
    </el-row>
    <el-divider style="margin: 5px 0"/>
    <el-row class="line-button" align="middle" justify="center" @click="logout"
            style="height: 40px; border-radius: 5px; margin-top: 5px; color: darkred">
      <el-col :span="6" style="text-align: center; font-size: 16px; height: 16px">
        <el-icon>
          <icon-export/>
        </el-icon>
      </el-col>
      <el-col :span="12" style="font-size: 14px">退出登录</el-col>
      <el-col :span="6"/>
    </el-row>
  </div>
</template>

<style scoped>
.line-button:hover {
  cursor: pointer;
  background-color: rgba(161, 155, 155, 0.2);
}
</style>