package com.winning.hic.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.winning.hic.model.MbzDictInfo;
import com.winning.hic.service.MbzDictInfoService;
import com.winning.hic.service.MbzLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.winning.hic.base.Constants;
import com.winning.hic.base.utils.PercentUtil;
import com.winning.hic.dao.cmdatacenter.MbzLoadDataInfoDao;
import com.winning.hic.dao.hdw.HlhtMjzcfXycfDao;
import com.winning.hic.dao.hdw.MZCommonQueryDao;
import com.winning.hic.model.HlhtMjzcfXycf;
import com.winning.hic.model.MbzDataCheck;
import com.winning.hic.model.MbzLoadDataInfo;
import com.winning.hic.service.HlhtMjzcfXycfService;
import com.winning.hic.service.MbzDataCheckService;


/**
 * @author HLHT
 * @title HLHT_MJZCF_XYCF
 * @email Winning Health
 * @package com.winning.hic.service.impl
 * @date 2018-29-31 16:29:53
 */
@Service
public class HlhtMjzcfXycfServiceImpl implements HlhtMjzcfXycfService {
    private final Logger logger = LoggerFactory.getLogger(HlhtMjzcfXycfServiceImpl.class);
    @Autowired
    private HlhtMjzcfXycfDao hlhtMjzcfXycfDao;
    @Autowired
    private MZCommonQueryDao commonQueryDao;
    @Autowired
    private MbzLoadDataInfoDao mbzLoadDataInfoDao;
    @Autowired
    private MbzDataCheckService mbzDataCheckService;
    @Autowired
    private MbzDictInfoService mbzDictInfoService;
    @Autowired
    private MbzLogService mbzLogService;

    public int createHlhtMjzcfXycf(HlhtMjzcfXycf hlhtMjzcfXycf) {
        return this.hlhtMjzcfXycfDao.insertHlhtMjzcfXycf(hlhtMjzcfXycf);
    }

    public int modifyHlhtMjzcfXycf(HlhtMjzcfXycf hlhtMjzcfXycf) {
        return this.hlhtMjzcfXycfDao.updateHlhtMjzcfXycf(hlhtMjzcfXycf);
    }

    public int removeHlhtMjzcfXycf(HlhtMjzcfXycf hlhtMjzcfXycf) {
        return this.hlhtMjzcfXycfDao.deleteHlhtMjzcfXycf(hlhtMjzcfXycf);
    }

    public HlhtMjzcfXycf getHlhtMjzcfXycf(HlhtMjzcfXycf hlhtMjzcfXycf) {
        return this.hlhtMjzcfXycfDao.selectHlhtMjzcfXycf(hlhtMjzcfXycf);
    }

    public int getHlhtMjzcfXycfCount(HlhtMjzcfXycf hlhtMjzcfXycf) {
        return (Integer) this.hlhtMjzcfXycfDao.selectHlhtMjzcfXycfCount(hlhtMjzcfXycf);
    }

    public List<HlhtMjzcfXycf> getHlhtMjzcfXycfList(HlhtMjzcfXycf hlhtMjzcfXycf) {
        return this.hlhtMjzcfXycfDao.selectHlhtMjzcfXycfList(hlhtMjzcfXycf);
    }

    public List<HlhtMjzcfXycf> getHlhtMjzcfXycfPageList(HlhtMjzcfXycf hlhtMjzcfXycf) {
        return this.hlhtMjzcfXycfDao.selectHlhtMjzcfXycfPageList(hlhtMjzcfXycf);
    }

    @Override
    public MbzDataCheck interfaceHlhtMjzcfXycf(MbzDataCheck entity) {
        //获取数据集字典表中配置，判断是否需要抽取
        MbzDictInfo mbzDictInfo = new MbzDictInfo();
        mbzDictInfo.setDictCode("platformTableName");
        mbzDictInfo.setDictValue(Constants.WN_MJZCF_XYCF_SOURCE_TYPE);
        mbzDictInfo = mbzDictInfoService.getMbzDictInfo(mbzDictInfo);
        if (mbzDictInfo == null || mbzDictInfo.getStatus() != 1) {
            //数据集不存在或者未配置需要抽取
            return new MbzDataCheck();
        }
        int emr_count = 0;//病历数量
        int real_count = 0;//实际数量
       //实际数量
        String startDate = (String) entity.getMap().get("startDate");
        startDate = startDate.replaceAll("-", "");
        startDate = startDate.replaceAll(" ", "");
        String endDate = (String) entity.getMap().get("endDate");
        endDate = endDate.replaceAll("-", "");
        endDate = endDate.replaceAll(" ", "");

        HlhtMjzcfXycf xycf = new HlhtMjzcfXycf();
        xycf.getMap().put("startDate", startDate);
        xycf.getMap().put("endDate", endDate);
        xycf.getMap().put("syxh", entity.getMap().get("syxh"));
        //日库
        List<HlhtMjzcfXycf> mjzcfXycfList = this.hlhtMjzcfXycfDao.selectHlhtMjzcfXycfListByProc(xycf);
        //年库
        List<HlhtMjzcfXycf> lsmjzcfXycfList = this.hlhtMjzcfXycfDao.selectHlhtMjzcfXycfListByProcForYear(xycf);
        mjzcfXycfList.addAll(lsmjzcfXycfList);
        emr_count = mjzcfXycfList.size();
        for (HlhtMjzcfXycf obj : mjzcfXycfList) {
            HlhtMjzcfXycf tempXycf = new HlhtMjzcfXycf();
            tempXycf.setYjlxh(obj.getYjlxh());
            this.hlhtMjzcfXycfDao.deleteHlhtMjzcfXycf(tempXycf);
            //清除日志
            Map<String, Object> param = new HashMap<>();
            param.put("SOURCE_ID", obj.getYjlxh());
            param.put("SOURCE_TYPE", Constants.WN_MJZCF_XYCF_SOURCE_TYPE);
            mbzLoadDataInfoDao.deleteMbzLoadDataInfoBySourceIdAndSourceType(param);
            try {
                this.createHlhtMjzcfXycf(obj);
            } catch (Exception e) {
                logger.error("数据入库报错,病历名称：{},源记录序号{},错误原因：{}", obj.getBlmc(), obj.getYjlxh(),e.getMessage());
                continue;
            }
            //插入日志
            try {
                mbzLoadDataInfoDao.insertMbzLoadDataInfo(new MbzLoadDataInfo(
                        Long.parseLong(Constants.WN_MJZCF_XYCF_SOURCE_TYPE),
                        Long.parseLong(obj.getYjlxh()), "西药处方", obj.getMjzh(), obj.getCfklrq(),
                        obj.getPatid(), obj.getMjzh(), obj.getHzxm(), obj.getXbmc(), obj.getXbdm(),
                        "NA", "NA", "NA", "NA", obj.getSfzhm(),
                        PercentUtil.getPercent(Long.parseLong(Constants.WN_MJZCF_XYCF_SOURCE_TYPE), obj, 1),
                        PercentUtil.getPercent(Long.parseLong(Constants.WN_MJZCF_XYCF_SOURCE_TYPE), obj, 0)));
            } catch (Exception e) {
                logger.error("病历百分比计算报错,病历名称：{},源记录序号{}", obj.getBlmc(), obj.getYjlxh());
                String log = Constants.WN_MJZCF_XYCF_SOURCE_TYPE +"||"+getClass().toString()+"||"+"病历百分比计算报错,病历名称：{"+obj.getBlmc()+"},源记录序号{"+obj.getYjlxh()+"}"+"||错误原因:{"+e.getMessage()+"}";
                mbzLogService.createMbzLog(log);
                continue;
            }
            real_count++;
        }
        //1.病历总数 2.抽取的病历数量 3.子集类型
        this.mbzDataCheckService.createMbzDataCheckNum(emr_count, real_count, Integer.parseInt(Constants.WN_MJZCF_XYCF_SOURCE_TYPE), entity);
        MbzDataCheck mbzDataCheck = new MbzDataCheck();
        mbzDataCheck.setDataCount(emr_count);
        mbzDataCheck.setRealCount(real_count);
        return mbzDataCheck;
    }
}