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

      <el-main>
        <div class="header">
          <h2>用户管理</h2>
        </div>

        <!-- 用户列表 -->
        <el-table :data="users" style="width: 100%" border>
          <el-table-column type="index" label="序号" width="80" />
          <el-table-column prop="username" label="用户名" />
          <el-table-column prop="id" label="用户ID" width="100" />
          <el-table-column label="操作" width="120">
            <template #default="{ row }">
              <el-button
                v-if="row.username !== 'admin'"
                size="small"
                type="danger"
                @click="handleDelete(row)"
              >
                删除
              </el-button>
              <span v-else>--</span>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 -->
        <div class="mt-4 text-right">
          <el-pagination layout="total" :total="users.length" />
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

// 登出
const removeToken = () => {
  localStorage.removeItem('token')
  location.reload()
}

// 当前激活菜单
const activeMenu = ref('/adminuser')

// 用户列表数据
const users = ref([])

// 获取用户列表
const fetchUsers = async () => {
  try {
    const res = await request.get('/user/info')
    console.log('✅ 接口返回数据:', res)

    if (res.data && Array.isArray(res.data)) {
      users.value = res.data.map((user) => ({
        id: user.id,
        username: user.username,
      }))
    } else {
      ElMessage.error('返回数据格式异常')
    }
  } catch (err) {
    console.error('❌ 获取用户失败:', err)
    ElMessage.error('获取用户列表失败')
  }
}

// 删除用户
const handleDelete = (user) => {
  ElMessageBox.confirm(`确定要删除用户 "${user.username}" 吗？`, '警告', {
    confirmButtonText: '删除',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      try {
        const res = await request.delete(`/user/delete/${user.id}`)
        if (res.success) {
          ElMessage.success('删除成功')
          fetchUsers() // 刷新列表
        } else {
          ElMessage.error(res.message || '删除失败')
        }
      } catch (err) {
        console.error('❌ 删除请求失败:', err)
        ElMessage.error('网络错误或无法连接服务器')
      }
    })
    .catch(() => {
      ElMessage.info('已取消删除')
    })
}

// 组件挂载后获取数据
onMounted(() => {
  fetchUsers()
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

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.search-form {
  margin-bottom: 20px;
}
.mt-4 {
  margin-top: 16px;
}
.text-right {
  text-align: right;
}
</style>
