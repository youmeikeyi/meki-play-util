package com.meki.play.util;

/**
 * 日志盒子
 * User: jinchao.xu
 * Date: 14-9-10
 * Time: 下午2:16
 */
public class LogBox {

    private LogBox(){}

    private static LogBox logBox = new LogBox();
    public static LogBox getInstance(){
        if (logBox == null)
            return  new LogBox();
        return logBox;
    }

    /**
     * 获取当前类名
     * 只会输出当前线程LogBox的，只能测试用
     * 非static方法可使用
     */
    public String getClassName(){
        String clazz = this.getClass().getName();
        return clazz;
    }

    /**
     * 获取实际运行的类名
     * 只会输出当前线程LogBox的，只能测试用
     * @return
     */
    public static String getRuntimeClassName(){
        String clazz = Thread.currentThread().getStackTrace()[1].getClassName();
        return  clazz;
    }

    /**
     * 获取实际运行的方法名
     * 只会输出当前线程LogBox的，只能测试用
     * @return
     */
    public static String getRuntimeMethodName(){
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        return  methodName;
    }

    /**
     * 获取实际运行时的行号
     * 只会输出当前线程LogBox的，只能测试用
     * @return
     */
    public static int getRuntimeLineNumber(){
        int lineNumber = Thread.currentThread().getStackTrace()[1].getLineNumber();
        return lineNumber;
    }

    /**
     * 获取栈跟踪信息
     * 可使用
     * @return
     */
    public static String getTraceInfo(){
        StringBuffer sb = new StringBuffer();
        StackTraceElement[] stacks = new Throwable().getStackTrace();
        int stacksLen = stacks.length;
        sb.append("#Class: " ).append(stacks[1].getClassName())
                .append("; Method: ").append(stacks[1].getMethodName())
                .append("; Line: ").append(stacks[1].getLineNumber())
                .append("; StackDepth:").append(stacksLen);
        return sb.toString();
    }

    public static void main(String[] args){
//        LogBox logBox = new LogBox();

        String clazz = Thread.currentThread().getStackTrace()[1].getClassName();
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        int lineNumber = Thread.currentThread().getStackTrace()[1].getLineNumber();
        System.out.println("class:"+ clazz+",method:"+methodName+",lineNum:"+lineNumber);

        System.out.println(LogBox.getInstance().getClassName());
        System.out.println(LogBox.getRuntimeClassName());
        System.out.println(LogBox.getRuntimeMethodName());
        System.out.println(LogBox.getRuntimeLineNumber());
        System.out.println();
        System.out.println(LogBox.getTraceInfo());
    }

}
