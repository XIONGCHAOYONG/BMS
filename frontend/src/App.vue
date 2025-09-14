<template>
    <Nav v-if="!hideNav"></Nav>

    <transition name="fade" mode="out-in">
      <router-view />
    </transition>
</template>

<script setup>
import Nav from '@/components/Nav/index.vue'
import { computed } from 'vue';
import { useRoute,useRouter } from 'vue-router'
import { useUserStore } from './stores/userStore';
const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
if(!userStore.getUser())
  router.push('/login')


const hideNav=computed(() => route.path === '/login')
</script>

<style>
/* 路由切换动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.6s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

</style>
