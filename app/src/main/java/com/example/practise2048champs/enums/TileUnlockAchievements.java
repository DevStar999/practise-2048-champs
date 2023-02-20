package com.example.practise2048champs.enums;

import com.example.practise2048champs.R;

import lombok.Getter;

@Getter
public enum TileUnlockAchievements {
    TILE_UNLOCK_ACHIEVEMENT_LEVEL_1("Masterful", R.string.achievement_masterful, 1,
            2048L),
    TILE_UNLOCK_ACHIEVEMENT_LEVEL_2("Brainiac \uD83E\uDDE0", R.string.achievement_brainiac, 2,
            4096L),
    TILE_UNLOCK_ACHIEVEMENT_LEVEL_3("Gifted \uD83C\uDF81", R.string.achievement_gifted, 3,
            16384L),
    TILE_UNLOCK_ACHIEVEMENT_LEVEL_4("Enigma \uD83E\uDD39", R.string.achievement_enigma, 4,
            131072L);

    private String nameOfAchievement; // Name as per the name given in the Google Play Games project
    private int achievementStringResourceId; // Achievement Id as per the Id generated in the Google Play Games project
    private int levelOfAchievement; // Custom information for us to keep track of the level of achievement completed
    private long achievementTileValue; // The value of the achievement tile

    TileUnlockAchievements(String nameOfAchievement, int achievementStringResourceId, int levelOfAchievement,
                           long achievementTileValue) {
        this.nameOfAchievement = nameOfAchievement;
        this.achievementStringResourceId = achievementStringResourceId;
        this.levelOfAchievement = levelOfAchievement;
        this.achievementTileValue = achievementTileValue;
    }
}
