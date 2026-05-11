package com.example.piagent.extension;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 扩展加载服务
 *
 * 设计目的：
 * - 负责加载所有实现了 Extension 接口的组件
 * - 在 Spring 容器初始化后调用各扩展的 onLoad 方法
 * - 统一管理扩展的生命周期
 */
@Service
public class ExtensionService {

    private static final Logger log = LoggerFactory.getLogger(ExtensionService.class);

    private final List<Extension> extensions;

    public ExtensionService(List<Extension> extensions) {
        this.extensions = extensions;
    }

    @PostConstruct
    public void loadExtensions() {
        log.info("开始加载扩展，共 {} 个", extensions.size());

        for (Extension extension : extensions) {
            try {
                log.info("加载扩展: {}", extension.getName());
                extension.onLoad(null);
                log.info("扩展加载成功: {}", extension.getName());
            } catch (Exception e) {
                log.error("扩展加载失败: {}", extension.getName(), e);
            }
        }

        log.info("扩展加载完成");
    }
}
