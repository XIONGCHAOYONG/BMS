<template>
  <div class="chat-container">
    <div class="chat-list" ref="chatList">
      <div v-for="msg in messages" :key="msg.id" :class="['chat-msg', msg.role]">
        <div class="avatar" v-if="msg.role === 'ai'">
          <img src="@/assets/呆唯.png" alt="AI" />
        </div>
        <div class="avatar" v-else>
          <img :src=user?.avatar  alt="用户" />
        </div>
        <div class="bubble">
          {{ msg.content }}
        </div>
      </div>
    </div>
    <div class="chat-input-bar">
      <el-input v-model="input" placeholder="请输入内容..." @keyup.enter="sendMsg" clearable />
      <el-button type="primary" @click="sendMsg">发送</el-button>
    </div>
  </div>
  <Footer></Footer>
</template>

<script lang="ts" setup>
import { ref, nextTick, watch } from 'vue'
import { useUserStore } from '@/stores/userStore'
import Footer from  "@/components/Footer/index.vue"
const userStore = useUserStore()
const user = userStore.getUser() 
interface Message {
  id: number
  role: 'user' | 'ai'
  content: string
}

const messages = ref<Message[]>([
  { id: 1, role: 'ai', content: '你好，我是你的图书馆AI助手，有什么可以帮您？' },
  { id: 2, role: 'user', content: '帮我推荐几本编程书籍。' },
  { id: 3, role: 'ai', content: '推荐：《深入理解计算机系统》《JavaScript高级程序设计》《算法导论》' },
  { id: 4, role: 'user', content: '这些书可以借吗？' },
  { id: 5, role: 'ai', content: '都可以借阅，您可以在借阅页面查看详情。' }
])

const input = ref('')
const chatList = ref<HTMLElement | null>(null)

function scrollToBottom() {
  nextTick(() => {
    if (chatList.value) {
      chatList.value.scrollTop = chatList.value.scrollHeight
    }
  })
}

function sendMsg() {
  if (!input.value.trim()) return
  messages.value.push({
    id: Date.now(),
    role: 'user',
    content: input.value
  })
  scrollToBottom()
  // 模拟AI回复
  setTimeout(() => {
    messages.value.push({
      id: Date.now() + 1,
      role: 'ai',
      content: 'AI已收到您的消息：' + input.value
    })
    scrollToBottom()
  }, 800)
  input.value = ''
}

watch(messages, scrollToBottom)
</script>

<style scoped>
.chat-container {
  width: 95vw;
  margin: 0 auto;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.08);
  display: flex;
  flex-direction: column;
  height: 85vh;
  margin-top: 70px;
  margin-bottom: 10px;
}
.chat-header {
  padding: 16px;
  font-size: 18px;
  font-weight: bold;
  border-bottom: 1px solid #eee;
  text-align: center;
}
.chat-list {
  flex: 1;
  padding: 16px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 20px;
}
.chat-msg {
  display: flex;
  align-items: flex-end;
}
.chat-msg.user {
  flex-direction: row-reverse;
}
.avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  overflow: hidden;
  margin: 0 8px;
}
.avatar img {
  width: 100%;
  height: 100%;
}
.bubble {
  max-width: 70%;
  padding: 10px 16px;
  border-radius: 16px;
  background: #f2f3f5;
  font-size: 16px;
  word-break: break-all;
}
.chat-msg.user .bubble {
  background: #409eff;
  color: #fff;
}
.chat-input-bar {
  display: flex;
  gap: 8px;
  padding: 16px;
  border-top: 1px solid #eee;
}
</style>

