package com.jarvis.launcher.accessibility

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.accessibilityservice.GestureResultCallback
import android.content.Context
import android.content.Intent
import android.graphics.Path
import android.graphics.Rect
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.view.accessibility.AccessibilityWindowInfo
import com.jarvis.core.util.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AccessibilityNodeSnapshot(
    val text: String?,
    val className: String?,
    val packageName: String?,
    val isClickable: Boolean,
    val isScrollable: Boolean,
    val isEditable: Boolean,
    val contentDescription: String?,
    val bounds: Rect?,
)

class JarvisAccessibilityService : AccessibilityService() {

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private val _currentWindowNodes = MutableStateFlow<List<AccessibilityNodeSnapshot>>(emptyList())
    val currentWindowNodes: StateFlow<List<AccessibilityNodeSnapshot>> = _currentWindowNodes.asStateFlow()

    private val _events = MutableStateFlow<List<AccessibilityEvent>>(emptyList())
    val events: StateFlow<List<AccessibilityEvent>> = _events.asStateFlow()

    companion object {
        @Volatile
        private var instance: JarvisAccessibilityService? = null

        fun getInstance(): JarvisAccessibilityService? = instance

        fun isEnabled(context: Context): Boolean {
            val services = android.provider.Settings.Secure.getString(
                context.contentResolver,
                "enabled_accessibility_services"
            )
            val serviceName = "${context.packageName}/${JarvisAccessibilityService::class.java.name}"
            return services?.contains(serviceName) ?: false
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        _isConnected.value = true
        Logger.info("Accessibility service connected")
    }

    override fun onDestroy() {
        _isConnected.value = false
        Logger.info("Accessibility service destroyed")
        super.onDestroy()
    }

    override fun onInterrupt() {
        Logger.warn("Accessibility service interrupted")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event ?: return
        val current = _events.value.toMutableList()
        current.add(event)
        if (current.size > 100) current.removeAt(0)
        _events.value = current
        Logger.debug("Accessibility event: ${event.eventType} ${event.className}")
    }

    fun getRootNode(): AccessibilityNodeInfo? = rootInActiveWindow

    fun getWindowInfo(): List<AccessibilityWindowInfo> = windows ?: emptyList()

    fun findNodesByText(text: String): List<AccessibilityNodeInfo> {
        val root = rootInActiveWindow ?: return emptyList()
        val results = mutableListOf<AccessibilityNodeInfo>()
        findNodesRecursive(root, text, results)
        return results
    }

    private fun findNodesRecursive(
        node: AccessibilityNodeInfo,
        text: String,
        results: MutableList<AccessibilityNodeInfo>,
    ) {
        val nodeText = node.text?.toString()
        val desc = node.contentDescription?.toString()
        if ((nodeText?.contains(text, ignoreCase = true) == true) ||
            (desc?.contains(text, ignoreCase = true) == true)
        ) {
            results.add(node)
        }
        for (i in 0 until node.childCount) {
            val child = node.getChild(i)
            if (child != null) {
                findNodesRecursive(child, text, results)
            }
        }
    }

    fun snapshotAllNodes(): List<AccessibilityNodeSnapshot> {
        val root = rootInActiveWindow ?: return emptyList()
        val results = mutableListOf<AccessibilityNodeSnapshot>()
        collectNodes(root, results)
        _currentWindowNodes.value = results
        return results
    }

    private fun collectNodes(
        node: AccessibilityNodeInfo,
        results: MutableList<AccessibilityNodeSnapshot>,
    ) {
        val rect = Rect()
        if (node.isClickable || node.isScrollable) {
            node.getBoundsInScreen(rect)
        }
        results.add(AccessibilityNodeSnapshot(
            text = node.text?.toString(),
            className = node.className?.toString(),
            packageName = node.packageName?.toString(),
            isClickable = node.isClickable,
            isScrollable = node.isScrollable,
            isEditable = node.isEditable,
            contentDescription = node.contentDescription?.toString(),
            bounds = if (rect.width() > 0) rect else null,
        ))
        for (i in 0 until node.childCount) {
            val child = node.getChild(i)
            if (child != null) {
                collectNodes(child, results)
            }
        }
    }

    fun clickAt(x: Float, y: Float) {
        val path = Path()
        path.addCircle(x, y, 1f, Path.Direction.CW)
        val stroke = GestureDescription.StrokeDescription(path, 0, 100)
        val gesture = GestureDescription.Builder()
            .addStroke(stroke)
            .build()
        dispatchGesture(gesture, null, null)
        Logger.info("Gesture: click at ($x, $y)")
    }

    fun clickNode(node: AccessibilityNodeInfo) {
        val action = AccessibilityNodeInfo.ACTION_CLICK
        if (node.addAction(action) || node.performAction(action)) {
            Logger.info("Clicked node via accessibility action")
        } else {
            val rect = Rect()
            node.getBoundsInScreen(rect)
            clickAt(rect.centerX().toFloat(), rect.centerY().toFloat())
        }
    }

    fun swipe(startX: Float, startY: Float, endX: Float, endY: Float) {
        val path = Path()
        path.moveTo(startX, startY)
        path.lineTo(endX, endY)
        val stroke = GestureDescription.StrokeDescription(path, 0, 500)
        val gesture = GestureDescription.Builder()
            .addStroke(stroke)
            .build()
        dispatchGesture(gesture, object : GestureResultCallback() {
            override fun onCompleted(gestureDescription: GestureDescription) {
                super.onCompleted(gestureDescription)
                Logger.debug("Swipe gesture completed")
            }
        }, null)
    }

    fun typeText(text: String) {
        val root = rootInActiveWindow ?: return
        val focused = findFocusedInput(root)
        if (focused != null) {
            val args = Bundle()
            args.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHAR_SEQUENCE, text)
            focused.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, args)
        }
    }

    private fun findFocusedInput(node: AccessibilityNodeInfo): AccessibilityNodeInfo? {
        if (node.isFocused && node.isEditable) return node
        for (i in 0 until node.childCount) {
            val child = node.getChild(i)
            if (child != null) {
                val result = findFocusedInput(child)
                if (result != null) return result
            }
        }
        return null
    }
}
