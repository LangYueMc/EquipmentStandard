package me.langyue.equipmentstandard.api;

public interface ProficiencyAccessor {

    String NBT_KEY = "ES:Proficiency";

    /**
     * 获取熟练度
     */
    default int es$getProficiency() {
        return 0;
    }

    /**
     * 增加熟练度后返回
     */
    default int es$incrementProficiency() {
        return 0;
    }
}
