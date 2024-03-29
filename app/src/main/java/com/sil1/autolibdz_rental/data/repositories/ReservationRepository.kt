package com.sil1.autolibdz_rental.data.repositories

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.sil1.autolibdz_rental.data.api.ServiceBuilder
import com.sil1.autolibdz_rental.data.api.ServiceProvider
import com.sil1.autolibdz_rental.data.model.Reservation
import com.sil1.autolibdz_rental.data.model.ReservationModel
import com.sil1.autolibdz_rental.data.model.ReservationResponse
import com.sil1.autolibdz_rental.data.model.VehiculeModel
import com.sil1.autolibdz_rental.data.room.RoomService
import com.sil1.autolibdz_rental.data.room.RoomService.context
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("DEPRECATION")
class ReservationRepository {
    companion object {
        val api: ServiceProvider by lazy {
            ServiceBuilder.buildService(ServiceProvider::class.java)
        }

        fun getReservations(TAG: String,token:String?, id: String?): MutableLiveData<List<Reservation>> {
            if (checkNetwork()) {
                //    return RoomService.database.getReservationDao().selectReservations();
                var call = api.getReservations("Basic $token",id) // consommation de l'api
                var reservationRespond: List<Reservation>?
                var reservationList = mutableListOf<Reservation>()
                var finalList = MutableLiveData<List<Reservation>>()

                call.enqueue(object : Callback<List<Reservation>> {
                    override fun onResponse(
                        call: Call<List<Reservation>>,
                        response: Response<List<Reservation>>
                    ) {
                        Log.i(TAG, "Display reservation List: call enqueue")

                        if (!response.isSuccessful) {
                            Log.i(TAG, "CODE:" + response.code().toString())
                            return
                        }

                        reservationRespond = response.body()  // Getting the list
                        if (reservationRespond != null) {
                            Log.i(TAG, "REPONSES: HERE is ALL THE reservations:")
                            for (m in reservationRespond!!) {
                                var content = ""
                                content += " $m \n"
                                Log.i(TAG, "\n=========\n$content")
                                reservationList.add(m)

                                RoomService.database.getReservationDao().addReservation(m)
                            }
                            finalList.value = reservationList
                            //Log.i("TAG",marqueList.toString())

//                            for (reservation in finalList.value!!) {
//                                Toast.makeText(context,reservation.dateReservation.toString()
//                                ,Toast.LENGTH_SHORT)
//                               // RoomService.database.getReservationDao().addReservation(reservation)
//                            }
                        }
                    }

                    override fun onFailure(call: Call<List<Reservation>>, t: Throwable) {
                        Log.i(TAG, "error CODE:" + t.message)
                    }
                })

                return finalList
            } else {
                var finalList = MutableLiveData<List<Reservation>>()
                finalList.value = RoomService.database.getReservationDao().selectReservations()
                return finalList
            }
        }

            private fun checkNetwork(): Boolean {
                val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
                val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
                return isConnected
            }

        fun  ajouterReservation(
            TAG: String,
            reservation: ReservationModel,
            token:String,
            onResult: (MutableLiveData<ReservationResponse>?) -> Unit
        )  {
            var reserver = MutableLiveData<ReservationResponse>()
            val addReservationRequest = api.ajouterReservation(reservation,token)// consommation de l'api
            addReservationRequest.enqueue(object : Callback<ReservationResponse> {
                override fun onResponse(
                    call: Call<ReservationResponse>,
                    response: Response<ReservationResponse>
                ) {
                    if (!response.isSuccessful()) {
                        val gson = Gson()
                        val message: ReservationResponse = gson.fromJson(
                            response.errorBody()!!.charStream(),
                            ReservationResponse::class.java
                        )
                        onResult(null)
                        /*  Toast.makeText(
                              context,
                              "Le id est: " + message.codePin,
                              Toast.LENGTH_LONG
                          ).show();*/

                    } else {
                        val resp = response.body()
                        /*Toast.makeText(
                            context,
                            resp?.codePin.toString(),
                            Toast.LENGTH_SHORT
                        ).show()*/
                        reserver.value = resp!!
                        onResult(reserver)
                    }
                }

                override fun onFailure(call: Call<ReservationResponse>,  t: Throwable) {
                    /*Toast.makeText(
                        context,
                        "JE SUIS LA" + t.localizedMessage,
                        Toast.LENGTH_SHORT
                    ).show()*/
                    onResult(null)

                }
            })


        }
    }
}