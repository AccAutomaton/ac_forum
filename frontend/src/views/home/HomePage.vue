<script setup>
import SignInCalendar from "@/views/home/SignInCalendar.vue";
import UserInformationCard from "@/views/home/UserInformationCard.vue";
import {ref} from "vue";
import SearchArea from "@/views/home/SearchArea.vue";
import RecommendArea from "@/views/home/RecommendArea.vue";

const userInformationCardRef = ref();
const increasePoints = (points) => {
  userInformationCardRef.value.increasePoints(points);
}
</script>

<template>
  <el-container>
    <el-main>
      <el-row align="middle" justify="center" style="height: 80vh; flex-direction: column">
        <SearchArea/>
        <RecommendArea/>
      </el-row>
    </el-main>
    <el-aside width="400px">
      <el-row align="middle" justify="center" style="height: 85vh; flex-direction: column">
        <UserInformationCard ref="userInformationCardRef"/>
        <SignInCalendar @increasePoints="increasePoints"/>
      </el-row>
    </el-aside>
  </el-container>
</template>

<style scoped>

</style>