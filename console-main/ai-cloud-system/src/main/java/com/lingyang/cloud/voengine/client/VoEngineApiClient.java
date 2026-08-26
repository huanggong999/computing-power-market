package com.lingyang.cloud.voengine.client;

import com.lingyang.cloud.voengine.config.VolEngineConfig;
import com.lingyang.common.core.utils.SpringUtils;
import com.lingyang.common.core.utils.StringUtils;
import com.volcengine.ApiClient;
import com.volcengine.billing.BillingApi;
import com.volcengine.clb.ClbApi;
import com.volcengine.cr.CrApi;
import com.volcengine.ecs.EcsApi;
import com.volcengine.natgateway.NatgatewayApi;
import com.volcengine.sign.Credentials;
import com.volcengine.storageebs.StorageEbsApi;
import com.volcengine.tos.TOSV2;
import com.volcengine.tos.TOSV2ClientBuilder;
import com.volcengine.vke.VkeApi;
import com.volcengine.vpc.VpcApi;
import lombok.Data;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/10/31 18:36
 */
@Data
public class VoEngineApiClient {
    private static final ApiClient API_CLIENT;
    private static final VolEngineConfig VOL_ENGINE_CONFIG;
    private static final TOSV2 TOSV2;

    static {
        VOL_ENGINE_CONFIG = SpringUtils.getBean(VolEngineConfig.class);
        API_CLIENT = new ApiClient()
                .setCredentials(Credentials.getCredentials(
                        VOL_ENGINE_CONFIG.getAccessKey(),
                        VOL_ENGINE_CONFIG.getAccessSecret())
                );
        VolEngineConfig.Tos tos = VOL_ENGINE_CONFIG.getTos();
        TOSV2 = new TOSV2ClientBuilder()
                .build(tos.getRegion(),
                        tos.getEndpoint(),
                        VOL_ENGINE_CONFIG.getAccessKey(),
                        VOL_ENGINE_CONFIG.getAccessSecret());
    }

    public static EcsApi getEcsApi() {
        return getEcsApi(null);
    }

    public static EcsApi getEcsApi(String region) {
        return new EcsApi(getApiClient(region));
    }

    public static VpcApi getVpcApi() {
        return getVpcApi(null);
    }

    public static VpcApi getVpcApi(String region) {
        return new VpcApi(getApiClient(region));
    }


    public static StorageEbsApi getStorageEbsApi() {
        return getStorageEbsApi(null);
    }

    public static StorageEbsApi getStorageEbsApi(String region) {
        return new StorageEbsApi(getApiClient(region));
    }


    public static BillingApi getBillingApi() {
        return getBillingApi(null);
    }

    public static BillingApi getBillingApi(String region) {
        return new BillingApi(getApiClient(region));
    }

    public static VkeApi getVkeApi() {
        return getVkeApi(null);
    }

    public static VkeApi getVkeApi(String region) {
        return new VkeApi(getApiClient(region));
    }

    public static CrApi getCrApi() {
        return getCrApi(null);
    }
    public static CrApi getCrApi(String region) {
        return new CrApi(getApiClient(region));
    }

    public static NatgatewayApi getNatgatewayApi() {
        return getNatgatewayApi(null);
    }
    public static NatgatewayApi getNatgatewayApi(String region) {
        return new NatgatewayApi(getApiClient(region));
    }

    public static ClbApi getClbApi() {
        return getClbApi(null);
    }
    public static ClbApi getClbApi(String region) {
        return new ClbApi(getApiClient(region));
    }

    private static ApiClient getApiClient(String region) {
        API_CLIENT.setRegion(StringUtils.isEmpty(region) ? VOL_ENGINE_CONFIG.getRegions() : region);
        return API_CLIENT;
    }

    public static TOSV2 getTosv2() {
        return TOSV2;
    }


}