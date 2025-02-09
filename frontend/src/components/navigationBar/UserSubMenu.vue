<script setup>
import store from "@/store/index.js";
import {Discount, Promotion, SwitchButton} from "@element-plus/icons-vue";
import {useStorage} from "@vueuse/core";
import {getNavigationBarUserInformation} from "@/request/user.js";
import {getObjectUrl} from "@/request/cos.js";
import router from "@/router/index.js";

const authorization = useStorage("Authorization", "");

const refreshNavigationBarUserInformation = async () => {
  if (authorization.value === "" && store.getters.getAuthorizationCode === "") {
    return;
  }
  const data = await getNavigationBarUserInformation();
  if (data !== null) {
    store.commit("setNickname", data["nickname"]);
    store.commit("setUserType", data["userType"]);
    store.commit("setIsLogin", true);
    getObjectUrl(data["avatar"], (url) => {
      store.commit("setAvatar", url);
    })
  }
}

const logout = () => {
  authorization.value = "";
  store.commit('clearLoginInformation');
  store.commit('clearUserInformation');
  router.push("/login");
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
    <el-menu-item index="/userCenter">
      <el-icon>
        <Discount/>
      </el-icon>
      <span style="margin: 0 auto">个人中心</span>
    </el-menu-item>
    <el-menu-item index="" @click="logout" style="color: red">
      <el-icon>
        <SwitchButton/>
      </el-icon>
      <span style="margin: 0 auto">退出登录</span>
    </el-menu-item>
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