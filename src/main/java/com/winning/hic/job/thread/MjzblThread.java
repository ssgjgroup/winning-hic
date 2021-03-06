package com.winning.hic.job.thread;

import com.winning.hic.model.MbzDataCheck;
import com.winning.hic.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 入出院记录线程
 */
public class MjzblThread extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(MjzblThread.class);

    private MbzDataCheck entity;

    private Facade facade;

    public MjzblThread(MbzDataCheck entity, Facade facade) {
        this.entity = entity;
        this.facade = facade;
    }

    @Override
    public void run() {
        logger.info("启动线程>>>>>>>>>>>>>>>>>>>>>>>>>>>>>MjzblThread");
        try {
            //5.入院记录* --陈枫
            facade.getHlhtRyjlJbxxService().interfaceHlhtRyjlJbxx(entity);
            //31.门急诊病历记录表* --陈蒯
            facade.getHlhtMjzblMjzblService().interfaceHlhtMjzblMjzbl(entity);
            //32.急诊留观病历记录表*   --陈世杰
            facade.getHlhtMjzblJzlgblService().interfaceHlhtMjzblJzlgbl(entity);
            //36.基本健康信息表* --陈世杰
            facade.getHlhtBlgyJbjkxxService().interfaceHlhtBlgyJbjkxx(entity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("线程结束>>>>>>>>>>>>>>>>>>>>>>>>>>>>>MjzblThread");
    }
}
