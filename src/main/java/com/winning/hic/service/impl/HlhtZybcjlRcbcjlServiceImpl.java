package com.winning.hic.service.impl;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.winning.hic.dao.hdw.SplitTableDao;
import com.winning.hic.model.*;
import com.winning.hic.service.MbzDictInfoService;
import com.winning.hic.service.MbzLogService;
import org.dom4j.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.winning.hic.base.Constants;
import com.winning.hic.base.utils.Base64Utils;
import com.winning.hic.base.utils.HicHelper;
import com.winning.hic.base.utils.PercentUtil;
import com.winning.hic.base.utils.ReflectUtil;
import com.winning.hic.base.utils.XmlUtil;

import com.winning.hic.dao.cmdatacenter.MbzDataSetDao;
import com.winning.hic.dao.cmdatacenter.MbzLoadDataInfoDao;
import com.winning.hic.dao.hdw.CommonQueryDao;
import com.winning.hic.dao.hdw.EmrQtbljlkDao;
import com.winning.hic.dao.hdw.HlhtZybcjlRcbcjlDao;
import com.winning.hic.service.HlhtZybcjlRcbcjlService;
import com.winning.hic.service.MbzDataCheckService;


/**
 * @author HLHT
 * @title HLHT_ZYBCJL_RCBCJL
 * @email Winning Health
 * @package com.winning.hic.service.impl
 * @date 2018-33-31 16:33:54
 */
@Service
public class HlhtZybcjlRcbcjlServiceImpl implements HlhtZybcjlRcbcjlService {
    private final Logger logger = LoggerFactory.getLogger(HlhtZybcjlRcbcjlServiceImpl.class);
    @Autowired
    private CommonQueryDao commonQueryDao;
    @Autowired
    private SplitTableDao splitTableDao;
    @Autowired
    private MbzDataSetDao mbzDataSetDao;
    @Autowired
    private HlhtZybcjlRcbcjlDao hlhtZybcjlRcbcjlDao;

    @Autowired
    private MbzDataCheckService mbzDataCheckService;
    @Autowired
    private MbzLoadDataInfoDao mbzLoadDataInfoDao;
    @Autowired
    private MbzDictInfoService mbzDictInfoService;
    @Autowired
    private MbzLogService mbzLogService;


    public int createHlhtZybcjlRcbcjl(HlhtZybcjlRcbcjl hlhtZybcjlRcbcjl) {
        return this.hlhtZybcjlRcbcjlDao.insertHlhtZybcjlRcbcjl(hlhtZybcjlRcbcjl);
    }

    public int modifyHlhtZybcjlRcbcjl(HlhtZybcjlRcbcjl hlhtZybcjlRcbcjl) {
        return this.hlhtZybcjlRcbcjlDao.updateHlhtZybcjlRcbcjl(hlhtZybcjlRcbcjl);
    }

    public int removeHlhtZybcjlRcbcjl(HlhtZybcjlRcbcjl hlhtZybcjlRcbcjl) {
        return this.hlhtZybcjlRcbcjlDao.deleteHlhtZybcjlRcbcjl(hlhtZybcjlRcbcjl);
    }

    public HlhtZybcjlRcbcjl getHlhtZybcjlRcbcjl(HlhtZybcjlRcbcjl hlhtZybcjlRcbcjl) {
        return this.hlhtZybcjlRcbcjlDao.selectHlhtZybcjlRcbcjl(hlhtZybcjlRcbcjl);
    }

    public int getHlhtZybcjlRcbcjlCount(HlhtZybcjlRcbcjl hlhtZybcjlRcbcjl) {
        return (Integer) this.hlhtZybcjlRcbcjlDao.selectHlhtZybcjlRcbcjlCount(hlhtZybcjlRcbcjl);
    }

    public List<HlhtZybcjlRcbcjl> getHlhtZybcjlRcbcjlList(HlhtZybcjlRcbcjl hlhtZybcjlRcbcjl) {
        return this.hlhtZybcjlRcbcjlDao.selectHlhtZybcjlRcbcjlList(hlhtZybcjlRcbcjl);
    }

    public List<HlhtZybcjlRcbcjl> getHlhtZybcjlRcbcjlPageList(HlhtZybcjlRcbcjl hlhtZybcjlRcbcjl) {
        return this.hlhtZybcjlRcbcjlDao.selectHlhtZybcjlRcbcjlPageList(hlhtZybcjlRcbcjl);
    }

    @Override
    public List<HlhtZybcjlRcbcjl> getHlhtRyjlJbxxListFromBaseData(EmrQtbljlk emrQtbljlk) throws DataAccessException {
        return this.commonQueryDao.getHlhtZybcjlRcbcjlListFromBaseData(emrQtbljlk);
    }

    @Override
    public void deleteHlhtRyjlJbxxByYjlxh(HlhtZybcjlRcbcjl hlhtZybcjlRcbcjl) {
        this.hlhtZybcjlRcbcjlDao.deleteHlhtZybcjlRcbcjl(hlhtZybcjlRcbcjl);
    }

    @Override
    public MbzDataCheck interfaceHlhtZybcjlRcbcjl(MbzDataCheck t) {
        //获取数据集字典表中配置，判断是否需要抽取
        MbzDictInfo mbzDictInfo = new MbzDictInfo();
        mbzDictInfo.setDictCode("platformTableName");
        mbzDictInfo.setDictValue(Constants.WN_ZYBCJL_RCBCJL_SOURCE_TYPE);
        mbzDictInfo = mbzDictInfoService.getMbzDictInfo(mbzDictInfo);
        if (mbzDictInfo == null || mbzDictInfo.getStatus() != 1) {
            //数据集不存在或者未配置需要抽取
            return new MbzDataCheck();
        }
        //执行过程信息记录

        int emr_count = 0;//病历数量
        int real_count = 0;//实际数量

        MbzDataSet mbzDataSet = new MbzDataSet();
        mbzDataSet.setSourceType(Constants.WN_ZYBCJL_RCBCJL_SOURCE_TYPE);
        mbzDataSet.setPId(Long.parseLong(Constants.WN_ZYBCJL_RCBCJL_SOURCE_TYPE));
        List<MbzDataSet> mbzDataSetList = this.mbzDataSetDao.selectMbzDataSetList(mbzDataSet);
        HlhtZybcjlRcbcjl hlhtZybcjlRcbcjlTemp = new HlhtZybcjlRcbcjl();
        hlhtZybcjlRcbcjlTemp.getMap().put("sourceType", Constants.WN_ZYBCJL_RCBCJL_SOURCE_TYPE);
        hlhtZybcjlRcbcjlTemp.getMap().put("startDate", t.getMap().get("startDate"));
        hlhtZybcjlRcbcjlTemp.getMap().put("endDate", t.getMap().get("endDate"));
        hlhtZybcjlRcbcjlTemp.getMap().put("syxh", t.getMap().get("syxh"));
        hlhtZybcjlRcbcjlTemp.getMap().put("yljgdm", t.getMap().get("yljgdm"));
        hlhtZybcjlRcbcjlTemp.getMap().put("regex", t.getMap().get("regex"));

        //2.根据模板代码去找到对应的病人病历
        List<HlhtZybcjlRcbcjl> hlhtZybcjlRcbcjlList = this.hlhtZybcjlRcbcjlDao.selectHlhtZybcjlRcbcjlByProc(hlhtZybcjlRcbcjlTemp);
        if (hlhtZybcjlRcbcjlList != null) {
            emr_count = emr_count + hlhtZybcjlRcbcjlList.size();
            for (HlhtZybcjlRcbcjl obj : hlhtZybcjlRcbcjlList) {
                //清库
                HlhtZybcjlRcbcjl temp = new HlhtZybcjlRcbcjl();
                temp.setYjlxh(obj.getYjlxh());
                this.hlhtZybcjlRcbcjlDao.deleteHlhtZybcjlRcbcjlByYjlxh(temp);
                //清除日志
                Map<String, Object> param = new HashMap<>();
                param.put("SOURCE_ID", obj.getYjlxh());
                param.put("SOURCE_TYPE", Constants.WN_ZYBCJL_RCBCJL_SOURCE_TYPE);
                mbzLoadDataInfoDao.deleteMbzLoadDataInfoBySourceIdAndSourceType(param);
                //3.xml文件解析 获取病历信息
                Document document = null;
                try {
                    document = XmlUtil.getDocument(Base64Utils.unzipEmrXml(obj.getBlnr()));
                } catch (IOException e) {
                    logger.error("解析病历报错,病历名称：{},源记录序号{}", obj.getBlmc(), obj.getYjlxh());
                    String log = Constants.WN_ZYBCJL_RCBCJL_SOURCE_TYPE +"||"+getClass().toString()+"||"+"解析病历报错,病历名称：{"+obj.getBlmc()+"},源记录序号{"+obj.getYjlxh()+"}"+"||错误原因:{"+e.getMessage()+"}";
                    mbzLogService.createMbzLog(log);
                    continue;
                }
                Map<String, String> paramTypeMap = ReflectUtil.getParamTypeMap(HlhtZybcjlRcbcjl.class);
                obj = (HlhtZybcjlRcbcjl) HicHelper.initModelValue(mbzDataSetList, document, obj, paramTypeMap);
                //this.createHlhtZybcjlRcbcjl(obj);
                try {
                    this.createHlhtZybcjlRcbcjl(obj);
                } catch (Exception e) {
                    logger.error("数据入库报错,病历名称：{},源记录序号{},错误原因：{}", obj.getBlmc(), obj.getYjlxh(),e.getMessage());
                    continue;
                }
                try {
                    mbzLoadDataInfoDao.insertMbzLoadDataInfo(new MbzLoadDataInfo(
                            Long.parseLong(Constants.WN_ZYBCJL_RCBCJL_SOURCE_TYPE),
                            Long.parseLong(obj.getYjlxh()), obj.getBlmc(), obj.getSyxh() + "",
                            obj.getFssj(),
                            obj.getPatid(), obj.getZyh(), obj.getHzxm(), obj.getXbmc(), obj.getXbdm(),
                            obj.getKsmc(), obj.getKsdm(), obj.getBqmc(), obj.getBqdm(), obj.getSfzhm(),
                            PercentUtil.getPercent(Long.parseLong(Constants.WN_ZYBCJL_RCBCJL_SOURCE_TYPE), obj, 1),
                            PercentUtil.getPercent(Long.parseLong(Constants.WN_ZYBCJL_RCBCJL_SOURCE_TYPE), obj, 0)));
                } catch (Exception e) {
                    logger.error("病历百分比计算报错,病历名称：{},源记录序号{}", obj.getBlmc(), obj.getYjlxh());
                    String log = Constants.WN_ZYBCJL_RCBCJL_SOURCE_TYPE +"||"+getClass().toString()+"||"+"病历百分比计算报错,病历名称：{"+obj.getBlmc()+"},源记录序号{"+obj.getYjlxh()+"}"+"||错误原因:{"+e.getMessage()+"}";
                    mbzLogService.createMbzLog(log);
                    continue;
                }
                real_count++;
            }
        }
        this.splitTableDao.selectAnmrZybcjlRcbcjlSplitByProc(hlhtZybcjlRcbcjlTemp);

        //更新dc表
        t.getMap().put("sourceType",Constants.WN_ZYBCJL_RCBCJL_SOURCE_TYPE);
        this.splitTableDao.updateDcTableData(t);
        //1.病历总数 2.抽取的病历数量 3.子集类型
        this.mbzDataCheckService.createMbzDataCheckNum(emr_count, real_count, Integer.parseInt(Constants.WN_ZYBCJL_RCBCJL_SOURCE_TYPE), t);

        MbzDataCheck mbzDataCheck = new MbzDataCheck();
        mbzDataCheck.setDataCount(emr_count);
        mbzDataCheck.setRealCount(real_count);
        return mbzDataCheck;
    }
}