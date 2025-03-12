package name.modid;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.*;
import java.nio.file.Path;

public class Config {
    private static final Path CONFIG_PATH = Path.of("./config/CombatTag.json");

    private static float COMBAT_DURATION_SEC = 20;
    private static float COMBAT_TP_COOLDOWN_SEC = 20;
    public static float HEALTH_REMAINING_PUNISH = 1.0F;
    public static float ABSORPTION_REMAINING_PUNISH = 0.0F;
    private static float POISON_DURATION_SEC = 20;
    private static int POISON_LEVEL = 5;
    public static boolean ENABLE_HEALTH_PUNISH = true;
    public static boolean ENABLE_ABSORPTION_PUNISH= true;
    public static boolean ENABLE_POISON_PUNISH = true;
    public static boolean ENABLE_INSTANT_TP_PUNISH = true;
    public static boolean ENABLE_TP_PUNISH = true;
    public static boolean DISABLE_TEAM_MSG_COMMAND = true;
    public static boolean DISABLE_TEAM_COMMAND = true;

    public static int COMBAT_DURATION = Math.round(COMBAT_DURATION_SEC * 20);
    public static int COMBAT_TP_COOLDOWN = Math.round(COMBAT_TP_COOLDOWN_SEC * 20);
    public static int POISON_DURATION = Math.round(POISON_DURATION_SEC * 20);
    public static int POISON_AMPLIFIER = POISON_LEVEL - 1;

    public static void load() throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        if (CONFIG_PATH.toFile().exists()) {
            parse(gson);
        } else {
            throw new IOException();
        }
    }

    public static void generate() throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        BufferedWriter bw = new BufferedWriter(new FileWriter(CONFIG_PATH.toString()));
        JsonObject configObj = new JsonObject();

        configObj.addProperty("CombatDurationSec", COMBAT_DURATION_SEC);
        configObj.addProperty("CombatTPCooldownSec", COMBAT_TP_COOLDOWN_SEC);
        configObj.addProperty("HealthRemainingPunish", HEALTH_REMAINING_PUNISH);
        configObj.addProperty("AbsorptionRemainingPunish", ABSORPTION_REMAINING_PUNISH);
        configObj.addProperty("PoisonDurationSec", POISON_DURATION_SEC);
        configObj.addProperty("PoisonLevel", POISON_LEVEL);
        configObj.addProperty("EnableHealthPunish", ENABLE_HEALTH_PUNISH);
        configObj.addProperty("EnableAbsorptionPunish", ENABLE_ABSORPTION_PUNISH);
        configObj.addProperty("EnablePoisonPunish", ENABLE_POISON_PUNISH);
        configObj.addProperty("EnableInstantTPPunish", ENABLE_INSTANT_TP_PUNISH);
        configObj.addProperty("EnableTPPunish", ENABLE_TP_PUNISH);
        configObj.addProperty("DisableTeamMsgCommand", DISABLE_TEAM_MSG_COMMAND);
        configObj.addProperty("DisableTeamCommand", DISABLE_TEAM_COMMAND);

        gson.toJson(configObj, bw);
        bw.close();
    }

    private static void parse(Gson gson) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(CONFIG_PATH.toString()));
        JsonObject configObj = gson.fromJson(br, JsonObject.class);

        if (configObj.get("CombatDurationSec") != null) {
            COMBAT_DURATION_SEC = configObj.get("CombatDurationSec").getAsFloat();
        }
        if (configObj.get("CombatTPCooldownSec") != null) {
            COMBAT_TP_COOLDOWN_SEC = configObj.get("CombatTPCooldownSec").getAsFloat();
        }
        if (configObj.get("HealthRemainingPunish") != null) {
            HEALTH_REMAINING_PUNISH = configObj.get("HealthRemainingPunish").getAsFloat();
        }
        if (configObj.get("AbsorptionRemainingPunish") != null) {
            ABSORPTION_REMAINING_PUNISH = configObj.get("AbsorptionRemainingPunish").getAsFloat();
        }
        if (configObj.get("PoisonDurationSec") != null) {
            POISON_DURATION_SEC = configObj.get("PoisonDurationSec").getAsFloat();
        }
        if (configObj.get("PoisonLevel") != null) {
            POISON_LEVEL = configObj.get("PoisonLevel").getAsInt();
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
        if (configObj.get("EnableInstantTPPunish") != null) {
            ENABLE_INSTANT_TP_PUNISH = configObj.get("EnableInstantTPPunish").getAsBoolean();
        }
        if (configObj.get("EnableTPPunish") != null) {
            ENABLE_TP_PUNISH = configObj.get("EnableTPPunish").getAsBoolean();
        }
        if (configObj.get("DisableTeamMsgCommand") != null) {
            DISABLE_TEAM_MSG_COMMAND = configObj.get("DisableTeamMsgCommand").getAsBoolean();
        }
        if (configObj.get("DisableTeamCommand") != null) {
            DISABLE_TEAM_COMMAND = configObj.get("DisableTeamCommand").getAsBoolean();
        }

        br.close();
    }
}
