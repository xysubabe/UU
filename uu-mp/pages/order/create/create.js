// pages/order/create/create.js
import http from '../../../utils/http.js'

Page({
  data: {
    type: '1', // 1-帮买 2-帮送 3-帮排队
    serviceTitle: '帮买',
    amount: '',
    description: '',
    startAddress: null,
    endAddress: null,
    queueAddress: null,
    submitting: false
  },

  onLoad(options) {
    const type = options.type || '1'
    this.setData({
      type,
      serviceTitle: this.getServiceTitle(type)
    })
  },

  /**
   * 获取服务标题
   */
  getServiceTitle(type) {
    const titles = {
      '1': '帮买',
      '2': '帮送',
      '3': '帮排队'
    }
    return titles[type] || '跑腿'
  },

  /**
   * 选择起始地址（帮送）
   */
  selectStartAddress() {
    wx.navigateTo({
      url: `/pages/address/list/list?mode=start`
    })
  },

  /**
   * 选择结束地址（帮买/帮送）
   */
  selectEndAddress() {
    wx.navigateTo({
      url: `/pages/address/list/list?mode=end`
    })
  },

  /**
   * 选择排队地址（帮排队）
   */
  selectQueueAddress() {
    wx.navigateTo({
      url: `/pages/address/list/list?mode=queue`
    })
  },

  /**
   * 输入描述
   */
  onDescriptionInput(e) {
    this.setData({
      description: e.detail.value
    })
  },

  /**
   * 输入金额
   */
  onAmountInput(e) {
    this.setData({
      amount: e.detail.value
    })
  },

  /**
   * 检查是否可以提交
   */
  get canSubmit() {
    if (this.data.submitting) return false

    // 检查金额
    if (!this.data.amount || this.data.amount < 5) return false
    if (this.data.amount > 9999) return false

    // 检查地址
    if (this.data.type === '1') {
      return !!this.data.endAddress
    } else if (this.data.type === '2') {
      return this.data.startAddress && this.data.endAddress
    } else if (this.data.type === '3') {
      return !!this.data.queueAddress
    }

    return false
  },

  /**
   * 提交订单
   */
  async submitOrder() {
    if (!this.canSubmit) {
      wx.showToast({
        title: '请完善订单信息',
        icon: 'none'
      })
      return
    }

    this.setData({ submitting: true })

    try {
      // 构建订单数据
      const orderData = {
        serviceType: parseInt(this.data.type),
        description: this.data.description,
        amount: Math.round(this.data.amount * 100) // 转换为分
      }

      // 添加地址信息
      if (this.data.type === '1') {
        orderData.endAddressId = this.data.endAddress.id
      } else if (this.data.type === '2') {
        orderData.startAddressId = this.data.startAddress.id
        orderData.endAddressId = this.data.endAddress.id
      } else if (this.data.type === '3') {
        orderData.queueAddressId = this.data.queueAddress.id
      }

      const res = await http.post('/order/create', orderData)

      wx.showToast({
        title: '下单成功',
        icon: 'success'
      })

      setTimeout(() => {
        wx.navigateBack()
      }, 1500)

    } catch (error) {
      wx.showToast({
        title: error.message || '下单失败',
        icon: 'none'
      })
    } finally {
      this.setData({ submitting: false })
    }
  },

  /**
   * 设置起始地址
   */
  setStartAddress(address) {
    this.setData({ startAddress: address })
  },

  /**
   * 设置结束地址
   */
  setEndAddress(address) {
    this.setData({ endAddress: address })
  },

  /**
   * 设置排队地址
   */
  setQueueAddress(address) {
    this.setData({ queueAddress: address })
  }
})