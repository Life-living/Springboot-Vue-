<!-- src/views/ForgetPassword.vue -->
<template>
  <div class="forget-container">
    <el-card class="forget-card">
      <template #header>
        <div class="text-center">
          <h2 style="color: red">忘记密码</h2>
        </div>
      </template>

      <el-form
        :model="form"
        :rules="rules"
        ref="forgetFormRef"
        label-width="0"
        @submit.prevent="handleReset"
      >
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" prefix-icon="User" />
        </el-form-item>

        <el-form-item prop="newPassword">
          <el-input
            v-model="form.newPassword"
            type="password"
            placeholder="请输入新密码"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>

        <el-form-item prop="confirmPassword">
          <el-input
            v-model="form.confirmPassword"
            type="password"
            placeholder="请确认新密码"
            prefix-icon="Check"
            show-password
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            class="reset-btn"
            native-type="submit"
            :loading="loading"
            style="width: 100%"
          >
            重置密码
          </el-button>
        </el-form-item>

        <div class="link-row">
          <el-link type="primary" @click="goToLogin">返回登录</el-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'
import { useRouter } from 'vue-router'

export default {
  name: 'ForgetPassword',
  setup() {
    const router = useRouter()
    const forgetFormRef = ref(null)
    const loading = ref(false)

    const form = ref({
      username: '',
      newPassword: '',
      confirmPassword: '',
    })

    // 表单验证规则
    const validateConfirm = (rule, value, callback) => {
      if (value !== form.value.newPassword) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }

    const rules = {
      username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
      newPassword: [
        { required: true, message: '请输入新密码', trigger: 'blur' },
        { min: 6, message: '密码至少6位', trigger: 'blur' },
      ],
      confirmPassword: [
        { required: true, message: '请确认新密码', trigger: 'blur' },
        { validator: validateConfirm, trigger: 'blur' },
      ],
    }

    // 提交重置密码
    const handleReset = async () => {
      await forgetFormRef.value.validate(async (valid) => {
        if (valid) {
          loading.value = true
          try {
            const res = await request.post('/user/forget', {
              username: form.value.username,
              newPassword: form.value.newPassword,
            })
            ElMessage.success(res.message || '密码重置成功！')

            // 成功后跳转到登录页
            await ElMessageBox.alert('密码已重置，请使用新密码登录。', '提示', {
              confirmButtonText: '去登录',
              type: 'success',
            })
            router.push('/login')
          } catch (err) {
            // 错误已在拦截器处理，或可在此补充提示
            console.error('密码重置失败:', err)
          } finally {
            loading.value = false
          }
        }
      })
    }

    // 返回登录页
    const goToLogin = () => {
      router.push('/login')
    }

    return {
      form,
      rules,
      forgetFormRef,
      loading,
      handleReset,
      goToLogin,
    }
  },
}
</script>

<style scoped>
.forget-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-image: url('@/assets/Image.jpg');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
}

.forget-card {
  width: 400px;
  background-color: rgba(255, 255, 255, 0.85) !important; /* 半透明白色背景 */
  border: 2px solid #409eff !important; /* 添加蓝色边框 */
  border-radius: 12px !important; /* 圆角 */
  box-shadow: 0 8px 20px rgba(64, 158, 255, 0.3) !important; /* 轻微阴影 */
}

/* 移除 el-card header 的默认下边框 */
.forget-card ::v-deep(.el-card__header) {
  border-bottom: 1px solid #ebeef5 !important;
}

/* 确保标题居中且无多余间距 */
.forget-card ::v-deep(.el-card__header) h2 {
  margin: 0;
  padding: 16px 0;
  text-align: center;
  color: #e60000; /* 保持红色 */
  font-weight: bold;
}

.link-row {
  display: flex;
  justify-content: flex-end;
  padding: 0 20px;
  margin-top: 10px;
}

.reset-btn {
  margin-top: 10px;
}
</style>
