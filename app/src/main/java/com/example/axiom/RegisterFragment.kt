package com.example.axiom

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.provider.WebAuthProvider
import com.auth0.android.result.Credentials
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.axiom.client.ApiClient
import com.example.axiom.model.request.RegisterRequest
import com.example.axiom.model.response.RegisterResponse
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Response
import com.auth0.android.callback.Callback as Auth0Callback
import retrofit2.Callback as RetrofitCallback

class RegisterFragment : Fragment() {

    // Late init variables
    private lateinit var appDb: UserRoomDatabase
    private lateinit var account: Auth0
    private lateinit var googleSignInTv: TextView
    private lateinit var registerBtn: Button
    private lateinit var loginBtn: Button
    private lateinit var firstName: EditText
    private lateinit var lastName: EditText
    private lateinit var email: EditText
    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var cnfPassword: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Room: Init database
        appDb = UserRoomDatabase.getDatabase(requireContext())

        // AuthO: Set up the account object with the Auth0 application details
        account = Auth0(
            getString(R.string.com_auth0_client_id),
            getString(R.string.com_auth0_domain)
        )

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_register, container, false)

        // Global: Get buttons
        registerBtn = view.findViewById(R.id.btnRegReg)
        googleSignInTv = view.findViewById(R.id.tvGoogle)
        loginBtn = view.findViewById(R.id.btnLogReg)

        // Global: Get inputs for EditTexts
        firstName = view.findViewById(R.id.etFirstName)!!
        lastName = view.findViewById(R.id.etLastName)!!
        email = view.findViewById(R.id.etEmail)!!
        username = view.findViewById(R.id.etUsername)!!
        password = view.findViewById(R.id.etPassword)!!
        cnfPassword = view.findViewById(R.id.etCnfPassword)!!

        // Rainbow Google TV
        colourfulGoogle()

        // Register button -> Home page or error
        registerBtn.setOnClickListener {
            hideKeyboard()
            register()
        }

        // Sign up with Google -> Google login web view
        googleSignInTv.setOnClickListener {
            loginWithBrowser()
        }

        loginBtn.setOnClickListener {
            navigateScreen(LoginFragment())
        }

        return view

    }


    // MAIN CODE


    private fun navigateScreen(fragment: Fragment) {
        val navLogin = activity as FragmentNavigation
        navLogin.navigateFrag(fragment, false)
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    private val JWT_SECRET = BuildConfig.MY_SECRET

    private fun generateJwt() {
        val token = JWT.create()
            .withClaim("email", email.toString())
            .withClaim("password", password.toString())
            //SIGNING
            // Once user authenticated via username & pw, grant token that has encrypted signature
            // to verify that they are who they say on
            //future requests
            .sign(Algorithm.HMAC256(JWT_SECRET))
    }

    private suspend fun sendUserToServer(
        id: Int,
        firstName: String,
        lastName: String,
        email: String,
        username: String,
        password: String,
        cnfPassword: String
    ) {
        val user =
            RegisterRequest(id, firstName, lastName, email, username, password, cnfPassword)

        // API call
        val apiCall = ApiClient.getApiService().registerUser(user)

        apiCall.enqueue(object : RetrofitCallback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                if (response.isSuccessful) {
                    // create a User object from the registration data
                    val user = RegisterRequest(id, firstName, lastName, email, username, password, cnfPassword)

                    // insert the User object into the Room database
                    lifecycleScope.launch {
                        withContext(Dispatchers.IO) {
                            appDb.userDao().register(user)
                        }
                    }
                    navigateScreen(HomeFragment())
                } else {
                    val errorBody = response.errorBody()?.string()
                    Snackbar.make(
                        requireView(),
                        "Unable to register: $errorBody",
                        Snackbar.LENGTH_SHORT
                    ).show()

                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                view?.let {
                    Snackbar.make(
                        it,
                        "An unexpected error has occurred ${t.localizedMessage}",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }

    private fun register(): Boolean {
        val firstName = view?.findViewById<EditText>(R.id.etFirstName)?.text.toString()
        val lastName = view?.findViewById<EditText>(R.id.etLastName)?.text.toString()
        val email = view?.findViewById<EditText>(R.id.etEmail)?.text.toString()
        val username = view?.findViewById<EditText>(R.id.etUsername)?.text.toString()
        val password = view?.findViewById<EditText>(R.id.etPassword)?.text.toString()
        val cnfPassword = view?.findViewById<EditText>(R.id.etCnfPassword)?.text.toString()

        return try {
            CoroutineScope(Dispatchers.Default).launch {
                withContext(Dispatchers.Main) {
                    if (nativeValidateForm()) {
                        // Check if email and username already exist in the database
                        val existingUserWithEmail = appDb.userDao().getUserByEmail(email)
                        val existingUserWithUsername = appDb.userDao().getUserByUsername(username)

                        if (existingUserWithEmail != null) {
                            // Email already exists, show error message
                            Snackbar.make(
                                requireView(),
                                "Email is already taken",
                                Snackbar.LENGTH_SHORT
                            ).show()

                        } else if (existingUserWithUsername != null) {
                            // Username already exists, show error message
                            Snackbar.make(
                                requireView(),
                                "Username is already taken",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        } else {

                            // Launch a coroutine and call sendUserToServer from within that coroutine
                            CoroutineScope(Dispatchers.Main).launch {
                                try {
                                    val registerRequest = RegisterRequest(
                                        null,
                                        firstName,
                                        lastName,
                                        email,
                                        username,
                                        password,
                                        cnfPassword
                                    )
                                    Log.d("test", "register: about to register $registerRequest")
                                    appDb.userDao().register(registerRequest)

                                    navigateScreen(HomeFragment())
                                    Toast.makeText(
                                        requireActivity(),
                                        "You are now an official Axiom affiliate 🤗.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } catch (e: Exception) {
                                    Snackbar.make(
                                        requireView(),
                                        "Unable to register",
                                        Snackbar.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    } else {
                        Snackbar.make(
                            requireView(),
                            "Please fill in all fields correctly.",
                            Snackbar.LENGTH_SHORT
                        ).show()

                    }
                }
            }
            true
        } catch (e: Exception) {
            Toast.makeText(requireActivity(), "Error:${e.message}", Toast.LENGTH_SHORT).show()
            false
        }
    }

    private fun nativeValidateForm(): Boolean {
        val firstName = view?.findViewById<EditText>(R.id.etFirstName)?.text.toString()
        val lastName = view?.findViewById<EditText>(R.id.etLastName)?.text.toString()
        val email = view?.findViewById<EditText>(R.id.etEmail)?.text.toString()
        val username = view?.findViewById<EditText>(R.id.etUsername)?.text.toString()
        val password = view?.findViewById<EditText>(R.id.etPassword)?.text.toString()
        val cnfPassword = view?.findViewById<EditText>(R.id.etCnfPassword)?.text.toString()

        val icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_warning)
        icon?.setBounds(0, 0, 50, 50)

        var isValid = true

        when {
            TextUtils.isEmpty(firstName.trim()) -> {
                view?.findViewById<EditText>(R.id.etFirstName)?.apply {
                    error = "Please Enter First Name"
                    setCompoundDrawablesWithIntrinsicBounds(null, null, icon, null)
                    isValid = false
                }
            }
            TextUtils.isEmpty(lastName.trim()) -> {
                view?.findViewById<EditText>(R.id.etLastName)?.apply {
                    error = "Please Enter Last Name"
                    setCompoundDrawablesWithIntrinsicBounds(null, null, icon, null)
                    isValid = false
                }
            }
            (TextUtils.isEmpty(email.trim())) || !email.matches(Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")) -> {
                view?.findViewById<EditText>(R.id.etEmail)?.apply {
                    error = "Please Enter Email In Correct Format"
                    setCompoundDrawablesWithIntrinsicBounds(null, null, icon, null)
                    isValid = false
                }
            }
            TextUtils.isEmpty(username.trim()) -> {
                view?.findViewById<EditText>(R.id.etUsername)?.apply {
                    error = "Please Enter Username"
                    setCompoundDrawablesWithIntrinsicBounds(null, null, icon, null)
                    isValid = false
                }
            }
            TextUtils.isEmpty(password.trim()) || password.length < 5 -> {
                view?.findViewById<EditText>(R.id.etPassword)?.apply {
                    error = "Please Enter Password Of At Least 5 Characters"
                    setCompoundDrawablesWithIntrinsicBounds(null, null, icon, null)
                    isValid = false
                }
            }
            TextUtils.isEmpty(cnfPassword.trim()) || password != cnfPassword -> {
                view?.findViewById<EditText>(R.id.etCnfPassword)?.apply {
                    error = "Please Ensure Passwords Match"
                    setCompoundDrawablesWithIntrinsicBounds(null, null, icon, null)
                    isValid = false
                }
            }
        }
        return isValid
    }

    private fun loginWithBrowser() {
        // Setup the WebAuthProvider, using the custom scheme and scope.
        WebAuthProvider.login(account)
            .withScheme(getString(R.string.com_auth0_scheme))
            .withScope("openid profile email read:current_user update:current_user_metadata")
            .withAudience("https://${getString(R.string.com_auth0_domain)}/api/v2/")

            // Launch the authentication passing the callback where the results will be received
            .start(requireContext(), object : Auth0Callback<Credentials, AuthenticationException> {
                override fun onFailure(error: AuthenticationException) {
                    Snackbar.make(
                        requireView(),
                        "Failure: ${error.getCode()}",
                        Snackbar.LENGTH_SHORT
                    ).setAction("Retry") {
                        // Perform action when "Retry" button is clicked
                        // For example, resend a network request
                    }
                        .setActionTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                androidx.appcompat.R.color.material_blue_grey_800
                            )
                        )
                        .setBackgroundTint(
                            ContextCompat.getColor(
                                requireContext(),
                                androidx.appcompat.R.color.material_deep_teal_200
                            )
                        )
                        .show()
                }

                override fun onSuccess(result: Credentials) {
                    val accessToken = result.accessToken
                    Snackbar.make(
                        requireView(),
                        "Success: ${result.accessToken}",
                        Snackbar.LENGTH_SHORT
                    ).show()

                    var navLogin = activity as FragmentNavigation
                    navLogin.navigateFrag(HomeFragment(), false)
                }
            })
    }

    private fun colourfulGoogle() {
        val googleSignInText = getString(R.string.colourful_google)
        val spannableString = SpannableString(googleSignInText)
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
        googleSignInTv.text = spannableString
    }
}
