package name.modid;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class Config {
    private static final Path CONFIG_PATH = Path.of("./config/CombatTag.json");

    private static float COMBAT_DURATION_SEC = 20;
    private static float COMBAT_TP_COOLDOWN_SEC = 10;
    private static float COMBAT_NOTCH_APPLE_COOLDOWN_SEC = 10;
    public static float HEALTH_REMAINING_PUNISH = 1;
    public static float ABSORPTION_REMAINING_PUNISH = 0;
    private static float POISON_DURATION_SEC = 20;
    private static int POISON_LEVEL = 5;
    public static boolean ENABLE_HEALTH_PUNISH = true;
    public static boolean ENABLE_ABSORPTION_PUNISH= true;
    public static boolean ENABLE_POISON_PUNISH = true;
    public static boolean ENABLE_TP_PUNISH = true;
    public static boolean ENABLE_INSTANT_TP_PUNISH = true;
    public static boolean ENABLE_NOTCH_APPLE_PUNISH = true;
    public static boolean ENABLE_INSTANT_NOTCH_APPLE_PUNISH = false;
    public static boolean ENABLE_ELYTRA_PUNISH = true;
    public static boolean ENABLE_COMBAT_COLOUR = true;
    public static boolean DISABLE_TEAM_MSG_COMMAND = true;
    public static boolean DISABLE_TEAM_COMMAND = true;

    public static int COMBAT_DURATION = Math.round(COMBAT_DURATION_SEC * 20);
    public static int COMBAT_TP_COOLDOWN = Math.round(COMBAT_TP_COOLDOWN_SEC * 20);
    public static int COMBAT_NOTCH_APPLE_COOLDOWN = Math.round(COMBAT_NOTCH_APPLE_COOLDOWN_SEC * 20);
    public static int POISON_DURATION = Math.round(POISON_DURATION_SEC * 20);
    public static int POISON_AMPLIFIER = POISON_LEVEL - 1;


    private static void update() {
        COMBAT_DURATION = Math.round(COMBAT_DURATION_SEC * 20);
        COMBAT_TP_COOLDOWN = Math.round(COMBAT_TP_COOLDOWN_SEC * 20);
        COMBAT_NOTCH_APPLE_COOLDOWN = Math.round(COMBAT_NOTCH_APPLE_COOLDOWN_SEC * 20);
        POISON_DURATION = Math.round(POISON_DURATION_SEC * 20);
        POISON_AMPLIFIER = POISON_LEVEL - 1;
    }

    public static void load() throws IOException {
        if (CONFIG_PATH.toFile().exists()) {
            parse();
        } else {
            throw new IOException();
        }

        update();
    }

    public static void write() throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        BufferedWriter bw = new BufferedWriter(new FileWriter(CONFIG_PATH.toString()));
        JsonObject configObj = new JsonObject();

        configObj.addProperty("CombatDurationSec", COMBAT_DURATION_SEC);
        configObj.addProperty("CombatTPCooldownSec", COMBAT_TP_COOLDOWN_SEC);
        configObj.addProperty("CombatNotchAppleCooldownSec", COMBAT_NOTCH_APPLE_COOLDOWN_SEC);
        configObj.addProperty("HealthRemainingPunish", HEALTH_REMAINING_PUNISH);
        configObj.addProperty("AbsorptionRemainingPunish", ABSORPTION_REMAINING_PUNISH);
        configObj.addProperty("PoisonDurationSec", POISON_DURATION_SEC);
        configObj.addProperty("PoisonLevel", POISON_LEVEL);
        configObj.addProperty("EnableHealthPunish", ENABLE_HEALTH_PUNISH);
        configObj.addProperty("EnableAbsorptionPunish", ENABLE_ABSORPTION_PUNISH);
        configObj.addProperty("EnablePoisonPunish", ENABLE_POISON_PUNISH);
        configObj.addProperty("EnableTpPunish", ENABLE_TP_PUNISH);
        configObj.addProperty("EnableInstantTpPunish", ENABLE_INSTANT_TP_PUNISH);
        configObj.addProperty("EnableNotchApplePunish", ENABLE_NOTCH_APPLE_PUNISH);
        configObj.addProperty("EnableInstantNotchApplePunish", ENABLE_INSTANT_NOTCH_APPLE_PUNISH);
        configObj.addProperty("EnableElytraPunish", ENABLE_ELYTRA_PUNISH);
        configObj.addProperty("EnableCombatColour", ENABLE_COMBAT_COLOUR);
        configObj.addProperty("DisableTeamMsgCommand", DISABLE_TEAM_MSG_COMMAND);
        configObj.addProperty("DisableTeamCommand", DISABLE_TEAM_COMMAND);

        gson.toJson(configObj, bw);
        bw.close();
    }

    private static void parse() throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonReader jr = new JsonReader(new FileReader(CONFIG_PATH.toString()));
        JsonObject configObj = gson.fromJson(jr, JsonObject.class);

        if (configObj.get("CombatDurationSec") != null) {
            COMBAT_DURATION_SEC = configObj.get("CombatDurationSec").getAsFloat();
            COMBAT_DURATION_SEC = Math.max(COMBAT_DURATION_SEC, 0.0F);
        }
        if (configObj.get("CombatTPCooldownSec") != null) {
            COMBAT_TP_COOLDOWN_SEC = configObj.get("CombatTPCooldownSec").getAsFloat();
            COMBAT_TP_COOLDOWN_SEC = Math.max(COMBAT_TP_COOLDOWN_SEC, 0.0F);
        }
        if (configObj.get("CombatNotchAppleCooldownSec") != null) {
            COMBAT_NOTCH_APPLE_COOLDOWN_SEC = configObj.get("CombatNotchAppleCooldownSec").getAsFloat();
            COMBAT_NOTCH_APPLE_COOLDOWN_SEC = Math.max(COMBAT_NOTCH_APPLE_COOLDOWN_SEC, 0.0F);
        }
        if (configObj.get("HealthRemainingPunish") != null) {
            HEALTH_REMAINING_PUNISH = configObj.get("HealthRemainingPunish").getAsFloat();
            HEALTH_REMAINING_PUNISH = Math.max(HEALTH_REMAINING_PUNISH, 0.0F);
        }
        if (configObj.get("AbsorptionRemainingPunish") != null) {
            ABSORPTION_REMAINING_PUNISH = configObj.get("AbsorptionRemainingPunish").getAsFloat();
            ABSORPTION_REMAINING_PUNISH = Math.max(ABSORPTION_REMAINING_PUNISH, 0.0F);
        }
        if (configObj.get("PoisonDurationSec") != null) {
            POISON_DURATION_SEC = configObj.get("PoisonDurationSec").getAsFloat();
            POISON_DURATION_SEC = Math.max(POISON_DURATION_SEC, 0.0F);
        }
        if (configObj.get("PoisonLevel") != null) {
            POISON_LEVEL = configObj.get("PoisonLevel").getAsInt();
            POISON_LEVEL = Math.max(POISON_LEVEL, 0);
        }
        if (configObj.get("EnableHealthPunish") != null) {
            ENABLE_HEALTH_PUNISH = configObj.get("EnableHealthPunish").getAsBoolean();
        }
        if (configObj.get("EnableAbsorptionPunish") != null) {
            ENABLE_ABSORPTION_PUNISH = configObj.get("EnableAbsorptionPunish").getAsBoolean();
        }
        if (configObj.get("EnablePoisonPunish") != null) {
            ENABLE_POISON_PUNISH = configObj.get("EnablePoisonPunish").getAsBoolean();
        }
        if (configObj.get("EnableTpPunish") != null) {
            ENABLE_TP_PUNISH = configObj.get("EnableTpPunish").getAsBoolean();
        }
        if (configObj.get("EnableInstantTpPunish") != null) {
            ENABLE_INSTANT_TP_PUNISH = configObj.get("EnableInstantTpPunish").getAsBoolean();
        }
        if (configObj.get("EnableNotchApplePunish") != null) {
            ENABLE_NOTCH_APPLE_PUNISH = configObj.get("EnableNotchApplePunish").getAsBoolean();
        }
        if (configObj.get("EnableInstantNotchApplePunish") != null) {
            ENABLE_INSTANT_NOTCH_APPLE_PUNISH = configObj.get("EnableInstantNotchApplePunish").getAsBoolean();
        }
        if (configObj.get("EnableElytraPunish") != null) {
            ENABLE_ELYTRA_PUNISH = configObj.get("EnableElytraPunish").getAsBoolean();
        }
        if (configObj.get("EnableCombatColour") != null) {
            ENABLE_COMBAT_COLOUR = configObj.get("EnableCombatColour").getAsBoolean();
        }
        if (configObj.get("DisableTeamMsgCommand") != null) {
            DISABLE_TEAM_MSG_COMMAND = configObj.get("DisableTeamMsgCommand").getAsBoolean();
        }
        if (configObj.get("DisableTeamCommand") != null) {
            DISABLE_TEAM_COMMAND = configObj.get("DisableTeamCommand").getAsBoolean();
        }

        jr.close();
        write();
    }
}
