// pages/user/setting/setting.js
import http from '../../../utils/http.js'

Page({
  data: {
    userInfo: {},
    notificationEnabled: true,
    darkModeEnabled: false,
    cacheSize: '0MB'
  },

  onLoad() {
    this.loadUserInfo()
    this.getCacheSize()
    this.loadSettings()
  },

  /**
   * 加载用户信息
   */
  loadUserInfo() {
    const app = getApp()
    const userInfo = app.getUserInfo()
    if (userInfo) {
      this.setData({ userInfo })
    }
  },

  /**
   * 获取缓存大小
   */
  getCacheSize() {
    wx.getStorageInfo({
      success: (res) => {
        const size = res.currentSize
        this.setData({ cacheSize: `${size}KB` })
      }
    })
  },

  /**
   * 加载设置
   */
  loadSettings() {
    const notificationEnabled = wx.getStorageSync('notificationEnabled') !== false
    const darkModeEnabled = wx.getStorageSync('darkModeEnabled') || false
    this.setData({ notificationEnabled, darkModeEnabled })
  },

  /**
   * 编辑昵称
   */
  editNickname() {
    wx.showToast({
      title: '功能开发中',
      icon: 'none'
    })
  },

  /**
   * 编辑手机号
   */
  editPhone() {
    wx.showToast({
      title: '功能开发中',
      icon: 'none'
    })
  },

  /**
   * 切换消息通知
   */
  toggleNotification() {
    const enabled = !this.data.notificationEnabled
    this.setData({ notificationEnabled: enabled })
    wx.setStorageSync('notificationEnabled', enabled)

    // 请求通知权限
    if (enabled) {
      wx.requestSubscribeMessage({
        tmplIds: [], // 这里填入模板ID
        success: () => {
          wx.showToast({
            title: '已开启通知',
            icon: 'success'
          })
        },
        fail: () => {
          wx.showToast({
            title: '授权失败',
            icon: 'none'
          })
        }
      })
    }
  },

  /**
   * 切换深色模式
   */
  toggleDarkMode() {
    const enabled = !this.data.darkModeEnabled
    this.setData({ darkModeEnabled: enabled })
    wx.setStorageSync('darkModeEnabled', enabled)
    wx.showToast({
      title: enabled ? '已开启深色模式' : '已关闭深色模式',
      icon: 'success'
    })
  },

  /**
   * 检查版本
   */
  checkVersion() {
    const updateManager = wx.getUpdateManager()

    updateManager.onCheckForUpdate((res) => {
      if (res.hasUpdate) {
        wx.showToast({
          title: '发现新版本',
          icon: 'success'
        })
      } else {
        wx.showToast({
          title: '已是最新版本',
          icon: 'success'
        })
      }
    })

    updateManager.onUpdateReady(() => {
      wx.showModal({
        title: '更新提示',
        content: '新版本已准备好，是否重启应用？',
        success: (res) => {
          if (res.confirm) {
            updateManager.applyUpdate()
          }
        }
      })
    })
  },

  /**
   * 用户协议
   */
  userAgreement() {
    wx.navigateTo({
      url: '/pages/user/agreement/agreement'
    })
  },

  /**
   * 隐私政策
   */
  privacyPolicy() {
    wx.navigateTo({
      url: '/pages/user/privacy/privacy'
    })
  },

  /**
   * 清除缓存
   */
  clearCache() {
    wx.showModal({
      title: '确认清除',
      content: '确定要清除缓存吗？',
      success: (res) => {
        if (res.confirm) {
          wx.clearStorage({
            success: () => {
              wx.showToast({
                title: '缓存已清除',
                icon: 'success'
              })
              setTimeout(() => {
                wx.reLaunch({
                  url: '/pages/index/index'
                })
              }, 1500)
            }
          })
        }
      }
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
      wx.removeStorageSync('token')
      wx.removeStorageSync('userInfo')

      const app = getApp()
      app.globalData.userInfo = null
      app.globalData.token = null

      wx.redirectTo({
        url: '/pages/auth/login/login'
      })
    } catch (error) {
      console.error('退出登录失败', error)
    }
  }
})