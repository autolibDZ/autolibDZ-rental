package com.sil1.autolibdz_rental.ui.view.fragment.splashscreen

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.sil1.autolibdz_rental.R
import com.sil1.autolibdz_rental.ui.view.activity.HomeActivity
import com.sil1.autolibdz_rental.ui.view.activity.LoginActivity
import kotlinx.android.synthetic.main.fragment_splash_one.*
import kotlinx.android.synthetic.main.fragment_splash_three.*

class SplashFragmentThree : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash_three, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        imageViewRound2_S3.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_splashFragmentThree_to_splashFragmentTwo)
        }

        imageViewRound1_S3.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_splashFragmentThree_to_splashFragmentTwo)
        }

        textViewPasserThree.setOnClickListener {
            val intent = Intent(activity, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

    }
}