<template>
  <div id="app">
    <Navbar v-if="!isAdminRoute" />
    <main id="main-content" :class="isAdminRoute ? '' : 'page-container'" role="main" aria-label="主要内容">
      <router-view />
    </main>
    <Footer v-if="!isAdminRoute" />
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useCartStore } from '@/stores/cart'
import Navbar from '@/components/layout/Navbar.vue'
import Footer from '@/components/layout/Footer.vue'

const route = useRoute()
const userStore = useUserStore()
const cartStore = useCartStore()

const isAdminRoute = computed(() => {
  return route.path.startsWith('/admin')
})

onMounted(() => {
  if (userStore.isLoggedIn) {
    cartStore.fetchCartCount()
  } else {
    cartStore.resetCart()
  }
})
</script>

<style scoped>
#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.page-container {
  flex: 1;
  background-color: #f5f7fa;
}
</style>
