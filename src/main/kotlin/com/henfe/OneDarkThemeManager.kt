package com.henfe

import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupManager
import com.intellij.util.messages.MessageBusConnection
import com.henfe.notification.Notifications
import com.henfe.settings.ThemeSettings
import java.util.*

enum class OneDarkThemes {
  REGULAR, ITALIC, VIVID, VIVID_ITALIC
}

object OneDarkThemeManager {
  private lateinit var messageBus: MessageBusConnection
  val THEMES = mapOf(
    "42c9c502-18f3-11ec-9621-0242ac130002" to OneDarkThemes.REGULAR,
    "42c9c70a-18f3-11ec-9621-0242ac130002" to OneDarkThemes.ITALIC,
    "42c9c7f0-18f3-11ec-9621-0242ac130002" to OneDarkThemes.VIVID,
    "42c9c8ae-18f3-11ec-9621-0242ac130002" to OneDarkThemes.VIVID_ITALIC
  )
  private const val PLUGIN_ID = "com.henfe.one-dark-ayu-theme"

  fun registerStartup(project: Project) {
    if (!this::messageBus.isInitialized) {
      registerUser()

      attemptToDisplayUpdates(project)

      subscribeToEvents()
    }
  }

  private fun registerUser() {
    if (ThemeSettings.instance.userId.isEmpty()) {
      ThemeSettings.instance.userId = UUID.randomUUID().toString()
    }
  }

  private fun attemptToDisplayUpdates(project: Project) {
    getVersion().ifPresent { currentVersion ->
      if (ThemeSettings.instance.version != currentVersion) {
        ThemeSettings.instance.version = currentVersion
        StartupManager.getInstance(project).runWhenProjectIsInitialized {
          Notifications.displayUpdateNotification(currentVersion)
        }
      }
    }
  }

  private fun getVersion(): Optional<String> =
    PluginManagerCore.getPlugin(PluginId.getId(PLUGIN_ID)).toOptional()
      .map { it.version }

  private fun subscribeToEvents() {
    messageBus = ApplicationManager.getApplication().messageBus.connect()
  }
}