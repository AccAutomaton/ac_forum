<script setup>
import {ref} from "vue";
import {synchronizeEsArticleData, synchronizeEsTopicData} from "@/request/root/elasticsearch.js";
import {ElNotification} from "element-plus";

const articleEsLoading = ref(false), topicEsLoading = ref(false);

const onClickSynchronizeArticleEsButton = async () => {
  articleEsLoading.value = true;
  const data = await synchronizeEsArticleData();
  if (data !== null) {
    ElNotification({type: 'success', title: '同步请求发送成功'});
    setTimeout(() => articleEsLoading.value = false, 5000);
  } else {
    articleEsLoading.value = false
  }
}

const onClickSynchronizeTopicEsButton = async () => {
  topicEsLoading.value = true;
  const data = await synchronizeEsTopicData();
  if (data !== null) {
    ElNotification({type: 'success', title: '同步请求发送成功'});
    setTimeout(() => topicEsLoading.value = false, 5000);
  } else {
    topicEsLoading.value = false
  }
}
</script>

<template>
  <el-row>
    <el-button size="large" style="width: 150px" plain :loading="articleEsLoading" @click="onClickSynchronizeArticleEsButton">文章 ES 同步</el-button>
  </el-row>
  <el-row style="margin-top: 20px">
    <el-button size="large" style="width: 150px;" plain :loading="topicEsLoading" @click="onClickSynchronizeTopicEsButton">话题 ES 同步</el-button>
  </el-row>
</template>

<style scoped>

</style>