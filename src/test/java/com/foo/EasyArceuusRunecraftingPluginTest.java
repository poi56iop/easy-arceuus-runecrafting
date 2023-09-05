package com.foo;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class EasyArceuusRunecraftingPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(EasyArceuusRunecraftingPlugin.class);
		RuneLite.main(args);
	}
}