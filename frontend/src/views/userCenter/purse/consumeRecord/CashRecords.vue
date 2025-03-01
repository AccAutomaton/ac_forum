<script setup>
import {nextTick, ref} from "vue";
import {cancelRechargeById, continueRechargeById, getRechargeById, getRechargeList} from "@/request/recharge.js";
import {Close, Money, View} from "@element-plus/icons-vue";
import {ElMessageBox, ElNotification} from "element-plus";

const tableData = ref([]);
const currentPageNumber = ref(1), currentPageSize = ref(10), pages = ref(0);
const clickedChargeId = ref(0), chargeDetailDialogVisible = ref(false);

const channelTagType = (index) => {
  switch (index) {
    case 2:
      return "primary";
    case 3:
      return "success";
    default:
      return "info";
  }
}

const statusTagType = (index) => {
  switch (index) {
    case -1:
      return "info"
    case 0:
      return "success";
    case 1:
      return "danger";
    case 2:
      return "info";
    case 3:
      return "warning";
    default:
      return "primary";
  }
}

const getData = async () => {
  const data = await getRechargeList(currentPageNumber.value, currentPageSize.value);
  if (data !== null) {
    tableData.value = data["records"];
    currentPageNumber.value = data["pageNumber"];
    currentPageSize.value = data["pageSize"];
    pages.value = data["pages"];
  }
}
getData();

const selectedRecharge = ref({});
const onClickViewDetailIcon = async (rechargeId) => {
  clickedChargeId.value = rechargeId;
  const data = await getRechargeById(rechargeId);
  if (data !== null) {
    selectedRecharge.value = data;
    selectedRecharge.value["amount"] /= 100
    chargeDetailDialogVisible.value = true;
  }
}

const alipayForm = ref(""), alipayPage = ref();
const onClickContinuePayingIcon = async (rechargeId) => {
  const data = await continueRechargeById(rechargeId);
  if (data !== null) {
    alipayForm.value = data["pageRedirectionData"];
    await nextTick(() => {
      alipayPage.value.children[0].submit();
      setTimeout(() => {
      }, 500)
    });
  }
}

const onClickCancelPayingIcon = async (rechargeId) => {
  ElMessageBox.confirm(
      '是否要取消该订单?',
      '取消订单',
      {
        confirmButtonText: '确认',
        cancelButtonText: '关闭',
        type: 'warning',
      }
  ).then(async () => {
    const data = await cancelRechargeById(rechargeId);
    if (data !== null) {
      await getData();
      ElNotification({type: "success", title: "订单取消成功"});
    }
  })
}
</script>

<template>
  <el-table :data="tableData" style="width: 100%" stripe height="54.5vh">
    <el-table-column prop="updateTime" label="交易时间" width="175" align="center" show-overflow-tooltip/>
    <el-table-column prop="subject" label="交易项目" width="115" align="center" show-overflow-tooltip/>
    <el-table-column label="交易金额" width="125" header-align="center" show-overflow-tooltip>
      <template #default="scope">
        <span style="padding-left: 20px">
          {{ '\u00A5' }} {{ scope.row["amount"] / 100 }}
        </span>
      </template>
    </el-table-column>
    <el-table-column label="交易渠道" width="115" align="center" show-overflow-tooltip>
      <template #default="scope">
        <el-tag :type="channelTagType(scope.row['channel']['index'])" effect="plain">
          {{ scope.row["channel"]["value"] }}
        </el-tag>
      </template>
    </el-table-column>
    <el-table-column label="交易类型" width="115" align="center" show-overflow-tooltip>
      <template #default="scope">
        {{ scope.row["type"]["value"] }}
      </template>
    </el-table-column>
    <el-table-column label="交易状态" width="115" align="center" show-overflow-tooltip>
      <template #default="scope">
        <el-tag :type="statusTagType(scope.row['status']['index'])" effect="plain">
          {{ scope.row["status"]["value"] }}
        </el-tag>
      </template>
    </el-table-column>
    <el-table-column label="备注" align="center" show-overflow-tooltip>
      <template #default="scope">
        <span v-if="scope.row['comment'] !== ''">
          {{ scope.row["comment"] }}
        </span>
        <span v-else style="color: rgba(161,155,155,0.5)">
          &#60;无备注&#62;
        </span>
      </template>
    </el-table-column>
    <el-table-column label="操作" fixed="right" width="115" align="center" show-overflow-tooltip>
      <template #default="scope">
        <el-tooltip content="查看详情" placement="top" effect="light">
          <el-icon size="16" class="view-icon-button" @click="onClickViewDetailIcon(scope.row['id'])">
            <View/>
          </el-icon>
        </el-tooltip>
        <span v-if="scope.row['status']['index'] === -1">
          <el-tooltip content="继续支付" placement="top" effect="light">
          <el-icon style="margin-left: 5px; margin-right: 4px" size="16" class="view-icon-button"
                   @click="onClickContinuePayingIcon(scope.row['id'])">
            <Money/>
          </el-icon>
        </el-tooltip>
        <el-tooltip content="取消订单" placement="top" effect="light">
          <el-icon size="16" class="view-icon-button" @click="onClickCancelPayingIcon(scope.row['id'])">
            <Close/>
          </el-icon>
        </el-tooltip>
        </span>
      </template>
    </el-table-column>
  </el-table>
  <div style="width: 100%; display: flex; align-items: center; justify-content: center; margin-top: 10px">
    <el-pagination
        v-model:current-page="currentPageNumber"
        v-model:page-size="currentPageSize"
        :page-sizes="[10, 20, 30, 40]"
        size=large
        layout="prev, pager, next, ->, sizes"
        :page-count="pages"
        @change="getData"
    />
  </div>

  <el-dialog v-model="chargeDetailDialogVisible" title="交易详情" width="700" align-center destroy-on-close>
    <el-row :gutter="20">
      <el-col :span="12">
        <div class="dialog-div">交易序号</div>
      </el-col>
      <el-col :span="12">
        <div class="dialog-div">交易项目</div>
      </el-col>
    </el-row>
    <el-row :gutter="20">
      <el-col :span="12">
        <el-input class="dialog-input" v-model="selectedRecharge['id']" readonly/>
      </el-col>
      <el-col :span="12">
        <el-input class="dialog-input" v-model="selectedRecharge['subject']" readonly/>
      </el-col>
    </el-row>
    <el-row :gutter="20">
      <el-col :span="12">
        <div class="dialog-div">交易编号</div>
      </el-col>
      <el-col :span="12">
        <div class="dialog-div">订单号</div>
      </el-col>
    </el-row>
    <el-row :gutter="20">
      <el-col :span="12">
        <el-input class="dialog-input" v-model="selectedRecharge['uuid']" readonly/>
      </el-col>
      <el-col :span="12">
        <el-input class="dialog-input" v-model="selectedRecharge['tradeId']" placeholder="<无>" readonly/>
      </el-col>
    </el-row>
    <el-row :gutter="20">
      <el-col :span="12">
        <div class="dialog-div">交易渠道</div>
      </el-col>
      <el-col :span="12">
        <div class="dialog-div">交易状态</div>
      </el-col>
    </el-row>
    <el-row :gutter="20">
      <el-col :span="12">
        <el-input class="dialog-input" v-model="selectedRecharge['channel']['value']" readonly/>
      </el-col>
      <el-col :span="12">
        <el-input class="dialog-input" v-model="selectedRecharge['status']['value']" readonly/>
      </el-col>
    </el-row>
    <el-row :gutter="20">
      <el-col :span="12">
        <div class="dialog-div">交易类型</div>
      </el-col>
      <el-col :span="12">
        <div class="dialog-div">交易金额</div>
      </el-col>
    </el-row>
    <el-row :gutter="20">
      <el-col :span="12">
        <el-input class="dialog-input" v-model="selectedRecharge['type']['value']" readonly/>
      </el-col>
      <el-col :span="12">
        <el-input class="dialog-input" v-model="selectedRecharge['amount']" readonly>
          <template #prefix>{{ '\u00A5' }}&nbsp;</template>
        </el-input>
      </el-col>
    </el-row>
    <el-row :gutter="20">
      <el-col :span="12">
        <div class="dialog-div">交易创建时间</div>
      </el-col>
      <el-col :span="12">
        <div class="dialog-div">交易更新时间</div>
      </el-col>
    </el-row>
    <el-row :gutter="20">
      <el-col :span="12">
        <el-input class="dialog-input" v-model="selectedRecharge['createTime']" readonly/>
      </el-col>
      <el-col :span="12">
        <el-input class="dialog-input" v-model="selectedRecharge['updateTime']" readonly/>
      </el-col>
    </el-row>
    <div class="dialog-div">备注</div>
    <el-input class="dialog-input" type="textarea" v-model="selectedRecharge['comment']" readonly rows="4" resize="none"
              placeholder="<无备注>"/>
  </el-dialog>
  <div ref="alipayPage" v-html="alipayForm"/>
</template>

<style scoped>
.view-icon-button:hover {
  cursor: pointer;
  color: #409eff;
}

.dialog-div {
  margin-left: 10px;
  margin-bottom: 5px;
}

.dialog-input {
  margin-bottom: 15px;
}
</style>