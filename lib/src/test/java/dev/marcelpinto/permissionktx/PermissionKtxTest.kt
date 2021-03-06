package dev.marcelpinto.permissionktx

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4


@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class PermissionKtxTest {

    private lateinit var permissionsStatus: PermissionStatus

    @Before
    fun setUp() {
        val fakeChecker = object : PermissionChecker {
            override fun getStatus(type: Permission) = permissionsStatus
        }
        val dummyObserver = object : PermissionObserver {
            override fun getStatusFlow(type: Permission) = emptyFlow<PermissionStatus>()

            override fun refreshStatus() {}
        }
        PermissionProvider.init(fakeChecker, dummyObserver)
    }

    @Test
    fun `test given a permission name that is granted, then isPermissionGranted returns true`() {
        val permissionType = Permission("any")
        permissionsStatus = PermissionStatus.Granted(permissionType)

        assertThat(permissionType.status.isGranted()).isTrue()
    }

    @Test
    fun `test given a permission name that is revoked, then isPermissionGranted returns false`() {
        val permissionType = Permission("any")
        permissionsStatus = PermissionStatus.Revoked(permissionType, PermissionRational.OPTIONAL)

        assertThat(permissionType.status.isGranted()).isFalse()
    }

    @Test
    fun `test getPermissionStatus with a permission name that is revoked with required rational`() {
        val permissionType = Permission("any")
        permissionsStatus = PermissionStatus.Revoked(permissionType, PermissionRational.REQUIRED)

        assertThat(permissionType.status).isEqualTo(permissionsStatus)
    }
}