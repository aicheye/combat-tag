package name.modid.access;

public interface ServerPlayerEntityAccess {
    boolean combat_tag$inCombat();
    void combat_tag$setCombat(boolean combat);
    float combat_tag$getProgress();
}