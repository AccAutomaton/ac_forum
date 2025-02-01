<script setup>

import {cos} from "@/request/cos.js";
import {ElNotification} from "element-plus";
import store from "@/store/index.js";
import {ref} from "vue";
import {
  getAvatarGetAuthorization,
  getAvatarUpdateAuthorization,
  getUserDetails,
  setAvatarCustomization
} from "@/request/user.js";
import {Edit, Upload} from "@element-plus/icons-vue";

const avatar = ref(""), isDefaultAvatar = ref(false);
const uid = ref(""), username = ref(""), email = ref("");
const status = ref({}), userType = ref({});
const nickname = ref(""), createTime = ref(""), updateTime = ref("");

const refreshAvatarUrl = async (needRefreshGlobalAvatar = false) => {
  const data = await getAvatarGetAuthorization();
  if (data !== null) {
    if (data["avatar"]["key"].includes("default-avatar")) {
      isDefaultAvatar.value = true;
    }
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
            avatar.value = data["Url"];
            if (needRefreshGlobalAvatar) {
              store.commit("setAvatar", data["Url"]);
            }
          }
        }
    )
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
      cos(data["targetAvatar"]).uploadFile(
          {
            Bucket: data["targetAvatar"]["bucket"],
            Region: data["targetAvatar"]["region"],
            Key: data["targetAvatar"]["key"],
            Body: file,
          },
          async (err) => {
            if (err !== null) {
              ElNotification({title: "服务器错误", type: "error", message: "存储服务发生错误"});
            } else {
              if (isDefaultAvatar.value) {
                const data = await setAvatarCustomization();
                if (data["result"] !== "success") {
                  ElNotification({title: "服务器错误", type: "error", message: "请稍后再试"})
                } else {
                  await refreshAvatarUrl(true);
                  ElNotification({title: "修改成功", type: "success", message: "新头像将稍后生效"});
                }
              } else {
                await refreshAvatarUrl(true);
                ElNotification({title: "修改成功", type: "success", message: "新头像将稍后生效"});
              }
            }
          }
      );
    }
  }).catch(() => {
    ElNotification({title: "操作取消", type: "info"});
  });
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
          <el-button style="margin-left: 25px" :icon="Edit" circle plain/>
        </el-row>
        <el-divider :border-style="'dotted'"/>
        <el-row>
          <el-text size="large" class="title-text">邮箱</el-text>
          <el-text>{{ email }}</el-text>
          <el-button style="margin-left: 25px" :icon="Edit" circle plain/>
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
</template>

<style scoped>
.title-text {
  font-weight: bold;
  margin-right: 25px;
  width: 96px;
  text-align: center
}
</style>