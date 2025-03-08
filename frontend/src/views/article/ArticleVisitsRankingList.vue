<script setup>
import {ref} from "vue";
import {queryArticleList} from "@/request/article.js";
import router from "@/router/index.js";
import {Histogram} from "@element-plus/icons-vue";

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
  const data = await queryArticleList(0, 10, 4, "");
  if (data !== null) {
    rankingList.value = data["articleList"]["records"];
  }
}
refreshRankingList();
</script>

<template>
  <el-container>
    <el-header style="height: 30px;">
      <div style="margin: 10px auto; height: 20px; display: flex; align-items: center; justify-content: center;">
        <el-icon style="color: red; margin-right: 5px">
          <Histogram/>
        </el-icon>
        <span style="color: darkred; font-weight: bold">热度榜</span>
        <el-button text icon="refresh" style="padding: 0; margin-left: 10px; width: 20px" @click="refreshRankingList"/>
      </div>
    </el-header>
    <el-main style="padding: 10px 5px 10px 5px">
      <el-scrollbar height="72vh" width="100%">
        <el-empty v-if="rankingList.length === 0" description="暂无文章" :image-size="125"/>
        <ul style="padding: 0 5px; margin-top: 0">
          <li v-for="(record, index) in rankingList" :key="record['id']" style="list-style: none;">
            <el-card shadow="hover" @click="router.push('/article/' + record['id'])"
                     style="margin-bottom: 10px; cursor: pointer; height: 80px; display: flex; align-items: center;
                     justify-content: center; border-radius: 15px" :class="getCardClass(index)">
              <el-row :gutter="10" style="width: 283px; margin-left: 15px">
                <el-col :span="2" class="col">
                  <span style="color: red; font-style: italic; font-weight: bolder">{{ index + 1 }}</span>
                </el-col>
                <el-col :span="1"/>
                <el-col :span="20">
                  <el-row>
                    <el-text style="color: black;" size="large" tag="b" line-clamp="2">{{ record["title"] }}</el-text>
                  </el-row>
                  <el-row>
                    <el-col :span="8">
                      <el-text>
                        <el-icon>
                          <icon-eye/>
                        </el-icon>
                        {{
                          Intl.NumberFormat('en', {
                            notation: "compact",
                            maximumFractionDigits: 1
                          }).format(record["visits"])
                        }}
                      </el-text>
                    </el-col>
                    <el-col :span="8">
                      <el-text>
                        <el-icon>
                          <icon-thumb-up/>
                        </el-icon>
                        {{
                          Intl.NumberFormat('en', {
                            notation: "compact",
                            maximumFractionDigits: 1
                          }).format(record["thumbsUp"])
                        }}
                      </el-text>
                    </el-col>
                    <el-col :span="8">
                      <el-text>
                        <el-icon>
                          <icon-star/>
                        </el-icon>
                        {{
                          Intl.NumberFormat('en', {
                            notation: "compact",
                            maximumFractionDigits: 1
                          }).format(record["collections"])
                        }}
                      </el-text>
                    </el-col>
                  </el-row>
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