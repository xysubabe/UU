// pages/index/index.js
import http from '../../utils/http.js'

Page({
  data: {
    ongoingOrders: []
  },

  onShow() {
    this.checkLogin()
    if (this.isLoggedIn()) {
      this.loadOngoingOrders()
    }
  },

  /**
   * 检查登录状态
   */
  checkLogin() {
    const app = getApp()
    if (!app.isLoggedIn()) {
      wx.redirectTo({
        url: '/pages/auth/login/login'
      })
    }
  },

  /**
   * 是否已登录
   */
  isLoggedIn() {
    return getApp().isLoggedIn()
  },

  /**
   * 跳转到创建订单页面
   */
  navigateToCreateOrder(e) {
    const type = e.currentTarget.dataset.type
    wx.navigateTo({
      url: `/pages/order/create/create?type=${type}`
    })
  },

  /**
   * 跳转到订单列表
   */
  navigateToOrderList() {
    wx.switchTab({
      url: '/pages/order/list/list'
    })
  },

  /**
   * 跳转到订单详情
   */
  navigateToOrderDetail(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({
      url: `/pages/order/detail/detail?id=${id}`
    })
  },

  /**
   * 加载进行中订单
   */
  async loadOngoingOrders() {
    try {
      const res = await http.get('/order/ongoing?page=1&pageSize=5')
      const orders = res.data.list || []

      // 格式化订单数据
      const formattedOrders = orders.map(order => ({
        ...order,
        serviceTypeDesc: order.serviceTypeDesc || this.getServiceTypeDesc(order.serviceType?.code),
        statusDesc: order.statusDesc || this.getOrderStatusDesc(order.status?.code),
        statusColor: this.getOrderStatusColor(order.status?.code),
        createTime: this.formatTime(order.createAt),
        amount: this.formatAmount(order.amount)
      }))

      this.setData({ ongoingOrders: formattedOrders })
    } catch (error) {
      console.error('加载订单失败', error)
    }
  },

  /**
   * 获取服务类型描述
   */
  getServiceTypeDesc(type) {
    if (!type) return '未知'
    const types = { 1: '帮买', 2: '帮送', 3: '帮排队' }
    return types[type] || '未知'
  },

  /**
   * 获取订单状态描述
   */
  getOrderStatusDesc(status) {
    if (!status) return '未知'
    const statuses = {
      100: '待支付',
      200: '待接单',
      300: '进行中',
      600: '已完成',
      999: '已取消'
    }
    return statuses[status] || '未知'
  },

  /**
   * 获取订单状态颜色
   */
  getOrderStatusColor(status) {
    if (!status) return '#999'
    const colors = {
      100: '#FF9800',
      200: '#4CAF50',
      300: '#4A90E2',
      600: '#9E9E9E',
      999: '#F44336'
    }
    return colors[status] || '#999'
  },

  /**
   * 格式化时间
   */
  formatTime(timestamp) {
    const date = new Date(timestamp)
    const year = date.getFullYear()
    const month = (date.getMonth() + 1).toString().padStart(2, '0')
    const day = date.getDate().toString().padStart(2, '0')
    const hour = date.getHours().toString().padStart(2, '0')
    const minute = date.getMinutes().toString().padStart(2, '0')
    return `${month}-${day} ${hour}:${minute}`
  },

  /**
   * 格式化金额
   */
  formatAmount(amount) {
    return (amount / 100).toFixed(2)
  }
})