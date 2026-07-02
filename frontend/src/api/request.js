import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'

const request = axios.create({
  baseURL: '/api',
  timeout: 15000,
  headers: { 'Content-Type': 'application/json;charset=utf-8' }
})

// 请求拦截器：自动注入用户ID到Authorization头
request.interceptors.request.use(
  (config) => {
    const userId = localStorage.getItem('userId')
    if (userId) config.headers['Authorization'] = userId
    return config
  },
  (error) => Promise.reject(error)
)

// 响应拦截器：统一处理错误码
request.interceptors.response.use(
  (response) => {
    const res = response.data
    if (res.code === 200 || res.code === 0) return res
    ElMessage.error(res.message || '请求失败')
    if (res.code === 401) {
      localStorage.removeItem('userId')
      localStorage.removeItem('userInfo')
      router.push('/login')
    }
    return Promise.reject(new Error(res.message || '请求失败'))
  },
  (error) => {
    if (error.response) {
      const s = error.response.status
      if (s === 401) { localStorage.removeItem('userId'); router.push('/login'); ElMessage.error('登录已过期') }
      else if (s === 403) { ElMessage.error('没有权限访问') }
      else if (s === 404) { ElMessage.error('请求的资源不存在') }
      else { ElMessage.error('服务器错误 (' + s + ')') }
    } else if (error.code === 'ECONNABORTED') {
      ElMessage.error('请求超时，请稍后重试')
    } else {
      ElMessage.error('网络错误，请检查网络连接')
    }
    return Promise.reject(error)
  }
)

export default request
