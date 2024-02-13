package com.ridesharedriver.app.connection;

import com.ridesharedriver.app.pojo.AppVersionUpdateResponse;
import com.ridesharedriver.app.pojo.CallingRiderResponse;
import com.ridesharedriver.app.pojo.CheckAppVersionResponse;
import com.ridesharedriver.app.pojo.CheckDeviceTokenResponse;
import com.ridesharedriver.app.pojo.DeleteAccount;
import com.ridesharedriver.app.pojo.LogoutResponse;
import com.ridesharedriver.app.pojo.Register;
import com.ridesharedriver.app.pojo.SignupResponse;
import com.ridesharedriver.app.pojo.add_bank_detail.AddBankDetailResponse;
import com.ridesharedriver.app.pojo.changeStatus.ChangeVehicleStatusResponse;
import com.ridesharedriver.app.pojo.changepassword.ChangePasswordResponse;
import com.ridesharedriver.app.pojo.doc_approval.DocApprovalResponse;
import com.ridesharedriver.app.pojo.driverDestination.DriverDestination;
import com.ridesharedriver.app.pojo.driverEarning.DriverEarningResponse;
import com.ridesharedriver.app.pojo.driverstatus.UpdateDriverStatus;
import com.ridesharedriver.app.pojo.getRideStatus.GetRideStatus;
import com.ridesharedriver.app.pojo.getVehicleDetails.GetAddedVehicleResponse;
import com.ridesharedriver.app.pojo.getVehicleDetails.VehicleDetails;
import com.ridesharedriver.app.pojo.getprofile.GetProfile;
import com.ridesharedriver.app.pojo.google.DistanceMatrixResponse;
import com.ridesharedriver.app.pojo.help_question.QuestionCategoryResponse;
import com.ridesharedriver.app.pojo.help_question.QuestionResponse;
import com.ridesharedriver.app.pojo.help_question.SubmitAnswerResponse;
import com.ridesharedriver.app.pojo.last_ride.LastRideResponse;
import com.ridesharedriver.app.pojo.paymenthistory.PaymentHistoryResponse;
import com.ridesharedriver.app.pojo.profileresponse.ProfileResponse;
import com.ridesharedriver.app.pojo.rides.RideResponse;
import com.ridesharedriver.app.pojo.upload_docs.DocStatusResponse;
import com.ridesharedriver.app.pojo.upload_docs.UploadDocsResponse;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;

/**
 * Created by Saurabh on 05/05/2019.
 */

//End points for Api call
public interface ApiNetworkCall {

    @Multipart
    @POST("register")
    Call<Register> upload(@Part("email") RequestBody email, @Part("mobile") RequestBody mobile, @Part("password") RequestBody password, @Part("name") RequestBody name, @Part("latitude") RequestBody latitude, @Part("longitude") RequestBody longitude, @Part("country") RequestBody country, @Part("state") RequestBody state, @Part("city") RequestBody city, @Part("utype") RequestBody utype, @Part("gcm_token") RequestBody gcm_token, @Part("year") RequestBody year, @Part("vehicle_no") RequestBody vehicle_no, @Part("brand") RequestBody brand, @Part("model") RequestBody model, @Part("color") RequestBody color, @Part("vehicle_type") RequestBody vehicle_type, @Part("rate") RequestBody rate, @Part MultipartBody.Part insurance, @Part MultipartBody.Part carspic, @Part MultipartBody.Part permit, @Part MultipartBody.Part licence);


    @FormUrlEncoded
    @POST("update_driver_status")
    Call<UpdateDriverStatus> updateDriverStatus(@Header("Authorization") String authorization, @FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("logout")
    Call<LogoutResponse> logoutStatus(@Header("Authorization") String authorization, @FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("change_password")
    Call<ChangePasswordResponse> changePassword(@Header("Authorization") String authorization, @FieldMap Map<String, String> params);

    @Multipart
    @POST("update_profile_of_user")
    Call<ProfileResponse> updateProfile(@Header("Authorization") String authorization, @Part MultipartBody.Part file, @Part("name") RequestBody name,@Part("last_name") RequestBody lname,@Part("name_title") RequestBody request_title_name, @Part("mobile") RequestBody mobile, @Part("country_code") RequestBody countryCode, @Part("identification_document_id") RequestBody docId, @Part("identification_issue_date") RequestBody issueDate, @Part("identification_expiry_date") RequestBody expireDate,

                                        @Part MultipartBody.Part identityFile);

    @Multipart
    @POST("upload_document")
    Call<UploadDocsResponse> uploadCarRegistration(@Header("Authorization") String authorization, @Part MultipartBody.Part file, @Part("car_issue_date") RequestBody carIssueDate, @Part("car_expiry_date") RequestBody carExpiryDate

    );

    @Multipart
    @POST("upload_document")
    Call<UploadDocsResponse> uploadInspection(@Header("Authorization") String authorization, @Part MultipartBody.Part file, @Part("inspection_issue_date") RequestBody inspectionIssueDate, @Part("inspection_expiry_date") RequestBody inspectionExpireDate

    );

    @Multipart
    @POST("upload_document")
    Call<UploadDocsResponse> uploadDrivingLicence(@Header("Authorization") String authorization, @Part MultipartBody.Part file, @Part("license_issue_date") RequestBody carIssueDate, @Part("license_expiry_date") RequestBody carExpiryDate);

    @Multipart
    @POST("upload_document")
    Call<UploadDocsResponse> uploadInsurance(@Header("Authorization") String authorization, @Part MultipartBody.Part file, @Part("insurance_issue_date") RequestBody carIssueDate, @Part("insurance_expiry_date") RequestBody carExpiryDate

    );


    @GET("get_profile")
    Call<GetProfile> getProfile(@Header("Authorization") String authorization);

    @GET("get_approval_document_status")
    Call<DocApprovalResponse> getDocumentApprovalStatus(@Header("Authorization") String authorization);


    @GET("get_check_document_expiry")
    Call<DocStatusResponse> getDocsStatus(@Header("Authorization") String authorization);


    @GET("get_question_category")
    Call<QuestionResponse> getHelpQuestions(@Header("Authorization") String authorization);


    @GET("get_question_answer")
    Call<QuestionCategoryResponse> getHelpQuestionCategory(@Header("Authorization") String authorization, @Query(value = "question_category_id") String id);


    @GET("rides")
    Call<RideResponse> getRides(@Header("Authorization") String authorization, @Query(value = "status") String status, @Query(value = "from") String from, @Query(value = "to") String to, @Query(value = "per_page") String perPage, @Query(value = "page_no") String pageNo);

    @GET("payment_history")
    Call<PaymentHistoryResponse> getPaymentHistory(@Header("Authorization") String authorization, @Query(value = "from") String fromDate, @Query(value = "to") String toDate, @Query(value = "per_page") String perPage, @Query(value = "page_no") String pageNo);

    @FormUrlEncoded
    @POST("save_answer")
    Call<SubmitAnswerResponse> submitAnswer(@Header("Authorization") String authorization, @FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("resend")
    Call<ChangePasswordResponse> sendOTP(@FieldMap Map<String, String> params);

 
    @FormUrlEncoded
    @POST("add_account")
    Call<AddBankDetailResponse> addBankAccount(@Header("Authorization") String authorization, @FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("forgot-password")
    Call<ChangePasswordResponse> resetPassword(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("earn")
    Call<DriverEarningResponse> getDriverEarning(@Header("Authorization") String authorization, @FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("update_lat_long")
    Call<ChangePasswordResponse> updateLatLong(@Header("Authorization") String authorization, @FieldMap Map<String, String> params);

    @Multipart
    @POST("audio_capture")
    Call<ChangePasswordResponse> uploadRecording(@Header("Authorization") String authorization, @Part MultipartBody.Part file, @Part("ride_id") RequestBody rideId);

    @Multipart
    @POST("add_vehicle_detail")
    Call<ChangePasswordResponse> addVehicle(@Header("Authorization") String authorization, @Part("year") RequestBody year, @Part("vehicle_no") RequestBody vehicle_no, @Part("brand") RequestBody brand, @Part("model") RequestBody model, @Part("color") RequestBody color, @Part("vehicle_type") RequestBody vehicle_type, @Part("seat_no") RequestBody seat, @Part("premium_facility") RequestBody premium_facility, @Part MultipartBody.Part carspic, @Part MultipartBody.Part car_insurance, @Part("insurance_issue_date") RequestBody insuranceIssueDate, @Part("insurance_expiry_date") RequestBody insuranceExpireDate, @Part MultipartBody.Part car_registration, @Part("car_issue_date") RequestBody carRegistrationIssueDate, @Part("car_expiry_date") RequestBody carRegistrationExpireDate, @Part("inspection_issue_date") RequestBody inspectionIssueDate, @Part("inspection_expiry_date") RequestBody inspectionExpireDate, @Part MultipartBody.Part inspectionFile, @Part("license_issue_date") RequestBody licenceIssueDate, @Part("license_expiry_date") RequestBody licenceExpireDate, @Part MultipartBody.Part licenceFile

    );

    @Multipart
    @POST("update_vehicle_detail")
    Call<ChangePasswordResponse> updateVehicle(@Header("Authorization") String authorization, @Part("year") RequestBody year, @Part("vehicle_no") RequestBody vehicle_no, @Part("brand") RequestBody brand, @Part("model") RequestBody model, @Part("color") RequestBody color, @Part("vehicle_detail_id") RequestBody vehicle_detail_id, @Part("vehicle_type") RequestBody vehicle_type, @Part("seat_no") RequestBody seat, @Part("premium_facility") RequestBody premiumFacilities, @Part MultipartBody.Part insurancepic, @Part("insurance_issue_date") RequestBody insuranceIssueDate, @Part("insurance_expiry_date") RequestBody insuranceExpireDate, @Part MultipartBody.Part carRegFile, @Part("car_issue_date") RequestBody carIssueDate, @Part("car_expiry_date") RequestBody carExpireDate, @Part MultipartBody.Part carspic, @Part("license_issue_date") RequestBody licenceIssueDate, @Part("license_expiry_date") RequestBody licenceExpireDate, @Part MultipartBody.Part licenceFile, @Part("inspection_issue_date") RequestBody inspectionIssueDate, @Part("inspection_expiry_date") RequestBody inspectionExpireDate, @Part MultipartBody.Part inspectionFile);


    @Multipart
    @POST("update_vehicle_detail")
    Call<ChangePasswordResponse> updateDocs(@Header("Authorization") String authorization, @Part("year") RequestBody year, @Part("vehicle_no") RequestBody vehicle_no, @Part("brand") RequestBody brand, @Part("model") RequestBody model, @Part("color") RequestBody color, @Part("vehicle_type") RequestBody vehicle_type, @Part("seat_no") RequestBody seat, @Part("vehicle_detail_id") RequestBody vehicle_detail_id, @Part MultipartBody.Part insuranceFile, @Part("insurance_issue_date") RequestBody insuranceIssueDate, @Part("insurance_expiry_date") RequestBody insuranceExpireDate, @Part MultipartBody.Part carRegFile, @Part("car_issue_date") RequestBody carIssueDate, @Part("car_expiry_date") RequestBody carExpireDate, @Part MultipartBody.Part licenceFile, @Part("license_issue_date") RequestBody licenceIssueDate, @Part("license_expiry_date") RequestBody licenceExpireDate, @Part("inspection_issue_date") RequestBody inspectionIssueDate, @Part("inspection_expiry_date") RequestBody inspectionExpireDate, @Part MultipartBody.Part inspectionFile

    );


    @GET("get_vehicle_detail")
    Call<VehicleDetails> getVehicleDetails(@Header("Authorization") String authorization, @Query(value = "vehicle_detail_id", encoded = true) String vehicle_detail_id);


    @GET("get_added_vehicle_services")
    Call<GetAddedVehicleResponse> getAddedVehicles(@Header("Authorization") String authorization);


    @GET("get_last_ride")
    Call<LastRideResponse> getLastRide(@Header("Authorization") String authorization);


    @FormUrlEncoded
    @POST("add_service")
    Call<ChangeVehicleStatusResponse> selectService(@Header("Authorization") String authorization, @FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("update_vehicle_status")
    Call<ChangeVehicleStatusResponse> changeVehicleStatus(@Header("Authorization") String authorization, @FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("get_ride_status")
    Call<GetRideStatus> getRideStatus(@Header("Authorization") String authorization, @FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("set_destination_location")
    Call<DriverDestination> setDriverDestination(@Header("Authorization") String authorization, @FieldMap Map<String, String> params);

    @GET("delete_account")
    Call<DeleteAccount> deleteAccount(@Header("Authorization") String authorization);

    @FormUrlEncoded
    @POST("updateloginlogout")
    Call<AddBankDetailResponse> updateloginlogout(@Header("Authorization") String authorization, @FieldMap Map<String, String> params);


    @POST("checkdevicetoken")
    Call<CheckDeviceTokenResponse> checkDeviceToken(@Header("Authorization") String authorization);

    @FormUrlEncoded
    @POST("app-version-check")
    Call<CheckAppVersionResponse> checkAppVersionApi(@Header("Authorization") String authorization, @FieldMap Map<String, String> param);

    @FormUrlEncoded
    @POST("app-version-update")
    Call<AppVersionUpdateResponse> updateAppVersionApi(@Header("Authorization") String authorization, @FieldMap Map<String, String> param);

    //    @FormUrlEncoded
//    @POST("version")
//    Call<VersionResponse> getVersion(
//            @Header("Authorization") String authorization,
//            @FieldMap Map<String, String> params);
    @FormUrlEncoded
    @POST("twilio/forward_call")
    Call<CallingRiderResponse> callingRiderApi(@Header("Authorization") String authorization, @FieldMap Map<String, String> param);


    @Multipart
    @POST("register")
    Call<SignupResponse> signin(@PartMap Map<String, RequestBody> param, @Part MultipartBody.Part carspic, @Part MultipartBody.Part userIcon, @Part MultipartBody.Part licence);

    @GET("getBankDetails")
    Call<AddBankDetailResponse> fetchBankAccount(@Header("Authorization") String authorization);

    @FormUrlEncoded
    @POST("rider_feedback")
    Call<ChangePasswordResponse> giveFeedBack(
            @Header("Authorization") String authorization,
            @FieldMap Map<String, String> params);

    @GET("maps/api/distancematrix/json")
    Call<DistanceMatrixResponse> getDistanceFromDistanceMatrix(
            @Query(value = "origins", encoded = true) String origin,
            @Query(value = "destinations", encoded = true) String destination,
            @Query(value = "key", encoded = true) String key
    );
}