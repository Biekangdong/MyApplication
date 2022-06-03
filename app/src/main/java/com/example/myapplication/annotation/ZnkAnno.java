package com.example.myapplication.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @ClassName ZnkAnno
 * @Description TODO 内容
 * @Author dong
 * @CreateDate 2022/3/9 9:27
 * @Version 1.0
 * @UpdateDate 2022/3/9 9:27
 * @UpdateRemark 更新说明
 */

/**
 * @Target：描述注解能够作用的位置
 * ElementType常用取值：
 * TYPE：可以作用在类上；
 * FIELD：可以作用在属性上；
 * METHOD：可以作用在方法上。
 */

/**
 * @Retention：描述注解被保留的阶段（java代码SOURCE(源码阶段)、CLASS(字节码文件)、RUNTIME(运行时阶段)三个阶段）
 *
 * -- @Retention(RetentionPolicy.RUNTIME) 表示当前被描述的注解会被保留到class字节码文件中，并会被JVM读取到
 * 版权声明：本文为CSDN博主「科小喵」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
 * 原文链接：https://blog.csdn.net/qq_35101450/article/details/108994206
 */

/**
 * ③@Documented：描述注解是否被抽取到api文档中
 */

/**
 * @Inherited：描述注解是否被子类继承
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE,ElementType.METHOD})
public @interface ZnkAnno {
    String value();
}
