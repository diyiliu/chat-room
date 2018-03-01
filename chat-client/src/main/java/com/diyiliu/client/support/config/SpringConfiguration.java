package com.diyiliu.client.support.config;

import com.diyiliu.common.util.SpringUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Description: SpringConfiguration
 * Author: DIYILIU
 * Update: 2018-03-01 16:06
 */

@Configuration
public class SpringConfiguration {

    /**
     * spring 工具类
     *
     * @return
     */
    @Bean
    public SpringUtil springUtil() {

        return new SpringUtil();
    }

}
