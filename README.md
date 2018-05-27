# Http4s Extend
[![Build Status](https://travis-ci.org/barambani/http4s-extend.svg?branch=master)](https://travis-ci.org/barambani/http4s-extend)
[![codecov](https://codecov.io/gh/barambani/http4s-extend/branch/master/graph/badge.svg)](https://codecov.io/gh/barambani/http4s-extend)
[![scalaindex](https://img.shields.io/badge/scalaindex-http4s--extend-orange.svg)](https://index.scala-lang.org/barambani/http4s-extend/http4s-extend)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.barambani/http4s-extend_2.11.svg?label=central%20repo%202.11&colorB=2282c3)](https://maven-badges.herokuapp.com/maven-central/com.github.barambani/http4s-extend_2.11)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.barambani/http4s-extend_2.12.svg?label=central%20repo%202.12&colorB=2282c3)](https://maven-badges.herokuapp.com/maven-central/com.github.barambani/http4s-extend_2.12)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](https://github.com/barambani/http4s-extend/blob/master/LICENSE)

A basic, still descriptive, example that demonstrates some possible uses for Http4s Extend can be found [here](https://github.com/barambani/http4s-poc-api)

**Note:** this project is in early alpha stage. Not usable in production.

### Using Http4s Extend
Http4s Extend is available for Scala `2.11.x` and `2.12.x`. To use it create the dependency by adding the following to the sbt build
```scala
libraryDependencies += "com.github.barambani" %% "http4s-extend" % "0.0.37"
```
a sample configuration can be found [here](https://github.com/barambani/http4s-poc-api/blob/master/build.sbt) where the dependencies are taken from [here](https://github.com/barambani/http4s-poc-api/blob/master/project/Dependencies.scala)

### Dependencies
For its own purpose is integration, Http4s Extend will bring into a using project some dependencies. Those are:

|        | Http4s    | Monix     | Scalaz Concurrent |
| ------ |:---------:|:---------:|:-----------------:|
| 0.0.37 | 0.18.11   | 3.0.0-RC1 | 7.2.23            |

### New Type
The `NewType` trait is a building block to help create zero allocation new types like
```scala
object MkAndBoolean extends NewType {
  def apply(b: Boolean): T = b.asInstanceOf[T]
  def mkF[F[_]](fs: F[Boolean]): F[T] = fs.asInstanceOf[F[T]]

  implicit final class MkAndBooleanSyntax(val t: T) extends AnyVal {
    def unMk: Boolean = t.asInstanceOf[Boolean]
  }
}

val AndBoolean = MkAndBoolean
type AndBoolean = AndBoolean.T
```
This tecnique is a great help when trying to avoid orphan type class instances for creating a `newtype` allows to have eventual instances in the implicit scope even when the type of interest and the type class itself are owned by someone else and their companion objects cannot be changed. Having this possibility without paying an allocation cost per use is very desirable and cannot be achieved with the language's Value Classes. Consideing this example in fact
```scala
class ValueClass(val v: Boolean) extends AnyVal

class testNewType {

  val ntA1 = AndBoolean(true)
  val ntA2 = AndBoolean(false)

  val ntTuple = (ntA1, ntA2)

  val ntLs = List(ntA1, ntA2)

  val ntId = identity(ntA1)
}

class testValueClass {

  val vcA1 = new ValueClass(true)
  val vcA2 = new ValueClass(false)

  val vcTuple = (vcA1, vcA2)

  val vcLs = List(vcA1, vcA2)

  val vcId = identity(vcA1)
}
```
and giving a look at its disassembled code we can see how `NewType`'s approach differs from the Value Classes in terms of allocations.

**Tuple**
```
public http4s.extend.testValueClass();
  descriptor: ()V
    Code:
      37: new           #54                 // class scala/Tuple2
      40: dup

      41: new           #81                 // class http4s/extend/ValueClass
      44: dup
      45: aload_0
      46: invokevirtual #83                 // Method vcA1:()Z
      49: invokespecial #86                 // Method http4s/extend/ValueClass."<init>":(Z)V

      52: new           #81                 // class http4s/extend/ValueClass
      55: dup
      56: aload_0
      57: invokevirtual #88                 // Method vcA2:()Z
      60: invokespecial #86                 // Method http4s/extend/ValueClass."<init>":(Z)V

      63: invokespecial #91                 // Method scala/Tuple2."<init>":(Ljava/lang/Object;Ljava/lang/Object;)V
      66: putfield      #50                 // Field vcTuple:Lscala/Tuple2;


public http4s.extend.testNewType();
  descriptor: ()V
    Code:
      55: new           #59                 // class scala/Tuple2
      58: dup

      59: aload_0
      60: invokevirtual #116                // Method ntA1:()Ljava/lang/Object;

      63: aload_0
      64: invokevirtual #118                // Method ntA2:()Ljava/lang/Object;

      67: invokespecial #121                // Method scala/Tuple2."<init>":(Ljava/lang/Object;Ljava/lang/Object;)V
      70: putfield      #55                 // Field ntTuple:Lscala/Tuple2;
```
**List**
```
public http4s.extend.testValueClass();
    descriptor: ()V
    Code:
      81: getstatic     #97                 // Field scala/collection/immutable/List$.MODULE$:Lscala/collection/immutable/List$;
      84: getstatic     #102                // Field scala/Predef$.MODULE$:Lscala/Predef$;
      87: iconst_2
      88: anewarray     #81                 // class http4s/extend/ValueClass
      91: dup
      92: iconst_0
      
      93: new           #81                 // class http4s/extend/ValueClass
      96: dup
      97: aload_0
      98: invokevirtual #83                 // Method vcA1:()Z
     101: invokespecial #86                 // Method http4s/extend/ValueClass."<init>":(Z)V
     104: aastore
     
     105: dup
     106: iconst_1
     
     107: new           #81                 // class http4s/extend/ValueClass
     110: dup
     111: aload_0
     112: invokevirtual #88                 // Method vcA2:()Z
     115: invokespecial #86                 // Method http4s/extend/ValueClass."<init>":(Z)V
     118: aastore
     
     119: invokevirtual #106                // Method scala/Predef$.genericWrapArray:(Ljava/lang/Object;)Lscala/collection/mutable/WrappedArray;
     122: invokevirtual #110                // Method scala/collection/immutable/List$.apply:(Lscala/collection/Seq;)Lscala/collection/immutable/List;
     125: putfield      #57                 // Field vcLs:Lscala/collection/immutable/List;


public http4s.extend.testNewType();
    descriptor: ()V
    Code:
      85: getstatic     #126                // Field scala/collection/immutable/List$.MODULE$:Lscala/collection/immutable/List$;
      88: getstatic     #131                // Field scala/Predef$.MODULE$:Lscala/Predef$;
      91: iconst_2
      92: anewarray     #4                  // class java/lang/Object
      
      95: dup
      96: iconst_0
      97: aload_0
      98: invokevirtual #116                // Method ntA1:()Ljava/lang/Object;
     101: aastore
     
     102: dup
     103: iconst_1
     104: aload_0
     105: invokevirtual #118                // Method ntA2:()Ljava/lang/Object;
     108: aastore
     
     109: invokevirtual #135                // Method scala/Predef$.genericWrapArray:(Ljava/lang/Object;)Lscala/collection/mutable/WrappedArray;
     112: invokevirtual #138                // Method scala/collection/immutable/List$.apply:(Lscala/collection/Seq;)Lscala/collection/immutable/List;
     115: putfield      #62                 // Field ntLs:Lscala/collection/immutable/List;
```
**Identity**
```
public http4s.extend.testValueClass();
    descriptor: ()V
    Code:
      141: getstatic     #102                // Field scala/Predef$.MODULE$:Lscala/Predef$;
      
      144: new           #81                 // class http4s/extend/ValueClass
      147: dup
      148: aload_0
      149: invokevirtual #83                 // Method vcA1:()Z
      152: invokespecial #86                 // Method http4s/extend/ValueClass."<init>":(Z)V
      155: invokevirtual #114                // Method scala/Predef$.identity:(Ljava/lang/Object;)Ljava/lang/Object;
      158: checkcast     #81                 // class http4s/extend/ValueClass
      
      161: invokevirtual #117                // Method http4s/extend/ValueClass.v:()Z
      164: putfield      #63                 // Field vcId:Z


public http4s.extend.testNewType();
    descriptor: ()V
    Code:
      131: getstatic     #131                // Field scala/Predef$.MODULE$:Lscala/Predef$;
      
      134: aload_0
      135: invokevirtual #116                // Method ntA1:()Ljava/lang/Object;
      
      138: invokevirtual #141                // Method scala/Predef$.identity:(Ljava/lang/Object;)Ljava/lang/Object;
      141: putfield      #68                 // Field ntId:Ljava/lang/Object;
```
Even if this approach is understandably the best compromise available in Scala at the moment, it comes at a cost. When the `newtype`s are used on value types in fact, they are not erased to the original base type as it's the case for Value Classes, but they are erased to `Object` as can be seen from the disassembled code above and from the snippet below
```java
public boolean vcA1();
  descriptor: ()Z

public boolean vcA2();
  descriptor: ()Z
```
```java
public java.lang.Object ntA1();
  descriptor: ()Ljava/lang/Object;

public java.lang.Object ntA2();
  descriptor: ()Ljava/lang/Object;
```
