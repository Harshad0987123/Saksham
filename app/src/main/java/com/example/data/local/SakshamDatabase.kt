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
    version = 2,
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
                ).fallbackToDestructiveMigration()
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

            val initialShelters = listOf(
                Shelter(
                    id = 1,
                    name = "Saksham Shelter Alpha",
                    area = "Central District",
                    availableBeds = 4,
                    totalBeds = 12,
                    acceptsChildren = true,
                    acceptsWheelchair = true,
                    status = "OPEN",
                    travelTimeMinutes = 18,
                    lastUpdated = fiveMinAgo,
                    features = "Private family units, Trauma support, 24/7 Security, Ramped entry",
                    staffContact = "Alpha Duty Lead (Extension 101)"
                ),
                Shelter(
                    id = 2,
                    name = "Aashray Safe Harbor",
                    area = "North District",
                    availableBeds = 6,
                    totalBeds = 15,
                    acceptsChildren = true,
                    acceptsWheelchair = false,
                    status = "OPEN",
                    travelTimeMinutes = 22,
                    lastUpdated = twelveMinAgo,
                    features = "Mother & child nursery, On-site nurse, Enclosed courtyard",
                    staffContact = "North Harbor Reception"
                ),
                Shelter(
                    id = 3,
                    name = "Grace Sanctuary Care",
                    area = "West District",
                    availableBeds = 2,
                    totalBeds = 8,
                    acceptsChildren = false,
                    acceptsWheelchair = true,
                    status = "OPEN",
                    travelTimeMinutes = 14,
                    lastUpdated = eightMinAgo,
                    features = "Step-free accessible suites, Specialized medical aid, Quiet zone",
                    staffContact = "West Sanctuary Supervisor"
                ),
                Shelter(
                    id = 4,
                    name = "Metro Oasis Shelter",
                    area = "South Suburbs",
                    availableBeds = 5,
                    totalBeds = 20,
                    acceptsChildren = true,
                    acceptsWheelchair = true,
                    status = "OPEN",
                    travelTimeMinutes = 25,
                    lastUpdated = twentyEightMinAgo,
                    features = "Full accessibility ramps, Child play zone, Legal guidance desk",
                    staffContact = "Metro Duty Coordinator"
                ),
                Shelter(
                    id = 5,
                    name = "Sunrise Haven Residence",
                    area = "East Sector",
                    availableBeds = 0,
                    totalBeds = 10,
                    acceptsChildren = true,
                    acceptsWheelchair = true,
                    status = "FULL",
                    travelTimeMinutes = 30,
                    lastUpdated = fortyTwoMinAgo,
                    features = "Family suites, Accessible bathrooms, Emergency pantry",
                    staffContact = "East Sector Lead"
                ),
                Shelter(
                    id = 6,
                    name = "Peace Path Safe Home",
                    area = "Midtown Central",
                    availableBeds = 3,
                    totalBeds = 6,
                    acceptsChildren = false,
                    acceptsWheelchair = false,
                    status = "OPEN",
                    travelTimeMinutes = 15,
                    lastUpdated = fourMinAgo,
                    features = "Rapid single placement, Confidential entrance, Clothing aid",
                    staffContact = "Midtown Desk"
                )
            )
            dao.insertShelters(initialShelters)

            val initialProviders = listOf(
                TransportProvider(
                    id = 1,
                    name = "SafeRide Accessible Van",
                    vehicleType = "Van",
                    wheelchairAccessible = true,
                    passengerCapacity = 7,
                    available = true,
                    status = "Available",
                    driverName = "Ramesh Kumar",
                    vehiclePlate = "DL-04-AR-8821",
                    etaMinutes = 15
                ),
                TransportProvider(
                    id = 2,
                    name = "SwiftCare Transit Car",
                    vehicleType = "Car",
                    wheelchairAccessible = false,
                    passengerCapacity = 4,
                    available = true,
                    status = "Available",
                    driverName = "Anita Patel",
                    vehiclePlate = "DL-01-BK-3390",
                    etaMinutes = 8
                ),
                TransportProvider(
                    id = 3,
                    name = "HopeShuttle Large Accessible Van",
                    vehicleType = "Large Van",
                    wheelchairAccessible = true,
                    passengerCapacity = 10,
                    available = true,
                    status = "Available",
                    driverName = "Vikram Singh",
                    vehiclePlate = "DL-08-WZ-5512",
                    etaMinutes = 15
                ),
                TransportProvider(
                    id = 4,
                    name = "City QuickRelief Compact",
                    vehicleType = "Car",
                    wheelchairAccessible = false,
                    passengerCapacity = 3,
                    available = true,
                    status = "Available",
                    driverName = "Mohan Das",
                    vehiclePlate = "DL-02-XY-9014",
                    etaMinutes = 10
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
