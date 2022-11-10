package com.example.phoneaddress

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.phoneaddress.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //1. 이 앱에서 전화번호부 접근권한 퍼미션 허용했는지 확인
        val status = ContextCompat.checkSelfPermission(this, "android.permission.READ_CONTACTS")

        if (status == PackageManager.PERMISSION_GRANTED) {
            Log.d("phoneaddress", "READ_CONTACTS승인")
        } else {
            //퍼미션 요청 다이얼로그
            Log.d("phoneaddress", "READ_CONTACTS 불승인")
            ActivityCompat.requestPermissions(
                this,
                arrayOf<String>("android.permission.READ_CONTACTS"),
                100
            )
        }
        //3.전화번호부 앱에 사용자 전화번호부 정보를 인텐트 요청하면 해당되는 데이터(1사람)를 보내줄때 콜백함수(작동하는 함수)
        val requestLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == RESULT_OK) {
                    binding.tvData.text = it.data?.data.toString()


                    //uri를 알았으니까 쿼리문으로 정보 요청
                    val cursor =contentResolver.query(
                        it.data!!.data!!,
                        arrayOf<String>(
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                            ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ),
                        null, null, null
                    )

                    if (cursor!!.moveToFirst()) {
                        val name = cursor.getString(0)
                        val phone = cursor.getString(1)
                        Log.d("phoneaddress", "${it.data?.data.toString()}")
                        binding.tvData.text = "${name} , ${phone}"
                    }
                }
            }
        //4.전화번호부에 요청
        binding.btnCallAddress.setOnClickListener {
            val intent =
                Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
            requestLauncher.launch(intent)
        }
        //5.전화번호부에 모든 사람의 정보를 요청
        binding.btnAllAddress.setOnClickListener{
            val cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                arrayOf<String>(ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER),
                null,
                null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                 )
            while(cursor?.moveToNext()?:false){
                val id = cursor?.getString(0)
                val name = cursor?.getString(1)
                val phone = cursor?.getString(2)
                Log.d("phoneaddress", "${id}, ${name}, ${phone}")
            }
        }
    }

    //2.퍼미션 요청 다이얼로그
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d("phoneaddress", "READ_CONTACTS 승인")
        } else {
            Log.d("phoneaddress", "READ_CONTACTS 불승인")
        }
    }
}