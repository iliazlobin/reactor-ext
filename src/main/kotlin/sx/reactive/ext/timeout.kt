package sx.reactive.ext

//fun <T, T1, S> Flux<T>.stateTimeout(
//    initial: S,
//    stateHandler: (S, T) -> Pair<S, Duration>,
//    source: Flux<T1>,
//    sourceHandler: (S, T1) -> S,
//    duration: Duration,
//    scheduler: Scheduler = Schedulers.parallel(),
//    timeoutHandler: (S) -> S
//): Flux<T> {
//    return Flux.create<T> { sink ->
//        var s: S = initial
//
//        var schedule: Disposable? = null
//
//        var q: Queue<Duration> = Queue.empty()
//        val e = SimpleEvent()
//
//        thread {
//            while (true) {
//                val o = q.dequeueOption()
//                if (o.isDefined) {
//                    val (cur, rem) = o.get().pair()
//                    schedule = scheduler.schedule({
//                        s = timeoutHandler(s)
//                        // join
//                    }, cur.toMillis(), java.util.concurrent.TimeUnit.MILLISECONDS)
//                    q = rem
//                }
//
//                // wait
//
//            }
//        }
//
//        fun newScedule() {
//            val o = q.dequeueOption()
//            if (o.isDefined) {
//                val (cur, rem) = o.get().pair()
//                schedule = scheduler.schedule({
//                    s = timeoutHandler(s)
//                    newScedule()
//                }, cur.toMillis(), java.util.concurrent.TimeUnit.MILLISECONDS)
//                q = rem
//            }
//        }
//
//        subscribe {
//            val (res, dur) = stateHandler(s, it)
//            s = res
//            q.enqueue(dur)
//            if (schedule == null) {
//
//            }
//        }
//        source.subscribe {
//            s = sourceHandler(s, it)
//        }
//    }
//}
