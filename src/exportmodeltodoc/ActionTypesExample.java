/*
 *
 * Copyright (c) 2002 NoMagic, Inc. All Rights Reserved.
 */
package exportmodeltodoc;

import com.nomagic.actions.AMConfigurator;
import com.nomagic.actions.ActionsCategory;
import com.nomagic.actions.ActionsManager;
import com.nomagic.actions.NMAction;
import com.nomagic.magicdraw.actions.ActionsConfiguratorsManager;
import com.nomagic.magicdraw.actions.BrowserContextAMConfigurator;
import com.nomagic.magicdraw.actions.MDActionsCategory;
import com.nomagic.magicdraw.plugins.Plugin;
import com.nomagic.magicdraw.ui.browser.Tree;

import exportmodeltodoc.action.ExportDocAction;

/**
 * Example shows how to create more complicated GUI components from actions.
 *
 * @author Donatas Simkunas
 */
public class ActionTypesExample extends Plugin {

	/**
	 * @see com.nomagic.magicdraw.plugins.Plugin#init()
	 */
	@Override
	public void init() {
		ActionsConfiguratorsManager manager = ActionsConfiguratorsManager.getInstance();
		// adding sub-menu
//		manager.addMainMenuConfigurator(new MainMenuConfigurator(getSubMenuActions()));
		// adding actions with separator
//		manager.addMainMenuConfigurator(new MainMenuConfigurator(getSeparatedActions()));
		// adding check box menu item
//		manager.addMainMenuConfigurator(new MainMenuConfigurator(getStateAction()));
		// adding radio button menu items
//		manager.addMainMenuConfigurator(new MainMenuConfigurator(getGroupedStateAction()));

		ExportDocAction export = new ExportDocAction();

		BrowserContextAMConfigurator brCfg = new BrowserContextAMConfigurator() {
			// Implement configuration.
			// Add or remove some actions in ActionsManager.
			// A tree is passed as an argument, provides ability to access nodes.
			public void configure(ActionsManager mngr, Tree browser) {
				// Actions must be added into some category.
				// So create the new one, or add an action into the existing category.
				MDActionsCategory category = new MDActionsCategory("", "");
				category.addAction(export);

				// Add a category into the manager.
				// A category isn't displayed in a shortcut menu.
				mngr.addCategory(category);
			}

			public int getPriority() {
				return AMConfigurator.MEDIUM_PRIORITY;
			}
		};

		manager.addContainmentBrowserContextConfigurator(brCfg);
		manager.addDiagramsBrowserContextConfigurator(brCfg);
	}

	/**
	 * @see com.nomagic.magicdraw.plugins.Plugin#close()
	 */
	@Override
	public boolean close() {
		return true;
	}

	/**
	 * @see com.nomagic.magicdraw.plugins.Plugin#isSupported()
	 */
	@Override
	public boolean isSupported() {
		return true;
	}

	/**
	 * Creates group of actions. This group is separated from others using menu
	 * separator (when it represented in menu). Separator is added for group of
	 * actions in one actions category.
	 */
	private static NMAction getSeparatedActions() {
		ActionsCategory category = new ActionsCategory();

		return category;
	}

	/**
	 * Creates action which is sub-menu (when it represented in menu). Separator is
	 * added for group of actions in actions category.
	 */
	private static NMAction getSubMenuActions() {
		ActionsCategory category = new ActionsCategory(null, "SubMenu");
		// this call makes sub-menu.
		category.setNested(true);

		return category;
	}

	/**
	 * Creates action which is represented by JCheckBoxMenuItem.
	 */

	/**
	 * @return action which represents state action groups. It is represented in
	 *         menu by JRadioButtonMenuItem.
	 */

}
