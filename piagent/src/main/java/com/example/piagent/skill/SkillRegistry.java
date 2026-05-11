package com.example.piagent.skill;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 技能注册表
 *
 * 设计目的：
 * - 管理所有可用技能的注册和查询
 * - 与 ToolRegistry 配合使用
 * - 支持运行时动态注册技能
 */
@Component
public class SkillRegistry {

    private final Map<String, SkillDefinition> skills = new ConcurrentHashMap<>();

    /**
     * 注册技能
     *
     * @param skill 技能定义
     * @return 是否注册成功
     */
    public boolean register(SkillDefinition skill) {
        if (skill == null || skill.getName() == null) {
            return false;
        }
        String name = skill.getName();
        if (skills.containsKey(name)) {
            return false;
        }
        skills.put(name, skill);
        return true;
    }

    /**
     * 根据名称获取技能
     */
    public Optional<SkillDefinition> get(String name) {
        return Optional.ofNullable(skills.get(name));
    }

    /**
     * 获取所有已注册的技能
     */
    public List<SkillDefinition> getAll() {
        return skills.values().stream().toList();
    }

    /**
     * 注销技能
     */
    public boolean unregister(String name) {
        return skills.remove(name) != null;
    }

    /**
     * 返回已注册技能数量
     */
    public int count() {
        return skills.size();
    }
}
