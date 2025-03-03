<script setup>
import {Histogram} from "@element-plus/icons-vue";
import {queryTopicList} from "@/request/topic.js";
import {ref} from "vue";
import {getObjectUrlOfPublicResources} from "@/request/cos.js";
import router from "@/router/index.js";

const getCardClass = (index) => {
  switch (index) {
    case 0:
      return "card-top-1";
    case 1:
      return "card-top-2";
    case 2:
      return "card-top-3";
    default:
      return "card-top-lower";
  }
}

const rankingList = ref([]);
const refreshRankingList = async () => {
  const data = await queryTopicList(1, 10, "visitsByDesc", "");
  if (data !== null) {
    rankingList.value = data["topicList"]["records"];
    for (let i = 0; i < rankingList.value.length; i++) {
      if (rankingList.value[i]["avatar"] !== "") {
        await getObjectUrlOfPublicResources(data["avatarPrefix"] + rankingList.value[i]["avatar"], (url) => {
          rankingList.value[i]["avatar"] = url;
        })
      }
    }
  }
}
refreshRankingList();
</script>

<template>
  <el-container>
    <el-header style="height: 30px;">
      <div style="margin: 5px auto; height: 30px; display: flex; align-items: center; justify-content: center;">
        <el-icon style="color: red; margin-right: 5px">
          <Histogram/>
        </el-icon>
        <span style="color: darkred; font-weight: bold">热度榜</span>
        <el-button text icon="refresh" style="padding: 0; margin-left: 10px; width: 20px" @click="refreshRankingList"/>
      </div>
    </el-header>
    <el-main style="padding-top: 10px">
      <el-scrollbar height="69.5vh" width="100%">
        <el-empty v-if="rankingList.length === 0" description="暂无话题" :image-size="125"/>
        <ul style="padding: 0; margin-top: 0">
          <li v-for="(record, index) in rankingList" :key="record['id']" style="list-style: none;">
            <el-card shadow="hover" @click="router.push('/topic/' + record['id'])"
                     style="margin-bottom: 10px; cursor: pointer; height: 80px; display: flex; align-items: center;
                     justify-content: center; border-radius: 15px" :class="getCardClass(index)">
              <el-row :gutter="10" style="width: 283px; margin-left: 15px">
                <el-col :span="2" class="col">
                  <span style="color: red; font-style: italic; font-weight: bolder">{{ index + 1 }}</span>
                </el-col>
                <el-col :span="6" class="col">
                  <el-avatar v-if="record['avatar'] === ''" shape="square" :size="50">
                    <span style="font-size: large">{{ record["title"].slice(0, 2) }}</span>
                  </el-avatar>
                  <el-avatar v-else shape="square" :size="50" :src="record['avatar']"
                             style="background-color: transparent"/>
                </el-col>
                <el-col :span="15">
                  <div>
                    <el-text style="color: black;" size="large" tag="b" line-clamp="2">{{ record["title"] }}</el-text>
                  </div>
                  <div>
                    <el-text line-clamp="1">{{ record["visits"] }} 次浏览</el-text>
                  </div>
                </el-col>
              </el-row>
            </el-card>
          </li>
        </ul>
      </el-scrollbar>
    </el-main>
  </el-container>
</template>

<style scoped>
.col {
  display: flex;
  text-align: center;
  align-items: center;
  justify-content: center
}

.card-top-1 {
  background-color: rgba(255, 215, 0, 0.2);
}

.card-top-2 {
  background-color: rgba(255, 215, 0, 0.15);
}

.card-top-3 {
  background-color: rgba(255, 215, 0, 0.1);
}

.card-top-lower {
  background-color: rgba(211, 211, 211, 0.1);
}
</style>