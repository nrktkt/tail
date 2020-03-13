# com.github.kag0.tail

simple tail call optimization for Java

## Install



## Use

```java
import com.github.kag0.tail.Tail;
import static com.github.kag0.tail.Tail.*;

Tail<Void> infiniteLoop(int i) {
    System.out.println("Loop " + i + ", stack still intact!");
    return call(() -> infiniteLoop(i + 1));
}

infiniteLoop(0).evaluate;
```