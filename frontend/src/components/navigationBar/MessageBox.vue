<script setup>
import {MessageBox} from "@element-plus/icons-vue";
import {ref, watch} from "vue";
import {doReadMessage, getMessageList, getNotSeenMessageCount} from "@/request/message.js";
import moment from "moment";
import router from "@/router/index.js";
import store from "@/store/index.js";
import {GetAuthorizationCode} from "@/request/index.js";
import {ElNotification} from "element-plus";

const segmentedValue = ref("notSeen")
const segmentedOptions = [
  {
    value: "notSeen",
    label: "未读消息"
  },
  {
    value: "seen",
    label: "已读消息"
  }
]
const notSeenMessageCount = ref(0);

const refreshNotSeenMessageCount = async () => {
  const data = await getNotSeenMessageCount();
  if (data !== null) {
    notSeenMessageCount.value = data;
  }
}

let nextPageNumber = 1, pageSize = 5;
const records = ref([]), isLoading = ref(false), isLastPage = ref(false);
const getMoreMessage = async () => {
  if (!isLastPage.value) {
    let seen = segmentedValue.value === "seen";
    isLoading.value = true;
    const data = await getMessageList(nextPageNumber, pageSize, seen);
    if (data !== null) {
      isLastPage.value = data["isLastPage"];
      if (!isLastPage.value) {
        nextPageNumber++;
      }
      records.value.push(...data["records"]);
    }
    isLoading.value = false;
  }
}

const onChangeSegmentedValue = async () => {
  isLastPage.value = false;
  nextPageNumber = 1;
  records.value = [];
  await getMoreMessage();
}

const readMessage = async (messageId, targetUrl, seen, index) => {
  if (seen === 0) {
    const data = await doReadMessage(messageId);
    if (data !== null) {
      records.value.splice(index, 1);
      notSeenMessageCount.value--;
    }
  }
  if (targetUrl !== "") {
    router.push(targetUrl).then(() => {
    });
  }
}

let webSocket;
const initWebSocket = () => {
  webSocket = new WebSocket(
      import.meta.env.VITE_WEBSOCKET_API_HOST + "/webSocket/message?Authorization=" + GetAuthorizationCode()
  );
  webSocket.onopen = () => {
  }
  webSocket.onmessage = (event) => {
    let data = JSON.parse(event.data);
    records.value.unshift(data);
    notSeenMessageCount.value++;
  }
  webSocket.onerror = () => {
    ElNotification({type: "error", title: "与消息推送服务器的连接发生错误", message: "请刷新后重试"});
  }
  webSocket.onclose = () => {
    ElNotification({type: "warning", title: "与消息推送服务器的连接已断开", message: "请刷新后重试"});
  };
}

if (store.getters.getIsLogin) {
  refreshNotSeenMessageCount();
  getMoreMessage();
  initWebSocket();
}
watch(() => store.getters.getIsLogin, (newValue) => {
  if (newValue) {
    refreshNotSeenMessageCount();
    getMoreMessage();
    initWebSocket();
  } else {
    notSeenMessageCount.value = 0;
    isLastPage.value = false;
    isLoading.value = false;
    nextPageNumber = 1;
    records.value = [];
  }
})
</script>

<template>
  <el-popover placement="bottom" :width="300" trigger="click">
    <template #reference>
      <el-badge :value="notSeenMessageCount" :max="99" class="item" style="display: grid; place-items: center;"
                :offset="[0, 15]"
                :show-zero="false">
        <el-button>
          <el-icon size="large"><MessageBox /></el-icon>
        </el-button>
      </el-badge>
    </template>
    <el-segmented v-model="segmentedValue" :options="segmentedOptions" block @change="onChangeSegmentedValue"/>
    <el-scrollbar max-height="400px" style="margin-top: 10px">
      <ul style="padding: 0; margin-top: 0"
          v-infinite-scroll="getMoreMessage"
          infinite-scroll-immediate="false">
        <li v-for="(record, index) in records" :key="record['id']" style="list-style: none;">
          <el-card shadow="hover" style="margin-bottom: 10px; cursor: pointer"
                   @click="readMessage(record['id'], record['targetUrl'], record['seen'], index)">
            <div style="font-weight: bold">{{ record["title"] }}</div>
            <div>{{ record["content"] }}</div>
            <div style="float: right; color: #a19b9b; margin-bottom: 5px">
              {{ moment(record["createTime"], "YYYY-MM-DD hh:mm:ss").fromNow() }}
            </div>
          </el-card>
        </li>
      </ul>
      <el-empty v-if="records.length === 0" description="暂无消息" :image-size="125"/>
      <div v-if="isLastPage" style="text-align: center; color: #a19b9b;">- - - 我也是有底线的 - - -</div>
      <div v-if="isLoading" style="text-align: center; color: #a19b9b;">- - - 加载中 - - -</div>
    </el-scrollbar>
  </el-popover>

</template>

<style scoped>
:deep() .el-card__body {
  padding: 10px;
}
</style>