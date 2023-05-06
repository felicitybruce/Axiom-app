package com.example.axiom

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.callback.Callback
import com.auth0.android.provider.WebAuthProvider
import com.auth0.android.result.Credentials
import com.example.axiom.client.ApiClient
import com.example.axiom.model.request.LoginRequest
import com.example.axiom.model.response.LoginResponse
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Response


class LoginFragment : Fragment() {
    private lateinit var appDb: UserRoomDatabase
    private lateinit var account: Auth0
    private lateinit var passwordLog: EditText
    private lateinit var emailLog: EditText
    private lateinit var email: EditText
    private lateinit var password: EditText


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        appDb = UserRoomDatabase.getDatabase(requireContext())


        // Set up the account object with the Auth0 application details
        account = Auth0(
            getString(R.string.com_auth0_client_id),
            getString(R.string.com_auth0_domain)
        )

        email = view.findViewById(R.id.etLoginUsernameOrEmail)!!
        email = view.findViewById(R.id.etLoginPassword)!!


        // Colourful Google TV
        val googleText = "Sign in with Google"
        val spannableString = SpannableString(googleText)
        spannableString.setSpan(
            ForegroundColorSpan(Color.parseColor("#006DFF")),
            13,
            14,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            ForegroundColorSpan(Color.RED),
            14,
            15,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            ForegroundColorSpan(Color.parseColor("#F2DC23")),
            15,
            16,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            ForegroundColorSpan(Color.parseColor("#006DFF")),
            16,
            17,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            ForegroundColorSpan(Color.GREEN),
            17,
            18,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            ForegroundColorSpan(Color.RED),
            18,
            19,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        val textView = view.findViewById<TextView>(R.id.tvGoogle)
        textView.text = spannableString
        textView.setOnClickListener{
            loginWithBrowser()
        }

        // Find views and assign them to the properties
        passwordLog = view.findViewById(R.id.etLoginPassword)
        emailLog = view.findViewById(R.id.etLoginUsernameOrEmail)

        //REGISTER button navigates to register page
        view.findViewById<Button>(R.id.btnLogReg).setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
            findNavController().navigate(action)
        }

        view.findViewById<Button>(R.id.btnLogLog).setOnClickListener {
            // Hide keyboard on register button click
            when {
                TextUtils.isEmpty(emailLog.text.toString().trim()) -> {
                    view?.findViewById<EditText>(R.id.etLoginUsernameOrEmail)?.apply {
                        error = "Please Enter Email"
                    }
                }
                TextUtils.isEmpty(passwordLog.text.toString().trim()) -> {
                    view?.findViewById<EditText>(R.id.etLoginPassword)?.apply {
                        error = "Please Enter Password"
                    }
                }
            }
            passwordLog.text.toString()
            emailLog.text.toString()

            val inputMethodManager =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
            lifecycleScope.launch {
                login(emailLog.text.toString(), passwordLog.text.toString())

            }
        }

        return view
    }

    // MAIN CODE
    private fun loginWithBrowser() {
        // Setup the WebAuthProvider, using the custom scheme and scope.
        WebAuthProvider.login(account)
            .withScheme(getString(R.string.com_auth0_scheme))
            .withScope("openid profile email read:current_user update:current_user_metadata")
            .withAudience("https://${getString(R.string.com_auth0_domain)}/api/v2/")

            // Launch the authentication passing the callback where the results will be received
            .start(requireContext(), object : Callback<Credentials, AuthenticationException> {
                override fun onFailure(error: AuthenticationException) {
                    showSnackBar("Failure: ${error.getCode()}")
                }

                override fun onSuccess(result: Credentials) {
                    val accessToken = result.accessToken
                    showSnackBar("Success: ${result.accessToken}")
                    var navLogin = activity as FragmentNavigation
                    navLogin.navigateFrag(HomeFragment(), false)

                }
            })
    }

    private fun clearFormIcons() {
        view?.findViewById<EditText>(R.id.etLoginUsernameOrEmail)?.text?.clear()
        view?.findViewById<EditText>(R.id.etLoginPassword)?.text?.clear()

        //view?.findViewById<EditText>(R.id.etLoginUsernameOrEmail)?.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        //view?.findViewById<EditText>(R.id.etLoginPassword)?.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
    }
    private val JWT_SECRET = BuildConfig.MY_SECRET

//    private suspend fun tokenisation() {
//        val JWT_SECRET = BuildConfig.MY_SECRET
//
//
//        // Format xxx.xxxx.xxx
//        //      algorithm/token type -> payload/user data -> signature that tells us if the person is verified
//        val token = JWT.create()
//            .withClaim("username", emailLog.text.toString())
//            .withClaim("password", passwordLog.text.toString())
//            //SIGNING
//            // Once user authenticated via username & pw, grant token that has encrypted signature
//            // to verify that they are who they say on
//            //future requests
//            .sign(Algorithm.HMAC256(JWT_SECRET))
//
//        Log.d("jwt", "3 part token $token")
//        showSnackBar("Thanks for signing in! Here is your token: $token")
//
//
//        val verifier = JWT.require(Algorithm.HMAC256(JWT_SECRET))
//            .withClaim("username", emailLog.text.toString())
//            .build()
//
//        // returns a DecodedJWT object
//        // To know if we can trust the log in request
//        // -decrypt signature
//        // -check expiration
//        // -decode payload
//        val decodedJwt = JWT.decode(token)
//        //can then access the individual claims or fields from
//        // the decoded JWT using the getter methods provided by the DecodedJWT object
//        val password = decodedJwt.getClaim("password").asString()
//        val username = decodedJwt.getClaim("username").asString()
//
//        Log.d("jwt", "decoded jwt $decodedJwt, password $password, username $username")
//
//
//        Log.d("jwt", "my secret from gradle $JWT_SECRET")
//
//        val email = emailLog.text.toString()
//        val user = withContext(Dispatchers.IO) {
//            appDb.userDao().getUserByEmail(email)
//        }
//
//    }

    private suspend fun login(email: String, plainTextPw: String) {

        if (nativeValidateForm()) {
            val user = withContext(Dispatchers.IO) {
                appDb.userDao().getUserByEmail(email)
            }

            val loginRequest = LoginRequest(email, plainTextPw)
            val apiCall = ApiClient.getApiService().loginUser(loginRequest)
            apiCall.enqueue(object : retrofit2.Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    if (response.isSuccessful) {
                        var navLogin = activity as FragmentNavigation
                        navLogin.navigateFrag(HomeFragment(), false)
                    } else {
                        showSnackBar(
                            "Unable to login. Please check your credentials and \n" +
                                    "try again."
                        )
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    showSnackBar("An error occured ${t.localizedMessage}")
                }
            })
        } else {
            Snackbar.make(
                requireView(),
                "Please fill in all fields correctly.",
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    private fun nativeValidateForm(): Boolean {
        val email = view?.findViewById<EditText>(R.id.etLoginUsernameOrEmail)?.text.toString()
        val password = view?.findViewById<EditText>(R.id.etLoginPassword)?.text.toString()

        var isValid = true

        when {
            TextUtils.isEmpty(email.trim()) -> {
                view?.findViewById<EditText>(R.id.etLoginUsernameOrEmail)?.apply {
                    error = "Please Enter Email"
                    isValid = false
                }
            }
            TextUtils.isEmpty(password.trim()) -> {
                view?.findViewById<EditText>(R.id.etLoginPassword)?.apply {
                    error = "Please Ensure Enter Password"
                    isValid = false
                }
            }
        }
        return isValid
    }

    private fun showSnackBar(text: String) {
        Snackbar.make(
            requireView(),
            text,
            Snackbar.LENGTH_LONG
        ).show()
    }
}
// TODO: 1)LOGIN ALWAYS RETURNS TRUE EVEN IF PASSWORD IS WRONG. OK FOR IF EMAIL IS WRONG
// TODO: 2) JWT: ACTUAL AUTH PROCESS - DON'T HAVE TO KEEP SIGINING IN - CREATE BACKEND IN JAVASCRIPT
// TODO: 3)