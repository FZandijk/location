package com.example.locatie_test

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private val MY_PERMISSION_FINE_LOCATION = 101
    private var locationRequest: LocationRequest? = null
    private var updatesOn = false;
    private var locationCallback: LocationCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        locationRequest = LocationRequest()
        locationRequest!!.interval = 7500 // use 10 to 15 seconds for a real app
        locationRequest!!.fastestInterval = 5000
        locationRequest!!.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY

        tbGps_Balanced.setOnClickListener {
            if (tbGps_Balanced.isChecked) {
                //using GPS only
                tvSensor.text = "GPS"
                locationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            } else {
                //using balanced power accuracy
                tvSensor.text = "Cell Tower and WiFi"
                locationRequest!!.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
            }
        }

        tbLocationOnOff.setOnClickListener {
            if (tbLocationOnOff.isChecked) {
                //location update on
                tvUpdates.text = "On"
                updatesOn = true
                startLocationUpdates()

            } else {
                //location update off
                tvUpdates.text = "Off"
                updatesOn = false
                stopLocationUpdates()
            }
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient!!.lastLocation.addOnSuccessListener { location ->

                if (location != null) {
                    //update UI
                    tvLatitude.text = location.latitude.toString()
                    tvLongitude.text = location.longitude.toString()
                    if (location.hasAccuracy()) {
                        tvAccuracy.text = location.accuracy.toString()
                    } else {
                        tvAccuracy.text = "No Accuracy Available"
                    }
                    if (location.hasAltitude()) {
                        tvAltitude.text = location.altitude.toString()
                    } else {
                        tvAltitude.text = "No Altitude Available"
                    }
                    if (location.hasSpeed()) {
                        tvSpeed.text = location.speed.toString()
                    } else {
                        tvSpeed.text = "No Speed Available"
                    }
                }

            }
        } else {
            //request permissions
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), MY_PERMISSION_FINE_LOCATION)
            }
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {
                super.onLocationResult(p0)
                for (location in p0!!.locations) {
                    //update UI
                    if (location != null) {
                        //update UI
                        tvLatitude.text = location.latitude.toString()
                        tvLongitude.text = location.longitude.toString()
                        if (location.hasAccuracy()) {
                            tvAccuracy.text = location.accuracy.toString()
                        } else {
                            tvAccuracy.text = "No Accuracy Available"
                        }
                        if (location.hasAltitude()) {
                            tvAltitude.text = location.altitude.toString()
                        } else {
                            tvAltitude.text = "No Altitude Available"
                        }
                        if (location.hasSpeed()) {
                            tvSpeed.text = location.speed.toString()
                        } else {
                            tvSpeed.text = "No Speed Available"
                        }
                    }
                }
            }
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSION_FINE_LOCATION ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission granted. No need to do anything
                } else {
                    Toast.makeText(applicationContext, "This app requires location permissions to be granted", Toast.LENGTH_SHORT).show()
                    finish()
                }
        }
    }

    override fun onResume() {
        super.onResume()
        if (updatesOn) startLocationUpdates()
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient!!.requestLocationUpdates(locationRequest, locationCallback, null)
        } else {
            //request permissions
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), MY_PERMISSION_FINE_LOCATION)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    private fun stopLocationUpdates() {
        fusedLocationProviderClient!!.removeLocationUpdates(locationCallback)
    }
}
