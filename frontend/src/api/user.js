/**
 * 用户模块API接口
 * 封装用户相关的后端请求方法
 */
import request from './request'                             // 引入封装好的axios实例

/**
 * 用户登录接口
 * @param {Object} data - 登录参数
 * @param {string} data.username - 用户名
 * @param {string} data.password - 密码
 * @returns {Promise} 返回登录结果
 */
export function login(data) {
  return request({
    url: '/user/login',                                     // 请求路径
    method: 'post',                                         // 请求方法：POST
    data                                                    // 请求体数据
  })
}

/**
 * 用户注册接口
 * @param {Object} data - 注册参数
 * @param {string} data.username - 用户名
 * @param {string} data.password - 密码
 * @returns {Promise} 返回注册结果
 */
export function register(data) {
  return request({
    url: '/user/register',                                  // 请求路径
    method: 'post',                                         // 请求方法：POST
    data                                                    // 请求体数据
  })
}

/**
 * 获取用户信息接口
 * @returns {Promise} 返回用户信息
 */
export function getUserInfo() {
  return request({
    url: '/user/info',                                     // 请求路径
    method: 'get'                                           // 请求方法：GET
  })
}

/**
 * 更新用户信息接口
 * @param {Object} data - 更新参数
 * @param {string} [data.email] - 邮箱
 * @param {string} [data.phone] - 手机号
 * @param {string} [data.nickname] - 昵称
 * @param {string} [data.oldPassword] - 旧密码（修改密码时使用）
 * @param {string} [data.newPassword] - 新密码（修改密码时使用）
 * @returns {Promise} 返回更新结果
 */
export function updateUserInfo(data) {
  return request({
    url: '/user/update',                                    // 请求路径
    method: 'put',                                          // 请求方法：PUT
    data                                                    // 请求体数据
  })
}
