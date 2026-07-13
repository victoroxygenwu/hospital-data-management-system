package cn.iocoder.yudao.module.hospital.framework.security.config;

import cn.iocoder.yudao.framework.security.config.AuthorizeRequestsCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Hospital 模块的 Security 配置
 */
@Configuration("hospitalSecurityConfiguration")
public class SecurityConfiguration {

    @Bean("hospitalAuthorizeRequestsCustomizer")
    public AuthorizeRequestsCustomizer authorizeRequestsCustomizer() {
        return registry -> {
            // 医院管理接口暂不开放 permitAll，走正常的权限校验
        };
    }
}
