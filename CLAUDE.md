# CLAUDE.md

Operational notes for engineering sessions on this repository.
Deep reference: `docs/codebase-map.md`. Fork history and merge procedure: `docs/fork-notes.md`.

## What this repo is

- `github.com/mindbound/Hbm-s-Nuclear-Tech-DBS`: the owner's personal fork ("DBS") of NTM: Space. Owner: Arets Paeglis (GitHub `astronfo` / `mindbound`).
- Lineage: `HbmMods/Hbm-s-Nuclear-Tech-GIT` master (original NTM, Minecraft 1.7.10) -> `JameH2/Hbm-s-Nuclear-Tech-GIT` branch `space-travel-twopointfive` ("NTM: Space", JamesH_2 and MellowArpeggiation/George Paton) -> this fork.
- Mod identity: modid `hbm`, name "NTM: Space", `mod_version=1.0.27`, `mod_build_number=5778`, version string `1.0.27_X5778_H261`, `RefStrings.VERSION` = "1.0.27 BETA (5778)".
- Main development branch: `space-travel-twopointfive`. HEAD is a clean merge of JameH2's tip plus the fork's own commits, which restore deprecated content (research/breeding reactors, BAT9000 recipe) through the fork-owned package `src/main/java/com/hbm/dbs/`. Hashes, restore details and the merge procedure are owned by `docs/fork-notes.md`.
- Git remotes in the cloud clone: `origin` (fork), `upstream` (JameH2), `hbm` (HbmMods). The cloud clone is shallow (24 boundary commits), so `git log -S` and `--diff-filter` can miss old history there; the owner's local clone has full history.
- Owner's local checkout (Windows, JDK 8 and JDK 21 installed): `C:\Users\astro\Downloads\Hbm-s-Nuclear-Tech-DBS`. It is the only place the mod gets compiled and tested. Cloud sessions work on `claude/...` staging branches.
- Sibling repo: `/home/user/OC-LuaJIT` (`github.com/mindbound/OC-LuaJIT`), a LuaJIT CPU architecture addon for GTNH OpenComputers. It touches this mod only at OC's component-call boundary.
- Size: 3889 `.java` files (~530k LOC); 6790 png, 591 ogg, 580 obj, 201 json, 130 nbt, 10 lang files.

## Build and test

- Toolchain: JDK 8 only. Gradle wrapper 4.4.1, ForgeGradle `com.anatawa12.forge:ForgeGradle:1.2-1.0.+` (dynamic, `changing = true`), Forge `1.7.10-10.13.4.1614`, `runDir = "eclipse"`.
- Commands: `./gradlew setupDecompWorkspace` once (and again after editing `src/main/resources/META-INF/HBM_at.cfg`), then `./gradlew build`.
- Output: `build/libs/HBM-NTM-[1.0.27_X5778_H261].jar` plus `-src` and `-dev` jars. `./gradlew version` prints the bracketed version.
- The cloud container cannot build (JDK 21 only; the network policy blocks every Forge/NTM maven host; host list in `docs/fork-notes.md`, "Cloud / local split"). It also has no ForgeGradle workspace, so vanilla/Forge sources are not readable here: reason about vanilla methods from how this repo's callers use them and flag such assumptions. Cloud sessions write code and notes; the owner compiles and tests locally. Say this explicitly when handing off and never claim a build passed.
- What you can verify here: grep-level consistency (registration lines present, `methods()`/`invoke()` lists complete, `serialize`/`deserialize` order, lang keys, texture files, TileMappings entries).
- There are no automated tests. CI (`.github/workflows/build.yml`) runs only on push/PR to `space-travel-twopointfive`, on Temurin JDK 8, and sed-rewrites `RefStrings.VERSION` and `mod_build_number` to days-since-2010-10-10 before `./gradlew build`. Cloud branches get no CI. GitHub Actions has never run on this fork (0 workflow runs as of 2026-09-24), so enabling Actions in the fork's repository settings is the cheapest way to get jars built from pushes without a local build.
- Testing aids to name in the local hand-off: `hbm.cfg` `1.00_enableDebugMode` (verbose worldgen/structure logging); `/ntmserver set STRUCTURE_DEBUG true` (jigsaw diagnostics, `SpawnCondition.buildAll`); `/ntmclient set SHOW_BLOCK_META_OVERLAY true` and `DODD_RBMK_DIAGNOSTIC` (look overlays); `MobConfig.waypointDebug`; commands `/ntmrad set|clear`, `/ntmlocate <structure>`, `/ntmloadchunk`, `/ntmcustomize`, `/ntmstations`, `/ntmsatellites`, `/totaltime`, `/ntmpackets info`, `/ntmreload`, `/ntmreapnetworks`, `/dumpthreadsandcrashgame`; F3+T reloads OBJ/VBO models and QMAW pages; the dev run dir is `eclipse/` with its own `config/`. Full command list: `docs/codebase-map.md` area 1.

## Repository layout

One line per top-level package; `docs/codebase-map.md` has the depth.

- `src/main/java/api/hbm/`: integration surface (energymk2, fluidmk2, fluid (deprecated shims), block, conveyor, entity, item, ntl, recipe, redstoneoverradio, tile).
- `src/main/java/cofh/`: vendored CoFH RedstoneFlux API (12 files; keep upstream signatures).
- `com/hbm/main/`: `MainRegistry` (@Mod, lifecycle, registration order), `ServerProxy`/`ClientProxy`, `ModEventHandler*`, `NetworkHandler`, `ResourceManager`, `StructureManager`, `CraftingManager`, `NEIRegistry`/`NEIConfig`, `NTMSounds`.
- `com/hbm/lib/`: `RefStrings`, `Library` (connector checks, `POS_X`.. direction constants), `HbmWorld`/`HbmWorldGen`, `ModDamageSource`.
- `com/hbm/config/`: `hbm.cfg` classes (`GeneralConfig`, `SpaceConfig`, `WorldConfig`, `RadiationConfig`, ...) and JSON runtime configs written to `config/hbmConfig/`.
- `com/hbm/blocks/`: `ModBlocks` (flat registry), `BlockDummyable` multiblocks, `generic/`, `machine/` (+ rbmk, fusion, albion, pile), `bomb/`, `network/`, `turret/`, `rail/`, `fluid/`, `gas/`.
- `com/hbm/tileentity/`: `TileEntityLoadedBase` -> `TileEntityMachineBase` hierarchy, `TileMappings` (TE registry), proxies, `machine/`, `bomb/`, `turret/`, `network/`, `deco/`.
- `com/hbm/items/`: `ModItems` (7192-line registry), `ModItemsArmor`, `armor/`, `tool/`, `machine/`, `special/`, `block/` (ItemBlocks), `weapon/` (+ `sedna/` gun framework).
- `com/hbm/inventory/`: `OreDictManager`, `RecipesCommon` (AStack), `FluidContainerRegistry`, `fluid/` (Fluids, FluidType, traits), `material/` (Mats, MaterialShapes), `recipes/` (+ `loader/SerializableRecipe`), `container/`, `gui/`.
- `com/hbm/uninos/`: UNINOS node-network engine (UniNodespace, NodeNet, GenNode, networkproviders).
- `com/hbm/handler/`: glue handlers (GUIHandler, HbmKeybinds, HazmatRegistry, ArmorModHandler, EntityEffectHandler, radiation/, pollution/, neutron/, atmosphere/, threading/, ability/, nei/, imc/, ae2/, CompatHandler for OpenComputers).
- `com/hbm/hazard/`: item-carried hazards (HazardSystem, HazardRegistry, type/, modifier/, transformer/).
- `com/hbm/entity/`: mobs, effects, logic (explosion drivers, planes), projectiles, missiles, grenades, carts, trains, particles; `EntityMappings` registry.
- `com/hbm/explosion/`: ExplosionVNT pipeline (`vanillant/`), nuke ray tracers, legacy ExplosionNT.
- `com/hbm/dim/`: NTM: Space solar system, per-body dimension packages, traits (`CBT_*`), orbit, sky/cloud/weather providers.
- `com/hbm/world/`: world generation (features, NBT/jigsaw structures, terrain carvers, legacy dungeons).
- `com/hbm/packet/`: packets (`toclient/`, `toserver/`, `threading/`), `PacketDispatcher`, `PermaSyncHandler`.
- `com/hbm/render/`: TESRs, item/entity renderers, ISBRHs, OBJ loader, bus animations, shaders. Also `com/hbm/animloader/` (Collada, seal door only), `com/hbm/particle/`, `com/hbm/sound/`.
- `com/hbm/util/`: helpers (BufferUtil, I18nUtil, BobMathUtil, InventoryUtil, ItemStackUtil, Compat*, fauxpointtwelve BlockPos/DirPos; role table in `docs/codebase-map.md` area 16).
- `com/hbm/saveddata/`: WorldSavedData (satellites, Tom impact, annihilator). Smaller packages: `qmaw/` (in-game manual), `wiaj/` (client tutorial viewer), `module/`, `extprop/`, `potion/`, `commands/`, `creativetabs/`, `interfaces/`.
- `src/main/resources/assets/hbm/`: lang, manual (QMAW json), structures (nbt), models, textures, sounds + sounds.json, shaders, disks (OC floppy). `tools/`: Blender animation exporters and `check-fork.sh` (post-merge fork check).

## Core conventions

- Indentation: tabs by default (`.editorconfig`), but match the file you are in: 17 files are 4-space only (mostly `dim/*/genlayer/*`, `handler/ae2/MSUExternalStorageHandler`, `world/ModBiomes`, `inventory/gui/GuiInfoContainerLayered`) and `handler/CompatHandler.java`, `handler/atmosphere/ChunkAtmosphereManager.java`, `handler/ae2/AE2CompatHandler.java` are mostly spaces with a few tab lines.
- Line endings: everything is LF in the repository since `27811a59` (582 upstream CRLF files normalised, content-identical). `.gitattributes` declares every text type `text eol=lf` (`*.bat` checks out as CRLF, binaries `-text`), so git normalises on commit, and `.editorconfig` says `lf`. Upstream still ships CRLF files, so every merge from `upstream`/`hbm` must run with `-Xrenormalize` (`git config merge.renormalize true` once per clone makes it the default); without it every file upstream touched conflicts wholesale. Never re-introduce CRLF; `tools/check-fork.sh` fails if the index contains any. `git blame` skips the normalisation via `.git-blame-ignore-revs` (`git config blame.ignoreRevsFile .git-blame-ignore-revs` once per clone). Patches from cloud sessions apply with plain `git am`.
- Style: `if(cond)` and `for(` without a space, braces on the same line, obfuscated MCP names (`func_147480_a`) left as-is, informal comments.
- Logging: `MainRegistry.logger` (Log4j `HBM`; `preInit` swaps in FML's mod log) is the only logger, with the subsystem in brackets (`[QMAW]`, `[Jigsaw]`, `[Debug]`). Do not create loggers or add `System.out.println` (62 legacy calls remain).
- Translation: use `com.hbm.util.i18n.I18nUtil` (`resolveKey`, `resolveKeyArray`, `format`), never `net.minecraft.client.resources.I18n` (CONTRIBUTING rule; 343 legacy direct calls remain). On the dedicated server `I18nUtil` returns "I18N CALL SERVERSIDE - GREAT JOB"; server text uses `ChatBuilder`/`ChatComponentTranslation`.
- Side discipline: common code reaches the client only through `MainRegistry.proxy` virtuals (`ServerProxy` no-op base, `ClientProxy` override). `@SideOnly(Side.CLIENT)` on client-only methods (`provideGUI`, `getMaxRenderDistanceSquared`, render helpers). Never reference `ResourceManager`, `IItemRenderer`, `ModelBiped` or `AnimatedModel` from static fields or constructors of common classes.
- Tick split: TE logic under `if(!worldObj.isRemote)`, then `networkPackNT(range)` (block radius of the `TargetPoint`: 15-50 for single-block tiles, 100-150 for multiblocks, 250 for the largest; `serialize` runs every tick, keep it small); the client branch only does animation and audio. Sync fields via `serialize(ByteBuf)`/`deserialize(ByteBuf)` in identical order, calling `super` first.
- `CONTRIBUTING.md`: no new libraries, no `I18n`, no refactor PRs, no changelog edits, test on client and server. It also refuses AI-written code ("If you dare send me clanker code..."), so session output stays in this fork: never open PRs against HbmMods or JameH2 from a session. OC/NEI/AE2/CoFH are the existing compile dependencies; foreign APIs only behind `Loader.isModLoaded` / `@Optional`.
- Registration lives in `MainRegistry` (`PreLoad` -> `load` -> `PostLoad`) and is order-dependent: append a new registration at the END of the block that already holds its kind (full chain in `docs/codebase-map.md` area 1). Hazard-class and rad-resistance tables freeze in `PostLoad` (`ArmorUtil.register()` ~L599, `HazmatRegistry.registerHazmats()` ~L600), so armor must be constructed before that, in practice inside `ModItemsArmor.init()`.
- Persisted positional values, append only, never insert or reorder: packet discriminators (`PacketDispatcher`), `EnumAmmo` ordinals, `BulletConfig` ids (XFactory init order), weapon-mod ids (`WeaponModBase`; HashBiMap silently overwrites duplicate ids), `SolarSystem.Body` ordinals (ore meta), `XSatelliteRegistry` ids, `SiegeTier` ids, `CelestialBodyTrait` indices, Fluids IDs, `Mats` material ids.
- Lang keys: `item.<unloc>.name`, `tile.<name>.name`, `.desc` with `$` as the line break (all other key families in `docs/codebase-map.md` area 17). Edit `en_US.lang` (LF); mirror to `zh_CN`/`ru_RU` when practical. A literal `%` must be `%%`.
- Do not edit `changelog` in ordinary work (the CurseForge task rotates it and it conflicts on every upstream merge). A deliberate fork note goes under `## Added`/`## Changed`/`## Fixed` in its own commit.
- Upstream-merge friendliness: keep fork changes additive; do not reorder `ModItems`/`ModBlocks`/`TileMappings`/`ClientProxy` lists; leave `gradle.properties`, `RefStrings`, `README.md` alone unless releasing (`.editorconfig` carries one fork line, `end_of_line = lf`).
- Fork-owned code lives in `src/main/java/com/hbm/dbs/`: `DBSFork.init()` is the single entry point, called from the one `// DBS fork hook` line in `MainRegistry.PreLoad` directly after `ModItems.mainRegistry()` (it must run after `ModItems.mainRegistry()` and before `PostLoad`; keep it on the very next line so a merge conflict stays local and the script can verify the order); `DBSRecipes` is an `IRecipeRegisterListener` whose `onRecipeLoad` adds the restored recipes when `SerializableRecipe.initialize()` reports `AssemblyMachineRecipes` or `AnvilRecipes` (same JSON-override semantics as an inline default, no hook in any recipe class); `DBSItems` re-applies creative tabs. The only other fork lines in upstream files are the marked `BreederRecipeHandler` registration in `NEIRegistry` and the " (LEGACY)"-stripping lang edits in `en_US.lang`/`de_DE.lang`. Never put fork logic inline in an upstream file; never edit `ModItems`/`ModBlocks` for fork purposes when a post-init hook can do it (upstream's `@Deprecated` markers are inert; leave them). After every upstream merge run `bash tools/check-fork.sh` from the repo root (Git Bash on Windows) and fix every `MISS` before building; the script is LF like the whole tree.
- Marker annotations from `com.hbm.interfaces`: `@Spaghetti("why")` for known-bad code, `@Untested`, `@NotableComments`; keep them when editing. `@Deprecated` is used liberally on still-live legacy paths; check callers before deleting anything.
- Working agreement (details in `docs/fork-notes.md`): either the owner or a session makes code changes; a session always provides a commit message draft for its own changes; the owner reviews everything and pushes `space-travel-twopointfive` themselves. Running locally: edit the working tree, do not commit, put the draft message in the hand-off. Running in the cloud: commit and push only to the session's `claude/...` staging branch (the container is ephemeral), never to `space-travel-twopointfive`; the staging commit message is the draft. Attribution trailers for session commits come from the session's system reminder.

## How-to checklists

Full recipes with exemplar paths are in `docs/codebase-map.md`; the four most common tasks are inlined here.

Add an item:
1. Declare `public static Item x;` in `src/main/java/com/hbm/items/ModItems.java`.
2. Instantiate in `initializeItem()`: `new Item().setUnlocalizedName("x").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":x")`; use `ItemCustomLore` for `.desc` tooltips, `ItemEnumMulti(Enum.class, true, true)` for meta variants.
3. Add `GameRegistry.registerItem(x, x.getUnlocalizedName());` in `registerItem()` (forgetting this silently makes it unobtainable; 7 items already suffer this).
4. Texture `assets/hbm/textures/items/x.png` (enum: `x.<enum_lower>.png`); lang `item.x.name` (enum: `item.x.<enum_lower>.name`).
5. Hazards in `hazard/HazardRegistry.registerItems` (fuel helpers such as `registerRBMKRod`/`registerRTGPellet` are private to that class); ore dict in `inventory/OreDictManager`.
Exemplar: `src/main/java/com/hbm/items/special/ItemExpensive.java`.

Add a block:
1. Declare in `blocks/ModBlocks.java`; fluent chain in `initializeBlock()` ending in `.setBlockTextureName(RefStrings.MODID + ":x")` (mandatory for bases that do not extend `BlockBase`, e.g. `BlockOre`, `BlockHazard`, `BlockDummyable`).
2. `register(x);` in `registerBlock()` to get `ItemBlockBase`. The 2-arg `GameRegistry.registerBlock` gives a vanilla ItemBlock and silently drops `ITooltipProvider`/`IBlockMulti`/`IPersistentInfoProvider`.
3. Texture `textures/blocks/x.png`; lang `tile.x.name` (+ `tile.x.desc`).
4. Registry name is `hbm:tile.<name>`; renames need `ModBlocks.addRemap` or `MainRegistry.handleMissingMappings`.
Exemplar: `src/main/java/com/hbm/blocks/generic/BlockGeneric.java`; enum subtypes: `generic/BlockMeteorOre.java`.

Add a machine end-to-end:
1. Block `extends BlockDummyable`: `createNewTileEntity` returns the core TE for `meta >= 12`, `new TileEntityProxyCombo().inventory().power().fluid()` for `meta >= 6`, else null; `getDimensions()` = {U, D, N, S, W, E} in the SOUTH-facing frame; `getOffset()`; override `fillSpace` to call super then `makeExtra` on ports after `x += dir.offsetX * o; z += dir.offsetZ * o;`; `onBlockActivated` -> `standardOpenBehavior(world, x, y, z, player, 0)`.
2. TE `extends TileEntityMachineBase implements IEnergyReceiverMK2, IFluidStandardTransceiverMK2, IGUIProvider` (+ `IUpgradeInfoProvider`, `IControlReceiver`, `IConfigurableMachine` as needed); `DirPos[] getConPos()`; server tick: `trySubscribe`/`tryProvide` per port, work, `networkPackNT(range)`; `serialize`/`deserialize`; NBT.
3. `put(TileEntityMachineX.class, "tileentity_machine_x");` inside `tileentity/TileMappings.writeMappings()`.
4. `ModBlocks` field, init and `register(machine_x)`.
5. `inventory/container/ContainerMachineX` (prefer `ContainerBase`) and `inventory/gui/GUIMachineX extends GuiInfoContainer`; the TE's `provideContainer`/`provideGUI`.
6. `render/tileentity/RenderX extends TileEntitySpecialRenderer implements IItemRendererProvider`, bound in `ClientProxy.registerTileEntitySpecialRenderer` (the item renderer auto-registers); model and texture statics in `main/ResourceManager` (triangulated OBJ with normals for `.asVBO()`).
7. Lang `tile.machine_x.name`, `tile.machine_x.desc`, `container.x`; recipes from fork code, not inline upstream: assembler recipe as a branch in `com/hbm/dbs/DBSRecipes.registerAssembly` ("Add a recipe" step 5), crafting-table recipe via `CraftingManager.addRecipeAuto(...)` called from a fork method.
Exemplars: `blocks/machine/MachineChemicalPlant.java`, `tileentity/machine/TileEntityMachineChemicalPlant.java`, `render/tileentity/RenderPump.java`, `tileentity/machine/TileEntityMachineElectricFurnace.java` (single block).

Add a recipe:
1. GenericRecipes machines: in the set's `registerDefaults()` add `this.register(new GenericRecipe("chem.x").setup(dur, power).inputItems(new OreDictStack(STEEL.ingot(), 2)).inputFluids(new FluidStack(Fluids.WATER, 1000)).outputItems(...).outputFluids(...));` (names unique within the set, keep the machine prefix, respect the `*Limit()` counts).
2. Classic SerializableRecipe sets: use the class's add helper in `registerDefaults()` and make sure `readRecipe`/`writeRecipe` cover any new field.
3. Crafting table: `CraftingManager.addRecipeAuto(...)` in `com/hbm/crafting/*` (ore-dict Strings switch to ShapedOreRecipe).
4. Defaults apply only when `config/hbmRecipes/hbm<Machine>.json` is absent; delete the JSON on test installs.
5. Fork-only recipes go in `src/main/java/com/hbm/dbs/DBSRecipes.java` (add a branch for the set's class name in `onRecipeLoad`), never inline in the upstream set.
Exemplar: `src/main/java/com/hbm/inventory/recipes/ChemicalPlantRecipes.java`; classic: `PressRecipes.java`.

Rarer tasks, full recipes in the map: a fluid (`Fluids.init()` above `//ADD NEW FLUIDS HERE` + `metaOrder.add`; area 10), a packet (`IMessage` + nested `Handler`, APPEND to `PacketDispatcher.registerPackets()`; area 16), an OC component (`@Optional.InterfaceList` + direct `SimpleComponent` + `CompatHandler.OCComponent`, `@Callback(direct = true)`, `methods()`/`invoke()` for multiblocks; area 18), armor sets and armor mods (area 8), worldgen (area 14), entities (area 12).

## Top gotchas

Only the ones that bite most tasks; every area in `docs/codebase-map.md` has its own Gotchas list.

- `MainRegistry` has two `FMLPreInitializationEvent` handlers (`PreLoad`, `preInit`); their relative order is not guaranteed. Config is loaded inside `PreLoad` (`loadConfig()` at line ~275). Do not add cross-dependencies between the two.
- `TileMappings.put` is private: new TEs go inside `writeMappings()`. An unregistered TE fails at chunk load ("Skipping TileEntity"), not at startup. OC-only TEs belong inside the `ocPresent` guard.
- `onChunkUnload()` overrides must call `super.onChunkUnload()`: `TileEntityLoadedBase` sets `isLoaded = false` there and `NodeNet.isBadLink` depends on it. 25 of 50 existing overrides (including the Centrifuge/Diesel audio exemplars) skip it.
- Net subscriptions expire after 3000 ms wall-clock (`PowerNetMK2.timeout`, `FluidNetMK2.timeout`; pneumatic 1000 ms): re-`trySubscribe` at least every 20 ticks. Destroying any node destroys its whole `NodeNet`; it re-forms next tick. `IEnergyReceiverMK2.tryUnsubscribe` is a no-op.
- `MachineDynConfig` instantiates every `IConfigurableMachine` via `newInstance()`: public no-arg ctor required, no side effects, and constructor-time reads of config statics see defaults.
- `ArmorFSB.cloneStats` shares the effects list and re-runs `setRadResist` per piece but does not copy `setHazardClass`/`hides`/`setOverlay`; set those per piece. Hazard tables freeze in `PostLoad` (see Core conventions).
- `SerializableRecipe.initialize()` has no per-handler error isolation: one malformed user JSON aborts every later handler. A user `config/hbmRecipes/hbmPrecisionAssembly.json` disables expensive mode (`GeneralConfig.trueExp()`).
- `BlockOre` meta is the planet (`SolarSystem.Body.ordinal()`), not a subtype: ore-dict with `oreAll()`. `CelestialBody.getBody(...)` falls back to Kerbin for unknown ids; Thatmo is unbound (no Body enum entry, `WorldGeneratorThatmo` never registered).
- QMAW manual pages require `name`, `title`, `content` AND `trigger` (use `"trigger": []`); a page without it is skipped with `[QMAW] Error reading manual`.
- Config locations: `hbm.cfg` (read once, no reload); `config/hbmConfig/` for JSON (`hbmClient/Server/Machines/CustomMachines/Fallout/ItemPools/FluidTypes/FluidTraits/Armor/RadResist.json`; underscore-prefixed files are templates and never read); `config/hbmRecipes/` for recipe JSON.
- Known upstream oddities not to "fix" blindly (they change balance or saves): `PowerNetMK2.update` cumulative `toTransfer -= energyUsed` across priorities, `OreLayer3D` using `cacheX` for the z axis, `AnimationLoader` never storing `rotmode` (details in the map).

## Pointers

- `docs/codebase-map.md`: per-area purpose, key files, patterns, full how-to recipes, gotchas, smells, open questions.
- `docs/known-bugs.md`: tracker of verified upstream defects (location, evidence, proposed fix, policy, status) plus the claims that were checked and rejected. Read the entries for a subsystem before changing it; update Status when a fix lands.
- `docs/fork-notes.md`: lineage, what NTM: Space changed, what the owner restored, upstream-merge procedure (routine JameH2-tip merge and direct HbmMods merge) and conflict hotspots, cloud/local split, OC-LuaJIT relation, future work candidates.
- Upstream docs in-tree: `CONTRIBUTING.md`, `README.md` (JameH2 setup notes), `src/main/resources/assets/hbm/manual/STYLEGUIDE.md`, `src/main/resources/assets/hbm/disks/README.md`, `src/main/java/api/hbm/*/package-info.java`, `src/main/java/com/hbm/handler/neutron/package-info.java`.
