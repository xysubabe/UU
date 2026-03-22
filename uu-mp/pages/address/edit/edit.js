// pages/address/edit/edit.js
import http from '../../../utils/http.js'

Page({
  data: {
    addressId: null,
    contactName: '',
    contactPhone: '',
    region: '',
    detailedAddress: '',
    isDefault: false,
    submitting: false
  },

  onLoad(options) {
    const id = options.id
    if (id) {
      this.setData({ addressId: id })
      this.loadAddressDetail(id)
    }
  },

  /**
   * 加载地址详情
   */
  async loadAddressDetail(id) {
    try {
      const res = await http.get(`/address/${id}`)
      const address = res.data

      this.setData({
        contactName: address.contactName || '',
        contactPhone: address.contactPhone || '',
        region: address.region || '',
        detailedAddress: address.detailedAddress || '',
        isDefault: address.isDefault || false
      })
    } catch (error) {
      console.error('加载地址详情失败', error)
      wx.showToast({
        title: error.message || '加载失败',
        icon: 'none'
      })
    }
  },

  /**
   * 输入联系人姓名
   */
  onContactNameInput(e) {
    this.setData({ contactName: e.detail.value })
  },

  /**
   * 输入联系电话
   */
  onContactPhoneInput(e) {
    this.setData({ contactPhone: e.detail.value })
  },

  /**
   * 输入地区
   */
  onRegionInput(e) {
    this.setData({ region: e.detail.value })
  },

  /**
   * 输入详细地址
   */
  onDetailedAddressInput(e) {
    this.setData({ detailedAddress: e.detail.value })
  },

  /**
   * 切换默认地址
   */
  toggleDefault() {
    this.setData({ isDefault: !this.data.isDefault })
  },

  /**
   * 验证表单
   */
  validateForm() {
    const { contactName, contactPhone, region, detailedAddress } = this.data

    if (!contactName || contactName.trim().length === 0) {
      wx.showToast({
        title: '请输入联系人姓名',
        icon: 'none'
      })
      return false
    }

    if (!contactPhone || contactPhone.length !== 11) {
      wx.showToast({
        title: '请输入11位手机号码',
        icon: 'none'
      })
      return false
    }

    // 验证手机号格式
    const phoneRegex = /^1[3-9]\d{9}$/
    if (!phoneRegex.test(contactPhone)) {
      wx.showToast({
        title: '手机号码格式不正确',
        icon: 'none'
      })
      return false
    }

    if (!region || region.trim().length === 0) {
      wx.showToast({
        title: '请输入所在地区',
        icon: 'none'
      })
      return false
    }

    if (!detailedAddress || detailedAddress.trim().length === 0) {
      wx.showToast({
        title: '请输入详细地址',
        icon: 'none'
      })
      return false
    }

    return true
  },

  /**
   * 保存地址
   */
  async saveAddress() {
    if (!this.validateForm()) return

    this.setData({ submitting: true })

    try {
      const addressData = {
        contactName: this.data.contactName.trim(),
        contactPhone: this.data.contactPhone.trim(),
        region: this.data.region.trim(),
        detailedAddress: this.data.detailedAddress.trim(),
        isDefault: this.data.isDefault
      }

      if (this.data.addressId) {
        // 更新地址
        await http.post(`/address/${this.data.addressId}/update`, addressData)
        wx.showToast({
          title: '修改成功',
          icon: 'success'
        })
      } else {
        // 创建地址
        await http.post('/address/create', addressData)
        wx.showToast({
          title: '添加成功',
          icon: 'success'
        })
      }

      setTimeout(() => {
        wx.navigateBack()
      }, 1500)
    } catch (error) {
      wx.showToast({
        title: error.message || '保存失败',
        icon: 'none'
      })
    } finally {
      this.setData({ submitting: false })
    }
  }
})