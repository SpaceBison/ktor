/*
 * Copyright 2014-2019 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package io.ktor.network.sockets

import io.ktor.network.NetworkInterface
import io.ktor.network.selector.*
import io.ktor.network.util.*
import kotlinx.cinterop.*
import platform.posix.*

@OptIn(UnsafeNumber::class)
internal actual fun UDPSocketBuilder.Companion.connectUDP(
    selector: SelectorManager,
    remoteAddress: SocketAddress,
    localAddress: SocketAddress?,
    options: SocketOptions.UDPSocketOptions
): ConnectedDatagramSocket {
    val address = localAddress?.address ?: getAnyLocalAddress()
    val descriptor = socket(address.family.convert(), SOCK_DGRAM, 0).check()

    assignOptions(descriptor, options)
    nonBlocking(descriptor)

    address.nativeAddress { pointer, size ->
        bind(descriptor, pointer, size).check()
    }

    remoteAddress.address.nativeAddress { pointer, size ->
        connect(descriptor, pointer, size).check()
    }

    return DatagramSocketNative(
        descriptor = descriptor,
        selector = selector,
        remote = remoteAddress,
        parent = selector.coroutineContext
    )
}

@OptIn(UnsafeNumber::class)
internal actual fun UDPSocketBuilder.Companion.bindUDP(
    selector: SelectorManager,
    localAddress: SocketAddress?,
    options: SocketOptions.UDPSocketOptions
): BoundDatagramSocket {
    val address = localAddress?.address ?: getAnyLocalAddress()
    val descriptor = socket(address.family.convert(), SOCK_DGRAM, 0).check()

    assignOptions(descriptor, options)
    nonBlocking(descriptor)

    address.nativeAddress { pointer, size ->
        bind(descriptor, pointer, size).check()
    }

    return DatagramSocketNative(
        descriptor = descriptor,
        selector = selector,
        remote = null,
        parent = selector.coroutineContext
    )
}

internal actual fun UDPSocketBuilder.Companion.joinGroupUDP(
    selector: SelectorManager,
    multicastAddress: InetSocketAddress?,
    networkInterface: NetworkInterface,
    options: SocketOptions.UDPSocketOptions
): BoundDatagramSocket {
    val address = (multicastAddress?.address ?: getAnyLocalAddress()) as NativeIPv4SocketAddress
    val descriptor = socket(address.family.convert(), SOCK_DGRAM, 0).check()

    assignOptions(descriptor, options)
    nonBlocking(descriptor)

    address.nativeAddress { pointer, size ->
        bind(descriptor, pointer, size).check()
    }

    val interf = networkInterface.networkInterfaceAddress

    val mreq = NativeMulticastGroup(address.address, interf.s_addr)

    mreq.nativeAddress { pointer, size ->
        setsockopt(descriptor, IPPROTO_IP, IP_ADD_MEMBERSHIP, pointer, size).check()
    }

    return DatagramSocketNative(
        descriptor = descriptor,
        selector = selector,
        remote = null,
        parent = selector.coroutineContext
    )
}
