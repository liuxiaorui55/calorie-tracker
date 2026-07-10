import api from './index'
import type { Food } from '@/types'

/** 食物列表（支持 keyword + category 筛选） */
export function getFoods(keyword?: string, category?: string): Promise<Food[]> {
  return api.get('/foods', { params: { keyword, category } })
}

/** 新增食物 */
export function addFood(food: Partial<Food>): Promise<Food> {
  return api.post('/foods', food)
}

/** 更新食物 */
export function updateFood(id: number, food: Partial<Food>): Promise<Food> {
  return api.put(`/foods/${id}`, food)
}

/** 删除食物 */
export function deleteFood(id: number): Promise<void> {
  return api.delete(`/foods/${id}`)
}
