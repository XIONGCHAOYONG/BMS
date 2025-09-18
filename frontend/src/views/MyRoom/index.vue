<template>
  <div class="myRoom">
    <el-descriptions title="My Room" :column="3" border="true" direction="vertical" class="profile-descriptions"
      size="large">
            <el-descriptions-item label="avatar" :rowspan="3" class="avatar-item" :label-width="300" align="center">
        <Avatar style="margin: 10px auto; width: 180px; height: 180px;" :avatar="user?.avatar" :username="user?.username as string"></Avatar>
      </el-descriptions-item>

      <el-descriptions-item label="用户名">
        {{ user?.username || '未设置' }}
      </el-descriptions-item>
      <el-descriptions-item label="账号">
        {{ user?.account || '未设置' }}
      </el-descriptions-item>
      <el-descriptions-item label="姓名">
        {{ user?.realName || '请及时补充真实姓名' }}
      </el-descriptions-item>
      <el-descriptions-item label="手机">
        {{ user?.phone || '请及时补充联系方式' }}
      </el-descriptions-item>
      <el-descriptions-item label="地址">
        {{ user?.address || '请及时补充联系地址' }}
      </el-descriptions-item>
      <el-descriptions-item label="注册时间">
        {{ user?.createTime || '未知' }}
      </el-descriptions-item>
      <el-descriptions-item label="状态">
        <el-tag :type="user?.status === 0 ? 'success' : 'danger'">
          {{ user?.status === 0 ? '正常' : '已禁用' }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="角色">
        <el-tag type="info">{{ user?.role === 1 ? '管理员' : '普通用户' }}</el-tag>
      </el-descriptions-item>
    </el-descriptions>

    <div class="edit-button-container">
      <el-button type="primary" @click="showEditDialog = true">
        编辑信息
      </el-button>
    </div>

    <!-- 编辑对话框 -->
    <el-dialog v-model="showEditDialog" title="编辑个人信息" width="500px" :close-on-click-modal="false">
            <el-form
        ref="editFormRef"
        :model="editForm"
        :rules="editRules"
        label-width="100px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="editForm.username" maxlength="20" show-word-limit />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="editForm.realName" maxlength="20" show-word-limit />
        </el-form-item>
        <el-form-item label="手机号码" prop="phone">
          <el-input v-model="editForm.phone" maxlength="11" />
        </el-form-item>
        <el-form-item label="联系地址" prop="adderss">
          <el-input v-model="editForm.address" maxlength="100" show-word-limit />
        </el-form-item>

        <el-form-item label="头像">
          <div class="avatar-upload">
            <el-upload
              class="avatar-uploader"
              :show-file-list="false"
              :before-upload="beforeAvatarUpload"
              :on-success="handleAvatarSuccess"
              :on-error="handleAvatarError"
              :action="uploadUrl"
              :headers="headers"
              accept=".jpg,.jpeg,.png,.gif"
            >
              <div v-if="avatarUrl" class="avatar-preview">
                <img :src="avatarUrl" class="avatar-image" />
              </div>
              <div v-else class="avatar-placeholder">
                <el-icon><Plus /></el-icon>
                <span>点击上传或更新头像</span>
              </div>
            </el-upload>
            <div class="upload-tip">支持 jpg、png、gif 格式，大小不超过 2MB</div>
          </div>
        </el-form-item>


      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showEditDialog = false">取消</el-button>
          <el-button type="primary" @click="handleSave" :loading="saveLoading">
            保存
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
  <Footer></Footer>
</template>

<script setup lang="ts">
import type { User } from '@/api/User';
import { onMounted, ref, watch } from 'vue';
import { useUserStore } from '@/stores/userStore';
import router from '@/router';
import { ElNotification, ElMessage } from 'element-plus';
import { Plus } from '@element-plus/icons-vue';
import Avatar from '@/components/Avatar/index.vue';
import { updateUser } from '@/api/User';
import Footer from '@/components/Footer/index.vue';
const userStore = useUserStore();
const user = ref<User>();

//upload
const uploadUrl=import.meta.env.VITE_SERVER_URL+'/user/upload';
const headers={
  token: userStore.token
};


// 编辑对话框相关
const showEditDialog = ref(false);
const saveLoading = ref(false);
const editFormRef = ref();

// 编辑表单数据
// 头像预览URL
const avatarUrl = ref('');

const editForm = ref<Partial<User>>({});


// 上传前校验
const beforeAvatarUpload = (file: File) => {
  const isValidFormat = ['image/jpeg', 'image/png', 'image/gif'].includes(file.type);
  const isLt2M = file.size / 1024 / 1024 < 2;

  if (!isValidFormat) {
    ElMessage.error('头像只能是 JPG/PNG/GIF 格式!');
    return false;
  }
  if (!isLt2M) {
    ElMessage.error('头像大小不能超过 2MB!');
    return false;
  }
  return true;
};

// 上传成功回调
const handleAvatarSuccess = (response: any, uploadFile: any) => {
  if (response.code === 1) {
    // 使用完整的URL路径
    const fullPath = response.data;
    avatarUrl.value = fullPath;
    
    if (user.value) {
      user.value.avatar = fullPath;
      // 更新 store 中的用户数据
      userStore.$patch({
        user: user.value
      });
    }
    ElMessage.success('头像上传成功');
  } else {
    ElMessage.error('头像上传失败：' + response.message);
  }
};

// 上传失败回调
const handleAvatarError = () => {
  ElMessage.error('头像上传失败，请重试');
};

// 表单验证规则
const editRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 1, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  realName: [
    { max: 20, message: '长度不能超过 20 个字符', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  adderss: [
    { max: 100, message: '长度不能超过 100 个字符', trigger: 'blur' }
  ]
};

// 打开编辑对话框时初始化表单数据
const initEditForm = () => {
  if (user.value) {
    editForm.value = {
      username: user.value.username ,
      realName: user.value.realName ,
      phone: user.value.phone ,
      address: user.value.address ,
    };
  }
};

// 处理保存
const handleSave = async () => {
  if (!editFormRef.value) return;

  try {
    await editFormRef.value.validate();
    saveLoading.value = true;

     // 更新本地用户数据
    if (user.value) {
      user.value = {
        ...user.value,
        ...editForm.value,
        avatar: avatarUrl.value? avatarUrl.value : user.value.avatar
      };
      userStore.setUser(user.value);
    }
    editForm.value.avatar = avatarUrl.value ? avatarUrl.value : user?.value?.avatar;

    showEditDialog.value = false;

    //更新
    const res = await updateUser(editForm.value );
    if(res.data.code===1){
      // 成功处理
      ElNotification({
        title: '成功',
        message: '个人信息更新成功',
        type: 'success'
      });
    } else {
      ElNotification({
        title: '错误',
        message: res.data.message || '保存失败，请重试',
        type: 'error'
      });
      return;
    }
    
  } catch (error) {
    console.error('保存失败:', error);
    ElNotification({
      title: '错误',
      message: '保存失败，请重试',
      type: 'error'
    });
  } finally {
    saveLoading.value = false;
  }
};

// 监听对话框打开
watch(showEditDialog, (newVal) => {
  if (newVal) {
    initEditForm();
  }
});

onMounted(() => {
  if (userStore.user) {
    user.value = userStore.user;
  } else {
    ElNotification({
      title: 'Error',
      message: '请先登录哦!',
      type: 'error',
    });
    router.push('/login');
  }
});

</script>
<style scoped>
.myRoom {
  width: 95vw;
  height: 80vh;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin: 65px auto;
}

.profile-descriptions {
  min-height: 600px;
  padding: 20px;
}

.avatar-item {
  display: flex;
  justify-content: center;
  align-items: center;
}

.user-avatar {
  background-color: var(--el-color-primary);
  font-size: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.default-avatar {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 60px;
  font-weight: bold;
  text-transform: uppercase;
}


.edit-button-container {
  position: absolute;
  bottom: 130px;
  right: 80px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

:deep(.el-form-item__content) {
  flex-wrap: nowrap;
}

::v-deep .el-descriptions-item__content {
  padding: 15px 10px;
  font-size: 16px;
}

:deep(.el-dialog__body) {
  padding: 20px 40px;
}

.avatar-upload {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.avatar-uploader {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration);
}

.avatar-uploader:hover {
  border-color: var(--el-color-primary);
}

.avatar-placeholder {
  width: 178px;
  height: 178px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: #8c939d;
  gap: 8px;
}

.avatar-placeholder span {
  font-size: 14px;
}

.avatar-preview {
  width: 178px;
  height: 178px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.avatar-image {
  max-width: 100%;
  max-height: 100%;
  object-fit: cover;
}

.upload-tip {
  color: var(--el-text-color-secondary);
  font-size: 12px;
}
</style>
