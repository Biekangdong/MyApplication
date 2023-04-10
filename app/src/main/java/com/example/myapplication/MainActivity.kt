package com.example.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import java.util.regex.Matcher
import java.util.regex.Pattern

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val str = "19525558642"
        val pattern = "^1(34[0-8]|3[5-9]\\\\d|4[7-8]\\\\d|5[0-27-9]\\\\d|7[28]\\\\d|8[2-4,7-8]\\\\d|98\\\\d)\\\\d{7}\$";

        val r: Pattern = Pattern.compile(pattern)
        val m: Matcher = r.matcher(str)
        System.out.println("是否移动："+m.matches())
    }
}