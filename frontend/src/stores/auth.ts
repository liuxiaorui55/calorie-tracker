import { defineStore } from 'pinia'
import { ref } from 'vue'
import api from '@/api/index'

export const useAuthStore = defineStore('auth', () => {
  const user = ref<any>(null)
  const token = ref(localStorage.getItem('token') || '')

  // 是否已登录
  const isLoggedIn = () => !!token.value

  // 登录
  async function login(username: string, password: string) {
    const data = await api.post('/user/login', { username, password })
    token.value = data.token
    localStorage.setItem('token', data.token)
    user.value = { id: data.id, username: data.username, gender: data.gender, age: data.age }
    return data
  }

  // 注册
  async function register(username: string, password: string, gender: string, age: number | null) {
    await api.post('/user/register', { username, password, gender, age })
  }

  // 获取当前用户信息
  async function fetchUser() {
    if (!token.value) return
    try {
      const data = await api.get('/user/info')
      user.value = data
    } catch {
      logout()
    }
  }

  // 登出
  async function logout() {
    try {
      await api.post('/user/logout')
    } catch { /* ignore */ }
    token.value = ''
    user.value = null
    localStorage.removeItem('token')
  }

  return { user, token, isLoggedIn, login, register, fetchUser, logout }
})
