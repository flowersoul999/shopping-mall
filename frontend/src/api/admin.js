import request from './request'

export function getDashboard() {
  return request({
    url: '/admin/dashboard',
    method: 'get'
  })
}

export function getAdminUsers(params) {
  return request({
    url: '/admin/users',
    method: 'get',
    params
  })
}

export function updateUser(data) {
  return request({
    url: '/admin/users',
    method: 'put',
    data
  })
}

export function getAdminProducts(params) {
  return request({
    url: '/admin/products',
    method: 'get',
    params
  })
}

export function createProduct(data) {
  return request({
    url: '/admin/products',
    method: 'post',
    data
  })
}

export function updateProduct(data) {
  return request({
    url: '/admin/products',
    method: 'put',
    data
  })
}

export function deleteProduct(id) {
  return request({
    url: '/admin/products',
    method: 'delete',
    params: { id }
  })
}

export function getAdminOrders(params) {
  return request({
    url: '/admin/orders',
    method: 'get',
    params
  })
}

export function updateOrderStatus(data) {
  return request({
    url: '/admin/orders/status',
    method: 'put',
    data
  })
}

export function getAdminCategories(params) {
  return request({
    url: '/admin/categories',
    method: 'get',
    params
  })
}

export function createCategory(data) {
  return request({
    url: '/admin/categories',
    method: 'post',
    data
  })
}

export function updateCategory(data) {
  return request({
    url: '/admin/categories',
    method: 'put',
    data
  })
}

export function deleteCategory(id) {
  return request({
    url: '/admin/categories',
    method: 'delete',
    params: { id }
  })
}
