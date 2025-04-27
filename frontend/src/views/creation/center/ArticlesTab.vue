<script setup>
import {ref} from "vue";
import {deleteArticleById, getOwnArticleList} from "@/request/article.js";
import router from "@/router/index.js";
import {Close, Edit, View} from "@element-plus/icons-vue";
import {ElMessageBox, ElNotification} from "element-plus";

const currentPageNumber = ref(1), currentPageSize = ref(20), pages = ref(0);
const articleList = ref([]);

const refreshArticleList = async () => {
  const data = await getOwnArticleList(currentPageNumber.value - 1, currentPageSize.value);
  if (data !== null) {
    articleList.value = data["articleList"]["records"];
    pages.value = data["articleList"]["pages"];
    currentPageNumber.value = data["articleList"]["pageNumber"] + 1;
    currentPageSize.value = data["articleList"]["pageSize"];
  }
}
refreshArticleList();

const onClickDeleteArticleButton = (articleId, articleTitle) => {
  ElMessageBox.confirm(
      `真的要删除文章: ${articleTitle} 吗？`,
      '警告：删除文章',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning',
      }
  ).then(async () => {
    const data = await deleteArticleById(articleId);
    if (data !== null) {
      ElNotification({title: "删除成功", type: "success"});
      for (let index = 0; index < articleList.value.length; index++) {
        if (articleList.value[index].id === articleId) {
          articleList.value.splice(index, 1);
          break;
        }
      }
    }
  }).catch(() => {
  })
}
</script>

<template>
  <el-table :data="articleList" style="width: 100%" stripe height="75vh">
    <el-table-column prop="id" label="文章ID" width="100" align="center" show-overflow-tooltip/>
    <el-table-column prop="title" label="标题" align="center" show-overflow-tooltip/>
    <el-table-column prop="topicTitle" label="话题" width="150" align="center" show-overflow-tooltip/>
    <el-table-column prop="visits" label="浏览" width="75" align="center" show-overflow-tooltip/>
    <el-table-column prop="thumbsUp" label="点赞" width="75" align="center" show-overflow-tooltip/>
    <el-table-column prop="collections" label="收藏" width="75" align="center" show-overflow-tooltip/>
    <el-table-column prop="tipping" label="投币" width="75" align="center" show-overflow-tooltip/>
    <el-table-column prop="forwards" label="转发" width="75" align="center" show-overflow-tooltip/>
    <el-table-column prop="createTime" label="创建时间" width="200" align="center" show-overflow-tooltip/>
    <el-table-column prop="createTime" label="更新时间" width="200" align="center" show-overflow-tooltip/>
    <el-table-column label="操作" fixed="right" width="115" align="center" show-overflow-tooltip>
      <template #default="scope">
        <el-row align="middle" justify="center">
          <el-tooltip content="查看文章" placement="top" effect="light">
            <el-icon size="16" class="view-icon-button" @click="router.push(`/article/${scope.row['id']}`)">
              <View/>
            </el-icon>
          </el-tooltip>
          <el-tooltip content="编辑文章" placement="top" effect="light">
            <el-icon style="margin: 0 10px" size="16" class="view-icon-button"
                     @click="router.push(`/creation/edit/${scope.row['id']}`)">
              <Edit/>
            </el-icon>
          </el-tooltip>
          <el-tooltip content="删除文章" placement="top" effect="light">
            <el-icon size="16" class="view-icon-dangerous-button"
                     @click="onClickDeleteArticleButton(scope.row['id'], scope.row['title'])">
              <Close/>
            </el-icon>
          </el-tooltip>
        </el-row>
      </template>
    </el-table-column>
  </el-table>
  <div style="width: 100%; display: flex; align-items: center; justify-content: center; margin-top: 15px">
    <el-pagination
        v-model:current-page="currentPageNumber"
        v-model:page-size="currentPageSize"
        :page-sizes="[10, 20]"
        size=large
        layout="prev, pager, next, ->, sizes"
        :page-count="pages"
        @change="refreshArticleList"
    />
  </div>
</template>

<style scoped>
.view-icon-button:hover {
  cursor: pointer;
  color: #409eff;
}

.view-icon-dangerous-button:hover {
  cursor: pointer;
  color: red;
}
</style>