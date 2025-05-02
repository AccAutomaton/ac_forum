<script setup>
import {computed, ref} from "vue";
import {useRoute} from "vue-router";
import {getArtistThumbsUpListByUid} from "@/request/normal/artist.js";
import {getObjectUrlOfPublicResources} from "@/request/normal/cos.js";
import router from "@/router/index.js";
import removeMd from "remove-markdown";
import moment from "moment";
import {Warning} from "@element-plus/icons-vue";
import {unThumbsUpArticle} from "@/request/normal/article.js";
import store from "@/store/index.js";

const currentPageNumber = ref(1), currentPageSize = ref(10), pages = ref(0);
const articleList = ref([]);

const artistId = useRoute().params.artistId;
const isSelfLivingRoom = computed(() => artistId === store.getters.getUid.toString());

const getArticleList = async () => {
  const data = await getArtistThumbsUpListByUid(artistId, currentPageNumber.value, currentPageSize.value);
  if (data !== null) {
    articleList.value = data["articleList"]["records"];
    pages.value = data["articleList"]["pages"];
    currentPageNumber.value = data["articleList"]["pageNumber"];
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

const emit = defineEmits(["decreaseThumbsUp"]);

const onClickUnThumbsUpButton = async (index, articleId) => {
  const data = await unThumbsUpArticle(articleId);
  if (data !== null) {
    articleList.value.splice(index, 1);
    emit("decreaseThumbsUp");
  }
}
</script>

<template>
  <template v-if="articleList.length === 0">
    <el-empty description="无点赞内容" :image-size="150"/>
  </template>
  <template v-else>
    <el-scrollbar height="58.5vh" width="100%">
      <el-row :gutter="15" style="width: 100%; margin-top: 10px">
        <el-col :span="12" v-for="(article, index) in articleList" :key="article['id']">
          <el-card shadow="hover" @click="router.push(`/article/${article['id']}`)"
                   style="border-radius: 10px; border-color: rgba(211,211,211,0.5); border-width: thin; width: 100%; cursor: pointer; margin-bottom: 10px">
            <el-row align="middle" style="padding: 10px 15px">
              <el-col :span="isSelfLivingRoom ? 17 : 19">
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
                      <el-col :span="12">
                        <el-row class="owner" @click.stop="router.push(`/topic/${article['topic']}`)"
                                style="border-radius: 25px; cursor: pointer; pointer-events: visible; padding: 0 5px; margin-left: -7px">
                          <el-avatar v-if="article['topicAvatar'] === ''" shape="square" size="small">
                            {{
                              article["topicTitle"].slice(0, 1)
                            }}
                          </el-avatar>
                          <el-avatar v-else :size="25" :src="article['topicAvatar']" shape="square"
                                     style="background-color: transparent"/>
                          <el-text line-clamp="1" style="margin-left: 5px; max-width: 80%">{{
                              article["topicTitle"]
                            }}
                          </el-text>
                        </el-row>
                      </el-col>
                      <el-col :span="12">
                        <el-row class="owner" @click.stop="router.push(`/artist/${article['owner']}/livingRoom`)"
                                style="border-radius: 25px; cursor: pointer; pointer-events: visible; padding: 0 5px; margin-left: -7px">
                          <el-avatar v-if="article['ownerAvatar'] === ''" size="small">
                            {{
                              article["ownerNickname"].slice(0, 1)
                            }}
                          </el-avatar>
                          <el-avatar v-else :size="25" :src="article['ownerAvatar']"
                                     style="background-color: transparent"/>
                          <el-text line-clamp="1" style="margin-left: 5px; max-width: 80%">{{
                              article["ownerNickname"]
                            }}
                          </el-text>
                        </el-row>
                      </el-col>
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
              <el-col v-if="isSelfLivingRoom" :span="2">
                <el-row align="middle" justify="center" style="margin-right: -15px">
                  <el-popconfirm confirm-button-text="确认" cancel-button-text="取消"
                                 :icon="Warning"
                                 icon-color="red"
                                 title="取消点赞该文章？"
                                 offset="20"
                                 @confirm="onClickUnThumbsUpButton(index, article['id'])"
                                 :hide-after="0"
                                 width="200px"
                                 style="pointer-events: none;"
                  >
                    <template #reference>
                      <el-button style="border-radius: 10px" plain @click.stop>
                        <el-icon :size="16" color="#409eff">
                          <icon-thumb-up-fill/>
                        </el-icon>
                      </el-button>
                    </template>
                  </el-popconfirm>
                </el-row>
              </el-col>
            </el-row>
          </el-card>
        </el-col>
      </el-row>
    </el-scrollbar>
    <div
        style="width: 100%; display: flex; align-items: center; justify-content: center; margin-top: 5px; margin-bottom: 5px">
      <el-pagination
          @click.stop
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