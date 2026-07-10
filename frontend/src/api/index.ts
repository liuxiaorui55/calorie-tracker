import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 响应拦截：提取 data 字段
api.interceptors.response.use(
  res => {
    if (res.data.code === 200) {
      return res.data.data
    }
    return Promise.reject(new Error(res.data.message || '请求失败'))
  },
  err => Promise.reject(err)
)

export default api
