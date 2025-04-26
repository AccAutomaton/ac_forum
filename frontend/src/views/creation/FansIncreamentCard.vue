<script setup>
import {ref} from "vue";
import {getFansIncreamentNearly7Days} from "@/request/follow.js";

const chartOption = ref({});
const dateList = ref([]), countList = ref([]);

const init = async () => {
  const data = await getFansIncreamentNearly7Days();
  if (data !== null) {
    dateList.value.length = 0;
    countList.value.length = 0;
    for (const article of data) {
      dateList.value.push(article["date"]);
      countList.value.push(article["count"]);
    }
    chartOption.value = {
      tooltip: {},
      xAxis: {
        data: dateList.value,
      },
      yAxis: {},
      series: [
        {
          name: "新增粉丝量",
          type: "line",
          data: countList.value,
          itemStyle: {
            barBorderRadius: 3,
            color: 'rgba(64,158,255,0.75)',
          },
          label: {
            show: true,
            position: "top",
          },
          smooth: true,
        },
      ],
    }
  }
}
init();
</script>

<template>
  <el-card style="width: 100%; border-radius: 10px; height: 65vh">
    <el-row>
      <el-text style="margin-right: 10px; font-weight: bolder; color: darkred; font-size: 18px">近 7 天</el-text>
      <el-text style="font-weight: bolder">新增粉丝量</el-text>
    </el-row>
    <el-row>
      <vue-echarts :option="chartOption" autoresize style="height: 60vh"/>
    </el-row>
  </el-card>
</template>

<style scoped>

</style>