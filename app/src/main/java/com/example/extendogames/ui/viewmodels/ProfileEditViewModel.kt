package com.example.extendogames.ui.viewmodels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class ProfileEditViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val storageReference = FirebaseStorage.getInstance().reference

    private val _userProfile = MutableLiveData<FirebaseUser?>()
    val userProfile: LiveData<FirebaseUser?> get() = _userProfile

    private val _profileImageUrl = MutableLiveData<Uri?>()
    val profileImageUrl: LiveData<Uri?> get() = _profileImageUrl

    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String> get() = _userName

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> get() = _message

    fun loadProfile() {
        val user = auth.currentUser
        if (user != null) {
            _userProfile.value = user
            _userName.value = user.displayName

            firestore.collection("Users").document(user.uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        _userName.value = document.getString("name")
                    }
                }
                .addOnFailureListener { e ->
                    _message.value = "Ошибка загрузки данных профиля: ${e.message}"
                }
        }
    }

    fun saveProfileChanges(name: String, email: String, newPassword: String, currentPassword: String) {
        val user = auth.currentUser
        if (user != null) {
            val profileUpdates = UserProfileChangeRequest.Builder().setDisplayName(name).build()
            user.updateProfile(profileUpdates).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    updateEmail(user, email, newPassword, currentPassword)
                } else {
                    _message.value = "Ошибка изменения профиля: ${task.exception?.message}"
                }
            }
        } else {
            _message.value = "Пользователь не авторизован"
        }
    }

    private fun updateEmail(user: FirebaseUser, email: String, newPassword: String, currentPassword: String) {
        user.updateEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                updateFirestoreUserProfile(user.uid, user.displayName ?: "", email)
                if (newPassword.isNotEmpty()) {
                    updatePassword(user, email, newPassword, currentPassword)
                }
            } else {
                _message.value = "Ошибка изменения почты: ${task.exception?.message}"
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
                _message.value = "Профиль изменён успешно"
            }
            .addOnFailureListener { e ->
                _message.value = "Ошибка изменения профиля: ${e.message}"
            }
    }

    private fun updatePassword(user: FirebaseUser, email: String, newPassword: String, currentPassword: String) {
        if (!isPasswordValid(newPassword)) {
            _message.value = "Пароль должен содержать не менее 8 символов, включать верхний и нижний регистры, а также специальные символы."
            return
        }

        val credential = EmailAuthProvider.getCredential(email, currentPassword)
        user.reauthenticate(credential).addOnCompleteListener { reauthTask ->
            if (reauthTask.isSuccessful) {
                user.updatePassword(newPassword).addOnCompleteListener { passTask ->
                    if (passTask.isSuccessful) {
                        _message.value = "Пароль успешно изменён"
                    } else {
                        _message.value = "Ошибка изменения пароля: ${passTask.exception?.message}"
                    }
                }
            } else {
                _message.value = "Повторная аутентификация не удалась: ${reauthTask.exception?.message}"
            }
        }
    }

    private fun isPasswordValid(password: String): Boolean {
        val passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{8,}$"
        return password.matches(passwordPattern.toRegex())
    }

    fun uploadImageToFirebase(imageUri: Uri) {
        val user = auth.currentUser
        if (user == null) {
            Log.e("UploadImage", "Authentication required")
            _message.value = "Пользователь должен быть авторизован для загрузки фотографии"
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
                    _message.value = "Не удалось получить URL-адрес загрузки."
                }
            }
            .addOnFailureListener { e ->
                Log.e("UploadImage", "Failed to upload image: ${e.message}", e)
                _message.value = "Ошибка загрузки: ${e.message}"
            }
    }

    private fun updateProfileImage(downloadUri: Uri) {
        val update = UserProfileChangeRequest.Builder().setPhotoUri(downloadUri).build()
        auth.currentUser?.updateProfile(update)
            ?.addOnSuccessListener {
                Log.d("ProfileUpdate", "Profile photo updated")
                _message.value = "Изображение профиля изменено"
                _profileImageUrl.value = downloadUri
            }
            ?.addOnFailureListener { e ->
                Log.e("ProfileUpdate", "Failed to update profile photo: ${e.message}", e)
                _message.value = "Ошибка изменения фотографии профиля: ${e.message}"
            }
    }
}