<script setup>

import {cos} from "@/request/cos.js";
import {ElNotification} from "element-plus";
import store from "@/store/index.js";
import {ref} from "vue";
import {getAvatar} from "@/request/user.js";

const avatar = ref("");

const refreshAvatarUrl = async (needRefreshGlobalAvatar = false) => {
  const data = await getAvatar();
  if (data !== null) {
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
</script>

<template>
  <el-container>
    <el-header>
      <h2 style="margin-top: 10px">账户信息</h2>
    </el-header>
    <el-divider style="margin: 0"/>
    <el-main>
      <el-scrollbar>
        <el-row>
          <el-image
              style="width: 100px; height: 100px"
              :src="avatar"
              :zoom-rate="1.2"
              :max-scale="7"
              :min-scale="0.2"
              :preview-src-list="[avatar]"
              fit="cover"
          />
        </el-row>
      </el-scrollbar>
    </el-main>
  </el-container>
</template>

<style scoped>

</style>