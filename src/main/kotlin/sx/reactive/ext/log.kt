package sx.reactive.ext

import io.vavr.control.Try
import sx.utils.ext.hexDump
import sx.utils.ext.stackDump
import org.slf4j.Logger
import org.springframework.boot.logging.LogLevel
import reactor.core.publisher.Flux

fun log(message: String, logger: Logger, prefix: String = "", ll: LogLevel = LogLevel.DEBUG) {
    when(ll) {
        LogLevel.ERROR -> logger.error(prefix + message)
        LogLevel.INFO -> logger.info(prefix + message)
        LogLevel.DEBUG -> logger.debug(prefix + message)
        LogLevel.WARN -> logger.warn(prefix + message)
        LogLevel.TRACE -> logger.trace(prefix + message)
        LogLevel.FATAL -> logger.error(prefix + message)
        LogLevel.OFF -> Unit
    }
}

fun <T> Flux<T>.log(logger: Logger, prefix: String = "", ll: LogLevel = LogLevel.DEBUG): Flux<T> {
    return this.doOnNext {
        log(it.toString(), logger, prefix, ll)
    }
}

fun Flux<ByteArray>.logByteArray(logger: Logger, prefix: String = "", ll: LogLevel = LogLevel.DEBUG): Flux<ByteArray> {
    return this.doOnNext {
        log(it.hexDump(), logger, prefix, ll)
    }
}

fun <T> Flux<T>.logTrace(logger: Logger, prefix: String = ""): Flux<T> {
    return this.doOnNext {
        logger.trace(prefix + it)
    }
}

fun <T> Flux<T>.logTrace(logger: Logger, prefix: String = "", condition: (T) -> Boolean): Flux<T> {
    return this.doOnNext {
        if (condition(it)) {
            logger.trace(prefix + it)
        }
    }
}

fun <T> Flux<T>.logDebug(logger: Logger, prefix: String = ""): Flux<T> {
    return this.doOnNext {
        logger.debug(prefix + it)
    }
}

fun <T> Flux<T>.logDebug(logger: Logger, prefix: String = "", condition: (T) -> Boolean): Flux<T> {
    return this.doOnNext {
        if (condition(it)) {
            logger.debug(prefix + it)
        }
    }
}

fun <T> Flux<T>.logInfo(logger: Logger, prefix: String = ""): Flux<T> {
    return this.doOnNext {
        logger.info(prefix + it)
    }
}

fun <T> Flux<T>.logInfo(logger: Logger, prefix: String = "", condition: (T) -> Boolean): Flux<T> {
    return this.doOnNext {
        if (condition(it)) {
            logger.info(prefix + it)
        }
    }
}

fun Flux<ByteArray>.logByteArrayDebug(logger: Logger, prefix: String = ""): Flux<ByteArray> {
    return this.doOnNext {
        logger.debug(prefix + it.hexDump())
    }
}

fun Flux<ByteArray>.logByteArrayTrace(logger: Logger, prefix: String = ""): Flux<ByteArray> {
    return this.doOnNext {
        logger.trace(prefix + it.hexDump())
    }
}

fun <T> Flux<Try<T>>.resolveTry(logger: Logger, prefix: String = "", ll: LogLevel = LogLevel.WARN): Flux<T> {
    return this.doOnNext {
        if (it.isFailure) {
            log(it.cause.stackDump(), logger, prefix, ll)
        }
    }
        .filter { it.isSuccess }
        .map { it.get() }
}
