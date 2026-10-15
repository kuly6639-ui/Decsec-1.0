package com.example

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.DeviceCategory
import com.example.model.ExploitStatus
import com.example.viewmodel.HackerViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun testAppNameResource() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("DedSec cTOS Hacker", appName)
    }

    @Test
    fun testHackerViewModelInitializationAndCommands() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = HackerViewModel(app)

        // Verify initial state
        assertTrue(viewModel.terminalLines.value.isNotEmpty())
        assertEquals(0, viewModel.selectedTab.value)
        assertNotNull(viewModel.botnetStatus.value)
        assertTrue(viewModel.botnetStatus.value.followers > 0)
        assertTrue(viewModel.profilerTargets.value.isNotEmpty())
        assertTrue(viewModel.nearbyDevices.value.isNotEmpty())
        assertTrue(viewModel.showBootScreen.value)

        // Test executing command
        viewModel.executeCommand("help")
        val lines = viewModel.terminalLines.value
        assertTrue(lines.any { it.text.contains("DEDSEC") })

        // Test whoami
        viewModel.executeCommand("whoami")
        assertTrue(viewModel.terminalLines.value.any { it.text.contains("Marcus Holloway") })

        // Test audio toggle
        viewModel.executeCommand("audio off")
        assertFalse(viewModel.soundManager.isSoundEnabled)
        viewModel.executeCommand("audio on")
        assertTrue(viewModel.soundManager.isSoundEnabled)

        // Test devices command
        viewModel.executeCommand("devices")
        assertTrue(viewModel.terminalLines.value.any { it.text.contains("NEARBY ELECTRONIC DEVICES") })
    }

    @Test
    fun testNearbyDevicesAndWiretap() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = HackerViewModel(app)

        val devices = viewModel.nearbyDevices.value
        assertTrue(devices.any { it.category == DeviceCategory.SMART_TV })
        assertTrue(devices.any { it.category == DeviceCategory.SMARTPHONE })
        assertTrue(devices.any { it.category == DeviceCategory.SMART_SPEAKER })

        // Tap device call
        val phone = devices.first { it.category == DeviceCategory.SMARTPHONE }
        viewModel.tapDeviceCall(phone)
        assertNotNull(viewModel.activeTappedDevice.value)
        assertEquals(phone.id, viewModel.activeTappedDevice.value?.id)

        // Extract intel from intercepted call
        viewModel.extractIntelFromCall(phone)
        assertNull(viewModel.activeTappedDevice.value)
    }

    @Test
    fun testBootScreenSequenceFlow() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = HackerViewModel(app)

        assertTrue(viewModel.showBootScreen.value)
        viewModel.finishBoot()
        assertFalse(viewModel.showBootScreen.value)
        viewModel.restartBoot()
        assertTrue(viewModel.showBootScreen.value)
    }

    @Test
    fun testProfilerActions() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = HackerViewModel(app)

        val target = viewModel.profilerTargets.value.first()
        viewModel.drainCrypto(target)
        val updatedTarget = viewModel.profilerTargets.value.first { it.id == target.id }
        assertTrue(updatedTarget.isHacked)
    }
}
