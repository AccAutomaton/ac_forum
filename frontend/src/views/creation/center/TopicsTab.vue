<script setup>
import {ref} from "vue";
import {getObjectUrlOfPublicResources} from "@/request/cos.js";
import {getOwnTopicList} from "@/request/topic.js";
import {View} from "@element-plus/icons-vue";
import router from "@/router/index.js";

const topicList = ref([]);
const currentPageNumber = ref(1), currentPageSize = ref(20);
const pages = ref(0);
const refreshTopicList = async () => {
  const data = await getOwnTopicList(currentPageNumber.value - 1, currentPageSize.value, 2);
  if (data !== null) {
    topicList.value = data["topicList"]["records"];
    pages.value = data["topicList"]["pages"];
    currentPageNumber.value = data["topicList"]["pageNumber"] + 1;
    currentPageSize.value = data["topicList"]["pageSize"];
    for (let i = 0; i < topicList.value.length; i++) {
      if (topicList.value[i]["avatar"] !== "") {
        await getObjectUrlOfPublicResources(data["avatarPrefix"] + topicList.value[i]["avatar"], (url) => {
          topicList.value[i]["avatar"] = url;
        });
      }
      if (topicList.value[i]["description"] === "") {
        topicList.value[i]["description"] = "该话题没有简介";
      }
    }
  }
}
refreshTopicList();
</script>

<template>
  <el-table :data="topicList" style="width: 100%" stripe height="75vh">
    <el-table-column prop="id" label="话题ID" width="100" align="center" show-overflow-tooltip/>
    <el-table-column label="话题头像" width="100" align="center">
      <template #default="scope">
        <el-row align="middle" justify="center">
          <el-avatar v-if="scope.row['avatar'] === ''" shape="square" size="small">
            <span>{{ scope.row['title'].slice(0, 1) }}</span>
          </el-avatar>
          <el-avatar v-else shape="square" :src="scope.row['avatar']" size="small"
                     style="background-color: transparent"/>
        </el-row>
      </template>
    </el-table-column>
    <el-table-column prop="title" label="话题名" width="150" align="center" show-overflow-tooltip/>
    <el-table-column prop="description" label="话题简介" align="center" show-overflow-tooltip/>
    <el-table-column prop="articles" label="文章数" width="100" align="center" show-overflow-tooltip/>
    <el-table-column prop="visits" label="浏览量" width="100" align="center" show-overflow-tooltip/>
    <el-table-column prop="createTime" label="创建时间" width="200" align="center" show-overflow-tooltip/>
    <el-table-column label="操作" fixed="right" width="115" align="center" show-overflow-tooltip>
      <template #default="scope">
        <el-tooltip content="查看话题" placement="top" effect="light">
          <el-icon size="16" class="view-icon-button" @click="router.push(`/topic/${scope.row['id']}`)">
            <View/>
          </el-icon>
        </el-tooltip>
      </template>
    </el-table-column>
  </el-table>
  <div style="width: 100%; display: flex; align-items: center; justify-content: center; margin-top: 15px">
      <el-pagination
          v-model:current-page="currentPageNumber"
          v-model:page-size="currentPageSize"
          :page-sizes="[10, 20]"
          size=large
          layout="prev, pager, next, ->, sizes"
          :page-count="pages"
          @change="refreshTopicList"
      />
    </div>
</template>

<style scoped>
.view-icon-button:hover {
  cursor: pointer;
  color: #409eff;
}
</style>