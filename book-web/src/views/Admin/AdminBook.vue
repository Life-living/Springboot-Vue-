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
        <!-- 搜索 + 标题 + 新增按钮 -->
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
            <el-button type="primary" size="small" @click="openAddDialog">新增图书</el-button>
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
          <el-table-column label="操作" width="220">
            <template #default="{ row }">
              <el-button size="small" @click="openEditDialog(row)">编辑</el-button>
              <el-button
                size="small"
                type="primary"
                v-if="row.isBorrowed === 0"
                @click="handleBorrow(row.id)"
              >
                借阅
              </el-button>
              <el-button size="small" type="warning" v-else @click="handleReturn(row.id)">
                归还
              </el-button>
              <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
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

        <!-- 新增/编辑对话框 -->
        <el-dialog :title="dialogTitle" v-model="dialogVisible" width="500px" class="book-dialog">
          <el-form
            :model="bookForm"
            :rules="formRules"
            ref="bookFormRef"
            label-width="80px"
            label-position="left"
          >
            <el-form-item label="书名" prop="bookName">
              <el-input v-model="bookForm.bookName" />
            </el-form-item>
            <el-form-item label="作者" prop="bookAuthor">
              <el-input v-model="bookForm.bookAuthor" />
            </el-form-item>
            <el-form-item label="价格" prop="bookPrice">
              <el-input-number
                v-model="bookForm.bookPrice"
                :precision="2"
                :min="0"
                style="width: 100%"
              />
            </el-form-item>
            <el-form-item label="简介">
              <el-input v-model="bookForm.bookDesc" type="textarea" />
            </el-form-item>
          </el-form>
          <template #footer>
            <el-button @click="dialogVisible = false" size="small">取消</el-button>
            <el-button type="primary" size="small" @click="saveBook">确定</el-button>
          </template>
        </el-dialog>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue' // 添加 computed
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

// 登出
const removeToken = () => {
  localStorage.removeItem('token')
  location.reload()
}

// 菜单高亮
const activeMenu = ref('/adminbook')

// 分页与搜索
const searchKeyword = ref('')
const currentPage = ref(1)
const pageSize = ref(7)
const total = ref(0)
const books = ref([])
const loading = ref(false)

// 表单相关
const dialogVisible = ref(false)
const isEditing = ref(false)
const bookFormRef = ref(null)
const bookForm = ref({
  id: null, // 注意：这里假设后端返回的是 'id' 而非 'Id'
  bookName: '',
  bookAuthor: '',
  bookPrice: null,
  bookDesc: '',
  isBorrowed: 0,
})

// 定义表单规则
const formRules = {
  bookName: [{ required: true, message: '请输入书名', trigger: 'blur' }],
  bookAuthor: [{ required: true, message: '请输入作者', trigger: 'blur' }],
  bookPrice: [{ required: true, message: '请输入价格', trigger: 'blur' }],
}

// 获取对话框标题 (使用 computed)
const dialogTitle = computed(() => {
  return isEditing.value ? '编辑图书' : '新增图书'
})

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
    // 错误已在拦截器处理
    console.error('加载图书列表失败:', err) // 可选：添加错误日志
  } finally {
    loading.value = false
  }
}

// 监听搜索关键词变化，自动搜索
watch(searchKeyword, () => {
  loadBooks(1) // 每次搜索都从第一页开始
})

// 打开新增对话框
const openAddDialog = () => {
  isEditing.value = false
  bookForm.value = {
    id: null,
    bookName: '',
    bookAuthor: '',
    bookPrice: null,
    bookDesc: '',
    isBorrowed: 0,
  }
  dialogVisible.value = true
}

// 打开编辑对话框
const openEditDialog = (row) => {
  isEditing.value = true
  bookForm.value = { ...row } // 这里会保留 row 的所有属性，包括 id
  dialogVisible.value = true
}

// 保存图书（新增或更新）
const saveBook = async () => {
  if (!bookFormRef.value) return // 确保表单引用存在

  try {
    // 先验证表单
    const isValid = await bookFormRef.value.validate()
    if (!isValid) return // 如果验证失败则不继续

    if (isEditing.value) {
      await request.post('/book/bookInfo/update', bookForm.value)
    } else {
      await request.post('/book/bookInfo/add', bookForm.value)
    }
    ElMessage.success(isEditing.value ? '修改成功' : '添加成功')
    dialogVisible.value = false
    loadBooks() // 刷新数据
  } catch (err) {
    // 拦截器已处理错误，但可以在这里添加额外的日志或处理
    console.error('保存图书失败:', err)
  }
}

// 删除图书
const handleDelete = (id) => {
  ElMessageBox.confirm('确定要删除这本书吗？', '提示', {
    type: 'warning',
  })
    .then(async () => {
      try {
        await request.delete(`/book/bookInfo/delete/${id}`)
        ElMessage.success('删除成功')
        loadBooks()
      } catch (err) {
        // 拦截器已处理
        console.error('删除图书失败:', err)
      }
    })
    .catch(() => {
      // 用户取消操作，无需处理
    })
}

// 借阅
const handleBorrow = async (bookId) => {
  try {
    // 使用 params 发送查询参数
    await request.post('/book/bookInfo/borrow', null, {
      params: { bookId },
    })
    ElMessage.success('借阅成功')
    loadBooks()
  } catch (err) {
    console.error('借阅失败:', err)
    // 可以给用户更友好的提示
    ElMessage.error(err.response?.data?.message || '借阅失败，请稍后重试')
  }
}

// 归还
const handleReturn = async (bookId) => {
  try {
    // 使用 params 发送查询参数
    await request.post('/book/bookInfo/return', null, {
      params: { bookId },
    })
    ElMessage.success('归还成功')
    loadBooks()
  } catch (err) {
    console.error('归还失败:', err)
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
  background: linear-gradient(135deg, #f5f7fa 0%, #e4edf5 100%);
  overflow: hidden; /* 禁止整个容器滚动 */
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

/* 主内容区：关键修改点 */
.main-content {
  background-color: #ffffff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  overflow: hidden; /* 不允许滚动 */
  height: 100%;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
}

/* 表格区域：限制高度并禁止滚动 */
.book-table {
  --el-table-row-hover-bg-color: #f5f7fa;
  border-radius: 4px;
  overflow: hidden;
  flex: 1;
  /* 强制表格不产生滚动 */
}

/* 分页区域 */
.pagination {
  margin-top: 20px;
  text-align: center;
  flex-shrink: 0; /* 防止被压缩 */
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
  width: 240px;
}

/* 对话框 */
.book-dialog ::v-deep .el-dialog__body {
  padding: 20px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header {
    flex-direction: column;
    align-items: stretch;
    gap: 10px;
  }

  .header-controls {
    flex-wrap: wrap;
    justify-content: flex-start;
  }

  .search-input {
    width: 100%;
  }
}

/* ========== 彻底隐藏所有滚动条 ========== */
/* Webkit 浏览器（Chrome, Edge, Safari） */
::-webkit-scrollbar {
  display: none !important;
}

/* Firefox */
html {
  scrollbar-width: none !important;
}

/* 所有可能滚动的 Element Plus 容器也禁用 */
.el-main,
.el-aside,
.el-container,
.el-table__body-wrapper {
  overflow: hidden !important;
}
</style>
