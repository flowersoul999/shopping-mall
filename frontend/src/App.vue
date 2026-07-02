<template>
  <div id="app-wrapper">
    <NavBar />
    <div class="main-content">
      <router-view />
    </div>
    <FooterBar />
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import NavBar from './components/NavBar.vue'
import FooterBar from './components/FooterBar.vue'
import { useUserStore } from './store/user'
import { useCartStore } from './store/cart'

const userStore = useUserStore()
const cartStore = useCartStore()

onMounted(() => {
  if (userStore.isLoggedIn) {
    userStore.fetchUserInfo()
    cartStore.fetchCartList()
  }
})
</script>

<style>
#app-wrapper {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.main-content {
  flex: 1;
  padding-top: 60px;
}
</style>
