<script setup>
import {ref} from "vue";
import router from "@/router/index.js";
import {getOwnArticlesVisitsTopX} from "@/request/normal/article.js";

const chartOption = ref({});
const articleTitleList = ref([]), articleVisitsList = ref([]), articleIdList = ref([]);

const init = async () => {
  const data = await getOwnArticlesVisitsTopX(10);
  if (data !== null) {
    articleTitleList.value.length = 0;
    articleVisitsList.value.length = 0;
    articleIdList.value.length = 0;
    for (const article of data) {
      if (article["title"].length > 5) {
        articleTitleList.value.push((article["title"].substring(0, 5) + "..."));
      } else {
        articleTitleList.value.push(article["title"]);
      }
      articleVisitsList.value.push(article["visits"]);
      articleIdList.value.push(article["id"]);
    }
    chartOption.value = {
      tooltip: {},
      xAxis: {
        data: articleTitleList.value,
      },
      yAxis: {},
      series: [
        {
          name: "浏览量",
          type: "bar",
          data: articleVisitsList.value,
          itemStyle: {
            borderRadius: 3,
            color: 'rgba(64,158,255,0.75)',
          },
          label: {
            show: true,
            position: "top",
          }
        },
      ],
    }
  }
}
init();

const onClick = (params) => {
  router.push(`/article/${articleIdList.value[params.dataIndex]}`);
}
</script>

<template>
  <el-card style="width: 100%; border-radius: 10px; height: 65vh">
    <el-row>
      <el-text style="margin-right: 10px; font-weight: bolder; color: darkred; font-size: 18px">Top 10</el-text>
      <el-text style="font-weight: bolder">文章浏览量</el-text>
    </el-row>
    <el-row>
      <vue-echarts :option="chartOption" autoresize style="height: 60vh" @click="onClick"/>
    </el-row>
  </el-card>
</template>

<style scoped>

</style>