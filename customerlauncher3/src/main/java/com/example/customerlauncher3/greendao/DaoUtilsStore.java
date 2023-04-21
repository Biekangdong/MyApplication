package com.example.customerlauncher3.greendao;


import com.example.customerlauncher3.greendao.entity.DJCell;
import com.example.customerlauncher3.greendao.entity.DJCellDao;
import com.example.customerlauncher3.greendao.entity.DJScreens;
import com.example.customerlauncher3.greendao.entity.DJScreensDao;

public class DaoUtilsStore
{
    private volatile static DaoUtilsStore instance;

    private CommonDaoUtils<DJCell> djCellCommonDaoUtils;
    private CommonDaoUtils<DJScreens> djScreensCommonDaoUtils;

    public static DaoUtilsStore getInstance()
    {
        synchronized (DaoUtilsStore.class) {
            if(instance==null){
                instance = new DaoUtilsStore();
            }
        }

        return instance;
    }

    private DaoUtilsStore()
    {
        DaoManager mManager = DaoManager.getInstance();


        DJCellDao djDataDao = mManager.getDaoSession().getDJCellDao();
        djCellCommonDaoUtils = new CommonDaoUtils(DJCell.class,djDataDao);

        DJScreensDao djScreensDao = mManager.getDaoSession().getDJScreensDao();
        djScreensCommonDaoUtils = new CommonDaoUtils(DJScreens.class,djScreensDao);

    }


    public CommonDaoUtils<DJCell> getDjCellCommonDaoUtils() {
        return djCellCommonDaoUtils;
    }


    public CommonDaoUtils<DJScreens> getDjScreensCommonDaoUtils() {
        return djScreensCommonDaoUtils;
    }

}