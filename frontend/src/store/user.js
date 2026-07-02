import { defineStore } from 'pinia'
import { login as loginApi, register as registerApi, getUserInfo as getUserInfoApi } from '../api/user'
import { ElMessage } from 'element-plus'
import router from '../router'

export const useUserStore = defineStore('user', {
  state: () => ({
    userInfo: JSON.parse(localStorage.getItem('userInfo') || 'null'),
    userId: localStorage.getItem('userId') || ''
  }),
  getters: {
    isLoggedIn: (state) => !!state.userId,
    isAdmin: (state) => state.userInfo?.role === 'admin'
  },
  actions: {
    async login(credentials) {
      try {
        const res = await loginApi(credentials)
        if (res.code === 200) {
          const userData = res.data
          const userId = String(userData.id || '')
          this.userId = userId
          this.userInfo = userData
          localStorage.setItem('userId', userId)
          localStorage.setItem('userInfo', JSON.stringify(userData))
          ElMessage.success('登录成功')
          const redirect = router.currentRoute.value.query.redirect || '/'
          router.push(redirect)
          return true
        }
        return false
      } catch (error) {
        return false
      }
    },
    async register(data) {
      try {
        const res = await registerApi(data)
        if (res.code === 200) {
          ElMessage.success('注册成功，请登录')
          router.push('/login')
          return true
        }
        return false
      } catch (error) {
        return false
      }
    },
    logout() {
      this.userId = ''
      this.userInfo = null
      localStorage.removeItem('userId')
      localStorage.removeItem('userInfo')
      ElMessage.success('已退出登录')
      router.push('/login')
    },
    async fetchUserInfo() {
      try {
        const res = await getUserInfoApi()
        if (res.code === 200) {
          this.userInfo = res.data
          localStorage.setItem('userInfo', JSON.stringify(res.data))
        }
      } catch (error) {}
    }
  }
})
