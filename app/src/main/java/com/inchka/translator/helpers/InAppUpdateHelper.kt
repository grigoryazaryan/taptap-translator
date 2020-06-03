package com.inchka.translator.helpers

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability

class InAppUpdateHelper(private val activity: AppCompatActivity) : LifecycleObserver {
    private val REQUEST_CODE = 124

    private lateinit var appUpdateManager: AppUpdateManager

    private var updateStateListener: InstallStateUpdatedListener = InstallStateUpdatedListener { state ->
        state?.takeIf { it.installStatus() == InstallStatus.DOWNLOADED }?.let {
            downloadCompleteListener?.invoke()
        }
    }

    private var downloadCompleteListener: (() -> Unit)? = null

    init {
        activity.lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        // Creates instance of the manager.
        appUpdateManager = AppUpdateManagerFactory.create(activity)
        // Before starting an update, register a listener for updates.
        appUpdateManager.registerListener(updateStateListener)

        checkForUpdateAvailability()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        // When status updates are no longer needed, unregister the listener.
        appUpdateManager.unregisterListener(updateStateListener)
    }

    fun addOnDownloadCompleteListener(listener: () -> Unit): InAppUpdateHelper {
        downloadCompleteListener = listener
        return this
    }

    private fun checkForUpdateAvailability() {

        appUpdateManager
            .appUpdateInfo
            .addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                    // Request the update.
                    appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.FLEXIBLE, activity, REQUEST_CODE)
                } else if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                    // Update file was already downloaded
                    downloadCompleteListener?.invoke()
                }
            }
            .addOnFailureListener { err ->
                err.printStackTrace()
            }
    }

    fun completeUpdate() {
        appUpdateManager.completeUpdate()
    }
}