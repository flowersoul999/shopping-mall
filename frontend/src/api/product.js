import request from './request'

export function getProductList(params) {
  return request({
    url: '/product/list',
    method: 'get',
    params
  })
}

export function searchProducts(keyword, params) {
  return request({
    url: '/product/search',
    method: 'get',
    params: { keyword, ...params }
  })
}

export function getProductDetail(id) {
  return request({
    url: `/product/detail`,
    method: 'get',
    params: { id }
  })
}

export function getCategoryList() {
  return request({
    url: '/category/list',
    method: 'get'
  })
}
