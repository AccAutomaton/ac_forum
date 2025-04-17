<script setup>
import {nextTick, ref} from "vue";
import moment from "moment";
import {createCommentByArticleId, getCommentListByArticleId} from "@/request/article.js";
import {getObjectUrlOfPublicResources} from "@/request/cos.js";
import store from "@/store/index.js";
import {deleteCommentById, getCommentById, thumbsUpCommentById, unThumbsUpCommentById} from "@/request/comment.js";
import {ElNotification} from "element-plus";
import {useRoute} from "vue-router";
import router from "@/router/index.js";

const drawerVisible = ref(false);
const {articleId, comments} = defineProps({
  articleId: String,
  comments: Number,
  articleOwner: Number,
});
const emit = defineEmits(["onAddingComment", "onDeletingComment"]);
const latest = ref(false);
const currentPageNumber = ref(1), currentPageSize = ref(10), isLastPage = ref(false);
const commentList = ref([]);
const newComment = ref(""), newCommentTargetComment = ref(0), newCommentTargetCommenter = ref(0),
    newCommentTargetCommenterNickname = ref(""), newCommentTargetIndex = ref(0);
const commenterAvatarPrefix = ref("");
const avatarCache = new Map();
const showComment = useRoute().query.showComment;

const getCommentList = async () => {
  if (!isLastPage.value) {
    const data = await getCommentListByArticleId(articleId, latest.value, currentPageNumber.value, currentPageSize.value);
    if (data !== null) {
      commenterAvatarPrefix.value = data["commenterAvatarPrefix"];
      for (let i = 0; i < data["commentList"]["records"].length; i++) {
        if (data["commentList"]["records"][i]["commenterAvatar"] !== "") {
          if (avatarCache.has(data["commentList"]["records"][i]["commenterAvatar"])) {
            data["commentList"]["records"][i]["commenterAvatar"] = avatarCache.get(data["commentList"]["records"][i]["commenterAvatar"]);
            commentList.value.push(data["commentList"]["records"][i]);
          } else {
            await getObjectUrlOfPublicResources(commenterAvatarPrefix.value + data["commentList"]["records"][i]["commenterAvatar"], (url) => {
              data["commentList"]["records"][i]["commenterAvatar"] = url;
              commentList.value.push(data["commentList"]["records"][i]);
            });
          }
        }
      }
      isLastPage.value = data["commentList"]["isLastPage"];
      if (!isLastPage.value) {
        currentPageNumber.value++;
      }
    }
  }
}

{
  getCommentList();
  nextTick();
}

const showDrawer = async () => {
  await getCommentList();
  drawerVisible.value = true;
}

defineExpose({
  showDrawer,
})

const changeOrderMode = (needLatest) => {
  latest.value = needLatest;
  isLastPage.value = false;
  currentPageNumber.value = 1;
  commentList.value.length = 0;
  getCommentList();
}

const onClickReplyButton = (comment, commenter, commenterNickname, index) => {
  newCommentTargetComment.value = comment;
  newCommentTargetCommenter.value = commenter;
  newCommentTargetCommenterNickname.value = commenterNickname;
  newCommentTargetIndex.value = index;
}

const onClickCancelReplyButton = () => {
  newCommentTargetComment.value = 0;
  newCommentTargetCommenter.value = 0;
  newCommentTargetCommenterNickname.value = "";
  newCommentTargetIndex.value = 0;
}

const scrollToCommentId = async (commentId, insertPosition = 0) => {
  let flag = false;
  for (let i = 0; i < commentList.value.length; i++) {
    if (commentList.value[i]['id'].toString() === commentId.toString()) {
      flag = true;
      break;
    }
  }
  if (!flag) {
    const data = await getCommentById(commentId);
    if (data !== null) {
      if (data["commenterAvatar"] !== "") {
        if (avatarCache.has(data["commenterAvatar"])) {
          data["commenterAvatar"] = avatarCache.get(data["commenterAvatar"]);
          commentList.value.splice(insertPosition, 0, data);
          await nextTick();
        } else {
          await getObjectUrlOfPublicResources(commenterAvatarPrefix.value + data["commenterAvatar"], (url) => {
            data["commenterAvatar"] = url;
            commentList.value.splice(insertPosition, 0, data);
            nextTick(() => {
              const comment = document.getElementById("comment_" + commentId);
              if (comment) {
                comment.style.backgroundColor = "rgba(11,11,11,0.1)";
                comment.scrollIntoView({behavior: "smooth", block: "start"});
                setTimeout(() => {
                  comment.style.backgroundColor = "transparent";
                }, 1000)
              }
            });
          });
          return;
        }
      }
    }
  }
  await nextTick(() => {
    const comment = document.getElementById("comment_" + commentId);
    if (comment) {
      comment.style.backgroundColor = "rgba(11,11,11,0.1)";
      comment.scrollIntoView({behavior: "smooth", block: "start"});
      setTimeout(() => {
        comment.style.backgroundColor = "transparent";
      }, 1000)
    }
  });
}

const onClickThumbsUpButton = async (commentId, index) => {
  const data = await thumbsUpCommentById(commentId);
  if (data !== null) {
    commentList.value[index]["alreadyThumbsUp"] = true;
    commentList.value[index]["thumbsUp"]++;
  }
}

const onClickUnThumbsUpButton = async (commentId, index) => {
  const data = await unThumbsUpCommentById(commentId);
  if (data !== null) {
    commentList.value[index]["alreadyThumbsUp"] = false;
    commentList.value[index]["thumbsUp"]--;
  }
}

const onClickSendCommentButton = async () => {
  if (newComment.value === "") {
    ElNotification({type: "error", title: "评论不能为空"});
    return;
  }
  let data;
  if (newCommentTargetCommenter.value === 0) {
    data = await createCommentByArticleId(articleId, newComment.value);
  } else {
    data = await createCommentByArticleId(articleId, newComment.value, newCommentTargetComment.value);
  }
  if (data !== null) {
    await scrollToCommentId(data["commentId"], newCommentTargetIndex.value);
    newComment.value = "";
    emit("onAddingComment");
    onClickCancelReplyButton();
  }
}

const onClickDeleteCommentButton = async (commentId, index) => {
  const data = await deleteCommentById(commentId);
  if (data !== null) {
    commentList.value.splice(index, 1);
    emit("onDeletingComment");
  }
}

if (showComment) {
  nextTick(() => {
    drawerVisible.value = true;
    scrollToCommentId(showComment);
    router.push({query: {}});
  })
}
</script>

<template>
  <el-drawer v-model="drawerVisible" destroy-on-close size="27%">
    <template #header>
      <el-text style="font-size: 20px; font-weight: bolder">评论（{{ comments }}）</el-text>
    </template>
    <el-row style="margin-top: -20px; margin-bottom: 30px">
      <el-text class="query-mode-selector" v-if="latest" @click="changeOrderMode(false)">点赞最多</el-text>
      <el-text v-else style="color: #3397fd; cursor: default">点赞最多</el-text>
      <el-text style="margin: 0 10px">|</el-text>
      <el-text v-if="latest" style="color: #3397fd; cursor: default">最新发布</el-text>
      <el-text class="query-mode-selector" v-else @click="changeOrderMode(true)">最新发布</el-text>
    </el-row>
    <el-scrollbar style="height: 67.5vh">
      <ul style="padding: 0; margin-top: 0" v-infinite-scroll="getCommentList" infinite-scroll-immediate="false">
        <li v-for="(comment, index) in commentList" :key="comment['id']" :id="'comment_' + comment['id']"
            style="list-style: none; margin: 5px; border-radius: 15px; padding: 5px 5px 0 5px">
          <el-row :gutter="10" align="middle" justify="center">
            <el-col :span="2">
              <el-avatar v-if="comment['commenterAvatar'] === ''" size="small">
                {{ comment["commenterNickname"].slice(0, 1) }}
              </el-avatar>
              <el-avatar v-else size="small" :src="comment['commenterAvatar']"/>
            </el-col>
            <el-col :span="16">
              <el-text line-clamp="1" truncated>
                <el-link style="font-weight: bold; font-size: 15px" @click="router.push(`/artist/${comment['commenter']}/livingRoom/`)">
                  {{ comment["commenterNickname"] }}
                </el-link>
                <el-tag v-if="comment['commenter'] === articleOwner" size="small" effect="plain"
                        style="margin-left: 5px; font-weight: normal">
                  作者
                </el-tag>
              </el-text>
            </el-col>
            <el-col :span="6" style="font-size: 14px">
              <el-row align="middle" style="height: 100%; font-size: 12px; color: grey">
                {{ moment(comment["createTime"], 'YYYY-MM-DD HH:mm:ss').fromNow() }}
              </el-row>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="2"/>
            <el-col :span="22">
            <span v-if="comment['targetCommenter']">
              <el-text style="color: black">回复</el-text>
              <el-text style="margin-left: 2px; color: #3397fd">@{{ comment["targetCommenterNickname"] }}</el-text>
              <el-text style="color: black">：</el-text>
            </span>
              <el-text style="color: black">
                {{ comment["content"] }}
              </el-text>
            </el-col>
          </el-row>
          <el-row style="margin-top: 10px; color: #888888">
            <el-col :span="2"/>
            <el-col :span="4">
              <el-row align="middle">
                <el-tooltip v-if="comment['alreadyThumbsUp']" content="取消点赞" effect="light">
                  <el-icon color="#3397fd" class="query-mode-selector"
                           @click="onClickUnThumbsUpButton(comment['id'], index)">
                    <icon-thumb-up-fill/>
                  </el-icon>
                </el-tooltip>
                <el-tooltip v-else content="点赞" effect="light">
                  <el-icon class="query-mode-selector" @click="onClickThumbsUpButton(comment['id'], index)">
                    <icon-thumb-up/>
                  </el-icon>
                </el-tooltip>
                <el-text style="margin-left: 3px">
                  {{
                    Intl.NumberFormat('en', {
                      notation: "compact",
                      maximumFractionDigits: 1
                    }).format(comment["thumbsUp"])
                  }}
                </el-text>
              </el-row>
            </el-col>
            <el-col :span="4">
              <el-tooltip content="回复" effect="light">
                <el-icon class="query-mode-selector"
                         @click="onClickReplyButton(comment['id'], comment['commenter'], comment['commenterNickname'], index)">
                  <icon-reply/>
                </el-icon>
              </el-tooltip>
            </el-col>
            <el-col v-if="comment['targetComment'] !== undefined" :span="4">
              <el-tooltip content="定位到被引用的评论" effect="light">
                <el-icon class="query-mode-selector" @click="scrollToCommentId(comment['targetComment'])">
                  <icon-history/>
                </el-icon>
              </el-tooltip>
            </el-col>
            <el-col style="margin-left: auto" v-if="comment['commenter'] === store.getters.getUid" :span="4">
              <el-tooltip content="删除评论" effect="light">
                <el-icon style="color: darkred" class='query-mode-selector'
                         @click="onClickDeleteCommentButton(comment['id'], index)">
                  <icon-delete/>
                </el-icon>
              </el-tooltip>
            </el-col>
          </el-row>
          <el-divider style="margin: 10px 0"/>
        </li>
      </ul>
    </el-scrollbar>
    <el-input v-model="newComment" :rows="3" type="textarea" placeholder="请输入要评论的内容" style="margin-top: 10px"
              maxlength="1024" show-word-limit resize="none"/>
    <el-row style="margin-top: 10px" align="middle" justify="center">
      <el-col v-if="newCommentTargetCommenter !== 0" :span="18" style="float: right;">
        <el-row align="middle" justify="end" style="padding-right: 20px">
          <el-text>回复</el-text>
          <el-text style="margin-left: 2px; color: #3397fd; max-width: 80%" truncated>
            @{{ newCommentTargetCommenterNickname }}
          </el-text>
          <el-icon class="query-mode-selector" style="font-size: 12px; margin-left: 5px"
                   @click="onClickCancelReplyButton">
            <icon-close/>
          </el-icon>
        </el-row>
      </el-col>
      <el-col style="margin-left: auto" :span="6">
        <el-button style="width: 100%" plain type="primary" @click="onClickSendCommentButton">发送</el-button>
      </el-col>
    </el-row>
  </el-drawer>
</template>

<style scoped>
.query-mode-selector {
  cursor: pointer;
}

.query-mode-selector:hover {
  color: #3397fd
}
</style>