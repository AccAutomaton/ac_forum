<script setup>
import {ChatDotRound, Check, Lock, Message, User} from "@element-plus/icons-vue";
import router from "@/router/index.js";
import {ref} from "vue";
import {ElMessageBox, ElNotification} from "element-plus";
import {findBackPassword, getEmailVerifyCodeForFindingBackPassword, getGraphicCaptchaImage,} from "@/request/normal/login.js";

const username = ref(""), newPassword = ref(""), confirmPassword = ref("");
const email = ref(""), verifyCode = ref("");
const graphicCaptchaDialogVisible = ref(false), graphicCaptchaUuid = ref(""), graphicCaptchaCode = ref("");
const graphicCaptchaImage = ref("");
const isSendingEmailVerifyCodeStatus = ref(false);
const emailCountdownValue = ref(Date.now());

const checkUsername = () => {
  if (username.value === "") {
    ElNotification({title: "用户名不能为空", type: "error"});
    return false;
  }
  if (!username.value.match(/^[a-zA-Z0-9_-]{4,16}$/)) {
    ElNotification({title: "用户名不合法", message: "应由4~16位字母/数字/下划线/连接符构成", type: "error"})
    return false;
  }
  return true;
}

const checkNewPassword = () => {
  if (newPassword.value === "") {
    ElNotification({title: "密码不能为空", type: "error"});
    return false;
  }
  if (!newPassword.value.match(/^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,}$/)) {
    ElNotification({title: "密码不合法", message: "应至少8位，且同时包含字母和数字(不含特殊符号)", type: "error"})
    return false;
  }
  return true;
}

const checkConfirmPassword = () => {
  if (newPassword.value !== confirmPassword.value) {
    ElNotification({title: "两次密码不一致", type: "error"});
    return false;
  }
  return true;
}

const checkEmail = () => {
  if (email.value === "") {
    ElNotification({title: "邮箱不能为空", type: "error"});
    return false;
  }
  if (!email.value.match(/^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/)) {
    ElNotification({title: "邮箱不合法", type: "error"})
    return false;
  }
  return true;
}

const checkVerifyCode = () => {
  if (verifyCode.value === "") {
    ElNotification({title: "验证码不能为空", type: "error"});
    return false;
  }
  if (!verifyCode.value.match(/^\d{6}$/)) {
    ElNotification({title: "验证码不合法", type: "error"})
    return false;
  }
  return true;
}

const checkGraphicCaptchaCode = () => {
  if (graphicCaptchaCode.value === "") {
    ElNotification({title: "图形验证码不能为空", type: "error"});
    return false;
  }
  return true;
}

const getNewGraphicCaptcha = async () => {
  const data = await getGraphicCaptchaImage();
  if (data !== null) {
    graphicCaptchaUuid.value = data["captchaUUID"];
    graphicCaptchaImage.value = data["base64Image"];
  }
}

const clickSendVerifyCodeButton = () => {
  if (checkEmail()) {
    graphicCaptchaCode.value = "";
    getNewGraphicCaptcha();
    graphicCaptchaDialogVisible.value = true
  }
}

const clickRefreshGraphicCaptchaButton = () => {
  getNewGraphicCaptcha();
}

const clickConfirmGraphicCaptchaButton = async () => {
  if (checkGraphicCaptchaCode()) {
    const data = await getEmailVerifyCodeForFindingBackPassword(username.value, email.value, graphicCaptchaUuid.value, graphicCaptchaCode.value);
    if (data !== null) {
      graphicCaptchaDialogVisible.value = false;
      resetEmailVerifyCodeCountdown();
      ElNotification({
        title: '发送成功',
        message: '请到邮箱 ' + email.value + ' 查收验证码',
        type: 'success'
      })
    } else {
      graphicCaptchaCode.value = "";
      await getNewGraphicCaptcha();
    }
  }
}

const resetEmailVerifyCodeCountdown = () => {
  isSendingEmailVerifyCodeStatus.value = true;
  emailCountdownValue.value = Date.now() + 1000 * 60;
}

const clickRegisterButton = async () => {
  if (checkUsername() && checkNewPassword() && checkConfirmPassword() && checkEmail() && checkVerifyCode()) {
    const data = await findBackPassword(username.value, verifyCode.value, newPassword.value);
    if (data !== null) {
      ElMessageBox.confirm(
          '是否跳转到登录页?',
          '重置密码成功',
          {
            customStyle: {
              width: "300px",
            },
            confirmButtonText: '确认',
            cancelButtonText: '取消',
            type: 'info',
          }
      ).then(() => {
        router.push('/login');
      })
      username.value = "";
      newPassword.value = "";
      confirmPassword.value = "";
      email.value = "";
      verifyCode.value = "";
    }
  }
}
</script>

<template>
  <el-card class="x-y-center" style="width: 480px; height: 460px" shadow="always">
    <div class="x-y-center" style="width: 80%; text-align: center;">
      <h1>重置密码</h1>
      <el-row style="text-align: center; margin-bottom: 10px;" align="middle">
        <el-input v-model="username" placeholder="请输入用户名" clearable size="large"
                  :prefix-icon="User" minlength="4" maxlength="16">
          <template #prepend>
            <span style="width: 50px">用户名</span>
          </template>
        </el-input>
      </el-row>
      <el-row style="text-align: center; margin-bottom: 10px;" align="middle">
        <el-input v-model="email" placeholder="请输入邮箱" clearable size="large" :prefix-icon="Message">
          <template #prepend>
            <span style="width: 50px">邮箱</span>
          </template>
        </el-input>
      </el-row>
      <el-row style="text-align: center; margin-bottom: 10px;" align="middle">
        <el-input v-model="verifyCode" placeholder="请输入验证码" clearable size="large" :prefix-icon="ChatDotRound"
                  minlength="6" maxlength="6">
          <template #prepend>
            <span style="width: 50px">验证码</span>
          </template>
          <template #append>
            <el-text v-if="isSendingEmailVerifyCodeStatus">
              <el-countdown :value="emailCountdownValue" format="ss" value-style="font-size: medium; color: darkgray"
                            @finish="isSendingEmailVerifyCodeStatus = false">
                <template #suffix>
                  <span style="font-size: medium; color: darkgray">秒</span>
                </template>
              </el-countdown>
            </el-text>
            <el-button v-else @click="clickSendVerifyCodeButton">
              发送验证码
            </el-button>
          </template>
        </el-input>
      </el-row>
      <el-row style="text-align: center; margin-bottom: 10px;" align="middle">
        <el-input v-model="newPassword" placeholder=" 8 位及以上字母和数字的组合" clearable show-password size="large"
                  :prefix-icon="Lock" minlength="8">
          <template #prepend>
            <span style="width: 50px">新密码</span>
          </template>
        </el-input>
      </el-row>
      <el-row style="text-align: center; margin-bottom: 20px;" align="middle">
        <el-input v-model="confirmPassword" placeholder="请确认密码" clearable show-password size="large"
                  :prefix-icon="Lock" minlength="8">
          <template #prepend>
            <span style="width: 50px">确认密码</span>
          </template>
        </el-input>
      </el-row>
      <el-row style="text-align: center" align="middle">
        <el-button style="margin: 0 auto; width: 120px" type="primary" :icon="Check" round size="large" plain
                   @click="clickRegisterButton">重置密码
        </el-button>
      </el-row>
    </div>

    <el-dialog v-model="graphicCaptchaDialogVisible" width="300" title="发送邮件验证码" align-center destroy-on-close
               :modal="false">
      <el-input v-model="graphicCaptchaCode" placeholder="请输入图形验证码" clearable size="large"
                :prefix-icon="ChatDotRound" minlength="5" maxlength="5">
        <template #append>
          <el-button style="padding: 0" @click="clickRefreshGraphicCaptchaButton">
            <el-image :src="graphicCaptchaImage" style="height: 30px"/>
          </el-button>
        </template>
      </el-input>
      <template #footer>
        <el-button @click="graphicCaptchaDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="clickConfirmGraphicCaptchaButton">
          确认
        </el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<style scoped>

</style>