package com.example.project_graduation.presentation.navigation

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.project_graduation.data.local.PreferencesManager
import com.example.project_graduation.data.remote.api.StaffApi
import com.example.project_graduation.domain.model.Room
import com.example.project_graduation.domain.model.UserRole
import com.example.project_graduation.presentation.admin.AdminScreen
import com.example.project_graduation.presentation.admin.booking_management.BookingsManagementViewModel
import com.example.project_graduation.presentation.admin.hotel_management.HotelsManagementViewModel
import com.example.project_graduation.presentation.admin.room_management.RoomsManagementScreen
import com.example.project_graduation.presentation.admin.room_management.RoomsManagementViewModel
import com.example.project_graduation.presentation.admin.user_management.UsersManagementViewModel
import com.example.project_graduation.presentation.booking.BookingScreen
import com.example.project_graduation.presentation.chat.ChatViewModel
import com.example.project_graduation.presentation.room.RoomListScreen
import com.example.project_graduation.presentation.room.RoomListViewModel
import com.example.project_graduation.presentation.home.HomeScreen
import com.example.project_graduation.presentation.home.HomeViewModel
import com.example.project_graduation.presentation.hotel.HotelDetailScreen
import com.example.project_graduation.presentation.hotel.HotelDetailViewModel
import com.example.project_graduation.presentation.login.LoginScreen
import com.example.project_graduation.presentation.login.LoginViewModel
import com.example.project_graduation.presentation.main.MainUserScreen
import com.example.project_graduation.presentation.payment.PaymentScreen
import com.example.project_graduation.presentation.register.RegisterScreen
import com.example.project_graduation.presentation.register.RegisterViewModel
import com.example.project_graduation.presentation.profile.ProfileScreen
import com.example.project_graduation.presentation.profile.ProfileViewModel
import com.example.project_graduation.presentation.room.RoomDetailScreen
import com.example.project_graduation.presentation.room_detail.RoomDetailViewModel
import com.example.project_graduation.presentation.staff.StaffScreen
import com.example.project_graduation.presentation.staff.StaffViewModel
import com.example.project_graduation.presentation.staff.staff_booking_management.StaffBookingsViewModel
import com.example.project_graduation.presentation.staff.staff_chat_management.StaffChatViewModel
import com.example.project_graduation.presentation.staff.staff_dashboard_management.StaffDashboardViewModel
import com.example.project_graduation.presentation.staff.staff_room_management.StaffRoomsViewModel
import kotlinx.coroutines.runBlocking
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")

    //    object Home : Screen("home")
    object MainUser : Screen("main_user")

    object Home : Screen("home?checkIn={checkIn}&checkOut={checkOut}&guests={guests}") {

        fun createRoute(checkIn: String = "", checkOut: String = "", guests: Int = 2): String {
            return if (checkIn.isEmpty()) {
                "home"
            } else {
                "home?checkIn=$checkIn&checkOut=$checkOut&guests=$guests"
            }
        }
    }

    object Profile : Screen("profile")
    object Admin : Screen("admin")
//    object HotelDetail : Screen("hotels/{hotelId}") {
//        fun createRoute(hotelId: Int) = "hotels/$hotelId"
//    }

    object HotelDetail :
        Screen("hotels/{hotelId}?checkIn={checkIn}&checkOut={checkOut}&guests={guests}") {
        fun createRoute(hotelId: Int, checkIn: String, checkOut: String, guests: Int) =
            "hotels/$hotelId?checkIn=$checkIn&checkOut=$checkOut&guests=$guests"
    }

    //    object RoomList : Screen("hotels/{hotelId}/rooms?hotelName={hotelName}") {
//        fun createRoute(hotelId: Int, hotelName: String) =
//            "hotels/$hotelId/rooms?hotelName=$hotelName"
//    }
    object RoomList :
        Screen("hotels/{hotelId}/rooms?hotelName={hotelName}&checkIn={checkIn}&checkOut={checkOut}&guests={guests}") {
        fun createRoute(
            hotelId: Int,
            hotelName: String,
            checkIn: String,
            checkOut: String,
            guests: Int
        ): String {
            val encodedName = URLEncoder.encode(hotelName, StandardCharsets.UTF_8.toString())
            return "hotels/$hotelId/rooms?hotelName=$encodedName&checkIn=$checkIn&checkOut=$checkOut&guests=$guests"
        }
    }


    object RoomDetail : Screen(
        "room-detail/{roomId}/{hotelId}/{hotelName}/{availableUnits}/{pricePerNight}/{totalNights}/{checkIn}/{checkOut}/{roomNumber}/{roomType}/{floor}/{status}/{basePrice}/{capacity}/{amenities}"
    ) {
        fun createRoute(
            roomId: Int,
            hotelId: Int,
            hotelName: String,
            availableUnits: Int,
            pricePerNight: Double,
            totalNights: Int,
            checkIn: String,
            checkOut: String,
            roomNumber: String,
            roomType: String,
            floor: Int?,
            status: String?,
            basePrice: Double?,
            capacity: Int?,
            amenities: List<String>
        ): String {
            // Encode amenities as comma-separated string
            val amenitiesString = amenities.joinToString(",")
            val encodedAmenities =
                URLEncoder.encode(amenitiesString, StandardCharsets.UTF_8.toString())

            return "room-detail/$roomId/$hotelId/$hotelName/$availableUnits/$pricePerNight/$totalNights/$checkIn/$checkOut/${roomNumber}/$roomType/${floor ?: 0}/${status ?: "null"}/${basePrice ?: 0.0}/${capacity ?: 0}/$encodedAmenities"
        }
    }

    object Payment : Screen(
        "payment/{roomId}/{hotelId}/{hotelName}/{quantity}/{pricePerNight}/{totalNights}/{checkIn}/{checkOut}/{roomNumber}/{roomType}/{floor}/{status}/{basePrice}/{capacity}"
    ) {
        fun createRoute(
            roomId: Int,
            hotelId: Int,
            hotelName: String,
            quantity: Int,
            pricePerNight: Double,
            totalNights: Int,
            checkIn: String,
            checkOut: String,
            roomNumber: String?,
            roomType: String?,
            floor: Int?,
            status: String?,
            basePrice: Double?,
            capacity: Int?
        ) =
            "payment/$roomId/$hotelId/$hotelName/$quantity/$pricePerNight/$totalNights/$checkIn/$checkOut/${roomNumber}/$roomType/${floor}/${status ?: "null"}/${basePrice ?: 0.0}/${capacity ?: 0}"
    }

    object Booking : Screen("bookings")

    object RoomsManagement : Screen("admin/hotels/{hotelId}/rooms?hotelName={hotelName}") {
        fun createRoute(hotelId: Int, hotelName: String): String {
            val encodedName = URLEncoder.encode(hotelName, StandardCharsets.UTF_8.toString())
            return "admin/hotels/$hotelId/rooms?hotelName=$encodedName"
        }
    }

    object Staff : Screen("staff")
}

@Composable
fun NavGraph(
    navController: NavHostController,

    preferencesManager: PreferencesManager,

    loginViewModel: LoginViewModel,
    registerViewModel: RegisterViewModel,
    homeViewModel: HomeViewModel,
    profileViewModel: ProfileViewModel,
    hotelDetailViewModel: HotelDetailViewModel,
    roomListViewModel: RoomListViewModel,
    hotelsManagementViewModel: HotelsManagementViewModel,
    usersManagementViewModel: UsersManagementViewModel,
    bookingsManagementViewModel: BookingsManagementViewModel,
    roomsManagementViewModel: RoomsManagementViewModel,
    roomDetailViewModel: RoomDetailViewModel,

    staffViewModel: StaffViewModel,
    staffApi: StaffApi,
    staffDashboardViewModel: StaffDashboardViewModel,
    staffBookingsViewModel: StaffBookingsViewModel,
    staffRoomsViewModel: StaffRoomsViewModel,
    staffChatViewModel: StaffChatViewModel,
    chatViewModel: ChatViewModel

) {
    // Check if logged in
    var startDestination by remember { mutableStateOf<String?>(null) }

    val searchCriteria by homeViewModel.searchCriteria.collectAsState()

//    LaunchedEffect(Unit) {
//        val isLoggedIn = preferencesManager.isLoggedIn()
//        startDestination = if (isLoggedIn) {
////            Screen.MainUser.route
//            Screen.Home.route
//        } else {
//            Screen.Login.route
//        }
//    }


    // UPDATED: Check both login status AND role
//    LaunchedEffect(Unit) {
//        val isLoggedIn = preferencesManager.isLoggedIn()
//
//        startDestination = if (isLoggedIn) {
//            val isAdmin = preferencesManager.isAdmin()
//            val isStaff = preferencesManager.isStaff()
//            val user = preferencesManager.getUser()
//
//            Log.d("ContentValues", "User logged in: ${user?.username}, Role: ${user?.role}")
//
//            // Route based on role
//
//            if (isAdmin) {
//                Log.d("ContentValues", "Routing to Admin screen")
//                Screen.Admin.route
//            }
//            else {
//                Log.d("ContentValues", "Routing to Home screen")
//                Screen.MainUser.route
//            }
//        } else {
//            Log.d("ContentValues", "No user logged in, routing to Login")
//            Screen.Login.route
//        }
//    }

    LaunchedEffect(Unit) {
        val isLoggedIn = preferencesManager.isLoggedIn()
        val isAdmin = preferencesManager.isAdmin()
        val isStaff = preferencesManager.isStaff()
        val user = preferencesManager.getUser()

        Log.d("ContentValues", "User logged in: ${user?.username}, Role: ${user?.role}")

        startDestination = when {
            !isLoggedIn -> {
                Log.d("ContentValues", "No user logged in, routing to Login")
                Screen.Login.route
            }

            isAdmin -> {
                Log.d("ContentValues", "Routing to Admin screen")
                Screen.Admin.route
            }

            isStaff -> {
                Log.d("ContentValues", "Routing to Staff screen")
                Screen.Staff.route
            }

            else -> {
                Log.d("ContentValues", "Routing to Home screen")
                Screen.MainUser.route
            }
        }
    }


    // Show loading while checking
    if (startDestination == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    NavHost(
        navController = navController,
        startDestination = startDestination!!
//        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = loginViewModel,
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
//                onLoginSuccess = {
//                    navController.navigate(Screen.Home.route) {
//                        popUpTo(Screen.Login.route) { inclusive = true }
//                    }
//                }
                onLoginSuccess = {
                    // Lấy user từ preferences
                    val user = runBlocking { preferencesManager.getUser() }


                    Log.d(
                        "ContentValues",
                        "Login successful: ${user?.username}, Role: ${user?.role}"
                    )

                    if (user != null) {
                        chatViewModel.init(user.userId, user.username)
                    }
//                    // Navigate based on role
//                    val destination = if (user?.isAdmin() == true) {
//                        Screen.Admin.route
//                    } else {
//                        Screen.MainUser.route
//                    }
                    val destination = when (user?.role) {
                        UserRole.ADMIN -> {
                            Log.d("ContentValues", "Routing to Admin screen")
                            Screen.Admin.route
                        }

                        UserRole.STAFF -> {
                            Log.d("ContentValues", "Routing to Staff screen")
                            Screen.Staff.route
                        }

                        else -> {
                            Log.d("ContentValues", "Routing to Home screen")
                            Screen.MainUser.route
                        }
                    }

                    navController.navigate(destination) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }

            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                viewModel = registerViewModel,
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }


        composable(Screen.MainUser.route) {
            MainUserScreen(
                chatViewModel = chatViewModel,
                homeViewModel = homeViewModel,
                profileViewModel = profileViewModel,
                preferencesManager = preferencesManager,
                initialTab = 0,
                onNavigateToHotelDetail = { hotelId ->
                    val criteria = homeViewModel.searchCriteria.value
                    navController.navigate(
                        Screen.HotelDetail.createRoute(
                            hotelId,
                            criteria.checkIn,
                            criteria.checkOut,
                            criteria.guests
                        )
                    )
                },

                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.MainUser.route) { inclusive = true }
                    }
                }
            )
        }
//        composable(Screen.Home.route) {
//            HomeScreen(
//                viewModel = homeViewModel,
//                onNavigateToProfile = {
//                    navController.navigate(Screen.Profile.route)
//                },
//                onNavigateToHotelDetail = { hotelId ->
//                    navController.navigate(Screen.HotelDetail.createRoute(hotelId))
//                },
//                onLogout = {
//                    navController.navigate(Screen.Login.route) {
//                        popUpTo(Screen.Home.route) { inclusive = true }
//                    }
//                }
//            )
//        }

//        composable(Screen.Staff.route) {
//            StaffScreen(
//                staffViewModel = staffViewModel,
//                initialTab = 0,
//                onLogout = {
//                    navController.navigate(Screen.Login.route) {
//                        popUpTo(0) { inclusive = true }
//                    }
//                }
//            )
//        }

        // ADDED: Admin Screen
        composable(Screen.Admin.route) {

            val initialTab = it.savedStateHandle.get<Int>("selectedTab") ?: 0

            AdminScreen(
                profileViewModel = profileViewModel,
                hotelsManagementViewModel = hotelsManagementViewModel,
                usersManagementViewModel = usersManagementViewModel,
                bookingsManagementViewModel = bookingsManagementViewModel,
                roomsManagementViewModel = roomsManagementViewModel,
                initialTab = initialTab,
                onNavigateToRooms = { hotelId, hotelName ->
                    // Navigate to rooms management

                    navController.currentBackStackEntry?.savedStateHandle?.set("selectedTab", 2)

                    navController.navigate(
                        Screen.RoomsManagement.createRoute(hotelId, hotelName)
                    )
                },
                onLogout = {
                    Log.d("ContentValues", "Admin logout")
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Admin.route) { inclusive = true }
                    }
                }
            )
        }

        // NEW: Rooms Management Screen
        composable(
            route = Screen.RoomsManagement.route,
            arguments = listOf(
                navArgument("hotelId") { type = NavType.IntType },
                navArgument("hotelName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val hotelId = backStackEntry.arguments?.getInt("hotelId") ?: 0
            val hotelName = URLDecoder.decode(
                backStackEntry.arguments?.getString("hotelName") ?: "",
                StandardCharsets.UTF_8.toString()
            )

            RoomsManagementScreen(
                hotelId = hotelId,
                hotelName = hotelName,
                viewModel = roomsManagementViewModel,
                onBack = {
                    navController.previousBackStackEntry?.savedStateHandle?.set("selectedTab", 2)
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = "home?checkIn={checkIn}&checkOut={checkOut}&guests={guests}",
            arguments = listOf(
                navArgument("checkIn") {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument("checkOut") {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument("guests") {
                    type = NavType.IntType
                    defaultValue = 2
                }
            )
        ) { backStackEntry ->
            val checkIn = backStackEntry.arguments?.getString("checkIn")
            val checkOut = backStackEntry.arguments?.getString("checkOut")
            val guests = backStackEntry.arguments?.getInt("guests") ?: 2

            // Restore searchCriteria nếu có
            LaunchedEffect(checkIn, checkOut, guests) {
                if (!checkIn.isNullOrEmpty() && !checkOut.isNullOrEmpty()) {
                    Log.d(
                        "NavGraph",
                        "Restoring SearchCriteria: checkIn=$checkIn, checkOut=$checkOut, guests=$guests"
                    )
                    homeViewModel.updateSearchCriteria(checkIn, checkOut, guests)
                }
            }

            HomeScreen(
                viewModel = homeViewModel,
                profileViewModel = profileViewModel,
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                },
                onNavigateToHotelDetail = { hotelId ->
                    val criteria = homeViewModel.searchCriteria.value
                    Log.d("NavGraph", "Navigating to hotel with criteria: $criteria")
                    navController.navigate(
                        Screen.HotelDetail.createRoute(
                            hotelId,
                            criteria.checkIn,
                            criteria.checkOut,
                            criteria.guests
                        )
                    )
                },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }


        composable(Screen.Profile.route) {
            ProfileScreen(
                viewModel = profileViewModel,
                onNavigateToBookings = {
                    navController.navigate(Screen.Booking.route)
                },
                onBack = {
                    navController.popBackStack()
                },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        // Hotel Detail Screen với parameter
//        composable(
//            route = Screen.HotelDetail.route,
//            arguments = listOf(
//                navArgument("hotelId") {
//                    type = NavType.IntType
//                }
//            )
//        ) { backStackEntry ->
//            val hotelId = backStackEntry.arguments?.getInt("hotelId") ?: 0
//            HotelDetailScreen(
//                hotelId = hotelId,
//                viewModel = hotelDetailViewModel,
//                onBack = {
//                    navController.popBackStack()
//                },
//                onBookNow = { hotelId, hotelName ->
//                    navController.navigate(
//                        Screen.RoomList.createRoute(hotelId, hotelName)
//                    )
//                }
//            )
//        }

        composable(
            route = "hotels/{hotelId}?checkIn={checkIn}&checkOut={checkOut}&guests={guests}",
            arguments = listOf(
                navArgument("hotelId") { type = NavType.IntType },
                navArgument("checkIn") {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument("checkOut") {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument("guests") {
                    type = NavType.IntType
                    defaultValue = 2
                }
            )
        ) { backStackEntry ->
            val hotelId = backStackEntry.arguments?.getInt("hotelId") ?: 0
            val checkIn = backStackEntry.arguments?.getString("checkIn") ?: ""
            val checkOut = backStackEntry.arguments?.getString("checkOut") ?: ""
            val guests = backStackEntry.arguments?.getInt("guests") ?: 2

            HotelDetailScreen(
                hotelId = hotelId,
                viewModel = hotelDetailViewModel,
                onBack = {
                    // Navigate back VỚI SearchCriteria
                    Log.d(
                        "NavGraph",
                        "Back from hotel, restoring: checkIn=$checkIn, checkOut=$checkOut, guests=$guests"
                    )
//                    navController.navigate(
//                        Screen.Home.createRoute(checkIn, checkOut, guests)
//                    ) {
//                        popUpTo("home") { inclusive = true }
//                    }
                    // Cập nhật lại search criteria trong HomeViewModel
                    homeViewModel.updateSearchCriteria(checkIn, checkOut, guests)

                    // Pop back về MainUserScreen
                    navController.popBackStack(Screen.MainUser.route, inclusive = false)

                },
                onBookNow = { hotelId, hotelName ->
                    navController.navigate(
                        Screen.RoomList.createRoute(hotelId, hotelName, checkIn, checkOut, guests)
                    )
                }
            )
        }

        // Room List Screen
//        composable(
//            route = Screen.RoomList.route,
//            arguments = listOf(
//                navArgument("hotelId") { type = NavType.IntType },
//                navArgument("hotelName") { type = NavType.StringType }
//            )
//        ) { backStackEntry ->
//            val hotelId = backStackEntry.arguments?.getInt("hotelId") ?: 0
//            val hotelName = backStackEntry.arguments?.getString("hotelName") ?: ""
        composable(
            route = Screen.RoomList.route,
            arguments = listOf(
                navArgument("hotelId") { type = NavType.IntType },
                navArgument("hotelName") { type = NavType.StringType },
                navArgument("checkIn") { type = NavType.StringType },
                navArgument("checkOut") { type = NavType.StringType },
                navArgument("guests") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val hotelId = backStackEntry.arguments?.getInt("hotelId") ?: 0
            val hotelName = URLDecoder.decode(
                backStackEntry.arguments?.getString("hotelName") ?: "",
                StandardCharsets.UTF_8.toString()
            )
            val checkIn = backStackEntry.arguments?.getString("checkIn") ?: ""
            val checkOut = backStackEntry.arguments?.getString("checkOut") ?: ""
            val guests = backStackEntry.arguments?.getInt("guests") ?: 2

            // Cập nhật dates cho RoomListViewModel
            LaunchedEffect(checkIn, checkOut) {
                roomListViewModel.updateDates(checkIn, checkOut)
            }


            RoomListScreen(
                hotelId = hotelId,
                hotelName = hotelName,
                viewModel = roomListViewModel,
                onBack = { navController.popBackStack() },
                onRoomSelected = { roomAvailability ->
                    val room = roomAvailability.room
                    val state = roomListViewModel.state.value

                    navController.navigate(
                        Screen.RoomDetail.createRoute(
                            roomId = room.roomId,
                            hotelId = room.hotelId,
                            hotelName = hotelName,
                            availableUnits = roomAvailability.availableUnits,
                            pricePerNight = roomAvailability.pricePerNight,
                            totalNights = state.numberOfNights,
                            checkIn = state.checkIn,
                            checkOut = state.checkOut,
                            roomNumber = room.roomNumber,
                            roomType = room.roomType,
                            floor = room.floor,
                            status = room.status,
                            basePrice = room.basePrice,
                            capacity = room.capacity,
                            amenities = room.amenities
                        )
                    )
                }
            )
        }


// Room Detail Screen
        composable(
            route = Screen.RoomDetail.route,
            arguments = listOf(
                navArgument("roomId") { type = NavType.IntType },
                navArgument("hotelId") { type = NavType.IntType },
                navArgument("hotelName") { type = NavType.StringType },
                navArgument("availableUnits") { type = NavType.IntType },
                navArgument("pricePerNight") { type = NavType.StringType },
                navArgument("totalNights") { type = NavType.IntType },
                navArgument("checkIn") { type = NavType.StringType },
                navArgument("checkOut") { type = NavType.StringType },
                navArgument("roomNumber") { type = NavType.StringType },
                navArgument("roomType") { type = NavType.StringType },
                navArgument("floor") { type = NavType.IntType },
                navArgument("status") { type = NavType.StringType },
                navArgument("basePrice") { type = NavType.StringType },
                navArgument("capacity") { type = NavType.IntType },
                navArgument("amenities") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val args = backStackEntry.arguments

            val amenitiesString = args?.getString("amenities") ?: ""
            val amenities = if (amenitiesString.isNotEmpty()) {
                URLDecoder.decode(amenitiesString, StandardCharsets.UTF_8.toString())
                    .split(",")
            } else {
                emptyList()
            }

            val room = Room(
                roomId = args?.getInt("roomId") ?: 0,
                hotelId = args?.getInt("hotelId") ?: 0,
                roomNumber = args?.getString("roomNumber")?.takeIf { it != "null" }.toString(),
                roomType = URLDecoder.decode(
                    args?.getString("roomType") ?: "",
                    StandardCharsets.UTF_8.toString()
                ).takeIf { it != "null" }.toString(),
                floor = args?.getInt("floor")?.takeIf { it != 0 }!!,
                status = args?.getString("status")?.takeIf { it != "null" }!!,
                basePrice = args?.getString("basePrice")?.toDoubleOrNull()!!,
                capacity = args?.getInt("capacity")?.takeIf { it != 0 }!!,
                description = null,
                amenities = amenities,
                images = emptyList(),
                primaryImage = null
            )

            val hotelName = URLDecoder.decode(
                args?.getString("hotelName") ?: "",
                StandardCharsets.UTF_8.toString()
            )

            RoomDetailScreen(
//                room = args?.getInt("roomId") ?: 0,
                room = room,
                availableUnits = args?.getInt("availableUnits") ?: 0,
                pricePerNight = args?.getString("pricePerNight")?.toDoubleOrNull() ?: 0.0,
                totalNights = args?.getInt("totalNights") ?: 1,
                checkIn = args?.getString("checkIn") ?: "",
                checkOut = args?.getString("checkOut") ?: "",
                viewModel = roomDetailViewModel,
                onBack = { navController.popBackStack() },
                onBookNow = { room, quantity ->
                    navController.navigate(
                        Screen.Payment.createRoute(
                            roomId = room.roomId,
                            hotelId = room.hotelId,
                            hotelName = hotelName,
                            quantity = quantity,
                            pricePerNight = args?.getString("pricePerNight")?.toDoubleOrNull()
                                ?: 0.0,
                            totalNights = args?.getInt("totalNights") ?: 1,
                            checkIn = args?.getString("checkIn") ?: "",
                            checkOut = args?.getString("checkOut") ?: "",
                            roomNumber = room.roomNumber,
                            roomType = room.roomType,
                            floor = room.floor,
                            status = room.status,
                            basePrice = room.basePrice,
                            capacity = room.capacity
                        )
                    )
                }
            )
        }

        // Payment Screen
        composable(
            route = Screen.Payment.route,
            arguments = listOf(
                navArgument("roomId") { type = NavType.IntType },
                navArgument("hotelId") { type = NavType.IntType },
                navArgument("hotelName") { type = NavType.StringType },
                navArgument("quantity") { type = NavType.IntType },
                navArgument("pricePerNight") { type = NavType.StringType },
                navArgument("totalNights") { type = NavType.IntType },
                navArgument("checkIn") { type = NavType.StringType },
                navArgument("checkOut") { type = NavType.StringType },
                navArgument("roomNumber") { type = NavType.StringType },
                navArgument("roomType") { type = NavType.StringType },
                navArgument("floor") { type = NavType.IntType },
                navArgument("status") { type = NavType.StringType },
                navArgument("basePrice") { type = NavType.StringType },
                navArgument("capacity") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val args = backStackEntry.arguments

            val room = Room(
                roomId = args?.getInt("roomId") ?: 0,
                hotelId = args?.getInt("hotelId") ?: 0,
                roomNumber = args?.getString("roomNumber")?.takeIf { it != "null" }.toString(),
                roomType = URLDecoder.decode(
                    args?.getString("roomType") ?: "",
                    StandardCharsets.UTF_8.toString()
                ).takeIf { it != "null" }.toString(),
                floor = args?.getInt("floor")?.takeIf { it != 0 }!!,
                status = args?.getString("status")?.takeIf { it != "null" }!!,
                basePrice = args?.getString("basePrice")?.toDoubleOrNull()!!,
                capacity = args?.getInt("capacity")?.takeIf { it != 0 }!!,
                description = null,
                amenities = emptyList(),
                images = emptyList(),
                primaryImage = null
            )

            val hotelName = URLDecoder.decode(
                args?.getString("hotelName") ?: "",
                StandardCharsets.UTF_8.toString()
            )

            PaymentScreen(
                room = room,
                quantity = args?.getInt("quantity") ?: 1,
                pricePerNight = args?.getString("pricePerNight")?.toDoubleOrNull() ?: 0.0,
                totalNights = args?.getInt("totalNights") ?: 1,
                checkIn = args?.getString("checkIn") ?: "",
                checkOut = args?.getString("checkOut") ?: "",
                hotelName = hotelName,
                preferencesManager = preferencesManager,
                onBack = { navController.popBackStack() },
                onPaymentSuccess = { bookingId ->
                    // Navigate to booking confirmation or home
                    navController.navigate(Screen.MainUser.route) {
                        popUpTo(Screen.MainUser.route) { inclusive = true }
                    }
                }
            )
        }


        composable(Screen.Booking.route) {
            BookingScreen(
                preferencesManager = preferencesManager,
//                onBack = {
//                    navController.navigate(Screen.Home.route) {
//                        popUpTo(Screen.Home.route) { inclusive = true }
//                    }
//                }
                onBack = {
                    navController.popBackStack()
                }
            )
        }


        composable(Screen.Staff.route) {

//            LaunchedEffect(Unit) {
//                val hotelId = staffDashboardViewModel.staffInfo.value.hotelId
//                if (hotelId > 0) {
//                    staffBookingsViewModel.loadBookings(hotelId)
//                    staffRoomsViewModel.loadRooms(hotelId)
//                }
//            }

//            LaunchedEffect(Unit) {
//                val hotelId = staffDashboardViewModel.profile.value.hotelId
//                if (hotelId > 0) {
//                    staffBookingsViewModel.loadBookings(hotelId)
//                    staffRoomsViewModel.loadRooms(hotelId)
//                    // Init chat nếu chưa có conversations (resume từ background)
//                    val user = preferencesManager.getUser()
//                    if (staffChatViewModel.conversations.value.isEmpty() && user != null) {
//                        val staffInfo = preferencesManager.getStaffInfo()
//                        if (staffInfo != null) {
//                            staffChatViewModel.init(staffInfo.staffId, user.username, staffInfo.hotelId)
//                        }
//                    }
//                }
//            }
            // Khi vào Staff screen: đọc thông tin từ DataStore (đã save lúc login)
            // rồi load dashboard stats, bookings, rooms, conversations
            LaunchedEffect(Unit) {
                val staffLocal = preferencesManager.getStaffInfo()
                val user       = preferencesManager.getUser()

                if (staffLocal != null && user != null) {
                    staffDashboardViewModel.initFromPrefs()

                    staffBookingsViewModel.loadBookings(staffLocal.hotelId)
                    staffRoomsViewModel.loadRooms(staffLocal.hotelId)
                    staffChatViewModel.init(
                        staffId   = user.userId,
                        staffName = user.username,
                        hotelId   = staffLocal.hotelId
                    )
                }
            }

            StaffScreen(
                staffViewModel = staffViewModel,
                dashboardViewModel = staffDashboardViewModel,
                bookingsViewModel  = staffBookingsViewModel,
                roomsViewModel     = staffRoomsViewModel,
                chatViewModel      = staffChatViewModel,
                profileViewModel = profileViewModel,
                initialTab = 0,
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}