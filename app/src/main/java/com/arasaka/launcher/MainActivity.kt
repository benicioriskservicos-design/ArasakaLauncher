package com.arasaka.launcher

import android.app.Activity
import android.app.AlertDialog
import android.app.WallpaperManager
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.content.Context
import android.widget.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : Activity() {
    private val prefs by lazy { getSharedPreferences("arasaka_settings", MODE_PRIVATE) }
    private var accent = Color.rgb(227, 38, 54)
    private var wallpaperMode = 0
    private var showingDrawer = false
    private lateinit var root: LinearLayout
    private lateinit var content: LinearLayout
    private lateinit var clock: TextView
    private lateinit var date: TextView
    private lateinit var appList: LinearLayout
    private lateinit var search: EditText
    private lateinit var sectionTitle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        accent = prefs.getInt("accent", Color.rgb(227, 38, 54))
        wallpaperMode = prefs.getInt("wallpaper", 0)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        buildShell()
        showHome()
    }

    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()

    private fun bg(color: Int, radius: Int = 14, stroke: Int = 0): GradientDrawable {
        return GradientDrawable().apply {
            setColor(color)
            cornerRadius = dp(radius).toFloat()
            if (stroke != 0) setStroke(dp(1), stroke)
        }
    }

    private fun label(text: String, size: Float, color: Int = Color.WHITE, bold: Boolean = false): TextView {
        return TextView(this).apply {
            this.text = text
            textSize = size
            setTextColor(color)
            if (bold) typeface = Typeface.create("sans-serif-condensed", Typeface.BOLD)
            setPadding(dp(2), dp(2), dp(2), dp(2))
        }
    }

    private fun buildShell() {
        root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(22), dp(18), dp(18))
            background = GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                when (wallpaperMode) {
                    1 -> intArrayOf(Color.rgb(22, 5, 8), Color.rgb(5, 5, 7), Color.rgb(35, 8, 12))
                    2 -> intArrayOf(Color.rgb(18, 18, 20), Color.rgb(5, 5, 7))
                    else -> intArrayOf(Color.rgb(30, 7, 10), Color.rgb(7, 7, 9), Color.rgb(18, 5, 7))
                }
            )
        }
        setContentView(root)

        val top = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        val brand = label("荒坂  //  ARASAKA", 17f, accent, true)
        top.addView(brand, LinearLayout.LayoutParams(0, -2, 1f))
        top.addView(label("SECURE NETWORK  ●", 10f, Color.rgb(235, 80, 88), true))
        root.addView(top)

        val line = View(this).apply { setBackgroundColor(accent) }
        root.addView(line, LinearLayout.LayoutParams(-1, dp(1)).apply { topMargin = dp(12); bottomMargin = dp(18) })

        content = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        root.addView(content, LinearLayout.LayoutParams(-1, 0, 1f))

        val footer = label("ARASAKA CORPORATION  •  NIGHT CITY 2077", 9f, Color.GRAY, true).apply {
            gravity = Gravity.CENTER
            setPadding(0, dp(12), 0, 0)
        }
        root.addView(footer)
    }

    private fun showHome() {
        showingDrawer = false
        content.removeAllViews()
        clock = label(SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()), 58f, Color.WHITE, true)
        date = label(SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault()).format(Date()).uppercase(), 11f, Color.LTGRAY, true)
        content.addView(label("CORPORATE OPERATING SYSTEM  /  PERSONAL TERMINAL", 9f, accent, true))
        content.addView(clock)
        content.addView(date)
        content.addView(label("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━", 10f, accent))
        val status = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(12), dp(12), dp(12), dp(12))
            background = bg(Color.argb(90, 80, 10, 18), 10, accent)
        }
        val statusText = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        statusText.addView(label("SYSTEM STATUS", 10f, accent, true))
        statusText.addView(label("ALL SYSTEMS NOMINAL  //  ENCRYPTION ACTIVE", 11f, Color.WHITE))
        status.addView(statusText, LinearLayout.LayoutParams(0, -2, 1f))
        status.addView(label("●", 18f, Color.rgb(255, 55, 65), true))
        content.addView(status, LinearLayout.LayoutParams(-1, -2).apply { topMargin = dp(14) })

        content.addView(label("QUICK ACCESS", 11f, accent, true).apply {
            setPadding(0, dp(24), 0, dp(10))
        })
        val grid = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        val row1 = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        val row2 = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        addTile(row1, "▦", "APPLICATIONS") { showDrawer() }
        addTile(row1, "⌕", "SEARCH") { showDrawer(true) }
        addTile(row2, "⚙", "SETTINGS") { showSettings() }
        addTile(row2, "◈", "SYSTEM INFO") { showSystemInfo() }
        grid.addView(row1)
        grid.addView(row2)
        content.addView(grid)

        content.addView(label("RECENTLY USED", 11f, accent, true).apply {
            setPadding(0, dp(20), 0, dp(8))
        })
        val recent = getLaunchableApps().take(4)
        if (recent.isEmpty()) content.addView(label("NO APPLICATIONS DETECTED", 10f, Color.GRAY))
        else recent.forEach { app -> addCompactApp(content, app) }

        val spacer = Space(this)
        content.addView(spacer, LinearLayout.LayoutParams(1, 0, 1f))
        val drawerButton = Button(this).apply {
            text = "OPEN APPLICATION DATABASE   →"
            setTextColor(Color.WHITE)
            textSize = 12f
            isAllCaps = false
            background = bg(accent, 8)
            setOnClickListener { showDrawer() }
        }
        content.addView(drawerButton, LinearLayout.LayoutParams(-1, dp(52)))
    }

    private fun addTile(row: LinearLayout, symbol: String, title: String, action: () -> Unit) {
        val tile = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            background = bg(Color.argb(115, 30, 30, 34), 10, Color.argb(180, accent))
            setPadding(dp(8), dp(14), dp(8), dp(14))
            setOnClickListener { action() }
        }
        tile.addView(label(symbol, 28f, accent, true).apply { gravity = Gravity.CENTER })
        tile.addView(label(title, 10f, Color.WHITE, true).apply { gravity = Gravity.CENTER })
        row.addView(tile, LinearLayout.LayoutParams(0, dp(88), 1f).apply {
            if (row.childCount > 0) marginStart = dp(10)
            bottomMargin = dp(10)
        })
    }

    private data class AppEntry(val name: String, val packageName: String, val icon: android.graphics.drawable.Drawable)

    @Suppress("DEPRECATION")
    private fun getLaunchableApps(): List<AppEntry> {
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        return packageManager.queryIntentActivities(intent, PackageManager.MATCH_ALL)
            .mapNotNull {
                val info = it.activityInfo ?: return@mapNotNull null
                if (info.packageName == packageName) return@mapNotNull null
                AppEntry(it.loadLabel(packageManager).toString(), info.packageName, it.loadIcon(packageManager))
            }
            .distinctBy { it.packageName }
            .sortedBy { it.name.lowercase(Locale.getDefault()) }
    }

    private fun addCompactApp(parent: LinearLayout, app: AppEntry) {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(8), dp(5), dp(8), dp(5))
            setOnClickListener { launch(app.packageName) }
        }
        val icon = ImageView(this).apply { setImageDrawable(app.icon) }
        row.addView(icon, LinearLayout.LayoutParams(dp(30), dp(30)))
        row.addView(label("  ${app.name}", 12f, Color.WHITE), LinearLayout.LayoutParams(0, -2, 1f))
        row.addView(label("OPEN  ›", 9f, accent, true))
        parent.addView(row)
    }

    private fun showDrawer(focusSearch: Boolean = false) {
        showingDrawer = true
        content.removeAllViews()
        val header = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
        header.addView(label("APPLICATION DATABASE", 17f, Color.WHITE, true), LinearLayout.LayoutParams(0, -2, 1f))
        val home = Button(this).apply {
            text = "HOME"
            textSize = 10f
            setTextColor(accent)
            background = bg(Color.TRANSPARENT, 6, accent)
            setOnClickListener { showHome() }
        }
        header.addView(home)
        content.addView(header)
        search = EditText(this).apply {
            hint = "Search installed applications..."
            setHintTextColor(Color.GRAY)
            setTextColor(Color.WHITE)
            textSize = 14f
            singleLine = true
            setPadding(dp(12), 0, dp(12), 0)
            background = bg(Color.rgb(28, 25, 28), 8, accent)
        }
        content.addView(search, LinearLayout.LayoutParams(-1, dp(48)).apply { topMargin = dp(12); bottomMargin = dp(10) })
        sectionTitle = label("", 10f, accent, true)
        content.addView(sectionTitle)
        val scroll = ScrollView(this)
        appList = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        scroll.addView(appList)
        content.addView(scroll, LinearLayout.LayoutParams(-1, 0, 1f))
        val apps = getLaunchableApps()
        fun render(filter: String) {
            appList.removeAllViews()
            val filtered = apps.filter { it.name.contains(filter, true) || it.packageName.contains(filter, true) }
            sectionTitle.text = "${filtered.size} APPLICATION(S) FOUND"
            filtered.forEach { app ->
                val item = LinearLayout(this).apply {
                    orientation = LinearLayout.HORIZONTAL
                    gravity = Gravity.CENTER_VERTICAL
                    setPadding(dp(10), dp(9), dp(10), dp(9))
                    background = bg(Color.argb(75, 45, 35, 38), 8)
                    setOnClickListener { launch(app.packageName) }
                }
                item.addView(ImageView(this).apply { setImageDrawable(app.icon) },
                    LinearLayout.LayoutParams(dp(38), dp(38)))
                val info = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
                info.addView(label(app.name, 13f, Color.WHITE, true))
                info.addView(label(app.packageName, 9f, Color.GRAY))
                item.addView(info, LinearLayout.LayoutParams(0, -2, 1f).apply { marginStart = dp(12) })
                item.addView(label("EXECUTE  ›", 9f, accent, true))
                appList.addView(item, LinearLayout.LayoutParams(-1, -2).apply { bottomMargin = dp(7) })
            }
            if (filtered.isEmpty()) appList.addView(label("NO MATCHING APPLICATIONS", 12f, Color.GRAY))
        }
        render("")
        search.addTextChangedListener(SimpleWatcher { render(it) })
        if (focusSearch) {
            search.requestFocus()
            search.post {
                (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                    .showSoftInput(search, InputMethodManager.SHOW_IMPLICIT)
            }
        }
    }

    private fun launch(pkg: String) {
        try {
            val intent = packageManager.getLaunchIntentForPackage(pkg)
            if (intent != null) startActivity(intent)
            else Toast.makeText(this, "Application unavailable", Toast.LENGTH_SHORT).show()
        } catch (_: Exception) {
            Toast.makeText(this, "Unable to launch application", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showSettings() {
        val options = arrayOf("ARASAKA RED", "WARNING AMBER", "ICE BLUE", "DARK WALLPAPER", "RED ALERT WALLPAPER", "GRAPHITE WALLPAPER", "SYSTEM INFORMATION")
        AlertDialog.Builder(this)
            .setTitle("ARASAKA // PERSONALIZATION")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> accent = Color.rgb(227, 38, 54)
                    1 -> accent = Color.rgb(255, 177, 45)
                    2 -> accent = Color.rgb(65, 205, 255)
                    3 -> wallpaperMode = 0
                    4 -> wallpaperMode = 1
                    5 -> wallpaperMode = 2
                    6 -> { showSystemInfo(); return@setItems }
                }
                prefs.edit().putInt("accent", accent).putInt("wallpaper", wallpaperMode).apply()
                buildShell()
                showHome()
            }
            .setNegativeButton("CLOSE", null)
            .show()
    }

    private fun showSystemInfo() {
        AlertDialog.Builder(this)
            .setTitle("SYSTEM INFORMATION")
            .setMessage("ARASAKA LAUNCHER v1.0\n\nDEVICE: ${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL}\nANDROID: ${android.os.Build.VERSION.RELEASE}\nAPI LEVEL: ${android.os.Build.VERSION.SDK_INT}\n\nLOCAL MODE: ACTIVE\nNETWORK CONNECTION: NOT REQUIRED")
            .setPositiveButton("ACKNOWLEDGE", null)
            .show()
    }

    override fun onBackPressed() {
        if (showingDrawer) showHome() else super.onBackPressed()
    }

    private class SimpleWatcher(val changed: (String) -> Unit) : android.text.TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { changed(s?.toString() ?: "") }
        override fun afterTextChanged(s: android.text.Editable?) {}
    }
}
