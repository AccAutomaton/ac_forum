<script setup>
import {ref} from "vue";
import {getDashboardData} from "@/request/artist.js";
import {ChatLineSquare, Coin, Document, User} from "@element-plus/icons-vue";
import TopicsTop10Card from "@/views/creation/center/dashboard/TopicsTop10Card.vue";
import ArticlesTop10Card from "@/views/creation/center/dashboard/ArticlesTop10Card.vue";
import FansIncreamentCard from "@/views/creation/center/dashboard/FansIncreamentCard.vue";
import TippingsIncreamentCard from "@/views/creation/center/dashboard/TippingsIncreamentCard.vue";
import {useRoute} from "vue-router";
import router from "@/router/index.js";

const route = useRoute();

const activateTab = ref(route.query.tab === undefined ? "topics" : route.query.tab);
if (!["articles", "topics", "tippings", "fans"].includes(activateTab.value)) {
  router.push("/404");
}
const handleSelect = (value) => {
  activateTab.value = value;
  router.push({query: {tab: value}});
}

const dashboardData = ref({});
const init = async () => {
  const data = await getDashboardData();
  if (data !== null) {
    dashboardData.value = data;
  }
}
init();
</script>

<template>
  <el-row style="width: 100%; margin-left: 10px" :gutter="20">
    <el-col :span="6">
      <el-card class="statistic-card" :style="{borderColor: 'topics' === activateTab ? 'rgba(64,158,255,0.75)' : ''}"
               @click="handleSelect('topics')">
        <el-row align="middle" justify="center" style="height: 100%">
          <el-col>
            <el-statistic :value="dashboardData['topics']" style="text-align: center"
                          :value-style="{'font-size': '24px', 'font-weight': 'bolder'}">
              <template #title>
                <div style="display: inline-flex; align-items: center; font-size: 16px">
                  话题数
                </div>
              </template>
              <template #prefix>
                <el-icon size="medium" style="margin-left: 5px">
                  <ChatLineSquare/>
                </el-icon>
              </template>
            </el-statistic>
          </el-col>
        </el-row>
      </el-card>
    </el-col>
    <el-col :span="6">
      <el-card class="statistic-card" :style="{borderColor: 'articles' === activateTab ? 'rgba(64,158,255,0.75)' : ''}"
               @click="handleSelect('articles')">
        <el-row align="middle" justify="center" style="height: 100%">
          <el-col>
            <el-statistic :value="dashboardData['articles']" style="text-align: center"
                          :value-style="{'font-size': '24px', 'font-weight': 'bolder'}">
              <template #title>
                <div style="display: inline-flex; align-items: center; font-size: 16px">
                  文章数
                </div>
              </template>
              <template #prefix>
                <el-icon size="medium" style="margin-left: 5px">
                  <Document/>
                </el-icon>
              </template>
            </el-statistic>
          </el-col>
        </el-row>
      </el-card>
    </el-col>
    <el-col :span="6">
      <el-card class="statistic-card" :style="{borderColor: 'fans' === activateTab ? 'rgba(64,158,255,0.75)' : ''}"
               @click="handleSelect('fans')">
        <el-row align="middle" justify="center" style="height: 100%">
          <el-col>
            <el-statistic :value="dashboardData['fans']" style="text-align: center"
                          :value-style="{'font-size': '24px', 'font-weight': 'bolder'}">
              <template #title>
                <div style="display: inline-flex; align-items: center; font-size: 16px">
                  粉丝数
                </div>
              </template>
              <template #prefix>
                <el-icon size="medium" style="margin-left: 5px">
                  <User/>
                </el-icon>
              </template>
            </el-statistic>
          </el-col>
        </el-row>
      </el-card>
    </el-col>
    <el-col :span="6">
      <el-card class="statistic-card" :style="{borderColor: 'tippings' === activateTab ? 'rgba(64,158,255,0.75)' : ''}"
               @click="handleSelect('tippings')">
        <el-row align="middle" justify="center" style="height: 100%">
          <el-col>
            <el-statistic :value="dashboardData['tippings']" style="text-align: center"
                          :value-style="{'font-size': '24px', 'font-weight': 'bolder'}">
              <template #title>
                <div style="display: inline-flex; align-items: center; font-size: 16px">
                  文章收益
                </div>
              </template>
              <template #prefix>
                <el-icon size="medium" style="margin-left: 5px">
                  <Coin/>
                </el-icon>
              </template>
            </el-statistic>
          </el-col>
        </el-row>
      </el-card>
    </el-col>
  </el-row>
  <el-row style="width: 100%; padding-left: 20px; margin-top: 40px">
    <TopicsTop10Card v-if="activateTab === 'topics'"/>
    <ArticlesTop10Card v-else-if="activateTab === 'articles'"/>
    <FansIncreamentCard v-else-if="activateTab === 'fans'"/>
    <TippingsIncreamentCard v-else-if="activateTab === 'tippings'"/>
  </el-row>
</template>

<style scoped>
.statistic-card {
  border-radius: 10px;
  cursor: pointer;
}
</style>