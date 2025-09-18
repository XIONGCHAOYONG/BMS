<template>
  <div v-if="!avatar" :style="avatarStyle" class="avatar-default">
    {{ firstChar }}
  </div>
  <img v-else :src="avatar" class="avatar-img" :alt="username" />
</template>

<script setup lang="ts">
/**
 * @description 用户头像组件，支持默认头像（随机背景色+用户名首字）
 * @param {string} avatar 用户头像路径
 * @param {string} username 用户名
 */
import { computed } from 'vue';

interface Props {
  avatar?: string;
  username: string;
}

const props = defineProps<Props>();

// 随机背景色生成（基于用户名hash，保证同一用户颜色一致）
function getRandomColor(str: string): string {
  let hash = 0;
  for (let i = 0; i < str.length; i++) {
    hash = str.charCodeAt(i) + ((hash << 5) - hash);
  }
  const color = `hsl(${Math.abs(hash) % 360}, 70%, 60%)`;
  return color;
}

const firstChar = computed(() => props.username ? props.username[0] : '用');
const avatarStyle = computed(() => ({
  background: getRandomColor(props.username || '用户'),
  color: '#fff',
  width: '40px',
  height: '40px',
  display: 'flex',
  'align-items': 'center',
  'justify-content': 'center',
  'border-radius': '50%',
  'font-size': '20px',
  'user-select': 'none'
}));
</script>

<style scoped>

.avatar-img {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  object-fit: cover;
}
</style>