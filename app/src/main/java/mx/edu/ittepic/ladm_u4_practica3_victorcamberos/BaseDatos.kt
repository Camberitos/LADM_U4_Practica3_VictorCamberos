package mx.edu.ittepic.ladm_u4_practica3_victorcamberos

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BaseDatos(
    context: Context?,
    name: String?,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
) : SQLiteOpenHelper(context, name, factory, version) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE ENTRANTES(CELULARES VARCHAR(200),MENSAJE VARCHAR(2000))")
        db.execSQL("CREATE TABLE CALIFICACIONES(NO_CONTROL VARCHAR(100),U1 VARCHAR(20),U2 VARCHAR(20),U3 VARCHAR(20),U4 VARCHAR(20))")
    }



    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}