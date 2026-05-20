const LOG_PREFIX = '[操作日志]'
const LOG_STYLES = {
  info: 'color: #409eff; font-weight: bold;',
  success: 'color: #67c23a; font-weight: bold;',
  warning: 'color: #e6a23c; font-weight: bold;',
  error: 'color: #f56c6c; font-weight: bold;',
  action: 'color: #909399; font-weight: bold;'
}

export const logAction = (action, details = {}, type = 'info') => {
  const timestamp = new Date().toLocaleString('zh-CN')
  const style = LOG_STYLES[type] || LOG_STYLES.info
  
  console.log(
    `%c${LOG_PREFIX} [${timestamp}] ${action}`,
    style,
    details
  )
}

export const logUserAction = (userId, action, details = {}) => {
  logAction(`用户[${userId}] ${action}`, details, 'action')
}

export const logAdminAction = (adminId, action, details = {}) => {
  logAction(`管理员[${adminId}] ${action}`, details, 'warning')
}

export const logSuccess = (action, details = {}) => {
  logAction(`✓ ${action}`, details, 'success')
}

export const logError = (action, error = {}) => {
  logAction(`✗ ${action}`, { error: error.message || error }, 'error')
}

export default {
  logAction,
  logUserAction,
  logAdminAction,
  logSuccess,
  logError
}
