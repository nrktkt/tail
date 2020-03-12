package tail;

import java.util.function.Supplier;

@FunctionalInterface
public interface Tail<A> {

    Tail<A> next();

    static <A> Tail<A> call(Supplier<Tail<A>> recursive) {
        return recursive::get;
    }

    static <A> Done<A> done(A result) {
        return new Done<>(result);
    }

    static <A> A evaluate(Tail<A> call) {
        Tail<A> next = call;
        while(!(next instanceof Done)) {
            next = next.next();
        }
        return ((Done<A>) next).result;
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
    }
}
