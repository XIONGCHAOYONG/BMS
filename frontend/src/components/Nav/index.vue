<template>
  <div class="all">
    <div class="logo">
      <h1>花椒图书</h1>
    </div>
    <div class="menus">
      <el-menu
        :default-active="activeMenu"
        class="el-menu-vertical-demo"
        @open="handleOpen"
        @close="handleClose"
        mode="horizontal"
        router
      >
        <el-menu-item index="/">
          <el-icon><icon-menu /></el-icon>
          <span>首页</span>
        </el-menu-item>

        <el-menu-item index="/my-lend">
          <el-icon><document /></el-icon>
          <span>我的借阅</span>
        </el-menu-item>

        <el-menu-item index="/my-room">
          <el-icon><setting /></el-icon>
          <span>个人空间</span>
        </el-menu-item>

        <el-menu-item index="/assistant">
          <el-icon><setting /></el-icon>
          <span>AI助手</span>
        </el-menu-item>

        <el-menu-item @click="show">
          <Avatar
            style="margin-top: 10px; height: 45px; width: 45px;"
            :username="userStore.getUser()?.username"
            :avatar="userStore.getUser()?.avatar"
          />
        </el-menu-item>
      </el-menu>
    </div>
  </div>

  <!-- 退出登录弹窗 -->
  <el-dialog v-model="showDialog" title="提示" width="30%">
    <div>
      <p>确定要退出登录吗？</p>
    </div>
    <template #footer>
      <el-button @click="showDialog = false">取消</el-button>
      <el-button type="primary" @click="logout">确定</el-button>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { Document, Menu as IconMenu, Setting } from "@element-plus/icons-vue";
import { useRouter, useRoute } from "vue-router";
import { computed, ref } from "vue";
import { useUserStore } from "@/stores/userStore";
import Avatar from "@/components/Avatar/index.vue";

const router = useRouter();
const route = useRoute(); // 新增
const userStore = useUserStore();

const handleOpen = (key: string, keyPath: string[]) => {
  console.log(key, keyPath);
};
const handleClose = (key: string, keyPath: string[]) => {
  console.log(key, keyPath);
};

const showDialog = ref(false);
const show = () => {
  showDialog.value = true;
};
const logout = async () => {
  showDialog.value = false;
  userStore.logout();
  router.push("/login");
};

const userAvatar = computed(() => userStore.getUser()?.avatar);

// ✅ 新增：动态计算当前高亮的菜单项
const menuPaths = ["/", "/my-lend", "/my-room", "/assistant"];
const activeMenu = computed(() => {
  return menuPaths.includes(route.path) ? route.path : "";
});
</script>

<style scoped>
.all {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  display: flex;
  border-bottom: 1px solid #e4e7ed;
  height: 60px;
  background-color: white;
  z-index: 100;
}

.logo {
  margin-left: 10px;
  text-align: center;
  line-height: 60px;
  color: #f1bef1;
  font-style: italic;
}

.menus {
  width: 42.2%;
  position: absolute;
  right: -70px;
}

.all .el-menu {
  height: 100%;
  border-right: none;
}
</style>
