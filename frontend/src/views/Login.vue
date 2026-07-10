<template>
  <div class="login-page">
    <div class="login-card">
      <div class="login-logo">🍽️</div>
      <h2>{{ isRegister ? '创建账号' : '欢迎回来' }}</h2>
      <p class="login-sub">{{ isRegister ? '填写信息开始记录饮食' : '登录查看你的热量统计' }}</p>

      <div class="form-item">
        <input v-model="form.username" placeholder="用户名" class="input" />
      </div>
      <div class="form-item">
        <input v-model="form.password" type="password" placeholder="密码" class="input" />
      </div>

      <template v-if="isRegister">
        <div class="form-row">
          <label class="radio-label">
            <input type="radio" v-model="form.gender" value="男" /> 男
          </label>
          <label class="radio-label">
            <input type="radio" v-model="form.gender" value="女" /> 女
          </label>
        </div>
        <div class="form-item">
          <input v-model.number="form.age" type="number" placeholder="年龄" class="input" min="1" max="120" />
        </div>
      </template>

      <button class="btn-primary" @click="handleSubmit" :disabled="loading">
        {{ loading ? '请稍候...' : (isRegister ? '注册并登录' : '登录') }}
      </button>

      <p class="switch-text">
        {{ isRegister ? '已有账号？' : '没有账号？' }}
        <a @click="isRegister = !isRegister">{{ isRegister ? '去登录' : '去注册' }}</a>
      </p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const auth = useAuthStore()
const isRegister = ref(false)
const loading = ref(false)
const form = reactive({ username: '', password: '', gender: '男', age: null as number | null })

async function handleSubmit() {
  if (!form.username || !form.password) { ElMessage.warning('请输入账号和密码'); return }
  loading.value = true
  try {
    if (isRegister.value) {
      await auth.register(form.username, form.password, form.gender, form.age)
      await auth.login(form.username, form.password)
      ElMessage.success('注册成功')
    } else {
      await auth.login(form.username, form.password)
    }
    router.push('/')
  } catch (e: any) {
    ElMessage.error(e.message || '操作失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  display: flex; justify-content: center; align-items: center; min-height: 70vh;
}
.login-card {
  width: 360px; max-width: 90vw;
  background: #fff; border-radius: 16px; padding: 40px 32px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06); text-align: center;
}
.login-logo { font-size: 40px; margin-bottom: 8px; }
h2 { margin: 0 0 4px; font-size: 22px; color: #2c3e50; }
.login-sub { font-size: 13px; color: #999; margin-bottom: 24px; }
.form-item { margin-bottom: 12px; }
.form-row { display: flex; gap: 24px; justify-content: center; margin-bottom: 12px; }
.radio-label { font-size: 14px; color: #555; cursor: pointer; display: flex; align-items: center; gap: 4px; }
.input {
  width: 100%; padding: 10px 14px; border: 1px solid #dde; border-radius: 8px;
  font-size: 14px; outline: none; transition: border-color 0.15s;
}
.input:focus { border-color: #52b788; }
.btn-primary {
  width: 100%; padding: 10px; background: #2d6a4f; color: #fff;
  border: none; border-radius: 8px; font-size: 15px; cursor: pointer;
  margin-top: 8px; transition: background 0.15s;
}
.btn-primary:hover { background: #1b4332; }
.btn-primary:disabled { opacity: 0.6; cursor: not-allowed; }
.switch-text { margin-top: 16px; font-size: 13px; color: #999; }
.switch-text a { color: #2d6a4f; cursor: pointer; font-weight: 600; }
</style>
