package com.example.myapplication

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.ActivityPhoneBookRecyclerViewBinding

class PhoneBookRecyclerView : AppCompatActivity() {
    lateinit var binding: ActivityPhoneBookRecyclerViewBinding
    val dataList = mutableListOf<DataVO>()
    lateinit var customAdapter: CustomAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhoneBookRecyclerViewBinding.inflate(layoutInflater)
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
            dataList.add(DataVO(id!!, name!!, phone!!))
        }


        val layoutManager: LinearLayoutManager = selectLayoutManager(1)
        customAdapter = CustomAdapter(dataList)
        this.customAdapter = customAdapter
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = customAdapter
        binding.recyclerView.setHasFixedSize(true)

    }

    private fun selectLayoutManager(i: Int): LinearLayoutManager {
        return LinearLayoutManager(this)
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