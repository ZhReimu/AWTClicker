package com.mrx;

import com.mrx.clicker.AWTClicker;

/**
 * @author Mr.X
 * @since 2022-05-01-0001
 **/
public class Main {
    public static void main(String[] args) {
        AWTClicker clicker = new AWTClicker();
        if (args.length > 0 && args[0] != null) {
            clicker.setDelay(Integer.parseInt(args[0]));
        }
        clicker.startKeyListener();

        System.out.println("程序用法: ");
        System.out.println("ALT + D \t 设置鼠标点击位置");
        System.out.println("ALT + S \t 开始 / 停止鼠标连点");
        System.out.println("ALT + E \t 结束程序");

    }
}