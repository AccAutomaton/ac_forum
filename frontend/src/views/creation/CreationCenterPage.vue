<script setup>
import {ChatLineSquare, DataAnalysis, Document} from "@element-plus/icons-vue";
</script>

<template>
  <el-container>
    <el-aside width="135px">
      <el-scrollbar height="85vh">
        <el-menu
            :default-active="$route.path"
            router
            popper-effect="light"
        >
          <el-menu-item index="/creation/dashboard" route="/creation/dashboard" style="padding-left: 37px">
            <el-icon>
              <DataAnalysis/>
            </el-icon>
            <span>看板</span>
          </el-menu-item>
          <el-menu-item index="/creation/topics" route="/creation/topics" style="padding-left: 37px">
            <el-icon>
              <ChatLineSquare/>
            </el-icon>
            <span>话题</span>
          </el-menu-item>
          <el-menu-item index="/creation/articles" route="/creation/articles" style="padding-left: 37px">
            <el-icon>
              <Document/>
            </el-icon>
            <span>文章</span>
          </el-menu-item>
        </el-menu>
      </el-scrollbar>
    </el-aside>
    <el-main>
      <router-view/>
    </el-main>
  </el-container>
</template>

<style scoped>

</style>