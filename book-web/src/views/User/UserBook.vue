<template>
  <div class="home-container">
    <el-container>
      <!-- 侧边栏 -->
      <el-aside width="200px" class="sidebar">
        <div class="logo-section">
          <el-icon size="24" color="#409EFF"><Reading /></el-icon>
          <span class="system-title">图书管理系统</span>
        </div>

        <el-menu :default-active="activeMenu" router class="custom-menu">
          <el-menu-item index="/userhome">
            <el-icon><House /></el-icon>
            <span>主页</span>
          </el-menu-item>
          <el-menu-item index="/userbook">
            <el-icon><Notebook /></el-icon>
            <span>图书管理</span>
          </el-menu-item>
          <el-menu-item index="/login" class="logout-item">
            <el-button @click="removeToken" type="text" class="logout-btn">
              <el-icon><SwitchButton /></el-icon>
              <span>退出登录</span>
            </el-button>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <el-main class="main-content">
        <div class="header">
          <h2>📖 图书列表</h2>
          <div class="header-controls">
            <el-input
              v-model="searchKeyword"
              placeholder="输入书名或作者搜索"
              clearable
              class="search-input"
              @keyup.enter="loadBooks(1)"
            />
          </div>
        </div>

        <!-- 图书表格 -->
        <el-table :data="books" style="width: 100%" v-loading="loading" class="book-table">
          <el-table-column prop="bookName" label="书名" width="200"></el-table-column>
          <el-table-column prop="bookAuthor" label="作者" width="150"></el-table-column>
          <el-table-column prop="bookPrice" label="价格" width="100"></el-table-column>
          <el-table-column prop="bookDesc" label="简介"></el-table-column>
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.isBorrowed === 1 ? 'danger' : 'success'">
                {{ row.isBorrowed === 1 ? '已借出' : '可借阅' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="180">
            <template #default="{ row }">
              <el-button
                v-if="row.isBorrowed === 0"
                type="primary"
                size="small"
                @click="handleBorrow(row.id)"
              >
                借阅
              </el-button>
              <el-button
                v-else-if="row.borrowedByMe"
                type="warning"
                size="small"
                @click="handleReturn(row.id)"
              >
                归还
              </el-button>
              <el-button v-else disabled size="small">已借出</el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 -->
        <el-pagination
          background
          layout="prev, pager, next, total, jumper"
          :total="total"
          :page-size="pageSize"
          v-model:current-page="currentPage"
          @current-change="loadBooks"
          class="pagination"
        />
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

// 登出
const removeToken = () => {
  localStorage.removeItem('token')
  location.reload()
}

// 菜单高亮（注意：当前是 userbook 页面）
const activeMenu = ref('/userbook')

// 实时时间
const currentTime = ref('')
const updateTime = () => {
  const now = new Date()
  currentTime.value = now.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
    weekday: 'short',
  })
}
setInterval(updateTime, 1000)
onMounted(() => {
  updateTime()
})

// 分页与搜索
const searchKeyword = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const books = ref([])
const loading = ref(false)

// 加载图书列表
const loadBooks = async (page = currentPage.value) => {
  loading.value = true
  try {
    const res = await request.get('/book/bookInfo/list', {
      params: {
        keyword: searchKeyword.value || undefined,
        current: page,
        size: pageSize.value,
      },
    })
    books.value = res.data.records
    total.value = res.data.total
    currentPage.value = res.data.current
  } catch (err) {
    console.error('加载图书列表失败:', err)
  } finally {
    loading.value = false
  }
}

// 监听搜索关键词变化
watch(searchKeyword, () => {
  loadBooks(1)
})

// 借阅
const handleBorrow = async (bookId) => {
  try {
    await request.post('/book/bookInfo/borrow', null, { params: { bookId } })
    ElMessage.success('借阅成功')
    loadBooks()
  } catch (err) {
    ElMessage.error(err.response?.data?.message || '借阅失败，请稍后重试')
  }
}

// 归还
const handleReturn = async (bookId) => {
  try {
    await request.post('/book/bookInfo/return', null, { params: { bookId } })
    ElMessage.success('归还成功')
    loadBooks()
  } catch (err) {
    ElMessage.error(err.response?.data?.message || '归还失败，请稍后重试')
  }
}

// 初始化加载
onMounted(() => {
  loadBooks()
})
</script>

<style scoped>
.home-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  background-attachment: fixed;
}

.el-container {
  flex: 1;
  overflow: hidden;
}

.sidebar {
  background: linear-gradient(180deg, #2c3e50 0%, #3498db 100%);
  padding-top: 20px;
  box-shadow: 2px 0 10px rgba(0, 0, 0, 0.1);
  height: 100%;
}

.logo-section {
  height: 60px;
  display: flex;
  align-items: center;
  padding: 0 20px;
  border-bottom: 1px solid #263445;
  background-color: linear-gradient(180deg, #2c3e50 0%, #3498db 100%);
}

.system-title {
  color: #fff;
  font-size: 18px;
  font-weight: 600;
  margin-left: 10px;
}

.custom-menu {
  background: transparent;
  border: none;
  height: calc(100% - 80px);
  overflow-y: auto;
}

.custom-menu .el-menu-item {
  color: #ecf0f1;
  height: 50px;
  line-height: 50px;
  margin: 5px 10px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.custom-menu .el-menu-item:hover {
  background-color: rgba(255, 255, 255, 0.1);
  color: #ffffff;
}

.custom-menu .el-menu-item.is-active {
  background-color: rgba(52, 152, 219, 0.8);
  color: #ffffff;
  font-weight: bold;
}

.custom-menu .el-menu-item .el-icon {
  color: #ecf0f1;
  margin-right: 8px;
}

.custom-menu .el-menu-item.is-active .el-icon {
  color: #ffffff;
}

.logout-item {
  margin-top: 20px !important;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  padding-top: 10px !important;
}

.logout-btn {
  color: #ecf0f1 !important;
  font-size: 14px;
  padding: 0 !important;
  height: auto;
  line-height: inherit;
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: flex-start;
}

.logout-btn:hover {
  color: #ffffff !important;
  background-color: rgba(231, 76, 60, 0.2) !important;
}

.logout-btn .el-icon {
  margin-right: 8px;
}

/* 头部区域 */
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #e4e7ed;
  gap: 15px;
  flex-shrink: 0;
}

.header h2 {
  margin: 0;
  color: #333;
}

.header-controls {
  display: flex;
  align-items: center;
  gap: 10px;
}

.search-input {
  width: 300px;
}

/* 表格 */
.book-table {
  --el-table-row-hover-bg-color: #f5f7fa;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

/* 分页 */
.pagination {
  margin-top: 20px;
  text-align: center;
}

/* 响应式 */
@media (max-width: 768px) {
  .header-controls {
    justify-content: center;
  }
  .search-input {
    width: 100%;
  }
}
</style>

<!-- 隐藏滚动条 -->
<style>
html,
body,
#app {
  overflow: hidden;
  height: 100%;
  width: 100%;
}

.home-container::-webkit-scrollbar,
.el-aside::-webkit-scrollbar,
.el-main::-webkit-scrollbar {
  display: none;
}

html {
  scrollbar-width: none;
}
</style>
