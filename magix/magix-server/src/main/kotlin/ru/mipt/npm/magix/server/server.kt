package ru.mipt.npm.magix.server

import io.ktor.server.application.Application
import io.ktor.server.cio.CIO
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.embeddedServer
import io.rsocket.kotlin.core.RSocketServer
import io.rsocket.kotlin.transport.ktor.tcp.TcpServer
import io.rsocket.kotlin.transport.ktor.tcp.TcpServerTransport
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import org.slf4j.LoggerFactory
import ru.mipt.npm.magix.api.MagixEndpoint
import ru.mipt.npm.magix.api.MagixEndpoint.Companion.DEFAULT_MAGIX_HTTP_PORT
import ru.mipt.npm.magix.api.MagixEndpoint.Companion.DEFAULT_MAGIX_RAW_PORT
import ru.mipt.npm.magix.api.MagixMessage

/**
 * Raw TCP magix server
 */
public fun CoroutineScope.launchMagixServerRawRSocket(
    magixFlow: MutableSharedFlow<MagixMessage>,
    rawSocketPort: Int = DEFAULT_MAGIX_RAW_PORT,
): TcpServer {
    val tcpTransport = TcpServerTransport(port = rawSocketPort)
    val rSocketJob: TcpServer = RSocketServer().bindIn(this, tcpTransport, magixAcceptor(magixFlow))

    coroutineContext[Job]?.invokeOnCompletion {
        rSocketJob.handlerJob.cancel()
    }

    return rSocketJob;
}

/**
 * A combined RSocket/TCP server
 * @param applicationConfiguration optional additional configuration for magix loop server
 */
public fun CoroutineScope.startMagixServer(
    port: Int = DEFAULT_MAGIX_HTTP_PORT,
    buffer: Int = 100,
    enableRawRSocket: Boolean = true,
    enableZmq: Boolean = true,
    applicationConfiguration: Application.(MutableSharedFlow<MagixMessage>) -> Unit = {},
): ApplicationEngine {
    val logger = LoggerFactory.getLogger("magix-server")
    val magixFlow = MutableSharedFlow<MagixMessage>(
        buffer,
        extraBufferCapacity = buffer
    )

    if (enableRawRSocket) {
        //Start tcpRSocket server
        val rawRSocketPort = DEFAULT_MAGIX_RAW_PORT
        logger.info("Starting magix raw rsocket server on port $rawRSocketPort")
        launchMagixServerRawRSocket(magixFlow, rawRSocketPort)
    }
    if (enableZmq) {
        //Start ZMQ server socket pair
        val zmqPubSocketPort: Int = MagixEndpoint.DEFAULT_MAGIX_ZMQ_PUB_PORT
        val zmqPullSocketPort: Int = MagixEndpoint.DEFAULT_MAGIX_ZMQ_PULL_PORT
        logger.info("Starting magix zmq server on pub port $zmqPubSocketPort and pull port $zmqPullSocketPort")
        launchMagixServerZmqSocket(
            magixFlow,
            zmqPubSocketPort = zmqPubSocketPort,
            zmqPullSocketPort = zmqPullSocketPort
        )
    }

    return embeddedServer(CIO, host = "localhost", port = port) {
        magixModule(magixFlow)
        applicationConfiguration(magixFlow)
    }.apply {
        start()
    }
}