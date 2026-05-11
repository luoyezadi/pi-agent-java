package com.example.piagent.skill.builtin;

import com.example.piagent.prompt.PromptTemplate;
import com.example.piagent.skill.SkillDefinition;
import com.example.piagent.skill.SkillRegistry;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 内置技能自动注册配置
 *
 * 设计目的：
 * - 在应用启动时自动注册内置技能
 * - 演示如何定义和使用技能
 */
@Configuration
public class BuiltinSkillConfiguration {

    @Bean
    public CommandLineRunner registerBuiltinSkills(SkillRegistry skillRegistry) {
        return args -> {
            registerSimpleQuestionSkill(skillRegistry);
            registerCodeReviewSkill(skillRegistry);
        };
    }

    private void registerSimpleQuestionSkill(SkillRegistry registry) {
        SkillDefinition skill = SkillDefinition.builder()
                .name("simple_question")
                .description("简单问答技能，适合回答一般性问题")
                .systemPrompt("你是一个友善的助手，简洁地回答用户的问题。")
                .allowedTools(java.util.List.of("current_time"))
                .maxSteps(3)
                .build();
        registry.register(skill);
    }

    private void registerCodeReviewSkill(SkillRegistry registry) {
        String systemPrompt = """
                你是一个专业的代码审查助手。
                请审查用户提供的代码，关注以下方面：
                1. 代码可读性
                2. 潜在的 bug
                3. 性能问题
                4. 安全漏洞
                5. 最佳实践

                请用中文给出审查结果。
                """;

        SkillDefinition skill = SkillDefinition.builder()
                .name("code_review")
                .description("代码审查技能，用于审查代码质量问题")
                .systemPrompt(systemPrompt)
                .allowedTools(java.util.List.of())
                .maxSteps(5)
                .build();
        registry.register(skill);
    }
}
