package com.flounderguy.knifefightutilities

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

// Empty base class required by Hilt to serve as the application-level dependency container.
@HiltAndroidApp
class KnifeFightApplication : Application() {
}