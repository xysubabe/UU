// pages/address/list/list.js
import http from '../../../utils/http.js'

Page({
  data: {
    mode: 'select', // select-选择地址, manage-地址管理
    addressList: [],
    loading: false
  },

  onLoad(options) {
    const mode = options.mode || 'select'
    this.setData({ mode })
  },

  onShow() {
    this.loadAddressList()
  },

  /**
   * 加载地址列表
   */
  async loadAddressList() {
    this.setData({ loading: true })

    try {
      const res = await http.get('/address/list')
      this.setData({ addressList: res.data.list || [] })
    } catch (error) {
      console.error('加载地址列表失败', error)
      wx.showToast({
        title: error.message || '加载失败',
        icon: 'none'
      })
    } finally {
      this.setData({ loading: false })
    }
  },

  /**
   * 选择地址
   */
  selectAddress(e) {
    if (this.data.mode !== 'select') return

    const address = e.currentTarget.dataset.address

    // 获取所有页面栈
    const pages = getCurrentPages()
    if (pages.length < 2) return

    // 获取上一页
    const prevPage = pages[pages.length - 2]

    // 根据页面设置不同的地址
    const pagePath = prevPage.route || prevPage.__route__
    if (pagePath.includes('order/create/create')) {
      // 判断是哪种地址模式
      const currentPages = getCurrentPages()
      const currentPage = currentPages[currentPages.length - 1]
      const currentUrl = currentPage.options.mode

      if (currentUrl === 'start') {
        prevPage.setStartAddress && prevPage.setStartAddress(address)
      } else if (currentUrl === 'end') {
        prevPage.setEndAddress && prevPage.setEndAddress(address)
      } else if (currentUrl === 'queue') {
        prevPage.setQueueAddress && prevPage.setQueueAddress(address)
      }
    }

    wx.navigateBack()
  },

  /**
   * 添加地址
   */
  addAddress() {
    wx.navigateTo({
      url: '/pages/address/edit/edit'
    })
  },

  /**
   * 编辑地址
   */
  editAddress(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({
      url: `/pages/address/edit/edit?id=${id}`
    })
  },

  /**
   * 删除地址
   */
  async deleteAddress(e) {
    const id = e.currentTarget.dataset.id

    const res = await wx.showModal({
      title: '确认删除',
      content: '确定要删除此地址吗？'
    })

    if (!res.confirm) return

    try {
      await http.post(`/address/${id}/delete`)

      wx.showToast({
        title: '删除成功',
        icon: 'success'
      })

      // 刷新地址列表
      this.loadAddressList()
    } catch (error) {
      wx.showToast({
        title: error.message || '删除失败',
        icon: 'none'
      })
    }
  },

  /**
   * 设置默认地址
   */
  async setDefaultAddress(e) {
    const id = e.currentTarget.dataset.id

    try {
      await http.post(`/address/${id}/default`)

      wx.showToast({
        title: '设置成功',
        icon: 'success'
      })

      // 刷新地址列表
      this.loadAddressList()
    } catch (error) {
      wx.showToast({
        title: error.message || '设置失败',
        icon: 'none'
      })
    }
  }
})