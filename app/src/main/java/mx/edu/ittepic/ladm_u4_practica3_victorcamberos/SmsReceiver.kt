package mx.edu.ittepic.ladm_u4_practica3_victorcamberos

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteException
import android.os.Build
import android.telephony.SmsManager
import android.telephony.SmsMessage
import android.widget.Toast
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.util.ArrayList

/*
    RECEIVER ES UNA ESPECIE DE EVENTO U OYENTE DE ANDROID QUE PERMITE LA LECTURA DE EVENTOS DEL SISTEMA OPERATIVO
*/

class SmsReceiver : BroadcastReceiver(){
    var numero = ""
    var numero2 = ""
    var mensaje = "Error - Sintaxis: CALIFICACION U# NO_CTRL"
    var unidad = ""
    var control = ""

    override fun onReceive(context: Context, intent: Intent) {
        val extras = intent.extras
        if (extras != null){
            var sms = extras.get("pdus") as Array<Any>

            for(indice in sms.indices){
                var formato = extras.getString("format")
                var smsMensaje = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { //ver qué versión de SMS manejo yo en mi celular
                    SmsMessage.createFromPdu(sms[indice] as  ByteArray,formato)
                }else{
                    SmsMessage.createFromPdu(sms[indice] as ByteArray)
                }

                var celularOrigen = smsMensaje.originatingAddress
                var contenidoSMS = smsMensaje.messageBody.toString()

                numero2 = celularOrigen + "h"
                ciclo()
                //DIVIDIR MENSAJE Y VALIDAR SU CORRECTA REDACCIÓN
                var arreglo = contenidoSMS.split(" ")
                if(arreglo.size>2) {
                    if (arreglo[0] == "CALIFICACION") {
                        control = arreglo[2]
                        unidad = arreglo[1]
                        //RECUPERAR INFORMACIÓN DE BD PARA MANDAR MENSAJE CORRECTO
                        try{
                            var cursor = BaseDatos(context,"calificaciones",null,1)
                                .readableDatabase
                                .rawQuery("SELECT ${unidad} FROM CALIFICACIONES WHERE NO_CONTROL = $control",null)

                            var ultimo = ""
                            if(cursor.moveToFirst()){
                                do{
                                    ultimo = "Calificación "+unidad+": "+ cursor.getString(0)
                                    mensaje = ultimo
                                }while(cursor.moveToNext())
                            }else{
                                ultimo = "Error - Sintaxis: CALIFICACION U# NO_CTRL"
                                mensaje = ultimo
                            }
                        }catch (err: SQLiteException){
                            Toast.makeText(context,err.message,Toast.LENGTH_LONG)
                                .show()
                        }

                    }else{
                        mensaje = "Error - Sintaxis: CALIFICACION U# NO_CTRL"
                    }
                }else{
                    mensaje = "Error - Sintaxis: CALIFICACION U# NO_CTRL"
                }

                //GUARDAR SOBRE TABLA SQLITE
                try{
                    var baseDatos = BaseDatos(context,"entrantes",null,1)
                    var insertar  = baseDatos.writableDatabase
                    var SQL = "INSERT INTO ENTRANTES VALUES ('${celularOrigen}','${contenidoSMS}')"
                    insertar.execSQL(SQL)
                    baseDatos.close()

                }catch (err: SQLiteException){
                    Toast.makeText(context,err.message,Toast.LENGTH_LONG)
                        .show()
                }
            }
            SmsManager.getDefault().sendTextMessage(numero,null,
                mensaje,null,null)
        }

    }
    fun ciclo(){
        var a=0
        var arrayList = ArrayList<String>()
        arrayList = numero2.split("") as ArrayList<String>
        a=0
        while (a<(arrayList.size)-2){
            numero += arrayList.get(a)
            a += 1
        }
    }
}