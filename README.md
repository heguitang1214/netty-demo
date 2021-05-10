我在dubug的时候，发现服务端断点还停留在处理新连接接入；客户端
```java
addListener(future -> {
        if (future.isSuccess()) {
            System.out.println("连接成功!");
        } else {
            System.err.println("连接失败，开始重连");
            
        }
    });
```
这个时候客户端已经打印出”连接成功了”
    
 作者回复: 这个问题很好，说明你看的很细致还动手实践了。这里涉及到一些细节了：
 首先连接（说白了，就是TCP的三次握手）在服务器端写完下面这句话的时候：
         ServerSocket serverSocket = new ServerSocket(8091);（注意这里没有accept）
 就已经可以连接上去了，而不是必须有accept（）的执行，那accept是做什么的？
 相当于快递的签入。如果你不签入，这个快递还是已经到了（连接已经建立好了），但是你无法对他进行后续处理（最起码，在服务器端你拿不到对应的socket channel来执行调用，更不用谈注册读写事件）。另外一个方面，如果你不accept，那中间的缓存区会满（取决于backlog大小），那很快，就无法建立新的连接了，和快递签入一个道理。你不签入，那快递都堆那个地方，最终还是存不下。
 
 所以总结起来就是，accept准确的说应该说是对连接的后续处理（签入），（但是经常在表述上包括本课程都是直接说处理连接，所以可能会让你误解，以为只有accept调用，连接才能建），至于连接本身（TCP）三次握手并不是accept这个方法做的事情。
 
 你可以写一些简单的例子验证下。很简单，几行代码，抛开netty来看，其实更容易把这些问题搞清楚，如果还有别的疑惑，欢迎继续提问，我应该说清楚这个问题了，哈哈
 


## 内存泄漏
1. 创建ByteBuf时调用了track0(obj)方法，传入的obj就是创建的ByteBuf对象。
2. track0(obj)方法内做了2件事
   a. 创建一个弱引用对象，绑定上面传入的ByteBuf对象和一个全局的弱引用队列refQueue。
   b. 把这个弱引用对象加入到另一个全局集合allLeaks里面。
3. ByteBuf对象用完了，正常情况会调用release()方法回收堆外内存，同时release()方法中调用了弱引用对象DefaultResourceLeak的close()方法，从allLeaks集合里面把这个弱引用对象移除。如果开发者忘记调用release()方法，则allLeaks集合里还会存在这个弱引用对象。
4. 一段时间后，ByteBuf对象被GC回收，此时会触发一个操作：ByteBuf对象所绑定的弱引用对象被加入到refQueue中。
5.下一次创建ByteBuf时又调用了track0(obj)方法，把refQueue和allLeaks这俩集合一对比，既存在于refQueue（说明ByteBuf用完了且已经被GC回收），又存在于allLeaks（说明没调用release释放内存），表明存在内存泄漏。