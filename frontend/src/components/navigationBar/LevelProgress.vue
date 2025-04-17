<script setup>
import {computed} from "vue";

const levels = [
  {
    value: "黑铁",
    maxPoint: 1024,
    disableColor: "rgba(37,37,37,0.1)",
    disableFontColor: "rgba(37,37,37,0.3)",
    enableColor: "rgba(37,37,37,1)",
  },
  {
    value: "青铜",
    maxPoint: 2048,
    disableColor: "rgba(80,56,7,0.1)",
    disableFontColor: "rgba(80,56,7,0.3)",
    enableColor: "rgba(80,56,7,1)",
  },
  {
    value: "白银",
    maxPoint: 4096,
    disableColor: "rgba(142,140,138,0.1)",
    disableFontColor: "rgba(142,140,138,0.5)",
    enableColor: "rgba(142,140,138,1)",
  },
  {
    value: "黄金",
    maxPoint: 8192,
    disableColor: "rgba(221,140,32,0.1)",
    disableFontColor: "rgba(221,140,32,0.4)",
    enableColor: "rgba(221,140,32,1)",
  },
  {
    value: "铂金",
    maxPoint: 16384,
    disableColor: "rgba(47,150,170,0.1)",
    disableFontColor: "rgba(47,150,170,0.3)",
    enableColor: "rgba(47,150,170,1)",
  },
  {
    value: "钻石",
    maxPoint: 32768,
    disableColor: "rgba(191,109,218,0.1)",
    disableFontColor: "rgba(191,109,218,0.4)",
    enableColor: "rgba(191,109,218,1)",
  },
  {
    value: "超凡",
    maxPoint: 65536,
    disableColor: "rgba(33,133,78,0.1)",
    disableFontColor: "rgba(33,133,78,0.3)",
    enableColor: "rgba(33,133,78,1)",
  },
  {
    value: "神话",
    maxPoint: 131072,
    disableColor: "rgba(157,29,65,0.1)",
    disableFontColor: "rgba(157,29,65,0.3)",
    enableColor: "rgba(157,29,65,1)",
  },
  {
    value: "辐能",
    maxPoint: 999999999,
    disableColor: "rgba(242,246,6,0.1)",
    disableFontColor: "rgba(169,174,11,0.4)",
    enableColor: "rgba(169,174,11,1)",
  },
];

const {currentPoints} = defineProps({
  currentPoints: Number,
});

const currentLevelIndex = computed(() => {
  for (let i = 0; i < levels.length; i++) {
    if (currentPoints < levels[i].maxPoint) {
      return i;
    }
  }
  return levels.length - 1;
})
</script>

<template>
  <el-col :span="3" :style="'font-weight: bold; color: ' + levels[currentLevelIndex].enableColor">
    {{ levels[currentLevelIndex].value }}
  </el-col>
  <el-col :span="18">
    <el-progress
        :text-inside="true"
        :stroke-width="15"
        :percentage="currentPoints * 100 / levels[currentLevelIndex].maxPoint"
        :color="levels[currentLevelIndex].enableColor"
    >
      <span>{{ currentPoints + " / " + levels[currentLevelIndex].maxPoint }}</span>
    </el-progress>
  </el-col>
  <el-col :span="3"
          :style="'font-weight: bold; color: ' + levels[currentLevelIndex === levels.length - 1 ? currentLevelIndex : currentLevelIndex + 1].disableFontColor">
    {{ levels[currentLevelIndex === levels.length - 1 ? currentLevelIndex : currentLevelIndex + 1].value }}
  </el-col>
</template>

<style scoped>

</style>