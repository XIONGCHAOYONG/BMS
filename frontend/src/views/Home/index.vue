<template>
  <div class="home">
    <div class="search">
      <el-input 
        class="search-input" 
        v-model="input" 
        placeholder="请输入内容" 
        clearable
        @keyup.enter="search"
      >
        <template #append>
          <el-button :icon="Search" @click="search">
            搜索
          </el-button>
        </template>
      </el-input>

      <!-- 搜索结果弹窗 -->
      <el-dialog
        v-model="showSearchDialog"
        title="搜索结果"
        width="800px"
        :close-on-click-modal="true"
      >
        <div v-if="searchLists.length > 0" class="search-results">
          <div 
            v-for="book in searchLists" 
            :key="book.bookId" 
            class="search-item"
            @click="$router.push(`/book/${book.bookId}`)"
          >
            <div class="search-book-cover" :style="{ backgroundColor: book.bgColor }">
              {{ book.title }}
            </div>
            <div class="search-book-info">
              <div class="search-book-title">{{ book.title }}</div>
              <div class="search-book-meta">
                <span>作者: {{ book.author }}</span>
              </div>
              <div class="search-book-status">
                <el-tag 
                  size="small" 
                  :type="book.availableStock > 0 ? 'success' : 'danger'"
                >
                  {{ book.availableStock > 0 ? '可借阅' : '已借完' }}
                </el-tag>
                <el-tag 
                  type="info" 
                  effect="dark" 
                  round 
                  :color="book.bgColor"
                >
                  {{ getCatgoryNameById(book.categoryId) }}
                </el-tag>
              </div>
              <div class="search-book-summary">
                {{ book.summary }}
              </div>
            </div>
          </div>
        </div>
        <div v-else class="no-results">
          <el-empty description="没有找到相关书籍" />
        </div>
      </el-dialog>
    </div>

    <div class="lun">
      <h3>热榜Top10</h3>
      <el-carousel :interval="2000" height="35vh">
        <el-carousel-item v-for="(group, groupIndex) in bookGroups" :key="groupIndex">
          <div class="book-group">
            <div v-for="(book, index) in group" :key="index" class="book-card"
              @click="$router.push(`/book/${book.bookId}`)">
              <div class="book-cover" :style="{ backgroundColor: book.bgColor }">
                {{ book.title }}
              </div>
              <div class="book-info">
                <div class="book-meta">
                  <div class="meta-row"><span>作者</span>: {{ book.author }}</div>
                  <div class="meta-row"><strong>ISBN</strong>: {{ book.isbn }}</div>
                  <div class="meta-row"><strong>出版社</strong>: {{ book.publisher }}</div>
                  <div class="meta-row andTag">
                    <span>
                      <strong>库存</strong>: {{ book.availableStock }}/{{ book.totalStock }}

                    </span>
                    <el-tag type="info" effect="dark" round :color="book.bgColor">
                      {{ getCatgoryNameById(book.categoryId) }}
                    </el-tag>
                  </div>
                </div>
                <div class="book-summary">
                  {{ book.summary }}
                </div>
              </div>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </div>


    <div class="new">
      <h3>最新上架</h3>
      <div class="new-books">
        <div v-for="book in newBook" :key="book.bookId" class="book-card" @click="$router.push(`/book/${book.bookId}`)">
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
                  {{ getCatgoryNameById(book.categoryId) }}
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
    <div class="lists">
      <h3>借阅排行</h3>
      <el-row :gutter="20">
        <el-col :span="8" v-for="(books, categoryId) in bookCategorys" :key="categoryId">
          <el-card class="list-card" shadow="hover">
            <template #header>
              <div class="category-header">
                <span class="category-name">{{ categories.find(c => c.categoryId === Number(categoryId))?.name }}</span>
                <el-tag type="danger">Top 10</el-tag>
              </div>
            </template>
            <div class="rank-list">
              <div v-for="(book, index) in books" :key="book.bookId" class="rank-item"
                @click="$router.push(`/book/${book.bookId}`)">
                <div class="rank-index" :class="{'top-three': index < 3}">{{ index + 1 }}</div>
                <div class="book-title">{{ book.title }}</div>
                <div class="book-heat">
                  <el-tooltip :content="'借阅次数: ' + book.totalLend">
                    <span>{{ book.totalLend }}次</span>
                  </el-tooltip>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>


  </div>
      <Footer></Footer>

</template>

<script lang="ts" setup>

import { ref, computed, onMounted } from 'vue';
import { Search } from '@element-plus/icons-vue';
import type { Book } from '@/api/Book';
import { getTop10,getTop10ByCategory,getNewBooks ,searchBooks} from '@/api/Book';
import { useBookStore } from '@/stores/bookStore';
import type { Category } from '@/api/Category';
import { getAllCategories } from '@/api/Category';
import { useCategoryStore } from '@/stores/categoryStore';
import { ElNotification } from 'element-plus';
import Footer from '@/components/Footer/index.vue';
interface DisplayBook extends Book {
  bgColor: string;
}


//search
const input = ref<string>('');
const searchLists = ref<DisplayBook[]>([]);
const showSearchDialog = ref(false);

const search = async () => {
  if (input.value.trim() === '') {
    ElNotification({
      title: '提示',
      message: '请输入搜索内容',
      type: 'warning',
    });
    return;
  }

  try {
    const res = await searchBooks(input.value.trim());
    if (res.data.code === 1) {
      searchLists.value = res.data.data.map((book: Book) => ({
        ...book,
        //根据book.id生成一个随机颜色
        bgColor: `hsl(${(book.bookId * 137.508) % 360}, 70%, 80%)`
      }));
      useBookStore().setBookMap(res.data.data);
      showSearchDialog.value = true; // 显示搜索结果弹窗
    } else {
      ElNotification({
        title: '错误',
        message: '搜索失败:' + res.data.message,
        type: 'error',
      });
    }
  } catch (error) {
    ElNotification({
      title: '错误',
      message: '搜索失败，请检查网络连接',
      type: 'error',
    });
  }
};





const bookStore = useBookStore();
const bookCategorys = ref<Record<number, Book[]>>({});
// top10本书数据，按照数据库表结构
const books = ref<DisplayBook[]>([]);
onMounted(async()=>{
  //缓存中有的话就不用请求
  if(bookStore.getTop10Books().length>0){
    books.value = bookStore.getTop10Books().map((book: Book) => ({
      ...book,
      //根据book.id生成一个随机颜色
      bgColor: `hsl(${(book.bookId * 137.508) % 360}, 70%, 80%)`
    }));
    return;
  }
const res =await getTop10();
  try{
    if(res.data.code===1){
    bookStore.setBookMap(res.data.data);
    // 为每本书生成一个随机背景颜色
    books.value = res.data.data.map((book: Book) => ({
      ...book,
      //根据book.id生成一个随机颜色
      bgColor: `hsl(${(book.bookId * 137.508) % 360}, 70%, 80%)`
    }));
    useBookStore().setTop10Books(res.data.data);
  }else{
    ElNotification({
      title: '错误',
      message: '获取书籍数据失败:'+res.data.message,
      type: 'error',
    });
  }
  }catch{
    ElNotification({
      title: '错误',
      message: "请检查网络!",
      type: 'error',
    });
  }
  
}) 

// 将书籍数据分组，每组5本
const bookGroups = computed(() => {
  const groups = [];
  for (let i = 0; i < books.value.length; i += 5) {
    groups.push(books.value.slice(i, i + 5));
  }
  return groups;
});


//分类
const categories=ref<Category[]>([]);
const categoryStore=useCategoryStore();
//根据id获得分类名称的函数
const getCatgoryNameById=(id:number):string=>{
  const category=categories.value.find(c=>c.categoryId===id);
  return category?category.name:"未知分类";
}
onMounted(async()=>{
  //优先使用缓存
  if(categoryStore.getCategories().length>0){
    categories.value=categoryStore.getCategories();
  }
  //获取分类
  else{
 try{
const res=await getAllCategories();
  if(res.data.code===1){
    categories.value=res.data.data;
    categoryStore.setCategories(res.data.data);
  }else{
    ElNotification({
      title: '错误',
      message: '获取分类失败:'+res.data.message,
      type: 'error',
    });
  }}catch{
    ElNotification({
      title: '错误',
      message: "请检查网络!",
      type: 'error',
    });
  }
  }
 

  //获取每个分类id下的10本书籍,根据借阅量排序
  for(const category of categories.value){
    try{  
      const res=await getTop10ByCategory(category.categoryId);
      if(res.data.code===1){
        bookCategorys.value[category.categoryId]=res.data.data;
        bookStore.setBookMap(res.data.data)
      }else{
        ElNotification({
          title: '错误',
          message: '获取分类下书籍失败:'+res.data.message,
          type: 'error',
        });
      }
    }catch{
      ElNotification({
        title: '错误',
        message: "请检查网络!",
        type: 'error',
      });
    }
  }
})

//10newbook
const newBook=ref<DisplayBook[]>([]);
onMounted(async()=>{
  //优先使用缓存
  if(bookStore.getNewBooks().length>0){
    newBook.value=bookStore.getNewBooks().map((book: Book) => ({
      ...book,
      //根据book.id生成一个随机颜色
      bgColor: `hsl(${(book.bookId * 137.508) % 360}, 70%, 80%)`
    }));
    return;
  }
  try{
  const res=await getNewBooks();
  if(res.data.code===1){
    bookStore.setBookMap(res.data.data)
    bookStore.setNewBooks(res.data.data)
    newBook.value=res.data.data.map((book: Book) => ({
      ...book,
      //根据book.id生成一个随机颜色
      bgColor: `hsl(${(book.bookId * 137.508) % 360}, 70%, 80%)`
    }));
  }else{
    ElNotification({
      title: '错误',
      message: '获取新书失败:'+res.data.message,
      type: 'error',
    });
  }}
    catch{
    ElNotification({
      title: '错误',
      message: "请检查网络!",
      type: 'error',
    });
  }
})

</script>

<style scoped>
.home {
  margin-top: 60px;
  background-color: #f0f2f5;
}

.search {
  height: 8vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: white;
  margin-bottom: 5px;
}

.search-input {
  width: 50%;
  max-width: 500px;
}

.search-input .el-input__wrapper {
  border-radius: 999px !important;
}

.search-results {
  max-height: 70vh;
  overflow-y: auto;
}

.search-item {
  display: flex;
  gap: 20px;
  padding: 15px;
  border-bottom: 1px solid #eee;
  cursor: pointer;
  transition: all 0.3s ease;
}

.search-item:hover {
  background-color: #f5f7fa;
}

.search-book-cover {
  flex: 0 0 100px;
  height: 140px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: bold;
  text-align: center;
  padding: 10px;
  font-size: 14px;
  word-break: break-word;
}

.search-book-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.search-book-title {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
}

.search-book-meta {
  display: flex;
  gap: 20px;
  color: #606266;
  font-size: 14px;
}

.search-book-status {
  display: flex;
  align-items: center;
  gap: 12px;
}

.stock-info {
  color: #606266;
  font-size: 14px;
}

.search-book-summary {
  color: #909399;
  font-size: 14px;
  margin-top: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-height: 1.5;
}

.no-results {
  padding: 40px 0;
}

.lun {
  height: 40vh;
  padding: 10px 20px;
  background-color: white;
  border-radius: 20px;
  width: 98vw;
  margin: 0 auto;
}

.book-group {
  display: flex;
  justify-content: space-between;
  height: 100%;
  padding: 10px 0;
  cursor: pointer;
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

.lists {
  padding: 10px 20px;
  background-color: white;
  min-height: 50vh;
  width: 98vw;
  border-radius: 20px;
  margin: 10px auto;
}


.lists .el-row {
  margin-bottom: 20px;
}

.list-card {
  height: 60vh;
  margin-bottom: 20px;
  border-radius: 10px;
}

.card-content {
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 18px;
  color: #666;
}

.category-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 5px;
}

.category-name {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.rank-list {
  height: 100%;
  overflow-y: auto;
}

.rank-item {
  display: flex;
  align-items: center;
  padding: 8px 12px;
  border-bottom: 1px solid #f0f0f0;
  transition: all 0.3s ease;
  cursor: pointer;
}

.rank-item:hover {
  background-color: #f5faf7;
  box-shadow: -2px 0 5px rgba(39, 214, 68, 0.1);
}

.rank-index {
  width: 24px;
  height: 24px;
  line-height: 24px;
  text-align: center;
  border-radius: 50%;
  background-color: #eee;
  margin-right: 12px;
  font-size: 14px;
  color: #666;
}

.rank-index.top-three {
  background-color: #ff4d4f;
  color: white;
}

.book-title {
  flex: 1;
  margin-right: 12px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  font-size: 14px;
  color: #333;
}

.book-heat {
  min-width: 60px;
  text-align: right;
  color: #ff4d4f;
  font-size: 13px;
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
  height: 35vh;
}
</style>
