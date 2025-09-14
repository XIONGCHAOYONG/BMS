import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('@/views/Home/index.vue'),
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/Login/index.vue'),
    },
    {
      path: '/my-lend',
      name: 'my-lend',
      component: () => import('@/views/MyLend/index.vue'),
    },
    {
      path: '/my-room',
      name: 'my-room',
      component: () => import('@/views/MyRoom/index.vue'),
    },
    {
      path: '/assistant',
      name: 'assistant',
      component: () => import('@/views/Assitant/index.vue'),
    },
  ],
})

export default router
