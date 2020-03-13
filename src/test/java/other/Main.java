package other;


import com.github.kag0.tail.Tail;
import static com.github.kag0.tail.Tail.*;

import java.math.BigInteger;


class Scratch {
    public static void main(String[] args) {
        try {
            fact(BigInteger.ONE, BigInteger.valueOf(99999));
            throw new RuntimeException("JVM stack too big for this test");
        } catch (StackOverflowError e) {}

        try {
            dangerousFact(BigInteger.ONE, BigInteger.valueOf(99999));
            throw new RuntimeException("JVM stack too big for this test");
        } catch (StackOverflowError e) {}

        System.out.println(safeFact(BigInteger.ONE, BigInteger.valueOf(99999)).evaluate());
    }

    static Tail<Void> infiniteLoop(int i) {
        System.out.println("Loop " + i + ", stack still intact!");

        return call(() -> infiniteLoop(i + 1));
    }

    static BigInteger fact(BigInteger fact, BigInteger n) {
        if(n.equals(BigInteger.ZERO)) {
            return fact;
        }
        return fact(fact.multiply(n), n.subtract(BigInteger.ONE));
    }

    static Tail<BigInteger> dangerousFact(BigInteger fact, BigInteger n) {
        if(n.equals(BigInteger.ZERO)) {
            return done(fact);
        }
        return call(dangerousFact(fact.multiply(n), n.subtract(BigInteger.ONE))::next);
    }

    static Tail<BigInteger> safeFact(BigInteger fact, BigInteger n) {
        if(n.equals(BigInteger.ZERO)) {
            return done(fact);
        }
        return call(() -> safeFact(fact.multiply(n), n.subtract(BigInteger.ONE)));
    }
}