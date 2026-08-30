<template>
  <div id="userLoginPage">
    <h2 class="title">AI 代码生成 - 用户登录</h2>
    <div class="desc">不写一行代码，生成完整应用</div>
    <a-form :model="formState" name="basic" autocomplete="off" @finish="handleSubmit">
      <a-form-item name="userAccount" :rules="[{ required: true, message: '请输入账号' }]">
        <a-input v-model:value="formState.userAccount" placeholder="请输入账号" />
      </a-form-item>

      <a-form-item
        name="userPassword"
        :rules="[
          { required: true, message: '请输入密码' },
          { min: 6, message: '密码不能小于6位' },
        ]"
      >
        <a-input-password v-model:value="formState.userPassword" placeholder="请输入密码" />
      </a-form-item>
      <div class="tips">没有账号<router-link to="/user/register">去注册</router-link></div>
      <a-form-item>
        <a-button type="primary" html-type="submit" style="width: 100%">登录</a-button>
      </a-form-item>
    </a-form>
  </div>
</template>
<script lang="ts" setup>
import { reactive } from 'vue'
import { login } from '@/api/userController.ts'
import { useLoginUserStore } from '@/store/loginUser.ts'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
const loginUser = useLoginUserStore()
const router = useRouter()

const formState = reactive<API.UserLoginRequest>({
  userAccount: '',
  userPassword: '',
})
const handleSubmit = async (values: any) => {
  const res = await login(formState)
  if (res.data.code === 0 && res.data.data) {
    message.success('登录成功')
    router.push({
      path: '/',
      replace: true,
    })
    loginUser.setLoginUser(res.data.data)
  } else {
    message.error('登录失败，' + res.data.message)
  }
}

// const userLogin = async () => {
//   const userInfo: API.BaseResponseLoginUserVO = login(formState)
//   if (userInfo.code === 0 && userInfo.data) {
//     // 登录成功
//   } else {
//     // 失败提示
//   }
// }
</script>

<style scoped>
#userLoginPage {
  max-width: 480px;
  margin: 0 auto;
}
.title {
  text-align: center;
  margin-bottom: 16px;
}
.desc {
  text-align: center;
  color: #bbb;
  margin-bottom: 16px;
}
.tips {
  text-align: right;
  font-size: 13px;
  color: #bbb;
  margin-bottom: 16px;
}
</style>
