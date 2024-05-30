package com.example.modul11;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

  private TextView latitude, longitude, altitude, akurasi, address;
  private Button btnFind;
  private FusedLocationProviderClient locationProviderClient;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    latitude = findViewById(R.id.latitude);
    longitude = findViewById(R.id.longitude);
    altitude = findViewById(R.id.altitude);
    akurasi = findViewById(R.id.akurasi);
    btnFind = findViewById(R.id.btn_find);
    address = findViewById(R.id.address);

    locationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
    btnFind.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        getLocation();
      }
    });
  }

  private void getLocation() {
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        requestPermissions(new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        }, 10);
      }
    } else {
      locationProviderClient.getLastLocation().addOnSuccessListener(location -> {
        if (location != null) {
          latitude.setText(String.valueOf(location.getLatitude()));
          longitude.setText(String.valueOf(location.getLongitude()));
          altitude.setText(String.valueOf(location.getAltitude()));
          akurasi.setText(location.getAccuracy() + " %");

          Geocoder geocoder = new Geocoder(this, Locale.getDefault());
          try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && !addresses.isEmpty()) {
              Address returnedAddress = addresses.get(0);
              StringBuilder strReturnedAddress = new StringBuilder("");
              for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
              }
              address.setText(strReturnedAddress.toString());
            } else {
              address.setText("Alamat tidak ditemukan");
            }
          } catch (Exception e) {
            e.printStackTrace();
            address.setText("Tidak bisa mendapatkan Alamat");
          }
        } else {
          Toast.makeText(getApplicationContext(), "Lokasi tidak aktif!", Toast.LENGTH_SHORT).show();
        }
      }).addOnFailureListener(e ->
          Toast.makeText(getApplicationContext(),
              e.getLocalizedMessage(), Toast.LENGTH_SHORT).show());
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == 10) {
      if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        getLocation();
      } else {
        Toast.makeText(getApplicationContext(), "Izin lokasi tidak diaktifkan!", Toast.LENGTH_SHORT).show();
      }
    }
  }
}
