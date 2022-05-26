package com.example.workethic.Pojo

import android.content.Context
import android.os.Build
import android.os.Build.VERSION_CODES.Q
import com.vmadalin.easypermissions.EasyPermissions

object HasPermissions {
   fun hasPermissions(context: Context) =
         if (Build.VERSION.SDK_INT < Q) {
            EasyPermissions.hasPermissions(
                context,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else {
            EasyPermissions.hasPermissions(
                context,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_BACKGROUND_LOCATION

            )
        }


}