package com.example.piagent.skill;

import com.example.piagent.prompt.PromptTemplate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * SkillRegistry 技能注册表测试
 */
@DisplayName("SkillRegistry 测试")
class SkillRegistryTest {

    private SkillRegistry createRegistry() {
        return new SkillRegistry();
    }

    @Nested
    @DisplayName("注册技能测试")
    class RegisterSkillTest {

        @Test
        @DisplayName("应该成功注册技能")
        void shouldRegisterSkillSuccessfully() {
            var registry = createRegistry();
            var skill = createSampleSkill("code_review", "代码审查");

            boolean registered = registry.register(skill);

            assertThat(registered).isTrue();
        }

        @Test
        @DisplayName("应该能够获取已注册的技能")
        void shouldGetRegisteredSkill() {
            var registry = createRegistry();
            var skill = createSampleSkill("code_review", "代码审查");
            registry.register(skill);

            Optional<SkillDefinition> found = registry.get("code_review");

            assertThat(found).isPresent();
            assertThat(found.get().getName()).isEqualTo("code_review");
        }

        @Test
        @DisplayName("注册同名技能应该返回 false")
        void shouldReturnFalseForDuplicateSkill() {
            var registry = createRegistry();
            var skill1 = createSampleSkill("qa", "问答");
            var skill2 = createSampleSkill("qa", "另一个问答");
            registry.register(skill1);

            boolean registered = registry.register(skill2);

            assertThat(registered).isFalse();
        }
    }

    @Nested
    @DisplayName("查询技能测试")
    class FindSkillTest {

        @Test
        @DisplayName("应该能找到已注册的技能")
        void shouldFindRegisteredSkill() {
            var registry = createRegistry();
            registry.register(createSampleSkill("search", "搜索"));

            Optional<SkillDefinition> found = registry.get("search");

            assertThat(found).isPresent();
        }

        @Test
        @DisplayName("查询不存在的技能应该返回 Optional.empty")
        void shouldReturnEmptyForNonExistentSkill() {
            var registry = createRegistry();

            Optional<SkillDefinition> found = registry.get("non_existent");

            assertThat(found).isEmpty();
        }
    }

    @Nested
    @DisplayName("获取技能列表测试")
    class GetAllSkillsTest {

        @Test
        @DisplayName("应该返回所有已注册的技能")
        void shouldReturnAllRegisteredSkills() {
            var registry = createRegistry();
            registry.register(createSampleSkill("skill1", "技能1"));
            registry.register(createSampleSkill("skill2", "技能2"));

            List<SkillDefinition> skills = registry.getAll();

            assertThat(skills).hasSize(2);
        }

        @Test
        @DisplayName("空注册表应该返回空列表")
        void shouldReturnEmptyListForEmptyRegistry() {
            var registry = createRegistry();

            List<SkillDefinition> skills = registry.getAll();

            assertThat(skills).isEmpty();
        }
    }

    private SkillDefinition createSampleSkill(String name, String description) {
        return SkillDefinition.builder()
                .name(name)
                .description(description)
                .systemPrompt("你是一个" + description)
                .build();
    }
}
