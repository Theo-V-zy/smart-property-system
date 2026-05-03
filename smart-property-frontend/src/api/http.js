import axios from 'axios'
import { showFailToast } from 'vant'

const http = axios.create({
  baseURL: '/api',
  timeout: 12000
})

http.interceptors.request.use((config) => {
  const token = localStorage.getItem('smart-property-token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

http.interceptors.response.use(
  (response) => {
    const payload = response.data
    if (payload.code !== 200) {
      showFailToast(payload.message || '请求失败')
      return Promise.reject(new Error(payload.message || '请求失败'))
    }
    return payload.data
  },
  (error) => {
    const message = error.response?.data?.message || error.message || '网络异常'
    showFailToast(message)
    if (error.response?.status === 401) {
      localStorage.removeItem('smart-property-token')
      localStorage.removeItem('smart-property-user')
      if (location.pathname !== '/login') {
        location.href = '/login'
      }
    }
    return Promise.reject(error)
  }
)

export default http
