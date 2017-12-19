# 前提
在接触到AOP，面向切面编程的时候，知道Java 有一个概念 动态代理。
在学习结束《公共技术点之Java 动态代理》后，对其应该有一个基本的了解和总结。
应该试图弄清楚下面几个问题：
* 什么是Java动态代理-》区别于Java静态代理？
* Java动态代理如何使用？
* Java动态代理简单实现原理
* Java动态代理实际应用场景
 
# 什么是Java动态代理
代理模式，比如现在我们有一个类A，实现了N方法，我们希望在类A的实现上扩展一些功能，那我们需要一个类B？
```
classs A{

public void method1(){
    //具体的业务代码
}

}

classs B{
private A a=new A();

public void method1(){

    //扩展的业务代码
     XXXXXXXX
     //
    a.method1();
}

}
```
这种需求是非常常见的，比如我们需要统计某个类所以方法的运行时间，那我们在不更改原有这个类的基础上，就要新建一个代理类。

>在某些情况下，我们不希望或是不能直接访问对象 A，而是通过访问一个中介对象 B，由 B 去访问 A 达成目的，这种方式我们就称为代理。
这里对象 A 所属类我们称为委托类，也称为被代理类，对象 B 所属类称为代理类。
代理优点有：
隐藏委托类的实现
解耦，不改变委托类代码情况下做一些额外处理，比如添加初始判断及其他公共操作
根据程序运行前代理类是否已经存在，可以将代理分为静态代理和动态代理

上面我们编写的明显就是**静态代理**。静态代理有一个非常明显的缺陷，就是太麻烦了，如果一个类有100个方法，我们都要统计它们的运行时间，那我们就必须写一个代理类，重复100次这样的代码，基本是不可接受的。

## 动态代理的产生
由于上面的静态代理有难以忍受的缺点，程序员都有一个终极愿望，写一个帮我们写代码的程序，动态代理某种程度上就是这样的。
程序非常适合做重复并且相似的工作，比如上面说的为100个方法扩展同一个的功能。

# Java动态代理如何使用
我们演示一个例子，如何扩展一个类的功能，比如，统计它每一个方法的调用时间
## 1. 定义一个接口 比如：Operate ：
```
public interface Operate {

    void method1();

    void method2(int i);

    void method3(String string);

}

```
## 2. 一个委托类 比如：OperateImpl实现接口Operate
我们需要的就是统计这个类所有方法的运行时间
```
public class OperateImpl implements Operate {
    private static final String TAG = "OperateImpl";

    @Override
    public void method1() {
        Log.debug(TAG, "method1()");
        sleep(100);
    }

    @Override
    public void method2(int i) {
        Log.debug(TAG, "method2() i=" + i);
        sleep(150);
    }

    @Override
    public void method3(String string) {
        Log.debug(TAG, "method3() String =" + string);
        sleep(200);
    }

    private void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

```
## 一个实现了 InvocationHandler接口的辅助类
我们把扩展的用于**统计运行时间**的代码写在 InvocationHandler的唯一方法invoke( )里面
```
public class TimingInvocationHandler implements InvocationHandler {
    private Object target;

    public TimingInvocationHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] objects) throws Throwable {
        long start = System.currentTimeMillis();
        Object obj = method.invoke(target, objects);

        System.out.println(method.getName() + " cost time is:" + (System.currentTimeMillis() - start));
        return obj;
    }
}

```

## 接下来见证奇迹

```

public class Main {

    public static void main(String[] args) {
        TimingInvocationHandler timingInvocationHandler = new TimingInvocationHandler(new OperateImpl());
        Operate operate = (Operate) (Proxy.newProxyInstance(Operate.class.getClassLoader(), new Class[]{Operate.class},
                timingInvocationHandler));

        // call method of proxy instance
        operate.method1();
        System.out.println();
        operate.method2(1);
        System.out.println();
        operate.method3("method 3");
    }
}
```
结果：
```
TAG: OperateImpl, Log: method1()
method1 cost time is:119

TAG: OperateImpl, Log: method2() i=1
method2 cost time is:151

TAG: OperateImpl, Log: method3() String =method 3
method3 cost time is:200

Process finished with exit code 0
```

# Java动态代理简单实现原理

静态代理的原理非常简单 无非就是 类A有N个方法，
我们为了扩展类A的功能，创建了一个代理类B，B也有N个和A一样的方法，不同的是，B的方法处理调用了A的同名方法，还自己扩展的功能

也就是：

B N个方法 -》 A N个方法

非常麻烦


动态代理就是，我不想写代理类B了，太麻烦，我希望Java帮我自动生成它
我只需要给出一个 类C，也就是我们上面的**TimingInvocationHandler **

B N个方法-》C 一个方法-》A N个方法

我们只需要需要扩展的功能写在C 的一个方法里面，自动生成的动态代理B所有的方法都会调用C这个方法，结果就是我们再也不用把重复的代码写N次了。

具体的实现原理分析，请参考：
http://www.codekk.com/blogs/detail/54cfab086c4761e5001b253d


# Java动态代理实际应用场景
非常多，比如数据库操作增删查改多个操作，我们希望添加操作前的权限检查
就可以用到动态代理
还有Android上Retrofit框架也使用到了动态代理+注解



















