<script setup>
import {HomeFilled} from "@element-plus/icons-vue";
import UserSubMenu from "@/components/navigationBar/UserSubMenu.vue";
import MessageBox from "@/components/navigationBar/MessageBox.vue";
</script>

<template>
  <el-container>
    <el-header>
      <el-menu
          :default-active="$route.path"
          mode="horizontal"
          :ellipsis="false"
          router
          popper-effect="light"
          close-on-click-outside
      >
        <el-menu-item style="pointer-events: none">
          <el-container>
            <img src="@/assets/favicon.png" alt="" style="height: 3.5rem"/>
            <div style="font-size: x-large; font-weight: bolder; margin-left: 5px">AC论坛</div>
          </el-container>
        </el-menu-item>
        <el-menu-item index="/home">
          <el-icon><HomeFilled /></el-icon>
          <span>首页</span>
        </el-menu-item>
        <div style="flex-grow: 1"/>
        <MessageBox/>
        <UserSubMenu/>
      </el-menu>
    </el-header>
    <el-main style="padding-bottom: 0">
      <el-scrollbar height="81vh">
        <slot/>
      </el-scrollbar>
    </el-main>
    <el-footer>
      <el-divider border-style="dashed">
        <el-text class="mx-1" style="color: darkgray"> © 2025 AC论坛 All Rights Reserved.
          网站仅供学习交流使用
        </el-text>
      </el-divider>
      <el-divider border-style="dashed">
        <el-link href="https://beian.miit.gov.cn/" target="_blank" style="color: darkgray">赣ICP备2022006483号</el-link>
        <span style="color: darkgray"> | </span>
        <el-link href="https://www.beian.gov.cn/portal/registerSystemInfo" target="_blank" style="color: darkgray">
          赣公网安备36011102000604号
        </el-link>
      </el-divider>
    </el-footer>
  </el-container>
</template>

<style scoped>

</style>