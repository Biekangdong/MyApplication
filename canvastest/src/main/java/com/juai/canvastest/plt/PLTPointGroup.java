package com.juai.canvastest.plt;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName PLTPointList
 * @Description TODO 内容
 * @Author biekangdong
 * @CreateDate 2023/5/19 18:00
 * @Version 1.0
 * @UpdateDate 2023/5/19 18:00
 * @UpdateRemark 更新说明
 */
public class PLTPointGroup implements Serializable {
   //全局最大最小
   public int maxYLength = 0;
   public int maxXLength = 0;
   public int minX = 0;
   public int minY = 0;

   //全局开始索引
   public int index;
   //下刀点集合
   public List<PLTPoint> pltPointList;

   //组最带最小,和是否外边框
   public int groupMaxYLength = 0;
   public int groupMaxXLength = 0;
   public int groupMinX = 0;
   public int groupMinY = 0;
   public boolean isBorder;
}
