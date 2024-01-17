package app.seamansbook.tests.interfaces;

import app.seamansbook.tests.models.CreateUserRequest;
import app.seamansbook.tests.models.CreateUserResponse;
import app.seamansbook.tests.models.Response2;
import app.seamansbook.tests.models.SendReportRequest;
import app.seamansbook.tests.models.UpdateTokenRequest;
import app.seamansbook.tests.models.UpdateTokenResponse;
import app.seamansbook.tests.models.Version;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiInterface {
    @GET("assemblies/{assemblyId}/questions")
    Call<Response2> getQuestions(@Path("assemblyId") String assemblyId);

    @GET("assemblies/{assemblyId}/version")
    Call<Version> getVersion(@Path("assemblyId") String assemblyId);

    @POST("users/create")
    Call<CreateUserResponse> registerUser(@Body CreateUserRequest createUserRequest);

    @POST("users/updateToken")
    Call<UpdateTokenResponse> updateToken(@Body UpdateTokenRequest createUserRequest);

    @POST("reports")
    Call<Void> createReport(@Body SendReportRequest sendReportRequest);

}
