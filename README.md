# Jype

Jype is a library for manually describing Java types in a robust manner.
[java.lang.Class](http://docs.oracle.com/javase/1.5.0/docs/api/java/lang/Class.html)
is sufficient for describing a simple, non-generic type. Once we start using
generics, [type erasure](http://docs.oracle.com/javase/tutorial/java/generics/erasure.html)
prevents us from easily understanding a type at runtime.

Enter Jype. If manually describing a type is within the constraints of your
project, Jype is there to help:

```java
// String
TypeDescriptor simpleType = new SimpleType(String.class);

// Map<String, Integer>
TypeDescriptor genericType = new GenericType(Map.class, String.class, Integer.class);

// List<List<String>>
TypeDescriptor genericType = new GenericType(List.class, new GenericType(List.class, String.class));
```

# License

Distributed under the [MIT license](//github.com/thegedge/jype/blob/master/LICENSE)
