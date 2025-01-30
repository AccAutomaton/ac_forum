<script setup>

import {Discount, Promotion, SwitchButton} from "@element-plus/icons-vue";
import store from "@/store/index.js";
import {useStorage} from "@vueuse/core";
import {getNavigationBarUserInformation} from "@/request/user.js";
import router from "@/router/index.js";
import {cos} from "@/request/cos.js";
import {ElNotification} from "element-plus";

const authorization = useStorage("Authorization", "");

const refreshNavigationBarUserInformation = async () => {
  if (authorization.value === "" && store.getters.getAuthorizationCode === "") {
    return;
  }
  const data = await getNavigationBarUserInformation();
  if (data !== null) {
    store.commit("setUsername", data["username"]);
    store.commit("setUserType", data["userType"]);
    store.commit("setIsLogin", true);
    cos(data["avatar"]).getObjectUrl(
        {
          Bucket: data["avatar"]["bucket"],
          Region: data["avatar"]["region"],
          Key: data["avatar"]["key"],
        },
        (err, data) => {
          if (err !== null) {
            ElNotification({title: "服务器错误", type: "error", message: "存储服务发生错误"});
          } else {
            store.commit("setAvatar", data["Url"]);
          }
        }
    )
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
  <el-container>
    <el-header>
      <el-menu
          :default-active="$route.path"
          mode="horizontal"
          :ellipsis="false"
          router
          popper-effect="light"
          close-on-click-outside

      >
        <el-menu-item style="pointer-events: none">
          <el-container>
            <img src="@/assets/favicon.png" alt="" style="height: 3.5rem"/>
            <div style="font-size: x-large; font-weight: bolder; margin-left: 5px">AC论坛</div>
          </el-container>
        </el-menu-item>
        <div style="flex-grow: 1"/>
        <el-sub-menu index="" v-if="store.getters.getIsLogin">
          <template #title>
            <el-avatar style="margin-right: 10px; background-color: transparent" :size="25"
                       :src="store.getters.getAvatar"/>
            {{ store.getters.getUsername }}
          </template>
          <el-menu-item index="/user/home">
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
          <span>
            注册 / 登录
          </span>
        </el-menu-item>
      </el-menu>
    </el-header>
    <el-main style="height: 83vh">
      <slot/>
    </el-main>
    <el-footer>
      <el-divider border-style="dashed">
        <el-text class="mx-1" style="color: darkgray"> © 2025 AC论坛 All Rights Reserved.
          网站仅供学习交流使用
        </el-text>
      </el-divider>
      <el-divider border-style="dashed">
        <el-link href="https://beian.miit.gov.cn/" target="_blank" style="color: darkgray">赣ICP备2022006483号</el-link>
        <span style="color: darkgray"> | </span>
        <el-link href="https://www.beian.gov.cn/portal/registerSystemInfo" target="_blank" style="color: darkgray">
          赣公网安备36011102000604号
        </el-link>
      </el-divider>
    </el-footer>
  </el-container>
</template>

<style scoped>

</style>