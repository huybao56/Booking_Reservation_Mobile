package com.example.project_graduation.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import com.example.project_graduation.domain.model.User
import com.example.project_graduation.domain.model.UserRole

//create a new local data in explorer
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "auth_preferences"
)

class PreferencesManager(private val context: Context) {

    private val dataStore = context.dataStore

    //    create column data local
    companion object {
        private val TOKEN_KEY = stringPreferencesKey("auth_token")
        private val USER_ID_KEY = intPreferencesKey("user_id")
        private val USER_NAME_KEY = stringPreferencesKey("user_name")
        private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
        private val USER_PHONE_KEY = stringPreferencesKey("user_phone")
        private val USER_ROLE_KEY = stringPreferencesKey("user_role")
        private val USER_CREATED_AT_KEY = stringPreferencesKey("user_created_at")

        // Staff info keys
        private val STAFF_ID_KEY = intPreferencesKey("staff_id")
        private val STAFF_HOTEL_ID_KEY = intPreferencesKey("staff_hotel_id")
        private val STAFF_HOTEL_NAME_KEY = stringPreferencesKey("staff_hotel_name")
        private val STAFF_POSITION_KEY = stringPreferencesKey("staff_position")
        private val STAFF_CAN_CHAT_KEY = booleanPreferencesKey("staff_can_chat")
    }

    //    save user and token
    suspend fun saveUser(token: String, user: User) {
        dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
            preferences[USER_ID_KEY] = user.userId
            preferences[USER_NAME_KEY] = user.username
            preferences[USER_EMAIL_KEY] = user.email
            user.phone?.let { preferences[USER_PHONE_KEY] = it }
            preferences[USER_ROLE_KEY] = user.role.name
            preferences[USER_CREATED_AT_KEY] = user.createdAt
        }
    }

    //    get user saved
    suspend fun getUser(): User? {
        val preferences = dataStore.data.first()
        val token = preferences[TOKEN_KEY] ?: return null
        val userId = preferences[USER_ID_KEY] ?: return null
        val userName = preferences[USER_NAME_KEY] ?: return null
        val userEmail = preferences[USER_EMAIL_KEY] ?: return null
        val userPhone = preferences[USER_PHONE_KEY] ?: return null
        val userRole = preferences[USER_ROLE_KEY] ?: "USER"
        val createdAt = preferences[USER_CREATED_AT_KEY] ?: return null

        return User(
            userId = userId,
            username = userName,
            email = userEmail,
            phone = userPhone,
            createdAt = createdAt,
            role = UserRole.fromString(userRole),
            token = token
        )
    }

    //    get token
    suspend fun getToken(): String? {
        return dataStore.data.map { it[TOKEN_KEY] }.first()
    }

    // ADDED: Get user role
    suspend fun getUserRole(): UserRole {
        val preferences = dataStore.data.first()
        val roleString = preferences[USER_ROLE_KEY] ?: "USER"
        return UserRole.fromString(roleString)
    }

    // ADDED: Check if user is admin
    suspend fun isAdmin(): Boolean {
        return getUserRole() == UserRole.ADMIN
    }

    suspend fun isStaff(): Boolean {
        return getUserRole() == UserRole.STAFF
    }

    suspend fun saveStaffInfo(
        staffId: Int,
        hotelId: Int,
        hotelName: String,
        position: String,
        canChat: Boolean
    ) {
        dataStore.edit { prefs ->
            prefs[STAFF_ID_KEY] = staffId
            prefs[STAFF_HOTEL_ID_KEY] = hotelId
            prefs[STAFF_HOTEL_NAME_KEY] = hotelName
            prefs[STAFF_POSITION_KEY] = position
            prefs[STAFF_CAN_CHAT_KEY] = canChat
        }
    }

    suspend fun getStaffInfo(): StaffInfoLocal? {
        val prefs = dataStore.data.first()
        val staffId = prefs[STAFF_ID_KEY] ?: return null
        return StaffInfoLocal(
            staffId = staffId,
            hotelId = prefs[STAFF_HOTEL_ID_KEY] ?: 0,
            hotelName = prefs[STAFF_HOTEL_NAME_KEY] ?: "",
            position = prefs[STAFF_POSITION_KEY] ?: "",
            canChat = prefs[STAFF_CAN_CHAT_KEY] ?: true
        )
    }

    suspend fun clearStaffInfo() {
        dataStore.edit { prefs ->
            prefs.remove(STAFF_ID_KEY)
            prefs.remove(STAFF_HOTEL_ID_KEY)
            prefs.remove(STAFF_HOTEL_NAME_KEY)
            prefs.remove(STAFF_POSITION_KEY)
            prefs.remove(STAFF_CAN_CHAT_KEY)
        }
    }


    //    clear data user
    suspend fun clearUserData() {
        dataStore.edit { it.clear() }
    }


    //    check token
    suspend fun isLoggedIn(): Boolean {
        return getToken() != null
    }
}


// Data class dùng khi đọc staff info từ local storage
data class StaffInfoLocal(
    val staffId: Int,
    val hotelId: Int,
    val hotelName: String,
    val position: String,
    val canChat: Boolean
)