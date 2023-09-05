package com.juai.canvastest.plt;

import java.io.Serializable;

/**
 * @ClassName PLTPoint
 * @Description TODO 内容
 * @Author biekangdong
 * @CreateDate 2023/5/19 17:49
 * @Version 1.0
 * @UpdateDate 2023/5/19 17:49
 * @UpdateRemark 更新说明
 */
public class PLTPoint implements Serializable {
    public int originalX;
    public int originalY;
    public int x;
    public int y;

    public PLTPoint(int originalX, int originalY, int x, int y) {
        this.originalX = originalX;
        this.originalY = originalY;
        this.x = x;
        this.y = y;
    }

    public PLTPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
