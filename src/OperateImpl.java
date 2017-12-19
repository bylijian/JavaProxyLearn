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
