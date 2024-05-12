package com.example.extendogames.ui.activites

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.extendogames.R
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory

class ProfileEditActivity : AppCompatActivity() {

    private lateinit var profileImageView: ImageView
    private lateinit var userNameEditText: EditText
    private lateinit var userEmailEditText: EditText
    private lateinit var userPasswordEditText: EditText
    private lateinit var saveChangesButton: Button
    private lateinit var uploadImageButton: Button

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val storageReference = FirebaseStorage.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_edit)

        initializeAppCheck()
        setupViews()
        loadProfile()
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

    private fun loadProfile() {
        auth.currentUser?.let { user ->
            userNameEditText.setText(user.displayName)
            userEmailEditText.setText(user.email)
            Glide.with(this).load(user.photoUrl ?: R.drawable.game_station_icon).into(profileImageView)
        }
    }

    private fun saveProfileChanges() {
        val name = userNameEditText.text.toString()
        val email = userEmailEditText.text.toString()
        val newPassword = userPasswordEditText.text.toString()
        val currentPassword = findViewById<EditText>(R.id.edit_current_password).text.toString()

        val user = auth.currentUser
        if (user != null) {
            val profileUpdates = UserProfileChangeRequest.Builder().setDisplayName(name).build()
            user.updateProfile(profileUpdates).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    updateEmail(user, email, newPassword, currentPassword)
                } else {
                    showToast("Ошибка изменения профиля: ${task.exception?.message}")
                }
            }
        } else {
            showToast("Пользователь не авторизован")
        }
    }

    private fun updateEmail(user: FirebaseUser, email: String, newPassword: String, currentPassword: String) {
        user.updateEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                updateFirestoreUserProfile(user.uid, userNameEditText.text.toString(), userEmailEditText.text.toString())
                if (newPassword.isNotEmpty()) {
                    updatePassword(user, email, newPassword, currentPassword)
                }
            } else {
                showToast("Ошибка изменения почты: ${task.exception?.message}")
            }
        }
    }

    private fun updateFirestoreUserProfile(userId: String, name: String, email: String) {
        val updates = mapOf(
            "name" to name,
            "email" to email
        )
        firestore.collection("Users").document(userId)
            .update(updates)
            .addOnSuccessListener {
                showToast("Профиль изменён успешно")
            }
            .addOnFailureListener { e ->
                showToast("Ошибка изменения профиля: ${e.message}")
            }
    }


    private fun updatePassword(user: FirebaseUser, email: String, newPassword: String, currentPassword: String) {
        val credential = EmailAuthProvider.getCredential(email, currentPassword)
        user.reauthenticate(credential).addOnCompleteListener { reauthTask ->
            if (reauthTask.isSuccessful) {
                user.updatePassword(newPassword).addOnCompleteListener { passTask ->
                    if (passTask.isSuccessful) {
                        showToast("Пароль успешно изменён")
                    } else {
                        showToast("Ошибка измнения пароля: ${passTask.exception?.message}")
                    }
                }
            } else {
                showToast("Повторная аутентификация не удалась: ${reauthTask.exception?.message}")
            }
        }
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            data.data?.let { uri ->
                profileImageView.setImageURI(uri)
                uploadImageToFirebase(uri)
            }
        }
    }

    private fun uploadImageToFirebase(imageUri: Uri) {
        val user = auth.currentUser
        if (user == null) {
            Log.e("UploadImage", "Authentication required")
            showToast("Пользователь должен быть авторизован, для загрузки фотографии")
            return
        }

        Log.d("UploadImage", "Uploading image to Firebase Storage")
        val profileImagesRef = storageReference.child("profile_images/${user.uid}")

        profileImagesRef.putFile(imageUri)
            .addOnSuccessListener {
                Log.d("UploadImage", "Image successfully uploaded")
                profileImagesRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    Log.d("UploadImage", "Download URL retrieved: $downloadUri")
                    updateProfileImage(downloadUri)
                }.addOnFailureListener { e ->
                    Log.e("UploadImage", "Failed to retrieve download URL: ${e.message}", e)
                    showToast("Не удалось получить URL-адрес загрузки.")
                }
            }
            .addOnFailureListener { e ->
                Log.e("UploadImage", "Failed to upload image: ${e.message}", e)
                showToast("Ошибка загрузки: ${e.message}")
            }
    }

    private fun updateProfileImage(downloadUri: Uri) {
        val update = UserProfileChangeRequest.Builder().setPhotoUri(downloadUri).build()
        auth.currentUser?.updateProfile(update)
            ?.addOnSuccessListener {
                Log.d("ProfileUpdate", "Profile photo updated")
                showToast("Изображение профиля изменено")
                Glide.with(this).load(downloadUri).into(profileImageView)
            }
            ?.addOnFailureListener { e ->
                Log.e("ProfileUpdate", "Failed to update profile photo: ${e.message}", e)
                showToast("Ошибка изменения фотографии профиля: ${e.message}")
            }
    }
}
