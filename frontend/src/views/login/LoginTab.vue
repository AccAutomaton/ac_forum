<script setup>
import {Check, Lock, User} from "@element-plus/icons-vue";
import {ref} from "vue";

const emit = defineEmits(["setEnabledTab"]);
const username = ref(""), password = ref("");
</script>

<template>
  <div class="x-y-center" style="width: 80%; text-align: center;">
    <h1>登录</h1>
    <el-row style="text-align: center; margin-bottom: 20px;" align="middle">
      <el-input v-model="username" placeholder="请输入用户名" clearable size="large" :prefix-icon="User">
        <template #prepend>
          <span style="width: 50px">用户名</span>
        </template>
      </el-input>
    </el-row>
    <el-row style="text-align: center; margin-bottom: 10px;" align="middle">
      <el-input v-model="password" placeholder="请输入密码" clearable show-password size="large"
                :prefix-icon="Lock">
        <template #prepend>
          <span style="width: 50px">密码</span>
        </template>
      </el-input>
    </el-row>
    <el-row style="text-align: center; margin-bottom: 10px;" align="middle">
      <el-col :span="12">
        <el-button type="primary" text>忘记密码?</el-button>
      </el-col>
      <el-col :span="12">
        <el-button type="warning" text @click="emit('setEnabledTab', 'register')">还没有账号?</el-button>
      </el-col>
    </el-row>
    <el-row style="text-align: center" align="middle">
      <el-button style="margin: 0 auto; width: 100px" type="primary" :icon="Check" round size="large" plain>登录
      </el-button>
    </el-row>
  </div>
</template>

<style scoped>

</style>