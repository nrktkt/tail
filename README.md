# com.github.kag0.tail

simple tail call optimization for Java

enables infinitely deep [tail recursive calls](https://en.wikipedia.org/wiki/Tail_call) without throwing a `StackOverflowError` 

no transitive dependencies

## Install
[![](https://jitpack.io/v/nrktkt/tail.svg)](https://jitpack.io/#nrktkt/tail)

add the jitpack repository
```xml
<repositories>
...
  <repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
  </repository>
...
</repositories>
```
add the dependency
```xml
<dependencies>
...
  <dependency>
    <groupId>com.github.nrktkt</groupId>
    <artifactId>tail</artifactId>
    <version>Tag</version>
  </dependency>
...
</dependencies>
```

## Use

```java
import com.github.kag0.tail.Tail;
import static com.github.kag0.tail.Tail.*;

Tail<Void> infiniteLoop(int i) {
  System.out.println("Loop " + i + ", stack still intact!");
  return call(() -> infiniteLoop(i + 1));
}

infiniteLoop(0).evaluate();
```

### Example: tail optimizing factorial computation

#### un-optimized first version

let's start with a simple recursive method to compute the `n`th factorial. 
this code will throw a `StackOverflowError` for large values of `n`.

```java
long factorial(long n) {
  if(n == 1) return 1;
  else return n * factorial(n - 1);
}
```

#### move the recursive call into the tail position

the tail position is just another way of saying 
"the last thing you do before the `return`".

```java
long factorial(long fact, long n) {
  if(n.equals(1)) return fact;
  return factorial(fact * n, n - 1);
}
```

this may require a slight refactor, 
usually to add an additional parameter to accumulate progress.

#### wrap the return type in `Tail`

this will enforce that the recursive call is in the tail position.

```java
Tail<Long> factorial(long fact, long n)
```

#### wrap base cases with `done`

```java
if(n.equals(0)) return done(fact);
```

#### wrap recursive calls with `call`

```java
return call(() -> factorial(fact * n, n - 1));
```

#### profit

call `.evaluate()` on the invocation of your method.

```java
factorial(1, Long.MAX_VALUE).evaluate();
```

recursive methods no longer blow the stack.  
note that if you skip the 'move the recursive call into the tail position' 
step, the code will not compile because the method is not tail recursive 
and therefore not stack safe. thanks to `Tail` that is covered by type safety.

### making safe recursive calls outside the tail position

in addition to making tail recursion safe, 
we can also use trampolining to enable recursive methods 
that would otherwise be tricky to make tail recursive.

to do this, just use `.flatMap` to chain two `call`s together.  
for example

```java
Tail<Integer> ackermann(int m, int n) {
  if(m == 0) 
    return done(n + 1);
  if(m > 0 && n == 0) 
    return call(() -> ack(m - 1, 1));
  if(m > 0 && n > 0) 
    return call(() -> ack(m, n - 1)).flatMap(nn -> ack(m - 1, nn));
  throw new IllegalArgumentException();
}
```

## [Benchmarks](benchmark.md)
