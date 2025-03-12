package name.modid.access;

public interface ServerPlayerEntityAccess {
    boolean combat_tag$inCombat();
    String combat_tag$setCombat(boolean combat);
    float combat_tag$getProgress();
}