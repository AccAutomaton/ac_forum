<script setup>
import {ChatLineSquare, CreditCard, Discount, Document, Message} from "@element-plus/icons-vue";
import UserSubMenu from "@/components/navigationBar/UserSubMenu.vue";
import MessageBox from "@/components/navigationBar/MessageBox.vue";
import store from "@/store/index.js";
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
          <el-icon>
            <Discount/>
          </el-icon>
          <span>首页</span>
        </el-menu-item>
        <el-menu-item index="/topic">
          <el-icon>
            <ChatLineSquare/>
          </el-icon>
          <span>话题</span>
        </el-menu-item>
        <el-menu-item index="/article">
          <el-icon>
            <Document/>
          </el-icon>
          <span>文章</span>
        </el-menu-item>
        <el-menu-item index="/chat">
          <el-icon>
            <Message/>
          </el-icon>
          <span>私信</span>
        </el-menu-item>
        <div style="flex-grow: 1"/>
        <el-menu-item index="/vip" style="margin-right: 20px">
          <el-icon>
            <CreditCard/>
          </el-icon>
          <span>会员</span>
        </el-menu-item>
        <MessageBox v-if="store.getters.getIsLogin"/>
        <UserSubMenu style="margin-left: 20px"/>
      </el-menu>
    </el-header>
    <el-main style="padding-bottom: 0">
      <el-scrollbar height="85.5vh">
        <slot/>
      </el-scrollbar>
    </el-main>
    <el-footer style="height: 20px">
      <el-divider border-style="dashed">
        <el-text class="mx-1" style="color: darkgray"> © 2025 AC论坛 All Rights Reserved.
          网站仅供学习交流使用
        </el-text>
      </el-divider>
    </el-footer>
  </el-container>
</template>

<style scoped>

</style>