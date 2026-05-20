<template>
  <div class="app-wrapper">
    <Sidebar class="sidebar-container" />
    <div class="main-container">
      <div class="fixed-header">
        <Navbar />
        <TagsView />
      </div>
      <AppMain />
    </div>
  </div>
</template>

<script setup>
import { Navbar, Sidebar, TagsView, AppMain } from './components'
</script>

<style lang="scss" scoped>
.app-wrapper {
  position: relative;
  height: 100%;
  width: 100%;
  display: flex;
}

.sidebar-container {
  transition: width 0.28s;
  width: 210px !important;
  background-color: #304156;
  height: 100%;
  position: fixed;
  font-size: 0px;
  top: 0;
  bottom: 0;
  left: 0;
  z-index: 1001;
  overflow: hidden;
}

.main-container {
  min-height: 100vh;
  transition: margin-left .28s;
  margin-left: 210px;
  position: relative;
  width: calc(100% - 210px);
}

.fixed-header {
  position: sticky;
  top: 0;
  z-index: 9;
  width: 100%;
  transition: width 0.28s;
}
</style>
