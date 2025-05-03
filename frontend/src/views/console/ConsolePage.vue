<script setup>

import {ChatDotSquare, Files, OfficeBuilding, Sunrise, User} from "@element-plus/icons-vue";
import store from "@/store/index.js";
</script>

<template>
  <el-container>
    <el-aside width="225px">
      <el-scrollbar height="85vh">
        <el-menu
            :default-active="$route.path"
            router
            popper-effect="light"
        >
          <el-menu-item v-if="store.getters.getUserType.index <= 2" index="/console/online" route="/console/online"
                        style="padding-left: 37px">
            <el-icon>
              <Sunrise/>
            </el-icon>
            <span>在线用户</span>
          </el-menu-item>
          <el-menu-item v-if="store.getters.getUserType.index <= 1" index="/console/user" route="/console/user"
                        style="padding-left: 37px">
            <el-icon>
              <User/>
            </el-icon>
            <span>用户注册</span>
          </el-menu-item>
          <el-menu-item v-if="store.getters.getUserType.index <= 2" index="/console/notification"
                        route="/console/notification" style="padding-left: 37px">
            <el-icon>
              <ChatDotSquare/>
            </el-icon>
            <span>消息通知管理</span>
          </el-menu-item>
          <el-menu-item v-if="store.getters.getUserType.index <= 1" index="/console/elasticsearch"
                        route="/console/elasticsearch" style="padding-left: 37px">
            <el-icon>
              <Files/>
            </el-icon>
            <span>Elastic Search 管理</span>
          </el-menu-item>
          <el-menu-item v-if="store.getters.getUserType.index === 0" index="/console/sql" route="/console/sql"
                        style="padding-left: 37px">
            <el-icon>
              <OfficeBuilding/>
            </el-icon>
            <span>MySQL 管理</span>
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