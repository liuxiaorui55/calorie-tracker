<template>
  <div id="app-container">
    <!-- 顶部导航 -->
    <header class="nav-header">
      <div class="nav-inner">
        <span class="nav-logo" @click="$router.push('/')">🍽️ 热量统计</span>
        <nav class="nav-links">
          <router-link to="/" class="nav-link">概览</router-link>
          <router-link to="/foods" class="nav-link">食物库</router-link>
          <router-link to="/profile" class="nav-link">我的</router-link>
          <a class="nav-link logout" @click="handleLogout">退出</a>
        </nav>
      </div>
    </header>

    <!-- 页面内容 -->
    <main class="page-content">
      <router-view />
    </main>
  </div>
</template>

<script setup lang="ts">
import { ElMessageBox } from 'element-plus'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()

async function handleLogout() {
  if (!auth.isLoggedIn()) {
    window.location.replace('/login')
    return
  }
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '退出确认', {
      type: 'warning',
      confirmButtonText: '确定退出',
      cancelButtonText: '取消'
    })
    await auth.logout()
    window.location.replace('/login')
  } catch { /* 取消 */ }
}
</script>

<style>
* { box-sizing: border-box; }
body {
  margin: 0;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Microsoft YaHei', sans-serif;
  background: #f0f4f3;
  color: #2c3e50;
  -webkit-font-smoothing: antialiased;
}
#app-container { min-height: 100vh; }

/* 导航 */
.nav-header {
  background: #fff;
  border-bottom: 1px solid #e8edeb;
  position: sticky;
  top: 0;
  z-index: 100;
  backdrop-filter: blur(10px);
}
.nav-inner {
  max-width: 860px;
  margin: 0 auto;
  padding: 0 20px;
  height: 52px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.nav-logo {
  font-size: 18px;
  font-weight: 700;
  color: #2d6a4f;
  cursor: pointer;
  user-select: none;
}
.nav-links { display: flex; gap: 4px; }
.nav-link {
  text-decoration: none;
  color: #5a6d65;
  font-size: 14px;
  padding: 6px 14px;
  border-radius: 6px;
  transition: all 0.15s;
  cursor: pointer;
}
.nav-link:hover { background: #e8f5e9; color: #2d6a4f; }
.nav-link.router-link-active { background: #e8f5e9; color: #2d6a4f; font-weight: 600; }
.nav-link.logout { color: #c0a8a0; }
.nav-link.logout:hover { background: #fef0ef; color: #d35b4a; }

/* 内容区 */
.page-content {
  max-width: 860px;
  margin: 0 auto;
  padding: 24px 20px 60px;
}
</style>
