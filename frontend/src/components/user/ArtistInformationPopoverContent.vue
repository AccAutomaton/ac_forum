<script setup>
const {uid, nickname, avatar} = defineProps({
  uid: Number,
  nickname: String,
  avatar: String,
});
</script>
<!--TODO-->
<template>
  <div>{{ uid }}</div>
  <div>{{ nickname }}</div>
  <el-avatar :src="avatar" />
</template>

<style scoped>

</style>