<script setup lang="ts">
import { ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'

interface MenuItem {
  key: string
  label: string
  path: string
}

interface Props {
  menuItems?: MenuItem[]
}

const props = withDefaults(defineProps<Props>(), {
  menuItems: () => [{ key: 'home', label: '首页', path: '/' }],
})

const route = useRoute()
const router = useRouter()

const current = ref<string[]>(['home'])
// 监听路由变化更新当前选中菜单
// watch(
//   () => route.path,
//   (path) => {
//     const item = props.menuItems.find((item) => item.path === path)
//     if (item) {
//       current.value = [item.key]
//     }
//   },
//   { immediate: true },
// )
router.afterEach((to, from, next) => {
  current.value = [to.name]
})

const handleMenuClick = ({ key }: { key: string }) => {
  const item = props.menuItems.find((item) => item.key === key)
  if (item) {
    router.push(item.path)
  }
}
</script>

<template>
  <div class="header-container">
    <div class="logo-section">
      <img src="@/assets/logo.svg" alt="logo" class="logo" />
      <span class="site-title">编程导航</span>
    </div>
    <a-menu
      v-model:selectedKeys="current"
      mode="horizontal"
      :items="menuItems"
      @click="handleMenuClick"
      class="menu"
    />
    <div class="user-section">
      <a-button type="primary">登录</a-button>
    </div>
  </div>
</template>

<style scoped>
.header-container {
  display: flex;
  align-items: center;
  padding: 0 24px;
  height: 64px;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.logo-section {
  display: flex;
  align-items: center;
  margin-right: 48px;
  cursor: pointer;
}

.logo {
  height: 32px;
  margin-right: 12px;
}

.site-title {
  font-size: 18px;
  font-weight: 600;
  color: #1890ff;
  white-space: nowrap;
}

.menu {
  flex: 1;
  border-bottom: none;
  line-height: 64px;
}

.user-section {
  margin-left: auto;
}

@media (max-width: 768px) {
  .header-container {
    padding: 0 12px;
  }

  .logo-section {
    margin-right: 16px;
  }

  .site-title {
    font-size: 16px;
  }

  .menu {
    display: none;
  }
}
</style>
