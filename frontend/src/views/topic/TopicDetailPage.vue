<!--suppress CssUnusedSymbol -->
<script setup>
import {useRoute} from "vue-router";
import {
  deleteTopic,
  getTopicAvatarUploadAuthorization,
  getTopicById,
  updateTopic,
  updateTopicAvatarById
} from "@/request/topic.js";
import {getObjectUrlOfPublicResources, uploadObject} from "@/request/cos.js";
import moment from "moment";
import ArtistInformationPopoverContent from "@/components/user/ArtistInformationPopoverContent.vue";
import {ref} from 'vue'
import {Setting} from "@element-plus/icons-vue";
import store from "@/store/index.js";
import ArticleOfTopicVisitsRankingList from "@/views/topic/ArticleOfTopicVisitsRankingList.vue";
import ArticleOfTopicThumbsUpRankingList from "@/views/topic/ArticleOfTopicThumbsUpRankingList.vue";
import ArticleListOfTopic from "@/views/topic/ArticleListOfTopic.vue";
import {ElMessageBox, ElNotification} from "element-plus";
import router from "@/router/index.js";

const topicId = useRoute().params.topicId;
const title = ref(""), description = ref("");
const administratorId = ref(0), administratorNickname = ref(""), administratorAvatar = ref("");
const articles = ref(0), visits = ref(0), createTime = ref(""), avatar = ref("");
const administratorStatisticRef = ref(), administratorInformationPopoverRef = ref();
const manageTopicDialogVisible = ref(false), newTopicTitle = ref(""), newTopicDescription = ref("");

const getTopicInfomation = async () => {
  const data = await getTopicById(topicId);
  if (data !== null) {
    title.value = data["title"];
    description.value = data["description"];
    administratorId.value = data["administratorId"];
    administratorNickname.value = data["administratorNickname"];
    if (data["administratorAvatar"] !== undefined) {
      await getObjectUrlOfPublicResources(data["administratorAvatar"], (url) => {
        administratorAvatar.value = url;
      });
    }
    articles.value = data["articles"];
    visits.value = data["visits"];
    createTime.value = data["createTime"];
    if (data["avatar"] !== undefined) {
      await getObjectUrlOfPublicResources(data["avatar"], (url) => {
        avatar.value = url;
      })
    }
    newTopicTitle.value = title.value;
    newTopicDescription.value = description.value;
  } else {
    await router.push("/404");
  }
}
getTopicInfomation();

const onClickConfirmUpdateTopicButton = async () => {
  const data = await updateTopic(topicId, newTopicTitle.value, newTopicDescription.value);
  if (data !== null) {
    ElNotification({type: "success", title: "修改成功"});
    title.value = newTopicTitle.value;
    description.value = newTopicDescription.value;
    manageTopicDialogVisible.value = false;
  }
}

const onClickConfirmDeleteTopicButton = () => {
  ElMessageBox.confirm(
      '确定要删除此话题？',
      '警告：删除话题',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning',
      }
  ).then(async () => {
    const data = await deleteTopic(topicId);
    if (data !== null) {
      ElNotification({type: "success", title: "删除成功"});
      manageTopicDialogVisible.value = false;
      router.back();
    }
  }).catch(() => {
  })
}

const onClickCancelUpdateTopicButton = () => {
  manageTopicDialogVisible.value = false;
  newTopicTitle.value = title.value;
  newTopicDescription.value = description.value;
}

const onClickUpdateTopicAvatarButton = () => {
  window.showOpenFilePicker({
    types: [
      {
        description: "图片（头像）",
        accept: {
          "image/*": [".png", ".gif", ".jpeg", ".jpg"],
        }
      }
    ],
    excludeAcceptAllOption: true,
    multiple: false,
  }).then(async (res) => {
    const file = await res[0].getFile();
    const data = await getTopicAvatarUploadAuthorization();
    const avatarFileName = data["key"].substring(data["key"].lastIndexOf("/") + 1);
    if (data !== null) {
      uploadObject(data, file, async () => {
        const data = await updateTopicAvatarById(topicId, avatarFileName);
        if (data !== null) {
          await getObjectUrlOfPublicResources(data["avatarKey"], (url) => {
            avatar.value = url;
          })
        }
      })
    }
  })
}
</script>

<template>
  <el-container>
    <el-header height="auto">
      <el-card style="border-radius: 25px;">
        <el-row align="middle" justify="center" style="width: 100%" :gutter="5">
          <el-col :span="2">
            <el-row align="middle" justify="center">
              <el-avatar v-if="avatar === ''" shape="square" size="large">
                <span style="font-size: large">{{ title.slice(0, 2) }}</span>
              </el-avatar>
              <el-avatar v-else shape="square" size="large" :src="avatar"
                         style="background-color: transparent;"/>
            </el-row>
          </el-col>
          <el-col :span="12">
            <el-text style="font-size: xx-large; font-weight: bolder; color: black" truncated>{{ title }}</el-text>
          </el-col>
          <el-col :span="2">
            <el-button v-if="administratorId === store.getters.getUid" round :icon="Setting"
                       @click="manageTopicDialogVisible = true">管理话题
            </el-button>
          </el-col>
          <el-col :span="2" style="text-align: center">
            <el-statistic :value="articles" style="float: right">
              <template #title>
                <div style="display: inline-flex; align-items: center">
                  文章数
                </div>
              </template>
            </el-statistic>
          </el-col>
          <el-col :span="2" style="text-align: center">
            <el-statistic :value="visits">
              <template #title>
                <div style="display: inline-flex; align-items: center">
                  浏览量
                </div>
              </template>
            </el-statistic>
          </el-col>
          <el-col :span="2" style="text-align: center">
            <el-statistic :value="''" style="float: left">
              <template #title>
                <div style="display: inline-flex; align-items: center">
                  创建时间
                </div>
              </template>
              <template #prefix>
                {{ moment(createTime, 'YYYY-MM-DD').fromNow() }}
              </template>
            </el-statistic>
          </el-col>
          <el-col :span="2" style="text-align: center">
            <el-statistic class="administrator-statistic" :value="''" ref="administratorStatisticRef"
                          style="background-color: rgba(128,128,128,0.1); border-radius: 15px">
              <template #title>
                <div style="display: inline-flex; align-items: center">
                  话题主
                </div>
              </template>
              <template #prefix>
                <el-avatar style="background-color: transparent" :src="administratorAvatar" :size="16"/>
                <el-text style="font-size: 14px; max-width: 85px; text-align: left; margin-left: 5px" truncated>
                  {{ administratorNickname }}
                </el-text>
              </template>
            </el-statistic>
            <el-popover ref="administratorInformationPopoverRef" :virtual-ref="administratorStatisticRef"
                        trigger="click" virtual-triggering width="300" :offset="24" :persistent="false">
              <ArtistInformationPopoverContent :uid="administratorId" :nickname="administratorNickname"
                                               :avatar="administratorAvatar"/>
            </el-popover>
          </el-col>
        </el-row>
      </el-card>
    </el-header>
    <el-container>
      <el-aside width="325px" style="margin-left: 20px">
        <el-card shadow="never"
                 style="border-radius: 25px; border-color: rgba(211,211,211,0.5); border-width: thin; margin-top: 20px">
          <ArticleOfTopicVisitsRankingList :topicId="topicId"/>
        </el-card>
      </el-aside>
      <el-main style="padding-bottom: 0">
        <ArticleListOfTopic :topicId="topicId"/>
      </el-main>
      <el-aside width="325px" style="margin-right: 20px">
        <el-card shadow="never"
                 style="border-radius: 25px; border-color: rgba(211,211,211,0.5); border-width: thin; margin-top: 20px">
          <ArticleOfTopicThumbsUpRankingList :topicId="topicId"/>
        </el-card>
      </el-aside>
    </el-container>
  </el-container>

  <el-dialog v-model="manageTopicDialogVisible" title="话题管理" width="400" align-center destroy-on-close>
    <el-row :gutter="20" style="margin-bottom: 10px">
      <el-col :span="19">
        <el-row style="margin-bottom: 5px">
          <el-text>话题名称</el-text>
        </el-row>
        <el-row style="height: 56px">
          <el-input v-model="newTopicTitle" placeholder="请输入话题名称" clearable maxlength="32" show-word-limit/>
        </el-row>
      </el-col>
      <el-col :span="5">
        <el-row style="margin-bottom: 5px">
          <el-text>话题头像</el-text>
        </el-row>
        <el-row style="cursor: pointer" @click="onClickUpdateTopicAvatarButton">
          <el-avatar v-if="avatar === ''" shape="square" size="large">
            <span style="font-size: large">{{ title.slice(0, 2) }}</span>
          </el-avatar>
          <el-avatar v-else shape="square" size="large" :src="avatar"
                     style="background-color: transparent;"/>
        </el-row>
      </el-col>
    </el-row>
    <el-row style="margin-bottom: 5px">
      <el-text>话题简介</el-text>
    </el-row>
    <el-row>
      <el-input v-model="newTopicDescription" placeholder="请输入话题简介" clearable
                type="textarea" rows="5" resize="none" maxlength="1024" show-word-limit/>
    </el-row>
    <template #footer>
      <el-button style="float: left" type="danger" @click="onClickConfirmDeleteTopicButton">删除话题</el-button>
      <el-button @click="onClickCancelUpdateTopicButton">取消</el-button>
      <el-button type="primary" @click="onClickConfirmUpdateTopicButton">
        确认
      </el-button>
    </template>
  </el-dialog>
</template>

<style scoped>
:deep() .el-card__body {
  padding: 10px;
}

.administrator-statistic {
  cursor: pointer;
}

.administrator-statistic:hover {
  border-style: solid;
  border-color: rgba(64, 158, 255, 0.5);
}
</style>