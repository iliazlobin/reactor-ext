package sx.reactive.ext

import reactor.core.publisher.Flux

fun <T1, T2, R> combineLatest(
    source1: Flux<T1>,
    source2: Flux<T2>,
    combinator: (T1, T2) -> (R)
): Flux<R> = combineLatest(source1, source2, null, null, combinator)

fun <T1, T2, R> combineLatest(
    source1: Flux<T1>,
    source2: Flux<T2>,
    default1: T1? = null,
    default2: T2? = null,
    combinator: (T1, T2) -> (R)
): Flux<R> {
    return Flux.create<R> { sink ->
        var s1: T1? = default1
        var s2: T2? = default2
        fun process() {
            if ((s1 != null) and (s2 != null)) sink.next(combinator(s1!!, s2!!))
        }
        source1.subscribe { s1 = it; process() }
        source2.subscribe { s2 = it; process() }
    }
}

fun <T1, T2, T3, R> combineLatest(
    source1: Flux<T1>,
    source2: Flux<T2>,
    source3: Flux<T3>,
    combinator: (T1, T2, T3) -> (R)
): Flux<R> = combineLatest(source1, source2, source3, null, null, null, combinator)

fun <T1, T2, T3, R> combineLatest(
    source1: Flux<T1>,
    source2: Flux<T2>,
    source3: Flux<T3>,
    default1: T1? = null,
    default2: T2? = null,
    default3: T3? = null,
    combinator: (T1, T2, T3) -> (R)
): Flux<R> {
    return Flux.create<R> { sink ->
        var s1: T1? = default1
        var s2: T2? = default2
        var s3: T3? = default3
        fun process() {
            if ((s1 != null) and (s2 != null) and (s3 != null)) sink.next(combinator(s1!!, s2!!, s3!!))
        }
        source1.subscribe { s1 = it; process() }
        source2.subscribe { s2 = it; process() }
        source3.subscribe { s3 = it; process() }
    }
}

fun <T1, T2, T3, T4, R> combineLatest(
    source1: Flux<T1>,
    source2: Flux<T2>,
    source3: Flux<T3>,
    source4: Flux<T4>,
    combinator: (T1, T2, T3, T4) -> (R)
): Flux<R> = combineLatest(source1, source2, source3, source4, null, null, null, null, combinator)

fun <T1, T2, T3, T4, R> combineLatest(
    source1: Flux<T1>,
    source2: Flux<T2>,
    source3: Flux<T3>,
    source4: Flux<T4>,
    default1: T1? = null,
    default2: T2? = null,
    default3: T3? = null,
    default4: T4? = null,
    combinator: (T1, T2, T3, T4) -> (R)
): Flux<R> {
    return Flux.create<R> { sink ->
        var s1: T1? = default1
        var s2: T2? = default2
        var s3: T3? = default3
        var s4: T4? = default4
        fun process() {
            if ((s1 != null) and (s2 != null) and (s3 != null) and (s4 != null)) sink.next(combinator(s1!!, s2!!, s3!!, s4!!))
        }
        source1.subscribe { s1 = it; process() }
        source2.subscribe { s2 = it; process() }
        source3.subscribe { s3 = it; process() }
        source4.subscribe { s4 = it; process() }
    }
}

fun <T1, T2, T3, T4, T5, R> combineLatest(
    source1: Flux<T1>,
    source2: Flux<T2>,
    source3: Flux<T3>,
    source4: Flux<T4>,
    source5: Flux<T5>,
    combinator: (T1, T2, T3, T4, T5) -> (R)
): Flux<R> = combineLatest(source1, source2, source3, source4, source5, null, null, null, null, null, combinator)

fun <T1, T2, T3, T4, T5, R> combineLatest(
    source1: Flux<T1>,
    source2: Flux<T2>,
    source3: Flux<T3>,
    source4: Flux<T4>,
    source5: Flux<T5>,
    default1: T1? = null,
    default2: T2? = null,
    default3: T3? = null,
    default4: T4? = null,
    default5: T5? = null,
    combinator: (T1, T2, T3, T4, T5) -> (R)
): Flux<R> {
    return Flux.create<R> { sink ->
        val lock = java.lang.Object()
        var s1: T1? = default1
        var s2: T2? = default2
        var s3: T3? = default3
        var s4: T4? = default4
        var s5: T5? = default5
        fun process() {
            synchronized(lock) {
                if ((s1 != null) && (s2 != null) && (s3 != null) && (s4 != null) && (s5 != null)) sink.next(combinator(s1!!, s2!!, s3!!, s4!!, s5!!))
            }
        }
        source1.subscribe { s1 = it; process() }
        source2.subscribe { s2 = it; process() }
        source3.subscribe { s3 = it; process() }
        source4.subscribe { s4 = it; process() }
        source5.subscribe { s5 = it; process() }
    }
}

// scan functions
fun <T1, T2, R> scanCombineLatest(
    initial: R,
    source1: Flux<T1>,
    source2: Flux<T2>,
    accumulator: (T1, T2, R) -> (R)
): Flux<R> = scanCombineLatest(initial, source1, source2, null, null, accumulator)

fun <T1, T2, R> scanCombineLatest(
    initial: R,
    source1: Flux<T1>,
    source2: Flux<T2>,
    default1: T1? = null,
    default2: T2? = null,
    accumulator: (T1, T2, R) -> (R)
): Flux<R> {
    return Flux.create<R> { sink ->
        var s1: T1? = default1
        var s2: T2? = default2
        var a: R = initial
        fun process() {
            a = accumulator(s1!!, s2!!, a)
            if ((s1 != null) and (s2 != null)) sink.next(a)
        }
        source1.subscribe { s1 = it; process() }
        source2.subscribe { s2 = it; process() }
    }
}
