<script setup>
import {ref} from "vue";
import {getUidAndNicknameList} from "@/request/normal/user.js";
import {ElNotification} from "element-plus";
import {createChat} from "@/request/normal/chat.js";

const isNumeric = (value) => {
  return !isNaN(parseInt(value)) && !isNaN(value);
}

const emit = defineEmits(["openChat"])

const createChatDialogVisible = ref(false);
const searchType = ref(0);
const userListLoadingStatus = ref(false), options = ref([]), targetUid = ref();

const searchUser = async (keyword) => {
  if (keyword.trim() === "") {
    return;
  }
  userListLoadingStatus.value = true;
  let data;
  if (searchType.value === 0) {
    data = await getUidAndNicknameList(null, keyword);
  } else if (searchType.value === 1) {
    if (isNumeric(keyword)) {
      data = await getUidAndNicknameList(keyword, "");
    } else {
      options.value.length = 0;
      userListLoadingStatus.value = false;
    }
  }
  if (data !== null) {
    options.value.length = 0;
    for (let user of data) {
      options.value.push({
        value: user["uid"],
        label: user["nickname"],
      })
    }
  }
  userListLoadingStatus.value = false;
}

const showDialog = () => {
  searchType.value = 0;
  userListLoadingStatus.value = false;
  options.value.length = 0;
  targetUid.value = null;
  createChatDialogVisible.value = true;
}

defineExpose({
  showDialog,
})

const onClickConfirmCreateChatButton = async () => {
  if (targetUid.value) {
    const data = await createChat(targetUid.value);
    if (data !== null) {
      emit("openChat", data["chatId"]);
      createChatDialogVisible.value = false;
    }
  } else {
    ElNotification({title: "请选择用户", type: "warning"});
  }
}
</script>

<template>
  <el-dialog v-model="createChatDialogVisible" title="发起聊天" width="375" align-center destroy-on-close>
    <el-radio-group v-model="searchType" style="margin-bottom: 10px; margin-left: 5px">
      <el-radio :value="0">按昵称查找</el-radio>
      <el-radio :value="1">按UID查找</el-radio>
    </el-radio-group>
    <el-select-v2 v-model="targetUid" filterable remote :remote-method="searchUser" clearable
                  :loading="userListLoadingStatus" placeholder="搜索用户..." :options="options"
                  :reserve-keyword="false" no-data-text="没有匹配的用户">
      <template #label="{ label, value }">
        <span>{{ label }}</span>
        <span style="color: var(--el-text-color-secondary); font-size: 12px; float: right">#{{ value }}</span>
      </template>
      <template #default="{ item }">
        <span>{{ item.label }}</span>
        <span style="color: var(--el-text-color-secondary); font-size: 12px; float: right">#{{
            item.value
          }}</span>
      </template>
    </el-select-v2>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="createChatDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="onClickConfirmCreateChatButton">确认</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<style scoped>

</style>