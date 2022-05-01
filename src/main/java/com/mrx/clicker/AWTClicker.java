package com.mrx.clicker;

import com.melloware.jintellitype.JIntellitype;
import com.melloware.jintellitype.JIntellitypeConstants;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author Mr.X
 * @since 2022-05-01-0001
 **/
@SuppressWarnings("all")
public class AWTClicker {

    private final JIntellitype instance = JIntellitype.getInstance();

    private final ExecutorService pool = Executors.newFixedThreadPool(1);

    private Robot robot;

    private Point mousePoint;

    private long delay = 1000;

    private volatile boolean clicking = false;

    public AWTClicker() {
        runCaching(() -> robot = new Robot());
    }

    public AWTClicker setDelay(long delay) {
        this.delay = delay;
        System.out.println("点击延时为: " + delay + " ms");
        return this;
    }

    public AWTClicker startKeyListener() {
        initKeyListener();
        return this;
    }

    private void getMousePoint() {
        mousePoint = MouseInfo.getPointerInfo().getLocation();
        System.out.println("鼠标坐标已录入: x = " + mousePoint.x + " y = " + mousePoint.y);
    }

    private boolean robotPress() {
        if (mousePoint == null) {
            System.out.println("请先使用 ALT + D 设置鼠标点击位置 !");
            return false;
        }
        pool.execute(() -> {
            robot.mouseMove(mousePoint.x, mousePoint.y);
            synchronized (AWTClicker.class) {
                while (clicking) {
                    robot.mousePress(MouseEvent.BUTTON1_DOWN_MASK);
                    robot.mouseRelease(MouseEvent.BUTTON1_DOWN_MASK);
                    if (delay == 0) continue;
                    runCaching(() -> Thread.sleep(delay));
                }
            }
        });
        return true;
    }

    private void initKeyListener() {
        if (!JIntellitype.isJIntellitypeSupported()) {
            throw new IllegalStateException("系统不支持按键监听!");
        }
        System.out.println("正在监听按键事件, 点击延时为: " + delay + " ms!");
        instance.addHotKeyListener(this::onRegisterMousePosition);
        instance.registerHotKey(1, JIntellitypeConstants.MOD_ALT, KeyEvent.VK_D);
        instance.registerHotKey(2, JIntellitypeConstants.MOD_ALT, KeyEvent.VK_S);
        instance.registerHotKey(3, JIntellitypeConstants.MOD_ALT, KeyEvent.VK_E);
    }

    private void onRegisterMousePosition(int identifier) {
        switch (identifier) {
            case 1:
                if (clicking) {
                    System.out.println("连点器已启用, 无法改变点击坐标!");
                } else {
                    getMousePoint();
                }
                break;
            case 2:
                if (!clicking) {
                    if (robotPress()) {
                        clicking = true;
                        System.out.println("已开始连点");
                    }
                } else {
                    System.out.println("已停止连点");
                    clicking = false;
                }
                break;
            case 3:
                exit();
                break;
            default:
                System.out.println("出现异常! ");
                break;
        }
    }

    private void exit() {
        runCaching(() -> {
            System.out.println("退出程序");
            clicking = false;
            pool.shutdownNow();
            pool.awaitTermination(1, TimeUnit.SECONDS);
            instance.cleanUp();
            System.exit(0);
        });
    }

    private void runCaching(RunCaching r) {
        r.run();
    }

    @FunctionalInterface
    private interface RunCaching extends Runnable {
        @Override
        default void run() {
            try {
                runBlock();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        void runBlock() throws Exception;

    }

}
