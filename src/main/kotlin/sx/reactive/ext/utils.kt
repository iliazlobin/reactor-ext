package sx.reactive.ext

import reactor.core.Disposable
import reactor.core.publisher.Flux
import reactor.core.scheduler.Scheduler
import reactor.core.scheduler.Schedulers
import java.time.Duration

fun <T> Flux<List<T>>.flatList() = this.flatMap { Flux.fromIterable(it) }

fun <T> Flux<T>.defaultTimeout(
    duration: Duration,
    default: T,
    scheduler: Scheduler = Schedulers.parallel()
): Flux<T> {
    // TODO: check syncronization
    return Flux.create<T> { sink ->
        var schedule: Disposable? = null
        this.subscribe {
            if (schedule != null) {
                if (!schedule!!.isDisposed) schedule!!.dispose()
                schedule = null
            }
            schedule = scheduler.schedule({
                sink.next(default)
            }, duration.toMillis(), java.util.concurrent.TimeUnit.MILLISECONDS)
            sink.next(it)
        }
    }
}

fun <T, R> Flux<T>.scan(
    initial: R,
    accumulator: (R, T) -> (R)
): Flux<R> {
    return Flux.create<R> { sink ->
        var a: R = initial
        this.subscribe {
            a = accumulator(a, it)
            sink.next(a)
        }
    }
}

fun <T, R> Flux<T>.state(
    initial: T,
    handler: (T, T) -> (R)
): Flux<R> {
    return Flux.create<R> { sink ->
        val lock = java.lang.Object()
        var e: T = initial
        this.subscribe {
            kotlin.synchronized(lock) {
                val r = handler(e, it)
                e = it
                sink.next(r)
            }
        }
    }
}

fun <T, R> Flux<T>.stateNull(
    initial: T? = null,
    handler: (T?, T) -> (R)
): Flux<R> {
    return Flux.create<R> { sink ->
        val lock = java.lang.Object()
        var e: T? = initial
        this.subscribe {
            kotlin.synchronized(lock) {
                val r = handler(e, it)
                e = it
                sink.next(r)
            }
        }
    }
}

fun <T, T1, R> Flux<T>.scanWithLatestFrom(
    initial: R,
    source1: Flux<T1>,
    accumulator: (T, T1, R) -> (R)
): Flux<R> = this.scanWithLatestFrom(initial, source1, null, null, accumulator)

fun <T, T1, R> Flux<T>.scanWithLatestFrom(
    initial: R,
    source1: Flux<T1>,
    default: T? = null,
    default1: T1? = null,
    accumulator: (T, T1, R) -> (R)
): Flux<R> {
    return Flux.create<R> { sink ->
        var s: T? = default
        var s1: T1? = default1
        var a: R = initial
        fun process() {
            if ((s != null) and (s1 != null)) {
                a = accumulator(s!!, s1!!, a)
                sink.next(a)
            }
        }
        subscribe {
            s = it
            process()
        }
        source1.subscribe { s1 = it }
    }
}

fun <T, T1, R> Flux<T>.scanWithLatestFromTimeout(
    initial: R,
    source1: Flux<T1>,
    accumulator: (T, T1, R, Boolean) -> (R),
    duration: Duration,
    scheduler: Scheduler = Schedulers.parallel(),
    timeoutHandler: (T, T1, R) -> (R)
): Flux<R> = this.scanWithLatestFromTimeout(initial, source1, null, null, accumulator, duration, scheduler, timeoutHandler)

fun <T, T1, R> Flux<T>.scanWithLatestFromTimeout(
    initial: R,
    source1: Flux<T1>,
    default: T? = null,
    default1: T1? = null,
    accumulator: (T, T1, R, Boolean) -> (R),
    duration: Duration,
    scheduler: Scheduler = Schedulers.parallel(),
    timeoutHandler: (T, T1, R) -> (R)
): Flux<R> {
    return Flux.create<R> { sink ->
        var s: T? = default
        var s1: T1? = default1
        var a: R = initial
        var schedule: Disposable? = null
        var timeoutExpired: Boolean = true
        fun process() {
            if ((s != null) and (s1 != null)) {
                a = accumulator(s!!, s1!!, a, timeoutExpired)
                sink.next(a)
            }
        }
        fun timeoutProcess() {
            if ((s != null) and (s1 != null)) {
                a = timeoutHandler(s!!, s1!!, a)
                sink.next(a)
            }
        }
        subscribe {
            s = it;
            if (schedule != null) {
                if (!schedule!!.isDisposed) schedule!!.dispose()
                schedule = null
            }
            schedule = scheduler.schedule({
                timeoutProcess()
                timeoutExpired = true
            }, duration.toMillis(), java.util.concurrent.TimeUnit.MILLISECONDS)
            timeoutExpired = false
            process()
        }
        source1.subscribe { s1 = it }
    }
}

fun <T, T1, R> Flux<T>.scanWithLatestFromTimeoutAccumulator(
    initial: R,
    source1: Flux<T1>,
    accumulator: (T, T1, R, Boolean) -> (R),
    duration: Duration,
    scheduler: Scheduler = Schedulers.parallel(),
    timeoutAccumulatorHandler: (T, T1, R) -> (R)
): Flux<R>  = this.scanWithLatestFromTimeoutAccumulator(initial, source1, null, null, accumulator, duration, scheduler, timeoutAccumulatorHandler)

fun <T, T1, R> Flux<T>.scanWithLatestFromTimeoutAccumulator(
    initial: R,
    source1: Flux<T1>,
    default: T? = null,
    default1: T1? = null,
    accumulator: (T, T1, R, Boolean) -> (R),
    duration: Duration,
    scheduler: Scheduler = Schedulers.parallel(),
    timeoutAccumulatorHandler: (T, T1, R) -> (R)
): Flux<R> {
    return Flux.create<R> { sink ->
        var s: T? = default
        var s1: T1? = default1
        var a: R = initial
        var schedule: Disposable? = null
        var timeoutExpired: Boolean = true
        fun process() {
            if ((s != null) and (s1 != null)) {
                a = accumulator(s!!, s1!!, a, timeoutExpired)
                sink.next(a)
            }
        }
        fun timeoutAccumulatorProcess() {
            if ((s != null) and (s1 != null)) {
                a = timeoutAccumulatorHandler(s!!, s1!!, a)
                sink.next(a)
            }
        }
        subscribe {
            s = it;
            if (schedule != null) {
                if (!schedule!!.isDisposed) schedule!!.dispose()
                schedule = null
            }
            schedule = scheduler.schedule({
                timeoutAccumulatorProcess()
                timeoutExpired = true
            }, duration.toMillis(), java.util.concurrent.TimeUnit.MILLISECONDS)
            timeoutExpired = false
            process()
        }
        source1.subscribe {
            s1 = it
            if (timeoutExpired) {
                timeoutAccumulatorProcess()
            }
        }
    }
}
