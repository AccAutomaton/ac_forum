<script setup>
import {ref} from "vue";
import {getUidAndNicknameList} from "@/request/normal/user.js";
import {sendPerpetualNormalMessage, sendTemporaryNormalMessage} from "@/request/administrator/message.js";
import {ElNotification} from "element-plus";

const isNumeric = (value) => {
  return !isNaN(parseInt(value)) && !isNaN(value);
}

const searchType = ref(0), messageType = ref(0), content = ref(""), targetUrl = ref("");
const userListLoadingStatus = ref(false), options = ref([]), targetUid = ref();
const clearContentAfterSending = ref(false);

const searchUser = async (keyword) => {
  if (keyword.trim() === "") {
    return;
  }
  userListLoadingStatus.value = true;
  let data;
  if (searchType.value === 0) {
    data = await getUidAndNicknameList(null, keyword);
  } else if (searchType.value === 1) {
    if (isNumeric(keyword)) {
      data = await getUidAndNicknameList(keyword, "");
    } else {
      options.value.length = 0;
      userListLoadingStatus.value = false;
    }
  }
  if (data !== null) {
    options.value.length = 0;
    for (let user of data) {
      options.value.push({
        value: user["uid"],
        label: user["nickname"],
      })
    }
  }
  userListLoadingStatus.value = false;
}

const onClickSendMessageButton = async () => {
  if (targetUid.value === undefined) {
    ElNotification({type: "error", title: "请选择目标用户"});
    return;
  }
  if (content.value.length === 0) {
    ElNotification({type: "error", title: "请输入消息内容"});
    return;
  }
  if (messageType.value === 0) {
    const data = await sendPerpetualNormalMessage(targetUid.value, content.value, targetUrl.value);
    if (data !== null) {
      ElNotification({type: "success", title: "发送成功"});
      if (clearContentAfterSending.value) {
        targetUid.value = undefined;
        content.value = "";
        targetUrl.value = "";
      }
    }
  } else if (messageType.value === 1) {
    const data = await sendTemporaryNormalMessage(targetUid.value, content.value, targetUrl.value);
    if (data !== null) {
      ElNotification({type: "success", title: "发送成功"});
      if (clearContentAfterSending.value) {
        targetUid.value = undefined;
        content.value = "";
        targetUrl.value = "";
      }
    }
  }
}
</script>

<template>
  <el-row style="margin-bottom: 10px">
    目标用户
  </el-row>
  <el-row>
    <el-radio-group v-model="searchType" style="margin-bottom: 10px; margin-left: 5px">
      <el-radio :value="0">按昵称查找</el-radio>
      <el-radio :value="1">按UID查找</el-radio>
    </el-radio-group>
  </el-row>
  <el-row style="width: 300px">
    <el-select-v2 v-model="targetUid" filterable remote :remote-method="searchUser" clearable
                  :loading="userListLoadingStatus" placeholder="搜索用户..." :options="options"
                  :reserve-keyword="false" no-data-text="没有匹配的用户">
      <template #label="{ label, value }">
        <span>{{ label }}</span>
        <span style="color: var(--el-text-color-secondary); font-size: 12px; float: right">#{{ value }}</span>
      </template>
      <template #default="{ item }">
        <span>{{ item.label }}</span>
        <span style="color: var(--el-text-color-secondary); font-size: 12px; float: right">#{{
            item.value
          }}</span>
      </template>
    </el-select-v2>
  </el-row>
  <el-row style="margin: 30px 0 10px 0">
    通知内容
  </el-row>
  <el-row>
    <el-radio-group v-model="messageType" style="margin-bottom: 10px; margin-left: 5px">
      <el-radio :value="0">普通通知</el-radio>
      <el-radio :value="1">临时通知（离线无法接收）</el-radio>
    </el-radio-group>
  </el-row>
  <el-row style="width: 500px">
    <el-input v-model="content" type="textarea" :autosize="{minRows: 6}"/>
  </el-row>
  <el-row style="margin: 30px 0 10px 0">
    目标URL
  </el-row>
  <el-row style="width: 500px">
    <el-input v-model="targetUrl" placeholder="站内路径"/>
  </el-row>
  <el-row>
    <el-button type="primary" style="margin-top: 20px; width: 100px" plain @click="onClickSendMessageButton">发送
    </el-button>
  </el-row>
  <el-row style="margin-top: 10px">
    <el-text>发送后清空内容</el-text>
    <el-switch style="margin-left: 10px" v-model="clearContentAfterSending"/>
  </el-row>
</template>

<style scoped>

</style>