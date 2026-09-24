# Fix roadmap for the upstream defects

Plan for working through `docs/known-bugs.md` (330 verified defects at `22c9c89c`). It says what to fix first, what depends on what, how each batch is delivered and tested, and which items need the owner's decision before anyone touches them. Update it as batches land; the tracker's Status fields are the source of truth for individual items, and each tracker entry carries a `Plan:` line naming its phase or decision row here.

Re-triaged on 2026-09-24 after `CONTRIBUTING.md` was removed (`ea346f74`): DBS is a personal fork with no upstream pull requests planned and no restriction on AI-authored code, so "would upstream accept this" and "is this a refactor" no longer decide anything. The only remaining criteria are benefit and merge surface. That moved 94 of the 117 entries in the old "leave" and "upstream-only" buckets: 88 into fix phases and 6 into the decision list; the 3 other upstream-only items became "leave", and 23 entries stay left alone.

## Principles

- One batch per patch. Each batch is a set of related fixes that is tested together, delivered as one commit (cloud) or one hand-off (local), built and tested by the owner on client and dedicated server, then merged. Batches are sized so a session can finish one.
- Reactors, OpenComputers, saves and crashes first. The fork exists for the restored reactors, the owner develops OC-LuaJIT, and save-data or crash defects hurt most, so those batches come before cosmetic or dead-code cleanup.
- Small diffs by default, larger ones when they pay for themselves. A fix is normally the smallest change that corrects the defect, on the defect's own line(s), because every changed line in an upstream file is a line that can conflict on the next merge. Refactors and structural changes are allowed (Phase 8 collects them) when the benefit justifies that cost; they are done as their own batches so a bad merge can be reverted in one piece.
- Every fix line in an upstream file carries a marker: `// DBS fix B-123` (optionally followed by a short reason) on the changed line or the line above. Asset fixes (`sounds.json`, lang, manual JSON, deleted files) cannot carry a comment; their tracker Status says `fixed <commit> (no-marker)`.
- The tracker is the manifest. When a fix lands, its entry gets `Status: fixed <short hash>`. `tools/check-fork.sh` reads those entries and fails after a merge if a marker has disappeared, which is exactly how an upstream merge would silently undo a fix.
- Check upstream first. Before fixing an item, look for an upstream fix since the merge base: `git log -S'<distinctive code>' hbm/master -- <file>` and `git diff 9a2eb731 hbm/master -- <file>`. If HbmMods fixed it, port their hunk instead (it will then merge cleanly) and record `fixed upstream <hash>`.
- Decide items are not fixed on a whim. Each "Needs a decision" entry changes balance, world generation, saves, or player-visible behaviour. The questionnaire below carries a recommendation; the owner answers, the answer is recorded in the tracker (`Status: wontfix <why>` or moved into a batch), and only then is it worked on.

## Phase 0: tooling (done)

- Marker convention above.
- `tools/check-fork.sh` walks `docs/known-bugs.md`, collects every entry whose Status starts with `fixed` and does not say `no-marker`, and asserts a `DBS fix B-xxx` marker exists under `src/`.
- B-025 (`.editorconfig` CRLF) is resolved by the LF normalisation and marked fixed as the first example.

## Phase 1: the reactors (owner's content)

Goal: the restored research and breeding reactors behave, automate and expose to OC like first-class machines.

| ID | What | Notes |
|---|---|---|
| B-079 | research reactor accepts fuel only in slot 0 | one-character fix (`i >= 0 && i < 12`) |
| B-078 | research reactor never lets automation extract waste plates | replace `containsValue(stack)` with an item check |
| B-168 | ContainerReactorResearch shift-click off by one | `index < 12` |
| B-163 | ContainerMachineReactorBreeding shift-click off by one | `index < 2` |
| B-312 | research reactor `setLevel` missing from `methods()`/`invoke()` | needed for OC control through ports |
| B-172 | BreederRecipeHandler hard-codes `"breeding"` | fork-owned file, no marker needed |
| B-077 (D11) | breeding progress uses integer division of flux | recommended: fix; small speed change for non-integer flux ratios |
| B-066, B-067 | RBMK auto control rod crashes (null function, wrong modulus) | two small edits in one file |
| B-307 | RBMK manual rod `getColor` NPE from OC | null check |
| B-080 | RBMK surge dial client fallback reads the wrong key | one constant |
| B-083 | GameRuleHelper creates a gamerule named "" | pass the key through |
| B-069, B-070, B-071 (D10) | RBMK `diag` static flag, blocked-stream irradiation target, dead control-rod tail check | recommended: fix all three |

Dependencies: B-312 is only testable through ports after Phase 4's B-319, so test it by direct cable contact in this phase and re-test in Phase 4. The D10 and D11 items wait for the owner's answer; the rest can go immediately.

Test: load a world with both reactors; hopper fuel plates into every research-reactor slot and pull waste plates out; shift-click in both GUIs from every slot; drive the research reactor from an OC computer wired directly to the core (`setLevel`, `getInfo`); RBMK auto rods with all three functions, plus `/gamerule` listing showing no rule named ""; save, quit, reload, confirm all state survives.

## Phase 2: crashes and save data

Small, isolated, each independently testable.

- Crashes: B-002 (potion array size), B-037 (corium fluid icons), B-093 and B-094 (pylon and nodespace NPEs), B-195 (satellite NBT NPE), B-218 (bedrock ore NPE), B-265 (keybind packet bounds), B-267 (BufferUtil short length), B-306 (floppy recipe null), B-308 (artillery target queue).
- Save data: B-003 (contamination effects lost on relog), B-055 (tank fill zeroed without `_max`), B-068 (Zirnox `fire`/`onFire`), B-176 (two entities named `entity_cloud_rainbow`).

Dependencies: B-267 changes the on-wire length prefix, so client and server must run the same fork build (they do; note it in the hand-off). B-176 renames a registry name; both clouds are short-lived visual entities, so no world migration is needed, but do it before any world depends on the fix.

Test: client and server start clean; trigger each path where practical (a Zirnox meltdown, a tank save/reload, relog with an active contamination effect; the keybind guard is reviewed rather than exercised).

## Phase 3: the shift-click family

Eleven containers with the same off-by-one shape: B-155, B-156, B-157, B-158, B-159, B-160, B-161, B-162, B-164, B-165, B-166, B-167. Each is a one- or two-line bound change; keep them per-file for now (the shared helper is Phase 8's B-174).

Test: open each GUI, shift-click from every tile slot and from the first player slot, both directions.

## Phase 4: OpenComputers wiring

Goal: every HBM component is reachable through multiblock ports and behaves the same from OC-LuaJIT as from stock Lua.

Order matters here:

1. B-319 (D27, recommended: fix) and B-326: normalise the eight lowercase `"opencomputers"` mod ids. Test a ported multiblock (fluid tank, coker) from OC before and after: ports should expose `methods()`.
2. B-321 and B-323: direct `SimpleComponent` on `TileEntityLaunchTable` and `TileEntityWatz`.
3. `methods()`/`invoke()` drift: B-310 (large turbine `getPower`), B-313 (Watz bogus name), B-315 and B-316 (artillery turrets), and Phase 1's B-312 re-tested through a port.
4. Argument and return hygiene: B-314 (telex argument probing), B-324 (crane console returns `"N/A"` instead of nil plus message), B-325 (battery mode narrowing), B-318 (cable `Colored` interface string), B-327 (doc typo), B-328 (D27, recommended: iterate entries) for the radio torch.
5. B-317: make `PWRangler.lua` 5.2-safe (five `//` sites), then run it under OC-LuaJIT and under stock Lua 5.2.
6. B-311 (D27, recommended: remove the microwave test callbacks).

Documented behaviour unless the owner decides otherwise (D27): B-309 (listed callbacks dispatch direct), B-320 (verify in-game only), B-322 (name reuse), B-329 (cache bypass).

Test: an OC computer per component class, through a port and by direct contact, on OC-LuaJIT and on stock Lua 5.2; `component.list()` names; every listed method callable; PWRangler runs.

## Phase 5: networks, threading, leaks

- B-061: add `super.onChunkUnload()` to the 25 overrides (list in the tracker). Mechanical, 25 files, one line each with a marker.
- B-101: battery node created at the port position but looked up at the tile's own position (REDD ghost nodes).
- B-107 and B-030 (D6, recommended: fix): evict `UniNodespace.worlds` and `TileAccessCache` entries on `WorldEvent.Unload`; same handler, do together.
- B-019: `tryUnsubscribe` uses `createNode()` (no-op).
- B-017: clamp fluid pressure ranges to the array size (latent out-of-bounds).
- B-104: request-network throttle dead code.
- B-212: simple radiation backend unload key.
- B-084 and B-210: neutron node caches (pile nodes never evicted; caches dropped whenever a world has no streams for one tick).
- B-102 and B-103 (D14): RTTY channels never evicted, plus the test channel injected every tick.
- B-098 and B-099 together: call `PacketThreading.init()` at startup and fix the inverted validation, so the config keys take effect. B-100 (double `toBytes`). B-095 and B-096 stay as designed unless the owner decides otherwise (D13).
- B-105 (D14, recommended: real render bbox for pipelines).
- B-278: release the inbound `BufPacket`/`EntityBufPacket` buffer in an outer `finally`, so a missing or foreign receiver no longer leaks it (Netty refcount warnings on the client).
- B-188: pass the time budget into `ExplosionNukeRayBatched.collectTip`, so ray collection for a large nuke is paced by `BombConfig.mk5` like the destruction phase instead of stalling the server tick.

Dependencies: B-061 first; the other net fixes assume `isLoaded` becomes false on unload. Test with cables and pipes across chunk borders: unload and reload chunks, `/ntmreapnetworks` count before and after, `/ntmpackets info` with threading on and off, a long session for memory growth, an idle RBMK for a few minutes.

## Phase 6: silent misconfiguration and gameplay one-liners

The remaining small `fix` items. Group by test surface; several sessions.

- Config and loaders: B-006 (fallout key), B-007 (virus field default), B-009 (identity file), B-004 (chunkloader callback `return` that skips every later ticket), B-011 (client config on servers), B-027 (tool types), B-059 (machine config logging), B-139 (log when a user PrecAss JSON disables expensive mode), B-140 (item pool template and error), B-141 (FT_Rocket constructor), B-142 and B-143 (foreign fluid traits, fluid id collisions), B-144, B-146, B-147, B-149, B-150 (recipe loader logging and round trips), B-204 (IMC tag type), B-206 (hazard key), B-207 (own flags for autism/glitch hazards), B-229 (structure default), B-230, B-232, B-235, B-236 (celestial body and trait lookups), B-237 (biome dictionary), B-275 (unregistered packet guard).
- Blocks and machines: B-053 (beam `findCore` signature), B-050 (placement preview rotation), B-051 (side passed as GUI id, 14 sites), B-056 (upgrade mutex), B-058 (electric furnace subscribes every 20 ticks), B-060 (Energy Control key), B-024 (xenon thruster flag), B-040 (finite fluid icons shared between liquid concrete and corium), B-039 (log the corium band-aid), B-072, B-073, B-074 (launcher copy-paste bugs), B-054 (BlockMachineBase early return), B-091 (RBMK console name), B-219 (restore `isHellWorld` after populate).
- Items, weapons, entities, hazards: B-110 (armor-mod tooltip loop), B-111 (duplicate armor material name), B-112 (cloned armor shares its effects list), B-119 (artillery rocket keeps its target across reloads), B-120, B-125, B-126, B-132 (sedna), B-137 (container ids), B-178, B-179, B-180, B-181, B-185 (entities and explosions), B-194 (atmosphere null guard), B-196 (AE2 long-to-int clamp), B-197 (microblocks meta 15), B-199 (PRISM z origin; only visible with PRISM on), B-201 (satellite miner cargo).
- Client: B-253 (hotbar animation expiry), B-256 (loop sound pitch), B-258 (animation rotmode), B-247, B-248, B-249, B-251, B-252 (loader and renderer guards that turn client crashes into log lines), B-269 (packet handlers swallow exceptions: log them), B-277 (direct BufPacket sends routed through `networkPackNT`), B-274 (wiaj static fast-forward state).
- Utilities: B-270, B-271, B-273, B-028.
- Space: B-223 (Thatmo provider args), B-222 (deterministic terrain noise), B-238 (Ike pedestal item set by the structure instead of a full-chunk block scan).
- NEI (only with GTNH NEI installed): B-154, B-171.

Confirm before doing, even though the tracker says fix (they change what players experience): B-041 (mercury, bromine and chlorine fluids start hurting entities), B-198 (mobs stop being healed on every spawn event), B-221 (sleeping on a planet advances to morning instead of resetting to 0), B-227 (planet structures start generating once the global flag is fixed; pair with B-233).

Test per group: the machines or items named in each entry, plus a client and server start with no new log warnings.

## Phase 7: assets, resources, dead code

Low risk, no gameplay change, mostly deletions and data edits. `no-marker` fixes for assets; deleted classes need no marker either (the tracker records the commit).

- Sounds: B-288, B-289, B-290, B-291 (wrong or missing event ids), B-296 (missing `grenadeBounce2.ogg`), B-303 (`fm.*` entries).
- Lang and manual: B-292 (23 duplicate keys in `en_US.lang`), B-294 and B-295 (manual pages that never load), B-298 (dead sound-category key).
- Client resources: B-257 (45 missing texture paths), B-261 and B-262 (unbound renderers), B-260 (dead theta branch), B-263 (test renderers with missing textures, together with their test block and entity), B-297 (missing `excavator.nbt`), B-300 (corrupt `.obj`), B-301 (unreferenced images), B-302 (unreferenced shaders), B-305 (unreferenced structures).
- Duplicate registrations: B-133 (casing registered twice), B-151, B-153.
- Dead code and misleading leftovers: B-012, B-014, B-015, B-031, B-032, B-034, B-035, B-045, B-048, B-062, B-063, B-085, B-087, B-088, B-089, B-090, B-114, B-117, B-118, B-129, B-131, B-152, B-173, B-175, B-190, B-191, B-192, B-214, B-217, B-241, B-243, B-245, B-246, B-279, B-280, B-330.

Test: client starts with fewer resource warnings than before (count `Unable to` and `not found` lines in the log before and after); a full build with no compile errors from deleted classes.

## Phase 8: structural improvements

Larger changes that upstream's no-refactor rule used to exclude. Each is its own batch with its own test, because each touches more than one line of upstream code.

| ID | Change | Why it pays |
|---|---|---|
| B-057 | `TileEntityLoadedBase.getDescriptionPacket`/`onDataPacket` carrying the `serialize` buffer | machines show real state the moment a chunk loads instead of up to a second later; affects every tile |
| B-010 | one deterministic pre-init path (`preInit` called from `PreLoad`) | removes an ordering hazard every future hook inherits |
| B-046 | a BLOCK branch in `handleMissingMappings` | the fork can rename blocks without silently deleting them from worlds |
| B-038 | slab pairs stored on `BlockMultiSlab` | new slabs stop needing a hard-coded table |
| B-043 | `WorldGenLiquidsCelestial` restores `scheduledUpdatesAreImmediate` in a `finally`; gas ticks stop clearing a world-global flag | removes a band-aid that can break the celestial liquid generator |
| B-123 | per-evaluation magazines in `WeaponModCaliber` instead of mutated statics | removes shared mutable state between guns and sides |
| B-124 | mod state save/restore over all receivers | akimbo and multi-receiver guns keep their magazines after a mod change |
| B-174 | fold `ContainerNT`'s merge fixes into `ContainerBase` (or delete `ContainerNT`) | one correct merge implementation for every container |
| B-220 | per-player sleep flag instead of a global static | multiplayer sleeping on planets stops racing |
| B-255 | `holdUntil` computed from unscaled durations | Trenchmaster and any other sped-up animation stop desyncing |
| B-268 | length-prefixed CBT section in `PermaSyncHandler` | one bad trait no longer blanks satellites, time and stations for the whole packet |

Test per change: the subsystem it touches, on client and dedicated server, plus a save/reload.

## Decisions needed

Each row groups tracker entries that share one decision. Answer per row; the answer goes into the tracker as a Status and the item into a phase.

| # | Items | Question | Recommendation |
|---|---|---|---|
| D1 | B-001, B-013 | BlockMigrations stub: finish it or delete it? Never fix only one of the two: wiring `buildNumber()` in makes the infinite loop reachable. | Delete both (nothing migrates today). |
| D2 | B-005 | Reset NBT-only player props only on death? | Yes; End-portal return should keep state. |
| D3 | B-008 | Update check points at JameH2's version string, so the fork always reads "outdated". | Point it at the fork's own `RefStrings` on the fork repo, or disable the check. |
| D4 | B-026, B-286, B-287 | CI branches, README maven coordinates, credits string. | Widen CI to `claude/**`; fix README and credits at the next release. |
| D5 | B-018, B-020, B-021, B-022, B-023, B-029 | Power and fluid net distribution math (double feed of conductor-receivers, cumulative subtraction across priority tiers, diode clamping, direct-push gate, one-sided purge). | Leave until measured in a creative test world with mixed priorities; changing it changes balance. |
| D6 | B-030, B-107 | Evict per-world caches on unload (leak fix, no gameplay change). | Fix, in Phase 5. |
| D7 | B-042, B-044 | Tooltips lost on 32 vanilla-ItemBlock blocks; all look-overlays gated on the RBMK diagnostic option. | Register `asphalt_stairs`, `steel_grate`, `spike_cacti` with `ItemBlockBase`; give look-overlays their own option. |
| D8 | B-049, B-052 | BlockDummyable static and unsynchronised scratch state. | `try/finally` around `safeRem`; make `positions` a local (now allowed; Phase 8 candidate if wanted). |
| D9 | B-064 | Inert industrial generator in the creative tab. | Hide it (`setCreativeTab(null)` via a `DBSItems`-style hook). |
| D10 | B-069, B-070, B-071, B-081 | RBMK diagnostic flag, blocked-stream irradiation target, the dead control-rod tail check (upstream's #1933 fix that never worked), PWR ROR `rods` inversion. | Fix all four (Phase 1); make ROR get/set symmetric on `rodLevel`. |
| D11 | B-077 | Breeding reactor float division. | Fix (Phase 1). |
| D12 | B-076 | Poison-dart trapped brick is placeable but does nothing. | Hide the variant; implement later as a fork feature if wanted. |
| D13 | B-095, B-096 | Packet threading drops queued packets after a 50 ms wait. | Leave (performance design); log when it happens. |
| D14 | B-103, B-105, B-106, B-108 | RTTY test channel, pipeline render bbox, request-network rescans, empty-update nets. | Remove the test channel; fix the pipeline bbox; leave the other two. |
| D15 | B-113 | Cyrillic letter in `item.med_ipecac.desc` (code and three lang files). | Fix all four together. |
| D16 | B-115, B-116 | Unregistered items (californium pellets; schizophrenia pill, PCH, misc ammo). | Leave unless the owner wants californium RBMK fuel; then register with lang, textures and hazards. |
| D17 | B-121 | Belt magazine NBT key not indexed. | Leave; changing it breaks existing guns' NBT. |
| D18 | B-134, B-135, B-136, B-145 | Recipe loader robustness (throws on empty cracking list, NPE on missing JSON fields, no per-handler isolation). | Fix: log and continue, as `DBSRecipes` already does. |
| D19 | B-169, B-170 | NEI drag-and-drop for raw containers; GTNH-NEI handler ordering. | Leave; B-170 only with GTNH NEI installed. |
| D20 | B-177, B-183, B-186, B-189, B-182 | Nuke explosion persistence and config options. | Persist MK5 state (B-177); honour `limitExplosionLifespan` in MK5 (B-186); delete dead options (B-189); leave the ray math and glyphid stats. |
| D21 | B-187 | Siege tunneler has a renderer but is never registered. | Register it as a mob if the owner wants it in siege waves; otherwise delete the renderer and class. |
| D22 | B-193, B-200, B-202, B-209, B-211, B-213, B-216 | Atmosphere thread reads, neutron double count, neutron default, satellite colours, PRISM force-load, dead radiation backends and options. | Fix B-200 (halves neutron-activated radiation, currently double-counted) and B-209; delete dead code; leave the rest. |
| D23 | B-224, B-225, B-226, B-231, B-233, B-234, B-239, B-240, B-242, B-244 | World generation: meteors on every planet, global water opacity, ore-layer z axis, shared trait instances, missing populate events, Thatmo, Eve cascades, overworld provider re-registered on every non-Earth load, dead layers. | Fix B-231, B-239, B-240 (guard on dimension 0); fix B-233 together with B-227; leave B-226 for existing worlds (changes strata); leave B-224, B-225, B-234; delete dead layers. |
| D24 | B-259, B-264 | Unfinished particle engine handler; unreferenced skybox chainloader. | Unregister the handler; delete the chainloader. |
| D25 | B-266, B-272, B-283, B-284 | ArmorUtil assertion, `readNBT` null marker, math helpers, dead classes. | Turn the assertion into a log line; keep `readNBT` behaviour; delete the dead classes. |
| D26 | B-293, B-299, B-304 | Duplicate keys in translations; unreferenced models and sounds. | Script-dedupe translations when next touched; delete assets after a manual sweep (smaller jar). |
| D27 | B-309, B-311, B-319, B-320, B-322, B-328, B-329 | OpenComputers design items. | Fix B-319 and B-328, remove B-311; document B-309 and B-322; verify B-320 in-game; leave B-329. |
| D28 | B-109 | `onEquip` fires only when the held item type changes, so swapping between two guns of the same type skips the equip animation. | Compare stack identity or NBT as well (small, but changes the feel of weapon swaps). |
| D29 | B-184 | Explosion presets and the nuclear glyphid still use the deprecated `EntityProcessorStandard` (weaker than the cross processors). | Switch to `EntityProcessorCross`; changes knockback and damage of those explosions. |
| D30 | B-203 | A user `hbmRadResist.json` replaces the whole built-in resistance table instead of merging into it. | Merge (no `clear()`), matching what users expect from an override file. |
| D31 | B-016 | `ModEventHandler.worldTick` keeps a dead reflective `reference` hack (never set; would randomise a static float and dismount players if it were). | Delete the block; nothing in the tree sets the field. |
| D32 | B-047 | `BlockHazard.getRarity` tests `block_schraranium` twice; the second condition was probably meant for another block. | Drop the duplicate unless the owner wants `block_schrabidium` (or another block) at that rarity; then name it. |
| D33 | B-086 | `TileEntityLaunchPadRocket.isPadObstructed` checks only the pad centre column although the tower footprint is 5 wide. | Widen the loops to -2..2 (rockets stop launching through partial cover); a single sky check if the current behaviour is wanted. |

## Not planned

Left alone on purpose (`policy: leave` in the tracker, 23 entries): harmless quirks, or upstream design the fork keeps because changing it costs more than it returns.

- Design kept: B-097 (breaking one node dissolves and re-forms the net), B-122 (NPC guns reload for free), B-130 (beams die on load), B-075 (assembly nukes have no sided slots), B-148 (a live recipe JSON also replaces the fork's recipes), B-208 (GT6 renames uranium), B-205 (simple radiation backend drops writes to unloaded chunks).
- Contracts too broad to change: B-138 (`ComparableStack` equals/hashCode), B-127 (gun NBT resync without a client cache), B-281 and B-282 (positional packet discriminators), B-228 (renumbering config keys resets values), B-250 (mixed-mode OBJ rendering unsupported).
- Harmless leftovers: B-033, B-036, B-065, B-082, B-092, B-128, B-215, B-254, B-276, B-285.

## Delivery checklist per batch

1. Re-check each item against `hbm/master` for an upstream fix; port it if one exists.
2. Apply the fix with a `// DBS fix B-xxx` marker (or note `no-marker` for assets and deletions).
3. Update the tracker: `Status: fixed <hash>` (hash filled in after the owner commits, or the session's staging hash).
4. Run `bash tools/check-fork.sh`.
5. Hand off with the test procedure from the phase above; the owner builds, tests on client and dedicated server, commits, pushes.
