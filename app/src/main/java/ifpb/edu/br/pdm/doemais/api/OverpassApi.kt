package ifpb.edu.br.pdm.doemais.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface OverpassApi {
    @GET("api/interpreter")
    suspend fun getHospitais(
        @Query("data") query: String
    ): OverpassResponse
}

data class OverpassResponse(val elements: List<Element>)
data class Element(val lat: Double, val lon: Double, val tags: Map<String, String>)

object OverpassRetrofit {
    val api: OverpassApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://overpass-api.de/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OverpassApi::class.java)
    }
}
