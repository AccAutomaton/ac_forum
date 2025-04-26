<script setup>
import {ref} from "vue";
import {getOwnTopicsVisitsTopX} from "@/request/topic.js";
import router from "@/router/index.js";

const chartOption = ref({});
const topicTitleList = ref([]), topicVisitsList = ref([]), topicIdList = ref([]);

const init = async () => {
  const data = await getOwnTopicsVisitsTopX(10);
  if (data !== null) {
    topicTitleList.value.length = 0;
    topicVisitsList.value.length = 0;
    topicIdList.value.length = 0;
    for (const topic of data) {
      if (topic["title"].length > 5) {
        topicTitleList.value.push((topic["title"].substring(0, 5) + "..."));
      } else {
        topicTitleList.value.push(topic["title"]);
      }
      topicVisitsList.value.push(topic["visits"]);
      topicIdList.value.push(topic["id"]);
    }
    chartOption.value = {
      tooltip: {},
      xAxis: {
        data: topicTitleList.value,
      },
      yAxis: {},
      series: [
        {
          name: "浏览量",
          type: "bar",
          data: topicVisitsList.value,
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
  router.push(`/topic/${topicIdList.value[params.dataIndex]}`);
}
</script>

<template>
  <el-card style="width: 100%; border-radius: 10px; height: 65vh">
    <el-row>
      <el-text style="margin-right: 10px; font-weight: bolder; color: darkred; font-size: 18px">Top 10</el-text>
      <el-text style="font-weight: bolder">话题浏览量</el-text>
    </el-row>
    <el-row>
      <vue-echarts :option="chartOption" autoresize style="height: 60vh" @click="onClick"/>
    </el-row>
  </el-card>
</template>

<style scoped>

</style>