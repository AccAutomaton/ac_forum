<script setup>

import {Document, Lock, Setting} from "@element-plus/icons-vue";
</script>

<template>
  <el-container>
    <el-aside>
      <el-scrollbar height="78vh">
        <el-menu
            :default-active="$route.path"
            router
            popper-effect="light"
        >
          <el-sub-menu index="/userCenter/account">
            <template #title>
              <el-icon><Setting /></el-icon>
              <span>账户管理</span>
            </template>
            <el-menu-item index="/userCenter/account/information" route="/userCenter/account/information">
              <el-icon><Document /></el-icon>
              <span>账户信息</span>
            </el-menu-item>
            <el-menu-item index="/userCenter/account/revisePassword" route="/userCenter/account/revisePassword">
              <el-icon><Lock /></el-icon>
              <span>修改密码</span>
            </el-menu-item>
          </el-sub-menu>
        </el-menu>
      </el-scrollbar>
    </el-aside>
    <router-view/>
  </el-container>
</template>

<style scoped>

</style>