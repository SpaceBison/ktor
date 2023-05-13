/*
 * Copyright 2014-2023 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package io.ktor.network

import platform.posix.*

public actual data class NetworkInterface(
    public val networkInterfaceAddress: in_addr
)
