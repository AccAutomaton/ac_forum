<script setup>
import router from "@/router/index.js";
import {nextTick, ref, watch} from "vue";
import {
  getChatMessageByChatId, getImageDownloadAuthorizationByChatId,
  getImageUploadAuthorizationByChatId,
  readChatById,
  sendChatImageMessage,
  sendChatMessage
} from "@/request/chat.js";
import store from "@/store/index.js";
import {Picture, Setting} from "@element-plus/icons-vue";
import {useStorage} from "@vueuse/core";
import {getObjectUrl, uploadObject} from "@/request/cos.js";

const {currentChat} = defineProps({
  currentChat: {},
});
const emit = defineEmits(["onReadChat", "onSendMessage"])

const chatMessageList = ref([]), isLastPage = ref(false), isFirstLoading = ref(true);
const newMessage = ref("");
const scrollBarRef = ref();
const imageDownloadAuthorization = ref();
const loading = ref(false);

const scrollToBottom = () => {
  nextTick(() => {
    const container = scrollBarRef.value.$el.querySelector('.el-scrollbar__wrap');
    container.style.scrollBehavior = 'smooth';
    container.scrollTop = container.scrollHeight;
  })
}

watch(() => currentChat, async (value) => {
  newMessage.value = "";
  chatMessageList.value = [];
  isFirstLoading.value = true;
  isLastPage.value = false;
  imageDownloadAuthorization.value = undefined;
  if (value["id"]) {
    const data = await getChatMessageByChatId(value["id"], 10);
    if (data !== null) {
      isLastPage.value = data["isLastPage"];
      for (let i = 0; i < data["records"].length; i++) {
        if (data["records"][i]["type"]["index"] === 1) {
          data["records"][i]["content"] = await converseImageUrl(data["records"][i]["content"]);
        }
      }
      chatMessageList.value.unshift(...data["records"].reverse());
      scrollToBottom();
      await readChatById(value["id"]);
      emit("onReadChat", value["id"]);
    }
  }
});

const sendMessage = async () => {
  const data = await sendChatMessage(currentChat["id"], newMessage.value);
  if (data !== null) {
    await appendMessage(data)
    scrollToBottom();
    emit("onSendMessage", currentChat["id"], newMessage.value)
    newMessage.value = "";
  }
}

const getImageDownloadAuthorization = async () => {
  if (!imageDownloadAuthorization.value) {
    const data = await getImageDownloadAuthorizationByChatId(currentChat["id"]);
    if (data !== null) {
      imageDownloadAuthorization.value = data;
    }
  }
}

const converseImageUrl = async (key) => {
  await getImageDownloadAuthorization()
  imageDownloadAuthorization.value["key"] = key;
  return getObjectUrl(imageDownloadAuthorization.value, () => {
  });
}

const sendImageMessage = () => {
  window.showOpenFilePicker({
    types: [
      {
        description: "图片",
        accept: {
          "image/*": [".png", ".gif", ".jpeg", ".jpg"],
        }
      }
    ],
    excludeAcceptAllOption: true,
    multiple: false,
  }).then(async (res) => {
    loading.value = true;
    const file = await res[0].getFile();
    const data = await getImageUploadAuthorizationByChatId(currentChat["id"]);
    const imageFileName = data["key"].substring(data["key"].lastIndexOf("/") + 1);
    if (data !== null) {
      uploadObject(data, file, async () => {
        const data = await sendChatImageMessage(currentChat["id"], imageFileName);
        if (data !== null) {
          await appendMessage(data)
          scrollToBottom();
          emit("onSendMessage", currentChat["id"], "[图片]")
        }
      })
    }
    loading.value = false;
  })
}

const appendMessage = async (message) => {
  isFirstLoading.value = false;
  if (message["type"]["index"] === 1) {
    message["content"] = await converseImageUrl(message["content"]);
  }
  chatMessageList.value.push(message);
  scrollToBottom();
}

defineExpose({
  appendMessage,
})

const onScroll = async (distance) => {
  if (distance.scrollTop === 0 && !isLastPage.value) {
    isFirstLoading.value = false;
    const container = scrollBarRef.value.$el.querySelector('.el-scrollbar__wrap');
    const beforeHeight = container.scrollHeight
    const data = await getChatMessageByChatId(currentChat["id"], 10, chatMessageList.value[0]["id"]);
    if (data !== null) {
      isLastPage.value = data["isLastPage"];
      for (let i = 0; i < data["records"].length; i++) {
        if (data["records"][i]["type"]["index"] === 1) {
          data["records"][i]["content"] = await converseImageUrl(data["records"][i]["content"]);
        }
      }
      chatMessageList.value.unshift(...data["records"].reverse());
      await nextTick(() => {
        container.style.scrollBehavior = 'auto';
        container.scrollTop = container.scrollHeight - beforeHeight;
      })
    }
  }
}

const onImageLoaded = (index) => {
  if (isFirstLoading.value || chatMessageList.value.length === index + 1) {
    nextTick(() => scrollToBottom());
  }
}

const keyOfSend = useStorage("keyOfSend", "Enter");
const onKeyUp = (event) => {
  if (event.key === "Enter") {
    if (event.ctrlKey && keyOfSend.value === "CtrlEnter") {
      sendMessage();
    } else if (event.shiftKey) {
    } else {
      sendMessage();
    }
  }
}
</script>

<template>
  <el-row v-loading="loading" v-if="currentChat.id">
    <el-row align="middle" justify="center" style="width: 100%; height: 52px">
      <el-link style="font-size: 18px; font-weight: bolder"
               @click="router.push(`/artist/${currentChat.receiverUid}/livingRoom/`)">
        {{ currentChat.receiverNickname }}
      </el-link>
    </el-row>
    <el-scrollbar ref="scrollBarRef" @scroll="onScroll"
                  style="border-bottom-style: solid; border-top-style: solid; border-color: rgba(161,155,155,0.2); width: 100%; height: 55vh">
      <el-row v-for="(message, index) in chatMessageList" :key="message['id']" style="margin: 10px 20px">
        <el-row v-if="message['type']['index'] === -1" style="width: 100%" align="middle" justify="center">
          <el-text size="small">
            <el-text size="small">
              {{
                message["content"].replace("${nickname}", message['sender'] === store.getters.getUid ? store.getters.getNickname : currentChat.receiverNickname)
              }}
            </el-text>
          </el-text>
        </el-row>
        <el-row v-else-if="message['type']['index'] === 0" style="width: 100%">
          <el-row v-if="message['sender'] === store.getters.getUid" style="width: 100%">
            <el-col :span="23">
              <el-row style="margin-right: 15px; float: right; width: 100%" align="middle" justify="end">
                <el-text style="font-size: 12px; margin-right: 10px">{{ message["createTime"] }}</el-text>
                <el-text style="font-size: 16px; font-weight: bold">{{ store.getters.getNickname }}</el-text>
              </el-row>
              <el-row style="margin-top: 5px; margin-right: 10px; float: right; width: 100%" justify="end">
                <el-text style="background-color: rgba(55,180,234,0.2); border-radius: 10px; padding: 8px 16px;
                              color: black; max-width: 60%; text-align: start; white-space: pre-line;">
                  {{ message["content"].trim() }}
                </el-text>
              </el-row>
            </el-col>
            <el-col :span="1">
              <el-avatar style="background-color: transparent" :size="36" :src="store.getters.getAvatar"/>
            </el-col>
          </el-row>
          <el-row v-else style="width: 100%; text-align: center" justify="center">
            <el-col :span="1">
              <el-avatar style="background-color: transparent" :size="36" :src="currentChat.receiverAvatar"/>
            </el-col>
            <el-col :span="23">
              <el-row style="margin-left: 15px; width: 100%" align="middle">
                <el-text style="font-size: 16px; font-weight: bold">{{ currentChat.receiverNickname }}</el-text>
                <el-text style="font-size: 12px; margin-left: 10px">{{ message["createTime"] }}</el-text>
              </el-row>
              <el-row style="margin-top: 5px; margin-left: 10px; width: 100%">
                <el-text style="background-color: rgba(161,155,155,0.2); border-radius: 10px; padding: 8px 16px;
                              color: black; max-width: 60%; text-align: start; white-space: pre-line;">
                  {{ message["content"].trim() }}
                </el-text>
              </el-row>
            </el-col>
          </el-row>
        </el-row>
        <el-row v-else-if="message['type']['index'] === 1" style="width: 100%">
          <el-row v-if="message['sender'] === store.getters.getUid" style="width: 100%">
            <el-col :span="23">
              <el-row style="margin-right: 15px; float: right; width: 100%" align="middle" justify="end">
                <el-text style="font-size: 12px; margin-right: 10px">{{ message["createTime"] }}</el-text>
                <el-text style="font-size: 16px; font-weight: bold">{{ store.getters.getNickname }}</el-text>
              </el-row>
              <el-row style="margin-top: 5px; margin-right: 10px; float: right; width: 100%" justify="end">
                <el-image style="max-width:40%; border-radius: 10px; cursor: pointer" :src="message['content']" @load="onImageLoaded(index)"
                          hide-on-click-modal :preview-src-list="[message['content']]"/>
              </el-row>
            </el-col>
            <el-col :span="1">
              <el-avatar style="background-color: transparent" :size="36" :src="store.getters.getAvatar"/>
            </el-col>
          </el-row>
          <el-row v-else style="width: 100%; text-align: center" justify="center">
            <el-col :span="1">
              <el-avatar style="background-color: transparent" :size="36" :src="currentChat.receiverAvatar"/>
            </el-col>
            <el-col :span="23">
              <el-row style="margin-left: 15px; width: 100%" align="middle">
                <el-text style="font-size: 16px; font-weight: bold">{{ currentChat.receiverNickname }}</el-text>
                <el-text style="font-size: 12px; margin-left: 10px">{{ message["createTime"] }}</el-text>
              </el-row>
              <el-row style="margin-top: 5px; margin-left: 10px; width: 100%">
                <el-image style="max-width:40%; border-radius: 10px; cursor: pointer" :src="message['content']" @load="onImageLoaded(index)"
                          hide-on-click-modal :preview-src-list="[message['content']]"/>
              </el-row>
            </el-col>
          </el-row>
        </el-row>
      </el-row>
    </el-scrollbar>
    <el-row style="width: 100%">
      <el-button plain :icon="Picture" style="font-size: 18px; border-style: none; padding: 5px 10px 5px 15px"
                 @click="sendImageMessage"/>
      <el-popover placement="right" trigger="click">
        <template #reference>
          <el-button plain :icon="Setting" style="font-size: 18px; border-style: none; padding: 5px; margin: 0"/>
        </template>
        <el-text size="small">发送消息快捷键</el-text>
        <el-radio-group v-model="keyOfSend" style="margin-top: 5px">
          <el-radio value="Enter" size="small">Enter</el-radio>
          <el-radio value="CtrlEnter" size="small">Ctrl + Enter</el-radio>
        </el-radio-group>
      </el-popover>
    </el-row>
    <el-row style="width: 100%">
      <el-input v-model="newMessage" type="textarea" rows="5" resize="none" :maxlength="1024" show-word-limit
                style="width: 100%" @keyup="onKeyUp"/>
    </el-row>
    <el-row style="width: 100%" justify="end">
      <el-button style="margin-right: 5px; border-bottom-right-radius: 20px" @click="sendMessage">
        发送
      </el-button>
    </el-row>
  </el-row>
  <el-row v-else align="middle" justify="center" style="height: 100%">
    <el-icon :size="200" style="color: rgba(135,132,132,0.25)">
      <icon-message/>
    </el-icon>
  </el-row>
</template>

<style scoped>
:deep(.el-textarea__inner) {
  box-shadow: none;
}
</style>