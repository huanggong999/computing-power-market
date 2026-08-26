package com.lingyang.common.datasource.config;

import com.github.pagehelper.PageInterceptor;
import com.lingyang.common.datasource.interceptor.DataScopeInterceptor;
import com.lingyang.common.datasource.interceptor.DataScopeInterface;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Properties;

/**
 * @Deprecated
 * @Author 王小龙
 * @Time 2023/3/3 14:25
 */
@Component
public class MybatisConfig implements InitializingBean {
    @Resource
    private List<SqlSessionFactory> sqlSessionFactoryList;

    private  final List<DataScopeInterface> dataScopeInterfaceList;

    @Resource
    private PageHelperConfig pageHelperConfig;

    public MybatisConfig(@Autowired(required = false) List<DataScopeInterface> dataScopeInterceptors) {
        this.dataScopeInterfaceList = dataScopeInterceptors;
    }
    @Override
    public void afterPropertiesSet() throws Exception {
        PageInterceptor pageInterceptor = new PageInterceptor();
        pageInterceptor.setProperties(this.pageHelperProperties());
        addInterceptor(pageInterceptor);
        if (ObjectUtils.isNotEmpty(dataScopeInterfaceList)) {
            addInterceptor(new DataScopeInterceptor(dataScopeInterfaceList));
        }
    }



    public Properties pageHelperProperties() {
        Properties properties = new Properties();
        properties.setProperty("support-methods-arguments", String.valueOf(pageHelperConfig.isSupportMethodsArguments()));
        properties.setProperty("reasonable", String.valueOf(pageHelperConfig.isReasonable()));
        properties.setProperty("helperDialect", pageHelperConfig.getHelperDialect());
        properties.setProperty("params", "count=co untSql");
        properties.setProperty("dialect", pageHelperConfig.getDialectClass());
        return properties;
    }


    private void addInterceptor(Interceptor interceptor) {
        for (SqlSessionFactory sqlSessionFactory : sqlSessionFactoryList) {
            sqlSessionFactory.getConfiguration().addInterceptor(interceptor);
        }
    }
}