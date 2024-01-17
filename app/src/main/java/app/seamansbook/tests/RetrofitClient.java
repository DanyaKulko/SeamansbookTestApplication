package app.seamansbook.tests;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit INSTANCE;

    private RetrofitClient() {}

    public static Retrofit getRetrofitInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Retrofit.Builder()
                    .baseUrl(Config.API_URL)
                    .addConverterFactory(GsonConverterFactory.create())

                    .build();
        }
        return INSTANCE;
    }
}