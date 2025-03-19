<script setup>
import {ref} from "vue";
import {Coin} from "@element-plus/icons-vue";
import {getCoins} from "@/request/coin.js";
import router from "@/router/index.js";
import {ElNotification} from "element-plus";
import {tippingArticle} from "@/request/article.js";

const dialogVisible = ref(false);
const balance = ref(0);
const volume = ref(0);
const {articleId} = defineProps({
  articleId: Number,
})

const showDialog = async () => {
  volume.value = 0;
  dialogVisible.value = true;
  const data = await getCoins();
  if (data !== null) {
    balance.value = data["coins"];
  }
}
defineExpose({
  showDialog,
})

const options = [{value: 88}, {value: 188}, {value: 688}, {value: 1888}, {value: 6888}];

const emit = defineEmits(['onTippingSuccess'])
const onClickTippingButton = async () => {
  if (volume.value < 1 || volume.value > 10000000) {
    ElNotification({title: '投币数超出限制', type: 'error'});
    return;
  }
  if (volume.value > balance.value) {
    ElNotification({title: '余额不足', type: 'warning'});
    return;
  }
  const data = await tippingArticle(articleId, volume.value);
  if (data !== null) {
    emit("onTippingSuccess", volume.value);
    dialogVisible.value = false;
    ElNotification({title: '投币成功', type: 'success'});
  }
}
</script>

<template>
  <el-dialog v-model="dialogVisible" title="&nbsp;&nbsp;投币" width="500" align-center destroy-on-close>
    <el-row style="margin-left: 10px">
      <el-text>余额 :</el-text>
      <el-text style="margin: 0 10px">
        <el-icon :size="14">
          <Coin/>
        </el-icon>
        {{ balance }}
      </el-text>
      <el-link type="primary" @click="router.push('/userCenter/purse/balance')">充值</el-link>
    </el-row>
    <el-segmented class="el-segmented" v-model="volume" :options="options" block style="margin: 10px 0 20px 0">
      <template #default="scope">
        <el-icon :size="14">
          <Coin/>
        </el-icon>
        <el-text style="color: black; margin-left: 5px; font-size: 18px">{{ scope.item.value }}</el-text>
      </template>
    </el-segmented>
    <el-input v-model="volume" placeholder="请输入投币数额" prefix-icon="Coin" type="number" clearable :min="1"
              :max="10000000">
      <template #prepend>自定义投币数</template>
    </el-input>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="onClickTippingButton">
          投币
          <el-icon style="margin: 0 3px 0 5px">
            <Coin/>
          </el-icon>
          {{ volume }}
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<style scoped>
.el-segmented {
  height: 60px;
  --el-segmented-item-selected-color: var(--el-text-color-primary);
  --el-segmented-item-selected-bg-color: rgba(0, 178, 255, 0.25);
  --el-border-radius-base: 10px;
}
</style>