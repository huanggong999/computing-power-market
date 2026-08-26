package com.lingyang.common.datasource.model;

import com.lingyang.common.core.utils.BeanUtils;
import com.lingyang.common.core.utils.ClassUtils;
import lombok.Data;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;


@Data
public class InvocationParseObject {
    private final Invocation invocation;
    private InvocationParseObject(Invocation invocation) {
        this.invocation = invocation;
    }

    public static InvocationParseObject build(Invocation invocation) {
        return new InvocationParseObject(invocation);
    }

    public String getSql() {
        return getBoundSql().getSql();
    }

    public void setSql(String sql) throws Exception {
        Object[] args = getArgs();
        if (args.length == 4 || args.length == 2) {
            MappedStatement mappedStatement = getMappedStatement();
            BoundSql boundsql = mappedStatement.getBoundSql(args[1]);
            MappedStatement newMs = newMappedStatement(mappedStatement, new BoundsqlsqlSource(boundsql));
            MetaObject msObject =  MetaObject.forObject(
                    newMs, new DefaultObjectFactory(),
                    new DefaultObjectWrapperFactory(),
                    new DefaultReflectorFactory());
            msObject.setValue("sqlSource.boundsql.sql", sql);
            args[0] = newMs;
        }else {
            BoundSql boundSql = getBoundSql();
            BeanUtils.updateProperties(boundSql, "sql", sql);
            args[5] = boundSql;
        }
    }

    private MappedStatement newMappedStatement(MappedStatement ms, SqlSource sqlSource) {
        MappedStatement.Builder builder  =
                new MappedStatement.Builder(ms.getConfiguration(), ms.getId(),sqlSource, ms.getSqlCommandType());
        builder.resource(ms.getResource());
        builder.fetchSize(ms.getFetchSize());
        builder.statementType(ms.getStatementType());
        builder.keyGenerator(ms.getKeyGenerator());
        if (ms.getKeyProperties() != null && ms.getKeyProperties().length != 0) {
            StringBuilder keyProperties = new StringBuilder();
            for (String keyProperty : ms.getKeyProperties()) {
                keyProperties.append(keyProperty).append(",");
            }
            keyProperties.delete(keyProperties.length() - 1, keyProperties.length());
            builder.keyProperty(keyProperties.toString());
        }
        builder.timeout(ms.getTimeout());
        builder.parameterMap(ms.getParameterMap());
        builder.resultMaps(ms.getResultMaps());
        builder.resultSetType(ms.getResultSetType());
        builder.cache(ms.getCache());
        builder.flushCacheRequired(ms.isFlushCacheRequired());
        builder.useCache(ms.isUseCache());
        return builder.build();
    }


    public BoundSql getBoundSql() {
        BoundSql boundSql;
        Object[] args = invocation.getArgs();
        // 判断参数，获取sql
        if(args.length == 4 || args.length == 2){
            MappedStatement ms = (MappedStatement) args[0];
            //4 个参数时
            boundSql = ms.getBoundSql(args[1]);
        } else {
            //6 个参数时
            boundSql = (BoundSql) args[5];
        }
        return boundSql;
    }
    public Object[] getArgs() {
        return invocation.getArgs();
    }

    public MappedStatement getMappedStatement() {
        return (MappedStatement) getArgs()[0];
    }

    public SqlCommandType getSqlCommandType() {
        return  getMappedStatement().getSqlCommandType();
    }

    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) throws ClassNotFoundException {
        MappedStatement mappedStatement = getMappedStatement();
        String id = mappedStatement.getId();
        String className = id.substring(0, id.lastIndexOf("."));
        Class<?> mapperClass = Class.forName(className);
        String methodName = id.substring(id.lastIndexOf(".") + 1);
        if (methodName.contains("_")) {
            methodName = methodName.split("_")[0];
        }
        Method[] methods = mapperClass.getMethods();
        Method mapperMethod = null;
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                mapperMethod = method;
                break;
            }
        }
        if (mapperMethod == null) {
            return null;
        }
        return ClassUtils.getAnnotationMethodOrClass(mapperMethod, annotationClass);
    }
}