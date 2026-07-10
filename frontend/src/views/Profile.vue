<template>
  <div class="profile-page">
    <div class="profile-card">
      <div class="avatar">👤</div>
      <h2>{{ auth.user?.username || '用户' }}</h2>

      <div class="info-grid">
        <div class="info-item">
          <span class="info-label">性别</span>
          <span class="info-value">{{ auth.user?.gender || '未设置' }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">年龄</span>
          <span class="info-value">{{ auth.user?.age ? auth.user.age + ' 岁' : '未设置' }}</span>
        </div>
      </div>
    </div>

    <div class="calorie-card">
      <div class="calorie-label">每日推荐热量</div>
      <div class="calorie-num">{{ targetCal }} <small>kcal</small></div>
      <div class="calorie-desc">根据你的性别和年龄计算</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()

const targetCal = computed(() => {
  const user = auth.user; let base = 2000
  if (!user) return base
  if (user.gender === '男') base = 2250
  else if (user.gender === '女') base = 1800
  if (user.age) {
    if (user.age < 14) base = Math.round(base * 0.8)
    else if (user.age > 60) base = Math.round(base * 0.85)
    else if (user.age > 45 && user.gender === '女') base = Math.round(base * 0.9)
  }
  return base
})
</script>

<style scoped>
.profile-page { max-width: 480px; margin: 0 auto; }

.profile-card {
  background: #fff; border-radius: 16px; padding: 32px 24px;
  text-align: center; box-shadow: 0 1px 3px rgba(0,0,0,0.04); margin-bottom: 16px;
}
.avatar { font-size: 56px; margin-bottom: 8px; }
h2 { margin: 0 0 20px; font-size: 20px; color: #2c3e50; }

.info-grid { display: flex; gap: 0; }
.info-item {
  flex: 1; padding: 12px 0; border-top: 1px solid #f0f0f0;
  display: flex; flex-direction: column; gap: 4px;
}
.info-item:first-child { border-right: 1px solid #f0f0f0; }
.info-label { font-size: 12px; color: #999; }
.info-value { font-size: 18px; font-weight: 700; color: #2c3e50; }

.calorie-card {
  background: linear-gradient(135deg, #2d6a4f, #40916c);
  border-radius: 16px; padding: 28px 24px; text-align: center;
  color: #fff; box-shadow: 0 2px 8px rgba(45,106,79,0.2);
}
.calorie-label { font-size: 13px; opacity: 0.8; }
.calorie-num { font-size: 42px; font-weight: 800; margin: 4px 0; }
.calorie-num small { font-size: 16px; font-weight: 400; opacity: 0.8; }
.calorie-desc { font-size: 13px; opacity: 0.7; }
</style>
