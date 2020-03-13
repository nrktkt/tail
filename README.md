# com.github.kag0.tail

simple tail call optimization for Java

enables infinitely deep tail recursive calls without throwing a `StackOverflowError` 

no transitive dependencies

## Install
[![](https://jitpack.io/v/kag0/tail.svg)](https://jitpack.io/#kag0/tail)

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
		<groupId>com.github.kag0</groupId>
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

infiniteLoop(0).evaluate;
```