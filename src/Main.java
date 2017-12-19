import java.lang.reflect.Proxy;

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
