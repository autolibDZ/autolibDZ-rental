package com.sil1.autolibdz_rental.ui.view.fragment.infos_trajet

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil.setContentView
import androidx.navigation.findNavController
import com.sil1.autolibdz_rental.R
import kotlinx.android.synthetic.main.infos_trajet_fragment.*
import androidx.navigation.fragment.findNavController

class InfosTrajetFragment : Fragment() {

    private lateinit var viewModel: InfosTrajetViewModel
    private var idReservation = 0
    private var prixAPayer = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.infos_trajet_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(InfosTrajetViewModel::class.java)

        prixAPayer = arguments?.getDouble("prixAPayer")!!
        idReservation = arguments?.getInt("idReservation")!!
        prixText.text = prixAPayer.toString() + "DZD"
        borneDepartText.text = arguments?.getString("borneDepart")!!.toString()
        borneArriveeText.text = arguments?.getString("borneArrivee")!!.toString()
        kilometrageText.text = arguments?.get("kilometres")!!.toString() + "Km"
        dureeText.text = arguments?.get("temps")!!.toString() + "min"

        Log.i("idReservationinfotrajet", idReservation.toString())

        var bundle = bundleOf(
            "prixAPayer" to prixAPayer,
            "idReservation" to idReservation
        )
        payWithStripe.setOnClickListener {toStripePayment(bundle) }
        payWithAbonnement.setOnClickListener {toAbonnementPayment(bundle)}
    }

    fun toStripePayment(bundle: Bundle) {
        findNavController().navigate(R.id.action_infosTrajetFragment_to_stripeCardFragment,bundle)
    }

    fun toAbonnementPayment(bundle: Bundle) {
        findNavController().navigate(R.id.action_infosTrajetFragment_to_abonnementPaymentFragment,bundle)
    }

}