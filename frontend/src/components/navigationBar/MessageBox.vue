<script setup>
import {Message} from "@element-plus/icons-vue";
import {ref} from "vue";
import {doReadMessage, getMessageList, getNotSeenMessageCount} from "@/request/message.js";
import moment from "moment";
import router from "@/router/index.js";

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
refreshNotSeenMessageCount();

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
getMoreMessage();

const onChangeSegmentedValue = async () => {
  isLastPage.value = false;
  nextPageNumber = 1;
  records.value = [];
  await getMoreMessage();
}

const readMessage = async (messageId, targetUrl, index) => {
  const data = await doReadMessage(messageId);
  if (data !== null) {
    records.value.splice(index, 1);
    notSeenMessageCount.value--;
    if (targetUrl !== "") {
      router.push(targetUrl).then(() => {});
    }
  }
}
</script>

<template>
  <el-popover placement="bottom" :width="300" trigger="click">
    <template #reference>
      <el-badge :value="notSeenMessageCount" :max="99" class="item" style="display: grid; place-items: center;"
                :offset="[0, 15]"
                :show-zero="false">
        <el-button>
          <el-icon>
            <Message/>
          </el-icon>
        </el-button>
      </el-badge>
    </template>
    <el-segmented v-model="segmentedValue" :options="segmentedOptions" block @change="onChangeSegmentedValue"/>
    <el-scrollbar max-height="400px" style="margin-top: 10px">
      <ul style="padding: 0; margin-top: 0"
          v-infinite-scroll="getMoreMessage"
          infinite-scroll-immediate="false">
        <li v-for="(record, index) in records" :key="record['id']" style="list-style: none;">
          <el-card shadow="hover" style="margin-bottom: 10px; cursor: pointer" @click="readMessage(record['id'], index)">
            <div style="font-weight: bold">{{ record["title"] }}</div>
            <div>{{ record["content"] }}</div>
            <div style="float: right; color: #a19b9b; margin-bottom: 5px">
              {{ moment(record["createTime"], "YYYY-MM-DD hh:mm:ss").fromNow() }}
            </div>
          </el-card>
        </li>
      </ul>
      <el-empty v-if="records.length === 0" description="暂无消息" :image-size="125"/>
      <div v-if="isLastPage" style="text-align: center; color: #a19b9b;">- - - 已经到底了 - - -</div>
      <div v-if="isLoading" style="text-align: center; color: #a19b9b;">- - - 加载中 - - -</div>
    </el-scrollbar>
  </el-popover>

</template>

<style scoped>
:deep() .el-card__body {
  padding: 10px;
}
</style>