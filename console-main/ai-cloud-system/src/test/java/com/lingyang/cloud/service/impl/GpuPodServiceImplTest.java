package com.lingyang.cloud.service.impl;

import com.lingyang.cloud.client.GpuPodApiClient;
import com.lingyang.cloud.client.GpuPodTenantProvider;
import com.lingyang.cloud.client.dto.InstanceResponse;
import com.lingyang.cloud.client.dto.SetInstanceNameRequest;
import com.lingyang.cloud.entity.SysCustomerGpuPodEntity;
import com.lingyang.cloud.mapper.SysCustomerGpuPodMapper;
import com.lingyang.cloud.model.dto.SetNameDTO;
import com.lingyang.common.core.security.SecurityContext;
import com.lingyang.common.core.security.authentication.MultipleSourceAuthenticationToken;
import com.lingyang.common.core.security.model.LoginUserInfoDetail;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GpuPodServiceImplTest {

    private static final String INSTANCE_ID = "instance-1";
    private static final String NEW_NAME = "renamed-instance";

    private GpuPodServiceImpl service;

    @Before
    public void setUp() throws Exception {
        LoginUserInfoDetail userInfo = new LoginUserInfoDetail();
        userInfo.setUserId(100L);
        userInfo.setUsername("tester");
        SecurityContext.setAuthentication(MultipleSourceAuthenticationToken.authenticated(userInfo));

        service = new GpuPodServiceImpl();
        setField(service, "tenantProvider", new GpuPodTenantProvider());
    }

    @After
    public void tearDown() {
        SecurityContext.clear();
    }

    @Test
    public void renameSucceedsWhenLocalRecordDoesNotExist() throws Exception {
        ExternalRenameClient client = new ExternalRenameClient();
        setField(service, "gpuPodApiClient", client);
        setField(service, "sysCustomerGpuPodMapper", mapperReturning(null));

        service.setInstanceName(INSTANCE_ID, nameRequest());

        assertTrue(client.validationCalled);
        assertTrue(client.renameCalled);
    }

    @Test
    public void renameUpdatesLocalRecordWhenItExists() throws Exception {
        ExternalRenameClient client = new ExternalRenameClient();
        SysCustomerGpuPodEntity entity = new SysCustomerGpuPodEntity();
        entity.setInstanceId(INSTANCE_ID);
        entity.setInstanceName("old-name");
        SysCustomerGpuPodEntity[] updated = new SysCustomerGpuPodEntity[1];

        setField(service, "gpuPodApiClient", client);
        setField(service, "sysCustomerGpuPodMapper", mapperReturning(entity, updated));

        service.setInstanceName(INSTANCE_ID, nameRequest());

        assertEquals(NEW_NAME, entity.getInstanceName());
        assertEquals(entity, updated[0]);
    }

    @Test
    public void validationFailureDoesNotCallExternalRename() throws Exception {
        ExternalRenameClient client = new ExternalRenameClient();
        client.instanceResponse = null;
        setField(service, "gpuPodApiClient", client);
        setField(service, "sysCustomerGpuPodMapper", mapperReturning(null));

        try {
            service.setInstanceName(INSTANCE_ID, nameRequest());
        } catch (RuntimeException expected) {
            assertFalse(client.renameCalled);
            return;
        }

        throw new AssertionError("Expected remote instance validation to fail");
    }

    @Test
    public void localSyncFailureDoesNotFailSuccessfulRename() throws Exception {
        ExternalRenameClient client = new ExternalRenameClient();
        setField(service, "gpuPodApiClient", client);
        setField(service, "sysCustomerGpuPodMapper", throwingMapper());

        service.setInstanceName(INSTANCE_ID, nameRequest());

        assertTrue(client.renameCalled);
    }

    private SetNameDTO nameRequest() {
        SetNameDTO dto = new SetNameDTO();
        dto.setName(NEW_NAME);
        return dto;
    }

    private SysCustomerGpuPodMapper mapperReturning(SysCustomerGpuPodEntity entity) {
        return mapperReturning(entity, new SysCustomerGpuPodEntity[1]);
    }

    private SysCustomerGpuPodMapper mapperReturning(
            SysCustomerGpuPodEntity entity, SysCustomerGpuPodEntity[] updated) {
        return (SysCustomerGpuPodMapper) Proxy.newProxyInstance(
                SysCustomerGpuPodMapper.class.getClassLoader(),
                new Class<?>[] {SysCustomerGpuPodMapper.class},
                (proxy, method, args) -> {
                    if ("selectOne".equals(method.getName())) {
                        return entity;
                    }
                    if ("updateById".equals(method.getName())) {
                        updated[0] = (SysCustomerGpuPodEntity) args[0];
                        return 1;
                    }
                    return defaultValue(method.getReturnType());
                });
    }

    private SysCustomerGpuPodMapper throwingMapper() {
        return (SysCustomerGpuPodMapper) Proxy.newProxyInstance(
                SysCustomerGpuPodMapper.class.getClassLoader(),
                new Class<?>[] {SysCustomerGpuPodMapper.class},
                (proxy, method, args) -> {
                    if ("selectOne".equals(method.getName())) {
                        throw new IllegalStateException("database unavailable");
                    }
                    return defaultValue(method.getReturnType());
                });
    }

    private Object defaultValue(Class<?> returnType) {
        if (returnType == boolean.class) return false;
        if (returnType == int.class) return 0;
        if (returnType == long.class) return 0L;
        return null;
    }

    private void setField(Object target, String name, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }

    private static class ExternalRenameClient extends GpuPodApiClient {
        private InstanceResponse instanceResponse = successfulInstance();
        private boolean validationCalled;
        private boolean renameCalled;

        @Override
        public InstanceResponse getInstance(String instanceId, String tenantId) {
            validationCalled = true;
            return instanceResponse;
        }

        @Override
        public void setInstanceNameExternal(String instanceId, String tenantId, SetInstanceNameRequest request) {
            renameCalled = true;
            assertEquals(NEW_NAME, request.getName());
        }

        private static InstanceResponse successfulInstance() {
            InstanceResponse response = new InstanceResponse();
            response.setSuccess(true);
            response.setInstanceId(INSTANCE_ID);
            response.setOrigin("gpu_pod");
            InstanceResponse.GpuSpec gpuSpec = new InstanceResponse.GpuSpec();
            gpuSpec.setModel("H20");
            gpuSpec.setCount(1);
            response.setGpuSpec(gpuSpec);
            return response;
        }
    }
}
