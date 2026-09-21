package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.AvailabilityLog
import com.example.data.model.PlacementRequest
import com.example.data.model.Shelter
import com.example.data.model.TransportProvider
import com.example.data.model.TransportRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Shelter::class,
        TransportProvider::class,
        PlacementRequest::class,
        TransportRequest::class,
        AvailabilityLog::class
    ],
    version = 6,
    exportSchema = false
)
abstract class SakshamDatabase : RoomDatabase() {
    abstract fun sakshamDao(): SakshamDao

    companion object {
        @Volatile
        private var INSTANCE: SakshamDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): SakshamDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SakshamDatabase::class.java,
                    "saksham_safety_db"
                ).fallbackToDestructiveMigration(true)
                .addCallback(DatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.sakshamDao())
                    }
                }
            }
        }

        suspend fun populateInitialData(dao: SakshamDao) {
            val now = System.currentTimeMillis()
            val fiveMinAgo = now - (5 * 60 * 1000)
            val twelveMinAgo = now - (12 * 60 * 1000)
            val eightMinAgo = now - (8 * 60 * 1000)
            val twentyEightMinAgo = now - (28 * 60 * 1000)
            val fortyTwoMinAgo = now - (42 * 60 * 1000)
            val fourMinAgo = now - (4 * 60 * 1000)
            val oneDayAgo = now - (24 * 60 * 60 * 1000)

            val initialShelters = listOf(
                // 1. Shelter Alpha - Approved, Verified, Available (Section 20)
                Shelter(
                    id = 1,
                    name = "Saksham Shelter Alpha",
                    area = "Central District",
                    availableBeds = 8,
                    totalBeds = 20,
                    acceptsChildren = true,
                    acceptsWheelchair = true,
                    status = "OPEN",
                    verificationStatus = "approved",
                    verified = true,
                    availabilityStatus = "available",
                    travelTimeMinutes = 18,
                    lastUpdated = fiveMinAgo,
                    createdAt = oneDayAgo,
                    features = "Private family units, Trauma support, 24/7 Security, Ramped entry",
                    staffContact = "Alpha Duty Lead (Extension 101)"
                ),
                // 2. Shelter Beta - Pending, Unverified, Available (Section 20)
                Shelter(
                    id = 2,
                    name = "Saksham Shelter Beta",
                    area = "Downtown Sector",
                    availableBeds = 10,
                    totalBeds = 15,
                    acceptsChildren = true,
                    acceptsWheelchair = true,
                    status = "OPEN",
                    verificationStatus = "pending",
                    verified = false,
                    availabilityStatus = "available",
                    travelTimeMinutes = 20,
                    lastUpdated = twelveMinAgo,
                    createdAt = now - (2 * 60 * 60 * 1000),
                    features = "Accessible family suites, Security guards on premise",
                    staffContact = "Beta Coordinator"
                ),
                // 3. Shelter Gamma - Rejected, Unverified, Available (Section 20)
                Shelter(
                    id = 3,
                    name = "Saksham Shelter Gamma",
                    area = "Industrial Corridor",
                    availableBeds = 12,
                    totalBeds = 18,
                    acceptsChildren = true,
                    acceptsWheelchair = false,
                    status = "OPEN",
                    verificationStatus = "rejected",
                    verified = false,
                    availabilityStatus = "available",
                    rejectionReason = "Verification information incomplete",
                    travelTimeMinutes = 35,
                    lastUpdated = eightMinAgo,
                    createdAt = oneDayAgo,
                    features = "Shared dormitories, Basic safety measures",
                    staffContact = "Gamma Supervisor"
                ),
                // 4. Shelter Delta - Approved, Verified, Available (Section 20)
                Shelter(
                    id = 4,
                    name = "Saksham Shelter Delta",
                    area = "South District",
                    availableBeds = 3,
                    totalBeds = 8,
                    acceptsChildren = false,
                    acceptsWheelchair = false,
                    status = "OPEN",
                    verificationStatus = "approved",
                    verified = true,
                    availabilityStatus = "available",
                    travelTimeMinutes = 14,
                    lastUpdated = fourMinAgo,
                    createdAt = oneDayAgo,
                    features = "Adult-only individual rooms, Trauma-informed staff",
                    staffContact = "Delta Operations Desk"
                ),
                // 5. Aashray Safe Harbor - Approved
                Shelter(
                    id = 5,
                    name = "Aashray Safe Harbor",
                    area = "North District",
                    availableBeds = 6,
                    totalBeds = 15,
                    acceptsChildren = true,
                    acceptsWheelchair = false,
                    status = "OPEN",
                    verificationStatus = "approved",
                    verified = true,
                    availabilityStatus = "available",
                    travelTimeMinutes = 22,
                    lastUpdated = twelveMinAgo,
                    createdAt = oneDayAgo,
                    features = "Mother & child nursery, On-site nurse, Enclosed courtyard",
                    staffContact = "North Harbor Reception"
                ),
                // 6. Grace Sanctuary Care - Approved
                Shelter(
                    id = 6,
                    name = "Grace Sanctuary Care",
                    area = "West District",
                    availableBeds = 2,
                    totalBeds = 8,
                    acceptsChildren = false,
                    acceptsWheelchair = true,
                    status = "OPEN",
                    verificationStatus = "approved",
                    verified = true,
                    availabilityStatus = "available",
                    travelTimeMinutes = 14,
                    lastUpdated = eightMinAgo,
                    createdAt = oneDayAgo,
                    features = "Step-free accessible suites, Specialized medical aid, Quiet zone",
                    staffContact = "West Sanctuary Supervisor"
                ),
                // 7. Metro Oasis Shelter - Approved
                Shelter(
                    id = 7,
                    name = "Metro Oasis Shelter",
                    area = "South Suburbs",
                    availableBeds = 5,
                    totalBeds = 20,
                    acceptsChildren = true,
                    acceptsWheelchair = true,
                    status = "OPEN",
                    verificationStatus = "approved",
                    verified = true,
                    availabilityStatus = "available",
                    travelTimeMinutes = 25,
                    lastUpdated = twentyEightMinAgo,
                    createdAt = oneDayAgo,
                    features = "Full accessibility ramps, Child play zone, Legal guidance desk",
                    staffContact = "Metro Duty Coordinator"
                ),
                // 8. Sunrise Haven Residence - Approved but Full / Unavailable
                Shelter(
                    id = 8,
                    name = "Sunrise Haven Residence",
                    area = "East Sector",
                    availableBeds = 0,
                    totalBeds = 10,
                    acceptsChildren = true,
                    acceptsWheelchair = true,
                    status = "FULL",
                    verificationStatus = "approved",
                    verified = true,
                    availabilityStatus = "unavailable",
                    travelTimeMinutes = 30,
                    lastUpdated = fortyTwoMinAgo,
                    createdAt = oneDayAgo,
                    features = "Family suites, Accessible bathrooms, Emergency pantry",
                    staffContact = "East Sector Lead"
                ),
                // 9. Peace Path Safe Home - Approved
                Shelter(
                    id = 9,
                    name = "Peace Path Safe Home",
                    area = "Midtown Central",
                    availableBeds = 3,
                    totalBeds = 6,
                    acceptsChildren = false,
                    acceptsWheelchair = false,
                    status = "OPEN",
                    verificationStatus = "approved",
                    verified = true,
                    availabilityStatus = "available",
                    travelTimeMinutes = 15,
                    lastUpdated = fourMinAgo,
                    createdAt = oneDayAgo,
                    features = "Rapid single placement, Confidential entrance, Clothing aid",
                    staffContact = "Midtown Desk"
                ),
                // 10. Hope House East - Pending
                Shelter(
                    id = 10,
                    name = "Hope House East",
                    area = "East District",
                    availableBeds = 5,
                    totalBeds = 12,
                    acceptsChildren = true,
                    acceptsWheelchair = false,
                    status = "OPEN",
                    verificationStatus = "pending",
                    verified = false,
                    availabilityStatus = "available",
                    travelTimeMinutes = 26,
                    lastUpdated = twentyEightMinAgo,
                    createdAt = now - (5 * 60 * 60 * 1000),
                    features = "Supportive family lodging, Communal kitchen",
                    staffContact = "East Intake Desk"
                ),
                // 11. Navjeevan Safe Refuge - Pending
                Shelter(
                    id = 11,
                    name = "Navjeevan Safe Refuge",
                    area = "Riverside Outer",
                    availableBeds = 8,
                    totalBeds = 16,
                    acceptsChildren = false,
                    acceptsWheelchair = true,
                    status = "OPEN",
                    verificationStatus = "pending",
                    verified = false,
                    availabilityStatus = "available",
                    travelTimeMinutes = 32,
                    lastUpdated = twelveMinAgo,
                    createdAt = now - (8 * 60 * 60 * 1000),
                    features = "Accessible recovery center, Trauma specialists",
                    staffContact = "Riverside Staff"
                ),
                // 12. City Center Care Home - Approved
                Shelter(
                    id = 12,
                    name = "City Center Care Home",
                    area = "Civil Lines",
                    availableBeds = 4,
                    totalBeds = 10,
                    acceptsChildren = true,
                    acceptsWheelchair = true,
                    status = "OPEN",
                    verificationStatus = "approved",
                    verified = true,
                    availabilityStatus = "available",
                    travelTimeMinutes = 16,
                    lastUpdated = fiveMinAgo,
                    createdAt = oneDayAgo,
                    features = "Accessible central shelter, Emergency medical kit",
                    staffContact = "Civil Lines Desk"
                )
            )
            dao.insertShelters(initialShelters)

            val initialProviders = listOf(
                TransportProvider(
                    id = 1,
                    name = "Auto 01",
                    vehicleType = "Auto",
                    wheelchairAccessible = false,
                    passengerCapacity = 2,
                    available = true,
                    status = "Available",
                    driverName = "Mohan Das",
                    vehiclePlate = "DL-02-XY-9014",
                    etaMinutes = 10
                ),
                TransportProvider(
                    id = 2,
                    name = "Vehicle A",
                    vehicleType = "Sedan",
                    wheelchairAccessible = false,
                    passengerCapacity = 4,
                    available = true,
                    status = "Available",
                    driverName = "Anita Patel",
                    vehiclePlate = "DL-01-BK-3390",
                    etaMinutes = 10
                ),
                TransportProvider(
                    id = 3,
                    name = "Van 01 (Accessible)",
                    vehicleType = "Van",
                    wheelchairAccessible = true,
                    passengerCapacity = 4,
                    available = true,
                    status = "Available",
                    driverName = "Ramesh Kumar",
                    vehiclePlate = "DL-04-AR-8821",
                    etaMinutes = 15
                ),
                TransportProvider(
                    id = 4,
                    name = "Vehicle C",
                    vehicleType = "Mini Van",
                    wheelchairAccessible = true,
                    passengerCapacity = 5,
                    available = false,
                    status = "Unavailable",
                    driverName = "Vikram Singh",
                    vehiclePlate = "DL-08-WZ-5512",
                    etaMinutes = 15
                ),
                TransportProvider(
                    id = 5,
                    name = "Vehicle B",
                    vehicleType = "Van",
                    wheelchairAccessible = true,
                    passengerCapacity = 7,
                    available = true,
                    status = "Available",
                    driverName = "Suresh Sharma",
                    vehiclePlate = "DL-05-SR-1100",
                    etaMinutes = 20
                )
            )
            dao.insertTransportProviders(initialProviders)

            initialShelters.forEach { s ->
                dao.insertAvailabilityLog(
                    AvailabilityLog(
                        shelterId = s.id,
                        shelterName = s.name,
                        availableBeds = s.availableBeds,
                        updatedAt = s.lastUpdated
                    )
                )
            }
        }
    }
}
