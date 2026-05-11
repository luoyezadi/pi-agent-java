package com.example.piagent.api;

import com.example.piagent.common.ApiResponse;
import com.example.piagent.skill.SkillDefinition;
import com.example.piagent.skill.SkillRegistry;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Skill REST API 控制器
 *
 * 提供技能管理相关接口
 */
@RestController
@RequestMapping("/api/v1/skills")
public class SkillController {

    private final SkillRegistry skillRegistry;

    public SkillController(SkillRegistry skillRegistry) {
        this.skillRegistry = skillRegistry;
    }

    @GetMapping
    public ApiResponse<List<SkillDefinition>> listSkills() {
        List<SkillDefinition> skills = skillRegistry.getAll();
        return ApiResponse.success(skills);
    }

    @GetMapping("/{name}")
    public ApiResponse<SkillDefinition> getSkill(@PathVariable String name) {
        return skillRegistry.get(name)
                .map(ApiResponse::success)
                .orElse(ApiResponse.error(404, "技能不存在: " + name));
    }
}
