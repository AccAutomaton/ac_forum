<script setup>
import {useRoute} from "vue-router";
import SelfLivingRoom from "@/views/artist/SelfLivingRoom.vue";
import store from "@/store/index.js";
import OthersLivingRoom from "@/views/artist/OthersLivingRoom.vue";

const artistId = useRoute().params.artistId;
</script>

<template>
  <SelfLivingRoom v-if="parseInt(artistId) === store.getters.getUid"/>
  <OthersLivingRoom v-else/>
</template>

<style scoped>

</style>