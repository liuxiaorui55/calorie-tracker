<template>
  <div class="dashboard">
    <!-- 功能介绍 -->
    <div class="intro-card">
      <div class="intro-header">
        <span>👋 功能介绍</span>
        <button class="intro-toggle" @click="showIntro = !showIntro">{{ showIntro ? '收起 ▲' : '展开 ▼' }}</button>
      </div>
      <div class="intro-grid" v-if="showIntro">
        <div class="intro-item"><span class="intro-icon">📊</span><strong>热量概览</strong><p>查看每日摄入总热量、三餐分布和营养素占比，获取个性化饮食建议。</p></div>
        <div class="intro-item"><span class="intro-icon">📝</span><strong>记录餐食</strong><p>点击下方三餐卡片选择餐别，搜索食物填份量即可记录。</p></div>
        <div class="intro-item"><span class="intro-icon">🍱</span><strong>食物库</strong><p>管理食物数据，支持搜索、分类筛选、新增和编辑。</p></div>
      </div>
    </div>

    <!-- 日期选择 -->
    <div class="date-bar">
      <button class="date-arrow" @click="prevDay">‹</button>
      <el-date-picker v-model="currentDate" type="date" format="YYYY-MM-DD" value-format="YYYY-MM-DD" @change="loadAll" class="date-picker" />
      <button class="date-arrow" @click="nextDay">›</button>
      <span class="date-today" @click="goToday">今天</span>
    </div>

    <!-- 总热量 -->
    <div class="calorie-ring" :class="{ empty: !summary }">
      <div class="ring-label">今日摄入</div>
      <div class="ring-value">{{ summary?.totalCalories ?? 0 }}</div>
      <div class="ring-unit">/ {{ targetCal }} kcal</div>
      <div class="ring-bar"><div class="ring-fill" :style="{ width: calPercent + '%', background: calColor }"></div></div>
      <div class="ring-hint">{{ calLabel }}</div>
    </div>

    <!-- 三餐卡片 -->
    <div class="meal-row">
      <div v-for="m in mealTypes" :key="m.value"
        :class="['meal-card', { active: recordingMeal === m.value }]"
      >
        <div class="meal-icon">{{ m.icon }}</div>
        <div class="meal-name">{{ m.label }}</div>
        <div class="meal-kcal">{{ mealCal(m.value) }}<small> kcal</small></div>
      </div>
    </div>

    <!-- 营养素 -->
    <div class="macro-row" v-if="summary && summary.totalCalories > 0">
      <div class="macro-item" v-for="m in macros" :key="m.key">
        <div class="macro-dot" :style="{ background: m.color }"></div>
        <div class="macro-info">
          <span class="macro-val">{{ m.value }}g</span>
          <span class="macro-tag">{{ m.label }}</span>
        </div>
      </div>
    </div>

    <!-- 📝 快速记录区（始终可见） -->
    <div class="section-title">📝 快速记录</div>
    <div class="record-box">
      <div class="meal-tabs">
        <button v-for="m in mealTypes" :key="m.value"
          :class="['meal-tab', { active: recordingMeal === m.value }]"
          @click="startRecord(m.value)"
        >{{ m.icon }} {{ m.label }}</button>
      </div>
      <div class="search-bar">
        <input v-model="keyword" placeholder="搜索食物..." class="search-input" @input="loadFoods" />
        <select v-model="category" class="cat-select" @change="loadFoods">
          <option value="">全部分类</option>
          <option v-for="c in FOOD_CATEGORIES" :key="c" :value="c">{{ c }}</option>
        </select>
      </div>
      <div class="food-mini-list" v-loading="foodLoading">
        <div v-for="food in foods" :key="food.id" class="food-mini" @click="openQty(food)">
          <span class="food-emoji-sm">{{ foodEmoji(food.name) }}</span>
          <span class="food-name-sm">{{ food.name }}</span>
          <span class="food-cal-sm">{{ food.calories }}kcal</span>
          <span class="food-plus-sm">+</span>
        </div>
        <div v-if="!foodLoading && foods.length === 0" class="food-empty">未找到食物</div>
      </div>
    </div>

    <!-- 饮食建议 -->
    <div class="tips-section" v-if="tips.length > 0">
      <div class="section-title">💡 饮食建议</div>
      <div v-for="(tip, i) in tips" :key="i" class="tip-item">{{ tip }}</div>
    </div>

    <!-- 今日记录 -->
    <div class="records-section" v-if="summary?.records?.length">
      <div class="section-title">📋 今日记录</div>
      <div v-for="r in summary.records" :key="r.id" class="record-row">
        <span class="rec-tag">{{ mealTypeLabel(r.mealType) }}</span>
        <span class="rec-name">{{ r.foodName }} ×{{ r.quantity }}</span>
        <span class="rec-cal">{{ r.totalCalories }} kcal</span>
        <button class="rec-del" @click="handleDelete(r.id!)">×</button>
      </div>
    </div>

    <!-- 份量弹窗 -->
    <el-dialog v-model="qtyVisible" title="输入份量" width="320px">
      <div v-if="selectedFood" style="text-align:center">
        <p style="font-size:18px;font-weight:700">{{ selectedFood.name }}</p>
        <p style="color:#999">{{ selectedFood.calories }} kcal / {{ selectedFood.unit }}</p>
        <el-input-number v-model="inputQty" :min="0.1" :max="99" :step="0.5" :precision="1" style="margin-top:8px" />
        <span style="margin-left:8px;color:#999">{{ selectedFood.unit }}</span>
        <p style="margin-top:8px;font-weight:700;color:#e76f51">≈ {{ Math.round(selectedFood.calories * inputQty) }} kcal</p>
      </div>
      <template #footer>
        <el-button @click="qtyVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmAdd" :loading="saving">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getSummary, getSuggestion, addMeal, getMeals, deleteMeal } from '@/api/meal'
import { getFoods } from '@/api/food'
import { useAuthStore } from '@/stores/auth'
import type { MealSummary, Food, MealRecord } from '@/types'
import { MEAL_TYPE_MAP, FOOD_CATEGORIES } from '@/types'

const auth = useAuthStore()
const showIntro = ref(true)
const currentDate = ref(new Date().toISOString().slice(0, 10))
const summary = ref<MealSummary | null>(null)
const tips = ref<string[]>([])

// 记录相关
const recordingMeal = ref('BREAKFAST')
const keyword = ref('')
const category = ref('')
const foods = ref<Food[]>([])
const foodLoading = ref(false)
const saving = ref(false)
const qtyVisible = ref(false)
const selectedFood = ref<Food | null>(null)
const inputQty = ref(1)

const mealTypes = [
  { value: 'BREAKFAST', label: '早餐', icon: '🌅' },
  { value: 'LUNCH', label: '午餐', icon: '☀️' },
  { value: 'DINNER', label: '晚餐', icon: '🌙' }
]

const macros = computed(() => {
  if (!summary.value) return []
  return [
    { key: 'protein', label: '蛋白质', value: summary.value.totalProtein, color: '#52b788' },
    { key: 'fat', label: '脂肪', value: summary.value.totalFat, color: '#f4a261' },
    { key: 'carbs', label: '碳水', value: summary.value.totalCarbs, color: '#5b9bd5' }
  ]
})

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

const calPercent = computed(() => {
  if (!summary.value) return 0
  return Math.min(100, Math.round((summary.value.totalCalories / targetCal.value) * 100))
})
const calColor = computed(() => {
  const p = calPercent.value
  if (p <= 70) return '#52b788'; if (p <= 100) return '#5b9bd5'
  if (p <= 120) return '#f4a261'; return '#e76f51'
})
const calLabel = computed(() => {
  const p = calPercent.value
  if (p === 0) return '还没有记录'
  if (p <= 70) return '还可以再吃点'; if (p <= 100) return '摄入刚刚好 👍'
  if (p <= 120) return '稍微多了一点'; return '今天超标啦'
})

function mealCal(type: string) {
  if (!summary.value) return 0
  if (type === 'BREAKFAST') return summary.value.breakfastCalories
  if (type === 'LUNCH') return summary.value.lunchCalories
  return summary.value.dinnerCalories
}
function mealTypeLabel(t: string) { return MEAL_TYPE_MAP[t] || t }

function foodEmoji(name: string) {
  const m: Record<string,string> = {
    '米饭':'🍚','馒头':'🥟','面条（煮）':'🍜','小米粥':'🥣','全麦面包':'🍞','红薯':'🍠',
    '鸡蛋（煮）':'🥚','鸡胸肉':'🍗','猪瘦肉':'🥩','牛肉（瘦）':'🥩','虾仁':'🦐','三文鱼':'🐟',
    '西兰花':'🥦','番茄':'🍅','黄瓜':'🥒','菠菜':'🥬','胡萝卜':'🥕','大白菜':'🥬',
    '苹果':'🍎','香蕉':'🍌','橙子':'🍊','葡萄':'🍇','西瓜':'🍉','草莓':'🍓',
    '全脂牛奶':'🥛','豆浆（无糖）':'💧','酸奶（原味）':'🫙','橙汁':'🧃',
    '核桃':'🥜','杏仁':'🥜','黑巧克力':'🍫','苏打饼干':'🍪'
  }; return m[name]||'🍽️'
}

function startRecord(meal: string) {
  recordingMeal.value = meal
}

async function loadFoods() {
  foodLoading.value = true
  try { foods.value = await getFoods(keyword.value || undefined, category.value || undefined) }
  finally { foodLoading.value = false }
}

function openQty(food: Food) { selectedFood.value = food; inputQty.value = 1; qtyVisible.value = true }

async function confirmAdd() {
  if (!selectedFood.value || !recordingMeal.value) return
  saving.value = true
  try {
    await addMeal({ recordDate: currentDate.value, mealType: recordingMeal.value, foodId: selectedFood.value.id!, quantity: inputQty.value })
    ElMessage.success(`已记录：${selectedFood.value.name}`)
    qtyVisible.value = false
    loadAll()
  } catch { ElMessage.error('添加失败') }
  finally { saving.value = false }
}

async function handleDelete(id: number) {
  try { await ElMessageBox.confirm('删除？', '确认', { type: 'warning' }); await deleteMeal(id); loadAll() }
  catch { /* 取消 */ }
}

async function loadAll() {
  try {
    const [s, sug] = await Promise.all([getSummary(currentDate.value), getSuggestion(currentDate.value)])
    summary.value = s; tips.value = sug.tips || []
  } catch { summary.value = null; tips.value = [] }
}

function prevDay() { const d = new Date(currentDate.value); d.setDate(d.getDate() - 1); currentDate.value = d.toISOString().slice(0, 10); loadAll() }
function nextDay() { const d = new Date(currentDate.value); d.setDate(d.getDate() + 1); currentDate.value = d.toISOString().slice(0, 10); loadAll() }
function goToday() { currentDate.value = new Date().toISOString().slice(0, 10); loadAll() }

onMounted(() => { loadAll(); loadFoods() })
</script>

<style scoped>
.dashboard { max-width: 600px; margin: 0 auto; }

/* 功能介绍 */
.intro-card { background: #fff; border-radius: 12px; padding: 16px 20px; margin-bottom: 16px; box-shadow: 0 1px 3px rgba(0,0,0,0.04); border-left: 3px solid #52b788; }
.intro-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; font-weight: 600; font-size: 15px; }
.intro-toggle { border: none; background: #f0f4f3; font-size: 12px; cursor: pointer; color: #2d6a4f; padding: 4px 10px; border-radius: 4px; }
.intro-grid { display: grid; grid-template-columns: 1fr 1fr 1fr; gap: 12px; }
.intro-item { text-align: center; }
.intro-item strong { display: block; font-size: 13px; margin: 4px 0; }
.intro-item p { font-size: 11px; color: #999; line-height: 1.5; margin: 0; }
.intro-icon { font-size: 24px; }

/* 日期 */
.date-bar { display: flex; align-items: center; justify-content: center; gap: 8px; margin-bottom: 20px; }
.date-arrow { width: 32px; height: 32px; border: 1px solid #dde; border-radius: 8px; background: #fff; font-size: 18px; cursor: pointer; color: #666; display: flex; align-items: center; justify-content: center; }
.date-arrow:hover { background: #e8f5e9; border-color: #52b788; }
.date-picker { width: 150px; }
.date-today { font-size: 13px; color: #2d6a4f; cursor: pointer; padding: 4px 8px; border-radius: 4px; }
.date-today:hover { background: #e8f5e9; }

/* 热量环 */
.calorie-ring { text-align: center; padding: 28px 20px; background: #fff; border-radius: 16px; margin-bottom: 14px; box-shadow: 0 1px 3px rgba(0,0,0,0.04); }
.calorie-ring.empty { opacity: 0.6; }
.ring-label { font-size: 13px; color: #999; margin-bottom: 4px; }
.ring-value { font-size: 48px; font-weight: 800; color: #2d6a4f; line-height: 1.1; }
.ring-unit { font-size: 14px; color: #999; margin-bottom: 14px; }
.ring-bar { width: 100%; height: 8px; background: #e8edeb; border-radius: 4px; overflow: hidden; }
.ring-fill { height: 100%; border-radius: 4px; transition: width 0.5s ease; }
.ring-hint { font-size: 13px; color: #999; margin-top: 8px; }

/* 三餐卡片 */
.meal-row { display: grid; grid-template-columns: 1fr 1fr 1fr; gap: 10px; margin-bottom: 14px; }
.meal-card { background: #fff; border-radius: 12px; padding: 14px 10px; text-align: center; box-shadow: 0 1px 3px rgba(0,0,0,0.04); transition: all 0.15s; border: 2px solid transparent; }
.meal-card.active { border-color: #2d6a4f; background: #f0faf4; }
.meal-icon { font-size: 22px; margin-bottom: 2px; }
.meal-name { font-size: 12px; color: #888; margin-bottom: 2px; }
.meal-kcal { font-size: 20px; font-weight: 700; color: #2c3e50; }
.meal-kcal small { font-size: 11px; font-weight: 400; color: #999; }

/* 营养素 */
.macro-row { display: flex; gap: 10px; margin-bottom: 14px; }
.macro-item { flex: 1; background: #fff; border-radius: 12px; padding: 12px; display: flex; align-items: center; gap: 10px; box-shadow: 0 1px 3px rgba(0,0,0,0.04); }
.macro-dot { width: 10px; height: 10px; border-radius: 50%; flex-shrink: 0; }
.macro-info { display: flex; flex-direction: column; }
.macro-val { font-size: 16px; font-weight: 700; }
.macro-tag { font-size: 11px; color: #999; }

/* 快速记录区 */
.meal-tabs { display: flex; gap: 6px; margin-bottom: 10px; }
.meal-tab {
  padding: 6px 14px; border: 1px solid #dde; border-radius: 8px;
  background: #fff; font-size: 13px; cursor: pointer; transition: all 0.15s;
}
.meal-tab.active { background: #2d6a4f; color: #fff; border-color: #2d6a4f; }
.meal-tab:hover:not(.active) { border-color: #52b788; }
.record-box { background: #fff; border-radius: 12px; padding: 14px 18px; margin-bottom: 14px; box-shadow: 0 1px 3px rgba(0,0,0,0.04); }
.search-bar { display: flex; gap: 8px; margin-bottom: 10px; }
.search-input { flex: 1; padding: 8px 12px; border: 1px solid #dde; border-radius: 8px; font-size: 13px; outline: none; }
.search-input:focus { border-color: #52b788; }
.cat-select { width: 100px; padding: 8px; border: 1px solid #dde; border-radius: 8px; font-size: 13px; outline: none; background: #fff; }
.food-mini-list { max-height: 240px; overflow-y: auto; }
.food-mini { display: flex; align-items: center; gap: 10px; padding: 8px 10px; border-radius: 8px; cursor: pointer; transition: background 0.1s; }
.food-mini:hover { background: #f5faf7; }
.food-emoji-sm { font-size: 22px; }
.food-name-sm { flex: 1; font-size: 14px; }
.food-cal-sm { font-size: 12px; color: #999; }
.food-plus-sm { width: 24px; height: 24px; border-radius: 50%; background: #e8f5e9; color: #2d6a4f; display: flex; align-items: center; justify-content: center; font-size: 16px; font-weight: 700; }
.food-empty { text-align: center; padding: 20px; color: #999; font-size: 13px; }

/* 建议 */
.section-title { font-size: 14px; font-weight: 600; margin-bottom: 10px; color: #2c3e50; }
.tips-section { background: #fff; border-radius: 12px; padding: 14px 18px; margin-bottom: 14px; box-shadow: 0 1px 3px rgba(0,0,0,0.04); }
.tip-item { font-size: 14px; line-height: 1.7; padding: 3px 0; color: #555; }

/* 记录 */
.records-section { background: #fff; border-radius: 12px; padding: 14px 18px; margin-bottom: 14px; box-shadow: 0 1px 3px rgba(0,0,0,0.04); }
.record-row { display: flex; align-items: center; padding: 8px 0; gap: 8px; border-bottom: 1px solid #f5f5f5; }
.record-row:last-child { border: none; }
.rec-tag { font-size: 11px; background: #e8f5e9; color: #2d6a4f; padding: 2px 8px; border-radius: 4px; flex-shrink: 0; }
.rec-name { flex: 1; font-size: 14px; }
.rec-cal { font-size: 13px; font-weight: 600; color: #e76f51; }
.rec-del { width: 22px; height: 22px; border: none; background: #fef0ef; color: #d35b4a; border-radius: 50%; cursor: pointer; font-size: 14px; display: flex; align-items: center; justify-content: center; }
</style>
