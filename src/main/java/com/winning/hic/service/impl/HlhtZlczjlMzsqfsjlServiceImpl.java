package com.winning.hic.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.winning.hic.dao.hdw.HlhtZlczjlMzsqfsjlDao;
import com.winning.hic.service.HlhtZlczjlMzsqfsjlService;
import com.winning.hic.service.MbzDataCheckService;


/**
 * @author HLHT
 * @title HLHT_ZLCZJL_MZSQFSJL
 * @email Winning Health
 * @package com.winning.hic.service.impl
 * @date 2018-31-31 16:31:37
 */
@Service
public class HlhtZlczjlMzsqfsjlServiceImpl implements HlhtZlczjlMzsqfsjlService {

    private final Logger logger = LoggerFactory.getLogger(HlhtZlczjlMzsqfsjlServiceImpl.class);
    @Autowired
    private CommonQueryDao commonQueryDao;
    @Autowired
    private MbzDataSetDao mbzDataSetDao;
    @Autowired
    private HlhtZlczjlMzsqfsjlDao hlhtZlczjlMzsqfsjlDao;
    @Autowired
    private MbzDataCheckService mbzDataCheckService;
    @Autowired
    private MbzLoadDataInfoDao mbzLoadDataInfoDao;
    @Autowired
    private MbzDictInfoService mbzDictInfoService;
    @Autowired
    private MbzLogService mbzLogService;

    public int createHlhtZlczjlMzsqfsjl(HlhtZlczjlMzsqfsjl hlhtZlczjlMzsqfsjl) {
        return this.hlhtZlczjlMzsqfsjlDao.insertHlhtZlczjlMzsqfsjl(hlhtZlczjlMzsqfsjl);
    }

    public int modifyHlhtZlczjlMzsqfsjl(HlhtZlczjlMzsqfsjl hlhtZlczjlMzsqfsjl) {
        return this.hlhtZlczjlMzsqfsjlDao.updateHlhtZlczjlMzsqfsjl(hlhtZlczjlMzsqfsjl);
    }

    public int removeHlhtZlczjlMzsqfsjl(HlhtZlczjlMzsqfsjl hlhtZlczjlMzsqfsjl) {
        return this.hlhtZlczjlMzsqfsjlDao.deleteHlhtZlczjlMzsqfsjl(hlhtZlczjlMzsqfsjl);
    }

    public HlhtZlczjlMzsqfsjl getHlhtZlczjlMzsqfsjl(HlhtZlczjlMzsqfsjl hlhtZlczjlMzsqfsjl) {
        return this.hlhtZlczjlMzsqfsjlDao.selectHlhtZlczjlMzsqfsjl(hlhtZlczjlMzsqfsjl);
    }

    public int getHlhtZlczjlMzsqfsjlCount(HlhtZlczjlMzsqfsjl hlhtZlczjlMzsqfsjl) {
        return (Integer) this.hlhtZlczjlMzsqfsjlDao.selectHlhtZlczjlMzsqfsjlCount(hlhtZlczjlMzsqfsjl);
    }

    public List<HlhtZlczjlMzsqfsjl> getHlhtZlczjlMzsqfsjlList(HlhtZlczjlMzsqfsjl hlhtZlczjlMzsqfsjl) {
        return this.hlhtZlczjlMzsqfsjlDao.selectHlhtZlczjlMzsqfsjlList(hlhtZlczjlMzsqfsjl);
    }

    public List<HlhtZlczjlMzsqfsjl> getHlhtZlczjlMzsqfsjlPageList(HlhtZlczjlMzsqfsjl hlhtZlczjlMzsqfsjl) {
        return this.hlhtZlczjlMzsqfsjlDao.selectHlhtZlczjlMzsqfsjlPageList(hlhtZlczjlMzsqfsjl);
    }

    @Override
    public List<HlhtZlczjlMzsqfsjl> getHlhtZlczjlMzsqfsjlListFromBaseData(EmrQtbljlk emrQtbljlk) throws DataAccessException {
        return this.commonQueryDao.getHlhtZlczjlMzsqfsjlListFromBaseData(emrQtbljlk);
    }

    @Override
    public void deleteHlhtZlczjlMzsqfsjlByYjlxh(HlhtZlczjlMzsqfsjl hlhtZlczjlMzsqfsjl) {
        this.hlhtZlczjlMzsqfsjlDao.deleteHlhtZlczjlMzsqfsjlByYjlxh(hlhtZlczjlMzsqfsjl);
    }

    @Override
    public MbzDataCheck interfaceHlhtZlczjlMzsqfsjl(MbzDataCheck t) {
        //获取数据集字典表中配置，判断是否需要抽取
        MbzDictInfo mbzDictInfo = new MbzDictInfo();
        mbzDictInfo.setDictCode("platformTableName");
        mbzDictInfo.setDictValue(Constants.WN_ZLCZJL_MZSQFSJL_SOURCE_TYPE);
        mbzDictInfo = mbzDictInfoService.getMbzDictInfo(mbzDictInfo);
        if (mbzDictInfo == null || mbzDictInfo.getStatus() != 1) {
            //数据集不存在或者未配置需要抽取
            return new MbzDataCheck();
        }
        //执行过程信息记录

        int emr_count = 0;//病历数量
        int real_count = 0;//实际数量

        MbzDataSet mbzDataSet = new MbzDataSet();
        mbzDataSet.setSourceType(Constants.WN_ZLCZJL_MZSQFSJL_SOURCE_TYPE);
        mbzDataSet.setPId(Long.parseLong(Constants.WN_ZLCZJL_MZSQFSJL_SOURCE_TYPE));
        List<MbzDataSet> mbzDataSetList = this.mbzDataSetDao.selectMbzDataSetList(mbzDataSet);
        HlhtZlczjlMzsqfsjl hlhtZlczjlMzsqfsjlTemp = new HlhtZlczjlMzsqfsjl();
        hlhtZlczjlMzsqfsjlTemp.getMap().put("sourceType", Constants.WN_ZLCZJL_MZSQFSJL_SOURCE_TYPE);
        hlhtZlczjlMzsqfsjlTemp.getMap().put("startDate", t.getMap().get("startDate"));
        hlhtZlczjlMzsqfsjlTemp.getMap().put("endDate", t.getMap().get("endDate"));
        hlhtZlczjlMzsqfsjlTemp.getMap().put("syxh", t.getMap().get("syxh"));
        //2.根据模板代码去找到对应的病人病历
        List<HlhtZlczjlMzsqfsjl> hlhtZlczjlMzsqfsjls = this.hlhtZlczjlMzsqfsjlDao.selectHlhtZlczjlMzsqfsjlListByProc(hlhtZlczjlMzsqfsjlTemp);
        if (hlhtZlczjlMzsqfsjls != null) {
            emr_count = emr_count + hlhtZlczjlMzsqfsjls.size();
            for (HlhtZlczjlMzsqfsjl obj : hlhtZlczjlMzsqfsjls) {
                //清库
                HlhtZlczjlMzsqfsjl temp = new HlhtZlczjlMzsqfsjl();
                temp.setYjlxh(obj.getYjlxh());
                this.hlhtZlczjlMzsqfsjlDao.deleteHlhtZlczjlMzsqfsjlByYjlxh(temp);
                //清除日志
                Map<String, Object> param = new HashMap<>();
                param.put("SOURCE_ID", obj.getYjlxh());
                param.put("SOURCE_TYPE", Constants.WN_ZLCZJL_MZSQFSJL_SOURCE_TYPE);
                mbzLoadDataInfoDao.deleteMbzLoadDataInfoBySourceIdAndSourceType(param);
                //3.xml文件解析 获取病历信息
                Document document = null;
                try {
                    document = XmlUtil.getDocument(Base64Utils.unzipEmrXml(obj.getBlnr()));
                } catch (IOException e) {
                    logger.error("解析病历报错,病历名称：{},源记录序号{}", obj.getBlmc(), obj.getYjlxh());
                    String log = Constants.WN_ZLCZJL_MZSQFSJL_SOURCE_TYPE +"||"+getClass().toString()+"||"+"解析病历报错,病历名称：{"+obj.getBlmc()+"},源记录序号{"+obj.getYjlxh()+"}"+"||错误原因:{"+e.getMessage()+"}";
                    mbzLogService.createMbzLog(log);
                    continue;
                }
                Map<String, String> paramTypeMap = ReflectUtil.getParamTypeMap(HlhtZlczjlMzsqfsjl.class);

                obj = (HlhtZlczjlMzsqfsjl) HicHelper.initModelValue(mbzDataSetList, document, obj, paramTypeMap);
                logger.info("Model:{}", obj);
                //this.createHlhtZlczjlMzsqfsjl(obj);
                try {
                    this.createHlhtZlczjlMzsqfsjl(obj);
                } catch (Exception e) {
                    logger.error("数据入库报错,病历名称：{},源记录序号{},错误原因：{}", obj.getBlmc(), obj.getYjlxh(), e.getMessage());
                    continue;
                }
                //插入日志
                try {
                    mbzLoadDataInfoDao.insertMbzLoadDataInfo(new MbzLoadDataInfo(
                            Long.parseLong(Constants.WN_ZLCZJL_MZSQFSJL_SOURCE_TYPE),
                            Long.parseLong(obj.getYjlxh()), obj.getBlmc(), obj.getSyxh() + "",
                            obj.getFssj(),
                            obj.getPatid(), obj.getZyh(), obj.getHzxm(), obj.getXbmc(), obj.getXbdm(),
                            obj.getKsmc(), obj.getKsdm(), obj.getBqmc(), obj.getBqdm(), obj.getSfzhm(),
                            PercentUtil.getPercent(Long.parseLong(Constants.WN_ZLCZJL_MZSQFSJL_SOURCE_TYPE), obj, 1),
                            PercentUtil.getPercent(Long.parseLong(Constants.WN_ZLCZJL_MZSQFSJL_SOURCE_TYPE), obj, 0)));
                } catch (Exception e) {
                    logger.error("病历百分比计算报错,病历名称：{},源记录序号{}", obj.getBlmc(), obj.getYjlxh());
                    String log = Constants.WN_ZLCZJL_MZSQFSJL_SOURCE_TYPE +"||"+getClass().toString()+"||"+"病历百分比计算报错,病历名称：{"+obj.getBlmc()+"},源记录序号{"+obj.getYjlxh()+"}"+"||错误原因:{"+e.getMessage()+"}";
                    mbzLogService.createMbzLog(log);
                    continue;
                }

                real_count++;
            }
        }
        //1.病历总数 2.抽取的病历数量 3.子集类型
        this.mbzDataCheckService.createMbzDataCheckNum(emr_count, real_count, Integer.parseInt(Constants.WN_ZLCZJL_MZSQFSJL_SOURCE_TYPE), t);
        MbzDataCheck mbzDataCheck = new MbzDataCheck();
        mbzDataCheck.setDataCount(emr_count);
        mbzDataCheck.setRealCount(real_count);
        return mbzDataCheck;
    }
}