package com.example.webviewapp.ui.fragment.signUp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.example.webviewapp.R
import com.example.webviewapp.common.utils.Cloud.CloudUser
import com.example.webviewapp.common.utils.EventUtils.UserEvent
import com.example.webviewapp.contract.LoginContract
import com.example.webviewapp.databinding.FragmentSecondBinding
import com.example.webviewapp.presenter.LoginPresenter
import com.example.webviewapp.ui.fragment.signUp.SecondFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class SecondFragment : Fragment() {
    var presenter: LoginContract.Presenter = LoginPresenter()
    private var binding: FragmentSecondBinding? = null
    var email: String? = null
    fun onCreateView(
            inflater: LayoutInflater?, container: ViewGroup?,
            savedInstanceState: Bundle?): View {
        binding = FragmentSecondBinding.inflate(inflater!!, container, false)
        EventBus.getDefault().register(this)
        userShow
        return binding!!.root
    }

    fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val logoutBtn = binding!!.logout
        logoutBtn.setOnClickListener { v: View? ->
            presenter.logout()
            FirebaseAuth.getInstance().signOut()
            NavHostFragment.findNavController(this@SecondFragment)
                    .navigate(R.id.action_SecondFragment_to_FirstFragment)
        }
    }

    //获取用户信息
    private val userShow: Unit
        private get() {
            //获取用户信息
            val user: FirebaseUser = FirebaseAuth.getInstance().getCurrentUser()
            val uid: String
            if (user != null) {
                uid = user.getUid()
                CloudUser.get().getUserCloud(uid)
                Log.i(TAG, "id: $uid")
            } else {
                email = ""
            }
        }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEmailEvent(event: UserEvent) {
        binding!!.textviewSecond.text = event.email
        selectImg(event.avatarId)
    }

    fun selectImg(avatarId: Int) {
        when (avatarId) {
            2131230895 -> binding!!.imageView5.setImageResource(R.drawable.avatar_1_raster)
            2131230896 -> binding!!.imageView5.setImageResource(R.drawable.avatar_2_raster)
            2131230897 -> binding!!.imageView5.setImageResource(R.drawable.avatar_3_raster)
            2131230898 -> binding!!.imageView5.setImageResource(R.drawable.avatar_4_raster)
            2131230899 -> binding!!.imageView5.setImageResource(R.drawable.avatar_5_raster)
            2131230900 -> binding!!.imageView5.setImageResource(R.drawable.avatar_6_raster)
            else -> {
            }
        }
    }

    fun onDestroyView() {
        super.onDestroyView()
        binding = null
        EventBus.getDefault().unregister(this)
    }

    companion object {
        private const val TAG = "SecondFragment"
    }
}