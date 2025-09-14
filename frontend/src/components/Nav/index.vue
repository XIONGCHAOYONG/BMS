<template>
  <div class="all">
    <div class="logo">
      <h1>花椒图书</h1>
    </div>
    <div class="menus">
      <el-menu default-active="/" class="el-menu-vertical-demo" @open="handleOpen" @close="handleClose"
        mode="horizontal" router>
        <el-menu-item index="/">
          <el-icon><icon-menu /></el-icon>
          <span>首页</span>
        </el-menu-item>

        <el-menu-item index="/my-lend">
          <el-icon>
            <document />
          </el-icon>
          <span>我的借阅</span>
        </el-menu-item>

        <el-menu-item index="/my-room">
          <el-icon>
            <setting />
          </el-icon>
          <span>个人空间</span>
        </el-menu-item>

        <el-menu-item index="/assistant">
          <el-icon>
            <setting />
          </el-icon>
          <span>AI助手</span>
        </el-menu-item>
        <el-menu-item @click="show">
          <el-avatar size="large">
            <img src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
          </el-avatar>
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
import { useRouter } from "vue-router";
import { computed, ref } from "vue";
import { useUserStore } from "@/stores/userStore";


const router = useRouter();
const userStore = useUserStore();
const handleOpen = (key: string, keyPath: string[]) => {
  console.log(key, keyPath);
};
const handleClose = (key: string, keyPath: string[]) => {
  console.log(key, keyPath);
};

const showDialog = ref(false)
const show = () => {
  showDialog.value = true
}
const logout = async () => {
  showDialog.value = false
  await userStore.logout()
  router.push("/login");
}


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
}

.logo {
  margin-left: 10px;
  text-align: center;
  line-height: 60px;
  color: #f1bef1;
  font-style: italic;
}

.menus {
  width: 43%;
  position: absolute;
  right: -70px;
}

.all .el-menu {
  height: 100%;
  border-right: none;
  /* 可选，去掉右边分割线 */
}
</style>
