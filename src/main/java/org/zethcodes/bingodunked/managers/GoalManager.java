package org.zethcodes.bingodunked.managers;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Biome;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.generator.structure.Structure;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.zethcodes.bingodunked.goals.*;
import org.zethcodes.bingodunked.listeners.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GoalManager {
    public List<Goal> availableGoals = new ArrayList<>();
    public HashMap<Biome, Goal> biomeGoals = new HashMap<Biome, Goal>();
    public HashMap<Structure, Goal> structureGoals = new HashMap<Structure, Goal>();

    public void ClearGoals()
    {
        availableGoals.clear();
        biomeGoals.clear();
    }

    public void SetStructureGoals()
    {
        ItemStack heartOfTheSea = new ItemStack(Material.HEART_OF_THE_SEA, 1);
        CollectItemGoal heartOfTheSeaGoal = new CollectItemGoal("Collect a Heart of the Sea", heartOfTheSea);
        structureGoals.put(Structure.BURIED_TREASURE, heartOfTheSeaGoal);

        ItemStack breezeRod = new ItemStack(Material.BREEZE_ROD, 1);
        KillEntityGoal killBreezeGoal = new KillEntityGoal("Kill a Breeze", breezeRod, EntityType.BREEZE, GameManager.instance.killEntityListener);
        structureGoals.put(Structure.TRIAL_CHAMBERS, killBreezeGoal);

//        ItemStack ominousBottle = new ItemStack(Material.OMINOUS_BOTTLE, 1);
//        PotionEffectGoal trialOmenGoal = new PotionEffectGoal("Get the Trial Omen Effect", ominousBottle, PotionEffectType.TRIAL_OMEN, GameManager.instance.potionEffectListener);
//        availableGoals.add(trialOmenGoal);

        ItemStack cobweb = new ItemStack(Material.COBWEB, 32);
        CollectItemsAmountGoal cobwebGoal = new CollectItemsAmountGoal("Collect 32 Cobwebs", cobweb);
        structureGoals.put(Structure.MINESHAFT, cobwebGoal);
    }

    public void SetBiomeGoals()
    {
        ItemStack bucketOfAxolotl = new ItemStack(Material.AXOLOTL_BUCKET, 1);
        CollectItemGoal bucketOfAxolotlGoal = new CollectItemGoal("Collect a Bucket of Axoltol", bucketOfAxolotl);
        biomeGoals.put(Biome.LUSH_CAVES, bucketOfAxolotlGoal);

        ItemStack guardianEgg = new ItemStack(Material.GUARDIAN_SPAWN_EGG,1);
        KillEntityGoal guardianKillGoal = new KillEntityGoal("Kill a Guardian", guardianEgg, EntityType.GUARDIAN,GameManager.instance.killEntityListener);
        biomeGoals.put(Biome.DEEP_OCEAN,guardianKillGoal);

        ItemStack elderGuardianEgg = new ItemStack(Material.ELDER_GUARDIAN_SPAWN_EGG,1);
        KillEntityGoal elderGuardianKillGoal = new KillEntityGoal("Kill an Elder Guardian", elderGuardianEgg, EntityType.GUARDIAN,GameManager.instance.killEntityListener);
        biomeGoals.put(Biome.DEEP_LUKEWARM_OCEAN,elderGuardianKillGoal);

        ItemStack goldenapple = new ItemStack(Material.GOLDEN_APPLE,1);
        BreedEntityGoal donkeyBreedGoal = new BreedEntityGoal("Breed Two Donkeys", goldenapple, EntityType.DONKEY, GameManager.instance.breedEntityListener);
        biomeGoals.put(Biome.MEADOW, donkeyBreedGoal);

        ItemStack blindPotion = new ItemStack(Material.POTION,1);
        PotionMeta blindPotionMeta = (PotionMeta) blindPotion.getItemMeta();
        blindPotionMeta.setColor(Color.fromRGB(0, 0, 0));
        blindPotion.setItemMeta(blindPotionMeta);
        PotionEffectGoal blindGoal = new PotionEffectGoal("Get the Blindness Effect", blindPotion, PotionEffectType.BLINDNESS, GameManager.instance.potionEffectListener);
        biomeGoals.put(Biome.PLAINS,blindGoal);

        ItemStack jumpPotion = new ItemStack(Material.POTION,1);
        PotionMeta jumpPotionMeta = (PotionMeta) jumpPotion.getItemMeta();
        jumpPotionMeta.setBasePotionType(PotionType.LEAPING);
        jumpPotion.setItemMeta(jumpPotionMeta);
        PotionEffectGoal jumpBoostGoal = new PotionEffectGoal("Get the Jump Boost Effect", jumpPotion, PotionEffectType.JUMP_BOOST, GameManager.instance.potionEffectListener);
        biomeGoals.put(Biome.SUNFLOWER_PLAINS,jumpBoostGoal);

        ItemStack weaknessPotion = new ItemStack(Material.POTION,1);
        PotionMeta weaknessPotionItemMeta = (PotionMeta) weaknessPotion.getItemMeta();
        weaknessPotionItemMeta.setBasePotionType(PotionType.WEAKNESS);
        weaknessPotion.setItemMeta(weaknessPotionItemMeta);
        PotionEffectGoal weaknessGoal = new PotionEffectGoal("Get the Weakness Effect", weaknessPotion, PotionEffectType.WEAKNESS, GameManager.instance.potionEffectListener);
        biomeGoals.put(Biome.FLOWER_FOREST,weaknessGoal);

        ItemStack cookedRabbit = new ItemStack(Material.COOKED_RABBIT, 1);
        EatGoal cookedRabbitGoal = new EatGoal("Eat some Cooked Rabbit", cookedRabbit, GameManager.instance.eatListener);
        biomeGoals.put(Biome.BADLANDS, cookedRabbitGoal);

        ItemStack cookie = new ItemStack(Material.COOKIE, 1);
        EatGoal cookieGoal = new EatGoal("Eat a Cookie", cookie, GameManager.instance.eatListener);
        biomeGoals.put(Biome.JUNGLE, cookieGoal);

        ItemStack boggedEgg = new ItemStack(Material.BOGGED_SPAWN_EGG,1);
        KillEntityGoal boggedGoal = new KillEntityGoal("Kill a Bogged", boggedEgg, EntityType.BOGGED, GameManager.instance.killEntityListener);
        biomeGoals.put(Biome.SWAMP,boggedGoal);

        ItemStack goatHorn = new ItemStack(Material.GOAT_HORN, 1);
        CollectItemGoal goatHornGoal = new CollectItemGoal("Collect a Goat Horn", goatHorn);
        biomeGoals.put(Biome.JAGGED_PEAKS,goatHornGoal);

        ItemStack armascute = new ItemStack(Material.ARMADILLO_SCUTE, 1);
        BreedEntityGoal armaBreedGoal = new BreedEntityGoal("Breed Two Armadillos", armascute, EntityType.ARMADILLO, GameManager.instance.breedEntityListener);
        biomeGoals.put(Biome.SAVANNA, armaBreedGoal);

        ItemStack polarEgg = new ItemStack(Material.POLAR_BEAR_SPAWN_EGG, 1);
        KillEntityGoal polarGoal = new KillEntityGoal("Kill a Polar Bear", polarEgg, EntityType.POLAR_BEAR, GameManager.instance.killEntityListener);
        biomeGoals.put(Biome.SNOWY_PLAINS, polarGoal);

        ItemStack deadBush = new ItemStack(Material.DEAD_BUSH, 1);
        CollectItemGoal deadBushGoal = new CollectItemGoal("Collect a Dead Bush", deadBush);
        biomeGoals.put(Biome.WOODED_BADLANDS,deadBushGoal);

        ItemStack pinkPetal = new ItemStack(Material.PINK_PETALS, 1);
        CollectItemGoal pinkPetalGoal = new CollectItemGoal("Collect some Pink Petals", pinkPetal);
        biomeGoals.put(Biome.CHERRY_GROVE, pinkPetalGoal);

        ItemStack scaffolding = new ItemStack(Material.SCAFFOLDING,1);
        CollectItemGoal scaffoldingGoal = new CollectItemGoal("Craft some Scaffolding", scaffolding);
        biomeGoals.put(Biome.BAMBOO_JUNGLE,scaffoldingGoal);

        ItemStack greenConcrete = new ItemStack(Material.GREEN_CONCRETE, 1);
        CollectColouredItemGoal greenConcreteGoal = new CollectColouredItemGoal("Collect a Block of Green Concrete", greenConcrete);
        biomeGoals.put(Biome.DESERT,greenConcreteGoal);

        ItemStack seaCucum = new ItemStack(Material.SEA_PICKLE,1);
        CollectItemGoal seaPickleGoal = new CollectItemGoal("Collect a Sea Pickle", seaCucum);
        biomeGoals.put(Biome.WARM_OCEAN, seaPickleGoal);

        ItemStack sculkSensor = new ItemStack(Material.SCULK_SENSOR,1);
        CompleteAdvancementGoal sneakGoal = new CompleteAdvancementGoal("Complete the advancement Sneak 100", sculkSensor, Bukkit.getAdvancement(new NamespacedKey("minecraft","adventure/avoid_vibration")));
        biomeGoals.put(Biome.DEEP_DARK, sneakGoal);

        ItemStack wolfArmour = new ItemStack(Material.WOLF_ARMOR, 1);
        CollectItemGoal wolfArmourGoal = new CollectItemGoal("Collect some Wolf Armor", wolfArmour);
        biomeGoals.put(Biome.SAVANNA_PLATEAU,wolfArmourGoal);

        ItemStack bucSalmon = new ItemStack(Material.SALMON_BUCKET, 1);
        CompleteAdvancementGoal tacFishGoal = new CompleteAdvancementGoal("Complete the advancement Tactical Fishing!", bucSalmon, Bukkit.getAdvancement(new NamespacedKey("minecraft", "husbandry/tactical_fishing")));
        biomeGoals.put(Biome.COLD_OCEAN,tacFishGoal);

        ItemStack squidEgg = new ItemStack(Material.SQUID_SPAWN_EGG,1);
        KillEntityGoal killSquidGoal = new KillEntityGoal("Kill a Squid",squidEgg,EntityType.SQUID,GameManager.instance.killEntityListener);
        biomeGoals.put(Biome.DEEP_COLD_OCEAN,killSquidGoal);

        ItemStack sealantern = new ItemStack(Material.SEA_LANTERN,1);
        CollectItemGoal seaLanternGoal = new CollectItemGoal("Collect a Sea Lantern",sealantern);
        biomeGoals.put(Biome.DEEP_FROZEN_OCEAN,seaLanternGoal);

        List<Material> mushrooms = new ArrayList<>();
        mushrooms.add(Material.RED_MUSHROOM);
        mushrooms.add(Material.BROWN_MUSHROOM);
        mushrooms.add(Material.WARPED_FUNGUS);
        mushrooms.add(Material.CRIMSON_FUNGUS);
        CollectItemsAmountGoal mushroomsGoal = new CollectItemsAmountGoal("Collect 32 Mushrooms of Any Type", mushrooms, 32);
        biomeGoals.put(Biome.DARK_FOREST, mushroomsGoal);

        ItemStack redMushroom = new ItemStack(Material.RED_MUSHROOM,1);
        BreedEntityGoal mooshroomBreedGoal = new BreedEntityGoal("Breed a Mooshroom", redMushroom, EntityType.MOOSHROOM,GameManager.instance.breedEntityListener);
        biomeGoals.put(Biome.MUSHROOM_FIELDS,mooshroomBreedGoal);

        ItemStack coarsedirt = new ItemStack(Material.COARSE_DIRT, 64);
        CollectItemsAmountGoal coarseDirtGoal = new CollectItemsAmountGoal("Collect 64 Coarse Dirt Blocks", coarsedirt);
        biomeGoals.put(Biome.OLD_GROWTH_SPRUCE_TAIGA,coarseDirtGoal);

        ItemStack muddyMangroveRoots = new ItemStack(Material.MUDDY_MANGROVE_ROOTS, 16);
        CollectItemsAmountGoal mangroveRootsGoal = new CollectItemsAmountGoal("Collect 16 Muddy Mangrove Roots", muddyMangroveRoots);
        biomeGoals.put(Biome.MANGROVE_SWAMP, mangroveRootsGoal);

        ItemStack creakingEgg = new ItemStack(Material.CREAKING_SPAWN_EGG, 1);
        KillEntityGoal killCreakingGoal = new KillEntityGoal("Kill a Creaking", creakingEgg, EntityType.CREAKING, GameManager.instance.killEntityListener);
        biomeGoals.put(Biome.PALE_GARDEN, killCreakingGoal);

        ItemStack leatherBoots = new ItemStack(Material.LEATHER_BOOTS, 1);
        CompleteAdvancementGoal lightAsRabbitGoal = new CompleteAdvancementGoal("Complete the advancement Light as a Rabbit", leatherBoots, Bukkit.getAdvancement(new NamespacedKey("minecraft", "adventure/walk_on_powder_snow_with_leather_boots")));
        biomeGoals.put(Biome.SNOWY_SLOPES, lightAsRabbitGoal);

        ItemStack berries = new ItemStack(Material.SWEET_BERRIES,1);
        DeathGoal berryPokedGoal = new DeathGoal("Achieve the Death Message \n'<player> was poked to death by a sweet berry bush'",berries,"was poked to death by a sweet berry bush",GameManager.instance.deathListener);
        biomeGoals.put(Biome.TAIGA,berryPokedGoal);
    }

    public void AddBiomeGoals(Biome biome)
    {
        if (!biomeGoals.containsKey(biome)) return;
        availableGoals.add(biomeGoals.get(biome));
        biomeGoals.remove(biome);
    }

    public void AddStructureGoals(Structure structure)
    {
        if (!structureGoals.containsKey(structure)) return;
        availableGoals.add(structureGoals.get(structure));
        structureGoals.remove(structure);
    }

    public void AddGoals(int stage)
    {

        if (stage == 0)
        {
            ItemStack decoratedPot = new ItemStack(Material.DECORATED_POT, 1);
            CollectItemGoal decoratedPotGoal = new CollectItemGoal("Craft a Decorated Pot", decoratedPot);
            availableGoals.add(decoratedPotGoal);

            ItemStack brush = new ItemStack(Material.BRUSH, 1);
            CollectItemGoal brushGoal = new CollectItemGoal("Collect a Brush", brush);
            availableGoals.add(brushGoal);

            ItemStack spyglass = new ItemStack(Material.SPYGLASS, 1);
            CollectItemGoal spyglassGoal = new CollectItemGoal("Collect a Spyglass", spyglass);
            availableGoals.add(spyglassGoal);

            ItemStack bundle = new ItemStack(Material.BUNDLE, 1);
            CollectItemGoal bundleGoal = new CollectItemGoal("Collect a Bundle", bundle);
            availableGoals.add(bundleGoal);

            ItemStack loom = new ItemStack(Material.LOOM, 1);
            CollectItemGoal loomGoal = new CollectItemGoal("Collect a Loom", loom);
            availableGoals.add(loomGoal);

            ItemStack wheat = new ItemStack(Material.WHEAT,1);
            BreedEntityGoal sheepBreedGoal = new BreedEntityGoal("Breed Two Sheep", wheat, EntityType.SHEEP, GameManager.instance.breedEntityListener);
            availableGoals.add(sheepBreedGoal);

            ItemStack leather = new ItemStack(Material.LEATHER, 1);
            BreedEntityGoal cowBreedGoal = new BreedEntityGoal("Breed Two Cows", leather, EntityType.COW, GameManager.instance.breedEntityListener);
            availableGoals.add(cowBreedGoal);

            ItemStack honeyBlock = new ItemStack(Material.HONEY_BLOCK, 1);
            BreedEntityGoal beeBreedGoal = new BreedEntityGoal("Breed Two Bees", honeyBlock, EntityType.BEE, GameManager.instance.breedEntityListener);
            availableGoals.add(beeBreedGoal);

            ItemStack seed = new ItemStack(Material.WHEAT_SEEDS,1);
            BreedEntityGoal chickenBreedGoal = new BreedEntityGoal("Breed Two Chickens", seed, EntityType.CHICKEN, GameManager.instance.breedEntityListener);
            availableGoals.add(chickenBreedGoal);

            ItemStack nautShell = new ItemStack(Material.NAUTILUS_SHELL,1);
            FishingGoal nautShellGoal = new FishingGoal("Fish up some Treasure", nautShell, Material.NAUTILUS_SHELL, GameManager.instance.fishingListener);
            availableGoals.add(nautShellGoal);

            ItemStack lilyPad = new ItemStack(Material.LILY_PAD,1);
            FishingGoal leatherBootsGoal = new FishingGoal("Fish up some Junk", lilyPad, Material.LEATHER_BOOTS, GameManager.instance.fishingListener);
            availableGoals.add(leatherBootsGoal);

            ItemStack pufferFish = new ItemStack(Material.PUFFERFISH,1);
            FishingGoal pufferFishGoal = new FishingGoal("Fish up a Pufferfish", pufferFish, Material.PUFFERFISH, GameManager.instance.fishingListener);
            availableGoals.add(pufferFishGoal);

            ItemStack feather = new ItemStack(Material.FEATHER,1);
            FallGoal fall75Goal = new FallGoal("Fall from 75 Blocks", feather, 75, GameManager.instance.fallHeightListener);
            availableGoals.add(fall75Goal);

            FallGoal fall150Goal = new FallGoal("Fall from 150 Blocks", feather, 150, GameManager.instance.fallHeightListener);
            availableGoals.add(fall150Goal);

            ItemStack xpBottle5 = new ItemStack(Material.EXPERIENCE_BOTTLE, 5);
            ExperienceGoal exp5Goal = new ExperienceGoal("Get to Level 5", xpBottle5, 5, GameManager.instance.experienceListener);
            availableGoals.add(exp5Goal);

            ItemStack driedKelp = new ItemStack(Material.DRIED_KELP, 1);
            EatGoal driedKelpGoal = new EatGoal("Eat some Dried Kelp", driedKelp, GameManager.instance.eatListener);
            availableGoals.add(driedKelpGoal);

            ItemStack magentaBanner = new ItemStack(Material.MAGENTA_BANNER, 1);
            CollectColouredItemGoal magentaBannerGoal = new CollectColouredItemGoal("Craft a Magenta Banner", magentaBanner);
            availableGoals.add(magentaBannerGoal);

            ItemStack orangeBanner = new ItemStack(Material.ORANGE_BANNER, 1);
            CollectColouredItemGoal orangeBannerGoal = new CollectColouredItemGoal("Craft an Orange Banner", orangeBanner);
            availableGoals.add(orangeBannerGoal);

            ItemStack pinkBanner = new ItemStack(Material.PINK_BANNER, 1);
            CollectColouredItemGoal pinkBannerGoal = new CollectColouredItemGoal("Craft an Pink Banner", pinkBanner);
            availableGoals.add(pinkBannerGoal);

            ItemStack lightblueBanner = new ItemStack(Material.LIGHT_BLUE_BANNER, 1);
            CollectColouredItemGoal lightblueBannerGoal = new CollectColouredItemGoal("Craft an Light Blue Banner", lightblueBanner);
            availableGoals.add(lightblueBannerGoal);

            ItemStack magentaConcrete = new ItemStack(Material.MAGENTA_CONCRETE, 1);
            CollectColouredItemGoal magentaConcreteGoal = new CollectColouredItemGoal("Collect a Block of Magenta Concrete", magentaConcrete);
            availableGoals.add(magentaConcreteGoal);

            ItemStack orangeConcrete = new ItemStack(Material.ORANGE_CONCRETE, 1);
            CollectColouredItemGoal orangeConcreteGoal = new CollectColouredItemGoal("Collect a Block of Orange Concrete", orangeConcrete);
            availableGoals.add(orangeConcreteGoal);

            ItemStack pinkConcrete = new ItemStack(Material.PINK_CONCRETE, 1);
            CollectColouredItemGoal pinkConcreteGoal = new CollectColouredItemGoal("Collect a Block of Pink Concrete", pinkConcrete);
            availableGoals.add(pinkConcreteGoal);

            ItemStack lightBlueConcrete = new ItemStack(Material.LIGHT_BLUE_CONCRETE, 1);
            CollectColouredItemGoal lightBlueConcreteGoal = new CollectColouredItemGoal("Collect a Block of Light Blue Concrete", lightBlueConcrete);
            availableGoals.add(lightBlueConcreteGoal);

            ItemStack magentaGlass = new ItemStack(Material.MAGENTA_STAINED_GLASS, 1);
            CollectColouredItemGoal magentaGlassGoal = new CollectColouredItemGoal("Collect a Block of Magenta Stained Glass", magentaGlass);
            availableGoals.add(magentaGlassGoal);

            ItemStack orangeGlass = new ItemStack(Material.ORANGE_STAINED_GLASS, 1);
            CollectColouredItemGoal orangeGlassGoal = new CollectColouredItemGoal("Collect a Block of Orange Stained Glass", orangeGlass);
            availableGoals.add(orangeGlassGoal);

            ItemStack pinkGlass = new ItemStack(Material.PINK_STAINED_GLASS, 1);
            CollectColouredItemGoal pinkGlassGoal = new CollectColouredItemGoal("Collect a Block of Pink Stained Glass", pinkGlass);
            availableGoals.add(pinkGlassGoal);

            ItemStack lightBlueGlass = new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS, 1);
            CollectColouredItemGoal lightBlueGlassGoal = new CollectColouredItemGoal("Collect a Block of Light Blue Stained Glass", lightBlueGlass);
            availableGoals.add(lightBlueGlassGoal);

            List<Material> bannerPatterns = new ArrayList<>();
            bannerPatterns.add(Material.FLOWER_BANNER_PATTERN);
            bannerPatterns.add(Material.CREEPER_BANNER_PATTERN);
            bannerPatterns.add(Material.SKULL_BANNER_PATTERN);
            bannerPatterns.add(Material.MOJANG_BANNER_PATTERN);
            bannerPatterns.add(Material.GLOBE_BANNER_PATTERN);
            bannerPatterns.add(Material.PIGLIN_BANNER_PATTERN);
            bannerPatterns.add(Material.FLOW_BANNER_PATTERN);
            bannerPatterns.add(Material.GUSTER_BANNER_PATTERN);
            bannerPatterns.add(Material.FIELD_MASONED_BANNER_PATTERN);
            bannerPatterns.add(Material.BORDURE_INDENTED_BANNER_PATTERN);
            CollectItemsGoal bannerPatternGoal = new CollectItemsGoal("Collect any Banner Pattern", bannerPatterns);
            availableGoals.add(bannerPatternGoal);

            ItemStack shield = new ItemStack(Material.SHIELD,1);
            CompleteAdvancementGoal nottodayGoal = new CompleteAdvancementGoal("Complete the advancement Not Today, Thank You", shield, Bukkit.getAdvancement(new NamespacedKey("minecraft", "story/deflect_arrow")));
            availableGoals.add(nottodayGoal);
        } else if (stage == 1)
        {
            ItemStack diamondBlock = new ItemStack(Material.DIAMOND_BLOCK, 1);
            CollectItemGoal diamondBlockGoal = new CollectItemGoal("Collect a Diamond Block", diamondBlock);
            availableGoals.add(diamondBlockGoal);

            ItemStack clock = new ItemStack(Material.CLOCK, 1);
            CollectItemGoal clockGoal = new CollectItemGoal("Collect a Clock", clock);
            availableGoals.add(clockGoal);

            ItemStack crossbow = new ItemStack(Material.CROSSBOW, 1);
            CollectItemGoal crossbowGoal = new CollectItemGoal("Collect a Crossbow", crossbow);
            availableGoals.add(crossbowGoal);

            ItemStack milk = new ItemStack(Material.MILK_BUCKET, 1);
            CollectItemGoal milkGoal = new CollectItemGoal("Collect a Bucket of Milk", milk);
            availableGoals.add(milkGoal);

            List<Material> hangingSigns = new ArrayList<>();
            hangingSigns.add(Material.OAK_HANGING_SIGN);
            hangingSigns.add(Material.ACACIA_HANGING_SIGN);
            hangingSigns.add(Material.BAMBOO_HANGING_SIGN);
            hangingSigns.add(Material.BIRCH_HANGING_SIGN);
            hangingSigns.add(Material.CRIMSON_HANGING_SIGN);
            hangingSigns.add(Material.DARK_OAK_HANGING_SIGN);
            hangingSigns.add(Material.JUNGLE_HANGING_SIGN);
            hangingSigns.add(Material.MANGROVE_HANGING_SIGN);
            hangingSigns.add(Material.OAK_HANGING_SIGN);
            hangingSigns.add(Material.SPRUCE_HANGING_SIGN);
            hangingSigns.add(Material.WARPED_HANGING_SIGN);
            hangingSigns.add(Material.PALE_OAK_HANGING_SIGN);
            CollectItemsGoal hangingSignGoal = new CollectItemsGoal("Collect a Hanging Sign of Any Type", hangingSigns);
            availableGoals.add(hangingSignGoal);

            ItemStack xpBottle15 = new ItemStack(Material.EXPERIENCE_BOTTLE, 15);
            ExperienceGoal exp15Goal = new ExperienceGoal("Get to Level 15", xpBottle15, 15, GameManager.instance.experienceListener);
            availableGoals.add(exp15Goal);

            ItemStack lichen = new ItemStack(Material.GLOW_LICHEN, 1);
            CollectItemGoal lichenGoal = new CollectItemGoal("Collect Glow Lichen", lichen);
            availableGoals.add(lichenGoal);

            List<Material> potterySherds = new ArrayList<>();
            potterySherds.add(Material.ANGLER_POTTERY_SHERD);
            potterySherds.add(Material.ARCHER_POTTERY_SHERD);
            potterySherds.add(Material.ARMS_UP_POTTERY_SHERD);
            potterySherds.add(Material.BLADE_POTTERY_SHERD);
            potterySherds.add(Material.BREWER_POTTERY_SHERD);
            potterySherds.add(Material.BURN_POTTERY_SHERD);
            potterySherds.add(Material.DANGER_POTTERY_SHERD);
            potterySherds.add(Material.EXPLORER_POTTERY_SHERD);
            potterySherds.add(Material.FRIEND_POTTERY_SHERD);
            potterySherds.add(Material.HEART_POTTERY_SHERD);
            potterySherds.add(Material.HEARTBREAK_POTTERY_SHERD);
            potterySherds.add(Material.HOWL_POTTERY_SHERD);
            potterySherds.add(Material.MINER_POTTERY_SHERD);
            potterySherds.add(Material.MOURNER_POTTERY_SHERD);
            potterySherds.add(Material.PLENTY_POTTERY_SHERD);
            potterySherds.add(Material.PRIZE_POTTERY_SHERD);
            potterySherds.add(Material.SHEAF_POTTERY_SHERD);
            potterySherds.add(Material.SHELTER_POTTERY_SHERD);
            potterySherds.add(Material.SKULL_POTTERY_SHERD);
            potterySherds.add(Material.SNORT_POTTERY_SHERD);
            potterySherds.add(Material.FLOW_POTTERY_SHERD);
            potterySherds.add(Material.GUSTER_POTTERY_SHERD);
            potterySherds.add(Material.SCRAPE_POTTERY_SHERD);
            CollectItemsGoal potterySherdGoal = new CollectItemsGoal("Collect any Pottery Sherd", potterySherds);
            availableGoals.add(potterySherdGoal);

            ItemStack carvedPumpkin = new ItemStack(Material.CARVED_PUMPKIN, 1);
            CompleteAdvancementGoal hiredHelpGoal = new CompleteAdvancementGoal("Complete the advancement Hired Help", carvedPumpkin, Bukkit.getAdvancement(new NamespacedKey("minecraft","adventure/summon_iron_golem")));
            availableGoals.add(hiredHelpGoal);
        } else if (stage == 2)
        {
            ItemStack emeraldBlock = new ItemStack(Material.EMERALD_BLOCK, 1);
            CollectItemGoal emeraldBlockGoal = new CollectItemGoal("Collect an Emerald Block", emeraldBlock);
            availableGoals.add(emeraldBlockGoal);

            List<Material> candles = new ArrayList<>();
            candles.add(Material.CANDLE);
            candles.add(Material.WHITE_CANDLE);
            candles.add(Material.ORANGE_CANDLE);
            candles.add(Material.MAGENTA_CANDLE);
            candles.add(Material.LIGHT_BLUE_CANDLE);
            candles.add(Material.YELLOW_CANDLE);
            candles.add(Material.LIME_CANDLE);
            candles.add(Material.PINK_CANDLE);
            candles.add(Material.GRAY_CANDLE);
            candles.add(Material.LIGHT_GRAY_CANDLE);
            candles.add(Material.CYAN_CANDLE);
            candles.add(Material.PURPLE_CANDLE);
            candles.add(Material.BLUE_CANDLE);
            candles.add(Material.BROWN_CANDLE);
            candles.add(Material.GREEN_CANDLE);
            candles.add(Material.RED_CANDLE);
            candles.add(Material.BLACK_CANDLE);
            CollectItemsGoal candleGoal = new CollectItemsGoal("Collect any Candle", candles);
            availableGoals.add(candleGoal);

            ItemStack endermanEgg = new ItemStack(Material.ENDERMAN_SPAWN_EGG,1);
            KillEntityGoal endermanKillGoal = new KillEntityGoal("Kill an Enderman", endermanEgg, EntityType.ENDERMAN, GameManager.instance.killEntityListener);
            availableGoals.add(endermanKillGoal);

            ItemStack witchEgg = new ItemStack(Material.WITCH_SPAWN_EGG,1);
            KillEntityGoal witchKillGoal = new KillEntityGoal("Kill a Witch", witchEgg, EntityType.WITCH, GameManager.instance.killEntityListener);
            availableGoals.add(witchKillGoal);

            ItemStack bone = new ItemStack(Material.BONE,1);
            BreedEntityGoal dogBreedGoal = new BreedEntityGoal("Breed Two Wolves", bone, EntityType.WOLF, GameManager.instance.breedEntityListener);
            availableGoals.add(dogBreedGoal);

            ItemStack satPotion = new ItemStack(Material.POTION,1);
            PotionMeta satPotionMeta = (PotionMeta) satPotion.getItemMeta();
            satPotionMeta.setColor(Color.fromRGB(155, 122, 1));
            satPotion.setItemMeta(satPotionMeta);
            PotionEffectGoal satGoal = new PotionEffectGoal("Get the Saturation Effect", satPotion, PotionEffectType.SATURATION, GameManager.instance.potionEffectListener);
            availableGoals.add(satGoal);

            ItemStack poisonPotion = new ItemStack(Material.POTION,1);
            PotionMeta poisonPotionMeta = (PotionMeta) poisonPotion.getItemMeta();
            poisonPotionMeta.setBasePotionType(PotionType.POISON);
            poisonPotion.setItemMeta(poisonPotionMeta);
            PotionEffectGoal poisonGoal = new PotionEffectGoal("Get the Poison Effect", poisonPotion, PotionEffectType.POISON, GameManager.instance.potionEffectListener);
            availableGoals.add(poisonGoal);

            ItemStack regenPotion = new ItemStack(Material.POTION,1);
            PotionMeta regenPotionMeta = (PotionMeta) regenPotion.getItemMeta();
            regenPotionMeta.setBasePotionType(PotionType.REGENERATION);
            regenPotion.setItemMeta(regenPotionMeta);
            PotionEffectGoal regenGoal = new PotionEffectGoal("Get the Regeneration Effect", regenPotion, PotionEffectType.REGENERATION, GameManager.instance.potionEffectListener);
            availableGoals.add(regenGoal);

            ItemStack nightVisionPotion = new ItemStack(Material.POTION,1);
            PotionMeta nightVisionPotionItemMeta = (PotionMeta) nightVisionPotion.getItemMeta();
            nightVisionPotionItemMeta.setBasePotionType(PotionType.NIGHT_VISION);
            nightVisionPotion.setItemMeta(nightVisionPotionItemMeta);
            PotionEffectGoal nightVisionGoal = new PotionEffectGoal("Get the Night Vision Effect", nightVisionPotion, PotionEffectType.NIGHT_VISION, GameManager.instance.potionEffectListener);
            availableGoals.add(nightVisionGoal);

            ItemStack stone = new ItemStack(Material.STONE, 1);
            BreakBlockTypeGoal stoneBreakGoal = new BreakBlockTypeGoal("Break 100 Blocks of Stone", stone, BreakBlockTypeListener.BlockType.STONE, 100, GameManager.instance.blockTypeListener);
            availableGoals.add(stoneBreakGoal);

            ItemStack oakLog = new ItemStack(Material.OAK_LOG, 1);
            BreakBlockTypeGoal logBreakGoal = new BreakBlockTypeGoal("Break 100 Logs", oakLog, BreakBlockTypeListener.BlockType.LOG, 100, GameManager.instance.blockTypeListener);
            availableGoals.add(logBreakGoal);

            ItemStack diamondOre = new ItemStack(Material.DIAMOND_ORE, 1);
            BreakBlockTypeGoal oreBreakGoal = new BreakBlockTypeGoal("Break 50 Ore Blocks", diamondOre, BreakBlockTypeListener.BlockType.ORE, 50, GameManager.instance.blockTypeListener);
            availableGoals.add(oreBreakGoal);

            ItemStack xpBottle30 = new ItemStack(Material.EXPERIENCE_BOTTLE, 30);
            ExperienceGoal exp30Goal = new ExperienceGoal("Get to Level 30", xpBottle30, 30, GameManager.instance.experienceListener);
            availableGoals.add(exp30Goal);

            List<Material> discs = new ArrayList<>();
            discs.add(Material.MUSIC_DISC_5);
            discs.add(Material.MUSIC_DISC_OTHERSIDE);
            discs.add(Material.MUSIC_DISC_11);
            discs.add(Material.MUSIC_DISC_13);
            discs.add(Material.MUSIC_DISC_CAT);
            discs.add(Material.MUSIC_DISC_BLOCKS);
            discs.add(Material.MUSIC_DISC_CHIRP);
            discs.add(Material.MUSIC_DISC_FAR);
            discs.add(Material.MUSIC_DISC_MALL);
            discs.add(Material.MUSIC_DISC_MELLOHI);
            discs.add(Material.MUSIC_DISC_PIGSTEP);
            discs.add(Material.MUSIC_DISC_RELIC);
            discs.add(Material.MUSIC_DISC_STAL);
            discs.add(Material.MUSIC_DISC_STRAD);
            discs.add(Material.MUSIC_DISC_WAIT);
            discs.add(Material.MUSIC_DISC_WARD);
            discs.add(Material.MUSIC_DISC_CREATOR);
            discs.add(Material.MUSIC_DISC_CREATOR_MUSIC_BOX);
            discs.add(Material.MUSIC_DISC_PRECIPICE);
            discs.add(Material.MUSIC_DISC_TEARS);
            CollectItemsGoal discGoal = new CollectItemsGoal("Collect any Music Disc",discs);
            availableGoals.add(discGoal);

            List<Material> horseArmors = new ArrayList<>();
            horseArmors.add(Material.IRON_HORSE_ARMOR);
            horseArmors.add(Material.LEATHER_HORSE_ARMOR);
            horseArmors.add(Material.GOLDEN_HORSE_ARMOR);
            horseArmors.add(Material.DIAMOND_HORSE_ARMOR);
            CollectItemsGoal horseArmorGoal = new CollectItemsGoal("Collect any Horse Armor", horseArmors);
            availableGoals.add(horseArmorGoal);

            List<Material> smithingTemplates = new ArrayList<>();
            smithingTemplates.add(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE);
            smithingTemplates.add(Material.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE);
            smithingTemplates.add(Material.VEX_ARMOR_TRIM_SMITHING_TEMPLATE);
            smithingTemplates.add(Material.WILD_ARMOR_TRIM_SMITHING_TEMPLATE);
            smithingTemplates.add(Material.COAST_ARMOR_TRIM_SMITHING_TEMPLATE);
            smithingTemplates.add(Material.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE);
            smithingTemplates.add(Material.WARD_ARMOR_TRIM_SMITHING_TEMPLATE);
            smithingTemplates.add(Material.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE);
            smithingTemplates.add(Material.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE);
            smithingTemplates.add(Material.RIB_ARMOR_TRIM_SMITHING_TEMPLATE);
            smithingTemplates.add(Material.EYE_ARMOR_TRIM_SMITHING_TEMPLATE);
            smithingTemplates.add(Material.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE);
            smithingTemplates.add(Material.FLOW_ARMOR_TRIM_SMITHING_TEMPLATE);
            smithingTemplates.add(Material.BOLT_ARMOR_TRIM_SMITHING_TEMPLATE);
            smithingTemplates.add(Material.WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE);
            CollectItemsGoal smithingTemplateGoal = new CollectItemsGoal("Collect any Smithing Template", smithingTemplates);
            availableGoals.add(smithingTemplateGoal);

            ItemStack cake = new ItemStack(Material.CAKE,1);
            CollectItemGoal cakeGoal = new CollectItemGoal("Craft a Cake", cake);
            availableGoals.add(cakeGoal);

            ItemStack pumpkinPie = new ItemStack(Material.PUMPKIN_PIE, 1);
            EatGoal pumpkinPieGoal = new EatGoal("Eat a Pumpkin Pie", pumpkinPie, GameManager.instance.eatListener);
            availableGoals.add(pumpkinPieGoal);

            ItemStack feather = new ItemStack(Material.FEATHER,1);
            FallGoal fall225Goal = new FallGoal("Fall from 225 Blocks", feather, 225, GameManager.instance.fallHeightListener);
            availableGoals.add(fall225Goal);

            ItemStack crafter = new ItemStack(Material.CRAFTER, 1);
            CollectItemGoal crafterGoal = new CollectItemGoal("Craft a Crafter", crafter);
            availableGoals.add(crafterGoal);

            ItemStack anvil = new ItemStack(Material.ANVIL, 1);
            CollectItemGoal anvilGoal = new CollectItemGoal("Collect an Anvil", anvil);
            availableGoals.add(anvilGoal);
        } else if (stage == 3)
        {
            ItemStack sharpnessBook = new ItemStack(Material.ENCHANTED_BOOK,1);
            EnchantmentStorageMeta sharpnessMeta = (EnchantmentStorageMeta) sharpnessBook.getItemMeta();
            sharpnessMeta.addStoredEnchant(Enchantment.SHARPNESS,2, false);
            EnchantItemGoal sharpnessGoal = new EnchantItemGoal("Enchant an Item with at Sharpness", sharpnessBook, Enchantment.SHARPNESS,1, GameManager.instance.enchantListener);
            availableGoals.add(sharpnessGoal);

            ItemStack protectionBook = new ItemStack(Material.ENCHANTED_BOOK,1);
            EnchantmentStorageMeta protectionMeta = (EnchantmentStorageMeta) protectionBook.getItemMeta();
            protectionMeta.addStoredEnchant(Enchantment.PROTECTION,2, false);
            EnchantItemGoal protectionGoal = new EnchantItemGoal("Enchant an Item with Protection", protectionBook, Enchantment.PROTECTION,1, GameManager.instance.enchantListener);
            availableGoals.add(protectionGoal);

            ItemStack efficiencyBook = new ItemStack(Material.ENCHANTED_BOOK,1);
            EnchantmentStorageMeta efficiencyMeta = (EnchantmentStorageMeta) efficiencyBook.getItemMeta();
            efficiencyMeta.addStoredEnchant(Enchantment.EFFICIENCY,2, false);
            EnchantItemGoal efficiencyGoal = new EnchantItemGoal("Enchant an Item with Efficiency", efficiencyBook, Enchantment.EFFICIENCY,1, GameManager.instance.enchantListener);
            availableGoals.add(efficiencyGoal);

            ItemStack powerBook = new ItemStack(Material.ENCHANTED_BOOK,1);
            EnchantmentStorageMeta powerMeta = (EnchantmentStorageMeta) powerBook.getItemMeta();
            powerMeta.addStoredEnchant(Enchantment.POWER,2, false);
            EnchantItemGoal powerGoal = new EnchantItemGoal("Enchant an Item with Power", powerBook, Enchantment.POWER,1, GameManager.instance.enchantListener);
            availableGoals.add(powerGoal);

            ItemStack piercingBook = new ItemStack(Material.ENCHANTED_BOOK,1);
            EnchantmentStorageMeta piercingMeta = (EnchantmentStorageMeta) piercingBook.getItemMeta();
            piercingMeta.addStoredEnchant(Enchantment.PIERCING,2, false);
            EnchantItemGoal piercingGoal = new EnchantItemGoal("Enchant an Item with Piercing", piercingBook, Enchantment.PIERCING,1, GameManager.instance.enchantListener);
            availableGoals.add(piercingGoal);

            ItemStack unbreakingBook = new ItemStack(Material.ENCHANTED_BOOK, 1);
            EnchantmentStorageMeta unbreakingMeta = (EnchantmentStorageMeta) unbreakingBook.getItemMeta();
            unbreakingMeta.addStoredEnchant(Enchantment.UNBREAKING, 2, false);
            EnchantItemGoal unbreakingGoal = new EnchantItemGoal("Enchant an Item with Unbreaking", unbreakingBook, Enchantment.UNBREAKING, 1, GameManager.instance.enchantListener);
            availableGoals.add(unbreakingGoal);

            ItemStack fireCharge = new ItemStack(Material.FIRE_CHARGE,1);
            CompleteAdvancementGoal returnToSender = new CompleteAdvancementGoal("Complete the advancement Return to Sender", fireCharge, Bukkit.getAdvancement(new NamespacedKey("minecraft","nether/return_to_sender")));
            availableGoals.add(returnToSender);
        } else if (stage == 4)
        {
            ItemStack magmacream = new ItemStack(Material.MAGMA_CREAM, 1);
            CollectItemGoal magmacreamGoal = new CollectItemGoal("Collect a Magma Cream", magmacream);
            biomeGoals.put(Biome.BASALT_DELTAS, magmacreamGoal);

            ItemStack zombiePiglinEgg = new ItemStack(Material.ZOMBIFIED_PIGLIN_SPAWN_EGG,1);
            KillEntityGoal zombiePiglinGoal = new KillEntityGoal("Kill a Zombie Piglin", zombiePiglinEgg, EntityType.ZOMBIFIED_PIGLIN, GameManager.instance.killEntityListener);
            availableGoals.add(zombiePiglinGoal);

            ItemStack zoglinEgg = new ItemStack(Material.ZOGLIN_SPAWN_EGG, 1);
            KillEntityGoal zoglinGoal = new KillEntityGoal("Kill a Zoglin", zoglinEgg, EntityType.ZOGLIN, GameManager.instance.killEntityListener);
            availableGoals.add(zoglinGoal);

            ItemStack warpedFungus = new ItemStack(Material.WARPED_FUNGUS,1);
            BreedEntityGoal striderBreedGoal = new BreedEntityGoal("Breed Two Striders", warpedFungus, EntityType.STRIDER, GameManager.instance.breedEntityListener);
            availableGoals.add(striderBreedGoal);

            ItemStack fireResPotion = new ItemStack(Material.POTION,1);
            PotionMeta fireResPotionMeta = (PotionMeta) fireResPotion.getItemMeta();
            fireResPotionMeta.setBasePotionType(PotionType.FIRE_RESISTANCE);
            fireResPotion.setItemMeta(fireResPotionMeta);
            PotionEffectGoal fireResGoal = new PotionEffectGoal("Get the Fire Resistance Effect", fireResPotion, PotionEffectType.FIRE_RESISTANCE, GameManager.instance.potionEffectListener);
            availableGoals.add(fireResGoal);

            ItemStack infestationPotion = new ItemStack(Material.POTION, 1);
            PotionMeta infestationPotionItemMeta = (PotionMeta) infestationPotion.getItemMeta();
            infestationPotionItemMeta.setBasePotionType(PotionType.INFESTED);
            infestationPotion.setItemMeta(infestationPotionItemMeta);
            PotionEffectGoal infestationGoal = new PotionEffectGoal("Get the Infestation Effect", infestationPotion, PotionEffectType.INFESTED, GameManager.instance.potionEffectListener);
            availableGoals.add(infestationGoal);

            ItemStack swiftnessPotion = new ItemStack(Material.POTION, 1);
            PotionMeta swiftnessPotionItemMeta = (PotionMeta) swiftnessPotion.getItemMeta();
            swiftnessPotionItemMeta.setBasePotionType(PotionType.SWIFTNESS);
            swiftnessPotion.setItemMeta(swiftnessPotionItemMeta);
            PotionEffectGoal swiftnessGoal = new PotionEffectGoal("Get the Speed Effect", swiftnessPotion, PotionEffectType.SPEED, GameManager.instance.potionEffectListener);
            availableGoals.add(swiftnessGoal);

            ItemStack strengthPotion = new ItemStack(Material.POTION, 1);
            PotionMeta strengthPotionItemMeta = (PotionMeta) strengthPotion.getItemMeta();
            strengthPotionItemMeta.setBasePotionType(PotionType.STRENGTH);
            strengthPotion.setItemMeta(strengthPotionItemMeta);
            PotionEffectGoal strengthGoal = new PotionEffectGoal("Get the Strength Effect", strengthPotion, PotionEffectType.STRENGTH, GameManager.instance.potionEffectListener);
            availableGoals.add(strengthGoal);

            ItemStack netherGoldOre = new ItemStack(Material.NETHER_GOLD_ORE, 1);
            BreakBlockTypeGoal netherOreBreakGoal = new BreakBlockTypeGoal("Break 50 Nether Ore Blocks", netherGoldOre, BreakBlockTypeListener.BlockType.NETHER, 50, GameManager.instance.blockTypeListener);
            availableGoals.add(netherOreBreakGoal);

            ItemStack netheriteScrap = new ItemStack(Material.NETHERITE_SCRAP, 1);
            CollectItemGoal netheriteScrapGoal = new CollectItemGoal("Collect a Netherite Scrap", netheriteScrap);
            availableGoals.add(netheriteScrapGoal);

            ItemStack comparator = new ItemStack(Material.COMPARATOR, 1);
            CollectItemGoal comparatorGoal = new CollectItemGoal("Craft a Comparator", comparator);
            availableGoals.add(comparatorGoal);

            ItemStack soulLantern = new ItemStack(Material.SOUL_LANTERN, 1);
            CollectItemGoal soulLanternGoal = new CollectItemGoal("Craft a Soul Lantern", soulLantern);
            availableGoals.add(soulLanternGoal);

            ItemStack copperBulb = new ItemStack(Material.COPPER_BULB, 1);
            CollectItemGoal copperBulbGoal = new CollectItemGoal("Collect a Copper Bulb", copperBulb);
            availableGoals.add(copperBulbGoal);

            ItemStack respawnAnchor = new ItemStack(Material.RESPAWN_ANCHOR,1);
            CollectItemGoal respawnAnchorGoal = new CollectItemGoal("Craft a Respawn Anchor", respawnAnchor);
            availableGoals.add(respawnAnchorGoal);

            ItemStack goldIngot = new ItemStack(Material.GOLD_INGOT,1);
            CompleteAdvancementGoal ohshinyGoal = new CompleteAdvancementGoal("Complete the advancement Oh Shiny", goldIngot, Bukkit.getAdvancement(new NamespacedKey("minecraft","nether/distract_piglin")));
            availableGoals.add(ohshinyGoal);

            ItemStack netheriteBoots = new ItemStack(Material.NETHERITE_BOOTS,1);
            CompleteAdvancementGoal hotTouristGoal = new CompleteAdvancementGoal("Complete the advancement Hot Tourist Destinations", netheriteBoots, Bukkit.getAdvancement(new NamespacedKey("minecraft","nether/explore_nether")));
            availableGoals.add(hotTouristGoal);
        } else
        {
            ItemStack endermiteEgg = new ItemStack(Material.ENDERMITE_SPAWN_EGG,1);
            KillEntityGoal endermiteKillGoal = new KillEntityGoal("Kill an Endermite", endermiteEgg, EntityType.ENDERMITE, GameManager.instance.killEntityListener);
            availableGoals.add(endermiteKillGoal);

            ItemStack diaBoots = new ItemStack(Material.DIAMOND_BOOTS,1);
            CompleteAdvancementGoal subspaceGoal = new CompleteAdvancementGoal("Complete the advancement Subspace Bubble", diaBoots, Bukkit.getAdvancement(new NamespacedKey("minecraft","nether/fast_travel")));
            availableGoals.add(subspaceGoal);

            ItemStack eyeOfEnder = new ItemStack(Material.ENDER_EYE,1);
            CompleteAdvancementGoal eyeSpyGoal = new CompleteAdvancementGoal("Complete the advancement Eye Spy", eyeOfEnder, Bukkit.getAdvancement(new NamespacedKey("minecraft","story/follow_ender_eye")));
            availableGoals.add(eyeSpyGoal);
        }

        /* DISABLED - villages too strong

        ItemStack bell = new ItemStack(Material.BELL, 1);
        CollectItemGoal bellGoal = new CollectItemGoal("Collect a Bell", bell);
        availableGoals.add(bellGoal);

        ItemStack rabbitStew = new ItemStack(Material.RABBIT_STEW, 1);
        EatGoal rabbitStewGoal = new EatGoal("Eat some Rabbit Stew", rabbitStew, GameManager.instance.eatListener);
        availableGoals.add(rabbitStewGoal);

        ItemStack rawCod = new ItemStack(Material.COD,1);
        BreedEntityGoal catBreedGoal = new BreedEntityGoal("Breed two Cats", rawCod, EntityType.CAT, GameManager.instance.breedEntityListener);
        availableGoals.add(catBreedGoal);

        ItemStack chainmailArmor = new ItemStack(Material.CHAINMAIL_HELMET, 1);
        List<Material> chainmailArmors = new ArrayList<>();
        chainmailArmors.add(Material.CHAINMAIL_HELMET);
        chainmailArmors.add(Material.CHAINMAIL_CHESTPLATE);
        chainmailArmors.add(Material.CHAINMAIL_LEGGINGS);
        chainmailArmors.add(Material.CHAINMAIL_BOOTS);
        CollectItemsGoal chainmailArmorGoal = new CollectItemsGoal("Collect any piece of Chainmail Armor", chainmailArmor, chainmailArmors);
        availableGoals.add(chainmailArmorGoal);

        */

        /*  Broken Goals
           ItemStack minecart = new ItemStack(Material.MINECART,1);
        TravelGoal cart250Goal = new TravelGoal("Use a Minecart to travel 250 Blocks", minecart, 250.0, TravelListener.TYPE.MINECART, GameManager.instance.travelListener);
        availableGoals.add(cart250Goal);

        TravelGoal cart500Goal = new TravelGoal("Use a Minecart to travel 500 Blocks", minecart, 500.0, TravelListener.TYPE.MINECART, GameManager.instance.travelListener);
        availableGoals.add(cart500Goal);

        ItemStack stalac = new ItemStack(Material.POINTED_DRIPSTONE,1);
        KillEntityWithCauseGoal killZombieWithFallingGoal = new KillEntityWithCauseGoal("Kill a Zombie with a Falling Block",stalac, EntityType.ZOMBIE, EntityDamageEvent.DamageCause.FALLING_BLOCK, GameManager.instance.killEntityListener);
        availableGoals.add(killZombieWithFallingGoal);

        ItemStack berries = new ItemStack(Material.SWEET_BERRIES, 64);
        List<Material> berriesList = new ArrayList<>();
        mudBrickList.add(Material.SWEET_BERRIES);
        CollectItemsAmountGoal berryGoal = new CollectItemsAmountGoal("Collect 64 Sweet Berries", berries, berriesList, 64);
        biomeGoals.put(Biome.TAIGA,berryGoal);

        ItemStack snowballs = new ItemStack(Material.SNOWBALL, 64);
        List<Material> snowList = new ArrayList<>();
        mudBrickList.add(Material.SNOWBALL);
        CollectItemsAmountGoal snowballGoal = new CollectItemsAmountGoal("Collect 64 Snowballs", snowballs, snowList, 64);
        biomeGoals.put(Biome.SNOWY_TAIGA,snowballGoal);

         */

        /* Bad Goals

        List<Material> torches = new ArrayList<>();
        torches.add(Material.TORCH);
        torches.add(Material.SOUL_TORCH);
        torches.add(Material.REDSTONE_TORCH);
        CollectItemsAmountGoal torchesGoal = new CollectItemsAmountGoal("Collect 128 Torches of Any Type", torches, 128);
        availableGoals.add(torchesGoal);

        List<Material> stairsList = new ArrayList<>();
        stairsList.add(Material.OAK_STAIRS);
        stairsList.add(Material.STONE_STAIRS);
        stairsList.add(Material.COBBLESTONE_STAIRS);
        stairsList.add(Material.BRICK_STAIRS);
        stairsList.add(Material.STONE_BRICK_STAIRS);
        stairsList.add(Material.NETHER_BRICK_STAIRS);
        stairsList.add(Material.SANDSTONE_STAIRS);
        stairsList.add(Material.SPRUCE_STAIRS);
        stairsList.add(Material.BIRCH_STAIRS);
        stairsList.add(Material.JUNGLE_STAIRS);
        stairsList.add(Material.ACACIA_STAIRS);
        stairsList.add(Material.DARK_OAK_STAIRS);
        stairsList.add(Material.MANGROVE_STAIRS);
        stairsList.add(Material.CRIMSON_STAIRS);
        stairsList.add(Material.WARPED_STAIRS);
        stairsList.add(Material.RED_SANDSTONE_STAIRS);
        stairsList.add(Material.PURPUR_STAIRS);
        stairsList.add(Material.PRISMARINE_STAIRS);
        stairsList.add(Material.PRISMARINE_BRICK_STAIRS);
        stairsList.add(Material.DARK_PRISMARINE_STAIRS);
        stairsList.add(Material.POLISHED_GRANITE_STAIRS);
        stairsList.add(Material.SMOOTH_RED_SANDSTONE_STAIRS);
        stairsList.add(Material.MOSSY_STONE_BRICK_STAIRS);
        stairsList.add(Material.POLISHED_DIORITE_STAIRS);
        stairsList.add(Material.MOSSY_COBBLESTONE_STAIRS);
        stairsList.add(Material.END_STONE_BRICK_STAIRS);
        stairsList.add(Material.STONE_STAIRS);
        stairsList.add(Material.SMOOTH_SANDSTONE_STAIRS);
        stairsList.add(Material.SMOOTH_QUARTZ_STAIRS);
        stairsList.add(Material.GRANITE_STAIRS);
        stairsList.add(Material.ANDESITE_STAIRS);
        stairsList.add(Material.RED_NETHER_BRICK_STAIRS);
        stairsList.add(Material.POLISHED_ANDESITE_STAIRS);
        stairsList.add(Material.DIORITE_STAIRS);
        stairsList.add(Material.COBBLED_DEEPSLATE_STAIRS);
        stairsList.add(Material.POLISHED_DEEPSLATE_STAIRS);
        stairsList.add(Material.DEEPSLATE_BRICK_STAIRS);
        stairsList.add(Material.DEEPSLATE_TILE_STAIRS);
        stairsList.add(Material.PALE_OAK_STAIRS);
        CollectItemsAmountGoal stairsGoal = new CollectItemsAmountGoal("Collect 128 Stairs of Any Type", stairsList, 128);
        availableGoals.add(stairsGoal);

        List<Material> pressurePlates = new ArrayList<>();
        pressurePlates.add(Material.OAK_PRESSURE_PLATE);
        pressurePlates.add(Material.SPRUCE_PRESSURE_PLATE);
        pressurePlates.add(Material.BIRCH_PRESSURE_PLATE);
        pressurePlates.add(Material.JUNGLE_PRESSURE_PLATE);
        pressurePlates.add(Material.ACACIA_PRESSURE_PLATE);
        pressurePlates.add(Material.DARK_OAK_PRESSURE_PLATE);
        pressurePlates.add(Material.CRIMSON_PRESSURE_PLATE);
        pressurePlates.add(Material.WARPED_PRESSURE_PLATE);
        pressurePlates.add(Material.STONE_PRESSURE_PLATE);
        pressurePlates.add(Material.LIGHT_WEIGHTED_PRESSURE_PLATE);
        pressurePlates.add(Material.HEAVY_WEIGHTED_PRESSURE_PLATE);
        pressurePlates.add(Material.MANGROVE_PRESSURE_PLATE);
        pressurePlates.add(Material.CHERRY_PRESSURE_PLATE);
        pressurePlates.add(Material.PALE_OAK_PRESSURE_PLATE);
        CollectItemSetAmountGoal pressurePlateGoal = new CollectItemSetAmountGoal("Collect 5 Unique Types of Pressure Plates", pressurePlates, 5);
        availableGoals.add(pressurePlateGoal);

        List<Material> gravityBlocks = new ArrayList<>();
        gravityBlocks.add(Material.SAND);
        gravityBlocks.add(Material.RED_SAND);
        gravityBlocks.add(Material.POINTED_DRIPSTONE);
        gravityBlocks.add(Material.GRAVEL);
        gravityBlocks.add(Material.ANVIL);
        CollectItemSetAmountGoal gravityGoal = new CollectItemSetAmountGoal("Collect 3 Unique Types of Gravity-Affected Blocks", gravityBlocks, 3);
        availableGoals.add(gravityGoal);

        */

        ItemStack targetblock = new ItemStack(Material.TARGET, 1);
        CompleteAdvancementGoal bullseyeGoal = new CompleteAdvancementGoal("Complete the advancement Bullseye", targetblock, Bukkit.getAdvancement(new NamespacedKey("minecraft", "adventure/bullseye")));
        availableGoals.add(bullseyeGoal);

        ItemStack glowInkSac = new ItemStack(Material.GLOW_INK_SAC, 1);
        CompleteAdvancementGoal glowAndBeholdGoal = new CompleteAdvancementGoal("Complete the advancement Glow and Behold!", glowInkSac, Bukkit.getAdvancement(new NamespacedKey("minecraft", "husbandry/make_a_sign_glow")));
        availableGoals.add(glowAndBeholdGoal);

        ItemStack armorStandItem = new ItemStack(Material.ARMOR_STAND, 1);
        ArmorStandInteractGoal fillArmorStandGoal = new ArmorStandInteractGoal("Fill all slots of an Armor Stand", armorStandItem, EntityType.ARMOR_STAND, GameManager.instance.armorStandInteractListener);
        availableGoals.add(fillArmorStandGoal);

        ItemStack composterItem = new ItemStack(Material.COMPOSTER, 1);
        BlockInteractGoal fillComposterGoal = new BlockInteractGoal("Completely fill a Composter", composterItem, Material.COMPOSTER, GameManager.instance.blockInteractListener);
        availableGoals.add(fillComposterGoal);

        ItemStack chiseledBookshelf = new ItemStack(Material.CHISELED_BOOKSHELF, 1);
        BlockInteractGoal fillchiseledBookshelfGoal = new BlockInteractGoal("Completely fill a Chiseled Bookshelf", chiseledBookshelf, Material.CHISELED_BOOKSHELF, GameManager.instance.blockInteractListener);
        availableGoals.add(fillchiseledBookshelfGoal);

        ItemStack tnt = new ItemStack(Material.TNT,1);
        CollectItemGoal tntGoal = new CollectItemGoal("Craft a Block of TNT", tnt);
        availableGoals.add(tntGoal);

        ItemStack lightningRod = new ItemStack(Material.LIGHTNING_ROD,1);
        CollectItemGoal lightningRodGoal = new CollectItemGoal("Craft a Lightning Rod", lightningRod);
        availableGoals.add(lightningRodGoal);

        List<Material> flowers = new ArrayList<>();
        flowers.add(Material.DANDELION);
        flowers.add(Material.POPPY);
        flowers.add(Material.BLUE_ORCHID);
        flowers.add(Material.ALLIUM);
        flowers.add(Material.AZURE_BLUET);
        flowers.add(Material.RED_TULIP);
        flowers.add(Material.ORANGE_TULIP);
        flowers.add(Material.WHITE_TULIP);
        flowers.add(Material.PINK_TULIP);
        flowers.add(Material.OXEYE_DAISY);
        flowers.add(Material.CORNFLOWER);
        flowers.add(Material.LILY_OF_THE_VALLEY);
        flowers.add(Material.WITHER_ROSE);
        flowers.add(Material.TORCHFLOWER);
        flowers.add(Material.SUNFLOWER);
        flowers.add(Material.LILAC);
        flowers.add(Material.ROSE_BUSH);
        flowers.add(Material.PEONY);
        flowers.add(Material.CLOSED_EYEBLOSSOM);
        flowers.add(Material.OPEN_EYEBLOSSOM);
        flowers.add(Material.WILDFLOWERS);
        CollectItemsAmountGoal flowersGoal = new CollectItemsAmountGoal("Collect 64 Flowers of Any Type", flowers,64);
        availableGoals.add(flowersGoal);

        List<Material> leaves = new ArrayList<>();
        leaves.add(Material.OAK_LEAVES);
        leaves.add(Material.SPRUCE_LEAVES);
        leaves.add(Material.BIRCH_LEAVES);
        leaves.add(Material.JUNGLE_LEAVES);
        leaves.add(Material.ACACIA_LEAVES);
        leaves.add(Material.DARK_OAK_LEAVES);
        leaves.add(Material.MANGROVE_LEAVES);
        leaves.add(Material.CHERRY_LEAVES);
        leaves.add(Material.AZALEA_LEAVES);
        leaves.add(Material.FLOWERING_AZALEA_LEAVES);
        CollectItemsAmountGoal leavesGoal = new CollectItemsAmountGoal("Collect 64 Leaves of Any Type", leaves, 64);
        availableGoals.add(leavesGoal);

        List<Material> mobDrops = new ArrayList<>();
        mobDrops.add(Material.ROTTEN_FLESH);
        mobDrops.add(Material.BONE);
        mobDrops.add(Material.STRING);
        mobDrops.add(Material.SPIDER_EYE);
        mobDrops.add(Material.GUNPOWDER);
        mobDrops.add(Material.ENDER_PEARL);
        mobDrops.add(Material.BLAZE_ROD);
        mobDrops.add(Material.GHAST_TEAR);
        mobDrops.add(Material.SLIME_BALL);
        mobDrops.add(Material.MAGMA_CREAM);
        mobDrops.add(Material.PHANTOM_MEMBRANE);
        mobDrops.add(Material.SHULKER_SHELL);
        mobDrops.add(Material.PRISMARINE_SHARD);
        mobDrops.add(Material.WITHER_SKELETON_SKULL);
        CollectItemsAmountGoal mobDropsGoal = new CollectItemsAmountGoal("Collect 32 Hostile Mob Drops of Any Type", mobDrops,32);
        availableGoals.add(mobDropsGoal);

        List<Material> rawBlocks = new ArrayList<>();
        rawBlocks.add(Material.RAW_IRON_BLOCK);
        rawBlocks.add(Material.RAW_COPPER_BLOCK);
        rawBlocks.add(Material.RAW_GOLD_BLOCK);
        CollectItemsAmountGoal rawBlocksGoal = new CollectItemsAmountGoal("Collect 16 Blocks of Any Raw Ore", rawBlocks, 16);
        availableGoals.add(rawBlocksGoal);

        List<Material> foodItems = new ArrayList<>();
        foodItems.add(Material.APPLE);
        foodItems.add(Material.GOLDEN_APPLE);
        foodItems.add(Material.ENCHANTED_GOLDEN_APPLE);
        foodItems.add(Material.MELON_SLICE);
        foodItems.add(Material.GLISTERING_MELON_SLICE);
        foodItems.add(Material.CARROT);
        foodItems.add(Material.GOLDEN_CARROT);
        foodItems.add(Material.POTATO);
        foodItems.add(Material.BAKED_POTATO);
        foodItems.add(Material.POISONOUS_POTATO);
        foodItems.add(Material.BEETROOT);
        foodItems.add(Material.BEETROOT_SOUP);
        foodItems.add(Material.BREAD);
        foodItems.add(Material.PUMPKIN_PIE);
        foodItems.add(Material.COOKIE);
        foodItems.add(Material.CAKE);
        foodItems.add(Material.MUSHROOM_STEW);
        foodItems.add(Material.RABBIT_STEW);
        foodItems.add(Material.SUSPICIOUS_STEW);
        foodItems.add(Material.DRIED_KELP);
        foodItems.add(Material.COD);
        foodItems.add(Material.SALMON);
        foodItems.add(Material.TROPICAL_FISH);
        foodItems.add(Material.PUFFERFISH);
        foodItems.add(Material.COOKED_COD);
        foodItems.add(Material.COOKED_SALMON);
        foodItems.add(Material.ROTTEN_FLESH);
        foodItems.add(Material.SPIDER_EYE);
        foodItems.add(Material.COOKED_BEEF);
        foodItems.add(Material.COOKED_CHICKEN);
        foodItems.add(Material.COOKED_MUTTON);
        foodItems.add(Material.COOKED_PORKCHOP);
        foodItems.add(Material.COOKED_RABBIT);
        foodItems.add(Material.RABBIT);
        foodItems.add(Material.CHICKEN);
        foodItems.add(Material.MUTTON);
        foodItems.add(Material.PORKCHOP);
        foodItems.add(Material.BEEF);
        foodItems.add(Material.DRIED_KELP);
        foodItems.add(Material.HONEY_BOTTLE);
        foodItems.add(Material.SWEET_BERRIES);
        foodItems.add(Material.GLOW_BERRIES);
        foodItems.add(Material.SUSPICIOUS_STEW);
        foodItems.add(Material.CHORUS_FRUIT);
        CollectItemsAmountGoal foodGoal = new CollectItemsAmountGoal("Collect 32 Food of Any Type", foodItems, 32);
        availableGoals.add(foodGoal);

        ItemStack fireworkRocket = new ItemStack(Material.FIREWORK_ROCKET, 16);
        CollectItemsAmountGoal fireworksGoal = new CollectItemsAmountGoal("Collect 16 Fireworks", fireworkRocket);
        availableGoals.add(fireworksGoal);

        ItemStack spectralArrows = new ItemStack(Material.SPECTRAL_ARROW, 32);
        CollectItemsAmountGoal spectralArrowsGoal = new CollectItemsAmountGoal("Collect 32 Spectral Arrows", spectralArrows);
        availableGoals.add(spectralArrowsGoal);

        ItemStack enderPearls = new ItemStack(Material.ENDER_PEARL, 16);
        CollectItemsAmountGoal enderPearlsGoal = new CollectItemsAmountGoal("Collect 16 Ender Pearls", enderPearls);
        availableGoals.add(enderPearlsGoal);

        List<Material> netherHyphaeMaterials = new ArrayList<>();
        netherHyphaeMaterials.add(Material.CRIMSON_HYPHAE);
        netherHyphaeMaterials.add(Material.WARPED_HYPHAE);
        CollectItemsAmountGoal netherHyphaeGoal = new CollectItemsAmountGoal("Collect 32 Nether Hyphae", netherHyphaeMaterials, 32);
        availableGoals.add(netherHyphaeGoal);

        List<Material> strippedLogsMaterials = new ArrayList<>();
        strippedLogsMaterials.add(Material.STRIPPED_OAK_LOG);
        strippedLogsMaterials.add(Material.STRIPPED_SPRUCE_LOG);
        strippedLogsMaterials.add(Material.STRIPPED_BIRCH_LOG);
        strippedLogsMaterials.add(Material.STRIPPED_JUNGLE_LOG);
        strippedLogsMaterials.add(Material.STRIPPED_ACACIA_LOG);
        strippedLogsMaterials.add(Material.STRIPPED_DARK_OAK_LOG);
        strippedLogsMaterials.add(Material.STRIPPED_MANGROVE_LOG);
        strippedLogsMaterials.add(Material.STRIPPED_CHERRY_LOG);
        CollectItemsAmountGoal strippedLogsGoal = new CollectItemsAmountGoal("Collect 64 Stripped Logs of Any Variant", strippedLogsMaterials, 64);
        availableGoals.add(strippedLogsGoal);

        ItemStack chiseledStoneBricks = new ItemStack(Material.CHISELED_STONE_BRICKS, 16);
        CollectItemsAmountGoal stoneBricksGoal = new CollectItemsAmountGoal("Collect 16 Chiseled Stone Bricks", chiseledStoneBricks);
        availableGoals.add(stoneBricksGoal);

        List<Material> walls = new ArrayList<>();
        walls.add(Material.COBBLESTONE_WALL);
        walls.add(Material.MOSSY_COBBLESTONE_WALL);
        walls.add(Material.BRICK_WALL);
        walls.add(Material.PRISMARINE_WALL);
        walls.add(Material.RED_SANDSTONE_WALL);
        walls.add(Material.MOSSY_STONE_BRICK_WALL);
        walls.add(Material.GRANITE_WALL);
        walls.add(Material.STONE_BRICK_WALL);
        walls.add(Material.NETHER_BRICK_WALL);
        walls.add(Material.ANDESITE_WALL);
        walls.add(Material.RED_NETHER_BRICK_WALL);
        walls.add(Material.SANDSTONE_WALL);
        walls.add(Material.END_STONE_BRICK_WALL);
        walls.add(Material.DIORITE_WALL);
        walls.add(Material.BLACKSTONE_WALL);
        walls.add(Material.POLISHED_BLACKSTONE_WALL);
        walls.add(Material.POLISHED_BLACKSTONE_BRICK_WALL);
        walls.add(Material.COBBLED_DEEPSLATE_WALL);
        walls.add(Material.POLISHED_DEEPSLATE_WALL);
        walls.add(Material.DEEPSLATE_BRICK_WALL);
        walls.add(Material.DEEPSLATE_TILE_WALL);
        walls.add(Material.MUD_BRICK_WALL);
        walls.add(Material.TUFF_WALL);
        walls.add(Material.POLISHED_TUFF_WALL);
        CollectItemsAmountGoal wallsGoal = new CollectItemsAmountGoal("Collect 64 Walls of Any Type", walls, 64);
        availableGoals.add(wallsGoal);

        List<Material> concreteBlocks = new ArrayList<>();
        concreteBlocks.add(Material.WHITE_CONCRETE);
        concreteBlocks.add(Material.ORANGE_CONCRETE);
        concreteBlocks.add(Material.MAGENTA_CONCRETE);
        concreteBlocks.add(Material.LIGHT_BLUE_CONCRETE);
        concreteBlocks.add(Material.YELLOW_CONCRETE);
        concreteBlocks.add(Material.LIME_CONCRETE);
        concreteBlocks.add(Material.PINK_CONCRETE);
        concreteBlocks.add(Material.GRAY_CONCRETE);
        concreteBlocks.add(Material.LIGHT_GRAY_CONCRETE);
        concreteBlocks.add(Material.CYAN_CONCRETE);
        concreteBlocks.add(Material.PURPLE_CONCRETE);
        concreteBlocks.add(Material.BLUE_CONCRETE);
        concreteBlocks.add(Material.BROWN_CONCRETE);
        concreteBlocks.add(Material.GREEN_CONCRETE);
        concreteBlocks.add(Material.RED_CONCRETE);
        concreteBlocks.add(Material.BLACK_CONCRETE);
        CollectItemsAmountGoal concreteGoal = new CollectItemsAmountGoal("Collect 64 Concrete of Any Colour", concreteBlocks, 64);
        availableGoals.add(concreteGoal);

        List<Material> glassBlocks = new ArrayList<>();
        glassBlocks.add(Material.GLASS);
        glassBlocks.add(Material.WHITE_STAINED_GLASS);
        glassBlocks.add(Material.ORANGE_STAINED_GLASS);
        glassBlocks.add(Material.MAGENTA_STAINED_GLASS);
        glassBlocks.add(Material.LIGHT_BLUE_STAINED_GLASS);
        glassBlocks.add(Material.YELLOW_STAINED_GLASS);
        glassBlocks.add(Material.LIME_STAINED_GLASS);
        glassBlocks.add(Material.PINK_STAINED_GLASS);
        glassBlocks.add(Material.GRAY_STAINED_GLASS);
        glassBlocks.add(Material.LIGHT_GRAY_STAINED_GLASS);
        glassBlocks.add(Material.CYAN_STAINED_GLASS);
        glassBlocks.add(Material.PURPLE_STAINED_GLASS);
        glassBlocks.add(Material.BLUE_STAINED_GLASS);
        glassBlocks.add(Material.BROWN_STAINED_GLASS);
        glassBlocks.add(Material.GREEN_STAINED_GLASS);
        glassBlocks.add(Material.RED_STAINED_GLASS);
        glassBlocks.add(Material.BLACK_STAINED_GLASS);
        glassBlocks.add(Material.TINTED_GLASS);
        CollectItemsAmountGoal glassGoal = new CollectItemsAmountGoal("Collect 64 of Any Glass Blocks", glassBlocks, 64);
        availableGoals.add(glassGoal);



        List<Material> netherBlocks = new ArrayList<>();
        netherBlocks.add(Material.NETHERRACK);
        netherBlocks.add(Material.NETHER_BRICKS);
        netherBlocks.add(Material.RED_NETHER_BRICKS);
        netherBlocks.add(Material.NETHER_WART_BLOCK);
        netherBlocks.add(Material.CHISELED_NETHER_BRICKS);
        netherBlocks.add(Material.CRACKED_NETHER_BRICKS);
        netherBlocks.add(Material.QUARTZ_BLOCK);
        netherBlocks.add(Material.GLOWSTONE);
        netherBlocks.add(Material.SOUL_SAND);
        netherBlocks.add(Material.SOUL_SOIL);
        netherBlocks.add(Material.BASALT);
        netherBlocks.add(Material.POLISHED_BASALT);
        netherBlocks.add(Material.BLACKSTONE);
        netherBlocks.add(Material.POLISHED_BLACKSTONE);
        netherBlocks.add(Material.POLISHED_BLACKSTONE_BRICKS);
        netherBlocks.add(Material.CRACKED_POLISHED_BLACKSTONE_BRICKS);
        netherBlocks.add(Material.CHISELED_POLISHED_BLACKSTONE);
        CollectItemsAmountGoal netherBlocksGoal = new CollectItemsAmountGoal("Collect 64 Nether Blocks of Any Type", netherBlocks, 64);
        availableGoals.add(netherBlocksGoal);

        List<Material> saplings = new ArrayList<>();
        saplings.add(Material.OAK_SAPLING);
        saplings.add(Material.SPRUCE_SAPLING);
        saplings.add(Material.BIRCH_SAPLING);
        saplings.add(Material.JUNGLE_SAPLING);
        saplings.add(Material.ACACIA_SAPLING);
        saplings.add(Material.DARK_OAK_SAPLING);
        saplings.add(Material.MANGROVE_PROPAGULE);
        saplings.add(Material.CHERRY_SAPLING);
        saplings.add(Material.BAMBOO);
        saplings.add(Material.PALE_OAK_SAPLING);
        CollectItemsAmountGoal saplingsGoal = new CollectItemsAmountGoal("Collect 32 Saplings of Any Type", saplings, 32);
        availableGoals.add(saplingsGoal);

        ItemStack tintedGlass = new ItemStack(Material.TINTED_GLASS,1);
        CollectItemGoal tintedGlassGoal = new CollectItemGoal("Collect a Block of Tinted Glass",tintedGlass);
        availableGoals.add(tintedGlassGoal);

        ItemStack endCrystal = new ItemStack(Material.END_CRYSTAL, 1);
        CollectItemGoal endCrystalGoal = new CollectItemGoal("Collect an End Crystal", endCrystal);
        availableGoals.add(endCrystalGoal);

        ItemStack tuffBricks = new ItemStack(Material.TUFF, 16);
        CollectItemsAmountGoal tuffBricksGoal = new CollectItemsAmountGoal("Collect 16 Tuff Bricks", tuffBricks);
        availableGoals.add(tuffBricksGoal);

        ItemStack seagrass = new ItemStack(Material.SEAGRASS, 64);
        CollectItemsAmountGoal seagrassGoal = new CollectItemsAmountGoal("Collect 64 Seagrass", seagrass);
        availableGoals.add(seagrassGoal);

        ItemStack sugarcane = new ItemStack(Material.SUGAR_CANE, 32);
        CollectItemsAmountGoal sugarcaneGoal = new CollectItemsAmountGoal("Collect 32 Sugar Cane", sugarcane);
        availableGoals.add(sugarcaneGoal);

        ItemStack goldBoots = new ItemStack(Material.GOLDEN_BOOTS,1);
        TravelGoal run4000Goal = new TravelGoal("Run 4000 Blocks", goldBoots, 4000.0, TravelListener.TYPE.RUNNING, GameManager.instance.travelListener);
        availableGoals.add(run4000Goal);

        TravelGoal run2000Goal = new TravelGoal("Run 2000 Blocks", goldBoots, 2000.0, TravelListener.TYPE.RUNNING, GameManager.instance.travelListener);
        availableGoals.add(run2000Goal);

        TravelGoal run3000Goal = new TravelGoal("Run 3000 Blocks", goldBoots, 3000.0, TravelListener.TYPE.RUNNING, GameManager.instance.travelListener);
        availableGoals.add(run3000Goal);

        ItemStack boat = new ItemStack(Material.OAK_BOAT,1);
        TravelGoal boat500Goal = new TravelGoal("Use a Boat to travel 500 Blocks", boat, 500.0, TravelListener.TYPE.BOAT, GameManager.instance.travelListener);
        availableGoals.add(boat500Goal);

        TravelGoal boat2000Goal = new TravelGoal("Use a Boat to travel 2000 Blocks", boat, 2000.0, TravelListener.TYPE.BOAT, GameManager.instance.travelListener);
        availableGoals.add(boat2000Goal);

        TravelGoal boat1500Goal = new TravelGoal("Use a Boat to travel 1500 Blocks", boat, 1500.0, TravelListener.TYPE.BOAT, GameManager.instance.travelListener);
        availableGoals.add(boat1500Goal);

        ItemStack saddle = new ItemStack(Material.SADDLE,1);
        TravelGoal pig50Goal = new TravelGoal("Use a Pig to travel 50 Blocks",saddle,50.0,TravelListener.TYPE.PIG,GameManager.instance.travelListener);
        availableGoals.add(pig50Goal);

        TravelGoal pig100Goal = new TravelGoal("Use a Pig to travel 100 Blocks",saddle,100.0,TravelListener.TYPE.PIG,GameManager.instance.travelListener);
        availableGoals.add(pig100Goal);

        ItemStack striderSaddle = new ItemStack(Material.WARPED_FUNGUS_ON_A_STICK, 1);

        TravelGoal horse250Goal = new TravelGoal("Use a Horse to travel 250 Blocks", saddle, 250.0, TravelListener.TYPE.HORSE, GameManager.instance.travelListener);
        availableGoals.add(horse250Goal);

        TravelGoal horse500Goal = new TravelGoal("Use a Horse to travel 500 Blocks", saddle, 500.0, TravelListener.TYPE.HORSE, GameManager.instance.travelListener);
        availableGoals.add(horse500Goal);

        TravelGoal strider100Goal = new TravelGoal("Use a Strider to travel 50 Blocks", striderSaddle, 100.0, TravelListener.TYPE.STRIDER, GameManager.instance.travelListener);
        availableGoals.add(strider100Goal);

        TravelGoal strider200Goal = new TravelGoal("Use a Strider to travel 100 Blocks", striderSaddle, 200.0, TravelListener.TYPE.STRIDER, GameManager.instance.travelListener);
        availableGoals.add(strider200Goal);

        ItemStack gunpowder = new ItemStack(Material.GUNPOWDER,1);
        KillEntityWithCauseGoal killCreeperWithTntGoal = new KillEntityWithCauseGoal("Kill a Creeper with a TNT Block",gunpowder, EntityType.CREEPER, EntityDamageEvent.DamageCause.BLOCK_EXPLOSION, GameManager.instance.killEntityListener);
        availableGoals.add(killCreeperWithTntGoal);

        ItemStack bow = new ItemStack(Material.BOW,1);
        KillEntityWithCauseGoal killSkeletonwithProjGoal = new KillEntityWithCauseGoal("Kill a Skeleton with a Projectile",bow, EntityType.SKELETON, EntityDamageEvent.DamageCause.PROJECTILE, GameManager.instance.killEntityListener);
        availableGoals.add(killSkeletonwithProjGoal);

        ItemStack spEye = new ItemStack(Material.SPIDER_EYE,1);
        KillEntityWithCauseGoal killSpiderFallGoal = new KillEntityWithCauseGoal("Kill a Spider with Fall Damage",spEye, EntityType.SPIDER, EntityDamageEvent.DamageCause.FALL, GameManager.instance.killEntityListener);
        availableGoals.add(killSpiderFallGoal);

        ItemStack batSpawnEgg = new ItemStack(Material.BAT_SPAWN_EGG,1);
        KillEntityGoal batKillGoal = new KillEntityGoal("Kill a Bat", batSpawnEgg, EntityType.BAT,GameManager.instance.killEntityListener);
        availableGoals.add(batKillGoal);

        ItemStack ferEye = new ItemStack(Material.FERMENTED_SPIDER_EYE,1);
        CollectItemGoal ferEyeGoal = new CollectItemGoal("Craft a Fermented Spider Eye",ferEye);
        availableGoals.add(ferEyeGoal);

        ItemStack lecternItem = new ItemStack(Material.LECTERN, 1);
        BlockInteractGoal placeBookOnLecternGoal = new BlockInteractGoal("Place a Book on a Lectern", lecternItem, Material.LECTERN, GameManager.instance.blockInteractListener);
        availableGoals.add(placeBookOnLecternGoal);

        List<Material> fishTypes = new ArrayList<>();
        fishTypes.add(Material.COD);
        fishTypes.add(Material.SALMON);
        fishTypes.add(Material.TROPICAL_FISH);
        fishTypes.add(Material.PUFFERFISH);
        CollectItemsAmountGoal fishGoal = new CollectItemsAmountGoal("Collect 32 Fish of Any Type", fishTypes, 32);
        availableGoals.add(fishGoal);

        List<Material> railTypes = new ArrayList<>();
        railTypes.add(Material.RAIL);
        railTypes.add(Material.POWERED_RAIL);
        railTypes.add(Material.DETECTOR_RAIL);
        railTypes.add(Material.ACTIVATOR_RAIL);
        CollectItemsAmountGoal railGoal = new CollectItemsAmountGoal("Collect 64 Rails of Any Type", railTypes, 64);
        availableGoals.add(railGoal);

        ItemStack mudBricks = new ItemStack(Material.MUD_BRICKS, 4);
        CollectItemsAmountGoal mudBricksGoal = new CollectItemsAmountGoal("Collect 4 Mud Bricks", mudBricks);
        availableGoals.add(mudBricksGoal);

        ItemStack encBook = new ItemStack(Material.ENCHANTED_BOOK,1);
        CollectItemGoal encbookGoal = new CollectItemGoal("Collect Any Enchanted Book", encBook);
        availableGoals.add(encbookGoal);

        ItemStack waterBucket = new ItemStack(Material.WATER_BUCKET,1);
        DeathGoal drownGoal = new DeathGoal("Achieve the Death Message '<player> drowned'", waterBucket,"drowned",GameManager.instance.deathListener);
        availableGoals.add(drownGoal);

        ItemStack tntMinecart = new ItemStack(Material.TNT_MINECART,1);
        DeathGoal tntCartGoal = new DeathGoal("Achieve the Death Message '<player> blew up'", tntMinecart, "blew up",GameManager.instance.deathListener);
        availableGoals.add(tntCartGoal);

        ItemStack bed = new ItemStack(Material.RED_BED,1);
        DeathGoal netherBedGoal = new DeathGoal("Achieve the Death Message '<player> was killed by [Intentional Game Design]'", bed, "was killed by [Intentional Game Design]",GameManager.instance.deathListener);
        availableGoals.add(netherBedGoal);

        ItemStack ladder = new ItemStack(Material.LADDER,1);
        DeathGoal ladderFallGoal = new DeathGoal("Achieve the Death Message '<player> fell off a ladder'",ladder,"fell off a ladder",GameManager.instance.deathListener);
        availableGoals.add(ladderFallGoal);

        ItemStack flintAndSteel = new ItemStack(Material.FLINT_AND_STEEL,1);
        DeathGoal flamesGoal = new DeathGoal("Achieve the Death Message '<player> went up in flames'",flintAndSteel,"went up in flames",GameManager.instance.deathListener);
        availableGoals.add(flamesGoal);

        ItemStack magmaBlock = new ItemStack(Material.MAGMA_BLOCK,1);
        DeathGoal floorLavaGoal = new DeathGoal("Achieve the Death Message '<player> discovered the floor was lava'",magmaBlock,"discovered the floor was lava",GameManager.instance.deathListener);
        availableGoals.add(floorLavaGoal);

        ItemStack gravel = new ItemStack(Material.GRAVEL,1);
        DeathGoal suffocationGoal = new DeathGoal("Achieve the Death Message '<player> suffocated in a wall'",gravel,"suffocated in a wall",GameManager.instance.deathListener);
        availableGoals.add(suffocationGoal);

        ItemStack trident = new ItemStack(Material.TRIDENT,1);
        DeathGoal tridentDeathGoal = new DeathGoal("Achieve the Death Message '<player> was impaled by Drowned'",trident,"was impaled by Drowned",GameManager.instance.deathListener);
        availableGoals.add(tridentDeathGoal);

        ItemStack nameTag = new ItemStack(Material.NAME_TAG, 1);
        CollectItemGoal nameTagGoal = new CollectItemGoal("Collect a Name Tag", nameTag);
        availableGoals.add(nameTagGoal);

        List<Material> diamondArmour = new ArrayList<>();
        diamondArmour.add(Material.DIAMOND_HELMET);
        diamondArmour.add(Material.DIAMOND_CHESTPLATE);
        diamondArmour.add(Material.DIAMOND_LEGGINGS);
        diamondArmour.add(Material.DIAMOND_BOOTS);
        CollectItemSetGoal diamondArmourGoal = new CollectItemSetGoal("Collect a Full Set of Diamond Armour", diamondArmour);
        availableGoals.add(diamondArmourGoal);

        List<Material> goldenTools = new ArrayList<>();
        goldenTools.add(Material.GOLDEN_PICKAXE);
        goldenTools.add(Material.GOLDEN_AXE);
        goldenTools.add(Material.GOLDEN_SHOVEL);
        goldenTools.add(Material.GOLDEN_HOE);
        CollectItemSetGoal goldenToolsGoal = new CollectItemSetGoal("Collect a Full Set of Golden Tools", goldenTools);
        availableGoals.add(goldenToolsGoal);

        List<Material> goldenArmour = new ArrayList<>();
        goldenArmour.add(Material.GOLDEN_HELMET);
        goldenArmour.add(Material.GOLDEN_CHESTPLATE);
        goldenArmour.add(Material.GOLDEN_LEGGINGS);
        goldenArmour.add(Material.GOLDEN_BOOTS);
        CollectItemSetGoal goldenArmourGoal = new CollectItemSetGoal("Collect a Full Set of Golden Armour", goldenArmour);
        availableGoals.add(goldenArmourGoal);

        List<Material> diamondTools = new ArrayList<>();
        diamondTools.add(Material.DIAMOND_PICKAXE);
        diamondTools.add(Material.DIAMOND_AXE);
        diamondTools.add(Material.DIAMOND_SHOVEL);
        diamondTools.add(Material.DIAMOND_HOE);
        CollectItemSetGoal diamondToolsGoal = new CollectItemSetGoal("Collect a Full Set of Diamond Tools", diamondTools);
        availableGoals.add(diamondToolsGoal);

        List<Material> allFurnaces = new ArrayList<>();
        allFurnaces.add(Material.FURNACE);
        allFurnaces.add(Material.BLAST_FURNACE);
        allFurnaces.add(Material.SMOKER);
        CollectItemSetGoal furnaceGoal = new CollectItemSetGoal("Collect Every Type of Furnace", allFurnaces);
        availableGoals.add(furnaceGoal);

        List<Material> oakWoodBlocks = new ArrayList<>();
        oakWoodBlocks.add(Material.OAK_LOG);
        oakWoodBlocks.add(Material.OAK_WOOD);
        oakWoodBlocks.add(Material.STRIPPED_OAK_LOG);
        oakWoodBlocks.add(Material.STRIPPED_OAK_WOOD);
        oakWoodBlocks.add(Material.OAK_PLANKS);
        oakWoodBlocks.add(Material.OAK_SLAB);
        oakWoodBlocks.add(Material.OAK_STAIRS);
        oakWoodBlocks.add(Material.OAK_FENCE);
        oakWoodBlocks.add(Material.OAK_FENCE_GATE);
        oakWoodBlocks.add(Material.OAK_DOOR);
        oakWoodBlocks.add(Material.OAK_TRAPDOOR);
        oakWoodBlocks.add(Material.OAK_BUTTON);
        oakWoodBlocks.add(Material.OAK_PRESSURE_PLATE);
        oakWoodBlocks.add(Material.OAK_SIGN);
        oakWoodBlocks.add(Material.OAK_HANGING_SIGN);
        CollectItemSetGoal oakWoodGoal = new CollectItemSetGoal("Collect All Oak Wood Related Blocks", oakWoodBlocks);
        availableGoals.add(oakWoodGoal);

        CollectItemSetAmountGoal flowerGoal = new CollectItemSetAmountGoal("Collect 5 Unique Types of Flowers", flowers, 5);
        availableGoals.add(flowerGoal);

        List<Material> graniteBlocks = new ArrayList<>();
        graniteBlocks.add(Material.GRANITE);
        graniteBlocks.add(Material.POLISHED_GRANITE);
        graniteBlocks.add(Material.GRANITE_SLAB);
        graniteBlocks.add(Material.POLISHED_GRANITE_SLAB);
        graniteBlocks.add(Material.GRANITE_STAIRS);
        graniteBlocks.add(Material.POLISHED_GRANITE_STAIRS);
        graniteBlocks.add(Material.GRANITE_WALL);
        CollectItemSetAmountGoal graniteGoal = new CollectItemSetAmountGoal("Collect 7 Unique Types of Granite Blocks", graniteBlocks, 7);
        availableGoals.add(graniteGoal);

        List<Material> dioriteBlocks = new ArrayList<>();
        dioriteBlocks.add(Material.DIORITE);
        dioriteBlocks.add(Material.POLISHED_DIORITE);
        dioriteBlocks.add(Material.DIORITE_SLAB);
        dioriteBlocks.add(Material.POLISHED_DIORITE_SLAB);
        dioriteBlocks.add(Material.DIORITE_STAIRS);
        dioriteBlocks.add(Material.POLISHED_DIORITE_STAIRS);
        dioriteBlocks.add(Material.DIORITE_WALL);
        CollectItemSetAmountGoal dioriteGoal = new CollectItemSetAmountGoal("Collect 7 Unique Types of Diorite Blocks", dioriteBlocks, 7);
        availableGoals.add(dioriteGoal);

        List<Material> andesiteBlocks = new ArrayList<>();
        andesiteBlocks.add(Material.ANDESITE);
        andesiteBlocks.add(Material.POLISHED_ANDESITE);
        andesiteBlocks.add(Material.ANDESITE_SLAB);
        andesiteBlocks.add(Material.POLISHED_ANDESITE_SLAB);
        andesiteBlocks.add(Material.ANDESITE_STAIRS);
        andesiteBlocks.add(Material.POLISHED_ANDESITE_STAIRS);
        andesiteBlocks.add(Material.ANDESITE_WALL);
        CollectItemSetAmountGoal andesiteGoal = new CollectItemSetAmountGoal("Collect 7 Unique Types of Andesite Blocks", andesiteBlocks, 7);
        availableGoals.add(andesiteGoal);

        ItemStack deepslate = new ItemStack(Material.DEEPSLATE, 64);
        CollectItemsAmountGoal deepslateGoal = new CollectItemsAmountGoal("Collect 64 Deepslate Blocks", deepslate);
        availableGoals.add(deepslateGoal);

        List<Material> sandBlocks = new ArrayList<>();
        sandBlocks.add(Material.SAND);
        sandBlocks.add(Material.SANDSTONE);
        sandBlocks.add(Material.SANDSTONE_SLAB);
        sandBlocks.add(Material.SANDSTONE_STAIRS);
        sandBlocks.add(Material.CHISELED_SANDSTONE);
        sandBlocks.add(Material.CUT_SANDSTONE);
        sandBlocks.add(Material.CUT_SANDSTONE_SLAB);
        sandBlocks.add(Material.SMOOTH_SANDSTONE);
        sandBlocks.add(Material.SMOOTH_SANDSTONE_SLAB);
        sandBlocks.add(Material.SMOOTH_SANDSTONE_STAIRS);
        sandBlocks.add(Material.SANDSTONE_WALL);
        sandBlocks.add(Material.RED_SAND);
        sandBlocks.add(Material.RED_SANDSTONE);
        sandBlocks.add(Material.RED_SANDSTONE_SLAB);
        sandBlocks.add(Material.RED_SANDSTONE_STAIRS);
        sandBlocks.add(Material.CHISELED_RED_SANDSTONE);
        sandBlocks.add(Material.CUT_RED_SANDSTONE);
        sandBlocks.add(Material.CUT_RED_SANDSTONE_SLAB);
        sandBlocks.add(Material.SMOOTH_RED_SANDSTONE);
        sandBlocks.add(Material.SMOOTH_RED_SANDSTONE_SLAB);
        sandBlocks.add(Material.SMOOTH_RED_SANDSTONE_STAIRS);
        sandBlocks.add(Material.RED_SANDSTONE_WALL);
        CollectItemSetAmountGoal sandGoal = new CollectItemSetAmountGoal("Collect 10 Unique Types of Sand Blocks", sandBlocks, 10);
        availableGoals.add(sandGoal);

        List<Material> redstoneItems = new ArrayList<>();
        redstoneItems.add(Material.REDSTONE_TORCH);
        redstoneItems.add(Material.REDSTONE_BLOCK);
        redstoneItems.add(Material.PISTON);
        redstoneItems.add(Material.REPEATER);
        redstoneItems.add(Material.COMPARATOR);
        redstoneItems.add(Material.DISPENSER);
        redstoneItems.add(Material.DROPPER);
        redstoneItems.add(Material.OBSERVER);
        redstoneItems.add(Material.DAYLIGHT_DETECTOR);
        redstoneItems.add(Material.CRAFTER);
        redstoneItems.add(Material.TARGET);
        redstoneItems.add(Material.COPPER_BULB);
        redstoneItems.add(Material.WAXED_COPPER_BULB);
        redstoneItems.add(Material.POWERED_RAIL);
        redstoneItems.add(Material.ACTIVATOR_RAIL);
        redstoneItems.add(Material.DETECTOR_RAIL);
        redstoneItems.add(Material.REDSTONE_LAMP);
        redstoneItems.add(Material.COMPASS);
        redstoneItems.add(Material.CLOCK);
        CollectItemSetAmountGoal redstoneGoal = new CollectItemSetAmountGoal("Collect 7 Unique Items Crafted with Redstone", redstoneItems, 7);
        availableGoals.add(redstoneGoal);

        List<Material> fish = new ArrayList<>();
        fish.add(Material.COD);
        fish.add(Material.COOKED_COD);
        fish.add(Material.SALMON);
        fish.add(Material.COOKED_SALMON);
        fish.add(Material.TROPICAL_FISH);
        fish.add(Material.PUFFERFISH);
        CollectItemSetAmountGoal fishUniqueGoal = new CollectItemSetAmountGoal("Collect 5 Unique Fish", fish, 5);
        availableGoals.add(fishUniqueGoal);

        ItemStack codBucket = new ItemStack(Material.COD_BUCKET, 1);
        KillEntityWithCauseGoal codWithLavaGoal = new KillEntityWithCauseGoal("Kill a Cod with Lava", codBucket, EntityType.COD, EntityDamageEvent.DamageCause.LAVA, GameManager.instance.killEntityListener);
        availableGoals.add(codWithLavaGoal);

        ItemStack bush = new ItemStack(Material.BUSH, 16);
        CollectItemsAmountGoal collectBushesGoal = new CollectItemsAmountGoal("Collect 16 Bushes", bush);
        availableGoals.add(collectBushesGoal);

        ItemStack lodestone = new ItemStack(Material.LODESTONE, 1);
        CollectItemGoal loadstoneGoal = new CollectItemGoal("Craft a Lodestone", lodestone);
        availableGoals.add(loadstoneGoal);

        ItemStack ghastEgg = new ItemStack(Material.GHAST_SPAWN_EGG, 1);
        KillEntityWithCauseGoal ghastWithSword = new KillEntityWithCauseGoal("Kill a Ghast with a Melee Attack", ghastEgg, EntityType.GHAST, EntityDamageEvent.DamageCause.ENTITY_ATTACK, GameManager.instance.killEntityListener);
        availableGoals.add(ghastWithSword);

        DeathGoal bangGoal = new DeathGoal("Achieve the Death Message '<player> went off with a bang'",fireworkRocket ,"went off with a bang",GameManager.instance.deathListener);
        availableGoals.add(bangGoal);

        ItemStack shroomLight = new ItemStack(Material.SHROOMLIGHT, 32);
        CollectItemsAmountGoal shroomLightGoal = new CollectItemsAmountGoal("Collect 32 Shroomlights", shroomLight);
        availableGoals.add(shroomLightGoal);

        ItemStack snifferEgg = new ItemStack(Material.SNIFFER_EGG, 1);
        CollectItemGoal snifferEggGoal = new CollectItemGoal("Find a Sniffer Egg", snifferEgg);
        availableGoals.add(snifferEggGoal);

        ItemStack calibratedSculkSensor = new ItemStack(Material.CALIBRATED_SCULK_SENSOR, 1);
        CollectItemGoal calibratedSculkSensorGoal = new CollectItemGoal("Craft a Calibrated Sculk Sensor", calibratedSculkSensor);
        availableGoals.add(calibratedSculkSensorGoal);

        ItemStack dragonEgg = new ItemStack(Material.DRAGON_EGG);
        CollectItemGoal dragonEggGoal = new CollectItemGoal("Collect the Dragon Egg", dragonEgg);
        availableGoals.add(dragonEggGoal);

        ItemStack dragonsBreath = new ItemStack(Material.DRAGON_BREATH, 1);
        CollectItemGoal dragonsBreathGoal = new CollectItemGoal("Collect a Dragon's Breath", dragonsBreath);
        availableGoals.add(dragonsBreathGoal);

        List<Material> harnesses = new ArrayList<>();
        harnesses.add(Material.BLACK_HARNESS);
        harnesses.add(Material.BLUE_HARNESS);
        harnesses.add(Material.BROWN_HARNESS);
        harnesses.add(Material.CYAN_HARNESS);
        harnesses.add(Material.GRAY_HARNESS);
        harnesses.add(Material.GREEN_HARNESS);
        harnesses.add(Material.LIGHT_BLUE_HARNESS);
        harnesses.add(Material.LIGHT_GRAY_HARNESS);
        harnesses.add(Material.LIME_HARNESS);
        harnesses.add(Material.MAGENTA_HARNESS);
        harnesses.add(Material.ORANGE_HARNESS);
        harnesses.add(Material.PINK_HARNESS);
        harnesses.add(Material.PURPLE_HARNESS);
        harnesses.add(Material.RED_HARNESS);
        harnesses.add(Material.WHITE_HARNESS);
        harnesses.add(Material.YELLOW_HARNESS);
        CollectItemsGoal harnessGoal = new CollectItemsGoal("Craft a Harness", harnesses);
        availableGoals.add(harnessGoal);

        ItemStack driedGhast = new ItemStack(Material.DRIED_GHAST, 1);
        CompleteAdvancementGoal driedGhastGoal = new CompleteAdvancementGoal("Complete the advancedment 'Stay Hydrated!'", driedGhast, Bukkit.getAdvancement(new NamespacedKey("minecraft","husbandry/place_dried_ghast_in_water")));
        availableGoals.add(driedGhastGoal);
    }
}
