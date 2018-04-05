package sx.reactive.ext

import reactor.core.publisher.Flux

fun <T> Flux<T>.hot() = this.publish().autoConnect()

fun <T> Flux<T>.default(default: T) = this.mergeWith(Flux.just(default))

fun <T> Flux<T>.hotDefault(default: T) = this.hot().default(default)
