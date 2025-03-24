<script setup>
import {queryArticleList} from "@/request/article.js";
import {ref} from "vue";
import {getObjectUrlOfPublicResources} from "@/request/cos.js";
import moment from "moment";
import router from "@/router/index.js";
import removeMd from "remove-markdown";

const currentQueryType = ref(0), currentKeyWord = ref("");
const currentPageNumber = ref(1), currentPageSize = ref(5), pages = ref(0);
const articleList = ref([]);

const refreshTopicList = async () => {
  const data = await queryArticleList(currentPageNumber.value - 1, currentPageSize.value, currentQueryType.value, currentKeyWord.value);
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
      if (articleList.value[i]["topicAvatar"] !== "") {
        await getObjectUrlOfPublicResources(data["topicAvatarPrefix"] + articleList.value[i]["topicAvatar"], (url) => {
          articleList.value[i]["topicAvatar"] = url;
        });
      }
    }
  }
}
refreshTopicList();

const search = (queryType, keyword) => {
  currentQueryType.value = queryType;
  currentKeyWord.value = keyword;
  currentPageNumber.value = 1;
  refreshTopicList();
}

defineExpose({
  search,
});

let ownerLock = false;
const clickArticle = (articleId) => {
  if (!ownerLock) {
    router.push(`/article/${articleId}`);
  }
}

const clickTopic = (topicId) => {
  ownerLock = true;
  router.push(`/topic/${topicId}`).then(() => {
    setTimeout(() => {
      ownerLock = false;
    }, 100)
  });
}

const clickOwner = (ownerId) => {
  ownerLock = true;
  router.push(`/artist/livingRoom/${ownerId}`).then(() => {
    setTimeout(() => {
      ownerLock = false;
    }, 100)
  });
}
</script>

<template>
  <el-scrollbar height="72vh" width="100%">
    <el-row align="middle" justify="center" style="margin-top: 5px;">
      <el-empty v-if="articleList.length === 0" description="暂无文章" :image-size="150"/>
      <el-card v-for="record in articleList" :key="record['id']" shadow="hover"
               @click="clickArticle(record['id'])"
               style="border-radius: 25px; border-color: rgba(211,211,211,0.5); border-width: thin; width: 100%;
                                margin: 5px 10px; cursor: pointer">
        <el-row align="middle" style="padding: 10px 15px">
          <el-col :span="20">
            <el-row align="middle">
              <el-col :span="18">
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
                    <el-row class="owner" align="middle" justify="center" @click="clickTopic(record['topic'])"
                            style="text-align: center; border-radius: 25px; cursor: pointer; pointer-events: visible">
                      <el-col :span="4">
                        <el-avatar v-if="record['topicAvatar'] === ''" shape="square" size="small">
                          {{
                            record["topicTitle"].slice(0, 1)
                          }}
                        </el-avatar>
                        <el-avatar v-else :size="25" :src="record['topicAvatar']" shape="square" style="background-color: transparent"/>
                      </el-col>
                      <el-col :span="20" style="text-align: start;">
                        <el-text line-clamp="1">{{ record["topicTitle"] }}</el-text>
                      </el-col>
                    </el-row>
                  </el-col>
                  <el-col :span="8">
                    <el-row class="owner" align="middle" justify="center" @click="clickOwner(record['owner'])"
                            style="text-align: center; border-radius: 25px; cursor: pointer; pointer-events: visible">
                      <el-col :span="4">
                        <el-avatar v-if="record['ownerAvatar'] === ''" size="small">{{
                            record["ownerNickname"].slice(0, 1)
                          }}
                        </el-avatar>
                        <el-avatar v-else :size="25" :src="record['ownerAvatar']" style="background-color: transparent"/>
                      </el-col>
                      <el-col :span="20" style="text-align: start;">
                        <el-text line-clamp="1">{{ record["ownerNickname"] }}</el-text>
                      </el-col>
                    </el-row>
                  </el-col>
                </el-row>
              </el-col>
              <el-col :span="3" style="padding-left: 20px">
                <el-row>
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
                      }).format(record["thumbsUp"])
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
                      }).format(record["tipping"])
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
                      }).format(record["collections"])
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
                      }).format(record["forwards"])
                    }}
                  </el-text>
                </el-col>
              </el-col>
              <el-col style="font-size: 14px" :span="3">
                <el-row style="color: #a19b9b">发布时间</el-row>
                <el-row>{{ moment(record["createTime"], 'YYYY-MM-DD HH:mm:ss').fromNow() }}</el-row>
              </el-col>
            </el-row>
          </el-col>
          <el-col v-if="record['firstImage'] !== ''" :span="4" style="text-align: center">
            <el-image :src="record['firstImage']" loading="lazy" fit="contain" style="height: 100px"/>
          </el-col>
          <el-col v-else :span="4" style="text-align: center; font-size: 14px; color: #a19b9b">
            无图片
          </el-col>
        </el-row>
      </el-card>
    </el-row>
  </el-scrollbar>
  <div
      style="width: 100%; display: flex; align-items: center; justify-content: center; margin-top: 5px; margin-bottom: 5px">
    <el-pagination
        v-model:current-page="currentPageNumber"
        v-model:page-size="currentPageSize"
        :page-sizes="[5, 10, 15, 20]"
        size=large
        layout="prev, pager, next, ->, sizes"
        :page-count="pages"
        @change="refreshTopicList"
    />
  </div>
</template>

<style scoped>
:deep() .el-card__body {
  padding: 0;
}

.owner:hover {
  background-color: rgba(161, 155, 155, 0.1);
}
</style>