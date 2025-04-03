<script setup>
import {Plus} from "@element-plus/icons-vue";
import {nextTick, onBeforeUnmount, ref} from "vue";
import {getChatById, getChatList, readChatById} from "@/request/chat.js";
import {getObjectUrlOfPublicResources} from "@/request/cos.js";
import store from "@/store/index.js";
import moment from "moment"
import ChatMessageArea from "@/views/chat/ChatMessageArea.vue";
import {useRoute} from "vue-router";
import router from "@/router/index.js";
import {GetAuthorizationCode} from "@/request/index.js";
import {ElNotification} from "element-plus";
import CreateChatDialog from "@/views/chat/CreateChatDialog.vue";

const chatList = ref([]), chatIdSet = new Set();
const currentChatId = ref(useRoute().params.id ? useRoute().params.id : 0), currentChat = ref({});
const currentPageNumber = ref(1), currentPageSize = ref(10), isLastPage = ref(false);
const chatMessageAreaRef = ref(), scrollBarRef = ref();

const getChatListData = async () => {
  if (!isLastPage.value) {
    const data = await getChatList(currentPageNumber.value, currentPageSize.value);
    if (data !== null) {
      const chatListRecords = data["chatList"]["records"];
      for (let i = 0; i < chatListRecords.length; i++) {
        if (!chatIdSet.has(chatListRecords[i]["chatId"])) {
          chatIdSet.add(chatListRecords[i]["chatId"]);
          const receiverVariablePrefix = "participant" + (chatListRecords[i]["participant1Uid"] === store.getters.getUid ? 2 : 1);
          await getObjectUrlOfPublicResources(data["participantAvatarPrefix"] + chatListRecords[i][receiverVariablePrefix + "Avatar"],
              (url) => {
                chatList.value.push({
                  id: chatListRecords[i]["chatId"],
                  receiverUid: chatListRecords[i][receiverVariablePrefix + "Uid"],
                  receiverNickname: chatListRecords[i][receiverVariablePrefix + "Nickname"],
                  receiverAvatar: url,
                  latestChatMessage: chatListRecords[i]["latestChatMessage"],
                  notReadMessages: chatListRecords[i]["notReadMessages"],
                  updateTime: chatListRecords[i]["updateTime"],
                })
              }
          );
        }
      }
      isLastPage.value = data["chatList"]["isLastPage"];
      if (!isLastPage.value) {
        currentPageNumber.value++;
      }
    }
  }
}

const openChatById = async (id) => {
  id = Number.parseInt(id);
  if (id === 0) {
    return;
  }
  if (chatIdSet.has(id)) {
    for (let i = 0; i < chatList.value.length; i++) {
      if (chatList.value[i]['id'] === id) {
        currentChat.value = chatList.value[i];
        return;
      }
    }
  } else {
    const data = await getChatById(id);
    if (data !== null) {
      chatIdSet.add(id);
      await getObjectUrlOfPublicResources(data["receiverAvatar"], (url) => {
        chatList.value.unshift({
          id: data["chatId"],
          receiverUid: data["receiverUid"],
          receiverNickname: data["receiverNickname"],
          receiverAvatar: url,
          latestChatMessage: data["latestChatMessage"],
          notReadMessages: data["notReadMessages"],
          updateTime: data["updateTime"],
        });
        currentChat.value = chatList.value[0];
      });
    }
  }
}

const refreshLatestChatMessageByChatId = (chatId, latestMessage) => {
  for (let i = 0; i < chatList.value.length; i++) {
    if (chatList.value[i]['id'] === chatId) {
      chatList.value[i]["latestChatMessage"] = latestMessage;
      chatList.value[i]["updateTime"] = moment().utc(true).format('YYYY-MM-DD HH:mm:ss');
      chatList.value.unshift(chatList.value.splice(i, 1)[0]);
      return;
    }
  }
}

const increaseNotReadMessagesByChatId = (chatId) => {
  for (let i = 0; i < chatList.value.length; i++) {
    if (chatList.value[i]['id'] === chatId) {
      chatList.value[i]["notReadMessages"]++;
      return;
    }
  }
}

const resetNotReadMessagesByChatId = (chatId) => {
  for (let i = 0; i < chatList.value.length; i++) {
    if (chatList.value[i]['id'] === chatId) {
      chatList.value[i]["notReadMessages"] = 0;
      return;
    }
  }
}

const onClickChatListItem = (id) => {
  currentChatId.value = id;
  router.push(`/chat/${id}`);
  openChatById(id);
}

let webSocket;
const initWebSocket = () => {
  webSocket = new WebSocket(
      import.meta.env.VITE_WEBSOCKET_API_HOST + "/webSocket/chat?Authorization=" + GetAuthorizationCode()
  );
  webSocket.onopen = () => {
  }
  webSocket.onmessage = (event) => {
    let chatMessage = JSON.parse(event.data);
    if (chatMessage) {
      if (chatIdSet.has(chatMessage["chatId"])) {
        if (currentChat.value.id === chatMessage["chatId"]) {
          chatMessageAreaRef.value.appendMessage(chatMessage);
          readChatById(chatMessage["chatId"]);
          resetNotReadMessagesByChatId(chatMessage["chatId"]);
        } else {
          increaseNotReadMessagesByChatId(chatMessage["chatId"])
        }
        if (chatMessage["type"]["index"] === 0) {
          refreshLatestChatMessageByChatId(chatMessage["chatId"], chatMessage["content"]);
        } else if (chatMessage["type"]["index"] === -1) {
          refreshLatestChatMessageByChatId(chatMessage["chatId"], "[系统消息]");
        } else if (chatMessage["type"]["index"] === 1) {
          refreshLatestChatMessageByChatId(chatMessage["chatId"], "[图片]");
        }
      } else {
        openChatById(chatMessage["chatId"]);
      }
    }
  }
  webSocket.onerror = () => {
    ElNotification({type: "error", title: "与聊天服务器的连接发生错误", message: "请刷新后重试"});
  }
  webSocket.onclose = () => {
  };
}

{
  nextTick(() => {
    getChatListData();
    openChatById(currentChatId.value);
    nextTick(() => initWebSocket());
  })
}

onBeforeUnmount(() => {
  webSocket.close();
})

const createChatDialogRef = ref();
const onClickCreateChatButton = () => {
  createChatDialogRef.value.showDialog();
}
</script>

<template>
  <el-row align="middle" justify="center">
    <el-card style="height: 83.5vh; width: 77.5%; border-radius: 25px; margin-top: 5px">
      <el-container>
        <el-aside style="height: 84vh; border-right-style: solid; border-color: rgba(161,155,155,0.3); width: 30%">
          <el-row align="middle" justify="center" style="margin: 10px 0">
            <el-button :icon="Plus" style="border-radius: 10px" plain @click="onClickCreateChatButton">发起聊天
            </el-button>
          </el-row>
          <el-scrollbar ref="scrollBarRef" style="height: 77.5vh; border-top-style: solid; border-color: rgba(161, 155, 155, 0.2)">
            <ul style="padding: 0; margin-top: 0; margin-bottom: 0" v-infinite-scroll="getChatListData" infinite-scroll-immediate="false">
              <li v-for="chat in chatList" :key="chat['id']" style="list-style: none">
                <el-row class="chat-list-item" align="middle" justify="center" @click="onClickChatListItem(chat['id'])"
                        :style="parseInt(currentChatId) === chat['id'] ? 'background-color: rgba(161, 155, 155, 0.2)' : ''">
                  <el-col :span="6">
                    <el-avatar style="background-color: transparent" :size="45" :src="chat['receiverAvatar']"/>
                  </el-col>
                  <el-col :span="18" style="padding-right: 10px">
                    <el-row align="middle">
                      <el-text v-if="chat['notReadMessages'] === 0"
                               style="font-weight: bolder; color: black; font-size: 16px; padding-right: 10px"
                               truncated>
                        {{ chat["receiverNickname"] }}
                      </el-text>
                      <el-col :span="18" v-if="chat['notReadMessages'] !== 0">
                        <el-text style="font-weight: bolder; color: black; font-size: 16px; float: left" truncated>
                          {{ chat["receiverNickname"] }}
                        </el-text>
                      </el-col>
                      <el-col :span="6" v-if="chat['notReadMessages'] !== 0">
                        <el-text
                            style="flex: max-content; text-align: right; background-color: rgba(202,13,13,0.7); border-radius: 25px; color: white; padding: 2px 10px">
                          {{ chat['notReadMessages'] > 99 ? "99+" : chat['notReadMessages'] }}
                        </el-text>
                      </el-col>
                    </el-row>
                    <el-row align="middle" style="margin-top: 2px">
                      <el-col :span="18" style="text-align: start; height: 14px">
                        <el-text v-if="chat['latestChatMessage']" line-clamp="1">
                          {{ chat["latestChatMessage"] }}
                        </el-text>
                        <el-text v-else line-clamp="1" style="color: #a19b9b">
                          &#60; 无消息 &#62;
                        </el-text>
                      </el-col>
                      <el-col :span="6" style="text-align: center">
                        <el-text size="small">
                          {{ moment(chat["updateTime"], 'YYYY-MM-DD HH:mm:ss').fromNow() }}
                        </el-text>
                      </el-col>
                    </el-row>
                  </el-col>
                </el-row>
              </li>
            </ul>
          </el-scrollbar>
        </el-aside>
        <el-main style="padding: 0">
          <ChatMessageArea :currentChat="currentChat" ref="chatMessageAreaRef"
                           @onReadChat="resetNotReadMessagesByChatId"
                           @onSendMessage="refreshLatestChatMessageByChatId"/>
        </el-main>
      </el-container>
    </el-card>
  </el-row>
  <CreateChatDialog ref="createChatDialogRef" @openChat="onClickChatListItem"/>
</template>

<style scoped>
:deep() .el-card__body {
  padding: 0;
}

.chat-list-item {
  text-align: center;
  height: 75px;
  background-color: rgba(161, 155, 155, 0.05);
  border-bottom-style: solid;
  border-color: rgba(161, 155, 155, 0.2);
}

.chat-list-item:hover {
  cursor: pointer;
  background-color: rgba(161, 155, 155, 0.1);
}
</style>