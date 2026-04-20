<script setup lang="ts">
import GlobalHeader from '@/components/GlobalHeader.vue'
import GlobalFooter from '@/components/GlobalFooter.vue'

const menuItems = [
  { key: 'home', label: '首页', path: '/' },
  { key: 'about', label: '关于', path: '/about' },
]
</script>

<template>
  <a-layout class="basic-layout">
    <a-layout-header class="header">
      <GlobalHeader :menu-items="menuItems" />
    </a-layout-header>
    <a-layout-content class="content">
      <router-view />
    </a-layout-content>
    <a-layout-footer class="footer">
      <GlobalFooter />
    </a-layout-footer>
  </a-layout>
</template>

<style scoped>
.basic-layout {
  min-height: 100vh;
}

.header {
  padding: 0;
  background: #fff;
  height: auto;
  line-height: normal;
}

.content {
  padding: 24px;
  background: #f0f2f5;
  min-height: calc(100vh - 64px - 70px);
}

.footer {
  padding: 0;
  background: transparent;
}

@media (max-width: 768px) {
  .content {
    padding: 12px;
  }
}
</style>
