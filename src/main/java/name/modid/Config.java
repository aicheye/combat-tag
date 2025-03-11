package name.modid;

public class Config {
    public static int COMBAT_DURATION_SEC = 20;
    public static int COMBAT_TP_COOLDOWN_SEC = 20;
    public static float HEALTH_REMAINING = 1.0F;
    public static float ABSORPTION_REMAINING = 0.0F;
    public static int POISON_DURATION_SEC = 20;
    public static int POISON_LEVEL = 5;
    public static boolean ENABLE_DAMAGE_PUNISH = true;
    public static boolean ENABLE_ABSORPTION_PUNISH= true;
    public static boolean ENABLE_POISON_PUNISH = true;
    public static boolean ENABLE_INSTANT_TP_PUNISH = true;
    public static boolean ENABLE_TP_PUNISH = true;
    public static boolean DISABLE_TEAM_MSG_COMMAND = true;
    public static boolean DISABLE_TEAM_COMMAND = true;

    public static int COMBAT_DURATION = COMBAT_DURATION_SEC * 20;
    public static int COMBAT_TP_COOLDOWN = COMBAT_TP_COOLDOWN_SEC * 20;
    public static int POISON_DURATION = POISON_DURATION_SEC * 20;
    public static int POISON_AMPLIFIER = POISON_LEVEL - 1;

}
