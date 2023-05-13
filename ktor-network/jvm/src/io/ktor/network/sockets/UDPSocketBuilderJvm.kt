/*
 * Copyright 2014-2019 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package io.ktor.network.sockets

import io.ktor.network.NetworkInterface
import io.ktor.network.selector.SelectorManager
import io.ktor.network.selector.buildOrClose
import java.net.MulticastSocket

internal actual fun UDPSocketBuilder.Companion.connectUDP(
    selector: SelectorManager,
    remoteAddress: SocketAddress,
    localAddress: SocketAddress?,
    options: SocketOptions.UDPSocketOptions
): ConnectedDatagramSocket = selector.buildOrClose({ openDatagramChannel() }) {
    assignOptions(options)
    nonBlocking()

    if (java7NetworkApisAvailable) {
        bind(localAddress?.toJavaAddress())
    } else {
        socket().bind(localAddress?.toJavaAddress())
    }
    connect(remoteAddress.toJavaAddress())

    return DatagramSocketImpl(this, selector)
}

internal actual fun UDPSocketBuilder.Companion.bindUDP(
    selector: SelectorManager,
    localAddress: SocketAddress?,
    options: SocketOptions.UDPSocketOptions
): BoundDatagramSocket = selector.buildOrClose({ openDatagramChannel() }) {
    assignOptions(options)
    nonBlocking()

    if (java7NetworkApisAvailable) {
        bind(localAddress?.toJavaAddress())
    } else {
        socket().bind(localAddress?.toJavaAddress())
    }
    return DatagramSocketImpl(this, selector)
}

internal actual fun UDPSocketBuilder.Companion.joinGroupUDP(
    selector: SelectorManager,
    networkInterface: NetworkInterface,
    multicastAddress: InetSocketAddress,
    options: SocketOptions.UDPSocketOptions
): BoundDatagramSocket = selector.buildOrClose({ openDatagramChannel() }) {
    assignOptions(options)
    nonBlocking()

    if (java7NetworkApisAvailable) {
        bind(java.net.InetSocketAddress(multicastAddress.port))
        join(multicastAddress.address.address, networkInterface.networkInterface)
    } else {
        val multicastSocket = socket() as MulticastSocket
        multicastSocket.bind(java.net.InetSocketAddress(multicastAddress.port))
        multicastSocket.joinGroup(multicastAddress.toJavaAddress(), networkInterface.networkInterface)
    }

    return DatagramSocketImpl(this, selector)
}
