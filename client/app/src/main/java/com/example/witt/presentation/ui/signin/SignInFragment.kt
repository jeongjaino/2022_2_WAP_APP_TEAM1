package com.example.witt.presentation.ui.signin

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.witt.R
import com.example.witt.databinding.FragmentSignInBinding
import com.example.witt.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

import kotlinx.coroutines.launch


@AndroidEntryPoint
class SignInFragment : BaseFragment<FragmentSignInBinding>(R.layout.fragment_sign_in) {

    private val viewModel : SignInViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //dataBinding viewModel
        binding.viewModel = viewModel

        //token 검사
        //viewModel.onEvent(SignInEvent.CheckToken)

        initButton()
        initChannel()

    }
    private fun initButton(){
        binding.signInButton.setOnClickListener {
            viewModel.onEvent(SignInEvent.Submit)
        }

        binding.goToSignUpButton.setOnClickListener{
            val direction = SignInFragmentDirections.actionSignInFragmentToSignUpFragment()
            findNavController().navigate(direction)
        }
        binding.kakaoSignInButton.setOnClickListener{
            viewModel.onEvent(SignInEvent.KakaoSignIn)
        }
        binding.naverSignInButton.setOnClickListener {
            viewModel.onEvent(SignInEvent.NaverSignIn)
        }
    }

    private fun initChannel(){
        lifecycleScope.launch{
            viewModel.signInEvents.collect { event ->
                when(event){
                    is SignInViewModel.SignInUiEvent.Success ->{ //프로필 있는 상태에서 로그인 성공시 홈으로
                        val direction = SignInFragmentDirections.actionSignInFragmentToHomeFragment()
                        findNavController().navigate(direction)
                    }
                    is SignInViewModel.SignInUiEvent.Failure ->{ //실패시 Toast 메세지
                        Toast.makeText(activity, event.message, Toast.LENGTH_SHORT).show()
                    }
                    is SignInViewModel.SignInUiEvent.SuccessSocialLogin ->{ //프로필 없는 상태에서 소셜 로그인 성공
                        val direction = SignInFragmentDirections.actionSignInFragmentToProfileEditFragment(
                            profileImage = event.profileImage,
                            nickName = event.nickName
                        )
                        findNavController().navigate(direction)
                    }
                    is SignInViewModel.SignInUiEvent.HasNoProfile ->{ //프로필 없는 상태에서 이메일 로그인 성공
                        val direction = SignInFragmentDirections.actionSignInFragmentToProfileEditFragment("","")
                        findNavController().navigate(direction)
                    }
                }
            }
        }
    }
}