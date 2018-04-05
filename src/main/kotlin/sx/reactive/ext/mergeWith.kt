package sx.reactive.ext

import reactor.core.publisher.Flux

fun <T> Flux<T>.merge(
    source1: Flux<T>,
    source2: Flux<T>
): Flux<T> = this.mergeWith(source1).mergeWith(source2)
