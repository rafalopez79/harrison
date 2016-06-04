# Harrison


HTTP based RPC for java similar to [HttpInvoker](http://docs.spring.io/spring/docs/current/spring-framework-reference/html/remoting.html)

### Status
[![Build Status](https://travis-ci.org/rafalopez79/harrison.svg?branch=master)](https://travis-ci.org/rafalopez79/harrison/)

#### Motivation

Harrison provides a RPC framework with streaming support for large uploads or downloads.

It provides easy integration in spring based applications and uses intensively [Apache HttpClient](https://hc.apache.org).

#### Remote service interface example

```java
    public int compute(String a, String b);

	public String streamUp(String a, String b, StreamIterable<String> stream);

	public StreamIterable<String> streamDown(String a, String b, StreamIterable<String> stream);

	public StreamIterable<String> streamUpDown(String a, String b, StreamIterable<String> stream);

	public StreamIterable<DataDTO> dataDown(Parameter po, StreamIterable<DataDTO> stream);
```

`StreamIterable` is the object used to write objects to the data stream or to iterate through it.
It can be created with `ClientUtil.createStreamIterable()`.

Clients can be created with the help of `ClientProxyFactory` and the server side can be configured with spring:

```xml
    <bean id="TestBean" class = "com.bzsoft.harrison.service.impl.TestServiceImpl"/>

    <bean name="/test" class="com.bzsoft.harrison.server.spring.HarrisonServiceExporter"
        p:service-ref="TestBean" p:serviceInterface="com.bzsoft.harrison.service.TestService" />
```
  
Client can choose Java based serialization or Kryo serialization.
  
##### Standard call

Standard RPC call with some parameters and a result value.

##### Stream call

RPC call that read or return a stream of java objects.
Stream calls must have `StreamIterable` as last parameter of the method signature.

#### Message format

* Standard call
  * Client: 

Field | Size
----- | -----
Magic | 4 bytes
Version | 4 bytes
Serialization type | 1 byte
Call type | 1 byte
Method name | String
Args length | 4 bytes
Args | Object list


  * Server

Field | Size
----- | -----
Success | 1 bytes
Result | Object

  
  
* Stream call

######TODO

#### Task list

- [ ] Improve tests.
- [ ] Improve documentation.



As Kanye West said:

> We're living the future so
> the present is our past.