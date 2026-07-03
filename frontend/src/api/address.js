/**
 * 收货地址模块API接口
 * 封装地址相关的后端请求方法
 */
import request from './request'                             // 引入封装好的axios实例

/**
 * 获取用户收货地址列表接口
 * @returns {Promise} 返回地址列表数据
 */
export function getAddressList() {
  return request({
    url: '/address',                                        // 请求路径
    method: 'get'                                           // 请求方法：GET
  })
}

/**
 * 添加收货地址接口
 * @param {Object} data - 地址参数
 * @param {string} data.name - 收货人姓名
 * @param {string} data.phone - 手机号码
 * @param {string} data.province - 省份
 * @param {string} data.city - 城市
 * @param {string} data.district - 区县
 * @param {string} data.detail - 详细地址
 * @param {boolean} [data.isDefault] - 是否设为默认地址
 * @returns {Promise} 返回添加结果
 */
export function addAddress(data) {
  return request({
    url: '/address',                                        // 请求路径
    method: 'post',                                         // 请求方法：POST
    data                                                    // 请求体数据
  })
}

/**
 * 更新收货地址接口
 * @param {Object} data - 更新参数
 * @param {number} data.id - 地址ID
 * @param {string} [data.name] - 收货人姓名
 * @param {string} [data.phone] - 手机号码
 * @param {string} [data.province] - 省份
 * @param {string} [data.city] - 城市
 * @param {string} [data.district] - 区县
 * @param {string} [data.detail] - 详细地址
 * @param {boolean} [data.isDefault] - 是否设为默认地址
 * @returns {Promise} 返回更新结果
 */
export function updateAddress(data) {
  return request({
    url: '/address',                                        // 请求路径
    method: 'put',                                          // 请求方法：PUT
    data                                                    // 请求体数据
  })
}

/**
 * 删除收货地址接口
 * @param {number} id - 地址ID
 * @returns {Promise} 返回删除结果
 */
export function deleteAddress(id) {
  return request({
    url: '/address',                                        // 请求路径
    method: 'delete',                                       // 请求方法：DELETE
    params: { id }                                          // URL参数：地址ID
  })
}
