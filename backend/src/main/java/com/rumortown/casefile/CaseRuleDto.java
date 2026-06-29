package com.rumortown.casefile;

public record CaseRuleDto(Long id, String ruleCode, String ruleText, Integer displayOrder) {
    public static CaseRuleDto from(CaseRule rule) {
        return new CaseRuleDto(rule.getId(), rule.getRuleCode(), rule.getRuleText(), rule.getDisplayOrder());
    }
}
