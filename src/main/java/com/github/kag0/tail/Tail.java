package com.github.kag0.tail;

import java.util.function.Function;
import java.util.function.Supplier;

@FunctionalInterface
public interface Tail<A> {

	Tail<A> next();

	static <A> Done<A> done(A result) {
		return new Done<>(result);
	}

	static <A> Tail<A> call(Supplier<Tail<A>> recursive) {
		return recursive::get;
	}

	default <B> Tail<B> flatMap(Function<A, Tail<B>> fn) {
		return new FlatMap<>(this, fn);
	}

	static <A> A evaluate(Tail<A> call) {
		while(!(call instanceof Done)) {
			call = call.next();
		}
		return ((Done<A>) call).result;
	}

	default A evaluate() {
		return evaluate(this);
	}

	final class Done<A> implements Tail<A> {
		public final A result;

		public Done(A result) {
			this.result = result;
		}

		public Tail<A> next() {
			return this;
		}

		public String toString() {
			return "Done{" +
				"result=" + result +
				'}';
		}
	}

	final class FlatMap<A, B> implements Tail<B> {
		public final Tail<A> tail;
		public final Function<A, Tail<B>> fn;

		public FlatMap(Tail<A> tail, Function<A, Tail<B>> fn) {
			this.tail = tail;
			this.fn = fn;
		}

		public Tail<B> next() {
			if(tail instanceof Done) {
				return fn.apply(((Done<A>) tail).result);
			}
			if(tail instanceof FlatMap) {
				FlatMap<Object, A> fm = (FlatMap<Object, A>) tail;
				return fm.tail.flatMap(tail2 -> fm.fn.apply(tail2).flatMap(fn));
			}
			return () -> tail.next().flatMap(fn);
		}
	}
}
