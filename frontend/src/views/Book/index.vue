<template>
  <div class="book">
    <div class="info">
      <el-descriptions direction="vertical" :column="3" border>
        <el-descriptions-item :rowspan="4" :width="80" label="封面" align="center">
          <div class="cover-placeholder" :style="{ backgroundColor: book?.bgColor }">
            <span>{{ book?.title }}</span>
          </div>
        </el-descriptions-item>

        <el-descriptions-item label="书名">{{ book?.title }}</el-descriptions-item>
        <el-descriptions-item label="作者">{{ book?.author }}</el-descriptions-item>
        <el-descriptions-item label="ISBN">{{ book?.isbn }}</el-descriptions-item>

        <el-descriptions-item label="出版社">{{ book?.publisher }}</el-descriptions-item>
        <el-descriptions-item label="出版日期">{{ book?.publishDate }}</el-descriptions-item>
        <el-descriptions-item label="分类">
                <el-tag type="info" effect="dark" round :color="book?.bgColor">
            {{ getCategoryName(book?.categoryId as number) }}
          </el-tag>

        </el-descriptions-item>

        <el-descriptions-item label="库存">
          <el-tag type="info">
            {{book?.availableStock}} / {{book?.totalStock}}
          </el-tag>
        </el-descriptions-item>

        <el-descriptions-item label="操作">
          <el-tag v-if="book?.status!==0" type="info">
            已下架,请联系管理员
          </el-tag>
          <el-tag v-else-if="book?.availableStock===0" type="info">
            库存不够,请联系管理员
          </el-tag>
          <el-tag v-else-if="isLending" type="info">
            您已借阅此书,不可重复借阅
          </el-tag>
          <el-button v-else type="primary" @click="showLendDialog=true">借阅</el-button>
        </el-descriptions-item>
        <el-descriptions-item label="简介" :span="2">{{ book?.summary }}</el-descriptions-item>
      </el-descriptions>

    </div>

  </div>

  <div class="new">
    <h3>相关推荐</h3>
    <div class="new-books">
      <div v-for="book in books" :key="book.bookId" class="book-card" @click="$router.push(`/book/${book.bookId}`)">
        <div class="book-cover" :style="{ backgroundColor: book.bgColor }">
          {{ book.title }}
        </div>
        <div class="book-info">
          <div class="book-meta">
            <div class="meta-row"><span>作者</span>:{{ book.author }}</div>
            <div class="meta-row"><strong>ISBN</strong>: {{ book.isbn }}</div>
            <div class="meta-row"><strong>出版社</strong>: {{ book.publisher }}</div>
            <div class="meta-row andTag">
              <span>
                <strong>库存</strong>: {{ book.availableStock }}/{{ book.totalStock }}

              </span>
              <el-tag type="info" effect="dark" round :color="book.bgColor">
                {{ getCategoryName(book.categoryId) }}
              </el-tag>
            </div>
          </div>
          <div class="book-summary">
            {{book.summary}}
          </div>
        </div>
      </div>
    </div>
  </div>



  <!-- 借阅对话框 -->
  <el-dialog v-model="showLendDialog" title="借阅确认" width="500px" :close-on-click-modal="false">
    <el-form ref="lendFormRef" :model="lendForm" :rules="lendRules" label-width="100px">
      <el-form-item label="书籍">
        <span>{{ book?.title }}</span>
      </el-form-item>
      <el-form-item label="借阅天数" prop="days">
        <el-input-number v-model="lendForm.days" :min="1" :max="60" :step="1" />
        <span class="days-hint">最长可借60天</span>
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="showLendDialog = false">取消</el-button>
        <el-button type="primary" @click="handleLend" :loading="lendLoading">
          确认借阅
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import type { Book } from '@/api/Book';
import { onMounted, ref,watch } from 'vue'
import { useBookStore } from '@/stores/bookStore';
import { useRoute,useRouter } from "vue-router";
import { getBookById ,getTop10ByCategory} from '@/api/Book';
import { ElNotification } from 'element-plus';
import { useCategoryStore } from '@/stores/categoryStore';
import { getAllCategories, type Category } from '@/api/Category';
const route = useRoute();
const router = useRouter();


//根据分类id获取分类名称、categories初始化
const categories=ref<Category[]>([])

const categoryStore = useCategoryStore()
onMounted(async()=>{
  if(categoryStore.getCategories().length===0){ 
      const res=await getAllCategories()
      if(res.data.code===1){
        categoryStore.setCategories(res.data.data)
        categories.value=categoryStore.getCategories()
      } else {
        ElNotification({
          title: '错误',
          message: '获取分类信息失败: ' + res.data.message,
          type: 'error',
        });
  }
}else{
    categories.value=categoryStore.getCategories()
  }
})

const getCategoryName=(id:number)=>{
  const category=categories.value.find(cat=>cat.categoryId===id)
  return category?category.name:'未知分类'
}

//根据分类id获取推荐书籍
interface DisplayBook extends Book {
  bgColor: string;
}
const books = ref<DisplayBook[]>([]);
const categoryId=ref(<number>(0))
const bookStore = useBookStore()



// 得到书本数据，根据id请求或者永缓存
const book = ref<DisplayBook>()
onMounted(async()=>{
  //得到路径中的id参数
  const id = Number(route.params.id); 
  if (!isNaN(id)) {
    // 判断 bookMap 里有没有
    if(bookStore.getBookMap()[id]){
      book.value = {
        ...bookStore.getBookMap()[id],
         bgColor: `hsl(${(bookStore.getBookMap()[id].bookId * 137.508) % 360}, 70%, 80%)`
      }
      categoryId.value=book.value.categoryId
    } else {
      // 没有就请求后端
        const res=await getBookById(id)
        if(res.data.code===1){
          let bookId=res.data.data.bookId
          book.value={
            ...res.data.data,
             bgColor: `hsl(${(bookId * 137.508) % 360}, 70%, 80%)`
          }
          categoryId.value=res.data.data.categoryId
        } else {
          ElNotification({
            title: '错误',
            message: '获取书本信息失败: ' + res.data.message,
            type: 'error',
          });
        }
    }
  } else {
    ElNotification({
      title: '错误',
      message: '无效的书本ID',
      type: 'error',
    });
    router.push('/books')
  }



  const res=await getTop10ByCategory(categoryId.value)
    if(res.data.code===1){
      //先筛选掉自己
      res.data.data=res.data.data.filter((b:Book)=>b.bookId!==book.value?.bookId)
      //只要前五个
      books.value=res.data.data.slice(0,5)
      books.value= books.value.map((book : DisplayBook)=>{
        // 随机生成背景色
      book.bgColor= `hsl(${(book.bookId * 137.508) % 360}, 70%, 80%)`
        return book;
      })
    } else {
      ElNotification({
        title: '错误',
        message: '获取推荐书籍失败: ' + res.data.message,
        type: 'error',
      });
    }
})


const loadBook=async()=>{ 
   //得到路径中的id参数
  const id = Number(route.params.id); 
  if (!isNaN(id)) {
    // 判断 bookMap 里有没有
    if(bookStore.getBookMap()[id]){
      book.value = {
        ...bookStore.getBookMap()[id],
         bgColor: `hsl(${(bookStore.getBookMap()[id].bookId * 137.508) % 360}, 70%, 80%)`
      }
      categoryId.value=book.value.categoryId
    } else {
      // 没有就请求后端
        const res=await getBookById(id)
        if(res.data.code===1){
          let bookId=res.data.data.bookId
          book.value={
            ...res.data.data,
             bgColor: `hsl(${(bookId * 137.508) % 360}, 70%, 80%)`
          }
          categoryId.value=res.data.data.categoryId
        } else {
          ElNotification({
            title: '错误',
            message: '获取书本信息失败: ' + res.data.message,
            type: 'error',
          });
        }
    }
  } else {
    ElNotification({
      title: '错误',
      message: '无效的书本ID',
      type: 'error',
    });
    router.push('/books')
  }


}

onMounted(() => {
  loadBook(); // 初始化加载一次
});

// ✅ 监听 id 变化
watch(
  () => route.params.id,
  () => {
    loadBook();
  }
);










const showLendDialog = ref(false)
const lendLoading = ref(false)
// 判断用户是否已借阅此书
const isLending = ref(false) 
// TODO: 根据实际用户借阅数据设置
// 借阅表单数据
const lendForm = ref({
  days: 7
})
const lendFormRef = ref()
// 借阅表单验证规则
const lendRules = {
  days: [
    { required: true, message: '请选择借阅天数', trigger: 'change' },
    { type: 'number', min: 1, max: 60, message: '借阅天数必须在1-60天之间', trigger: 'change' }
  ]
}

// 处理借阅
const handleLend = async () => {
  if (!lendFormRef.value) return
  
  try {
    // 表单验证
    await lendFormRef.value.validate()
    
    if (!book.value) {
      ElNotification({
        title: '错误',
        message: '书籍信息不存在',
        type: 'error'
      })
      return
    }

    // 检查库存
    if (book.value.availableStock <= 0) {
      ElNotification({
        title: '错误',
        message: '该书已无可借库存',
        type: 'error'
      })
      return
    }

    lendLoading.value = true

    // TODO: 调用借阅API
    // const res = await lendBook({
    //   bookId: book.value.bookId,
    //   days: lendForm.value.days,
    //   remarks: lendForm.value.remarks
    // })

    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))

    ElNotification({
      title: '成功',
      message: '借阅成功',
      type: 'success'
    })

    showLendDialog.value = false
    // 重置表单
    lendForm.value = {
      days: 7
    }

  } catch (error) {
    console.error('借阅失败:', error)
    ElNotification({
      title: '错误',
      message: '借阅失败，请重试',
      type: 'error'
    })
  } finally {
    lendLoading.value = false
  }
}

</script>

<style scoped>
.book {
  width: 95vw;
  height: 55vh;
  margin: 65px auto 10px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 20px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  align-items: center;
}

.info {
  width: 90vw;
  height: 52vh;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 10px;
}



.cover-placeholder {
  width: 70%;
  height: 25vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  text-align: center;
  border-radius: 8px;
  margin: 10px auto;
}

.cover-placeholder span {
  color: white;
  font-size: 18px;
  font-weight: bold;
  word-break: break-word;
}

:deep(.el-descriptions__label) {
  width: 120px;
  text-align: right;
}

:deep(.el-descriptions__content) {
  padding: 12px;
}

.recomend {
  width: 95vw;
  height: 20vh;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 10px;
}

.days-hint {
  margin-left: 10px;
  color: #909399;
  font-size: 14px;
}

:deep(.el-dialog__body) {
  padding: 20px 40px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}


.new {
  padding: 10px 20px;
  margin: 10px auto;
  width: 98vw;
  border-radius: 20px;
  background-color: white;

}

.new h3 {
  margin-bottom: 15px;
}

.new-books {
  display: flex;
  justify-content: space-between;
  gap: 20px;
  padding: 10px 0;
}

.new .book-card {
  flex: 0 0 19%;
  height: 26vh;
}
.book-cover {
  font-weight: bold;
  height: 50%;
  color: #fff;
  border-radius: 8px;
  display: flex;
  justify-content: center;
  align-items: center;
  text-align: center;
  word-break: break-word;
  padding: 10px;
  margin-bottom: 10px;
  font-size: 16px;
}

.book-card {
  flex: 0 0 19%;
  height: 100%;
  display: flex;
  flex-direction: column;
  background: #fff;
  padding: 12px;
  border-radius: 10px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  cursor: pointer;
}

.book-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
}


.book-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.book-meta {
  display: flex;
  flex-direction: column;
  font-size: 13px;
  color: #777;
  margin-bottom: 6px;
}

.meta-row {
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.andTag {
  display: flex;
  justify-content: space-between;

}

.meta-row strong {
  color: #111;
  font-weight: bold;
  font-size: 14px;
}


.book-summary {
  margin-top: auto;
  font-size: 14px;
  color: grey;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  line-clamp: 1;
  -webkit-box-orient: vertical;
  line-height: 1.4;
}

.book-meta strong {
  font-weight: 600;
  color: #333;
  margin-right: 2px;
}
</style>