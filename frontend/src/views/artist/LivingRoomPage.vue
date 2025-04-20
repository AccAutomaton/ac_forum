<script setup>
import {useRoute} from "vue-router";
import SelfLivingRoom from "@/views/artist/SelfLivingRoom.vue";
import store from "@/store/index.js";
import OthersLivingRoom from "@/views/artist/OthersLivingRoom.vue";
import {computed, inject, watch} from "vue";

const route = useRoute();

const artistId = computed(() => route.params.artistId);

watch(artistId, () => {
  inject("reloadPage");
})
</script>

<template>
  <SelfLivingRoom v-if="parseInt(artistId) === store.getters.getUid"/>
  <OthersLivingRoom v-else/>
</template>

<style scoped>

</style>