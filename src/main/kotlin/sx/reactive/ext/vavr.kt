package sx.reactive.ext

import io.vavr.control.Option
import reactor.core.publisher.Flux

fun <T> Flux<Option<T>>.notEmpty() = this.filter { it.isDefined }.map { it.get() }
