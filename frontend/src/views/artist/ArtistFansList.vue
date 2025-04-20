<script setup>
import {ref} from "vue";
import {useRoute} from "vue-router";
import {getArtistFansListByUid} from "@/request/artist.js";
import {getObjectUrlOfPublicResources} from "@/request/cos.js";
import router from "@/router/index.js";

const currentPageNumber = ref(1), currentPageSize = ref(12), pages = ref(0);
const fansList = ref([]);

const artistId = useRoute().params.artistId;

const getFansList = async () => {
  const data = await getArtistFansListByUid(artistId, currentPageNumber.value, currentPageSize.value);
  if (data !== null) {
    fansList.value = data["records"];
    pages.value = data["pages"];
    currentPageNumber.value = data["pageNumber"];
    currentPageSize.value = data["pageSize"];
    for (let i = 0; i < fansList.value.length; i++) {
      if (fansList.value[i]["avatar"] !== "") {
        await getObjectUrlOfPublicResources(fansList.value[i]["avatarPrefix"] + fansList.value[i]["avatar"], (url) => {
          fansList.value[i]["avatar"] = url;
        });
      }
    }
  }
}
getFansList();
</script>

<template>
  <template v-if="fansList.length === 0">
    <el-empty description="无关注" :image-size="150"/>
  </template>
  <template v-else>
    <el-scrollbar height="58.5vh" width="100%">
      <el-row :gutter="15" style="width: 100%; margin-top: 10px">
        <el-col :span="8" v-for="follow in fansList" :key="follow['id']">
          <el-card style="padding: 10px 5px; font-size: 14px; border-radius: 15px; cursor: pointer" shadow="hover"
                   @click="router.push(`/artist/${follow['uid']}/livingRoom`)">
            <el-row el-row align="middle" justify="center" style="text-align: center; height: 60px" :gutter="20">
              <el-col :span="6">
                <el-avatar style="background-color: transparent; float: right" :size="50" :src="follow['avatar']"/>
              </el-col>
              <el-col :span="18">
                <el-row>
                  <el-text style="font-weight: bolder; font-size: 18px; margin-bottom: 5px" line-clamp="1">
                    {{ follow["nickname"] }}
                  </el-text>
                </el-row>
                <el-row style="text-align: left">
                  <el-col :span="12">
                    <el-text style="font-weight: bolder">
                      {{
                        Intl.NumberFormat('en', {
                          notation: "compact",
                          maximumFractionDigits: 1
                        }).format(follow['follows'])
                      }}
                    </el-text>
                    <el-text style="color: #878484">
                      关注
                    </el-text>
                  </el-col>
                  <el-col :span="12">
                    <el-text style="font-weight: bolder">
                      {{
                        Intl.NumberFormat('en', {
                          notation: "compact",
                          maximumFractionDigits: 1
                        }).format(follow["fans"])
                      }}
                    </el-text>
                    <el-text style="color: #878484">
                      粉丝
                    </el-text>
                  </el-col>
                </el-row>
              </el-col>
            </el-row>
          </el-card>
        </el-col>
      </el-row>
    </el-scrollbar>
    <div
        style="width: 100%; display: flex; align-items: center; justify-content: center; margin-top: 5px; margin-bottom: 5px">
      <el-pagination
          @click.stop
          v-model:current-page="currentPageNumber"
          v-model:page-size="currentPageSize"
          :page-sizes="[6, 9, 12, 15, 18]"
          size=large
          layout="prev, pager, next, ->, sizes"
          :page-count="pages"
          @change="getFansList"
      />
    </div>
  </template>
</template>

<style scoped>
:deep() .el-card__body {
  padding: 0;
}
</style>