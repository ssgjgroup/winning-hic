package com.winning.hic.dao.cmdatacenter;



import java.util.List;  

import org.springframework.dao.DataAccessException;  

import com.winning.hic.model.MbzDataError;  



import org.springframework.stereotype.Repository;
/**
* @author ISC [Implementation service center]
* @title DAO接口
* @email Winning Health
* @package com.winning.hic.dao.cmdatacenter
* @date 2018-07-08 16:07:19
*/
@Repository
public interface MbzDataErrorDao {

    public int insertMbzDataError(MbzDataError mbzDataError) throws DataAccessException;

    public int updateMbzDataError(MbzDataError mbzDataError) throws DataAccessException;

    public int deleteMbzDataError(MbzDataError mbzDataError) throws DataAccessException;

    public MbzDataError selectMbzDataError(MbzDataError mbzDataError) throws DataAccessException;

    public Object selectMbzDataErrorCount(MbzDataError mbzDataError) throws DataAccessException;

    public List<MbzDataError> selectMbzDataErrorList(MbzDataError mbzDataError) throws DataAccessException;

    public List<MbzDataError> selectMbzDataErrorPageList(MbzDataError mbzDataError) throws DataAccessException;
}