# Extending and changing the fork

Design note for adding content to DBS and changing upstream behaviour. It comes from two design reviews on 2026-09-25 (new content; changes to existing content). Their mechanism claims were re-checked against the source at `85533666` and, for vanilla and FML behaviour, with `javap` on `forgeBin-1.7.10-10.13.4.1614-1.7.10.jar`. On 2026-09-26 it was updated with the owner's decisions and with a verified inspection of the owner's mod pack (the addons that patch or call HBM code, id usage, OpenComputers, NEI and AE2). Paths are repo-relative, and `com/hbm/...` means `src/main/java/com/hbm/...`. `MainRegistry` line numbers are current: they already count the fork hook at L285. How-to recipes for upstream-style edits are in `CLAUDE.md`, and per-area detail is in `docs/codebase-map.md`.

## 1. Purpose and status

Decided:
- Edit policy (owner, 2026-09-25): any file may be edited. `com/hbm/dbs` is a convenience, not a requirement: fork content may live there or inline in upstream files. Merge cost with JameH2's branch is a trade-off to weigh, not a rule. Sessions edit the source files directly.
- The fork's current footprint in upstream files is small. One hook line calls `DBSFork.init()` (`MainRegistry.java:285`), which runs `DBSItems.afterItemInit()` and registers `DBSRecipes` as an `IRecipeRegisterListener`. The other edits are one NEI line (`NEIRegistry.java:36`), the `en_US`/`de_DE` lang edits, `.editorconfig:5`, the deleted `CONTRIBUTING.md` and the version identity below (`build.gradle:38-41`, `HTTPHandler.java`, the login MOTD in `ModEventHandler.java`). `git diff --ignore-cr-at-eol --stat 64b64130 HEAD -- . ':!docs'` lists 14 paths at `2de1c6fe` (`64b64130` = `ee9fd54a^2`, the last merged JameH2 tip); the version identity adds those three files and the new `com/hbm/dbs/DBSVersion.java`.
- Version identity (2026-09-26, B-008): `RefStrings.VERSION` stays upstream's `1.0.27 BETA (5778)`. CI's sed rewrites it (`.github/workflows/build.yml:48`), every merge changes it, and it is also the `@Mod` version FML checks in the handshake (`MainRegistry.java:109`). The fork is identified by `DBSVersion.REVISION` (`"1"`) instead: `build.gradle` appends `_DBS<revision>` to the jar and `mcmod.info` version (`1.0.27_X5778_H261_DBS1`), the update check compares `REVISION` with the same file on the fork's branch (`HTTPHandler.java:41`, `DBSVersion.REMOTE_SOURCE`), each download there has its own try/catch, and the login MOTD shows `(DBS r1)` and links to the fork repository. `tools/check-fork.sh` checks this wiring.
- Persisted positional ids are never inserted into or reordered (section 5). That rule is technical, not a merge rule.
- Owner environment (2026-09-26): solo play, so the integrated server always runs the client's jar. The pack is the owner's own collection of many GTNH forks, without GregTech: GTNH OpenComputers kept at the latest stable (1.12.62 now), GTNH NEI 2.8.146, AE2 rv3-beta-1071-GTNH, EndlessIDs 1.7.4, UniMixins 0.3.1, and several addons that patch HBM classes (section 7, third-party patches).

The three decisions below were answered by the owner on 2026-09-26. The rejected options are kept as short notes.

### (1) Keep merging JameH2's `space-travel-twopointfive`: decided, merges continue

- Decision: upstream merges from JameH2's branch continue. Both upstreams are actively developing: HbmMods reworks machine fluid and item ports and adds satellites, NTM: Space adds space warfare. Follow rule 1 in section 4: fork files, listeners and one-line hooks where upstream churns (registries, recipe tables), inline edits with a marker on the changed line elsewhere. Keep `tools/check-fork.sh`, `-Xrenormalize` and the merge procedure in `docs/fork-notes.md`. Merge cost grows mostly with inline edits in `ModItems`, `ModBlocks` and `AssemblyMachineRecipes`, which the techniques below avoid.
- Remotes: `upstream` (JameH2) and `hbm` (HbmMods) were added to the owner's clone on 2026-09-26 as read-only (push URL `NO_PUSH_read_only_remote`) and fetched. JameH2's tip is still `64b64130`. HbmMods `master` is at `e674541d8` (build 5808), 200 first-parent commits past the merge base `9a2eb731`. Fetch again before a merge.
- The pending HbmMods delta reworks machine fluid and item ports. It deletes the old fluid API package `api/hbm/fluid` (`2393b6de` and three earlier commits), and in its TilePort preparation `f4ff0062` deletes `IEnergyReceiverMK2.trySubscribe`, and `0352a3e8` turns `NodeNet`'s subscriber `HashMap`s into `Set`s and deprecates the `IFluidStandardSenderMK2.tryProvide` defaults, which `7195c68e` and `5566a5d1` then delete. It is expected to conflict with inline edits to the affected tiles, and it breaks four addons in the owner's pack unless the merge keeps compatibility shims (section 7, third-party patches; merge handling in `docs/fork-notes.md`).
- Not chosen: a hard fork (rule 2 in section 4).

### (2) Fork content inside the `hbm` mod: decided

- Decision: fork content stays inside `hbm`, reached through one-line hook lines in `MainRegistry` (section 2); there is no second `@Mod`. Registry names stay `hbm:`, and upstream's one-shot passes also pick up fork content: item renderers at L287, ore dict at L305, machine config at L336 and TE registration at L339-345, which only the L285 window reaches. The `MainRegistry` hook lines sit in quiet neighbourhoods: the lines around L285 have had no upstream change since 2024-10-25 (L282; the adjacent L283-284 date from 2017 and L286 from 2023), and L600-602 were last changed in 2021-2024 (git blame). The `NEIRegistry` hook (`NEIRegistry.java:36`) and the `en_US`/`de_DE` lang edits do not: they re-add or replace lines HbmMods changed in `804d2bd1` (2026-07-29).
- Server rejection of unmodified clients is not a concern: the owner plays single-player. For reference only: the FML network identity is upstream's `@Mod` version, which the fork's revision does not touch, and with no `acceptableRemoteVersions` FML compares versions by `String.equals` (javap `NetworkModHolder$DefaultNetworkChecker`). A plain NTM: Space 5778 client therefore joins a DBS server until DBS registers its first own block or item; after that FML rejects it with "Fatally missing blocks and items" (`FMLHandshakeClientState`, `GameData.injectWorldIDMap`). Server recipe sync runs on dedicated servers only (`ModEventHandler.java:247`), so it is moot in solo play too.
- Not chosen: a second container, `@Mod(modid = "hbmdbs", dependencies = "required-after:hbm")`. It would get its own lifecycle, registry namespace, IMC, missing-mapping handler and network identity (javap `JarDiscoverer.discover`, `FMLHandshakeServerState$2`), but hbm's `@SidedProxy` (`MainRegistry.java:115`) would need `modId = RefStrings.MODID` or be injected twice (javap `ProxyInjector.inject`), its content would miss the one-shot passes above, QMAW pages and OC floppies still load only from `assets/hbm` (section 3), `IMCHandlerNHNEI.java:20,24` hard-codes `hbm:` in its NEI IMC item names, and the container would need its own `mcmod.info` entry.

### (3) Reserved id bands: decided, adopted

- Decision: the bands in the section 5 table are adopted: fluids 20000-20999, materials 30000-32767, bullet base 2000, weapon mods 9000-9999, mod entities 1000+, eggs 4000+, packets on an own channel (else 100-127), potions 110-119, biomes 230-239 (plus the EndlessIDs offset, section 5), dimensions 413_100+, satellites 1000+, siege tiers 90-99, molds 1000+. The pack inspection of 2026-09-26 found the potion, biome (with the EndlessIDs offset), entity, egg and fluid bands free in the owner's pack; the biome result rests on an incomplete AntiIDConflict dump, and the other bands were not part of that check.
- Keep every fork id in one class (`DBSIds`, created with the first fork id) with startup assertions (fluid id free, `!Mats.matById.containsKey`, bullet padding, weapon-mod id free, entity id free). Without them, several registries overwrite silently on a collision: `NTMMaterial.java:47`, `WeaponModBase.java:13`, `ItemMold.java:98` and `Fluids.java:1181-1185`. While merging continues, the bands keep fork ids out of the ranges upstream grows into; the positional registries (bullets, spawn eggs, satellites) need fixed bases in any case.
- The owner runs EndlessIDs 1.7.4 (GTMEGA), which lifts the 1.7.10 id limits in the pack: its `endlessids.cfg` extends biome, potion, entity, DataWatcher, enchantment and block/item ids (32k blocks, 64k items). The bands still stay inside the vanilla limits where one applies (potions below 128, biomes below 256), so fork content does not depend on it. Block metas above 15 are stored under EndlessIDs too, but fork blocks keep the 16-value cap.

## 2. Lifecycle and hook points

`MainRegistry` has two `FMLPreInitializationEvent` handlers, `PreLoad` (L254) and `preInit` (L682), and FML does not order them. `preInit` registers the event handlers and `PacketDispatcher.registerPackets()` (L697).

| Where | What runs | Relevance to fork code |
|---|---|---|
| PreLoad L275-281 | `loadConfig` (hbm.cfg; `Configuration` is a local, L765), `HbmPotion.init`, `Mats` class init (L280), `Fluids.init` (fluid listeners fire at `Fluids.java:551`) | fork fluid listeners registered at L285 miss this pass |
| L283-284 | `ModBlocks.mainRegistry`, `ModItems.mainRegistry` (`ModItemsArmor.init` at `ModItems.java:4375`, `GunFactory.init` at `:4881`) | all upstream blocks, items, armor and most `BulletConfig`s exist after this |
| **L285** | `com.hbm.dbs.DBSFork.init(); // DBS fork hook` | current single entry point |
| L287 | `proxy.registerRenderInfo()`: TESR binding and the only item-renderer pass (`ClientProxy.java:519-548`) | TESRs bound earlier that implement `IItemRendererProvider`, and `IItemRendererProvider` items registered earlier, get item renderers automatically |
| L292, L295 | `XSatelliteRegistry.register`, `SiegeTier.registerTiers` | positional (satellites, siege tiers): anything added at L285 takes id 0 |
| L298 | `XWeaponModManager.init` | explicit ids; an upstream mod with the same id replaces a fork mod created at L285 without error (HashBiMap) |
| L294, L296-297, L302-305 | `CraftingManager.mainRegistry`, `HazardRegistry.registerItems/Trafos`, `OreDictManager` | overwrite or extend whatever L285 wrote |
| L333, L335-345 | `registerGuiHandler`, `TileMappings.writeMappings`, `MachineDynConfig.initialize`, TE registration loop | read `TileMappings.map`/`configurables` filled at L285 |
| L361 | `EntityMappings.writeMappings` (mod-entity ids from 0, egg ids from 500) | egg ids are first-free (section 5) |
| load L388-548 | stats and achievements, `AchievementPage "Nuclear Tech"` (L468), `BobmazonOfferFactory.init` (L536, clears its lists) | append to either only after these lines |
| PostLoad L573-591 | `Fluids.reloadFluids` (listeners at `Fluids.java:1145`), `FluidContainerRegistry.register`, Magic/Lemegeton/SILEX/GasCent (L576-579), `SerializableRecipe` (L584-585, listeners at `SerializableRecipe.java:152-154`), anvil smithing (L588), radiolysis (L591) | recipe and fluid listeners fire here |
| L593-602 | Fallout/ItemPool JSON, `ClientConfig`/`ServerConfig.initConfig` (L596-597), `ArmorUtil.register` (L600), `HazmatRegistry.registerHazmats` (L601), `DamageResistanceHandler.init` (L602) | these fill after L285 and overwrite same-key writes (`DamageResistanceHandler.init` clears all its tables first, `DamageResistanceHandler.java:73,83-88`) |
| L615, L674 | `CompatHandler.init` (OC), `CommandWikiRender.register` (added 2026-07-16: the end of PostLoad is where upstream appends) | |
| L735-751, L800 | `serverStart` (commands), `handleMissingMappings` | |

What L285 cannot do: everything except potions, materials, fluids and the block/item registries is populated after it. Upstream tables that fill later can therefore only be reached through listeners (recipes, fluids) or from a later hook. L285 does not cover crafting, hazards, ore-dict changes to upstream entries, TE mappings or anything in PostLoad. The `DBSFork` Javadoc (`DBSFork.java:10-13`) says so and allows inline edits under the edit policy.

Hook set. Add each hook only when first needed. Each is one additive line marked `// DBS fork hook`, and `check-fork.sh` should verify its order the same way it checks L285 (`tools/check-fork.sh:71-74`).
- H1, L285 (existing; it could be renamed `preInit`, but `check-fork.sh` greps the name): new items, blocks, TEs, fluids, materials, bullets, weapon mods, hazards for fork items, entities with explicit ids, client binding, packets, config, creative tab and recipe listeners.
- H2, end of PreLoad (after L382): anything that must follow L292/L295/L361, such as satellites and siege tiers registered after upstream's. Egg mobs do not need H2: without the `eggIDCounter` wrap from section 3 (Entities) they still shift when upstream adds a mob (section 5), and with it they work at H1. L381-382 changed in 2025.
- H3, end of `load` (before L549): appending to the `Nuclear Tech` achievement page and to Bobmazon offers.
- H4, PostLoad directly after `DamageResistanceHandler.init();` (L602), not at the end: overrides of crafting, ore dict, hazards, `ArmorRegistry`, rad resistance, smithing and radiolysis (after adding an accessor, see section 3 (e)). Damage-resistance overrides made here are lost on `/ntmreload` (section 4, config).
- H5, commands: `FMLServerStartingEvent.registerServerCommand` only forwards to `((CommandHandler) server.getCommandManager()).registerCommand` (javap). A one-line call from `serverStart` (L735-751), or a Forge `WorldEvent.Load` handler using `MinecraftServer.getServer()`, is enough.
- H6, renames of `hbm:` names: `FMLMissingMappingsEvent` reaches only `@EventHandler` methods of `@Mod` classes (javap `Loader.fireMissingMappingEvent`). hbm's own tables are private and cleared on every call (`MainRegistry.java:795-803`), so a fork line inside `handleMissingMappings` (L800) is needed. `ModBlocks.addRemap` (`ModBlocks.java:4052`, public) is a separate alias-block mechanism.

Client code: declare `@SidedProxy(clientSide = "com.hbm.dbs.client.DBSClientProxy", serverSide = "com.hbm.dbs.DBSCommonProxy") public static DBSCommonProxy proxy;` in a small holder class and call it from H1. FML injects `@SidedProxy` fields from any class in the mod source during construction, initialising the declaring class (javap `ProxyInjector.inject`, `FMLModContainer.constructMod`), so keep the holder's static initialiser trivial. `DBSFork.init` runs on both sides, so `ClientRegistry`/`RenderingRegistry` calls must go through that proxy. Keep model and texture statics in a client-only fork class, not in `ResourceManager`.

## 3. Adding content, by kind

The advice follows decision (2), content inside `hbm`, and decision (3), the adopted id bands of section 5 (fluids 20000-20999, materials 30000-32767, mod entities 1000+, eggs 4000+, bullets 2000+, weapon mods 9000-9999). Fork names carry a `dbs_` prefix: registry, unlocalized, TE, entity, structure, QMAW and config names, so a later upstream addition cannot take the same name.

### Items and meta items

Declare the fields in a fork class, construct them at H1 and call `GameRegistry.registerItem(item, item.getUnlocalizedName())`, as `ModItems.registerItem` does. A duplicate registry name throws. Registration after the freeze only warns (javap `FMLControlledNamespacedRegistry.add`). For meta items, use a fork `ItemEnumMulti` with a fork enum. The meta is the enum ordinal (`ItemEnumMulti.java:40,88`), and unknown metas wrap silently (`EnumUtil.grabEnumSafely`, `:75,99`), so while merging, a constant appended to an upstream enum collides with upstream's next append without any error. When merging, appending to `ModItems` is allowed but costly: it changed in 17 of 20 syncs and lost 1009 lines.

### Blocks

`ModBlocks.register` is private (`ModBlocks.java:4044-4050`). Call `GameRegistry.registerBlock(b, ItemBlockBase.class, b.getUnlocalizedName())` (the same as `:4045`) to keep tooltips, `IBlockMulti` and `IPersistentInfoProvider`. `BlockEnumMulti` meta is the ordinal, 16 at most. `ModBlocks.addRemap` is public (`:4052`).

### Machines (TE, container, GUI, TESR)

- TE: at H1, `TileMappings.map.put(TE.class, new String[]{"tileentity_dbs_x"})`, plus `TileMappings.configurables.add(TE.class)` for an `IConfigurableMachine`, plus `Compat.blacklistAccelerator(TE.class)`. That mirrors the private `put` (`TileMappings.java:568-581`); `map` and `configurables` are public (`:73-74`) and are read at L336 and L339-345. A duplicate id throws `Duplicate id:` (javap `TileEntity.addMapping`). A TE that is never mapped works until its chunk saves: `TileEntity.writeToNBT` throws `... is missing a mapping! This is a bug!`, and `AnvilChunkLoader.writeChunkToNBT` catches it per TE, logs `A TileEntity type ... will not persist` and writes the chunk without that TE, so the machine's state is lost on reload (javap).
- GUI: implement `IGUIProvider` and open with `FMLNetworkHandler.openGui(player, MainRegistry.instance, 0, world, x, y, z)` (as `BlockDummyable.standardOpenBehavior` does, `BlockDummyable.java:459`). `GUIHandler` looks up the provider from the TE, the block, the held item and then the entity, with no id table (`GUIHandler.java:29-43`). The TE and block lookups take precedence.
- TESR: it must implement `IItemRendererProvider` and be bound before L287 (`ClientProxy.java:527-536`). A plain TESR gets no item renderer.
- Config: machine config keys get a `dbs_` prefix. `MachineDynConfig` instantiates the class with `newInstance()` (`MachineDynConfig.java:41`).

### Fluids

- Register only through `CompatFluidRegistry.registerFluid(name, id, ...)` (`CompatFluidRegistry.java:12-17`) with an explicit id from the fork band, from a fork `IFluidRegisterListener` (`CompatExternal.registerFluidRegisterListener`, `CompatExternal.java:209-211`). In the owner's pack EnviroMine registers 20 foreign fluids the same way, at explicit ids 7732-7751, clear of the fork band.
- Fluid names are save-relevant in the owner's pack, and fork fluids are permanent once a world has seen them. bobfluidtranslator registers a Forge fluid `<name>_fluid` plus a `bobfluidtranslator:<name>_fluid_block` block and its item for every `FluidType` in `Fluids.getAll()` (it skips names that already exist as Forge fluids, which is why EnviroMine's are not bridged), and those registry names enter the world's id map. Renaming or removing a fluid, upstream's or the fork's, leaves missing mappings on world load. Never rename a fluid.
- Fix B-137 (Phase 6) before shipping dispersable fork fluids. `FluidContainerRegistry.register` registers `disperser_canister` and `glyphid_gland` by loop index, not fluid id (`FluidContainerRegistry.java:98-99`). A fork fluid at 20000+ would therefore get those containers at meta = its `registerOrder` index, mapped to `Fluids.fromID(index)`, and none at its own id. Every fluid without `FT_Amat`, `FT_NoContainer` or `FT_Viscous` is dispersable (`FluidType.java:206-207`).
- `Fluids.init` has already fired the listeners by L285 (`Fluids.java:551`), so at H1 call the listener once yourself and add the types to `Fluids.metaOrder`, because init adds foreign fluids only during init (`:806-807`). `reloadFluids` (PostLoad and `/ntmreload`) drops foreign fluids, calls the listeners and re-adds them (`:1111-1147`).
- Do not use the positional `FluidType` constructor. It takes `idMapping.size()` (`:1173-1178`), which depends on the user's JSON fluids and on upstream's count, and `Fluids.register` overwrites an existing id without a check.
- Keep ids at or below 32767: ids are narrowed to `short` in `ItemPipette.java:66`, `TileEntityRBMKBoiler.java:254` and `TileEntityRBMKHeater.java:175`.
- Traits set in the listener win over `hbmFluidTraits.json`, because the JSON is read first (`:1136-1145`). The `_hbmFluidTraits.json` template is written before the listeners run, so it will not show fork traits. The lang key is `hbmfluid.<lowercased name>` (`FluidType.java:112`).
- Register fork fluid containers (`FluidContainerRegistry.registerContainer`, public) inside the listener, because `/ntmreload` clears the registry before re-running the listeners (`CommandReloadRecipes.java:33-35`). Skip them in the manual H1 call, because `registerContainer` does not dedupe (`FluidContainerRegistry.java:113-121`).
- Two-way fluid tiles: implement `IFluidStandardTransceiverMK2` rather than a receiver and a sender interface separately whenever the tile lives outside `com.hbm.tileentity` (for example in `com/hbm/dbs`) or gets its receiver and sender sides from different levels of a class hierarchy. bobfluidtranslator's coremod injects default Forge `IFluidHandler` trampolines (`fill`, `drain`, `canFill`, `canDrain`, `getTankInfo`) into all five MK2 fluid interfaces. Its diamond-conflict pass adds concrete trampolines only to `com.hbm.tileentity.*` classes whose own interface list names both a receiver-side and a sender-side interface. Any other two-way tile inherits conflicting defaults, and a Forge caller (a conduit, an AE2FC bus, a tank probe) gets `IncompatibleClassChangeError`. The pack jar has no such class today. The rule matters only while bobfluidtranslator is installed and costs nothing otherwise.

### Materials

`new NTMMaterial(id, ...)` puts the material into `Mats.matById` without a check (`NTMMaterial.java:47`). Create fork materials at H1 so that `OreDictManager.registerOres` (L305), which iterates `Mats.orderedList` (`OreDictManager.java:631,768`), sees them. Use ids from 30000-32767; metas are capped at 32767 (`Mats.java:38`).

### Recipes, by family

- (a) Existing `SerializableRecipe`/`GenericRecipes` sets: add a branch in `DBSRecipes.onRecipeLoad`. Inline in the set's `registerDefaults()` has identical semantics and is the better choice under a hard fork. `AssemblyMachineRecipes` changed in 14 of 20 syncs. Name fork recipes `<prefix>.dbs_<name>`. Names are persisted (`ModuleMachineBase.java:264`) and are unique only within a set (`GenericRecipes.java:100` throws). The same name in another set silently overwrites `GenericRecipes.nameToRecipeGlobal` (`:102`), which `ItemBlueprints.java:124` reads.
- (b) A fork machine's own recipe family: subclass `SerializableRecipe`/`GenericRecipes` and add it to the public `SerializableRecipe.recipeHandlers` (`:42`) before PostLoad. `registerAllHandlers` only appends, so the fork handler runs first and gets its own `hbm<X>.json`, `/ntmreload` and sync.
- (c) Crafting table: `CraftingManager.addRecipeAuto`/`addShapelessAuto` (public, `CraftingManager.java:1157,1182`) at H1. To change an upstream crafting recipe, use H4.
- (d) Anvil: construction recipes through the listener (`AnvilRecipes` is a serializable set, and its `deleteRecipes` clears `constructionRecipes`, `AnvilRecipes.java:50`). `smithingRecipes` is public and never cleared (`:41`, grep), so any hook works.
- (e) Non-serializable tables. `LemegetonRecipes.recipes` is public (`:17`), and `MagicRecipes.getRecipes()` returns the live list (`:68-70`). The gas centrifuge's working table `fluidConversions` is public (`GasCentrifugeRecipes.java:95`), but `PseudoFluidType`'s constructor is package-private (`:48`) and NEI reads the private `gasCent` (`:69`). `SILEXRecipes` (`:28-30`) and `RadiolysisRecipes` (`:21`) have no public add path, so adding to them needs an accessor in the upstream class. For radiolysis, keep `registerRadiolysis()` and the `radiolysis` field (name and static `Map` type) as they are, because EnviroMine patches both (section 7, third-party patches). Magic/Lemegeton/SILEX/GasCent fill at L576-579, before `SerializableRecipe.initialize`, so any `onRecipeLoad` callback can change them. Put the change inside one class-name branch. Listeners re-fire on `/ntmreload` (`CommandReloadRecipes.java:36`) and after a synced session (`SerializableRecipe.java:172-176`) while these tables are not rebuilt, so add to the Magic list at H1 or H4 (it is never cleared) or guard the add with a contains check. Only smithing (L588) and radiolysis (L591) fill after every listener call.
- (f) Bobmazon: after L536 (H3).
- `CompatRecipeRegistry` is add-only and has no `inputItemsEx`. Its legacy `registerAssembler(ItemStack, AStack[], int)` is an empty stub (`CompatRecipeRegistry.java:288`).

### Lang, textures, sounds, models, structures

- Every lowercase `assets/<domain>/` in the jar is a live resource domain (javap `FileResourcePack.getResourceDomains`; FML's packs do not override it). The jar already ships `hbm` and `minecraft`.
- Lang: new keys can go in `assets/dbs/lang/<lang>.lang`. The client loads `lang/<lang>.lang` from every domain, and later domains overwrite earlier ones (javap `Locale.loadLocaleDataFiles`). The dedicated server gets `en_US` from FML's jar scan (`assets/(.*)/lang/(?:.+/|)([\w_-]+).lang`, javap `LanguageRegistry`). A key defined in both `hbm` and `dbs` resolves differently per side: `dbs` wins on the client (hash order `minecraft` < `hbm` < `dbs`), while the server takes whichever entry comes later in the zip. So the `dbs` file is for new keys only. Changing an upstream key means editing `assets/hbm/lang`.
- Sounds: `assets/dbs/sounds.json` gives `dbs:<key>` events, and a file name without a colon resolves under `assets/dbs/sounds/` (javap `SoundHandler`). Fork events in `assets/hbm/sounds.json` also work, but they conflict while merging: that file changed in at least 5 of the 20 syncs.
- Textures and models: new files under `assets/hbm/` conflict only on an identical path. `dbs:` texture names are not javap-verified (`TextureMap`); check once whether the ForgeGradle dev run sees `assets/dbs`.
- NBT structures: the constructor reads `/assets/<domain>/<path>` from the classpath (`NBTStructure.java:108-110`), so any domain works.

### QMAW manual

Pages must live under `assets/hbm/manual/`; subfolders are allowed (`QMAWLoader.java:131,157,177-178`). Use `assets/hbm/manual/dbs/*.json` with `"name": "dbs_..."` and the four mandatory fields. A duplicate `name` replaces the earlier page with only an INFO log, `[QMAW] Overriding existing entry` (`:188-190`). Load order is mod jars, then the dev folder, then resource packs. Trigger mappings overwrite silently.

### OC components

- A fork TE implements `li.cil.oc.api.network.SimpleComponent` and `CompatHandler.OCComponent` directly, with `@Optional.Interface(..., modid = "OpenComputers")` in exact case. FML's stripping check is an exact-match lookup, so a lowercase modid strips the interface (B-319). Give component names a `dbs_` prefix.
- Only the topmost component TE of a hierarchy may reference `SimpleComponent`. GTNH OC 1.12.62 injects every class whose constant pool holds the exact Utf8 `li/cil/oc/api/network/SimpleComponent` (gtnhlib `ClassConstantPoolParser`), not only classes that list the interface. Any mention in a subclass of an already-injected TE (implements, cast, `instanceof`, class literal, interface call) therefore injects that subclass a second time: its injected `validate`, `invalidate`, `onChunkUnload`, `readFromNBT` and `writeToNBT` call the parent's wrappers, which dispatch back to the child, and the recursion ends in `StackOverflowError`. A non-TE class that mentions it gets a `Failed injecting component logic` WARN and a login chat warning. A subclass gets its own component name by overriding `getComponentName()`. Nothing triggers this today: 29 subclasses of 10 injected TEs exist, and none is injected again.
- The fork compiles against the MightyPirates OC API (`build.gradle:118`). Every OC member it uses resolves with the same descriptor in GTNH OC 1.12.62, which the owner keeps at the latest stable.
- Floppies: `CompatHandler.disks` is public (`CompatHandler.java:174`) and is read at L615 from `assets/hbm/disks/<name>` (`:86,201`). `FloppyDisk`'s constructor is package-private (`:99`), though, so code outside `com.hbm.handler` needs a change in `CompatHandler` first.

### AE2 storage handlers

- Register handlers behind `Loader.isModLoaded("appliedenergistics2")` from an `@Optional.Method` (`handler/ae2/AE2CompatHandler.java:9-17`), and put `@Optional.InterfaceList` on the handler and inventory classes (`MSUExternalStorageHandler.java:18`, `MassStorageMEInventory.java:17`).
- Implement the `StorageChannel` variants: `IExternalStorageHandler.canHandle`/`getInventory(..., StorageChannel, ...)` and `IMEInventory.getChannel()`/`getAvailableItems(IItemList)`, as `MSUExternalStorageHandler` and `MassStorageMEInventory` do. The owner's pack runs AE2 rv3-beta-1071-GTNH, which turned these methods into mutually delegating default pairs: `getAvailableItems(IItemList)` and `getAvailableItems(IItemList, int)`, `getChannel()` and `getStackType()`, and each `StorageChannel` handler method and its new `IAEStackType` overload. Compiling against the declared rv3-beta.56 API (`build.gradle:120`) forces the `StorageChannel` side, so every pair reaches fork code. If the dependency is ever bumped to an API with `IAEStackType`, implement exactly one method of each pair; a class that implements neither half then recurses endlessly at runtime (`StackOverflowError`).

### Hazards and rad resistance

- Registration is public and never frozen: `HazardSystem.register` (`HazardSystem.java:54`), `HazardData.addEntry` (`HazardData.java:25`) and `ArmorRegistry.registerHazard` (`ArmorRegistry.java:18-20`). Upstream's `HazardRegistry.registerItems` (L296) and `ArmorUtil.register` (L600) run after H1 and overwrite same-key entries, so hazards for fork items go at H1 and changes to upstream items go in H4. `HazardRegistry.makeData` and the fuel helpers are private; copy their arithmetic.
- Rad resistance: add `Pair(item, value)` to `HazmatRegistry.external` (`:32`) at H1. `registerHazmats` re-applies `external` and then the defaults on top of the table (`:39-43,227`). An existing `hbmRadResist.json` replaces the whole table (`:233-236`, B-203), so tell users to regenerate it. `registerHazmat` (`:163`) can be called at any time.

### Creative tab

`new DBSTab(CreativeTabs.getNextID(), "tabDBS")` with lang key `itemGroup.tabDBS`. Tab indices are runtime-only; the tab lands after the 9 hbm tabs (`MainRegistry.java:162-170`).

### Config

Load a fork `Configuration` from `new File(MainRegistry.configDir, "dbs.cfg")` at H1. hbm.cfg's `Configuration` is a local in `loadConfig` (L765). For in-game-editable options, put `ConfigWrapper`s under `DBS_*` keys into `ServerConfig.configMap`/`ClientConfig.configMap` (public, `ServerConfig.java:11`, `ClientConfig.java:13`) before L596-597. Only String, Float, Double, Integer and Boolean values are handled (`RunningConfig.java:42-46`), and this covers only `hbmServer.json`/`hbmClient.json`. A fork config cannot influence upstream constructors, because `ModItems` and `ModBlocks` run before H1.

### Packets

Use a separate channel, `NetworkRegistry.INSTANCE.newSimpleChannel("hbm_dbs")`. Channel names are limited to 20 characters. FML never detects duplicate names: `newChannel`'s `containsKey` runs on an `EnumMap` keyed by `Side`, and only names starting with `MC|`, `\u0001` or `FML` are rejected (javap). If the precompiled `PacketDispatcher.wrapper` path is needed, register fork discriminators 100-127 on it. Never construct a second `NetworkHandler` (traps).

### Entities

At H1, use `ModEntityList.registerEntity(cls, "dbs_x", 1000 + k, MainRegistry.instance, range, freq, velocity)` (public overloads, `ModEntityList.java:27-41`); a reused id throws (`:52-53`). For mobs with eggs, wrap the calls: save `ModEntityList.eggIDCounter`, set it to 3999, register, then restore it (fork eggs at 4000+). Renderers go through the fork client proxy. The lang key is `entity.hbm.dbs_x.name`.

### Worldgen and structures

Use `GameRegistry.registerWorldGenerator(gen, weight)` and `NBTStructure.registerStructure(dim, new SpawnCondition("dbs_x"){{...}})` from H1 or later. `SpawnCondition` and `JigsawPiece` names must be unique; duplicates throw at startup (`NBTStructure.java:153-157`, `JigsawPiece.java:60`). Jigsaw piece names and `SpawnCondition` names are persisted in world structure data (`NBTStructure.java:898,907` and `:1225,1234`), so a renamed piece loads as null (`:907`). Pool names exist only in the template files (`:417`). Put `.nbt` files under `assets/hbm/structures/dbs/` or `assets/dbs/structures/`. Fork biomes and dimensions take ids from `dbs.cfg`; fork biome defaults add the same EndlessIDs offset as upstream's (`defaultBiomeOffset`, `WorldConfig.java:352`), which gives 12230-12239 in the owner's pack. Under EndlessIDs, `Chunk.getBiomeArray()`/`setBiomeArray()` and the vanilla `ExtendedBlockStorage` block and metadata array accessors throw. Go through the `WorldUtil` helpers, which branch on `Loader.isModLoaded(Compat.MOD_EIDS)` (`world/WorldUtil.java:30-86`), and read blocks with `getBlockByExtId`/`getExtBlockMetadata`, which EndlessIDs keeps working. That check keys on the mod being present, not on its `extendBiome` flag, a latent defect that the owner's config (`extendBiome=true`) does not trigger.

### Achievements

Stat ids are strings, and a duplicate throws `Duplicate stat id` (javap `StatBase.registerStat`). Either register an own `AchievementPage("DBS", ...)` at H1, or at H3 call `AchievementPage.getAchievementPage("Nuclear Tech").getAchievements().add(a)`; `getAchievements()` returns the live list (javap). Use `achievement.dbs_*` ids and trigger them from fork event handlers.

### Weapons

- Ammo: a fork `ItemEnumMulti` bound with `BulletConfig.setItem(ItemStack/ComparableStack)`. While merging, do not extend `EnumAmmo`: it is ordinal-keyed (`GunFactory.java:132`: "ONLY ADD NEW ENTRIES AT THE BOTTOM"), and upstream appends too.
- Bullets: at H1, assert `BulletConfig.configs.size() < 2000`, pad with inert `new BulletConfig()` up to 2000, then create the fork configs (ids 2000+k). Before padding, read `TileEntityBatterySocket.discharge` (`:72-75`) and then `TileEntityPileCore.pile_debris` (`:654-656`) at H1. Both are created in static initializers: on the client inside `GunFactory.init` (`GunFactory.java:93` -> `GunFactoryClient.java:228,254`, in that order), on a dedicated server lazily. Forcing them first gives both sides the same count before the padding. Without that, padding keeps fork ids equal but moves these two upstream configs past the fork block on the server only.
- Weapon mods: ids 9000-9999, with an `XWeaponModManager.idToMod.containsKey` assert, because the `HashBiMap` silently replaces a duplicate (`WeaponModBase.java:13`). Gun renderers and HUD go through the fork client proxy.

### NEI

- Add a fork class named `NEI*Config` that directly `implements IConfigureNEI` (for example `com.hbm.dbs.client.NEIDBSConfig`). NEI 1.0.3.74 discovers it by file name (javap `NEIClientConfig$4.matches`). GTNH NEI 2.8.110 uses either that name scan or, when `jarjar.rfbPluginLoaded` is set, the ASM table; a correctly named class that directly implements the interface works either way. Subclassing `NEIConfig` is not found. The `BreederRecipeHandler` line at `NEIRegistry.java:36` could move into it, but only together with its own NHNEI IMC. It implements `ICompatNHNEI`, and `IMCHandlerNHNEI.IMCSender` (from `load`, `MainRegistry.java:546` -> `ClientProxy.java:197`) sends handler and catalyst messages only for handlers returned by `NEIRegistry.listAllHandlers()`.
- Never pre-fill `NEIRegistry.handlers`. A handler registered outside it misses the NHNEI catalyst IMC (`IMCHandlerNHNEI.java:15`; `sendHandler`/`sendCatalyst` are private), so send your own `FMLInterModComms` messages from H1. Startup IMC is accepted until POSTINITIALIZATION (javap `FMLInterModComms.enqueueStartupMessage`).
- The owner runs GTNH NEI 2.8.146, so the NHNEI IMC path is live (the log shows 74 handler-info messages from hbm). 2.8.146 keys handler info by the `handler` class name and catalysts by `handlerID`, which must equal `firstNonNull(getOverlayIdentifier(), getHandlerId())`: the overlay id when the handler has one, else its class name. It ignores the `handlerID` extra of `registerHandlerInfo` and the `catalystHandlerID` extra, so `getRecipeID()` has no effect through IMC.

## 4. Changing existing behaviour

### Decision rule while merging (rule 1)

This rule applies: merges continue (decision 1). Pick the technique by what is changed, not by file. Upstream churn since 2026-01-01 (20 HbmMods syncs reachable from `64b64130`):
- Breadth: 1030 of 3885 java files changed at least once, 34 in 5 or more syncs, 9 in 10 or more.
- Registries (`ModBlocks`, `CraftingManager`, `ModItems`, `ResourceManager`, `MainRegistry`, `TileMappings`, `ClientProxy`, `en_US`/`de_DE.lang`) change in 16-19 of 20 syncs.
- Recipe tables change in about two thirds (`AssemblyMachineRecipes` 14/20, `AnvilRecipes` 13/20); `NEIRegistry` and `SerializableRecipe` in 6/20; `GenericRecipes` in 2/20 and `HazmatRegistry` in 1/20. `CompatExternal` and both reactor TEs have not changed.
- Deletions: `ModItems` lost 1009 lines (-327 in `0f660349` alone), `ModBlocks` 390, `AssemblyMachineRecipes` 348 and `CraftingManager` 189. Blame shares of recent lines therefore understate the conflict risk of mid-file edits.

| Change | Technique | Reason |
|---|---|---|
| Logic in method bodies (TE ticks, containers, block callbacks, bug fixes) | inline edit, marker on the changed line | no hook reaches it: `TileEntityReactorResearch.java:62` `maxHeat` compiles to `ldc 50000` in `updateEntity` (javap on build/classes) |
| Recipes in serializable sets (add, mutate, remove) | `DBSRecipes` listener | recipe tables churn; semantics equal an inline default |
| Crafting, ore dict, hazards, armor hazard classes, rad resistance, smithing, radiolysis (after adding an accessor, see section 3 (e)) | H4 (crafting: any hook after L294) | they fill after L285 |
| Magic/Lemegeton/GasCent tables (SILEX only after adding an accessor) | H1 or H4 (the Magic list is never cleared); from an `onRecipeLoad` callback only with a contains check on the Magic list, because listeners re-fire on `/ntmreload` and after a synced session (section 3 (e)) | they fill at L576-579, before L585 |
| Gun and bullet values | H1 | `BulletConfig` fields (`BulletConfig.java:58`) and receiver setters are public, and the objects exist after `GunFactory.init` (`ModItems.java:4881`) |
| Creative tab, hardness, stack size | `DBSItems` at H1 | nothing resets tabs later (every other `setCreativeTab` runs before L285) |
| NEI hiding | fork `NEI*Config` with `API.hideItem` | as `NEIConfig.java:50-52` |
| Placement, interaction, damage scaling, tooltips | Forge events | see below |
| hbm.cfg / machine / damage-resistance / ItemPool defaults | inline default edit | see config below |
| Textures, models, sounds, lang | edit the files; record them in the manifest | assets cannot carry markers |

### Hard fork (rule 2, not chosen)

Kept for reference; decision 1 chose to keep merging. Under a hard fork, write every change inline in the owning class: recipes in `registerDefaults()`, tabs in `ModItems`/`ModBlocks`, and config defaults. `DBSRecipes`/`DBSItems` can stay as they are or be folded back in one batch. Keep `// DBS fix B-xxx` markers only for traceability to `docs/known-bugs.md`.

### Forge events

Available in Forge 10.13.4.1614 (javap on forgeBin):
- Cancelable: `PlayerInteractEvent` (`useBlock`/`useItem` results), `BlockEvent` Break/Place/MultiPlace, `LivingHurtEvent` (mutable `ammount`), LivingAttack/LivingHeal, EntityJoinWorld, EntityInteract, AttackEntity, `PlayerEvent.BreakSpeed`, LivingDrops, AnvilUpdate, `CommandEvent`, LivingUpdate, `PlayerUseItemEvent.Start`, FillBucket, EntityItemPickup, `ExplosionEvent.Start`, GuiOpen, DrawBlockHighlight, `RenderGameOverlayEvent`.
- Not cancelable but mutable: `ItemTooltipEvent` (`toolTip`), `HarvestDropsEvent` (`drops`, `dropChance`) and `ExplosionEvent.Detonate` (the affected lists).
- Result-based: CheckSpawn, PlayerOpenContainer, FuelBurnTime, `OreGenEvent.GenerateMinable`, `PopulateChunkEvent.Populate`.

Limits in HBM:
- HBM's custom damage fires LivingAttack and LivingHurt (`EntityDamageUtil.java:128,228,263,390`).
- `Detonate` is posted only by `ExplosionNT.java:142` and the VNT entity processors (`EntityProcessorCross.java:75`, `EntityProcessorStandard.java:49`), not by nukes or the other engines. HBM never posts `Start` itself, but its roughly 94 vanilla `createExplosion`/`newExplosion` calls go through it (javap `World.newExplosion`).
- HBM ores come from an `IWorldGenerator` (`HbmWorld.java:49`) and post no `OreGenEvent`.
- There is no TE-tick event.
- The overlay handlers at `ModEventHandlerClient.java:416,450` use `receiveCanceled = true`, so cancelling cannot hide them; the L152 handler can be suppressed.

Register common handlers at H1, and keep handlers for client-only event classes in a client class.

### Config and JSON systems

- No HBM config or recipe loader reads from the jar. Jar reads that bypass the resource manager do exist (`NBTStructure.java:110`; `QMAWLoader.java:125` opens the mod file as a `ZipFile`; OC floppies through `FileSystem.fromClass`, `CompatHandler.java:86,201`), so fork-shipped defaults need fork code (constants, or code that reads a bundled resource or writes config files).
- Changing a code default reaches fresh installs only. `Configuration.get` keeps a stored value (javap), and `MachineDynConfig` applies every key in `hbmMachines.json` at L336 and rewrites all keys from current values (`MachineDynConfig.java:49-84`; its own comment at `:95-101`).
- Forcing a value means writing it after load. That silently ignores the user's file, which keeps showing the old value.
  - hbm.cfg values are public statics, except `MachineConfig.scaleRTGPower`/`doRTGsDecay` (protected, `MachineConfig.java:7-8`).
  - Machine statics set at H1 are overwritten at L336 on any install that already has `hbmMachines.json` (`MachineDynConfig.java:49-63`), so forcing them needs a write after L336. `TileEntitySteamEngine` has private and `TileEntityMachineFrackingTower` protected fields.
- `/ntmreload` re-runs `FluidContainerRegistry` (clear and register), `Fluids.reloadFluids`, `SerializableRecipe.initialize`, `ItemPoolConfigJSON`, `DamageResistanceHandler.init` and the logic-block initialisers (`CommandReloadRecipes.java:33-42`). Only the fluid and recipe listeners re-fire, so post-init overrides of ItemPool or damage resistance are lost. Change those defaults inline.
- What these systems cannot do: ship defaults from the jar, add hbm.cfg keys without editing `loadConfig` (L765), push a changed default to existing installs, keep post-init overrides of ItemPool or damage resistance across `/ntmreload`, or apply listener recipes to a set that has a live JSON.
- A live recipe JSON or a synced stream replaces a set and skips the listeners (`SerializableRecipe.java:132-147`). That covers fork additions, changes and removals alike (B-148, kept as designed in `docs/fix-roadmap.md`; the files are `hbmAssemblyMachine.json`, `AssemblyMachineRecipes.java:57`, and `hbmAnvil.json`, `AnvilRecipes.java:48`). Templates include listener output (`:152-158`), so operators can regenerate the live file from `_hbmAssemblyMachine.json` after an update.

### Subclass-and-replace

Avoid it for TEs and edit the TE inline instead. A swap needs three things:
- the same id string in `TileMappings.map` between L335 and L339 (two classes on one id throw, and a new id orphans placed machines);
- a Block change, because `ReactorResearch.java:29-30` hard-codes the TE class;
- the Torcherino blacklist for the subclass (`Compat.java:268-270` sends the class name).

The TESR carries over, because `getSpecialRendererByClass` recurses into superclasses (javap), and `src` has no exact-class checks. To change a block or item class, edit its constructor line in `ModBlocks`/`ModItems` inline. A swap from fork code needs a hook between `initializeBlock()` and `registerBlock()` (`ModBlocks.java:49-52`; `ModItems.java:104-107`) and copies upstream's fluent init chain, which drifts silently.

### Access transformer

Use `src/main/resources/META-INF/HBM_at.cfg` only so that fork code can reach private vanilla members without reflection. The file is 85 lines, vanilla-only and `public`-only, and changes access, never behaviour. It had one Space-side commit in 2026 (`c8351238`). Append at the end and run `./gradlew clean setupDecompWorkspace` (`:3`).

### Coremods and mixins: not now

- There is no infrastructure in the fork: no `IFMLLoadingPlugin`, `IClassTransformer` or `@Mixin` anywhere, and the manifest carries only `FMLAT` (`build.gradle:149-153`). UniMixins 0.3.1 and RetroFuturaGradle sit in the Gradle cache from other projects, but nothing here uses them.
- The owner's pack already runs UniMixins 0.3.1 (SpongePowered Mixin 0.8.7), so a mixin dependency would cost the owner no extra jar. The remaining costs: `FMLCorePlugin`/`FMLCorePluginContainsFMLMod` manifest keys and `-Dfml.coreMods.load` for a coremod (javap `CoreModManager`); for mixins, refmap generation that anatawa ForgeGradle 1.2 lacks.
- For HBM's own code the recommendation stands: edit the source. A patch on HBM code fails silently when upstream changes the target, whereas an inline edit fails loudly as a merge conflict. The same holds in reverse for other mods' patches on HBM code, which the owner's pack already has (section 7, third-party patches).
- Mixins remain an option only for vanilla, Forge or other-mod behaviour that has no event and no AT-able member. In that case prefer UniMixins together with a move to RetroFuturaGradle, as its own batch.

### Hiding or removing content

Hide rather than unregister: `setCreativeTab(null)` from `DBSItems`, `API.hideItem` from a fork `NEI*Config`, recipe removal, and a `PlaceEvent` cancel if placement must be blocked. Hiding a single meta variant needs an inline `getSubBlocks` edit. Never change `setBlockName`/`setUnlocalizedName`. Unregistering raises missing mappings. `event.get()` returns only the handler's own mod's list, but `getAll()` exposes every mod's, and `MissingMapping.remap`/`ignore` do not check ownership (javap). `handleMissingMappings` has 925 `ignoreMappings.add` lines and remaps only items; it has no block branch (B-046), so placed blocks vanish.

### Hardening `DBSRecipes`

Dispatch is a string literal (`DBSRecipes.java:43-44`) inside a single try/catch (`:42-49`).
- A class rename already breaks compilation and trips `check-fork.sh:83-88`. Dispatch fails silently only if the class survives while the registered set's simple name changes.
- A duplicate id is the real hazard. `GenericRecipes.register` appends to `recipeOrderedList` before it throws (`GenericRecipes.java:99-100`), so the catch leaves a stray entry and skips the rest of that set's fork recipes. Register each recipe in its own try after `set.recipeNameMap.containsKey(name)`.
- Change upstream recipes by mutating the existing `GenericRecipe` under its name: the fields are public, and `name` is `protected final` (`GenericRecipe.java:30-37`).
- Removal must touch five structures: `recipeOrderedList`, `recipeNameMap`, `nameToRecipeGlobal`, `blueprintPools` and `autoSwitchGroups` (`GenericRecipes.java:45-54`).
- Never reuse a removed name. A machine left on a missing name idles without a message (`ModuleMachineBase.java:177-178`, `:49-50`).

## 5. Persisted and synced IDs

| Registry | Assignment | Persisted / synced | Current max | Collision risk | Fork band (adopted 2026-09-26) |
|---|---|---|---|---|---|
| Fluid ids | built-ins positional (`Fluids.java:1173-1178`); JSON and `CompatFluidRegistry` explicit (`:1181-1185`) | tank NBT int (`FluidTank.java:256-261`), fluid item damage, `short` in pipette/RBMK; synced | 216 built-ins (0-215); in the owner's pack EnviroMine holds 7732-7751 (explicit, through `CompatFluidRegistry.registerFluid`) | high on the positional path; explicit ids overwrite without a check | 20000-20999 |
| `FluidType` names | strings | not by HBM itself (tanks store the id); in the owner's pack bobfluidtranslator turns each name into `bobfluidtranslator:<name>_fluid_block` block and item names in the world id map | n/a | a renamed or removed fluid leaves missing mappings on world load | never rename or remove; fork names with the `dbs_` prefix |
| `Mats` ids | explicit literals, semantic bands (`Mats.java:43-50`) | autogen/scrap item damage, slag NBT | 127 materials, max 24003 | silent overwrite (`NTMMaterial.java:47`) | 30000-32767 |
| `ItemEnumMulti`/`BlockEnumMulti` metas | enum ordinal | item damage / chunk meta (blocks max 16) | per enum | certain if both sides append; unknown metas wrap | fork-owned enums while merging |
| `EnumAmmo`/`EnumAmmoSecret` | ordinal | ammo item damage | 95 / 8 | certain if both append | fork ammo item while merging |
| `BulletConfig` ids | `configs.size()` in ctor and `clone()` (`BulletConfig.java:91-100,303-306`) | gun NBT `magtype<N>` (`MagazineSingleTypeBase.java:240-241`); DataWatcher 3 (`EntityBulletBaseMK4.java:106-117`) | read `configs.size()` at startup | high: every upstream addition shifts fork ids; a shifted id falls back to `acceptedBullets.get(0)` (`MagazineSingleTypeBase.java:40-46`) | pad to 2000, fork 2000+k |
| Weapon-mod ids | explicit, `HashBiMap.put` | gun NBT int arrays (`XWeaponModManager.java:238-333`) | 371 | silent replace | 9000-9999 |
| Satellite ids | `currentId++` (`XSatelliteRegistry.java:48-54`) at L292 | `sat_id_<i>` (`SatelliteSavedData.java:48,66`); PermaSync | 14 (0-13) | id 0 if added at L285; writing `idToClass` directly fails (`satelliteColors` is private) | explicit-id overload (upstream edit), 1000+ |
| Siege tiers | private `nextID++` (`SiegeTier.java:63-66`) at L295 | entity NBT `siegeTier`; DataWatcher | 9 (0-8), array of 100 | as satellites; fork tiers also join the random roll (`EntitySiegeCraft.java:328`) | explicit-id ctor (upstream edit), 90-99 |
| `SolarSystem.Body` ordinals | enum ordinal (`SolarSystem.java:309-321`) | `BlockOre` meta in chunks | 11 (0-10), THATMO commented out | high: 16-value meta cap | none without a plan; from 15 downwards |
| `CelestialBodyTrait` indices | list order, private `registerTrait` (`CelestialBodyTrait.java:31-51`) | sync only (`PermaSyncHandler.java:88-93,220`); saved by name (`SolarSystemWorldSavedData.java:104-116`) | 13 | none on disk | `dbs_` names (needs a public `registerTrait`) |
| Packet discriminators | `i++` (`PacketDispatcher.java:16-77`), cast to byte (`NetworkHandler.java:41-44`) | sync only | 0-28 | same byte overwrites; upstream's next takes 29 | own channel, else 100-127 |
| Mod-entity ids | list order from 0 (`EntityMappings.java:228-235`) | sync only (javap `FMLMessage$EntitySpawnMessage`); saves use `hbm.<name>` | 0-164 | startup crash on reuse | 1000+ |
| Spawn-egg ids | first free above `eggIDCounter` 499 (`ModEntityList.java:113,128-132`) | vanilla `spawn_egg` damage (`:124-126`) | 41 eggs, 500-540 if free | fork eggs at L285 shift all 41; after L361 they shift when upstream adds a mob | 4000+ |
| Potion ids | config ints (`PotionConfig.java:9-28`) | NBT `Id` and packet as byte (javap `PotionEffect`, `S1DPacketEntityEffect`) | 62-77 | vanilla: a duplicate replaces `Potion.potionTypes[id]`, and ids above 127 are lost on reload and arrive negative. Owner's pack: EndlessIDs resizes `Potion.potionTypes` to 65536 and stores ids above 127 as an int, and hodgepodge masks every `PotionEffect` id with `& 255`, so 128-255 work, 256 and up silently become another potion, and the resize branch at `HbmPotion.java:84-102` never runs; AntiIDConflict crashes on a duplicate (`crashIfConflict=true`) | 110-119 via `dbs.cfg` (free in the owner's pack, whose highest occupied id is 85) |
| Mold ids | explicit (`ItemMold.java:96-99`) | mold item damage | 26 molds, max 28 | silent overwrite | 1000+ |
| GenericRecipe names | strings | machine NBT `recipe<N>`; recipe sync | n/a | loud within a set, silent across sets | `<prefix>.dbs_<name>` |
| Registry, TE, entity, structure, QMAW names | strings | level.dat id map, chunk/entity/structure NBT | n/a | items, blocks, TEs, structures and jigsaw pieces fail loudly; entity and QMAW names do not | `dbs_` prefix |
| Biome / dimension ids | config ints | chunk biome arrays; `DIM<id>` folders | biomes 80-82 (`WorldConfig.java:175-177`) and 87-126 (`SpaceConfig.java:44-83`), both +12000 when EndlessIDs is loaded (`WorldConfig.java:352,356-358`, `SpaceConfig.java:125`; the offset changes defaults only, and the owner's `hbm.cfg` holds 12080-12082 and 12087-12126); dims 413_015-413_025 | upstream grows upwards | biomes 230-239 plus the same offset (12230-12239 in the owner's pack, free); dims 413_100+ |
| hbm.cfg keys | full string keys; number prefixes are cosmetic and already duplicated upstream | config file | n/a | identical full key only | separate `dbs.cfg` |
| Achievement / stat ids | strings | player stats | n/a | loud (`Duplicate stat id`) | `achievement.dbs_*` |
| GUI ids | none global; the int is passed through (`GUIHandler.java:16-27`) | neither | n/a | none | 0 |
| Network identity | `@Mod` version = `RefStrings.VERSION`, no `acceptableRemoteVersions` (`MainRegistry.java:109`) | FML handshake | `1.0.27 BETA (5778)`, same as JameH2; the fork's `DBSVersion.REVISION` appears only in the jar and `mcmod.info` version and the MOTD | plain NTM: Space clients join DBS servers only until DBS registers its first own block or item; after that FML rejects them ("Fatally missing blocks and items") | none needed: solo play (decision 2) |

Enum ordinals that are only synced (`EnumKeybind`, RBMK `ColumnType`, casing-ejector ids) are safe while client and server run the same jar. Fork keybinds use their own enum and packet.

## 6. Tracking fork changes for merge audits

- Markers: `// DBS fork hook` on hook lines, and `// DBS fix B-xxx` on fix lines (convention in `docs/fix-roadmap.md`, which allows the changed line or the line above). Prefer the changed line itself, so that a merge resolution which keeps the comment but takes upstream's code shows up in the diff. Proposal, not yet adopted: mark intentional inline content or balance changes with `// DBS C-xxx`. That needs a C-id register (for example the manifest below) and a check in `check-fork.sh`, which greps only `DBS fix <id>` (`:48-53`).
- Base: record the upstream base commit (`64b64130`) in a file, and have `check-fork.sh` print `git diff --ignore-cr-at-eol --stat <base> HEAD`. That works without an upstream remote.
- Manifest idea: `tools/fork-manifest.tsv` with one row per intentional change: id (`B-xxx` tracker fix, `C-xxx` content/balance), path, kind (inline/override/asset), and an anchor regex that must match the changed code, or for assets the expected `git hash-object` blob. `check-fork.sh` would fail on an anchor that no longer matches, a changed asset hash, or a delta path missing from the manifest. Under a hard fork, neither the manifest nor the marker check is needed: the tracker Status plus git history are enough.
- `check-fork.sh` gaps:
  - Presence-only: the marker check (`:48-53`) proves a comment exists, not that the fix is intact.
  - Scope: the conflict scan (`:36`) covers only `src/main/java`, `lang`, `tools`, `CLAUDE.md` and `docs`; use `git grep -nE '^(<{7}|>{7})( |$)'` over the whole tree.
  - CRLF is checked in the index only (`:44-45`).
  - The Status-parser problems are listed in `docs/fork-notes.md` (Tooling gaps).
  - It checks only the L285 and NEI hooks; each new hook needs an order check.

## 7. Traps

- `NetworkHandler` keeps its channels and codec in static fields that every constructor overwrites (`NetworkHandler.java:97-107`). The instance constructed last carries every send from both wrappers, and FML does not order `PacketDispatcher`'s class init (from `preInit`, L697) against PreLoad. A second instance therefore breaks either hbm's packets or the fork's. An unregistered message class is sent as discriminator 0 (`:56`).
- `NEIRegistry.listAllHandlers` returns early when `handlers` is non-empty (`NEIRegistry.java:16`); pre-filling it drops every upstream handler.
- `NetworkRegistry.registerGuiHandler` is a `put` per mod (javap). Calling it for `MainRegistry.instance` replaces hbm's `GUIHandler` (L333).
- Recipe listeners fire only in the defaults branch (`SerializableRecipe.java:148-154`). A live JSON or a synced stream silently drops fork recipe changes for that set.
- `DBSRecipes` has one catch for the whole batch, and `GenericRecipes.register` appends before its duplicate check (section 4).
- `BulletConfig` ids are positional, and two upstream configs are created at different times on client and dedicated server (section 3, Weapons). `EntityBulletBaseMK4.java:116` and `EntityBulletBeamBase.java:118` check `id > configs.size()` instead of `>=`, so an id equal to `configs.size()` passes the check and `configs.get` throws `IndexOutOfBoundsException`. This is tracked as B-361 (Phase 2), which also covers the unchecked index in `MagazineBelt`.
- Spawn-egg ids are first-free and persisted, so fork eggs at L285 renumber all 41 upstream eggs.
- Satellites and siege tiers created at L285 take id 0 and shift every upstream id (they register at L292 and L295).
- The item-renderer pass runs once, at L287. Anything bound or registered later gets no item renderer.
- Hazards written at L285 are overwritten by L296/L600 for the same key. An existing `hbmRadResist.json` replaces the whole rad-resistance table.
- Machine statics written before L336 are overwritten by `hbmMachines.json`, and changed code defaults never reach existing installs.
- `/ntmreload` resets fluid containers, fluids, recipes, ItemPool and damage resistance (`CommandReloadRecipes.java:33-42`).
- A fork fluid listener registered at L285 misses `Fluids.init`, and its traits override the user's `hbmFluidTraits.json`.
- With a second container, hbm's modId-less `@SidedProxy` (L115) is injected again.
- `FloppyDisk`'s constructor is package-private. QMAW pages with a duplicate `name` replace each other.
- Potion ids: on vanilla-limit installs, ids above 127 break saves and sync (byte storage), whatever `HbmPotion.registerPotion` allows. In the owner's pack (EndlessIDs plus hodgepodge) 128-255 work and 256 and up are silently masked to `id & 255` (section 5). Fork potions stay at 110-119, which works everywhere.
- Other mods in the owner's pack patch HBM members by name (next subsection). A fork override of `validate`, `invalidate` or `onChunkUnload` added to `TileEntityPylonBase` is silently replaced by ntmrba's merged copy; renaming a member that EndlessIDs, ntmrba's invoker or an EnviroMine `@Shadow` targets crashes the game at load.
- Lang keys defined in both `assets/hbm` and `assets/dbs` resolve differently on client and server.
- NEI finds only classes that implement `IConfigureNEI` directly.
- Unregistering blocks loses placed blocks (no block remap branch, B-046).

### Third-party patches on HBM code in the owner's pack

Six mods in the owner's pack patch HBM classes by name, and OpenComputers injects into every component TE. The list was checked on 2026-09-26 against the jars in the pack and the `Mixing ...`, `Patching method ...` and `Successfully injected component logic ...` lines of `fml-client-latest.log`; all targets exist at HEAD. A fork edit that renames, retypes or moves one of these members still compiles; it fails in the game. Rule: before renaming or retyping a member listed here, or restructuring the code a patch anchors on, check this list, and launch-test with the addon installed.

| Addon | Patched HBM member | Patch | Effect of a mismatch |
|---|---|---|---|
| EndlessIDs 1.7.4 | `ChunkRadiationHandlerPRISM.updateSystem` (`ChunkRadiationHandlerPRISM.java:179`, call at `:222`) | `@Redirect` of `ExtendedBlockStorage.getBlockLSBArray()`, `require = 1`, required config; the outer class is loaded and mixed at every startup, even with PRISM off | crash at launch if the method is renamed or loses its `getBlockLSBArray()` call |
| EndlessIDs 1.7.4 | `ChunkRadiationHandlerPRISM$SubChunk.rebuild` (`:361`, call at `:391`) | the same redirect, `require = 1` | crash when `SubChunk` first loads (in practice with PRISM on) |
| ntmrba 1.3 | `BlockDummyable.getMetaForCore` (`:317`), `getDirModified` (`:327`), `checkRequirement` (`:331`) | `@Invoker` accessor, required config | crash when `BlockDummyable` loads if a signature changes; add overloads instead |
| ntmrba 1.3 | `TileEntityPylonBase` `validate`, `invalidate`, `onChunkUnload` | unannotated methods merged into the class, which defines none of them today; each calls `TileEntityCableBaseNT`'s version, then updates ntmrba's client wire list | a fork override added there is silently replaced, on the integrated server too; put pylon lifecycle logic in `TileEntityCableBaseNT` or in helpers called from `updateEntity` |
| ntmrba 1.3 | `RenderPylonBase.renderLinesGeneric` (`:26`) | `@WrapMethod`, no `require` | the wrap stops silently; with `OPTIMIZE_WIRES` and `OPTIMIZE_TILEENTITIES` both on (as in the owner's `ntmRBA_Client.json`), the original is skipped and ntmrba draws wires from its own list, so wire-drawing changes there are not visible |
| ntmrba 1.3 | `RenderDoorGeneric.renderTileEntityAt` (its `glBlendFunc` call) | wrap, no `require` | the patch stops silently |
| ntmrba 1.3 | both `ParticleRadiationFog` constructors (`CTOR_HEAD`) and `renderParticle` (`WrapOperation`) | no `require` | the optimisation stops silently |
| EnviroMine 1.3.148 (Kotmatross edition) | `RadiolysisRecipes.registerRadiolysis()` (`:49`) and `private static Map radiolysis` (`:21`) | HEAD inject that adds five `RADIOACTIVE_*_WATER` -> `WATER` entries through a `@Shadow` of the field; required config | renaming or retyping the method or the field stops startup. A `clear()` at the start of `registerRadiolysis` runs after the HEAD callback and silently drops EnviroMine's entries, so clear in the caller instead (for B-365, in the proposed `CrackingRecipes.registerPost()` override before it calls `registerRadiolysis()`). B-365's fix text in `docs/known-bugs.md` still says to clear inside `registerRadiolysis` and needs this change |
| EnviroMine | `TileEntityMachineDrain.updateEntity` (the `FT_Polluting.pollute` call, `:59`) and `public FluidTank tank` (`:34`) | INVOKE inject plus `@Shadow tank`; the mixin also adds `api.hbm.fluid.IFluidStandardReceiver`, `IBufPacketReceiver` and `IFluidCopiable` to the class | deleting `api/hbm/fluid/IFluidStandardReceiver` (the HbmMods merge does) fails startup with `NoClassDefFoundError` when `TileMappings.java:127` loads the drain; renaming or retyping `tank` crashes at load; replacing the `pollute` call stops the drain radiation silently |
| EnviroMine | `RenderFluidTank.getTextureFromType` (`:84`) | HEAD inject, no `require` | a moved injection point stops the patch silently; the same holds for the next six rows |
| EnviroMine | `FluidContainerRegistry.register` | TAIL inject | as above |
| EnviroMine | `LiquefactionRecipes.getOutput` (`:77`) | HEAD inject | as above |
| EnviroMine | `TileEntityWasteDrum.updateEntity` (the `ForgeDirection.VALID_DIRECTIONS` read, `:198`) | FIELD inject | as above |
| EnviroMine | `ExplosionThermo.freezeDest` (`:121`) | HEAD inject | as above |
| EnviroMine | `Landmine.isWaterAbove` (`:139`, the `Blocks.water` read) | FIELD inject | as above |
| EnviroMine | `TileEntityReactorResearch.blocksRad` (`:267`, fork-restored class) | HEAD inject | as above |
| Energy Control 0.3.7 | `updateEntity` of `TileEntityChungus`, `TileEntityCondenser`, `TileEntityMachineTurbine`, `TileEntityMachineLargeTurbine`, `TileEntityMachineBattery`, `TileEntityMachineFENSU` | hooklib hooks at method entry that read `tanks`, `power`, the `FT_Coolable` fields and `FluidTank` getters. Each returns at once unless an Energy Control card or sensor monitors that tile; for monitored Chungus, Turbine and LargeTurbine it calls `tanks[1].setTankType(coolsTo)` every server tick | a removed or moved target method: the hook stops firing silently (readings freeze). A renamed or retyped member the hook reads: `NoSuchFieldError`/`NoSuchMethodError`, a "Ticking block entity" crash, only for monitored machines |
| Energy Control 0.3.7 | `TileEntityCore.burn(long)` (entry, `:236`), `TileEntityMachineTurbineGas.makePower(double,int)` (exit, `:367`) | hooklib hooks that read `tanks`, plus `temp`/`tempIdle` and `FT_Combustible.getCombustionEnergy()` for the gas turbine | as the row above |
| bobfluidtranslator 2.1.12 | `IFluidStandardReceiverMK2`, `IFluidStandardSenderMK2`, `IFluidStandardTransceiverMK2`, `IFluidReceiverMK2`, `IFluidProviderMK2`, legacy `api.hbm.fluid.IFluidStandardTransceiver`; `TileEntityProxyBase`; `TileEntityPipeBaseNT`/`TileEntityPipeExhaust` (`updateEntity` wrapped); a diamond-conflict pass over `com.hbm.tileentity.*` | coremod ASM transformer (`UniversalFluidTransformer`) that adds Forge `IFluidHandler` and default trampolines | a two-way fluid tile outside the pass inherits conflicting defaults, and Forge callers get `IncompatibleClassChangeError` (section 3, Fluids) |
| bobfluidtranslator | `Library.canConnectFluid` (`Library.java:80`), `FluidTank.setType(int,int,ItemStack[])` (`:142`) and `setTankType(FluidType)` (`:61`, a `NONE` guard), `TileEntityMachineCompressorBase.setupTanks()` (`:180`), `IFluidStandardSenderMK2.tryProvide(FluidType,int,World,int,int,int,ForgeDirection)` (renamed `tryProvide$hbm` and wrapped) | ASM, matched by descriptor | a changed descriptor: that patch is skipped with a message in bobfluidtranslator's own `java.util.logging` output, not in `fml-client-latest.log`; the passive Forge bridges keep working |
| bobfluidtranslator | `NodeNet.receiverEntries`/`providerEntries` (`NodeNet.java:21-22`, read as `HashMap`); the inherited default `IFluidStandardSenderMK2.tryProvide(FluidTank,World,int,int,int,ForgeDirection)` (Universal Tank) | direct field reads and calls, not patches | renaming or retyping the fields: every Forge fill, drain or `getTankInfo` on an HBM duct logs an error and does nothing. Deleting that default: a Universal Tank in SENDER mode crashes its tick |
| Angelica 2.2.20 | `SkyProviderCelestial.render` (`:126`), `renderSun` (`:710`), `renderSunset` (`:513`), `renderCelestials` (`:913`), `renderAtmosphereGlow` (`:1415`) | mixin with call-position points (`glDisable`, `glCallList` ordinals 1/2, `glRotatef` ordinal 2, `Tessellator.draw` slices), no `defaultRequire`; active with `fixNTMSpace=true` in `angelica-compat.cfg` | a changed `renderSun` descriptor, or a changed order or count of those GL calls, drops the shader-pass fixes or moves them to the wrong call without an error; re-check the sky with an Iris shader pack |
| Angelica 2.2.20 | `SkyProviderOrbit.render`, `SkyProviderLaytheSunset.renderSunset` | as above | as above |
| OpenComputers 1.12.62 (GTNH) | every class whose constant pool holds the Utf8 `li/cil/oc/api/network/SimpleComponent` (62 HBM TEs today) | `ClassTransformer` wraps `validate`, `invalidate`, `onChunkUnload`, `readFromNBT` and `writeToNBT` and adds the component logic | a second injection into a subclass recurses without bound (`StackOverflowError`); a non-TE class gets a WARN and a login chat warning (section 3, OC components) |

Addons also link to HBM members without patching them, and those links fail only at runtime too: EnviroMine reads the public fields of about 30 machine TEs, about 80 `ModItems` fields and several Space classes; Energy Control's `CrossHBM` reads machine and tank state; bobfluidtranslator calls the fluid network and container API; effortless implements `IBatteryItem`; Eln reads `IFluidUserMK2.getAllTanks` by reflection; bogosorter names the crate containers. The latest client log shows no linkage error against HBM. The pending HbmMods delta breaks four of these addons (section 1, decision 1): ntmrba's two light TEs implement only `IControlReceiver.receiveControl(NBTTagCompound)`, which upstream deletes (`AbstractMethodError` when their GUI sends settings); Energy Control's Kit Assembler calls the default `IEnergyReceiverMK2.trySubscribe(World,int,int,int,ForgeDirection)` every tick; bobfluidtranslator reads the `NodeNet` maps and calls `tryProvide`, both changed upstream; EnviroMine's drain mixin needs `api/hbm/fluid`. Keep compatibility shims through that merge: bridge defaults for both `receiveControl` overloads, the old `trySubscribe` and `tryProvide(FluidTank,...)` defaults with their exact descriptors, the `HashMap` types of the `NodeNet` fields, and a stub `api.hbm.fluid.IFluidStandardReceiver` (or `B:005_MixinTileEntityMachineDrain=false` in `config/enviromine/mixins.cfg`). Do not delete them in a later cleanup while the addons are installed.

## 8. Open questions for the owner

Question numbers are kept, because other notes cite them.

Answered on 2026-09-26:
- 1, merging: JameH2's branch keeps being merged (decision 1).
- 2, container: fork content stays inside `hbm`; client rejection is moot, because the owner plays single-player (decision 2). The fork's own version identity is `DBSVersion.REVISION` (section 1), not a change to `RefStrings.VERSION`.
- 3, bands: adopted as listed in decision 3 and section 5.
- 4, small upstream API edits where no explicit-id or add path exists (`XSatelliteRegistry`, `SiegeTier`, `CelestialBodyTrait.registerTrait`, SILEX/radiolysis add methods, `FloppyDisk`): allowed under the 2026-09-25 edit policy. For radiolysis, keep EnviroMine's patch in mind (section 7, third-party patches).
- 7, live recipe JSON: the owner's instance has none (`config/hbmRecipes` holds only `_` templates, and the log shows the defaults registered for `hbmAssemblyMachine.json` and `hbmAnvil.json`), so the listeners fire and B-148 is moot for the owner. If a live file is ever added, regenerate it from the `_` template after an update: B-148 and the roadmap's "Design kept" list keep the listeners out of the live-JSON branch. Reopening that needs an inline change at `SerializableRecipe.java:144-147` plus duplicate guards for every set the listener touches: the per-recipe `recipeNameMap.containsKey` check for `GenericRecipes`, and an input/output comparison for `AnvilRecipes.constructionRecipes`, which has no name key (otherwise a template-derived JSON gets the seven fuel-plate recipes twice).
- 9, fork guns: the bullet base 2000 (padding `BulletConfig.configs`) is part of the adopted bands. Keying ammo by name in the magazine code remains a possible later rework.
- 11, identical jar: yes, solo play runs one jar on the integrated server, so the sync-only registries need no further protection.
- 12, remotes: `upstream` and `hbm` were added read-only and fetched (decision 1).

Still open:
- 5, textures and models: under `assets/dbs`, or under `assets/hbm` with `dbs_` file names? A separate fork lang file or `sounds.json` needs `assets/dbs`; QMAW pages and floppies must stay under `assets/hbm`.
- 6, balance: should balance changes override values already in the owner's `hbm.cfg`/`hbmMachines.json`, or only change defaults for fresh installs?
- 8, removal: for unwanted content, is hiding (tab, NEI, recipes, placement) enough, or is true removal with missing-mapping handling needed?
- 10, patches: is there vanilla, Forge or OC behaviour to change that has no event? If not, the fork should not take on a coremod or mixin dependency. UniMixins is already in the owner's pack (section 4), which lowers the cost of a mixin but not the case against patching HBM's own code.
