# **<font style="color:rgb(26,26,46);background-color:rgb(255,255,255);">GPU Resource Scheduler API 接口文档</font>**
<font style="color:rgb(102,102,102);background-color:rgb(255,255,255);">版本: 2.0.0  |  基础路径: </font><font style="background-color:rgb(192,192,192);">http://124.174.46.14:8000</font><font style="color:rgb(102,102,102);background-color:rgb(255,255,255);">  |  接口风格: RESTful + FastAPI </font>

**<font style="color:rgb(44,62,80);background-color:rgb(248,250,252);">目录</font>**

<font style="color:rgb(51,51,51);background-color:rgb(248,250,252);">·</font><font style="color:rgb(51,51,51);background-color:rgb(248,250,252);"> </font>[<font style="color:rgb(74,144,217);">一、总接口列表</font>](https://www.yuque.com/huangping-aznjq/gqmv5h/cvko2uzi44exm14s#summary)

<font style="color:rgb(51,51,51);background-color:rgb(248,250,252);">·</font><font style="color:rgb(51,51,51);background-color:rgb(248,250,252);"> </font>[<font style="color:rgb(74,144,217);">二、JWT 认证说明</font>](https://www.yuque.com/huangping-aznjq/gqmv5h/cvko2uzi44exm14s#auth)

<font style="color:rgb(51,51,51);background-color:rgb(248,250,252);">·</font><font style="color:rgb(51,51,51);background-color:rgb(248,250,252);"> </font>[<font style="color:rgb(74,144,217);">三、通用说明</font>](https://www.yuque.com/huangping-aznjq/gqmv5h/cvko2uzi44exm14s#common)

<font style="color:rgb(51,51,51);background-color:rgb(248,250,252);">·</font><font style="color:rgb(51,51,51);background-color:rgb(248,250,252);"> </font>[<font style="color:rgb(74,144,217);">四、JWT 认证接口</font>](https://www.yuque.com/huangping-aznjq/gqmv5h/cvko2uzi44exm14s#token-api)

<font style="color:rgb(51,51,51);background-color:rgb(248,250,252);">·</font><font style="color:rgb(51,51,51);background-color:rgb(248,250,252);"> </font>[<font style="color:rgb(74,144,217);">五、GPU Pod 管理接口</font>](https://www.yuque.com/huangping-aznjq/gqmv5h/cvko2uzi44exm14s#pod)

<font style="color:rgb(51,51,51);background-color:rgb(248,250,252);">·</font><font style="color:rgb(51,51,51);background-color:rgb(248,250,252);"> </font>[<font style="color:rgb(74,144,217);">六、算力市场接口</font>](https://www.yuque.com/huangping-aznjq/gqmv5h/cvko2uzi44exm14s#market)

<font style="color:rgb(51,51,51);background-color:rgb(248,250,252);">·</font><font style="color:rgb(51,51,51);background-color:rgb(248,250,252);"> </font>[<font style="color:rgb(74,144,217);">七、容器实例管理接口</font>](https://www.yuque.com/huangping-aznjq/gqmv5h/cvko2uzi44exm14s#instance)

<font style="color:rgb(51,51,51);background-color:rgb(248,250,252);">·</font><font style="color:rgb(51,51,51);background-color:rgb(248,250,252);"> </font>[<font style="color:rgb(74,144,217);">八、计费接口</font>](https://www.yuque.com/huangping-aznjq/gqmv5h/cvko2uzi44exm14s#billing)

<font style="color:rgb(51,51,51);background-color:rgb(248,250,252);">·</font><font style="color:rgb(51,51,51);background-color:rgb(248,250,252);"> </font>[<font style="color:rgb(74,144,217);">九、代码文件说明</font>](https://www.yuque.com/huangping-aznjq/gqmv5h/cvko2uzi44exm14s#files)

## **<font style="color:rgb(26,26,46);background-color:rgb(255,255,255);">一、总接口列表</font>**
### **<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">1. JWT </font>****<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">认证接口</font>**
| **<font style="color:rgb(44,62,80);">序号</font>** | **<font style="color:rgb(44,62,80);">方法</font>** | **<font style="color:rgb(44,62,80);">路径</font>** | **<font style="color:rgb(44,62,80);">功能说明</font>** |
| --- | --- | --- | --- |
| <font style="color:rgb(51,51,51);">0</font> | **<font style="color:rgb(255,255,255);">POST</font>** | <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">/api/v1/auth/token</font> | <font style="color:rgb(51,51,51);">租户登录获取</font><font style="color:rgb(51,51,51);"> JWT Access Token</font> |


### **<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">2. GPU Pod </font>****<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">管理接口</font>**
| **<font style="color:rgb(44,62,80);">序号</font>** | **<font style="color:rgb(44,62,80);">方法</font>** | **<font style="color:rgb(44,62,80);">路径</font>** | **<font style="color:rgb(44,62,80);">功能说明</font>** |
| --- | --- | --- | --- |
| <font style="color:rgb(51,51,51);">1</font> | **<font style="color:rgb(255,255,255);">POST</font>** | <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">/api/v1/gpu/pod/create</font> | <font style="color:rgb(51,51,51);">创建</font><font style="color:rgb(51,51,51);"> GPU Pod</font> |
| <font style="color:rgb(51,51,51);">2</font> | **<font style="color:rgb(255,255,255);">GET</font>** | <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">/api/v1/gpu/pod/{pod_name}</font> | <font style="color:rgb(51,51,51);">获取</font><font style="color:rgb(51,51,51);"> Pod </font><font style="color:rgb(51,51,51);">状态</font> |
| <font style="color:rgb(51,51,51);">3</font> | **<font style="color:rgb(255,255,255);">GET</font>** | <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">/api/v1/gpu/pod/{pod_name}/logs</font> | <font style="color:rgb(51,51,51);">获取</font><font style="color:rgb(51,51,51);"> Pod </font><font style="color:rgb(51,51,51);">日志</font> |
| <font style="color:rgb(51,51,51);">4</font> | **<font style="color:rgb(255,255,255);">ELE</font>** | <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">/api/v1/gpu/pod/{pod_name}</font> | <font style="color:rgb(51,51,51);">删除</font><font style="color:rgb(51,51,51);"> GPU Pod</font> |
| <font style="color:rgb(51,51,51);">5</font> | **<font style="color:rgb(255,255,255);">GET</font>** | <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">/api/v1/gpu/resources</font> | <font style="color:rgb(51,51,51);">获取</font><font style="color:rgb(51,51,51);"> GPU </font><font style="color:rgb(51,51,51);">资源总览</font> |
| <font style="color:rgb(51,51,51);">6</font> | **<font style="color:rgb(255,255,255);">GET</font>** | <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">/api/v1/tenant/{tenant_id}/usage</font> | <font style="color:rgb(51,51,51);">获取租户资源使用情况</font> |
| <font style="color:rgb(51,51,51);">7</font> | **<font style="color:rgb(255,255,255);">GET</font>** | <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">/api/v1/tenant/{tenant_id}/pods</font> | <font style="color:rgb(51,51,51);">列出租户的所有</font><font style="color:rgb(51,51,51);"> Pod</font> |
| <font style="color:rgb(51,51,51);">8</font> | **<font style="color:rgb(255,255,255);">GET</font>** | <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">/api/v1/gpu/models</font> | <font style="color:rgb(51,51,51);">获取支持的</font><font style="color:rgb(51,51,51);"> GPU </font><font style="color:rgb(51,51,51);">型号列表及价格</font> |
| <font style="color:rgb(51,51,51);">9</font> | **<font style="color:rgb(255,255,255);">GET</font>** | <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">/api/v1/gpu/pricing</font> | <font style="color:rgb(51,51,51);">查询指定</font><font style="color:rgb(51,51,51);"> GPU </font><font style="color:rgb(51,51,51);">价格</font> |
| <font style="color:rgb(51,51,51);">10</font> | **<font style="color:rgb(255,255,255);">GET</font>** | <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">/api/v1/health</font> | <font style="color:rgb(51,51,51);">健康检查</font> |
| <font style="color:rgb(51,51,51);">11</font> | **<font style="color:rgb(255,255,255);">GET</font>** | <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">/api/v1/debug/gpu-nodes</font> | <font style="color:rgb(51,51,51);">调试：查看</font><font style="color:rgb(51,51,51);"> GPU </font><font style="color:rgb(51,51,51);">节点详情</font> |


### **<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">3. </font>****<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">算力市场接口</font>**
| **<font style="color:rgb(44,62,80);">序号</font>** | **<font style="color:rgb(44,62,80);">方法</font>** | **<font style="color:rgb(44,62,80);">路径</font>** | **<font style="color:rgb(44,62,80);">功能说明</font>** |
| --- | --- | --- | --- |
| <font style="color:rgb(51,51,51);">12</font> | **<font style="color:rgb(255,255,255);">GET</font>** | <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">/api/v1/market/regions</font> | <font style="color:rgb(51,51,51);">获取地区列表</font> |
| <font style="color:rgb(51,51,51);">13</font> | **<font style="color:rgb(255,255,255);">GET</font>** | <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">/api/v1/market/gpu-categories</font> | <font style="color:rgb(51,51,51);">获取</font><font style="color:rgb(51,51,51);"> GPU </font><font style="color:rgb(51,51,51);">专区分类</font> |
| <font style="color:rgb(51,51,51);">14</font> | **<font style="color:rgb(255,255,255);">GET</font>** | <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">/api/v1/market/gpu-models</font> | <font style="color:rgb(51,51,51);">获取</font><font style="color:rgb(51,51,51);"> GPU </font><font style="color:rgb(51,51,51);">型号列表（含可用数量）</font> |
| <font style="color:rgb(51,51,51);">15</font> | **<font style="color:rgb(255,255,255);">GET</font>** | <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">/api/v1/market/gpu-counts</font> | <font style="color:rgb(51,51,51);">获取</font><font style="color:rgb(51,51,51);"> GPU </font><font style="color:rgb(51,51,51);">数量选项</font> |
| <font style="color:rgb(51,51,51);">16</font> | **<font style="color:rgb(255,255,255);">GET</font>** | <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">/api/v1/market/instances</font> | <font style="color:rgb(51,51,51);">获取可租用实例列表</font> |
| <font style="color:rgb(51,51,51);">17</font> | **<font style="color:rgb(255,255,255);">GET</font>** | <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">/api/v1/market/instances/{id}</font> | <font style="color:rgb(51,51,51);">获取市场实例详情</font> |
| <font style="color:rgb(51,51,51);">18</font> | **<font style="color:rgb(255,255,255);">POST</font>** | <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">/api/v1/market/rent</font> | <font style="color:rgb(51,51,51);">租用实例（创建租赁）</font> |


### **<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">4. </font>****<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">容器实例管理接口</font>**
| **<font style="color:rgb(44,62,80);">序号</font>** | **<font style="color:rgb(44,62,80);">方法</font>** | **<font style="color:rgb(44,62,80);">路径</font>** | **<font style="color:rgb(44,62,80);">功能说明</font>** |
| --- | --- | --- | --- |
| <font style="color:rgb(51,51,51);">19</font> | **<font style="color:rgb(255,255,255);">GET</font>** | <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">/api/v1/instances</font> | <font style="color:rgb(51,51,51);">获取用户实例列表</font> |
| <font style="color:rgb(51,51,51);">20</font> | **<font style="color:rgb(255,255,255);">GET</font>** | <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">/api/v1/instances/{id}</font> | <font style="color:rgb(51,51,51);">获取实例详情</font> |
| <font style="color:rgb(51,51,51);">21</font> | **<font style="color:rgb(255,255,255);">POST</font>** | <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">/api/v1/instances/{id}/start</font> | <font style="color:rgb(51,51,51);">启动实例</font> |
| <font style="color:rgb(51,51,51);">22</font> | **<font style="color:rgb(255,255,255);">POST</font>** | <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">/api/v1/instances/{id}/stop</font> | <font style="color:rgb(51,51,51);">停止实例（关机）</font> |
| <font style="color:rgb(51,51,51);">23</font> | **<font style="color:rgb(255,255,255);">POST</font>** | <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">/api/v1/instances/{id}/restart</font> | <font style="color:rgb(51,51,51);">重启实例</font> |
| <font style="color:rgb(51,51,51);">24</font> | **<font style="color:rgb(255,255,255);">POST</font>** | <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">/api/v1/instances/{id}/release</font> | <font style="color:rgb(51,51,51);">释放实例（删除，数据不可恢复）</font> |
| <font style="color:rgb(51,51,51);">25</font> | **<font style="color:rgb(255,255,255);">POST</font>** | <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">/api/v1/instances/{id}/reset-password</font> | <font style="color:rgb(51,51,51);">修改实例</font><font style="color:rgb(51,51,51);"> SSH </font><font style="color:rgb(51,51,51);">密码</font> |
| <font style="color:rgb(51,51,51);">26</font> | **<font style="color:rgb(255,255,255);">GET</font>** | <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">/api/v1/instances/{id}/ssh</font> | <font style="color:rgb(51,51,51);">获取</font><font style="color:rgb(51,51,51);"> SSH </font><font style="color:rgb(51,51,51);">登录信息</font> |
| <font style="color:rgb(51,51,51);">27</font> | **<font style="color:rgb(255,255,255);">POST</font>** | <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">/api/v1/instances/{id}/renew</font> | <font style="color:rgb(51,51,51);">实例续费</font> |
| <font style="color:rgb(51,51,51);">28</font> | **<font style="color:rgb(255,255,255);">POST</font>** | <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">/api/v1/instances/batch-renew</font> | <font style="color:rgb(51,51,51);">批量续费</font> |
| <font style="color:rgb(51,51,51);">29</font> | **<font style="color:rgb(255,255,255);">POST</font>** | <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">/api/v1/instances/{id}/shutdown-schedule</font> | <font style="color:rgb(51,51,51);">设置</font><font style="color:rgb(51,51,51);">/</font><font style="color:rgb(51,51,51);">取消定时关机</font> |
| <font style="color:rgb(51,51,51);">30</font> | **<font style="color:rgb(255,255,255);">PUT</font>** | <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">/api/v1/instances/{id}/name</font> | <font style="color:rgb(51,51,51);">修改实例显示名称</font> |
| <font style="color:rgb(51,51,51);">31</font> | **<font style="color:rgb(255,255,255);">GET</font>** | <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">/api/v1/instances/{id}/tools</font> | <font style="color:rgb(51,51,51);">获取实例快捷工具</font> |
| <font style="color:rgb(51,51,51);">32</font> | **<font style="color:rgb(255,255,255);">GET</font>** | <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">/api/v1/instances/{id}/monitor</font> | <font style="color:rgb(51,51,51);">获取实例监控数据</font> |


### **<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">5. </font>****<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">计费接口</font>**
| **<font style="color:rgb(44,62,80);">序号</font>** | **<font style="color:rgb(44,62,80);">方法</font>** | **<font style="color:rgb(44,62,80);">路径</font>** | **<font style="color:rgb(44,62,80);">功能说明</font>** |
| --- | --- | --- | --- |
| <font style="color:rgb(51,51,51);">33</font> | **<font style="color:rgb(255,255,255);">GET</font>** | <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">/api/v1/billing/records</font> | <font style="color:rgb(51,51,51);">获取租户计费记录</font> |


---

## **<font style="color:rgb(26,26,46);background-color:rgb(255,255,255);">二、</font>****<font style="color:rgb(26,26,46);background-color:rgb(255,255,255);">JWT </font>****<font style="color:rgb(26,26,46);background-color:rgb(255,255,255);">认证说明</font>**
**<font style="color:rgb(114,28,36);background-color:rgb(248,215,218);">⚠</font>****<font style="color:rgb(114,28,36);background-color:rgb(248,215,218);">️ </font>****<font style="color:rgb(114,28,36);background-color:rgb(248,215,218);">重要安全变更</font>**<font style="color:rgb(114,28,36);background-color:rgb(248,215,218);">  
</font><font style="color:rgb(114,28,36);background-color:rgb(248,215,218);">本系统已全面升级为</font><font style="color:rgb(114,28,36);background-color:rgb(248,215,218);"> JWT Bearer Token </font><font style="color:rgb(114,28,36);background-color:rgb(248,215,218);">认证，不再支持旧的 </font><font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">X-Tenant-ID</font><font style="color:rgb(114,28,36);background-color:rgb(248,215,218);"> Header </font><font style="color:rgb(114,28,36);background-color:rgb(248,215,218);">明文认证方式。 </font>

### **<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">认证流程</font>**
**<font style="color:rgb(51,51,51);background-color:rgb(255,255,255);">第一步</font>**<font style="color:rgb(51,51,51);background-color:rgb(255,255,255);">：调用</font><font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">POST /api/v1/auth/token</font><font style="color:rgb(51,51,51);background-color:rgb(255,255,255);"> </font><font style="color:rgb(51,51,51);background-color:rgb(255,255,255);">使用</font><font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">tenant_id</font><font style="color:rgb(51,51,51);background-color:rgb(255,255,255);"> + </font><font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">api_key</font><font style="color:rgb(51,51,51);background-color:rgb(255,255,255);"> </font><font style="color:rgb(51,51,51);background-color:rgb(255,255,255);">换取</font><font style="color:rgb(51,51,51);background-color:rgb(255,255,255);"> JWT Access Token</font><font style="color:rgb(51,51,51);background-color:rgb(255,255,255);">。</font>

**<font style="color:rgb(51,51,51);background-color:rgb(255,255,255);">第二步</font>**<font style="color:rgb(51,51,51);background-color:rgb(255,255,255);">：后续所有受保护接口必须在请求头中携带：</font>

Authorization: Bearer <JWT_ACCESS_TOKEN>

**<font style="color:rgb(51,51,51);background-color:rgb(255,255,255);">第三步</font>**<font style="color:rgb(51,51,51);background-color:rgb(255,255,255);">：如果请求参数或</font><font style="color:rgb(51,51,51);background-color:rgb(255,255,255);"> Body </font><font style="color:rgb(51,51,51);background-color:rgb(255,255,255);">中也传了 </font><font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">tenant_id</font><font style="color:rgb(51,51,51);background-color:rgb(255,255,255);">，后端会</font>**<font style="color:rgb(51,51,51);background-color:rgb(255,255,255);">强制校验</font>**<font style="color:rgb(51,51,51);background-color:rgb(255,255,255);">其与</font><font style="color:rgb(51,51,51);background-color:rgb(255,255,255);"> Token </font><font style="color:rgb(51,51,51);background-color:rgb(255,255,255);">中的 </font><font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">tenant_id</font><font style="color:rgb(51,51,51);background-color:rgb(255,255,255);"> </font><font style="color:rgb(51,51,51);background-color:rgb(255,255,255);">是否一致，不一致返回</font><font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">403 Forbidden</font><font style="color:rgb(51,51,51);background-color:rgb(255,255,255);">。</font>

### **<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">Token </font>****<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">载荷示例</font>**
<font style="color:rgb(199,37,78);background-color:rgb(30,30,46);">{</font>

"tenant_id": "tenant_001",

"tenant_name": "AI科技公司",

"role": "user",

"iat": 1716940000,

"exp": 1717544800,

"type": "access"

}

### **<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">环境变量配置</font>**
| **<font style="color:rgb(44,62,80);">环境变量</font>** | **<font style="color:rgb(44,62,80);">说明</font>** | **<font style="color:rgb(44,62,80);">默认值</font>** |
| --- | --- | --- |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">JWT_SECRET_KEY</font> | <font style="color:rgb(51,51,51);">JWT </font><font style="color:rgb(51,51,51);">签名密钥（生产环境必须修改为强随机字符串）</font> | <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">gpu-scheduler-jwt-secret-key-change-me-in-production</font> |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">JWT_ALGORITHM</font> | <font style="color:rgb(51,51,51);">签名算法</font> | <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">HS256</font> |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">JWT_ACCESS_TOKEN_EXPIRE_DAYS</font> | <font style="color:rgb(51,51,51);">Token </font><font style="color:rgb(51,51,51);">有效期（天）</font> | <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">7</font> |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">API_KEY</font> | <font style="color:rgb(51,51,51);">签发</font><font style="color:rgb(51,51,51);"> Token </font><font style="color:rgb(51,51,51);">时的校验密钥（演示环境使用全局密钥）</font> | <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">demo-api-key-2024</font> |


**<font style="color:rgb(133,100,4);background-color:rgb(255,243,205);">生产环境建议</font>**<font style="color:rgb(133,100,4);background-color:rgb(255,243,205);">：务必修改</font><font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">JWT_SECRET_KEY</font><font style="color:rgb(133,100,4);background-color:rgb(255,243,205);"> 和</font><font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">API_KEY</font><font style="color:rgb(133,100,4);background-color:rgb(255,243,205);">，使用 32 字节以上的强随机字符串。建议将 </font><font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">API_KEY</font><font style="color:rgb(133,100,4);background-color:rgb(255,243,205);"> 校验替换为数据库中的租户独立密码/AKSK，或对接统一认证平台（SSO/OAuth2）。 </font>

---

## **<font style="color:rgb(26,26,46);background-color:rgb(255,255,255);">三、通用说明</font>**
### **<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">计费模式（</font>****<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">BillingMode</font>****<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">）</font>**
| **<font style="color:rgb(44,62,80);">值</font>** | **<font style="color:rgb(44,62,80);">说明</font>** |
| --- | --- |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">on_demand</font> | <font style="color:rgb(51,51,51);">按量计费</font> |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">hourly</font> | <font style="color:rgb(51,51,51);">按小时</font> |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">daily</font> | <font style="color:rgb(51,51,51);">包日</font> |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">weekly</font> | <font style="color:rgb(51,51,51);">包周</font> |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">monthly</font> | <font style="color:rgb(51,51,51);">包月</font> |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">yearly</font> | <font style="color:rgb(51,51,51);">包年</font> |


### **<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">地区编码（</font>****<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">Region</font>****<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">）</font>**
| **<font style="color:rgb(44,62,80);">编码</font>** | **<font style="color:rgb(44,62,80);">名称</font>** |
| --- | --- |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">xibei-b</font> | <font style="color:rgb(51,51,51);">西北</font><font style="color:rgb(51,51,51);">B</font><font style="color:rgb(51,51,51);">区</font> |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">beijing-b</font> | <font style="color:rgb(51,51,51);">北京</font><font style="color:rgb(51,51,51);">B</font><font style="color:rgb(51,51,51);">区</font> |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">chongqing-a</font> | <font style="color:rgb(51,51,51);">重庆</font><font style="color:rgb(51,51,51);">A</font><font style="color:rgb(51,51,51);">区</font> |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">neimeng-b</font> | <font style="color:rgb(51,51,51);">内蒙</font><font style="color:rgb(51,51,51);">B</font><font style="color:rgb(51,51,51);">区</font> |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">beijing-a</font> | <font style="color:rgb(51,51,51);">北京</font><font style="color:rgb(51,51,51);">A</font><font style="color:rgb(51,51,51);">区</font> |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">foshan</font> | <font style="color:rgb(51,51,51);">佛山区</font> |


### **<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">实例状态（</font>****<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">InstanceStatus</font>****<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">）</font>**
| **<font style="color:rgb(44,62,80);">状态</font>** | **<font style="color:rgb(44,62,80);">说明</font>** |
| --- | --- |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">running</font> | <font style="color:rgb(51,51,51);">运行中</font> |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">stopped</font> | <font style="color:rgb(51,51,51);">已停止</font> |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">stopping</font> | <font style="color:rgb(51,51,51);">停止中</font> |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">starting</font> | <font style="color:rgb(51,51,51);">启动中</font> |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">creating</font> | <font style="color:rgb(51,51,51);">创建中</font> |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">error</font> | <font style="color:rgb(51,51,51);">异常</font> |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">releasing</font> | <font style="color:rgb(51,51,51);">释放中</font> |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">released</font> | <font style="color:rgb(51,51,51);">已释放</font> |


---

## **<font style="color:rgb(26,26,46);background-color:rgb(255,255,255);">四、</font>****<font style="color:rgb(26,26,46);background-color:rgb(255,255,255);">JWT </font>****<font style="color:rgb(26,26,46);background-color:rgb(255,255,255);">认证接口</font>**
### **<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">0. </font>****<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">租户登录获取 </font>****<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">JWT Token</font>**
**<font style="color:rgb(255,255,255);background-color:rgb(255,255,255);">POST</font>**<font style="color:rgb(51,51,51);background-color:rgb(255,255,255);"> </font><font style="color:rgb(44,62,80);background-color:rgb(244,244,244);">/api/v1/auth/token</font>

<font style="color:rgb(51,51,51);background-color:rgb(255,255,255);">租户通过</font><font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">api_key</font><font style="color:rgb(51,51,51);background-color:rgb(255,255,255);"> </font><font style="color:rgb(51,51,51);background-color:rgb(255,255,255);">换取</font><font style="color:rgb(51,51,51);background-color:rgb(255,255,255);"> JWT Access Token</font><font style="color:rgb(51,51,51);background-color:rgb(255,255,255);">，用于后续所有受保护接口的认证。</font>

#### **<font style="color:rgb(52,73,94);background-color:rgb(255,255,255);">请求参数</font>**
| **<font style="color:rgb(44,62,80);">参数</font>** | **<font style="color:rgb(44,62,80);">类型</font>** | **<font style="color:rgb(44,62,80);">必填</font>** | **<font style="color:rgb(44,62,80);">说明</font>** |
| --- | --- | --- | --- |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">tenant_id</font> | <font style="color:rgb(51,51,51);">string</font> | **<font style="color:rgb(231,76,60);">是</font>** | <font style="color:rgb(51,51,51);">租户</font><font style="color:rgb(51,51,51);">ID</font> |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">tenant_name</font> | <font style="color:rgb(51,51,51);">string</font> | <font style="color:rgb(127,140,141);">否</font> | <font style="color:rgb(51,51,51);">租户名称（可选）</font> |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">api_key</font> | <font style="color:rgb(51,51,51);">string</font> | **<font style="color:rgb(231,76,60);">是</font>** | <font style="color:rgb(51,51,51);">租户</font><font style="color:rgb(51,51,51);">API</font><font style="color:rgb(51,51,51);">密钥，用于签发</font><font style="color:rgb(51,51,51);">Token</font><font style="color:rgb(51,51,51);">（演示环境使用全局 </font><font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">API_KEY</font><font style="color:rgb(51,51,51);">）</font> |


#### **<font style="color:rgb(52,73,94);background-color:rgb(255,255,255);">请求示例</font>**
{

"tenant_id": "tenant_001",

"tenant_name": "AI科技公司",

"api_key": "demo-api-key-2024"

}

#### **<font style="color:rgb(52,73,94);background-color:rgb(255,255,255);">响应示例</font>**
{

"access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",

"token_type": "bearer",

"expires_in": 604800,

"tenant_id": "tenant_001",

"tenant_name": "AI科技公司"

}

---

## **<font style="color:rgb(26,26,46);background-color:rgb(255,255,255);">五、</font>****<font style="color:rgb(26,26,46);background-color:rgb(255,255,255);">GPU Pod </font>****<font style="color:rgb(26,26,46);background-color:rgb(255,255,255);">管理接口</font>**
### **<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">1. </font>****<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">创建 </font>****<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">GPU Pod</font>**
**<font style="color:rgb(255,255,255);background-color:rgb(255,255,255);">POST</font>**<font style="color:rgb(51,51,51);background-color:rgb(255,255,255);"> </font><font style="color:rgb(44,62,80);background-color:rgb(244,244,244);">/api/v1/gpu/pod/create</font>

<font style="color:rgb(51,51,51);background-color:rgb(255,255,255);">创建</font><font style="color:rgb(51,51,51);background-color:rgb(255,255,255);"> GPU Pod</font><font style="color:rgb(51,51,51);background-color:rgb(255,255,255);">（原有接口，兼容 </font><font style="color:rgb(51,51,51);background-color:rgb(255,255,255);">K8s </font><font style="color:rgb(51,51,51);background-color:rgb(255,255,255);">集群）</font>

**<font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">认证要求</font>**<font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">  
</font><font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">请求头必须携带</font><font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">Authorization: Bearer <JWT_ACCESS_TOKEN></font><font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">  
</font><font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">Body 中的 </font><font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">tenant_id</font><font style="color:rgb(26,82,118);background-color:rgb(232,244,253);"> 必须与 Token 中的租户ID一致 </font>

#### **<font style="color:rgb(52,73,94);background-color:rgb(255,255,255);">请求参数</font>**
| **<font style="color:rgb(44,62,80);">参数</font>** | **<font style="color:rgb(44,62,80);">类型</font>** | **<font style="color:rgb(44,62,80);">必填</font>** | **<font style="color:rgb(44,62,80);">说明</font>** |
| --- | --- | --- | --- |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">tenant_id</font> | <font style="color:rgb(51,51,51);">string</font> | **<font style="color:rgb(231,76,60);">是</font>** | <font style="color:rgb(51,51,51);">租户</font><font style="color:rgb(51,51,51);">ID</font><font style="color:rgb(51,51,51);">，必须与 </font><font style="color:rgb(51,51,51);">Token </font><font style="color:rgb(51,51,51);">中的 </font><font style="color:rgb(51,51,51);">tenant_id </font><font style="color:rgb(51,51,51);">一致</font> |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">tenant_name</font> | <font style="color:rgb(51,51,51);">string</font> | <font style="color:rgb(127,140,141);">否</font> | <font style="color:rgb(51,51,51);">租户名称</font> |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">gpu_spec.model</font> | <font style="color:rgb(51,51,51);">string</font> | **<font style="color:rgb(231,76,60);">是</font>** | <font style="color:rgb(51,51,51);">GPU</font><font style="color:rgb(51,51,51);">型号，如 </font><font style="color:rgb(51,51,51);">RTX-PRO-6000</font> |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">gpu_spec.count</font> | <font style="color:rgb(51,51,51);">int</font> | **<font style="color:rgb(231,76,60);">是</font>** | <font style="color:rgb(51,51,51);">GPU</font><font style="color:rgb(51,51,51);">数量，可选 </font><font style="color:rgb(51,51,51);">1,2,3,4,5,6,7,8,10,12</font> |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">image</font> | <font style="color:rgb(51,51,51);">string</font> | **<font style="color:rgb(231,76,60);">是</font>** | <font style="color:rgb(51,51,51);">容器镜像地址</font> |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">billing.mode</font> | <font style="color:rgb(51,51,51);">string</font> | **<font style="color:rgb(231,76,60);">是</font>** | <font style="color:rgb(51,51,51);">计费模式</font> |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">billing.duration</font> | <font style="color:rgb(51,51,51);">int</font> | <font style="color:rgb(51,51,51);">条件</font> | <font style="color:rgb(51,51,51);">包周期模式时必须指定</font> |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">resource.cpu</font> | <font style="color:rgb(51,51,51);">string</font> | <font style="color:rgb(127,140,141);">否</font> | <font style="color:rgb(51,51,51);">CPU</font><font style="color:rgb(51,51,51);">核数，默认 </font><font style="color:rgb(51,51,51);">4</font> |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">resource.memory</font> | <font style="color:rgb(51,51,51);">string</font> | <font style="color:rgb(127,140,141);">否</font> | <font style="color:rgb(51,51,51);">内存，默认</font><font style="color:rgb(51,51,51);"> 16Gi</font> |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">resource.system_disk</font> | <font style="color:rgb(51,51,51);">string</font> | <font style="color:rgb(127,140,141);">否</font> | <font style="color:rgb(51,51,51);">系统盘，默认</font><font style="color:rgb(51,51,51);"> 30Gi</font> |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">resource.data_disk</font> | <font style="color:rgb(51,51,51);">string</font> | <font style="color:rgb(127,140,141);">否</font> | <font style="color:rgb(51,51,51);">数据盘，默认</font><font style="color:rgb(51,51,51);"> 50Gi</font> |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">resource.data_disk_expandable</font> | <font style="color:rgb(51,51,51);">string</font> | <font style="color:rgb(127,140,141);">否</font> | <font style="color:rgb(51,51,51);">可扩容上限</font> |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">pod_name</font> | <font style="color:rgb(51,51,51);">string</font> | <font style="color:rgb(127,140,141);">否</font> | <font style="color:rgb(51,51,51);">自定义</font><font style="color:rgb(51,51,51);">Pod</font><font style="color:rgb(51,51,51);">名称</font> |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">region</font> | <font style="color:rgb(51,51,51);">string</font> | <font style="color:rgb(127,140,141);">否</font> | <font style="color:rgb(51,51,51);">地区编码</font> |


#### **<font style="color:rgb(52,73,94);background-color:rgb(255,255,255);">请求示例</font>**
<font style="color:rgb(199,37,78);background-color:rgb(30,30,46);">{</font>

"tenant_id": "tenant_001",

"tenant_name": "AI科技公司",

"gpu_spec": {

"model": "RTX-PRO-6000",

"count": 1

},

"image": "acs-sjkuu-cn-beijing.cr.volces.com/gpu/base/pytorch:2.5.0-cuda12.4-cudnn9-devel",

"billing": {

"mode": "on_demand",

"duration": 1

},

"resource": {

"cpu": "22",

"memory": "110Gi",

"system_disk": "30Gi",

"data_disk": "50Gi",

"data_disk_expandable": "139 GB"

},

"pod_name": "ai-job-001",

"region": "xibei-b"

}

#### **<font style="color:rgb(52,73,94);background-color:rgb(255,255,255);">响应示例</font>**
{

"success": true,

"pod_name": "ai-job-001",

"pod_namespace": "gpu-tenant-tenant-001",

"message": "GPU Pod 创建成功，分配 1 张 RTX-PRO-6000 GPU",

"gpu_info": {

"model": "RTX-PRO-6000",

"count": 1

},

"billing_info": {

"gpu_model": "RTX-PRO-6000",

"gpu_count": 1,

"billing_mode": "on_demand",

"unit_price": 9.5,

"discount_unit_price": 7.13,

"total_cost": 9.5,

"discount_total_cost": 7.13,

"currency": "CNY",

"unit": "小时"

}

}

### **<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">2. </font>****<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">获取 </font>****<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">Pod </font>****<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">状态</font>**
**<font style="color:rgb(255,255,255);background-color:rgb(255,255,255);">GET</font>**<font style="color:rgb(51,51,51);background-color:rgb(255,255,255);"> </font><font style="color:rgb(44,62,80);background-color:rgb(244,244,244);">/api/v1/gpu/pod/{pod_name}?tenant_id={tenant_id}</font>

**<font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">认证要求</font>**<font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">：请求头必须携带</font><font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">Authorization: Bearer <token></font><font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">，且</font><font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">tenant_id</font><font style="color:rgb(26,82,118);background-color:rgb(232,244,253);"> 必须与 Token 一致 </font>

| **<font style="color:rgb(44,62,80);">参数</font>** | **<font style="color:rgb(44,62,80);">类型</font>** | **<font style="color:rgb(44,62,80);">必填</font>** | **<font style="color:rgb(44,62,80);">说明</font>** |
| --- | --- | --- | --- |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">pod_name</font> | <font style="color:rgb(51,51,51);">path</font> | **<font style="color:rgb(231,76,60);">是</font>** | <font style="color:rgb(51,51,51);">Pod</font><font style="color:rgb(51,51,51);">名称</font> |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">tenant_id</font> | <font style="color:rgb(51,51,51);">query</font> | **<font style="color:rgb(231,76,60);">是</font>** | <font style="color:rgb(51,51,51);">租户</font><font style="color:rgb(51,51,51);">ID</font><font style="color:rgb(51,51,51);">，必须与 </font><font style="color:rgb(51,51,51);">Token </font><font style="color:rgb(51,51,51);">中的 </font><font style="color:rgb(51,51,51);">tenant_id </font><font style="color:rgb(51,51,51);">一致</font> |


### **<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">3. </font>****<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">获取 </font>****<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">Pod </font>****<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">日志</font>**
**<font style="color:rgb(255,255,255);background-color:rgb(255,255,255);">GET</font>**<font style="color:rgb(51,51,51);background-color:rgb(255,255,255);"> </font><font style="color:rgb(44,62,80);background-color:rgb(244,244,244);">/api/v1/gpu/pod/{pod_name}/logs?tenant_id={tenant_id}&tail_lines=100</font>

**<font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">认证要求</font>**<font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">：请求头必须携带</font><font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">Authorization: Bearer <token></font><font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">，且</font><font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">tenant_id</font><font style="color:rgb(26,82,118);background-color:rgb(232,244,253);"> 必须与 Token 一致 </font>

| **<font style="color:rgb(44,62,80);">参数</font>** | **<font style="color:rgb(44,62,80);">类型</font>** | **<font style="color:rgb(44,62,80);">必填</font>** | **<font style="color:rgb(44,62,80);">说明</font>** |
| --- | --- | --- | --- |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">pod_name</font> | <font style="color:rgb(51,51,51);">path</font> | **<font style="color:rgb(231,76,60);">是</font>** | <font style="color:rgb(51,51,51);">Pod</font><font style="color:rgb(51,51,51);">名称</font> |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">tenant_id</font> | <font style="color:rgb(51,51,51);">query</font> | **<font style="color:rgb(231,76,60);">是</font>** | <font style="color:rgb(51,51,51);">租户</font><font style="color:rgb(51,51,51);">ID</font><font style="color:rgb(51,51,51);">，必须与 </font><font style="color:rgb(51,51,51);">Token </font><font style="color:rgb(51,51,51);">中的 </font><font style="color:rgb(51,51,51);">tenant_id </font><font style="color:rgb(51,51,51);">一致</font> |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">tail_lines</font> | <font style="color:rgb(51,51,51);">query</font> | <font style="color:rgb(127,140,141);">否</font> | <font style="color:rgb(51,51,51);">返回最后</font><font style="color:rgb(51,51,51);">N</font><font style="color:rgb(51,51,51);">行，默认</font><font style="color:rgb(51,51,51);">100</font> |


### **<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">4. </font>****<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">删除 </font>****<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">GPU Pod</font>**
**<font style="color:rgb(255,255,255);background-color:rgb(255,255,255);">DELETE</font>**<font style="color:rgb(51,51,51);background-color:rgb(255,255,255);"> </font><font style="color:rgb(44,62,80);background-color:rgb(244,244,244);">/api/v1/gpu/pod/{pod_name}?tenant_id={tenant_id}</font>

**<font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">认证要求</font>**<font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">：请求头必须携带</font><font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">Authorization: Bearer <token></font><font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">，且</font><font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">tenant_id</font><font style="color:rgb(26,82,118);background-color:rgb(232,244,253);"> 必须与 Token 一致 </font>

| **<font style="color:rgb(44,62,80);">参数</font>** | **<font style="color:rgb(44,62,80);">类型</font>** | **<font style="color:rgb(44,62,80);">必填</font>** | **<font style="color:rgb(44,62,80);">说明</font>** |
| --- | --- | --- | --- |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">pod_name</font> | <font style="color:rgb(51,51,51);">path</font> | **<font style="color:rgb(231,76,60);">是</font>** | <font style="color:rgb(51,51,51);">Pod</font><font style="color:rgb(51,51,51);">名称</font> |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">tenant_id</font> | <font style="color:rgb(51,51,51);">query</font> | **<font style="color:rgb(231,76,60);">是</font>** | <font style="color:rgb(51,51,51);">租户</font><font style="color:rgb(51,51,51);">ID</font><font style="color:rgb(51,51,51);">，必须与 </font><font style="color:rgb(51,51,51);">Token </font><font style="color:rgb(51,51,51);">中的 </font><font style="color:rgb(51,51,51);">tenant_id </font><font style="color:rgb(51,51,51);">一致</font> |


### **<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">5. </font>****<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">获取 </font>****<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">GPU </font>****<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">资源总览</font>**
**<font style="color:rgb(255,255,255);background-color:rgb(255,255,255);">GET</font>**<font style="color:rgb(51,51,51);background-color:rgb(255,255,255);"> </font><font style="color:rgb(44,62,80);background-color:rgb(244,244,244);">/api/v1/gpu/resources?gpu_model={gpu_model}</font>

| **<font style="color:rgb(44,62,80);">参数</font>** | **<font style="color:rgb(44,62,80);">类型</font>** | **<font style="color:rgb(44,62,80);">必填</font>** | **<font style="color:rgb(44,62,80);">说明</font>** |
| --- | --- | --- | --- |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">gpu_model</font> | <font style="color:rgb(51,51,51);">query</font> | <font style="color:rgb(127,140,141);">否</font> | <font style="color:rgb(51,51,51);">按</font><font style="color:rgb(51,51,51);">GPU</font><font style="color:rgb(51,51,51);">型号筛选</font> |


### **<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">6. </font>****<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">获取租户资源使用情况</font>**
**<font style="color:rgb(255,255,255);background-color:rgb(255,255,255);">GET</font>**<font style="color:rgb(51,51,51);background-color:rgb(255,255,255);"> </font><font style="color:rgb(44,62,80);background-color:rgb(244,244,244);">/api/v1/tenant/{tenant_id}/usage</font>

**<font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">认证要求</font>**<font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">：请求头必须携带</font><font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">Authorization: Bearer <token></font><font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">，且</font><font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">tenant_id</font><font style="color:rgb(26,82,118);background-color:rgb(232,244,253);"> 必须与 Token 一致 </font>

### **<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">7. </font>****<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">列出租户的所有 </font>****<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">Pod</font>**
**<font style="color:rgb(255,255,255);background-color:rgb(255,255,255);">GET</font>**<font style="color:rgb(51,51,51);background-color:rgb(255,255,255);"> </font><font style="color:rgb(44,62,80);background-color:rgb(244,244,244);">/api/v1/tenant/{tenant_id}/pods</font>

**<font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">认证要求</font>**<font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">：请求头必须携带</font><font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">Authorization: Bearer <token></font><font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">，且</font><font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">tenant_id</font><font style="color:rgb(26,82,118);background-color:rgb(232,244,253);"> 必须与 Token 一致 </font>

### **<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">8. </font>****<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">获取支持的 </font>****<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">GPU </font>****<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">型号列表及价格</font>**
**<font style="color:rgb(255,255,255);background-color:rgb(255,255,255);">GET</font>**<font style="color:rgb(51,51,51);background-color:rgb(255,255,255);"> </font><font style="color:rgb(44,62,80);background-color:rgb(244,244,244);">/api/v1/gpu/models</font>

### **<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">9. </font>****<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">查询指定 </font>****<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">GPU </font>****<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">价格</font>**
**<font style="color:rgb(255,255,255);background-color:rgb(255,255,255);">GET</font>**<font style="color:rgb(51,51,51);background-color:rgb(255,255,255);"> </font><font style="color:rgb(44,62,80);background-color:rgb(244,244,244);">/api/v1/gpu/pricing?gpu_model={gpu_model}&gpu_count={gpu_count}&billing_mode={billing_mode}</font>

| **<font style="color:rgb(44,62,80);">参数</font>** | **<font style="color:rgb(44,62,80);">类型</font>** | **<font style="color:rgb(44,62,80);">必填</font>** | **<font style="color:rgb(44,62,80);">说明</font>** |
| --- | --- | --- | --- |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">gpu_model</font> | <font style="color:rgb(51,51,51);">query</font> | **<font style="color:rgb(231,76,60);">是</font>** | <font style="color:rgb(51,51,51);">GPU</font><font style="color:rgb(51,51,51);">型号</font> |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">gpu_count</font> | <font style="color:rgb(51,51,51);">query</font> | <font style="color:rgb(127,140,141);">否</font> | <font style="color:rgb(51,51,51);">GPU</font><font style="color:rgb(51,51,51);">数量，默认</font><font style="color:rgb(51,51,51);">1</font> |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">billing_mode</font> | <font style="color:rgb(51,51,51);">query</font> | <font style="color:rgb(127,140,141);">否</font> | <font style="color:rgb(51,51,51);">指定计费模式，不填返回所有模式</font> |


### **<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">10. </font>****<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">健康检查</font>**
**<font style="color:rgb(255,255,255);background-color:rgb(255,255,255);">GET</font>**<font style="color:rgb(51,51,51);background-color:rgb(255,255,255);"> </font><font style="color:rgb(44,62,80);background-color:rgb(244,244,244);">/api/v1/health</font>

### **<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">11. </font>****<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">调试：查看 </font>****<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">GPU </font>****<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">节点详情</font>**
**<font style="color:rgb(255,255,255);background-color:rgb(255,255,255);">GET</font>**<font style="color:rgb(51,51,51);background-color:rgb(255,255,255);"> </font><font style="color:rgb(44,62,80);background-color:rgb(244,244,244);">/api/v1/debug/gpu-nodes</font>

<font style="color:rgb(133,100,4);background-color:rgb(255,243,205);">⚠</font><font style="color:rgb(133,100,4);background-color:rgb(255,243,205);">️ </font><font style="color:rgb(133,100,4);background-color:rgb(255,243,205);">建议仅限管理员访问</font>

---

## **<font style="color:rgb(26,26,46);background-color:rgb(255,255,255);">六、算力市场接口</font>**
### **<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">12. </font>****<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">获取地区列表</font>**
**<font style="color:rgb(255,255,255);background-color:rgb(255,255,255);">GET</font>**<font style="color:rgb(51,51,51);background-color:rgb(255,255,255);"> </font><font style="color:rgb(44,62,80);background-color:rgb(244,244,244);">/api/v1/market/regions</font>

#### **<font style="color:rgb(52,73,94);background-color:rgb(255,255,255);">响应示例</font>**
{

"regions": [

{"code": "xibei-b", "name": "西北B区", "zones": ["C81", "D43", "A12", "B09"]},

{"code": "beijing-b", "name": "北京B区", "zones": ["B01", "B02", "B03"]},

{"code": "chongqing-a", "name": "重庆A区", "zones": ["A01", "A02"]},

{"code": "neimeng-b", "name": "内蒙B区", "zones": ["M01", "M02"]},

{"code": "beijing-a", "name": "北京A区", "zones": ["A01", "A02", "A03"]},

{"code": "foshan", "name": "佛山区", "zones": ["F01", "F02"]}

]

}

### **<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">13. </font>****<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">获取 </font>****<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">GPU </font>****<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">专区分类</font>**
**<font style="color:rgb(255,255,255);background-color:rgb(255,255,255);">GET</font>**<font style="color:rgb(51,51,51);background-color:rgb(255,255,255);"> </font><font style="color:rgb(44,62,80);background-color:rgb(244,244,244);">/api/v1/market/gpu-categories</font>

### **<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">14. </font>****<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">获取 </font>****<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">GPU </font>****<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">型号列表</font>**
**<font style="color:rgb(255,255,255);background-color:rgb(255,255,255);">GET</font>**<font style="color:rgb(51,51,51);background-color:rgb(255,255,255);"> </font><font style="color:rgb(44,62,80);background-color:rgb(244,244,244);">/api/v1/market/gpu-models?category={category}</font><font style="color:rgb(44,62,80);background-color:rgb(244,244,244);">®</font><font style="color:rgb(44,62,80);background-color:rgb(244,244,244);">ion={region}</font>

| **<font style="color:rgb(44,62,80);">参数</font>** | **<font style="color:rgb(44,62,80);">类型</font>** | **<font style="color:rgb(44,62,80);">必填</font>** | **<font style="color:rgb(44,62,80);">说明</font>** |
| --- | --- | --- | --- |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">category</font> | <font style="color:rgb(51,51,51);">query</font> | <font style="color:rgb(127,140,141);">否</font> | <font style="color:rgb(51,51,51);">专区编码，如</font><font style="color:rgb(51,51,51);"> v100</font><font style="color:rgb(51,51,51);">、</font><font style="color:rgb(51,51,51);">a800</font> |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">region</font> | <font style="color:rgb(51,51,51);">query</font> | <font style="color:rgb(127,140,141);">否</font> | <font style="color:rgb(51,51,51);">地区编码，用于统计可用数量</font> |


#### **<font style="color:rgb(52,73,94);background-color:rgb(255,255,255);">响应示例</font>**
{

"gpu_models": [

{

"model": "RTX-PRO-6000",

"name": "RTX PRO 6000",

"memory": "96 GB",

"available": 2,

"total": 17

}

],

"total": 20

}

### **<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">15. </font>****<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">获取 </font>****<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">GPU </font>****<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">数量选项</font>**
**<font style="color:rgb(255,255,255);background-color:rgb(255,255,255);">GET</font>**<font style="color:rgb(51,51,51);background-color:rgb(255,255,255);"> </font><font style="color:rgb(44,62,80);background-color:rgb(244,244,244);">/api/v1/market/gpu-counts</font>

#### **<font style="color:rgb(52,73,94);background-color:rgb(255,255,255);">响应示例</font>**
<font style="color:rgb(199,37,78);background-color:rgb(30,30,46);">{</font>

"options": [1, 2, 3, 4, 5, 6, 7, 8, 10, 12]

}

### **<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">16. </font>****<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">获取可租用实例列表</font>**
**<font style="color:rgb(255,255,255);background-color:rgb(255,255,255);">GET</font>**<font style="color:rgb(51,51,51);background-color:rgb(255,255,255);"> </font><font style="color:rgb(44,62,80);background-color:rgb(244,244,244);">/api/v1/market/instances?region={region}&gpu_model={gpu_model}&gpu_count={gpu_count}&zone={zone}</font>

| **<font style="color:rgb(44,62,80);">参数</font>** | **<font style="color:rgb(44,62,80);">类型</font>** | **<font style="color:rgb(44,62,80);">必填</font>** | **<font style="color:rgb(44,62,80);">说明</font>** |
| --- | --- | --- | --- |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">region</font> | <font style="color:rgb(51,51,51);">query</font> | <font style="color:rgb(127,140,141);">否</font> | <font style="color:rgb(51,51,51);">地区编码</font> |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">gpu_model</font> | <font style="color:rgb(51,51,51);">query</font> | <font style="color:rgb(127,140,141);">否</font> | <font style="color:rgb(51,51,51);">GPU</font><font style="color:rgb(51,51,51);">型号</font> |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">gpu_count</font> | <font style="color:rgb(51,51,51);">query</font> | <font style="color:rgb(127,140,141);">否</font> | <font style="color:rgb(51,51,51);">需要的</font><font style="color:rgb(51,51,51);">GPU</font><font style="color:rgb(51,51,51);">数量</font> |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">zone</font> | <font style="color:rgb(51,51,51);">query</font> | <font style="color:rgb(127,140,141);">否</font> | <font style="color:rgb(51,51,51);">可用区</font> |


#### **<font style="color:rgb(52,73,94);background-color:rgb(255,255,255);">响应示例</font>**
{

"total": 2,

"instances": [

{

"id": "mi-fa464e9524",

"region": "xibei-b",

"zone": "C81",

"machine_id": "fa464e9524",

"gpu_model": "RTX-PRO-6000",

"gpu_memory": "96 GB",

"available_gpus": 1,

"total_gpus": 9,

"cpu_cores": 22,

"cpu_model": "Xeon(R) Platinum 8470Q",

"memory": "110 GB",

"system_disk": "30 GB",

"data_disk": "50 GB",

"data_disk_expandable": "139 GB",

"gpu_driver": "580.95.05",

"cuda_version": "<= 13.0",

"price_per_hour": 7.97,

"price_discount": 5.98,

"status": "available",

"rentable_until": "2027-10-01"

}

]

}

### **<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">17. </font>****<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">获取市场实例详情</font>**
**<font style="color:rgb(255,255,255);background-color:rgb(255,255,255);">GET</font>**<font style="color:rgb(51,51,51);background-color:rgb(255,255,255);"> </font><font style="color:rgb(44,62,80);background-color:rgb(244,244,244);">/api/v1/market/instances/{id}</font>

| **<font style="color:rgb(44,62,80);">参数</font>** | **<font style="color:rgb(44,62,80);">类型</font>** | **<font style="color:rgb(44,62,80);">必填</font>** | **<font style="color:rgb(44,62,80);">说明</font>** |
| --- | --- | --- | --- |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">id</font> | <font style="color:rgb(51,51,51);">path</font> | **<font style="color:rgb(231,76,60);">是</font>** | <font style="color:rgb(51,51,51);">市场实例</font><font style="color:rgb(51,51,51);">ID</font> |


### **<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">18. </font>****<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">租用实例（创建租赁）</font>**
**<font style="color:rgb(255,255,255);background-color:rgb(255,255,255);">POST</font>**<font style="color:rgb(51,51,51);background-color:rgb(255,255,255);"> </font><font style="color:rgb(44,62,80);background-color:rgb(244,244,244);">/api/v1/market/rent</font>

<font style="color:rgb(51,51,51);background-color:rgb(255,255,255);">支持按量计费、包日、包周、包月、包年、按小时的</font><font style="color:rgb(51,51,51);background-color:rgb(255,255,255);"> 1</font><font style="color:rgb(51,51,51);background-color:rgb(255,255,255);">卡</font><font style="color:rgb(51,51,51);background-color:rgb(255,255,255);">/</font><font style="color:rgb(51,51,51);background-color:rgb(255,255,255);">多卡 租赁</font>

**<font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">认证要求</font>**<font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">：请求头必须携带</font><font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">Authorization: Bearer <token></font><font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">，且 Body 中的 </font><font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">tenant_id</font><font style="color:rgb(26,82,118);background-color:rgb(232,244,253);"> 必须与 Token 一致 </font>

#### **<font style="color:rgb(52,73,94);background-color:rgb(255,255,255);">请求参数</font>**
| **<font style="color:rgb(44,62,80);">参数</font>** | **<font style="color:rgb(44,62,80);">类型</font>** | **<font style="color:rgb(44,62,80);">必填</font>** | **<font style="color:rgb(44,62,80);">说明</font>** |
| --- | --- | --- | --- |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">tenant_id</font> | <font style="color:rgb(51,51,51);">string</font> | **<font style="color:rgb(231,76,60);">是</font>** | <font style="color:rgb(51,51,51);">租户</font><font style="color:rgb(51,51,51);">ID</font><font style="color:rgb(51,51,51);">，必须与 </font><font style="color:rgb(51,51,51);">Token </font><font style="color:rgb(51,51,51);">中的 </font><font style="color:rgb(51,51,51);">tenant_id </font><font style="color:rgb(51,51,51);">一致</font> |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">tenant_name</font> | <font style="color:rgb(51,51,51);">string</font> | <font style="color:rgb(127,140,141);">否</font> | <font style="color:rgb(51,51,51);">租户名称</font> |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">instance_id</font> | <font style="color:rgb(51,51,51);">string</font> | **<font style="color:rgb(231,76,60);">是</font>** | <font style="color:rgb(51,51,51);">市场实例</font><font style="color:rgb(51,51,51);">ID</font> |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">gpu_count</font> | <font style="color:rgb(51,51,51);">int</font> | <font style="color:rgb(127,140,141);">否</font> | <font style="color:rgb(51,51,51);">租用</font><font style="color:rgb(51,51,51);">GPU</font><font style="color:rgb(51,51,51);">数量，默认</font><font style="color:rgb(51,51,51);">1</font> |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">image</font> | <font style="color:rgb(51,51,51);">string</font> | <font style="color:rgb(127,140,141);">否</font> | <font style="color:rgb(51,51,51);">容器镜像，默认</font><font style="color:rgb(51,51,51);"> pytorch:latest</font> |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">billing_mode</font> | <font style="color:rgb(51,51,51);">string</font> | **<font style="color:rgb(231,76,60);">是</font>** | <font style="color:rgb(51,51,51);">计费模式</font> |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">duration</font> | <font style="color:rgb(51,51,51);">int</font> | <font style="color:rgb(51,51,51);">条件</font> | <font style="color:rgb(51,51,51);">包周期模式时必须指定</font> |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">region</font> | <font style="color:rgb(51,51,51);">string</font> | <font style="color:rgb(127,140,141);">否</font> | <font style="color:rgb(51,51,51);">地区编码</font> |


#### **<font style="color:rgb(52,73,94);background-color:rgb(255,255,255);">请求示例</font>**
{

"tenant_id": "tenant_001",

"tenant_name": "AI科技公司",

"instance_id": "mi-fa464e9524",

"gpu_count": 1,

"image": "pytorch:latest",

"billing_mode": "on_demand",

"duration": 1,

"region": "xibei-b"

}

#### **<font style="color:rgb(52,73,94);background-color:rgb(255,255,255);">响应示例</font>**
{

"success": true,

"instance_id": "a1b2c3d4e5f6",

"instance_name": "xibei-b / C81机",

"message": "实例租用成功，分配 1 张 RTX-PRO-6000",

"billing_info": {

"gpu_model": "RTX-PRO-6000",

"gpu_count": 1,

"billing_mode": "on_demand",

"unit_price": 9.5,

"total_cost": 9.5,

"currency": "CNY",

"unit": "小时"

}

}

---

## **<font style="color:rgb(26,26,46);background-color:rgb(255,255,255);">七、容器实例管理接口</font>**
### **<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">19. </font>****<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">获取用户实例列表</font>**
**<font style="color:rgb(255,255,255);background-color:rgb(255,255,255);">GET</font>**<font style="color:rgb(51,51,51);background-color:rgb(255,255,255);"> </font><font style="color:rgb(44,62,80);background-color:rgb(244,244,244);">/api/v1/instances?tenant_id={tenant_id}&status={status}</font><font style="color:rgb(44,62,80);background-color:rgb(244,244,244);">®</font><font style="color:rgb(44,62,80);background-color:rgb(244,244,244);">ion={region}</font>

**<font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">认证要求</font>**<font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">：请求头必须携带</font><font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">Authorization: Bearer <token></font><font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">，且</font><font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">tenant_id</font><font style="color:rgb(26,82,118);background-color:rgb(232,244,253);"> 必须与 Token 一致 </font>

| **<font style="color:rgb(44,62,80);">参数</font>** | **<font style="color:rgb(44,62,80);">类型</font>** | **<font style="color:rgb(44,62,80);">必填</font>** | **<font style="color:rgb(44,62,80);">说明</font>** |
| --- | --- | --- | --- |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">tenant_id</font> | <font style="color:rgb(51,51,51);">query</font> | **<font style="color:rgb(231,76,60);">是</font>** | <font style="color:rgb(51,51,51);">租户</font><font style="color:rgb(51,51,51);">ID</font><font style="color:rgb(51,51,51);">，必须与 </font><font style="color:rgb(51,51,51);">Token </font><font style="color:rgb(51,51,51);">中的 </font><font style="color:rgb(51,51,51);">tenant_id </font><font style="color:rgb(51,51,51);">一致</font> |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">status</font> | <font style="color:rgb(51,51,51);">query</font> | <font style="color:rgb(127,140,141);">否</font> | <font style="color:rgb(51,51,51);">按状态筛选，如</font><font style="color:rgb(51,51,51);"> running</font><font style="color:rgb(51,51,51);">、</font><font style="color:rgb(51,51,51);">stopped</font> |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">region</font> | <font style="color:rgb(51,51,51);">query</font> | <font style="color:rgb(127,140,141);">否</font> | <font style="color:rgb(51,51,51);">按地区筛选</font> |


#### **<font style="color:rgb(52,73,94);background-color:rgb(255,255,255);">响应示例</font>**
{

"total": 1,

"instances": [

{

"instance_id": "a1b2c3d4e5f6",

"instance_name": "西北B区 / 099机",

"region": "xibei-b",

"zone": "C81",

"status": "running",

"gpu_spec": "RTX-PRO-6000 * 1卡",

"gpu_count": 1,

"cpu": "22核, Xeon(R) Platinum 8470Q",

"memory": "110 GB",

"disk": {

"system_disk_usage": 0.0,

"data_disk_usage": 0.0

},

"health": {

"status": "normal",

"cpu_usage": 0.0,

"memory_usage": 0.0,

"gpu_usage": 0.0

},

"billing_mode": "on_demand",

"billing_status": "余额不足24小时",

"release_time": "关机15天后释放",

"ssh_info": {

"command": "ssh -p 22 root@fa464e9524.xibei-b.autodl.com",

"password_masked": "********",

"host": "fa464e9524.xibei-b.autodl.com",

"port": 22

},

"tools": [

{"name": "JupyterLab", "url": "http://...:8888", "icon": "jupyter"},

{"name": "AutoPanel", "url": "http://...:8080", "icon": "panel"},

{"name": "实例监控", "url": "http://...:9090", "icon": "monitor"},

{"name": "自定义服务", "url": "http://...:6006", "icon": "service"}

],

"create_time": "2026-05-26T17:00:00",

"expire_time": "2026-05-27T17:00:00"

}

]

}

### **<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">20. </font>****<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">获取实例详情</font>**
**<font style="color:rgb(255,255,255);background-color:rgb(255,255,255);">GET</font>**<font style="color:rgb(51,51,51);background-color:rgb(255,255,255);"> </font><font style="color:rgb(44,62,80);background-color:rgb(244,244,244);">/api/v1/instances/{instance_id}?tenant_id={tenant_id}</font>

**<font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">认证要求</font>**<font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">：请求头必须携带</font><font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">Authorization: Bearer <token></font><font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">，且</font><font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">tenant_id</font><font style="color:rgb(26,82,118);background-color:rgb(232,244,253);"> 必须与 Token 一致 </font>

| **<font style="color:rgb(44,62,80);">参数</font>** | **<font style="color:rgb(44,62,80);">类型</font>** | **<font style="color:rgb(44,62,80);">必填</font>** | **<font style="color:rgb(44,62,80);">说明</font>** |
| --- | --- | --- | --- |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">instance_id</font> | <font style="color:rgb(51,51,51);">path</font> | **<font style="color:rgb(231,76,60);">是</font>** | <font style="color:rgb(51,51,51);">实例</font><font style="color:rgb(51,51,51);">ID</font> |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">tenant_id</font> | <font style="color:rgb(51,51,51);">query</font> | **<font style="color:rgb(231,76,60);">是</font>** | <font style="color:rgb(51,51,51);">租户</font><font style="color:rgb(51,51,51);">ID</font><font style="color:rgb(51,51,51);">，必须与 </font><font style="color:rgb(51,51,51);">Token </font><font style="color:rgb(51,51,51);">中的 </font><font style="color:rgb(51,51,51);">tenant_id </font><font style="color:rgb(51,51,51);">一致</font> |


### **<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">21. </font>****<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">启动实例</font>**
**<font style="color:rgb(255,255,255);background-color:rgb(255,255,255);">POST</font>**<font style="color:rgb(51,51,51);background-color:rgb(255,255,255);"> </font><font style="color:rgb(44,62,80);background-color:rgb(244,244,244);">/api/v1/instances/{instance_id}/start?tenant_id={tenant_id}</font>

**<font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">认证要求</font>**<font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">：请求头必须携带</font><font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">Authorization: Bearer <token></font><font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">，且</font><font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">tenant_id</font><font style="color:rgb(26,82,118);background-color:rgb(232,244,253);"> 必须与 Token 一致 </font>

### **<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">22. </font>****<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">停止实例（关机）</font>**
**<font style="color:rgb(255,255,255);background-color:rgb(255,255,255);">POST</font>**<font style="color:rgb(51,51,51);background-color:rgb(255,255,255);"> </font><font style="color:rgb(44,62,80);background-color:rgb(244,244,244);">/api/v1/instances/{instance_id}/stop?tenant_id={tenant_id}</font>

**<font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">认证要求</font>**<font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">：请求头必须携带</font><font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">Authorization: Bearer <token></font><font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">，且</font><font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">tenant_id</font><font style="color:rgb(26,82,118);background-color:rgb(232,244,253);"> 必须与 Token 一致 </font>

### **<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">23. </font>****<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">重启实例</font>**
**<font style="color:rgb(255,255,255);background-color:rgb(255,255,255);">POST</font>**<font style="color:rgb(51,51,51);background-color:rgb(255,255,255);"> </font><font style="color:rgb(44,62,80);background-color:rgb(244,244,244);">/api/v1/instances/{instance_id}/restart?tenant_id={tenant_id}</font>

**<font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">认证要求</font>**<font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">：请求头必须携带</font><font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">Authorization: Bearer <token></font><font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">，且</font><font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">tenant_id</font><font style="color:rgb(26,82,118);background-color:rgb(232,244,253);"> 必须与 Token 一致 </font>

### **<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">24. </font>****<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">释放实例</font>**
**<font style="color:rgb(255,255,255);background-color:rgb(255,255,255);">POST</font>**<font style="color:rgb(51,51,51);background-color:rgb(255,255,255);"> </font><font style="color:rgb(44,62,80);background-color:rgb(244,244,244);">/api/v1/instances/{instance_id}/release?tenant_id={tenant_id}</font>

**<font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">认证要求</font>**<font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">：请求头必须携带</font><font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">Authorization: Bearer <token></font><font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">，且</font><font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">tenant_id</font><font style="color:rgb(26,82,118);background-color:rgb(232,244,253);"> 必须与 Token 一致 </font>

<font style="color:rgb(133,100,4);background-color:rgb(255,243,205);">⚠</font><font style="color:rgb(133,100,4);background-color:rgb(255,243,205);">️ </font><font style="color:rgb(133,100,4);background-color:rgb(255,243,205);">释放后实例数据清空且不可恢复</font>

### **<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">25. </font>****<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">修改实例 </font>****<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">SSH </font>****<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">密码</font>**
**<font style="color:rgb(255,255,255);background-color:rgb(255,255,255);">POST</font>**<font style="color:rgb(51,51,51);background-color:rgb(255,255,255);"> </font><font style="color:rgb(44,62,80);background-color:rgb(244,244,244);">/api/v1/instances/{instance_id}/reset-password?tenant_id={tenant_id}</font>

**<font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">认证要求</font>**<font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">：请求头必须携带</font><font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">Authorization: Bearer <token></font><font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">，且</font><font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">tenant_id</font><font style="color:rgb(26,82,118);background-color:rgb(232,244,253);"> 必须与 Token 一致 </font>

| **<font style="color:rgb(44,62,80);">参数</font>** | **<font style="color:rgb(44,62,80);">类型</font>** | **<font style="color:rgb(44,62,80);">必填</font>** | **<font style="color:rgb(44,62,80);">说明</font>** |
| --- | --- | --- | --- |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">new_password</font> | <font style="color:rgb(51,51,51);">string</font> | **<font style="color:rgb(231,76,60);">是</font>** | <font style="color:rgb(51,51,51);">新密码，长度</font><font style="color:rgb(51,51,51);">6-32</font><font style="color:rgb(51,51,51);">位</font> |


#### **<font style="color:rgb(52,73,94);background-color:rgb(255,255,255);">请求示例</font>**
{

"new_password": "MyNewP@ssw0rd"

}

### **<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">26. </font>****<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">获取 </font>****<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">SSH </font>****<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">登录信息</font>**
**<font style="color:rgb(255,255,255);background-color:rgb(255,255,255);">GET</font>**<font style="color:rgb(51,51,51);background-color:rgb(255,255,255);"> </font><font style="color:rgb(44,62,80);background-color:rgb(244,244,244);">/api/v1/instances/{instance_id}/ssh?tenant_id={tenant_id}</font>

**<font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">认证要求</font>**<font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">：请求头必须携带</font><font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">Authorization: Bearer <token></font><font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">，且</font><font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">tenant_id</font><font style="color:rgb(26,82,118);background-color:rgb(232,244,253);"> 必须与 Token 一致 </font>

#### **<font style="color:rgb(52,73,94);background-color:rgb(255,255,255);">响应示例</font>**
{

"instance_id": "a1b2c3d4e5f6",

"ssh_info": {

"host": "fa464e9524.xibei-b.autodl.com",

"port": 22,

"username": "root",

"password": "aBcD1234EfGh",

"command": "ssh -p 22 root@fa464e9524.xibei-b.autodl.com"

}

}

### **<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">27. </font>****<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">实例续费</font>**
**<font style="color:rgb(255,255,255);background-color:rgb(255,255,255);">POST</font>**<font style="color:rgb(51,51,51);background-color:rgb(255,255,255);"> </font><font style="color:rgb(44,62,80);background-color:rgb(244,244,244);">/api/v1/instances/{instance_id}/renew?tenant_id={tenant_id}</font>

**<font style="color:rgb(26,82,118);background-color:rgb(232,244,253);"> 认证要求</font>**<font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">：请求头必须携带</font><font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">Authorization: Bearer <token></font><font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">，且</font><font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">tenant_id</font><font style="color:rgb(26,82,118);background-color:rgb(232,244,253);"> 必须与 Token 一致 </font>

| **<font style="color:rgb(44,62,80);">参数</font>** | **<font style="color:rgb(44,62,80);">类型</font>** | **<font style="color:rgb(44,62,80);">必填</font>** | **<font style="color:rgb(44,62,80);">说明</font>** |
| --- | --- | --- | --- |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">billing_mode</font> | <font style="color:rgb(51,51,51);">string</font> | **<font style="color:rgb(231,76,60);">是</font>** | <font style="color:rgb(51,51,51);">续费计费模式</font> |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">duration</font> | <font style="color:rgb(51,51,51);">int</font> | **<font style="color:rgb(231,76,60);">是</font>** | <font style="color:rgb(51,51,51);">续费时长</font> |


#### **<font style="color:rgb(52,73,94);background-color:rgb(255,255,255);">请求示例</font>**
{

"billing_mode": "monthly",

"duration": 1

}

### **<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">28. </font>****<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">批量续费</font>**
**<font style="color:rgb(255,255,255);background-color:rgb(255,255,255);">POST</font>**<font style="color:rgb(51,51,51);background-color:rgb(255,255,255);"> </font><font style="color:rgb(44,62,80);background-color:rgb(244,244,244);">/api/v1/instances/batch-renew?tenant_id={tenant_id}</font>

**<font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">��</font>****<font style="color:rgb(26,82,118);background-color:rgb(232,244,253);"> </font>****<font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">认证要求</font>**<font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">：请求头必须携带</font><font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">Authorization: Bearer <token></font><font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">，且</font><font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">tenant_id</font><font style="color:rgb(26,82,118);background-color:rgb(232,244,253);"> </font><font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">必须与</font><font style="color:rgb(26,82,118);background-color:rgb(232,244,253);"> Token </font><font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">一致 </font>

| **<font style="color:rgb(44,62,80);">参数</font>** | **<font style="color:rgb(44,62,80);">类型</font>** | **<font style="color:rgb(44,62,80);">必填</font>** | **<font style="color:rgb(44,62,80);">说明</font>** |
| --- | --- | --- | --- |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">instance_ids</font> | <font style="color:rgb(51,51,51);">string[]</font> | **<font style="color:rgb(231,76,60);">是</font>** | <font style="color:rgb(51,51,51);">实例</font><font style="color:rgb(51,51,51);">ID</font><font style="color:rgb(51,51,51);">列表</font> |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">billing_mode</font> | <font style="color:rgb(51,51,51);">string</font> | **<font style="color:rgb(231,76,60);">是</font>** | <font style="color:rgb(51,51,51);">计费模式</font> |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">duration</font> | <font style="color:rgb(51,51,51);">int</font> | **<font style="color:rgb(231,76,60);">是</font>** | <font style="color:rgb(51,51,51);">时长</font> |


#### **<font style="color:rgb(52,73,94);background-color:rgb(255,255,255);">请求示例</font>**
{

"instance_ids": ["a1b2c3d4", "e5f6g7h8"],

"billing_mode": "monthly",

"duration": 1

}

### **<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">29. </font>****<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">设置</font>****<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">/</font>****<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">取消定时关机</font>**
**<font style="color:rgb(255,255,255);background-color:rgb(255,255,255);">POST</font>**<font style="color:rgb(51,51,51);background-color:rgb(255,255,255);"> </font><font style="color:rgb(44,62,80);background-color:rgb(244,244,244);">/api/v1/instances/{instance_id}/shutdown-schedule?tenant_id={tenant_id}</font>

**<font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">��</font>****<font style="color:rgb(26,82,118);background-color:rgb(232,244,253);"> </font>****<font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">认证要求</font>**<font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">：请求头必须携带</font><font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">Authorization: Bearer <token></font><font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">，且</font><font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">tenant_id</font><font style="color:rgb(26,82,118);background-color:rgb(232,244,253);"> </font><font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">必须与</font><font style="color:rgb(26,82,118);background-color:rgb(232,244,253);"> Token </font><font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">一致 </font>

| **<font style="color:rgb(44,62,80);">参数</font>** | **<font style="color:rgb(44,62,80);">类型</font>** | **<font style="color:rgb(44,62,80);">必填</font>** | **<font style="color:rgb(44,62,80);">说明</font>** |
| --- | --- | --- | --- |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">shutdown_time</font> | <font style="color:rgb(51,51,51);">string</font> | <font style="color:rgb(127,140,141);">否</font> | <font style="color:rgb(51,51,51);">ISO</font><font style="color:rgb(51,51,51);">格式时间，</font><font style="color:rgb(51,51,51);">null</font><font style="color:rgb(51,51,51);">或省略表示取消</font> |


#### **<font style="color:rgb(52,73,94);background-color:rgb(255,255,255);">请求示例</font>**
{

"shutdown_time": "2026-05-27T22:00:00"

}

### **<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">30. </font>****<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">修改实例显示名称</font>**
**<font style="color:rgb(255,255,255);background-color:rgb(255,255,255);">PUT</font>**<font style="color:rgb(51,51,51);background-color:rgb(255,255,255);"> </font><font style="color:rgb(44,62,80);background-color:rgb(244,244,244);">/api/v1/instances/{instance_id}/name?tenant_id={tenant_id}&name={name}</font>

**<font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">��</font>****<font style="color:rgb(26,82,118);background-color:rgb(232,244,253);"> </font>****<font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">认证要求</font>**<font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">：请求头必须携带</font><font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">Authorization: Bearer <token></font><font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">，且</font><font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">tenant_id</font><font style="color:rgb(26,82,118);background-color:rgb(232,244,253);"> </font><font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">必须与</font><font style="color:rgb(26,82,118);background-color:rgb(232,244,253);"> Token </font><font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">一致 </font>

| **<font style="color:rgb(44,62,80);">参数</font>** | **<font style="color:rgb(44,62,80);">类型</font>** | **<font style="color:rgb(44,62,80);">必填</font>** | **<font style="color:rgb(44,62,80);">说明</font>** |
| --- | --- | --- | --- |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">name</font> | <font style="color:rgb(51,51,51);">query</font> | **<font style="color:rgb(231,76,60);">是</font>** | <font style="color:rgb(51,51,51);">新的显示名称</font> |


### **<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">31. </font>****<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">获取实例快捷工具</font>**
**<font style="color:rgb(255,255,255);background-color:rgb(255,255,255);">GET</font>**<font style="color:rgb(51,51,51);background-color:rgb(255,255,255);"> </font><font style="color:rgb(44,62,80);background-color:rgb(244,244,244);">/api/v1/instances/{instance_id}/tools?tenant_id={tenant_id}</font>

**<font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">��</font>****<font style="color:rgb(26,82,118);background-color:rgb(232,244,253);"> </font>****<font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">认证要求</font>**<font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">：请求头必须携带</font><font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">Authorization: Bearer <token></font><font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">，且</font><font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">tenant_id</font><font style="color:rgb(26,82,118);background-color:rgb(232,244,253);"> </font><font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">必须与</font><font style="color:rgb(26,82,118);background-color:rgb(232,244,253);"> Token </font><font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">一致 </font>

#### **<font style="color:rgb(52,73,94);background-color:rgb(255,255,255);">响应示例</font>**
{

"instance_id": "a1b2c3d4e5f6",

"tools": [

{"name": "JupyterLab", "url": "http://...:8888", "icon": "jupyter"},

{"name": "AutoPanel", "url": "http://...:8080", "icon": "panel"},

{"name": "实例监控", "url": "http://...:9090", "icon": "monitor"},

{"name": "自定义服务", "url": "http://...:6006", "icon": "service"}

]

}

### **<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">32. </font>****<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">获取实例监控数据</font>**
**<font style="color:rgb(255,255,255);background-color:rgb(255,255,255);">GET</font>**<font style="color:rgb(51,51,51);background-color:rgb(255,255,255);"> </font><font style="color:rgb(44,62,80);background-color:rgb(244,244,244);">/api/v1/instances/{instance_id}/monitor?tenant_id={tenant_id}</font>

**<font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">��</font>****<font style="color:rgb(26,82,118);background-color:rgb(232,244,253);"> </font>****<font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">认证要求</font>**<font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">：请求头必须携带</font><font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">Authorization: Bearer <token></font><font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">，且</font><font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">tenant_id</font><font style="color:rgb(26,82,118);background-color:rgb(232,244,253);"> </font><font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">必须与</font><font style="color:rgb(26,82,118);background-color:rgb(232,244,253);"> Token </font><font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">一致 </font>

#### **<font style="color:rgb(52,73,94);background-color:rgb(255,255,255);">响应示例</font>**
{

"instance_id": "a1b2c3d4e5f6",

"health": {

"status": "normal",

"cpu_usage": 12.5,

"memory_usage": 34.2,

"gpu_usage": 67.8

},

"disk": {

"system_disk_usage": 15.3,

"data_disk_usage": 8.7

},

"timestamp": "2026-05-26T17:30:00"

}

---

## **<font style="color:rgb(26,26,46);background-color:rgb(255,255,255);">八、计费接口</font>**
### **<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">33. </font>****<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">获取租户计费记录</font>**
**<font style="color:rgb(255,255,255);background-color:rgb(255,255,255);">GET</font>**<font style="color:rgb(51,51,51);background-color:rgb(255,255,255);"> </font><font style="color:rgb(44,62,80);background-color:rgb(244,244,244);">/api/v1/billing/records?tenant_id={tenant_id}</font>

**<font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">��</font>****<font style="color:rgb(26,82,118);background-color:rgb(232,244,253);"> </font>****<font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">认证要求</font>**<font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">：请求头必须携带</font><font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">Authorization: Bearer <token></font><font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">，且</font><font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">tenant_id</font><font style="color:rgb(26,82,118);background-color:rgb(232,244,253);"> </font><font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">必须与</font><font style="color:rgb(26,82,118);background-color:rgb(232,244,253);"> Token </font><font style="color:rgb(26,82,118);background-color:rgb(232,244,253);">一致 </font>

| **<font style="color:rgb(44,62,80);">参数</font>** | **<font style="color:rgb(44,62,80);">类型</font>** | **<font style="color:rgb(44,62,80);">必填</font>** | **<font style="color:rgb(44,62,80);">说明</font>** |
| --- | --- | --- | --- |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">tenant_id</font> | <font style="color:rgb(51,51,51);">query</font> | **<font style="color:rgb(231,76,60);">是</font>** | <font style="color:rgb(51,51,51);">租户</font><font style="color:rgb(51,51,51);">ID</font><font style="color:rgb(51,51,51);">，必须与 </font><font style="color:rgb(51,51,51);">Token </font><font style="color:rgb(51,51,51);">中的 </font><font style="color:rgb(51,51,51);">tenant_id </font><font style="color:rgb(51,51,51);">一致</font> |


---

## **<font style="color:rgb(26,26,46);background-color:rgb(255,255,255);">九、代码文件说明</font>**
| **<font style="color:rgb(44,62,80);">文件</font>** | **<font style="color:rgb(44,62,80);">职责</font>** |
| --- | --- |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">main.py</font> | <font style="color:rgb(51,51,51);">FastAPI </font><font style="color:rgb(51,51,51);">路由入口，聚合所有 </font><font style="color:rgb(51,51,51);">HTTP </font><font style="color:rgb(51,51,51);">接口</font> |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">models.py</font> | <font style="color:rgb(51,51,51);">Pydantic </font><font style="color:rgb(51,51,51);">数据模型（请求</font><font style="color:rgb(51,51,51);">/</font><font style="color:rgb(51,51,51);">响应</font><font style="color:rgb(51,51,51);">/</font><font style="color:rgb(51,51,51);">枚举）</font> |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">auth.py</font> | <font style="color:rgb(51,51,51);">JWT </font><font style="color:rgb(51,51,51);">认证与租户资源归属校验</font> |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">k8s_client.py</font> | <font style="color:rgb(51,51,51);">Kubernetes </font><font style="color:rgb(51,51,51);">客户端封装（</font><font style="color:rgb(51,51,51);">Pod/Namespace </font><font style="color:rgb(51,51,51);">操作）</font> |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">resource_manager.py</font> | <font style="color:rgb(51,51,51);">GPU </font><font style="color:rgb(51,51,51);">节点资源查询与调度</font> |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">scheduler.py</font> | <font style="color:rgb(51,51,51);">GPU </font><font style="color:rgb(51,51,51);">调度策略（配额</font><font style="color:rgb(51,51,51);">/</font><font style="color:rgb(51,51,51);">节点选择）</font> |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">billing.py</font> | <font style="color:rgb(51,51,51);">计费服务（价格计算</font><font style="color:rgb(51,51,51);">/</font><font style="color:rgb(51,51,51);">计费记录</font><font style="color:rgb(51,51,51);">/</font><font style="color:rgb(51,51,51);">续费）</font> |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">instance_manager.py</font> | <font style="color:rgb(51,51,51);">容器实例生命周期管理（状态</font><font style="color:rgb(51,51,51);">/SSH/</font><font style="color:rgb(51,51,51);">密码</font><font style="color:rgb(51,51,51);">/</font><font style="color:rgb(51,51,51);">定时关机）</font> |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">market_data.py</font> | <font style="color:rgb(51,51,51);">算力市场数据（地区</font><font style="color:rgb(51,51,51);">/GPU</font><font style="color:rgb(51,51,51);">型号</font><font style="color:rgb(51,51,51);">/</font><font style="color:rgb(51,51,51);">可租用实例）</font> |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">requirements.txt</font> | <font style="color:rgb(51,51,51);">Python </font><font style="color:rgb(51,51,51);">依赖包</font> |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">API.md</font> | <font style="color:rgb(51,51,51);">Markdown </font><font style="color:rgb(51,51,51);">接口文档</font> |
| <font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">API.html</font> | <font style="color:rgb(51,51,51);">HTML </font><font style="color:rgb(51,51,51);">接口文档（本文件）</font> |


### **<font style="color:rgb(44,62,80);background-color:rgb(255,255,255);">对接说明</font>**
**<font style="color:rgb(51,51,51);background-color:rgb(255,255,255);">1. </font>****<font style="color:rgb(51,51,51);background-color:rgb(255,255,255);">纯本地演示</font>**<font style="color:rgb(51,51,51);background-color:rgb(255,255,255);">：当前</font><font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">instance_manager.py</font><font style="color:rgb(51,51,51);background-color:rgb(255,255,255);"> </font><font style="color:rgb(51,51,51);background-color:rgb(255,255,255);">使用</font><font style="color:rgb(51,51,51);background-color:rgb(255,255,255);"> JSON </font><font style="color:rgb(51,51,51);background-color:rgb(255,255,255);">文件持久化实例数据，无需连接 </font><font style="color:rgb(51,51,51);background-color:rgb(255,255,255);">K8s </font><font style="color:rgb(51,51,51);background-color:rgb(255,255,255);">即可运行所有 </font><font style="color:rgb(51,51,51);background-color:rgb(255,255,255);">API</font><font style="color:rgb(51,51,51);background-color:rgb(255,255,255);">。</font>

**<font style="color:rgb(51,51,51);background-color:rgb(255,255,255);">2. </font>****<font style="color:rgb(51,51,51);background-color:rgb(255,255,255);">对接真实 </font>****<font style="color:rgb(51,51,51);background-color:rgb(255,255,255);">K8s</font>**<font style="color:rgb(51,51,51);background-color:rgb(255,255,255);">：若需操作真实集群，</font><font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">instance_manager.start/stop/restart</font><font style="color:rgb(51,51,51);background-color:rgb(255,255,255);"> </font><font style="color:rgb(51,51,51);background-color:rgb(255,255,255);">可扩展调用</font><font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">k8s_client</font><font style="color:rgb(51,51,51);background-color:rgb(255,255,255);"> </font><font style="color:rgb(51,51,51);background-color:rgb(255,255,255);">对</font><font style="color:rgb(51,51,51);background-color:rgb(255,255,255);"> Pod </font><font style="color:rgb(51,51,51);background-color:rgb(255,255,255);">的实际操作；原有 </font><font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">/api/v1/gpu/pod/*</font><font style="color:rgb(51,51,51);background-color:rgb(255,255,255);"> </font><font style="color:rgb(51,51,51);background-color:rgb(255,255,255);">接口可直接使用。</font>

**<font style="color:rgb(51,51,51);background-color:rgb(255,255,255);">3. SSH </font>****<font style="color:rgb(51,51,51);background-color:rgb(255,255,255);">远程登录</font>**<font style="color:rgb(51,51,51);background-color:rgb(255,255,255);">：当前返回</font><font style="color:rgb(51,51,51);background-color:rgb(255,255,255);"> SSH </font><font style="color:rgb(51,51,51);background-color:rgb(255,255,255);">连接信息（主机、端口、用户名、密码），如需实现真正的 </font><font style="color:rgb(51,51,51);background-color:rgb(255,255,255);">WebSSH/JupyterLab </font><font style="color:rgb(51,51,51);background-color:rgb(255,255,255);">代理，可在 </font><font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">tools</font><font style="color:rgb(51,51,51);background-color:rgb(255,255,255);"> </font><font style="color:rgb(51,51,51);background-color:rgb(255,255,255);">中扩展代理跳转逻辑。</font>

**<font style="color:rgb(51,51,51);background-color:rgb(255,255,255);">4. JWT 安全提醒</font>**<font style="color:rgb(51,51,51);background-color:rgb(255,255,255);">：生产环境务必修改</font><font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">JWT_SECRET_KEY</font><font style="color:rgb(51,51,51);background-color:rgb(255,255,255);"> 和</font><font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">API_KEY</font><font style="color:rgb(51,51,51);background-color:rgb(255,255,255);"> 环境变量，使用强随机字符串，防止 Token 被伪造。建议将 </font><font style="color:rgb(199,37,78);background-color:rgb(244,244,244);">API_KEY</font><font style="color:rgb(51,51,51);background-color:rgb(255,255,255);"> 校验替换为数据库中的租户独立密码/AKSK，或对接统一认证平台（SSO/OAuth2）。</font>
