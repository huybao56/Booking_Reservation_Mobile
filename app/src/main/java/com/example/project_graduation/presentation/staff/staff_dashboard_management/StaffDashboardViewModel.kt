package com.example.project_graduation.presentation.staff.staff_dashboard_management

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_graduation.data.local.PreferencesManager
import com.example.project_graduation.data.remote.api.StaffApi
import com.example.project_graduation.data.remote.dto.StaffInfoDto
import com.example.project_graduation.data.remote.dto.UserDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ─── UI Model cho tab Profile ────────────────────────────────────────────────
data class StaffProfile(
    val staffId: Int = 0,
    val hotelId: Int = 0,
    val hotelName: String = "",
    val position: String = "",
    val canChat: Boolean = true,
    // từ UserDto (user object trong login response)
    val userId: Int = 0,
    val username: String = "",
    val email: String = "",
    val phone: String = ""
)

// ─── Dashboard Stats ─────────────────────────────────────────────────────────
data class StaffDashboardStats(
    val todayCheckIns: Int = 0,
    val todayCheckOuts: Int = 0,
    val availableRooms: Int = 0,
    val occupiedRooms: Int = 0,
    val maintenanceRooms: Int = 0,
    val pendingBookings: Int = 0,
    val totalRevenue: Double = 0.0
)

// ─── ViewModel ───────────────────────────────────────────────────────────────
class StaffDashboardViewModel(
    private val preferencesManager: PreferencesManager,
    private val staffApi: StaffApi
) : ViewModel() {

    private val _profile = MutableStateFlow(StaffProfile())
    val profile: StateFlow<StaffProfile> = _profile.asStateFlow()

    private val _stats = MutableStateFlow(StaffDashboardStats())
    val stats: StateFlow<StaffDashboardStats> = _stats.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()


    // Gọi ngay sau login thành công
    fun initFromLogin(staffInfoDto: StaffInfoDto, userDto: UserDto) {
        viewModelScope.launch {
            preferencesManager.saveStaffInfo(
                staffId   = staffInfoDto.staffId,
                hotelId   = staffInfoDto.hotelId,
                hotelName = staffInfoDto.hotelName,
                position  = staffInfoDto.position,
                canChat   = staffInfoDto.canChat
            )
            _profile.value = StaffProfile(
                staffId   = staffInfoDto.staffId,
                hotelId   = staffInfoDto.hotelId,
                hotelName = staffInfoDto.hotelName,
                position  = staffInfoDto.position,
                canChat   = staffInfoDto.canChat,
                userId    = userDto.userId,
                username  = userDto.username,
                email     = userDto.email,
                phone     = userDto.phone ?: ""
            )
            loadDashboardStats(staffInfoDto.hotelId)
        }
    }

    // Gọi khi resume app — đọc từ PreferencesManager, không cần API
    fun initFromPrefs() {
        viewModelScope.launch {
            val staffLocal = preferencesManager.getStaffInfo()
            val user       = preferencesManager.getUser()
            if (staffLocal == null || user == null) {
                _error.value = "Không tìm thấy thông tin đăng nhập"
                return@launch
            }
            _profile.value = StaffProfile(
                staffId   = staffLocal.staffId,
                hotelId   = staffLocal.hotelId,
                hotelName = staffLocal.hotelName,
                position  = staffLocal.position,
                canChat   = staffLocal.canChat,
                userId    = user.userId,
                username  = user.username,
                email     = user.email,
                phone     = user.phone ?: ""
            )
            Log.d("StaffDashboardVM", "initFromPrefs OK: ${user.username} @ ${staffLocal.hotelName}")
            loadDashboardStats(staffLocal.hotelId)
        }
    }

    // Gọi API thật
    private fun loadDashboardStats(hotelId: Int) {
        viewModelScope.launch {
            staffApi.getDashboardStats(hotelId)
                .onSuccess { dto ->
                    _stats.value = StaffDashboardStats(
                        todayCheckIns    = dto.todayCheckIns,
                        todayCheckOuts   = dto.todayCheckOuts,
                        availableRooms   = dto.availableRooms,
                        occupiedRooms    = dto.occupiedRooms,
                        maintenanceRooms = dto.maintenanceRooms,
                        pendingBookings  = dto.pendingBookings,
                        totalRevenue     = dto.totalRevenue
                    )
                }
                .onFailure { e ->
                    Log.e("StaffDashboardVM", "loadStats failed: ${e.message}")
                    // Giữ nguyên default 0 nếu API fail — không crash UI
                }
        }
    }


//    // ─────────────────────────────────────────────────────────────────────────
//    // Gọi ngay sau login thành công (từ LoginViewModel/NavGraph)
//    // staffInfoDto: lấy từ loginResponse.staffInfo
//    // userDto:      lấy từ loginResponse.user
//    // ─────────────────────────────────────────────────────────────────────────
//    fun initFromLogin(staffInfoDto: StaffInfoDto, userDto: UserDto) {
//        _profile.value = StaffProfile(
//            staffId = staffInfoDto.staffId,
//            hotelId = staffInfoDto.hotelId,
//            hotelName = staffInfoDto.hotelName,
//            position = staffInfoDto.position,
//            canChat = staffInfoDto.canChat,
//            userId = userDto.userId,
//            username = userDto.username,
//            email = userDto.email,
//            phone = userDto.phone ?: ""
//        )
//        Log.d(
//            "StaffDashboardVM",
//            "Initialized: ${_profile.value.username} @ ${_profile.value.hotelName}"
//        )
//        loadDashboardStats(staffInfoDto.hotelId)
//    }
//
//    // Gọi khi resume app (user đã login trước đó, lấy lại từ PreferencesManager)
//    fun initFromPrefs(userId: Int, username: String, email: String, phone: String) {
//        viewModelScope.launch {
//            _isLoading.value = true
//            val result = staffApi.getMyStaffInfo(userId)
//            result.onSuccess { dto ->
//                _profile.value = StaffProfile(
//                    staffId = dto.staffId,
//                    hotelId = dto.hotelId,
//                    hotelName = dto.hotelName,
//                    position = dto.position,
//                    canChat = dto.canChat,
//                    userId = userId,
//                    username = username,
//                    email = email,
//                    phone = phone
//                )
//                loadDashboardStats(dto.hotelId)
//            }
//            result.onFailure { e ->
//                _error.value = "Không tải được thông tin staff: ${e.message}"
//                Log.e("StaffDashboardVM", "initFromPrefs error: ${e.message}")
//            }
//            _isLoading.value = false
//        }
//    }
//
//    private fun loadDashboardStats(hotelId: Int) {
//        viewModelScope.launch {
//            // TODO: thay bằng API thật
//            // val bookings = bookingApi.getByHotel(hotelId)
//            // val rooms    = roomApi.getByHotel(hotelId)
//            _stats.value = StaffDashboardStats(
//                todayCheckIns = 3,
//                todayCheckOuts = 1,
//                availableRooms = 7,
//                occupiedRooms = 2,
//                maintenanceRooms = 1,
//                pendingBookings = 1,
//                totalRevenue = 13800000.0
//            )
//        }
//    }
//
//    // Được gọi từ StaffBookingsViewModel / StaffRoomsViewModel khi có data thật
//    fun updateStatsFromData(
//        checkIns: Int, checkOuts: Int, available: Int,
//        occupied: Int, maintenance: Int, pending: Int, revenue: Double
//    ) {
//        _stats.value = StaffDashboardStats(
//            todayCheckIns = checkIns,
//            todayCheckOuts = checkOuts,
//            availableRooms = available,
//            occupiedRooms = occupied,
//            maintenanceRooms = maintenance,
//            pendingBookings = pending,
//            totalRevenue = revenue
//        )
//    }

    fun clearError() {
        _error.value = null
    }
}