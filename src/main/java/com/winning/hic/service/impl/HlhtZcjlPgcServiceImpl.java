package com.winning.hic.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.winning.hic.dao.hdw.SplitTableDao;
import com.winning.hic.model.*;
import com.winning.hic.service.*;
import org.dom4j.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.winning.hic.base.Constants;
import com.winning.hic.base.utils.Base64Utils;
import com.winning.hic.base.utils.HicHelper;
import com.winning.hic.base.utils.PercentUtil;
import com.winning.hic.base.utils.ReflectUtil;
import com.winning.hic.base.utils.XmlUtil;
import com.winning.hic.dao.cmdatacenter.MbzLoadDataInfoDao;
import com.winning.hic.dao.hdw.HlhtZcjlPgcDao;


/**
 * @author HLHT
 * @title HLHT_ZCJL_PGC
 * @email Winning Health
 * @package com.winning.hic.service.impl
 * @date 2018-30-31 16:30:59
 */
@Service
public class HlhtZcjlPgcServiceImpl implements HlhtZcjlPgcService {
    private final Logger logger = LoggerFactory.getLogger(HlhtZcjlPgcServiceImpl.class);

    @Autowired
    private HlhtZcjlPgcDao hlhtZcjlPgcDao;

    @Autowired
    private SplitTableDao splitTableDao;

    @Autowired
    private MbzDataSetService mbzDataSetService;

    @Autowired
    private MbzDataCheckService mbzDataCheckService;

    @Autowired
    private MbzLoadDataInfoDao mbzLoadDataInfoDao;
    @Autowired
    private MbzDictInfoService mbzDictInfoService;
    @Autowired
    private MbzLogService mbzLogService;

    public int createHlhtZcjlPgc(HlhtZcjlPgc hlhtZcjlPgc) {
        return this.hlhtZcjlPgcDao.insertHlhtZcjlPgc(hlhtZcjlPgc);
    }

    public int modifyHlhtZcjlPgc(HlhtZcjlPgc hlhtZcjlPgc) {
        return this.hlhtZcjlPgcDao.updateHlhtZcjlPgc(hlhtZcjlPgc);
    }

    public int removeHlhtZcjlPgc(HlhtZcjlPgc hlhtZcjlPgc) {
        return this.hlhtZcjlPgcDao.deleteHlhtZcjlPgc(hlhtZcjlPgc);
    }

    public HlhtZcjlPgc getHlhtZcjlPgc(HlhtZcjlPgc hlhtZcjlPgc) {
        return this.hlhtZcjlPgcDao.selectHlhtZcjlPgc(hlhtZcjlPgc);
    }

    public int getHlhtZcjlPgcCount(HlhtZcjlPgc hlhtZcjlPgc) {
        return (Integer) this.hlhtZcjlPgcDao.selectHlhtZcjlPgcCount(hlhtZcjlPgc);
    }

    public List<HlhtZcjlPgc> getHlhtZcjlPgcList(HlhtZcjlPgc hlhtZcjlPgc) {
        return this.hlhtZcjlPgcDao.selectHlhtZcjlPgcList(hlhtZcjlPgc);
    }

    public List<HlhtZcjlPgc> getHlhtZcjlPgcPageList(HlhtZcjlPgc hlhtZcjlPgc) {
        return this.hlhtZcjlPgcDao.selectHlhtZcjlPgcPageList(hlhtZcjlPgc);
    }

    private HlhtZcjlPgc getInitialHlhtZcjlPgc(HlhtZcjlPgc entity) {
        return this.hlhtZcjlPgcDao.selectInitialHlhtZcjlPgc(entity);
    }

    @Override
    public MbzDataCheck interfaceHlhtZcjlPgc(MbzDataCheck t) {
        //获取数据集字典表中配置，判断是否需要抽取
        MbzDictInfo mbzDictInfo = new MbzDictInfo();
        mbzDictInfo.setDictCode("platformTableName");
        mbzDictInfo.setDictValue(Constants.WN_ZCJL_PGC_SOURCE_TYPE);
        mbzDictInfo = mbzDictInfoService.getMbzDictInfo(mbzDictInfo);
        if (mbzDictInfo == null || mbzDictInfo.getStatus() != 1) {
            //数据集不存在或者未配置需要抽取
            return new MbzDataCheck();
        }
        //执行过程信息记录

        int emr_count = 0;//病历数量
        int real_count = 0;//实际数量

        MbzDataSet mbzDataSet = new MbzDataSet();
        mbzDataSet.setSourceType(Constants.WN_ZCJL_PGC_SOURCE_TYPE);
        mbzDataSet.setPId(Long.parseLong(Constants.WN_ZCJL_PGC_SOURCE_TYPE));
        List<MbzDataSet> mbzDataSetList = mbzDataSetService.getMbzDataSetList(mbzDataSet);

        //获取首次病程的对象集合
        Map<String, String> paramTypeMap = ReflectUtil.getParamTypeMap(HlhtZcjlPgc.class);
        //2.根据首次病程去找到对应的病人病历
        HlhtZcjlPgc onePgc = new HlhtZcjlPgc();
        onePgc.getMap().put("sourceType", Constants.WN_ZCJL_PGC_SOURCE_TYPE);
        onePgc.getMap().put("startDate", t.getMap().get("startDate"));
        onePgc.getMap().put("endDate", t.getMap().get("endDate"));
        onePgc.getMap().put("syxh", t.getMap().get("syxh"));
        onePgc.getMap().put("yljgdm", t.getMap().get("yljgdm"));
        onePgc.getMap().put("regex", t.getMap().get("regex"));
        List<HlhtZcjlPgc> hlhtZcjlPgcs = this.hlhtZcjlPgcDao.selectHlhtZcjlPgcListByProc(onePgc);
        if (hlhtZcjlPgcs != null) {
            emr_count = emr_count + hlhtZcjlPgcs.size();
            for (HlhtZcjlPgc obj : hlhtZcjlPgcs) {
                //清库
                HlhtZcjlPgc temp = new HlhtZcjlPgc();
                temp.setYjlxh(obj.getYjlxh());
                this.removeHlhtZcjlPgc(temp);
                //清除日志
                Map<String, Object> param = new HashMap<>();
                param.put("SOURCE_ID", obj.getYjlxh());
                param.put("SOURCE_TYPE", Constants.WN_ZCJL_PGC_SOURCE_TYPE);
                mbzLoadDataInfoDao.deleteMbzLoadDataInfoBySourceIdAndSourceType(param);

                //3.xml文件解析 获取病历信息
                Document document = null;
                try {
                    document = XmlUtil.getDocument(Base64Utils.unzipEmrXml(obj.getBlnr()));
                    obj = (HlhtZcjlPgc) HicHelper.initModelValue(mbzDataSetList, document, obj, paramTypeMap);
                } catch (Exception e) {
                    logger.error("解析病历报错,病历名称：{},源记录序号{}", obj.getBlmc(), obj.getYjlxh());
                    String log = Constants.WN_ZCJL_PGC_SOURCE_TYPE +"||"+getClass().toString()+"||"+"解析病历报错,病历名称：{"+obj.getBlmc()+"},源记录序号{"+obj.getYjlxh()+"}"+"||错误原因:{"+e.getMessage()+"}";
                    mbzLogService.createMbzLog(log);
                    continue;
                }
                try {
                    this.createHlhtZcjlPgc(obj);
                } catch (Exception e) {
                    logger.error("数据入库报错,病历名称：{},源记录序号{},错误原因：{}", obj.getBlmc(), obj.getYjlxh(), e.getMessage());
                    continue;
                }
                //插入日志
                try {
                    mbzLoadDataInfoDao.insertMbzLoadDataInfo(new MbzLoadDataInfo(
                            Long.parseLong(Constants.WN_ZCJL_PGC_SOURCE_TYPE),
                            Long.parseLong(obj.getYjlxh()), obj.getBlmc(), obj.getSyxh() + "",
                            obj.getFssj(),
                            obj.getPatid(), obj.getZyh(), "NA", "NA", "NA",
                            obj.getKsmc(), obj.getKsdm(), obj.getBqmc(), obj.getBqdm(), obj.getSfzhm(), PercentUtil.getPercent(Long.parseLong(Constants.WN_ZCJL_PGC_SOURCE_TYPE), obj, 1),
                            PercentUtil.getPercent(Long.parseLong(Constants.WN_ZCJL_PGC_SOURCE_TYPE), obj, 0)));
                } catch (Exception e) {
                    logger.error("病历百分比计算报错,病历名称：{},源记录序号{}", obj.getBlmc(), obj.getYjlxh());
                    String log = Constants.WN_ZCJL_PGC_SOURCE_TYPE +"||"+getClass().toString()+"||"+"病历百分比计算报错,病历名称：{"+obj.getBlmc()+"},源记录序号{"+obj.getYjlxh()+"}"+"||错误原因:{"+e.getMessage()+"}";
                    mbzLogService.createMbzLog(log);
                    continue;
                }
                real_count++;
            }
        }
        this.splitTableDao.selectAnmrZcjlPgcSplitByProc(onePgc);
        t.getMap().put("sourceType",Constants.WN_ZCJL_PGC_SOURCE_TYPE);
        this.splitTableDao.updateDcTableData(t);
        //1.病历总数 2.抽取的病历数量 3.子集类型
        this.mbzDataCheckService.createMbzDataCheckNum(emr_count, real_count, Integer.parseInt(Constants.WN_ZCJL_PGC_SOURCE_TYPE), t);

        MbzDataCheck mbzDataCheck = new MbzDataCheck();
        mbzDataCheck.setDataCount(emr_count);
        mbzDataCheck.setRealCount(real_count);
        return mbzDataCheck;
    }


}