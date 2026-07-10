<template>
  <div class="food-page">
    <div class="page-header">
      <h2>食物库</h2>
      <button class="btn-add" @click="openAdd">+ 添加食物</button>
    </div>

    <div class="search-bar">
      <input v-model="keyword" placeholder="搜索食物..." class="search-input" />
      <select v-model="category" class="cat-select">
        <option value="">全部分类</option>
        <option v-for="c in FOOD_CATEGORIES" :key="c" :value="c">{{ c }}</option>
      </select>
    </div>

    <div v-loading="loading">
      <div class="empty" v-if="!loading && foods.length === 0">暂无食物</div>
      <div v-for="food in foods" :key="food.id" class="food-row">
        <span class="food-emoji">{{ foodEmoji(food.name) }}</span>
        <div class="food-info">
          <span class="food-name">{{ food.name }}</span>
          <span class="food-meta">{{ food.category }} · {{ food.calories }} kcal / {{ food.unit }}</span>
        </div>
        <div class="food-nums">
          <span>蛋白{{ food.protein }}g</span>
          <span>脂肪{{ food.fat }}g</span>
          <span>碳水{{ food.carbs }}g</span>
        </div>
        <button class="btn-edit" @click="openEdit(food)">✎</button>
        <button class="btn-del" @click="handleDelete(food)">×</button>
      </div>
    </div>

    <!-- 弹窗 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑食物' : '添加食物'" width="380px">
      <div class="form-grid">
        <label>名称</label><input v-model="form.name" class="f-input" placeholder="如：米饭" />
        <label>分类</label>
        <select v-model="form.category" class="f-input">
          <option value="">选择分类</option>
          <option v-for="c in FOOD_CATEGORIES" :key="c" :value="c">{{ c }}</option>
        </select>
        <label>单位热量</label><div><el-input-number v-model="form.calories" :min="0" :max="9999" size="small" /><span class="f-hint">kcal</span></div>
        <label>单位</label>
        <select v-model="form.unit" class="f-input">
          <option v-for="u in units" :key="u" :value="u">{{ u }}</option>
        </select>
        <label>蛋白质</label><div><el-input-number v-model="form.protein" :min="0" :max="999" :precision="1" size="small" /><span class="f-hint">g</span></div>
        <label>脂肪</label><div><el-input-number v-model="form.fat" :min="0" :max="999" :precision="1" size="small" /><span class="f-hint">g</span></div>
        <label>碳水</label><div><el-input-number v-model="form.carbs" :min="0" :max="999" :precision="1" size="small" /><span class="f-hint">g</span></div>
      </div>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave" :loading="saving">{{ isEdit ? '保存' : '添加' }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getFoods, addFood, updateFood, deleteFood } from '@/api/food'
import type { Food } from '@/types'
import { FOOD_CATEGORIES } from '@/types'

const keyword = ref(''); const category = ref('')
const foods = ref<Food[]>([]); const loading = ref(false); const saving = ref(false)
const dialogVisible = ref(false); const isEdit = ref(false); const editId = ref<number | null>(null)
const units = ['100g', '个', '碗', '杯', '根', '片', '份']
const form = ref<Partial<Food>>({ name: '', category: '', calories: 100, unit: '100g', defaultQuantity: 100, protein: 0, fat: 0, carbs: 0 })

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

function resetForm() { form.value = { name: '', category: '', calories: 100, unit: '100g', defaultQuantity: 100, protein: 0, fat: 0, carbs: 0 }; isEdit.value = false; editId.value = null }
function openAdd() { resetForm(); dialogVisible.value = true }
function openEdit(row: Food) { isEdit.value = true; editId.value = row.id ?? null; form.value = { ...row }; dialogVisible.value = true }

async function loadFoods() { loading.value = true; try { foods.value = await getFoods(keyword.value || undefined, category.value || undefined) } finally { loading.value = false } }
async function handleSave() {
  saving.value = true
  try {
    if (isEdit.value && editId.value) { await updateFood(editId.value, form.value); ElMessage.success('已更新') }
    else { await addFood(form.value); ElMessage.success('已添加') }
    dialogVisible.value = false; loadFoods()
  } catch { ElMessage.error('操作失败') }
  finally { saving.value = false }
}
async function handleDelete(row: Food) {
  try { await ElMessageBox.confirm(`删除「${row.name}」？`, '确认', { type: 'warning' }); if (row.id) { await deleteFood(row.id); loadFoods() } }
  catch { /* 取消 */ }
}
watch([keyword, category], () => loadFoods())
onMounted(() => loadFoods())
</script>

<style scoped>
.food-page { max-width: 700px; margin: 0 auto; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.page-header h2 { margin: 0; font-size: 20px; }
.btn-add {
  padding: 7px 16px; background: #2d6a4f; color: #fff; border: none;
  border-radius: 8px; font-size: 13px; cursor: pointer;
}
.btn-add:hover { background: #1b4332; }

.search-bar { display: flex; gap: 8px; margin-bottom: 16px; }
.search-input { flex: 1; padding: 9px 14px; border: 1px solid #dde; border-radius: 8px; font-size: 14px; outline: none; }
.search-input:focus { border-color: #52b788; }
.cat-select { width: 110px; padding: 9px 8px; border: 1px solid #dde; border-radius: 8px; font-size: 13px; outline: none; background: #fff; }

.empty { text-align: center; color: #999; padding: 40px 0; }
.food-row {
  display: flex; align-items: center; gap: 12px; padding: 12px 16px; margin-bottom: 4px;
  background: #fff; border-radius: 10px; box-shadow: 0 1px 2px rgba(0,0,0,0.03);
}
.food-emoji { font-size: 28px; }
.food-info { flex: 1; display: flex; flex-direction: column; }
.food-name { font-size: 15px; font-weight: 600; }
.food-meta { font-size: 12px; color: #999; }
.food-nums { display: flex; gap: 10px; font-size: 12px; color: #888; }
.btn-edit, .btn-del {
  width: 28px; height: 28px; border-radius: 50%; border: none; cursor: pointer;
  display: flex; align-items: center; justify-content: center; font-size: 14px;
}
.btn-edit { background: #e8f5e9; color: #2d6a4f; }
.btn-del { background: #fef0ef; color: #d35b4a; }
.btn-edit:hover { background: #c8e6c9; }
.btn-del:hover { background: #ffcdd2; }

.form-grid { display: grid; grid-template-columns: 70px 1fr; gap: 10px; align-items: center; }
.form-grid label { font-size: 13px; color: #666; text-align: right; }
.f-input { padding: 6px 10px; border: 1px solid #dde; border-radius: 6px; font-size: 13px; outline: none; width: 100%; }
.f-hint { margin-left: 6px; font-size: 12px; color: #999; }
</style>
