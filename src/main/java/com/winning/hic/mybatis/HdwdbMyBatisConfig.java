package com.winning.hic.mybatis;

import com.alibaba.druid.pool.DruidDataSource;
import com.winning.hic.base.Constants;
import com.winning.hic.base.utils.ConfigUtils;
import com.winning.hic.model.Environment;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * Description: HDW 数据库配置
 */
@Configuration
@MapperScan(basePackages = Constants.HDW_PACKAGE, sqlSessionFactoryRef = "hdwdbSqlSessionFactory")
public class HdwdbMyBatisConfig {
    // 精确到 data 目录，以便跟其他数据源隔离

    private static final Logger logger = LoggerFactory.getLogger(HdwdbMyBatisConfig.class);
    @Bean(name = "hdwdb")
    public DataSource dataSource() throws SQLException {
        Environment env = ConfigUtils.getEnvironment();
        DruidDataSource datasource = new DruidDataSource();
        datasource.setUrl(env.getHDWDB_URL());
        datasource.setUsername(env.getHdwdbUsername());
        datasource.setPassword(env.getHdwdbPassword());
        datasource.setDriverClassName(Constants.DRIVE_CLASS_NAME);
        datasource.setInitialSize(5);
        datasource.setMinIdle(5);
        datasource.setMaxActive(20);
        datasource.setMaxWait(60000);
        datasource.setTimeBetweenEvictionRunsMillis(60000);
        datasource.setMinEvictableIdleTimeMillis(300000);
        datasource.setValidationQuery("SELECT GETDATE()");
        datasource.setTestWhileIdle(true);
        datasource.setTestOnBorrow(false);
        datasource.setTestOnReturn(false);
        datasource.setMaxPoolPreparedStatementPerConnectionSize(20);
        Properties prop = new Properties();
        prop.setProperty("druid.stat.mergeSql","true");
        prop.setProperty("druid.stat.slowSqlMillis","5000");
        datasource.setConnectProperties(prop);
        datasource.setUseGlobalDataSourceStat(true);
        datasource.setName(env.getHdwdbName());
        try {
            datasource.setFilters("stat,wall,log4j");
        } catch (SQLException e) {
            logger.error("[{}] druid configuration initialization filter", env.getHdwdbName(),e);
        }
        logger.info("[{}] inited.",env.getHdwdbName());
        return datasource;
    }

    @Bean(name = "hdwdbTransactionManager")
    public DataSourceTransactionManager hdwdbTransactionManager(@Qualifier("hdwdb") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "hdwdbSqlSessionFactory")
    public SqlSessionFactory hdwdbSqlSessionFactory(@Qualifier("hdwdb") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        //bean.setTypeAliasesPackage(PACKAGE);
        //xml路径
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(Constants.HDW_MAPPER_LOCATION));
        bean.setVfs(SpringBootVFS.class);
        return bean.getObject();
    }

}
