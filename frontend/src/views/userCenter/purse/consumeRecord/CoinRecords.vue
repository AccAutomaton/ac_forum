<script setup>
import {Coin, View} from "@element-plus/icons-vue";
import {ref} from "vue";
import {getCoinRecordById, getCoinRecordList} from "@/request/coin.js";

const tableData = ref([]);
const currentPageNumber = ref(1), currentPageSize = ref(20), pages = ref(0);
const coinRecordDetailDialogVisible = ref(false);

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

const coinVolumeClass = (index) => {
  switch (index) {
    case -2:
    case -1:
    case 1:
    case 3:
    case 5:
      return "coin-volume-income";
    case 2:
    case 4:
    case 6:
      return "coin-volume-outcome";
    default:
      return "";
  }
}

const getData = async () => {
  const data = await getCoinRecordList(currentPageNumber.value, currentPageSize.value);
  if (data !== null) {
    tableData.value = data["records"];
    currentPageNumber.value = data["pageNumber"];
    currentPageSize.value = data["pageSize"];
    pages.value = data["pages"];
  }
}
getData();

const selectedCoinRecord = ref({});
const onClickViewDetailIcon = async (coinRecordId) => {
  const data = await getCoinRecordById(coinRecordId);
  if (data !== null) {
    selectedCoinRecord.value = data;
    coinRecordDetailDialogVisible.value = true;
  }
}
</script>

<template>
  <el-table :data="tableData" style="width: 100%" stripe height="61vh">
    <el-table-column prop="updateTime" label="交易时间" width="175" align="center" show-overflow-tooltip/>
    <el-table-column prop="project" label="交易项目" width="115" align="center" show-overflow-tooltip/>
    <el-table-column label="交易额" width="125" header-align="center" show-overflow-tooltip>
      <template #default="scope">
        <span :class="coinVolumeClass(scope.row['type']['index'])" style="padding-left: 20px; align-items: center">
          <el-icon><Coin/></el-icon>
          <span v-if="coinVolumeClass(scope.row['type']['index']) === 'coin-volume-income'">&nbsp;+{{
              scope.row["coinVolume"]
            }}</span>
          <span v-else-if="coinVolumeClass(scope.row['type']['index']) === 'coin-volume-outcome'">&nbsp;-{{
              scope.row["coinVolume"]
            }}</span>
          <span v-else>&nbsp;{{ scope.row["coinVolume"] }}</span>
        </span>
      </template>
    </el-table-column>
    <el-table-column label="余额" width="115" header-align="center" show-overflow-tooltip>
      <template #default="scope">
        <span style="padding-left: 15px; align-items: center">
          <el-icon><Coin/></el-icon>
          <span>&nbsp;{{ scope.row["coinBalance"] }}</span>
        </span>
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

  <el-dialog v-model="coinRecordDetailDialogVisible" title="交易详情" width="700" align-center destroy-on-close>
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
        <el-input class="dialog-input" v-model="selectedCoinRecord['id']" readonly/>
      </el-col>
      <el-col :span="12">
        <el-input class="dialog-input" v-model="selectedCoinRecord['project']" readonly/>
      </el-col>
    </el-row>
    <el-row :gutter="20">
      <el-col :span="12">
        <div class="dialog-div">交易额</div>
      </el-col>
      <el-col :span="12">
        <div class="dialog-div">余额</div>
      </el-col>
    </el-row>
    <el-row :gutter="20">
      <el-col :span="12">
        <el-input class="dialog-input" v-model="selectedCoinRecord['coinVolume']" readonly>
          <template #prefix>
            <el-icon>
              <Coin/>
            </el-icon>
          </template>
        </el-input>
      </el-col>
      <el-col :span="12">
        <el-input class="dialog-input" v-model="selectedCoinRecord['coinBalance']" placeholder="<无>" readonly>
          <template #prefix>
            <el-icon>
              <Coin/>
            </el-icon>
          </template>
        </el-input>
      </el-col>
    </el-row>
    <el-row :gutter="20">
      <el-col :span="12">
        <div class="dialog-div">交易类型</div>
      </el-col>
      <el-col :span="12">
        <div class="dialog-div">交易状态</div>
      </el-col>
    </el-row>
    <el-row :gutter="20">
      <el-col :span="12">
        <el-input class="dialog-input" v-model="selectedCoinRecord['type']['value']" readonly/>
      </el-col>
      <el-col :span="12">
        <el-input class="dialog-input" v-model="selectedCoinRecord['status']['value']" readonly/>
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
        <el-input class="dialog-input" v-model="selectedCoinRecord['createTime']" readonly/>
      </el-col>
      <el-col :span="12">
        <el-input class="dialog-input" v-model="selectedCoinRecord['updateTime']" readonly/>
      </el-col>
    </el-row>
    <div class="dialog-div">备注</div>
    <el-input class="dialog-input" type="textarea" v-model="selectedCoinRecord['comment']" readonly rows="4"
              resize="none"
              placeholder="<无备注>"/>
  </el-dialog>
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

.coin-volume-income {
  color: green;
}

.coin-volume-outcome {
  color: darkred;
}
</style>