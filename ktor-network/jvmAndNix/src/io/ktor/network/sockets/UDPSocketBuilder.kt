package io.ktor.network.sockets

import io.ktor.network.NetworkInterface
import io.ktor.network.selector.SelectorManager

/**
 * UDP socket builder
 */
public class UDPSocketBuilder(
    private val selector: SelectorManager,
    override var options: SocketOptions.UDPSocketOptions
) : Configurable<UDPSocketBuilder, SocketOptions.UDPSocketOptions> {
    /**
     * Bind server socket to listen to [localAddress].
     */
    public fun bind(
        localAddress: SocketAddress? = null,
        configure: SocketOptions.UDPSocketOptions.() -> Unit = {}
    ): BoundDatagramSocket = bindUDP(selector, localAddress, options.udp().apply(configure))

    /**
     * Create a datagram socket to listen datagrams at [localAddress] and set to [remoteAddress].
     */
    public fun connect(
        remoteAddress: SocketAddress,
        localAddress: SocketAddress? = null,
        configure: SocketOptions.UDPSocketOptions.() -> Unit = {}
    ): ConnectedDatagramSocket = connectUDP(selector, remoteAddress, localAddress, options.udp().apply(configure))

    public fun joinGroup(
        networkInterface: NetworkInterface,
        multicastAddress: InetSocketAddress,
        configure: SocketOptions.UDPSocketOptions.() -> Unit = {}
    ): BoundDatagramSocket =
        joinGroupUDP(selector, networkInterface, multicastAddress, options.udp().apply(configure))

    public companion object
}

internal expect fun UDPSocketBuilder.Companion.connectUDP(
    selector: SelectorManager,
    remoteAddress: SocketAddress,
    localAddress: SocketAddress?,
    options: SocketOptions.UDPSocketOptions
): ConnectedDatagramSocket

internal expect fun UDPSocketBuilder.Companion.bindUDP(
    selector: SelectorManager,
    localAddress: SocketAddress?,
    options: SocketOptions.UDPSocketOptions
): BoundDatagramSocket

internal expect fun UDPSocketBuilder.Companion.joinGroupUDP(
    selector: SelectorManager,
    networkInterface: NetworkInterface,
    multicastAddress: InetSocketAddress,
    options: SocketOptions.UDPSocketOptions
): BoundDatagramSocket
