<script setup>
import {getPoint, getPointRecordById, getPointRecordList} from "@/request/normal/point.js";
import {ref} from "vue";
import {View} from "@element-plus/icons-vue";

const levels = [
  {
    value: "黑铁",
    maxPoint: 1024,
    width: "10vw",
    disableColor: "rgba(37,37,37,0.1)",
    disableFontColor: "rgba(37,37,37,0.3)",
    enableColor: "rgba(37,37,37,1)",
  },
  {
    value: "青铜",
    maxPoint: 2048,
    width: "12vw",
    disableColor: "rgba(80,56,7,0.1)",
    disableFontColor: "rgba(80,56,7,0.3)",
    enableColor: "rgba(80,56,7,1)",
  },
  {
    value: "白银",
    maxPoint: 4096,
    width: "15vw",
    disableColor: "rgba(142,140,138,0.1)",
    disableFontColor: "rgba(142,140,138,0.5)",
    enableColor: "rgba(142,140,138,1)",
  },
  {
    value: "黄金",
    maxPoint: 8192,
    width: "19vw",
    disableColor: "rgba(221,140,32,0.1)",
    disableFontColor: "rgba(221,140,32,0.4)",
    enableColor: "rgba(221,140,32,1)",
  },
  {
    value: "铂金",
    maxPoint: 16384,
    width: "24vw",
    disableColor: "rgba(47,150,170,0.1)",
    disableFontColor: "rgba(47,150,170,0.3)",
    enableColor: "rgba(47,150,170,1)",
  },
  {
    value: "钻石",
    maxPoint: 32768,
    width: "30vw",
    disableColor: "rgba(191,109,218,0.1)",
    disableFontColor: "rgba(191,109,218,0.4)",
    enableColor: "rgba(191,109,218,1)",
  },
  {
    value: "超凡",
    maxPoint: 65536,
    width: "37vw",
    disableColor: "rgba(33,133,78,0.1)",
    disableFontColor: "rgba(33,133,78,0.3)",
    enableColor: "rgba(33,133,78,1)",
  },
  {
    value: "神话",
    maxPoint: 131072,
    width: "45vw",
    disableColor: "rgba(157,29,65,0.1)",
    disableFontColor: "rgba(157,29,65,0.3)",
    enableColor: "rgba(157,29,65,1)",
  },
  {
    value: "辐能",
    maxPoint: 999999999,
    width: "54vw",
    disableColor: "rgba(242,246,6,0.1)",
    disableFontColor: "rgba(169,174,11,0.4)",
    enableColor: "rgba(169,174,11,1)",
  },
];

const statusTagType = (index) => {
  switch (index) {
    case 0:
      return "success";
    case 1:
      return "danger";
    default:
      return "info";
  }
}

const points = ref(0), levelIndex = ref(0);
const getUserLevel = async () => {
  const data = await getPoint();
  if (data !== null) {
    points.value = data["points"];
    levelIndex.value = data["userLevel"]["index"];
  }
}
getUserLevel();

const pointRecordList = ref([]);
const currentPageNumber = ref(1), currentPageSize = ref(10), pages = ref(0);
const pointRecordDetailDialogVisible = ref(false);
const getListData = async () => {
  const data = await getPointRecordList(currentPageNumber.value, currentPageSize.value);
  if (data !== null) {
    pointRecordList.value = data["records"];
    currentPageNumber.value = data["pageNumber"];
    currentPageSize.value = data["pageSize"];
    pages.value = data["pages"];
  }
}
getListData();

const selectedPointRecord = ref({});
const onClickViewDetailIcon = async (pointRecordId) => {
  const data = await getPointRecordById(pointRecordId);
  if (data !== null) {
    selectedPointRecord.value = data;
    pointRecordDetailDialogVisible.value = true;
  }
}
</script>

<template>
  <el-container>
    <el-header>
      <h2 style="margin-top: 10px">账户等级</h2>
    </el-header>
    <el-divider style="margin: 0"/>
    <el-main style="padding-bottom: 0">
      <el-scrollbar style="height: 75.5vh">
        <div v-for="(level, index) in levels" :key="index" :style="'width: ' + level.width">
          <el-progress v-if="levelIndex === index" :percentage="(points / level.maxPoint) * 100"
                       style="margin-bottom: 10px" :color="level.enableColor"
                       stroke-width="16">
            <el-tooltip :content="'经验值: ' + points + ' / ' + level.maxPoint" placement="bottom" effect="light"
                        offset="32">
              <div>
                <span
                    :style="'font-weight: bolder; margin-right: 10px; cursor: default; float: left; margin-right: 1px; color: ' + level.enableColor">
                  {{ level.value }}
                </span>
                <el-icon :style="'float: right; color: ' + level.enableColor" size="14">
                  <icon-question-circle/>
                </el-icon>
              </div>
            </el-tooltip>
          </el-progress>
          <el-progress v-else :percentage="100" style="margin-bottom: 10px"
                       :color="level.disableColor" stroke-width="8">
            <span :style="'font-weight: bolder; cursor: default; color: ' + level.disableFontColor">
              {{ level.value }}
            </span>
          </el-progress>
        </div>
        <el-table :data="pointRecordList" style="width: 100%" stripe height="42vh">
          <el-table-column prop="updateTime" label="时间" width="175" align="center" show-overflow-tooltip/>
          <el-table-column label="经验变化" width="115" align="center" show-overflow-tooltip>
            <template #default="scope">
              <span style="color: green" v-if="scope.row['type']['index'] >= 1">
                + {{ scope.row['pointVolume'] }}
              </span>
              <span v-else-if="scope.row['type']['index'] === 0">
                {{ scope.row['pointVolume'] }}
              </span>
              <span style="color: darkred;" v-else>
                - {{ scope.row['pointVolume'] }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="pointBalance" label="总经验值" width="135" align="center" show-overflow-tooltip/>
          <el-table-column label="类型" width="115" align="center" show-overflow-tooltip>
            <template #default="scope">
              {{ scope.row["type"]["value"] }}
            </template>
          </el-table-column>
          <el-table-column label="状态" width="115" align="center" show-overflow-tooltip>
            <template #default="scope">
              <el-tag :type="statusTagType(scope.row['status']['index'])" effect="plain">
                {{ scope.row["status"]["value"] }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="project" label="项目" width="135" align="center" show-overflow-tooltip/>
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
              @change="getListData"
          />
        </div>
      </el-scrollbar>
    </el-main>
  </el-container>

  <el-dialog v-model="pointRecordDetailDialogVisible" title="经验变动详情" width="700" align-center destroy-on-close>
    <el-row :gutter="20">
      <el-col :span="12">
        <div class="dialog-div">序号</div>
      </el-col>
      <el-col :span="12">
        <div class="dialog-div">项目</div>
      </el-col>
    </el-row>
    <el-row :gutter="20">
      <el-col :span="12">
        <el-input class="dialog-input" v-model="selectedPointRecord['id']" readonly/>
      </el-col>
      <el-col :span="12">
        <el-input class="dialog-input" v-model="selectedPointRecord['project']" readonly/>
      </el-col>
    </el-row>
    <el-row :gutter="20">
      <el-col :span="12">
        <div class="dialog-div">经验变化</div>
      </el-col>
      <el-col :span="12">
        <div class="dialog-div">总经验值</div>
      </el-col>
    </el-row>
    <el-row :gutter="20">
      <el-col :span="12">
        <el-input class="dialog-input" v-model="selectedPointRecord['pointVolume']" readonly/>
      </el-col>
      <el-col :span="12">
        <el-input class="dialog-input" v-model="selectedPointRecord['pointBalance']" placeholder="<无>" readonly/>
      </el-col>
    </el-row>
    <el-row :gutter="20">
      <el-col :span="12">
        <div class="dialog-div">类型</div>
      </el-col>
      <el-col :span="12">
        <div class="dialog-div">状态</div>
      </el-col>
    </el-row>
    <el-row :gutter="20">
      <el-col :span="12">
        <el-input class="dialog-input" v-model="selectedPointRecord['type']['value']" readonly/>
      </el-col>
      <el-col :span="12">
        <el-input class="dialog-input" v-model="selectedPointRecord['status']['value']" readonly/>
      </el-col>
    </el-row>
    <el-row :gutter="20">
      <el-col :span="12">
        <div class="dialog-div">创建时间</div>
      </el-col>
      <el-col :span="12">
        <div class="dialog-div">更新时间</div>
      </el-col>
    </el-row>
    <el-row :gutter="20">
      <el-col :span="12">
        <el-input class="dialog-input" v-model="selectedPointRecord['createTime']" readonly/>
      </el-col>
      <el-col :span="12">
        <el-input class="dialog-input" v-model="selectedPointRecord['updateTime']" readonly/>
      </el-col>
    </el-row>
    <div class="dialog-div">备注</div>
    <el-input class="dialog-input" type="textarea" v-model="selectedPointRecord['comment']" readonly rows="4"
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
</style>