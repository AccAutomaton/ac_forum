<script setup>
import {Search} from "@element-plus/icons-vue";
import {ref, watch} from "vue";
import {queryArticleListOfTopic} from "@/request/normal/article.js";
import {getObjectUrlOfPublicResources} from "@/request/normal/cos.js";
import router from "@/router/index.js";
import removeMd from "remove-markdown";
import {articleQueryOptions} from "@/utils/options.js";
import {useRoute} from "vue-router";

const {topicId} = defineProps({
  topicId: String,
})

const route = useRoute();

const currentPageNumber = ref(route.query.pageNumber ? parseInt(route.query.pageNumber) : 1), currentPageSize = ref(route.query.pageSize ? parseInt(route.query.pageSize) : 5),
    currentQueryType = ref(route.query.searchType ? parseInt(route.query.searchType) : 0), currentKeyWord = ref(route.query.keywords ? route.query.keywords : "");
const pages = ref(0);
const articleList = ref([]);

const refreshRoute = () => {
  router.push({
    query: {
      keywords: currentKeyWord.value,
      searchType: currentQueryType.value,
      pageNumber: currentPageNumber.value,
      pageSize: currentPageSize.value
    }
  })
}

watch(currentPageNumber, () => {
  refreshRoute();
})

watch(currentPageSize, () => {
  refreshRoute();
})


const search = async () => {
  const data = await queryArticleListOfTopic(topicId, currentPageNumber.value - 1, currentPageSize.value, currentQueryType.value, currentKeyWord.value);
  if (data !== null) {
    articleList.value = data["articleList"]["records"];
    pages.value = data["articleList"]["pages"] > 100 ? 100 : data["articleList"]["pages"];
    currentPageNumber.value = data["articleList"]["pageNumber"] + 1;
    currentPageSize.value = data["articleList"]["pageSize"];
    for (let i = 0; i < articleList.value.length; i++) {
      if (articleList.value[i]["ownerAvatar"] !== "") {
        await getObjectUrlOfPublicResources(data["artistAvatarPrefix"] + articleList.value[i]["ownerAvatar"], (url) => {
          articleList.value[i]["ownerAvatar"] = url;
        });
      }
      if (articleList.value[i]["firstImage"] !== "") {
        await getObjectUrlOfPublicResources(data["articleImagePrefix"] + articleList.value[i]["firstImage"], (url) => {
          articleList.value[i]["firstImage"] = url;
        });
      }
    }
  }
  refreshRoute();
}
search();

const onPaginationParametersChanged = () => {
  search();
}

let ownerLock = false;
const clickArticle = (articleId) => {
  if (!ownerLock) {
    router.push(`/article/${articleId}`);
  }
}

const clickOwner = (ownerId) => {
  ownerLock = true;
  router.push(`/artist/${ownerId}/livingRoom`).then(() => {
    setTimeout(() => {
      ownerLock = false;
    }, 100)
  });
}
</script>

<template>
  <el-card shadow="never"
           style="border-radius: 25px; border-color: rgba(211,211,211,0.5); border-width: thin">
    <el-card shadow="never" style="height: 60px; border: none; text-align: center; margin-top: -10px">
      <el-input v-model="currentKeyWord" size="large" placeholder="请输入你想搜索的文章"
                style="border-radius: 25px; width: 100%;"
                clearable maxlength="32" show-word-limit autofocus @keyup.enter="search">
        <template #prepend>搜索文章</template>
        <template #append>
          <el-select v-model="currentQueryType" placeholder="请选择排序方式" style="margin-right: 20px; height: 40px"
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
    <el-scrollbar height="59vh" width="100%">
      <el-row align="middle" justify="center">
        <el-empty v-if="articleList.length === 0" description="暂无文章" :image-size="150"/>
        <el-card v-for="record in articleList" :key="record['id']" shadow="hover"
                 @click="clickArticle(record['id'])"
                 style="border-radius: 25px; border-color: rgba(211,211,211,0.5); border-width: thin; width: 100%;
                                margin: 5px 10px; cursor: pointer">
          <div style="height: 80px; margin: 1px">
            <el-row align="middle">
              <el-col :span="20">
                <el-row>
                  <el-text style="font-size: 18px; font-weight: bolder; color: black" line-clamp="1">
                    {{ record["title"] }}
                  </el-text>
                </el-row>
                <el-row>
                  <el-text line-clamp="2" style="height: 40px">
                    {{ removeMd(record["content"]) }}
                  </el-text>
                </el-row>
                <el-row :gutter="5">
                  <el-col :span="8">
                    <el-row class="owner" align="middle" justify="center" @click="clickOwner(record['owner'])"
                            style="text-align: center; border-radius: 25px; cursor: pointer; pointer-events: visible">
                      <el-col :span="4">
                        <el-avatar v-if="record['ownerAvatar'] === ''" size="small">{{
                            record["ownerNickname"].slice(0, 1)
                          }}
                        </el-avatar>
                        <el-avatar v-else :size="16" :src="record['ownerAvatar']"/>
                      </el-col>
                      <el-col :span="20" style="text-align: start;">
                        <el-text line-clamp="1">{{ record["ownerNickname"] }}</el-text>
                      </el-col>
                    </el-row>
                  </el-col>
                  <el-col :span="16">
                    <el-row>
                      <el-col :span="4">
                        <el-text>
                          <el-icon>
                            <icon-eye/>
                          </el-icon>
                          {{
                            Intl.NumberFormat('en', {
                              notation: "compact",
                              maximumFractionDigits: 1
                            }).format(record["visits"])
                          }}
                        </el-text>
                      </el-col>
                      <el-col :span="1"/>
                      <el-col :span="4">
                        <el-text>
                          <el-icon>
                            <icon-thumb-up/>
                          </el-icon>
                          {{
                            Intl.NumberFormat('en', {
                              notation: "compact",
                              maximumFractionDigits: 1
                            }).format(record["thumbsUp"])
                          }}
                        </el-text>
                      </el-col>
                      <el-col :span="1"/>
                      <el-col :span="4">
                        <el-text>
                          <el-icon>
                            <icon-trophy/>
                          </el-icon>
                          {{
                            Intl.NumberFormat('en', {
                              notation: "compact",
                              maximumFractionDigits: 1
                            }).format(record["tipping"])
                          }}
                        </el-text>
                      </el-col>
                      <el-col :span="1"/>
                      <el-col :span="4">
                        <el-text>
                          <el-icon>
                            <icon-star/>
                          </el-icon>
                          {{
                            Intl.NumberFormat('en', {
                              notation: "compact",
                              maximumFractionDigits: 1
                            }).format(record["collections"])
                          }}
                        </el-text>
                      </el-col>
                      <el-col :span="1"/>
                      <el-col :span="4">
                        <el-text>
                          <el-icon>
                            <icon-share-internal/>
                          </el-icon>
                          {{
                            Intl.NumberFormat('en', {
                              notation: "compact",
                              maximumFractionDigits: 1
                            }).format(record["forwards"])
                          }}
                        </el-text>
                      </el-col>
                    </el-row>
                  </el-col>
                </el-row>
              </el-col>
              <el-col v-if="record['firstImage'] !== ''" :span="4" style="text-align: center">
                <el-image :src="record['firstImage']" loading="lazy" fit="cover" style="height: 75px; width: 100%; border-radius: 10px"/>
              </el-col>
              <el-col v-else :span="4" style="text-align: center; font-size: 14px; color: #a19b9b">
                无图片
              </el-col>
            </el-row>
          </div>
        </el-card>
      </el-row>
    </el-scrollbar>
    <div style="width: 100%; display: flex; align-items: center; justify-content: center; margin-top: 10px">
      <el-pagination
          v-model:current-page="currentPageNumber"
          v-model:page-size="currentPageSize"
          :page-sizes="[5, 10, 15, 20]"
          size=large
          layout="prev, pager, next, ->, sizes"
          :page-count="pages"
          @change="onPaginationParametersChanged"
      />
    </div>
  </el-card>
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

.owner:hover {
  background-color: rgba(161, 155, 155, 0.1);
}
</style>