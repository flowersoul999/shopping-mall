import request from './request'

export function getAddressList() {
  return request({
    url: '/address',
    method: 'get'
  })
}

export function addAddress(data) {
  return request({
    url: '/address',
    method: 'post',
    data
  })
}

export function updateAddress(data) {
  return request({
    url: '/address',
    method: 'put',
    data
  })
}

export function deleteAddress(id) {
  return request({
    url: '/address',
    method: 'delete',
    params: { id }
  })
}
