package ca.stclaircollege.fitgrind.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import ca.stclaircollege.fitgrind.api.Food;
import ca.stclaircollege.fitgrind.api.FoodAPI;
import ca.stclaircollege.fitgrind.api.Nutrient;


public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DB_VERSION = 13;


    private static final String DB_NAME = "fitgrind.db";


    private static final String WEIGHTLOG_TABLE_NAME = "weight_log";
    private static final String FOOD_TABLE_NAME = "food";
    private static final String FOODLOG_TABLE_NAME = "food_log";
    private static final String WORKOUTDAY_TABLE_NAME = "workout_day";
    private static final String WORKOUTROUTINE_TABLE_NAME = "workout_routine";
    private static final String EXERCISE_TABLE_NAME = "exercise";
    private static final String CARDIOLOG_TABLE_NAME = "cardio_log";
    private static final String STRENGTHLOG_TABLE_NAME = "strength_log";
    private static final String WORKOUT_TABLE_NAME = "workout";
    private static final String PROGRESS_TABLE_NAME = "progress";


    private static final HashMap<String, String> NUTRIENT_KEYS = new HashMap<String, String>();
    private static final HashMap<String, String> CALORIE_KEY = new HashMap<String, String>();

    static {
        NUTRIENT_KEYS.put("Fiber, total dietary", "fiber");
        NUTRIENT_KEYS.put("Vitamin A, RAE", "vitamin_a");
        NUTRIENT_KEYS.put("Calcium, Ca", "calcium");
        NUTRIENT_KEYS.put("Sugars, total", "sugar");
        NUTRIENT_KEYS.put("Protein", "protein");
        NUTRIENT_KEYS.put("Vitamin C, total ascorbic acid", "vitamin_c");
        NUTRIENT_KEYS.put("Total lipid (fat)", "total_fat");
        NUTRIENT_KEYS.put("Iron, Fe", "iron");
        NUTRIENT_KEYS.put("Carbohydrate, by difference", "carbohydrate");
        NUTRIENT_KEYS.put("Cholesterol", "cholesterol");
        NUTRIENT_KEYS.put("Potassium, K", "potassium");
        NUTRIENT_KEYS.put("Calories", "calories");
        NUTRIENT_KEYS.put("Sodium, Na", "sodium");
        NUTRIENT_KEYS.put("Fatty acids, total trans", "trans_fat");
        NUTRIENT_KEYS.put("Fatty acids, total saturated", "saturated_fat");
        CALORIE_KEY.put("calories", "Calories");
        CALORIE_KEY.put("sugar", "Sugars, total");
        CALORIE_KEY.put("total_fat", "Total lipid (fat)");
        CALORIE_KEY.put("carbohydrate", "Carbohydrate, by difference");
        CALORIE_KEY.put("trans_fat", "Fatty acids, total trans");
        CALORIE_KEY.put("cholesterol", "Cholesterol");
        CALORIE_KEY.put("sodium", "Sodium, Na");
        CALORIE_KEY.put("fiber", "Fiber, total dietary");
        CALORIE_KEY.put("protein", "Protein");
        CALORIE_KEY.put("vitamin_a", "Vitamin A, RAE");
        CALORIE_KEY.put("vitamin_c", "Vitamin C, total ascorbic acid");
        CALORIE_KEY.put("calcium", "Calcium, Ca");
        CALORIE_KEY.put("iron", "Iron, Fe");
        CALORIE_KEY.put("potassium", "Potassium, K");
        CALORIE_KEY.put("saturated_fat", "Fatty acids, total saturated");
    }

    private static final String CREATE_WEIGHTLOG_TABLE =
            "CREATE TABLE weight_log (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "weight FLOAT, " +
                "date DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME')));";

    private static final String CREATE_FOOD_TABLE =
            "CREATE TABLE food (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "name TEXT, " +
                "serving TEXT, " +
                "calories FLOAT, " +
                "sugar FLOAT, " +
                "total_fat FLOAT, " +
                "carbohydrate FLOAT, " +
                "saturated_fat FLOAT, " +
                "trans_fat FLOAT, " +
                "cholesterol FLOAT, " +
                "sodium FLOAT, " +
                "fiber FLOAT, " +
                "protein FLOAT, " +
                "vitamin_a FLOAT, " +
                "vitamin_c FLOAT, " +
                "calcium FLOAT, " +
                "iron FLOAT, " +
                "potassium FLOAT);";

    private static final String CREATE_FOODLOG_TABLE =
            "CREATE TABLE food_log (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "food_id INTEGER REFERENCES food(id) ON DELETE CASCADE, " +
                "date DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME')));";

    private static final String CREATE_PROGRESS_TABLE =
            "CREATE TABLE progress (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "weight_id INTEGER REFERENCES weight_log (id) ON DELETE CASCADE, " +
                "resource TEXT);";

    private static final String CREATE_WORKOUTDAY_TABLE =
            "CREATE TABLE workout_day (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "day VARCHAR(9));";

    private static final String CREATE_WORKOUTROUTINE_TABLE =
            "CREATE TABLE workout_routine (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "name VARCHAR(100) NOT NULL, " +
                "description TEXT NOT NULL);";

    private static final String CREATE_EXERCISE_TABLE =
            "CREATE TABLE exercise (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "name VARCHAR(100) NOT NULL);";

    private static final String CREATE_CARDIOLOG_TABLE =
            "CREATE TABLE cardio_log (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "exercise_id INTEGER REFERENCES exercise(id) ON DELETE CASCADE, " +
                "time FLOAT);";

    private static final String CREATE_STRENGTHLOG_TABLE =
            "CREATE TABLE strength_log (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "exercise_id INTEGER REFERENCES exercise(id) ON DELETE CASCADE, " +
                "sets INTEGER, " +
                "rep INTEGER, " +
                "weight FLOAT);";

    private static final String CREATE_WORKOUT_TABLE =
            "CREATE TABLE workout (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "routine_id INTEGER REFERENCES workout_routine(id) ON DELETE CASCADE, " +
                "exercise_id INTEGER REFERENCES exercise(id) ON DELETE CASCADE, " +
                "day_id INTEGER REFERENCES workout_day(id));";

    public DatabaseHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_WEIGHTLOG_TABLE);
        db.execSQL(CREATE_FOOD_TABLE);
        db.execSQL(CREATE_FOODLOG_TABLE);
        db.execSQL(CREATE_PROGRESS_TABLE);
        db.execSQL(CREATE_WORKOUTDAY_TABLE);
        db.execSQL(CREATE_WORKOUTROUTINE_TABLE);
        db.execSQL(CREATE_EXERCISE_TABLE);
        db.execSQL(CREATE_CARDIOLOG_TABLE);
        db.execSQL(CREATE_STRENGTHLOG_TABLE);
        db.execSQL(CREATE_WORKOUT_TABLE);
        db.execSQL("INSERT INTO workout_day(id, day) VALUES (null, 'Sunday');");
        db.execSQL("INSERT INTO workout_day(id, day) VALUES (null, 'Monday');");
        db.execSQL("INSERT INTO workout_day(id, day) VALUES (null, 'Tuesday');");
        db.execSQL("INSERT INTO workout_day(id, day) VALUES (null, 'Wednesday');");
        db.execSQL("INSERT INTO workout_day(id, day) VALUES (null, 'Thursday');");
        db.execSQL("INSERT INTO workout_day(id, day) VALUES (null, 'Friday');");
        db.execSQL("INSERT INTO workout_day(id, day) VALUES (null, 'Saturday');");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + WEIGHTLOG_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + FOOD_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + FOODLOG_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PROGRESS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + WORKOUTDAY_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + WORKOUTROUTINE_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + EXERCISE_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CARDIOLOG_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + STRENGTHLOG_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + WORKOUT_TABLE_NAME);
        onCreate(db);
    }

    @Override
    public void onConfigure(SQLiteDatabase db){
        db.setForeignKeyConstraintsEnabled(true);
    }

    public long insertProgram(Program program) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", program.getName());
        values.put("description", program.getDescription());
        return db.insert(WORKOUTROUTINE_TABLE_NAME, null, values);
    }

    public long insertWeight(Weight weight) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("weight", weight.getWeight());
        values.put("date", weight.getDate());
        return db.insert(WEIGHTLOG_TABLE_NAME, null, values);
    }

    public boolean insertWorkout(Cardio cardio, long routineId, long dayId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", cardio.getName());
        long id = db.insert(EXERCISE_TABLE_NAME, null, values);
        values.clear();
        values.put("exercise_id", id);
        values.put("time", cardio.getTime());
        long row = db.insert(CARDIOLOG_TABLE_NAME, null, values);
        values.clear();
        values.put("routine_id", routineId);
        values.put("exercise_id", id);
        values.put("day_id", dayId);
        long secondRow = db.insert(WORKOUT_TABLE_NAME, null, values);
        return row > 0 && secondRow > 0;
    }

    public boolean insertWorkout(Strength strength, long routineId, long dayId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", strength.getName());
        long id = db.insert(EXERCISE_TABLE_NAME, null, values);
        values.clear();
        values.put("exercise_id", id);
        values.put("sets", strength.getSet());
        values.put("rep", strength.getReptitions());
        values.put("weight", strength.getWeight());
        long row = db.insert(STRENGTHLOG_TABLE_NAME, null, values);
        values.clear();
        values.put("routine_id", routineId);
        values.put("exercise_id", id);
        values.put("day_id", dayId);
        long secondRow = db.insert(WORKOUT_TABLE_NAME, null, values);
        return row > 0 && secondRow > 0;
    }

    public boolean insertProgress(Progress progress, long weightId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("resource", progress.getResource());
        values.put("weight_id", weightId);
        return db.insert(PROGRESS_TABLE_NAME, null, values) > 0;
    }

    public long insertFood(Food food) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", food.getName());
        values.put("serving", food.getServingSize());
        for (Nutrient nutrient : food.getNutrients()) values.put(NUTRIENT_KEYS.get(nutrient.getNutrient()), nutrient.getValue());
        long id = db.insert(FOOD_TABLE_NAME, null, values);
        return id;
    }

    public long insertCustomFood(Food food) {
        SQLiteDatabase db = getWritableDatabase();
        // create content values
        ContentValues values = new ContentValues();
        values.put("name", food.getName());
        values.put("serving", food.getServingSize());
        for (Nutrient nutrient : food.getNutrients()) values.put(nutrient.getNutrient(), nutrient.getValue());
        long id = db.insert(FOOD_TABLE_NAME, null, values);
        return id;
    }
    public long insertFoodLog(long foodId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("food_id", foodId);
        long id = db.insert(FOODLOG_TABLE_NAME, null, values);
        db.close();
        return id;
    }

    public boolean updateRoutine(Program program) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", program.getName());
        values.put("description", program.getDescription());

        return db.update(WORKOUTROUTINE_TABLE_NAME, values, "id = ?", new String[]{String.valueOf(program.getId())}) > 0;
    }
    public boolean updateWorkout(Cardio cardio) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", cardio.getName());
        int row = db.update(EXERCISE_TABLE_NAME, values, "id = ?", new String[]{String.valueOf(cardio.getId())});
        values.clear();
        values.put("time", cardio.getTime());
        int secondRow = db.update(CARDIOLOG_TABLE_NAME, values, "id = ?", new String[]{String.valueOf(cardio.getCardioId())});
        return row > 0 && secondRow > 0;
    }

    public boolean updateWorkout(Strength strength) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", strength.getName());
        int row = db.update(EXERCISE_TABLE_NAME, values, "id = ?", new String[]{String.valueOf(strength.getId())});
        values.clear();
        values.put("sets", strength.getSet());
        values.put("rep", strength.getReptitions());
        values.put("weight", strength.getWeight());
        int secondRow = db.update(STRENGTHLOG_TABLE_NAME, values, "id = ?", new String[]{String.valueOf(strength.getStrengthId())});
        return row > 0 && secondRow > 0;
    }

    public boolean updateFood(Food food) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        for (Nutrient nutrient : food.getNutrients()) values.put(NUTRIENT_KEYS.get(nutrient.getNutrient()), nutrient.getValue());
        return db.update(FOOD_TABLE_NAME, values, "id = ?", new String[]{String.valueOf(food.getId())}) > 0;
    }

    public boolean updateWeight(Weight weight) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("weight", weight.getWeight());
        values.put("date", weight.getDate());
        return db.update(WEIGHTLOG_TABLE_NAME, values, "id = ?", new String[]{String.valueOf(weight.getId())}) > 0;
    }

    public boolean deleteRoutine(long id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(WORKOUTROUTINE_TABLE_NAME, "id = ?", new String[]{String.valueOf(id)}) > 0 ;
    }

    public boolean deleteCardioWorkout(long id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(CARDIOLOG_TABLE_NAME, "id = ?", new String[]{String.valueOf(id)}) > 0;
    }

    public boolean deleteStrengthWorkout(long id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(STRENGTHLOG_TABLE_NAME, "id = ?", new String[]{String.valueOf(id)}) > 0;
    }
    public boolean deleteProgress(long id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(PROGRESS_TABLE_NAME, "id = ?", new String[]{String.valueOf(id)}) > 0;
    }

    public boolean deleteProgressByWeight(long weightId) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(PROGRESS_TABLE_NAME, "weight_id = ?", new String[]{String.valueOf(weightId)}) > 0;
    }

    public boolean deleteFood(long id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(FOOD_TABLE_NAME, "id = ?", new String[]{String.valueOf(id)}) > 0;
    }

    public boolean deleteWeight(long id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(WEIGHTLOG_TABLE_NAME, "id = ?", new String[]{String.valueOf(id)}) > 0;
    }

    public ArrayList<Weight> selectAllWeightLog() {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<Weight> results = new ArrayList<Weight>();;
        Cursor cursor = db.rawQuery("SELECT * FROM " + WEIGHTLOG_TABLE_NAME, null);
        if (cursor.moveToFirst()) {
            do {
                results.add(new Weight(cursor.getLong(0), cursor.getDouble(1), cursor.getString(2)));
            } while(cursor.moveToNext());
        }
        return results;
    }

    public ArrayList<Progress> selectAllProgress() {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<Progress> results = new ArrayList<Progress>();
        Cursor cursor = db.rawQuery("SELECT id, resource FROM " + PROGRESS_TABLE_NAME, null);
        if (cursor.moveToFirst()) {
            do {
                results.add(new Progress(cursor.getLong(0), cursor.getString(1)));
            } while (cursor.moveToNext());
        }
        return results;
    }

    public ArrayList<Program> selectAllRoutine() {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<Program> results = new ArrayList<Program>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + WORKOUTROUTINE_TABLE_NAME, null);
        if (cursor.moveToFirst()) {
            do {
                results.add(new Program(cursor.getInt(0), cursor.getString(1), cursor.getString(2)));
            } while (cursor.moveToNext());
        }
        db.close();
        return results;
    }

    public ArrayList<WorkoutType> selectAllWorkoutAt(long dayId, long routineId) {

        SQLiteDatabase db = getReadableDatabase();
        ArrayList<WorkoutType> workoutList = new ArrayList<WorkoutType>();
        Cursor cursor = db.rawQuery("SELECT * FROM exercise INNER JOIN cardio_log ON exercise.id = cardio_log.exercise_id INNER JOIN workout ON exercise.id = workout.exercise_id WHERE day_id = ? AND routine_id = ?", new String[]{String.valueOf(dayId), String.valueOf(routineId)});
        if (cursor.moveToFirst()) {
            do {
                workoutList.add(new Cardio(cursor.getInt(0), cursor.getString(1), cursor.getInt(2), Double.parseDouble(cursor.getString(cursor.getColumnIndex("cardio_log.time")))));
            } while (cursor.moveToNext());
        }
        cursor.close();
        Cursor cursor2 = db.rawQuery("SELECT * FROM exercise INNER JOIN strength_log ON exercise.id = strength_log.exercise_id INNER JOIN workout ON exercise.id = workout.exercise_id WHERE day_id = ? AND routine_id = ?", new String[]{String.valueOf(dayId), String.valueOf(routineId)});
        if (cursor2.moveToFirst()) {
            do {
                workoutList.add(new Strength(cursor2.getInt(0), cursor2.getString(1), cursor2.getInt(2), cursor2.getInt(3), cursor2.getInt(4), cursor2.getDouble(cursor2.getColumnIndex("strength_log.weight"))));
            } while (cursor2.moveToNext());
        }
        db.close();
        return workoutList;
    }

    public Food selectFood(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Food food = null;
        Cursor cursor = db.rawQuery("SELECT * FROM food WHERE id = ?", new String[]{String.valueOf(id)});
        if (cursor.moveToFirst()) {
            food = new Food(cursor.getLong(0), cursor.getString(1), cursor.getString(2));
            for (String key : NUTRIENT_KEYS.values()) food.addNutrient(new Nutrient(CALORIE_KEY.get(key), cursor.getDouble(cursor.getColumnIndex(key))));
        }
        return food;
    }

    public Progress selectProgress(long id) {
        Progress progress = null;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM progress WHERE weight_id = ?", new String[]{String.valueOf(id)});
        if (cursor.moveToFirst()) {
            cursor.moveToLast();
            progress = new Progress(cursor.getLong(0), cursor.getString(2));
        }
        return progress;
    }
    public FoodLog selectCalorieLogAt(int day) {
        FoodLog foodLog = null;
        ArrayList<Food> foodList = new ArrayList<Food>();
        SQLiteDatabase db = getReadableDatabase();
        String now = getCurrDateMinus(day);
        String sql = "SELECT food_log.date, food.* FROM food_log INNER JOIN food ON food_log.food_id = food.id " +
                "WHERE food_log.date BETWEEN ? AND ?;";
        Cursor cursor = db.rawQuery(sql, new String[]{now + " 00:00:00", now + "23:59:59"});
        if (cursor.moveToFirst()) {
            do {
                Food food = new Food(cursor.getLong(1), cursor.getString(2), cursor.getString(3), cursor.getString(0));
                for (String key : NUTRIENT_KEYS.values()) food.addNutrient(new Nutrient(key, cursor.getDouble(cursor.getColumnIndex(key))));
                foodList.add(food);
            } while (cursor.moveToNext());
            foodLog = new FoodLog(now, foodList);
        }
        return foodLog;
    }

    public double[] selectNutrientsAt(int day) {
        double[] nutrients = null;
        SQLiteDatabase db = getReadableDatabase();
        String now = getCurrDateMinus(day);
        String sql = "SELECT SUM(food.calories), SUM(food.total_fat), SUM(food.carbohydrate), SUM(food.protein) FROM food_log INNER JOIN food ON food_log.food_id = food.id " +
                "WHERE food_log.date BETWEEN ? AND ?;";
        Cursor cursor = db.rawQuery(sql, new String[]{now + " 00:00:00", now + "23:59:59"});
        if (cursor.moveToFirst()) {
            nutrients = new double[4];
            cursor.moveToLast();
            nutrients[0] = cursor.getDouble(0);
            nutrients[1] = cursor.getDouble(1);
            nutrients[2] = cursor.getDouble(2);
            nutrients[3] = cursor.getDouble(3);
        }
        return nutrients;
    }
    public double selectCaloriesAt(int day) {
        double calories = -1;
        SQLiteDatabase db = getReadableDatabase();
        String now = getCurrDateMinus(day);
        String sql = "SELECT SUM(food.calories) FROM food_log INNER JOIN food ON food_log.food_id = food.id " +
                "WHERE food_log.date BETWEEN ? AND ?;";
        Cursor cursor = db.rawQuery(sql, new String[]{now + " 00:00:00", now + "23:59:59"});
        if (cursor.moveToFirst()) {
            cursor.moveToLast();
            calories = cursor.getDouble(0);
        }
        return calories;
    }


    public ArrayList<Food> selectRecentFoodLog() {
        ArrayList<Food> foodList = null;
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT food_log.date, food.id, food.name, food.serving, food.calories FROM food_log INNER JOIN food ON food_log.food_id = food.id " +
                "ORDER BY food_log.date LIMIT 10;";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            foodList = new ArrayList<Food>();
            do {
                Food food = new Food(cursor.getLong(1), cursor.getString(2), cursor.getString(3), cursor.getString(0));
                food.addNutrient(new Nutrient("calories", cursor.getDouble(cursor.getColumnIndex("calories"))));
                foodList.add(food);

            } while (cursor.moveToNext());
        }
        return foodList;
    }

    private String getCurrDateMinus(int day) {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -day);
        return new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
    }

    public String lastRecordedWeightLog() {
        String result = null;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT date FROM weight_LOG ORDER BY date DESC LIMIT 1;", null);
        if (cursor.moveToLast()) result = cursor.getString(0);
        return result;
    }

    public String lastRecordedCalorieLog() {
        String result = null;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT strftime('%Y-%m-%d', date) FROM food_log ORDER by date DESC LIMIT 1;", null);
        if (cursor.moveToLast()) result = cursor.getString(0);
        return result;
    }
    public boolean isProgressEmpty() {

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT count(*) FROM progress", null);
        if (cursor.moveToFirst()) {
            if (cursor.getInt(0) == 0) {
                return true;
            }
            return false;
        }
        return false;
    }
}
