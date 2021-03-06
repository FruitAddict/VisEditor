/**
 * Copyright 2014-2015 Pawel Pastuszak
 * 
 * This file is part of VisEditor.
 * 
 * VisEditor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * VisEditor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with VisEditor.  If not, see <http://www.gnu.org/licenses/>.
 */

package pl.kotcrab.vis.editor.ui.tab;

import pl.kotcrab.vis.ui.VisTable;
import pl.kotcrab.vis.ui.VisUI;
import pl.kotcrab.vis.ui.widget.VisTextButton;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class TabbedPane {
	private static final Drawable bottomBar = VisUI.skin.getDrawable("list-selection");

	private VisTable tabItems;
	private VisTable mainTable;

	private Array<Tab> tabs;
	private ObjectMap<Tab, VisTextButton> tabsButtonMap;
	private ButtonGroup<Button> group;

	private Tab activeTab;

	private TabbedPaneListener listener;

	public TabbedPane (TabbedPaneListener listener) {
		this.listener = listener;

		group = new ButtonGroup<Button>();

		mainTable = new VisTable();
		tabItems = new VisTable();

		tabs = new Array<Tab>();
		tabsButtonMap = new ObjectMap<Tab, VisTextButton>();

		mainTable.add(tabItems).padTop(2).left().expand();
		mainTable.row();
		mainTable.add(new Image(bottomBar)).expand().fill();
		mainTable.setBackground(VisUI.skin.getDrawable("menu-bg"));
	}

	public void add (Tab tab) {
		tabs.add(tab);
		activeTab = tab;
		rebuildTabsTable();
		listener.switched(tab);
	}

	public void add (int index, Tab tab) {
		tabs.insert(index, tab);
		rebuildTabsTable();
	}

	public boolean remove (Tab tab) {
		boolean success = tabs.removeValue(tab, true);

		if (success) {
			rebuildTabsTable();
			listener.removed(tab);

			if (activeTab == tab) switchTab(0);

			if (tabs.size == 0) listener.removedAll();
		}

		return success;
	}

	public void switchTab (int index) {
		group.getButtons().get(index).setChecked(true);
	}

	public void switchTab (Tab tab) {
		tabsButtonMap.get(tab).setChecked(true);
	}

	private void rebuildTabsTable () {
		tabItems.clear();
		group.clear();
		tabsButtonMap.clear();

		for (final Tab tab : tabs) {
			final VisTextButton button = new VisTextButton(tab.getButtonText(), "toggle");
			button.setFocusBorderEnabled(false);

			tabItems.add(button);
			group.add(button);
			tabsButtonMap.put(tab, button);

			if (tabs.size == 1) {
				button.setChecked(true);
				listener.switched(tab);
			}

			if (tab == activeTab) button.setChecked(true);

			button.addListener(new ChangeListener() {
				@Override
				public void changed (ChangeEvent event, Actor actor) {
					activeTab = tab;
					if (button.isChecked()) listener.switched(tab);
				}
			});
		}
	}

	public Table getTable () {
		return mainTable;
	}
}
