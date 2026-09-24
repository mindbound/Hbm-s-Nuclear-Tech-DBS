package com.hbm.dbs;

import com.hbm.items.ModItems;
import com.hbm.main.MainRegistry;

/**
 * Item adjustments owned by this fork (mindbound/Hbm-s-Nuclear-Tech-DBS). ModItems itself stays
 * byte-identical to upstream; whatever upstream hides or deprecates is re-enabled here after
 * ModItems.mainRegistry() has run, so that upstream merges never conflict on ModItems and never
 * silently undo these choices. See DBSRecipes for the matching recipes and tools/check-fork.sh.
 */
public class DBSItems {

	/** Called from DBSFork.init(), i.e. from MainRegistry.PreLoad directly after ModItems.mainRegistry(). */
	public static void afterItemInit() {
		// Research reactor fuel plates: craftable in the anvil (DBSRecipes), shown with the other reactor fuel.
		// Upstream constructs them with setCreativeTab(null).
		ModItems.plate_fuel_u233.setCreativeTab(MainRegistry.controlTab);
		ModItems.plate_fuel_u235.setCreativeTab(MainRegistry.controlTab);
		ModItems.plate_fuel_mox.setCreativeTab(MainRegistry.controlTab);
		ModItems.plate_fuel_pu239.setCreativeTab(MainRegistry.controlTab);
		ModItems.plate_fuel_sa326.setCreativeTab(MainRegistry.controlTab);
		ModItems.plate_fuel_ra226be.setCreativeTab(MainRegistry.controlTab);
		ModItems.plate_fuel_pu238be.setCreativeTab(MainRegistry.controlTab);

		// Depleted plates: upstream hides them from the creative menu (HbmMods 13b8efef, not yet merged); keep them in the parts tab.
		ModItems.waste_plate_u233.setCreativeTab(MainRegistry.partsTab);
		ModItems.waste_plate_u235.setCreativeTab(MainRegistry.partsTab);
		ModItems.waste_plate_mox.setCreativeTab(MainRegistry.partsTab);
		ModItems.waste_plate_pu239.setCreativeTab(MainRegistry.partsTab);
		ModItems.waste_plate_sa326.setCreativeTab(MainRegistry.partsTab);
		ModItems.waste_plate_ra226be.setCreativeTab(MainRegistry.partsTab);
		ModItems.waste_plate_pu238be.setCreativeTab(MainRegistry.partsTab);
	}
}
