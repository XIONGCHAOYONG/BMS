<template>
  <div class="lend">
    <!-- 借阅时间线 -->
    <div class="lendList">
      <h3 class="section-title">借阅历史</h3>
      <div class="timeline">
        <div class="timeline-line"></div>


        <div class="timeline-items">
          <div v-for="(item, index) in lendHistory" :key="item.lend.lendId" class="timeline-item"
            :class="{ 'left': index % 2 === 0, 'right': index % 2 === 1 }"
            @click="$router.push(`/book/${item.lend.bookId}`)"
            >
            <!-- 时间点 -->
            <div class="timeline-dot">
              <div class="dot"></div>
              <div class="date">{{ formatDate(item.lend.borrowDate) }}</div>
            </div>

            <!-- 内容卡片 -->
            <el-card class="lend-card" shadow="hover">
              <div class="book-info">
                <div class="book-cover-placeholder" :style="{ backgroundColor: getBookBgColor(item.lend.bookId) }">
                  {{ item.title }}
                </div>
                <div class="book-details">
                  <h4 class="book-title">{{ item.title }}</h4>
                  <p class="book-author">{{ item.author }}</p>
                  <div class="lend-meta">
                    <span>借阅日期:{{ formatDate(item.lend.borrowDate) }}</span>
                    <span>应还日期:
                      {{ formatDate(item.lend.dueDate) }}</span>
                  </div>
                  <div class="book-status">
                    <el-tag :type="getStatusTypeAndText(item.lend.status)?.type || 'info'" size="small">
                      {{ getStatusTypeAndText(item.lend.status)?.text }}
                    </el-tag>

                  </div>
                </div>
              </div>
            </el-card>
          </div>
        </div>



      </div>
    </div>

    <!-- 右侧状态卡片 -->
    <div class="right">

      <!-- 借阅提醒卡片 -->
      <el-card class="status-card notification-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <span>借阅提醒</span>
          </div>
        </template>
        <div class="notification-content">
          <div class="notification-item" >
            <el-icon>
              <Reading />
            </el-icon>
            <span>当前借阅：{{ currentLending ===0? 0: currentLending }}本</span>
          </div>
          <div class="notification-item" >
            <el-icon color="#e6a23c">
              <Warning />
            </el-icon>
            <span class="warning">即将逾期：{{ nearDue ===0 ? 0: nearDue }}本</span>
          </div>
          <div class="notification-item" >
            <el-icon color="#f56c6c">
              <CircleCloseFilled />
            </el-icon>
            <span class="danger">已逾期：{{ overdue ===0 ? 0: overdue }}本</span>
          </div>
        </div>
      </el-card>

      <!-- 通知卡片 -->
      <div class="notification">
        <el-card class="status-card notification-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>最新通知</span>
            </div>
          </template>
          <div class="notification-list">
            <!-- 逾期通知 -->
            <template v-if="dueSoonNotices.length > 0">
              <div v-for="notice in dueSoonNotices" :key="notice.noticeId" class="notice-item overdue">
                <el-icon color="#f56c6c">
                  <CircleCloseFilled />
                </el-icon>
                <div class="notice-content">
                  <div class="notice-title">
                    《{{ notice.title }}》已逾期 {{ notice.days }} 天
                  </div>
                </div>
              </div>
            </template>

            <!-- 即将到期通知 -->
            <template v-if="soonOverdueNotices.length > 0">
              <div v-for="notice in soonOverdueNotices" :key="notice.noticeId" class="notice-item due-soon">
                <el-icon color="#e6a23c">
                  <Warning />
                </el-icon>
                <div class="notice-content">
                  <div class="notice-title">
                    《{{ notice.title }}》将在 {{ notice.days }} 天后到期
                  </div>
                </div>
              </div>
            </template>

            <!-- 无通知时显示 -->
            <div v-if="soonOverdueNotices.length === 0 && dueSoonNotices.length === 0" class="no-notice">
              <el-icon>
                <Reading />
              </el-icon>
              <span>暂无通知</span>
            </div>
          </div>
        </el-card>
      </div>
    </div>
  </div>


  <!-- 当前借阅 -->
  <div class="curLend">
    <h3 class="curLendH3">当前借阅</h3>
    <div class="current-lending-list">
      <div v-for="item in activeLendBooks" :key="item.lend.lendId" class="current-lending-item"
                  @click="$router.push(`/book/${item.lend.bookId}`)"
>
        <div class="book-info">
          <div class="book-cover-small book-cover-placeholder"
            :style="{ backgroundColor: getBookBgColor(item.lend.bookId) }">
            {{ item.title }}
          </div>
          <div class="book-meta">
            <h4 class="book-title">{{ item.title }}</h4>
            <p class="book-author">{{ item.author }}</p>
             <div class="book-status">
                    <el-tag :type="getStatusTypeAndText(item.lend.status)?.type || 'info'" size="small">
                      {{ getStatusTypeAndText(item.lend.status)?.text }}
                    </el-tag>

                  </div>
            <div class="due-info" :class="{ 'near-due': item.lend.status === 3 }">
              <span>应还日期：{{ formatDate(item.lend.dueDate) }}</span>
              <span v-if="item.lend.status === 3" class="warning">
                （还剩 {{ remainingDays(item.lend.dueDate) }} 天）
              </span>
            </div>
            
          </div>
        </div>
      </div>
    </div>
  </div>
  <Footer></Footer>
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import { Reading, Warning, CircleCloseFilled } from '@element-plus/icons-vue'
import { ElMessage, ElNotification } from 'element-plus'
import dayjs from 'dayjs'
import Footer from '@/components/Footer/index.vue'
import type { LendVO } from '@/api/Lend'
import { getAllLend } from '@/api/Lend'

// 借阅历史
const lendHistory = ref<LendVO[]>([])
// 当前正在借阅的图书
const activeLendBooks = ref<LendVO[]>([])

// 统计数据
const currentLending = ref()
const nearDue = ref()
const overdue = ref()


onMounted(async () => {
  const res = await getAllLend()
  if (res.data.code === 1) {
    lendHistory.value = res.data.data
  } else {
    ElNotification.error({
      title: '错误',
      message: res.data.message || '获取借阅历史失败'
    })
  }
  activeLendBooks.value = lendHistory.value.filter(item => item.lend.status === 0 || item.lend.status === 3 || item.lend.status === 4)
  currentLending.value = activeLendBooks.value.length
  nearDue.value = lendHistory.value.filter(item => item.lend.status === 3).length
  overdue.value = lendHistory.value.filter(item => item.lend.status === 2).length
})


import type {NotificationVO} from '@/api/Notification'
import { getAllNotifications } from '@/api/Notification'
// 通知相关逻辑  
const notifications= ref<NotificationVO[]>([])
const soonOverdueNotices= ref<NotificationVO[]>([])
const dueSoonNotices= ref<NotificationVO[]>([])
onMounted(async()=>{
  const res=await getAllNotifications()
  if(res.data.code===1){
    notifications.value=res.data.data
  }else{
    ElNotification.error({
      title:'错误',
      message:res.data.message||'获取通知失败'
    })
  }
    dueSoonNotices.value=res.data.data.filter((item: NotificationVO)=>item.type===2)
    soonOverdueNotices.value=res.data.data.filter((item: NotificationVO)=>item.type===1)
})


function getBookBgColor(bookId: number): string {
  return `hsl(${(bookId * 137.508) % 360}, 70%, 80%)`;
}

const formatDate = (date: [number, number, number]) => {
  return `${date[0]}-${date[1]}-${date[2]}`;
};


// 获取状态类型和文本
function getStatusTypeAndText(status: number) {
  switch (status) {
    case 0:
      return {
        type: 'info',
        text: '在借'
      };
    case 1:
      return {
        type: 'success',
        text: '已还'
      };
    case 2:
      return {
        type: 'warning',
        text: '逾期'
      };
    case 3:
      return {
        type: 'danger',
        text: '将逾期'
      };
    case 4:
      return {
        type: 'info',
        text: '续借中'
      };
  }
}


//计算剩余天数
function remainingDays(dueDate: [number, number, number]): number {
  const [year, month, day] = dueDate
  const due = new Date(year, month - 1, day) // JS 月份从 0 开始
  const today = new Date()
  due.setHours(0, 0, 0, 0)
  today.setHours(0, 0, 0, 0)
  const diff = (due.getTime() - today.getTime()) / (1000 * 60 * 60 * 24)
  return diff > 0 ? diff : 0
}
</script>
<style scoped>
.lend {
  width: 98vw;
  margin: 0 auto;
  margin-top: 65px;
  margin-bottom: 10px;
  padding: 20px;
  height: 68vh;
  display: flex;
  gap: 24px;
  /* background-color: red; */
  box-shadow: rgba(0, 0, 0, 0.05) 0px 0px 0px 1px, rgb(209, 213, 219) 0px 0px 0px 1px inset;
  border-radius: 20px;
}

.curLend {
  width: 98vw;
  height: 22.7vh;
  margin: 0 auto;
  border-radius: 15px;
  display: flex;
  overflow-x: auto;
  scrollbar-width: none;
  /* Firefox */
  -ms-overflow-style: none;
  /* IE and Edge */
  scroll-behavior: smooth;
  /* 平滑滚动 */
  box-shadow: rgba(0, 0, 0, 0.05) 0px 0px 0px 1px, rgb(209, 213, 219) 0px 0px 0px 1px inset;
  border-radius: 20px;
  position: relative;
}

.curLendH3 {
  position: absolute;
  top: 5px;
  left: 25px;
}

.curLend::-webkit-scrollbar {
  display: none;
  /* Chrome, Safari and Opera */
}

.lendList {
  flex: 1;
  min-width: 0;
  background-color: white;
  box-shadow: rgba(9, 30, 66, 0.25) 0px 1px 1px, rgba(9, 30, 66, 0.13) 0px 0px 1px 1px;
  height: 62vh;
  overflow-y: auto;
  border-radius: 10px;
  padding: 20px;
  scrollbar-width: none;
  /* Firefox */
  -ms-overflow-style: none;
  /* IE and Edge */
}

.lendList::-webkit-scrollbar {
  display: none;
  /* Chrome, Safari and Opera */
}

.right {
  width: 380px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.section-title {
  margin-bottom: 20px;
  color: #303133;
  font-weight: bold;
}

.lend-card {
  margin-bottom: 10px;
}

.book-info {
  display: flex;
  gap: 16px;
}

.book-cover,
.book-cover-placeholder {
  width: 80px;
  height: 120px;
  border-radius: 4px;
  object-fit: cover;
}

.book-cover-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  color: white;
  font-size: 14px;
  padding: 8px;
}

.book-details {
  flex: 1;
  min-width: 0;
}

.book-title {
  margin: 0 0 8px;
  font-size: 16px;
  color: #303133;
}

.book-author {
  margin: 0 0 12px;
  color: #606266;
  font-size: 14px;
}

.lend-meta {
  display: flex;
  flex-direction: column;
  gap: 4px;
  color: #909399;
  font-size: 13px;
  margin-bottom: 12px;
}

.book-status {
  position: absolute;
  top: 10px;
  right: 10px;
}

.status-card {
  background: white;
  border-radius: 8px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.notification-content {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.notification-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
}

.warning {
  color: #e6a23c;
}

.danger {
  color: #f56c6c;
}

.status-cards {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.overdue-list,
.renew-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.overdue-item,
.renew-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;
}

.book-name {
  flex: 1;
  min-width: 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-right: 12px;
}

.overdue-days {
  color: #f56c6c;
  font-size: 13px;
}

/* 时间线样式 */
.timeline {
  position: relative;
  padding: 20px 0;
  width: 100%;
}

.timeline-line {
  position: absolute;
  left: 50%;
  top: 0;
  bottom: 0;
  width: 2px;
  background-color: #e4e7ed;
  transform: translateX(-50%);
}

.timeline-items {
  position: relative;
}

.timeline-item {
  position: relative;
  display: flex;
  gap: 20px;
  margin-bottom: 30px;
  width: 100%;
}

.timeline-item.left {
  padding-right: 55%;
  justify-content: flex-end;
  .book-status{
    top: 10px;
    left: 39%;
  }
}

.timeline-item.right {
  padding-left: 55%;
  .book-status{
    top: 10px;
    right: 5%;
  }
}

.timeline-dot {
  position: absolute;
  top: 0;
  left: 50%;
  display: flex;
  flex-direction: column;
  align-items: center;
  transform: translateX(-50%);
}

.timeline-dot .dot {
  width: 16px;
  height: 16px;
  background-color: #409EFF;
  border-radius: 50%;
  margin-bottom: 8px;
}

.timeline-dot .date {
  font-size: 14px;
  color: #909399;
  white-space: nowrap;
}

.timeline-item .lend-card {
  width: calc(100% - 40px);
  border-radius: 10px;
  cursor: pointer;
  margin: 0;
}

/* 当前借阅卡片样式 */
.current-lending-list {
  display: flex;
  gap: 16px;
  padding: 10px;
}

.current-lending-item {
  position: relative;
  margin-top: 3vh;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-radius: 8px;
  height: 17vh;
  width: 400px;
  flex-shrink: 0;
  transition: all 0.4s ease;
  cursor: pointer;
  box-shadow: rgba(9, 30, 66, 0.25) 0px 4px 8px -2px, rgba(9, 30, 66, 0.08) 0px 0px 0px 1px;
}

.current-lending-item:hover {
  box-shadow: rgba(9, 30, 66, 0.25) 0px 8px 16px -4px, rgba(9, 30, 66, 0.08) 0px 0px 0px 1px;
  scale: 1.02;
}

.current-lending-item .book-info {
  display: flex;
  gap: 12px;
  flex: 1;
  min-width: 0;
}

.book-cover-small {
  width: 60px;
  height: 90px;
  border-radius: 4px;
  object-fit: cover;
}

.book-meta {
  flex: 1;
  min-width: 0;
}

.due-info {
  font-size: 13px;
  color: #606266;
  margin-top: 4px;
}

.due-info.near-due {
  color: #e6a23c;
}

.book-actions {
  display: flex;
  gap: 8px;
  align-items: flex-start;
}

/* 通知卡片样式 */
.notification-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  max-height: 300px;
  overflow-y: auto;
  scrollbar-width: none;
}

.notification-list::-webkit-scrollbar {
  display: none;
}

.notice-item {
  display: flex;
  gap: 12px;
  padding: 12px;
  border-radius: 6px;
  transition: all 0.3s ease;
  border-bottom: 1px solid #ebeef5;
}

.notice-item:last-child {
  border-bottom: none;
}

.notice-item:hover {
  transform: translateX(4px);
}

.notice-content {
  flex: 1;
  min-width: 0;
}

.notice-title {
  font-size: 14px;
  color: #303133;
  margin-bottom: 4px;
}

.notice-time {
  font-size: 12px;
  color: #909399;
}

.notice-item.overdue .notice-title {
  color: #f56c6c;
}

.notice-item.due-soon .notice-title {
  color: #e6a23c;
}

.no-notice {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 24px;
  color: #909399;
  font-size: 14px;
}

.no-notice .el-icon {
  font-size: 24px;
}
</style>
