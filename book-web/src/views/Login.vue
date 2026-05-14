<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <div class="text-center">
          <h2 style="color: red">图书管理系统</h2>
        </div>
      </template>

      <el-form
        :model="form"
        :rules="rules"
        ref="loginFormRef"
        label-width="0"
        @submit.prevent="handleLogin"
      >
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" prefix-icon="User" />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            class="login-btn"
            native-type="submit"
            :loading="loading"
            style="width: 100%"
          >
            登录
          </el-button>
        </el-form-item>

        <div class="link-row">
          <el-link type="primary" @click="goToRegister">注册账号</el-link>
          <el-link type="primary" @click="goToForget">忘记密码</el-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import { useRouter } from 'vue-router'

export default {
  name: 'Login',
  setup() {
    const router = useRouter()
    const loginFormRef = ref(null)
    const loading = ref(false)
    const form = ref({
      username: '',
      password: '',
    })

    const rules = {
      username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
      password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        // { min: 6, message: '密码至少6位', trigger: 'blur' },
      ],
    }

    const handleLogin = async () => {
      await loginFormRef.value.validate(async (valid) => {
        if (valid) {
          loading.value = true
          try {
            const res = await request.post('/user/login', {
              username: form.value.username,
              password: form.value.password,
            })
            console.log(res)
            // res.data 就是 token 字符串
            localStorage.setItem('token', res.data)
            ElMessage.success('登录成功！')
            if (form.value.username === 'admin') {
              router.push('/adminhome') // 跳转Admin界面
            } else {
              localStorage.setItem('username', form.value.username)
              router.push('/userhome') // 跳转User界面
            }
          } catch (err) {
            // 错误已在拦截器处理
          } finally {
            loading.value = false
          }
        }
      })
    }

    // 注册账号跳转
    const goToRegister = () => {
      router.push('/register')
    }

    // 忘记密码跳转
    const goToForget = () => {
      router.push('/forget') // 确保路径匹配上面定义的
    }

    return {
      form,
      rules,
      loginFormRef,
      loading,
      handleLogin,
      goToRegister,
      goToForget,
    }
  },
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-image: url('@/assets/Image.jpg');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
}

.login-card {
  width: 400px;
  background-color: rgba(255, 255, 255, 0.85) !important; /* 半透明白色背景 */
  border: 2px solid #409eff !important; /* 添加蓝色边框 */
  border-radius: 12px !important; /* 圆角 */
  box-shadow: 0 8px 20px rgba(64, 158, 255, 0.3) !important; /* 轻微阴影 */
}

/* 移除 el-card header 的默认下边框 */
.login-card ::v-deep(.el-card__header) {
  border-bottom: 1px solid #ebeef5 !important;
}

/* 确保标题居中且无多余间距 */
.login-card ::v-deep(.el-card__header) h2 {
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

.login-btn {
  margin-top: 10px;
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
