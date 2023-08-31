package com.system.reflect;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Accessable属性是继承自AccessibleObject 类. 功能是启用或禁用安全检查
 * 在反射对象中设置 accessible 标志允许具有足够特权的复杂应用程序（比如 Java Object Serialization 或其他持久性机制）以某种通常禁止使用的方式来操作对象。
 * setAccessible
 * public void setAccessible(boolean flag)
 * throws SecurityException
 * <p>
 * 将此对象的 accessible 标志设置为指示的布尔值。值为 true 则指示反射的对象在使用时应该取消 Java 语言访问检查。值为 false 则指示反射的对象应该实施 Java 语言访问检查。
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//        try {
//            Class<?> testReflection = Class.forName("com.system.reflect.User");
//            Constructor c = testReflection.getDeclaredConstructor(String.class);
//            c.setAccessible(true);
//            Object obj = c.newInstance("jueme");
//            Field name = testReflection.getDeclaredField("name");
//            name.setAccessible(true);
//            String nameStr = (String) name.get(obj);
//            Log.e(TAG, nameStr);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        //getField();
        //getMethod();
    }


    /**
      1,获取所有公共成员变量
      Field[] fields = c.getFields();
      2,获取所有成员变量
      Field[] fields = c.getDeclaredFields();
      3,获取指定成员变量
      Field field = c.getField("age");
     */

    /**
     testReflection.getFields();//获取所有的public变量
     testReflection.getDeclaredFields();//获取所有的变量
     testReflection.getField("name");//获取指定名字的public变量
     testReflection.getDeclaredField("name"); //获取指定名字的变量
     testReflection.getConstructors();//获取所有的public构造方法
     */

    public void getField() {
        try {
            //1.获取指定成员变量并赋值：
            // 获取字节码文件对象
            Class c = Class.forName("com.system.reflect.User");

            //获取构造器对象，创建person类对象
            Object obj = c.newInstance();

            // 获取单个age成员变量
            Field field = c.getField("name");
            field.setAccessible(true);
            // 给obj对象的field字段赋值
            field.set(obj, "小明");

            Log.e(TAG, "getField: "+field.get(obj));

        } catch (Exception exception) {
            Log.e(TAG, "exception: "+exception.getMessage() );
        }
    }


    /**
     * （1）获取所有公共方法，包括父类的方法
     *  c.getMethods();
     *
     *  （2）获取本类的所有方法
     *  c.getDeclaredMethods();
     *
     *  （3）获取指定的成员方法
     */
//    public void getMethod() {
//        try {
//            // 获取字节码文件对象
//            Class c = Class.forName("com.system.reflect.User");
//
//            // 创建对象
//            Constructor con = c.getConstructor();
//            Object obj = con.newInstance();
//
//            //第一种：无参数无返回值
//            Method m1 = c.getMethod("show", null);  //show是方法名称，后边是方法参数，null表示无参方法
//            m1.invoke(obj, null);//nvoke 主要是用来调用某个类中的方法的，但是他不是通过当前类直接去调用而是通过反射的机制去调用
//
//            //第二种：带string类型参数无返回值
//            Method m2 = c.getMethod("function", String.class);
//            m2.invoke(obj, "岳飞"); //invoke表示对方法进行调用
//
//            //第三种：带多个参数有返回值
//            Method m3 = c.getMethod("reutrnValue", String.class, int.class);
//             m3.invoke(obj, "张飞", 26);
//
//
//            //第四种：私有方法的调用
//            Method m4 = c.getDeclaredMethod("hello", null);
//            m4.setAccessible(true); //设置访问权限，这一点很重要
//            m4.invoke(obj, null);
//        }catch (Exception exception) {
//            Log.e(TAG, "exception: "+exception.getMessage() );
//        }
//    }
}