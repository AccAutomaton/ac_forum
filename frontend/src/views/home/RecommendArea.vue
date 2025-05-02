<script setup>
import {ref} from "vue";
import {queryArticleList} from "@/request/article.js";
import router from "@/router/index.js";
import {queryTopicList} from "@/request/topic.js";

const articlesRankingList = ref([]);
const refreshArticlesRankingList = async () => {
  const data = await queryArticleList(0, 10, 4, "");
  if (data !== null) {
    articlesRankingList.value = data["articleList"]["records"];
  }
}
refreshArticlesRankingList();

const topicsrankingList = ref([]);
const refreshTopicsRankingList = async () => {
  const data = await queryTopicList(0, 10, 4, "");
  if (data !== null) {
    topicsrankingList.value = data["topicList"]["records"];
  }
}
refreshTopicsRankingList();
</script>

<template>
  <el-row style="width: 100%; margin-top: 100px; padding: 0 100px" justify="center" align="middle" :gutter="50">
    <el-col :span="12">
      <el-card class="card">
        <el-row style="width: 100%; color: rgba(139,0,0,0.6); font-weight: bold" align="middle" justify="center">
          话题热度榜
        </el-row>
        <el-scrollbar height="310">
          <el-row style="font-size: 14px; width: 100%; margin: 15px 0" v-for="(topic, index) in topicsrankingList"
                  :key="topic['id']" :gutter="20"
                  align="middle" justify="center">
            <el-col :span="4"
                    style="color: rgba(186,2,2,0.75); font-style: italic; font-weight: bold; text-align: center; width: 100%">
              {{ index + 1 }}
            </el-col>
            <el-col :span="14">
              <el-link @click="router.push(`/topic/${topic['id']}`)">
                <el-text line-clamp="1">
                  {{ topic["title"] }}
                </el-text>
              </el-link>
            </el-col>
            <el-col :span="6" style="font-size: 12px">
              <el-row align="middle">
                <el-icon style="margin-right: 5px">
                  <icon-eye/>
                </el-icon>
                {{
                  Intl.NumberFormat('en', {
                    notation: "compact",
                    maximumFractionDigits: 1
                  }).format(topic["visits"])
                }}
              </el-row>
            </el-col>
          </el-row>
        </el-scrollbar>
      </el-card>
    </el-col>
    <el-col :span="12">
      <el-card class="card">
        <el-row style="width: 100%; color: rgba(139,0,0,0.6); font-weight: bold" align="middle" justify="center">
          文章热度榜
        </el-row>
        <el-scrollbar height="310">
          <el-row style="font-size: 14px; width: 100%; margin: 15px 0" v-for="(article, index) in articlesRankingList"
                  :key="article['id']" :gutter="20"
                  align="middle" justify="center">
            <el-col :span="4"
                    style="color: rgba(186,2,2,0.75); font-style: italic; font-weight: bold; text-align: center; width: 100%">
              {{ index + 1 }}
            </el-col>
            <el-col :span="14">
              <el-link @click="router.push(`/article/${article['id']}`)">
                <el-text line-clamp="1">
                  {{ article["title"] }}
                </el-text>
              </el-link>
            </el-col>
            <el-col :span="6" style="font-size: 12px">
              <el-row align="middle">
                <el-icon style="margin-right: 5px">
                  <icon-eye/>
                </el-icon>
                {{
                  Intl.NumberFormat('en', {
                    notation: "compact",
                    maximumFractionDigits: 1
                  }).format(article["visits"])
                }}
              </el-row>
            </el-col>
          </el-row>
        </el-scrollbar>
      </el-card>
    </el-col>
  </el-row>
</template>

<style scoped>
:deep() .el-card__body {
  padding: 10px;
}

.card {
  height: 350px;
  border-radius: 25px;
}
</style>