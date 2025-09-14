<template>
  <div class="all">
    <div class="left">
      <div class="content">
        花椒图书
      </div>
    </div>

    <div class="right">
      <el-card class="login-card">
        <h2 class="title">{{ isLogin ? "登录" : "注册" }}</h2>

        <el-form ref="formRef" class="form" label-width="auto" :model="form">
          <el-form-item label="用户名" v-if="!isLogin">
            <el-input v-model="form.username" placeholder="请输入用户名" clearable />
          </el-form-item>

          <el-form-item label="手机号" v-if="!isLogin">
            <el-input v-model="form.phone" placeholder="输入手机号" clearable />
          </el-form-item>

          <!-- 账号/手机号 -->
          <el-form-item label="账号/手机号" v-if="isLogin">
            <el-input v-model="form.identifier" placeholder="输入6位账号或手机号" clearable />
          </el-form-item>

          <!-- 密码 -->
          <el-form-item label="密码">
            <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
          </el-form-item>

          <el-form-item label="确认密码" v-if="!isLogin">
            <el-input v-model="form.confirmPassword" type="password" placeholder="请再次输入密码" show-password />
          </el-form-item>


        </el-form>

        <div class="btn">
          <el-button type="primary" @click="handleSubmit">
            {{ isLogin ? "登录" : "注册" }}
          </el-button>
          <el-button type="text" @click="toggleMode">
            {{ isLogin ? "去注册" : "已有账号？去登录" }}
          </el-button>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from "vue";
import { ElMessage, ElNotification } from "element-plus";
import { login, register } from "@/api/User";
import { useUserStore } from "@/stores/userStore";
import type { UserLoginVO } from "@/api/User";
import { useRoute, useRouter } from "vue-router";
const route = useRoute();
const router = useRouter();
const userStore = useUserStore();
const isLogin = ref(true);
const formRef = ref();
const form = reactive({
  identifier: "",
  username: "",
  password: "",
  confirmPassword: "",
  phone: ""
});

//登录过前往/
if (userStore.getUser()) {
  router.push("/");
  ElNotification({
    title: "你想干啥呢?",
    message: "已经登录了哦",
    type: "warning",
  });
}

const handleSubmit = async () => {
  if (!validateForm()) return;

  if (isLogin.value) {
    //登录逻辑
    try {
      const res = await login({
        identifier: form.identifier,
        password: form.password,
      });
      console.log(res.data);
      const userVO = res.data.data as UserLoginVO;
      if (res.data.code === 1) {
        ElNotification({
          title: "登录成功",
          message: `欢迎回来,${userVO.user.username}`,
          type: "success",
          duration: 3000,
        });
        //存储用户信息
        userStore.setUser(userVO.user);
        userStore.setToken(userVO.token);
        //登录成功后回到首页
        router.push("/");
      } else {
        ElNotification({
          title: "登录失败",
          message: res.data.msg,
          type: "error",
          duration: 3000,
        });
      }
    } catch {
      ElNotification({
        title: "登录失败",
        message: "请检查网络!",
        type: "error",
        duration: 3000,
      });
    }

  } else {
    //注册逻辑
    try {
      const res = await register({
        username: form.username,
        password: form.password,
        phone: form.phone,
      });
      if (res.data.code == 1) {
        ElNotification({
          title: "注册成功",
          message: "请登录",
          type: "success",
          duration: 3000,
        });
        toggleMode();
      } else {
        ElNotification({
          title: "注册失败",
          message: res.data.msg,
          type: "error",
          duration: 3000,
        })
      }
    } catch (error) {
      ElNotification({
        title: "注册失败",
        message: "请检查网络!",
        type: "error",
        duration: 3000,
      });
    }
  }
};










const toggleMode = () => {
  isLogin.value = !isLogin.value;
  form.identifier = "";
  form.username = "";
  form.password = "";
  form.confirmPassword = "";
};
// 自定义校验函数
const validateForm = () => {
  // 账号/手机号
  if (isLogin.value) {
    const identifier = form.identifier.trim();
    if (!identifier) {
      ElMessage.error("请输入账号或手机号");
      return false;
    }
    const isPhone = /^1[3-9]\d{9}$/.test(identifier);
    const isAccount = /^\d{6}$/.test(identifier);
    if (!isPhone && !isAccount) {
      ElMessage.error("请输入6位数字账号或正确的手机号");
      return false;
    }
  }



  // 密码
  if (!form.password) {
    ElMessage.error("请输入密码");
    return false;
  }

  if (!isLogin.value) {
    // 注册额外校验
    if (!form.phone) {
      ElMessage.error("请输入手机号");
      return false;
    }
    if (!/^1[3-9]\d{9}$/.test(form.phone)) {
      ElMessage.error("请输入正确的手机号");
      return false;
    }
    if (!form.username) {
      ElMessage.error("请输入用户名");
      return false;
    }
    if (!form.confirmPassword) {
      ElMessage.error("请确认密码");
      return false;
    }
    if (form.confirmPassword !== form.password) {
      ElMessage.error("两次输入的密码不一致");
      return false;
    }
  }

  return true;
};
</script>
<style scoped>
.all {
  width: 100vw;
  height: 100vh;
  display: flex;
}

.left {
  width: 50%;
  height: 100%;
  background-color: #ffe1ff;
  background-color: #f1bef1;
}

.content {
  width: 100px;
  height: 400px;
  font-size: 65px;
  color: white;
  font-style: italic;
  font-weight: bold;
  margin-top: 180px;
  margin-left: 290px;
}

.right {
  width: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #3f9eff;
}

.login-card {
  width: 400px;
  padding: 30px 20px;
  border-radius: 10px;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.1);
}

.title {
  text-align: center;
  margin-bottom: 25px;
  font-size: 24px;
  font-weight: 600;
}

.form {
  margin-top: 10px;
}

/* 标签右对齐 */
.el-form-item__label {
  text-align: right;
  width: 120px;
  /* 标签宽度，可以根据需要调整 */
  padding-right: 10px;
}

/* 输入框填满剩余空间 */
.el-form-item {
  display: flex;
  align-items: center;
}

/* 保证输入框宽度一致 */
.el-input {
  flex: 1;
}

.btn {
  display: flex;
  /* 让按钮横向排列 */
  justify-content: center;
  /* 水平居中 */
  gap: 20px;
  /* 按钮间距 */
  margin-top: 30px;
  /* 向下移动 */
}
</style>
