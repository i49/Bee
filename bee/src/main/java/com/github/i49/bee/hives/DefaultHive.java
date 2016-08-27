package com.github.i49.bee.hives;

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
