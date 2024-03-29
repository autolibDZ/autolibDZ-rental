package com.sil1.autolibdz_rental.ui.view.fragment.reservation_vehicule

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sil1.autolibdz_rental.data.model.*
import com.sil1.autolibdz_rental.data.repositories.AbonnementRepository
import com.sil1.autolibdz_rental.data.repositories.ReservationRepository
import com.sil1.autolibdz_rental.data.repositories.TrajetRepository
import com.sil1.autolibdz_rental.data.repositories.VehiculeRepository
import com.sil1.autolibdz_rental.utils.sharedPrefFile

class InfosReservationViewModel(token : String): ViewModel() {
    private val TAG = "TAG-InfosReservationViewModel"
    var reservation=  MutableLiveData<ReservationResponse>()


    private val _trajet = MutableLiveData<Trajet>()
    val trajet: LiveData<Trajet>
        get() = _trajet


    fun  ajouterReservation(res : ReservationModel,token : String)  {
        ReservationRepository.ajouterReservation(TAG,res,"Basic $token") {

            Log.i(TAG, "view model here")
            reservation.value = it?.value
        }
    }

    fun getTrajet(idReservation: Int) {
        TrajetRepository.getTrajetByReservation(TAG, idReservation) {
            Log.i(TAG, "view model here")
            _trajet.postValue(it)
        }
    }
}