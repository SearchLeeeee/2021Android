package com.example.webviewapp.ui.fragment.signUp

import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.example.webviewapp.R
import com.example.webviewapp.contract.LoginContract
import com.example.webviewapp.databinding.FragmentFirstBinding
import com.example.webviewapp.presenter.LoginPresenter
import com.example.webviewapp.ui.activity.SignUpActivity
import com.example.webviewapp.ui.fragment.signUp.FirstFragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

@RequiresApi(api = Build.VERSION_CODES.N)
class FirstFragment : Fragment(), LoginContract.View {
    var presenter: LoginContract.Presenter = LoginPresenter()
    private var binding: FragmentFirstBinding? = null
    private var mAuth: FirebaseAuth? = null
    private var mAuthListner: FirebaseAuth.AuthStateListener? = null
    private var mUser: FirebaseUser? = null
    fun onCreateView(
            inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentFirstBinding.inflate(inflater!!, container, false)
        return binding!!.root
    }

    /**
     * 初始化登录窗口
     * 还有登录逻辑
     * 数据可以与数据库交互
     */
    fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //        binding.loginButton.setOnClickListener(view1 -> loginWindow());
        loginWindow()
        binding!!.signupButton.setOnClickListener { v: View? -> startActivity(Intent(getActivity(), SignUpActivity::class.java)) }
    }

    fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: " + presenter.isLogin)
        if (presenter.isLogin!!) NavHostFragment.findNavController(this@FirstFragment)
                .navigate(R.id.action_FirstFragment_to_SecondFragment)
    }

    private fun loginWindow() {
        // 登录窗口的控件绑定
        //AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        //dialog = builder.create();
        //View dialogView = View.inflate(getContext(), R.layout.fragment_first, null);
        val loginButton = binding!!.loginButton
        //Button cancelButton = dialogView.findViewById(R.id.cancelButton);
        val uidtext = binding!!.UserNumber
        val passwordtext = binding!!.loginPassword

        //dialog.setView(dialogView);
        //dialog.show();
        mAuth = FirebaseAuth.getInstance()
        mUser = FirebaseAuth.getInstance().getCurrentUser()
        mAuthListner = object : AuthStateListener() {
            fun onAuthStateChanged(firebaseAuth: FirebaseAuth) {
                if (mUser != null) {
                    //dialog.dismiss();
                    NavHostFragment.findNavController(this@FirstFragment)
                            .navigate(R.id.action_FirstFragment_to_SecondFragment)
                    onDestroyView()
                } else {
                    Log.d("TAG", "AuthStateChanged:Logout")
                }
            }
        }

        // 登录事件绑定
        loginButton.setOnClickListener { v: View? ->
//            if (presenter.Login(uidtext.getText(), passwordtext.getText())) {
//                dialog.dismiss();
//                NavHostFragment.findNavController(FirstFragment.this)
//                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
//                onDestroyView();
//            } else {
//                Toast.makeText(getContext(), "密码错误", Toast.LENGTH_SHORT).show();
//            }
            val email: String
            val password: String
            email = uidtext.text.toString().trim { it <= ' ' }
            password = passwordtext.text.toString().trim { it <= ' ' }
            userSign(email, password)
        }

        //cancelButton.setOnClickListener(v -> dialog.dismiss());
    }

    fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun userSign(email: String, password: String) {
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getContext(), "请输入正确的邮箱", Toast.LENGTH_SHORT).show()
            return
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(getContext(), "请输入正确的密码", Toast.LENGTH_SHORT).show()
            return
        }
        val mDialog: ProgressDialog
        mDialog = ProgressDialog(getContext())
        mDialog.setMessage("正在登录中...")
        mDialog.isIndeterminate = true
        mDialog.show()
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(object : OnCompleteListener<AuthResult?>() {
            fun onComplete(task: Task<AuthResult?>) {
                if (!task.isSuccessful()) {
                    mDialog.dismiss()
                    Toast.makeText(getContext(), "登录失败，请输入正确用户名或密码或检查网络", Toast.LENGTH_SHORT).show()
                } else {
                    presenter.login(email)
                    try {
                        presenter.loadRecord(email)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                    mDialog.dismiss()
                    checkIfEmailVerified()
                }
            }
        })
    }

    /**
     * 检查将要注册的email是否已经注册了
     */
    private fun checkIfEmailVerified() {
        val users: FirebaseUser = FirebaseAuth.getInstance().getCurrentUser()
        val emailVerified: Boolean = users.isEmailVerified()
        if (!emailVerified) {
            Toast.makeText(getContext(), "请先完成邮箱验证", Toast.LENGTH_SHORT).show()
            mAuth.signOut()
            //            finish();
        } else {
            NavHostFragment.findNavController(this@FirstFragment)
                    .navigate(R.id.action_FirstFragment_to_SecondFragment)
            onDestroyView()
        }
    }

    companion object {
        private const val TAG = "FirstFragment"
    }
}