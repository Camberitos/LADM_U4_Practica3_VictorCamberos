package mx.edu.ittepic.ladm_u4_practica3_victorcamberos


import android.content.pm.PackageManager
import android.database.sqlite.SQLiteException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val siPermiso = 1
    val siPermisoReciver = 2
    var siPermisoLectura = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //Permisos mayores a la M se requiere código Kt y no solo el permiso declarado en Manifest
        ActivityCompat.requestPermissions(this,
            arrayOf(android.Manifest.permission.SEND_SMS), siPermiso)//el tercer parámetro es el valor que se otorga si se da el permiso (1)
        permisos.setOnClickListener(){
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.RECEIVE_SMS), siPermisoReciver)
            Toast.makeText(this,"Permisos otorgados",Toast.LENGTH_SHORT).show()
        }

        leerAlumnos()
        insertarAlumnos()
    }
    //En unidad uno al entrar por primera vez se preguntaba por el permiso, como se define en el if de arriba, pero si ya tenía permiso no pasaba nada
    //hasta la segunda vez ya se disparaba un mensaje de que el permiso ya se tenía, esto se corrige en el código de abajo, ya que indirectamente,
    //si ya tiene permisos la aplicación se dispara el mensaje desde al primera vez
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == siPermisoReciver){
            //mensajeRecibir()
        }
        if(requestCode == siPermisoLectura){
            leerAlumnos()
        }

    }

    private fun insertarAlumnos(){
        button.setOnClickListener(){
            if(editText.text.toString() != "" && editText2.text.toString() != "" && editText3.text.toString() != "" &&
                editText4.text.toString() != "" && editText5.text.toString() != ""){
                try{
                    var baseDatos = BaseDatos(this,"calificaciones",null,1)
                    var insertar  = baseDatos.writableDatabase
                    var SQL = "INSERT INTO CALIFICACIONES VALUES ('${editText.text}','${editText2.text}','${editText3.text}','${editText4.text}','${editText5.text}')"
                    insertar.execSQL(SQL)
                    baseDatos.close()
                    Toast.makeText(this,"Alumno insertado",Toast.LENGTH_SHORT)
                        .show()
                }catch (err: SQLiteException){
                    Toast.makeText(this,err.message,Toast.LENGTH_LONG)
                        .show()
                }
                leerAlumnos()
            }else{
                Toast.makeText(this,"Datos incompletos",Toast.LENGTH_SHORT).show()
            }

            editText.setText("")
            editText2.setText("")
            editText3.setText("")
            editText4.setText("")
            editText5.setText("")
        }
    }
    private fun envioSMS(){
        SmsManager.getDefault().sendTextMessage(editText.text.toString(),null,
            editText2.text.toString(),null,null)
        Toast.makeText(this,"Se envió el sms",Toast.LENGTH_LONG)
            .show()
    }

    private fun leerAlumnos(){
        try{
            var cursor = BaseDatos(this,"calificaciones",null,1)
                .readableDatabase
                .rawQuery("SELECT * FROM CALIFICACIONES",null)

            var ultimo = ""
            if(cursor.moveToFirst()){
                do{
                    ultimo += "CALIFICACIONES:\n"+
                            "No_Control: "+ cursor.getString(0)+
                            "\nUnidad 1: "+cursor.getString(1)+
                            "\nUnidad 2: "+cursor.getString(2)+
                            "\nUnidad 3: "+cursor.getString(3)+
                            "\nUnidad 4: "+cursor.getString(4)+
                            "\n--------------------------\n"
                }while(cursor.moveToNext())
            }else{
                ultimo = "Sin calificaciones registradas aún, Tabla vacía"
            }
            textView2.setText(ultimo)
        }catch (err: SQLiteException){
            Toast.makeText(this,err.message,Toast.LENGTH_LONG)
                .show()
        }
    }
}
