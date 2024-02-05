package com.moneyclickmode;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class MoneyClickModePluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(MoneyClickModePlugin.class);
		RuneLite.main(args);
	}
}
