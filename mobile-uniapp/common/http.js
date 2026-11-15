export const BASE_URL = 'http://192.168.3.197:8080/api'

export function getBackendOrigin() {
  return BASE_URL.replace(/\/api$/, '')
}

export function request(url, options = {}) {
  return new Promise((resolve, reject) => {
    uni.request({
      url: `${BASE_URL}${url}`,
      method: options.method || 'GET',
      data: options.data || {},
      header: {
        'Content-Type': 'application/json',
        ...(options.header || {})
      },
      success: (res) => {
        const body = res.data || {}
        if (body.code === 200) {
          resolve(body.data)
        } else {
          uni.showToast({
            title: body.message || '请求失败',
            icon: 'none'
          })
          reject(new Error(body.message || '请求失败'))
        }
      },
      fail: (err) => {
        uni.showToast({
          title: '网络异常',
          icon: 'none'
        })
        reject(err)
      }
    })
  })
}
