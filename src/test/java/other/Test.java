package other;


import com.github.kag0.tail.Tail;
import static com.github.kag0.tail.Tail.*;

import java.math.BigInteger;


class Test {
	public static void main(String[] args) {
		try {
			fact(BigInteger.ONE, BigInteger.valueOf(99999));
			throw new RuntimeException("JVM stack too big for this test");
		} catch (StackOverflowError e) {}

		try {
			dangerousFact(BigInteger.ONE, BigInteger.valueOf(99999));
			throw new RuntimeException("JVM stack too big for this test");
		} catch (StackOverflowError e) {}

		try {
			unsafeAck(4, 1);
			throw new RuntimeException("JVM stack too big for this test");
		} catch (StackOverflowError e) {}

		System.out.println(safeFact(BigInteger.ONE, BigInteger.valueOf(99999)).evaluate());
		System.out.println(ack(4, 1).evaluate());
	}

	static int unsafeAck(int m, int n) {
		if(m == 0)
			return n + 1;
		if(m > 0 && n == 0)
			return unsafeAck(m - 1, 1);
		if(m > 0 && n > 0)
			return unsafeAck(m - 1, unsafeAck(m, n- 1));
		throw new IllegalArgumentException();
	}

	static Tail<Integer> ack(int m, int n) {
		if(m == 0)
			return done(n + 1);
		if(m > 0 && n == 0)
			return call(() -> ack(m - 1, 1));
		if(m > 0 && n > 0)
			return call(() -> ack(m, n - 1).flatMap(nn -> ack(m - 1, nn)));
		throw new IllegalArgumentException();
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
		return dangerousFact(fact.multiply(n), n.subtract(BigInteger.ONE));
	}

	static Tail<BigInteger> safeFact(BigInteger fact, BigInteger n) {
		if(n.equals(BigInteger.ZERO)) {
			return done(fact);
		}
		return call(() -> safeFact(fact.multiply(n), n.subtract(BigInteger.ONE)));
	}
}