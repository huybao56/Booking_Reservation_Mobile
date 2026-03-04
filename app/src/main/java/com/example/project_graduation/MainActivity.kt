package com.example.project_graduation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.project_graduation.ui.theme.Project_GraduationTheme
import com.example.project_graduation.data.local.PreferencesManager
import com.example.project_graduation.data.remote.api.AuthApi
import com.example.project_graduation.data.remote.api.BookingApi
import com.example.project_graduation.data.remote.api.BookingPaymentApi
import com.example.project_graduation.data.remote.api.HotelApi
import com.example.project_graduation.data.remote.api.RoomApi
import com.example.project_graduation.data.remote.api.UploadApi
import com.example.project_graduation.data.remote.api.UserApi
import com.example.project_graduation.data.repository.AuthRepositoryImpl
import com.example.project_graduation.data.repository.BookingRepositoryImpl
import com.example.project_graduation.data.repository.HotelRepositoryImpl
import com.example.project_graduation.data.repository.RoomRepositoryImpl
import com.example.project_graduation.data.repository.UserRepositoryImpl
import com.example.project_graduation.domain.usecase.GetUserProfileUseCase
import com.example.project_graduation.domain.usecase.LoginUseCase
import com.example.project_graduation.domain.usecase.LogoutUseCase
import com.example.project_graduation.domain.usecase.RegisterUseCase
import com.example.project_graduation.presentation.admin.booking_management.BookingsManagementViewModel
import com.example.project_graduation.presentation.admin.hotel_management.HotelsManagementViewModel
import com.example.project_graduation.presentation.admin.room_management.RoomsManagementViewModel
import com.example.project_graduation.presentation.admin.user_management.UsersManagementViewModel
import com.example.project_graduation.presentation.room.RoomListViewModel
import com.example.project_graduation.presentation.home.HomeViewModel
import com.example.project_graduation.presentation.hotel.HotelDetailViewModel
import com.example.project_graduation.presentation.login.LoginViewModel
import com.example.project_graduation.presentation.navigation.NavGraph
import com.example.project_graduation.presentation.profile.ProfileViewModel
import com.example.project_graduation.presentation.register.RegisterViewModel
import com.example.project_graduation.presentation.room_detail.RoomDetailViewModel
import com.example.project_graduation.presentation.staff.StaffViewModel
import com.example.project_graduation.data.remote.api.StaffApi
import com.example.project_graduation.presentation.staff.staff_booking_management.StaffBookingsViewModel
import com.example.project_graduation.presentation.staff.staff_chat_management.StaffChatViewModel
import com.example.project_graduation.presentation.staff.staff_dashboard_management.StaffDashboardViewModel
import com.example.project_graduation.presentation.staff.staff_room_management.StaffRoomsViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

//        create apis
        val authApi = AuthApi()
        val hotelApi = HotelApi()
        val roomApi = RoomApi()
        val userApi = UserApi()
        val bookingApi = BookingApi()
        val bookingPaymentApi = BookingPaymentApi()
        val uploadApi = UploadApi()
        val staffApi = StaffApi()


//        create preferencemanager
        val preferencesManager = PreferencesManager(applicationContext)

//        create repositories
        val authRepository = AuthRepositoryImpl(authApi, preferencesManager)
        val hotelRepository = HotelRepositoryImpl(hotelApi)
        val roomRepository = RoomRepositoryImpl(roomApi)
        val userRepository = UserRepositoryImpl(userApi)
        val bookingRepository = BookingRepositoryImpl(bookingApi, bookingPaymentApi)


//        create usecases
        val loginUseCase = LoginUseCase(authRepository)
        val registerUseCase = RegisterUseCase(authRepository)
        val getUserProfileUseCase = GetUserProfileUseCase(authRepository)
        val logoutUseCase = LogoutUseCase(authRepository)


//        create viewmodels
        val loginViewModel = LoginViewModel(loginUseCase)
        val registerViewModel = RegisterViewModel(registerUseCase)
        val homeViewModel = HomeViewModel(hotelRepository)
        val profileViewModel = ProfileViewModel(getUserProfileUseCase, logoutUseCase)
        val hotelDetailViewModel = HotelDetailViewModel(hotelRepository)
        val roomListViewModel = RoomListViewModel(roomRepository)
        val hotelsManagementViewModel = HotelsManagementViewModel(hotelRepository, uploadApi)
        val usersManagementViewModel = UsersManagementViewModel(userRepository)
        val bookingsManagementViewModel = BookingsManagementViewModel(bookingRepository)
        val roomsManagementViewModel = RoomsManagementViewModel(roomRepository, uploadApi)
        val roomDetailViewModel = RoomDetailViewModel(roomRepository)


        val staffViewModel = StaffViewModel()
        // ViewModels — Staff (thay thế staffViewModel cũ)
        val staffDashboardViewModel = StaffDashboardViewModel(staffApi)
        val staffBookingsViewModel = StaffBookingsViewModel()
        val staffRoomsViewModel = StaffRoomsViewModel()
        val staffChatViewModel = StaffChatViewModel()

        setContent {
            Project_GraduationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavGraph(
                        navController = navController,

                        preferencesManager = preferencesManager,

                        loginViewModel = loginViewModel,
                        registerViewModel = registerViewModel,
                        homeViewModel = homeViewModel,
                        profileViewModel = profileViewModel,
                        hotelDetailViewModel = hotelDetailViewModel,
                        roomListViewModel = roomListViewModel,

                        hotelsManagementViewModel = hotelsManagementViewModel,
                        usersManagementViewModel = usersManagementViewModel,
                        bookingsManagementViewModel = bookingsManagementViewModel,
                        roomsManagementViewModel = roomsManagementViewModel,
                        roomDetailViewModel = roomDetailViewModel,

                        staffViewModel = staffViewModel,
                        staffDashboardViewModel = staffDashboardViewModel,
                        staffBookingsViewModel = staffBookingsViewModel,
                        staffRoomsViewModel = staffRoomsViewModel,
                        staffChatViewModel = staffChatViewModel
                    )
                }
            }
        }
    }
}