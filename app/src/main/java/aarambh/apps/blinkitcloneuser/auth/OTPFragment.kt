package aarambh.apps.blinkitcloneuser.auth

import aarambh.apps.blinkitcloneuser.R
import aarambh.apps.blinkitcloneuser.Utils
import aarambh.apps.blinkitcloneuser.activity.UsersMainActivity
import aarambh.apps.blinkitcloneuser.databinding.FragmentOTPBinding
import aarambh.apps.blinkitcloneuser.models.Users
import aarambh.apps.blinkitcloneuser.viewmodels.AuthViewModel
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch

class OTPFragment : Fragment() {

    private val viewModel: AuthViewModel by viewModels()
    private lateinit var binding: FragmentOTPBinding
    private lateinit var userNumber: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOTPBinding.inflate(layoutInflater)
        onBackButtonClicked()

        getUserNumber()

        customizingEnteringOTP()

        sendOTP()

        onLoginButtonClicked()


        observeErrors()
        return binding.root
    }

    private fun onLoginButtonClicked() {
        binding.btnLogin.setOnClickListener {
            Utils.showDialog(requireContext(), "Signing you...")
            val editTexts = arrayOf(
                binding.etOtp1,
                binding.etOtp2,
                binding.etOtp3,
                binding.etOtp4,
                binding.etOtp5,
                binding.etOtp6
            )
            val otp = editTexts.joinToString("") { it.text.toString() }

            if (otp.length < editTexts.size) {
                Utils.hideDialog()
                Utils.showToast(requireContext(), "Please enter valid OTP")
            } else {
                editTexts.forEach { it.text?.clear();it.clearFocus() }
                verifyOTP(otp)
            }
        }
    }

    private fun verifyOTP(otp: String) {
        viewModel.signInWithPhoneAuthCredential(otp, userNumber)
        lifecycleScope.launch {
            viewModel.isSignedInSuccessfully.collect {
                if (it) {
                    Utils.hideDialog()
                    Utils.showToast(requireContext(), "Logged In Successfully")
                    startActivity(Intent(requireContext(), UsersMainActivity::class.java))
                    requireActivity().finish()
                }
            }
        }
    }

    private fun sendOTP() {
        Utils.showDialog(requireContext(), "Sending OTP...")
        viewModel.apply {
            sendOTP(userNumber, requireActivity())
            lifecycleScope.launch {
                otpSent.collect {
                    if (it) {
                        Utils.hideDialog()
                        Utils.showToast(requireContext(), "OTP sent successfully")
                    }
                }
            }
        }
    }

    private fun onBackButtonClicked() {
        binding.tbOtpFragment.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_OTPFragment_to_signInFragment)
        }
    }

    private fun customizingEnteringOTP() {
        val editTexts = arrayOf(
            binding.etOtp1,
            binding.etOtp2,
            binding.etOtp3,
            binding.etOtp4,
            binding.etOtp5,
            binding.etOtp6
        )
        for (i in editTexts.indices) {
            editTexts[i].addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }

                override fun afterTextChanged(s: Editable?) {
                    if (s?.length == 1) {
                        if (i < editTexts.size - 1) {
                            editTexts[i + 1].requestFocus()
                        }
                    } else if (s?.length == 0) {
                        if (i > 0) {
                            editTexts[i - 1].requestFocus()
                        }
                    }
                }
            })
        }
    }

    private fun getUserNumber() {
        val bundle = arguments
        userNumber = bundle?.getString("number").toString()

        binding.tvUserNumber.text = userNumber
    }

    private fun observeErrors() {
        lifecycleScope.launch {
            viewModel.error.collect { errorMessage ->
                errorMessage?.let {
                    Utils.hideDialog()
                    Utils.showToast(requireContext(), it)
                }
            }
        }
    }
}
