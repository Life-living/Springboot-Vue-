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
        <!-- 欢迎区域（保留） -->
        <div class="welcome-banner">
          <h1>📚 用户前台 · 主页</h1>
          <p>
            你好，<strong>{{ username }}</strong
            >！当前时间：<span id="current-time">{{ currentTime }}</span>
          </p>
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

// 登出
const removeToken = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('username')
  console.log(localStorage.removeItem('token'))
  console.log(localStorage.removeItem('username'))
}

// 👇 从 localStorage 获取用户名
const username = ref(localStorage.getItem('username') || '用户')
console.log(username.value)

// 菜单高亮
const activeMenu = ref('/adminhome')

// 实时时间（保留）
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
</script>

<style scoped>
.home-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-image: url('@/assets/Image1.png');
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
  max-height: 100%;
  overflow: hidden;
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

.custom-menu {
  background: transparent;
  border: none;
  height: 100%;
  overflow: hidden;
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

/* 欢迎横幅（保留） */
.welcome-banner {
  margin-bottom: 24px;
  padding: 20px;
  background: linear-gradient(to right, #409eff, #66b1ff);
  color: white;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.2);
}

.welcome-banner h1 {
  margin: 0 0 8px;
  font-size: 24px;
  font-weight: 600;
}

.welcome-banner p {
  margin: 0;
  opacity: 0.9;
  font-size: 14px;
}
</style>

<!-- 非 scoped：隐藏全局滚动条 -->
<style>
/* 隐藏页面滚动条（Chrome, Safari, Edge） */
html,
body,
#app {
  overflow: hidden;
  height: 100%;
  width: 100%;
}

/* 隐藏滚动条轨道（视觉上） */
.home-container::-webkit-scrollbar,
.el-aside::-webkit-scrollbar,
.el-main::-webkit-scrollbar {
  display: none;
}

/* Firefox 隐藏滚动条 */
html {
  scrollbar-width: none; /* Firefox */
}
</style>
