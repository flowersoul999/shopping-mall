/**
 * 商品模块API接口
 * 封装商品相关的后端请求方法
 */
import request from './request'                             // 引入封装好的axios实例

/**
 * 获取商品列表接口
 * @param {Object} params - 查询参数
 * @param {number} [params.page] - 页码
 * @param {number} [params.pageSize] - 每页条数
 * @param {number} [params.categoryId] - 分类ID
 * @returns {Promise} 返回商品列表数据
 */
export function getProductList(params) {
  return request({
    url: '/product/list',                                   // 请求路径
    method: 'get',                                          // 请求方法：GET
    params                                                  // URL查询参数
  })
}

/**
 * 搜索商品接口
 * @param {string} keyword - 搜索关键词
 * @param {Object} params - 额外查询参数
 * @param {number} [params.page] - 页码
 * @param {number} [params.pageSize] - 每页条数
 * @returns {Promise} 返回搜索结果
 */
export function searchProducts(keyword, params) {
  return request({
    url: '/product/search',                                 // 请求路径
    method: 'get',                                          // 请求方法：GET
    params: { keyword, ...params }                          // 合并关键词和其他参数
  })
}

/**
 * 获取商品详情接口
 * @param {number|string} id - 商品ID
 * @returns {Promise} 返回商品详情数据
 */
export function getProductDetail(id) {
  return request({
    url: `/product/detail`,                                 // 请求路径
    method: 'get',                                          // 请求方法：GET
    params: { id }                                          // 商品ID参数
  })
}

/**
 * 获取商品分类列表接口
 * @returns {Promise} 返回分类列表数据
 */
export function getCategoryList() {
  return request({
    url: '/category/list',                                  // 请求路径
    method: 'get'                                           // 请求方法：GET
  })
}
