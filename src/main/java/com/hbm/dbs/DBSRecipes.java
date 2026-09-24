package com.hbm.dbs;

import static com.hbm.inventory.OreDictManager.*;

import api.hbm.recipe.IRecipeRegisterListener;

import com.hbm.blocks.ModBlocks;
import com.hbm.inventory.RecipesCommon.ComparableStack;
import com.hbm.inventory.RecipesCommon.OreDictStack;
import com.hbm.inventory.recipes.AssemblyMachineRecipes;
import com.hbm.inventory.recipes.anvil.AnvilRecipes;
import com.hbm.inventory.recipes.anvil.AnvilRecipes.AnvilConstructionRecipe;
import com.hbm.inventory.recipes.anvil.AnvilRecipes.AnvilOutput;
import com.hbm.inventory.recipes.loader.GenericRecipe;
import com.hbm.items.ItemEnums.EnumExpensiveType;
import com.hbm.items.ModItems;
import com.hbm.items.machine.ItemCircuit.EnumCircuitType;
import com.hbm.main.MainRegistry;
import com.hbm.util.CompatExternal;

import net.minecraft.item.ItemStack;

/**
 * Recipes owned by this fork (mindbound/Hbm-s-Nuclear-Tech-DBS). Nothing here touches an upstream
 * recipe class: the fork registers this class as an IRecipeRegisterListener (upstream's own addon
 * hook, see api.hbm.recipe), and SerializableRecipe.initialize() calls onRecipeLoad once per recipe
 * set with the set's simple class name, directly after that set's registerDefaults() and before
 * its template file is written, and only when no live (underscore-less) JSON exists in
 * config/hbmRecipes/. So every recipe below behaves exactly like a default written inline: it is
 * replaced by a live JSON, reloaded by /ntmreload, overridden by server recipe sync whenever the
 * server ships a live JSON for that set, and lands at the end of the set's list. tools/check-fork.sh verifies the wiring after upstream merges.
 */
public class DBSRecipes implements IRecipeRegisterListener {

	/** Called from DBSFork.init() during PreLoad, i.e. before SerializableRecipe.initialize() runs in PostLoad. */
	public static void registerListener() {
		CompatExternal.registerRecipeRegisterListener(new DBSRecipes());
	}

	@Override
	public void onRecipeLoad(String recipeClassName) {
		try {
			if("AssemblyMachineRecipes".equals(recipeClassName)) registerAssembly(AssemblyMachineRecipes.INSTANCE);
			else if("AnvilRecipes".equals(recipeClassName)) registerAnvilConstruction();
		} catch(Exception ex) {
			// SerializableRecipe.initialize() has no per-handler error isolation: an exception here (duplicate recipe id,
			// stack-limit change upstream, ...) would abort every later recipe set and crash PostLoad. Log loudly instead.
			MainRegistry.logger.error("[DBS] Failed to register fork recipes for " + recipeClassName, ex);
		}
	}

	/** Assembler recipes restored from HbmMods 804d2bd1 (reactors) and b8c71e2a (BAT9000). */
	public static void registerAssembly(AssemblyMachineRecipes set) {
		set.register(new GenericRecipe("ass.bat9k").setup(200, 100).outputItems(new ItemStack(ModBlocks.machine_bat9000, 1))
				.inputItems(new OreDictStack(STEEL.plate(), 16), new OreDictStack(ANY_RESISTANTALLOY.plateWelded(), 2), new ComparableStack(ModBlocks.steel_scaffold, 16), new OreDictStack(ANY_TAR.any(), 16))
				.inputItemsEx(new ComparableStack(ModItems.item_expensive, 4, EnumExpensiveType.FERRO_PLATING), new ComparableStack(ModBlocks.steel_scaffold, 16), new OreDictStack(ANY_TAR.any(), 16)));
		set.register(new GenericRecipe("ass.breedingreactor").setup(200, 100).outputItems(new ItemStack(ModBlocks.machine_reactor_breeding, 1))
				.inputItems(new ComparableStack(ModItems.reactor_core, 1), new OreDictStack(STEEL.ingot(), 12), new OreDictStack(PB.plate(), 16), new ComparableStack(ModBlocks.reinforced_glass, 4), new OreDictStack(ASBESTOS.ingot(), 4), new OreDictStack(ANY_RESISTANTALLOY.ingot(), 4), new ComparableStack(ModItems.crt_display, 1))
				.inputItemsEx(new ComparableStack(ModItems.item_expensive, 4, EnumExpensiveType.LEAD_PLATING), new ComparableStack(ModItems.reactor_core, 1), new OreDictStack(ASBESTOS.ingot(), 16), new OreDictStack(ANY_RESISTANTALLOY.ingot(), 4), new ComparableStack(ModItems.crt_display, 1)));
		set.register(new GenericRecipe("ass.researchreactor").setup(200, 100).outputItems(new ItemStack(ModBlocks.reactor_research, 1))
				.inputItems(new OreDictStack(STEEL.ingot(), 8), new OreDictStack(ANY_RESISTANTALLOY.ingot(), 4), new ComparableStack(ModItems.motor_desh, 2), new OreDictStack(B.ingot(), 5), new OreDictStack(PB.plate(), 8), new ComparableStack(ModItems.crt_display, 3), new ComparableStack(ModItems.circuit, 4, EnumCircuitType.BASIC))
				.inputItemsEx(new ComparableStack(ModItems.item_expensive, 4, EnumExpensiveType.FERRO_PLATING), new ComparableStack(ModItems.motor_desh, 2), new ComparableStack(ModItems.crt_display, 3), new ComparableStack(ModItems.item_expensive, 2, EnumExpensiveType.CIRCUIT)));
	}

	/** Anvil construction recipes for the research reactor fuel plates, restored from HbmMods 804d2bd1. */
	public static void registerAnvilConstruction() {
		AnvilRecipes.constructionRecipes.add(new AnvilConstructionRecipe(new ComparableStack(ModItems.ingot_u233, 1), new AnvilOutput(new ItemStack(ModItems.plate_fuel_u233))).setTier(4));
		AnvilRecipes.constructionRecipes.add(new AnvilConstructionRecipe(new ComparableStack(ModItems.ingot_u235, 1), new AnvilOutput(new ItemStack(ModItems.plate_fuel_u235))).setTier(4));
		AnvilRecipes.constructionRecipes.add(new AnvilConstructionRecipe(new ComparableStack(ModItems.ingot_mox_fuel, 1), new AnvilOutput(new ItemStack(ModItems.plate_fuel_mox))).setTier(4));
		AnvilRecipes.constructionRecipes.add(new AnvilConstructionRecipe(new ComparableStack(ModItems.ingot_pu239, 1), new AnvilOutput(new ItemStack(ModItems.plate_fuel_pu239))).setTier(4));
		AnvilRecipes.constructionRecipes.add(new AnvilConstructionRecipe(new ComparableStack(ModItems.ingot_schrabidium, 1), new AnvilOutput(new ItemStack(ModItems.plate_fuel_sa326))).setTier(4));
		AnvilRecipes.constructionRecipes.add(new AnvilConstructionRecipe(new ComparableStack(ModItems.billet_ra226be, 1), new AnvilOutput(new ItemStack(ModItems.plate_fuel_ra226be))).setTier(4));
		AnvilRecipes.constructionRecipes.add(new AnvilConstructionRecipe(new ComparableStack(ModItems.billet_pu238be, 1), new AnvilOutput(new ItemStack(ModItems.plate_fuel_pu238be))).setTier(4));
	}
}
