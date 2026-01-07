# Nutrition Tracking - Mobile App API Integration Guide

## Overview
Hướng dẫn implement phần Nutrition Tracking cho mobile app (React Native/Expo).

---

## API Base URL
```
http://localhost:8080/api/nutrition
```

> **Note**: Tất cả endpoints đều yêu cầu JWT token trong header:
> ```
> Authorization: Bearer <token>
> ```

---

## 1. Nutrition Goals

### 1.1 Get Current Goals
```http
GET /goals
```

**Response:**
```json
{
  "status": 200,
  "data": {
    "id": 1,
    "dailyCalories": 2000,
    "dailyProtein": 150,
    "dailyCarbs": 250,
    "dailyFat": 67
  }
}
```

### 1.2 Set/Update Goals
```http
POST /goals
Content-Type: application/json

{
  "dailyCalories": 2000,     // required
  "dailyProtein": 150,       // optional
  "dailyCarbs": 250,         // optional
  "dailyFat": 67             // optional
}
```

### 1.3 Calculate Recommended Goals
```http
GET /goals/calculate
```
> Tự động tính dựa trên profile user (weight, height, age, activityLevel)

---

## 2. Daily Nutrition Summary

### 2.1 Get Daily Nutrition
```http
GET /daily?date=2026-01-06
```

**Response:**
```json
{
  "status": 200,
  "data": {
    "date": "2026-01-06",
    "consumedCalories": 1500,
    "consumedProtein": 100,
    "consumedCarbs": 180,
    "consumedFat": 50,
    "goalCalories": 2000,
    "goalProtein": 150,
    "goalCarbs": 250,
    "goalFat": 67,
    "progressCalories": 75.0,
    "progressProtein": 66.7,
    "progressCarbs": 72.0,
    "progressFat": 74.6
  }
}
```

### 2.2 Get Weekly Summary
```http
GET /weekly?startDate=2026-01-06
```

---

## 3. Mark Meal as Consumed

### 3.1 Toggle Consumed Status
```http
PATCH /meals/{mealSlotId}/consumed
Content-Type: application/json

{
  "consumed": true
}
```

**Response:**
```json
{
  "status": 200,
  "data": {
    "mealSlotId": 123,
    "recipeId": 456,
    "recipeName": "Phở bò",
    "consumed": true,
    "consumedAt": "2026-01-06T12:30:00",
    "message": "Đã đánh dấu bữa ăn đã được thực hiện"
  }
}
```

---

## 4. Food Logs (Log đồ ăn ngoài plan)

### 4.1 Log Food
```http
POST /food-logs
Content-Type: application/json

{
  "recipeId": 123,
  "consumeDate": "2026-01-06T12:30:00",
  "quantity": 1.5
}
```

**Response:**
```json
{
  "status": 200,
  "data": {
    "id": 1,
    "recipeId": 123,
    "recipeName": "Cơm rang dưa bò",
    "recipeImageUrl": "https://...",
    "consumeDate": "2026-01-06T12:30:00",
    "quantity": 1.5,
    "calories": 750,
    "protein": 30,
    "carbs": 90,
    "fat": 25
  }
}
```

### 4.2 Get Food Logs by Date
```http
GET /food-logs?date=2026-01-06
```

### 4.3 Delete Food Log
```http
DELETE /food-logs/{foodLogId}
```

---

## 5. MealSlot Response (updated)

Khi fetch meal slots từ `/api/meal-plans/slot`, response có thêm:

```json
{
  "mealSlotId": 123,
  "recipeId": 456,
  "title": "Phở bò",
  "calories": 500,
  "consumed": false,
  "consumedAt": null
}
```

---

## TypeScript Interfaces

```typescript
interface NutritionGoal {
  id: number;
  dailyCalories: number;
  dailyProtein?: number;
  dailyCarbs?: number;
  dailyFat?: number;
}

interface SetGoalRequest {
  dailyCalories: number;
  dailyProtein?: number;
  dailyCarbs?: number;
  dailyFat?: number;
}

interface DailyNutrition {
  date: string;
  consumedCalories: number;
  consumedProtein: number;
  consumedCarbs: number;
  consumedFat: number;
  goalCalories: number;
  goalProtein?: number;
  goalCarbs?: number;
  goalFat?: number;
  progressCalories: number;
  progressProtein?: number;
  progressCarbs?: number;
  progressFat?: number;
}

interface FoodLog {
  id: number;
  recipeId: number;
  recipeName: string;
  recipeImageUrl?: string;
  consumeDate: string;
  quantity: number;
  calories: number;
  protein: number;
  carbs: number;
  fat: number;
}

interface LogFoodRequest {
  recipeId: number;
  consumeDate: string;  // ISO: "2026-01-06T12:30:00"
  quantity?: number;    // default=1
}

interface MealSlot {
  mealSlotId: number;
  recipeId: number;
  title: string;
  calories: number;
  consumed: boolean;
  consumedAt?: string;
}
```

---

## API Service Example

```typescript
import api from './api';

export const nutritionApi = {
  getGoals: () => api.get('/nutrition/goals'),
  setGoals: (data: SetGoalRequest) => api.post('/nutrition/goals', data),
  calculateGoals: () => api.get('/nutrition/goals/calculate'),
  
  getDailyNutrition: (date: string) => 
    api.get(`/nutrition/daily?date=${date}`),
  
  getWeeklyNutrition: (startDate: string) => 
    api.get(`/nutrition/weekly?startDate=${startDate}`),
  
  markMealConsumed: (mealSlotId: number, consumed: boolean) =>
    api.patch(`/nutrition/meals/${mealSlotId}/consumed`, { consumed }),
  
  logFood: (data: LogFoodRequest) => 
    api.post('/nutrition/food-logs', data),
  
  getFoodLogs: (date: string) => 
    api.get(`/nutrition/food-logs?date=${date}`),
  
  deleteFoodLog: (id: number) => 
    api.delete(`/nutrition/food-logs/${id}`),
};
```

---

## Screens to Implement

| Screen | Purpose |
|--------|---------|
| `NutritionGoalScreen` | Set/view nutrition goals |
| `DailyNutritionScreen` | Daily progress với circular progress bars |
| `WeeklyNutritionScreen` | Weekly summary với charts |
| `FoodLogScreen` | List food logs, add new, delete |
| `MealSlotItem` | Component với checkbox để mark consumed |

---

## UI/UX Notes

- **Daily Progress**: Circular progress bars cho calories, protein, carbs, fat
- **Meal Toggle**: Checkbox hoặc swipe gesture để mark consumed
- **Food Log**: FAB button để quick-add food
- **Date Navigation**: Date picker để navigate giữa các ngày
