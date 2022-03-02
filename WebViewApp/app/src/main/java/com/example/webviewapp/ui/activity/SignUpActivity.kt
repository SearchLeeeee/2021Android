package com.example.webviewapp.ui.activity

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.webviewapp.R
import com.example.webviewapp.common.utils.Cloud.CloudUser
import com.example.webviewapp.contract.SignUpContract
import com.example.webviewapp.data.User
import com.example.webviewapp.databinding.FragmentRegisterBinding
import com.example.webviewapp.presenter.SignUpPresenter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.regex.Matcher
import java.util.regex.Pattern

class SignUpActivity : AppCompatActivity(), SignUpContract.View {
    var viewBinding: FragmentRegisterBinding? = null
    var flag = false
    private var presenter: SignUpContract.Presenter? = null
    private var clickedImageID = 2131230895
    var userEmail: String? = null
        private set
    private var Password: String? = null
    private var mDialog: ProgressDialog? = null
    private var mAuth: FirebaseAuth? = null
    private var mDatabase: DatabaseReference? = null

    @RequiresApi(api = Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = SignUpPresenter()
        viewBinding = FragmentRegisterBinding.inflate(layoutInflater)
        setContentView(viewBinding!!.root)
        mDialog = ProgressDialog(this)
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference.child("Users")
        initButton()
    }

    /**
     * 初始化界面
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    fun initButton() {
        viewBinding!!.signupButton.setOnClickListener { v: View? -> signUp() }
        viewBinding!!.cancelButton.setOnClickListener { v: View? -> back() }
        viewBinding!!.includeAvatarPicker.imageView.setOnClickListener { v: View? -> click(viewBinding!!.includeAvatarPicker.imageView) }
        viewBinding!!.includeAvatarPicker.imageView2.setOnClickListener { v: View? -> click(viewBinding!!.includeAvatarPicker.imageView2) }
        viewBinding!!.includeAvatarPicker.imageView3.setOnClickListener { v: View? -> click(viewBinding!!.includeAvatarPicker.imageView3) }
        viewBinding!!.includeAvatarPicker.imageView4.setOnClickListener { v: View? -> click(viewBinding!!.includeAvatarPicker.imageView4) }
        viewBinding!!.includeAvatarPicker.imageView5.setOnClickListener { v: View? -> click(viewBinding!!.includeAvatarPicker.imageView5) }
        viewBinding!!.includeAvatarPicker.imageView6.setOnClickListener { v: View? -> click(viewBinding!!.includeAvatarPicker.imageView6) }
    }

    /**
     * 注册功能的实现
     * 将注册数据写进数据库中
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    fun signUp() {
//        if (!presenter.ContainsUid(viewBinding.UserNumber.getText())) {
//            presenter.SignUp(viewBinding.UserNumber.getText(), viewBinding.loginPassword.getText());
//            Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_SHORT).show();
//            this.finish();
//        } else {
//            Toast.makeText(getApplicationContext(), "用户已存在，注册失败", Toast.LENGTH_SHORT).show();
//        }
        Log.i(TAG, "图片id: $clickedImageID")
        userEmail = viewBinding!!.UserNumber.text.toString().trim { it <= ' ' }
        Password = viewBinding!!.loginPassword.text.toString().trim { it <= ' ' }
        if (TextUtils.isEmpty(userEmail)) {
            Toast.makeText(applicationContext, "请输入您的Email", Toast.LENGTH_SHORT).show()
            return
        } else if (!checkEmail(userEmail)) {
            Toast.makeText(applicationContext, "请输入正确邮箱格式", Toast.LENGTH_SHORT).show()
            return
        } else if (TextUtils.isEmpty(Password)) {
            Toast.makeText(applicationContext, "请输入您的密码", Toast.LENGTH_SHORT).show()
            return
        } else if (Password!!.length < 6) {
            Toast.makeText(applicationContext, "密码不能少于六位", Toast.LENGTH_SHORT).show()
            return
        }
        mDialog!!.setMessage("正在为您创建用户...")
        mDialog!!.setCanceledOnTouchOutside(false)
        mDialog!!.show()
        mAuth!!.createUserWithEmailAndPassword(userEmail!!, Password!!).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                sendEmailVerification()
                mDialog!!.dismiss()
                OnAuth(task.result!!.user)
                mAuth!!.signOut()
                presenter!!.signUp()
            } else {
                mDialog!!.dismiss()
                Toast.makeText(applicationContext, "创建用户失败，可能原因:\n1.网络连接失败\n2.该账号已存在", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendEmailVerification() {
        val user = FirebaseAuth.getInstance().currentUser
        user?.sendEmailVerification()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(applicationContext, "请在邮箱中验证", Toast.LENGTH_SHORT).show()
                FirebaseAuth.getInstance().signOut()
            }
        }
    }

    private fun OnAuth(user: FirebaseUser?) {
        createAnewUser(user!!.uid)
    }

    private fun createAnewUser(uid: String) {
        val user = BuildNewUser()
        CloudUser.get().uploadUser(uid, user)
    }

    private fun BuildNewUser(): User {
        return User(
                userEmail,
                clickedImageID
        )
    }

    fun back() {
        finish()
    }

    @SuppressLint("NonConstantResourceId")
    fun click(v: View) {
        if (flag) {
            when (clickedImageID) {
                R.id.imageView -> viewBinding!!.includeAvatarPicker.imageView.setImageResource(R.drawable.circle)
                R.id.imageView2 -> viewBinding!!.includeAvatarPicker.imageView2.setImageResource(R.drawable.circle)
                R.id.imageView3 -> viewBinding!!.includeAvatarPicker.imageView3.setImageResource(R.drawable.circle)
                R.id.imageView4 -> viewBinding!!.includeAvatarPicker.imageView4.setImageResource(R.drawable.circle)
                R.id.imageView5 -> viewBinding!!.includeAvatarPicker.imageView5.setImageResource(R.drawable.circle)
                R.id.imageView6 -> viewBinding!!.includeAvatarPicker.imageView6.setImageResource(R.drawable.circle)
                else -> {
                }
            }
        }
        when (v.id) {
            R.id.imageView -> viewBinding!!.includeAvatarPicker.imageView.setImageResource(R.drawable.purple_frame)
            R.id.imageView2 -> viewBinding!!.includeAvatarPicker.imageView2.setImageResource(R.drawable.purple_frame)
            R.id.imageView3 -> viewBinding!!.includeAvatarPicker.imageView3.setImageResource(R.drawable.purple_frame)
            R.id.imageView4 -> viewBinding!!.includeAvatarPicker.imageView4.setImageResource(R.drawable.purple_frame)
            R.id.imageView5 -> viewBinding!!.includeAvatarPicker.imageView5.setImageResource(R.drawable.purple_frame)
            R.id.imageView6 -> viewBinding!!.includeAvatarPicker.imageView6.setImageResource(R.drawable.purple_frame)
            else -> {
            }
        }
        clickedImageID = v.id
        flag = true
    }

    companion object {
        private const val TAG = "SignUpActivity"

        /**
         * 验证邮箱的正则表达式
         *
         * @param email
         * @return
         */
        fun checkEmail(email: String?): Boolean {
            val rule = "[\\w!#$%&'*+/=?^_`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\\w](?:[\\w-]*[\\w])?\\.)+[\\w](?:[\\w-]*[\\w])?"
            val pattern: Pattern
            val matcher: Matcher
            pattern = Pattern.compile(rule)
            matcher = pattern.matcher(email)
            return matcher.matches()
        }
    }
}