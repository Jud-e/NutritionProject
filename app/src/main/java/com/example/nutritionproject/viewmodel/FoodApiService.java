package com.example.nutritionproject.viewmodel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface FoodApiService {
    @GET("cgi/search.pl")
    Call<FoodResponse> searchFood(
            @Query("search_terms") String query,
            @Query("json") int json,         // always pass 1
            @Query("page_size") int pageSize  // limit results e.g. 20
    );
}
