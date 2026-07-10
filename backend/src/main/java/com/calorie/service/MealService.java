package com.calorie.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.calorie.config.LoginFilter;
import com.calorie.entity.Food;
import com.calorie.entity.MealRecord;
import com.calorie.entity.User;
import com.calorie.mapper.MealRecordMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

/**
 * 餐食记录 Service
 */
@Service
public class MealService extends ServiceImpl<MealRecordMapper, MealRecord> {

    private final FoodService foodService;

    public MealService(FoodService foodService) {
        this.foodService = foodService;
    }

    /**
     * 按日期 + 当前用户查询餐食记录
     */
    public List<MealRecord> getByDate(LocalDate date) {
        User user = LoginFilter.currentUser();
        LambdaQueryWrapper<MealRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MealRecord::getRecordDate, date);
        if (user != null) {
            wrapper.eq(MealRecord::getUserId, user.getId());
        }
        wrapper.orderByAsc(MealRecord::getMealType, MealRecord::getCreatedAt);
        return list(wrapper);
    }

    /**
     * 新增一条餐食记录
     */
    public MealRecord addRecord(MealRecord record) {
        Food food = foodService.getById(record.getFoodId());
        if (food == null) {
            throw new RuntimeException("食物不存在");
        }
        record.setFoodName(food.getName());
        // 热量 = 食物单位热量 × 份量
        int totalCalories = new BigDecimal(food.getCalories())
                .multiply(record.getQuantity())
                .setScale(0, RoundingMode.HALF_UP)
                .intValue();
        record.setTotalCalories(totalCalories);
        if (record.getRecordDate() == null) {
            record.setRecordDate(LocalDate.now());
        }
        // 绑定当前用户
        User user = LoginFilter.currentUser();
        if (user != null) {
            record.setUserId(user.getId());
        }
        save(record);
        return record;
    }

    /**
     * 某天饮食汇总
     */
    public Map<String, Object> getSummary(LocalDate date) {
        List<MealRecord> records = getByDate(date);

        int breakfastCal = 0, lunchCal = 0, dinnerCal = 0;
        BigDecimal totalProtein = BigDecimal.ZERO;
        BigDecimal totalFat = BigDecimal.ZERO;
        BigDecimal totalCarbs = BigDecimal.ZERO;

        for (MealRecord r : records) {
            Food food = foodService.getById(r.getFoodId());
            if (food == null) continue;

            switch (r.getMealType()) {
                case "BREAKFAST":
                    breakfastCal += r.getTotalCalories();
                    break;
                case "LUNCH":
                    lunchCal += r.getTotalCalories();
                    break;
                case "DINNER":
                    dinnerCal += r.getTotalCalories();
                    break;
            }

            totalProtein = totalProtein.add(food.getProtein().multiply(r.getQuantity()));
            totalFat = totalFat.add(food.getFat().multiply(r.getQuantity()));
            totalCarbs = totalCarbs.add(food.getCarbs().multiply(r.getQuantity()));
        }

        int totalCal = breakfastCal + lunchCal + dinnerCal;

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("date", date.toString());
        result.put("totalCalories", totalCal);
        result.put("breakfastCalories", breakfastCal);
        result.put("lunchCalories", lunchCal);
        result.put("dinnerCalories", dinnerCal);
        result.put("totalProtein", totalProtein.setScale(1, RoundingMode.HALF_UP));
        result.put("totalFat", totalFat.setScale(1, RoundingMode.HALF_UP));
        result.put("totalCarbs", totalCarbs.setScale(1, RoundingMode.HALF_UP));
        result.put("records", records);
        return result;
    }

    /**
     * 饮食建议（根据性别、年龄个性化）
     */
    public Map<String, Object> getSuggestion(LocalDate date) {
        Map<String, Object> summary = getSummary(date);
        int totalCal = (int) summary.get("totalCalories");
        int breakfastCal = (int) summary.get("breakfastCalories");
        int dinnerCal = (int) summary.get("dinnerCalories");
        BigDecimal protein = (BigDecimal) summary.get("totalProtein");
        BigDecimal fat = (BigDecimal) summary.get("totalFat");
        BigDecimal carbs = (BigDecimal) summary.get("totalCarbs");

        // 根据用户信息确定推荐热量
        User user = LoginFilter.currentUser();
        int targetCal = getTargetCalories(user);

        List<String> tips = new ArrayList<>();

        if (totalCal == 0) {
            tips.add("今天还没有记录任何餐食，记得按时吃饭哦！");
        } else {
            double ratio = (double) totalCal / targetCal;
            if (ratio > 1.2) {
                tips.add(String.format("今日总热量 %d kcal，超出推荐值（%d kcal）%.0f%%，建议减少高热量食物。",
                        totalCal, targetCal, (ratio - 1) * 100));
            } else if (ratio < 0.7) {
                tips.add(String.format("今日总热量 %d kcal，低于推荐值（%d kcal），建议适当增加营养摄入。",
                        totalCal, targetCal));
            } else {
                tips.add(String.format("今日总热量 %d kcal，在推荐范围（%d kcal）内，继续保持！",
                        totalCal, targetCal));
            }
        }

        // 三餐配比
        if (totalCal > 0) {
            double dinnerRatio = (double) dinnerCal / totalCal;
            double breakfastRatio = (double) breakfastCal / totalCal;

            if (dinnerRatio > 0.4) {
                tips.add("晚餐热量占比偏高（" + String.format("%.0f%%", dinnerRatio * 100)
                        + "），建议适当减少晚餐份量。");
            }
            if (breakfastCal > 0 && breakfastRatio < 0.15) {
                tips.add("早餐热量偏低，早餐是一天能量的重要来源，建议吃好早餐。");
            }
        }

        // 营养素评估
        if (totalCal > 0) {
            double proteinCal = protein.doubleValue() * 4;
            double fatCal = fat.doubleValue() * 9;
            double carbsCal = carbs.doubleValue() * 4;
            double totalFromMacro = proteinCal + fatCal + carbsCal;

            if (totalFromMacro > 0) {
                double proteinPct = proteinCal / totalFromMacro * 100;
                double fatPct = fatCal / totalFromMacro * 100;

                if (proteinPct < 10) {
                    tips.add("蛋白质摄入占比偏低，建议增加蛋奶肉类的摄入。");
                }
                if (fatPct > 35) {
                    tips.add("脂肪摄入占比偏高，建议减少油炸和高脂食物。");
                }
            }

            if (fat.doubleValue() > 60) {
                tips.add("建议增加蔬菜水果摄入，补充膳食纤维和维生素。");
            }
        }

        // 个性化建议
        if (user != null && tips.size() > 0) {
            if ("女".equals(user.getGender()) && user.getAge() != null && user.getAge() > 45) {
                tips.add("💡 女性更年期前后钙流失加快，建议多摄入牛奶、豆制品等富含钙的食物。");
            }
            if (user.getAge() != null && user.getAge() > 60) {
                tips.add("💡 老年人新陈代谢减慢，建议少食多餐，以清淡易消化食物为主。");
            }
            if (user.getAge() != null && user.getAge() < 18) {
                tips.add("💡 青少年处于生长发育期，需保证充足的蛋白质和钙质摄入。");
            }
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("date", date.toString());
        result.put("targetCalories", targetCal);
        result.put("tips", tips);
        result.put("summary", summary);
        return result;
    }

    /**
     * 根据性别和年龄计算推荐热量
     */
    private int getTargetCalories(User user) {
        int base = 2000; // 默认
        if (user == null) return base;

        // 性别差异
        if ("男".equals(user.getGender())) {
            base = 2250;
        } else if ("女".equals(user.getGender())) {
            base = 1800;
        }

        // 年龄调整
        if (user.getAge() != null) {
            if (user.getAge() < 14) {
                base = (int) (base * 0.8);
            } else if (user.getAge() > 60) {
                base = (int) (base * 0.85);
            } else if (user.getAge() > 45 && "女".equals(user.getGender())) {
                base = (int) (base * 0.9);
            }
        }

        return base;
    }
}
