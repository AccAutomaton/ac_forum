<script setup>
import store from "@/store/index.js";
import {Promotion} from "@element-plus/icons-vue";
import {useStorage} from "@vueuse/core";
import {getNavigationBarUserInformation} from "@/request/normal/user.js";
import {getObjectUrlOfPublicResources} from "@/request/normal/cos.js";
import UserPopoverCard from "@/components/navigationBar/UserPopoverCard.vue";
import {ref} from "vue";

const authorization = useStorage("Authorization", "");
const userInformation = ref({
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

const refreshNavigationBarUserInformation = async () => {
  if (authorization.value === "" && store.getters.getAuthorizationCode === "") {
    return;
  }
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
refreshNavigationBarUserInformation();
</script>

<template>
  <el-sub-menu index="" v-if="store.getters.getIsLogin">
    <template #title>
      <el-avatar style="margin-right: 10px; background-color: transparent" :size="25"
                 :src="store.getters.getAvatar"/>
      {{ store.getters.getNickname }}
    </template>
    <UserPopoverCard :userInfomation="userInformation"/>
  </el-sub-menu>
  <el-menu-item index="/login" v-else>
    <el-icon>
      <Promotion/>
    </el-icon>
    <span>注册 / 登录</span>
  </el-menu-item>
</template>

<style scoped>

</style>