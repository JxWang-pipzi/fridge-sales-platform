<template>
  <div class="tags-view-container">
    <div class="tags-view-wrapper">
      <router-link
        v-for="tag in visitedViews"
        :key="tag.path"
        :to="{ path: tag.path, query: tag.query, fullPath: tag.fullPath }"
        class="tags-view-item"
        :class="isActive(tag) ? 'active' : ''"
        @click.middle="closeSelectedTag(tag)"
        @contextmenu.prevent="openMenu(tag, $event)"
      >
        {{ tag.title }}
        <el-icon 
          v-if="!tag.meta?.affix" 
          class="el-icon-close" 
          @click.prevent.stop="closeSelectedTag(tag)"
        >
          <Close />
        </el-icon>
      </router-link>
    </div>
    
    <ul v-show="visible" :style="{ left: left + 'px', top: top + 'px' }" class="context-menu">
      <li @click="refreshSelectedTag(selectedTag)">刷新</li>
      <li v-if="!selectedTag?.meta?.affix" @click="closeSelectedTag(selectedTag)">关闭</li>
      <li @click="closeOthersTags">关闭其他</li>
      <li @click="closeAllTags">关闭所有</li>
    </ul>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Close } from '@element-plus/icons-vue'
import { useTagsViewStore } from '@/stores/tagsView'

const route = useRoute()
const router = useRouter()
const tagsViewStore = useTagsViewStore()

const visitedViews = computed(() => tagsViewStore.visitedViews)

const visible = ref(false)
const top = ref(0)
const left = ref(0)
const selectedTag = ref(null)

const isActive = (tag) => {
  return tag.path === route.path
}

const addTags = () => {
  const { name } = route
  if (name) {
    tagsViewStore.addVisitedView(route)
    tagsViewStore.addCachedView(route)
  }
}

const closeSelectedTag = (view) => {
  tagsViewStore.delVisitedView(view).then((visitedViews) => {
    if (isActive(view)) {
      toLastView(visitedViews, view)
    }
  })
  tagsViewStore.delCachedView(view)
}

const closeOthersTags = () => {
  if (selectedTag.value) {
    router.push(selectedTag.value)
    tagsViewStore.delOthersVisitedViews(selectedTag.value)
    tagsViewStore.delOthersCachedViews(selectedTag.value)
  }
}

const closeAllTags = () => {
  tagsViewStore.delAllVisitedViews()
  tagsViewStore.delAllCachedViews()
  toLastView(visitedViews.value, selectedTag.value)
}

const refreshSelectedTag = (view) => {
  tagsViewStore.delCachedView(view)
  const { fullPath } = view
  nextTick(() => {
    router.replace({ path: '/redirect' + fullPath })
  })
}

const toLastView = (visitedViews, view) => {
  const latestView = visitedViews.slice(-1)[0]
  if (latestView) {
    router.push(latestView.fullPath)
  } else {
    if (view?.name === 'Dashboard') {
      router.replace({ path: '/redirect' + view.fullPath })
    } else {
      router.push('/')
    }
  }
}

const openMenu = (tag, e) => {
  const menuMinWidth = 105
  const offsetLeft = document.body.getBoundingClientRect().left
  const offsetWidth = document.body.offsetWidth
  const maxLeft = offsetWidth - menuMinWidth
  const l = e.clientX - offsetLeft + 15

  left.value = l > maxLeft ? maxLeft : l
  top.value = e.clientY
  visible.value = true
  selectedTag.value = tag
}

const closeMenu = () => {
  visible.value = false
}

watch(
  () => route.path,
  () => {
    addTags()
  }
)

watch(visible, (value) => {
  if (value) {
    document.body.addEventListener('click', closeMenu)
  } else {
    document.body.removeEventListener('click', closeMenu)
  }
})

onMounted(() => {
  addTags()
})
</script>

<style lang="scss" scoped>
.tags-view-container {
  height: 34px;
  width: 100%;
  background: #fff;
  border-bottom: 1px solid #d8dce5;
  box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.12), 0 0 3px 0 rgba(0, 0, 0, 0.04);
  .tags-view-wrapper {
    .tags-view-item {
      display: inline-flex;
      align-items: center;
      position: relative;
      cursor: pointer;
      height: 26px;
      line-height: 26px;
      border: 1px solid #d8dce5;
      color: #495060;
      background: #fff;
      padding: 0 8px;
      font-size: 12px;
      margin-left: 5px;
      margin-top: 4px;
      border-radius: 3px;
      text-decoration: none;
      &:first-of-type {
        margin-left: 15px;
      }
      &:last-of-type {
        margin-right: 15px;
      }
      &.active {
        background-color: #409eff;
        color: #fff;
        border-color: #409eff;
        &::before {
          content: '';
          background: #fff;
          display: inline-block;
          width: 8px;
          height: 8px;
          border-radius: 50%;
          position: relative;
          margin-right: 5px;
        }
        .el-icon-close {
          background-color: rgba(255, 255, 255, 0.2);
          border-radius: 50%;
          &:hover {
            background-color: rgba(255, 255, 255, 0.3);
          }
        }
      }
      &:hover:not(.active) {
        color: #409eff;
      }
      .el-icon-close {
        margin-left: 5px;
        width: 14px;
        height: 14px;
        border-radius: 50%;
        text-align: center;
        transition: all 0.3s;
        display: inline-flex;
        align-items: center;
        justify-content: center;
        &:hover {
          background-color: #f56c6c;
          color: #fff;
        }
      }
    }
  }
  .context-menu {
    margin: 0;
    background: #fff;
    z-index: 3000;
    position: absolute;
    list-style-type: none;
    padding: 5px 0;
    border-radius: 4px;
    font-size: 12px;
    font-weight: 400;
    color: #333;
    box-shadow: 2px 2px 3px 0 rgba(0, 0, 0, 0.3);
    li {
      margin: 0;
      padding: 7px 16px;
      cursor: pointer;
      &:hover {
        background: #eee;
      }
    }
  }
}
</style>