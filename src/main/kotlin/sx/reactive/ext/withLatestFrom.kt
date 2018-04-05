package sx.reactive.ext

import reactor.core.Disposable
import reactor.core.publisher.Flux
import reactor.core.scheduler.Scheduler
import reactor.core.scheduler.Schedulers
import java.time.Duration

fun <T, T1, R> Flux<T>.withLatestFrom(
    source1: Flux<T1>,
    combinator: (T, T1) -> (R)
): Flux<R> = this.withLatestFrom(source1, null, null, combinator)

fun <T, T1, R> Flux<T>.withLatestFrom(
    source1: Flux<T1>,
    default: T? = null,
    default1: T1? = null,
    combinator: (T, T1) -> (R)
): Flux<R> {
    return Flux.create<R> { sink ->
        var s: T? = default
        var s1: T1? = default1
        fun process() {
            if ((s != null) and (s1 != null)) sink.next(combinator(s!!, s1!!))
        }
        subscribe { s = it; process() }
        source1.subscribe { s1 = it }
    }
}

fun <T, T1, R> Flux<T>.withLatestFromTimeout(
    source1: Flux<T1>,
    combinator: (T, T1) -> (R),
    duration: Duration,
    scheduler: Scheduler = Schedulers.parallel(),
    timeoutHandler: (T, T1) -> (R)
): Flux<R> = this.withLatestFromTimeout(source1, null, null, combinator, duration, scheduler, timeoutHandler)

fun <T, T1, R> Flux<T>.withLatestFromTimeout(
    source1: Flux<T1>,
    default: T? = null,
    default1: T1? = null,
    combinator: (T, T1) -> (R),
    duration: Duration,
    scheduler: Scheduler = Schedulers.parallel(),
    timeoutHandler: (T, T1) -> (R)
): Flux<R> {
    return Flux.create<R> { sink ->
        var s: T? = default
        var s1: T1? = default1
        var schedule: Disposable? = null
        fun process() {
            if ((s != null) and (s1 != null)) sink.next(combinator(s!!, s1!!))
        }
        fun timeoutProcess() {
            if ((s != null) and (s1 != null)) sink.next(timeoutHandler(s!!, s1!!))
        }
        subscribe {
            s = it
            if (schedule != null) {
                if (!schedule!!.isDisposed) schedule!!.dispose()
                schedule = null
            }
            schedule = scheduler.schedule({
                timeoutProcess()
            }, duration.toMillis(), java.util.concurrent.TimeUnit.MILLISECONDS)
            process()
        }
        source1.subscribe { s1 = it }
    }
}

fun <T, T1, T2, R> Flux<T>.withLatestFrom(
    source1: Flux<T1>,
    source2: Flux<T2>,
    combinator: (T, T1, T2) -> (R)
): Flux<R> = this.withLatestFrom(source1, source2, null, null, null, combinator)

fun <T, T1, T2, R> Flux<T>.withLatestFrom(
    source1: Flux<T1>,
    source2: Flux<T2>,
    default: T? = null,
    default1: T1? = null,
    default2: T2? = null,
    combinator: (T, T1, T2) -> (R)
): Flux<R> {
    return Flux.create<R> { sink ->
        var s: T? = default
        var s1: T1? = default1
        var s2: T2? = default2
        fun process() {
            if ((s != null) and (s1 != null) and (s2 != null)) sink.next(combinator(s!!, s1!!, s2!!))
        }
        subscribe { s = it; process() }
        source1.subscribe { s1 = it }
        source2.subscribe { s2 = it }
    }
}

fun <T, T1, T2, T3, R> Flux<T>.withLatestFrom(
    source1: Flux<T1>,
    source2: Flux<T2>,
    source3: Flux<T3>,
    combinator: (T, T1, T2, T3) -> (R)
): Flux<R> = this.withLatestFrom(source1, source2, source3, null, null, null, null, combinator)

fun <T, T1, T2, T3, R> Flux<T>.withLatestFrom(
    source1: Flux<T1>,
    source2: Flux<T2>,
    source3: Flux<T3>,
    default: T? = null,
    default1: T1? = null,
    default2: T2? = null,
    default3: T3? = null,
    combinator: (T, T1, T2, T3) -> (R)
): Flux<R> {
    return Flux.create<R> { sink ->
        var s: T? = default
        var s1: T1? = default1
        var s2: T2? = default2
        var s3: T3? = default3
        fun process() {
            if ((s != null) and (s1 != null) and (s2 != null) and (s3 != null)) sink.next(combinator(s!!, s1!!, s2!!, s3!!))
        }
        subscribe { s = it; process() }
        source1.subscribe { s1 = it }
        source2.subscribe { s2 = it }
        source3.subscribe { s3 = it }
    }
}
