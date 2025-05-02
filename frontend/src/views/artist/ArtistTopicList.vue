<script setup>
import {ref} from "vue";
import {useRoute} from "vue-router";
import {getArtistTopicListByUid} from "@/request/normal/artist.js";
import {getObjectUrlOfPublicResources} from "@/request/normal/cos.js";
import {topicQueryOptions} from "@/utils/options.js";
import router from "@/router/index.js";

const currentQueryType = ref(2);
const currentPageNumber = ref(1), currentPageSize = ref(12), pages = ref(0);
const topicList = ref([]);

const artistId = useRoute().params.artistId;

const getTopicList = async () => {
  const data = await getArtistTopicListByUid(artistId, currentPageNumber.value - 1, currentPageSize.value, currentQueryType.value);
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
getTopicList();
</script>

<template>
  <template v-if="topicList.length === 0">
    <el-empty description="无话题" :image-size="150"/>
  </template>
  <template v-else>
    <el-row style="margin: 10px 0 10px 10px">
      <el-select v-model="currentQueryType" style="width: 125px" @change="getTopicList">
        <el-option v-for="option in topicQueryOptions" :key="option.value" :label="option.label" :value="option.value"/>
      </el-select>
    </el-row>
    <el-scrollbar height="52.5vh" width="100%">
      <el-row :gutter="15" style="width: 100%; padding: 0 10px">
        <el-col :span="8" v-for="topic in topicList" :key="topic['id']">
          <el-card shadow="hover" @click="router.push('/topic/' + topic['id'])"
                   style="border-radius: 10px; cursor: pointer; padding: 15px 10px; margin-bottom: 15px">
            <el-row align="middle" justify="center" style="width: 100%; height: 100%" :gutter="5">
              <el-col :span="5" style="text-align: center">
                <el-avatar v-if="topic['avatar'] === ''" shape="square" size="large">
                  <span style="font-size: large">{{ topic["title"].slice(0, 2) }}</span>
                </el-avatar>
                <el-avatar v-else shape="square" size="large" :src="topic['avatar']"
                           style="background-color: transparent;"/>
              </el-col>
              <el-col :span="14">
                <el-row>
                  <el-text style="color: black" size="large" tag="b" line-clamp="1">{{ topic["title"] }}</el-text>
                </el-row>
                <el-row style="margin-top: 5px">
                  <el-text line-clamp="2">{{ topic["description"] }}</el-text>
                </el-row>
              </el-col>
              <el-col :span="5">
                <div>
                  <el-text type="info" size="small" line-clamp="1">
                    {{
                      Intl.NumberFormat('en', {
                        notation: "compact",
                        maximumFractionDigits: 1
                      }).format(topic["articles"])
                    }} 篇文章
                  </el-text>
                </div>
                <div>
                  <el-text type="info" size="small" line-clamp="1">
                    {{
                      Intl.NumberFormat('en', {
                        notation: "compact",
                        maximumFractionDigits: 1
                      }).format(topic["visits"])
                    }} 次浏览
                  </el-text>
                </div>
              </el-col>
            </el-row>
          </el-card>
        </el-col>
      </el-row>
    </el-scrollbar>
    <div
        style="width: 100%; display: flex; align-items: center; justify-content: center; margin-top: 5px; margin-bottom: 5px">
      <el-pagination
          v-model:current-page="currentPageNumber"
          v-model:page-size="currentPageSize"
          :page-sizes="[6, 12, 18]"
          size=large
          layout="prev, pager, next, ->, sizes"
          :page-count="pages"
          @change="getTopicList"
      />
    </div>
  </template>
</template>

<style scoped>
:deep() .el-card__body {
  padding: 0;
}

:deep() .el-select__wrapper {
  border-radius: 10px;
}
</style>