# Harrison

HTTP based RPC for java similar to [HttpInvoker](http://docs.spring.io/spring/docs/current/spring-framework-reference/html/remoting.html)

### Status
[![Build Status](https://travis-ci.org/rafalopez79/harrison.svg?branch=master)](https://travis-ci.org/rafalopez79/harrison/)

#### Motivation

Harrison provides a RPC framework with streaming support for large uploads or downloads.
Easy integration in spring based applications.

#### Remote service example

```java
    public int compute(String a, String b);

	public String streamUp(String a, String b, StreamIterable<String> stream);

	public StreamIterable<String> streamDown(String a, String b, StreamIterable<String> stream);

	public StreamIterable<String> streamUpDown(String a, String b, StreamIterable<String> stream);

	public StreamIterable<DataDTO> dataDown(Parameter po, StreamIterable<DataDTO> stream);
```
    
    
#### Standard call

#### Stream call

#### Message format

