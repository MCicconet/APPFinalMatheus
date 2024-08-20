package com.example.myapplication

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.SimpleAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.registerForActivityResult
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private  var btPermission = false
    private lateinit var binding: ActivityMainBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        enableEdgeToEdge()
        setContentView(binding.root)





        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun scanBt(view: View){
        //tirado do próprio site dos developers do android studio
        val bluetoothManager: BluetoothManager = getSystemService(BluetoothManager::class.java)
        val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter
        if (bluetoothAdapter == null) {
            // Não suporta bluetooth
        }else{
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
                blueToothPermissionLauncher.launch(android.Manifest.permission.BLUETOOTH_CONNECT)

            }else{
                blueToothPermissionLauncher.launch(android.Manifest.permission.BLUETOOTH_ADMIN)
            }
        }

    }
    private val blueToothPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()
    ){ isGranted: Boolean ->
        if (isGranted) {
            val bluetoothManager: BluetoothManager = getSystemService(BluetoothManager::class.java)
            val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter
            btPermission = true
            if (bluetoothAdapter?.isEnabled == false) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                btActivityResultLauncher.launch(enableBtIntent)
            } else {
                scanBT()
            }

        }
    }

    private val btActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()
    ){ result: ActivityResult ->
        if(result.resultCode == RESULT_OK){
            scanBT()
        }

    }


    @SuppressLint("MissingPermission")
    private fun scanBT(){
        val bluetoothManager: BluetoothManager = getSystemService(BluetoothManager::class.java)
        val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter
        val builder = AlertDialog.Builder(this@MainActivity)
        val inflater = layoutInflater
        val dialogView:View = inflater.inflate(R.layout.scan_bt,null)
        builder.setCancelable(false)
        builder.setView(dialogView)
        val btlst =dialogView.findViewById<ListView>(R.id.bt_lst)
        val dialog = builder.create()
        val pairedDevices:Set<BluetoothDevice> = bluetoothAdapter?.bondedDevices as Set<BluetoothDevice>
        val ADAhere:SimpleAdapter
        var data: MutableList<Map<String?, Any?>?>? = null
        data = ArrayList()
        if(pairedDevices.isNotEmpty()){
            val datanum1: MutableMap<String?, Any?> = HashMap()
            datanum1["A"] = ""
            datanum1["B"] = ""
            data.add(datanum1)
            for(device in pairedDevices){
                val datanum:MutableMap<String?, Any?> = HashMap()
                datanum["A"] = device.name
                datanum["B"] = device.address
                data.add(datanum)

            }
            val fromwhere = arrayOf("A")
            val viewswhere = intArrayOf(R.id.btname)
            ADAhere = SimpleAdapter(this@MainActivity,data,R.layout.lista,fromwhere,viewswhere)
            btlst.adapter = ADAhere
            ADAhere.notifyDataSetChanged()
            btlst.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, l ->
                val string = ADAhere.getItem(position) as HashMap<String,String>
                    val deviceName = string["A"]
                    binding.DispositivosBlue.text = deviceName
                    dialog.dismiss()

            }


        }else{
            val value = "Nenhum dispositivo encontrado"
            Toast.makeText(this, value, Toast.LENGTH_LONG).show()
            return
        }
        dialog.show()


    }

}