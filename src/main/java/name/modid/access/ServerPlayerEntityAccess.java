package name.modid.access;

public interface ServerPlayerEntityAccess {
    boolean combat_tag$inCombat();
    void combat_tag$setCombat(boolean combat);
    int combat_tag$getPearlCooldown();
    void combat_tag$setPearlCooldown(int duration);
}