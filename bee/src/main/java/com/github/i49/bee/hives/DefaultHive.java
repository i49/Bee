package com.github.i49.bee.hives;

import com.github.i49.bee.hives.layouts.HostBaseLayout;
import com.github.i49.bee.hives.layouts.Layout;

public class DefaultHive extends AbstractHive {

	@Override
	protected Layout createDefaultLayout() {
		return new HostBaseLayout();
	}
	
	@Override
	protected Storage createDefaultStorage() {
		return new DirectoryStorage();
	}
}
