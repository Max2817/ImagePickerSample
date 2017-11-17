/*
 * Copyright (c) 2017, Samsao Development Inc. All rights reserved.
 */
package co.samsao.imagepicker

import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat

object Permissions {

    fun hasPermission(context: Context, permissionType: String): Boolean =
            ContextCompat.checkSelfPermission(context, permissionType) == PackageManager.PERMISSION_GRANTED
}
