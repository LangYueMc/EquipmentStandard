package me.langyue.equipmentstandard.api.data;

public class Bonus {
    private final double luck;
    private final double proficiency;

    public Bonus(double luck, double proficiency) {
        this.luck = luck;
        this.proficiency = proficiency;
    }

    public double getLuck() {
        return luck;
    }

    public double getProficiency() {
        return proficiency;
    }
}
