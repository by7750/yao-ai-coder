<template>
  <a-card title="关于我们" class="about-card">
    <a-typography>
      <a-typography-title :level="4">编程导航</a-typography-title>
      <a-typography-paragraph>
        这是一个编程学习导航网站，旨在帮助开发者快速找到优质的学习资源和工具。
      </a-typography-paragraph>
      <a-typography-paragraph>
        本项目由程序员小y原创开发，致力于打造一个简洁、实用的编程资源聚合平台。
      </a-typography-paragraph>
      <a-divider />
      <a-typography-title :level="5">联系我们</a-typography-title>
      <a-typography-paragraph>
        如有任何问题或建议，欢迎通过以下方式联系我们：
      </a-typography-paragraph>
      <a-space direction="vertical">
        <a-typography-text>
          <mail-outlined /> 邮箱：contact@example.com
        </a-typography-text>
        <a-typography-text>
          <github-outlined /> GitHub：github.com/example
        </a-typography-text>
      </a-space>
    </a-typography>
  </a-card>
</template>

<script setup lang="ts">
import { MailOutlined, GithubOutlined } from '@ant-design/icons-vue'
</script>

<style scoped>
.about-card {
  max-width: 800px;
  margin: 0 auto;
}
</style>
