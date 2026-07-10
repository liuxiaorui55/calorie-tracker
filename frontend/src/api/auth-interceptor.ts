import api from './index'

// 请求拦截：附加 Authorization 头
api.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = token
  }
  return config
})

export default api
