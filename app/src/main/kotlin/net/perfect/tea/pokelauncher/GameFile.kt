package net.perfect.tea.pokelauncher.GameFile

import java.io.File
import android.util.Log
import kotlinx.coroutines.GlobalScope
import android.content.Intent
import android.content.pm.PackageManager
import android.app.PendingIntent
import android.content.Context
import net.perfect.tea.receiver.InstallReceiver
import android.content.BroadcastReceiver

class GameFile {
    val dir = File("games")

    fun createGamesDir() {
        Log.d("PokeLauncherApp", "createGamesDir")
        dir.mkdir()
    }

    fun getPackageNameFromApk(context: Context, apkPath: String): String? {
        val info = context.packageManager.getPackageArchiveInfo(apkPath, 0)
        return info?.packageName
    }

    fun installAndOpen(context: Context, apkFile: File) {

        val packageName = context.packageManager
            .getPackageArchiveInfo(apkFile.absolutePath, 0)
            ?.packageName

        if (packageName == null) {
            println("Invalid APK")
            return
        }


        installApk(context, apkFile, packageName)


        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
        intent?.let { context.startActivity(it) }
    }

    fun installApk(context: Context, apkFile: File, packageName: String) {
        val packageInstaller = context.packageManager.packageInstaller

        val params = PackageInstaller.SessionParams(
            PackageInstaller.SessionParams.MODE_FULL_INSTALL
        )
        val sessionId = packageInstaller.createSession(params)
        val session = packageInstaller.openSession(sessionId)!!

        val out = session.openWrite("base.apk", 0, apkFile.length())!!
        apkFile.inputStream().use { input ->
            input.copyTo(out)
        }
        session.fsync(out)
        out.close()

        val intent = Intent(context, InstallReceiver::class.java).apply {
            putExtra("session_id", sessionId)
            putExtra("package", packageName)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context, sessionId, intent, PendingIntent.FLAG_IMMUTABLE
        )
        session.commit(pendingIntent.intentSender)
        session.close()
    }
}

fun installApk(context: Context, apkFile: File, packageName: String) {
    val packageInstaller = context.packageManager.packageInstaller

    val params = PackageInstaller.SessionParams(
        PackageInstaller.SessionParams.MODE_FULL_INSTALL
    )
    val sessionId = packageInstaller.createSession(params)
    val session = packageInstaller.openSession(sessionId)!!

    val out = session.openWrite("base.apk", 0, apkFile.length())!!
    apkFile.inputStream().use { input ->
        input.copyTo(out)
    }
    session.fsync(out)
    out.close()

    val intent = Intent(context, InstallReceiver::class.java).apply {
        putExtra("session_id", sessionId)
        putExtra("package", packageName)
    }
    val pendingIntent = PendingIntent.getBroadcast(
        context, sessionId, intent, PendingIntent.FLAG_IMMUTABLE
    )
    session.commit(pendingIntent.intentSender)
    session.close()
}