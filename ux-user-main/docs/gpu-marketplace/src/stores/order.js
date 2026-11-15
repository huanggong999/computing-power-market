import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { useUserStore } from './user'
import { useGPUStore } from './gpu'
import { message } from 'ant-design-vue'

export const useOrderStore = defineStore('order', () => {
  // State
  const orders = ref([])
  const currentOrder = ref(null)
  const loading = ref(false)

  // Getters
  const orderHistory = computed(() => {
    return orders.value.sort((a, b) => b.createTime - a.createTime)
  })

  const unpaidOrders = computed(() => {
    return orders.value.filter(order => order.status === 'unpaid')
  })

  const runningOrders = computed(() => {
    return orders.value.filter(order => order.status === 'running')
  })

  // Actions
  async function createOrder(gpuId, duration, config = {}) {
    loading.value = true
    try {
      const userStore = useUserStore()
      const gpuStore = useGPUStore()
      const gpu = gpuStore.getGPUById(gpuId)

      if (!gpu) {
        throw new Error('GPU不存在')
      }

      const totalAmount = gpu.price * duration
      const orderId = `ORD${Date.now()}${Math.floor(Math.random() * 1000)}`

      const order = {
        id: orderId,
        gpuId,
        gpuModel: gpu.model,
        gpuInfo: {
          model: gpu.model,
          vram: gpu.vram,
          cpu: gpu.cpu,
          cpuCores: gpu.cpuCores,
          memory: gpu.memory,
          disk: gpu.disk,
          region: gpu.region,
          bandwidth: gpu.bandwidth,
          network: gpu.network,
        },
        duration,
        price: gpu.price,
        totalAmount: parseFloat(totalAmount.toFixed(2)),
        status: 'unpaid',
        userId: userStore.userInfo?.id,
        username: userStore.userInfo?.username,
        createTime: Date.now(),
        config: {
          image: config.image || 'Ubuntu 22.04',
          sshKey: config.sshKey || '',
          password: config.password || '',
          ...config,
        },
      }

      orders.value.push(order)
      currentOrder.value = order

      message.success('订单创建成功，请完成支付')
      return { success: true, order }
    } catch (error) {
      message.error(error.message || '创建订单失败')
      return { success: false, error: error.message }
    } finally {
      loading.value = false
    }
  }

  async function payOrder(orderId) {
    loading.value = true
    try {
      const userStore = useUserStore()
      const gpuStore = useGPUStore()

      const order = orders.value.find(o => o.id === orderId)
      if (!order) {
        throw new Error('订单不存在')
      }

      if (order.status !== 'unpaid') {
        throw new Error('订单状态异常')
      }

      // 检查余额
      if (userStore.balance < order.totalAmount) {
        throw new Error('余额不足，请先充值')
      }

      // 扣款
      userStore.balance -= order.totalAmount

      // 更新订单状态
      order.status = 'paid'
      order.payTime = Date.now()

      // 启动GPU实例
      const gpuResult = await gpuStore.rentGPU(order.gpuId, order.duration)

      if (gpuResult.success) {
        order.status = 'running'
        order.instanceId = gpuResult.instanceId || `INS${Date.now()}`
        message.success('支付成功，GPU实例已启动')
      } else {
        // 启动失败，退款
        userStore.balance += order.totalAmount
        order.status = 'failed'
        message.error('GPU实例启动失败，已退款')
      }

      return { success: true, order }
    } catch (error) {
      message.error(error.message || '支付失败')
      return { success: false, error: error.message }
    } finally {
      loading.value = false
    }
  }

  async function cancelOrder(orderId) {
    try {
      const order = orders.value.find(o => o.id === orderId)
      if (!order) {
        throw new Error('订单不存在')
      }

      if (order.status !== 'unpaid') {
        throw new Error('只能取消未支付订单')
      }

      order.status = 'cancelled'
      message.success('订单已取消')

      return { success: true }
    } catch (error) {
      message.error(error.message || '取消订单失败')
      return { success: false, error: error.message }
    }
  }

  function getOrderById(orderId) {
    return orders.value.find(o => o.id === orderId)
  }

  function clearCurrentOrder() {
    currentOrder.value = null
  }

  // 初始化订单数据（从localStorage）
  function initOrders() {
    const savedOrders = localStorage.getItem('gpu_orders')
    if (savedOrders) {
      try {
        orders.value = JSON.parse(savedOrders)
      } catch (e) {
        console.error('Failed to parse orders from localStorage')
      }
    }
  }

  // 持久化订单数据到localStorage
  function persistOrders() {
    localStorage.setItem('gpu_orders', JSON.stringify(orders.value))
  }

  return {
    orders,
    currentOrder,
    loading,
    orderHistory,
    unpaidOrders,
    runningOrders,
    createOrder,
    payOrder,
    cancelOrder,
    getOrderById,
    clearCurrentOrder,
    initOrders,
    persistOrders,
  }
})
