package com.winning.hic.model;

import java.util.Date; 
import java.util.Date; 
import java.io.Serializable; 

import org.apache.ibatis.type.Alias; 

import com.winning.hic.model.BaseDomain;



/**
 * @author HLHT
 * @title 
 * @email Winning Health
 * @package com.winning.hic.model
 * @date 2018-19-21 11:19:22
 */
@Alias("mbzOperateLog")
public class MbzOperateLog extends BaseDomain implements Serializable {

    /**
     * 字段名：id
     * 备注: 
     * 默认值：无
     */
    private Long id;
    /**
     * 字段名：set_name
     * 备注: 数据集名称
     * 默认值：无
     */
    private String setName;
    /**
     * 字段名：start_date
     * 备注: 数据开始时间
     * 默认值：无
     */
    private String startDate;
    /**
     * 字段名：end_date
     * 备注: 数据截止时间
     * 默认值：无
     */
    private String endDate;
    /**
     * 字段名：create_time
     * 备注: 抽取开始时间
     * 默认值：(getdate())
     */
    private Date createTime;
    /**
     * 字段名：update_time
     * 备注: 抽取结束时间
     * 默认值：无
     */
    private Date updateTime;
    /**
     * 字段名：type
     * 备注: 抽取方式
     * 默认值：无
     */
    private Integer type;
    /**
     * 字段名：status
     * 备注: 抽取状态
     * 默认值：无
     */
    private Integer status;

    public MbzOperateLog (){

    }

   /**
   * 字段名：id
   * get方法
   * 备注: 
   */
   public Long getId(){

        return id;
   }

   /**
   * 字段名：id
   * set方法
   * 备注: 
   */
   public void setId(Long id){
        this.id = id;
   }
   /**
   * 字段名：set_name
   * get方法
   * 备注: 数据集名称
   */
   public String getSetName(){

        return setName;
   }

   /**
   * 字段名：set_name
   * set方法
   * 备注: 数据集名称
   */
   public void setSetName(String setName){
        this.setName = setName;
   }
   /**
   * 字段名：start_date
   * get方法
   * 备注: 数据开始时间
   */
   public String getStartDate(){

        return startDate;
   }

   /**
   * 字段名：start_date
   * set方法
   * 备注: 数据开始时间
   */
   public void setStartDate(String startDate){
        this.startDate = startDate;
   }
   /**
   * 字段名：end_date
   * get方法
   * 备注: 数据截止时间
   */
   public String getEndDate(){

        return endDate;
   }

   /**
   * 字段名：end_date
   * set方法
   * 备注: 数据截止时间
   */
   public void setEndDate(String endDate){
        this.endDate = endDate;
   }
   /**
   * 字段名：create_time
   * get方法
   * 备注: 抽取开始时间
   */
   public Date getCreateTime(){

        return createTime;
   }

   /**
   * 字段名：create_time
   * set方法
   * 备注: 抽取开始时间
   */
   public void setCreateTime(Date createTime){
        this.createTime = createTime;
   }
   /**
   * 字段名：update_time
   * get方法
   * 备注: 抽取结束时间
   */
   public Date getUpdateTime(){

        return updateTime;
   }

   /**
   * 字段名：update_time
   * set方法
   * 备注: 抽取结束时间
   */
   public void setUpdateTime(Date updateTime){
        this.updateTime = updateTime;
   }
   /**
   * 字段名：type
   * get方法
   * 备注: 抽取方式
   */
   public Integer getType(){

        return type;
   }

   /**
   * 字段名：type
   * set方法
   * 备注: 抽取方式
   */
   public void setType(Integer type){
        this.type = type;
   }
   /**
   * 字段名：status
   * get方法
   * 备注: 抽取状态
   */
   public Integer getStatus(){

        return status;
   }

   /**
   * 字段名：status
   * set方法
   * 备注: 抽取状态
   */
   public void setStatus(Integer status){
        this.status = status;
   }

}