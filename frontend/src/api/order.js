/**
 * 订单模块API接口
 * 封装订单相关的后端请求方法
 */
import request from './request'                             // 引入封装好的axios实例

/**
 * 创建订单接口
 * @param {Object} data - 订单参数
 * @param {number} data.addressId - 收货地址ID
 * @param {string} [data.remark] - 订单备注
 * @returns {Promise} 返回创建的订单信息
 */
export function createOrder(data) {
  return request({
    url: '/order/create',                                   // 请求路径
    method: 'post',                                         // 请求方法：POST
    data                                                    // 请求体数据
  })
}

/**
 * 获取用户订单列表接口
 * @param {Object} params - 查询参数
 * @param {number} [params.page] - 页码
 * @param {number} [params.pageSize] - 每页条数
 * @param {string} [params.status] - 订单状态筛选
 * @returns {Promise} 返回订单列表数据
 */
export function getOrderList(params) {
  return request({
    url: '/order/list',                                     // 请求路径
    method: 'get',                                          // 请求方法：GET
    params                                                  // URL查询参数
  })
}

/**
 * 获取订单详情接口
 * @param {number|string} id - 订单ID
 * @returns {Promise} 返回订单详情数据
 */
export function getOrderDetail(id) {
  return request({
    url: '/order/detail',                                   // 请求路径
    method: 'get',                                          // 请求方法：GET
    params: { id }                                          // 订单ID参数
  })
}

/**
 * 管理员获取订单列表接口
 * @param {Object} params - 查询参数
 * @param {number} [params.page] - 页码
 * @param {number} [params.pageSize] - 每页条数
 * @param {string} [params.status] - 订单状态筛选
 * @returns {Promise} 返回订单列表数据（包含统计信息）
 */
export function getAdminOrderList(params) {
  return request({
    url: '/order/admin/list',                               // 请求路径
    method: 'get',                                          // 请求方法：GET
    params                                                  // URL查询参数
  })
}

/**
 * 更新订单状态接口
 * @param {Object} data - 更新参数
 * @param {number} data.id - 订单ID
 * @param {string} data.status - 新状态（pending/paid/shipped/completed/cancelled）
 * @returns {Promise} 返回更新结果
 */
export function updateOrderStatus(data) {
  return request({
    url: '/order/updateStatus',                             // 请求路径
    method: 'put',                                          // 请求方法：PUT
    data                                                    // 请求体数据
  })
}
