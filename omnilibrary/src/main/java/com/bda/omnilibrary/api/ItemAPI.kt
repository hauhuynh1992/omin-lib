package com.bda.omnilibrary.api


import com.bda.omnilibrary.model.*
import io.reactivex.Observable
import retrofit2.http.*
import java.util.*

interface ItemAPI {
    @POST("home_v2")
    fun postHome(@Body request: HistoryModel): Observable<HomeModel>

    @Headers("Content-Type: application/json")
    @GET
    fun getUserBox(
        @Url url: String
    ): Observable<UserBoxInfoModel>

    @GET("collection/{uid}")
    fun getSubcollection(@Path("uid") uid: String): Observable<SubCollection>

    @GET("landing-page/{uid}")
    fun getLandingPage(@Path("uid") uid: String): Observable<LandingPageModel>

    @GET("list/district/{uid}")
    fun getDistrict(@Path("uid") uid: String): Observable<ProvinceDistrictModel>

    @GET("list/province")
    fun getProvince(): Observable<ProvinceDistrictModel>

    @POST("list/relate/products")
    fun postRelativeProduct(@Body request: RelativeProductRequest): Observable<CartModel>

    @POST("saveCart")
    fun postSaveCart(@Body request: CartRequest): Observable<CartModel>

    @POST("list/voucher")
    fun postGetListVoucher(@Body request: VoucherRequest): Observable<VoucherResponse>

    @POST("getCart/{id}")
    fun getCartItem(@Path("id") userID: String): Observable<CartModel>

    @POST("search/products")
    fun searchProduct(@Body request: SearchRequestModel): Observable<SearchProductResponse>

    @GET("config")
    fun getConfig(): Observable<ConfigModel>

    @POST("submitOrder")
    fun postResponce(@Body request: PaymentRequest): Observable<MomoPaymentResponce>

    @POST("checkTransactionStatus")
    fun postTransactionStatus(@Body body: HashMap<String, Any>): Observable<MomoTransactionStatusResponce>

    @GET("checkCustomer/{uid}")
    fun postCheckCustomer(@Path("uid") userID: String): Observable<CheckCustomerResponse>

    @POST("checkCustomer_v3")
    fun postCheckCustomer(@Body body: HashMap<String, String>): Observable<CheckCustomerResponse>

    @POST("setDefaultAddress")
    fun postDefaultAddress(@Body addressRequest: DefaultAddressRequest): Observable<DefaultAddressResponse>

    @POST("updateCustomer")
    fun postUpdateCustomer(@Body customer: UpdateCustomerRequest): Observable<CheckCustomerResponse>

    @POST("list/orders_v2")
    fun postListOrder(@Body body: HashMap<String, String>): Observable<ListOrderResponce>

    @POST("list/orders_v3")
    fun postListOrderV3(@Body body: HashMap<String, String>): Observable<ListOrderResponceV3>

    @POST("list/products")
    fun postListProduct(@Body request: ProductMoreRequestModel): Observable<ProductMoreModel>

    @POST("pushOrderStatus")
    fun pushOrderStatus(@Body request: PushOrderStatus): Observable<PushOrderStatus>

    @GET("music/active")
    fun getMusic(): Observable<MusicResponse>

    @POST
    fun textToSpeedFPTAI(@Url url: String, @Body text: String): Observable<FPTAITextToSpeedModel>

    @POST("assistant/search")
    fun getAssistant(@Body request: QueryAssistant): Observable<AssistantAIResponse>

    @POST("deleteCustomerInfo")
    fun deleteCustomerInfo(@Body request: DeleteCustomerInfoRequest): Observable<CheckCustomerResponse>

    @POST("list/products/uids")
    fun getProductFromId(@Body request: ProductByUiRequest): Observable<ProductByUiResponse>

    @GET
    fun getSkyMusic(@Url url: String, @Query("key") key: String): Observable<SkyMusicResponse>

    @POST
    fun logShoppingTracking(@Url url: String, @Body request: LogRequest): Observable<Any>

    @POST
    fun logShoppingListTracking(
        @Url url: String,
        @Body requests: ArrayList<LogRequest>?
    ): Observable<Any>

    @GET("product/best-voucher/{id}/{uid}")
    fun getBestVoucherForProduct(
        @Path("id") id: String, @Path("uid") uid: String?
    ): Observable<BestVoucherForProductResponse>

    @POST("voucher/apply")
    fun getApplyVoucherForCart(
        @Body request: PaymentRequest
    ): Observable<BestVoucherForCartResponse>

    @POST("in-app-popup/get-list")
    fun getListPopup(
        @Body request: PopUpRequest
    ): Observable<PopUpResponse>

    @POST("in-app-popup/active")
    fun postActivePopup(
        @Body request: ActivePopUpRequest
    ): Observable<ActivePopUpResponse>

    @GET("live-stream/{uid}")
    fun getLiveStream(@Path("uid") uid: String): Observable<LiveStreamResponse>

    @GET("collection/child-details/{uid}")
    fun getSubcollection(
        @Path("uid") uid: String,
        @Query("order_by") type: String,
        @Query("length") length: Int,
        @Query("page") page: Int
    ): Observable<ChildDetails>


    ////favourite/////
    @GET("checkFavourite/{customer_id}/{product_id}")
    fun getSingleFavourite(
        @Path("customer_id") customer_id: String,
        @Path("product_id") product_id: String
    ): Observable<FavouriteResponse>

    @POST("addFavourite")
    fun postAddFavourite(@Body request: FavouriteResquest): Observable<FavouriteResponse>

    @POST("deleteFavourite")
    fun postDeleteFavourite(@Body request: FavouriteResquest): Observable<FavouriteResponse>

    @POST("updateCustomerProfile")
    fun postUpdateCustomerProfile(@Body customer: UpdateCustomerProfileRequest): Observable<UpdateCustomerProfileResponse>

    @POST("updateCustomerAlt_info")
    fun postUpdateCustomerAlt(@Body customer: UpdateCustomerAltRequest): Observable<UpdateCustomerProfileResponse>

    @POST("addCustomerAlt_info")
    fun postAddCustomerAlt(@Body customer: UpdateCustomerAltRequest): Observable<UpdateCustomerProfileResponse>

    @GET("profileCustomer/{uid}")
    fun getCustomerProfile(@Path("uid") uid: String): Observable<CustomerProfileResponse>

    @GET("listFavourite/{uid}/{page}")
    fun getListFavourite(
        @Path("uid") uid: String,
        @Path("page") page: Int
    ): Observable<FavouriteProductResponse>

    @GET("list/suggested/products")
    fun getSuggestedProducts(): Observable<HightlightModel>

    @POST("brand-shop/home/{uid}")
    fun getHomeBrandShop(
        @Path("uid") uid: String,
        @Body request: HistoryModel
    ): Observable<BrandShopModel>

    @GET("brand-shop-detail/{uid}")
    fun getBrandShopDetail(@Path("uid") uid: String): Observable<BrandShopResponse>

    @GET("brand-shop-collection/{brandID}/{collectionID}")
    fun getBrandShopCollection(
        @Path("brandID") brandID: String,
        @Path("collectionID") collectionID: String
    ): Observable<SubCollection>

    @GET("brand-shop-child-collection/{childCollectionID}")
    fun getBrandShopChildCollection(
        @Path("childCollectionID") childCollectionID: String,
        @Query("order_by") type: String,
        @Query("length") length: Int,
        @Query("page") page: Int
    ): Observable<ChildDetails>

    @POST("brand-shop-collection/products")
    fun postListProductBrandShop(@Body request: ProductBrandShopMoreRequestModel): Observable<ProductMoreModel>

    @GET("brand-shop-highlight/{brandID}")
    fun getBrandShopHighlight(@Path("brandID") brandID: String): Observable<HighlightProductOfBrandShopResponse>


    @GET("collection/new-arrived-nh1/{uid}")
    fun getNewArrivalProduct(
        @Path("uid") uid: String,
        @Query("page") page: Int,
        @Query("length") length: Int

    ): Observable<ProductMoreModel>

    @POST
    fun getGoogleDeviceCode(
        @Url url: String,
        @Query("client_id") clientId: String,
        @Query("scope") scope: String
    ): Observable<GoogleDeviceCodeResponse>

    @POST
    fun getGoogleDeviceToken(
        @Url url: String,
        @Query("client_id") clientId: String,
        @Query("client_secret") clientSecret: String,
        @Query("device_code") deviceCode: String,
        @Query("grant_type") grantType: String,
    ): Observable<VerifyGoogleDeviceCodeResponse>

    @GET
    fun getGoogleDriveInfo(
        @Url url: String,
        @Header("Authorization") authHeader: String
    ): Observable<GoogleDriveInfoResponse>

    @POST("login/google")
    fun loginByGoogle(
        @Body request: LoginByGoogleRequest
    ): Observable<LoginByGoogleResponse>

    @POST("login/phone")
    fun loginByPhone(
        @Body request: LoginByPhoneRequest
    ): Observable<LoginByGoogleResponse>

    @POST("register/phone/step-1")
    fun registerPhoneName(
        @Body request: RegisterPhoneNameRequest
    ): Observable<RegisterPhoneNameResponse>

    @POST("register/phone/step-2")
    fun registerOtp(
        @Body request: RegisterOtpRequest
    ): Observable<RegisterOtpResponse>

    @POST("register/phone/step-3")
    fun registerPassword(
        @Body request: RegisterPasswordRequest
    ): Observable<RegisterPasswordResponse>

    @GET("assistant/token")
    fun getTextToSpeechToken(): Observable<TextToSpeechTokenRequest>

    @POST
    fun textToSpeech(@Url url: String, @Body request: TTSRequest): Observable<TextToSpeedModel>

    @POST("megamenu/order")
    fun orderMegaMenu(@Body body: HashMap<String, String>): Observable<MegaMenuResponse>

    @GET("list/promotionItem/{uid}")
    fun getPromotion(@Path("uid") uid: String): Observable<PromotionResponse>
}