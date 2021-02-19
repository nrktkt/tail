# Benchmarking

in general I think benchmarking this library for comparison is a bit apples to oranges.
the library enables deep recursive methods, and unless we're comparing to other 
deep recursion then we're not really comparing the same things. 
on this page we'll compare either  
a trampolined tail-recursive solution to a normal recursive solution for a shallow depth. 
this doesn't make a lot of sense because the latter is incapable of deep recursion, 
and we'd only ever care about using the former if the recursion was deep.  
or  
a recursive solution to an iterative solution. 
this doesn't make much sense because they're different implementations, 
and not all problems can easily be solved both ways.

what does matter is understanding the impact we have on memory. 
not how much, but how it behaves.

## Memory Impact

the take away here is that rather than using stack memory, 
young gen heap memory is used. 
as a consequence, other objects may be moved from young to old heap unnecessarily.

note that this difference is only applicable to methods that use primitives.
any method using objects or boxed primitives is going to generate the same 
amount of objects each loop.

## Performance

Comparing safe and unsafe factorial computation with and without using `Tail`. 
Both implementations were otherwise identical (both tail recursive).  
`iter`- implementations are written with a `for` loop and no recursion.

```
# JMH version: 1.23
# VM version: JDK 11.0.6, OpenJDK 64-Bit Server VM, 11.0.6+10
# VM invoker: C:\Program Files\AdoptOpenJDK\jdk-11.0.6.10-hotspot\bin\java.exe
# Warmup: 2 iterations, 10 s each
# Measurement: 3 iterations, 10 s each
# Timeout: 10 min per iteration
# Threads: 2 threads, will synchronize iterations
# Benchmark mode: Throughput, ops/time

Benchmark     Mode  Cnt       Score       Error  Units
  safe100    thrpt    3  281133.189 ±  7150.046  ops/s
  safe1000   thrpt    3    5556.739 ±  2087.674  ops/s
  safe10000  thrpt    3      53.123 ±     2.157  ops/s
  safe100000 thrpt    3       0.432 ±     0.087  ops/s
unsafe100    thrpt    3  319579.637 ± 47343.027  ops/s
unsafe1000   thrpt    3    5703.756 ±  2433.366  ops/s
unsafe10000  thrpt    3      52.861 ±     0.823  ops/s
unsafe100000 thrpt           StackOverflowError
  iter1000   thrpt    5    7021.811 ±    71.309  ops/s
  iter10000  thrpt    5      61.583 ±     1.298  ops/s
  iter100000 thrpt    5       0.504 ±     0.020  ops/s
```

### Implementations

```java
static BigInteger factorial(BigInteger result, BigInteger n) {
    if(n.equals(BigInteger.ONE)) {
        return result;
    }
    return factorial(result.multiply(n), n.subtract(BigInteger.ONE));
}

static Tail<BigInteger> tailRecFactorial(BigInteger result, BigInteger n) {
    if(n.equals(BigInteger.ONE)) {
        return done(result);
    }
    return call(() -> tailRecFactorial(result.multiply(n), n.subtract(BigInteger.ONE)));
}

static BigInteger iterativeFactorial(BigInteger n) {
    BigInteger result = BigInteger.ONE;
    for(BigInteger i = BigInteger.ONE; i.compareTo(n) < 0; i = i.add(BigInteger.ONE)) {
        result = result.multiply(i);
    }
    return result;
}
```
