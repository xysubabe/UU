// pages/order/list/list.js
import http from '../../../utils/http.js'

Page({
  data: {
    activeTab: 'all',
    orderList: [],
    page: 1,
    pageSize: 20,
    hasMore: true,
    loading: false
  },

  onLoad() {
    this.loadOrders()
  },

  onShow() {
    // 每次显示页面时刷新订单列表
    this.setData({
      page: 1,
      orderList: [],
      hasMore: true
    })
    this.loadOrders()
  },

  /**
   * 切换标签
   */
  switchTab(e) {
    const tab = e.currentTarget.dataset.tab
    if (tab === this.data.activeTab) return

    this.setData({
      activeTab: tab,
      page: 1,
      orderList: [],
      hasMore: true
    })
    this.loadOrders()
  },

  /**
   * 加载订单列表
   */
  async loadOrders() {
    if (this.data.loading || !this.data.hasMore) return

    this.setData({ loading: true })

    try {
      let url = `/order/list?page=${this.data.page}&pageSize=${this.data.pageSize}`

      // 根据标签筛选
      if (this.data.activeTab === 'ongoing') {
        url = `/order/ongoing?page=${this.data.page}&pageSize=${this.data.pageSize}`
      } else if (this.data.activeTab === 'completed') {
        url = `/order/completed?page=${this.data.page}&pageSize=${this.data.pageSize}`
      }

      const res = await http.get(url)
      const newOrders = res.data.list || []

      // 格式化订单数据
      const formattedOrders = newOrders.map(order => ({
        ...order,
        serviceTypeDesc: order.serviceTypeDesc || this.getServiceTypeDesc(order.serviceType?.code),
        statusDesc: order.statusDesc || this.getOrderStatusDesc(order.status?.code),
        statusColor: this.getOrderStatusColor(order.status?.code),
        createTime: this.formatTime(order.createAt),
        amount: this.formatAmount(order.amount)
      }))

      this.setData({
        orderList: [...this.data.orderList, ...formattedOrders],
        hasMore: formattedOrders.length >= this.data.pageSize
      })
    } catch (error) {
      console.error('加载订单失败', error)
      wx.showToast({
        title: error.message || '加载失败',
        icon: 'none'
      })
    } finally {
      this.setData({ loading: false })
    }
  },

  /**
   * 加载更多
   */
  onReachBottom() {
    if (this.data.hasMore && !this.data.loading) {
      this.setData({ page: this.data.page + 1 })
      this.loadOrders()
    }
  },

  /**
   * 下拉刷新
   */
  onPullDownRefresh() {
    this.setData({
      page: 1,
      orderList: [],
      hasMore: true
    })
    this.loadOrders().then(() => {
      wx.stopPullDownRefresh()
    })
  },

  /**
   * 跳转到订单详情
   */
  navigateToDetail(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({
      url: `/pages/order/detail/detail?id=${id}`
    })
  },

  /**
   * 取消订单
   */
  async cancelOrder(e) {
    const id = e.currentTarget.dataset.id
    const status = e.currentTarget.dataset.status

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
      await http.put(`/order/cancel/${id}`)

      wx.showToast({
        title: '订单已取消',
        icon: 'success'
      })

      // 刷新订单列表
      this.setData({
        page: 1,
        orderList: [],
        hasMore: true
      })
      this.loadOrders()
    } catch (error) {
      wx.showToast({
        title: error.message || '取消失败',
        icon: 'none'
      })
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