import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useUserStore = defineStore('user', () => {
  // State
  const userInfo = ref(null)
  const token = ref(localStorage.getItem('token') || '')
  const balance = ref(0)

  // Getters
  const isLoggedIn = computed(() => !!token.value)
  const username = computed(() => userInfo.value?.username || '')
  const avatar = computed(() => userInfo.value?.avatar || '')

  // Actions
  const login = async (credentials) => {
    // 模拟登录
    const mockUser = {
      id: 1,
      username: credentials.username || 'user001',
      email: 'user@example.com',
      avatar: '',
      phone: '138****8888'
    }

    userInfo.value = mockUser
    token.value = 'mock_token_' + Date.now()
    balance.value = 1000.00

    localStorage.setItem('token', token.value)
    localStorage.setItem('userInfo', JSON.stringify(mockUser))

    return { success: true }
  }

  const logout = () => {
    userInfo.value = null
    token.value = ''
    balance.value = 0
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  const recharge = (amount) => {
    balance.value += amount
    return { success: true, balance: balance.value }
  }

  const initUser = () => {
    const savedToken = localStorage.getItem('token')
    const savedUser = localStorage.getItem('userInfo')

    if (savedToken && savedUser) {
      token.value = savedToken
      userInfo.value = JSON.parse(savedUser)
      balance.value = 1000.00
    }
  }

  return {
    userInfo,
    token,
    balance,
    isLoggedIn,
    username,
    avatar,
    login,
    logout,
    recharge,
    initUser
  }
})
