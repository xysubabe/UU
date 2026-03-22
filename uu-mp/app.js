/**
 * UU跑腿小程序
 * 提供帮买、帮送、帮排队三种跑腿服务
 */
App({
  globalData: {
    userInfo: null,
    token: null,
    baseUrl: 'http://localhost:8080/v1'
  },

  onLaunch() {
    console.log('小程序启动')
    this.checkLoginStatus()
  },

  onShow() {
    // 页面显示
  },

  /**
   * 检查登录状态
   */
  checkLoginStatus() {
    const token = wx.getStorageSync('token')
    if (token) {
      this.globalData.token = token
    }
  },

  /**
   * 设置用户信息
   */
  setUserInfo(userInfo) {
    this.globalData.userInfo = userInfo
    wx.setStorageSync('userInfo', userInfo)
  },

  /**
   * 设置Token
   */
  setToken(token) {
    this.globalData.token = token
    wx.setStorageSync('token', token)
  },

  /**
   * 清除登录状态
   */
  clearLoginStatus() {
    this.globalData.userInfo = null
    this.globalData.token = null
    wx.removeStorageSync('token')
    wx.removeStorageSync('userInfo')
  },

  /**
   * 获取用户信息
   */
  getUserInfo() {
    return this.globalData.userInfo
  },

  /**
   * 获取Token
   */
  getToken() {
    return this.globalData.token
  },

  /**
   * 检查是否登录
   */
  isLoggedIn() {
    return !!this.globalData.token
  }
})