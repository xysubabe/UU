/**
 * HTTP请求封装
 */
const app = getApp()

/**
 * 基础请求方法
 */
function request(url, method = 'GET', data = {}, needAuth = true) {
  const header = {
    'content-type': 'application/json'
  }

  // 添加token
  if (needAuth && app.getToken()) {
    header['Authorization'] = `Bearer ${app.getToken()}`
  }

  return new Promise((resolve, reject) => {
    wx.request({
      url: app.globalData.baseUrl + url,
      method: method,
      data: data,
      header: header,
      success: (res) => {
        if (res.statusCode === 200) {
          resolve(res.data)
        } else {
          reject(res.data)
        }
      },
      fail: (err) => {
        wx.showToast({
          title: '网络错误',
          icon: 'none'
        })
        reject(err)
      }
    })
  })
}

/**
 * GET请求
 */
export function get(url, data = {}, needAuth = true) {
  return request(url, 'GET', data, needAuth)
}

/**
 * POST请求
 */
export function post(url, data = {}, needAuth = true) {
  return request(url, 'POST', data, needAuth)
}

/**
 * PUT请求
 */
export function put(url, data = {}, needAuth = true) {
  return request(url, 'PUT', data, needAuth)
}

/**
 * DELETE请求
 */
export function del(url, data = {}, needAuth = true) {
  return request(url, 'DELETE', data, needAuth)
}

export default {
  get,
  post,
  put,
  del
}