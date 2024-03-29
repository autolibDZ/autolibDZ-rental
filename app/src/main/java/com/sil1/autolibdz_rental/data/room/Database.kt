package com.sil1.autolibdz_rental.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sil1.autolibdz_rental.data.model.Converter
import com.sil1.autolibdz_rental.data.model.Reservation
import com.sil1.autolibdz_rental.data.model.ReservationDao

//cette classe représentre notre base de données
@Database(entities = arrayOf(Reservation::class),version=1)
@TypeConverters(Converter::class)
abstract class Database: RoomDatabase() {
    abstract fun getReservationDao():ReservationDao


}