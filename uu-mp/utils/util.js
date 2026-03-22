/**
 * 工具函数
 */

/**
 * 格式化时间
 */
export function formatTime(timestamp) {
  const date = new Date(timestamp)
  const year = date.getFullYear()
  const month = (date.getMonth() + 1).toString().padStart(2, '0')
  const day = date.getDate().toString().padStart(2, '0')
  const hour = date.getHours().toString().padStart(2, '0')
  const minute = date.getMinutes().toString().padStart(2, '0')
  return `${year}-${month}-${day} ${hour}:${minute}`
}

/**
 * 格式化金额
 */
export function formatAmount(amount) {
  return (amount / 100).toFixed(2)
}

/**
 * 手机号脱敏
 */
export function maskPhone(phone) {
  if (!phone || phone.length !== 11) {
    return phone
  }
  return phone.substring(0, 3) + '****' + phone.substring(7)
}

/**
 * 获取服务类型描述
 */
export function getServiceTypeDesc(type) {
  const types = {
    1: '帮买',
    2: '帮送',
    3: '帮排队'
  }
  return types[type] || '未知'
}

/**
 * 获取订单状态描述
 */
export function getOrderStatusDesc(status) {
  const statuses = {
    100: '待支付',
    200: '待接单',
    300: '进行中',
    600: '已完成',
    999: '已取消'
  }
  return statuses[status] || '未知'
}

/**
 * 获取订单状态颜色
 */
export function getOrderStatusColor(status) {
  const colors = {
    100: '#FF9800',
    200: '#4CAF50',
    300: '#4A90E2',
    600: '#9E9E9E',
    999: '#F44336'
  }
  return colors[status] || '#999'
}

export default {
  formatTime,
  formatAmount,
  maskPhone,
  getServiceTypeDesc,
  getOrderStatusDesc,
  getOrderStatusColor
}