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

      <!-- 主内容区 -->
      <el-main class="main-content">
        <!-- 标题栏 -->
        <div class="header">
          <h2>📊 数据概览</h2>
        </div>

        <!-- 统计卡片 -->
        <el-row :gutter="20" class="stats-cards" justify="center">
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-card shadow="hover" class="stat-card card-1">
              <div class="card-decoration"></div>
              <el-icon size="24"><Reading /></el-icon>
              <div class="stat-info">
                <div class="stat-value">{{ total }}</div>
                <div class="stat-label">总借阅记录</div>
              </div>
            </el-card>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-card shadow="hover" class="stat-card card-2">
              <div class="card-decoration"></div>
              <el-icon size="24" color="#e6a23c"><Timer /></el-icon>
              <div class="stat-info">
                <div class="stat-value">{{ notReturnedCount }}</div>
                <div class="stat-label">未归还书籍</div>
              </div>
            </el-card>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-card shadow="hover" class="stat-card card-3">
              <div class="card-decoration"></div>
              <el-icon size="24" color="#67c23a"><Finished /></el-icon>
              <div class="stat-info">
                <div class="stat-value">{{ returnedCount }}</div>
                <div class="stat-label">已归还书籍</div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import request from '@/utils/request'

const removeToken = () => {
  localStorage.removeItem('token')
  location.reload()
}

const activeMenu = ref('/admindata')

const total = ref(0)
const records = ref([])

const notReturnedCount = computed(() => records.value.filter((r) => !r.returnTime).length)
const returnedCount = computed(() => records.value.filter((r) => r.returnTime).length)

const loadRecordsForStats = async () => {
  try {
    const res = await request.get('/book/borrow/list', {
      params: { current: 1, size: 1000 },
    })
    records.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (err) {
    records.value = []
    total.value = 0
    console.error('加载借阅记录失败:', err)
  }
}

onMounted(() => {
  loadRecordsForStats()
})
</script>

<style scoped>
/* ========== 全局容器 ========== */
.home-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-image: url('https://images.unsplash.com/photo-1507842217343-583bb7270b66?ixlib=rb-1.2.1&auto=format&fit=crop&w=1950&q=80');
  background-size: cover;
  background-position: center;
  background-attachment: fixed;
}

.el-container {
  flex: 1;
  overflow: hidden;
}

/* ========== 侧边栏 ========== */
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

.sidebar {
  background: linear-gradient(180deg, #2c3e50 0%, #3498db 100%);
  padding-top: 20px;
  box-shadow: 2px 0 10px rgba(0, 0, 0, 0.1);
  height: 100%;
  overflow: hidden;
}

.custom-menu {
  background: transparent;
  border: none;
  height: 100%;
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

/* ========== 主内容区 ========== */
.main-content {
  background-color: transparent;
  padding: 20px;
  overflow-y: auto;
  height: 100%;
  box-sizing: border-box;
}

.header {
  margin-bottom: 24px;
  padding-bottom: 15px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
}

.header h2 {
  margin: 0;
  color: white;
  font-size: 22px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
  font-weight: 600;
}

/* ========== 统计卡片 ========== */
.stats-cards {
  min-height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-wrap: wrap;
  gap: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 20px;
  width: 100%;
  max-width: 300px;
  border-radius: 16px;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  border: none;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
}

.stat-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.25);
  background: rgba(255, 255, 255, 1);
}

.card-decoration {
  position: absolute;
  top: 0;
  right: 0;
  width: 80px;
  height: 80px;
  border-radius: 0 0 0 80px;
  opacity: 0.15;
  z-index: 0;
}

.card-1 .card-decoration {
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
}

.card-2 .card-decoration {
  background: linear-gradient(135deg, #e6a23c 0%, #ebb563 100%);
}

.card-3 .card-decoration {
  background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
}

.stat-card .el-icon {
  margin-right: 16px;
  flex-shrink: 0;
  position: relative;
  z-index: 1;
}

.stat-info {
  flex: 1;
  position: relative;
  z-index: 1;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  margin-bottom: 4px;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

.card-1 .stat-value {
  color: #409eff;
}

.card-2 .stat-value {
  color: #e6a23c;
}

.card-3 .stat-value {
  color: #67c23a;
}

.stat-label {
  font-size: 14px;
  color: #606266;
  margin-top: 6px;
  font-weight: 500;
}

/* ========== 响应式 ========== */
@media (max-width: 768px) {
  .stats-cards {
    padding: 0 10px;
  }

  .stat-card {
    max-width: 100%;
  }

  .main-content {
    padding: 15px;
  }
}
</style>
