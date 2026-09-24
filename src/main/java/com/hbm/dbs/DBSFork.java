package com.hbm.dbs;

/**
 * Single entry point for everything this fork (mindbound/Hbm-s-Nuclear-Tech-DBS) adds on top of
 * upstream NTM: Space. It is called from the one "// DBS fork hook" line in MainRegistry.PreLoad,
 * directly after ModItems.mainRegistry(). It must run after ModItems.mainRegistry() (DBSItems needs
 * the items) and before PostLoad (SerializableRecipe.initialize() reads the recipe listeners); keeping
 * it on the very next line keeps any merge conflict local and lets tools/check-fork.sh verify the order.
 *
 * Anything the fork needs that can be done after item/block init belongs here rather than inline
 * in an upstream file; tools/check-fork.sh verifies the hook and everything it depends on.
 */
public class DBSFork {

	public static void init() {
		DBSItems.afterItemInit();
		DBSRecipes.registerListener();
	}
}
