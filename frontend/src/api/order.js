import request from './request'

export function createOrder(data) {
  return request({
    url: '/order/create',
    method: 'post',
    data
  })
}

export function getOrderList(params) {
  return request({
    url: '/order/list',
    method: 'get',
    params
  })
}

export function getOrderDetail(id) {
  return request({
    url: '/order/detail',
    method: 'get',
    params: { id }
  })
}

export function updateOrderStatus(data) {
  return request({
    url: '/order/updateStatus',
    method: 'put',
    data
  })
}
