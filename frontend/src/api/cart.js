import request from './request'

export function getCartList() {
  return request({
    url: '/cart',
    method: 'get'
  })
}

export function addToCart(data) {
  return request({
    url: '/cart',
    method: 'post',
    data
  })
}

export function updateCartItem(id, data) {
  return request({
    url: '/cart',
    method: 'put',
    params: { id },
    data
  })
}

export function deleteCartItem(id) {
  return request({
    url: '/cart',
    method: 'delete',
    params: { id }
  })
}

export function clearCart() {
  return request({
    url: '/cart/clear',
    method: 'delete'
  })
}
