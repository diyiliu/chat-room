package com.diyiliu.server.support.config;

import com.diyiliu.common.cache.ICache;
import com.diyiliu.common.cache.ram.RamCacheProvider;
import com.diyiliu.common.util.SpringUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Description: SpringConfiguration
 * Author: DIYILIU
 * Update: 2018-03-01 14:07
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


    /**
     * 客户端缓存
     *
     * @return
     */
    @Bean
    public ICache onlineCacheProvider() {

        return new RamCacheProvider();
    }
}
