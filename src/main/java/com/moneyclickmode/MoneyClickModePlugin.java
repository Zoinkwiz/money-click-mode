package com.moneyclickmode;

import com.google.inject.Provides;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.inject.Inject;
import javax.swing.SwingUtilities;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.ItemID;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.input.MouseListener;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.input.MouseManager;

@Slf4j
@PluginDescriptor(
	name = "Money Click Mode"
)
public class MoneyClickModePlugin extends Plugin implements MouseListener
{
	@Inject
	private Client client;

	@Inject
	private MoneyClickModeConfig config;

	@Inject
	ConfigManager configManager;

	@Inject
	InfoBoxManager infoBoxManager;

	@Inject
	ItemManager itemManager;

	@Inject
	private MouseManager mouseManager;

	int[] coinQuantities = new int[] { 1, 2, 3, 4, 5, 25, 100, 25, 1000, 10000 };
	int currentCoinIndex = 0;

	@Override
	protected void startUp() throws Exception
	{
		mouseManager.registerMouseListener(this);
	}

	@Override
	protected void shutDown() throws Exception
	{
		mouseManager.unregisterMouseListener(this);
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
		{
			onLogin();
		}
	}

	@Provides
	MoneyClickModeConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(MoneyClickModeConfig.class);
	}

	final String CONFIG_GROUP = "money-mode";
	final String CONFIG_MONEY_SPENT_KEY = "spent";
	final String CONFIG_COFFER_KEY = "coffer";
	final int COFFER_VARP = 261;

	int cofferMoney = -1;
	int spentMoneyFromCoffer = 0;

	private ClickCounter clickCounter;

	public void onLogin()
	{
		try
		{
			cofferMoney = Integer.parseInt(configManager.getRSProfileConfiguration(CONFIG_GROUP, CONFIG_COFFER_KEY));
		}
		catch (NumberFormatException error)
		{
			cofferMoney = 0;
			configManager.setRSProfileConfiguration(CONFIG_GROUP, CONFIG_COFFER_KEY, 0);
		}

		try
		{
			spentMoneyFromCoffer = Integer.parseInt(configManager.getRSProfileConfiguration(CONFIG_GROUP, CONFIG_MONEY_SPENT_KEY));
		}
		catch (NumberFormatException error)
		{
			spentMoneyFromCoffer = 0;
			configManager.setRSProfileConfiguration(CONFIG_GROUP, CONFIG_MONEY_SPENT_KEY, 0);
		}
		updateInfobox();
	}

	private int getRemainingMoney()
	{
		return cofferMoney - spentMoneyFromCoffer;
	}

	private void updateInfobox()
	{
		if (clickCounter != null)
		{
			clickCounter.setCount(getRemainingMoney());
			return;
		}

		removeInfobox();
		final BufferedImage image = itemManager.getImage(ItemID.COINS_995, getRemainingMoney(), false);
		clickCounter = new ClickCounter(this, getRemainingMoney(), "Remaining money", image);
		infoBoxManager.addInfoBox(clickCounter);
	}

	private void removeInfobox()
	{
		infoBoxManager.removeInfoBox(clickCounter);
		clickCounter = null;
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged varbitChanged)
	{
		if (varbitChanged.getVarpId() == COFFER_VARP)
		{
			configManager.setRSProfileConfiguration(CONFIG_GROUP, "coffer", varbitChanged.getValue());
			cofferMoney = varbitChanged.getValue();
		}
	}

	@Override
	public MouseEvent mouseClicked(MouseEvent mouseEvent)
	{
		if (client == null || configManager == null || client.getGameState() != GameState.LOGGED_IN) return mouseEvent;
		if (!SwingUtilities.isLeftMouseButton(mouseEvent)) return mouseEvent;

		spentMoneyFromCoffer += 1000;
		configManager.setRSProfileConfiguration(CONFIG_GROUP, CONFIG_MONEY_SPENT_KEY, spentMoneyFromCoffer);
		System.out.println("I have now spent " + getRemainingMoney());
		updateInfobox();
		if (getRemainingMoney() < 0)
		{
			System.out.println("BLOCKED");

			return mouseEvent;
		}

		return mouseEvent;
	}
	@Override
	public MouseEvent mousePressed(MouseEvent mouseEvent)
	{
		return mouseEvent;
	}

	@Override
	public MouseEvent mouseReleased(MouseEvent mouseEvent)
	{
		return mouseEvent;
	}

	@Override
	public MouseEvent mouseEntered(MouseEvent mouseEvent)
	{
		return mouseEvent;
	}

	@Override
	public MouseEvent mouseExited(MouseEvent mouseEvent)
	{
		return mouseEvent;
	}

	@Override
	public MouseEvent mouseDragged(MouseEvent mouseEvent)
	{
		return mouseEvent;
	}

	@Override
	public MouseEvent mouseMoved(MouseEvent mouseEvent)
	{
		return mouseEvent;
	}
}
