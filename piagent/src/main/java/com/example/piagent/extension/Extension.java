package com.example.piagent.extension;

/**
 * 扩展接口
 *
 * 设计目的：
 * - 定义框架的扩展点
 * - 扩展可以在应用启动时注册工具、技能等
 * - 通过 Spring 的 @Component 注解自动发现和加载
 *
 * 为什么使用接口？
 * - 解耦扩展和核心框架
 * - 便于单元测试
 * - 支持动态加载/卸载扩展
 *
 * 使用方式：
 * 1. 实现此接口
 * 2. 添加 @Component 注解
 * 3. 在 onLoad 方法中注册工具或技能
 */
public interface Extension {

    /**
     * 获取扩展名称
     */
    String getName();

    /**
     * 获取扩展描述
     */
    String getDescription();

    /**
     * 扩展加载时调用
     *
     * @param context 扩展上下文，提供访问框架组件的接口
     */
    void onLoad(ExtensionContext context);

    /**
     * 扩展卸载时调用（可选实现）
     */
    default void onUnload() {
    }
}
