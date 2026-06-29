package com.example.portfoliowatcher.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

// MFapi.in - Free public API for Indian mutual fund NAV data
// Docs: https://www.mfapi.in/
// No API key needed, completely free

data class MfSchemeInfo(
    val schemeCode: Int,
    val schemeName: String,
    val schemeType: String,
    val schemeCategory: String
)

data class MfNavData(
    val date: String,  // "29-06-2026"
    val nav: String    // "123.4567"
)

data class MfSchemeResponse(
    val schemeCode: Int,
    val schemeName: String,
    val schemeType: String,
    val schemeCategory: String,
    val schemeNavData: List<MfNavData>
)

data class MfLatestNavResponse(
    val schemeCode: Int,
    val schemeName: String,
    val data: List<MfNavData>  // First element is the latest NAV
)

interface MfApiService {

    // Get list of all mutual fund schemes
    @GET("mf")
    suspend fun getAllSchemes(): List<MfSchemeInfo>

    // Get latest NAV for a specific scheme by scheme code
    // MFapi.in uses scheme code, not ISIN - we'll need to map ISIN to scheme code
    @GET("mf/{schemeCode}/latest")
    suspend fun getLatestNav(@Path("schemeCode") schemeCode: Int): MfLatestNavResponse

    // Get full NAV history for a scheme
    @GET("mf/{schemeCode}")
    suspend fun getSchemeDetails(@Path("schemeCode") schemeCode: Int): MfSchemeResponse

    companion object {
        private const val BASE_URL = "https://api.mfapi.in/"

        fun create(): MfApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MfApiService::class.java)
        }
    }
}
