<script setup>
import {ref} from "vue";
import {articleQueryOptions} from "@/utils/options.js";
import {getArtistArticleListByUid} from "@/request/normal/artist.js";
import {useRoute} from "vue-router";
import {getObjectUrlOfPublicResources} from "@/request/normal/cos.js";
import removeMd from "remove-markdown";
import moment from "moment";
import router from "@/router/index.js";

const currentQueryType = ref(2);
const currentPageNumber = ref(1), currentPageSize = ref(10), pages = ref(0);
const articleList = ref([]);

const artistId = useRoute().params.artistId;

const getArticleList = async () => {
  const data = await getArtistArticleListByUid(artistId, currentPageNumber.value - 1, currentPageSize.value, currentQueryType.value);
  if (data !== null) {
    articleList.value = data["articleList"]["records"];
    pages.value = data["articleList"]["pages"];
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
      if (articleList.value[i]["topicAvatar"] !== "") {
        await getObjectUrlOfPublicResources(data["topicAvatarPrefix"] + articleList.value[i]["topicAvatar"], (url) => {
          articleList.value[i]["topicAvatar"] = url;
        });
      }
    }
  }
}
getArticleList();

const clickArticle = (articleId) => {
  router.push(`/article/${articleId}`);
}

const clickTopic = (topicId) => {
  router.push(`/topic/${topicId}`);
}
</script>

<template>
  <template v-if="articleList.length === 0">
    <el-empty description="无文章" :image-size="150"/>
  </template>
  <template v-else>
    <el-row style="margin: 10px 0 10px 10px">
      <el-select v-model="currentQueryType" style="width: 125px" @change="getArticleList">
        <el-option v-for="option in articleQueryOptions" :key="option.value" :label="option.label"
                   :value="option.value"/>
      </el-select>
    </el-row>
    <el-scrollbar height="52.5vh" width="100%">
      <el-row :gutter="15" style="width: 100%">
        <el-col :span="12" v-for="article in articleList" :key="article['id']">
          <el-card shadow="hover" @click="clickArticle(article['id'])"
                   style="border-radius: 10px; border-color: rgba(211,211,211,0.5); border-width: thin; width: 100%;
                                margin: 5px 10px; cursor: pointer">
            <el-row align="middle" style="padding: 10px 15px">
              <el-col :span="19">
                <el-row align="middle">
                  <el-col :span="16">
                    <el-row>
                      <el-text style="font-size: 18px; font-weight: bolder; color: black" line-clamp="1">
                        {{ article["title"] }}
                      </el-text>
                    </el-row>
                    <el-row>
                      <el-text line-clamp="2" style="height: 40px">
                        {{ removeMd(article["content"]) }}
                      </el-text>
                    </el-row>
                    <el-row style="text-align: center">
                      <el-row class="owner" @click.stop="clickTopic(article['topic'])"
                              style="border-radius: 25px; cursor: pointer; pointer-events: visible; padding: 0 5px; margin-left: -7px">
                        <el-avatar v-if="article['topicAvatar'] === ''" shape="square" size="small">
                          {{
                            article["topicTitle"].slice(0, 1)
                          }}
                        </el-avatar>
                        <el-avatar v-else :size="25" :src="article['topicAvatar']" shape="square"
                                   style="background-color: transparent"/>
                        <el-text line-clamp="1" style="margin-left: 5px; max-width: 90%; text-align: start">
                          {{ article["topicTitle"] }}
                        </el-text>
                      </el-row>
                    </el-row>
                  </el-col>
                  <el-col :span="4" style="padding-left: 20px">
                    <el-row>
                      <el-text>
                        <el-icon>
                          <icon-eye/>
                        </el-icon>
                        {{
                          Intl.NumberFormat('en', {
                            notation: "compact",
                            maximumFractionDigits: 1
                          }).format(article["visits"])
                        }}
                      </el-text>
                    </el-row>
                    <el-row>
                      <el-text>
                        <el-icon>
                          <icon-thumb-up/>
                        </el-icon>
                        {{
                          Intl.NumberFormat('en', {
                            notation: "compact",
                            maximumFractionDigits: 1
                          }).format(article["thumbsUp"])
                        }}
                      </el-text>
                    </el-row>
                    <el-row>
                      <el-text>
                        <el-icon>
                          <icon-trophy/>
                        </el-icon>
                        {{
                          Intl.NumberFormat('en', {
                            notation: "compact",
                            maximumFractionDigits: 1
                          }).format(article["tipping"])
                        }}
                      </el-text>
                    </el-row>
                    <el-row>
                      <el-text>
                        <el-icon>
                          <icon-star/>
                        </el-icon>
                        {{
                          Intl.NumberFormat('en', {
                            notation: "compact",
                            maximumFractionDigits: 1
                          }).format(article["collections"])
                        }}
                      </el-text>
                    </el-row>
                    <el-col>
                      <el-text>
                        <el-icon>
                          <icon-share-internal/>
                        </el-icon>
                        {{
                          Intl.NumberFormat('en', {
                            notation: "compact",
                            maximumFractionDigits: 1
                          }).format(article["forwards"])
                        }}
                      </el-text>
                    </el-col>
                  </el-col>
                  <el-col style="font-size: 14px" :span="4">
                    <el-row style="color: #a19b9b">发布时间</el-row>
                    <el-row>{{ moment(article["createTime"], 'YYYY-MM-DD HH:mm:ss').fromNow() }}</el-row>
                  </el-col>
                </el-row>
              </el-col>
              <el-col v-if="article['firstImage'] !== ''" :span="5" style="text-align: center">
                <el-image :src="article['firstImage']" loading="lazy" fit="cover"
                          style="height: 90px; width: 100%; border-radius: 10px"/>
              </el-col>
              <el-col v-else :span="5" style="text-align: center; font-size: 14px; color: #a19b9b">
                无图片
              </el-col>
            </el-row>
          </el-card>
        </el-col>
      </el-row>
    </el-scrollbar>
    <div
        style="width: 100%; display: flex; align-items: center; justify-content: center; margin-top: 5px; margin-bottom: 5px">
      <el-pagination
          v-model:current-page="currentPageNumber"
          v-model:page-size="currentPageSize"
          :page-sizes="[10, 20]"
          size=large
          layout="prev, pager, next, ->, sizes"
          :page-count="pages"
          @change="getArticleList"
      />
    </div>
  </template>
</template>

<style scoped>
:deep() .el-card__body {
  padding: 0;
}

:deep() .el-select__wrapper {
  border-radius: 10px;
}

.owner:hover {
  background-color: rgba(161, 155, 155, 0.1);
}
</style>