<template>
  <el-dropdown trigger="click" @command="handleCommand">
    <el-button type="primary" link>
      操作 <el-icon class="el-icon--right"><ArrowDown /></el-icon>
    </el-button>
    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item v-if="showView" command="view">
          <el-icon><View /></el-icon>详情
        </el-dropdown-item>
        <el-dropdown-item v-if="showEdit" command="edit">
          <el-icon><Edit /></el-icon>编辑
        </el-dropdown-item>
        <el-dropdown-item v-if="showShip" command="ship">
          <el-icon><Van /></el-icon>发货
        </el-dropdown-item>
        <el-dropdown-item v-if="showToggle" :command="toggleStatus ? 'disable' : 'enable'">
          <el-icon><Switch /></el-icon>{{ toggleStatus ? toggleOffText : toggleOnText }}
        </el-dropdown-item>
        <el-dropdown-item v-if="showReset" command="reset">
          <el-icon><RefreshRight /></el-icon>重置密码
        </el-dropdown-item>
        <el-dropdown-item v-if="showDelete" command="delete" divided>
          <el-icon color="#f56c6c"><Delete /></el-icon><span style="color: #f56c6c">删除</span>
        </el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>

<script setup>
import { ArrowDown, View, Edit, Van, Switch, RefreshRight, Delete } from '@element-plus/icons-vue'

defineOptions({
  name: 'ActionDropdown'
})

const props = defineProps({
  showView: {
    type: Boolean,
    default: false
  },
  showEdit: {
    type: Boolean,
    default: false
  },
  showShip: {
    type: Boolean,
    default: false
  },
  showToggle: {
    type: Boolean,
    default: false
  },
  showReset: {
    type: Boolean,
    default: false
  },
  showDelete: {
    type: Boolean,
    default: false
  },
  toggleStatus: {
    type: Boolean,
    default: true
  },
  toggleOnText: {
    type: String,
    default: '启用'
  },
  toggleOffText: {
    type: String,
    default: '禁用'
  }
})

const emit = defineEmits(['view', 'edit', 'ship', 'toggle', 'reset', 'delete'])

const handleCommand = (command) => {
  switch (command) {
    case 'view':
      emit('view')
      break
    case 'edit':
      emit('edit')
      break
    case 'ship':
      emit('ship')
      break
    case 'enable':
    case 'disable':
      emit('toggle')
      break
    case 'reset':
      emit('reset')
      break
    case 'delete':
      emit('delete')
      break
  }
}
</script>

<style scoped>
.el-dropdown-menu__item {
  display: flex;
  align-items: center;
  gap: 8px;
}
</style>