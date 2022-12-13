package me.langyue.equipmentstandard.api;

public interface ProficiencyAccessor {

    String NBT_KEY = "ES:Proficiency";

    /**
     * 获取熟练度
     */
    int getProficiency();

    /**
     * 增加熟练度后返回
     */
    int incrementProficiency();
}
