// pages/user/profile/profile.js
import http from '../../../utils/http.js'

Page({
  data: {
    userInfo: {},
    stats: {
      totalOrders: 0,
      completedOrders: 0,
      ongoingOrders: 0
    }
  },

  onLoad() {
    this.loadUserInfo()
    this.loadOrderStats()
  },

  onShow() {
    this.loadOrderStats()
  },

  /**
   * 加载用户信息
   */
  async loadUserInfo() {
    try {
      const app = getApp()
      const userInfo = app.getUserInfo()
      if (userInfo) {
        this.setData({ userInfo })
      }
    } catch (error) {
      console.error('加载用户信息失败', error)
    }
  },

  /**
   * 加载订单统计
   */
  async loadOrderStats() {
    try {
      const res = await http.get('/order/stats')
      this.setData({ stats: res.data || { totalOrders: 0, completedOrders: 0, ongoingOrders: 0 } })
    } catch (error) {
      console.error('加载订单统计失败', error)
    }
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
   * 跳转到地址管理
   */
  navigateToAddressList() {
    wx.navigateTo({
      url: '/pages/address/list/list?mode=manage'
    })
  },

  /**
   * 跳转到设置
   */
  navigateToSetting() {
    wx.navigateTo({
      url: '/pages/user/setting/setting'
    })
  },

  /**
   * 联系客服
   */
  contactService() {
    wx.navigateTo({
      url: '/pages/user/service/service'
    })
  },

  /**
   * 关于我们
   */
  aboutUs() {
    wx.showToast({
      title: 'UU 跑腿 v1.0.0',
      icon: 'none'
    })
  },

  /**
   * 退出登录
   */
  async logout() {
    const res = await wx.showModal({
      title: '确认退出',
      content: '确定要退出登录吗？'
    })

    if (!res.confirm) return

    try {
      // 清除本地存储
      wx.removeStorageSync('token')
      wx.removeStorageSync('userInfo')

      // 清除全局数据
      const app = getApp()
      app.globalData.userInfo = null
      app.globalData.token = null

      // 跳转到登录页
      wx.redirectTo({
        url: '/pages/auth/login/login'
      })
    } catch (error) {
      console.error('退出登录失败', error)
    }
  }
})