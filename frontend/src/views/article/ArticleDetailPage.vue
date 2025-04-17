<script setup>
import {MdPreview} from "md-editor-v3";
import {useRoute} from "vue-router";
import {
  collectArticle, forwardArticle,
  getArticleById,
  thumbsUpArticle,
  unCollectArticle,
  unThumbsUpArticle
} from "@/request/article.js";
import {ref} from "vue";
import moment from "moment";
import {getObjectUrlOfPublicResources, SyncGetObjectUrlOfPublicResources} from "@/request/cos.js";
import {Coin, EditPen, Warning} from "@element-plus/icons-vue";
import store from "@/store/index.js";
import router from "@/router/index.js";
import {follow, unfollow} from "@/request/follow.js";
import TippingDialog from "@/views/article/TippingDialog.vue";
import CommentDrawer from "@/views/article/CommentDrawer.vue";
import removeMd from "remove-markdown";

const articleId = useRoute().params.articleId;

const title = ref(""), content = ref("");
const createTime = ref(""), updateTime = ref("");
const owner = ref(""), ownerNickname = ref(""), ownerAvatar = ref(""), ownerFans = ref(0), ownerArticles = ref(0);
const topic = ref(""), topicTitle = ref(""), topicAvatar = ref(""), topicArticles = ref(0), topicVisits = ref(0);
const visits = ref(0), thumbsUp = ref(0), collections = ref(0), tipping = ref(0), forwards = ref(0), comments = ref(0);
const alreadyFollowOwner = ref(false), alreadyThumbsUp = ref(false), alreadyCollected = ref(false);

const getArticle = async () => {
  const data = await getArticleById(articleId);
  if (data !== null) {
    title.value = data["title"];
    {
      const regex = /!\[.*?]\((.*?)\)/g;
      content.value = data["content"].replace(regex, (match, src) => {
        if (src === "") {
          return match;
        }
        const key = "article/image/" + src;
        const newSrc = SyncGetObjectUrlOfPublicResources(key);
        return match.replace(src, newSrc);
      });
    }
    createTime.value = data["createTime"];
    updateTime.value = data["updateTime"];

    owner.value = data["owner"];
    ownerNickname.value = data["ownerNickname"];
    {
      if (data["ownerAvatar"] !== "") {
        await getObjectUrlOfPublicResources(data["ownerAvatarPrefix"] + data["ownerAvatar"], (url) => {
          ownerAvatar.value = url;
        });
      }
    }
    ownerFans.value = data["ownerFans"];
    ownerArticles.value = data["ownerArticles"];
    alreadyFollowOwner.value = data["alreadyFollowOwner"];

    topic.value = data["topic"];
    topicTitle.value = data["topicTitle"];
    {
      if (data["topicAvatar"] !== "") {
        await getObjectUrlOfPublicResources(data["topicAvatarPrefix"] + data["topicAvatar"], (url) => {
          topicAvatar.value = url;
        });
      }
    }
    topicArticles.value = data["topicArticles"];
    topicVisits.value = data["topicVisits"];

    visits.value = data["visits"];
    thumbsUp.value = data["thumbsUp"];
    collections.value = data["collections"];
    tipping.value = data["tipping"];
    forwards.value = data["forwards"];
    comments.value = data["comments"];
    alreadyThumbsUp.value = data["alreadyThumbsUp"];
    alreadyCollected.value = data["alreadyCollected"];
  }
}
getArticle();

const onClickFollowButton = async () => {
  const data = await follow(owner.value);
  if (data !== null) {
    alreadyFollowOwner.value = true;
  }
}

const onClickUnfollowButton = async () => {
  const data = await unfollow(owner.value);
  if (data !== null) {
    alreadyFollowOwner.value = false;
  }
}

const onClickThumbsUpButton = async () => {
  const data = await thumbsUpArticle(articleId);
  if (data !== null) {
    alreadyThumbsUp.value = true;
    thumbsUp.value++;
  }
}

const onClickUnThumbsUpButton = async () => {
  const data = await unThumbsUpArticle(articleId);
  if (data !== null) {
    alreadyThumbsUp.value = false;
    thumbsUp.value--;
  }
}

const onClickCollectButton = async () => {
  const data = await collectArticle(articleId);
  if (data !== null) {
    alreadyCollected.value = true;
    collections.value++;
  }
}

const onClickUnCollectButton = async () => {
  const data = await unCollectArticle(articleId);
  if (data !== null) {
    alreadyCollected.value = false;
    collections.value--;
  }
}

const onClickForwardButton = () => {
  navigator.share({
    title: `文章 - ${title.value} - AC论坛`,
    text: removeMd(content.value),
    url: window.location.href,
  }).then(async () => {
    const data = await forwardArticle(articleId);
    if (data !== null) {
      forwards.value++;
    }
  })
}

const tippingDialogRef = ref(), commentDrawerRef = ref();
const increaseTipping = (volume) => {
  tipping.value += volume;
}
const increaseComments = () => {
  comments.value++;
}
const decreaseComments = () => {
  comments.value--;
}
</script>

<template>
  <TippingDialog ref="tippingDialogRef" @onTippingSuccess="increaseTipping" :articleId="articleId"/>
  <CommentDrawer ref="commentDrawerRef" @onAddingComment="increaseComments" @onDeletingComment="decreaseComments"
                 :articleId="articleId" :comments="comments" :articleOwner="owner"/>
  <el-container>
    <el-main style="padding: 0">
      <el-card style="border-style: none; padding-left: 30px">
        <div>
          <el-text style="font-size: 32px; font-weight: bolder; color: black">
            {{ title }}
          </el-text>
        </div>
        <el-divider style="margin: 10px 0"/>
        <el-scrollbar height="77.5vh">
          <MdPreview style="padding-right: 10px" v-model="content" previewTheme="github" codeTheme="github"
                     :codeFoldable="false"/>
        </el-scrollbar>
      </el-card>
    </el-main>
    <el-aside width="25%" style="margin-left: 20px">
      <el-scrollbar height="85.5vh">
        <el-card style="padding: 10px 5px; font-size: 14px; border-radius: 15px" shadow="never">
          <el-row el-row align="middle" justify="center" style="text-align: center; height: 60px" :gutter="20">
            <el-col :span="6">
              <el-avatar style="background-color: transparent; float: right" :size="50" :src="ownerAvatar"/>
            </el-col>
            <el-col :span="10">
              <el-row>
                <el-link style="font-weight: bolder; font-size: 18px; margin-bottom: 5px"
                         @click="router.push(`/artist/${owner}/livingRoom`)">{{ ownerNickname }}
                </el-link>
              </el-row>
              <el-row style="text-align: left">
                <el-col :span="12">
                  <el-text style="font-weight: bolder">
                    {{
                      Intl.NumberFormat('en', {
                        notation: "compact",
                        maximumFractionDigits: 1
                      }).format(ownerArticles)
                    }}
                  </el-text>
                  <el-text style="color: #878484">
                    文章
                  </el-text>
                </el-col>
                <el-col :span="12">
                  <el-text style="font-weight: bolder">
                    {{
                      Intl.NumberFormat('en', {
                        notation: "compact",
                        maximumFractionDigits: 1
                      }).format(ownerFans)
                    }}
                  </el-text>
                  <el-text style="color: #878484">
                    粉丝
                  </el-text>
                </el-col>
              </el-row>
            </el-col>
            <el-col :span="8">
              <span v-if="store.getters.getUid === owner">
                <el-button type="primary" round plain
                           @click="router.push(`/artist/${owner}/livingRoom`)">创作中心</el-button>
              </span>
              <span v-else>
                <el-popconfirm v-if="alreadyFollowOwner" confirm-button-text="确认" cancel-button-text="取消"
                               :icon="Warning"
                               icon-color="red"
                               title="真的要取消关注TA吗？"
                               @confirm="onClickUnfollowButton"
                               :hide-after="0"
                               width="200px"
                >
                  <template #reference>
                    <el-button type="info" round plain>已关注</el-button>
                  </template>
                </el-popconfirm>
                <el-button v-else icon="plus" type="primary" round @click="onClickFollowButton">关注</el-button>
              </span>
            </el-col>
          </el-row>
        </el-card>
        <el-card style="margin-top: 20px; padding: 10px 5px; font-size: 14px; border-radius: 15px" shadow="never">
          <el-row el-row align="middle" justify="center" style="text-align: center; height: 60px" :gutter="20">
            <el-col :span="6">
              <el-avatar v-if="topicAvatar === ''" style="float: right; font-size: 16px" :size="50"
                         :src="topicAvatar" shape="square">
                {{ topicTitle.slice(0, 2) }}
              </el-avatar>
              <el-avatar v-else style="background-color: transparent; float: right" :size="50" :src="topicAvatar" shape="square"/>
            </el-col>
            <el-col :span="10">
              <el-row>
                <el-link style="font-weight: bolder; font-size: 18px; margin-bottom: 5px"
                         @click="router.push('/topic/' + topic)">
                  {{ topicTitle }}
                </el-link>
              </el-row>
              <el-row style="text-align: left">
                <el-col :span="12">
                  <el-text style="font-weight: bolder">
                    {{
                      Intl.NumberFormat('en', {
                        notation: "compact",
                        maximumFractionDigits: 1
                      }).format(topicArticles)
                    }}
                  </el-text>
                  <el-text style="color: #878484">
                    文章
                  </el-text>
                </el-col>
                <el-col :span="12">
                  <el-text style="font-weight: bolder">
                    {{
                      Intl.NumberFormat('en', {
                        notation: "compact",
                        maximumFractionDigits: 1
                      }).format(topicVisits)
                    }}
                  </el-text>
                  <el-text style="color: #878484">
                    浏览
                  </el-text>
                </el-col>
              </el-row>
            </el-col>
            <el-col :span="8">
              <el-button type="info" round plain @click="router.push('/topic/' + topic)">
                进入话题
              </el-button>
            </el-col>
          </el-row>
        </el-card>
        <el-card style="padding: 10px 5px; font-size: 14px; border-radius: 15px; margin-top: 20px" shadow="never">
          <el-row align="middle" justify="center" style="text-align: center; height: 25px" :gutter="20">
            <el-col :span="12" style="color: grey">浏览量</el-col>
            <el-col :span="12">
              {{
                Intl.NumberFormat('en', {
                  notation: "compact",
                  maximumFractionDigits: 1
                }).format(visits)
              }}
            </el-col>
          </el-row>
          <el-row align="middle" justify="center" style="text-align: center; height: 25px; margin-top: 10px"
                  :gutter="20">
            <el-col :span="12" style="color: grey">发布时间</el-col>
            <el-col :span="12">{{ moment(createTime, 'YYYY-MM-DD HH:mm:ss').fromNow() }}</el-col>
          </el-row>
          <el-row align="middle" justify="center" style="text-align: center; height: 25px; margin-top: 10px"
                  :gutter="20">
            <el-col :span="12" style="color: grey">最后修改时间</el-col>
            <el-col :span="12">{{ moment(updateTime, 'YYYY-MM-DD HH:mm:ss').fromNow() }}</el-col>
          </el-row>
        </el-card>
        <el-card style="margin-top: 20px; padding: 10px 0; font-size: 14px; border-radius: 15px" shadow="never">
          <el-row align="middle" justify="center" style="text-align: center; padding: 0 10px" :gutter="10">
            <el-col :span="6">
              <el-card v-if="alreadyThumbsUp" class="operate-card" style="color: #409eff" shadow="hover"
                       @click="onClickUnThumbsUpButton">
                <el-row justify="center" align="middle">
                  <el-icon :size="24">
                    <icon-thumb-up-fill/>
                  </el-icon>
                </el-row>
                <el-row justify="center" align="middle">
                  点赞
                </el-row>
                <el-row justify="center" align="middle" style="font-weight: bold">
                  {{
                    Intl.NumberFormat('en', {
                      notation: "compact",
                      maximumFractionDigits: 1
                    }).format(thumbsUp)
                  }}
                </el-row>
              </el-card>
              <el-card v-else class="operate-card" shadow="hover" @click="onClickThumbsUpButton">
                <el-row justify="center" align="middle">
                  <el-icon :size="24">
                    <icon-thumb-up/>
                  </el-icon>
                </el-row>
                <el-row justify="center" align="middle">
                  点赞
                </el-row>
                <el-row justify="center" align="middle" style="font-weight: bold">
                  {{
                    Intl.NumberFormat('en', {
                      notation: "compact",
                      maximumFractionDigits: 1
                    }).format(thumbsUp)
                  }}
                </el-row>
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card v-if="alreadyCollected" class="operate-card" style="color: #409eff" shadow="hover"
                       @click="onClickUnCollectButton">
                <el-row justify="center" align="middle">
                  <el-icon :size="24">
                    <icon-star-fill/>
                  </el-icon>
                </el-row>
                <el-row justify="center" align="middle">
                  收藏
                </el-row>
                <el-row justify="center" align="middle" style="font-weight: bold">
                  {{
                    Intl.NumberFormat('en', {
                      notation: "compact",
                      maximumFractionDigits: 1
                    }).format(collections)
                  }}
                </el-row>
              </el-card>
              <el-card v-else class="operate-card" shadow="hover" @click="onClickCollectButton">
                <el-row justify="center" align="middle">
                  <el-icon :size="24">
                    <icon-star/>
                  </el-icon>
                </el-row>
                <el-row justify="center" align="middle">
                  收藏
                </el-row>
                <el-row justify="center" align="middle" style="font-weight: bold">
                  {{
                    Intl.NumberFormat('en', {
                      notation: "compact",
                      maximumFractionDigits: 1
                    }).format(collections)
                  }}
                </el-row>
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card class="operate-card" shadow="hover" @click="tippingDialogRef.showDialog()">
                <el-row justify="center" align="middle">
                  <el-icon :size="24">
                    <Coin/>
                  </el-icon>
                </el-row>
                <el-row justify="center" align="middle">
                  投币
                </el-row>
                <el-row justify="center" align="middle" style="font-weight: bold">
                  {{
                    Intl.NumberFormat('en', {
                      notation: "compact",
                      maximumFractionDigits: 1
                    }).format(tipping)
                  }}
                </el-row>
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card class="operate-card" shadow="hover" @click="onClickForwardButton">
                <el-row justify="center" align="middle">
                  <el-icon :size="24">
                    <icon-share-internal/>
                  </el-icon>
                </el-row>
                <el-row justify="center" align="middle">
                  转发
                </el-row>
                <el-row justify="center" align="middle" style="font-weight: bold">
                  {{
                    Intl.NumberFormat('en', {
                      notation: "compact",
                      maximumFractionDigits: 1
                    }).format(forwards)
                  }}
                </el-row>
              </el-card>
            </el-col>
          </el-row>
        </el-card>
        <el-card style="margin-top: 20px; padding: 10px 5px; font-size: 14px; border-radius: 15px; cursor: pointer"
                 shadow="hover" @click="commentDrawerRef.showDrawer()">
          <el-row el-row align="middle" justify="center" style="height: 25px" :gutter="20">
            <el-col :span="2"/>
            <el-col :span="10" style="font-size: 16px; font-weight: bolder">
              <span>评论（{{ comments }}）</span>
            </el-col>
            <el-col :span="10">
              <span style="float: right; font-size: 16px; font-weight: bolder">
                <el-icon><icon-message/></el-icon>
                <el-icon><icon-right/></el-icon>
              </span>
            </el-col>
            <el-col :span="2"/>
          </el-row>
        </el-card>
        <el-card v-if="store.getters.getUid === owner"
                 style="margin-top: 20px; padding: 10px 5px; font-size: 14px; border-radius: 15px; cursor: pointer"
                 shadow="hover" @click="router.push('/creation/edit/' + articleId)">
          <el-row el-row align="middle" justify="center" style="height: 25px" :gutter="20">
            <el-col :span="2"/>
            <el-col :span="10" style="font-size: 16px; font-weight: bolder">
              <span>编辑文章</span>
            </el-col>
            <el-col :span="10">
              <span style="float: right; font-size: 16px; font-weight: bolder">
                <el-icon><EditPen/></el-icon>
                <el-icon><icon-right/></el-icon>
              </span>
            </el-col>
            <el-col :span="2"/>
          </el-row>
        </el-card>
      </el-scrollbar>
    </el-aside>
  </el-container>
</template>

<style scoped>
:deep() .el-card__body {
  padding: 0;
}

.operate-card {
  padding: 5px;
  border-style: none;
  border-radius: 10px;
}

.operate-card:hover {
  cursor: pointer;
}
</style>