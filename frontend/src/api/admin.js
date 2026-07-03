/**
 * 管理后台模块API接口
 * 封装管理员相关的后端请求方法
 */
import request from './request'                             // 引入封装好的axios实例

/**
 * 获取仪表盘统计数据接口
 * @returns {Promise} 返回仪表盘数据（商品数、订单数、用户数、销售额等）
 */
export function getDashboard() {
  return request({
    url: '/admin/dashboard',                                // 请求路径
    method: 'get'                                           // 请求方法：GET
  })
}

/**
 * 获取用户列表（管理员）接口
 * @param {Object} params - 查询参数
 * @param {number} [params.page] - 页码
 * @param {number} [params.pageSize] - 每页条数
 * @returns {Promise} 返回用户列表数据
 */
export function getAdminUsers(params) {
  return request({
    url: '/admin/users',                                    // 请求路径
    method: 'get',                                          // 请求方法：GET
    params                                                  // URL查询参数
  })
}

/**
 * 更新用户信息（管理员）接口
 * @param {Object} data - 更新参数
 * @param {number} data.id - 用户ID
 * @param {string} [data.role] - 用户角色
 * @returns {Promise} 返回更新结果
 */
export function updateUser(data) {
  return request({
    url: '/admin/users',                                    // 请求路径
    method: 'put',                                          // 请求方法：PUT
    data                                                    // 请求体数据
  })
}

/**
 * 获取商品列表（管理员）接口
 * @param {Object} params - 查询参数
 * @param {number} [params.page] - 页码
 * @param {number} [params.pageSize] - 每页条数
 * @returns {Promise} 返回商品列表数据
 */
export function getAdminProducts(params) {
  return request({
    url: '/admin/products',                                 // 请求路径
    method: 'get',                                          // 请求方法：GET
    params                                                  // URL查询参数
  })
}

/**
 * 创建商品接口
 * @param {Object} data - 商品参数
 * @param {string} data.name - 商品名称
 * @param {string} data.description - 商品描述
 * @param {number} data.price - 商品价格
 * @param {number} data.stock - 库存数量
 * @param {string} data.image - 商品图片路径
 * @param {number} data.categoryId - 分类ID
 * @returns {Promise} 返回创建结果
 */
export function createProduct(data) {
  return request({
    url: '/admin/products',                                 // 请求路径
    method: 'post',                                         // 请求方法：POST
    data                                                    // 请求体数据
  })
}

/**
 * 更新商品接口
 * @param {Object} data - 更新参数
 * @param {number} data.id - 商品ID
 * @param {string} [data.name] - 商品名称
 * @param {string} [data.description] - 商品描述
 * @param {number} [data.price] - 商品价格
 * @param {number} [data.stock] - 库存数量
 * @param {string} [data.image] - 商品图片路径
 * @param {number} [data.categoryId] - 分类ID
 * @returns {Promise} 返回更新结果
 */
export function updateProduct(data) {
  return request({
    url: '/admin/products',                                 // 请求路径
    method: 'put',                                          // 请求方法：PUT
    data                                                    // 请求体数据
  })
}

/**
 * 删除商品接口
 * @param {number} id - 商品ID
 * @returns {Promise} 返回删除结果
 */
export function deleteProduct(id) {
  return request({
    url: '/admin/products',                                 // 请求路径
    method: 'delete',                                       // 请求方法：DELETE
    params: { id }                                          // URL参数：商品ID
  })
}

/**
 * 获取订单列表（管理员）接口
 * @param {Object} params - 查询参数
 * @param {number} [params.page] - 页码
 * @param {number} [params.pageSize] - 每页条数
 * @param {string} [params.status] - 订单状态筛选
 * @returns {Promise} 返回订单列表数据（包含统计信息）
 */
export function getAdminOrders(params) {
  return request({
    url: '/admin/orders',                                   // 请求路径
    method: 'get',                                          // 请求方法：GET
    params                                                  // URL查询参数
  })
}

/**
 * 更新订单状态（管理员）接口
 * @param {Object} data - 更新参数
 * @param {number} data.id - 订单ID
 * @param {string} data.status - 新状态
 * @returns {Promise} 返回更新结果
 */
export function updateOrderStatus(data) {
  return request({
    url: '/admin/orders/status',                            // 请求路径
    method: 'put',                                          // 请求方法：PUT
    data                                                    // 请求体数据
  })
}

/**
 * 获取分类列表（管理员）接口
 * @param {Object} params - 查询参数
 * @returns {Promise} 返回分类列表数据
 */
export function getAdminCategories(params) {
  return request({
    url: '/admin/categories',                               // 请求路径
    method: 'get',                                          // 请求方法：GET
    params                                                  // URL查询参数
  })
}

/**
 * 创建分类接口
 * @param {Object} data - 分类参数
 * @param {string} data.name - 分类名称
 * @returns {Promise} 返回创建结果
 */
export function createCategory(data) {
  return request({
    url: '/admin/categories',                               // 请求路径
    method: 'post',                                         // 请求方法：POST
    data                                                    // 请求体数据
  })
}

/**
 * 更新分类接口
 * @param {Object} data - 更新参数
 * @param {number} data.id - 分类ID
 * @param {string} [data.name] - 分类名称
 * @returns {Promise} 返回更新结果
 */
export function updateCategory(data) {
  return request({
    url: '/admin/categories',                               // 请求路径
    method: 'put',                                          // 请求方法：PUT
    data                                                    // 请求体数据
  })
}

/**
 * 删除分类接口
 * @param {number} id - 分类ID
 * @returns {Promise} 返回删除结果
 */
export function deleteCategory(id) {
  return request({
    url: '/admin/categories',                               // 请求路径
    method: 'delete',                                       // 请求方法：DELETE
    params: { id }                                          // URL参数：分类ID
  })
}
