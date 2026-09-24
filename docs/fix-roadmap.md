# Fix roadmap for the upstream defects

Plan for working through `docs/known-bugs.md` (330 verified defects at `22c9c89c`). It says what to fix first, what depends on what, how each batch is delivered and tested, and which items need the owner's decision before anyone touches them. Update it as batches land; the tracker's Status fields are the source of truth for individual items.

## Principles

- One batch per patch. Each batch is a set of related fixes that is tested together, delivered as one commit (cloud) or one hand-off (local), built and tested by the owner on client and dedicated server, then merged. Batches are sized so a session can finish one.
- Reactors, OpenComputers, saves and crashes first. The fork exists for the restored reactors, the owner develops OC-LuaJIT, and save-data or crash defects hurt most, so those batches come before cosmetic or dead-code cleanup.
- Minimal diffs in upstream files. A fix is the smallest change that corrects the defect, on the defect's own line(s). No refactors, no reformatting, no "while I'm here". Anything larger goes through `com/hbm/dbs/` or waits for a decision.
- Every fix line in an upstream file carries a marker: `// DBS fix B-123` (optionally followed by a short reason) on the changed line or the line above. Asset fixes (`sounds.json`, lang, manual JSON, deleted files) cannot carry a comment; their tracker Status says `fixed <commit> (no-marker)`.
- The tracker is the manifest. When a fix lands, its entry gets `Status: fixed <short hash>`. `tools/check-fork.sh` reads those entries and fails after a merge if a marker has disappeared, which is exactly how an upstream merge would silently undo a fix.
- Check upstream first. Before fixing an item, look for an upstream fix since the merge base: `git log -S'<distinctive code>' hbm/master -- <file>` and `git diff 9a2eb731 hbm/master -- <file>`. If HbmMods fixed it, port their hunk instead (it will then merge cleanly) and record `fixed upstream <hash>`.
- Decide items are not fixed on a whim. Each "Needs a decision" entry changes balance, world generation, saves, or player-visible behaviour. The questionnaire below carries a recommendation; the owner answers, the answer is recorded in the tracker (`Status: wontfix <why>` or moved into a batch), and only then is it worked on.

## Phase 0: tooling (done with this document)

- Marker convention above.
- `tools/check-fork.sh` gains a section that walks `docs/known-bugs.md`, collects every entry whose Status starts with `fixed` and does not say `no-marker`, and asserts a `DBS fix B-xxx` marker exists under `src/`.
- B-025 (`.editorconfig` CRLF) is already resolved by the LF normalisation and is marked fixed in the tracker as the first example.

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
| B-077 (decide) | breeding progress uses integer division of flux | recommended: fix; small speed change for non-integer flux ratios |
| B-066, B-067 | RBMK auto control rod crashes (null function, wrong modulus) | two small edits in one file |
| B-307 | RBMK manual rod `getColor` NPE from OC | null check |
| B-080 | RBMK surge dial client fallback reads the wrong key | one constant |
| B-083 | GameRuleHelper creates a gamerule named "" | pass the key through |
| B-069 (decide) | RBMK `diag` static flag can corrupt a save | recommended: fix via a dedicated diagnostic write method |
| B-070 (decide) | blocked RBMK streams irradiate the origin column | recommended: fix; radiation lands at the obstruction instead of under the reactor |

Dependencies: B-312 is only testable through ports after Phase 4's B-319, so test it by direct cable contact in this phase and re-test in Phase 4. B-069 and B-070 wait for the owner's answer; the rest can go immediately.

Test: load a world with both reactors; hopper fuel plates into every research-reactor slot and pull waste plates out; shift-click in both GUIs from every slot; drive the research reactor from an OC computer wired directly to the core (`setLevel`, `getInfo`); RBMK auto rods with all three functions, plus `/gamerule` listing showing no rule named ""; save, quit, reload, confirm all state survives.

## Phase 2: crashes and save data

Small, isolated, each independently testable. All are `policy: fix`.

- Crashes: B-002 (potion array size), B-037 (corium fluid icons), B-093 and B-094 (pylon and nodespace NPEs), B-195 (satellite NBT NPE), B-218 (bedrock ore NPE), B-265 (keybind packet bounds), B-267 (BufferUtil short length), B-306 (floppy recipe null), B-308 (artillery target queue).
- Save data: B-003 (contamination effects lost on relog), B-055 (tank fill zeroed without `_max`), B-068 (Zirnox `fire`/`onFire`), B-176 (two entities named `entity_cloud_rainbow`).

Dependencies: B-267 changes the on-wire length prefix, so client and server must run the same fork build (they do; note it in the hand-off). B-176 renames a registry name; both clouds are short-lived visual entities, so no world migration is needed, but do it before any world depends on the fix.

Test: client and server start clean; trigger each path where practical (a Zirnox meltdown, a tank save/reload, relog with an active contamination effect, a keybind packet with an out-of-range value from a test command is not practical, so review the guard instead).

## Phase 3: the shift-click family

Eleven containers with the same off-by-one shape: B-155, B-156, B-157, B-158, B-159, B-160, B-161, B-162, B-164, B-165, B-166, B-167. Each is a one- or two-line bound change; keep them per-file, no shared helper (a helper would be a refactor of upstream files).

Test: open each GUI, shift-click from every tile slot and from the first player slot, both directions.

## Phase 4: OpenComputers wiring

Goal: every HBM component is reachable through multiblock ports and behaves the same from OC-LuaJIT as from stock Lua.

Order matters here:

1. B-319 (decide, recommended: fix) and B-326: normalise the eight lowercase `"opencomputers"` mod ids. Test a ported multiblock (fluid tank, coker) from OC before and after: ports should expose `methods()`.
2. B-321 and B-323: direct `SimpleComponent` on `TileEntityLaunchTable` and `TileEntityWatz`.
3. `methods()`/`invoke()` drift: B-310 (large turbine `getPower`), B-313 (Watz bogus name), B-315 and B-316 (artillery turrets), and Phase 1's B-312 re-tested through a port.
4. B-314 (telex argument probing), B-318 (cable `Colored` interface string), B-327 (doc typo), B-328 (decide, recommended: iterate entries) for the radio torch.
5. B-317: make `PWRangler.lua` 5.2-safe (five `//` sites), then run it under OC-LuaJIT and under stock Lua 5.2.
6. B-311 (decide, recommended: remove the microwave test callbacks).

Leave as documented behaviour unless the owner wants otherwise: B-309 (listed callbacks dispatch direct), B-320 (verify in-game only), B-322 (name reuse), B-329 (cache bypass).

Test: an OC computer per component class, through a port and by direct contact, on OC-LuaJIT and on stock Lua 5.2; `component.list()` names; every listed method callable; PWRangler runs.

## Phase 5: networks, threading, leaks

- B-061: add `super.onChunkUnload()` to the 25 overrides (list in the tracker). Mechanical, 25 files, one line each with a marker.
- B-101: battery node created at the port position but looked up at the tile's own position (REDD ghost nodes).
- B-107 and B-030 (decide, recommended: fix): evict `UniNodespace.worlds` and `TileAccessCache` entries on `WorldEvent.Unload`; same handler, do together.
- B-019: `tryUnsubscribe` uses `createNode()` (no-op).
- B-104: request-network throttle dead code.
- B-212: simple radiation backend unload key.
- B-098 and B-099 together: call `PacketThreading.init()` at startup and fix the inverted validation, so the config keys take effect. B-100 (double `toBytes`). B-095 and B-096 stay as designed unless the owner decides otherwise.
- B-103 (decide, recommended: remove the test RTTY channel), B-105 (decide, recommended: real render bbox for pipelines).

Dependencies: B-061 first; the other net fixes assume `isLoaded` becomes false on unload. Test with cables and pipes across chunk borders: unload and reload chunks, `/ntmreapnetworks` count before and after, `/ntmpackets info` with threading on and off, a long session for memory growth.

## Phase 6: silent misconfiguration and gameplay one-liners

The remaining `policy: fix` items outside the phases above. Group by test surface; several sessions.

- Config and loaders: B-006 (fallout key), B-009 (identity file), B-027 (tool types), B-059 (machine config logging), B-141 (FT_Rocket constructor), B-147 and B-150 (recipe loader), B-204 (IMC tag type), B-206 (hazard key), B-229 (structure default), B-230 and B-236 (trait map and loading), B-237 (biome dictionary), B-275 (unregistered packet guard).
- Blocks and machines: B-053 (beam `findCore` signature), B-050 (placement preview rotation), B-051 (side passed as GUI id, 14 sites), B-056 (upgrade mutex), B-060 (Energy Control key), B-024 (xenon thruster flag), B-072, B-073, B-074 (launcher copy-paste bugs), B-054 (BlockMachineBase early return).
- Items, weapons, entities, hazards: B-110 (armor-mod tooltip loop), B-120 and B-125 (sedna), B-137 (container ids), B-178 and B-179 (entities), B-197 (microblocks meta 15), B-199 (PRISM z origin; only visible with PRISM on), B-201 (satellite miner cargo).
- Client: B-253 (hotbar animation expiry), B-256 (loop sound pitch), B-258 (animation rotmode), B-269 (packet handlers swallow exceptions: log them).
- Utilities: B-270, B-271, B-273, B-028.
- Space: B-223 (Thatmo provider args), B-222 (deterministic terrain noise).

Confirm before doing, even though the tracker says fix (they change what players experience): B-041 (mercury, bromine and chlorine fluids start hurting entities), B-198 (mobs stop being healed on every spawn event), B-221 (sleeping on a planet advances to morning instead of resetting to 0), B-227 (planet structures start generating once the global flag is fixed; pair with B-233).

Test per group: the machines or items named in each entry, plus a client and server start with no new log warnings.

## Phase 7: assets, resources, dead code

Low risk, no gameplay change, mostly deletions and data edits. `no-marker` fixes.

- Sounds: B-288, B-289, B-290, B-291 (wrong or missing event ids), B-296 (missing `grenadeBounce2.ogg`), B-303 (`fm.*` entries).
- Lang and manual: B-292 (23 duplicate keys in `en_US.lang`), B-294 and B-295 (manual pages that never load).
- Client resources: B-257 (45 missing texture paths), B-261 and B-262 (unbound renderers), B-297 (missing `excavator.nbt`), B-300 (corrupt `.obj`).
- Duplicate registrations and dead code: B-151, B-153, B-085, B-034, B-191, B-214, B-217, B-241, B-243, B-279.

Test: client starts with fewer resource warnings than before (count `Unable to` and `not found` lines in the log before and after).

## Decisions needed

Each row groups tracker entries that share one decision. Answer per row; the answer goes into the tracker as a Status and the item into a phase.

| # | Items | Question | Recommendation |
|---|---|---|---|
| D1 | B-001, B-013 | BlockMigrations stub: finish it or delete it? Never fix only one of the two: wiring `buildNumber()` in makes the infinite loop reachable. | Delete both (nothing migrates today). |
| D2 | B-005 | Reset NBT-only player props only on death? | Yes; End-portal return should keep state. |
| D3 | B-008 | Update check points at JameH2's version string, so the fork always reads "outdated". | Point it at the fork's own `RefStrings` on the fork repo, or disable the check. |
| D4 | B-026, B-286, B-287 | CI branches, README maven coordinates, credits string. | Widen CI to `claude/**`; fix README and credits at the next release. |
| D5 | B-020, B-021, B-022, B-023, B-029 | Power and fluid net distribution math (cumulative subtraction across priority tiers, diode clamping, direct-push gate, one-sided purge). | Leave until measured in a creative test world with mixed priorities; changing it changes balance. |
| D6 | B-030, B-107 | Evict per-world caches on unload (leak fix, no gameplay change). | Fix, in Phase 5. |
| D7 | B-042, B-044 | Tooltips lost on 32 vanilla-ItemBlock blocks; all look-overlays gated on the RBMK diagnostic option. | Register `asphalt_stairs`, `steel_grate`, `spike_cacti` with `ItemBlockBase`; give look-overlays their own option. |
| D8 | B-049, B-052 | BlockDummyable static and unsynchronised scratch state. | `try/finally` around `safeRem`; leave `positions` (a refactor). |
| D9 | B-064 | Inert industrial generator in the creative tab. | Hide it (`setCreativeTab(null)` via `DBSItems`-style hook). |
| D10 | B-069, B-070, B-081 | RBMK diagnostic flag, blocked-stream irradiation target, PWR ROR `rods` inversion. | Fix all three (Phase 1); make ROR get/set symmetric on `rodLevel`. |
| D11 | B-077 | Breeding reactor float division. | Fix (Phase 1). |
| D12 | B-076 | Poison-dart trapped brick is placeable but does nothing. | Hide the variant; implement later as a fork feature if wanted. |
| D13 | B-095, B-096 | Packet threading drops queued packets after a 50 ms wait. | Leave (upstream performance design); log when it happens. |
| D14 | B-103, B-105, B-106, B-108 | RTTY test channel, pipeline render bbox, request-network rescans, empty-update nets. | Remove the test channel; fix the pipeline bbox; leave the other two. |
| D15 | B-113 | Cyrillic letter in `item.med_ipecac.desc` (code and three lang files). | Fix all four together. |
| D16 | B-115, B-116 | Unregistered items (californium pellets; schizophrenia pill, PCH, misc ammo). | Leave unless the owner wants californium RBMK fuel; then register with lang, textures and hazards. |
| D17 | B-121 | Belt magazine NBT key not indexed. | Leave; changing it breaks existing guns' NBT. |
| D18 | B-134, B-135, B-136, B-145 | Recipe loader robustness (throws on empty cracking list, NPE on missing JSON fields, no per-handler isolation). | Fix: log and continue, as `DBSRecipes` already does. |
| D19 | B-169, B-170 | NEI drag-and-drop for raw containers; GTNH-NEI handler ordering. | Leave; B-170 only with GTNH NEI installed. |
| D20 | B-177, B-183, B-186, B-189, B-182 | Nuke explosion persistence and config options. | Persist MK5 state (B-177); honour `limitExplosionLifespan` in MK5 (B-186); delete dead options (B-189); leave the ray math and glyphid stats. |
| D21 | B-187 | Siege tunneler has a renderer but is never registered. | Leave (upstream WIP). |
| D22 | B-193, B-200, B-202, B-209, B-211, B-213, B-216 | Atmosphere thread reads, neutron double count, neutron default, satellite colours, PRISM force-load, dead radiation backends and options. | Fix B-200 (halves neutron-activated radiation, currently double-counted) and B-209; delete dead code; leave the rest. |
| D23 | B-224, B-225, B-226, B-231, B-233, B-234, B-239, B-242, B-244 | World generation: meteors on every planet, global water opacity, ore-layer z axis, shared trait instances, missing populate events, Thatmo, Eve cascades, dead layers. | Fix B-231 and B-239; fix B-233 together with B-227; leave B-226 for existing worlds (changes strata); leave B-224, B-225, B-234; delete dead layers. |
| D24 | B-259, B-264 | Unfinished particle engine handler; unreferenced skybox chainloader. | Unregister the handler; delete the chainloader. |
| D25 | B-266, B-272, B-283, B-284 | ArmorUtil assertion, `readNBT` null marker, math helpers, dead classes. | Turn the assertion into a log line; keep `readNBT` behaviour; delete the dead classes. |
| D26 | B-293, B-299, B-304 | Duplicate keys in translations; unreferenced models and sounds. | Script-dedupe translations when next touched; delete assets after a manual sweep (smaller jar). |
| D27 | B-309, B-311, B-319, B-320, B-322, B-328, B-329 | OpenComputers design items. | Fix B-319 and B-328, remove B-311; document B-309 and B-322; verify B-320 in-game; leave B-329. |

## Not planned

- `policy: leave` entries (106) are upstream design or harmless; they stay documented in the tracker only.
- `policy: upstream` entries (11) cannot be fixed cleanly in the fork; if the owner wants, they can be reported to HbmMods as plain bug reports (B-071, B-084, B-114, B-117, B-119, B-124, B-249, B-254, B-255, B-276, B-285). CONTRIBUTING rejects AI-written code, not bug reports.

## Delivery checklist per batch

1. Re-check each item against `hbm/master` for an upstream fix; port it if one exists.
2. Apply the minimal fix with a `// DBS fix B-xxx` marker (or note `no-marker` for assets).
3. Update the tracker: `Status: fixed <hash>` (hash filled in after the owner commits, or the session's staging hash).
4. Run `bash tools/check-fork.sh`.
5. Hand off with the test procedure from the phase above; the owner builds, tests on client and dedicated server, commits, pushes.
