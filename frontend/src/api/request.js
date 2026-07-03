/**
 * Axios请求封装工具
 * 统一配置请求基础URL、超时时间、请求头
 * 实现请求拦截器（自动注入用户ID）和响应拦截器（统一错误处理）
 */
import axios from 'axios'                                   // 引入axios库
import { ElMessage } from 'element-plus'                    // 引入消息提示组件
import router from '../router'                              // 引入路由，用于登录过期时重定向

/**
 * 创建axios实例并配置默认参数
 */
const request = axios.create({
  baseURL: '/api',                                          // 请求基础URL，所有请求都会带上此前缀
  timeout: 15000,                                          // 请求超时时间，15秒
  headers: { 'Content-Type': 'application/json;charset=utf-8' } // 默认请求头，JSON格式
})

/**
 * 请求拦截器
 * 在发送请求前执行，自动注入用户ID到Authorization请求头
 * @param {Object} config - 请求配置对象
 * @returns {Object} 修改后的配置对象
 */
request.interceptors.request.use(
  (config) => {
    // 从localStorage获取用户ID
    const userId = localStorage.getItem('userId')
    // 如果存在用户ID，注入到Authorization请求头
    if (userId) config.headers['Authorization'] = userId
    return config
  },
  (error) => Promise.reject(error)                          // 请求错误时直接拒绝Promise
)

/**
 * 响应拦截器
 * 在接收响应后执行，统一处理错误码和异常情况
 * @param {Object} response - 响应对象
 * @returns {Object|Promise} 成功时返回响应数据，失败时返回拒绝的Promise
 */
request.interceptors.response.use(
  (response) => {
    const res = response.data                               // 获取响应体中的数据
    // 业务成功状态码（后端返回200或0表示成功）
    if (res.code === 200 || res.code === 0) return res
    // 业务失败时显示错误消息
    ElMessage.error(res.message || '请求失败')
    // 401表示未登录或登录过期，清除本地存储并重定向到登录页
    if (res.code === 401) {
      localStorage.removeItem('userId')
      localStorage.removeItem('userInfo')
      router.push('/login')
    }
    return Promise.reject(new Error(res.message || '请求失败'))
  },
  (error) => {
    // HTTP状态码错误处理
    if (error.response) {
      const s = error.response.status
      if (s === 401) { 
        localStorage.removeItem('userId')
        router.push('/login')
        ElMessage.error('登录已过期') 
      }
      else if (s === 403) { ElMessage.error('没有权限访问') }
      else if (s === 404) { ElMessage.error('请求的资源不存在') }
      else { ElMessage.error('服务器错误 (' + s + ')') }
    } else if (error.code === 'ECONNABORTED') {
      // 请求超时处理
      ElMessage.error('请求超时，请稍后重试')
    } else {
      // 网络错误处理（如断网）
      ElMessage.error('网络错误，请检查网络连接')
    }
    return Promise.reject(error)
  }
)

// 导出封装好的axios实例，供其他API模块使用
export default request
