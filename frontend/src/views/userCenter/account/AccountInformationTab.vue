<script setup>

import {getObjectUrlOfPublicResources, uploadObject} from "@/request/cos.js";
import {ElNotification} from "element-plus";
import store from "@/store/index.js";
import {ref} from "vue";
import {
  getAvatarGetAuthorization,
  getAvatarUpdateAuthorization,
  getEmailVerifyCodeForSettingEmail,
  getUserDetails,
  setAvatarCustomization,
  setEmail,
  setNickname
} from "@/request/user.js";
import {ChatDotRound, Coordinate, Edit, Message, Upload} from "@element-plus/icons-vue";
import {getGraphicCaptchaImage} from "@/request/login.js";

const avatar = ref(""), isDefaultAvatar = ref(false);
const uid = ref(""), username = ref(""), email = ref("");
const status = ref({}), userType = ref({});
const nickname = ref(""), createTime = ref(""), updateTime = ref("");

const newNickname = ref(""), reviseNicknameDialogVisible = ref(false);

const newEmail = ref(""), verifyCode = ref(""), reviseEmailDialogVisible = ref(false);
const graphicCaptchaUuid = ref(""), graphicCaptchaCode = ref(""), graphicCaptchaImage = ref("");
const isSendingEmailVerifyCodeStatus = ref(false);
const emailCountdownValue = ref(Date.now());

const refreshAvatarUrl = async (needRefreshGlobalAvatar = false) => {
  const data = await getAvatarGetAuthorization();
  if (data !== null) {
    if (data.includes("default-avatar")) {
      isDefaultAvatar.value = true;
    }
    await getObjectUrlOfPublicResources(data, (url) => {
      avatar.value = url;
      if (needRefreshGlobalAvatar) {
        store.commit("setAvatar", url);
      }
    });
  }
}
refreshAvatarUrl();

const refreshUserDetails = async () => {
  const data = await getUserDetails();
  if (data !== null) {
    uid.value = data["uid"];
    store.commit("setUid", data["uid"]);
    username.value = data["username"];
    store.commit("setUsername", data["username"]);
    email.value = data["email"];
    status.value = data["status"];
    userType.value = data["userType"];
    store.commit("setUserType", data["userType"]);
    nickname.value = data["nickname"];
    newNickname.value = data["nickname"];
    store.commit("setNickname", data["nickname"]);
    createTime.value = data["createTime"];
    updateTime.value = data["updateTime"];
  }
}
refreshUserDetails();

const doUpdateAvatar = () => {
  window.showOpenFilePicker({
    types: [
      {
        description: "图片(头像)",
        accept: {
          "image/png": [".png"],
        }
      }
    ],
    excludeAcceptAllOption: true,
    multiple: false,
  }).then(async (res) => {
    const file = await res[0].getFile();
    const data = await getAvatarUpdateAuthorization();
    if (data !== null) {
      uploadObject(data["targetAvatar"], file, async () => {
        if (isDefaultAvatar.value) {
          const data = await setAvatarCustomization();
          if (data !== null) {
            await refreshAvatarUrl(true);
            ElNotification({title: "修改成功", type: "success", message: "新头像将稍后生效"});
          } else {
            ElNotification({title: "服务器错误", type: "error", message: "请稍后再试"})
          }
        } else {
          await refreshAvatarUrl(true);
          ElNotification({title: "修改成功", type: "success", message: "新头像将稍后生效"});
        }
      })
    }
  }).catch(() => {
    ElNotification({title: "操作取消", type: "info"});
  });
}

const clickConfirmReviseNicknameButton = async () => {
  if (newNickname.value === "") {
    ElNotification({title: "昵称不能为空", type: "error"})
  } else {
    const data = await setNickname(newNickname.value);
    if (data !== null) {
      await refreshUserDetails();
      ElNotification({title: "修改成功", type: "success"});
      reviseNicknameDialogVisible.value = false;
    }
  }
}

const checkNewEmail = () => {
  if (newEmail.value === "") {
    ElNotification({title: "邮箱不能为空", type: "error"});
    return false;
  }
  if (!newEmail.value.match(/^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/)) {
    ElNotification({title: "邮箱不合法", type: "error"})
    return false;
  }
  return true;
}

const resetEmailVerifyCodeCountdown = () => {
  isSendingEmailVerifyCodeStatus.value = true;
  emailCountdownValue.value = Date.now() + 1000 * 60;
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

const clickRefreshGraphicCaptchaButton = () => {
  getNewGraphicCaptcha();
}

const clickSendVerifyCodeButton = async () => {
  if (checkNewEmail() && checkGraphicCaptchaCode()) {
    const data = await getEmailVerifyCodeForSettingEmail(newEmail.value, graphicCaptchaUuid.value, graphicCaptchaCode.value);
    if (data !== null) {
      resetEmailVerifyCodeCountdown();
      ElNotification({
        title: '发送成功',
        message: '请到邮箱 ' + newEmail.value + ' 查收验证码',
        type: 'success'
      })
    } else {
      graphicCaptchaCode.value = "";
      await getNewGraphicCaptcha();
    }
  }
}

const clickConfirmReviseEmailButton = async () => {
  if (checkNewEmail() && checkVerifyCode()) {
    const data = await setEmail(newEmail.value, verifyCode.value);
    if (data !== null) {
      await refreshUserDetails();
      ElNotification({title: "修改成功", type: "success"});
      newEmail.value = "";
      graphicCaptchaCode.value = "";
      verifyCode.value = "";
      reviseEmailDialogVisible.value = false;
    }
  }
}
</script>

<template>
  <el-container>
    <el-header>
      <h2 style="margin-top: 10px">账户信息</h2>
    </el-header>
    <el-divider style="margin: 0"/>
    <el-main style="padding-bottom: 0">
      <el-scrollbar style="height: 70vh">
        <el-row style="align-items: center">
          <el-text size="large" class="title-text">头像</el-text>
          <el-image
              style="width: 75px; height: 75px; margin-right: 55px;
               border-style: dotted; border-color: lightgray; border-radius: 38px;"
              :src="avatar"
              :zoom-rate="1.2"
              :max-scale="7"
              :min-scale="0.2"
              :preview-src-list="[avatar]"
              fit="cover"
              hide-on-click-modal
              close-on-press-escape
          />
          <el-button :icon="Upload" round @click="doUpdateAvatar">更换头像</el-button>
        </el-row>
        <el-divider :border-style="'dotted'"/>
        <el-row>
          <el-text size="large" class="title-text">账号ID</el-text>
          <el-text>{{ uid }}</el-text>
        </el-row>
        <el-divider :border-style="'dotted'"/>
        <el-row>
          <el-text size="large" class="title-text">用户名</el-text>
          <el-text>{{ username }}</el-text>
        </el-row>
        <el-divider :border-style="'dotted'"/>
        <el-row>
          <el-text size="large" class="title-text">昵称</el-text>
          <el-text>{{ nickname }}</el-text>
          <el-button style="margin-left: 25px" :icon="Edit" circle plain @click="reviseNicknameDialogVisible = true"/>
        </el-row>
        <el-divider :border-style="'dotted'"/>
        <el-row>
          <el-text size="large" class="title-text">邮箱</el-text>
          <el-text>{{ email }}</el-text>
          <el-button style="margin-left: 25px" :icon="Edit" circle plain
                     @click="reviseEmailDialogVisible = true; getNewGraphicCaptcha()"/>
        </el-row>
        <el-divider :border-style="'dotted'"/>
        <el-row>
          <el-text size="large" class="title-text">账号类型</el-text>
          <el-tag type="info" effect="plain" v-if="userType['index'] === 3">{{ userType["value"] }}</el-tag>
          <el-tag type="primary" effect="plain" v-else-if="userType['index'] === 2">{{ userType["value"] }}</el-tag>
          <el-tag type="warning" effect="plain" v-else-if="userType['index'] === 1">{{ userType["value"] }}</el-tag>
          <el-tag type="danger" effect="plain" v-else-if="userType['index'] === 0">{{ userType["value"] }}</el-tag>
        </el-row>
        <el-divider :border-style="'dotted'"/>
        <el-row>
          <el-text size="large" class="title-text">账号状态</el-text>
          <el-tag type="success" effect="plain" v-if="status['index'] === 0">{{ status["value"] }}</el-tag>
          <el-tag type="danger" effect="plain" v-else-if="status['index'] === 1">{{ status["value"] }}</el-tag>
        </el-row>
        <el-divider :border-style="'dotted'"/>
        <el-row>
          <el-text size="large" class="title-text">创建时间</el-text>
          <el-text>
            <el-date-picker
                v-model="createTime"
                type="datetime"
                value-format="YYYY-MM-DD hh:mm:ss"
                format="YYYY年MM月DD日   hh:mm:ss"
                readonly
            />
          </el-text>
        </el-row>
        <el-divider :border-style="'dotted'"/>
        <el-row>
          <el-text size="large" class="title-text">修改时间</el-text>
          <el-text>
            <el-date-picker
                v-model="updateTime"
                type="datetime"
                value-format="YYYY-MM-DD hh:mm:ss"
                format="YYYY年MM月DD日   hh:mm:ss"
                readonly
            />
          </el-text>
        </el-row>
        <el-divider :border-style="'dotted'"/>
      </el-scrollbar>
    </el-main>
  </el-container>

  <el-dialog v-model="reviseNicknameDialogVisible" width="400" title="修改昵称" align-center destroy-on-close>
    <el-row style="text-align: center" align="middle">
      <el-input v-model="newNickname" placeholder="请输入新昵称" clearable size="large"
                :prefix-icon="Coordinate" maxlength="16" show-word-limit>
        <template #prepend>
          <span style="width: 40px">新昵称</span>
        </template>
      </el-input>
    </el-row>
    <template #footer>
      <el-button @click="reviseNicknameDialogVisible = false; newNickname = nickname">取消</el-button>
      <el-button type="primary" @click="clickConfirmReviseNicknameButton">
        确认
      </el-button>
    </template>
  </el-dialog>

  <el-dialog v-model="reviseEmailDialogVisible" width="500" title="修改邮箱" align-center destroy-on-close>
    <el-row style="text-align: center; margin-bottom: 10px;" align="middle">
      <el-input v-model="newEmail" placeholder="请输入邮箱" clearable size="large" :prefix-icon="Message">
        <template #prepend>
          <span style="width: 70px">新邮箱</span>
        </template>
      </el-input>
    </el-row>
    <el-row style="text-align: center; margin-bottom: 10px;" align="middle" v-if="!isSendingEmailVerifyCodeStatus">
      <el-input v-model="graphicCaptchaCode" placeholder="请输入图形验证码" clearable size="large"
                :prefix-icon="ChatDotRound" minlength="5" maxlength="5" show-word-limit>
        <template #prepend>
          <span style="width: 70px">图形验证码</span>
        </template>
        <template #append>
          <el-button style="padding: 0" @click="clickRefreshGraphicCaptchaButton">
            <el-image :src="graphicCaptchaImage" style="height: 30px"/>
          </el-button>
        </template>
      </el-input>
    </el-row>
    <el-row style="text-align: center" align="middle">
      <el-input v-model="verifyCode" placeholder="请输入验证码" clearable size="large" :prefix-icon="ChatDotRound"
                minlength="6" maxlength="6" show-word-limit>
        <template #prepend>
          <span style="width: 70px">邮箱验证码</span>
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
          <el-button v-else @click="clickSendVerifyCodeButton" style="width: 100px">
            发送验证码
          </el-button>
        </template>
      </el-input>
    </el-row>
    <template #footer>
      <el-button @click="reviseEmailDialogVisible = false; newEmail = ''">取消</el-button>
      <el-button type="primary" @click="clickConfirmReviseEmailButton">
        确认
      </el-button>
    </template>
  </el-dialog>
</template>

<style scoped>
.title-text {
  font-weight: bold;
  margin-right: 25px;
  width: 96px;
  text-align: center
}
</style>