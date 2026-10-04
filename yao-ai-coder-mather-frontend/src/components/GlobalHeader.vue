<template>
  <div class="header-container">
    <div class="logo-section">
      <img src="@/assets/logo.svg" alt="logo" class="logo" />
      <span class="site-title">AI应用生成</span>
    </div>
    <!-- 桌面端菜单 -->
    <a-menu
      v-model:selectedKeys="current"
      mode="horizontal"
      :items="menuItems"
      @click="handleMenuClick"
      class="menu desktop-only"
    />
    <!-- 移动端汉堡按钮 -->
    <MenuOutlined
      class="hamburger"
      :class="{ active: mobileMenuOpen }"
      @click="mobileMenuOpen = !mobileMenuOpen"
    />
    <!-- 移动端侧边菜单 -->
    <a-drawer
      :open="mobileMenuOpen"
      placement="right"
      :closable="false"
      class="mobile-drawer"
      @close="mobileMenuOpen = false"
    >
      <template #title>
        <span class="site-title mobile-title">AI应用生成</span>
      </template>
      <a-menu
        v-model:selectedKeys="current"
        mode="vertical"
        :items="menuItems"
        @click="
          (item) => {
            handleMenuClick(item)
            mobileMenuOpen = false
          }
        "
        class="mobile-menu"
      />
      <div class="mobile-user-section">
        <div v-if="loginUserStore.loginUser.id">
          <a-avatar :src="loginUserStore.loginUser.userAvatar" />
          <span class="mobile-username">{{ loginUserStore.loginUser.userName }}</span>
          <a-button type="primary" danger ghost block @click="logout" style="margin-top: 12px">
            <LogoutOutlined />
            退出登录
          </a-button>
        </div>
        <div v-else>
          <a-button
            type="primary"
            block
            @click="
              () => {
                router.push('/user/login')
                mobileMenuOpen = false
              }
            "
          >
            登录
          </a-button>
        </div>
      </div>
    </a-drawer>
    <!-- 桌面端用户区 -->
    <div class="user-section">
      <div v-if="loginUserStore.loginUser.id">
        <a-dropdown>
          <a-avatar :src="loginUserStore.loginUser.userAvatar"></a-avatar>
          {{ loginUserStore.loginUser.userName }}
          <template #overlay>
            <a-menu>
              <a-menu-item @click="logout">
                <LogoutOutlined />
                退出登录
              </a-menu-item>
            </a-menu>
          </template>
        </a-dropdown>
      </div>
      <div v-else>
        <a-button type="primary" @click="router.push('/user/login')">登录</a-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, h, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useLoginUserStore } from '@/store/loginUser.ts'
import { userLogout } from '@/api/userController.ts'
import LogoutOutlined, { MenuOutlined } from '@ant-design/icons-vue'
import { type MenuProps, message } from 'ant-design-vue'
import { HomeOutlined } from '@ant-design/icons-vue'

const loginUserStore = useLoginUserStore()

interface MenuItem {
  key: string
  label: string
  path: string
}

const router = useRouter()

const current = ref<string[]>(['home'])
const mobileMenuOpen = ref(false)

const originItems: MenuItem[] = [
  { key: 'home', icon: () => h(HomeOutlined), label: '首页', path: '/' },
  { key: '用户管理', label: '用户管理', path: '/admin/userManage' },
  { key: 'about', label: '关于', path: '/about' },
]

const filterMenus = (menus: MenuItem[]) => {
  return menus.filter((menu) => {
    if (menu.key === '用户管理') {
      const loginUser = loginUserStore.loginUser
      if (!loginUser || loginUser.userRole !== 'admin') {
        return false
      }
    }
    return true
  })
}

const menuItems = computed<MenuProps['items']>(() => filterMenus(originItems))

router.afterEach((to) => {
  current.value = [to.name as string]
})

const handleMenuClick = ({ key }: { key: string }) => {
  const item = originItems.find((i) => i.key === key)
  if (item) {
    router.push(item.path)
  }
}

const logout = async () => {
  const res = await userLogout()
  if (res.data.code === 0) {
    loginUserStore.setLoginUser({
      userName: '未登录',
    })
    message.success('退出登录成功')
    await router.push('/')
  } else {
    message.error('退出登录失败，' + res.data.message)
  }
}
</script>

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

.hamburger {
  display: none;
  font-size: 20px;
  color: #333;
  cursor: pointer;
  padding: 8px;
  transition: transform 0.3s;
}

.hamburger.active {
  transform: rotate(90deg);
}

/* 移动端样式 */
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

  .desktop-only {
    display: none !important;
  }

  .user-section {
    display: none;
  }

  .hamburger {
    display: flex;
    align-items: center;
  }

  .mobile-drawer {
    .ant-drawer-body {
      padding: 16px;
      display: flex;
      flex-direction: column;
    }
  }

  .mobile-menu {
    flex: 1;
    border-right: none;
  }

  .mobile-user-section {
    margin-top: 16px;
    padding-top: 16px;
    border-top: 1px solid #f0f0f0;
    display: flex;
    flex-direction: column;
    align-items: center;

    :deep(.ant-avatar) {
      margin-bottom: 8px;
    }

    .mobile-username {
      font-size: 14px;
      color: #333;
    }
  }
}
</style>
