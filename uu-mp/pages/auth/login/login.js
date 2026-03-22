// pages/auth/login/login.js
import http from '../../../utils/http.js'

Page({
  data: {
    logging: false,
    agreed: false
  },

  onLoad() {
    this.checkLoginStatus()
  },

  /**
   * 检查登录状态
   */
  checkLoginStatus() {
    const app = getApp()
    if (app.isLoggedIn()) {
      // 已登录，跳转到首页
      wx.switchTab({
        url: '/pages/index/index'
      })
    }
  },

  /**
   * 切换协议同意状态
   */
  toggleAgreement() {
    this.setData({ agreed: !this.data.agreed })
  },

  /**
   * 微信登录
   */
  async wechatLogin() {
    if (!this.data.agreed) {
      wx.showToast({
        title: '请先阅读并同意用户协议',
        icon: 'none'
      })
      return
    }

    this.setData({ logging: true })

    try {
      // 获取微信登录code
      const loginRes = await wx.login()
      if (!loginRes.code) {
        throw new Error('获取登录凭证失败')
      }

      // 调用后端登录接口
      const res = await http.post('/auth/wechat-login', {
        code: loginRes.code
      }, false)

      const { token, userInfo } = res.data

      // 保存token和用户信息
      wx.setStorageSync('token', token)
      wx.setStorageSync('userInfo', userInfo)

      // 更新全局数据
      const app = getApp()
      app.globalData.token = token
      app.globalData.userInfo = userInfo

      wx.showToast({
        title: '登录成功',
        icon: 'success'
      })

      // 跳转到首页
      setTimeout(() => {
        wx.switchTab({
          url: '/pages/index/index'
        })
      }, 1500)
    } catch (error) {
      console.error('微信登录失败', error)
      wx.showToast({
        title: error.message || '登录失败，请重试',
        icon: 'none'
      })
    } finally {
      this.setData({ logging: false })
    }
  },

  /**
   * 显示用户协议
   */
  showUserAgreement() {
    wx.navigateTo({
      url: '/pages/user/agreement/agreement'
    })
  },

  /**
   * 显示隐私政策
   */
  showPrivacyPolicy() {
    wx.navigateTo({
      url: '/pages/user/privacy/privacy'
    })
  }
})