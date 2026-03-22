// pages/user/service/service.js
Page({
  data: {
    feedback: '',
    submitting: false
  },

  onLoad() {
    wx.setNavigationBarTitle({
      title: '联系客服'
    })
  },

  /**
   * 拨打客服电话
   */
  callPhone() {
    wx.makePhoneCall({
      phoneNumber: '400XXXXXXXX'
    })
  },

  /**
   * 联系在线客服
   */
  contactOnline() {
    wx.showToast({
      title: '在线客服功能开发中',
      icon: 'none'
    })
  },

  /**
   * 显示常见问题详情
   */
  showFaq(e) {
    const type = e.currentTarget.dataset.type
    const answers = {
      cancel: '订单在骑手接单前可以取消。请在订单列表中找到对应订单，点击"取消订单"按钮即可。',
      payment: '订单完成后，您可以通过微信支付完成付款。付款金额以您下单时设定的跑腿费用为准。',
      address: '进入"我的"页面，点击"地址管理"，然后点击"添加新地址"按钮，填写收货信息即可。',
      refund: '如需申请退款，请联系在线客服或拨打客服热线，说明退款原因，客服会为您处理。'
    }

    wx.showModal({
      title: '常见问题',
      content: answers[type] || '暂无信息',
      showCancel: false
    })
  },

  /**
   * 输入反馈内容
   */
  onFeedbackInput(e) {
    this.setData({ feedback: e.detail.value })
  },

  /**
   * 提交反馈
   */
  async submitFeedback() {
    const { feedback } = this.data

    if (!feedback || feedback.trim().length === 0) {
      wx.showToast({
        title: '请输入反馈内容',
        icon: 'none'
      })
      return
    }

    this.setData({ submitting: true })

    try {
      // 模拟提交反馈
      await new Promise(resolve => setTimeout(resolve, 1000))

      wx.showToast({
        title: '感谢您的反馈',
        icon: 'success'
      })

      this.setData({ feedback: '' })

      setTimeout(() => {
        wx.navigateBack()
      }, 1500)
    } catch (error) {
      wx.showToast({
        title: '提交失败，请重试',
        icon: 'none'
      })
    } finally {
      this.setData({ submitting: false })
    }
  }
})