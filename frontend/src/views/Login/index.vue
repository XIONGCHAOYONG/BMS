<template>
  <div class="all">
    <div class="left">
      <div class="content">
        花椒图书
      </div>
    </div>

    <div class="right">
      <el-card class="login-card">
        <!-- <h2 class="title">{{ isLogin ? "" : "注册" }}</h2> -->
        
        <!-- 登录方式切换导航栏 -->
        <div class="login-tabs" v-if="isLogin">
          <div 
            :class="['tab-item', loginType === 'password' ? 'active' : '']" 
            @click="loginType = 'password'"
          >
            密码登录
          </div>
          <div 
            :class="['tab-item', loginType === 'sms' ? 'active' : '']" 
            @click="loginType = 'sms'"
          >
            短信登录
          </div>
        </div>

        <el-form ref="formRef" class="form" label-width="auto" :model="form">
          <!-- 注册表单 -->
          <template v-if="!isLogin">
            <el-form-item label="用户名">
              <el-input v-model="form.username" placeholder="请输入用户名" clearable />
            </el-form-item>

            <el-form-item label="手机号">
              <el-input v-model="form.phone" placeholder="输入手机号" clearable />
            </el-form-item>
            
            <el-form-item label="密码">
              <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
            </el-form-item>

            <el-form-item label="确认密码">
              <el-input v-model="form.confirmPassword" type="password" placeholder="请再次输入密码" show-password />
            </el-form-item>
          </template>

          <!-- 密码登录表单 -->
          <template v-if="isLogin && loginType === 'password'">
            <el-form-item label="账号/手机号">
              <el-input v-model="form.identifier" placeholder="输入6位账号或手机号" clearable />
            </el-form-item>

            <el-form-item label="密码">
              <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
            </el-form-item>
          </template>

          <!-- 短信登录表单 -->
          <template v-if="isLogin && loginType === 'sms'">
            <el-form-item label="手机号">
              <el-input v-model="form.phone" placeholder="请输入手机号" clearable />
            </el-form-item>

            <el-form-item label="验证码">
              <div class="verification-code">
                <el-input v-model="form.smsCode" placeholder="请输入验证码" />
                <el-button type="primary" :disabled="isSendingSms" @click="sendSmsCode">
                  {{ smsButtonText }}
                </el-button>
              </div>
            </el-form-item>
          </template>
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
import { reactive, ref ,computed} from "vue";
import { ElMessage, ElNotification } from "element-plus";
import { login, register } from "@/api/User";
import { useUserStore } from "@/stores/userStore";
import type { UserLoginVO } from "@/api/User";
import { useRoute, useRouter } from "vue-router";
import {sendCode,codeLogin} from "@/api/User";
const route = useRoute();
const router = useRouter();
const userStore = useUserStore();
const isLogin = ref(true);
const loginType = ref('password'); // 登录类型：password或sms
const formRef = ref();
const form = reactive({
  identifier: "",
  username: "",
  password: "",
  confirmPassword: "",
  phone: "",
  smsCode: "" // 短信验证码
});

// 短信验证码相关
const isSendingSms = ref(false);
const smsCountdown = ref(0);
const smsButtonText = computed(() => {
  return isSendingSms.value ? `${smsCountdown.value}秒后重新发送` : '获取验证码';
});

//登录过前往"/"
if (localStorage.getItem('token') != null) {
  router.push("/");
  ElNotification({
    title: "你想干啥呢?",
    message: "已经登录了哦",
    type: "warning",
    offset: 50,
  });
}


//提交注册或者登录表单
// 发送短信验证码
const sendSmsCode = async () => {
  if (!form.phone) {
    ElMessage.error("请输入手机号");
    return;
  }
  
  // 手机号格式验证
  const phoneRegex = /^1[3-9]\d{9}$/;
  if (!phoneRegex.test(form.phone)) {
    ElMessage.error("请输入正确的手机号格式");
    return;
  }

  try {
    isSendingSms.value = true;
    smsCountdown.value = 60;
    
    const res = await sendCode(form.phone);
    if(res.data.code==1){
      ElMessage.success("验证码已发送，请注意查收");
    }else{
      ElMessage.error(res.data.msg);
    }
    
    // 模拟API调用
    
    // 倒计时
    const timer = setInterval(() => {
      smsCountdown.value--;
      if (smsCountdown.value <= 0) {
        clearInterval(timer);
        isSendingSms.value = false;
      }
    }, 1000);
  } catch (error) {
    console.error(error);
    ElMessage.error("发送验证码失败");
    isSendingSms.value = false;
  }
};

const handleSubmit = async () => {
  // if (!validateForm()) return;

  if (isLogin.value) {
    if (loginType.value === 'password') {
      //密码登录逻辑
      try {
        const res = await login({
          identifier: form.identifier,
          password: form.password,
        });
        const userVO = res.data.data as UserLoginVO;
        if (res.data.code === 1) {
          ElNotification({
            title: "登录成功",
            message: `欢迎回来,${userVO.user.username}`,
            type: "success",
            duration: 3000,
            offset: 50,
          });
          //存储用户信息到本地/缓存
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
            offset: 50,
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
      // 短信登录
      if (!form.phone) {
        ElMessage.error("请输入手机号");
        return;
      }
      if (!form.smsCode) {
        ElMessage.error("请输入验证码");
        return;
      }

      try {
        const res = await codeLogin(form.phone,form.smsCode);
        const userVO = res.data.data as UserLoginVO;
        if(res.data.code==1){
          ElNotification({
          title: "登录成功",
          message: `欢迎回来,${userVO.user.username}`,
          type: "success",
          duration: 3000,
          offset: 50,
        });
        //存储用户信息到本地/缓存
        userStore.setUser(userVO.user);
        userStore.setToken(userVO.token);
        //登录成功后回到首页
        router.push("/");
      } else {
        ElMessage.error(res.data.msg);
        return;
      }
    } catch (error) {
      ElNotification({
        title: "登录失败",
        message: "请检查网络!",
        type: "error",
          duration: 3000,
        });
      }
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
          offset: 50,
          duration: 3000,
        });
        //切换登录
        toggleMode();
        loginType.value = 'password';
      } else {
        ElNotification({
          title: "注册失败",
          message: res.data.msg,
          type: "error",
          offset: 50,
          duration: 3000,
        })
      }
    } catch (error) {
      ElNotification({
        title: "注册失败",
        message: "请检查网络!",
        type: "error",
        offset: 50,
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
  form.phone = "";
};
// 自定义校验函数
const validateForm = () => {
  if(loginType.value !== 'password'){
      return;
  }
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
  color: #409eff;
}

/* 登录方式切换导航栏样式 */
.login-tabs {
  display: flex;
  margin-bottom: 20px;
  border-bottom: 1px solid #ebeef5;
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: 10px 0;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 16px;
  color: #606266;
}

.tab-item.active {
  color: #409eff;
  font-weight: bold;
  border-bottom: 2px solid #409eff;
}

.tab-item:hover {
  color: #409eff;
}

/* 验证码输入框样式 */
.verification-code {
  display: flex;
  gap: 10px;
}

.verification-code .el-input {
  flex: 1;
}

.verification-code .el-button {
  width: 120px;
  white-space: nowrap;
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
