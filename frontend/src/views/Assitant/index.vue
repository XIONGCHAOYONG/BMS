<template>
  <div class="chat-container">
    <div class="chat-list" ref="chatList">
      <div v-for="msg in messages" :key="msg.messageId" :class="['chat-msg', msg.role]">
        <div class="avatar" v-if="msg.role === 'ai'">
          <img src="@/assets/呆唯.png" alt="AI" />
        </div>
        <div class="avatar" v-else>
          <img :src=user?.avatar  alt="用户" />
        </div>
        <div class="bubble">
          {{ msg.content }}
          <span v-if="isLoading && msg === messages[messages.length - 1] && msg.role === 'ai'" class="loading-dots">
            <span class="dot"></span>
            <span class="dot"></span>
            <span class="dot"></span>
          </span>
        </div>
      </div>
    </div>
    <div class="chat-input-bar">
      <el-input 
        v-model="input" 
        placeholder="请输入内容..." 
        @keyup.enter="sendMsg" 
        clearable 
        :disabled="isLoading" 
      />
      <el-button 
        type="primary" 
        @click="sendMsg" 
        :loading="isLoading"
      >
        {{ isLoading ? '接收中...' : '发送' }}
      </el-button>
    </div>
  </div>
  <Footer></Footer>
</template>

<script lang="ts" setup>
import { ref, nextTick, watch, onMounted } from 'vue'
import { useUserStore } from '@/stores/userStore'
import Footer from  "@/components/Footer/index.vue"
import { streamChat, getChatMessage, addChatMessage } from '@/api/ChatMessage'
import type { ChatMessage } from '@/api/ChatMessage'
const userStore = useUserStore()
const user = localStorage.getItem('user') ? JSON.parse(localStorage.getItem('user') || '{}') : null
// 使用ChatMessage接口
const messages = ref<ChatMessage[]>([])
const input = ref('')
const chatList = ref<HTMLElement | null>(null)
const isLoading = ref(false)

// 页面加载时获取历史消息
onMounted(async () => {

  try {
    const response = await getChatMessage()
    if (response.data.data && response.data.data.length > 0) {
      messages.value = response.data.data
    } else {
      // 如果没有历史消息，添加默认欢迎消息
      messages.value = [{
        messageId: Date.now(),
        userId: user?.userId || null,
        content: '你好，我是你的图书馆AI助手，有什么可以帮您？',
        role: 'ai',
        createTime: new Date().toISOString()
      }]
    }
    scrollToBottom()
  } catch (error) {
    console.error('获取历史消息失败:', error)
    // 添加默认欢迎消息
    messages.value = [{
      messageId: Date.now(),
      userId: user?.userId || null,
      content: '你好，我是你的图书馆AI助手，有什么可以帮您？',
      role: 'ai',
      createTime: new Date().toISOString()
    }]
  }
})

function scrollToBottom() {
  nextTick(() => {
    if (chatList.value) {
      chatList.value.scrollTop = chatList.value.scrollHeight
    }
  })
}

async function sendMsg() {
  if (!input.value.trim() || isLoading.value) return
  
  const userMessage: ChatMessage = {
    messageId: Date.now(),
    userId: user?.userId || null,
    content: input.value,
    role: 'user',
    createTime: new Date().toISOString()
  }
  
  // 添加用户消息到界面
  messages.value.push(userMessage)
  scrollToBottom()
  
  // 保存用户消息到数据库
  try {
    await addChatMessage(userMessage)
  } catch (error) {
    console.error('保存用户消息失败:', error)
  }
  
  const userInput = input.value
  input.value = ''
  isLoading.value = true
  
  // 创建AI回复消息
  const aiMessageId = Date.now() + 1
  const aiMessage: ChatMessage = {
    messageId: aiMessageId,
    userId: user?.userId || null,
    content: '',
    role: 'ai',
    createTime: new Date().toISOString()
  }
  
  messages.value.push(aiMessage)
  
  try {
    // 调用流式聊天API
    const response = await streamChat(userInput)
    
    if (!response.ok) {
      throw new Error('网络请求失败')
    }
    
    const reader = response.body?.getReader()
    if (!reader) {
      throw new Error('无法读取响应流')
    }
    
    const decoder = new TextDecoder()
    let done = false
    
    while (!done) {
      const { value, done: readerDone } = await reader.read()
      done = readerDone
      
      if (value) {
        const text = decoder.decode(value, { stream: !done })
        // 更新AI回复内容
        const currentAiMessage = messages.value.find(msg => msg.messageId === aiMessageId)
        if (currentAiMessage) {
          currentAiMessage.content += text
          scrollToBottom()
        }
      }
    }
    
    // 流式接收完成后，保存AI回复到数据库
    const finalAiMessage = messages.value.find(msg => msg.messageId === aiMessageId)
    if (finalAiMessage && finalAiMessage.content) {
      try {
        await addChatMessage(finalAiMessage)
      } catch (error) {
        console.error('保存AI回复失败:', error)
      }
    }
    
  } catch (error) {
    console.error('聊天请求失败:', error)
    // 更新错误消息
    const currentAiMessage = messages.value.find(msg => msg.messageId === aiMessageId)
    if (currentAiMessage) {
      currentAiMessage.content = '抱歉，请求失败，请稍后再试。'
      
      // 保存错误消息到数据库
      try {
        await addChatMessage(currentAiMessage)
      } catch (saveError) {
        console.error('保存错误消息失败:', saveError)
      }
    }
  } finally {
    isLoading.value = false
    scrollToBottom()
  }
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

.loading-dots {
  display: inline-flex;
  align-items: center;
  margin-left: 4px;
}

.dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background-color: #999;
  margin: 0 2px;
  animation: dot-flashing 1s infinite alternate;
}

.dot:nth-child(2) {
  animation-delay: 0.2s;
}

.dot:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes dot-flashing {
  0% {
    opacity: 0.2;
  }
  100% {
    opacity: 1;
  }
}
</style>

