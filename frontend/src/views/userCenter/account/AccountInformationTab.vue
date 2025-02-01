<script setup>

import {cos} from "@/request/cos.js";
import {ElNotification} from "element-plus";
import store from "@/store/index.js";
import {ref} from "vue";
import {getAvatarGetAuthorization, getAvatarUpdateAuthorization, setAvatarCustomization} from "@/request/user.js";
import {Upload} from "@element-plus/icons-vue";

const avatar = ref(""), isDefaultAvatar = ref(false);

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
    <el-main>
      <el-scrollbar>
        <el-row style="align-items: center;">
          <el-text size="large">头像</el-text>
          <el-image
              style="width: 75px; height: 75px; margin-left: 25px; margin-right: 25px;
               border-style: dotted; border-color: lightgray; border-radius: 10px;"
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
      </el-scrollbar>
    </el-main>
  </el-container>
</template>

<style scoped>

</style>