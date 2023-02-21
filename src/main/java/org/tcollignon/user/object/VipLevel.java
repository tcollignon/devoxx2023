package org.tcollignon.user.object;

public enum VipLevel {

    LEVEL_0(0),
    LEVEL_1(1);

    private int level;

    VipLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    static VipLevel fromLevel(int level) {
        for (VipLevel vipLevel : values()) {
            if (vipLevel.level == level) {
                return vipLevel;
            }
        }
        return LEVEL_0;
    }
}
