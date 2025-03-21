package ifpb.edu.br.pdm.doemais.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface NominatimApi {
    @GET("search")
    suspend fun getCoordenadas(
        @Query("q") cidade: String,
        @Query("format") format: String = "json"
    ): List<CoordenadaResponse>
}

data class CoordenadaResponse(val lat: String, val lon: String)

object RetrofitInstance {
    val api: NominatimApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://nominatim.openstreetmap.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NominatimApi::class.java)
    }
}
