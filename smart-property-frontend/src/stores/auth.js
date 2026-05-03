import { defineStore } from 'pinia'
import { getCurrentUserApi, loginApi, logoutApi } from '../api'

const TOKEN_KEY = 'smart-property-token'
const USER_KEY = 'smart-property-user'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem(TOKEN_KEY) || '',
    user: JSON.parse(localStorage.getItem(USER_KEY) || 'null')
  }),
  getters: {
    isOwner: (state) => state.user?.role === 'OWNER',
    isStaff: (state) => state.user?.role === 'STAFF'
  },
  actions: {
    saveAuth(token, user) {
      this.token = token
      this.user = user
      localStorage.setItem(TOKEN_KEY, token)
      localStorage.setItem(USER_KEY, JSON.stringify(user))
    },
    clearAuth() {
      this.token = ''
      this.user = null
      localStorage.removeItem(TOKEN_KEY)
      localStorage.removeItem(USER_KEY)
    },
    async login(payload) {
      const data = await loginApi(payload)
      this.saveAuth(data.token, data.user)
      return data
    },
    async fetchCurrentUser() {
      try {
        const user = await getCurrentUserApi()
        this.user = user
        localStorage.setItem(USER_KEY, JSON.stringify(user))
        return user
      } catch (error) {
        this.clearAuth()
        throw error
      }
    },
    async logout() {
      try {
        await logoutApi()
      } finally {
        this.clearAuth()
      }
    }
  }
})
