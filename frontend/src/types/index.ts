/** 食物 */
export interface Food {
  id?: number
  name: string
  category: string
  calories: number
  unit: string
  defaultQuantity: number
  protein: number
  fat: number
  carbs: number
  createdAt?: string
}

/** 餐食记录 */
export interface MealRecord {
  id?: number
  recordDate: string
  mealType: 'BREAKFAST' | 'LUNCH' | 'DINNER'
  foodId: number
  foodName: string
  quantity: number
  totalCalories: number
  createdAt?: string
}

/** 每日汇总 */
export interface MealSummary {
  date: string
  totalCalories: number
  breakfastCalories: number
  lunchCalories: number
  dinnerCalories: number
  totalProtein: number
  totalFat: number
  totalCarbs: number
  records: MealRecord[]
}

/** 饮食建议 */
export interface MealSuggestion {
  date: string
  targetCalories: number
  tips: string[]
  summary: MealSummary
}

/** 食物分类 */
export const FOOD_CATEGORIES = ['主食', '肉类', '蔬菜', '水果', '饮品', '零食']

/** 餐别映射 */
export const MEAL_TYPE_MAP: Record<string, string> = {
  BREAKFAST: '早餐',
  LUNCH: '午餐',
  DINNER: '晚餐'
}
