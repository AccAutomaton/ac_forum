<script setup>
import {ref} from "vue";
import {MdEditor} from 'md-editor-v3';
import {SyncGetObjectUrlOfPublicResources, uploadObject} from "@/request/cos.js";
import {ElMessage, ElMessageBox, ElNotification} from "element-plus";
import {queryTopicIdAndTitleList} from "@/request/topic.js";
import {
  getArticleById,
  getArticleImageUploadAuthorization,
  updateArticleById
} from "@/request/article.js";
import router from "@/router/index.js";
import {useStorage} from "@vueuse/core";
import {useRoute} from "vue-router";

const toolbars = [
  'bold',
  'underline',
  'italic',
  'strikeThrough',
  '-',
  'title',
  'sub',
  'sup',
  'quote',
  'unorderedList',
  'orderedList',
  'task',
  '-',
  'codeRow',
  'code',
  'link',
  'table',
  'mermaid',
  'katex',
  '-',
  'revoke',
  'next',
  'save',
  '=',
  'prettier',
  'pageFullscreen',
  'fullscreen',
  'preview',
  'previewOnly',
  'catalog',
];

const articleId = useRoute().params.articleId;
const articleCache = useStorage("editArticleCache_" + articleId, {
  title: "",
  topicId: undefined,
  topicTitle: "",
  content: "",
});
const title = ref(""), content = ref("");
const topicId = ref(0);
const options = ref([]), showDefaultTopic = ref(false);
const topicListLoadingStatus = ref(true);

const init = async () => {
  const data = await getArticleById(articleId);
  if (data !== null) {
    if (articleCache.value.content === "" || articleCache.value.content === data["content"]) {
      articleCache.value.title = data["title"];
      articleCache.value.topicId = data["topic"];
      articleCache.value.topicTitle = data["topicTitle"];
      articleCache.value.content = data["content"];
    } else {
      ElMessageBox.confirm(
          '本地已存储修改该文章的草稿',
          '请选择要保留的内容',
          {
            confirmButtonText: '使用本地数据',
            cancelButtonText: '使用云端数据覆盖',
            type: 'warning',
          }
      ).then(() => {
        ElMessage("使用本地数据");
      }).catch(() => {
        articleCache.value.title = data["title"];
        articleCache.value.topicId = data["topic"];
        articleCache.value.topicTitle = data["topicTitle"];
        articleCache.value.content = data["content"];
        ElMessage("使用云端数据覆盖");
      });
    }
    title.value = articleCache.value.title;
    topicId.value = articleCache.value.topicId;
    content.value = articleCache.value.content;
  }
}
init();

const searchTopics = async (keyword) => {
  topicListLoadingStatus.value = true;
  const data = await queryTopicIdAndTitleList(keyword);
  if (data !== null) {
    options.value.length = 0;
    for (let topic of data) {
      options.value.push({
        value: topic["id"],
        label: topic["title"],
      })
    }
  }
  topicListLoadingStatus.value = false;
}

const imgCache = new Map();
const sanitize = (html) => {
  const regex = /<img[^>]*src="([^"]*)"[^>]*>/gi;
  return html.replace(regex, (match, src) => {
    if (src === "") {
      return match;
    }
    if (src.includes("http")) {
      ElNotification({title: "不允许使用站外图片", type: "warning"});
      return match.replace(src, "");
    }
    if (imgCache.has(src)) {
      return match.replace(src, imgCache.get(src));
    }
    const key = "article/image/" + src;
    const newSrc = SyncGetObjectUrlOfPublicResources(key);
    imgCache.set(src, newSrc);
    return match.replace(src, newSrc);
  });
};

const onUploadImg = async (files, callback) => {
  const res = await Promise.all(
      files.map((file) => {
        return new Promise(async (rev) => {
          const data = await getArticleImageUploadAuthorization();
          if (data !== null) {
            const filename = data["key"].substring(data["key"].toString().lastIndexOf("/") + 1);
            uploadObject(data, file, () => {
              rev(filename);
            });
          }
        });
      })
  );
  callback(res.map((item) => item));
};

const onClickEditArticleButton = async () => {
  if (title.value === "") {
    ElNotification({title: "标题不能为空", type: "warning"});
    return;
  }
  if (content.value === "") {
    ElNotification({title: "内容不能为空", type: "warning"});
    return;
  }
  if (content.value > 100000) {
    ElNotification({title: "内容不能超过 100,000 个字", type: "warning"});
    return;
  }
  if (topicId.value === undefined) {
    ElNotification({title: "请选择话题", type: "warning"});
    return;
  }
  const data = await updateArticleById(articleId, topicId.value, title.value, content.value);
  if (data !== null) {
    articleCache.value = null;
    ElNotification({title: "发布成功", type: "success"});
    router.push("/article/" + articleId).then(() => {
    });
  }
}

const save = (content) => {
  articleCache.value.title = title.value;
  articleCache.value.topicId = topicId.value;
  for (let i = 0; i < options.value.length; i++) {
    if (options.value[i].value === topicId.value) {
      articleCache.value.topicTitle = options.value[i].label;
      break;
    }
  }
  articleCache.value.content = content;
};

const onSave = (content) => {
  save(content);
  ElNotification({title: "保存成功", type: "success", message: "已保存到本地"});
}

const onBlur = () => {
  save(content.value);
  ElMessage("已自动保存到本地");
}
</script>

<template>
  <el-container>
    <el-main style="padding: 0 10px">
      <el-row style="margin-bottom: 10px" :gutter="20">
        <el-col :span="16">
          <el-input v-model="title" style="font-size: 16px; font-weight: bolder; color: black"
                    placeholder="请输入文章标题" maxlength="32" show-word-limit @blur="onBlur">
            <template #prepend>
              <span style="font-size: 14px; font-weight: lighter">文章标题</span>
            </template>
          </el-input>
        </el-col>
        <el-col :span="5">
          <el-select-v2 v-model="topicId" filterable remote :remote-method="searchTopics" clearable
                        :loading="topicListLoadingStatus" placeholder="话题" :options="options"
                        :reserve-keyword="false" @change="showDefaultTopic = true" @blur="onBlur">
            <template #label="{ label, value }">
              <span v-if="showDefaultTopic">{{ label }}</span>
              <span v-else>{{ articleCache.topicTitle }}</span>
              <span style="color: var(--el-text-color-secondary); font-size: 12px; float: right">#{{ value }}</span>
            </template>
            <template #default="{ item }">
              <span>{{ item.label }}</span>
              <span style="color: var(--el-text-color-secondary); font-size: 12px; float: right">#{{
                  item.value
                }}</span>
            </template>
          </el-select-v2>
        </el-col>
        <el-col :span="3">
          <el-button style="width: 100%" type="primary" plain @click="onClickEditArticleButton">发布文章</el-button>
        </el-col>
      </el-row>
      <el-row>
        <MdEditor v-model="content" previewTheme="github" codeTheme="github" :codeFoldable="false" :toolbars="toolbars"
                  :tableShape="[8, 8]" placeholder="在此输入正文..." showToolbarName style="height: 79.5vh"
                  :sanitize="sanitize" @onUploadImg="onUploadImg" @onSave="onSave" @blur="onBlur"/>
      </el-row>
    </el-main>
  </el-container>
</template>

<style scoped>

</style>