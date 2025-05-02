<script setup>
import {Check, Lock} from "@element-plus/icons-vue";
import {ref} from "vue";
import {ElNotification} from "element-plus";
import {setPassword} from "@/request/normal/user.js";

const oldPassword = ref(""), newPassword = ref(""), confirmPassword = ref("");

const clickRevisePasswordButton = async () => {
  if (oldPassword.value === "") {
    ElNotification({title: "旧密码不能为空", type: "error"});
    return;
  }
  if (newPassword.value === "") {
    ElNotification({title: "新密码不能为空", type: "error"});
    return;
  }
  if (oldPassword.value === newPassword.value) {
    ElNotification({title: "新旧密码不能相同", type: "error"});
    return;
  }
  if (newPassword.value !== confirmPassword.value) {
    ElNotification({title: "确认密码与新密码不一致", type: "error"});
    return;
  }
  const data = await setPassword(oldPassword.value, newPassword.value);
  if (data !== null) {
    oldPassword.value = "";
    newPassword.value = "";
    confirmPassword.value = "";
    ElNotification({title: "修改成功", type: "success"});
  }
}
</script>

<template>
  <el-container>
    <el-header>
      <h2 style="margin-top: 10px">修改密码</h2>
    </el-header>
    <el-divider style="margin: 0"/>
    <el-main style="padding-bottom: 0">
      <el-scrollbar style="height: 75.5vh">
        <el-row style="text-align: center; margin-bottom: 10px; width: 400px;" align="middle">
          <el-input v-model="oldPassword" placeholder="请输入旧密码" clearable show-password size="large"
                    :prefix-icon="Lock" minlength="8">
            <template #prepend>
              <span style="width: 50px">旧密码</span>
            </template>
          </el-input>
        </el-row>
        <el-row style="text-align: center; margin-bottom: 10px; width: 400px;" align="middle">
          <el-input v-model="newPassword" placeholder="8 位及以上字母和数字" clearable show-password size="large"
                    :prefix-icon="Lock" minlength="8">
            <template #prepend>
              <span style="width: 50px">新密码</span>
            </template>
          </el-input>
        </el-row>
        <el-row style="text-align: center; margin-bottom: 20px; width: 400px;" align="middle">
          <el-input v-model="confirmPassword" placeholder="请确认密码" clearable show-password size="large"
                    :prefix-icon="Lock" minlength="8">
            <template #prepend>
              <span style="width: 50px">确认密码</span>
            </template>
          </el-input>
        </el-row>
        <el-row style="text-align: center; width: 400px;" align="middle">
          <el-button plain type="primary" style="margin: 0 auto" @click="clickRevisePasswordButton">
            <el-icon style="margin-right: 5px"><Check /></el-icon>
            <span>修改密码</span>
          </el-button>
        </el-row>
      </el-scrollbar>
    </el-main>
  </el-container>
</template>

<style scoped>

</style>