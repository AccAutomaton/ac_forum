<script setup>
import {ref} from "vue";
import {getSignInInfo, signIn} from "@/request/signIn.js";
import {ElNotification} from "element-plus";

const signedInSet = ref(new Set());
const currentDate = new Date();
const showYear = ref(currentDate.getFullYear()), showMonth = ref(currentDate.getMonth() + 1);

const calendar = ref();
const selectDate = (val) => {
  if (!calendar.value) return
  calendar.value.selectDate(val)
}

const isSignedInToday = ref(false);

const emit = defineEmits(["increasePoints"]);

const getSignInData = async (year = currentDate.getFullYear(), month = currentDate.getMonth() + 1) => {
  const data = await getSignInInfo(year, month);
  if (data !== null) {
    signedInSet.value.clear();
    for (let i = 0; i < data.length; i++) {
      signedInSet.value.add(data[i]["time"]);
    }
  }
  if (year === currentDate.getFullYear() && month === currentDate.getMonth() + 1) {
    isSignedInToday.value = signedInSet.value.has(`${currentDate.getFullYear()}-${(currentDate.getMonth() + 1).toString().padStart(2, '0')}-${currentDate.getDate().toString().padStart(2, '0')}`);
  }
}
getSignInData();

const needBackground = (day) => {
  if (showYear.value === currentDate.getFullYear() && showMonth.value === currentDate.getMonth() + 1) {
    return currentDate.getMonth() + 1 === parseInt(day.split('-').slice(1, 2).join())
        && currentDate.getDate() >= parseInt(day.split('-').slice(2).join());
  } else if (showYear.value > currentDate.getFullYear()
      || (showYear.value === currentDate.getFullYear() && showMonth.value > currentDate.getMonth() + 1)) {
    return false;
  } else {
    return showMonth.value === parseInt(day.split('-').slice(1, 2).join());
  }
}

const onClickPrevMonthButton = () => {
  selectDate('prev-month');
  if (showMonth.value === 1) {
    showMonth.value = 12;
    showYear.value--;
  } else {
    showMonth.value--;
  }
  getSignInData(showYear.value, showMonth.value);
}

const onClickTodayButton = () => {
  selectDate('today');
  showYear.value = currentDate.getFullYear();
  showMonth.value = currentDate.getMonth() + 1;
  getSignInData();
}

const onClickNextMonthButton = () => {
  selectDate('next-month');
  if (showMonth.value === 12) {
    showMonth.value = 1;
    showYear.value++;
  } else {
    showMonth.value++;
  }
  getSignInData(showYear.value, showMonth.value);
}

const onClickSignInButton = async () => {
  const data = await signIn();
  if (data !== null) {
    signedInSet.value.add(`${currentDate.getFullYear()}-${(currentDate.getMonth() + 1).toString().padStart(2, '0')}-${currentDate.getDate().toString().padStart(2, '0')}`);
    isSignedInToday.value = true;
    if (data["points"] !== 0) {
      emit("increasePoints", data["points"]);
      ElNotification({title: "签到成功", type: "success", message: `经验值 + ${data["points"]}`});
    }
  }
}
</script>

<template>
  <el-row align="middle" justify="center">
    <el-calendar ref="calendar">
      <template #header="{ date }">
        <span>{{ date }}</span>
        <el-button-group>
          <el-button size="small" @click="onClickPrevMonthButton">
            上个月
          </el-button>
          <el-button size="small" @click="onClickTodayButton">今天</el-button>
          <el-button size="small" @click="onClickNextMonthButton">
            下个月
          </el-button>
        </el-button-group>
      </template>
      <template #date-cell="{ data }">
        <el-row align="middle" justify="center" style="width: 100%; height: 100%">
        <span v-if="needBackground(data.day)">
          <span v-if="signedInSet.has(data.day)" class="green-calendar-date">
            <el-row align="middle" justify="center" style="width: 100%; height: 100%">
              <el-tooltip content="已签到" placement="top" effect="light" offset="15">
                {{ data.day.split("-").slice(2).join() }}
              </el-tooltip>
            </el-row>
          </span>
          <span v-else class="grey-calendar-date">
            <el-row align="middle" justify="center" style="width: 100%; height: 100%">
              <el-tooltip content="未签到" placement="top" effect="light" offset="15">
                {{ data.day.split("-").slice(2).join() }}
              </el-tooltip>
            </el-row>
          </span>
        </span>
          <span v-else>
          {{ data.day.split("-").slice(2).join() }}
        </span>
        </el-row>
      </template>
    </el-calendar>
    <el-button v-if="isSignedInToday" type="info" size="default" plain round style="width: 200px; font-size: 14px">
      今日已签到
    </el-button>
    <el-button v-else type="primary" size="default" plain round style="width: 200px; font-size: 14px"
               @click="onClickSignInButton">签到
    </el-button>
  </el-row>
</template>

<style scoped>
:deep() .el-calendar-table .el-calendar-day {
  height: 50px;
}

.green-calendar-date {
  background-color: rgba(20, 209, 20, 0.3);
  padding: 5px 5px;
  border-radius: 50px;
  width: 20px;
  height: 20px;
  display: flex;
}

.grey-calendar-date {
  background-color: rgba(128, 128, 128, 0.1);
  padding: 4px 5px;
  border-radius: 20px;
  width: 20px;
  height: 20px;
  display: flex;
}
</style>