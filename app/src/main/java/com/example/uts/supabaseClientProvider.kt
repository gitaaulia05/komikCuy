package com.example.uts

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseClientProvider {
    val client: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = "https://gjiahecfesmuttvcpuiv.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImdqaWFoZWNmZXNtdXR0dmNwdWl2Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTI1MzY2NzYsImV4cCI6MjA2ODExMjY3Nn0.kSe95OtHvOaAIQru6eG2uosD0lEadUAtATLFGBLJebM"
        ) {
            install(Auth) {
                alwaysAutoRefresh = false
                autoSaveToStorage = true

            }
            install(Postgrest)

        }
    }
}