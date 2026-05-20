<template>
  <div v-if="!item.hidden">
    <template v-if="!hasChildren">
      <router-link v-if="item.meta" :to="resolvePath(item.path)">
        <el-menu-item :index="resolvePath(item.path)" :class="{ 'submenu-title-noDropdown': !isNest }">
          <item :icon="item.meta.icon" :title="item.meta.title" />
        </el-menu-item>
      </router-link>
    </template>

    <el-sub-menu v-else :index="resolvePath(item.path)" popper-append-to-body>
      <template #title>
        <item v-if="item.meta" :icon="item.meta.icon" :title="item.meta.title" />
      </template>
      <sidebar-item
        v-for="child in visibleChildren"
        :key="child.path"
        :is-nest="true"
        :item="child"
        :base-path="resolvePath(item.path)"
        class="nest-menu"
      />
    </el-sub-menu>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import Item from './Item.vue'

defineOptions({
  name: 'SidebarItem'
})

const props = defineProps({
  item: {
    type: Object,
    required: true
  },
  isNest: {
    type: Boolean,
    default: false
  },
  basePath: {
    type: String,
    default: ''
  }
})

const visibleChildren = computed(() => (props.item.children || []).filter(child => !child.hidden))
const hasChildren = computed(() => visibleChildren.value.length > 0)

const resolvePath = (routePath) => {
  if (isExternal(routePath)) {
    return routePath
  }
  if (isExternal(props.basePath)) {
    return props.basePath
  }
  if (routePath.startsWith('/')) {
    return routePath
  }
  if (props.basePath.endsWith('/')) {
    return props.basePath + routePath
  }
  return props.basePath + '/' + routePath
}

const isExternal = (path) => {
  return /^(https?:|mailto:|tel:)/.test(path)
}
</script>
