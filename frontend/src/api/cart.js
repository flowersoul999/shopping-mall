/**
 * 购物车模块API接口
 * 封装购物车相关的后端请求方法
 */
import request from './request'                             // 引入封装好的axios实例

/**
 * 获取购物车列表接口
 * @returns {Promise} 返回购物车列表数据
 */
export function getCartList() {
  return request({
    url: '/cart',                                           // 请求路径
    method: 'get'                                           // 请求方法：GET
  })
}

/**
 * 添加商品到购物车接口
 * @param {Object} data - 添加参数
 * @param {number} data.productId - 商品ID
 * @param {number} [data.quantity] - 数量，默认为1
 * @returns {Promise} 返回添加结果
 */
export function addToCart(data) {
  return request({
    url: '/cart',                                           // 请求路径
    method: 'post',                                         // 请求方法：POST
    data                                                    // 请求体数据
  })
}

/**
 * 更新购物车商品数量接口
 * @param {number} id - 购物车项ID
 * @param {Object} data - 更新参数
 * @param {number} data.quantity - 新的数量
 * @returns {Promise} 返回更新结果
 */
export function updateCartItem(id, data) {
  return request({
    url: '/cart',                                           // 请求路径
    method: 'put',                                          // 请求方法：PUT
    params: { id },                                         // URL参数：购物车项ID
    data                                                    // 请求体数据
  })
}

/**
 * 删除购物车商品接口
 * @param {number} id - 购物车项ID
 * @returns {Promise} 返回删除结果
 */
export function deleteCartItem(id) {
  return request({
    url: '/cart',                                           // 请求路径
    method: 'delete',                                       // 请求方法：DELETE
    params: { id }                                          // URL参数：购物车项ID
  })
}

/**
 * 清空购物车接口
 * @returns {Promise} 返回清空结果
 */
export function clearCart() {
  return request({
    url: '/cart/clear',                                     // 请求路径
    method: 'delete'                                        // 请求方法：DELETE
  })
}
