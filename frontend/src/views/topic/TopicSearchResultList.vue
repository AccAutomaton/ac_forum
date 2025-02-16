<script setup>
import {ref} from "vue";
import {queryTopicList} from "@/request/topic.js";
import {getObjectUrlOfPublicResources} from "@/request/cos.js";
import router from "@/router/index.js";

const getCardCss = (index) => {
  const css = {
    "height": "125px",
    "border-radius": "25px",
    "cursor": "pointer",
  }
  if (index % 3 === 0) {
    css["margin"] = "10px 10px 0 10px"
  } else {
    css["margin"] = "10px 10px 0 0px"
  }
  return css;
}

const topicList = ref([]);
const currentPageNumber = ref(1), currentPageSize = ref(12), currentQueryType = ref("synthesize"), currentKeyWord = ref("");
const pages = ref(0);
const refreshTopicList = async () => {
  const data = await queryTopicList(currentPageNumber.value, currentPageSize.value, currentQueryType.value, currentKeyWord.value);
  if (data !== null) {
    topicList.value = data["topicList"]["records"];
    pages.value = data["topicList"]["pages"];
    currentPageNumber.value = data["topicList"]["pageNumber"];
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
refreshTopicList("");

const onPaginationParametersChanged = () => {
  refreshTopicList();
}

const search = (keyword, queryType) => {
  currentKeyWord.value = keyword;
  currentQueryType.value = queryType;
  currentPageNumber.value = 1;
  refreshTopicList();
}

defineExpose({
  search,
})
</script>

<template>
  <el-card shadow="never"
           style="border-radius: 25px; border-color: rgba(211,211,211,0.5); border-width: thin; margin-top: 30px">
    <el-scrollbar height="66vh">
      <div style="width: 100%; padding-bottom: 10px; display: grid; grid-template-columns: 33.3% 33.3% 33.3%;">
        <el-card v-for="(record, index) in topicList" :key="record['id']" shadow="hover" :style="getCardCss(index)"
                 @click="router.push('/topic/' + record['id'])">
          <div style="height: 100px; margin: 10px; display: flex">
            <el-row align="middle" justify="center" style="width: 100%" :gutter="5">
              <el-col :span="5" style="text-align: center">
                <el-avatar v-if="record['avatar'] === ''" shape="square" size="large">
                  <span style="font-size: large">{{ record["title"].slice(0, 2) }}</span>
                </el-avatar>
                <el-avatar v-else shape="square" size="large" :src="record['avatar']"
                           style="background-color: transparent;"/>
              </el-col>
              <el-col :span="19">
                <el-row>
                  <div>
                    <el-text size="large" tag="b" line-clamp="1">{{ record["title"] }}</el-text>
                  </div>
                </el-row>
                <el-row align="middle" justify="center">
                  <el-col :span="16">
                    <div>
                      <el-text line-clamp="4">{{ record["description"] }}</el-text>
                    </div>
                  </el-col>
                  <el-col :span="8" style="text-align: center">
                    <div>
                      <el-text type="info" size="small" line-clamp="1">{{ record["articles"] }} 篇文章</el-text>
                    </div>
                    <div>
                      <el-text type="info" size="small" line-clamp="1">{{ record["visits"] }} 次浏览</el-text>
                    </div>
                  </el-col>
                </el-row>
              </el-col>
            </el-row>
          </div>
        </el-card>
      </div>
    </el-scrollbar>
  </el-card>
  <div style="width: 100%; display: flex; align-items: center; justify-content: center; margin-top: 10px">
    <el-pagination
        v-model:current-page="currentPageNumber"
        v-model:page-size="currentPageSize"
        :page-sizes="[6, 12, 18]"
        size=large
        layout="prev, pager, next, ->, sizes"
        :page-count="pages"
        @change="onPaginationParametersChanged"
    />
  </div>
</template>

<style scoped>
:deep() .el-card__body {
  padding: 0;
}
</style>