# Known bugs and defects in upstream code

Tracker for defects found by the 2026-09-24 code survey (19 subsystem readers, each adversarially verified) and re-verified item by item against the tree at `22c9c89c`. Line numbers are from that snapshot; class and method names are the stable anchors. Everything here is upstream (HbmMods / NTM: Space) code. Policies were re-triaged on 2026-09-24 after `CONTRIBUTING.md` was removed (`ea346f74`): DBS is a personal fork with no upstream pull requests, so upstream's rules (no refactors, no AI-authored code) no longer influence what gets fixed; only benefit and merge surface do. Each entry's `Plan:` line names its phase or decision row in `docs/fix-roadmap.md`.

How to use: pick from "Fix in the fork" first; each item there is safe to change without altering balance or saves. "Needs a decision" items change something players can notice; decide per item and record it. Update the **Status** field when acting (`open`, `fixed <commit>`, `fixed upstream <hash>` for a ported HbmMods fix, `wontfix <why>`). Fixes go through the fork-owned package or minimal, marked lines per `CLAUDE.md`; every fix is built and tested locally before merging.

Severity: **crash** (exception or hang reachable in normal play) · **save data** (NBT/persistence wrong) · **gameplay** (wrong behaviour players can hit) · **silent misconfig** (config or registration silently ignored) · **leak / perf** · **dead code** (misleading leftovers, unreachable features).

## Summary

| Policy | crash | save data | gameplay | silent misconfig | leak / perf | dead code | total |
|---|---|---|---|---|---|---|---|
| Fix in the fork | 21 | 4 | 69 | 57 | 15 | 54 | 220 |
| Needs a decision | 7 | 2 | 26 | 24 | 10 | 18 | 87 |
| Leave | 1 | 0 | 5 | 6 | 1 | 10 | 23 |
| **All** | **29** | **6** | **100** | **87** | **26** | **82** | **330** |

Reviewed and rejected (kept so they are not rediscovered): 22, listed at the end.

## Fix in the fork

| ID | Severity | Area | Location | Title | Plan | Status |
|---|---|---|---|---|---|---|
| B-002 | crash | core | `src/main/java/com/hbm/potion/HbmPotion.java:86-99` | HbmPotion.registerPotion sizes potionTypes to max(256, id) and swallows errors | Phase 2 | open |
| B-017 | crash | api | `src/main/java/api/hbm/fluidmk2/FluidNetMK2.java:24-25,45-49` | FluidNetMK2 pressure arrays are fixed at HIGHEST_VALID_PRESSURE+1 = 6 | Phase 5 | open |
| B-037 | crash | blocks-generic | `src/main/java/com/hbm/blocks/fluid/CoriumFluid.java:23-29` | CoriumFluid returns icons of never-instantiated CoriumBlock (null) | Phase 2 | open |
| B-038 | crash | blocks-generic | `src/main/java/com/hbm/items/block/ItemModSlab.java:14-31` | ItemModSlab hard-codes slab pairs; unknown slab yields null ItemSlab refs | Phase 8 | open |
| B-066 | crash | te-special | `src/main/java/com/hbm/tileentity/machine/rbmk/TileEntityRBMKControlAuto.java:117-127` | TileEntityRBMKControlAuto: null function breaks serialize/deserialize symmetry | Phase 1 | open |
| B-067 | crash | te-special | `src/main/java/com/hbm/tileentity/machine/rbmk/TileEntityRBMKControlAuto.java:134-135` | TileEntityRBMKControlAuto.receiveControl mods function index by RBMKColor count | Phase 1 | open |
| B-093 | crash | networks | `src/main/java/com/hbm/tileentity/network/TileEntityPylonBase.java:80-82` | TileEntityPylonBase.addConnection dereferences a possibly null PowerNode | Phase 2 | open |
| B-094 | crash | networks | `src/main/java/com/hbm/uninos/UniNodespace.java:52-56` | UniNodespace.destroyNode(World, GenNode) NPEs when the world has no node map | Phase 2 | open |
| B-194 | crash | hazards | `src/main/java/com/hbm/handler/atmosphere/ChunkAtmosphereHandler.java:243-256` | ChunkAtmosphereHandler register/unregisterAtmosphere lack a null guard | Phase 6 | open |
| B-195 | crash | hazards | `src/main/java/com/hbm/saveddata/SatelliteSavedData.java:48-49` | SatelliteSavedData.readFromNBT NPEs on an unknown satellite id | Phase 2 | open |
| B-218 | crash | space | `src/main/java/com/hbm/world/feature/BedrockOre.java:56-60` | BedrockOre.generateAuto NPEs for a Body without a bedrock table | Phase 2 | open |
| B-247 | crash | render | `src/main/java/com/hbm/render/anim/AnimationLoader.java:40-45` | AnimationLoader: missing JSON returns null, malformed JSON kills client | Phase 6 | open |
| B-248 | crash | render | `src/main/java/com/hbm/render/block/RenderISBRHUniversal.java:12-17` | RenderISBRHUniversal casts block unconditionally | Phase 6 | open |
| B-249 | crash | render | `src/main/java/com/hbm/render/icon/TextureAtlasSpriteMutatable.java:24-25` | TextureAtlasSpriteMutatable crashes stitching when base texture is missing | Phase 6 | open |
| B-251 | crash | render | `src/main/java/com/hbm/render/loader/HFRWavefrontObjectVBO.java:39-47` | HFRWavefrontObjectVBO crashes client startup on quad or normal-less OBJ | Phase 6 | open |
| B-252 | crash | render | `src/main/java/com/hbm/render/util/ObjUtil.java:58-130` | ObjUtil.renderWithIcon NPEs on OBJ without UV coordinates | Phase 6 | open |
| B-265 | crash | net-util | `src/main/java/com/hbm/packet/toserver/KeybindPacket.java:42` | KeybindPacket.Handler indexes EnumKeybind.values() with an unchecked client int | Phase 2 | open |
| B-267 | crash | net-util | `src/main/java/com/hbm/util/BufferUtil.java:104,114` | BufferUtil.writeNBT stores the compressed length as a short | Phase 2 | open |
| B-306 | crash | oc | `src/main/java/com/hbm/handler/CompatHandler.java:196-223` | CompatHandler.init adds a recipe for a floppy whose item may be null | Phase 2 | open |
| B-307 | crash | oc | `src/main/java/com/hbm/tileentity/machine/rbmk/TileEntityRBMKControlManual.java:161-162` | RBMKControlManual.getColor NPEs when no colour is set | Phase 1 | open |
| B-308 | crash | oc | `src/main/java/com/hbm/tileentity/turret/TileEntityTurretBaseArtillery.java:86-88` | TurretBaseArtillery.getCurrentTarget throws when target queue is empty | Phase 2 | open |
| B-003 | save data | core | `src/main/java/com/hbm/extprop/HbmLivingProps.java:496-498` | ContaminationEffect.load reads time/maxTime/ignoreArmor from the parent compound | Phase 2 | open |
| B-055 | save data | te-machine | `src/main/java/com/hbm/inventory/fluid/tank/FluidTank.java:264-268` | FluidTank.readFromNBT zeroes fill when `<key>_max` is absent | Phase 2 | open |
| B-068 | save data | te-special | `src/main/java/com/hbm/tileentity/machine/TileEntityZirnoxDestroyed.java:30-36` | TileEntityZirnoxDestroyed NBT key mismatch: reads "fire", writes "onFire" | Phase 2 | open |
| B-176 | save data | entities | `src/main/java/com/hbm/entity/EntityMappings.java:82-86` | Duplicate entity name entity_cloud_rainbow for two classes | Phase 2 | open |
| B-004 | gameplay | core | `src/main/java/com/hbm/main/MainRegistry.java:367-377` | ForgeChunkManager callback returns after the first NORMAL ticket | Phase 6 | open |
| B-019 | gameplay | api | `src/main/java/api/hbm/energymk2/IEnergyReceiverMK2.java:70-82` | IEnergyReceiverMK2.tryUnsubscribe uses createNode() instead of getNode (no-op) | Phase 5 | open |
| B-024 | gameplay | api | `src/main/java/com/hbm/tileentity/machine/TileEntityXenonThruster.java:59-61` | TileEntityXenonThruster sets hasRegistered even when not facing prograde | Phase 6 | open |
| B-039 | gameplay | blocks-generic | `src/main/java/com/hbm/blocks/fluid/CoriumFinite.java:56-64` | CoriumFinite.updateTick deletes the block on ClassCastException | Phase 6 | open |
| B-040 | gameplay | blocks-generic | `src/main/java/com/hbm/blocks/fluid/GenericFiniteFluid.java:19-45` | GenericFiniteFluid static icons shared by concrete_liquid and corium_block | Phase 6 | open |
| B-041 | gameplay | blocks-generic | `src/main/java/com/hbm/blocks/fluid/GenericFluidBlock.java:68-70` | GenericFluidBlock damage only applied for sulfuric_acid_block | Phase 6 | open |
| B-050 | gameplay | blocks-machine | `src/main/java/com/hbm/blocks/BlockDummyable.java:757-760` | Placement preview rotates with `facing` while the footprint uses getDirModified | Phase 6 | open |
| B-051 | gameplay | blocks-machine | `src/main/java/com/hbm/blocks/machine/MachineCoker.java:34` | 14 blocks pass the clicked side as GUI id to standardOpenBehavior | Phase 6 | open |
| B-056 | gameplay | te-machine | `src/main/java/com/hbm/inventory/UpgradeManagerNT.java:57-66` | UpgradeManagerNT.mutexType never reset; re-inserted mutex upgrade ignored | Phase 6 | open |
| B-057 | gameplay | te-machine | `src/main/java/com/hbm/tileentity/TileEntityLoadedBase.java:95-101` | networkPackNT dedupe without getDescriptionPacket delays first sync up to 1 s | Phase 8 | open |
| B-058 | gameplay | te-machine | `src/main/java/com/hbm/tileentity/machine/TileEntityMachineElectricFurnace.java:180` | Electric furnace can take ~60 ticks to see a new cable | Phase 6 | open |
| B-072 | gameplay | te-special | `src/main/java/com/hbm/tileentity/bomb/TileEntityCompactLauncher.java:291` | sendCommandEntity passes posX for both x and z in two launchers | Phase 6 | open |
| B-073 | gameplay | te-special | `src/main/java/com/hbm/tileentity/bomb/TileEntityLaunchPadLarge.java:99` | TileEntityLaunchPadLarge tests `erector == 1F` inside the lift-extension branch | Phase 6 | open |
| B-074 | gameplay | te-special | `src/main/java/com/hbm/tileentity/bomb/TileEntityLaunchPadRocket.java:627-630` | TileEntityLaunchPadRocket.getDestination OC callback has inverted hasDrive() | Phase 6 | open |
| B-078 | gameplay | te-special | `src/main/java/com/hbm/tileentity/machine/TileEntityReactorResearch.java:119-124` | Research reactor: canExtractItem uses fuelMap.containsValue(stack), always false | Phase 1 | open |
| B-079 | gameplay | te-special | `src/main/java/com/hbm/tileentity/machine/TileEntityReactorResearch.java:89` | Research reactor: isItemValidForSlot only accepts slot 0 (`i < 12 && i <= 0`) | Phase 1 | open |
| B-080 | gameplay | te-special | `src/main/java/com/hbm/tileentity/machine/rbmk/RBMKDials.java:261-266` | RBMKDials.getSurgeMod client fallback reads the passive-cooling dial | Phase 1 | open |
| B-110 | gameplay | items | `src/main/java/com/hbm/main/ModEventHandlerClient.java:791` | Armor-mod tooltip loop stops at 8, hiding the battery slot mod | Phase 6 | open |
| B-119 | gameplay | weapons | `src/main/java/com/hbm/entity/projectile/EntityArtilleryRocket.java:34-35` | EntityArtilleryRocket keeps a raw Entity target reference | Phase 6 | open |
| B-120 | gameplay | weapons | `src/main/java/com/hbm/items/weapon/sedna/factory/Lego.java:128-129` | LAMBDA_STANDARD_CLICK_SECONDARY reads mode of config 0, writes ctx.configIndex | Phase 6 | open |
| B-123 | gameplay | weapons | `src/main/java/com/hbm/items/weapon/sedna/mods/WeaponModCaliber.java:16-39` | WeaponModCaliber returns shared static DUMMY_* magazines mutated per eval | Phase 8 | open |
| B-124 | gameplay | weapons | `src/main/java/com/hbm/items/weapon/sedna/mods/XWeaponModManager.java:284-296` | restoreMagState only restores receiver 0 after mod changes | Phase 8 | open |
| B-125 | gameplay | weapons | `src/main/java/com/hbm/items/weapon/sedna/mods/XWeaponModManager.java:308` | Weapon-mod install keys the gun with a non-singular ComparableStack | Phase 6 | open |
| B-137 | gameplay | recipes | `src/main/java/com/hbm/inventory/FluidContainerRegistry.java:96-97` | Disperser/gland containers registered by loop index instead of fluid id | Phase 6 | open |
| B-154 | gameplay | gui | `src/main/java/com/hbm/handler/nei/CustomMachineHandler.java:28` | CustomMachineHandler lacks ICompatNHNEI, invisible to GTNH NEI catalysts | Phase 6 | open |
| B-155 | gameplay | gui | `src/main/java/com/hbm/inventory/container/ContainerBarrel.java:55-56` | ContainerBarrel tile-to-player merge skips player slot 6 | Phase 3 | open |
| B-156 | gameplay | gui | `src/main/java/com/hbm/inventory/container/ContainerElectrolyserMetal.java:57-58` | ContainerElectrolyserMetal shift-click bounds off by one | Phase 3 | open |
| B-157 | gameplay | gui | `src/main/java/com/hbm/inventory/container/ContainerMachineCryoDistill.java:61-71` | ContainerMachineCryoDistill merges into its own tile slots | Phase 3 | open |
| B-158 | gameplay | gui | `src/main/java/com/hbm/inventory/container/ContainerMachineCyclotron.java:66-67` | ContainerMachineCyclotron shift-click bounds off by four | Phase 3 | open |
| B-159 | gameplay | gui | `src/main/java/com/hbm/inventory/container/ContainerMachineDiesel.java:45-51` | ContainerMachineDiesel shift-click bounds off by one | Phase 3 | open |
| B-160 | gameplay | gui | `src/main/java/com/hbm/inventory/container/ContainerMachineKeyForge.java:54-60` | ContainerMachineKeyForge / SatLinker route tile slots 1-2 into slot 0 | Phase 3 | open |
| B-161 | gameplay | gui | `src/main/java/com/hbm/inventory/container/ContainerMachineMilkReformer.java:61-68` | ContainerMachineMilkReformer bounds off by two and empty battery range | Phase 3 | open |
| B-162 | gameplay | gui | `src/main/java/com/hbm/inventory/container/ContainerMachineOilWell.java:55-62` | ContainerMachineOilWell shift-click off by one; upgrade range hits player slot | Phase 3 | open |
| B-163 | gameplay | gui | `src/main/java/com/hbm/inventory/container/ContainerMachineReactorBreeding.java:46-47` | ContainerMachineReactorBreeding shift-click off-by-one (index <= 2 for 2 slots) | Phase 1 | open |
| B-164 | gameplay | gui | `src/main/java/com/hbm/inventory/container/ContainerMachineVacuumDistill.java:72-73` | ContainerMachineVacuumDistill treats tile slot 11 as a player slot | Phase 3 | open |
| B-165 | gameplay | gui | `src/main/java/com/hbm/inventory/container/ContainerNukeAntimatter.java:50-51` | ContainerNukeAntimatter: slots 3-4 not shift-clickable, player->bomb never works | Phase 3 | open |
| B-166 | gameplay | gui | `src/main/java/com/hbm/inventory/container/ContainerNukeFstbmb.java:47-48` | ContainerNukeFstbmb treats player slot 2 as a bomb slot | Phase 3 | open |
| B-167 | gameplay | gui | `src/main/java/com/hbm/inventory/container/ContainerPADetector.java:55-56` | ContainerPADetector / ContainerPASource shift-click bounds off by one | Phase 3 | open |
| B-168 | gameplay | gui | `src/main/java/com/hbm/inventory/container/ContainerReactorResearch.java:56-57` | ContainerReactorResearch shift-click off-by-one (index <= 12 for 12 slots) | Phase 1 | open |
| B-178 | gameplay | entities | `src/main/java/com/hbm/entity/EntityMappings.java:260-270` | EntityMappings.addSpawn appends a duplicate after adjusting an entry | Phase 6 | open |
| B-179 | gameplay | entities | `src/main/java/com/hbm/entity/effect/EntityFalloutRain.java:38-42` | EntityFalloutRain(World, int maxAge) ignores maxAge and frustum flag | Phase 6 | open |
| B-180 | gameplay | entities | `src/main/java/com/hbm/entity/logic/EntityNukeExplosionMK5.java:63-65` | MK5 explosion grants the Manhattan achievement to every player in the world | Phase 6 | open |
| B-181 | gameplay | entities | `src/main/java/com/hbm/entity/logic/EntityNukeExplosionMK5.java:68-80` | MK5 radiate() runs on ticks 1-9, not the first 10 | Phase 6 | open |
| B-196 | gameplay | hazards | `src/main/java/com/hbm/handler/ae2/MassStorageMEInventory.java:34-53` | AE2 storage handlers truncate long stack sizes to int | Phase 6 | open |
| B-197 | gameplay | hazards | `src/main/java/com/hbm/handler/microblocks/MicroBlocksCompatHandler.java:78` | MicroBlocksCompatHandler skips metadata 15 | Phase 6 | open |
| B-198 | gameplay | hazards | `src/main/java/com/hbm/handler/pollution/PollutionHandler.java:369-387` | PollutionHandler.decorateMob heals mobs on every LivingSpawnEvent subtype | Phase 6 | open |
| B-199 | gameplay | hazards | `src/main/java/com/hbm/handler/radiation/ChunkRadiationHandlerPRISM.java:331-371` | PRISM SubChunk uses cX for the Z chunk origin | Phase 6 | open |
| B-201 | gameplay | hazards | `src/main/java/com/hbm/saveddata/satellites/SatelliteMiner.java:56-58` | SatelliteMiner.getCargoForItem looks up an Item-keyed map with a ComparableStack | Phase 6 | open |
| B-219 | gameplay | space | `src/main/java/com/hbm/dim/ChunkProviderCelestial.java:414` | ChunkProviderCelestial.populate clears isHellWorld and never restores it | Phase 6 | open |
| B-220 | gameplay | space | `src/main/java/com/hbm/dim/WorldProviderCelestial.java:693-698` | WorldProviderCelestial.canRespawnHere depends on a global static flag | Phase 8 | open |
| B-221 | gameplay | space | `src/main/java/com/hbm/dim/WorldProviderCelestial.java:723-725` | Sleeping on a planet resets local time to 0 instead of next morning | Phase 6 | open |
| B-222 | gameplay | space | `src/main/java/com/hbm/dim/duna/biome/BiomeGenDunaPlains.java:76` | Seventeen celestial biomes use Math.random() in genTerrainBlocks | Phase 6 | open |
| B-223 | gameplay | space | `src/main/java/com/hbm/dim/thatmo/WorldProviderThatmo.java:29-31` | WorldProviderThatmo passes dimensionId as rainfall and rebuilds its biome | Phase 6 | open |
| B-227 | gameplay | space | `src/main/java/com/hbm/world/gen/NTMWorldGenerator.java:283-304` | NTMWorldGenerator global hasPopulationEvent flag suppresses planet structures | Phase 6 after D23 | open |
| B-253 | gameplay | render | `src/main/java/com/hbm/main/ModEventHandlerClient.java:354-371` | Hotbar animation expiry runs ungated in RenderGameOverlayEvent.Pre | Phase 6 | open |
| B-255 | gameplay | render | `src/main/java/com/hbm/render/anim/BusAnimationSequence.java:83-87` | BusAnimationSequence.holdUntil wrong when animation speed is not 1 | Phase 8 | open |
| B-268 | gameplay | net-util | `src/main/java/com/hbm/packet/PermaSyncHandler.java:240-247` | PermaSyncHandler CBT section aborts the whole packet on any exception | Phase 8 | open |
| B-269 | gameplay | net-util | `src/main/java/com/hbm/packet/toclient/HbmAnimationPacket.java:88` | HbmAnimationPacket and PlayerInformPacket handlers swallow every exception | Phase 6 | open |
| B-270 | gameplay | net-util | `src/main/java/com/hbm/util/BobMathUtil.java:45,55` | BobMathUtil.max(float...)/max(double...) seed with MIN_VALUE (smallest positive) | Phase 6 | open |
| B-271 | gameplay | net-util | `src/main/java/com/hbm/util/BobMathUtil.java:64` | BobMathUtil.safeClamp tests `val == Double.NaN` (always false) | Phase 6 | open |
| B-273 | gameplay | net-util | `src/main/java/com/hbm/util/Tuple.java:236` | Tuple.Quintet.equals compares v against other.w | Phase 6 | open |
| B-274 | gameplay | net-util | `src/main/java/com/hbm/wiaj/JarScript.java:146,148` | JarScript.ffwTarget/freeRun are static (one fast-forward at a time) | Phase 6 | open |
| B-310 | gameplay | oc | `src/main/java/com/hbm/tileentity/machine/TileEntityMachineLargeTurbine.java:316-339` | LargeTurbine methods() lists getPower but invoke() has no case | Phase 4 | open |
| B-312 | gameplay | oc | `src/main/java/com/hbm/tileentity/machine/TileEntityReactorResearch.java:487-517` | ReactorResearch setLevel callback missing from methods()/invoke() | Phase 1 | open |
| B-313 | gameplay | oc | `src/main/java/com/hbm/tileentity/machine/TileEntityWatz.java:626-651` | TileEntityWatz methods() lists bogus 'getComponentName' | Phase 4 | open |
| B-314 | gameplay | oc | `src/main/java/com/hbm/tileentity/network/TileEntityRadioTelex.java:300-314` | RadioTelex.setSendingText errors when fewer than five arguments are passed | Phase 4 | open |
| B-315 | gameplay | oc | `src/main/java/com/hbm/tileentity/turret/TileEntityTurretArty.java:472-490` | TileEntityTurretArty.methods() omits inherited getPos | Phase 4 | open |
| B-316 | gameplay | oc | `src/main/java/com/hbm/tileentity/turret/TileEntityTurretBaseArtillery.java:86-93` | TurretBaseArtillery/HIMARS callbacks unreachable through ports | Phase 4 | open |
| B-317 | gameplay | oc | `src/main/resources/assets/hbm/disks/pwrangler/usr/bin/PWRangler.lua:207-256` | PWRangler.lua uses Lua 5.3 '//' and cannot run on Lua 5.2 / OC-LuaJIT | Phase 4 | open |
| B-006 | silent misconfig | core | `src/main/java/com/hbm/config/FalloutConfigJSON.java:292` | FalloutConfigJSON reads matchesMaterial from the mustBeOpaque key | Phase 6 | open |
| B-007 | silent misconfig | core | `src/main/java/com/hbm/config/GeneralConfig.java:28,118` | GeneralConfig.enableVirus field default (true) contradicts config default | Phase 6 | open |
| B-009 | silent misconfig | core | `src/main/java/com/hbm/handler/Identity.java:35` | Identity.init writes -1 instead of the new random id to the identity file | Phase 6 | open |
| B-010 | silent misconfig | core | `src/main/java/com/hbm/main/MainRegistry.java:254,682` | Two FMLPreInitializationEvent handlers (PreLoad, preInit) dispatched unordered | Phase 8 | open |
| B-011 | silent misconfig | core | `src/main/java/com/hbm/main/MainRegistry.java:596` | ClientConfig.initConfig runs on dedicated servers, writing hbmClient.json | Phase 6 | open |
| B-027 | silent misconfig | api | `src/main/java/api/hbm/block/IToolable.java:32-45` | ToolType.getType freezes its lookup map on first call, ignoring later register() | Phase 6 | open |
| B-043 | silent misconfig | blocks-generic | `src/main/java/com/hbm/blocks/gas/BlockGasBase.java:102` | BlockGasBase.updateTick clears world-global scheduledUpdatesAreImmediate | Phase 8 | open |
| B-053 | silent misconfig | blocks-machine | `src/main/java/com/hbm/blocks/BlockDummyableBeam.java:40` | BlockDummyableBeam.findCore(World) is an overload, not an override | Phase 6 | open |
| B-059 | silent misconfig | te-machine | `src/main/java/com/hbm/config/MachineDynConfig.java:41` | MachineDynConfig swallows newInstance() failures silently | Phase 6 | open |
| B-060 | silent misconfig | te-machine | `src/main/java/com/hbm/tileentity/machine/TileEntityMachineCentrifuge.java:387-388` | TileEntityMachineCentrifuge.provideExtraInfo writes two types under B_ACTIVE | Phase 6 | open |
| B-083 | silent misconfig | te-special | `src/main/java/com/hbm/util/GameRuleHelper.java:29-30` | GameRuleHelper.parseDouble/parseInt create a gamerule named by the value string | Phase 1 | open |
| B-098 | silent misconfig | networks | `src/main/java/com/hbm/handler/threading/PacketThreading.java:37` | PacketThreading.init() is never called during mod init | Phase 5 | open |
| B-099 | silent misconfig | networks | `src/main/java/com/hbm/handler/threading/PacketThreading.java:44-47` | PacketThreading.init() rejects max > core (validation inverted) | Phase 5 | open |
| B-111 | silent misconfig | items | `src/main/java/com/hbm/items/ModItemsArmor.java:318-330` | Two ArmorMaterials both named HBM_TRENCH via EnumHelper | Phase 6 | open |
| B-112 | silent misconfig | items | `src/main/java/com/hbm/items/armor/ArmorFSB.java:165-184` | ArmorFSB.cloneStats shares effects list, skips hazardClass/hides/overlay | Phase 6 | open |
| B-126 | silent misconfig | weapons | `src/main/java/com/hbm/items/weapon/sedna/mods/WeaponModBase.java:13` | Duplicate weapon-mod ids silently overwrite in HashBiMap | Phase 6 | open |
| B-139 | silent misconfig | recipes | `src/main/java/com/hbm/config/GeneralConfig.java:47-49` | A user hbmPrecisionAssembly.json silently disables expensive mode | Phase 6 | open |
| B-140 | silent misconfig | recipes | `src/main/java/com/hbm/config/ItemPoolConfigJSON.java:40-44` | ItemPoolConfigJSON never refreshes its template and fails silently | Phase 6 | open |
| B-141 | silent misconfig | recipes | `src/main/java/com/hbm/inventory/fluid/Fluids.java:1085-1099` | Fluids.readTraits clears traits, and FT_Rocket cannot be instantiated | Phase 6 | open |
| B-142 | silent misconfig | recipes | `src/main/java/com/hbm/inventory/fluid/Fluids.java:1130-1146` | Foreign (CompatFluidRegistry) fluids excluded from trait template/override | Phase 6 | open |
| B-143 | silent misconfig | recipes | `src/main/java/com/hbm/inventory/fluid/Fluids.java:1181-1184` | Fluids.register(fluid, id) overwrites idMapping without a collision check | Phase 6 | open |
| B-144 | silent misconfig | recipes | `src/main/java/com/hbm/inventory/recipes/ArcFurnaceRecipes.java:139-140` | ArcFurnaceRecipes.register silently drops recipes whose input is occupied | Phase 6 | open |
| B-146 | silent misconfig | recipes | `src/main/java/com/hbm/inventory/recipes/loader/GenericRecipes.java:102` | nameToRecipeGlobal silently overwrites same-named recipes across sets | Phase 6 | open |
| B-147 | silent misconfig | recipes | `src/main/java/com/hbm/inventory/recipes/loader/GenericRecipes.java:99-100` | GenericRecipes.register appends to recipeOrderedList before the duplicate check | Phase 6 | open |
| B-149 | silent misconfig | recipes | `src/main/java/com/hbm/inventory/recipes/loader/SerializableRecipe.java:244-245` | writeTemplateFile swallows every exception, leaving truncated templates | Phase 6 | open |
| B-150 | silent misconfig | recipes | `src/main/java/com/hbm/inventory/recipes/loader/SerializableRecipe.java:276` | readAStack drops metadata for NBT stacks that writeAStack emits | Phase 6 | open |
| B-171 | silent misconfig | gui | `src/main/java/com/hbm/handler/imc/IMCHandlerNHNEI.java:42-45` | IMCHandlerNHNEI hard-codes panel size for every handler | Phase 6 | open |
| B-172 | silent misconfig | gui | `src/main/java/com/hbm/handler/nei/BreederRecipeHandler.java:70-120` | BreederRecipeHandler hard-codes the 'breeding' id instead of getRecipeID() | Phase 1 | open |
| B-185 | silent misconfig | entities | `src/main/java/com/hbm/entity/logic/EntityExplosionChunkloading.java:38-44` | EntityExplosionChunkloading.loadChunk is a silent one-shot | Phase 6 | open |
| B-204 | silent misconfig | hazards | `src/main/java/com/hbm/handler/imc/IMCBlastFurnace.java:64` | IMCBlastFurnace reads input2 ore list with the wrong tag type | Phase 6 | open |
| B-206 | silent misconfig | hazards | `src/main/java/com/hbm/hazard/HazardSystem.java:62-63` | HazardSystem.register(ItemStack) does not makeSingular the key | Phase 6 | open |
| B-207 | silent misconfig | hazards | `src/main/java/com/hbm/hazard/type/HazardTypeAutism.java:24` | HazardTypeAutism/Glitch are gated by disableBlinding | Phase 6 | open |
| B-229 | silent misconfig | space | `src/main/java/com/hbm/config/StructureConfig.java:14-78` | StructureConfig.structureMaxChunks field default 12 vs config default 16 | Phase 6 | open |
| B-230 | silent misconfig | space | `src/main/java/com/hbm/dim/CelestialBody.java:258-272` | CelestialBody.getTraits returns the live saved trait map despite its comment | Phase 6 | open |
| B-232 | silent misconfig | space | `src/main/java/com/hbm/dim/CelestialBody.java:506-514` | CelestialBody.getBody silently falls back to Kerbin | Phase 6 | open |
| B-235 | silent misconfig | space | `src/main/java/com/hbm/dim/SolarSystemWorldSavedData.java:40` | SolarSystemWorldSavedData.get() relies on DimensionManager.getWorlds()[0] | Phase 6 | open |
| B-236 | silent misconfig | space | `src/main/java/com/hbm/dim/SolarSystemWorldSavedData.java:63-67` | SolarSystemWorldSavedData.readFromNBT silently drops traits that fail to load | Phase 6 | open |
| B-237 | silent misconfig | space | `src/main/java/com/hbm/world/ModBiomes.java:13-17` | ModBiomes double-registers Duna biome dictionary types and tags dunaHills twice | Phase 6 | open |
| B-256 | silent misconfig | render | `src/main/java/com/hbm/main/ClientProxy.java:2183-2206` | ClientProxy.getLoopedSound discards the pitch argument | Phase 6 | open |
| B-257 | silent misconfig | render | `src/main/java/com/hbm/main/ResourceManager.java:645-1471` | ResourceManager holds 45 texture paths that do not exist on disk | Phase 7 | open |
| B-258 | silent misconfig | render | `src/main/java/com/hbm/render/anim/AnimationLoader.java:68-77` | AnimationLoader parses rotmode but never stores it | Phase 6 | open |
| B-275 | silent misconfig | net-util | `src/main/java/com/hbm/main/NetworkHandler.java:56-57` | Unregistered IMessage encodes as discriminator 0 and mis-decodes as TESiren | Phase 6 | open |
| B-288 | silent misconfig | resources-build | `src/main/java/com/hbm/entity/missile/EntityRideableRocket.java:400` | Rocket 'hbm:entity.pipefail' sound has no sounds.json event | Phase 7 | open |
| B-289 | silent misconfig | resources-build | `src/main/java/com/hbm/entity/mob/EntityHunterChopper.java:181` | EntityHunterChopper plays unknown sound 'hbm:weapon.osiprShoot' | Phase 7 | open |
| B-290 | silent misconfig | resources-build | `src/main/java/com/hbm/items/armor/ItemModSensor.java:70` | ItemModSensor plays unknown sound 'hbm:weapon.follyAquired' | Phase 7 | open |
| B-291 | silent misconfig | resources-build | `src/main/java/com/hbm/items/food/ItemLemon.java:114` | ItemLemon plays 'hbm:entity.vomit' but the event is player.vomit | Phase 7 | open |
| B-292 | silent misconfig | resources-build | `src/main/resources/assets/hbm/lang/en_US.lang:1709-5422` | en_US.lang has 23 duplicate keys, two with conflicting values | Phase 7 | open |
| B-294 | silent misconfig | resources-build | `src/main/resources/assets/hbm/manual/concepts/fluidhandling.json` | QMAW page fluidhandling.json never loads (no trigger, empty content) | Phase 7 | open |
| B-295 | silent misconfig | resources-build | `src/main/resources/assets/hbm/manual/satellite/detector.json` | Satellite manual pages trigger on non-existent hbm:item.satellite | Phase 7 | open |
| B-296 | silent misconfig | resources-build | `src/main/resources/assets/hbm/sounds.json:248` | sounds.json weapon.grenadeBounce lists missing grenadeBounce2.ogg | Phase 7 | open |
| B-318 | silent misconfig | oc | `src/main/java/com/hbm/blocks/network/BlockOpenComputersCablePaintable.java:189-191` | OC cable @Optional.Interface names the wrong Colored interface | Phase 4 | open |
| B-321 | silent misconfig | oc | `src/main/java/com/hbm/tileentity/bomb/TileEntityLaunchTable.java:53` | TileEntityLaunchTable implements OCComponent without direct SimpleComponent | Phase 4 | open |
| B-323 | silent misconfig | oc | `src/main/java/com/hbm/tileentity/machine/TileEntityWatz.java:55` | TileEntityWatz implements OCComponent without direct SimpleComponent | Phase 4 | open |
| B-324 | silent misconfig | oc | `src/main/java/com/hbm/tileentity/machine/rbmk/TileEntityCraneConsole.java:455-470` | CraneConsole getDepletion/getXenonPoison return the string 'N/A' | Phase 4 | open |
| B-325 | silent misconfig | oc | `src/main/java/com/hbm/tileentity/machine/storage/TileEntityBatteryBase.java:222-233` | setModeLow/High narrow Lua numbers to short silently | Phase 4 | open |
| B-326 | silent misconfig | oc | `src/main/java/com/hbm/tileentity/machine/storage/TileEntityMachineFluidTank.java:58` | Six TEs use lowercase modid 'opencomputers' in @Optional.Interface | Phase 4 | open |
| B-327 | silent misconfig | oc | `src/main/java/com/hbm/tileentity/network/TileEntityRadioTorchBase.java:122` | Radio torch OC doc string misspells setChannel as 'setChannle' | Phase 4 | open |
| B-028 | leak / perf | api | `src/main/java/api/hbm/energymk2/IBatteryItem.java:31-34` | IBatteryItem.emptyBattery copies the stack twice | Phase 6 | open |
| B-054 | leak / perf | blocks-machine | `src/main/java/com/hbm/blocks/machine/BlockMachineBase.java:51-56` | BlockMachineBase.breakBlock skips super when TE is not ISidedInventory | Phase 6 | open |
| B-061 | leak / perf | te-machine | `src/main/java/com/hbm/tileentity/TileEntityLoadedBase.java:36-39` | 25 onChunkUnload overrides skip super, so isLoaded never becomes false | Phase 5 | open |
| B-084 | leak / perf | te-special | `src/main/java/com/hbm/handler/neutron/NeutronNodeWorld.java:70-80` | NeutronNodeWorld.cleanNodes never uncaches PILE nodes | Phase 5 | open |
| B-100 | leak / perf | networks | `src/main/java/com/hbm/handler/threading/PacketThreading.java:97,123` | Threaded send recompiles every packet on the worker thread; reuse throws | Phase 5 | open |
| B-101 | leak / perf | networks | `src/main/java/com/hbm/tileentity/machine/storage/TileEntityBatteryBase.java:65,102,111` | TileEntityBatteryBase node created at ports but looked up/destroyed at own xyz | Phase 5 | open |
| B-102 | leak / perf | networks | `src/main/java/com/hbm/tileentity/network/RTTYSystem.java:21,61` | RTTYSystem.broadcast channels are never evicted | Phase 5 | open |
| B-104 | leak / perf | networks | `src/main/java/com/hbm/tileentity/network/RequestNetwork.java:43-48` | RequestNetwork.updateEntries 20-tick throttle is dead code (purges every tick) | Phase 5 | open |
| B-107 | leak / perf | networks | `src/main/java/com/hbm/uninos/UniNodespace.java:27` | UniNodespace.worlds is keyed by World and never cleared on unload | Phase 5 after D6 | open |
| B-188 | leak / perf | entities | `src/main/java/com/hbm/explosion/ExplosionNukeRayBatched.java:260-264` | ExplosionNukeRayBatched.cacheChunksTick ignores its time budget | Phase 5 | open |
| B-210 | leak / perf | hazards | `src/main/java/com/hbm/handler/neutron/NeutronNodeWorld.java:39-43` | NeutronNodeWorld.removeEmptyWorlds drops node caches when a world has no streams | Phase 5 | open |
| B-212 | leak / perf | hazards | `src/main/java/com/hbm/handler/radiation/ChunkRadiationHandlerSimple.java:181` | Simple radiation backend unload removes by Chunk instead of ChunkCoordIntPair | Phase 5 | open |
| B-238 | leak / perf | space | `src/main/java/com/hbm/dim/Ike/WorldGeneratorIke.java:67-82` | WorldGeneratorIke scans every block column of each chunk for pedestals | Phase 6 | open |
| B-277 | leak / perf | net-util | `src/main/java/com/hbm/blocks/BlockVolcanoV2.java:91` | BufPacket is also sent directly, bypassing networkPackNT's dedupe and threading | Phase 6 | open |
| B-278 | leak / perf | net-util | `src/main/java/com/hbm/packet/toclient/BufPacket.java:57-67` | BufPacket/EntityBufPacket handlers leak the inbound buffer for non-receivers | Phase 5 | open |
| B-012 | dead code | core | `src/main/java/com/hbm/extprop/HbmPlayerProps.java:225-227` | HbmPlayerProps/HbmLivingProps NBT persistence methods are marked @Deprecated | Phase 7 | open |
| B-014 | dead code | core | `src/main/java/com/hbm/handler/HbmKeybinds.java:40,67,229` | EnumKeybind.SLAM / slamKey is registered but has no handler or proxy case | Phase 7 | open |
| B-015 | dead code | core | `src/main/java/com/hbm/main/MainRegistry.java:674,748` | CommandWikiRender is registered both as a client and as a server command | Phase 7 | open |
| B-031 | dead code | api | `src/main/java/api/hbm/block/IRadioControllable.java:1` | Dead API interfaces with zero implementers; two lack @Deprecated | Phase 7 | open |
| B-032 | dead code | api | `src/main/java/api/hbm/energymk2/Nodespace.java:24-33` | @Deprecated Nodespace statics are still the API's own subscription path | Phase 7 | open |
| B-034 | dead code | api | `src/main/java/api/hbm/fluidmk2/FluidNetMK2.java:49` | FluidNetMK2.transfered[] is allocated and reset but never written | Phase 7 | open |
| B-035 | dead code | api | `src/main/java/api/hbm/ntl/StackCache.java:181` | StackCache.CacheSlot.reCount is self-documented as unused | Phase 7 | open |
| B-045 | dead code | blocks-generic | `src/main/java/com/hbm/blocks/BlockBase.java:36-41` | BlockBase.setBeaconable()/noMobSpawn() have no call sites | Phase 7 | open |
| B-046 | dead code | blocks-generic | `src/main/java/com/hbm/blocks/ModBlocks.java:4052-4055` | Block rename compat path is dead: addRemap unused, no BLOCK remap branch | Phase 8 | open |
| B-048 | dead code | blocks-generic | `src/main/java/com/hbm/blocks/turret/TurretBase.java:1` | blocks/turret/TurretBase.java is dead (zero subclasses) | Phase 7 | open |
| B-062 | dead code | te-machine | `src/main/java/com/hbm/tileentity/IRTGUser.java:83-153` | IRTGUser interface (with three @Untested overloads) has no implementers | Phase 7 | open |
| B-063 | dead code | te-machine | `src/main/java/com/hbm/tileentity/TileEntityMachineBase.java:140` | TileEntityMachineBase.getGaugeScaled takes Forge's FluidTank (dead, misleading) | Phase 7 | open |
| B-085 | dead code | te-special | `src/main/java/com/hbm/tileentity/bomb/EntityAntimatter.java:1` | tileentity/bomb/EntityAntimatter.java is a 0-byte file | Phase 7 | open |
| B-087 | dead code | te-special | `src/main/java/com/hbm/tileentity/bomb/TileEntityNukeBoy.java:185` | TileEntityNukeBoy.isReady uses octal literal slots[01] | Phase 7 | open |
| B-088 | dead code | te-special | `src/main/java/com/hbm/tileentity/bomb/TileEntityNukeCustom.java:385` | TileEntityNukeCustom.destruct() passes xCoord as z (and is never called) | Phase 7 | open |
| B-089 | dead code | te-special | `src/main/java/com/hbm/tileentity/machine/TileEntityPWRController.java:557` | TileEntityPWRController writes "rodCount" twice in writeToNBT | Phase 7 | open |
| B-090 | dead code | te-special | `src/main/java/com/hbm/tileentity/machine/rbmk/TileEntityRBMKBase.java:602-604` | TileEntityRBMKBase.getFancyStats(NBT) is a static stub returning null | Phase 7 | open |
| B-091 | dead code | te-special | `src/main/java/com/hbm/tileentity/machine/rbmk/TileEntityRBMKConsole.java:66-68` | TileEntityRBMKConsole.getName() returns null | Phase 6 | open |
| B-114 | dead code | items | `src/main/java/com/hbm/items/ModItems.java:249-2232` | Six ModItems fields declared but never assigned (null) | Phase 7 | open |
| B-117 | dead code | items | `src/main/java/com/hbm/items/ModItemsArmor.java:276-277` | jackt/jackt2 instantiated twice in ModItemsArmor.init() | Phase 7 | open |
| B-118 | dead code | items | `src/main/resources/assets/hbm/textures/items/achievement_icon.digammaunity.png:1` | Orphan texture achievement_icon.digammaunity.png | Phase 7 | open |
| B-129 | dead code | weapons | `src/main/java/com/hbm/entity/projectile/EntityBullet.java:408-416` | EntityBullet legacy 'test feature' left in release code | Phase 7 | open |
| B-131 | dead code | weapons | `src/main/java/com/hbm/handler/guncfg/GunDGKFactory.java:8-20` | GunDGKFactory is dead code (CASINGDGK never referenced) | Phase 7 | open |
| B-132 | dead code | weapons | `src/main/java/com/hbm/items/weapon/sedna/ItemGunBaseNT.java:276` | playAnimation always sends receiverIndex 0 to the recoil LambdaContext | Phase 6 | open |
| B-133 | dead code | weapons | `src/main/java/com/hbm/items/weapon/sedna/factory/XFactory35800.java:56-58` | SpentCasing "35-800" registered twice with identical stats | Phase 7 | open |
| B-151 | dead code | recipes | `src/main/java/com/hbm/inventory/recipes/SILEXRecipes.java:51-58` | SILEXRecipes registers ingot_cm_mix twice | Phase 7 | open |
| B-152 | dead code | recipes | `src/main/java/com/hbm/inventory/recipes/loader/SerializableRecipe.java:85-95` | Stale '//AFTER Assembler' ordering comment in registerAllHandlers | Phase 7 | open |
| B-153 | dead code | recipes | `src/main/java/com/hbm/itempool/ItemPoolsSingle.java:116-130` | ItemPoolsSingle registers POOL_BLUEPRINTS twice | Phase 7 | open |
| B-173 | dead code | gui | `src/main/java/com/hbm/handler/nei/AlloyFurnaceRecipeHandler.java:1` | AlloyFurnaceRecipeHandler is never registered | Phase 7 | open |
| B-174 | dead code | gui | `src/main/java/com/hbm/inventory/container/ContainerNT.java:1` | ContainerNT is dead code | Phase 3 | open |
| B-175 | dead code | gui | `src/main/java/com/hbm/inventory/gui/GUIMachineReactorBreeding.java:53-63` | GUIMachineReactorBreeding carries a stale 'dud TE' comment with no handling | Phase 7 | open |
| B-190 | dead code | entities | `src/main/java/com/hbm/entity/ModEntityList.java:62-124` | ModEntityList lookup helpers are dead code | Phase 7 | open |
| B-191 | dead code | entities | `src/main/java/com/hbm/entity/train/EntityRailCarBase.java:119-133` | EntityRailCarBase.interactFirst builds debug NBT that is never sent | Phase 7 | open |
| B-192 | dead code | entities | `src/main/java/com/hbm/explosion/ExplosionNukeRayBalefire.java:1` | ExplosionNukeRayBalefire is never instantiated | Phase 7 | open |
| B-214 | dead code | hazards | `src/main/java/com/hbm/handler/ThreeInts.java:46-48` | ThreeInts.compareTo violates the Comparable contract | Phase 7 | open |
| B-217 | dead code | hazards | `src/main/java/com/hbm/main/ModEventHandler.java:1466-1474` | Legacy neutron-activation branch in ModEventHandler.onPlayerTick is unreachable | Phase 7 | open |
| B-241 | dead code | space | `src/main/java/com/hbm/dim/dres/WorldGeneratorDres.java:77-80` | WorldGeneratorDres registers ore_lanthanium as a valid body twice | Phase 7 | open |
| B-243 | dead code | space | `src/main/java/com/hbm/dim/trait/CBT_Compromised.java:3` | Empty top-level CBT_Compromised shadows the registered inner CBT_COMPROMISED | Phase 7 | open |
| B-245 | dead code | space | `src/main/java/com/hbm/world/feature/OreLayer.java:1` | OreLayer (2D) is never instantiated | Phase 7 | open |
| B-246 | dead code | space | `src/main/java/com/hbm/world/test/WorldGenTest.java:1` | world/test scaffolding is unreferenced | Phase 7 | open |
| B-260 | dead code | render | `src/main/java/com/hbm/render/entity/projectile/RenderBombletTheta.java:22` | RenderBombletTheta fallback texture bombletThetaTexture.png missing | Phase 7 | open |
| B-261 | dead code | render | `src/main/java/com/hbm/render/tileentity/RenderBombMultiLarge.java:15-22` | RenderBombMultiLarge unbound and references missing model/texture | Phase 7 | open |
| B-262 | dead code | render | `src/main/java/com/hbm/render/tileentity/RenderVaultDoor.java:34-77` | Legacy render/tileentity/RenderVaultDoor is unbound and uses 11 missing textures | Phase 7 | open |
| B-263 | dead code | render | `src/main/java/com/hbm/render/tileentity/RendererObjTester.java:436-445` | Test renderers reference missing textures (ObjTester, MinecartTest) | Phase 7 | open |
| B-279 | dead code | net-util | `src/main/java/com/hbm/main/NetworkHandler.java:168-183` | NetworkHandler.sendToAllAround(ByteBuf, TargetPoint) is an unused trap overload | Phase 7 | open |
| B-280 | dead code | net-util | `src/main/java/com/hbm/packet/GuiLayerPacket.java:38` | GuiLayerPacket handler has an unreachable client-side check | Phase 7 | open |
| B-297 | dead code | resources-build | `src/main/java/com/hbm/main/StructureManager.java:165` | StructureManager.excavator references missing structures/excavator.nbt | Phase 7 | open |
| B-298 | dead code | resources-build | `src/main/resources/assets/hbm/lang/en_US.lang:5577` | Lang key soundCategory.ntmMachines has no implementation | Phase 7 | open |
| B-300 | dead code | resources-build | `src/main/resources/assets/hbm/models/weapons/.obj` | Corrupt, nameless models/weapons/.obj ships in the jar | Phase 7 | open |
| B-301 | dead code | resources-build | `src/main/resources/assets/hbm/my_hecking_realism.png` | Unreferenced images my_hecking_realism.png and textures/ABC123.png | Phase 7 | open |
| B-302 | dead code | resources-build | `src/main/resources/assets/hbm/shaders/supernovae.frag` | Shaders blackholed.frag and supernovae.frag are unreferenced | Phase 7 | open |
| B-303 | dead code | resources-build | `src/main/resources/assets/hbm/sounds.json:393-395` | sounds.json fm.clap/fm.mug/fm.sample point at non-existent root files | Phase 7 | open |
| B-305 | dead code | resources-build | `src/main/resources/assets/hbm/structures/crane.nbt` | Seven unreferenced structure NBTs ship in the jar | Phase 7 | open |
| B-330 | dead code | oc | `src/main/java/com/hbm/tileentity/machine/rbmk/TileEntityCraneConsole.java:400-412` | worldObj.isRemote checks inside OC callbacks are dead code | Phase 7 | open |

## Needs a decision (changes balance, saves or player-visible behaviour)

| ID | Severity | Area | Location | Title | Plan | Status |
|---|---|---|---|---|---|---|
| B-001 | crash | core | `src/main/java/com/hbm/handler/BlockMigrations.java:43` | BlockMigrations.doMigraion loops y upward from the height value (infinite loop) | decision D1 | open |
| B-049 | crash | blocks-machine | `src/main/java/com/hbm/blocks/BlockDummyable.java:174` | BlockDummyable.positions/internalPlayers: unsynchronized per-Block scratch state | decision D8 | open |
| B-134 | crash | recipes | `src/main/java/com/hbm/inventory/recipes/RadiolysisRecipes.java:54-57` | RadiolysisRecipes.registerRadiolysis throws when cracking recipes are empty | decision D18 | open |
| B-135 | crash | recipes | `src/main/java/com/hbm/inventory/recipes/loader/GenericRecipes.java:116-117` | GenericRecipes.readRecipe NPEs when user JSON omits duration or power | decision D18 | open |
| B-136 | crash | recipes | `src/main/java/com/hbm/inventory/recipes/loader/SerializableRecipe.java:126-160` | SerializableRecipe.initialize has no per-handler error isolation | decision D18 | open |
| B-193 | crash | hazards | `src/main/java/com/hbm/handler/atmosphere/AtmosphereBlob.java:171-175` | AtmosphereBlob flood fill reads world blocks from a pool thread | decision D22 | open |
| B-266 | crash | net-util | `src/main/java/com/hbm/util/ArmorUtil.java:235-250` | ArmorUtil static initializer throws AssertionError on unreflectable fiskheroes | decision D25 | open |
| B-069 | save data | te-special | `src/main/java/com/hbm/tileentity/machine/rbmk/TileEntityRBMKBase.java:305-309` | Static `diag` flag makes RBMK writeToNBT skip base/items during client HUD dump | Phase 1 after D10 | open |
| B-177 | save data | entities | `src/main/java/com/hbm/entity/logic/EntityNukeExplosionMK5.java:144-151` | MK5 nuke explosion loses all state on save/reload | decision D20 | open |
| B-005 | gameplay | core | `src/main/java/com/hbm/main/ModEventHandler.java:1664-1670` | onPlayerClone resets NBT-only player props on every clone, not only on death | decision D2 | open |
| B-018 | gameplay | api | `src/main/java/api/hbm/energymk2/IEnergyProviderMK2.java:43-52` | IEnergyProviderMK2.tryProvide pushes directly to any receiver, conductor or not | decision D5 | open |
| B-020 | gameplay | api | `src/main/java/api/hbm/energymk2/PowerNetMK2.java:128-145` | PowerNetMK2.sendPowerDiode lacks isBadLink, zero-demand skip and demand clamp | decision D5 | open |
| B-021 | gameplay | api | `src/main/java/api/hbm/energymk2/PowerNetMK2.java:85,149` | PowerNetMK2 priority loop subtracts the cumulative energyUsed every tier | decision D5 | open |
| B-022 | gameplay | api | `src/main/java/api/hbm/fluidmk2/FluidNetMK2.java:108` | FluidNetMK2.transferFluid subtracts cumulative received[p] per priority tier | decision D5 | open |
| B-023 | gameplay | api | `src/main/java/api/hbm/fluidmk2/IFluidStandardSenderMK2.java:48-57` | IFluidStandardSenderMK2.tryProvide direct push has no allowDirectProvision gate | decision D5 | open |
| B-070 | gameplay | te-special | `src/main/java/com/hbm/handler/neutron/RBMKNeutronHandler.java:286-290` | Blocked or partially blocked streams irradiate the origin column | Phase 1 after D10 | open |
| B-071 | gameplay | te-special | `src/main/java/com/hbm/handler/neutron/RBMKNeutronHandler.java:385` | RBMK stream tail check (#1933) tests the origin node, so the branch is dead | Phase 1 after D10 | open |
| B-076 | gameplay | te-special | `src/main/java/com/hbm/tileentity/deco/TileEntityTrappedBrick.java:77-78` | Trapped brick POISON_DART variant is selectable but unimplemented | decision D12 | open |
| B-077 | gameplay | te-special | `src/main/java/com/hbm/tileentity/machine/TileEntityMachineReactorBreeding.java:59` | Breeding reactor progress uses integer division of flux | Phase 1 after D11 | open |
| B-095 | gameplay | networks | `src/main/java/com/hbm/handler/threading/PacketThreading.java:147,165-168` | packetThreadingErrorBypass skips the join entirely and drops the remaining queue | Phase 5 after D13 | open |
| B-096 | gameplay | networks | `src/main/java/com/hbm/handler/threading/PacketThreading.java:150-160,193` | waitUntilThreadFinished drops queued packets after 50 ms and latches to sync | Phase 5 after D13 | open |
| B-109 | gameplay | items | `src/main/java/com/hbm/main/ModEventHandler.java:764-770` | IEquipReceiver.onEquip fires only when the held Item type changes | decision D28 | open |
| B-121 | gameplay | weapons | `src/main/java/com/hbm/items/weapon/sedna/mags/MagazineBelt.java:159-161` | MagazineBelt persists its type under an un-indexed 'magtype' NBT key | decision D17 | open |
| B-169 | gameplay | gui | `src/main/java/com/hbm/inventory/gui/GuiInfoContainer.java:362-371` | NEI drag-and-drop dead for raw containers with SlotPattern | decision D19 | open |
| B-170 | gameplay | gui | `src/main/java/com/hbm/main/NEIRegistry.java:14-16` | GTNH NEI: handler list built (and memoised) before recipes are initialised | decision D19 | open |
| B-182 | gameplay | entities | `src/main/java/com/hbm/entity/mob/glyphid/GlyphidStats.java:13-15` | Active glyphid stat set GLYPHID_STATS_NT is marked UNTESTED | decision D20 | open |
| B-183 | gameplay | entities | `src/main/java/com/hbm/explosion/ExplosionNukeRayBatched.java:89-98` | Batched nuke ray decays resistance over strength but cuts rays at length | decision D20 | open |
| B-184 | gameplay | entities | `src/main/java/com/hbm/explosion/vanillant/ExplosionVNT.java:123-141` | ExplosionVNT.makeStandard/makeAmat use the deprecated EntityProcessorStandard | decision D29 | open |
| B-200 | gameplay | hazards | `src/main/java/com/hbm/hazard/type/HazardTypeRadiation.java:34-36` | Neutron activation NBT is counted twice in radiation hazard level | decision D22 | open |
| B-224 | gameplay | space | `src/main/java/com/hbm/lib/HbmWorldGen.java:88-102` | HbmWorldGen rolls meteorites and crashed spaceships on every celestial dimension | decision D23 | open |
| B-225 | gameplay | space | `src/main/java/com/hbm/main/ModEventHandler.java:938-972` | updateWaterOpacity mutates Blocks.water opacity globally on every world tick | decision D23 | open |
| B-226 | gameplay | space | `src/main/java/com/hbm/world/feature/OreLayer3D.java:137` | OreLayer3D reuses cacheX for the z noise axis | decision D23 | open |
| B-272 | gameplay | net-util | `src/main/java/com/hbm/util/BufferUtil.java:116-117,158` | BufferUtil.readNBT returns a new empty compound for the null marker | decision D25 | open |
| B-309 | gameplay | oc | `src/main/java/com/hbm/tileentity/bomb/TileEntityLaunchPadBase.java:522-545` | methods()-listed callbacks run as direct PeripheralCallbacks, ignoring @Callback | Phase 4 after D27 | open |
| B-311 | gameplay | oc | `src/main/java/com/hbm/tileentity/machine/TileEntityMicrowave.java:241-258` | TileEntityMicrowave ships OC test callbacks test/variableget/variableset | Phase 4 after D27 | open |
| B-008 | silent misconfig | core | `src/main/java/com/hbm/handler/HTTPHandler.java:43-58` | HTTPHandler version check compares against JameH2's RefStrings on GitHub | decision D3 | open |
| B-025 | silent misconfig | api | `.editorconfig:5` | .editorconfig declares CRLF for every file but the tree is mostly LF | Phase 0 | open |
| B-026 | silent misconfig | api | `.github/workflows/build.yml:15-17,48` | CI workflow only triggers on space-travel-twopointfive and hard-codes 1.0.27 | decision D4 | open |
| B-042 | silent misconfig | blocks-generic | `src/main/java/com/hbm/blocks/ModBlocks.java:3110` | 32 blocks implement ITooltipProvider/IBlockMulti but use the vanilla ItemBlock | decision D7 | open |
| B-044 | silent misconfig | blocks-generic | `src/main/java/com/hbm/main/ModEventHandlerClient.java:225` | All ILookOverlay HUDs gated on ClientConfig.DODD_RBMK_DIAGNOSTIC | decision D7 | open |
| B-052 | silent misconfig | blocks-machine | `src/main/java/com/hbm/blocks/BlockDummyable.java:71-80` | BlockDummyable.safeRem/overrideTileMeta statics toggled without try/finally | decision D8 | open |
| B-081 | silent misconfig | te-special | `src/main/java/com/hbm/tileentity/machine/TileEntityPWRController.java:678` | PWR ROR: value "rods" is 100 - rodLevel while setrods takes raw percent | decision D10 | open |
| B-113 | silent misconfig | items | `src/main/java/com/hbm/items/food/ItemLemon.java:40` | item.med_ipecac.desc key uses Cyrillic 'c' in code and en_US/uk_UA/zh_CN | decision D15 | open |
| B-145 | silent misconfig | recipes | `src/main/java/com/hbm/inventory/recipes/AtmosphereRecipes.java:70-73` | AtmosphereRecipes JSON read/write are no-ops | decision D18 | open |
| B-186 | silent misconfig | entities | `src/main/java/com/hbm/entity/logic/EntityNukeExplosionMK3.java:63` | BombConfig.limitExplosionLifespan only honoured by MK3 | decision D20 | open |
| B-187 | silent misconfig | entities | `src/main/java/com/hbm/main/ClientProxy.java:796` | EntitySiegeTunneler has a renderer but is never registered | decision D21 | open |
| B-202 | silent misconfig | hazards | `src/main/java/com/hbm/config/RadiationConfig.java:31-73` | RadiationConfig.disableNeutron defaults to true | decision D22 | open |
| B-203 | silent misconfig | hazards | `src/main/java/com/hbm/handler/HazmatRegistry.java:233-237` | HazmatRegistry user JSON replaces the whole resistance table | decision D30 | open |
| B-209 | silent misconfig | hazards | `src/main/java/com/hbm/saveddata/satellites/XSatelliteRegistry.java:35-36` | SatelliteHorizons and SatelliteRailgun registered with colour 0,0,0 | decision D22 | open |
| B-231 | silent misconfig | space | `src/main/java/com/hbm/dim/CelestialBody.java:262-266` | getTraits fallback map exposes shared default trait instances to mutation | decision D23 | open |
| B-233 | silent misconfig | space | `src/main/java/com/hbm/dim/ChunkProviderCelestial.java:413-430` | Planet chunk providers never post PopulateChunkEvent | Phase 6 after D23 | open |
| B-234 | silent misconfig | space | `src/main/java/com/hbm/dim/SolarSystem.java:275-321` | Thatmo dimension is registered but its body is unbound | decision D23 | open |
| B-286 | silent misconfig | resources-build | `README.md:48-49` | README maven coordinates omit the _H261 suffix build.gradle publishes | decision D4 | open |
| B-287 | silent misconfig | resources-build | `gradle.properties:19-82` | gradle.properties credits has mojibake and punctuation errors | decision D4 | open |
| B-293 | silent misconfig | resources-build | `src/main/resources/assets/hbm/lang/it_IT.lang` | it_IT/zh_CN/ru_RU lang files carry duplicate keys | decision D26 | open |
| B-319 | silent misconfig | oc | `src/main/java/com/hbm/tileentity/TileEntityProxyCombo.java:34-35` | TileEntityProxyCombo @Optional.Interface uses lowercase modid 'opencomputers' | Phase 1 after D27 | open |
| B-320 | silent misconfig | oc | `src/main/java/com/hbm/tileentity/TileEntityProxyCombo.java:502-511` | ProxyCombo reports cached 'ntm_null' name before world load | Phase 4 after D27 | open |
| B-322 | silent misconfig | oc | `src/main/java/com/hbm/tileentity/machine/TileEntityMachineIndustrialTurbine.java:260` | Component names reused with divergent APIs (ntm_turbine, ntm_energy_storage) | Phase 4 after D27 | open |
| B-328 | silent misconfig | oc | `src/main/java/com/hbm/tileentity/network/TileEntityRadioTorchBase.java:146-151` | setCustomMapValues looks up Integer keys in an OC table map | Phase 4 after D27 | open |
| B-029 | leak / perf | api | `src/main/java/api/hbm/energymk2/PowerNetMK2.java:31-32` | PowerNetMK2/FluidNetMK2.update return early on one-sided nets (stale entries) | decision D5 | open |
| B-030 | leak / perf | api | `src/main/java/api/hbm/tile/ILoadedTile.java:20,48` | ILoadedTile.TileAccessCache is a static map that is never evicted | Phase 5 after D6 | open |
| B-103 | leak / perf | networks | `src/main/java/com/hbm/tileentity/network/RTTYSystem.java:64-72` | RTTYSystem injects the '2012-08-06' test channel into every world every tick | Phase 5 after D14 | open |
| B-105 | leak / perf | networks | `src/main/java/com/hbm/tileentity/network/TileEntityPipelineBase.java:189` | TileEntityPipelineBase.getRenderBoundingBox returns INFINITE_EXTENT_AABB | Phase 5 after D14 | open |
| B-106 | leak / perf | networks | `src/main/java/com/hbm/tileentity/network/TileEntityRequestNetwork.java:80-89` | TileEntityRequestNetwork rescans hasPath for every known node each pass | decision D14 | open |
| B-108 | leak / perf | networks | `src/main/java/com/hbm/uninos/networkproviders/KlystronNetwork.java:8` | Klystron/Plasma/Foundry/Rebar nets have empty update() and never purge members | decision D14 | open |
| B-211 | leak / perf | hazards | `src/main/java/com/hbm/handler/radiation/ChunkRadiationHandlerPRISM.java:92` | PRISM.setRadiation force-loads chunks | decision D22 | open |
| B-239 | leak / perf | space | `src/main/java/com/hbm/dim/eve/WorldGeneratorEve.java:56-63` | Eve volcano/spike generators cascade chunks | decision D23 | open |
| B-240 | leak / perf | space | `src/main/java/com/hbm/main/ModEventHandler.java:823-831` | overrideOverworldProvider re-registers provider 0 on every non-Earth world load | decision D23 | open |
| B-329 | leak / perf | oc | `src/main/java/com/hbm/handler/CompatHandler.java:249-252` | OCComponent extends ManagedPeripheral, disabling OC's callback cache | Phase 4 after D27 | open |
| B-013 | dead code | core | `src/main/java/com/hbm/handler/BlockMigrations.java:14-36` | BlockMigrations.buildNumber() is never called so chunks are stamped -1 | decision D1 | open |
| B-016 | dead code | core | `src/main/java/com/hbm/main/ModEventHandler.java:840-858` | ModEventHandler.worldTick keeps a dead reflective `reference` field hack | decision D31 | open |
| B-047 | dead code | blocks-generic | `src/main/java/com/hbm/blocks/generic/BlockHazard.java:162-163` | BlockHazard.getRarity lists block_schraranium twice | decision D32 | open |
| B-064 | dead code | te-machine | `src/main/java/com/hbm/tileentity/machine/TileEntityMachineIGenerator.java:85-210` | machine_industrial_generator sits in the creative tab but is fully inert | decision D9 | open |
| B-086 | dead code | te-special | `src/main/java/com/hbm/tileentity/bomb/TileEntityLaunchPadRocket.java:235-236` | TileEntityLaunchPadRocket.isPadObstructed loops over a single block | decision D33 | open |
| B-115 | dead code | items | `src/main/java/com/hbm/items/ModItems.java:3459-3461` | rbmk_pellet_lecf/mecf/hecf constructed but never registered | decision D16 | open |
| B-116 | dead code | items | `src/main/java/com/hbm/items/ModItems.java:4050-4771` | med_schizophrenia, pch, ammo_misc instantiated but never registered | decision D16 | open |
| B-189 | dead code | entities | `src/main/java/com/hbm/config/BombConfig.java:97-98` | BombConfig.explosionAlgorithm and enableChunkLoading are dead options | decision D20 | open |
| B-213 | dead code | hazards | `src/main/java/com/hbm/config/RadiationConfig.java:72-84` | RadiationConfig.disableFibrosis and smokeStackSootMult are dead options | decision D22 | open |
| B-216 | dead code | hazards | `src/main/java/com/hbm/handler/radiation/ChunkRadiationHandlerNT.java:1` | Dead radiation backends NT/3D/Blank | decision D22 | open |
| B-242 | dead code | space | `src/main/java/com/hbm/dim/duna/genlayer/GenLayerDunaHills.java:1` | GenLayerDunaHills and GenLayerDunaPolarHills are unused | decision D23 | open |
| B-244 | dead code | space | `src/main/java/com/hbm/world/biome/BiomeGenNoMansLand.java:24` | BiomeGenNoMansLand and WorldProviderTom are dead and hard-code biome id 99 | decision D23 | open |
| B-259 | dead code | render | `src/main/java/com/hbm/particle/psys/engine/EventHandlerParticleEngine.java:14-30` | Unfinished particle psys engine is registered and allocates per world load | decision D24 | open |
| B-264 | dead code | render | `src/main/java/com/hbm/render/world/RenderNTMSkyboxChainloader.java:17-49` | RenderNTMSkyboxChainloader/RenderNTMSkyboxImpact are unreferenced | decision D24 | open |
| B-283 | dead code | net-util | `src/main/java/com/hbm/util/BobMathUtil.java:87,263` | BobMathUtil.getAngleFrom2DVecs uses cos instead of acos; setPi rewrites Math.PI | decision D25 | open |
| B-284 | dead code | net-util | `src/main/java/com/hbm/util/FogMessage.java:1` | FogMessage, FauxWorld and TimeAnalyzer are unused dead classes | decision D25 | open |
| B-299 | dead code | resources-build | `src/main/resources/assets/hbm/models/missileDoomsday.obj` | 35 OBJ models are not referenced by any Java path | decision D26 | open |
| B-304 | dead code | resources-build | `src/main/resources/assets/hbm/sounds/misc/desktop.ini` | Stray non-OGG and 32 unreferenced OGG files ship in sounds/ | decision D26 | open |

## Leave (harmless, or design the fork keeps)

| ID | Severity | Area | Location | Title | Plan | Status |
|---|---|---|---|---|---|---|
| B-250 | crash | render | `src/main/java/com/hbm/render/loader/HFRWavefrontObject.java:177` | Mixed-mode HFRWavefrontObject throws from renderAll/renderPart | not planned | open |
| B-075 | gameplay | te-special | `src/main/java/com/hbm/tileentity/bomb/TileEntityNukeBoy.java:129-141` | Assembly nukes expose no sided slots and have an odd canExtractItem | not planned | open |
| B-097 | gameplay | networks | `src/main/java/com/hbm/uninos/UniNodespace.java:151` | UniNodeWorld.popNode destroys the entire NodeNet of the removed node | not planned | open |
| B-122 | gameplay | weapons | `src/main/java/com/hbm/items/weapon/sedna/mags/MagazineSingleTypeBase.java:84-91` | NPC-held sedna guns reload for free and skip EQUIP | not planned | open |
| B-138 | gameplay | recipes | `src/main/java/com/hbm/inventory/RecipesCommon.java:198-218` | ComparableStack equals accepts WILDCARD but hashCode includes meta | not planned | open |
| B-254 | gameplay | render | `src/main/java/com/hbm/main/ModEventHandlerRenderer.java:673-676` | Badge HUD still hidden by F1 despite ElementType.ALL | not planned | open |
| B-082 | silent misconfig | te-special | `src/main/java/com/hbm/tileentity/machine/rbmk/TileEntityRBMKBase.java:61` | RBMK explodeOnBroken is a static toggled through instance references | not planned | open |
| B-148 | silent misconfig | recipes | `src/main/java/com/hbm/inventory/recipes/loader/SerializableRecipe.java:150-153` | IRecipeRegisterListener (fork recipes) skipped when a user JSON exists | not planned | open |
| B-205 | silent misconfig | hazards | `src/main/java/com/hbm/handler/radiation/ChunkRadiationHandlerSimple.java:51` | Simple backend silently drops radiation writes to unloaded chunks | not planned | open |
| B-208 | silent misconfig | hazards | `src/main/java/com/hbm/inventory/OreDictManager.java:135` | OreDictManager names uranium 'Uraninite' when GT6 is loaded | not planned | open |
| B-228 | silent misconfig | space | `src/main/java/com/hbm/config/SpaceConfig.java:114-150` | Duplicate config key prefixes in SpaceConfig and WorldConfig | not planned | open |
| B-276 | silent misconfig | net-util | `src/main/java/com/hbm/util/CompatRecipeRegistry.java:288-292` | CompatRecipeRegistry deprecated registerAssembler/registerChemplant are NOPs | not planned | open |
| B-127 | leak / perf | weapons | `src/main/java/com/hbm/items/weapon/sedna/ItemGunBaseNT.java:473-474` | ItemGunBaseNT.getShareTag disabled: full gun NBT resynced on every change | not planned | open |
| B-033 | dead code | api | `src/main/java/api/hbm/entity/RadarEntry.java:47-65` | RadarEntry.fromBytes/toBytes omit the redstone field | not planned | open |
| B-036 | dead code | api | `src/main/java/cofh/api/energy/TileEnergyHandler.java:1` | Vendored cofh TileEnergyHandler and ItemEnergyContainer are unused | not planned | open |
| B-065 | dead code | te-machine | `src/main/java/com/hbm/tileentity/machine/TileEntityMachineWarController.java:40-60` | TileEntityMachineWarController is an auto-generated stub (isLoaded() false) | not planned | open |
| B-092 | dead code | te-special | `src/main/java/com/hbm/tileentity/machine/rbmk/TileEntityRBMKRod.java:134` | ItemRBMKRod.specialFluxCurve is never enabled; rod branch is dead | not planned | open |
| B-128 | dead code | weapons | `src/main/java/com/hbm/entity/missile/EntityMissileCustom.java:179-243` | EntityMissileCustom impact switch: CLUSTER no-op, several types unhandled | not planned | open |
| B-130 | dead code | weapons | `src/main/java/com/hbm/entity/projectile/EntityBulletBeamBase.java:325` | EntityBulletBeamBase kills itself on NBT read | not planned | open |
| B-215 | dead code | hazards | `src/main/java/com/hbm/handler/pollution/PollutionHandler.java:358` | PollutionType.FALLOUT is an unused enum slot | not planned | open |
| B-281 | dead code | net-util | `src/main/java/com/hbm/packet/PacketDispatcher.java:76-77` | ContainerNBTCommsPacket is registered twice (discriminators 27 and 28) | not planned | open |
| B-282 | dead code | net-util | `src/main/java/com/hbm/packet/toserver/AuxButtonPacket.java:29,150` | AuxButtonPacket is @Deprecated yet used by 19 GUIs; value 999 spawns a duck | not planned | open |
| B-285 | dead code | net-util | `src/main/java/com/hbm/wiaj/actors/ActorFancyPanel.java:130-133,148-151,340` | ActorFancyPanel measures scaled-stack elements but never draws them | not planned | open |

## Details by area

### Bootstrap, config, commands (`core`)

- **B-001** · crash · needs a decision · `src/main/java/com/hbm/handler/BlockMigrations.java:43`
  - BlockMigrations.doMigraion loops y upward from the height value (infinite loop). The empty migration body iterates `for(y = heightValue; y >= 0; y++)`, so if it were ever invoked it would spin forever (y only stops at int overflow). Currently unreachable because of the -1 stamp bug, so the two defects mask each other.
  - Evidence: `for(int y = chunk.getHeightValue(x, z); y >= 0; y++) { }`
  - Fix: Change to y-- (and add a body) before ever wiring buildNumber() in.
  - Plan: decision D1
  - Status: open
- **B-002** · crash · fix in the fork · `src/main/java/com/hbm/potion/HbmPotion.java:86-99`
  - HbmPotion.registerPotion sizes potionTypes to max(256, id) and swallows errors. A configured potion id >= 256 (8.xx keys are unbounded ints) makes the resized array too short by one (`new Potion[Math.max(256, id)]` cannot hold index id), so the Potion constructor throws AIOOBE; a failed reflective resize is also silently ignored by the empty catch and crashes later.
  - Evidence: `Potion[] newArray = new Potion[Math.max(256, id)]; ... } catch (Exception e) { }`
  - Fix: Use Math.max(256, id + 1) and log the caught exception via MainRegistry.logger.
  - Plan: Phase 2
  - Status: open
- **B-003** · save data · fix in the fork · `src/main/java/com/hbm/extprop/HbmLivingProps.java:496-498`
  - ContaminationEffect.load reads time/maxTime/ignoreArmor from the parent compound. load() fetches the child compound `me` but reads maxTime, time and ignoreArmor from the parent `nbt`, so every contamination effect restored from NBT (loadNBTData L442-445) comes back with maxTime=0/time=0/ignoreArmor=false; getRad() yields NaN and the effect is dead after a relog.
  - Evidence: `int maxTime = nbt.getInteger("maxTime"); int time = nbt.getInteger("time"); boolean ignoreArmor = nbt.getBoolean("ignoreArmor");`
  - Fix: Read all three fields from `me` (the child compound), matching save().
  - Plan: Phase 2
  - Status: open
- **B-004** · gameplay · fix in the fork · `src/main/java/com/hbm/main/MainRegistry.java:367-377`
  - ForgeChunkManager callback returns after the first NORMAL ticket. ticketsLoaded iterates all tickets of a world but `return`s inside the loop once a NORMAL ticket is handled, so any later tickets in that list (other block loaders and entity IChunkLoader tickets) are never re-initialised on world load and silently stop chunkloading.
  - Evidence: `if(ticket.getType() == ForgeChunkManager.Type.NORMAL) { ChunkLoaderManager.loadTicket(world, ticket); return; }`
  - Fix: Replace `return` with `continue` so every ticket in the list is processed.
  - Plan: Phase 6
  - Status: open
- **B-005** · gameplay · needs a decision · `src/main/java/com/hbm/main/ModEventHandler.java:1664-1670`
  - onPlayerClone resets NBT-only player props on every clone, not only on death. The handler serialises only the serialize() subset (shield, backpack, HUD, reputation, magnet...) and never checks event.wasDeath, so hasWarped, lastDimension, maskManTimer, nitanCount and dash state reset on death AND on End-portal return.
  - Evidence: `HbmPlayerProps.getData(event.original).serialize(buf); HbmPlayerProps.getData(event.entityPlayer).deserialize(buf);`
  - Fix: Copy the full NBT (saveNBTData/loadNBTData) when !event.wasDeath, or add the missing fields to the copy.
  - Plan: decision D2
  - Status: open
- **B-006** · silent misconfig · fix in the fork · `src/main/java/com/hbm/config/FalloutConfigJSON.java:292`
  - FalloutConfigJSON reads matchesMaterial from the mustBeOpaque key. readEntry checks obj.has("matchesMaterial") but then reads obj.get("mustBeOpaque"); a user hbmFallout.json entry with matchesMaterial gets a null/garbage material (or NPE when mustBeOpaque is absent), so the filter silently never matches.
  - Evidence: `if(obj.has("matchesMaterial")) entry.mMa(matNames.get(obj.get("mustBeOpaque").getAsString()));`
  - Fix: Read obj.get("matchesMaterial").getAsString() instead of "mustBeOpaque".
  - Plan: Phase 6
  - Status: open
- **B-007** · silent misconfig · fix in the fork · `src/main/java/com/hbm/config/GeneralConfig.java:28,118`
  - GeneralConfig.enableVirus field default (true) contradicts config default. The static initialiser says true but the hbm.cfg default is false; anything reading the field before loadConfig (or a config read failure) sees the opposite value from a fresh install.
  - Evidence: `public static boolean enableVirus = true; ... config.get(CATEGORY_GENERAL, "1.21_enableVirus", false, ...).getBoolean(false);`
  - Fix: Align the field initialiser with the config default.
  - Plan: Phase 6
  - Status: open
- **B-008** · silent misconfig · needs a decision · `src/main/java/com/hbm/handler/HTTPHandler.java:43-58`
  - HTTPHandler version check compares against JameH2's RefStrings on GitHub. loadStats downloads RefStrings.java from JameH2/space-travel-twopointfive and sets newVersion when the string differs; a fork with its own VERSION always sees 'new version available' and the MOTD links to JameH2 releases.
  - Evidence: `new URL("https://raw.githubusercontent.com/JameH2/Hbm-s-Nuclear-Tech-GIT/space-travel-twopointfive/..."); newVersion = !RefStrings.VERSION.equals(sub)`
  - Fix: Point the URL at the fork's branch or disable the check when VERSION carries a fork marker.
  - Plan: decision D3
  - Status: open
- **B-009** · silent misconfig · fix in the fork · `src/main/java/com/hbm/handler/Identity.java:35`
  - Identity.init writes -1 instead of the new random id to the identity file. When no identity file exists the writer prints `value + ""` (still -1) rather than newValue, so the persisted id is always -1 and clamps to 0 on the next launch; the random id lives only for one session. Identity.value also has no readers anywhere (only MainRegistry L262 calls init).
  - Evidence: `int newValue = new Random().nextInt(MAX + 1); printer.write(value + "");`
  - Fix: printer.write(newValue + ""); or drop the class if the id is never consumed.
  - Plan: Phase 6
  - Status: open
- **B-010** · silent misconfig · fix in the fork · `src/main/java/com/hbm/main/MainRegistry.java:254,682`
  - Two FMLPreInitializationEvent handlers (PreLoad, preInit) dispatched unordered. FML 1.7.10 dispatches @EventHandler methods in reflection order; nothing currently cross-depends, but config is loaded in PreLoad and buses/packets are registered in preInit, so any future dependency between them is a latent ordering bug.
  - Evidence: `public void PreLoad(FMLPreInitializationEvent PreEvent) { ... public void preInit(FMLPreInitializationEvent event) {`
  - Fix: Merge preInit into PreLoad (or call it explicitly from PreLoad) to make the order deterministic.
  - Plan: Phase 8
  - Status: open
- **B-011** · silent misconfig · fix in the fork · `src/main/java/com/hbm/main/MainRegistry.java:596`
  - ClientConfig.initConfig runs on dedicated servers, writing hbmClient.json. PostLoad calls ClientConfig.initConfig() and ServerConfig.initConfig() on both sides; a dedicated server therefore creates and rewrites config/hbmConfig/hbmClient.json every start (harmless but misleading to admins).
  - Evidence: `ClientConfig.initConfig(); ServerConfig.initConfig();`
  - Fix: Guard the client call with FMLCommonHandler side check.
  - Plan: Phase 6
  - Status: open
- **B-012** · dead code · fix in the fork · `src/main/java/com/hbm/extprop/HbmPlayerProps.java:225-227`
  - HbmPlayerProps/HbmLivingProps NBT persistence methods are marked @Deprecated. Both IExtendedEntityProperties overrides carry @Deprecated although Forge calls them for every save/load and the mod has no other persistence for these props; the marker misleads editors into thinking the methods are inert.
  - Evidence: `@Deprecated @Override public void saveNBTData(NBTTagCompound nbt) {`
  - Fix: Remove the misleading @Deprecated or add a comment explaining why it is there.
  - Plan: Phase 7
  - Status: open
- **B-013** · dead code · needs a decision · `src/main/java/com/hbm/handler/BlockMigrations.java:14-36`
  - BlockMigrations.buildNumber() is never called so chunks are stamped -1. The lazy accessor parsing RefStrings.VERSION has zero callers; onChunkLoad/onChunkSave read the raw field which stays -1, so every chunk is stamped -1 and `prevBuildNo != buildNumber` never fires. Migrations are dead; enabling the accessor alone would trigger doMigraion on every old chunk.
  - Evidence: `private static int buildNumber = -1; ... if(prevBuildNo != buildNumber) ... event.getData().setInteger(NBT_KEY_BUILD_NUMBER, buildNumber);`
  - Fix: Call buildNumber() in both handlers only together with a working doMigraion; otherwise leave the stub inert.
  - Plan: decision D1
  - Status: open
- **B-014** · dead code · fix in the fork · `src/main/java/com/hbm/handler/HbmKeybinds.java:40,67,229`
  - EnumKeybind.SLAM / slamKey is registered but has no handler or proxy case. slamKey (LCONTROL) is registered with ClientRegistry and SLAM exists in the enum, but ClientProxy.getIsKeyPressed has no case for it and nothing else references it, so the key shows in Controls but does nothing.
  - Evidence: `public static KeyBinding slamKey = new KeyBinding(category + ".slamkey", Keyboard.KEY_LCONTROL, category); / SLAM,`
  - Fix: Either add the ClientProxy case + server handler or stop registering the key (keep the enum ordinal).
  - Plan: Phase 7
  - Status: open
- **B-015** · dead code · fix in the fork · `src/main/java/com/hbm/main/MainRegistry.java:674,748`
  - CommandWikiRender is registered both as a client and as a server command. PostLoad registers it via ClientCommandHandler and serverStart registers it again as a server command with an in-source TODO; on the integrated server the client handler wins and the server copy is dead, on a dedicated server a render command runs server-side.
  - Evidence: `CommandWikiRender.register(); ... event.registerServerCommand(new CommandWikiRender()); // TODO: make this shitfuck be clientside`
  - Fix: Drop the server registration once the command is confirmed client-only.
  - Plan: Phase 7
  - Status: open
- **B-016** · dead code · needs a decision · `src/main/java/com/hbm/main/ModEventHandler.java:840-858`
  - ModEventHandler.worldTick keeps a dead reflective `reference` field hack. A public static Field `reference` is always null; if any code ever set it, the tick handler would randomly rewrite that static float around PI and dismount riding players every minute. Pure latent behaviour with no setter in the tree.
  - Evidence: `public static Field reference = null; ... if(reference != null) { ... reference.setFloat(null, (float) (rand.nextGaussian() * 0.1 + Math.PI)); }`
  - Fix: Delete the block (or keep as upstream easter egg).
  - Plan: decision D31
  - Status: open
### api.hbm / cofh / repo meta (`api`)

- **B-017** · crash · fix in the fork · `src/main/java/api/hbm/fluidmk2/FluidNetMK2.java:24-25,45-49`
  - FluidNetMK2 pressure arrays are fixed at HIGHEST_VALID_PRESSURE+1 = 6. providers/receivers/fluidAvailable arrays are sized 6; any tank or provider reporting a pressure range above 5 indexes out of bounds in setupFluidProviders/Receivers. Latent: no in-tree pressure exceeds 5, but the API exposes no guard.
  - Evidence: `for(int i = 0; i < IFluidUserMK2.HIGHEST_VALID_PRESSURE + 1; i++) providers[i] = new ArrayList();`
  - Fix: Clamp pressure ranges to 0..HIGHEST_VALID_PRESSURE in setupFluid*.
  - Plan: Phase 5
  - Status: open
- **B-018** · gameplay · needs a decision · `src/main/java/api/hbm/energymk2/IEnergyProviderMK2.java:43-52`
  - IEnergyProviderMK2.tryProvide pushes directly to any receiver, conductor or not. The direct branch runs for every te instanceof IEnergyReceiverMK2 independent of the conductor branch above, so a tile that is both conductor and receiver is fed twice per tick unless it overrides allowDirectProvision() to false (TileEntityBatteryBase does; new machine-as-link tiles will not).
  - Evidence: `if(te instanceof IEnergyReceiverMK2 && te != this) { ... if(rec.canConnect(dir.getOpposite()) && rec.allowDirectProvision()) {`
  - Fix: Skip the direct branch when te is an IEnergyConductorMK2, or document the allowDirectProvision requirement.
  - Plan: decision D5
  - Status: open
- **B-019** · gameplay · fix in the fork · `src/main/java/api/hbm/energymk2/IEnergyReceiverMK2.java:70-82`
  - IEnergyReceiverMK2.tryUnsubscribe uses createNode() instead of getNode (no-op). tryUnsubscribe calls con.createNode(), whose net is always null, so the removeReceiver branch never runs and the receiver stays subscribed until the 3 s timeout. Only caller is MachineCapacitor L208, which expects an immediate detach when its output side changes.
  - Evidence: `PowerNode node = con.createNode(); if(node != null && node.net != null) { node.net.removeReceiver(this); }`
  - Fix: Use Nodespace.getNode(world, x, y, z) (or UniNodespace.getNode with THE_POWER_PROVIDER) like the legacy fluid tryUnsubscribe.
  - Plan: Phase 5
  - Status: open
- **B-020** · gameplay · needs a decision · `src/main/java/api/hbm/energymk2/PowerNetMK2.java:128-145`
  - PowerNetMK2.sendPowerDiode lacks isBadLink, zero-demand skip and demand clamp. Unlike update(), the diode path keeps unloaded/invalid receivers (no isBadLink), adds receivers with rec <= 0 to the lists and does not clamp toSend to the receiver's demand (no Math.min(..., entry.getValue())), so diode-fed nets distribute differently from normal nets.
  - Evidence: `if(timestamp - entry.getValue() > timeout) { recIt.remove(); continue; } ... long toSend = (long) Math.max(toTransfer * weight, 0D);`
  - Fix: Mirror update(): add `|| isBadLink(...)`, `if(rec > 0)` and the Math.min clamp.
  - Plan: decision D5
  - Status: open
- **B-021** · gameplay · needs a decision · `src/main/java/api/hbm/energymk2/PowerNetMK2.java:85,149`
  - PowerNetMK2 priority loop subtracts the cumulative energyUsed every tier. Inside the HIGHEST->LOWEST loop `toTransfer -= energyUsed` uses the running total, so after two tiers the higher tier's share is subtracted twice and lower priorities are under-served whenever higher ones consumed anything. Same shape in sendPowerDiode L149.
  - Evidence: `energyUsed += (toSend - entry.getKey().transferPower(toSend)); } toTransfer -= energyUsed;`
  - Fix: Track per-tier usage (`long used = energyUsed - before; toTransfer -= used;`) if the intent is to pass leftovers down; confirm intent against HbmMods master first.
  - Plan: decision D5
  - Status: open
- **B-022** · gameplay · needs a decision · `src/main/java/api/hbm/fluidmk2/FluidNetMK2.java:108`
  - FluidNetMK2.transferFluid subtracts cumulative received[p] per priority tier. totalAvailable -= received[p] uses the cumulative per-pressure total inside the priority loop, over-subtracting for lower priorities exactly like PowerNetMK2; lower-priority fluid receivers get less than the available surplus.
  - Evidence: `received[p] += toSend; ... } totalAvailable -= received[p];`
  - Fix: Subtract only this tier's delta of received[p]; decide together with the power-net fix.
  - Plan: decision D5
  - Status: open
- **B-023** · gameplay · needs a decision · `src/main/java/api/hbm/fluidmk2/IFluidStandardSenderMK2.java:48-57`
  - IFluidStandardSenderMK2.tryProvide direct push has no allowDirectProvision gate. The fluid direct-provision branch only checks rec.canConnect, whereas the power side also requires rec.allowDirectProvision(); a fluid tile that is both pipe-connector and receiver (or one that wants to opt out) cannot prevent being fed directly and via the net in the same tick.
  - Evidence: `if(te != this && te instanceof IFluidReceiverMK2) { IFluidReceiverMK2 rec = ...; if(rec.canConnect(type, dir.getOpposite())) {`
  - Fix: Add an allowDirectProvision()-style default to IFluidReceiverMK2 and check it here.
  - Plan: decision D5
  - Status: open
- **B-024** · gameplay · fix in the fork · `src/main/java/com/hbm/tileentity/machine/TileEntityXenonThruster.java:59-61`
  - TileEntityXenonThruster sets hasRegistered even when not facing prograde. On first server tick the thruster calls registerPropulsion() only if isFacingPrograde() but sets hasRegistered = true regardless, so a thruster placed at the wrong orientation never retries and never contributes thrust until the tile is recreated.
  - Evidence: `if(!hasRegistered) { if(isFacingPrograde()) registerPropulsion(); hasRegistered = true;`
  - Fix: Set hasRegistered = true only inside the isFacingPrograde() branch.
  - Plan: Phase 6
  - Status: open
- **B-025** · silent misconfig · needs a decision · `.editorconfig:5`
  - .editorconfig declares CRLF for every file but the tree is mostly LF. `end_of_line = crlf` under [*] while api/ is 100% LF, cofh/ 100% CRLF and only ~580 files are CRLF overall; an editor honouring the config rewrites whole files to CRLF and produces noisy diffs and merge conflicts.
  - Evidence: `[*] ... end_of_line = crlf / [{*.info,*.mcmeta,*.cfg}] end_of_line = lf`
  - Fix: Leave the file (upstream-owned) and keep the per-file rule from CLAUDE.md; optionally add `.gitattributes` text=auto entries.
  - Plan: Phase 0
  - Status: fixed 27811a59 + 22c9c89c (no-marker; LF normalisation and .gitattributes policy)
- **B-026** · silent misconfig · needs a decision · `.github/workflows/build.yml:15-17,48`
  - CI workflow only triggers on space-travel-twopointfive and hard-codes 1.0.27. Push/PR filters name only the main branch so claude/* staging branches never build; the sed rewrites RefStrings.VERSION with a literal `1.0.27` that is independent of gradle.properties mod_version, so a version bump in one place silently diverges.
  - Evidence: `branches: [ "space-travel-twopointfive" ] ... VERSION = \"1.0.27 BETA ($days)\"`
  - Fix: Add `claude/**` to the branch filters and derive the prefix from gradle.properties.
  - Plan: decision D4
  - Status: open
- **B-027** · silent misconfig · fix in the fork · `src/main/java/api/hbm/block/IToolable.java:32-45`
  - ToolType.getType freezes its lookup map on first call, ignoring later register(). The map is filled lazily and every later call short-circuits on !map.isEmpty(); any ToolType.register(stack) executed after the first getType (e.g. an addon item constructed late, or /ntmreload paths) is silently never recognised as a tool.
  - Evidence: `if(!map.isEmpty()) { return map.get(new ComparableStack(stack)); }`
  - Fix: Have register() insert into the map directly (or clear the map on register).
  - Plan: Phase 6
  - Status: open
- **B-028** · leak / perf · fix in the fork · `src/main/java/api/hbm/energymk2/IBatteryItem.java:31-34`
  - IBatteryItem.emptyBattery copies the stack twice. stackOut is already a copy of the input; returning stackOut.copy() allocates a second ItemStack and NBT for every call (used in recipe/NEI listings).
  - Evidence: `ItemStack stackOut = stack.copy(); ... return stackOut.copy();`
  - Fix: return stackOut;
  - Plan: Phase 6
  - Status: open
- **B-029** · leak / perf · needs a decision · `src/main/java/api/hbm/energymk2/PowerNetMK2.java:31-32`
  - PowerNetMK2/FluidNetMK2.update return early on one-sided nets (stale entries). Both update() methods return before the timeout/isBadLink purge when either providerEntries or receiverEntries is empty (FluidNetMK2 L33-34 identical), so unloaded or removed subscribers linger in the other map until the net gains both sides or is destroyed.
  - Evidence: `if(providerEntries.isEmpty()) return; if(receiverEntries.isEmpty()) return;`
  - Fix: Run the purge loops before the early return.
  - Plan: decision D5
  - Status: open
- **B-030** · leak / perf · needs a decision · `src/main/java/api/hbm/tile/ILoadedTile.java:20,48`
  - ILoadedTile.TileAccessCache is a static map that is never evicted. Every trySubscribe/tryProvide lookup puts a new entry keyed by (x,y,z,dim); entries are only overwritten on expiry and nothing ever calls clear()/remove(), so the map grows with every position ever probed and holds TileEntity references of invalidated tiles for the JVM lifetime.
  - Evidence: `public static Map<Quartet, TileAccessCache> cache = new HashMap(); ... TileAccessCache.cache.put(publicCumRag.clone(), cache);`
  - Fix: Evict expired entries periodically (e.g. from UniNodespace's 5-minute reaper) and clear on WorldEvent.Unload.
  - Plan: Phase 5 after D6
  - Status: open
- **B-031** · dead code · fix in the fork · `src/main/java/api/hbm/block/IRadioControllable.java:1`
  - Dead API interfaces with zero implementers; two lack @Deprecated. IRadioControllable and IGunHUDProvider (api/hbm/item) have no references anywhere and no @Deprecated marker; IDrillInteraction/IMiningDrill reference only each other and IFluidConnector (api/hbm/fluid) is unreferenced (all three @Deprecated). Addon authors may implement them expecting behaviour.
  - Evidence: `grep: IRadioControllable 0 refs, IGunHUDProvider 0 refs, IFluidConnector 0 refs outside their own files`
  - Fix: Mark IRadioControllable/IGunHUDProvider @Deprecated; keep files for addon binary compat.
  - Plan: Phase 7
  - Status: open
- **B-032** · dead code · fix in the fork · `src/main/java/api/hbm/energymk2/Nodespace.java:24-33`
  - @Deprecated Nodespace statics are still the API's own subscription path. getNode/createNode/destroyNode are @Deprecated but IEnergyReceiverMK2.trySubscribe (L48), IEnergyProviderMK2.tryProvide (L34) and ~99 call sites in com/ use them; the marker cannot be honoured and misleads cleanups.
  - Evidence: `@Deprecated public static PowerNode getNode(World world, int x, int y, int z) {`
  - Fix: Remove the @Deprecated markers or migrate the API defaults to UniNodespace.
  - Plan: Phase 7
  - Status: open
- **B-033** · dead code · leave · `src/main/java/api/hbm/entity/RadarEntry.java:47-65`
  - RadarEntry.fromBytes/toBytes omit the redstone field. The sync methods serialise name/level/pos/dim/entityID but not `redstone`; the client-side copy always has redstone=false. Server-side redstone output (TileEntityMachineRadarNT L385/401) uses the unsynced list, so this is currently harmless but the API contract is incomplete.
  - Evidence: `public void toBytes(ByteBuf buf) { ByteBufUtils.writeUTF8String(buf, this.unlocalizedName); ... buf.writeInt(this.entityID); }`
  - Fix: Append writeBoolean/readBoolean(redstone) if clients ever need it.
  - Plan: not planned
  - Status: open
- **B-034** · dead code · fix in the fork · `src/main/java/api/hbm/fluidmk2/FluidNetMK2.java:49`
  - FluidNetMK2.transfered[] is allocated and reset but never written. The public array is reset in cleanUp() but no code writes it (transferFluid uses a local received[]), so it is a dead field that misleads readers into thinking per-pressure transfer stats are tracked.
  - Evidence: `public long[] transfered = new long[IFluidUserMK2.HIGHEST_VALID_PRESSURE + 1];`
  - Fix: Delete the field or populate it from received[].
  - Plan: Phase 7
  - Status: open
- **B-035** · dead code · fix in the fork · `src/main/java/api/hbm/ntl/StackCache.java:181`
  - StackCache.CacheSlot.reCount is self-documented as unused. The method recomputes stacksize from monitors but the comment says it is not used and nothing calls it; the NTL cache relies on incremental updates being always correct with no reconciliation path.
  - Evidence: `// not actually used, and probably not needed. ... public void reCount() {`
  - Fix: Either call it on joinNetworks/onNewCacheHasJoined as a consistency check or remove it.
  - Plan: Phase 7
  - Status: open
- **B-036** · dead code · leave · `src/main/java/cofh/api/energy/TileEnergyHandler.java:1`
  - Vendored cofh TileEnergyHandler and ItemEnergyContainer are unused. The two CoFH reference implementations have zero references in com/ or api/; only the interfaces are consumed (converters, EMP, ExplosionNukeGeneric). They add surface to keep in sync with upstream CoFH.
  - Evidence: `grep: TileEnergyHandler 0 refs, ItemEnergyContainer 0 refs`
  - Fix: Keep for CoFHAPI @API completeness (removing changes the provided API set).
  - Plan: not planned
  - Status: open
### Blocks: generic (`blocks-generic`)

- **B-037** · crash · fix in the fork · `src/main/java/com/hbm/blocks/fluid/CoriumFluid.java:23-29`
  - CoriumFluid returns icons of never-instantiated CoriumBlock (null). CoriumFluid.getStillIcon/getFlowingIcon return CoriumBlock.stillIcon/flowingIcon, but CoriumBlock is never constructed (corium_block is a CoriumFinite), so the registered Forge fluid corium_fluid has null icons; any third-party fluid renderer calling Fluid.getIcon() can NPE.
  - Evidence: `return CoriumBlock.stillIcon; // CoriumBlock has no new CoriumBlock anywhere`
  - Fix: Point CoriumFluid at the CoriumFinite block's icons (block.getIcon(0,0)/getIcon(1,0) like GenericFluid) and delete CoriumBlock.
  - Plan: Phase 2
  - Status: open
- **B-038** · crash · fix in the fork · `src/main/java/com/hbm/items/block/ItemModSlab.java:14-31`
  - ItemModSlab hard-codes slab pairs; unknown slab yields null ItemSlab refs. shittyFuckingHackSingle/Double return null for any slab not in the four hard-coded pairs, and the ctor passes those into ItemSlab; a new BlockMultiSlab registered with ItemModSlab would NPE on naming/placement. All four currently registered pairs (ModBlocks:3080-3091) are covered.
  - Evidence: `super(block, shittyFuckingHackSingle(block), shittyFuckingHackDouble(block), ...); ... return null;`
  - Fix: Store the pair on BlockMultiSlab (single/double refs) and read it in the ItemModSlab ctor.
  - Plan: Phase 8
  - Status: open
- **B-039** · gameplay · fix in the fork · `src/main/java/com/hbm/blocks/fluid/CoriumFinite.java:56-64`
  - CoriumFinite.updateTick deletes the block on ClassCastException. Documented CoFH band-aid: any ClassCastException from BlockFluidFinite.updateTick is swallowed and the corium block is set to air server-side, so under the conflict corium silently disappears instead of flowing.
  - Evidence: `try { super.updateTick(world, x, y, z, rand); } catch(ClassCastException ex) { if(!world.isRemote) world.setBlockToAir(x, y, z); }`
  - Fix: Leave (upstream workaround); log via MainRegistry.logger if it ever fires.
  - Plan: Phase 6
  - Status: open
- **B-040** · gameplay · fix in the fork · `src/main/java/com/hbm/blocks/fluid/GenericFiniteFluid.java:19-45`
  - GenericFiniteFluid static icons shared by concrete_liquid and corium_block. stillIcon/flowingIcon are static; both finite fluid blocks (concrete_liquid ModBlocks:2659, corium_block/CoriumFinite ModBlocks:2636) write the same pair in registerBlockIcons, so whichever registers last textures both blocks.
  - Evidence: `public static IIcon stillIcon; ... stillIcon = register.registerIcon(RefStrings.MODID + ":" + stillName);`
  - Fix: Make stillIcon/flowingIcon instance fields (as GenericFluidBlock does) so each finite fluid keeps its own icons.
  - Plan: Phase 6
  - Status: open
- **B-041** · gameplay · fix in the fork · `src/main/java/com/hbm/blocks/fluid/GenericFluidBlock.java:68-70`
  - GenericFluidBlock damage only applied for sulfuric_acid_block. onEntityCollidedWithBlock gates everything on `this == ModBlocks.sulfuric_acid_block`, so mercury_block (ModBlocks:2652), bromine_block (:2656) and ccl_block (:2667) call setDamage(...) but never hurt entities; the setting is silently inert.
  - Evidence: `if(damageSource != null) { if(this == ModBlocks.sulfuric_acid_block) {`
  - Fix: Remove the identity gate around the damage/slowdown code and keep only the slime-ball achievement branch gated on sulfuric_acid_block.
  - Plan: Phase 6
  - Status: open
- **B-042** · silent misconfig · needs a decision · `src/main/java/com/hbm/blocks/ModBlocks.java:3110`
  - 32 blocks implement ITooltipProvider/IBlockMulti but use the vanilla ItemBlock. Only ItemBlockBase is an `instanceof ITooltipProvider` site (ItemBlockBase:67,85). asphalt_stairs (BlockSpeedyStairs:36, reg :3110) loses its speed tooltip, steel_grate (BlockGrate:168, reg :3177) its .desc (lang key missing), spike_cacti (:3302) its subtypes; 28 hazard/pillar blocks lose rarity.
  - Evidence: `GameRegistry.registerBlock(asphalt_stairs, asphalt_stairs.getUnlocalizedName()); // BlockSpeedyStairs.addInformation never runs`
  - Fix: Switch asphalt_stairs, steel_grate (add tile.steel_grate.desc) and spike_cacti to register(x); optionally the hazard blocks for rarity colours.
  - Plan: decision D7
  - Status: open
- **B-043** · silent misconfig · fix in the fork · `src/main/java/com/hbm/blocks/gas/BlockGasBase.java:102`
  - BlockGasBase.updateTick clears world-global scheduledUpdatesAreImmediate. Every gas tick force-sets world.scheduledUpdatesAreImmediate = false as a band-aid; the only code setting it true is WorldGenLiquidsCelestial (dim/WorldGenLiquidsCelestial.java:68-70) around its generation, so a gas tick in that window silently disables the generator's immediate updates.
  - Evidence: `world.scheduledUpdatesAreImmediate = false; //prevent recursive loop when some dumbass forgets to clean up immediate updating`
  - Fix: Leave; if touched, remove the line and make WorldGenLiquidsCelestial restore the flag in a finally block.
  - Plan: Phase 8
  - Status: open
- **B-044** · silent misconfig · needs a decision · `src/main/java/com/hbm/main/ModEventHandlerClient.java:225`
  - All ILookOverlay HUDs gated on ClientConfig.DODD_RBMK_DIAGNOSTIC. The crosshair dispatch for every block/item ILookOverlay (machines, tanks, BlockRemap...) sits inside `event.type == CROSSHAIRS && ClientConfig.DODD_RBMK_DIAGNOSTIC.get()`; the option (ClientConfig:28, default true) is named as an RBMK diagnostic, so turning it off silently kills all look-overlays.
  - Evidence: `if(event.type == ElementType.CROSSHAIRS && ClientConfig.DODD_RBMK_DIAGNOSTIC.get()) {`
  - Fix: Gate only TileEntityRBMKBase.diagnosticPrintHook on DODD_RBMK_DIAGNOSTIC and run the generic ILookOverlay dispatch unconditionally (or under its own option).
  - Plan: decision D7
  - Status: open
- **B-045** · dead code · fix in the fork · `src/main/java/com/hbm/blocks/BlockBase.java:36-41`
  - BlockBase.setBeaconable()/noMobSpawn() have no call sites. Both fluent setters are unused repo-wide; the codebase uses BlockBeaconable, BlockHazard.makeBeaconable() and BlockNoSpawn instead, so the flags they set are never exercised.
  - Evidence: `public BlockBase setBeaconable() { ... public BlockBase noMobSpawn() {`
  - Fix: Delete both methods and their fields, or migrate the three special blocks to them.
  - Plan: Phase 7
  - Status: open
- **B-046** · dead code · fix in the fork · `src/main/java/com/hbm/blocks/ModBlocks.java:4052-4055`
  - Block rename compat path is dead: addRemap unused, no BLOCK remap branch. ModBlocks.addRemap (only public BlockRemap entry) has zero callers and MainRegistry.handleMissingMappings only ignores names or remaps GameRegistry.Type.ITEM (MainRegistry:1752-1754); BlockRemap converts only on random tick (BlockRemap:24). A renamed block silently vanishes from worlds.
  - Evidence: `public static void addRemap(String unloc, Block block, int meta) { ... } // no callers; mapping.type == GameRegistry.Type.ITEM only`
  - Fix: Add a BLOCK branch to handleMissingMappings (remapBlocks map) or document that setBlockName must never change.
  - Plan: Phase 8
  - Status: open
- **B-047** · dead code · needs a decision · `src/main/java/com/hbm/blocks/generic/BlockHazard.java:162-163`
  - BlockHazard.getRarity lists block_schraranium twice. Duplicated condition in the rare-rarity chain; harmless today but probably a copy-paste slot meant for another block (block_schrabidium?). Rarity is only rendered through ItemBlockBase anyway.
  - Evidence: `if(this == ModBlocks.block_schraranium || this == ModBlocks.block_schraranium`
  - Fix: Replace the duplicate with the intended block or drop it.
  - Plan: decision D32
  - Status: open
- **B-048** · dead code · fix in the fork · `src/main/java/com/hbm/blocks/turret/TurretBase.java:1`
  - blocks/turret/TurretBase.java is dead (zero subclasses). Legacy abstract turret block with executeHoldAction/executeReleaseAction; `grep -rn 'extends TurretBase\b'` finds nothing. All live turrets extend TurretBaseNT, BlockDummyable or BlockContainer.
  - Evidence: `grep -rn 'extends TurretBase\b' src/main/java -> no matches`
  - Fix: Delete the class.
  - Plan: Phase 7
  - Status: open
### Blocks: machines, bombs, networks (`blocks-machine`)

- **B-049** · crash · needs a decision · `src/main/java/com/hbm/blocks/BlockDummyable.java:174`
  - BlockDummyable.positions/internalPlayers: unsynchronized per-Block scratch state. findCore clears/appends the instance list `positions` (170-201); onBlockPlacedBy/addCollisionBoxesToList use `internalPlayers` (492-497). One Block instance serves the client render and integrated-server threads, so concurrent findCore calls can throw ConcurrentModificationException.
  - Evidence: `List<ThreeInts> positions = new ArrayList<>(); ... private List<EntityPlayer> internalPlayers = new ArrayList<>();`
  - Fix: Make `positions` a local (pass a Set through findCoreRec) and key internalPlayers per world or guard with a ThreadLocal.
  - Plan: decision D8
  - Status: open
- **B-050** · gameplay · fix in the fork · `src/main/java/com/hbm/blocks/BlockDummyable.java:757-760`
  - Placement preview rotates with `facing` while the footprint uses getDirModified. drawPlacementHighlight computes dir=getDirModified(facing) (696) but rotates dims/offsets with `facing`, while onBlockPlacedBy checks/fills with `dir` (240,260,285). MachineHTRF4:51, HTR3:94, HTRFNeo:53, LPW2:46 rotate 90 degrees with asymmetric dims, so their wireframe is 90 degrees off.
  - Evidence: `int[] rot = MultiblockHandlerXR.rotate(dims, facing); // checkRequirement(..., dir, o) uses dir`
  - Fix: Use `dir` (and dir.getRotation(UP)) instead of `facing` for the highlight rotation and lateral offsets.
  - Plan: Phase 6
  - Status: open
- **B-051** · gameplay · fix in the fork · `src/main/java/com/hbm/blocks/machine/MachineCoker.java:34`
  - 14 blocks pass the clicked side as GUI id to standardOpenBehavior. MilkReformer:31, VacuumDistill:27, Annihilator:29, BlockPAQuadrupole/Detector/Source/Dipole/RFC:36, Hydrotreater:35, OreSlopper:52, Coker:34, CatalyticReformer:36, BatteryREDD:39, BatterySocket:36 forward `side` (0-5) as ID. Latent: only RadarNT, Electrolyser and PneumoTube TEs branch on ID.
  - Evidence: `return standardOpenBehavior(world, x, y, z, player, side);`
  - Fix: Pass 0 in all 14 call sites.
  - Plan: Phase 6
  - Status: open
- **B-052** · silent misconfig · needs a decision · `src/main/java/com/hbm/blocks/BlockDummyable.java:71-80`
  - BlockDummyable.safeRem/overrideTileMeta statics toggled without try/finally. safeRem is flipped around world.setBlock in onBlockPlacedBy (219-221), makeExtra (351-353), removeExtra (367-369) and several tiles; an exception inside leaves safeRem=true for every multiblock in the JVM (cascade removal silently off). setOverride()/resetOverride() (75-80) have zero callers.
  - Evidence: `public static int overrideTileMeta = 0; public static boolean safeRem = false; ... safeRem = true; world.setBlock(...); safeRem = false;`
  - Fix: Wrap every safeRem toggle in try/finally; delete setOverride/resetOverride or give them a caller.
  - Plan: decision D8
  - Status: open
- **B-053** · silent misconfig · fix in the fork · `src/main/java/com/hbm/blocks/BlockDummyableBeam.java:40`
  - BlockDummyableBeam.findCore(World) is an overload, not an override. Base findCore is findCore(IBlockAccess,...) (BlockDummyable:169); the beam only overrides the World signature, so any caller passing an IBlockAccess/ChunkCache hits the base walk with the beam's {0,...} dims and gets null instead of the adjacent core.
  - Evidence: `public int[] findCore(World world, int x, int y, int z) { // base: public int[] findCore(IBlockAccess world, ...)`
  - Fix: Change the signature to findCore(IBlockAccess world, ...) with @Override.
  - Plan: Phase 6
  - Status: open
- **B-054** · leak / perf · fix in the fork · `src/main/java/com/hbm/blocks/machine/BlockMachineBase.java:51-56`
  - BlockMachineBase.breakBlock skips super when TE is not ISidedInventory. Early `return` before super.breakBlock skips BlockContainer.breakBlock/world.removeTileEntity for a non-ISidedInventory TE, leaving an orphan TE ticking on air. Latent: all 8 subclasses (WarController, NukeAntimatter/Balefire, Diesel, Microwave, StorageDrum, PneumoStorage*) use MachineBase TEs.
  - Evidence: `if(!(te instanceof ISidedInventory)) return; ... super.breakBlock(world, x, y, z, block, meta); // line 98`
  - Fix: Replace the early return with an `if(te instanceof ISidedInventory) { spill }` guard so super.breakBlock always runs.
  - Plan: Phase 6
  - Status: open
### Tile entities: base and machines (`te-machine`)

- **B-055** · save data · fix in the fork · `src/main/java/com/hbm/inventory/fluid/tank/FluidTank.java:264-268`
  - FluidTank.readFromNBT zeroes fill when `<key>_max` is absent. fill is clamped to the NBT `_max` value (local int), not the tank's maxFluid; a compound written without `_max` (older saves, hand-written or copied NBT) yields max=0 and the stored fluid is silently discarded.
  - Evidence: `int max = nbt.getInteger(s + "_max"); if(max > 0) maxFluid = max; fluid = MathHelper.clamp_int(fluid, 0, max);`
  - Fix: Clamp to `max > 0 ? max : maxFluid`.
  - Plan: Phase 2
  - Status: open
- **B-056** · gameplay · fix in the fork · `src/main/java/com/hbm/inventory/UpgradeManagerNT.java:57-66`
  - UpgradeManagerNT.mutexType never reset; re-inserted mutex upgrade ignored. checkSlotsInternal clears `upgrades` on every re-scan but never clears mutexType, and the comparison is strict `>`; once a mutex upgrade (LM_SMELTER/SHREDDER/CENTRIFUGE/CRYSTALLIZER) was seen, removing it and inserting the same or a lower-ordinal one is ignored until the TE is recreated.
  - Evidence: `upgrades.clear(); ... if(mutexType == null) {...} else if(item.type.ordinal() > mutexType.ordinal()) {`
  - Fix: Set mutexType = null right after upgrades.clear().
  - Plan: Phase 6
  - Status: open
- **B-057** · gameplay · fix in the fork · `src/main/java/com/hbm/tileentity/TileEntityLoadedBase.java:95-101`
  - networkPackNT dedupe without getDescriptionPacket delays first sync up to 1 s. Identical buffers are skipped except on ticks where worldTime%20==0 and TileEntityLoadedBase never overrides getDescriptionPacket, so a client that (re)loads a chunk sees default tile state (empty barrels, idle machines) for up to a second. Documented in the code comment.
  - Evidence: `if(preBuf.equals(lastPackedBuf) && this.worldObj.getTotalWorldTime() % 20 != 0) return;`
  - Fix: Leave; a real fix is getDescriptionPacket/onDataPacket carrying the serialized buffer.
  - Plan: Phase 8
  - Status: open
- **B-058** · gameplay · fix in the fork · `src/main/java/com/hbm/tileentity/machine/TileEntityMachineElectricFurnace.java:180`
  - Electric furnace can take ~60 ticks to see a new cable. updateConnections runs only every 40 ticks and ILoadedTile.TileAccessCache caches null lookups for 20 ticks (ILoadedTile.java:22), so a cable placed next to the furnace can take up to ~3 s to connect while the how-to says to subscribe every tick.
  - Evidence: `if(worldObj.getTotalWorldTime() % 40 == 0) this.updateConnections();`
  - Fix: Leave, or subscribe every 20 ticks like other machines.
  - Plan: Phase 6
  - Status: open
- **B-059** · silent misconfig · fix in the fork · `src/main/java/com/hbm/config/MachineDynConfig.java:41`
  - MachineDynConfig swallows newInstance() failures silently. Each IConfigurableMachine is instantiated inside a lambda with an empty catch; a missing public no-arg ctor or a ctor side effect silently drops that machine from hbmMachines.json and its readIfPresent/writeConfig never run.
  - Evidence: `TileMappings.configurables.forEach(x -> { try { dummies.add(x.newInstance()); } catch(Exception ex) {} });`
  - Fix: Log via MainRegistry.logger.error("[Config] could not instantiate " + x.getName(), ex) in the catch.
  - Plan: Phase 6
  - Status: open
- **B-060** · silent misconfig · fix in the fork · `src/main/java/com/hbm/tileentity/machine/TileEntityMachineCentrifuge.java:387-388`
  - TileEntityMachineCentrifuge.provideExtraInfo writes two types under B_ACTIVE. setBoolean then setInteger with the same CompatEnergyControl.B_ACTIVE key; the int overwrites the boolean, so Energy Control readers asking for the boolean get an NBT type mismatch (reads as false/0).
  - Evidence: `data.setBoolean(CompatEnergyControl.B_ACTIVE, this.progress > 0); data.setInteger(CompatEnergyControl.B_ACTIVE, this.progress);`
  - Fix: Write the progress under CompatEnergyControl.I_PROGRESS (or drop the int line).
  - Plan: Phase 6
  - Status: open
- **B-061** · leak / perf · fix in the fork · `src/main/java/com/hbm/tileentity/TileEntityLoadedBase.java:36-39`
  - 25 onChunkUnload overrides skip super, so isLoaded never becomes false. Only TileEntityLoadedBase.onChunkUnload sets isLoaded=false; uninos/NodeNet.isBadLink:87 relies on it, so unloaded tiles stay in power/fluid nets. Skipping super: DoorGeneric, AtmosphericEmitter, HeatBoiler(+Industrial), HeaterElectric, AssemblyFactory/Machine, Autosaw and 17 more (see fix).
  - Evidence: `public void onChunkUnload() { super.onChunkUnload(); this.isLoaded = false; } // 25 subclasses omit the super call`
  - Fix: Call super.onChunkUnload() first; rest: Centrifuge, ChemFactory, ChemPlant, CombustionEngine, Crystallizer, Diesel, GasCent, Intake, PrecAss, Thresher, TurbineGas, Turbofan, PWRController, Soyuz
  - Plan: Phase 5
  - Status: open
- **B-062** · dead code · fix in the fork · `src/main/java/com/hbm/tileentity/IRTGUser.java:83-153`
  - IRTGUser interface (with three @Untested overloads) has no implementers. No class implements IRTGUser; RTG tiles (TileEntityRtgFurnace, TileEntityMachineRTG, TileEntityMachineRadiolysis) call util/RTGUtil.updateRTGs instead, so the interface and its @Untested @Beta defaults are dead code.
  - Evidence: `grep -rn 'IRTGUser' src/main/java -> only tileentity/IRTGUser.java`
  - Fix: Delete the interface or move it next to RTGUtil as documentation.
  - Plan: Phase 7
  - Status: open
- **B-063** · dead code · fix in the fork · `src/main/java/com/hbm/tileentity/TileEntityMachineBase.java:140`
  - TileEntityMachineBase.getGaugeScaled takes Forge's FluidTank (dead, misleading). The base class imports net.minecraftforge.fluids.FluidTank (L21) for getGaugeScaled(int, FluidTank); no caller exists (only an unrelated Zirnox overload) and HBM tanks are com.hbm.inventory.fluid.tank.FluidTank, so the method invites a wrong-type mistake.
  - Evidence: `import net.minecraftforge.fluids.FluidTank; ... public int getGaugeScaled(int i, FluidTank tank) {`
  - Fix: Delete the method and import.
  - Plan: Phase 7
  - Status: open
- **B-064** · dead code · needs a decision · `src/main/java/com/hbm/tileentity/machine/TileEntityMachineIGenerator.java:85-210`
  - machine_industrial_generator sits in the creative tab but is fully inert. updateEntity's whole body is a block comment (still referencing the removed networkPack/updateTank API), yet the block is created in machineTab (ModBlocks:2071), registered (:3549) and rendered; its anvil recipe is commented out (AnvilRecipes:440). Players can place a machine that does nothing.
  - Evidence: `public void updateEntity() { /*if(!worldObj.isRemote) { ... }*/ }`
  - Fix: Either hide it (setCreativeTab(null)) or port the commented logic to serialize/networkPackNT.
  - Plan: decision D9
  - Status: open
- **B-065** · dead code · leave · `src/main/java/com/hbm/tileentity/machine/TileEntityMachineWarController.java:40-60`
  - TileEntityMachineWarController is an auto-generated stub (isLoaded() false). getPower/setPower/getMaxPower return 0 and isLoaded() returns false, so the tile can never join a power net; the block war_controller is hidden (ModBlocks:2464 setCreativeTab(null)) but registered (:3474).
  - Evidence: `public boolean isLoaded() { // TODO Auto-generated method stub return false; }`
  - Fix: Leave as dev stub or remove the isLoaded override so the inherited flag is used.
  - Plan: not planned
  - Status: open
### Tile entities: RBMK, fusion, Albion, bombs, turrets (`te-special`)

- **B-066** · crash · fix in the fork · `src/main/java/com/hbm/tileentity/machine/rbmk/TileEntityRBMKControlAuto.java:117-127`
  - TileEntityRBMKControlAuto: null function breaks serialize/deserialize symmetry. readFromNBT sets function=null when the key is absent (91-94) and writeToNBT then never writes it, so it stays null; serialize skips the int when null but deserialize always reads it: BufPacket buffer-underflow warning every tick and NPE in GUIRBMKControlAuto:160 (rod.function.ordinal()).
  - Evidence: `if(function != null) buf.writeInt(function.ordinal()); ... this.function = RBMKFunction.values()[buf.readInt()];`
  - Fix: Default to RBMKFunction.LINEAR in readFromNBT instead of null and always write the ordinal.
  - Plan: Phase 1
  - Status: open
- **B-067** · crash · fix in the fork · `src/main/java/com/hbm/tileentity/machine/rbmk/TileEntityRBMKControlAuto.java:134-135`
  - TileEntityRBMKControlAuto.receiveControl mods function index by RBMKColor count. `Math.abs(x) % RBMKColor.values().length` (5) is used to index RBMKFunction.values() (3 entries); the GUI only sends 0-2 (GUIRBMKControlAuto:135) but any other NBTControlPacket value 3/4 throws ArrayIndexOutOfBoundsException on the server.
  - Evidence: `int c = Math.abs(data.getInteger("function")) % RBMKColor.values().length; this.function = RBMKFunction.values()[c];`
  - Fix: Use RBMKFunction.values().length (or EnumUtil.grabEnumSafely).
  - Plan: Phase 1
  - Status: open
- **B-068** · save data · fix in the fork · `src/main/java/com/hbm/tileentity/machine/TileEntityZirnoxDestroyed.java:30-36`
  - TileEntityZirnoxDestroyed NBT key mismatch: reads "fire", writes "onFire". The burning state is never restored after a chunk reload (getBoolean on the missing key returns false), so a destroyed ZIRNOX always stops burning on the first reload instead of the intended 1/5000-per-tick decay.
  - Evidence: `onFire = nbt.getBoolean("fire"); ... nbt.setBoolean("onFire", onFire);`
  - Fix: Use the same key in both methods (read "onFire" with a fallback to "fire").
  - Plan: Phase 2
  - Status: open
- **B-069** · save data · needs a decision · `src/main/java/com/hbm/tileentity/machine/rbmk/TileEntityRBMKBase.java:305-309`
  - Static `diag` flag makes RBMK writeToNBT skip base/items during client HUD dump. getDiagData (client-only diagnosticPrintHook) sets static `diag` true, calls writeToNBT, resets it; every column's readFromNBT/writeToNBT (268-285, SlottedBase:138-157) skips super and items while the flag is set, so a single-player server-thread save in that window drops base NBT and inventories.
  - Evidence: `public void getDiagData(NBTTagCompound nbt) { diag = true; this.writeToNBT(nbt); diag = false; }`
  - Fix: Write the diagnostic compound through a dedicated writeDiag(nbt) method instead of a shared static flag.
  - Plan: Phase 1 after D10
  - Status: open
- **B-070** · gameplay · needs a decision · `src/main/java/com/hbm/handler/neutron/RBMKNeutronHandler.java:286-290`
  - Blocked or partially blocked streams irradiate the origin column. When a non-RBMK block partially blocks a stream, irradiateFromFlux(pos, hits) is called with `pos` = the emitting column instead of `targetPos`, so radiation is deposited under the reactor rather than at the obstruction (the fully-blocked case at 283 just returns).
  - Evidence: `irradiateFromFlux(pos, hits); ... irradiateFromFlux(pos, 0); // pos = new BlockPos(origin.tile)`
  - Fix: Pass targetPos (copy it first: the iterator reuses one BlockPos instance, NeutronStream:71).
  - Plan: Phase 1 after D10
  - Status: open
- **B-071** · gameplay · needs a decision · `src/main/java/com/hbm/handler/neutron/RBMKNeutronHandler.java:385`
  - RBMK stream tail check (#1933) tests the origin node, so the branch is dead. When the last node is a control rod the code checks `NeutronNodeWorld.getNode(worldObj, pos) == null` with `pos` = the origin column (line 248), always cached (252-260); the intended check on `posAfter` never runs, so flux leaving an edge control rod neither irradiates nor registers the next block.
  - Evidence: `if(NeutronNodeWorld.getNode(worldObj, pos) == null) { TileEntity te = blockPosToTE(worldObj, posAfter);`
  - Fix: Use posAfter in the getNode call.
  - Plan: Phase 1 after D10
  - Status: open
- **B-072** · gameplay · fix in the fork · `src/main/java/com/hbm/tileentity/bomb/TileEntityCompactLauncher.java:291`
  - sendCommandEntity passes posX for both x and z in two launchers. TileEntityCompactLauncher.sendCommandEntity and TileEntityLaunchTable.sendCommandEntity (line 287) call sendCommandPosition(floor(posX), yCoord, floor(posX)); radar/entity-targeted launches from these pads fly to the wrong Z.
  - Evidence: `return sendCommandPosition((int) Math.floor(target.posX), yCoord, (int) Math.floor(target.posX));`
  - Fix: Use target.posZ for the third argument in both files.
  - Plan: Phase 6
  - Status: open
- **B-073** · gameplay · fix in the fork · `src/main/java/com/hbm/tileentity/bomb/TileEntityLaunchPadLarge.java:99`
  - TileEntityLaunchPadLarge tests `erector == 1F` inside the lift-extension branch. In both copies (lines 99 and 134) the lift branch only runs after erector reached 90F, so `erector == 1F` is never true; readyToLoad=true and delay=20 from that path never fire and loading readiness relies solely on the `erector == 90F && lift == 1F` check at line 72 (delay reset is lost).
  - Evidence: `} else if(lift < 1F) { lift = Math.min(lift + liftSpeed, 1F); if(erector == 1F) { readyToLoad = true; delay = 20; } }`
  - Fix: Change both conditions to `lift == 1F`.
  - Plan: Phase 6
  - Status: open
- **B-074** · gameplay · fix in the fork · `src/main/java/com/hbm/tileentity/bomb/TileEntityLaunchPadRocket.java:627-630`
  - TileEntityLaunchPadRocket.getDestination OC callback has inverted hasDrive(). Returns "No destination drive." exactly when a drive IS present and otherwise proceeds to ItemVOTVdrive.getTarget(slots[1], null) with an empty slot, so the OC method never reports the real destination.
  - Evidence: `if(hasDrive()) { // ok maybe I should actually check if there's an item there first return new Object[] {null, "No destination drive."}; }`
  - Fix: Change to `if(!hasDrive())`.
  - Plan: Phase 6
  - Status: open
- **B-075** · gameplay · leave · `src/main/java/com/hbm/tileentity/bomb/TileEntityNukeBoy.java:129-141`
  - Assembly nukes expose no sided slots and have an odd canExtractItem. getAccessibleSlotsFromSide returns an empty array so automation cannot load or unload assembly nukes; canExtractItem (`j != 0 || i != 1 || bucket`) is unreachable. Upstream design.
  - Evidence: `return new int[0]; ... return j != 0 || i != 1 || itemStack.getItem() == Items.bucket;`
  - Fix: Leave.
  - Plan: not planned
  - Status: open
- **B-076** · gameplay · needs a decision · `src/main/java/com/hbm/tileentity/deco/TileEntityTrappedBrick.java:77-78`
  - Trapped brick POISON_DART variant is selectable but unimplemented. TrappedBrick.Trap.POISON_DART (blocks/generic/TrappedBrick.java:144, TrapType.DETECTOR) exists as a placeable subtype, but TileEntityTrappedBrick's trigger switch has only `//TBI` for it, so the trap does nothing when sprung.
  - Evidence: `case POISON_DART: //TBI break;`
  - Fix: Implement the dart (e.g. spawn an EntityArrow with poison) or remove the enum constant from the creative list.
  - Plan: decision D12
  - Status: open
- **B-077** · gameplay · needs a decision · `src/main/java/com/hbm/tileentity/machine/TileEntityMachineReactorBreeding.java:59`
  - Breeding reactor progress uses integer division of flux. `this.flux / BreederRecipes.getOutput(slots[0]).flux` is int/int (BreederRecipe.flux is int, BreederRecipes.java:71), so the speed multiplier truncates (150/100 -> 1); a user JSON recipe with flux 0 would throw ArithmeticException every tick.
  - Evidence: `progress += 0.0025F * (this.flux / BreederRecipes.getOutput(slots[0]).flux);`
  - Fix: Cast to float: `0.0025F * ((float) this.flux / recipe.flux)` and guard recipe.flux > 0.
  - Plan: Phase 1 after D11
  - Status: open
- **B-078** · gameplay · fix in the fork · `src/main/java/com/hbm/tileentity/machine/TileEntityReactorResearch.java:119-124`
  - Research reactor: canExtractItem uses fuelMap.containsValue(stack), always false. ItemStack does not override equals(), so containsValue compares identities against the map's own template stacks and never matches; spent waste plates can never be pulled out by hoppers/automation, only by hand.
  - Evidence: `if(fuelMap.containsValue(stack)) return true;`
  - Fix: Return true when stack.getItem() equals any fuelMap value's item (or `!(stack.getItem() instanceof ItemPlateFuel)`).
  - Plan: Phase 1
  - Status: open
- **B-079** · gameplay · fix in the fork · `src/main/java/com/hbm/tileentity/machine/TileEntityReactorResearch.java:89`
  - Research reactor: isItemValidForSlot only accepts slot 0 (`i < 12 && i <= 0`). canInsertItem delegates to isItemValidForSlot (TileEntityMachineBase:126-128); the condition reduces to i == 0, so hoppers/automation can only load fuel plates into the first of the 12 rod slots even though getAccessibleSlotsFromSide exposes all 12.
  - Evidence: `if(i < 12 && i <= 0) if(itemStack.getItem().getClass() == ItemPlateFuel.class) return true;`
  - Fix: Use `i >= 0 && i < 12`.
  - Plan: Phase 1
  - Status: open
- **B-080** · gameplay · fix in the fork · `src/main/java/com/hbm/tileentity/machine/rbmk/RBMKDials.java:261-266`
  - RBMKDials.getSurgeMod client fallback reads the passive-cooling dial. On the client the method returns GameRuleHelper.getDoubleMinimum(world, KEY_PASSIVE_COOLING, 0) instead of KEY_SURGE_MOD (author comment: whoops!), so client-side control-rod surge rendering/diag uses the wrong number.
  - Evidence: `if(world.isRemote) { return GameRuleHelper.getDoubleMinimum(world, RBMKKeys.KEY_PASSIVE_COOLING, 0.0D); }`
  - Fix: Use RBMKKeys.KEY_SURGE_MOD in the client branch.
  - Plan: Phase 1
  - Status: open
- **B-081** · silent misconfig · needs a decision · `src/main/java/com/hbm/tileentity/machine/TileEntityPWRController.java:678`
  - PWR ROR: value "rods" is 100 - rodLevel while setrods takes raw percent. provideRORValue returns the inverted level (author comment: why the fuck did i invert this again?) whereas runRORFunction setrods (690-693) assigns the raw percent to rodTarget and OC getters return rodLevel unchanged, so a redstone-over-radio loop reading rods and writing setrods flips the meaning.
  - Evidence: `return "" + (int) (100 - this.rodLevel); // why the fuck did i invert this again?`
  - Fix: Decide the intended semantics and make get/set symmetric (probably return rodLevel).
  - Plan: decision D10
  - Status: open
- **B-082** · silent misconfig · leave · `src/main/java/com/hbm/tileentity/machine/rbmk/TileEntityRBMKBase.java:61`
  - RBMK explodeOnBroken is a static toggled through instance references. RBMKBase.onScrew (228-230) and ItemRBMKLid (57-59) set rbmk.explodeOnBroken=false/true around a metadata change; because the field is static the toggle affects every column in every world, but it is restored on the same thread immediately, so only re-entrant breakBlock paths could observe it.
  - Evidence: `public static boolean explodeOnBroken = true; ... rbmk.explodeOnBroken = false; world.setBlockMetadataWithNotify(...); rbmk.explodeOnBroken = true;`
  - Fix: Leave; if touched, make it an instance field or pass a flag through the lid-removal path.
  - Plan: not planned
  - Status: open
- **B-083** · silent misconfig · fix in the fork · `src/main/java/com/hbm/util/GameRuleHelper.java:29-30`
  - GameRuleHelper.parseDouble/parseInt create a gamerule named by the value string. When the rule is missing the value string `s` is "" and the code calls rules.hasRule(s)/addGameRule(s, def) with that empty string (also at 44-45), so a gamerule literally named "" is added and the real dial rule is not created; callers still get the default because of the fallback.
  - Evidence: `if(s.isEmpty() && !rules.hasRule(s)) { rules.addGameRule(s, String.valueOf(def));`
  - Fix: Pass the RBMKKeys rule into parseDouble/parseInt and use rule.keyString for hasRule/addGameRule.
  - Plan: Phase 1
  - Status: open
- **B-084** · leak / perf · fix in the fork · `src/main/java/com/hbm/handler/neutron/NeutronNodeWorld.java:70-80`
  - NeutronNodeWorld.cleanNodes never uncaches PILE nodes. Only RBMK nodes are checked/removed every 20 ticks; PileNeutronHandler:76 still adds PileNeutronNode entries to the same nodeCache and the pile branch is a commented-out TODO, so the cache grows with every pile block ever irradiated until the StreamWorld is dropped (no streams) or the world unloads.
  - Evidence: `/* TODO: actually do this and uncache pile nodes ... */`
  - Fix: Implement PileNeutronNode.checkNode (invalid/unloaded tile -> remove) and call it in cleanNodes.
  - Plan: Phase 5
  - Status: open
- **B-085** · dead code · fix in the fork · `src/main/java/com/hbm/tileentity/bomb/EntityAntimatter.java:1`
  - tileentity/bomb/EntityAntimatter.java is a 0-byte file. Empty compilation unit (javac accepts it) with no class; it is not referenced anywhere and only confuses navigation.
  - Evidence: `wc -c -> 0 tileentity/bomb/EntityAntimatter.java`
  - Fix: Delete the file.
  - Plan: Phase 7
  - Status: open
- **B-086** · dead code · needs a decision · `src/main/java/com/hbm/tileentity/bomb/TileEntityLaunchPadRocket.java:235-236`
  - TileEntityLaunchPadRocket.isPadObstructed loops over a single block. `for(int ox = 0; ox <= 0; ox++)` / `oz <= 0` iterate once, so only the pad centre column is checked for sky access although the rocket tower footprint is 5 wide; either the bounds were meant to be -2..2 or the loops are leftovers.
  - Evidence: `for(int ox = 0; ox <= 0; ox++) { for(int oz = 0; oz <= 0; oz++) {`
  - Fix: Decide the intended footprint; either widen the bounds or replace with a single canBlockSeeTheSky call.
  - Plan: decision D33
  - Status: open
- **B-087** · dead code · fix in the fork · `src/main/java/com/hbm/tileentity/bomb/TileEntityNukeBoy.java:185`
  - TileEntityNukeBoy.isReady uses octal literal slots[01]. `01` is octal 1 so behaviour is correct; purely a readability trap.
  - Evidence: `if(slots[0] != null && slots[01] != null && slots[2] != null`
  - Fix: Write slots[1].
  - Plan: Phase 7
  - Status: open
- **B-088** · dead code · fix in the fork · `src/main/java/com/hbm/tileentity/bomb/TileEntityNukeCustom.java:385`
  - TileEntityNukeCustom.destruct() passes xCoord as z (and is never called). func_147480_a(xCoord, yCoord, xCoord, false) would destroy the wrong block; `grep '.destruct()'` finds no callers, so the bug is dormant. The yield tally (272+) also runs on both sides with no sync packet, by design.
  - Evidence: `worldObj.func_147480_a(xCoord, yCoord, xCoord, false);`
  - Fix: Use zCoord; delete the method if it stays unused.
  - Plan: Phase 7
  - Status: open
- **B-089** · dead code · fix in the fork · `src/main/java/com/hbm/tileentity/machine/TileEntityPWRController.java:557`
  - TileEntityPWRController writes "rodCount" twice in writeToNBT. Line 557 stores the rodCount field and line 571 overwrites the same key with rods.size(); harmless because setup() adds one rods entry per pwr_fuel (line 127) so both are equal, and readFromNBT reads the key twice (513, 529) for the field and the list loop.
  - Evidence: `nbt.setInteger("rodCount", rodCount); ... nbt.setInteger("rodCount", rods.size());`
  - Fix: Drop the second write or use a distinct key for the position list.
  - Plan: Phase 7
  - Status: open
- **B-090** · dead code · fix in the fork · `src/main/java/com/hbm/tileentity/machine/rbmk/TileEntityRBMKBase.java:602-604`
  - TileEntityRBMKBase.getFancyStats(NBT) is a static stub returning null. No caller exists (the live method is TileEntityRBMKConsole.RBMKColumn.getFancyStats used by GUIRBMKConsole:89); the stub only suggests an API that does not work.
  - Evidence: `public static List<String> getFancyStats(NBTTagCompound nbt) { return null; }`
  - Fix: Delete the stub.
  - Plan: Phase 7
  - Status: open
- **B-091** · dead code · fix in the fork · `src/main/java/com/hbm/tileentity/machine/rbmk/TileEntityRBMKConsole.java:66-68`
  - TileEntityRBMKConsole.getName() returns null. getInventoryName() therefore returns null; harmless today because GUIRBMKConsole/ContainerRBMKConsole never read the name, but any generic inventory viewer calling I18n.format(getInventoryName()) would NPE.
  - Evidence: `public String getName() { return null; }`
  - Fix: Return "container.rbmkConsole" (add lang key).
  - Plan: Phase 6
  - Status: open
- **B-092** · dead code · leave · `src/main/java/com/hbm/tileentity/machine/rbmk/TileEntityRBMKRod.java:134`
  - ItemRBMKRod.specialFluxCurve is never enabled; rod branch is dead. The `if(rod.specialFluxCurve)` path (flux ratio curve rods) is only reachable when a rod calls the setter (ItemRBMKRod:409); no ModItems rod does, as the code comment admits (nothing really uses this so its just idle code).
  - Evidence: `if(rod.specialFluxCurve) { fluxRatioOut = rod.fluxRatioOut(...)`
  - Fix: Leave as experimental; remove if it keeps drifting from the main path.
  - Plan: not planned
  - Status: open
### UNINOS networks, packet threading (`networks`)

- **B-093** · crash · fix in the fork · `src/main/java/com/hbm/tileentity/network/TileEntityPylonBase.java:80-82`
  - TileEntityPylonBase.addConnection dereferences a possibly null PowerNode. Wiring a pylon in the same tick it was placed (before updateEntity created its node) NPEs on node.recentlyChanged; TileEntityPipelineBase.addConnection (L42-48) guards the same case by creating the node.
  - Evidence: `PowerNode node = Nodespace.getNode(worldObj, xCoord, yCoord, zCoord); node.recentlyChanged = true;`
  - Fix: Create the node when null (as TileEntityPipelineBase does) or return early.
  - Plan: Phase 2
  - Status: open
- **B-094** · crash · fix in the fork · `src/main/java/com/hbm/uninos/UniNodespace.java:52-56`
  - UniNodespace.destroyNode(World, GenNode) NPEs when the world has no node map. The by-reference overload used by offset nodes (Klystron, HTR Neo) calls worlds.get(world).popNode(node); after /ntmreapnetworks (worlds.clear()) or for a world that never had a node it NPEs on invalidate. The (x,y,z,provider) overload is null-safe.
  - Evidence: `public static void destroyNode(World world, GenNode node) { if(node != null) { worlds.get(world).popNode(node); } }`
  - Fix: Null-check the UniNodeWorld before popNode.
  - Plan: Phase 2
  - Status: open
- **B-095** · gameplay · needs a decision · `src/main/java/com/hbm/handler/threading/PacketThreading.java:147,165-168`
  - packetThreadingErrorBypass skips the join entirely and drops the remaining queue. With 0.04_packetThreadingErrorBypass=true the join loop is not entered at all; the finally block then clears any packet the single worker has not yet dequeued, every tick, with no log. The option is strictly more lossy than the default although its description suggests it only silences errors.
  - Evidence: `if (GeneralConfig.enablePacketThreading && (!GeneralConfig.packetThreadingErrorBypass && !hasTriggered)) { ... } finally { ... clearThreadPoolTasks();`
  - Fix: Still join futures under bypass (only suppress the latch/logging), or document the loss.
  - Plan: Phase 5 after D13
  - Status: open
- **B-096** · gameplay · needs a decision · `src/main/java/com/hbm/handler/threading/PacketThreading.java:150-160,193`
  - waitUntilThreadFinished drops queued packets after 50 ms and latches to sync. Each Future gets a 50 ms wait; on timeout the whole queue is cleared (clients silently miss TE updates that tick) and after clearCnt > 5 (6th non-empty clear) hasTriggered permanently reverts to inline sends until /ntmpackets resetState.
  - Evidence: `future.get(50, TimeUnit.MILLISECONDS); // I HATE EVERYTHING ... if(clearCnt > 5 && !isTriggered()) { ... hasTriggered = true; }`
  - Fix: Upstream policy; consider a total-time budget instead of per-future and making the latch recover automatically.
  - Plan: Phase 5 after D13
  - Status: open
- **B-097** · gameplay · leave · `src/main/java/com/hbm/uninos/UniNodespace.java:151`
  - UniNodeWorld.popNode destroys the entire NodeNet of the removed node. Removing any single node (breaking one cable, pipe setType, pylon disconnect, cable switch) calls node.net.destroy(), nulling net on every link and clearing all subscriptions; the remaining nodes re-form next tick but every machine stays unsubscribed until its next heartbeat (up to 20 ticks).
  - Evidence: `public void popNode(GenNode node) { if(node.net != null) node.net.destroy();`
  - Fix: Upstream design (avoids graph-split detection); leave, but document in how-tos.
  - Plan: not planned
  - Status: open
- **B-098** · silent misconfig · fix in the fork · `src/main/java/com/hbm/handler/threading/PacketThreading.java:37`
  - PacketThreading.init() is never called during mod init. init() is the only place the 0.02/0.03 core/max thread counts and keep-alive are applied, yet its sole caller is /ntmpackets toggleThreadingStatus (CommandPacketInfo L40); at startup the pool is the raw newFixedThreadPool(1), so the config keys are dead until an admin runs the command.
  - Evidence: `public static void init() { ... } / CommandPacketInfo.java:40 PacketThreading.init(); // Reinit threads.`
  - Fix: Call PacketThreading.init() from MainRegistry after loadConfig (PostLoad).
  - Plan: Phase 5
  - Status: open
- **B-099** · silent misconfig · fix in the fork · `src/main/java/com/hbm/handler/threading/PacketThreading.java:44-47`
  - PacketThreading.init() rejects max > core (validation inverted). The config comment says max 'must be greater than or equal to core', but init treats packetThreadingMaxCount > packetThreadingCoreCount as an error and resets both to 1; only core == max survives. The pool is a fixed pool with an unbounded queue so max never matters anyway.
  - Evidence: `} else if (GeneralConfig.packetThreadingMaxCount > GeneralConfig.packetThreadingCoreCount) { ...error... setCorePoolSize(1); setMaximumPoolSize(1);`
  - Fix: Flip the comparison to `<` (or drop the max setting entirely).
  - Plan: Phase 5
  - Status: open
- **B-100** · leak / perf · fix in the fork · `src/main/java/com/hbm/handler/threading/PacketThreading.java:97,123`
  - Threaded send recompiles every packet on the worker thread; reuse throws. After sendToAllAround consumes the compiled buffer, the task's getCompiledBuffer().release() sees readableBytes()<=0 (ThreadedPacket L27), recompiles via toBytes() -> serialize() on NTM-Packet-Thread against live TE state, then releases that. Reusing the packet later throws.
  - Evidence: `PacketDispatcher.wrapper.sendToAllAround(message, target); packet.getCompiledBuffer().release();`
  - Fix: Release the existing field directly (`if(packet.compiledBuffer != null) packet.compiledBuffer.release(); packet.compiledBuffer = null;`) instead of via getCompiledBuffer().
  - Plan: Phase 5
  - Status: open
- **B-101** · leak / perf · fix in the fork · `src/main/java/com/hbm/tileentity/machine/storage/TileEntityBatteryBase.java:65,102,111`
  - TileEntityBatteryBase node created at ports but looked up/destroyed at own xyz. createNode() registers a PowerNode at getPortPos() while updateEntity's lookup and invalidate's destroyNode use (xCoord,yCoord,zCoord). TileEntityBatteryREDD.getPortPos (L162-172) excludes the core, so each tile recreation adds a duplicate node and breaking a REDD never removes it (ghost node).
  - Evidence: `UniNodespace.getNode(worldObj, xCoord, yCoord, zCoord, ...) / new PowerNode(this.getPortPos()) / destroyNode(worldObj, xCoord, yCoord, zCoord, ...)`
  - Fix: Look up and destroy via the first port position (or keep the node reference and use destroyNode(world, node)).
  - Plan: Phase 5
  - Status: open
- **B-102** · leak / perf · fix in the fork · `src/main/java/com/hbm/tileentity/network/RTTYSystem.java:21,61`
  - RTTYSystem.broadcast channels are never evicted. broadcast only ever puts; a channel keeps its last RTTYChannel (with the old timeStamp) forever, so `listen() != null` is not 'signal present' and every distinct channel name ever used stays in the map for the JVM lifetime.
  - Evidence: `public static HashMap<Pair<World, String>, RTTYChannel> broadcast = new HashMap(); ... broadcast.put(identifier, channel);`
  - Fix: Evict channels older than N ticks in updateBroadcastQueue and on WorldEvent.Unload.
  - Plan: Phase 5
  - Status: open
- **B-103** · leak / perf · needs a decision · `src/main/java/com/hbm/tileentity/network/RTTYSystem.java:64-72`
  - RTTYSystem injects the '2012-08-06' test channel into every world every tick. updateBroadcastQueue allocates a new RTTYChannel and Pair per loaded world per tick for a hard-coded test channel that nothing else in the tree references; constant allocation churn and a phantom channel visible to receivers.
  - Evidence: `for(World world : MinecraftServer.getServer().worldServers) { RTTYChannel chan = new RTTYChannel(); ... toAdd.put(new Pair(world, "2012-08-06"), chan)`
  - Fix: Gate behind GeneralConfig.enableDebugMode or remove.
  - Plan: Phase 5 after D14
  - Status: open
- **B-104** · leak / perf · fix in the fork · `src/main/java/com/hbm/tileentity/network/RequestNetwork.java:43-48`
  - RequestNetwork.updateEntries 20-tick throttle is dead code (purges every tick). The guard `if(timer < 0) { timer--; return; }` can never be true after `timer = 20` (it only decrements when already negative), so the full world/chunk/node lease purge runs every server tick instead of every 20.
  - Evidence: `if(timer < 0) { timer--; return; } timer = 20;`
  - Fix: Use `if(timer > 0) { timer--; return; } timer = 20;`.
  - Plan: Phase 5
  - Status: open
- **B-105** · leak / perf · needs a decision · `src/main/java/com/hbm/tileentity/network/TileEntityPipelineBase.java:189`
  - TileEntityPipelineBase.getRenderBoundingBox returns INFINITE_EXTENT_AABB. Every loaded pipeline tile is always rendered regardless of frustum/distance because its render AABB is infinite (self-annotated '// not great!'); many pipelines cost TESR time even when off-screen.
  - Evidence: `return TileEntity.INFINITE_EXTENT_AABB; // not great!`
  - Fix: Compute an AABB spanning the tile and its connected endpoints.
  - Plan: Phase 5 after D14
  - Status: open
- **B-106** · leak / perf · needs a decision · `src/main/java/com/hbm/tileentity/network/TileEntityRequestNetwork.java:80-89`
  - TileEntityRequestNetwork rescans hasPath for every known node each pass. The comment says the costly hasPath (LOS raytrace) runs 'one op at a time', but the rescan loop calls it for every knownNode every 20 ticks; only the discovery loop (L92-103) honours newNodeLimit = 5. Large drone networks pay O(known) raytraces per tile per second.
  - Evidence: `//...hasPath function which is costly, so it only runs one op at a time ... for(PathNode known : knownNodes) { if(!hasPath(worldObj, pos, known.pos)`
  - Fix: Rescan a bounded slice of knownNodes per pass (round-robin).
  - Plan: decision D14
  - Status: open
- **B-107** · leak / perf · fix in the fork · `src/main/java/com/hbm/uninos/UniNodespace.java:27`
  - UniNodespace.worlds is keyed by World and never cleared on unload. There is no WorldEvent.Unload handler in UniNodespace; only /ntmreapnetworks clears `worlds`, so every node of an unloaded/re-created World (integrated-server world switches, dimension unloads) leaks for the JVM lifetime together with its NodeNets in activeNodeNets.
  - Evidence: `public static Map<World, UniNodeWorld> worlds = new HashMap(); ... (only CommandReapNetworks L36 calls worlds.clear())`
  - Fix: Add a WorldEvent.Unload subscriber that destroys the world's nets and removes its UniNodeWorld.
  - Plan: Phase 5 after D6
  - Status: open
- **B-108** · leak / perf · needs a decision · `src/main/java/com/hbm/uninos/networkproviders/KlystronNetwork.java:8`
  - Klystron/Plasma/Foundry/Rebar nets have empty update() and never purge members. All four single-purpose nets override update() with `{ }`, so receiverEntries/providerEntries are never timed out or isBadLink-checked; TileEntityFusionKlystron L172 and TileEntityFusionTorus L111 re-add themselves every tick and zombie members persist until the net is destroyed.
  - Evidence: `public void update() { }`
  - Fix: Add a shared purge (timeout + isBadLink) to these nets or to NodeNet as a default.
  - Plan: decision D14
  - Status: open
### Items (`items`)

- **B-109** · gameplay · needs a decision · `src/main/java/com/hbm/main/ModEventHandler.java:764-770`
  - IEquipReceiver.onEquip fires only when the held Item type changes. Dispatch compares prevArmor[0].getItem() != heldItem.getItem(), so switching between two stacks of the same gun (or re-equipping the same item) never fires onEquip; sedna guns skip their EQUIP path in that case.
  - Evidence: `L766 '(prevArmor[0] == null || prevArmor[0].getItem() != event.entityLiving.getHeldItem().getItem())'`
  - Fix: Compare stack identity/NBT as well, or accept as upstream design.
  - Plan: decision D28
  - Status: open
- **B-110** · gameplay · fix in the fork · `src/main/java/com/hbm/main/ModEventHandlerClient.java:791`
  - Armor-mod tooltip loop stops at 8, hiding the battery slot mod. The client tooltip iterates 'i < 8' over ArmorModHandler.pryMods(stack) although MOD_SLOTS == 9 and the battery mod lives in slot 8 (ArmorModHandler L20/22). A battery mod's addDesc line never appears on the armor tooltip.
  - Evidence: `L791 'for(int i = 0; i < 8; i++) {' vs ArmorModHandler.java:22 'public static final int MOD_SLOTS = 9;'`
  - Fix: Use 'i < ArmorModHandler.MOD_SLOTS' (or mods.length).
  - Plan: Phase 6
  - Status: open
- **B-111** · silent misconfig · fix in the fork · `src/main/java/com/hbm/items/ModItemsArmor.java:318-330`
  - Two ArmorMaterials both named HBM_TRENCH via EnumHelper. aMatTaurun (L318) and aMatTrench (L330) are both created with EnumHelper.addArmorMaterial("HBM_TRENCH", ...). They are distinct objects so ArmorFSB.hasFSBArmor still separates the sets, but ArmorMaterial.valueOf("HBM_TRENCH") and any name-keyed serialisation/compat becomes ambiguous.
  - Evidence: `L318 'EnumHelper.addArmorMaterial("HBM_TRENCH", 150, ...10)' and L330 'EnumHelper.addArmorMaterial("HBM_TRENCH", 150, ...0)'`
  - Fix: Rename the Taurun material to "HBM_TAURUN".
  - Plan: Phase 6
  - Status: open
- **B-112** · silent misconfig · fix in the fork · `src/main/java/com/hbm/items/armor/ArmorFSB.java:165-184`
  - ArmorFSB.cloneStats shares effects list, skips hazardClass/hides/overlay. cloneStats copies 13 fields and re-runs setRadResist but assigns 'this.effects = original.effects' by reference and never copies setHazardClass, hides() or setOverlay. Cloned pieces silently lack hazard class/hides, and adding an effect to one piece after cloning mutates all pieces.
  - Evidence: `L168 'this.effects = original.effects;' ... L181 'this.setRadResist(original.radResist);' (no hazardClass/hides/overlay)`
  - Fix: Copy the effects list (new ArrayList(original.effects)) and document that hazard class/hides are per-piece; ModItemsArmor already sets them on helmets only, by design.
  - Plan: Phase 6
  - Status: open
- **B-113** · silent misconfig · needs a decision · `src/main/java/com/hbm/items/food/ItemLemon.java:40`
  - item.med_ipecac.desc key uses Cyrillic 'c' in code and en_US/uk_UA/zh_CN. ItemLemon resolves 'item.med_ipecac.desс' (U+0441). en_US.lang:3614, uk_UA:3376 and zh_CN:5966+6142 (duplicate key) carry the same Cyrillic key so those locales work; ru_RU.lang:3087 uses ASCII 'desc', so Russian players get the en_US fallback text. Fixing only one side breaks the tooltip.
  - Evidence: `od -c ItemLemon.java:40 shows 'd e s 321 201'; ru_RU.lang:3087 'item.med_ipecac.desc=...' (ASCII)`
  - Fix: Change ItemLemon.java:40 and en_US/uk_UA/zh_CN keys to ASCII 'desc' in one commit (ru_RU already ASCII); drop the duplicate zh_CN:6142 line.
  - Plan: decision D15
  - Status: open
- **B-114** · dead code · fix in the fork · `src/main/java/com/hbm/items/ModItems.java:249-2232`
  - Six ModItems fields declared but never assigned (null). remote (L2232; its registerItem is commented at L7067), flask_empty (L1072), ingot_pet (L490) and ingot/nugget/powder_tetraneutronium (L249-251) are never assigned. No code outside ModItems uses them, so no NPE today; any new use crashes. The survey's 8 extra names are commented-out declarations.
  - Evidence: `L2232 'public static Item remote;' + L7067 '//GameRegistry.registerItem(remote, ...)'; L1117 '//public static Item coin_siege;'`
  - Fix: Delete the six dead declarations (and the commented remote registration) so a future reference fails at compile time instead of with an NPE.
  - Plan: Phase 7
  - Status: open
- **B-115** · dead code · needs a decision · `src/main/java/com/hbm/items/ModItems.java:3459-3461`
  - rbmk_pellet_lecf/mecf/hecf constructed but never registered. Three californium RBMK pellet items are instantiated but have no registerItem/addRemap line anywhere; no other code, lang or texture references them. Dead constructions that can never enter a world.
  - Evidence: `L3459 'rbmk_pellet_lecf = (ItemRBMKPellet) new ItemRBMKPellet("Low Enriched Californium-252")'; grep 'registerItem( *rbmk_pellet_lecf' = 0`
  - Fix: Delete the three declarations/instantiations or register them and add lang/textures/HazardRegistry entries if californium rods are wanted.
  - Plan: decision D16
  - Status: open
- **B-116** · dead code · needs a decision · `src/main/java/com/hbm/items/ModItems.java:4050-4771`
  - med_schizophrenia, pch, ammo_misc instantiated but never registered. Three items are constructed in initializeItem() but have no GameRegistry.registerItem line (and no addRemap): med_schizophrenia (L4089), pch (L4771), ammo_misc (L4050). They are unobtainable and their ItemStacks cannot be saved; ItemLemon L53/117 still identity-checks med_schizophrenia.
  - Evidence: `L4089 'med_schizophrenia = new ItemLemon(...)', L4771 'pch = new WeaponSpecial(...)', L4050 'ammo_misc = new ItemAmmo(...)'; no registerItem`
  - Fix: Either add GameRegistry.registerItem(x, x.getUnlocalizedName()) lines in registerItem() or delete the three instantiations (and ItemLemon's med_schizophrenia branches, AmmoMisc enum).
  - Plan: decision D16
  - Status: open
- **B-117** · dead code · fix in the fork · `src/main/java/com/hbm/items/ModItemsArmor.java:276-277`
  - jackt/jackt2 instantiated twice in ModItemsArmor.init(). jackt and jackt2 are constructed at L276-277 and again at L347-348 with identical arguments; the first pair is discarded. Harmless today but two ModArmor objects with the same unlocalized name exist, and any future fluent setter on the first pair is silently lost.
  - Evidence: `L276 and L347 both 'jackt = new ModArmor(MainRegistry.aMatSteel, 1).setUnlocalizedName("jackt")'`
  - Fix: Delete lines 347-348 (or 276-277).
  - Plan: Phase 7
  - Status: open
- **B-118** · dead code · fix in the fork · `src/main/resources/assets/hbm/textures/items/achievement_icon.digammaunity.png:1`
  - Orphan texture achievement_icon.digammaunity.png. The texture exists but no EnumAchievementType constant or Java reference named digammaunity exists, so it is never loaded.
  - Evidence: `ls textures/items | grep digammaunity = 1 file; grep -rn digammaunity src/main/java = 0 hits`
  - Fix: Delete the PNG or add the missing enum constant/achievement.
  - Plan: Phase 7
  - Status: open
### Weapons, projectiles, missiles (`weapons`)

- **B-119** · gameplay · fix in the fork · `src/main/java/com/hbm/entity/projectile/EntityArtilleryRocket.java:34-35`
  - EntityArtilleryRocket keeps a raw Entity target reference. targetEntity is a plain Entity field (TODO at L34) that is never persisted; after a chunk unload/reload or a world restart the rocket loses its target and only lastTargetPos remains.
  - Evidence: `L34 '//TODO: find satisfying solution for when an entity is unloaded and reloaded...' L35 'public Entity targetEntity = null;'`
  - Fix: Persist the target UUID and re-resolve it on load.
  - Plan: Phase 6
  - Status: open
- **B-120** · gameplay · fix in the fork · `src/main/java/com/hbm/items/weapon/sedna/factory/Lego.java:128-129`
  - LAMBDA_STANDARD_CLICK_SECONDARY reads mode of config 0, writes ctx.configIndex. The standard mode-toggle lambda reads getMode(stack, 0) but writes setMode(stack, index, 1 - mode) with index = ctx.configIndex. For a config index 1 receiver (akimbo second gun) the toggle is driven by config 0's mode, so the two modes desynchronise and the sound cue is wrong.
  - Evidence: `L128 'int mode = ItemGunBaseNT.getMode(stack, 0);' L129 'ItemGunBaseNT.setMode(stack, index, 1 - mode);'`
  - Fix: Read 'ItemGunBaseNT.getMode(stack, index)'.
  - Plan: Phase 6
  - Status: open
- **B-121** · gameplay · needs a decision · `src/main/java/com/hbm/items/weapon/sedna/mags/MagazineBelt.java:159-161`
  - MagazineBelt persists its type under an un-indexed 'magtype' NBT key. MagazineBelt uses KEY_MAG_TYPE = "magtype" without the magazine index, unlike MagazineSingleTypeBase (KEY_MAG_TYPE + index, L240-245). gun_minigun_dual has two belt receivers (XFactory762mm L106/116) that share and clobber the cached ammo type; a belt beside an indexed mag would also collide.
  - Evidence: `L160 'getValueInt(stack, KEY_MAG_TYPE)' vs MagazineSingleTypeBase.java:240 'getValueInt(stack, KEY_MAG_TYPE + index)'`
  - Fix: Suffix the belt key with this.index (migrating the old key on read).
  - Plan: decision D17
  - Status: open
- **B-122** · gameplay · leave · `src/main/java/com/hbm/items/weapon/sedna/mags/MagazineSingleTypeBase.java:84-91`
  - NPC-held sedna guns reload for free and skip EQUIP. With a null inventory (EntityAIFireGun LambdaContext) canReload returns true and standardReload fills the magazine without consuming ammo; ItemGunBaseNT.onUpdate only runs the equip path for players. Intentional for mobs, but any new IMagazine must tolerate inventory == null or NPC guns crash.
  - Evidence: `L84 'if(inventory == null) return true;' L91 'if(inventory == null) {' (fill to capacity)`
  - Fix: None (design); document the null-inventory contract for new magazine types.
  - Plan: not planned
  - Status: open
- **B-123** · gameplay · fix in the fork · `src/main/java/com/hbm/items/weapon/sedna/mods/WeaponModCaliber.java:16-39`
  - WeaponModCaliber returns shared static DUMMY_* magazines mutated per eval. eval() mutates static DUMMY_SINGLE/DUMMY_FULL/DUMMY_BELT (acceptedBullets, capacity, index) and returns them. Any caller that keeps the IMagazine across another gun's eval (or a client/server thread interleave) sees the other gun's values; magazine identity cannot be used as a key.
  - Evidence: `L16 'protected static MagazineSingleReload DUMMY_SINGLE = new MagazineSingleReload(0, 0);' L36-39 'DUMMY_SINGLE.acceptedBullets = cfg; ... return (T)`
  - Fix: Return a per-mod (or per-eval) magazine instance instead of shared statics.
  - Plan: Phase 8
  - Status: open
- **B-124** · gameplay · fix in the fork · `src/main/java/com/hbm/items/weapon/sedna/mods/XWeaponModManager.java:284-296`
  - restoreMagState only restores receiver 0 after mod changes. saveMagState/restoreMagState operate on getReceivers(stack)[0] only (TODO at L277). A caliber/mag mod that changes receiver 1+ leaves stale magcount/magtype NBT on the other receivers, producing wrong ammo counts or a mag of a removed type.
  - Evidence: `L288 'IMagazine mag = ...getConfig(stack, cfg).getReceivers(stack)[0].getMagazine(stack);' and L277 TODO`
  - Fix: Iterate all receivers when saving/restoring mag state (upstream TODO describes the mapping approach).
  - Plan: Phase 8
  - Status: open
- **B-125** · gameplay · fix in the fork · `src/main/java/com/hbm/items/weapon/sedna/mods/XWeaponModManager.java:308`
  - Weapon-mod install keys the gun with a non-singular ComparableStack. install() looks up modByGun with new ComparableStack(stack) (stacksize is part of equals) while modFromStack (L364) uses makeSingular(). ContainerWeaponTable L166 calls onPickupFromSlot -> install with the gun already at stackSize 0, so gun-specific mods miss and only default mods apply.
  - Evidence: `L308 'ComparableStack gun = new ComparableStack(stack);' vs L364 'modByGun.get(new ComparableStack(gun).makeSingular()); //shift clicking...'`
  - Fix: Use 'new ComparableStack(stack).makeSingular()' in install().
  - Plan: Phase 6
  - Status: open
- **B-126** · silent misconfig · fix in the fork · `src/main/java/com/hbm/items/weapon/sedna/mods/WeaponModBase.java:13`
  - Duplicate weapon-mod ids silently overwrite in HashBiMap. The ctor does XWeaponModManager.idToMod.put(id, this). A repeated id with a new mod instance replaces the earlier mapping without error (HashBiMap only rejects duplicate values), so existing guns' NBT ids would resolve to the wrong mod. A scan of all 81 constructed ids finds no duplicate today.
  - Evidence: `L13 'public WeaponModBase(int id, String... slots) { this.slots = slots; XWeaponModManager.idToMod.put(id, this); }'`
  - Fix: Throw if idToMod.containsKey(id) in the constructor.
  - Plan: Phase 6
  - Status: open
- **B-127** · leak / perf · leave · `src/main/java/com/hbm/items/weapon/sedna/ItemGunBaseNT.java:473-474`
  - ItemGunBaseNT.getShareTag disabled: full gun NBT resynced on every change. The getShareTag()=false override is commented out ('nbt sync dupe fix, didn't work'), so every timer/state NBT write on a held gun triggers a full ItemStack resync to the client each tick, which is why animations rely on separate packets.
  - Evidence: `L473-474 '/*@Override public boolean getShareTag() { return false; }*/ // nbt sync dupe fix, didn't work'`
  - Fix: Leave; revisit only with a proper client-side state cache.
  - Plan: not planned
  - Status: open
- **B-128** · dead code · leave · `src/main/java/com/hbm/entity/missile/EntityMissileCustom.java:179-243`
  - EntityMissileCustom impact switch: CLUSTER no-op, several types unhandled. CLUSTER has an explicit empty case (L188-189); SCHRAB, APOLLO, SATELLITE and CUSTOM0-9 fall to 'default: break' unless impactCustom is set. No missile part uses CLUSTER/SCHRAB and APOLLO/SATELLITE are rocket capsules, so no live duds today; a future warhead of those types would silently do nothing.
  - Evidence: `L188 'case CLUSTER:' L189 'break;'; grep 'makeWarhead(WarheadType.CLUSTER' = 0 hits`
  - Fix: Leave; route new warhead types through WarheadType.impactCustom (CompatExternal.setWarheadImpact).
  - Plan: not planned
  - Status: open
- **B-129** · dead code · fix in the fork · `src/main/java/com/hbm/entity/projectile/EntityBullet.java:408-416`
  - EntityBullet legacy 'test feature' left in release code. The oldest bullet class special-cases item frames displaying ModItems.flame_pony under a 'TODO: Remove test feature in release version' comment; the class is still used by two mobs.
  - Evidence: `L408 '//TODO: Remove test feature in release version' followed by EntityItemFrame/flame_pony checks`
  - Fix: Remove the item-frame special case.
  - Plan: Phase 7
  - Status: open
- **B-130** · dead code · leave · `src/main/java/com/hbm/entity/projectile/EntityBulletBeamBase.java:325`
  - EntityBulletBeamBase kills itself on NBT read. readEntityFromNBT unconditionally calls setDead(): a beam saved in an unloading chunk is discarded on load. Beams are 1-tick hitscan visuals so this is intended, but a beam with a long life would silently vanish.
  - Evidence: `L325 '@Override public void readEntityFromNBT(NBTTagCompound nbt) { this.setDead(); }'`
  - Fix: None (design).
  - Plan: not planned
  - Status: open
- **B-131** · dead code · fix in the fork · `src/main/java/com/hbm/handler/guncfg/GunDGKFactory.java:8-20`
  - GunDGKFactory is dead code (CASINGDGK never referenced). Only CASINGDGK is defined (registered as "DGK" in a static block) and getDGKConfig is commented out; no other class references GunDGKFactory or CASINGDGK, so the class is never even loaded. (The survey's 'GunRocketFactory is dead' claim is refuted: GunNPCFactory L225 calls getRocketConfig().).
  - Evidence: `grep -rn CASINGDGK outside the file = 0; L14-20 '/*public static BulletConfiguration getDGKConfig() {...}*/'`
  - Fix: Delete GunDGKFactory.java.
  - Plan: Phase 7
  - Status: open
- **B-132** · dead code · fix in the fork · `src/main/java/com/hbm/items/weapon/sedna/ItemGunBaseNT.java:276`
  - playAnimation always sends receiverIndex 0 to the recoil LambdaContext. HbmAnimationPacket is built with receiverIndex 0 for every gun, and HbmAnimationPacket.handleSedna (L105) passes that as the LambdaContext configIndex to Receiver.getRecoil. No current recoil lambda reads ctx.configIndex, so it is latent, but any recoil lambda that does will always see 0.
  - Evidence: `L276 'new HbmAnimationPacket(type.ordinal(), 0, index)'; HbmAnimationPacket.java:105 'new LambdaContext(config, player, ..., receiverIndex)'`
  - Fix: Send the real receiver index or build the context with gunIndex; at minimum document that recoil lambdas must not use ctx.configIndex.
  - Plan: Phase 6
  - Status: open
- **B-133** · dead code · fix in the fork · `src/main/java/com/hbm/items/weapon/sedna/factory/XFactory35800.java:56-58`
  - SpentCasing "35-800" registered twice with identical stats. Two SpentCasing instances are registered under the same global name "35-800" (SpentCasing.casingMap put overwrites). Both use color 0xCEB78E so nothing visible breaks, but the first registration is dead and the pattern invites silent overwrites.
  - Evidence: `L56 and L58 '.setCasing(new SpentCasing(CasingType.STRAIGHT).setColor(0xCEB78E).register("35-800"))'`
  - Fix: Register once into a static field and reuse it for both configs.
  - Plan: Phase 7
  - Status: open
### Recipes, fluids, materials (`recipes`)

- **B-134** · crash · needs a decision · `src/main/java/com/hbm/inventory/recipes/RadiolysisRecipes.java:54-57`
  - RadiolysisRecipes.registerRadiolysis throws when cracking recipes are empty. Called from MainRegistry.PostLoad L591 after SerializableRecipe.initialize; a user hbmCracking JSON with an empty list (or an earlier handler failure) makes it throw IllegalStateException and crash PostInit instead of just having no radiolysis recipes.
  - Evidence: `L56 'if(cracking.isEmpty()) {' L57 'throw new IllegalStateException("RefineryRecipes.getCrackingRecipes has yielded an empty map...")'`
  - Fix: Log an error and return instead of throwing.
  - Plan: decision D18
  - Status: open
- **B-135** · crash · needs a decision · `src/main/java/com/hbm/inventory/recipes/loader/GenericRecipes.java:116-117`
  - GenericRecipes.readRecipe NPEs when user JSON omits duration or power. obj.get("duration").getAsInt()/obj.get("power").getAsLong() are called whenever hasDuration()/hasPower() is true (default true; BlastFurnaceRecipesNT overrides neither). A user recipe entry without either key throws NullPointerException, which (see initialize isolation) aborts all later handlers.
  - Evidence: `L116 'if(this.hasDuration()) recipe.setDuration(obj.get("duration").getAsInt());' L117 '...obj.get("power").getAsLong()'`
  - Fix: Guard with obj.has(...) and default to 0, or override hasPower() in sets that never use power.
  - Plan: decision D18
  - Status: open
- **B-136** · crash · needs a decision · `src/main/java/com/hbm/inventory/recipes/loader/SerializableRecipe.java:126-160`
  - SerializableRecipe.initialize has no per-handler error isolation. The handler loop runs deleteRecipes()/readRecipeFile()/registerDefaults() with no try/catch; readRecipeFile catches only FileNotFoundException (L254) and calls json.get("recipes").getAsJsonArray() (L259). One malformed user JSON aborts every later handler (zero recipes) and crashes PostLoad.
  - Evidence: `L254 '} catch(FileNotFoundException ex) { }' L259 'JsonArray recipes = json.get("recipes").getAsJsonArray();'`
  - Fix: Wrap each handler iteration in try/catch, log the file name and fall back to registerDefaults() for that handler.
  - Plan: decision D18
  - Status: open
- **B-137** · gameplay · fix in the fork · `src/main/java/com/hbm/inventory/FluidContainerRegistry.java:96-97`
  - Disperser/gland containers registered by loop index instead of fluid id. In the Fluids.getAll() loop every container uses 'id = type.getID()' except disperser_canister and glyphid_gland, which use the loop index i and Fluids.fromID(i). For custom fluids (explicit ids like 1000 at a lower list index) those containers are registered against another fluid or NONE.
  - Evidence: `L96 'new ItemStack(ModItems.disperser_canister, 1, i), ..., Fluids.fromID(i), 2000' vs L89 'int id = type.getID();'`
  - Fix: Replace both 'i' and 'Fluids.fromID(i)' with 'id' and 'type'.
  - Plan: Phase 6
  - Status: open
- **B-138** · gameplay · leave · `src/main/java/com/hbm/inventory/RecipesCommon.java:198-218`
  - ComparableStack equals accepts WILDCARD but hashCode includes meta. hashCode folds in meta and stacksize (L198-201) while equals treats WILDCARD_VALUE meta as matching anything (L217) and still compares stacksize. HashMap lookups keyed by ComparableStack never find WILDCARD entries and must makeSingular() first; consumers compensate case by case.
  - Evidence: `L200 'result = prime * result + meta;' L201 '+ stacksize' vs L217 'if(meta != OreDictionary.WILDCARD_VALUE && other.meta != ... && meta != other.meta)`
  - Fix: Leave the contract (changing it alters every recipe map); document that map keys must be singular and WILDCARD needs a second lookup.
  - Plan: not planned
  - Status: open
- **B-139** · silent misconfig · fix in the fork · `src/main/java/com/hbm/config/GeneralConfig.java:47-49`
  - A user hbmPrecisionAssembly.json silently disables expensive mode. trueExp() returns enableExpensiveMode && !PrecAssRecipes.INSTANCE.modified; PrecAssRecipes.getFileName() is 'hbmPrecisionAssembly.json' (L32), so any user override of that file turns expensive mode off everywhere with no message.
  - Evidence: `L48 'return enableExpensiveMode && !PrecAssRecipes.INSTANCE.modified;'`
  - Fix: Log once at PostLoad when enableExpensiveMode is set but PrecAssRecipes.modified is true.
  - Plan: Phase 6
  - Status: open
- **B-140** · silent misconfig · fix in the fork · `src/main/java/com/hbm/config/ItemPoolConfigJSON.java:40-44`
  - ItemPoolConfigJSON never refreshes its template and fails silently. _hbmItemPools.json is written only when hbmItemPools.json is absent, so the template goes stale once a user config exists; readConfig assigns ItemPool.pools only at the end of its try (L108), so a parse error leaves code defaults active with just a stack trace.
  - Evidence: `L40 'if(!config.exists()) {' L41 'writeDefault(template);' L42 '} else {' L43 'readConfig(config);'`
  - Fix: Always write the template; log a clear error naming the file when readConfig fails.
  - Plan: Phase 6
  - Status: open
- **B-141** · silent misconfig · fix in the fork · `src/main/java/com/hbm/inventory/fluid/Fluids.java:1085-1099`
  - Fluids.readTraits clears traits, and FT_Rocket cannot be instantiated. For each fluid in hbmFluidTraits.json the trait map is cleared, then each trait is Class.newInstance()'d. FT_Rocket only has FT_Rocket(int, long) (FT_Rocket.java:35) but is registered as key 'rocket' (FluidTrait.java:33), so the exception is printed and a listed rocket fuel loses FT_Rocket.
  - Evidence: `L1088 'type.traits.clear();' L1094 'FluidTrait trait = traitClass.newInstance();' FT_Rocket.java:35 'public FT_Rocket(int isp, long twr) {'`
  - Fix: Add a public no-arg constructor to FT_Rocket (deserializeJSON already sets the fields) and log unknown trait keys instead of NPE-ing.
  - Plan: Phase 6
  - Status: open
- **B-142** · silent misconfig · fix in the fork · `src/main/java/com/hbm/inventory/fluid/Fluids.java:1130-1146`
  - Foreign (CompatFluidRegistry) fluids excluded from trait template/override. reloadFluids removes foreign fluids from metaOrder (L1130), runs writeDefaultTraits/readTraits which iterate metaOrder (L1140-1142), and only re-adds the foreign fluids afterwards (L1146). Their traits therefore never appear in _hbmFluidTraits.json and cannot be overridden.
  - Evidence: `L1130 'metaOrder.remove(type);' ... L1142 'readTraits(config);' ... L1146 'for(FluidType custom : foreignFluids) metaOrder.add(custom);'`
  - Fix: Re-add foreign fluids to metaOrder before the trait template/read step.
  - Plan: Phase 6
  - Status: open
- **B-143** · silent misconfig · fix in the fork · `src/main/java/com/hbm/inventory/fluid/Fluids.java:1181-1184`
  - Fluids.register(fluid, id) overwrites idMapping without a collision check. A custom (hbmFluidTypes.json) or CompatFluidRegistry fluid whose id collides with a built-in one replaces it in idMapping while registerOrder/metaOrder grow; this surfaces only as the generic 'MetaOrder and Mappings are inconsistent' IllegalStateException at L887-888 without naming the fluid.
  - Evidence: `L1181-1183 'protected static void register(FluidType fluid, int id) { idMapping.put(id, fluid); registerOrder.add(fluid);'`
  - Fix: Throw a descriptive IllegalStateException naming both fluids when idMapping already contains id.
  - Plan: Phase 6
  - Status: open
- **B-144** · silent misconfig · fix in the fork · `src/main/java/com/hbm/inventory/recipes/ArcFurnaceRecipes.java:139-140`
  - ArcFurnaceRecipes.register silently drops recipes whose input is occupied. A second recipe for an already-occupied solid/liquid input returns without any log, so autogen/furnace imports quietly shadow hand-written ones depending on registration order.
  - Evidence: `L139 'if(output.solidOutput != null) if(occupiedSolid.contains(compStack)) return;' L140 same for fluid`
  - Fix: Log at debug level when a recipe is skipped (getComment already says these are not overridable).
  - Plan: Phase 6
  - Status: open
- **B-145** · silent misconfig · needs a decision · `src/main/java/com/hbm/inventory/recipes/AtmosphereRecipes.java:70-73`
  - AtmosphereRecipes JSON read/write are no-ops. readRecipe and writeRecipe are empty, so _hbmAtmosphere.json is written as a list of empty objects and a user hbmAtmosphere.json cannot change anything (but its presence still suppresses registerDefaults, leaving the set empty).
  - Evidence: `L70 'public void readRecipe(JsonElement recipe) { }' L73 'public void writeRecipe(Object recipe, JsonWriter writer) throws IOException { }'`
  - Fix: Either implement (de)serialisation or exclude the set from templating and ignore user files for it.
  - Plan: decision D18
  - Status: open
- **B-146** · silent misconfig · fix in the fork · `src/main/java/com/hbm/inventory/recipes/loader/GenericRecipes.java:102`
  - nameToRecipeGlobal silently overwrites same-named recipes across sets. Uniqueness is enforced only per set (recipeNameMap); the static nameToRecipeGlobal.put overwrites a same-named recipe from another GenericRecipes set, and ItemBlueprints L124 resolves blueprint pools through that map. A scan of all new GenericRecipe("...") names finds no cross-set duplicate today.
  - Evidence: `L102 'nameToRecipeGlobal.put(recipe.name, recipe);' ItemBlueprints.java:124 'GenericRecipes.nameToRecipeGlobal.get(name)'`
  - Fix: Throw or log when nameToRecipeGlobal already contains the name; keep the machine-prefix naming convention.
  - Plan: Phase 6
  - Status: open
- **B-147** · silent misconfig · fix in the fork · `src/main/java/com/hbm/inventory/recipes/loader/GenericRecipes.java:99-100`
  - GenericRecipes.register appends to recipeOrderedList before the duplicate check. recipeOrderedList.add(recipe) runs before the duplicate-name IllegalStateException, so after the exception the ordered list (used by NEI/iteration) still contains the duplicate while the name maps do not.
  - Evidence: `L99 'this.recipeOrderedList.add(recipe);' L100 'if(recipeNameMap.containsKey(recipe.name)) throw new IllegalStateException(...)'`
  - Fix: Move the containsKey check above the add.
  - Plan: Phase 6
  - Status: open
- **B-148** · silent misconfig · leave · `src/main/java/com/hbm/inventory/recipes/loader/SerializableRecipe.java:150-153`
  - IRecipeRegisterListener (fork recipes) skipped when a user JSON exists. onRecipeLoad fires only in the registerDefaults branch. A user config/hbmRecipes/hbmAssembler.json or hbmAnvil.json therefore removes the fork's DBSRecipes additions (BAT9000, fuel plates) unless copied into the JSON. Documented in docs/fork-notes.md as intended JSON-override semantics.
  - Evidence: `L150 'recipe.registerDefaults();' ... L153 'listener.onRecipeLoad(recipe.getClass().getSimpleName());' (else branch only)`
  - Fix: None; keep documented. Optionally have check-fork.sh warn when those JSON files exist in the dev run dir.
  - Plan: not planned
  - Status: open
- **B-149** · silent misconfig · fix in the fork · `src/main/java/com/hbm/inventory/recipes/loader/SerializableRecipe.java:244-245`
  - writeTemplateFile swallows every exception, leaving truncated templates. Any exception while streaming a _hbm*.json template is only printed; the half-written template stays on disk and initialize() continues, so users may copy a truncated template as their config.
  - Evidence: `L244 '} catch(Exception ex) {' L245 'ex.printStackTrace();'`
  - Fix: Log through MainRegistry.logger with the file name and delete the partial file on failure.
  - Plan: Phase 6
  - Status: open
- **B-150** · silent misconfig · fix in the fork · `src/main/java/com/hbm/inventory/recipes/loader/SerializableRecipe.java:276`
  - readAStack drops metadata for NBT stacks that writeAStack emits. JSON 'nbt' ingredients are rebuilt as new NBTStack(item, stacksize, 0) although writeAStack writes the meta at index 3 (L310). Any NBTStack ingredient with meta != 0 silently changes to meta 0 when a recipe set is loaded from JSON. Only PrecAssRecipes L205 uses NBTStack today (meta 0), so latent.
  - Evidence: `L276 'return new NBTStack(item, stacksize, 0).withNBT(...)' vs L310 'if(comp.meta > 0 || comp.nbt != null) writer.value(comp.meta);'`
  - Fix: Read 'int meta = array.size() > 3 ? array.get(3).getAsInt() : 0;' and pass it to NBTStack.
  - Plan: Phase 6
  - Status: open
- **B-151** · dead code · fix in the fork · `src/main/java/com/hbm/inventory/recipes/SILEXRecipes.java:51-58`
  - SILEXRecipes registers ingot_cm_mix twice. Two recipes.put calls with the same ComparableStack(ModItems.ingot_cm_mix) key; the second (WeightedRandomObject form) overwrites the first with equivalent outputs. Harmless duplicate that misleads readers and template diffing.
  - Evidence: `L51 and L56 'recipes.put(new ComparableStack(ModItems.ingot_cm_mix), new SILEXRecipe(900, 100, 2)'`
  - Fix: Delete lines 51-54.
  - Plan: Phase 7
  - Status: open
- **B-152** · dead code · fix in the fork · `src/main/java/com/hbm/inventory/recipes/loader/SerializableRecipe.java:85-95`
  - Stale '//AFTER Assembler' ordering comment in registerAllHandlers. The comment above AnvilRecipes says it must come after the assembler, but AssemblyMachineRecipes.INSTANCE is added later (L95) and AnvilRecipes has no reference to it; the only real ordering constraint is MatDistribution before ArcFurnaceRecipes. The comment misleads anyone reordering handlers.
  - Evidence: `L85 '//AFTER Assembler' L86 'recipeHandlers.add(new AnvilRecipes());' L95 'recipeHandlers.add(AssemblyMachineRecipes.INSTANCE);'`
  - Fix: Delete the comment or move it to document the MatDistribution->ArcFurnace dependency.
  - Plan: Phase 7
  - Status: open
- **B-153** · dead code · fix in the fork · `src/main/java/com/hbm/itempool/ItemPoolsSingle.java:116-130`
  - ItemPoolsSingle registers POOL_BLUEPRINTS twice. Two identical 'new ItemPool(POOL_BLUEPRINTS)' blocks; ItemPool's ctor does pools.put(name, this) (ItemPool.java:43) so the second overwrites the first. Identical content, so harmless, but the duplicate also appears twice in any template written from code.
  - Evidence: `L116 'new ItemPool(POOL_BLUEPRINTS) {{' and L124 'new ItemPool(POOL_BLUEPRINTS) {{'`
  - Fix: Delete the second block (lines 124-130).
  - Plan: Phase 7
  - Status: open
### Containers, GUIs, NEI (`gui`)

- **B-154** · gameplay · fix in the fork · `src/main/java/com/hbm/handler/nei/CustomMachineHandler.java:28`
  - CustomMachineHandler lacks ICompatNHNEI, invisible to GTNH NEI catalysts. IMCHandlerNHNEI.IMCSender (L19) only sends handlers that are 'instanceof ICompatNHNEI'; CustomMachineHandler extends TemplateRecipeHandler without it and is registered via registerHandlerBypass, so custom machines never get GTNH NEI tabs/catalysts.
  - Evidence: `L28 'public class CustomMachineHandler extends TemplateRecipeHandler {' IMCHandlerNHNEI.java:19 'if(handler instanceof ICompatNHNEI && ...'`
  - Fix: Implement ICompatNHNEI (recipe id 'ntm_' + conf.unlocalizedName, machine block) and include bypass handlers in IMCSender.
  - Plan: Phase 6
  - Status: open
- **B-155** · gameplay · fix in the fork · `src/main/java/com/hbm/inventory/container/ContainerBarrel.java:55-56`
  - ContainerBarrel tile-to-player merge skips player slot 6. Six tile slots (0-5) and the bound 'par2 <= 5' is right, but the merge into the player inventory starts at 7, so player slot 6 (first main-inventory slot) never receives shift-clicked items.
  - Evidence: `L55 'if (par2 <= 5) {' L56 'if (!this.mergeItemStack(var5, 7, this.inventorySlots.size(), true))'`
  - Fix: Merge from 6.
  - Plan: Phase 3
  - Status: open
- **B-156** · gameplay · fix in the fork · `src/main/java/com/hbm/inventory/container/ContainerElectrolyserMetal.java:57-58`
  - ContainerElectrolyserMetal shift-click bounds off by one. Ten tile slots are added (0-9, L23-35) but the transfer uses 'par2 <= 10' and merges into 11..size, so player slot 10 is treated as a machine slot and never receives outputs.
  - Evidence: `L57 'if(par2 <= 10) {' L58 'if(!this.mergeItemStack(var5, 11, this.inventorySlots.size(), true)) {'`
  - Fix: Use 'par2 < 10' and merge from 10.
  - Plan: Phase 3
  - Status: open
- **B-157** · gameplay · fix in the fork · `src/main/java/com/hbm/inventory/container/ContainerMachineCryoDistill.java:61-71`
  - ContainerMachineCryoDistill merges into its own tile slots. Ten tile slots (0-9) but the tile-to-player merge starts at 8 (covering tile slots 8-9) and the IItemFluidIdentifier branch merges into 11..12, a player slot. Copy-pasted from VacuumDistill's 12-slot layout: identifiers cannot be shift-clicked into the machine and outputs can loop into slots 8/9.
  - Evidence: `L61 'if(!this.mergeItemStack(var5, 8, this.inventorySlots.size(), true)) {' L71 'if(!this.mergeItemStack(var5, 11, 12, false)) {'`
  - Fix: Merge from 10 and point the identifier branch at the correct tile slot (7 or 8 per TE layout).
  - Plan: Phase 3
  - Status: open
- **B-158** · gameplay · fix in the fork · `src/main/java/com/hbm/inventory/container/ContainerMachineCyclotron.java:66-67`
  - ContainerMachineCyclotron shift-click bounds off by four. Twelve tile slots (0-11) but 'index <= 15' and merge into 16..size: shift-clicking from player slots 12-15 shuffles items within the player inventory instead of into the cyclotron, and machine outputs never land in those four slots.
  - Evidence: `L66 'if(index <= 15) {' L67 'if(!this.mergeItemStack(stack, 16, this.inventorySlots.size(), true)) {'`
  - Fix: Use 'index < 12' and merge from 12.
  - Plan: Phase 3
  - Status: open
- **B-159** · gameplay · fix in the fork · `src/main/java/com/hbm/inventory/container/ContainerMachineDiesel.java:45-51`
  - ContainerMachineDiesel shift-click bounds off by one. Four tile slots (0-3) but 'par2 <= 4' and merge into 5..size: player slot 4 is treated as a machine slot, and the third fallback 'mergeItemStack(var5, 4, 5, false)' inserts into player slot 4 instead of a tile slot.
  - Evidence: `L45 'if(par2 <= 4) {' L46 'mergeItemStack(var5, 5, this.inventorySlots.size(), true)' L51 'mergeItemStack(var5, 4, 5, false)'`
  - Fix: Use 'par2 < 4', merge from 4, and target slot 3 (upgrade) in the last branch.
  - Plan: Phase 3
  - Status: open
- **B-160** · gameplay · fix in the fork · `src/main/java/com/hbm/inventory/container/ContainerMachineKeyForge.java:54-60`
  - ContainerMachineKeyForge / SatLinker route tile slots 1-2 into slot 0. Three tile slots (0-2) but only 'par2 <= 0' goes to the player inventory; shift-clicking the output/other tile slots falls to the else branch and merges them into tile slot 0 instead of the player inventory. ContainerMachineSatLinker.java:54-60 is identical.
  - Evidence: `L54 'if (par2 <= 0) {' L55 'mergeItemStack(var5, 1, this.inventorySlots.size(), true)' L60 'else if (!this.mergeItemStack(var5, 0, 1, false))'`
  - Fix: Use 'par2 < 3' with merge from 3 in both containers.
  - Plan: Phase 3
  - Status: open
- **B-161** · gameplay · fix in the fork · `src/main/java/com/hbm/inventory/container/ContainerMachineMilkReformer.java:61-68`
  - ContainerMachineMilkReformer bounds off by two and empty battery range. Nine tile slots (0-8) but 'par2 <= 10' with merge into 11..size (player slots 9-10 mis-handled), and the battery branch calls mergeItemStack(var5, 1, 0, false), an empty range, so batteries can never be shift-clicked into slot 0.
  - Evidence: `L61 'if(par2 <= 10) {' L62 'mergeItemStack(var5, 11, ...)' L68 'if(!this.mergeItemStack(var5, 1, 0, false)) {'`
  - Fix: Use 'par2 < 9', merge from 9, and battery range (0, 1).
  - Plan: Phase 3
  - Status: open
- **B-162** · gameplay · fix in the fork · `src/main/java/com/hbm/inventory/container/ContainerMachineOilWell.java:55-62`
  - ContainerMachineOilWell shift-click off by one; upgrade range hits player slot. Seven tile slots (0-6) but 'par2 <= 7' with merge into 8..size; the upgrade branch merges into 5..8 which includes player slot 7, so a machine upgrade can be shift-clicked into the first player slot instead of the upgrade slots 5-6.
  - Evidence: `L55 'if(par2 <= 7) {' L56 'mergeItemStack(var5, 8, ...)' L62 'if(!this.mergeItemStack(var5, 5, 8, true)) {'`
  - Fix: Use 'par2 < 7', merge from 7, and upgrade range (5, 7).
  - Plan: Phase 3
  - Status: open
- **B-163** · gameplay · fix in the fork · `src/main/java/com/hbm/inventory/container/ContainerMachineReactorBreeding.java:46-47`
  - ContainerMachineReactorBreeding shift-click off-by-one (index <= 2 for 2 slots). Only tile slots 0-1 exist (L21-22) but transferStackInSlot treats index 2, the first player slot, as a tile slot: a breeding rod shift-clicked from that slot is merged back into the player inventory (from index 2) instead of into slot 0. Fork-restored machine; the survey cited L135-136 (stale).
  - Evidence: `L46 'if(index <= 2) {' L47 'if(!this.mergeItemStack(stack, 2, this.inventorySlots.size(), true)) {'`
  - Fix: Use 'if(index < 2)'.
  - Plan: Phase 1
  - Status: open
- **B-164** · gameplay · fix in the fork · `src/main/java/com/hbm/inventory/container/ContainerMachineVacuumDistill.java:72-73`
  - ContainerMachineVacuumDistill treats tile slot 11 as a player slot. Twelve tile slots (0-11, L23-45) but 'par2 <= 10' and merge into 11..size: shift-clicking from tile slot 11 routes into the input slots, and tile outputs are merged into tile slot 11 when the player inventory is full.
  - Evidence: `L72 'if(par2 <= 10) {' L73 'if(!this.mergeItemStack(var5, 11, this.inventorySlots.size(), true)) {'`
  - Fix: Use 'par2 < 12' and merge from 12.
  - Plan: Phase 3
  - Status: open
- **B-165** · gameplay · fix in the fork · `src/main/java/com/hbm/inventory/container/ContainerNukeAntimatter.java:50-51`
  - ContainerNukeAntimatter: slots 3-4 not shift-clickable, player->bomb never works. Five tile slots (0-4) but only 'par2 <= 2' is handled (merging into 2..size, which starts inside the bomb's own slots); every other index returns null, so shift-click from tile slots 3-4 and from the player inventory does nothing.
  - Evidence: `L50 'if (par2 <= 2) {' L51 'if (!this.mergeItemStack(var5, 2, this.inventorySlots.size(), true))' else 'return null;'`
  - Fix: Use 'par2 < 5', merge from 5, and add a player->tile branch (0, 5).
  - Plan: Phase 3
  - Status: open
- **B-166** · gameplay · fix in the fork · `src/main/java/com/hbm/inventory/container/ContainerNukeFstbmb.java:47-48`
  - ContainerNukeFstbmb treats player slot 2 as a bomb slot. Two tile slots (0-1) but 'par2 <= 2': shift-clicking player slot 2 merges it back into the player inventory from index 2; everything else returns null, so nothing can be shift-clicked into the bomb.
  - Evidence: `L47 'if (par2 <= 2) {' L48 'if (!this.mergeItemStack(var5, 2, this.inventorySlots.size(), true))'`
  - Fix: Use 'par2 < 2' and add a player->tile branch (0, 2).
  - Plan: Phase 3
  - Status: open
- **B-167** · gameplay · fix in the fork · `src/main/java/com/hbm/inventory/container/ContainerPADetector.java:55-56`
  - ContainerPADetector / ContainerPASource shift-click bounds off by one. Both particle-accelerator containers add five tile slots (0-4) but use 'index <= 5' and merge into 6..size (ContainerPASource.java:56-57 identical), so player slot 5 is mis-routed and never receives outputs.
  - Evidence: `L55 'if(index <= 5) {' L56 'if(!this.mergeItemStack(stack, 6, this.inventorySlots.size(), true)) {'`
  - Fix: Use 'index < 5' and merge from 5 in both files.
  - Plan: Phase 3
  - Status: open
- **B-168** · gameplay · fix in the fork · `src/main/java/com/hbm/inventory/container/ContainerReactorResearch.java:56-57`
  - ContainerReactorResearch shift-click off-by-one (index <= 12 for 12 slots). Twelve tile slots (0-11, L20-31) but the transfer uses 'index <= 12' and merges tile items into player slots from 13: player slot 12 never receives shift-clicked plates and a plate shift-clicked from player slot 12 is moved within the player inventory instead of into the reactor. Fork-restored.
  - Evidence: `L56 'if (index <= 12) {' L57 'if (!this.mergeItemStack(stack, 13, this.inventorySlots.size(), true)){'`
  - Fix: Change to 'index < 12' and 'mergeItemStack(stack, 12, ...)'.
  - Plan: Phase 1
  - Status: open
- **B-169** · gameplay · needs a decision · `src/main/java/com/hbm/inventory/gui/GuiInfoContainer.java:362-371`
  - NEI drag-and-drop dead for raw containers with SlotPattern. handleDragNDrop only acts when the slot is a SlotPattern AND inventorySlots instanceof ContainerBase (to read .tile). ContainerMachineCustom (6 SlotPattern, extends Container) and ContainerCartDestroyer (2 SlotPattern, extends Container) therefore silently ignore NEI ghost-item drops.
  - Evidence: `L363 'if(inventorySlots instanceof ContainerBase) {' L371 'TileEntity te = (TileEntity) ((ContainerBase) inventorySlots).tile;'`
  - Fix: Port the two containers to ContainerBase or add an interface exposing the TE for drag-and-drop.
  - Plan: decision D19
  - Status: open
- **B-170** · gameplay · needs a decision · `src/main/java/com/hbm/main/NEIRegistry.java:14-16`
  - GTNH NEI: handler list built (and memoised) before recipes are initialised. MainRegistry.load (L544) -> handleNHNEICompat -> IMCSender -> NEIRegistry.listAllHandlers builds every handler; NEIUniversalHandler copies its recipe map in the ctor (L40-43) but SerializableRecipe.initialize runs in PostLoad (L585) and the memo never rebuilds, so GTNH NEI shows empty handlers.
  - Evidence: `L16 'if(!handlers.isEmpty()) return handlers;' MainRegistry.java:544 'proxy.handleNHNEICompat();' MainRegistry.java:585 'SerializableRecipe.initialize`
  - Fix: Defer IMCSender to PostLoad after SerializableRecipe.initialize, or make listAllHandlers rebuildable and universal handlers read their maps lazily.
  - Plan: decision D19
  - Status: open
- **B-171** · silent misconfig · fix in the fork · `src/main/java/com/hbm/handler/imc/IMCHandlerNHNEI.java:42-45`
  - IMCHandlerNHNEI hard-codes panel size for every handler. Every handler is sent with handlerHeight 65, handlerWidth 166, yShift 6 regardless of its real gui rectangle, so hand-rolled handlers with a different panel (e.g. BreederRecipeHandler's rect at 68,9,30,37) can be mis-sized in GTNH NEI's layout.
  - Evidence: `L42 'aNBT.setInteger("handlerHeight", 65);' L43 'aNBT.setInteger("handlerWidth", 166);' L45 'aNBT.setInteger("yShift", 6);'`
  - Fix: Expose per-handler dimensions through ICompatNHNEI defaults.
  - Plan: Phase 6
  - Status: open
- **B-172** · silent misconfig · fix in the fork · `src/main/java/com/hbm/handler/nei/BreederRecipeHandler.java:70-120`
  - BreederRecipeHandler hard-codes the 'breeding' id instead of getRecipeID(). getRecipeID() returns "breeding" (L30-31) but the four load methods and the transfer rect compare/pass the literal "breeding" (L70, 95, 96, 120). Changing the id in one place silently breaks NEI lookups and the clickable rect. Fork-owned file (NEIRegistry L36 '// DBS fork hook').
  - Evidence: `L70 'if((outputId.equals("breeding")) && getClass() == BreederRecipeHandler.class) {' L120 'new RecipeTransferRect(..., "breeding")'`
  - Fix: Use getRecipeID() in all five places.
  - Plan: Phase 1
  - Status: open
- **B-173** · dead code · fix in the fork · `src/main/java/com/hbm/handler/nei/AlloyFurnaceRecipeHandler.java:1`
  - AlloyFurnaceRecipeHandler is never registered. The hand-rolled NEI handler is referenced nowhere (not in NEIRegistry.listAllHandlers), so the legacy blast furnace has no NEI page through this class; the file is dead code.
  - Evidence: `grep -rn AlloyFurnaceRecipeHandler outside its own file = 0 hits`
  - Fix: Register it in NEIRegistry or delete it.
  - Plan: Phase 7
  - Status: open
- **B-174** · dead code · fix in the fork · `src/main/java/com/hbm/inventory/container/ContainerNT.java:1`
  - ContainerNT is dead code. ContainerNT (with its slotClick/mergeItemStack fixes) has zero subclasses; its behaviour applies to no live container, which misleads readers into assuming those fixes are active.
  - Evidence: `grep 'extends ContainerNT' = 0 hits`
  - Fix: Delete or fold its mergeItemStack into ContainerBase.
  - Plan: Phase 3
  - Status: open
- **B-175** · dead code · fix in the fork · `src/main/java/com/hbm/inventory/gui/GUIMachineReactorBreeding.java:53-63`
  - GUIMachineReactorBreeding carries a stale 'dud TE' comment with no handling. A long comment describes refreshing a 'dud' tile entity after a block-state swap, ending in 'what?', but the code simply reads breeder.getProgressScaled and MachineReactorBreeding never swaps block state, so the comment documents a non-existent mechanism.
  - Evidence: `L54 '* A dud is a tile entity which did not survive a block state change...' L61 '* what?' L63 'int i = breeder.getProgressScaled(70);'`
  - Fix: Delete the comment.
  - Plan: Phase 7
  - Status: open
### Entities, mobs, explosions (`entities`)

- **B-176** · save data · fix in the fork · `src/main/java/com/hbm/entity/EntityMappings.java:82-86`
  - Duplicate entity name entity_cloud_rainbow for two classes. EntityCloudFleijaRainbow and EntityCloudSolinium are both registered as 'entity_cloud_rainbow'; FML's string->class map keeps the last one, so a saved Rainbow cloud reloads as a Solinium cloud (client numeric ids unaffected).
  - Evidence: `L82 addEntity(EntityCloudFleijaRainbow.class, "entity_cloud_rainbow", 1000); L86 addEntity(EntityCloudSolinium.class, "entity_cloud_rainbow", 1000);`
  - Fix: Rename the Solinium registration to "entity_cloud_solinium" (both are short-lived visual entities, so no remap needed).
  - Plan: Phase 2
  - Status: open
- **B-177** · save data · needs a decision · `src/main/java/com/hbm/entity/logic/EntityNukeExplosionMK5.java:144-151`
  - MK5 nuke explosion loses all state on save/reload. Only ticksExisted is persisted; strength/speed/length/fallout are not, so a reloaded in-progress MK5 explosion has strength 0 and setDead()s on its first tick (L56-59): the crater stops half-done and no fallout is spawned. MK3/Balefire/Tom persist their state.
  - Evidence: `readEntityFromNBT: this.ticksExisted = nbt.getInteger("ticksExisted"); onUpdate: if(strength == 0) { clearChunkLoader(); setDead(); return; }`
  - Fix: Persist strength, speed, length, fallout, falloutAdd (and optionally the ray's progress) in writeEntityToNBT/readEntityFromNBT.
  - Plan: decision D20
  - Status: open
- **B-178** · gameplay · fix in the fork · `src/main/java/com/hbm/entity/EntityMappings.java:260-270`
  - EntityMappings.addSpawn appends a duplicate after adjusting an entry. After finding and adjusting an existing SpawnListEntry the loop breaks but execution falls through to spawns.add(...), so a second call for the same class doubles the spawn weight. Latent today: every current caller registers each class once.
  - Evidence: `for(SpawnListEntry entry : spawns) { if(entry.entityClass == entityClass) { ...; break; } } spawns.add(new SpawnListEntry(...));`
  - Fix: Track a boolean 'found' and only add when no entry was adjusted (or return after the adjust).
  - Plan: Phase 6
  - Status: open
- **B-179** · gameplay · fix in the fork · `src/main/java/com/hbm/entity/effect/EntityFalloutRain.java:38-42`
  - EntityFalloutRain(World, int maxAge) ignores maxAge and frustum flag. The two-arg constructor used by EntityNukeExplosionMK3 (L192) never stores maxAge and, unlike the one-arg ctor, does not set ignoreFrustumCheck, so MK3 fallout ignores its intended lifetime and can be culled when off-screen.
  - Evidence: `public EntityFalloutRain(World p_i1582_1_, int maxAge) { super(p_i1582_1_); this.setSize(4, 20); this.isImmuneToFire = true; }`
  - Fix: Delegate to this(world), then apply maxAge (or remove the parameter and the MK3 argument).
  - Plan: Phase 6
  - Status: open
- **B-180** · gameplay · fix in the fork · `src/main/java/com/hbm/entity/logic/EntityNukeExplosionMK5.java:63-65`
  - MK5 explosion grants the Manhattan achievement to every player in the world. Every tick of an MK5 explosion iterates worldObj.playerEntities and triggers achManhattan for all of them regardless of distance or involvement (also runs client-side before the client copy dies).
  - Evidence: `for(Object player : this.worldObj.playerEntities) { ((EntityPlayer)player).triggerAchievement(MainRegistry.achManhattan); }`
  - Fix: Trigger once on the first server tick for players within a radius, or leave as upstream flavour.
  - Plan: Phase 6
  - Status: open
- **B-181** · gameplay · fix in the fork · `src/main/java/com/hbm/entity/logic/EntityNukeExplosionMK5.java:68-80`
  - MK5 radiate() runs on ticks 1-9, not the first 10. radiate() requires explosion != null, but the ray object is created later in the same tick, so the initial flash of radiation is skipped on tick 0 and only 9 pulses are emitted; harmless but not what the ticksExisted < 10 condition suggests.
  - Evidence: `L68: if(!isRemote && fallout && explosion != null && ticksExisted < 10 && strength >= 75) radiate(...); explosion created later at L74-80`
  - Fix: Move the radiate() call after the explosion == null block, or drop the explosion != null guard.
  - Plan: Phase 6
  - Status: open
- **B-182** · gameplay · needs a decision · `src/main/java/com/hbm/entity/mob/glyphid/GlyphidStats.java:13-15`
  - Active glyphid stat set GLYPHID_STATS_NT is marked UNTESTED. getStats() is hard-wired to GLYPHID_STATS_NT whose javadoc (L119) says 'UNTESTED! Spreadsheet will be consulted soon', while the tested GLYPHID_STATS_70K set is retained but unreachable; glyphid balance in this fork rests on the untested table.
  - Evidence: `public static GlyphidStats getStats() { return GLYPHID_STATS_NT; } ... /** UNTESTED! Spreadsheet will be consulted soon */`
  - Fix: Decide the canonical set (config switch or delete the other) and remove the UNTESTED note once play-tested.
  - Plan: decision D20
  - Status: open
- **B-183** · gameplay · needs a decision · `src/main/java/com/hbm/explosion/ExplosionNukeRayBatched.java:89-98`
  - Batched nuke ray decays resistance over strength but cuts rays at length. collectTip uses a local length = ceil(strength) (= 2r after statFac) for the loop bound and the 0.07 exponent falloff, but breaks when i > this.length (= r). Rays are therefore limited to min(strength, length+1) steps while the falloff curve assumes strength steps; tuning either value changes both.
  - Evidence: `int length = (int)Math.ceil(strength); ... for(int i = 0; i < length; i ++) { if(i > this.length) break;`
  - Fix: Decide which radius is canonical and compute the falloff against this.length (changes nuke crater shape; balance decision).
  - Plan: decision D20
  - Status: open
- **B-184** · gameplay · needs a decision · `src/main/java/com/hbm/explosion/vanillant/ExplosionVNT.java:123-141`
  - ExplosionVNT.makeStandard/makeAmat use the deprecated EntityProcessorStandard. The convenience presets and EntityGlyphidNuclear (L158) instantiate EntityProcessorStandard, annotated '@Deprecated // an inferior version to the cross processors', so preset users silently get the inferior entity damage model instead of EntityProcessorCross.
  - Evidence: `this.setEntityProcessor(new EntityProcessorStandard()); (makeStandard and makeAmat); EntityProcessorStandard.java:22 @Deprecated`
  - Fix: Switch the presets and EntityGlyphidNuclear to new EntityProcessorCross(nodeDist) or drop the @Deprecated marker if Standard is intended there.
  - Plan: decision D29
  - Status: open
- **B-185** · silent misconfig · fix in the fork · `src/main/java/com/hbm/entity/logic/EntityExplosionChunkloading.java:38-44`
  - EntityExplosionChunkloading.loadChunk is a silent one-shot. loadChunk(x, z) only forces a chunk while loadedChunk == null; later calls with other coordinates are ignored, and when requestTicket returned null (ticket quota) forceChunk(null, ...) silently does nothing, so drivers believe the chunk is forced when it is not.
  - Evidence: `if(this.loadedChunk == null) { this.loadedChunk = new ChunkCoordIntPair(x, z); ForgeChunkManager.forceChunk(loaderTicket, loadedChunk); }`
  - Fix: Unforce the previous chunk and force the new one when coordinates differ; log when loaderTicket is null.
  - Plan: Phase 6
  - Status: open
- **B-186** · silent misconfig · needs a decision · `src/main/java/com/hbm/entity/logic/EntityNukeExplosionMK3.java:63`
  - BombConfig.limitExplosionLifespan only honoured by MK3. The config option is described generically but only EntityNukeExplosionMK3 checks it; MK5, Balefire and Tom explosions run to completion regardless of the configured lifespan.
  - Evidence: `MK3 L63: if(BombConfig.limitExplosionLifespan > 0 && currentTimeMillis() - time > ... * 1000) is the only reader outside BombConfig`
  - Fix: Add the same wall-clock check to EntityNukeExplosionMK5/EntityBalefire/EntityTomBlast or reword the config comment.
  - Plan: decision D20
  - Status: open
- **B-187** · silent misconfig · needs a decision · `src/main/java/com/hbm/main/ClientProxy.java:796`
  - EntitySiegeTunneler has a renderer but is never registered. ClientProxy registers RenderSiegeTunneler, but EntityMappings has no addMob/addEntity for EntitySiegeTunneler and nothing spawns it (CBT_Invasion does not reference it), so the mob, its renderer and texture are dead weight and cannot be spawned or synced.
  - Evidence: `RenderingRegistry.registerEntityRenderingHandler(EntitySiegeTunneler.class, new RenderSiegeTunneler()); no match in EntityMappings.java`
  - Fix: Either addMob(EntitySiegeTunneler.class, "entity_siege_tunneler", ...) at the end of the mob block and wire it into CBT_Invasion, or drop the renderer line.
  - Plan: decision D21
  - Status: open
- **B-188** · leak / perf · fix in the fork · `src/main/java/com/hbm/explosion/ExplosionNukeRayBatched.java:260-264`
  - ExplosionNukeRayBatched.cacheChunksTick ignores its time budget. cacheChunksTick(time) discards the ms budget and always runs collectTip(speed*10), so BombConfig.mk5 bounds only the destruction phase; the ray-collection phase of a large nuke can stall the server tick.
  - Evidence: `// time ignored here since collectTip() did not implement a time limit / collectTip(speed*10);`
  - Fix: Pass the budget into collectTip and break the while loop when System.currentTimeMillis() exceeds start + time.
  - Plan: Phase 5
  - Status: open
- **B-189** · dead code · needs a decision · `src/main/java/com/hbm/config/BombConfig.java:97-98`
  - BombConfig.explosionAlgorithm and enableChunkLoading are dead options. Both options are only read by ExplosionNukeRayParallelized, whose only construction site is commented out in EntityNukeExplosionMK5.onUpdate (L76-77); the MK5 always uses ExplosionNukeRayBatched, so the config comment '0 = Legacy, 1 = Threaded DDA...' is misleading.
  - Evidence: `MK5 L76-77: //if(BombConfig.explosionAlgorithm == 1 ...) ... always new ExplosionNukeRayBatched(...); readers only in Parallelized L109/176/473`
  - Fix: Either re-enable the Parallelized branch behind the option or remove both options and ExplosionNukeRayParallelized.
  - Plan: decision D20
  - Status: open
- **B-190** · dead code · fix in the fork · `src/main/java/com/hbm/entity/ModEntityList.java:62-124`
  - ModEntityList lookup helpers are dead code. getName, getData, hasEntitiesWithEggs, createEntityByID, getDatasWithEggs and getEggFromEntity have no callers; the 'hbm.<name>' lang key actually comes from FML registerModEntity, so these helpers mislead readers into thinking they drive naming.
  - Evidence: `grep for getEggFromEntity|getDatasWithEggs|hasEntitiesWithEggs|ModEntityList.getName|createEntityByID outside ModEntityList.java: none`
  - Fix: Delete the unused methods (keep registerEntity/registerEntityEgg).
  - Plan: Phase 7
  - Status: open
- **B-191** · dead code · fix in the fork · `src/main/java/com/hbm/entity/train/EntityRailCarBase.java:119-133`
  - EntityRailCarBase.interactFirst builds debug NBT that is never sent. A '//DEBUG' block allocates an NBTTagCompound per coupled train on every interaction, but the dispatch line is commented out, so it is pure allocation with no effect.
  - Evidence: `data.setString("text", id + ...); //PacketDispatcher.wrapper.sendToAllAround(new AuxParticlePacketNT(data, ...`
  - Fix: Remove the block or gate it behind a debug flag and restore the send.
  - Plan: Phase 7
  - Status: open
- **B-192** · dead code · fix in the fork · `src/main/java/com/hbm/explosion/ExplosionNukeRayBalefire.java:1`
  - ExplosionNukeRayBalefire is never instantiated. The balefire ray variant (handleTip places ModBlocks.balefire) has no construction site anywhere; balefire bombs use EntityBalefire/ExplosionBalefire instead, so the class is unreachable.
  - Evidence: `grep 'ExplosionNukeRayBalefire' outside its own file: no results`
  - Fix: Delete the class or wire it into a statFac variant if the ray-based balefire crater is wanted.
  - Plan: Phase 7
  - Status: open
### Hazards, radiation, pollution, atmosphere, saved data (`hazards`)

- **B-193** · crash · needs a decision · `src/main/java/com/hbm/handler/atmosphere/AtmosphereBlob.java:171-175`
  - AtmosphereBlob flood fill reads world blocks from a pool thread. With GeneralConfig.enableThreadedAtmospheres (default true) run() executes on a ThreadPoolExecutor and calls world.getBlock/blockExists (L82, L355, L381) while the server thread mutates chunks; the static fullBounds HashMap (L50) is shared unsynchronised. Risk of CME/NPE on chunk load/unload.
  - Evidence: `if(GeneralConfig.enableThreadedAtmospheres) { try { pool.execute(this); } ...} else { this.run(); }`
  - Fix: Snapshot the region on the main thread before dispatching, or default enableThreadedAtmospheres to false; make fullBounds a ConcurrentHashMap.
  - Plan: decision D22
  - Status: open
- **B-194** · crash · fix in the fork · `src/main/java/com/hbm/handler/atmosphere/ChunkAtmosphereHandler.java:243-256`
  - ChunkAtmosphereHandler register/unregisterAtmosphere lack a null guard. Both methods do worldBlobs.get(dimId) with no null check; receiveWorldLoad skips remote worlds (L314), so any client-side or pre-WorldEvent.Load caller NPEs. Only TileEntityAirPump calls them today, server-side from updateEntity/invalidate, so the crash is latent.
  - Evidence: `blobs = worldBlobs.get(handler.getWorld().provider.dimensionId); AtmosphereBlob blob = blobs.get(handler); (receiveWorldLoad skips remote worlds)`
  - Fix: Add if(blobs == null) return; guards (and computeIfAbsent in register).
  - Plan: Phase 6
  - Status: open
- **B-195** · crash · fix in the fork · `src/main/java/com/hbm/saveddata/SatelliteSavedData.java:48-49`
  - SatelliteSavedData.readFromNBT NPEs on an unknown satellite id. XSatelliteRegistry.createFromId swallows exceptions and returns null (L113-124); readFromNBT dereferences the result, so a save containing a satellite id that no longer exists (registration removed/reordered) crashes world load instead of skipping the entry.
  - Evidence: `SatelliteBase sat = XSatelliteRegistry.createFromId(nbt.getInteger("sat_id_" + i)); sat.readFromNBT(...)`
  - Fix: Skip and log entries where createFromId returns null; log the swallowed exception in createFromId.
  - Plan: Phase 2
  - Status: open
- **B-196** · gameplay · fix in the fork · `src/main/java/com/hbm/handler/ae2/MassStorageMEInventory.java:34-53`
  - AE2 storage handlers truncate long stack sizes to int. IAEItemStack.getStackSize() is a long; the cast to int overflows for ME requests above 2^31 items (possible with large storage networks), producing negative stockpile changes.
  - Evidence: `tile.increaseTotalStockpile((int)input.getStackSize(), ...); tile.decreaseTotalStockpile((int)request.getStackSize(), ...)`
  - Fix: Clamp with (int)Math.min(size, Integer.MAX_VALUE).
  - Plan: Phase 6
  - Status: open
- **B-197** · gameplay · fix in the fork · `src/main/java/com/hbm/handler/microblocks/MicroBlocksCompatHandler.java:78`
  - MicroBlocksCompatHandler skips metadata 15. The IMC loop registers meta 0..14 only ('meta < 15'), so 16-variant blocks (e.g. concrete colours) lose their last subtype for ForgeMicroblock cutting.
  - Evidence: `for(int meta = 0; meta < 15; meta++) {`
  - Fix: Use meta < 16.
  - Plan: Phase 6
  - Status: open
- **B-198** · gameplay · fix in the fork · `src/main/java/com/hbm/handler/pollution/PollutionHandler.java:369-387`
  - PollutionHandler.decorateMob heals mobs on every LivingSpawnEvent subtype. The handler subscribes to the LivingSpawnEvent base class, which includes AllowDespawn (fired every 32 ticks for despawnable mobs); in regions with soot above buffMobThreshold every hostile mob is healed to full health each despawn check, making them near-unkillable.
  - Evidence: `public void decorateMob(LivingSpawnEvent event) ... if(soot > RadiationConfig.buffMobThreshold) { ...; living.heal(living.getMaxHealth()); }`
  - Fix: Subscribe to LivingSpawnEvent.CheckSpawn/SpecialSpawn only (or return unless event is one of those) and heal only when the modifier was just applied.
  - Plan: Phase 6
  - Status: open
- **B-199** · gameplay · fix in the fork · `src/main/java/com/hbm/handler/radiation/ChunkRadiationHandlerPRISM.java:331-371`
  - PRISM SubChunk uses cX for the Z chunk origin. SubChunk.rebuild (L331) and updateBlock (L371) compute int tZ = cX << 4 instead of cZ << 4, so the world z passed to Block.getExplosionResistance and the sZ clamp are wrong away from the diagonal; resistance arrays are built from the wrong column.
  - Evidence: `int tX = cX << 4; int tY = cY << 4; int tZ = cX << 4;`
  - Fix: Change both to int tZ = cZ << 4.
  - Plan: Phase 6
  - Status: open
- **B-200** · gameplay · needs a decision · `src/main/java/com/hbm/hazard/type/HazardTypeRadiation.java:34-36`
  - Neutron activation NBT is counted twice in radiation hazard level. HazardTransformerRadiationNBT (L24-27) already appends a RADIATION entry = hfrHazRadiation + ntmNeutron; HazardTypeRadiation.onUpdate adds ntmNeutron again to EVERY radiation entry (tooltip L60-62 too), so activated items irradiate and display about double.
  - Evidence: `level *= stack.stackSize; if(stack.stackTagCompound.hasKey(HazardTypeNeutron.NEUTRON_KEY)) { level += ...getFloat(NEUTRON_KEY); }`
  - Fix: Remove the NEUTRON_KEY addition from HazardTypeRadiation (transformer already folds it) or from the transformer; balance decision.
  - Plan: decision D22
  - Status: open
- **B-201** · gameplay · fix in the fork · `src/main/java/com/hbm/saveddata/satellites/SatelliteMiner.java:56-58`
  - SatelliteMiner.getCargoForItem looks up an Item-keyed map with a ComparableStack. XSatelliteRegistry.itemToClass is HashMap<Item, Class> (L17) but getOrDefault is called with the ComparableStack, so the result is always null; the NEI SatelliteHandler (L58/72/99) therefore never resolves a miner's cargo pool and shows nothing.
  - Evidence: `XSatelliteRegistry.itemToClass.getOrDefault(satelliteItem, null); // satelliteItem is a ComparableStack`
  - Fix: Use itemToClass.get(satelliteItem.item) (the Item).
  - Plan: Phase 6
  - Status: open
- **B-202** · silent misconfig · needs a decision · `src/main/java/com/hbm/config/RadiationConfig.java:31-73`
  - RadiationConfig.disableNeutron defaults to true. HAZ_01_disableNeutrons defaults TRUE, so the entire neutron hazard path (HazardTypeNeutron, ContaminationUtil NEUTRON, EntityEffectHandler decay) is off by default even though HazardRegistry/OreDictManager register many NEUTRON entries; key also shares the HAZ_01 prefix with disableCoaldust.
  - Evidence: `public static boolean disableNeutron = true; ... createConfigBool(config, CATEGORY_HAZ, "HAZ_01_disableNeutrons", ..., true);`
  - Fix: Decide the fork default (false to enable) and give the key its own prefix number.
  - Plan: decision D22
  - Status: open
- **B-203** · silent misconfig · needs a decision · `src/main/java/com/hbm/handler/HazmatRegistry.java:233-237`
  - HazmatRegistry user JSON replaces the whole resistance table. When config/hbmConfig/hbmRadResist.json exists, entries.clear() drops every built-in and ArmorFSB-provided rating; any armor not listed in the file silently gets 0 radiation resistance.
  - Evidence: `if(conf != null) { entries.clear(); entries.putAll(conf); }`
  - Fix: Merge (putAll without clear) or document that the file must be complete.
  - Plan: decision D30
  - Status: open
- **B-204** · silent misconfig · fix in the fork · `src/main/java/com/hbm/handler/imc/IMCBlastFurnace.java:64`
  - IMCBlastFurnace reads input2 ore list with the wrong tag type. input1 uses getTagList("input1", 8) (string list) but input2 uses type 9; NBTTagCompound.getTagList returns an empty list when the stored list type differs, so an IMC blast furnace recipe whose second input is an ore list silently gets no ore names.
  - Evidence: `L42: data.getTagList("input1", 8); L64: data.getTagList("input2", 9); followed by list.getStringTagAt(i)`
  - Fix: Use tag type 8 for input2 (deprecated API, but still dispatched by MainRegistry.initIMC).
  - Plan: Phase 6
  - Status: open
- **B-205** · silent misconfig · leave · `src/main/java/com/hbm/handler/radiation/ChunkRadiationHandlerSimple.java:51`
  - Simple backend silently drops radiation writes to unloaded chunks. setRadiation requires world.blockExists(x, 0, z); writes for unloaded chunks (fallout edges, satellite/command targets) are discarded without any log, and behaviour differs from PRISM which loads the chunk.
  - Evidence: `if(world.blockExists(x, 0, z)) { ... radWorld.radiation.put(coords, ...) }`
  - Fix: Document the behaviour or queue the write until the chunk loads; keep consistent with PRISM.
  - Plan: not planned
  - Status: open
- **B-206** · silent misconfig · fix in the fork · `src/main/java/com/hbm/hazard/HazardSystem.java:62-63`
  - HazardSystem.register(ItemStack) does not makeSingular the key. register(ItemStack) stores new ComparableStack(stack) which copies stackSize, but every lookup (L83, L138) uses makeSingular(); a registration with stackSize > 1 creates a key that never matches. Latent: all current registrations use size 1.
  - Evidence: `stackMap.put(new ComparableStack((ItemStack)o), data); vs ComparableStack comp = new ComparableStack(stack).makeSingular();`
  - Fix: Call .makeSingular() in register() for the ItemStack and ComparableStack branches.
  - Plan: Phase 6
  - Status: open
- **B-207** · silent misconfig · fix in the fork · `src/main/java/com/hbm/hazard/type/HazardTypeAutism.java:24`
  - HazardTypeAutism/Glitch are gated by disableBlinding. Both types reuse RadiationConfig.disableBlinding (HazardTypeGlitch L27) rather than their own switch, so disabling the blinding hazard silently disables these unrelated effects too; Radiation and Digamma have no kill switch at all.
  - Evidence: `if(RadiationConfig.disableBlinding) (HazardTypeAutism.java:24, HazardTypeGlitch.java:27)`
  - Fix: Add dedicated flags or document the shared switch.
  - Plan: Phase 6
  - Status: open
- **B-208** · silent misconfig · leave · `src/main/java/com/hbm/inventory/OreDictManager.java:135`
  - OreDictManager names uranium 'Uraninite' when GT6 is loaded. The U DictFrame switches its ore-dict material name to 'Uraninite' under GT6, changing every autogenerated hazard/ore-dict key for uranium (ingotUraninite etc.) and breaking recipes written against 'Uranium' in that configuration.
  - Evidence: `public static final DictFrame U = new DictFrame(Compat.isModLoaded(Compat.MOD_GT6) ? "Uraninite" : "Uranium");`
  - Fix: Intentional GT6 compat; keep unless GT6 users report missing recipes.
  - Plan: not planned
  - Status: open
- **B-209** · silent misconfig · needs a decision · `src/main/java/com/hbm/saveddata/satellites/XSatelliteRegistry.java:35-36`
  - SatelliteHorizons and SatelliteRailgun registered with colour 0,0,0. Both satellites get an all-black registered colour, so their orbit markers/renders are invisible against space while every other satellite has a distinct colour.
  - Evidence: `registerSatellite(SatelliteHorizons.class, ModItems.sat_gerald, 0.0F, 0.0F, 0.0F); registerSatellite(SatelliteRailgun.class, ..., 0.0F, 0.0F, 0.0F);`
  - Fix: Give them visible colours.
  - Plan: decision D22
  - Status: open
- **B-210** · leak / perf · fix in the fork · `src/main/java/com/hbm/handler/neutron/NeutronNodeWorld.java:39-43`
  - NeutronNodeWorld.removeEmptyWorlds drops node caches when a world has no streams. Every server tick StreamWorld objects with an empty stream list are removed, which also discards their nodeCache; an idling reactor (no flux for one tick) rebuilds every node next tick, defeating the cache.
  - Evidence: `streamWorlds.values().removeIf((streamWorld) -> { return streamWorld.streams.isEmpty(); });`
  - Fix: Remove worlds only when both streams and nodeCache are empty, or on WorldEvent.Unload.
  - Plan: Phase 5
  - Status: open
- **B-211** · leak / perf · needs a decision · `src/main/java/com/hbm/handler/radiation/ChunkRadiationHandlerPRISM.java:92`
  - PRISM.setRadiation force-loads chunks. setRadiation calls world.getChunkFromBlockCoords(x, z) with no chunkExists guard, which generates/loads the chunk; far-away writers (satellites, commands, nuke rays) can pull chunks in, whereas the Simple backend drops the write.
  - Evidence: `world.getChunkFromBlockCoords(x, z).isModified = true;`
  - Fix: Guard with world.getChunkProvider().chunkExists(x >> 4, z >> 4) before marking modified.
  - Plan: decision D22
  - Status: open
- **B-212** · leak / perf · fix in the fork · `src/main/java/com/hbm/handler/radiation/ChunkRadiationHandlerSimple.java:181`
  - Simple radiation backend unload removes by Chunk instead of ChunkCoordIntPair. The per-world map is keyed by ChunkCoordIntPair but unload calls remove(event.getChunk()) with a Chunk object, which never matches; radiation entries accumulate until world unload and unloaded chunks keep being iterated/spread every cycle. Same bug in ChunkRadiationHandler3D L181 (dead).
  - Evidence: `radWorld.radiation.remove(event.getChunk());`
  - Fix: Use radWorld.radiation.remove(event.getChunk().getChunkCoordIntPair()) as PRISM does (L171).
  - Plan: Phase 5
  - Status: open
- **B-213** · dead code · needs a decision · `src/main/java/com/hbm/config/RadiationConfig.java:72-84`
  - RadiationConfig.disableFibrosis and smokeStackSootMult are dead options. HAZ_06_disableFibrosis and POL_08_smokeStackSootMult are written to hbm.cfg with descriptive comments but no code reads them (TileEntityChimneyBase uses fluid / 100F directly), so users toggling them see no effect.
  - Evidence: `L72 "HAZ_06_disableFibrosis" and L84 "POL_08_smokeStackSootMult" are created; grep finds no reader outside RadiationConfig.java`
  - Fix: Wire them (fibrosis in handleLungDisease, mult in TileEntityChimneyBase) or remove the options.
  - Plan: decision D22
  - Status: open
- **B-214** · dead code · fix in the fork · `src/main/java/com/hbm/handler/ThreeInts.java:46-48`
  - ThreeInts.compareTo violates the Comparable contract. compareTo returns 1 for every unequal pair (never negative), so ordering is inconsistent; fields x/y/z are public and mutable while instances key AtmosphereBlob.plants (L60/L398). No sorted collection uses it today, so it is latent.
  - Evidence: `public int compareTo(ThreeInts o) { return equals(o) ? 0 : 1; }`
  - Fix: Implement a lexicographic compare (x, then y, then z) or drop Comparable; consider making the fields final.
  - Plan: Phase 7
  - Status: open
- **B-215** · dead code · leave · `src/main/java/com/hbm/handler/pollution/PollutionHandler.java:358`
  - PollutionType.FALLOUT is an unused enum slot. FALLOUT is declared in PollutionType and serialised by name but nothing increments, reads, spreads or displays it (ItemPollutionDetector L31 is commented out); it just widens every PollutionData array.
  - Evidence: `SOOT, POISON, HEAVYMETAL, FALLOUT; // only other reference: ItemPollutionDetector.java:31 (commented)`
  - Fix: Either implement fallout pollution (EntityFalloutRain producer + detector line) or leave the slot documented as reserved.
  - Plan: not planned
  - Status: open
- **B-216** · dead code · needs a decision · `src/main/java/com/hbm/handler/radiation/ChunkRadiationHandlerNT.java:1`
  - Dead radiation backends NT/3D/Blank. ChunkRadiationHandlerNT, ChunkRadiationHandler3D (@Untested, 'will most definitely crash', same unload leak) and ChunkRadiationHandlerBlank are never instantiated; only Simple (default) and PRISM are selectable in RadiationConfig.loadFromConfig.
  - Evidence: `only 'new ChunkRadiationHandlerSimple()' (ChunkRadiationManager.java:15) and 'new ChunkRadiationHandlerPRISM()' (RadiationConfig.java:60) exist`
  - Fix: Delete the three classes (and IRadResistantBlock if unused) or mark them clearly as non-functional.
  - Plan: decision D22
  - Status: open
- **B-217** · dead code · fix in the fork · `src/main/java/com/hbm/main/ModEventHandler.java:1466-1474`
  - Legacy neutron-activation branch in ModEventHandler.onPlayerTick is unreachable. The loop contaminates from ntmNeutron only when getHazardLevelFromStack(stack, RADIATION) == 0, but HazardTransformerRadiationNBT always adds a RADIATION entry containing ntmNeutron, so the level is > 0 whenever the key is non-zero and the branch never fires.
  - Evidence: `if(stack2.hasTagCompound() && HazardSystem.getHazardLevelFromStack(stack2, RADIATION) == 0) { activation = ...NEUTRON_KEY; contaminate(...)`
  - Fix: Delete the loop (the hazard system handles it).
  - Plan: Phase 7
  - Status: open
### Dimensions and world generation (`space`)

- **B-218** · crash · fix in the fork · `src/main/java/com/hbm/world/feature/BedrockOre.java:56-60`
  - BedrockOre.generateAuto NPEs for a Body without a bedrock table. CelestialBedrockOre.get(body).types is dereferenced without a null check, so any new Body enum entry that lacks an ItemBedrockOreNew register(...) entry crashes chunk generation on that planet (this is the trap waiting for Thatmo).
  - Evidence: `for(CelestialBedrockOreType type : CelestialBedrockOre.get(body).types) { ... totalLevel /= CelestialBedrockOre.get(body).types.length;`
  - Fix: Return early when get(body) is null and log once.
  - Plan: Phase 2
  - Status: open
- **B-219** · gameplay · fix in the fork · `src/main/java/com/hbm/dim/ChunkProviderCelestial.java:414`
  - ChunkProviderCelestial.populate clears isHellWorld and never restores it. populate sets worldObj.provider.isHellWorld = false to keep other mods' generators working, but nothing restores it; the vacuum trick is off until updateWeather runs next tick, so water can be placed on airless bodies in that window.
  - Evidence: `worldObj.provider.isHellWorld = false; // Prevent other mod world generators thinking this is hell`
  - Fix: Save the previous value and restore it at the end of populate.
  - Plan: Phase 6
  - Status: open
- **B-220** · gameplay · fix in the fork · `src/main/java/com/hbm/dim/WorldProviderCelestial.java:693-698`
  - WorldProviderCelestial.canRespawnHere depends on a global static flag. canRespawnHere returns true only when static attemptingSleep was set by ModEventHandler.onTrySleep (L1839) and clears it; the flag is shared by all players/dimensions (WorldProviderOrbit L225 also consumes it), so concurrent sleeps or other mods calling canRespawnHere get inconsistent answers.
  - Evidence: `public static boolean attemptingSleep = false; ... if(attemptingSleep) { attemptingSleep = false;`
  - Fix: Return true and handle bed logic via getRespawnDimension/PlayerSleepInBedEvent, or key the flag per player.
  - Plan: Phase 8
  - Status: open
- **B-221** · gameplay · fix in the fork · `src/main/java/com/hbm/dim/WorldProviderCelestial.java:723-725`
  - Sleeping on a planet resets local time to 0 instead of next morning. resetRainAndThunder computes i = getWorldTime() % dayLength then setWorldTime(i - i % dayLength), which is always 0; the per-dimension day counter is wiped every time all players sleep, breaking anything keyed on elapsed days.
  - Evidence: `long i = getWorldTime() % dayLength; setWorldTime(i - i % dayLength);`
  - Fix: long t = getWorldTime(); setWorldTime(t - t % dayLength + dayLength); (vanilla semantics).
  - Plan: Phase 6
  - Status: open
- **B-222** · gameplay · fix in the fork · `src/main/java/com/hbm/dim/duna/biome/BiomeGenDunaPlains.java:76`
  - Seventeen celestial biomes use Math.random() in genTerrainBlocks. Terrain surface replacement rolls Math.random() > 0.4 instead of the seeded Random, so planet surfaces are not reproducible per seed: Duna Plains/Polar/PolarHills, Dres Canyon/Plains, all five Eve biomes, all five Tekto biomes, Laythe Polar and Thatmo (see grep list).
  - Evidence: `if (Math.random() > 0.4) { (BiomeGenDunaPlains:76, BiomeGenThatmo:88, BiomeGenEveOcean:75, BiomeGenForest:77, BiomeGenDresPlains:67, ...)`
  - Fix: Replace with rand.nextDouble() > 0.4 using the Random passed into genTerrainBlocks (changes future chunk surfaces only).
  - Plan: Phase 6
  - Status: open
- **B-223** · gameplay · fix in the fork · `src/main/java/com/hbm/dim/thatmo/WorldProviderThatmo.java:29-31`
  - WorldProviderThatmo passes dimensionId as rainfall and rebuilds its biome. registerWorldChunkManager builds WorldChunkManagerHell(new BiomeGenThatmo(id), dimensionId): the int dimension id (413_025) is passed as the float rainfall, and a fresh BiomeGenThatmo is constructed on every call instead of a static instance.
  - Evidence: `this.worldChunkMgr = new WorldChunkManagerHell(new BiomeGenThatmo(SpaceConfig.thatmoBiome), dimensionId);`
  - Fix: Use a static final BiomeGenThatmo instance and 0.0F for rainfall like the other single-biome providers.
  - Plan: Phase 6
  - Status: open
- **B-224** · gameplay · needs a decision · `src/main/java/com/hbm/lib/HbmWorldGen.java:88-102`
  - HbmWorldGen rolls meteorites and crashed spaceships on every celestial dimension. generateSurface performs the Meteorite and Spaceship rolls before the 'celestial dims return' check (L107), so airless bodies get overworld-style meteor craters and vertibird wrecks with overworld loot at overworld frequencies.
  - Evidence: `L88-102 Meteorite/Spaceship rolls run first; L107 if(world.provider instanceof WorldProviderCelestial && dimensionId != 0) return; comes after`
  - Fix: Gate by body (e.g. only bodies with hasLife or via WorldGeneratorCelestial) if unwanted.
  - Plan: decision D23
  - Status: open
- **B-225** · gameplay · needs a decision · `src/main/java/com/hbm/main/ModEventHandler.java:938-972`
  - updateWaterOpacity mutates Blocks.water opacity globally on every world tick. Each ticking world sets Blocks.water/flowing_water light opacity to its provider's value (Laythe 1, everything else 3); on a server with Laythe and another dimension loaded the global block property flips every tick, giving inconsistent light updates between worlds.
  - Evidence: `worldTick: updateWaterOpacity(event.world); ... Blocks.water.setLightOpacity(waterOpacity); Blocks.flowing_water.setLightOpacity(waterOpacity);`
  - Fix: Give Laythe its own water block, or only set opacity on the client for the current world.
  - Plan: decision D23
  - Status: open
- **B-226** · gameplay · needs a decision · `src/main/java/com/hbm/world/feature/OreLayer3D.java:137`
  - OreLayer3D reuses cacheX for the z noise axis. cacheZ is filled (L116/123) but never read; nz = cacheX[ox][y] duplicates the x noise, so 3D ore strata are shaped by two axes instead of three. Fixing it changes ore placement in existing worlds.
  - Evidence: `double nx = cacheX[oz][y]; double nz = cacheX[ox][y];`
  - Fix: double nz = cacheZ[ox][y]; (accept that new chunks get different strata).
  - Plan: decision D23
  - Status: open
- **B-227** · gameplay · fix in the fork · `src/main/java/com/hbm/world/gen/NTMWorldGenerator.java:283-304`
  - NTMWorldGenerator global hasPopulationEvent flag suppresses planet structures. The single field is set true by ANY dimension's PopulateChunkEvent.Pre (overworld) and reset only on WorldEvent.Load; planets never post the event and rely on generate(), which returns early while the flag is true, so NBT structures stop generating on all planets after any overworld chunk populates.
  - Evidence: `generateStructures(PopulateChunkEvent.Pre) { hasPopulationEvent = true; ... } generate(...) { if(hasPopulationEvent) return;`
  - Fix: Track the flag per dimension id (Set<Integer>) or decide by chunkGenerator type instead of a global boolean.
  - Plan: Phase 6 after D23
  - Status: open
- **B-228** · silent misconfig · leave · `src/main/java/com/hbm/config/SpaceConfig.java:114-150`
  - Duplicate config key prefixes in SpaceConfig and WorldConfig. '17.10_' is used for both tektoDimension and thatmoDimension, '16.24_' for eveRiverBiome and laytheCoastBiome, and WorldConfig '2.18_' for cinnebar and cobalt; keys stay distinct so nothing breaks, but the sorted hbm.cfg misorders them and misleads editing.
  - Evidence: `"17.10_tektoDimension"/"17.10_thatmoDimension"; "16.24_eveRiverBiome"/"16.24_laytheCoastBiome"; WorldConfig L208-209 "2.18_cinnebar"/"2.18_cobalt"`
  - Fix: Renumber only when a config migration is acceptable (renaming keys resets user values).
  - Plan: not planned
  - Status: open
- **B-229** · silent misconfig · fix in the fork · `src/main/java/com/hbm/config/StructureConfig.java:14-78`
  - StructureConfig.structureMaxChunks field default 12 vs config default 16. The static initialiser says 12 and setDef(…, 12) at L127 uses 12 as the zero-fallback, but the value written to hbm.cfg is 16; readers of the source get a different spacing than users get by default.
  - Evidence: `L14 structureMaxChunks = 12; L78 createConfigInt(..., "5.02_structureMaxChunks", ..., 16); L127 setDef(structureMaxChunks, 12);`
  - Fix: Align both to one number (16).
  - Plan: Phase 6
  - Status: open
- **B-230** · silent misconfig · fix in the fork · `src/main/java/com/hbm/dim/CelestialBody.java:258-272`
  - CelestialBody.getTraits returns the live saved trait map despite its comment. The comment says 'Gets a clone of the body traits that are SAFE for modifying', but when overrides exist the method returns SolarSystemWorldSavedData.traitMap's live HashMap (L159-161) with shared trait instances; callers mutating it change persisted state without markDirty and desync clients.
  - Evidence: `// Gets a clone of the body traits that are SAFE for modifying ... currentTraits = traitsData.getTraits(getBody(world).name); return currentTraits;`
  - Fix: Return new HashMap<>(currentTraits) (and clone trait objects if callers mutate them) or fix the comment and audit callers.
  - Plan: Phase 6
  - Status: open
- **B-231** · silent misconfig · needs a decision · `src/main/java/com/hbm/dim/CelestialBody.java:262-266`
  - getTraits fallback map exposes shared default trait instances to mutation. When a body has no saved overrides, getTraits builds a fresh map but fills it with the shared default CelestialBodyTrait instances from body.traits; consumers such as CBT_Atmosphere.consumeGas that mutate entries in place alter the defaults for every world.
  - Evidence: `for(CelestialBodyTrait trait : body.traits.values()) { currentTraits.put(trait.getClass(), trait); }`
  - Fix: Clone trait objects (add a copy() on CelestialBodyTrait) when building the fallback map.
  - Plan: decision D23
  - Status: open
- **B-232** · silent misconfig · fix in the fork · `src/main/java/com/hbm/dim/CelestialBody.java:506-514`
  - CelestialBody.getBody silently falls back to Kerbin. getBody(String)/getBody(int) return dimToBodyMap.get(0) for unknown names/ids, so any unregistered dimension (Thatmo, other mods' dims) inherits Earth's traits without any log; getBodyOrNull exists but is rarely used.
  - Evidence: `return body != null ? body : dimToBodyMap.get(0);`
  - Fix: Log once per unknown id, or use getBodyOrNull in provider code paths where absence matters.
  - Plan: Phase 6
  - Status: open
- **B-233** · silent misconfig · needs a decision · `src/main/java/com/hbm/dim/ChunkProviderCelestial.java:413-430`
  - Planet chunk providers never post PopulateChunkEvent. populate() decorates and spawns directly without posting PopulateChunkEvent.Pre/Post (vanilla ChunkProviderGenerate does), so other mods' population hooks (and ModEventHandlerImpact.populateChunkPost) never run on planets and NTMWorldGenerator must fall back to its global generate() path.
  - Evidence: `populate(...) { ... biomegenbase.decorate(worldObj, rand, k, l); ... } with no MinecraftForge.EVENT_BUS.post(new PopulateChunkEvent.Pre/Post`
  - Fix: Post PopulateChunkEvent.Pre before decorate and Post after, mirroring vanilla.
  - Plan: Phase 6 after D23
  - Status: open
- **B-234** · silent misconfig · needs a decision · `src/main/java/com/hbm/dim/SolarSystem.java:275-321`
  - Thatmo dimension is registered but its body is unbound. new CelestialBody("thatmo") has no dim id and Body.THATMO is commented out, yet PlanetGen registers thatmoDimension (L47); getBody falls back to Kerbin so Thatmo gets Earth traits and Kerbin ore meta; WorldGeneratorThatmo is never registered; bedrock table (ItemBedrockOreNew L284-290) commented out.
  - Evidence: `L275 new CelestialBody("thatmo"); L321 //THATMO("thatmo"); sit this one out buddy :); PlanetGen.java:47 registerDimension(thatmoDimension, ...)`
  - Fix: Either append THATMO to Body, use the (name, id, Body) ctor with withBlockTextures, register WorldGeneratorThatmo and the bedrock table, or unregister the dimension until it is finished.
  - Plan: decision D23
  - Status: open
- **B-235** · silent misconfig · fix in the fork · `src/main/java/com/hbm/dim/SolarSystemWorldSavedData.java:40`
  - SolarSystemWorldSavedData.get() relies on DimensionManager.getWorlds()[0]. getWorlds()[0] is the first value of a Hashtable, not guaranteed to be dim 0; it only works because every WorldServerMulti shares the overworld's mapStorage. Anyone 'fixing' the lookup to a per-dimension storage would split trait data.
  - Evidence: `return get(DimensionManager.getWorlds()[0]);`
  - Fix: Use DimensionManager.getWorld(0) with a null fallback and comment the shared-storage assumption.
  - Plan: Phase 6
  - Status: open
- **B-236** · silent misconfig · fix in the fork · `src/main/java/com/hbm/dim/SolarSystemWorldSavedData.java:63-67`
  - SolarSystemWorldSavedData.readFromNBT silently drops traits that fail to load. Each trait is created via newInstance() and any exception (missing no-arg ctor, NBT error) is swallowed, so a body silently reverts that trait to defaults with no log entry.
  - Evidence: `try { CelestialBodyTrait trait = entry.getValue().newInstance(); trait.readFromNBT(...); ... } catch (Exception ex) {}`
  - Fix: Log the failure with MainRegistry.logger ([Space] prefix).
  - Plan: Phase 6
  - Status: open
- **B-237** · silent misconfig · fix in the fork · `src/main/java/com/hbm/world/ModBiomes.java:13-17`
  - ModBiomes double-registers Duna biome dictionary types and tags dunaHills twice. BiomeGenBaseDuna's constructor already registers COLD/DRY/DEAD (L31); ModBiomes.init registers again and gives dunaHills two conflicting tag sets (HILLS vs SNOWY+MOUNTAIN); BiomeDictionary unions them so dunaHills is simultaneously HILLS, SNOWY and MOUNTAIN.
  - Evidence: `L16 registerBiomeType(dunaHills, COLD, DRY, DEAD, HILLS); L17 registerBiomeType(dunaHills, COLD, DRY, DEAD, SNOWY, MOUNTAIN);`
  - Fix: Keep one registration site with the intended tag set.
  - Plan: Phase 6
  - Status: open
- **B-238** · leak / perf · fix in the fork · `src/main/java/com/hbm/dim/Ike/WorldGeneratorIke.java:67-82`
  - WorldGeneratorIke scans every block column of each chunk for pedestals. Every Ike chunk population iterates 16x16x256 world.getBlock calls looking for ModBlocks.pedestal to inject a divine shard ('the HACKIEST hack'); it is also order-dependent on the artifact structure already existing in that chunk.
  - Evidence: `for (int y = 0; y < world.getHeight(); y++) { Block b = world.getBlock(ox, y, oz); if (b == ModBlocks.pedestal) {`
  - Fix: Set the pedestal item from the structure's LogicBlock/tile transformer instead of scanning.
  - Plan: Phase 6
  - Status: open
- **B-239** · leak / perf · needs a decision · `src/main/java/com/hbm/dim/eve/WorldGeneratorEve.java:56-63`
  - Eve volcano/spike generators cascade chunks. WorldGenEveSpike and the electric volcano are placed from IWorldGenerator.generate and span multiple chunks (author TODO), forcing neighbouring chunk generation recursively during population (lag spikes, possible runaway on Eve).
  - Evidence: `// TODO: these span multiple chunks, fix this cascade! if(rand.nextInt(100) == 0) { volcano.generate(world, rand, x, y, z); }`
  - Fix: Offset by +8 and bound the features to the 16x16 population window, or convert to a MapGen carver.
  - Plan: decision D23
  - Status: open
- **B-240** · leak / perf · needs a decision · `src/main/java/com/hbm/main/ModEventHandler.java:823-831`
  - overrideOverworldProvider re-registers provider 0 on every non-Earth world load. onLoad (LOWEST) calls overrideOverworldProvider() whenever the loaded world's provider is not WorldProviderEarth, i.e. for the Nether, End and every planet; each call unregisters and re-registers DimensionManager provider type 0 although it was already replaced in PostLoad.
  - Evidence: `if(!(event.world.provider instanceof WorldProviderEarth)) { PlanetGen.overrideOverworldProvider(); }`
  - Fix: Only override when event.world.provider.dimensionId == 0 and it is not WorldProviderEarth.
  - Plan: decision D23
  - Status: open
- **B-241** · dead code · fix in the fork · `src/main/java/com/hbm/dim/dres/WorldGeneratorDres.java:77-80`
  - WorldGeneratorDres registers ore_lanthanium as a valid body twice. BlockOre.addValidBody(ModBlocks.ore_lanthanium, DRES) appears twice; harmless duplicate that suggests a copy-paste slip (maybe another ore was meant).
  - Evidence: `L77 and L80: BlockOre.addValidBody(ModBlocks.ore_lanthanium, SolarSystem.Body.DRES);`
  - Fix: Remove the duplicate or replace it with the intended ore.
  - Plan: Phase 7
  - Status: open
- **B-242** · dead code · needs a decision · `src/main/java/com/hbm/dim/duna/genlayer/GenLayerDunaHills.java:1`
  - GenLayerDunaHills and GenLayerDunaPolarHills are unused. WorldProviderDuna.createBiomeGenerators uses only GenLayerDunaBiomes/DiversifyDuna/DunaLowlands; the hills layers have no references, so dunaHills/dunaPolarHills biomes may never be placed by the layer stack.
  - Evidence: `grep 'GenLayerDunaHills|GenLayerDunaPolarHills' outside the two files: none`
  - Fix: Wire them into createBiomeGenerators or delete them.
  - Plan: decision D23
  - Status: open
- **B-243** · dead code · fix in the fork · `src/main/java/com/hbm/dim/trait/CBT_Compromised.java:3`
  - Empty top-level CBT_Compromised shadows the registered inner CBT_COMPROMISED. CBT_Compromised is an empty class never registered or referenced; the live trait is CelestialBodyTrait.CBT_COMPROMISED (registered as 'infected', used by SkyProviderCelestial L378). Two near-identical names invite using the wrong one.
  - Evidence: `public class CBT_Compromised extends CelestialBodyTrait { }`
  - Fix: Delete CBT_Compromised.java.
  - Plan: Phase 7
  - Status: open
- **B-244** · dead code · needs a decision · `src/main/java/com/hbm/world/biome/BiomeGenNoMansLand.java:24`
  - BiomeGenNoMansLand and WorldProviderTom are dead and hard-code biome id 99. The only reference is a commented-out initDictionary call (MainRegistry L308); WorldProviderTom is referenced nowhere. The static biome uses hard-coded id 99, which equals the default SpaceConfig.mohoLavaBiome (L67), so reviving it collides.
  - Evidence: `public static final BiomeGenBase noMansLand = new BiomeGenNoMansLand(99)...; MainRegistry.java:308 //BiomeGenNoMansLand.initDictionary();`
  - Fix: Delete the three classes or move the id into SpaceConfig before reviving.
  - Plan: decision D23
  - Status: open
- **B-245** · dead code · fix in the fork · `src/main/java/com/hbm/world/feature/OreLayer.java:1`
  - OreLayer (2D) is never instantiated. The legacy 2D stratum generator self-registers on EVENT_BUS in its constructor but nothing constructs it; only OreLayer3D/OreCave/SchistStratum are live.
  - Evidence: `grep 'new OreLayer(' in src/main/java: no results`
  - Fix: Delete or mark @Deprecated.
  - Plan: Phase 7
  - Status: open
- **B-246** · dead code · fix in the fork · `src/main/java/com/hbm/world/test/WorldGenTest.java:1`
  - world/test scaffolding is unreferenced. MapGenTest, StructureStartTest, StructureComponentTest and WorldGenTest use System.out logging and are never registered or referenced outside their package.
  - Evidence: `grep 'world.test|WorldGenTest|MapGenTest' outside src/main/java/com/hbm/world/test/: none`
  - Fix: Delete the package.
  - Plan: Phase 7
  - Status: open
### Client rendering, models, sound (`render`)

- **B-247** · crash · fix in the fork · `src/main/java/com/hbm/render/anim/AnimationLoader.java:40-45`
  - AnimationLoader: missing JSON returns null, malformed JSON kills client. A missing animation file yields null (later NPE in the gun config's .get("Fire")), while a present-but-malformed JSON (no offset/anim object) throws inside ResourceManager's static initializer and the client dies at startup with an obscure ExceptionInInitializerError.
  - Evidence: `} catch (IOException ex) { return null; } ... json.getAsJsonObject("offset").entrySet()`
  - Fix: Log the path via MainRegistry.logger and return an empty HashMap on any failure so a bad animation degrades to 'no animation'.
  - Plan: Phase 6
  - Status: open
- **B-248** · crash · fix in the fork · `src/main/java/com/hbm/render/block/RenderISBRHUniversal.java:12-17`
  - RenderISBRHUniversal casts block unconditionally. A block whose getRenderType() returns ISBRHUniversal.renderID without implementing the interface throws ClassCastException in chunk rendering (client crash) instead of a clear error.
  - Evidence: `((ISBRHUniversal) block).renderInventoryBlock(block, metadata, modelId, renderer); ... return ((ISBRHUniversal) block).renderWorldBlock(...)`
  - Fix: Add an instanceof guard that logs once via MainRegistry.logger and returns false.
  - Plan: Phase 6
  - Status: open
- **B-249** · crash · fix in the fork · `src/main/java/com/hbm/render/icon/TextureAtlasSpriteMutatable.java:24-25`
  - TextureAtlasSpriteMutatable crashes stitching when base texture is missing. Upstream TODO: using a mutatable sprite whose base texture is missing crashes the game during texture stitching (mipmap level -1). Any new ItemAutogen-style icon with a typo'd texture name takes the client down at startup.
  - Evidence: `* TODO: using this with a missing texture for some reason crashes the game * TexMan's mip map levels seem to be -1 for some reason`
  - Fix: Fall back to the missing-texture sprite in loadSprite when the base resource is absent.
  - Plan: Phase 6
  - Status: open
- **B-250** · crash · leave · `src/main/java/com/hbm/render/loader/HFRWavefrontObject.java:177`
  - Mixed-mode HFRWavefrontObject throws from renderAll/renderPart. A model constructed with mixedMode=true (rbmk_element, ResourceManager L1795) throws UnsupportedOperationException on renderAll/renderPart; only tessellate*/ObjUtil work. Any renderer that reuses that field for direct drawing crashes at render time.
  - Evidence: `if(allowMixedMode) throw new UnsupportedOperationException("Rendering of mixed-mode model " + this.fileName + " is not supported!");`
  - Fix: Keep as documented design constraint; optionally implement mixed-mode rendering by tessellating tris and quads separately.
  - Plan: not planned
  - Status: open
- **B-251** · crash · fix in the fork · `src/main/java/com/hbm/render/loader/HFRWavefrontObjectVBO.java:39-47`
  - HFRWavefrontObjectVBO crashes client startup on quad or normal-less OBJ. Buffers are sized faces*3 and face.vertexNormals[i] is read unconditionally, so a quad-based or normal-less OBJ loaded with .asVBO() throws BufferOverflow/NullPointerException in ResourceManager's static init and the client dies at startup. Latent: shipped VBO models are triangulated with normals.
  - Evidence: `createFloatBuffer(g.faces.size() * 3 * VERTEX_SIZE); ... Vertex normal = face.vertexNormals[i];`
  - Fix: Validate in load(): triangulate quads or throw a ModelFormatException naming the file; null-check normals and compute face normals when absent.
  - Plan: Phase 6
  - Status: open
- **B-252** · crash · fix in the fork · `src/main/java/com/hbm/render/util/ObjUtil.java:58-130`
  - ObjUtil.renderWithIcon NPEs on OBJ without UV coordinates. f.textureCoordinates[i] is dereferenced without a null check, so an ISBRH drawing an OBJ exported without UVs throws NullPointerException during chunk rendering. Static red/green/blue tint state is global (forgetting clearColor tints later draws).
  - Evidence: `TextureCoordinate t = f.textureCoordinates[i];`
  - Fix: Guard with if(f.textureCoordinates == null) use icon min UVs; leave tint state but document clearColor().
  - Plan: Phase 6
  - Status: open
- **B-253** · gameplay · fix in the fork · `src/main/java/com/hbm/main/ModEventHandlerClient.java:354-371`
  - Hotbar animation expiry runs ungated in RenderGameOverlayEvent.Pre. The '/// HANLDE ANIMATION BUSES ///' expiry loop sits in onOverlayRender (L152) with no event.type check: it runs once per HUD element per frame and never while the HUD is hidden (F1/GUI), so expired gun animations linger. startMillis is currentTimeMillis (HbmAnimationPacket:121) vs Clock.get_ms().
  - Evidence: `for(int i = 0; i < HbmAnimations.hotbar.length; i++) ... if(time > animation.animation.getDuration()) HbmAnimations.hotbar[i][j] = null;`
  - Fix: Move the loop into clientTick(ClientTickEvent) at L983 (phase END) or gate it on event.type == ElementType.ALL.
  - Plan: Phase 6
  - Status: open
- **B-254** · gameplay · leave · `src/main/java/com/hbm/main/ModEventHandlerRenderer.java:673-676`
  - Badge HUD still hidden by F1 despite ElementType.ALL. onRenderHUD renders badges under ElementType.ALL of RenderGameOverlayEvent.Pre, which vanilla does not fire while the HUD is hidden; the upstream TODO acknowledges badges vanish in F1 contrary to intent.
  - Evidence: `//TODO: using ALL doesn't work as anticipated - still hides in F1. need a different event for this`
  - Fix: Render badges from RenderWorldLastEvent or a ClientTickEvent-driven overlay if they must survive F1; otherwise drop the TODO.
  - Plan: not planned
  - Status: open
- **B-255** · gameplay · fix in the fork · `src/main/java/com/hbm/render/anim/BusAnimationSequence.java:83-87`
  - BusAnimationSequence.holdUntil wrong when animation speed is not 1. holdUntil(end) computes end - getTotalTime() from already-scaled keyframe durations, so after multiplyTime()/setTimeMult != 1 the hold length is wrong and buses desync (upstream FIXME). Affects Trenchmaster reload animations which use setTimeMult(0.5).
  - Evidence: `//FIXME: holdUntil breaks as soon as the animation speed is not 1`
  - Fix: Compute the hold from originalDuration sums (unscaled total) and let multiplyTime rescale it, or store holdUntil as an end-time keyframe.
  - Plan: Phase 8
  - Status: open
- **B-256** · silent misconfig · fix in the fork · `src/main/java/com/hbm/main/ClientProxy.java:2183-2206`
  - ClientProxy.getLoopedSound discards the pitch argument. All three overloads set position/volume/range/keepAlive but never call updatePitch, so the pitch parameter is dropped. Callers that do not re-apply it play at 1.0: EntityRideableRocket.java:548/554 passes 0.9-1.4 random pitch for the stage-decouple sound and never calls updatePitch.
  - Evidence: `audio.updatePosition(x, y, z); audio.updateVolume(volume); audio.updateRange(range); return audio;`
  - Fix: Call audio.updatePitch(pitch) in all three overloads (AudioWrapperClient.updatePitch exists, L53); then the per-tick updatePitch calls elsewhere stay valid.
  - Plan: Phase 6
  - Status: open
- **B-257** · silent misconfig · fix in the fork · `src/main/java/com/hbm/main/ResourceManager.java:645-1471`
  - ResourceManager holds 45 texture paths that do not exist on disk. 45 ResourceLocation fields (L645-648, 668-685, 827-828, 859-863, 872-878, 890-900, 1112-1129, 1217-1219, 1471) point at absent PNGs. Live users: RenderForceField:23 (radar_body_tex; ModBlocks.ff never placed), RenderFOEQ:72, RendererObjTester (igen_*, nikonium). The rest are dead fields.
  - Evidence: `L827 textures/models/radar_base.png (file is textures/models/machines/radar_base.png); L873 textures/models/sat_radar.png absent`
  - Fix: Delete the 40 unused fields; point radar_body_tex at textures/models/machines/radar_base.png; drop RenderFOEQ.getEntityTexture's sat_foeq_tex.
  - Plan: Phase 7
  - Status: open
- **B-258** · silent misconfig · fix in the fork · `src/main/java/com/hbm/render/anim/AnimationLoader.java:68-77`
  - AnimationLoader parses rotmode but never stores it. The rotmode block is parsed into a local double[] but rotModes.put() is never called, so every bus falls back to {0,1,2} (XYZ). No effect today (only lag.json has rotmode and it is all XYZ) but any future export with another Euler order is silently mis-rotated.
  - Evidence: `rotMode[2] = getRot(mode.charAt(1)); } } (no rotModes.put; L91 rotModes.containsKey never true)`
  - Fix: Add rotModes.put(root.getKey(), rotMode) after the three getRot lines; shipped JSONs are unaffected.
  - Plan: Phase 6
  - Status: open
- **B-259** · dead code · needs a decision · `src/main/java/com/hbm/particle/psys/engine/EventHandlerParticleEngine.java:14-30`
  - Unfinished particle psys engine is registered and allocates per world load. ClientProxy.java:170 registers EventHandlerParticleEngine on both buses; its tick/render handlers are empty and onWorldLoad allocates a new ParticleEngine (layers empty) every world load. Pure dead weight in the hot event path.
  - Evidence: `public void onRenderWorldLast(RenderWorldLastEvent event) { //float interp = event.partialTicks; }`
  - Fix: Unregister the handler (drop ClientProxy L170) until the engine is finished, or delete particle/psys/engine.
  - Plan: decision D24
  - Status: open
- **B-260** · dead code · fix in the fork · `src/main/java/com/hbm/render/entity/projectile/RenderBombletTheta.java:22`
  - RenderBombletTheta fallback texture bombletThetaTexture.png missing. boyTexture points at textures/models/bombletThetaTexture.png which does not exist; the renderer is only bound to EntityBombletZeta (ClientProxy L672) which takes the bombletZetaTexture branch, so the missing file is never bound in practice.
  - Evidence: `boyTexture = new ResourceLocation(RefStrings.MODID, "textures/models/bombletThetaTexture.png"); (absent)`
  - Fix: Remove the dead theta branch and field.
  - Plan: Phase 7
  - Status: open
- **B-261** · dead code · fix in the fork · `src/main/java/com/hbm/render/tileentity/RenderBombMultiLarge.java:15-22`
  - RenderBombMultiLarge unbound and references missing model/texture. Loads models/BombGenericLarge.obj and textures/models/BombGenericLargeLayout.png, neither of which exists on disk; the renderer is not registered in ClientProxy, so it is dead but would throw ModelFormatException if ever bound.
  - Evidence: `new ResourceLocation(RefStrings.MODID, "models/BombGenericLarge.obj") / "textures/models/BombGenericLargeLayout.png" (both absent)`
  - Fix: Delete the class.
  - Plan: Phase 7
  - Status: open
- **B-262** · dead code · fix in the fork · `src/main/java/com/hbm/render/tileentity/RenderVaultDoor.java:34-77`
  - Legacy render/tileentity/RenderVaultDoor is unbound and uses 11 missing textures. The old vault-door TESR is never bound in ClientProxy (the live one is render/tileentity/door/RenderVaultDoor) and binds ResourceManager.vault_*/stable_*/vault4_* textures whose files (textures/models/vault_frame.png etc., ResourceManager L890-900) do not exist.
  - Evidence: `bindTexture(ResourceManager.vault_frame_tex); // textures/models/vault_frame.png absent; no ClientRegistry.bind for this class`
  - Fix: Delete the legacy TESR and the 11 ResourceManager texture fields L890-900.
  - Plan: Phase 7
  - Status: open
- **B-263** · dead code · fix in the fork · `src/main/java/com/hbm/render/tileentity/RendererObjTester.java:436-445`
  - Test renderers reference missing textures (ObjTester, MinecartTest). RendererObjTester binds textures/models/ModelCalDualStock/ModelCalBarrel/ModelCalStock.png and RenderMinecartTest.java:26 textures/models/LilBoy2.png; none exist. Both are bound (ClientProxy L211, L758) for the dev-only obj_tester block and entity_minecart_test, so they render magenta if spawned.
  - Evidence: `new ResourceLocation(RefStrings.MODID, "textures/models/ModelCalDualStock.png") (absent)`
  - Fix: Leave as dev scaffolding or delete the test renderers together with TestObjTester/EntityMinecartTest.
  - Plan: Phase 7
  - Status: open
- **B-264** · dead code · needs a decision · `src/main/java/com/hbm/render/world/RenderNTMSkyboxChainloader.java:17-49`
  - RenderNTMSkyboxChainloader/RenderNTMSkyboxImpact are unreferenced. Neither class is referenced anywhere outside render/world; the chainloader's static didLastRender recursion brake (L33) is never installed as an IRenderHandler, so edits there have no effect.
  - Evidence: `grep -rn RenderNTMSkyboxChainloader|RenderNTMSkyboxImpact src/main/java -> only the two files themselves`
  - Fix: Delete both classes or wire the chainloader into the sky provider setup; decide with upstream intent.
  - Plan: decision D24
  - Status: open
### Networking, packets, utilities, wiaj (`net-util`)

- **B-265** · crash · fix in the fork · `src/main/java/com/hbm/packet/toserver/KeybindPacket.java:42`
  - KeybindPacket.Handler indexes EnumKeybind.values() with an unchecked client int. A malformed or malicious client packet with key >= values().length throws ArrayIndexOutOfBoundsException in the server network thread; HbmAnimationPacket L83 has the same pattern but inside a swallowing catch.
  - Evidence: `HbmKeybindsServer.onPressedServer(p, EnumKeybind.values()[m.key], m.pressed);`
  - Fix: Bounds-check m.key (or use EnumUtil.grabEnumSafely) and drop invalid packets.
  - Plan: Phase 2
  - Status: open
- **B-266** · crash · needs a decision · `src/main/java/com/hbm/util/ArmorUtil.java:235-250`
  - ArmorUtil static initializer throws AssertionError on unreflectable fiskheroes. If modid fiskheroes is loaded but WorldHelper/HeroTracker/HeroIteration cannot be resolved (different version), the static block throws AssertionError and ArmorUtil (used by hazmat checks, HUD, oxygen) fails to load, crashing the game.
  - Evidence: `static { if(Loader.isModLoaded("fiskheroes")) { try { ... } catch(Exception e) { throw new AssertionError(); }`
  - Fix: Log and null the handles instead of throwing.
  - Plan: decision D25
  - Status: open
- **B-267** · crash · fix in the fork · `src/main/java/com/hbm/util/BufferUtil.java:104,114`
  - BufferUtil.writeNBT stores the compressed length as a short. Compressed NBT longer than 32767 bytes is written with a wrapped negative/short length; readNBT then reads -1 (empty compound) or a bogus length and the remainder of the packet is misaligned (buffer underflow in the next field). Affects HeldItemNBTPacket and every serialize() that ships NBT.
  - Evidence: `buf.writeShort((short) nbtData.length); ... short nbtLength = buf.readShort();`
  - Fix: Use writeInt/readInt (both sides change together, protocol is per-build).
  - Plan: Phase 2
  - Status: open
- **B-268** · gameplay · fix in the fork · `src/main/java/com/hbm/packet/PermaSyncHandler.java:240-247`
  - PermaSyncHandler CBT section aborts the whole packet on any exception. The trait/station section is wrapped in try/catch whose handler returns early, so a single malformed trait entry leaves satellites, celestial time, riding fix and CBT_War data unread for that tick (intentional because the stream is misaligned, but it hides the root cause).
  - Evidence: `} catch (Exception ex) { MainRegistry.logger.catching(ex); SolarSystemWorldSavedData.updateClientTraits(null); return; }`
  - Fix: Keep; prefer a length-prefixed CBT section so later fields can be skipped safely.
  - Plan: Phase 8
  - Status: open
- **B-269** · gameplay · fix in the fork · `src/main/java/com/hbm/packet/toclient/HbmAnimationPacket.java:88`
  - HbmAnimationPacket and PlayerInformPacket handlers swallow every exception. Both client handlers end with `catch(Exception x) { }` (PlayerInformPacket L87), so decode/logic bugs surface only as missing animations or HUD messages with no log line.
  - Evidence: `} catch(Exception x) { }`
  - Fix: Log via MainRegistry.logger.catching(x) in both catches.
  - Plan: Phase 6
  - Status: open
- **B-270** · gameplay · fix in the fork · `src/main/java/com/hbm/util/BobMathUtil.java:45,55`
  - BobMathUtil.max(float...)/max(double...) seed with MIN_VALUE (smallest positive). Float.MIN_VALUE/Double.MIN_VALUE are tiny positives, not negative infinity, so max() of all-negative inputs returns ~0 instead of the largest input. Current callers (maxPower clamps, |delta| axis pick) pass non-negative values, so the bug is latent.
  - Evidence: `float largest = Float.MIN_VALUE; ... double largest = Double.MIN_VALUE;`
  - Fix: Seed with -Float.MAX_VALUE / -Double.MAX_VALUE (or NEGATIVE_INFINITY).
  - Plan: Phase 6
  - Status: open
- **B-271** · gameplay · fix in the fork · `src/main/java/com/hbm/util/BobMathUtil.java:64`
  - BobMathUtil.safeClamp tests `val == Double.NaN` (always false). NaN is never equal to itself, so the NaN fallback to (min+max)/2 never triggers and NaN propagates out of safeClamp.
  - Evidence: `if(val == Double.NaN) { val = (min + max) / 2D; }`
  - Fix: Use Double.isNaN(val).
  - Plan: Phase 6
  - Status: open
- **B-272** · gameplay · needs a decision · `src/main/java/com/hbm/util/BufferUtil.java:116-117,158`
  - BufferUtil.readNBT returns a new empty compound for the null marker. A -1 marker (null NBT) decodes as `new NBTTagCompound()`, so readItemStack always yields a stack with a non-null tag; client-side stacks decoded this way (and HeldItemNBTPacket L44/62) never compare tag-equal to the server's null-tag stack (areItemStackTagsEqual treats null vs empty as different).
  - Evidence: `if (nbtLength == -1) return new NBTTagCompound(); ... item.stackTagCompound = readNBT(buf);`
  - Fix: Return null for the -1 marker and let callers null-check (audit callers first).
  - Plan: decision D25
  - Status: open
- **B-273** · gameplay · fix in the fork · `src/main/java/com/hbm/util/Tuple.java:236`
  - Tuple.Quintet.equals compares v against other.w. The generated equals compares this.v with other.w instead of other.v, so two Quintets with equal fields are unequal unless v equals w; any Quintet used as a map/set key or in contains() misbehaves.
  - Evidence: `} else if(!v.equals(other.w)) return false;`
  - Fix: Compare against other.v.
  - Plan: Phase 6
  - Status: open
- **B-274** · gameplay · fix in the fork · `src/main/java/com/hbm/wiaj/JarScript.java:146,148`
  - JarScript.ffwTarget/freeRun are static (one fast-forward at a time). Rewind/forward state lives in static fields shared by every JarScript instance; opening a second viewer while one is fast-forwarding corrupts the other's replay. Only one GuiWorldInAJar is open at a time in practice.
  - Evidence: `public static int ffwTarget = 0; ... public static boolean freeRun = false;`
  - Fix: Make the fields instance members.
  - Plan: Phase 6
  - Status: open
- **B-275** · silent misconfig · fix in the fork · `src/main/java/com/hbm/main/NetworkHandler.java:56-57`
  - Unregistered IMessage encodes as discriminator 0 and mis-decodes as TESiren. PrecompilingNetworkCodec.encode does `types.get(msgClass)`; trove's TObjectByteHashMap returns no-entry value 0 for unknown keys, so a packet whose registerMessage line was forgotten is silently sent as discriminator 0 and parsed as TESirenPacket on the receiver (garbage/underflow), never failing.
  - Evidence: `discriminator = types.get(msgClass); outboundBuf.writeByte(discriminator);`
  - Fix: Throw CodecException when !types.containsKey(msgClass).
  - Plan: Phase 6
  - Status: open
- **B-276** · silent misconfig · leave · `src/main/java/com/hbm/util/CompatRecipeRegistry.java:288-292`
  - CompatRecipeRegistry deprecated registerAssembler/registerChemplant are NOPs. Three public addon-facing methods are documented '/** NOP */' and have empty bodies; an addon calling them believes its recipes were registered while nothing happens.
  - Evidence: `/** NOP */ @Deprecated public static void registerAssembler(ItemStack output, AStack[] input, int time) { }`
  - Fix: Log a deprecation warning inside the NOPs.
  - Plan: not planned
  - Status: open
- **B-277** · leak / perf · fix in the fork · `src/main/java/com/hbm/blocks/BlockVolcanoV2.java:91`
  - BufPacket is also sent directly, bypassing networkPackNT's dedupe and threading. BlockVolcanoV2 L91 (range 256) and BlockAtmosphereEditor L154 (range 25) build and send BufPacket via wrapper.sendToAllAround each call instead of networkPackNT, so identical payloads are resent every tick and serialize() runs on the main thread inline without the PrecompiledPacket path.
  - Evidence: `PacketDispatcher.wrapper.sendToAllAround(new BufPacket(xCoord, yCoord, zCoord, this), new TargetPoint(..., 256));`
  - Fix: Route both through TileEntityLoadedBase.networkPackNT or a shared dedupe helper.
  - Plan: Phase 6
  - Status: open
- **B-278** · leak / perf · fix in the fork · `src/main/java/com/hbm/packet/toclient/BufPacket.java:57-67`
  - BufPacket/EntityBufPacket handlers leak the inbound buffer for non-receivers. m.buf (a slice retained from fromBytes) is released in the finally of the instanceof branch only; when the TE/entity is missing or of another type the buffer is never released (EntityBufPacket L49-57 same), producing Netty refcount leak warnings on the client.
  - Evidence: `if (te instanceof IBufPacketReceiver) { try { ... } finally { m.buf.release(); } }`
  - Fix: Move the release into an outer try/finally around the whole handler.
  - Plan: Phase 5
  - Status: open
- **B-279** · dead code · fix in the fork · `src/main/java/com/hbm/main/NetworkHandler.java:168-183`
  - NetworkHandler.sendToAllAround(ByteBuf, TargetPoint) is an unused trap overload. The overload writes a raw ByteBuf to the channel; the codec's encode only accepts ThreadedPacket/IMessage and throws CodecException for anything else, so any future caller gets a runtime failure. No callers exist.
  - Evidence: `public void sendToAllAround(ByteBuf message, NetworkRegistry.TargetPoint point) { ... serverChannel.write(message);`
  - Fix: Remove the overload.
  - Plan: Phase 7
  - Status: open
- **B-280** · dead code · fix in the fork · `src/main/java/com/hbm/packet/GuiLayerPacket.java:38`
  - GuiLayerPacket handler has an unreachable client-side check. The packet is registered with Side.SERVER (PacketDispatcher L71) so its handler can only run on the server, making the getEffectiveSide() == CLIENT early return unreachable; misleading but harmless.
  - Evidence: `if(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) return null;`
  - Fix: Remove the dead check.
  - Plan: Phase 7
  - Status: open
- **B-281** · dead code · leave · `src/main/java/com/hbm/packet/PacketDispatcher.java:76-77`
  - ContainerNBTCommsPacket is registered twice (discriminators 27 and 28). The same Handler/class pair is registered for CLIENT and SERVER with two discriminators; the codec's class->byte map is overwritten so outbound always uses 28 while decode accepts both. Works today, but any refactor of the codec maps or insertion before it breaks the numbering.
  - Evidence: `wrapper.registerMessage(ContainerNBTCommsPacket.Handler.class, ContainerNBTCommsPacket.class, i++, Side.CLIENT); ...(same)..., i++, Side.SERVER);`
  - Fix: Leave; discriminators are positional and persisted in protocol.
  - Plan: not planned
  - Status: open
- **B-282** · dead code · leave · `src/main/java/com/hbm/packet/toserver/AuxButtonPacket.java:29,150`
  - AuxButtonPacket is @Deprecated yet used by 19 GUIs; value 999 spawns a duck. The packet is marked deprecated in favour of NBTControlPacket yet 19 GUIs still send it; its handler has a global easter-egg side effect (te == null && value == 999 spawns a duck) before the TileEntityMachineBase.handleButtonPacket dispatch.
  - Evidence: `@Deprecated //use the NBT control packet instead ... if(te == null && m.value == 999) {`
  - Fix: Leave (discriminator 2 is positional); migrate GUIs gradually.
  - Plan: not planned
  - Status: open
- **B-283** · dead code · needs a decision · `src/main/java/com/hbm/util/BobMathUtil.java:87,263`
  - BobMathUtil.getAngleFrom2DVecs uses cos instead of acos; setPi rewrites Math.PI. getAngleFrom2DVecs applies Math.cos to a cosine value (should be acos) and setPi reflectively overwrites Math.PI; both have zero callers in the tree, so they are dead but dangerous helpers.
  - Evidence: `double result = Math.toDegrees(Math.cos(upper / lower)); ... Field field = ReflectionHelper.findField(Math.class, "PI"); field.setDouble(null, pi);`
  - Fix: Fix acos or delete both helpers.
  - Plan: decision D25
  - Status: open
- **B-284** · dead code · needs a decision · `src/main/java/com/hbm/util/FogMessage.java:1`
  - FogMessage, FauxWorld and TimeAnalyzer are unused dead classes. FogMessage is an IMessage never registered in PacketDispatcher (sending it would hit the discriminator-0 path); com/hbm/wiaj/FauxWorld.java and com/hbm/util/TimeAnalyzer.java have zero references.
  - Evidence: `grep: FogMessage 0 external refs, FauxWorld 0 external refs, TimeAnalyzer 0 external refs`
  - Fix: Delete the three files (upstream-merge friendliness suggests leaving them).
  - Plan: decision D25
  - Status: open
- **B-285** · dead code · leave · `src/main/java/com/hbm/wiaj/actors/ActorFancyPanel.java:130-133,148-151,340`
  - ActorFancyPanel measures scaled-stack elements but never draws them. getElementHeight/getElementWidth handle Object[]{ItemStack, double} entries, but the draw loop only renders String and ItemStack elements and ends with `//TODO: scaled stacks`, so a cannery using scaled stacks reserves space and shows nothing.
  - Evidence: `return (int) Math.ceil(STACK_HEIGHT * (double) scaledStack[1]); ... } //TODO: scaled stacks`
  - Fix: Implement the scaled draw branch or drop the measurement branches.
  - Plan: not planned
  - Status: open
### Resources, localization, build (`resources-build`)

- **B-286** · silent misconfig · needs a decision · `README.md:48-49`
  - README maven coordinates omit the _H261 suffix build.gradle publishes. README shows com.hbm:HBM-NTM:1.0.27_X${ntmBuildNumber}:dev while build.gradle L35 sets version_name = mod_version + "_X" + mod_build_number + "_H261", so the documented dependency string does not match what this build publishes.
  - Evidence: `README: implementation "com.hbm:HBM-NTM:1.0.27_X${ntmBuildNumber}:dev" vs build.gradle:35 ... + "_H261"`
  - Fix: Either drop the _H261 suffix in build.gradle at the next release or document it in README (upstream file; decide).
  - Plan: decision D4
  - Status: open
- **B-287** · silent misconfig · needs a decision · `gradle.properties:19-82`
  - gradle.properties credits has mojibake and punctuation errors. The credits value expanded into mcmod.info contains U+FFFD mojibake 'V?r' (L19), a doubled paren 'Doctor17 (russian localization)),' (L26), missing commas after Maksymisio (L44) and SuperCraftAlex (L73), and lists mikkerlo twice (L71, L82). Shown verbatim in the mod list.
  - Evidence: `L26: \ Doctor17 (russian localization)),\ ; L19 contains U+FFFD`
  - Fix: Fix the five lines (CLAUDE.md says leave gradle.properties alone unless releasing, so batch with a release).
  - Plan: decision D4
  - Status: open
- **B-288** · silent misconfig · fix in the fork · `src/main/java/com/hbm/entity/missile/EntityRideableRocket.java:400`
  - Rocket 'hbm:entity.pipefail' sound has no sounds.json event. playSoundEffect uses hbm:entity.pipefail but sounds.json has no entity.pipefail key, although sounds/entity/pipefail.ogg exists (unreferenced). The rocket-failure sound never plays; Minecraft logs 'Unable to play unknown soundEvent'.
  - Evidence: `worldObj.playSoundEffect(posX, posY, posZ, "hbm:entity.pipefail", 10_000, ...); // no entity.pipefail in sounds.json`
  - Fix: Add "entity.pipefail": {"category": "block", "sounds": ["entity/pipefail"]} to sounds.json.
  - Plan: Phase 7
  - Status: open
- **B-289** · silent misconfig · fix in the fork · `src/main/java/com/hbm/entity/mob/EntityHunterChopper.java:181`
  - EntityHunterChopper plays unknown sound 'hbm:weapon.osiprShoot'. sounds.json has no weapon.osiprShoot event, so the hunter chopper's attack sound is silent and logs 'Unable to play unknown soundEvent' every other attack tick.
  - Evidence: `worldObj.playSoundAtEntity(this, "hbm:weapon.osiprShoot", 10.0F, 1.0F); // key absent`
  - Fix: Add the event to sounds.json or use an existing gun sound id.
  - Plan: Phase 7
  - Status: open
- **B-290** · silent misconfig · fix in the fork · `src/main/java/com/hbm/items/armor/ItemModSensor.java:70`
  - ItemModSensor plays unknown sound 'hbm:weapon.follyAquired'. No weapon.follyAquired (or follyAcquired) event exists in sounds.json, so the sensor's explosive-detected cue never plays.
  - Evidence: `entity.worldObj.playSoundAtEntity(entity, "hbm:weapon.follyAquired", 0.5F, 1.0F); // key absent from sounds.json`
  - Fix: Add a sounds.json event (with an OGG) or switch to an existing alert sound such as hbm:alarm.* .
  - Plan: Phase 7
  - Status: open
- **B-291** · silent misconfig · fix in the fork · `src/main/java/com/hbm/items/food/ItemLemon.java:114`
  - ItemLemon plays 'hbm:entity.vomit' but the event is player.vomit. The lemon's vomit sound id hbm:entity.vomit does not exist in sounds.json (the event is registered as player.vomit), so the sound is silent with an 'unknown soundEvent' log.
  - Evidence: `world.playSoundEffect(player.posX, player.posY, player.posZ, "hbm:entity.vomit", 1.0F, 1.0F); // sounds.json key is player.vomit`
  - Fix: Change the literal to "hbm:player.vomit".
  - Plan: Phase 7
  - Status: open
- **B-292** · silent misconfig · fix in the fork · `src/main/resources/assets/hbm/lang/en_US.lang:1709-5422`
  - en_US.lang has 23 duplicate keys, two with conflicting values. 23 keys are defined twice (last wins): hbmfluid.polythylene L1709 'Polythylene' vs L1762 'Polyethylene'; item.missile.fuel.solid L3697 'Solid Fuel' vs L5418 'Solid Fuel (pre-fueled)' (block repeated L5418-5422); 21 identical repeats (commands.station.* L681-695, item.*_cn989 L3320-3376, ...).
  - Evidence: `L3697 item.missile.fuel.solid=Solid Fuel / L5418 item.missile.fuel.solid=Solid Fuel (pre-fueled)`
  - Fix: Delete the second copies (L127, L689-695, L1559, L1644, L1709, L2790, L3372-3376, L4441, L5418-5422); keep 'Polyethylene'.
  - Plan: Phase 7
  - Status: open
- **B-293** · silent misconfig · needs a decision · `src/main/resources/assets/hbm/lang/it_IT.lang`
  - it_IT/zh_CN/ru_RU lang files carry duplicate keys. Duplicate keys (last occurrence silently wins): it_IT 213, zh_CN 114, ru_RU 9 (de_DE/pl_PL/uk_UA/fr_FR none). Conflicting duplicates in a translation are invisible to testers using en_US.
  - Evidence: `python key count: it_IT dups 213, zh_CN dups 114, ru_RU dups 9`
  - Fix: Dedupe with a script keeping the last value per key when mirroring en_US changes; low priority for translations the fork does not maintain.
  - Plan: decision D26
  - Status: open
- **B-294** · silent misconfig · fix in the fork · `src/main/resources/assets/hbm/manual/concepts/fluidhandling.json`
  - QMAW page fluidhandling.json never loads (no trigger, empty content). The only manual page without a 'trigger' key; QMAWLoader.registerJson L208 calls json.get("trigger").getAsJsonArray() unconditionally, NPEs, is caught and logged '[QMAW] Error reading manual concepts/fluidhandling.json', and the page is never registered. Its en_US content is an empty string.
  - Evidence: `QMAWLoader.java:208 JsonArray triggers = json.get("trigger").getAsJsonArray(); // file has no trigger key`
  - Fix: Either delete the stub page or add "trigger": [] and real content; optionally null-guard trigger in registerJson.
  - Plan: Phase 7
  - Status: open
- **B-295** · silent misconfig · fix in the fork · `src/main/resources/assets/hbm/manual/satellite/detector.json`
  - Satellite manual pages trigger on non-existent hbm:item.satellite. detector.json and rayscanner.json use trigger/icon ["hbm:item.satellite", 1, 9|10]; ModItems.satellite is commented out (ModItems.java:1560), so SerializableRecipe.readItemStack returns ModItems.nothing, QMAW logs 'references nonexistant trigger' and neither page can be opened from an item.
  - Evidence: `"trigger": [["hbm:item.satellite", 1, 9]] vs ModItems.java:1560 // public static Item satellite;`
  - Fix: Retarget both pages to the current sat_* items (e.g. hbm:item.sat_scanner / a new detector item) or delete the two legacy pages.
  - Plan: Phase 7
  - Status: open
- **B-296** · silent misconfig · fix in the fork · `src/main/resources/assets/hbm/sounds.json:248`
  - sounds.json weapon.grenadeBounce lists missing grenadeBounce2.ogg. The event lists weapon/grenadeBounce1..3 but only grenadeBounce1.ogg and grenadeBounce3.ogg exist (gBounce1-3.ogg exist separately), so one in three grenade bounces (EntityGrenadeUniversal.java:137) is silent with a missing-resource warning.
  - Evidence: `"weapon.grenadeBounce": {... "sounds": ["weapon/grenadeBounce1", "weapon/grenadeBounce2", "weapon/grenadeBounce3"]}; ls shows no grenadeBounce2.ogg`
  - Fix: Drop the grenadeBounce2 entry or add the file (e.g. copy gBounce2.ogg).
  - Plan: Phase 7
  - Status: open
- **B-297** · dead code · fix in the fork · `src/main/java/com/hbm/main/StructureManager.java:165`
  - StructureManager.excavator references missing structures/excavator.nbt. The field loads structures/excavator.nbt which is not on disk, so NBTStructure logs 'NBT Structure not found: structures/excavator.nbt' (NBTStructure.java:115) at class init on both sides and yields an empty structure; nothing references StructureManager.excavator.
  - Evidence: `public static final NBTStructure excavator = new NBTStructure(new ResourceLocation(RefStrings.MODID, "structures/excavator.nbt"));`
  - Fix: Remove the field (or restore the NBT if the excavator ruin is wanted).
  - Plan: Phase 7
  - Status: open
- **B-298** · dead code · fix in the fork · `src/main/resources/assets/hbm/lang/en_US.lang:5577`
  - Lang key soundCategory.ntmMachines has no implementation. soundCategory.ntmMachines=NTM Machines is defined but no Java code creates or uses such a SoundCategory, so the entry is dead.
  - Evidence: `soundCategory.ntmMachines=NTM Machines (grep ntmMachines in src/main/java: no hits)`
  - Fix: Remove the key or implement the custom category.
  - Plan: Phase 7
  - Status: open
- **B-299** · dead code · needs a decision · `src/main/resources/assets/hbm/models/missileDoomsday.obj`
  - 35 OBJ models are not referenced by any Java path. 35 of 579 OBJ files have no literal reference (e.g. models/machines/zpe.obj, models/weapons/chodeblaster.obj, models/turbofan_*.obj, models/missile_parts/mp_*.obj, models/mobs/scuttercrab.obj); shipped as dead payload unless built by string concatenation (none found).
  - Evidence: `python scan: obj on disk 579, unreferenced 35`
  - Fix: Review the list and delete confirmed leftovers; keep missile parts if a loader builds paths dynamically.
  - Plan: decision D26
  - Status: open
- **B-300** · dead code · fix in the fork · `src/main/resources/assets/hbm/models/weapons/.obj`
  - Corrupt, nameless models/weapons/.obj ships in the jar. A 214 KB OBJ whose basename is empty; 1587 vertex lines are corrupted ('v -+.1875++ +.5625++ 6.5625++') so it could not parse even if referenced, and nothing references it.
  - Evidence: `v -+.1875++ +.5625++ 6.5625++ (grep -c '^v [-+]*+' = 1587)`
  - Fix: Delete the file.
  - Plan: Phase 7
  - Status: open
- **B-301** · dead code · fix in the fork · `src/main/resources/assets/hbm/my_hecking_realism.png`
  - Unreferenced images my_hecking_realism.png and textures/ABC123.png. assets/hbm/my_hecking_realism.png (452 KB) and assets/hbm/textures/ABC123.png are shipped but no source or resource references either name.
  - Evidence: `grep over src/main for 'my_hecking_realism' / 'ABC123': no references`
  - Fix: Delete both files.
  - Plan: Phase 7
  - Status: open
- **B-302** · dead code · fix in the fork · `src/main/resources/assets/hbm/shaders/supernovae.frag`
  - Shaders blackholed.frag and supernovae.frag are unreferenced. Of the 11 fragment shaders only these two have no Java reference (SkyProviderCelestial, SolarSystem, GUIMachineStardar, GUIScreenSatSettings load the rest), so they are dead assets.
  - Evidence: `shaders unreferenced: ['shaders/blackholed.frag', 'shaders/supernovae.frag']`
  - Fix: Delete or wire into a sky provider.
  - Plan: Phase 7
  - Status: open
- **B-303** · dead code · fix in the fork · `src/main/resources/assets/hbm/sounds.json:393-395`
  - sounds.json fm.clap/fm.mug/fm.sample point at non-existent root files. The three fm.* events resolve to sounds/clap.ogg, mug.ogg, sample.ogg which do not exist; no Java code references hbm:fm.*, so they are dead entries that only produce warnings if ever played.
  - Evidence: `"fm.clap":  {"category": "block", "sounds": [{"name": "clap", "stream": false}]}, (sounds/clap.ogg absent)`
  - Fix: Delete the three entries.
  - Plan: Phase 7
  - Status: open
- **B-304** · dead code · needs a decision · `src/main/resources/assets/hbm/sounds/misc/desktop.ini`
  - Stray non-OGG and 32 unreferenced OGG files ship in sounds/. sounds/misc/desktop.ini, sounds/misc/htrstop.wav and sounds/block/turbofanOperate.mp3 (an .ogg twin exists) are shipped, plus 32 OGGs no sounds.json event references (root crash.ogg, alarm/vox*, mining/Morkite*, turret/maxwellLoop, weapon/chainsaw_loop, entity/pipefail ...).
  - Evidence: `non-ogg files: misc/desktop.ini, misc/htrstop.wav, block/turbofanOperate.mp3; unreferenced ogg count 32`
  - Fix: Delete the three stray files; review the 32 OGGs (pipefail should get an event, the rest can go).
  - Plan: decision D26
  - Status: open
- **B-305** · dead code · fix in the fork · `src/main/resources/assets/hbm/structures/crane.nbt`
  - Seven unreferenced structure NBTs ship in the jar. crane.nbt (superseded by crane_mod.nbt) and test-rot/test-jigsaw/test-jigsaw-core/test-jigsaw-hall/test-tandem/test-tandem-core.nbt are on disk but their registrations are commented out (StructureManager L170-175) or absent, so they are dead payload.
  - Evidence: `StructureManager.java:170-175 // public static final NBTStructure test_* ...; crane.nbt has no reference`
  - Fix: Delete crane.nbt; keep or delete the test-* files together with the commented registrations.
  - Plan: Phase 7
  - Status: open
### OpenComputers integration (`oc`)

- **B-306** · crash · fix in the fork · `src/main/java/com/hbm/handler/CompatHandler.java:196-223`
  - CompatHandler.init adds a recipe for a floppy whose item may be null. If the disk asset folder is missing, fromClass returns null, disk.item stays null, yet addShapelessAuto(disks.get("PWRangler").item, ...) at L223 still runs (NPE at PostInit). The early return at L196 when disks is empty also skips the closing logs. Latent: the asset ships today.
  - Evidence: `addShapelessAuto(disks.get("PWRangler").item, "oc:floppy", new ItemStack(ModBlocks.pwr_casing));`
  - Fix: Guard the recipe with if(disk.item != null) inside the forEach, and drop the early return.
  - Plan: Phase 2
  - Status: open
- **B-307** · crash · fix in the fork · `src/main/java/com/hbm/tileentity/machine/rbmk/TileEntityRBMKControlManual.java:161-162`
  - RBMKControlManual.getColor NPEs when no colour is set. this.color.ordinal() is dereferenced without a null check, while the NBT path explicitly loads color = null (L101) for an unassigned rod, so rbmk_control_rod.getColor() throws NullPointerException into Lua on any uncoloured manual control rod.
  - Evidence: `return new Object[] {this.color.ordinal()};`
  - Fix: return new Object[] {this.color == null ? -1 : this.color.ordinal()};
  - Plan: Phase 1
  - Status: open
- **B-308** · crash · fix in the fork · `src/main/java/com/hbm/tileentity/turret/TileEntityTurretBaseArtillery.java:86-88`
  - TurretBaseArtillery.getCurrentTarget throws when target queue is empty. targetQueue.get(0) is called without an isEmpty check, so with no queued target the callback throws IndexOutOfBoundsException, surfacing as a Lua component error instead of a nil result.
  - Evidence: `return new Object[] {targetQueue.get(0).xCoord, targetQueue.get(0).yCoord, targetQueue.get(0).zCoord};`
  - Fix: if(targetQueue.isEmpty()) return new Object[] {null, "no target"}; before the access.
  - Plan: Phase 2
  - Status: open
- **B-309** · gameplay · needs a decision · `src/main/java/com/hbm/tileentity/bomb/TileEntityLaunchPadBase.java:522-545`
  - methods()-listed callbacks run as direct PeripheralCallbacks, ignoring @Callback. OC Callbacks.dynamicAnalyze registers every methods() name as a PeripheralCallback before adding unlisted @Callback methods (Callbacks.scala:44,62-65), so in the 32 classes with methods() direct/limit/doc are ignored: launch (here, LaunchTable:689-712), setCoords, addCoords run on OC worker threads.
  - Evidence: `@Callback public Object[] launch(...) at L522-524 is also listed in methods() at L539+ -> dispatched via invoke() as PeripheralCallback`
  - Fix: Keep world-mutating callbacks OUT of methods() (reflection still finds them on direct wiring) or make invoke() defer mutations to the server tick via a queue.
  - Plan: Phase 4 after D27
  - Status: open
- **B-310** · gameplay · fix in the fork · `src/main/java/com/hbm/tileentity/machine/TileEntityMachineLargeTurbine.java:316-339`
  - LargeTurbine methods() lists getPower but invoke() has no case. "getPower" is in methods() (L321) but the invoke() switch only handles getFluid/getType/setType/getInfo, so component.ntm_turbine.getPower() on a large turbine throws NoSuchMethodException on every wiring path (the listed name overrides the reflected @Callback).
  - Evidence: `methods(): "getPower", ... invoke(): case ("getFluid") ... case ("getInfo") ... throw new NoSuchMethodException();`
  - Fix: Add case ("getPower"): return getPower(context, args); to invoke().
  - Plan: Phase 4
  - Status: open
- **B-311** · gameplay · needs a decision · `src/main/java/com/hbm/tileentity/machine/TileEntityMicrowave.java:241-258`
  - TileEntityMicrowave ships OC test callbacks test/variableget/variableset. Placeholder callbacks are live on every microwave: test() returns a test string, variableget/variableset (getter/setter=true) expose and mutate 'speed' with 'test of the getter callback function' strings.
  - Evidence: `return new Object[] {"This is a testing device for everything OC."};`
  - Fix: Delete the three methods or rename them into a documented API (getSpeed/setSpeed).
  - Plan: Phase 4 after D27
  - Status: open
- **B-312** · gameplay · fix in the fork · `src/main/java/com/hbm/tileentity/machine/TileEntityReactorResearch.java:487-517`
  - ReactorResearch setLevel callback missing from methods()/invoke(). setLevel (L517, @Callback limit=4) is not listed in methods() (L487-495) nor handled in invoke(), so through TileEntityProxyCombo ports the research reactor cannot be controlled from OC; only direct cable contact with the core reaches it via reflection. Upstream TODO 'fix reactor control' at L51.
  - Evidence: `methods() returns {getTemp,getLevel,getTargetLevel,getFlux,getInfo}; public Object[] setLevel(Context context, Arguments args) unlisted`
  - Fix: Add "setLevel" to methods() and a case in invoke().
  - Plan: Phase 1
  - Status: open
- **B-313** · gameplay · fix in the fork · `src/main/java/com/hbm/tileentity/machine/TileEntityWatz.java:626-651`
  - TileEntityWatz methods() lists bogus 'getComponentName'. methods() lists "getComponentName" which is not a @Callback and has no invoke() case, so it appears in component.methods() but calling it throws NoSuchMethodException.
  - Evidence: `return new String[] { "getComponentName", "getHeat", ... }; invoke() has no case for it`
  - Fix: Remove "getComponentName" from the array.
  - Plan: Phase 4
  - Status: open
- **B-314** · gameplay · fix in the fork · `src/main/java/com/hbm/tileentity/network/TileEntityRadioTelex.java:300-314`
  - RadioTelex.setSendingText errors when fewer than five arguments are passed. The loop probes args.checkAny(i) == null for i in 0..4, but OC ArgumentsImpl.checkAny calls checkIndex, which throws 'bad arguments #N (value expected, got no value)' for missing arguments; only an explicit nil yields null. setSendingText("a") errors instead of blanking lines 2-5 as intended.
  - Evidence: `if(args.checkAny(i) == null || args.checkString(i).isEmpty()) { // 'check if it was never given'`
  - Fix: Use if(i >= args.count() || !args.isString(i) || args.checkString(i).isEmpty()) or args.optString(i, "").
  - Plan: Phase 4
  - Status: open
- **B-315** · gameplay · fix in the fork · `src/main/java/com/hbm/tileentity/turret/TileEntityTurretArty.java:472-490`
  - TileEntityTurretArty.methods() omits inherited getPos. The re-listed array has 14 names but drops TurretBaseNT's getPos (TileEntityTurretBaseNT.java:1001), so getPos is unreachable through ProxyCombo ports on artillery (reflection still finds it on direct wiring).
  - Evidence: `methods() { // :vomit: ... "isAligned", "getCurrentTarget", "getTargetDistance", "addCoords" }; (no "getPos")`
  - Fix: Add "getPos" to methods() (invoke inherits the base switch or needs a case).
  - Plan: Phase 4
  - Status: open
- **B-316** · gameplay · fix in the fork · `src/main/java/com/hbm/tileentity/turret/TileEntityTurretBaseArtillery.java:86-93`
  - TurretBaseArtillery/HIMARS callbacks unreachable through ports. TileEntityTurretBaseArtillery adds getCurrentTarget/getTargetDistance and TileEntityTurretHIMARS:380 adds addCoords without overriding methods()/invoke(); they inherit TurretBaseNT's list, so via ProxyCombo ports HIMARS cannot be targeted from OC (only TurretArty re-lists them).
  - Evidence: `class has @Callback getCurrentTarget/getTargetDistance but no String[] methods() override; HIMARS likewise for addCoords`
  - Fix: Move the methods()/invoke() re-listing from TurretArty into TileEntityTurretBaseArtillery and add addCoords in HIMARS.
  - Plan: Phase 4
  - Status: open
- **B-317** · gameplay · fix in the fork · `src/main/resources/assets/hbm/disks/pwrangler/usr/bin/PWRangler.lua:207-256`
  - PWRangler.lua uses Lua 5.3 '//' and cannot run on Lua 5.2 / OC-LuaJIT. Floor division '//' at L207, 213, 217, 252, 256 is a syntax error on OC's Lua 5.2 architecture and on the fork's OC-LuaJIT (5.2-class, no '//'), so the shipped floppy fails to load there; the disks README even says 'Preferably 5.3'.
  - Evidence: `coreHeat = coreHeat // (const.coreHeatCapacity / 10)`
  - Fix: Replace each a // b with math.floor(a / b) (5 sites) and note 5.2-safety in disks/README.md.
  - Plan: Phase 4
  - Status: open
- **B-318** · silent misconfig · fix in the fork · `src/main/java/com/hbm/blocks/network/BlockOpenComputersCablePaintable.java:189-191`
  - OC cable @Optional.Interface names the wrong Colored interface. The annotation strips li.cil.oc.api.network.Colored but the TE implements li.cil.oc.api.internal.Colored (import L38), so the strip target never matches. Harmless only because the class is loaded solely behind Loader.isModLoaded guards (ModBlocks, TileMappings:563).
  - Evidence: `@Optional.Interface(iface = "li.cil.oc.api.network.Colored", modid = "OpenComputers") ... implements Environment, SidedEnvironment, Colored`
  - Fix: Change the iface string to "li.cil.oc.api.internal.Colored".
  - Plan: Phase 4
  - Status: open
- **B-319** · silent misconfig · needs a decision · `src/main/java/com/hbm/tileentity/TileEntityProxyCombo.java:34-35`
  - TileEntityProxyCombo @Optional.Interface uses lowercase modid 'opencomputers'. FML Loader.isModLoaded is an exact-match containsKey and OC's id is 'OpenComputers', so ModAPITransformer strips OCComponent and SimpleComponent from every multiblock port even with OC installed (methods()/invoke()/canConnectNode invisible to OC). Same upstream; confirm in-game.
  - Evidence: `@Optional.Interface(iface = "com.hbm.handler.CompatHandler.OCComponent", modid = "opencomputers")`
  - Fix: Change both entries to modid = "OpenComputers" (safe either way); test a ported multiblock from OC before and after.
  - Plan: Phase 1 after D27
  - Status: open
- **B-320** · silent misconfig · needs a decision · `src/main/java/com/hbm/tileentity/TileEntityProxyCombo.java:502-511`
  - ProxyCombo reports cached 'ntm_null' name before world load. OC asks getComponentName() before worldObj is set; the proxy answers from the NBT-cached componentName, which is 'ntm_null' for a never-connected port. Whether OC re-reads the name later (so a freshly placed port registers correctly) is not verifiable here.
  - Evidence: `if(this.worldObj == null) // OC is going too fast, grab from NBT! return componentName;`
  - Fix: Confirm in-game that a newly placed port shows the core's name; if not, re-create the node in onLoad/first tick.
  - Plan: Phase 4 after D27
  - Status: open
- **B-321** · silent misconfig · fix in the fork · `src/main/java/com/hbm/tileentity/bomb/TileEntityLaunchTable.java:53`
  - TileEntityLaunchTable implements OCComponent without direct SimpleComponent. OC's ClassTransformer injects the component node only when the class's own interfaces list contains li/cil/oc/api/network/SimpleComponent (ClassTransformer.scala:136); inherited via OCComponent does not count. The launch table has no ProxyCombo ports, so ntm_custom_launch_pad is never a component.
  - Evidence: `... IRadarCommandReceiver, CompatHandler.OCComponent { (no SimpleComponent in implements; superclass TileEntityLoadedBase has none)`
  - Fix: Add SimpleComponent to the implements list (as every other component does).
  - Plan: Phase 4
  - Status: open
- **B-322** · silent misconfig · needs a decision · `src/main/java/com/hbm/tileentity/machine/TileEntityMachineIndustrialTurbine.java:260`
  - Component names reused with divergent APIs (ntm_turbine, ntm_energy_storage). ntm_turbine is returned by MachineTurbine:364, LargeTurbine:260, Chungus:292 and IndustrialTurbine:260 (no setType, extra getFlywheel); ntm_energy_storage getEnergyInfo is {power,max} in BatteryBase:210, {power,delta} in REDD:229, {power,max,delta} in Socket:395: scripts get different shapes.
  - Evidence: `return "ntm_turbine"; (4 classes) / REDD: return new Object[] {this.power.doubleValue(), this.delta.longValue()};`
  - Fix: Give each class a distinct name or make getEnergyInfo/getType shapes identical across subclasses; document the differences at minimum.
  - Plan: Phase 4 after D27
  - Status: open
- **B-323** · silent misconfig · fix in the fork · `src/main/java/com/hbm/tileentity/machine/TileEntityWatz.java:55`
  - TileEntityWatz implements OCComponent without direct SimpleComponent. Same ASM-injection miss as the launch table: the Watz core itself gets no OC node, so watz_reactor is only reachable through its TileEntityProxyCombo ports (which are themselves affected by the lowercase modid stripping).
  - Evidence: `... IFluidCopiable, CompatHandler.OCComponent, IRORValueProvider { (no SimpleComponent)`
  - Fix: Add SimpleComponent to the implements list.
  - Plan: Phase 4
  - Status: open
- **B-324** · silent misconfig · fix in the fork · `src/main/java/com/hbm/tileentity/machine/rbmk/TileEntityCraneConsole.java:455-470`
  - CraneConsole getDepletion/getXenonPoison return the string 'N/A'. With no rod loaded both callbacks return {"N/A"} instead of nil + message, so a Lua script doing arithmetic on the result errors with a type error rather than a clear nil.
  - Evidence: `return new Object[] {"N/A"};`
  - Fix: Return new Object[] {null, "no rod loaded"}.
  - Plan: Phase 4
  - Status: open
- **B-325** · silent misconfig · fix in the fork · `src/main/java/com/hbm/tileentity/machine/storage/TileEntityBatteryBase.java:222-233`
  - setModeLow/High narrow Lua numbers to short silently. (short) args.checkInteger(0) wraps out-of-range values (65536 -> 0) before the mode validation, so an absurd argument can pass as a valid mode instead of being rejected.
  - Evidence: `short newMode = (short) args.checkInteger(0);`
  - Fix: Validate the int range before casting.
  - Plan: Phase 4
  - Status: open
- **B-326** · silent misconfig · fix in the fork · `src/main/java/com/hbm/tileentity/machine/storage/TileEntityMachineFluidTank.java:58`
  - Six TEs use lowercase modid 'opencomputers' in @Optional.Interface. TileEntityMachineFluidTank:58, MachineBattery:41, BatteryBase:35, Barrel:54, MachineCoker:38, RBMKHeater:32 list SimpleComponent under the wrong-case modid; it is stripped even with OC present, but OCComponent still supplies SimpleComponent, so harmless today.
  - Evidence: `@Optional.InterfaceList({@Optional.Interface(iface = "li.cil.oc.api.network.SimpleComponent", modid = "opencomputers")})`
  - Fix: Normalise all six to "OpenComputers".
  - Plan: Phase 4
  - Status: open
- **B-327** · silent misconfig · fix in the fork · `src/main/java/com/hbm/tileentity/network/TileEntityRadioTorchBase.java:122`
  - Radio torch OC doc string misspells setChannel as 'setChannle'. The @Callback doc shown by OC's component documentation reads setChannle(channel: string), misleading users about the real method name.
  - Evidence: `@Callback(direct = true, limit = 4, doc = "setChannle(channel: string) -- Set the channel ...")`
  - Fix: Fix the doc string to setChannel.
  - Plan: Phase 4
  - Status: open
- **B-328** · silent misconfig · needs a decision · `src/main/java/com/hbm/tileentity/network/TileEntityRadioTorchBase.java:146-151`
  - setCustomMapValues looks up Integer keys in an OC table map. values.containsKey(i) autoboxes int to Integer; OC marshals Lua numbers to Double, so if checkTable yields an eagerly converted map the lookup silently never matches and mappings are never set. Depends on OC's JNLua table conversion (live proxy vs copied map); not verifiable without runtime.
  - Evidence: `Map values = args.checkTable(0); for (int i = 1; i <= 16; i++){ if (values.containsKey(i) && values.get(i) instanceof String){`
  - Fix: Iterate values.entrySet() and coerce keys via ((Number) key).intValue(), which works for both representations.
  - Plan: Phase 4 after D27
  - Status: open
- **B-329** · leak / perf · needs a decision · `src/main/java/com/hbm/handler/CompatHandler.java:249-252`
  - OCComponent extends ManagedPeripheral, disabling OC's callback cache. Because every HBM component is a ManagedPeripheral, OC's Callbacks.apply (Callbacks.scala:24) bypasses its per-class cache and re-runs reflective analysis of the whole class hierarchy on every component call, even for the 37 classes that never override methods().
  - Evidence: `public interface OCComponent extends SimpleComponent, SidedComponent, ManagedPeripheral {`
  - Fix: Split OCComponent into a plain SimpleComponent variant for reflection-only classes and keep ManagedPeripheral only where ports need forwarding.
  - Plan: Phase 4 after D27
  - Status: open
- **B-330** · dead code · fix in the fork · `src/main/java/com/hbm/tileentity/machine/rbmk/TileEntityCraneConsole.java:400-412`
  - worldObj.isRemote checks inside OC callbacks are dead code. CraneConsole.move guards posFront/posLeft updates with if(!worldObj.isRemote), but OC only invokes components server-side, so the checks are always true and merely obscure the intent.
  - Evidence: `if(!worldObj.isRemote) posFront += speed;`
  - Fix: Remove the checks.
  - Plan: Phase 7
  - Status: open
## Reviewed and rejected

Claims from the survey that did not survive re-verification. Do not re-open without new evidence.

- `core` `src/main/java/com/hbm/commands/CommandTotalTime.java:75`: CommandTotalTime.addTime doubles the base time. incrementTotalWorldTime is a setter in 1.7.10 WorldInfo, so add semantics are correct
- `core` `src/main/java/com/hbm/main/ModEventHandler.java:825-831`: WorldEvent.Load re-runs BobmazonOfferFactory.init and the provider override. both calls are idempotent (lists cleared, provider unregistered before re-register)
- `api` `src/main/java/api/hbm/redstoneoverradio/IRORInfo.java:3-5`: IRORInfo PREFIX_*/separator constants are non-final and reassignable. interface fields are implicitly final
- `te-special` `src/main/java/com/hbm/handler/neutron/NeutronHandler.java:33-47`: RBMKNeutronHandler statics overwritten per world every tick (multi-world race). statics are set per world right before use and not read elsewhere
- `te-special` `src/main/java/com/hbm/tileentity/machine/rbmk/RBMKDials.java:59-69`: RBMKDials.createDials refreshes the cache before writing default gamerules. cached values equal the defaults on a new world
- `te-special` `src/main/java/com/hbm/tileentity/turret/TileEntityTurretArty.java:325-329`: Arty updateEntity copy decrements casingDelay regardless of usesCasings(). intentional; behaviour equals the base path once cachedCasingConfig is set, no casing spam
- `networks` `src/main/java/com/hbm/handler/threading/PacketThreading.java:32`: /ntmpackets forceLock deadlocks the server tick. ReentrantLock re-enters on the owning server thread; forceLock only starves the worker
- `networks` `src/main/java/com/hbm/tileentity/network/TileEntityPipeBaseNT.java:121-131`: TileEntityPipeBaseNT shadows TileEntityLoadedBase.isLoaded. the subclass overrides isLoaded() consistently; redundant but correct
- `items` `src/main/java/com/hbm/items/ModItems.java:6009`: Survey claim: rbmk_fuel_bk247 unregistered yet feeds craftableRods. registerItem line exists at ModItems.java:6009 (double spaces defeated the survey regex)
- `weapons` `src/main/java/com/hbm/items/weapon/sedna/BulletConfig.java:160-167`: BulletConfig.getDamage gives no flags for PLASMA/MICROWAVE/OTHER. deliberate incomplete switch; missing classes behave like ELECTRIC/LASER
- `weapons` `src/main/java/com/hbm/render/entity/projectile/RenderBulletMK4.java:28`: Survey claim: flamer BulletConfigs get no renderer. null renderer is handled and particles are the intended visual
- `gui` `src/main/java/com/hbm/inventory/container/ContainerRadiobox.java:1`: Survey claim: OrbitalStationComputer/Radiobox containers crash on shift-click. zero slots, path unreachable
- `entities` `src/main/java/com/hbm/entity/cart/EntityMinecartCrate.java:102-104`: EntityMinecartCrate.getCartItem hardcodes EnumCartBase.VANILLA. ItemModMinecart.EnumMinecart.CRATE (L48) only allows EnumCartBase.VANILLA, so getBase() can never differ; the hardcode is equivalent today
- `hazards` `src/main/java/com/hbm/handler/neutron/NeutronHandler.java:34-42`: RBMKNeutronHandler static dial cache races across worlds. the statics are set immediately before that world's runStreamInteractions in the same loop iteration on the server thread and no code outside NeutronHandler/RBMKNeutronHandler reads them
- `hazards` `src/main/java/com/hbm/handler/radiation/ChunkRadiationHandlerPRISM.java:176`: PRISM newAdditions is a static map shared across worlds. putAll/clear happen inside the per-world loop (L183-254) right after that world's spread, single-threaded, so no cross-world mixing occurs
- `render` `src/main/java/com/hbm/main/ClientProxy.java:2188`: AudioDynamic default 10-block range when updateRange forgotten. range is applied by all three proxy overloads; the dropped argument is pitch (separate item)
- `render` `src/main/java/com/hbm/packet/toclient/AuxParticlePacketNT.java:22-23`: Particle creator packets spawn at (0,0,0) when posX omitted. AuxParticlePacketNT(nbt,x,y,z) sets posX/posY/posZ for every IParticleCreator.sendPacket call
- `render` `src/main/java/com/hbm/tileentity/DoorDecl.java:110-111`: DoorDecl.getSEDNARenderer returns client renderers without @SideOnly. interface return type avoids verifier class loading and no server code path calls it
- `net-util` `src/main/java/com/hbm/packet/toclient/BufPacket.java:62`: BufPacket.Handler NPEs on te.getBlockType() in its catch block. te is non-null there and getBlockType() cannot return null for a world-attached tile
- `net-util` `src/main/java/com/hbm/wiaj/cannery/Jars.java:27-28`: Jars.initJars registers CanneryWillow twice. distinct keys for two item forms of the same plant
- `resources-build` `tools/export-json-animation-2_79.py:279-285`: Blender 2.79 exporter will not load on Blender 2.80+. script contains 2.80+ menu guards; only deprecated property syntax, untested here
- `oc` `src/main/java/com/hbm/tileentity/TileEntityProxyCombo.java:516-535`: ProxyCombo.canConnectNode lacks the ntm_null guard. the ntm_null guard is applied to the core's name on both paths
