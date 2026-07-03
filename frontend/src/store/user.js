/**
 * 用户状态管理Store
 * 使用Pinia管理用户登录状态、用户信息等全局状态
 * 实现登录、注册、退出、获取用户信息等功能
 */
import { defineStore } from 'pinia'                         // 引入Pinia的defineStore方法
import { login as loginApi, register as registerApi, getUserInfo as getUserInfoApi } from '../api/user' // 引入用户API
import { ElMessage } from 'element-plus'                    // 引入消息提示组件
import router from '../router'                              // 引入路由

/**
 * 创建用户Store
 * @param {string} 'user' - Store名称，用于Pinia内部标识
 * @param {Object} options - Store配置对象
 */
export const useUserStore = defineStore('user', {
  /**
   * 状态定义
   * 存储用户相关的数据
   */
  state: () => ({
    /**
     * 用户详细信息对象
     * 从localStorage读取，页面刷新后保持状态
     */
    userInfo: JSON.parse(localStorage.getItem('userInfo') || 'null'),
    /**
     * 用户ID
     * 从localStorage读取，用于请求时的Authorization头
     */
    userId: localStorage.getItem('userId') || ''
  }),

  /**
   * 计算属性（Getters）
   * 根据state计算派生值，具有缓存特性
   */
  getters: {
    /**
     * 判断用户是否已登录
     * @returns {boolean} true表示已登录，false表示未登录
     */
    isLoggedIn: (state) => !!state.userId,
    /**
     * 判断用户是否为管理员
     * @returns {boolean} true表示管理员，false表示普通用户
     */
    isAdmin: (state) => state.userInfo?.role === 'admin'
  },

  /**
   * 动作方法（Actions）
   * 用于修改state、调用API、执行异步操作
   */
  actions: {
    /**
     * 用户登录方法
     * @param {Object} credentials - 登录凭证
     * @param {string} credentials.username - 用户名
     * @param {string} credentials.password - 密码
     * @returns {boolean} true表示登录成功，false表示登录失败
     */
    async login(credentials) {
      try {
        // 调用登录API
        const res = await loginApi(credentials)
        if (res.code === 200) {
          const userData = res.data
          // 确保用户ID为字符串类型
          const userId = String(userData.id || '')
          // 更新state中的用户ID和用户信息
          this.userId = userId
          this.userInfo = userData
          // 将用户信息持久化到localStorage，防止页面刷新丢失
          localStorage.setItem('userId', userId)
          localStorage.setItem('userInfo', JSON.stringify(userData))
          // 显示登录成功提示
          ElMessage.success('登录成功')
          // 获取重定向路径（登录前的页面），默认为首页
          const redirect = router.currentRoute.value.query.redirect || '/'
          // 跳转到目标页面
          router.push(redirect)
          return true
        }
        return false
      } catch (error) {
        // 登录失败，返回false
        return false
      }
    },

    /**
     * 用户注册方法
     * @param {Object} data - 注册数据
     * @param {string} data.username - 用户名
     * @param {string} data.password - 密码
     * @returns {boolean} true表示注册成功，false表示注册失败
     */
    async register(data) {
      try {
        // 调用注册API
        const res = await registerApi(data)
        if (res.code === 200) {
          // 显示注册成功提示
          ElMessage.success('注册成功，请登录')
          // 跳转到登录页面
          router.push('/login')
          return true
        }
        return false
      } catch (error) {
        // 注册失败，返回false
        return false
      }
    },

    /**
     * 用户退出登录方法
     * 清除用户状态并跳转到登录页面
     */
    logout() {
      // 清空state中的用户ID和用户信息
      this.userId = ''
      this.userInfo = null
      // 清除localStorage中的用户数据
      localStorage.removeItem('userId')
      localStorage.removeItem('userInfo')
      // 显示退出成功提示
      ElMessage.success('已退出登录')
      // 跳转到登录页面
      router.push('/login')
    },

    /**
     * 获取用户信息方法
     * 用于页面刷新后重新加载用户信息
     */
    async fetchUserInfo() {
      try {
        // 调用获取用户信息API
        const res = await getUserInfoApi()
        if (res.code === 200) {
          // 更新state中的用户信息
          this.userInfo = res.data
          // 将最新用户信息持久化到localStorage
          localStorage.setItem('userInfo', JSON.stringify(res.data))
        }
      } catch (error) {
        // 获取失败时不做处理，保持静默
      }
    }
  }
})
