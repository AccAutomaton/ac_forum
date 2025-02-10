<script setup>
import {Plus, Search} from "@element-plus/icons-vue";
import {ref} from "vue";
import TopicVisitsRankingList from "@/views/topic/TopicVisitsRankingList.vue";
import TopicSearchResultList from "@/views/topic/TopicSearchResultList.vue";

const searchInput = ref("");
const selectSearchType = ref("synthesize");
const selectOptions = [
  {value: "synthesize", label: "综合排序"},
  {value: "visitsByDesc", label: "热度由高到低"},
  {value: "visitsByAsc", label: "热度由低到高"},
  {value: "createTimeByDesc", label: "时间从近到远"},
  {value: "createTimeByAsc", label: "时间从远到近"},
  {value: "articlesByDesc", label: "帖子从多到少"},
  {value: "articlesByAsc", label: "帖子从少到多"},
]
</script>

<template>
  <el-container>
    <el-aside width="325px">
      <el-card shadow="never" style="height: 40px; border: none; text-align: center">
        <el-button :icon="Plus" plain type="info"
                   style="font-size: 16px; font-weight: bolder; padding-left: 30px; padding-right: 30px; height: 40px; width: 70%; border-radius: 25px">
          <span>发起新话题</span>
        </el-button>
      </el-card>
      <el-card shadow="never"
               style="border-radius: 25px; border-color: rgba(211,211,211,0.5); border-width: thin; margin-top: 30px">
        <TopicVisitsRankingList/>
      </el-card>
    </el-aside>
    <el-main style="padding-top: 0; padding-bottom: 0;">
      <el-card shadow="never" style="height: 40px; border: none; text-align: center">
        <el-input v-model="searchInput" size="large" placeholder="请输入你想搜索的话题"
                  style="border-radius: 25px; width: 75%">
          <template #prepend>搜索话题</template>
          <template #append>
            <el-select v-model="selectSearchType" placeholder="请选择排序方式" style="margin-right: 20px; height: 40px" size="large">
              <el-option v-for="option in selectOptions" :key="option.value" :label="option.label" :value="option.value"/>
            </el-select>
            <el-button style="padding: 0 25px;">
              <el-icon size="20">
                <Search/>
              </el-icon>
            </el-button>
          </template>
        </el-input>
      </el-card>
      <TopicSearchResultList/>
    </el-main>
  </el-container>
</template>

<style scoped>
:deep() .el-card__body {
  padding: 0;
}

:deep(.el-input-group__prepend) {
  height: 40px;
  font-size: 16px;
  color: black;
  padding-left: 25px;
  border-top-left-radius: 25px;
  border-bottom-left-radius: 25px;
}

:deep(.el-input-group__append) {
  width: 200px;
  height: 40px;
  font-size: 16px;
  color: dodgerblue;
  padding-right: 25px;
  border-top-right-radius: 25px;
  border-bottom-right-radius: 25px;
}
</style>