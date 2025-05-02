<script setup>
import store from "@/store/index.js";
import router from "@/router/index.js";
import {Coin} from "@element-plus/icons-vue";
import VipTag from "@/components/navigationBar/VipTag.vue";
import LevelProgress from "@/components/navigationBar/LevelProgress.vue";
import {ref} from "vue";
import {getNavigationBarUserInformation} from "@/request/normal/user.js";
import {getObjectUrlOfPublicResources} from "@/request/normal/cos.js";

const userInformation = ref({
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
);

const refreshUserInformation = async () => {
  const data = await getNavigationBarUserInformation();
  if (data !== null) {
    store.commit("setUid", data["uid"]);
    store.commit("setNickname", data["nickname"]);
    store.commit("setUserType", data["userType"]);
    store.commit("setIsLogin", true);
    await getObjectUrlOfPublicResources(data["avatar"], (url) => {
      data["avatar"] = url;
      store.commit("setAvatar", url);
    })
    userInformation.value = data;
  }
}
refreshUserInformation();

const increasePoints = (points) => {
  userInformation.value.points += points;
}

defineExpose({
  increasePoints,
})
</script>

<template>
  <div style="width: 90%; padding: 20px">
    <el-row align="middle" justify="center">
      <el-avatar style="background-color: transparent" :size="75"
                 :src="store.getters.getAvatar"/>
    </el-row>
    <el-row align="middle" justify="center" style="margin-top: 5px">
      <el-text style="font-weight: bolder; color: black">{{ store.getters.getNickname }}</el-text>
    </el-row>
    <el-row align="middle" justify="center" style="margin-top: 5px; cursor: pointer" @click="router.push('/vip')">
      <VipTag :vip-type="userInformation.vip.vipType"/>
    </el-row>
    <el-row align="middle" justify="center" style="margin-top: 5px; cursor: pointer"
            @click="router.push('/userCenter/purse/balance')">
      <el-icon style="margin-right: 5px">
        <Coin/>
      </el-icon>
      <el-text>{{ userInformation.coins }}</el-text>
    </el-row>
    <el-row align="middle" justify="center" style="text-align: center; margin-top: 5px; cursor: pointer"
            @click="router.push('/userCenter/account/level')">
      <LevelProgress :current-points="userInformation.points"/>
    </el-row>
    <el-row align="middle" justify="center" style="margin-top: 10px; text-align: center">
      <el-col :span="8" class="line-button" style="border-radius: 5px"
              @click="router.push({path: `/artist/${store.getters.getUid}/livingRoom`, query: {tab: 'follows'}})">
        <el-statistic style="margin-bottom: 5px" title="关注" :value="userInformation.follows"/>
      </el-col>
      <el-col :span="8" class="line-button" style="border-radius: 5px"
              @click="router.push({path: `/artist/${store.getters.getUid}/livingRoom`, query: {tab: 'collections'}})">
        <el-statistic style="margin-bottom: 5px" title="收藏" :value="userInformation.collections"/>
      </el-col>
      <el-col :span="8" class="line-button" style="border-radius: 5px"
              @click="router.push({path: `/artist/${store.getters.getUid}/livingRoom`, query: {tab: 'articles'}})">
        <el-statistic style="margin-bottom: 5px" title="文章" :value="userInformation.articles"/>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped>
.line-button:hover {
  cursor: pointer;
  background-color: rgba(161, 155, 155, 0.2);
}
</style>