import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  // 重定义路径‘/’
  { path: '/', redirect: '/login' },

  { path: '/login', component: () => import('@/views/Login.vue') },
  { path: '/register', component: () => import('@/views/Register.vue') },
  { path: '/forget', component: () => import('@/views/ForgetPassword.vue') },

  // 管理员
  { path: '/adminhome', component: () => import('@/views/Admin/AdminHome.vue') },
  { path: '/adminbook', component: () => import('@/views/Admin/AdminBook.vue') },
  { path: '/adminborrow', component: () => import('@/views/Admin/AdminBorrow.vue') },
  { path: '/admindata', component: () => import('@/views/Admin/AdminData.vue') },
  { path: '/adminuser', component: () => import('@/views/Admin/AdminUser.vue') },

  // 用户
  { path: '/userhome', component: () => import('@/views/User/UserHome.vue') },
  { path: '/userbook', component: () => import('@/views/User/UserBook.vue') },
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: routes,
})

// 添加全局前置守卫，判断是否登录
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const publicPages = ['/login', '/register', '/forget']

  if (publicPages.includes(to.path)) {
    next()
  } else {
    token ? next() : next('/login')
  }
})

export default router
