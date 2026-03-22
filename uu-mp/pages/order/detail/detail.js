// pages/order/detail/detail.js
import http from '../../../utils/http.js'

Page({
  data: {
    orderId: null,
    order: {},
    orderLogs: [],
    statusColor: '#999',
    statusColorLight: '#ccc',
    statusIcon: ''
  },

  onLoad(options) {
    const id = options.id
    if (!id) {
      wx.showToast({
        title: '订单不存在',
        icon: 'none'
      })
      setTimeout(() => {
        wx.navigateBack()
      }, 1500)
      return
    }

    this.setData({ orderId: id })
    this.loadOrderDetail()
  },

  onShow() {
    // 每次显示页面时刷新
    if (this.data.orderId) {
      this.loadOrderDetail()
    }
  },

  /**
   * 加载订单详情
   */
  async loadOrderDetail() {
    try {
      // 加载订单详情
      const orderRes = await http.get(`/order/detail/${this.data.orderId}`)
      const order = orderRes.data

      // 格式化订单数据
      const formattedOrder = {
        ...order,
        serviceTypeDesc: order.serviceTypeDesc || this.getServiceTypeDesc(order.serviceType?.code),
        statusDesc: order.statusDesc || this.getOrderStatusDesc(order.status?.code),
        createTime: this.formatTime(order.createAt),
        amount: this.formatAmount(order.amount)
      }

      // 更新状态颜色和图标
      this.updateStatusColor(order.status?.code)

      this.setData({ order: formattedOrder })

      // 加载订单日志
      this.loadOrderLogs()
    } catch (error) {
      console.error('加载订单详情失败', error)
      wx.showToast({
        title: error.message || '加载失败',
        icon: 'none'
      })
    }
  },

  /**
   * 加载订单日志
   */
  async loadOrderLogs() {
    try {
      const res = await http.get(`/order/${this.data.orderId}/logs`)
      const logs = res.data.list || []

      // 格式化日志数据
      const formattedLogs = logs.map(log => ({
        ...log,
        title: log.action,
        description: log.description,
        createTime: this.formatTime(log.createAt)
      }))

      this.setData({ orderLogs: formattedLogs })
    } catch (error) {
      console.error('加载订单日志失败', error)
    }
  },

  /**
   * 更新状态颜色
   */
  updateStatusColor(status) {
    const colorMap = {
      100: { color: '#FF9800', light: '#FFB74D', icon: '💰' },
      200: { color: '#4CAF50', light: '#81C784', icon: '⏳' },
      300: { color: '#4A90E2', light: '#64B5F6', icon: '🚚' },
      600: { color: '#9E9E9E', light: '#BDBDBD', icon: '✅' },
      999: { color: '#F44336', light: '#EF5350', icon: '❌' }
    }

    const statusInfo = colorMap[status] || { color: '#999', light: '#ccc', icon: '' }
    this.setData({
      statusColor: statusInfo.color,
      statusColorLight: statusInfo.light,
      statusIcon: statusInfo.icon
    })
  },

  /**
   * 取消订单
   */
  async cancelOrder() {
    const status = this.data.order.status?.code

    // 检查订单状态
    if (status >= 300) {
      wx.showToast({
        title: '订单进行中，无法取消',
        icon: 'none'
      })
      return
    }

    const res = await wx.showModal({
      title: '确认取消',
      content: '确定要取消此订单吗？'
    })

    if (!res.confirm) return

    try {
      await http.put(`/order/cancel/${this.data.orderId}`)

      wx.showToast({
        title: '订单已取消',
        icon: 'success'
      })

      // 刷新订单详情
      setTimeout(() => {
        this.loadOrderDetail()
      }, 1000)
    } catch (error) {
      wx.showToast({
        title: error.message || '取消失败',
        icon: 'none'
      })
    }
  },

  /**
   * 联系骑手
   */
  contactRider() {
    wx.showToast({
      title: '功能开发中',
      icon: 'none'
    })
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