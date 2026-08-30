<script setup lang="ts">
import BasicLayout from '@/layouts/BasicLayout.vue'
import { useLoginUserStore } from '@/store/loginUser.ts'
const useLoginUser = useLoginUserStore()
useLoginUser.fetchLoginUser()

</script>

<template>
  <BasicLayout />
</template>
