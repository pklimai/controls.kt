package space.kscience.controls.demo.car

import kotlinx.coroutines.launch
import space.kscience.controls.api.PropertyChangedMessage
import space.kscience.controls.client.controlsMagixFormat
import space.kscience.controls.spec.write
import space.kscience.dataforge.context.Context
import space.kscience.dataforge.context.Factory
import space.kscience.dataforge.meta.Meta
import space.kscience.dataforge.meta.get
import space.kscience.dataforge.meta.string
import space.kscience.dataforge.names.Name
import space.kscience.magix.api.MagixEndpoint
import space.kscience.magix.api.subscribe
import space.kscience.magix.rsocket.rSocketWithWebSockets
import kotlin.time.ExperimentalTime

class MagixVirtualCar(context: Context, meta: Meta) : VirtualCar(context, meta) {

    private fun MagixEndpoint.launchMagixVirtualCarUpdate() = launch {
        subscribe(controlsMagixFormat).collect { (_, payload) ->
            (payload as? PropertyChangedMessage)?.let { message ->
                if (message.sourceDevice == Name.parse("virtual-car")) {
                    when (message.property) {
                        "acceleration" -> write(IVirtualCar.acceleration, Vector2D.metaToObject(message.value))
                    }
                }
            }
        }
    }


    @OptIn(ExperimentalTime::class)
    override suspend fun open() {
        super.open()

        val magixEndpoint = MagixEndpoint.rSocketWithWebSockets(
            meta["magixServerHost"].string ?: "localhost",
        )

        launch {
            magixEndpoint.launchMagixVirtualCarUpdate()
        }
    }

    companion object : Factory<MagixVirtualCar> {
        override fun build(context: Context, meta: Meta): MagixVirtualCar = MagixVirtualCar(context, meta)
    }
}
