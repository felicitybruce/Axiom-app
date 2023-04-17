package com.example.axiom

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.callback.Callback
import com.auth0.android.provider.WebAuthProvider
import com.auth0.android.result.Credentials
import com.google.android.material.snackbar.Snackbar



class LoginFragment : Fragment() {
    private lateinit var appDb: UserRoomDatabase
    private lateinit var account: Auth0
    private lateinit var passwordLog: EditText
    private lateinit var emailLog: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        appDb = UserRoomDatabase.getDatabase(requireContext())

        val icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_warning)
        icon?.setBounds(0, 0, 50, 50)


        // Set up the account object with the Auth0 application details
        account = Auth0(
            getString(R.string.com_auth0_client_id),
            getString(R.string.com_auth0_domain)
        )

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
            val navRegister = activity as FragmentNavigation
            navRegister.navigateFrag(RegisterFragment(), false)
        }

        view.findViewById<Button>(R.id.btnLogLog).setOnClickListener {
            // Hide keyboard on register button click
            when {
                TextUtils.isEmpty(emailLog.text.toString().trim()) -> {
                    view?.findViewById<EditText>(R.id.etLoginUsernameOrEmail)?.apply {
                        error = "Please Enter Email"
                        setCompoundDrawablesWithIntrinsicBounds(null, null, icon, null)
                    }
                }
                TextUtils.isEmpty(passwordLog.text.toString().trim()) -> {
                    view?.findViewById<EditText>(R.id.etLoginPassword)?.apply {
                        error = "Please Enter Password"
                        setCompoundDrawablesWithIntrinsicBounds(null, null, icon, null)
                    }
                }
            }
//            val inputMethodManager =
//                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
//            lifecycleScope.launch {
//                login(emailLog.text.toString(), passwordLog.text.toString())
//
//            }
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
    
//    private suspend fun login(email: String, plainTextPw: String) {
//        val user = withContext(Dispatchers.IO) {
//            appDb.userDao().getUserByEmail(email)
//        }
//
//        if (user == null) {
//            Toast.makeText(
//                requireActivity(),
//                "User not found. Check your details.",
//                Toast.LENGTH_SHORT
//            ).show()
//            Log.d("LOGIN", "User not found for email: $email")
//        } else {
//            // Extract the salt value from the user object
//            val salt = user.salt
//            // Hash the entered password using the retrieved salt value
//            val hashedPw = BCrypt.hashpw(plainTextPw, salt)
//            Log.d("LOGIN", "passwords: from db ${user.password} from newly hashed editext $hashedPw and plaintext $plainTextPw")
//            Log.d("LOGIN", "salt from db ${user.salt} test salt for edittext $salt")
//            Log.d("LOGIN", "lengths pw in db ${user.password.length} pw from editeext ${hashedPw.length}")
//
//            if (hashedPw.substringBefore(".")== user.password.substringBefore(".")) {
//                // Passwords match
//
//                //Sign token
//                // Format xxx.xxxx.xxx
//                // algorithm/token type -> payload/user data -> signature that tells us if the person is verified
//                val token = JWT.create()
//                    .withClaim("username", emailLog.text.toString())
//                    .withClaim("password", passwordLog.text.toString())
//                    //SIGNING
//                    // Once user authenticated via username & pw, grant token that has encrypted signature
//                    // to verify that they are who they say on
//                    //future requests
//                    .sign(Algorithm.HMAC256(JWT_SECRET))
//
//                Log.d("jwt", "3 part token $token")
//                Log.d("jwt", "my secret from gradle $JWT_SECRET")
//
//                Toast.makeText(requireActivity(), "Welcome back! Here is your token $token", Toast.LENGTH_LONG).show()
//                Log.d("LOGIN", "Passwords match for email: $email")
//                var navLogin = activity as FragmentNavigation
//                navLogin.navigateFrag(HomeFragment(), false)
//            } else {
//                // Passwords do not match
//                Log.d("LOGIN", "Passwords do not match for email: $email")
//                showSnackBar("Password does not match the email: $email")
//            }
//        }
//    }

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