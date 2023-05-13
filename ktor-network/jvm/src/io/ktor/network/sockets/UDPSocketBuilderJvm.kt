/*
 * Copyright 2014-2019 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package io.ktor.network.sockets

import io.ktor.network.*
import io.ktor.network.selector.*
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
    multicastAddress: InetSocketAddress?,
    networkInterface: NetworkInterface,
    options: SocketOptions.UDPSocketOptions
): BoundDatagramSocket = selector.buildOrClose({ openDatagramChannel() }) {
    assignOptions(options)
    nonBlocking()

    if (java7NetworkApisAvailable) {
        join(multicastAddress?.address?.address, networkInterface.networkInterface)
    } else {
        (socket() as MulticastSocket).joinGroup(multicastAddress?.toJavaAddress(), networkInterface.networkInterface)
    }

    return DatagramSocketImpl(this, selector)
}
