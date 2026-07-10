<template>
  <div class="record-page">
    <!-- 日期 + 餐别 -->
    <div class="top-bar">
      <el-date-picker
        v-model="recordDate" type="date" format="YYYY-MM-DD" value-format="YYYY-MM-DD"
        class="date-pick"
      />
      <div class="meal-tabs">
        <button
          v-for="m in meals" :key="m.value"
          :class="['meal-tab', { active: mealType === m.value }]"
          @click="mealType = m.value"
        >{{ m.icon }} {{ m.label }}</button>
      </div>
    </div>

    <!-- 搜索 -->
    <div class="search-bar">
      <input v-model="keyword" placeholder="搜索食物..." class="search-input" />
      <select v-model="category" class="cat-select">
        <option value="">全部分类</option>
        <option v-for="c in FOOD_CATEGORIES" :key="c" :value="c">{{ c }}</option>
      </select>
    </div>

    <!-- 食物列表 -->
    <div class="food-list" v-loading="foodLoading">
      <div class="empty" v-if="!foodLoading && foods.length === 0">没有找到食物</div>
      <div v-for="food in foods" :key="food.id" class="food-item" @click="openDialog(food)">
        <div class="food-left">
          <span class="food-emoji">{{ foodEmoji(food.name) }}</span>
          <div>
            <div class="food-name">{{ food.name }}</div>
            <div class="food-meta">{{ food.category }} · {{ food.calories }}kcal/{{ food.unit }}</div>
          </div>
        </div>
        <span class="food-plus">+</span>
      </div>
    </div>

    <!-- 已记录 -->
    <div class="records-card" v-if="records.length > 0">
      <div class="card-title">已记录 · {{ mealTypeLabel(mealType) }}（{{ totalCal }} kcal）</div>
      <div v-for="r in records" :key="r.id" class="rec-row">
        <span class="rec-name">{{ r.foodName }}</span>
        <span class="rec-qty">×{{ r.quantity }}</span>
        <span class="rec-cal">{{ r.totalCalories }} kcal</span>
        <button class="rec-del" @click="handleDelete(r.id!)">×</button>
      </div>
    </div>

    <!-- 份量弹窗 -->
    <el-dialog v-model="qtyVisible" title="输入份量" width="320px">
      <div v-if="selectedFood" class="qty-dialog">
        <span class="qty-emoji">{{ foodEmoji(selectedFood.name) }}</span>
        <p class="qty-name">{{ selectedFood.name }}</p>
        <p class="qty-meta">{{ selectedFood.calories }} kcal / {{ selectedFood.unit }}</p>
        <el-input-number v-model="inputQty" :min="0.1" :max="99" :step="0.5" :precision="1" />
        <span class="qty-unit">{{ selectedFood.unit }}</span>
        <p class="qty-est">≈ {{ Math.round(selectedFood.calories * inputQty) }} kcal</p>
      </div>
      <template #footer>
        <el-button @click="qtyVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmAdd" :loading="saving">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getFoods } from '@/api/food'
import { addMeal, getMeals, deleteMeal } from '@/api/meal'
import type { Food, MealRecord } from '@/types'
import { FOOD_CATEGORIES, MEAL_TYPE_MAP } from '@/types'

const recordDate = ref(new Date().toISOString().slice(0, 10))
const mealType = ref<'BREAKFAST' | 'LUNCH' | 'DINNER'>('BREAKFAST')
const meals = [
  { value: 'BREAKFAST', label: '早餐', icon: '🌅' },
  { value: 'LUNCH', label: '午餐', icon: '☀️' },
  { value: 'DINNER', label: '晚餐', icon: '🌙' }
]
const keyword = ref('')
const category = ref('')
const foods = ref<Food[]>([])
const foodLoading = ref(false)
const saving = ref(false)
const records = ref<MealRecord[]>([])
const qtyVisible = ref(false)
const selectedFood = ref<Food | null>(null)
const inputQty = ref(1)

const totalCal = ref(0)
const recalcTotal = () => { totalCal.value = records.value.reduce((s, r) => s + r.totalCalories, 0) }

function mealTypeLabel(t: string) { return MEAL_TYPE_MAP[t] || t }
function foodEmoji(name: string) {
  const m: Record<string,string> = {
    '米饭':'🍚','馒头':'🥟','面条（煮）':'🍜','小米粥':'🥣','全麦面包':'🍞','红薯':'🍠',
    '鸡蛋（煮）':'🥚','鸡胸肉':'🍗','猪瘦肉':'🥩','牛肉（瘦）':'🥩','虾仁':'🦐','三文鱼':'🐟',
    '西兰花':'🥦','番茄':'🍅','黄瓜':'🥒','菠菜':'🥬','胡萝卜':'🥕','大白菜':'🥬',
    '苹果':'🍎','香蕉':'🍌','橙子':'🍊','葡萄':'🍇','西瓜':'🍉','草莓':'🍓',
    '全脂牛奶':'🥛','豆浆（无糖）':'💧','酸奶（原味）':'🫙','橙汁':'🧃',
    '核桃':'🥜','杏仁':'🥜','黑巧克力':'🍫','苏打饼干':'🍪'
  }
  return m[name] || '🍽️'
}

async function loadFoods() {
  foodLoading.value = true
  try { foods.value = await getFoods(keyword.value || undefined, category.value || undefined) }
  finally { foodLoading.value = false }
}
async function loadRecords() {
  try {
    const all = await getMeals(recordDate.value)
    records.value = all.filter((r: MealRecord) => r.mealType === mealType.value)
    recalcTotal()
  } catch { records.value = [] }
}

watch([keyword, category], () => loadFoods(), { immediate: true })
watch([recordDate, mealType], () => loadRecords(), { immediate: true })

function openDialog(food: Food) { selectedFood.value = food; inputQty.value = 1; qtyVisible.value = true }

async function confirmAdd() {
  if (!selectedFood.value) return
  saving.value = true
  try {
    await addMeal({ recordDate: recordDate.value, mealType: mealType.value, foodId: selectedFood.value.id!, quantity: inputQty.value })
    ElMessage.success(`已添加：${selectedFood.value.name}`)
    qtyVisible.value = false
    loadRecords()
  } catch { ElMessage.error('添加失败') }
  finally { saving.value = false }
}

async function handleDelete(id: number) {
  try {
    await ElMessageBox.confirm('删除这条记录？', '确认', { type: 'warning' })
    await deleteMeal(id); loadRecords()
  } catch { /* 取消 */ }
}
</script>

<style scoped>
.record-page { max-width: 600px; margin: 0 auto; }

.top-bar { display: flex; align-items: center; gap: 12px; margin-bottom: 16px; flex-wrap: wrap; }
.date-pick { width: 150px; }
.meal-tabs { display: flex; gap: 4px; }
.meal-tab {
  padding: 6px 14px; border: 1px solid #dde; border-radius: 8px;
  background: #fff; font-size: 13px; cursor: pointer; transition: all 0.15s;
}
.meal-tab.active { background: #2d6a4f; color: #fff; border-color: #2d6a4f; }
.meal-tab:hover:not(.active) { border-color: #52b788; }

.search-bar { display: flex; gap: 8px; margin-bottom: 16px; }
.search-input {
  flex: 1; padding: 9px 14px; border: 1px solid #dde; border-radius: 8px;
  font-size: 14px; outline: none;
}
.search-input:focus { border-color: #52b788; }
.cat-select {
  width: 110px; padding: 9px 8px; border: 1px solid #dde; border-radius: 8px;
  font-size: 13px; outline: none; background: #fff;
}

.food-list { margin-bottom: 16px; }
.empty { text-align: center; color: #999; padding: 32px 0; font-size: 14px; }
.food-item {
  display: flex; align-items: center; justify-content: space-between;
  padding: 12px 16px; margin-bottom: 6px; background: #fff;
  border-radius: 10px; cursor: pointer; transition: box-shadow 0.15s;
  box-shadow: 0 1px 2px rgba(0,0,0,0.03);
}
.food-item:hover { box-shadow: 0 2px 8px rgba(0,0,0,0.08); }
.food-left { display: flex; align-items: center; gap: 12px; }
.food-emoji { font-size: 28px; }
.food-name { font-size: 15px; font-weight: 600; }
.food-meta { font-size: 12px; color: #999; }
.food-plus {
  width: 28px; height: 28px; border-radius: 50%; background: #e8f5e9;
  display: flex; align-items: center; justify-content: center;
  color: #2d6a4f; font-size: 18px; font-weight: 700;
}

.records-card {
  background: #fff; border-radius: 12px; padding: 16px 20px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.04);
}
.card-title { font-size: 14px; font-weight: 600; margin-bottom: 10px; color: #2c3e50; }
.rec-row { display: flex; align-items: center; padding: 8px 0; gap: 8px; border-bottom: 1px solid #f5f5f5; }
.rec-row:last-child { border: none; }
.rec-name { flex: 1; font-size: 14px; }
.rec-qty { color: #999; font-size: 13px; }
.rec-cal { font-weight: 600; color: #e76f51; font-size: 14px; }
.rec-del {
  width: 22px; height: 22px; border: none; background: #fef0ef; color: #d35b4a;
  border-radius: 50%; cursor: pointer; font-size: 14px; display: flex;
  align-items: center; justify-content: center;
}

.qty-dialog { text-align: center; }
.qty-emoji { font-size: 40px; }
.qty-name { font-size: 18px; font-weight: 700; margin: 8px 0 4px; }
.qty-meta { color: #999; font-size: 13px; }
.qty-unit { margin-left: 8px; color: #999; font-size: 13px; }
.qty-est { margin-top: 10px; font-weight: 700; color: #e76f51; font-size: 16px; }
</style>
