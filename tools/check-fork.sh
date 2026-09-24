#!/usr/bin/env bash
# tools/check-fork.sh -- verify that this fork's restored content survived an upstream merge.
#
# Run from anywhere after every merge from JameH2 or HbmMods, before building:
#   bash tools/check-fork.sh
# Exit status 0 = everything present, 1 = at least one MISS line (each one names what to fix).
#
# The fork keeps its own code in src/main/java/com/hbm/dbs/ and touches upstream files only with
# two single lines marked "// DBS fork hook" (MainRegistry, NEIRegistry); recipes arrive through
# upstream's IRecipeRegisterListener addon hook. The tree is all-LF (.gitattributes enforces it). This script checks those files, the hooks, the restored
# recipes, and every upstream class or registration the restores depend on, so that a merge which
# silently removes something (as HbmMods did with MachineTurbine) is caught before the build.

set -u
cd "$(dirname "$0")/.." || exit 2
J=src/main/java/com/hbm
L=src/main/resources/assets/hbm/lang
fail=0

ok()   { printf 'ok    %s\n' "$1"; }
miss() { printf 'MISS  %s\n' "$1"; fail=1; }
warn() { printf 'note  %s\n' "$1"; }

need_file()  { if [ -f "$1" ]; then ok "$2"; else miss "$2 ($1 not found)"; fi; }
need_class() { if grep -rl --include="$1.java" -e "" src/main/java | grep -q .; then ok "class $1"; else miss "class $1 (no $1.java under src/main/java)"; fi; }
need_grep()  { if grep -qF -- "$1" "$2" 2>/dev/null; then ok "$3"; else miss "$3 (expected '$1' in $2)"; fi; }
need_egrep() { if grep -qE -- "$1" "$2" 2>/dev/null; then ok "$3"; else miss "$3 (expected /$1/ in $2)"; fi; }
need_count() { local n; n=$(grep -cF -- "$1" "$2" 2>/dev/null); n=${n:-0}; if [ "$n" = "$3" ]; then ok "$4"; else miss "$4 (found $n of $3 '$1' in $2)"; fi; }
absent_egrep() { if grep -qE -- "$1" "$2" 2>/dev/null; then miss "$3 (found /$1/ in $2)"; else ok "$3"; fi; }

echo "== merge state"
# Only the labelled markers are checked: upstream's it_IT.lang carries a bare "=======" line (L546) that is not a conflict.
if grep -rlE '^(<{7}|>{7})( |\r?$)' src/main/java src/main/resources/assets/hbm/lang tools CLAUDE.md docs 2>/dev/null | grep -q .; then
	miss "unresolved merge conflict markers present (git diff --check; grep -rn '^<<<<<<<' src)"
else
	ok "no merge conflict markers"
fi

echo "== line endings (all-LF policy since 27811a59)"
need_egrep '^\*\.java[[:space:]]+text[[:space:]]+eol=lf' .gitattributes "gitattributes declares *.java text eol=lf"
n=$(git ls-files --eol | grep -cE 'i/(crlf|mixed)')
if [ "${n:-0}" -eq 0 ]; then ok "no CRLF or mixed-ending file in the index"; else miss "$n file(s) with CRLF/mixed endings in the index (git ls-files --eol | grep -E 'i/(crlf|mixed)'); merge upstream with -Xrenormalize"; fi
if [ "$(git config --get merge.renormalize)" = "true" ]; then ok "merge.renormalize is set in this clone"; else warn "merge.renormalize is not set in this clone: run 'git config merge.renormalize true' or pass -Xrenormalize on every upstream merge"; fi

echo "== fork-owned files"
need_file "$J/dbs/DBSFork.java" "DBSFork"
need_file "$J/dbs/DBSRecipes.java" "DBSRecipes"
need_file "$J/dbs/DBSItems.java" "DBSItems"
need_file "$J/handler/nei/BreederRecipeHandler.java" "BreederRecipeHandler"

echo "== hook lines in upstream files"
need_egrep '^[[:space:]]*com\.hbm\.dbs\.DBSFork\.init\(\);' "$J/main/MainRegistry.java" "MainRegistry hook line (not commented out)"
a=$(grep -nE '^[[:space:]]*ModItems\.mainRegistry\(\);' "$J/main/MainRegistry.java" | head -1 | cut -d: -f1)
b=$(grep -nE '^[[:space:]]*com\.hbm\.dbs\.DBSFork\.init\(\);' "$J/main/MainRegistry.java" | head -1 | cut -d: -f1)
if [ -n "$a" ] && [ -n "$b" ] && [ "$b" -gt "$a" ]; then ok "hook runs after ModItems.mainRegistry() (lines $a -> $b)"; else miss "hook must come after ModItems.mainRegistry() in MainRegistry.PreLoad (found lines '$a' and '$b')"; fi
need_egrep '^[[:space:]]*handlers\.add\(new BreederRecipeHandler\(\)\);' "$J/main/NEIRegistry.java" "NEI handler registration (not commented out)"

echo "== recipe listener wiring (upstream addon API)"
need_grep "implements IRecipeRegisterListener" "$J/dbs/DBSRecipes.java" "DBSRecipes is an IRecipeRegisterListener"
need_grep "CompatExternal.registerRecipeRegisterListener(" "$J/dbs/DBSRecipes.java" "DBSRecipes registers itself"
need_grep "public static void registerRecipeRegisterListener(IRecipeRegisterListener listener)" "$J/util/CompatExternal.java" "CompatExternal.registerRecipeRegisterListener present"
need_grep "listener.onRecipeLoad(recipe.getClass().getSimpleName())" "$J/inventory/recipes/loader/SerializableRecipe.java" "initialize() dispatches listeners by simple class name"
need_grep '"AssemblyMachineRecipes"' "$J/dbs/DBSRecipes.java" "listener branch for AssemblyMachineRecipes"
need_class AssemblyMachineRecipes
need_grep "public static final AssemblyMachineRecipes INSTANCE" "$J/inventory/recipes/AssemblyMachineRecipes.java" "AssemblyMachineRecipes.INSTANCE"
need_egrep '^[[:space:]]*recipeHandlers\.add\(AssemblyMachineRecipes\.INSTANCE\)' "$J/inventory/recipes/loader/SerializableRecipe.java" "assembler set registered as INSTANCE"
need_grep '"AnvilRecipes"' "$J/dbs/DBSRecipes.java" "listener branch for AnvilRecipes"
need_class AnvilRecipes
need_egrep '^[[:space:]]*recipeHandlers\.add\(new AnvilRecipes\(\)\)' "$J/inventory/recipes/loader/SerializableRecipe.java" "anvil set registered"

echo "== restored recipes (defined once, in DBSRecipes only)"
for r in ass.bat9k ass.breedingreactor ass.researchreactor; do
	need_grep "\"$r\"" "$J/dbs/DBSRecipes.java" "assembly recipe $r"
	n=$(grep -rlF --exclude-dir=dbs -- "\"$r\"" src/main/java | wc -l)
	if [ "$n" -eq 0 ]; then ok "$r defined nowhere else (a duplicate id would throw at PostLoad)"; else miss "$r also defined outside com/hbm/dbs ($n file(s)); GenericRecipes.register throws on duplicate ids"; fi
done
need_count "AnvilOutput(new ItemStack(ModItems.plate_fuel_" "$J/dbs/DBSRecipes.java" 7 "seven fuel-plate anvil recipes"
n=$(grep -rlF --exclude-dir=dbs -- "AnvilOutput(new ItemStack(ModItems.plate_fuel_" src/main/java | wc -l)
if [ "$n" -eq 0 ]; then ok "fuel-plate anvil recipes defined nowhere else"; else miss "fuel-plate anvil recipes also defined outside com/hbm/dbs ($n file(s))"; fi

echo "== upstream API the fork code calls"
need_grep "public void register(T recipe)" "$J/inventory/recipes/loader/GenericRecipes.java" "GenericRecipes.register is public"
need_grep "public static List<AnvilConstructionRecipe> constructionRecipes" "$J/inventory/recipes/anvil/AnvilRecipes.java" "AnvilRecipes.constructionRecipes is public"
need_grep "public static CreativeTabs controlTab" "$J/main/MainRegistry.java" "MainRegistry.controlTab"
need_grep "public static CreativeTabs partsTab" "$J/main/MainRegistry.java" "MainRegistry.partsTab"

echo "== upstream classes the restores depend on"
for c in MachineReactorBreeding TileEntityMachineReactorBreeding ContainerMachineReactorBreeding GUIMachineReactorBreeding RenderBreeder \
         ReactorResearch TileEntityReactorResearch ContainerReactorResearch GUIReactorResearch RenderSmallReactor \
         MachineBigAssTank9000 TileEntityMachineBAT9000 RenderBAT9000 \
         BreederRecipes FuelPoolRecipes; do
	need_class "$c"
done

echo "== registrations"
need_egrep '^[[:space:]]*recipeHandlers\.add\(new BreederRecipes\(\)\)' "$J/inventory/recipes/loader/SerializableRecipe.java" "BreederRecipes JSON handler registered"
need_egrep '^[[:space:]]*recipeHandlers\.add\(new FuelPoolRecipes\(\)\)' "$J/inventory/recipes/loader/SerializableRecipe.java" "FuelPoolRecipes JSON handler registered"
need_egrep '^[[:space:]]*put\(TileEntityMachineReactorBreeding\.class' "$J/tileentity/TileMappings.java" "TileMappings breeding reactor"
need_egrep '^[[:space:]]*put\(TileEntityReactorResearch\.class' "$J/tileentity/TileMappings.java" "TileMappings research reactor"
need_egrep '^[[:space:]]*put\(TileEntityMachineBAT9000\.class' "$J/tileentity/TileMappings.java" "TileMappings BAT9000"
need_egrep '^[[:space:]]*ClientRegistry\.bindTileEntitySpecialRenderer\(TileEntityMachineReactorBreeding\.class' "$J/main/ClientProxy.java" "TESR breeding reactor"
need_egrep '^[[:space:]]*ClientRegistry\.bindTileEntitySpecialRenderer\(TileEntityReactorResearch\.class' "$J/main/ClientProxy.java" "TESR research reactor"
need_egrep '^[[:space:]]*ClientRegistry\.bindTileEntitySpecialRenderer\(TileEntityMachineBAT9000\.class' "$J/main/ClientProxy.java" "TESR BAT9000"
for b in machine_reactor_breeding reactor_research machine_bat9000; do
	need_egrep "^[[:space:]]*$b = new " "$J/blocks/ModBlocks.java" "ModBlocks.$b constructed"
	need_egrep "^[[:space:]]*(GameRegistry\.)?register(Block)?\($b[,)]" "$J/blocks/ModBlocks.java" "ModBlocks.$b registered"
done
for i in plate_fuel_u233 plate_fuel_u235 plate_fuel_mox plate_fuel_pu239 plate_fuel_sa326 plate_fuel_ra226be plate_fuel_pu238be \
         waste_plate_u233 waste_plate_u235 waste_plate_mox waste_plate_pu239 waste_plate_sa326 waste_plate_ra226be waste_plate_pu238be \
         ingot_u233 ingot_u235 ingot_mox_fuel ingot_pu239 ingot_schrabidium billet_ra226be billet_pu238be reactor_core crt_display motor_desh; do
	need_egrep "^[[:space:]]*$i = new " "$J/items/ModItems.java" "ModItems.$i constructed"
done
for i in plate_fuel_u233 plate_fuel_u235 plate_fuel_mox plate_fuel_pu239 plate_fuel_sa326 plate_fuel_ra226be plate_fuel_pu238be; do
	need_egrep "^[[:space:]]*GameRegistry\.registerItem\($i[,)]" "$J/items/ModItems.java" "ModItems.$i registered"
done

echo "== lang: restored machines carry no LEGACY marker"
for f in en_US de_DE; do
	need_egrep "^tile\.machine_reactor\.name=" "$L/$f.lang" "$f tile.machine_reactor.name present"
	need_egrep "^tile\.machine_reactor_small\.name=" "$L/$f.lang" "$f tile.machine_reactor_small.name present"
	absent_egrep "^tile\.machine_reactor(_small)?\.name=.*LEGACY" "$L/$f.lang" "$f reactors not marked LEGACY"
done

echo
if [ "$fail" -eq 0 ]; then
	echo "check-fork: everything present"
else
	echo "check-fork: MISS lines above must be fixed before building"
fi
exit "$fail"
