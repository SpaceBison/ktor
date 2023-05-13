/*
 * Copyright 2014-2023 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package io.ktor.network.util

import kotlinx.cinterop.*
import platform.posix.*

internal class NativeMulticastGroup(
    val multicastAddress: in_addr_t,
    val networkInterface: in_addr_t
) {
    fun nativeAddress(block: (address: CPointer<ip_mreq>, size: socklen_t) -> Unit) {
        cValue<ip_mreq> {
            imr_multiaddr.s_addr = multicastAddress
            imr_interface.s_addr = networkInterface

            block(ptr.reinterpret(), sizeOf<ip_mreq>().convert())
        }
    }
}
