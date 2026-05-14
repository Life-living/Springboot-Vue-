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
          <el-menu-item index="/adminhome">
            <el-icon><House /></el-icon>
            <span>主页</span>
          </el-menu-item>
          <el-menu-item index="/adminbook">
            <el-icon><Notebook /></el-icon>
            <span>图书管理</span>
          </el-menu-item>
          <el-menu-item index="/adminborrow">
            <el-icon><Collection /></el-icon>
            <span>借阅记录</span>
          </el-menu-item>
          <el-menu-item index="/admindata">
            <el-icon><DataAnalysis /></el-icon>
            <span>数据查询</span>
          </el-menu-item>
          <el-menu-item index="/adminuser">
            <el-icon><User /></el-icon>
            <span>用户管理</span>
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
        <!-- 借阅列表标题 -->
        <div class="header">
          <h2>📖 借阅记录列表</h2>
        </div>

        <!-- 借阅记录表格 -->
        <el-table :data="records" style="width: 100%" v-loading="loading" class="borrow-table">
          <el-table-column prop="bookName" label="书名" width="200"></el-table-column>
          <el-table-column prop="bookAuthor" label="作者" width="150"></el-table-column>
          <el-table-column label="借阅时间" width="180">
            <template #default="{ row }">
              {{ formatDate(row.borrowTime) }}
            </template>
          </el-table-column>
          <el-table-column label="归还时间" width="180">
            <template #default="{ row }">
              <span v-if="row.returnTime">{{ formatDate(row.returnTime) }}</span>
              <el-tag v-else type="danger">未归还</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.returnTime ? 'success' : 'warning'">
                {{ row.returnTime ? '已归还' : '借阅中' }}
              </el-tag>
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
          @current-change="loadRecords"
          class="pagination"
        />
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'

// 登出
const removeToken = () => {
  localStorage.removeItem('token')
  location.reload()
}

// 菜单高亮
const activeMenu = ref('/adminborrow')

// 分页与数据
const currentPage = ref(1)
const pageSize = ref(8)
const total = ref(0)
const records = ref([])
const loading = ref(false)

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  if (isNaN(date.getTime())) return ''
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
  })
}

// 加载借阅记录
const loadRecords = async (page = currentPage.value) => {
  loading.value = true
  try {
    const res = await request.get('/book/borrow/list', {
      params: { current: page, size: pageSize.value },
    })
    records.value = res.data.records || []
    total.value = res.data.total || 0
    currentPage.value = res.data.current || page
  } catch (err) {
    records.value = []
    total.value = 0
    console.error('加载借阅记录失败:', err)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadRecords()
})
</script>

<style scoped>
.home-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4edf5 100%);
}

.el-container {
  flex: 1;
  overflow: hidden; /* 防止内部溢出影响布局 */
}

.sidebar {
  background: linear-gradient(180deg, #2c3e50 0%, #3498db 100%);
  padding-top: 20px;
  box-shadow: 2px 0 10px rgba(0, 0, 0, 0.1);
  height: 100%;
  max-height: 100%;
  overflow: hidden; /* 禁止滚动条 */
  position: relative;
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

/* 自定义菜单样式 */
.custom-menu {
  background: transparent;
  border: none;
  height: 100%;
  overflow: hidden; /* 双重保险 */
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

/* 退出登录按钮特殊样式 */
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

/* 借阅列表标题 */
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #e4e7ed;
  gap: 15px;
}

.header h2 {
  margin: 0;
  color: #333;
}

.borrow-table {
  --el-table-row-hover-bg-color: #f5f7fa;
  border-radius: 8px;
  overflow: hidden;
}

.pagination {
  margin-top: 20px;
  text-align: center;
}
</style>
