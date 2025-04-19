<script setup>
import {Plus, Search} from "@element-plus/icons-vue";
import {ref} from "vue";
import ArticleVisitsRankingList from "@/views/article/ArticleVisitsRankingList.vue";
import ArticleSearchResultList from "@/views/article/ArticleSearchResultList.vue";
import router from "@/router/index.js";
import {articleQueryOptions} from "@/utils/options.js";

const searchInput = ref("");
const selectSearchType = ref(0);

const onClickCreateArticleButton = () => {
  router.push("/creation/create")
}

const articleSearchResultList = ref();
const search = () => {
  articleSearchResultList.value.search(selectSearchType.value, searchInput.value);
}
</script>

<template>
  <el-container>
    <el-aside width="325px">
      <el-card shadow="never" style="height: 40px; border: none; text-align: center">
        <el-button :icon="Plus"
                   style="font-size: 16px; font-weight: bolder; padding-left: 30px; padding-right: 30px; height: 40px; width: 70%; border-radius: 25px; color: black"
                   @click="onClickCreateArticleButton">
          <span>写文章</span>
        </el-button>
      </el-card>
      <el-card shadow="never"
               style="border-radius: 25px; border-color: rgba(211,211,211,0.5); border-width: thin; margin-top: 20px">
        <ArticleVisitsRankingList/>
      </el-card>
    </el-aside>
    <el-main style="padding-top: 0; padding-bottom: 0;">
      <el-card shadow="never" style="height: 40px; border: none; text-align: center">
        <el-input v-model="searchInput" size="large" placeholder="请输入你想搜索的文章"
                  style="border-radius: 25px; width: 75%"
                  clearable maxlength="32" show-word-limit autofocus @keyup.enter="search">
          <template #prepend>搜索文章</template>
          <template #append>
            <el-select v-model="selectSearchType" placeholder="请选择排序方式" style="margin-right: 20px; height: 40px"
                       size="large" @change="search">
              <el-option v-for="option in articleQueryOptions" :key="option.value" :label="option.label"
                         :value="option.value"/>
            </el-select>
            <el-button style="padding: 0 25px;" @click="search">
              <el-icon size="20">
                <Search/>
              </el-icon>
            </el-button>
          </template>
        </el-input>
      </el-card>
      <el-card shadow="never"
               style="border-radius: 25px; border-color: rgba(211,211,211,0.5); border-width: thin; margin-top: 20px">
        <ArticleSearchResultList ref="articleSearchResultList"/>
      </el-card>
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