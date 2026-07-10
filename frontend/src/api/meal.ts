import api from './index'
import type { MealRecord, MealSummary, MealSuggestion } from '@/types'

/** 某天所有餐食记录 */
export function getMeals(date: string): Promise<MealRecord[]> {
  return api.get('/meals', { params: { date } })
}

/** 新增一条餐食记录 */
export function addMeal(record: Partial<MealRecord>): Promise<MealRecord> {
  return api.post('/meals', record)
}

/** 删除一条餐食记录 */
export function deleteMeal(id: number): Promise<void> {
  return api.delete(`/meals/${id}`)
}

/** 某天饮食汇总 */
export function getSummary(date: string): Promise<MealSummary> {
  return api.get('/meals/summary', { params: { date } })
}

/** 某天饮食建议 */
export function getSuggestion(date: string): Promise<MealSuggestion> {
  return api.get('/meals/suggestion', { params: { date } })
}
