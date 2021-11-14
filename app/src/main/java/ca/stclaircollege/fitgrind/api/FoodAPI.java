package ca.stclaircollege.fitgrind.api;

import android.content.Context;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.rx2androidnetworking.Rx2ANRequest;
import com.rx2androidnetworking.Rx2AndroidNetworking;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class FoodAPI {

    private static final String BASE_URL = "https://api.nal.usda.gov/ndb/";
    private static final String NUTRIENT_URL = BASE_URL + "nutrients/?nutrients=208&nutrients=269&nutrients=204&nutrients=205&nutrients=606&nutrients=605" +
            "&nutrients=601&nutrients=307&nutrients=291&nutrients=203&nutrients=320&nutrients=401&nutrients=301&nutrients=303&nutrients=306";
    private static String apiKey = "pwiN99R45M4Zs6Wj0yHYBxokOxhPYQcZ6WxbQMYt";

    private static final String LIST_KEY = "list";
    private static final String ERRORS_KEY = "errors";
    private static final String ERROR_KEY = "error";
    private static final String MESSAGE_KEY = "message";
    private static final String ITEM_KEY = "item";
    private static final String REPORT_KEY = "report";
    private static final String FOODS_KEY = "foods";
    private static final String NUTRIENT_KEY = "nutrients";

    public FoodAPI(Context context) {
        AndroidNetworking.initialize(context);
    }

    public Observable<ArrayList<Item>> searchFood(String food) {
         return Rx2AndroidNetworking.get(BASE_URL + "search/?")
                .addQueryParameter("format", "json")
                .addQueryParameter("q", food)
                .addQueryParameter("api_key", this.apiKey)
                .build()
                .getJSONObjectObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<JSONObject, ArrayList<Item>>() {
                    @Override
                    public ArrayList<Item> apply(JSONObject json) throws Exception {
                        if (json.has(LIST_KEY)) {
                            JSONObject list = json.getJSONObject(LIST_KEY);
                            JSONArray jsonArray = list.getJSONArray(ITEM_KEY);
                            ArrayList<Item> itemList = new ArrayList<Item>();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                itemList.add(new Item(obj.getString(Item.GROUP_KEY), obj.getString(Item.NAME_KEY), Integer.parseInt(obj.getString(Item.NDBNO_KEY))));
                            }
                            return itemList;
                        }
                        JSONArray result = json.getJSONObject(ERRORS_KEY).getJSONArray(ERROR_KEY);
                        String message = (String) ((JSONObject) result.get(0)).get(MESSAGE_KEY);
                        throw new Exception(message);
                    }
                });

    }

    public Observable<Food> getFood(int ndbno) {
        return Rx2AndroidNetworking.get(NUTRIENT_URL)
                .addQueryParameter("format", "json")
                .addQueryParameter("ndbno", Integer.toString(ndbno))
                .addQueryParameter("api_key", this.apiKey)
                .build()
                .getJSONObjectObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<JSONObject, Food>() {
                    @Override
                    public Food apply(JSONObject json) throws Exception {
                        JSONObject report = json.getJSONObject(REPORT_KEY);
                        JSONObject foodObj = (JSONObject) report.getJSONArray(FOODS_KEY).get(0);
                        String serving = foodObj.getString(Food.MEASURE_KEY) + " " + foodObj.getString(Food.WEIGHT_KEY) + "g";
                        Food currFood = new Food(foodObj.getString(Food.NAME_KEY), serving);
                        JSONArray nutrientObj = foodObj.getJSONArray(NUTRIENT_KEY);
                        for (int i=0; i < nutrientObj.length(); i++) {
                            JSONObject obj = nutrientObj.getJSONObject(i);
                            currFood.addNutrient(new Nutrient(obj.getString(Nutrient.NUTRIENT_KEY),
                                    obj.getString(Nutrient.UNIT_KEY), obj.getString(Nutrient.VALUE_KEY)));
                        }
                        return currFood;
                    }
                });
    }

    public void foodResult(int ndbno, JSONObjectRequestListener requestListener) {
        AndroidNetworking.get(NUTRIENT_URL)
                .addQueryParameter("format", "json")
                .addQueryParameter("ndbno", Integer.toString(ndbno))
                .addQueryParameter("api_key", this.apiKey)
                .build()
                .getAsJSONObject(requestListener);
    }
}
