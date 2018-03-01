package com.diyiliu.common.util;

import javax.swing.*;
import java.awt.*;

/**
 * Description: UIHepler
 * Author: DIYILIU
 * Update: 2018-03-01 14:26
 */
public class UIHepler {

    /**
     * 设置窗口居中显示
     *
     * @param container
     */
    public static void setCenter(Container container){

        //设置窗口居中
        int WIDTH = container.getWidth();
        int HEIGHT = container.getHeight();
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screamSize = kit.getScreenSize();
        container.setBounds((screamSize.width - WIDTH) / 2, (screamSize.height - HEIGHT) / 2, WIDTH, HEIGHT);
    }


    /**
     * 样式美化
     */
    public static void beautify(){
        try {
            // 设置样式
            String ui = UIManager.getSystemLookAndFeelClassName();
            UIManager.setLookAndFeel(ui);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void beautify(String ui){
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if (ui.equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
