<script setup>
import {Check, Lock, User} from "@element-plus/icons-vue";
import {ref} from "vue";
import {login} from "@/request/login.js";
import {useStorage} from "@vueuse/core";
import store from "@/store/index.js";
import {ElNotification} from "element-plus";
import router from "@/router/index.js";
import {getNavigationBarUserInformation} from "@/request/user.js";
import {getObjectUrlOfPublicResources} from "@/request/cos.js";

const emit = defineEmits(["setEnabledTab"]);
const username = ref(""), password = ref("");
const loginButtonDisabled = ref(true);

const checkIfEnableLoginButton = () => {
  loginButtonDisabled.value = !(username.value.length >= 4 && username.value.length <= 16 &&
      password.value.length >= 8 && password.value.length <= 20);
}

const Login = async () => {
  const loginData = await login(username.value, password.value);
  if (loginData !== null) {
    const authorization = useStorage("Authorization", "");
    authorization.value = loginData["authorization"];
    store.commit("setAuthorizationCode", loginData["authorization"]);
    ElNotification({
      "title": "登录成功",
      "type": "success"
    });
    const userData = await getNavigationBarUserInformation();
    if (userData !== null) {
      store.commit("setNickname", userData["nickname"]);
      store.commit("setUserType", userData["userType"]);
      await getObjectUrlOfPublicResources(userData["avatar"], (url) => {
        store.commit("setAvatar", url);
        store.commit("setIsLogin", true);
        router.push("/home");
      });
    }
  }
}

</script>

<template>
  <div class="x-y-center" style="width: 80%; text-align: center;">
    <h1>登录</h1>
    <el-row style="text-align: center; margin-bottom: 20px;" align="middle">
      <el-input v-model="username" placeholder="请输入用户名" clearable size="large" :prefix-icon="User"
                minlength="4" maxlength="16" @input="checkIfEnableLoginButton">
        <template #prepend>
          <span style="width: 50px">用户名</span>
        </template>
      </el-input>
    </el-row>
    <el-row style="text-align: center; margin-bottom: 10px;" align="middle">
      <el-input v-model="password" placeholder="请输入密码" clearable show-password size="large"
                :prefix-icon="Lock" minlength="8" @input="checkIfEnableLoginButton">
        <template #prepend>
          <span style="width: 50px">密码</span>
        </template>
      </el-input>
    </el-row>
    <el-row style="text-align: center; margin-bottom: 10px;" align="middle">
      <el-col :span="12">
        <el-button type="primary" text @click="router.push('/findBackPassword')">忘记密码?</el-button>
      </el-col>
      <el-col :span="12">
        <el-button type="warning" text @click="emit('setEnabledTab', 'register')">还没有账号?</el-button>
      </el-col>
    </el-row>
    <el-row style="text-align: center" align="middle">
      <el-button style="margin: 0 auto; width: 100px" type="primary" :icon="Check" round size="large"
                 plain v-bind:disabled="loginButtonDisabled" @click="Login">
        登录
      </el-button>
    </el-row>
  </div>
</template>

<style scoped>

</style>