package com.moneyclickmode;

import java.awt.image.BufferedImage;
import lombok.Getter;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.ui.overlay.infobox.Counter;
import net.runelite.client.util.QuantityFormatter;

class ClickCounter extends Counter
{
	private final String name;

	ClickCounter(Plugin plugin, int count, String name, BufferedImage image)
	{
		super(image, plugin, count);
		this.name = name;
	}

	@Override
	public String getText()
	{
		return QuantityFormatter.quantityToRSDecimalStack(getCount());
	}

	@Override
	public String getTooltip()
	{
		return name;
	}
}
