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
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.provider.WebAuthProvider
import com.auth0.android.result.Credentials
import com.example.axiom.model.request.RegisterRequest
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.auth0.android.callback.Callback as Auth0Callback

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
//        // Hide the app bar
//        requireActivity().actionBar?.hide()

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
            val action = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
            this.findNavController().navigate(action)
        }

        return view

    }


    // MAIN CODE


    private fun register() {
        val firstName = view?.findViewById<EditText>(R.id.etFirstName)?.text.toString()
        val lastName = view?.findViewById<EditText>(R.id.etLastName)?.text.toString()
        val email = view?.findViewById<EditText>(R.id.etEmail)?.text.toString()
        val username = view?.findViewById<EditText>(R.id.etUsername)?.text.toString()
        val password = view?.findViewById<EditText>(R.id.etPassword)?.text.toString()
        val cnfPassword = view?.findViewById<EditText>(R.id.etCnfPassword)?.text.toString()

        if (nativeValidateForm()) {
            // Check if email and username already exist in the database
            CoroutineScope(Dispatchers.Main).launch {
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
                    val registerRequest = RegisterRequest(
                        null,
                        firstName,
                        lastName,
                        email,
                        username,
                        password,
                        cnfPassword
                    )

                    appDb.userDao().register(registerRequest)

                    clearEditTexts()

                    Toast.makeText(
                        requireActivity(),
                        "You are now an official Axiom affiliate 🤗.",
                        Toast.LENGTH_LONG
                    ).show()

                    val action = RegisterFragmentDirections.actionRegisterFragmentToHomeFragment()
                    findNavController().navigate(action)
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




    private fun nativeValidateForm(): Boolean {
        val firstName = view?.findViewById<EditText>(R.id.etFirstName)?.text.toString()
        val lastName = view?.findViewById<EditText>(R.id.etLastName)?.text.toString()
        val email = view?.findViewById<EditText>(R.id.etEmail)?.text.toString()
        val username = view?.findViewById<EditText>(R.id.etUsername)?.text.toString()
        val password = view?.findViewById<EditText>(R.id.etPassword)?.text.toString()
        val cnfPassword = view?.findViewById<EditText>(R.id.etCnfPassword)?.text.toString()

        var isValid = true

        when {
            TextUtils.isEmpty(firstName.trim()) -> {
                view?.findViewById<EditText>(R.id.etFirstName)?.apply {
                    error = "Please Enter First Name"
                    isValid = false
                }
            }
            TextUtils.isEmpty(lastName.trim()) -> {
                view?.findViewById<EditText>(R.id.etLastName)?.apply {
                    error = "Please Enter Last Name"
                    isValid = false
                }
            }
            (TextUtils.isEmpty(email.trim())) || !email.matches(Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")) -> {
                view?.findViewById<EditText>(R.id.etEmail)?.apply {
                    error = "Please Enter Email In Correct Format"
                    isValid = false
                }
            }
            TextUtils.isEmpty(username.trim()) -> {
                view?.findViewById<EditText>(R.id.etUsername)?.apply {
                    error = "Please Enter Username"
                    isValid = false
                }
            }
            TextUtils.isEmpty(password.trim()) || password.length < 5 -> {
                view?.findViewById<EditText>(R.id.etPassword)?.apply {
                    error = "Please Enter Password Of At Least 5 Characters"
                    isValid = false
                }
            }
            TextUtils.isEmpty(cnfPassword.trim()) || password != cnfPassword -> {
                view?.findViewById<EditText>(R.id.etCnfPassword)?.apply {
                    error = "Please Ensure Passwords Match"
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

    private fun clearEditTexts() {
        firstName.setText("")
        lastName.setText("")
        email.setText("")
        username.setText("")
        password.setText("")
        cnfPassword.setText("")

    }

    private fun hideKeyboard() {
        val inputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
    }


}
