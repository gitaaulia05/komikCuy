package com.example.uts


import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import io.github.jan.supabase.gotrue.auth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map


val Context.dataStore : DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferencesRepository(private val dataStore: DataStore<Preferences>)
{
    companion object {
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val AUTH_TOKEN = stringPreferencesKey("auth_token")
        val USER_ID = stringPreferencesKey("user_id")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
        private val USER_NAME_KEY = stringPreferencesKey("user_name")
        private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
        private val USER_AVATAR= stringPreferencesKey("avatar_url")
    }

    suspend fun saveLoginData(idToken: String, userId: String, refreshToken: String, userName: String, userEmail: String , userAvatar: String  ){
        dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = true
            preferences[AUTH_TOKEN] = idToken
            preferences[USER_ID] = userId
            preferences[REFRESH_TOKEN_KEY] = refreshToken
            preferences[USER_NAME_KEY] = userName
            preferences[USER_EMAIL_KEY] = userEmail
            preferences[USER_AVATAR] = userAvatar

        }
    }

    suspend fun  clearLoginData(){
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    suspend fun getUserId(): String? {
        return dataStore.data.map { preferences ->
            preferences[USER_ID]
        }.firstOrNull()
    }


    suspend fun getUserEmail(): String? {
        return dataStore.data.map { preferences ->
            preferences[USER_EMAIL_KEY]
        }.firstOrNull()
    }

    suspend fun getUserName(): String? {
        return dataStore.data.map { preferences ->
            preferences[USER_NAME_KEY]
        }.firstOrNull()
    }


    suspend fun isUserLoggedIn(): Boolean {
        val prefs = dataStore.data.first()
        val isLoggedIn = prefs[IS_LOGGED_IN] ?: false
        val userId = prefs[USER_ID] ?: ""

        Log.d("Repository", "DataStore - isLoggedIn: $isLoggedIn")
        Log.d("Repository", "DataStore - userId: $userId")

        return isLoggedIn
    }

    // Add session expiration check
    suspend fun getAuthToken(): String? = try {
        dataStore.data
            .map { it[AUTH_TOKEN] }
            .first()
    } catch (e: Exception) {
        null
    }

    suspend fun clearLoginState() {
        dataStore.edit { preferences ->
            preferences.remove(IS_LOGGED_IN)
            preferences.remove(USER_ID)
            preferences.remove(USER_NAME_KEY)
            preferences.remove(USER_EMAIL_KEY)
            preferences.remove(USER_AVATAR)
        }
    }

    // Tambahkan di UserPreferencesRepository
    suspend fun isSessionValid(): Boolean {
        val token = getAuthToken()
        val session = SupabaseClientProvider.client.auth.currentSessionOrNull()
        return session != null && token == session.accessToken
    }


}