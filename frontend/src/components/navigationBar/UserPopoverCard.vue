<script setup>
import store from "@/store/index.js";
import {ArrowRight, Coin, EditPen, User} from "@element-plus/icons-vue";
import router from "@/router/index.js";
import VipTag from "@/components/navigationBar/VipTag.vue";
import LevelProgress from "@/components/navigationBar/LevelProgress.vue";

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

const logout = () => {
  localStorage.setItem("Authorization", "");
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
      <VipTag :vip-type="userInfomation.vip.vipType"/>
    </el-row>
    <el-row align="middle" justify="center" style="margin-top: 5px; cursor: pointer" @click="router.push('/userCenter/purse/balance')">
      <el-icon style="margin-right: 5px">
        <Coin/>
      </el-icon>
      <el-text>{{ userInfomation.coins }}</el-text>
    </el-row>
    <el-row align="middle" justify="center" style="text-align: center; margin-top: 5px; cursor: pointer" @click="router.push('/userCenter/account/level')">
      <LevelProgress :current-points="userInfomation.points"/>
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
      <el-col :span="12" style="font-size: 14px">账户管理</el-col>
      <el-col :span="6" style="text-align: center; font-size: 16px; height: 16px">
        <el-icon>
          <ArrowRight/>
        </el-icon>
      </el-col>
    </el-row>
    <el-row class="line-button" align="middle" justify="center" style="height: 40px; border-radius: 5px; margin-top: 5px" @click="router.push(`/artist/${store.getters.getUid}/livingRoom`)">
      <el-col :span="6" style="text-align: center; font-size: 16px; height: 16px">
        <el-icon>
          <icon-home/>
        </el-icon>
      </el-col>
      <el-col :span="12" style="font-size: 14px">个人空间</el-col>
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