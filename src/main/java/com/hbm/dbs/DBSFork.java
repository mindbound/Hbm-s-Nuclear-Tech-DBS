package com.hbm.dbs;

/**
 * Single entry point for everything this fork (mindbound/Hbm-s-Nuclear-Tech-DBS) adds on top of
 * upstream NTM: Space. It is called from the one "// DBS fork hook" line in MainRegistry.PreLoad,
 * directly after ModItems.mainRegistry(). It must run after ModItems.mainRegistry() (DBSItems needs
 * the items) and before PostLoad (SerializableRecipe.initialize() reads the recipe listeners); keeping
 * it on the very next line keeps any merge conflict local and lets tools/check-fork.sh verify the order.
 *
 * Fork code that needs a hook after item/block init can be called from here (inline edits in upstream
 * files are allowed too; see the edit policy in CLAUDE.md). This hook runs before CraftingManager,
 * HazardRegistry, OreDictManager, TE registration and all of PostLoad (docs/extending.md, section 2).
 * tools/check-fork.sh verifies the hook and everything it depends on.
 */
public class DBSFork {

	public static void init() {
		DBSItems.afterItemInit();
		DBSRecipes.registerListener();
	}
}
