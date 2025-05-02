<script setup>
import {ref} from "vue";
import {rootRegisterUser} from "@/request/root/user.js";
import {ElNotification} from "element-plus";

const username = ref(""), password = ref(""), email = ref("");
const clearContentAfterSending = ref(false);

const onClickRegisterButton = async () => {
  const data = await rootRegisterUser(username.value, password.value, email.value);
  if (data !== null) {
    ElNotification({type: 'success', title: '注册成功'})
    if (clearContentAfterSending.value) {
      username.value = "";
      password.value = "";
      email.value = "";
    }
  }
}
</script>

<template>
  <el-row>
    <el-text style="font-size: large; color: black; font-weight: bold">
      注册用户
    </el-text>
  </el-row>
  <el-row style="margin: 20px 0 10px 0">
    <el-text>
      用户名
    </el-text>
  </el-row>
  <el-row style="width: 300px">
    <el-input v-model="username"/>
  </el-row>
  <el-row style="margin: 20px 0 10px 0">
    <el-text>
      密码
    </el-text>
  </el-row>
  <el-row style="width: 300px">
    <el-input v-model="password" type="password"/>
  </el-row>
  <el-row style="margin: 20px 0 10px 0">
    <el-text>
      邮箱
    </el-text>
  </el-row>
  <el-row style="width: 300px">
    <el-input v-model="email"/>
  </el-row>
  <el-row>
    <el-button type="primary" style="margin-top: 20px; width: 100px" plain @click="onClickRegisterButton">注册</el-button>
  </el-row>
  <el-row style="margin-top: 10px">
    <el-text>注册后清空内容</el-text>
    <el-switch style="margin-left: 10px" v-model="clearContentAfterSending"/>
  </el-row>
</template>

<style scoped>

</style>