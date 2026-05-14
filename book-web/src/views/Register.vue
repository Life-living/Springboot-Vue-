<template>
  <div class="register-container">
    <el-card class="register-card">
      <!-- 标题 -->
      <template #header>
        <div class="text-center">
          <h2 style="color: red">注册新账号</h2>
        </div>
      </template>

      <el-form
        :model="form"
        :rules="rules"
        ref="registerFormRef"
        label-width="0"
        @submit.prevent="handleRegister"
      >
        <!-- 用户名 -->
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" prefix-icon="User" />
        </el-form-item>

        <!-- 密码 -->
        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>

        <!-- 确认密码 -->
        <el-form-item prop="confirmPassword">
          <el-input
            v-model="form.confirmPassword"
            type="password"
            placeholder="请确认密码"
            prefix-icon="Check"
            show-password
          />
        </el-form-item>

        <!-- 按钮 -->
        <el-form-item>
          <el-button
            type="primary"
            class="register-btn"
            native-type="submit"
            :loading="loading"
            style="width: 100%"
          >
            注册
          </el-button>
        </el-form-item>

        <!-- 返回登录 -->
        <div class="text-center">
          <el-link type="primary" @click="goToLogin">已有账号？去登录</el-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import { useRouter } from 'vue-router'

const router = useRouter()
const registerFormRef = ref(null)
const loading = ref(false)

// 表单数据
const form = ref({
  username: '',
  password: '',
  confirmPassword: '',
})

// 自定义确认密码校验
const validateConfirmPassword = (rule, value, callback) => {
  if (value !== form.value.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

// 表单规则
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符之间', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少 6 位', trigger: 'blur' },
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' },
  ],
}

// 注册处理
const handleRegister = async () => {
  await registerFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const res = await request.post('/user/register', {
          username: form.value.username,
          password: form.value.password,
        })

        // 假设后端返回 success: true 表示成功
        if (res.success) {
          ElMessage.success('注册成功！请登录')
          router.push('/login') // 跳转到登录页
        } else {
          ElMessage.error(res.message || '注册失败')
        }
      } catch (err) {
        // 错误已在拦截器中提示
      } finally {
        loading.value = false
      }
    }
  })
}

// 跳转登录
const goToLogin = () => {
  router.push('/login')
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-image: url('@/assets/Image.jpg');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
}

.register-card {
  width: 400px;
  background-color: rgba(255, 255, 255, 0.85) !important; /* 半透明白色背景 */
  border: 2px solid #409eff !important; /* 添加蓝色边框 */
  border-radius: 12px !important; /* 圆角 */
  box-shadow: 0 8px 20px rgba(64, 158, 255, 0.3) !important; /* 轻微阴影 */
}

/* 移除 el-card header 的默认下边框 */
.register-card ::v-deep(.el-card__header) {
  border-bottom: 1px solid #ebeef5 !important;
}

/* 确保标题居中且无多余间距 */
.register-card ::v-deep(.el-card__header) h2 {
  margin: 0;
  padding: 16px 0;
  text-align: center;
  color: #e60000; /* 保持红色 */
  font-weight: bold;
}

.link-row {
  display: flex;
  justify-content: space-between;
  padding: 0 20px;
  margin-top: 10px;
}

.register-btn {
  margin-top: 10px;
}

.text-center {
  text-align: center;
}
</style>
