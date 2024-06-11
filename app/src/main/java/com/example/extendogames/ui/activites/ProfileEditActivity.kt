package com.example.extendogames.ui.activites

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.extendogames.R
import com.example.extendogames.ui.viewmodels.ProfileEditViewModel
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory

class ProfileEditActivity : AppCompatActivity() {

    private lateinit var profileImageView: ImageView
    private lateinit var userNameEditText: EditText
    private lateinit var userEmailEditText: EditText
    private lateinit var userPasswordEditText: EditText
    private lateinit var saveChangesButton: Button
    private lateinit var uploadImageButton: Button

    private val viewModel: ProfileEditViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_edit)

        val backButton = findViewById<ImageButton>(R.id.back_button)
        backButton.setOnClickListener {
            onBackPressed()
        }

        initializeAppCheck()
        setupViews()
        setupObservers()

        viewModel.loadProfile()
    }

    private fun initializeAppCheck() {
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(SafetyNetAppCheckProviderFactory.getInstance())
    }

    private fun setupViews() {
        profileImageView = findViewById(R.id.profile_image)
        userNameEditText = findViewById(R.id.edit_user_name)
        userEmailEditText = findViewById(R.id.edit_user_email)
        userPasswordEditText = findViewById(R.id.edit_user_password)
        saveChangesButton = findViewById(R.id.button_save_changes)
        uploadImageButton = findViewById(R.id.upload_photo_button)

        uploadImageButton.setOnClickListener { pickImage() }
        saveChangesButton.setOnClickListener { saveProfileChanges() }
    }

    private fun setupObservers() {
        viewModel.userProfile.observe(this, Observer { user ->
            user?.let {
                userEmailEditText.setText(it.email)
                Glide.with(this).load(it.photoUrl ?: R.drawable.game_station_icon).into(profileImageView)
            }
        })

        viewModel.userName.observe(this, Observer { name ->
            userNameEditText.setText(name)
        })

        viewModel.profileImageUrl.observe(this, Observer { uri ->
            uri?.let {
                Glide.with(this).load(it).into(profileImageView)
                // Установить результат OK, чтобы обновить аватарку в ProfileActivity
                setResult(RESULT_OK)
            }
        })

        viewModel.message.observe(this, Observer { message ->
            message?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                if (it == "Профиль изменён успешно" || it == "Изображение профиля изменено") {
                    setResult(RESULT_OK)
                    finish()
                }
            }
        })
    }

    private fun saveProfileChanges() {
        val name = userNameEditText.text.toString()
        val email = userEmailEditText.text.toString()
        val newPassword = userPasswordEditText.text.toString()
        val currentPassword = findViewById<EditText>(R.id.edit_current_password).text.toString()

        if (newPassword.isNotEmpty() && !isPasswordValid(newPassword)) {
            Toast.makeText(this, "Пароль должен содержать не менее 8 символов, включать верхний и нижний регистры, а также специальные символы.", Toast.LENGTH_LONG).show()
            return
        }

        viewModel.saveProfileChanges(name, email, newPassword, currentPassword)
    }

    private fun isPasswordValid(password: String): Boolean {
        val passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{8,}$"
        return password.matches(passwordPattern.toRegex())
    }

    private val pickImageResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            result.data?.data?.let { uri ->
                profileImageView.setImageURI(uri)
                viewModel.uploadImageToFirebase(uri)
            }
        } else {
            Toast.makeText(this, "Не удалось выбрать изображение", Toast.LENGTH_SHORT).show()
        }
    }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageResultLauncher.launch(intent)
    }
}