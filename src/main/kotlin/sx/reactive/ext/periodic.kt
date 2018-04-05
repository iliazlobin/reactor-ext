package sx.reactive.ext

import reactor.core.publisher.Flux
import reactor.core.scheduler.Scheduler
import reactor.core.scheduler.Schedulers
import java.time.Duration

fun <T> Flux<T>.periodic(
    duration: Duration,
    scheduler: Scheduler = Schedulers.parallel()
): Flux<T> = this.periodic(null, duration, scheduler)

fun <T> Flux<T>.periodic(
    default: T? = null,
    duration: Duration,
    scheduler: Scheduler = Schedulers.parallel()
): Flux<T> {
    return Flux.create<T> { sink ->
        var s: T? = default
        scheduler.schedulePeriodically({
            if (s != null) sink.next(s!!)
        }, duration.toMillis(), duration.toMillis(), java.util.concurrent.TimeUnit.MILLISECONDS)
        subscribe {
            s = it
        }
    }
}

fun <T, S, R> Flux<T>.statePeriodic(
    initial: S,
    default: T? = null,
    additionHandler: (S, T) -> S,
    duration: Duration,
    scheduler: Scheduler = Schedulers.parallel(),
    scheduleHandler: (S, T) -> Pair<S, R>
): Flux<R> {
    return Flux.create<R> { sink ->
        val lock = java.lang.Object()
        var s: S = initial
        var t: T? = default
        scheduler.schedulePeriodically({
            synchronized(lock) {
                if (t != null) {
                    val (newS, r) = scheduleHandler(s, t!!)
                    sink.next(r)
                    s = newS
                }
            }
        }, duration.toMillis(), duration.toMillis(), java.util.concurrent.TimeUnit.MILLISECONDS)
        subscribe {
            synchronized(lock) {
                t = it
                s = additionHandler(s, t!!)
            }
        }
    }
}
