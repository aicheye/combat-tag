🛠️ Under Development 🛠️

Lightweight fabric server plugin to make PvP fairer and prevent combat-logging. Download on [Modrinth](https://modrinth.com/mod/aicheyes-combat-tag)!

![image](https://github.com/user-attachments/assets/30e4fe6e-58d0-4fb9-89a9-b41005c27fb0)

## Features:
- Combat disconnect punishing
  - Reduced health to half a heart (can be configured to instantly kill)
  - Reduced absorption
  - Poison
- TP cooldown (ender pearls and chorus fruit)
  - By default, the cooldown starts as soon as the player enters combat
- Enchanted golden apple cooldown
  - By default, the cooldown only starts after eating the first enchanted golden apply
- Elytras disabled while combat tagged
- Players currently combat tagged have a red username
- Combat bar to indicate how long until the player's combat tag expires
- **Fully configurable**

### Note:
An unexpected server shutdown **will** count as a disconnect (stopping the server gracefully will not).

By default, /teammsg and /team are disabled
- All players in combat are put on a "combat" team for the sake of username colouring
- Disabling username colouring also re-enables these commands

## Configuration Options
Found in `./config/CombatTag.json`

|Key|Type|Default|Description|
|---|---|---|---|
|CombatDurationSec|Float|20.0|How long the combat tag should last for|
|CombatTpCooldownSec|Float|10.0|How long the cooldown for enderpearls and chorus fruits should be during combat|
|CombatNotchAppleCooldownSec|Float|10.0|How long the cooldown for enchanted golden apples should be during combat|
|HealthRemainingPunish|Float|1.0|How much health the player should have upon reconneccting while combat tagged|
|AbsorptionRemainingPunish|Float|0.0|How much absorption the player should have upon reconnecting while combat tagged|
|PoisonDurationSec|Float|20.0|How long the player should be poisoned for upon reconnecting while combat tagged|
|PoisonLevel|Integer|5|The amplification level of the poison effect upon reconnecting while combat tagged|
|EnableHealthPunish|Boolean|true|Whether the player should lose health upon reconnecting while combat tagged|
|EnableAbsorptionPunish|Boolean|true|Whether the player should lose absorption upon reconnecting while combat tagged|
|EnablePoisonPunish|Boolean|true|Whether the player should be poisoned upon reconnecting while combat tagged|
|EnableTpPunish|Boolean|true|Whether the teleport cooldown should be active during combat|
|EnableInstantTpPunish|Boolean|true|Whether the teleport cooldown should be activated immediately upon entering combat|
|EnableNotchApplePunish|Boolean|true|Whether the enchanted golden apple cooldown should be active during combat|
|EnableInstantNotchApplePunish|Boolean|false|Whether the enchanted golden apple cooldown should be activated immediately upon entering combat|
|EnableElytraPunish|Boolean|true|Whether elytra should be disabled during combat|
|EnableCombatColour|Boolean|true|Whether players in combat should appear red (spectral arrows, scoreboard, username in chat, etc.)|
|DisableTeamMsgCommand*|Boolean|true|Whether players should be allowed to use /teammsg|
|DisableTeamCommand*|Boolean|true|Whether players should be allowed to use /team|

*Warning: disabling these options may allow players in combat to message each other or leave the combat team and lose their colour

## Requirements
- Fabric loader 16.10 or later
- Minecraft 1.21.4 or later
  - May work with older versions (have to build yourself to test)
- Java 21 or later
