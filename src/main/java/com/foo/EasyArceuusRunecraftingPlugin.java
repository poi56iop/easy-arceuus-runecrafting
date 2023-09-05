package com.foo;

import com.google.inject.Provides;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static net.runelite.api.AnimationID.IDLE;

// TODO instead of highlighting specific floor tiles, highlight some at the edge of the rendered range towards the goal location.
// TODO highlight tiles on the way to soul altar etc.

@Slf4j
@PluginDescriptor(
	name = "Easy Arceuus Runecrafting"
)
public class EasyArceuusRunecraftingPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private EasyArceuusRunecraftingConfig config;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private NextActionOverlay nextActionOverlay;

	@Inject
	private NextItemOverlay nextItemOverlay;
	@Inject
	private TintOverlay tintOverlay;

	@Getter(AccessLevel.PACKAGE)
	private TileObject nextActionClick;

	@Getter(AccessLevel.PACKAGE)
	private int nextItemClick;

	@Getter(AccessLevel.PACKAGE)
	private int nextOtherClick; // TODO do this way better lol

	private Instant lastActive = Instant.now();

	@Getter(AccessLevel.PACKAGE)
	private boolean playerInArea;

	private boolean inventoryFull;

	private boolean heldEssenceDense;

	private boolean heldEssenceDark;

	private boolean heldEssenceFragments;
	private boolean heldOnlyInactiveBloodEssence;
	private boolean heldMaxEssenceFragments = false; // TODO implement this. Doesn't use item quantity or bits that show up with the var inspector.

	@Getter(AccessLevel.PACKAGE)
	private boolean idle = true; // TODO make different levels of idle for different durations.

	@Getter(AccessLevel.PACKAGE)
	private boolean disableTintForLevel = false;

	private static final Set<Integer> chunks = new HashSet<>(Arrays.asList(6460, 6716, 6972, 7228, 6715, 6971));

	private static final int RUNESTONE_SOUTH_ID = NullObjectID.NULL_10796;
	private static final int RUNESTONE_NORTH_ID = NullObjectID.NULL_8981;

	private TileObject runestoneSouth;

	private TileObject runestoneNorth;

	private TileObject altarDark;

	private TileObject altarBlood;

	private TileObject altarSoul;

	private TileObject shortcut73; // West of runestones. (Return from Blood Altar.)

	private TileObject shortcut69; // North of runestones. (Return to/from Dark Altar.)

	private TileObject shortcut52Inner; // East of runestones; Western half.

	private TileObject shortcut52Outer; // East of runestones; Eastern half.

	private TileObject shortcut49; // North of runestones, farther. (Return from Soul Altar.)


	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(nextActionOverlay);
		overlayManager.add(nextItemOverlay);
		overlayManager.add(tintOverlay);
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (event.getGroup().equals("easyarceuusrunecrafting"))
		{
			// TODO add constraints to flash times?
		}
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(nextActionOverlay);
		overlayManager.remove(nextItemOverlay);
		overlayManager.remove(tintOverlay);
		runestoneNorth = null;
		runestoneSouth = null;
		altarDark = null;
		altarBlood = null;
		altarSoul = null;
		shortcut73 = null;
		shortcut69 = null;
		shortcut52Outer = null;
		shortcut52Inner = null;
		shortcut49 = null;
		nextItemClick = -2;
		nextOtherClick = 0;
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		GameState gameState = event.getGameState();
		switch (gameState)
		{
			case LOADING:
				runestoneNorth = null;
				runestoneSouth = null;
				altarDark = null;
				altarBlood = null;
				altarSoul = null;
				shortcut73 = null;
				shortcut69 = null;
				shortcut52Outer = null;
				shortcut52Inner = null;
				shortcut49 = null;
				nextActionClick = null;
				break;
			case CONNECTION_LOST:
			case HOPPING:
			case LOGIN_SCREEN:
				break;
		}
	}

	@Provides
	EasyArceuusRunecraftingConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(EasyArceuusRunecraftingConfig.class);
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		if (!playerInArea) {return;}
		// TODO need to run this on plugin start too?
		if (event.getItemContainer() == client.getItemContainer(InventoryID.INVENTORY))
		{
			Item[] items = event.getItemContainer().getItems();
			if (client.getWidget(WidgetInfo.BANK_CONTAINER) == null)
			{
				boolean hasEssenceDense = false;
				boolean hasEssenceDark = false;
				boolean hasEssenceFragments = false;
				boolean hasBloodEssenceInactive = false;
				boolean hasBloodEssenceActive = false;
				boolean hasSpace = false;

				for (Item i : items) {
					switch (i.getId()) {
						case ItemID.DENSE_ESSENCE_BLOCK:
							hasEssenceDense = true;
							break;
						case ItemID.DARK_ESSENCE_BLOCK:
							hasEssenceDark = true;
							break;
						case ItemID.DARK_ESSENCE_FRAGMENTS:
							hasEssenceFragments = true;
							break;
						case ItemID.BLOOD_ESSENCE:
							hasBloodEssenceInactive = true;
							break;
						case ItemID.BLOOD_ESSENCE_ACTIVE:
							hasBloodEssenceActive = true;
							break;
						case -1:
							hasSpace = true;
							break;
					}
				}
				heldEssenceDense = hasEssenceDense;
				heldEssenceDark = hasEssenceDark;
				heldEssenceFragments = hasEssenceFragments;
				heldOnlyInactiveBloodEssence = (hasBloodEssenceInactive && !hasBloodEssenceActive);
				inventoryFull = !hasSpace;

				if (items.length < 28) {hasSpace = true;} // NOTE: this can happen after logging in when there's nothing in the last inventory slot.
			}
		}
	}

	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned event)
	{
		onTileObject(null, event.getGameObject());
	}

	@Subscribe
	public void onGameObjectDespawned(GameObjectDespawned event)
	{
		onTileObject(event.getGameObject(), null);
	}

	@Subscribe
	public void onGroundObjectSpawned(GroundObjectSpawned event)
	{
		onTileObject(null, event.getGroundObject());
	}

	@Subscribe
	public void onGroundObjectDespawned(GroundObjectDespawned event)
	{
		onTileObject(event.getGroundObject(), null);
	}

	@Subscribe
	public void onWallObjectSpawned(WallObjectSpawned event)
	{
		onTileObject(null, event.getWallObject());
	}

	@Subscribe
	public void onWallObjectDespawned(WallObjectDespawned event)
	{
		onTileObject(event.getWallObject(), null);
	}

	@Subscribe
	public void onDecorativeObjectSpawned(DecorativeObjectSpawned event)
	{
		onTileObject(null, event.getDecorativeObject());
	}

	@Subscribe
	public void onDecorativeObjectDespawned(DecorativeObjectDespawned event)
	{
		onTileObject(event.getDecorativeObject(), null);
	}

	private void onTileObject(TileObject oldObject, TileObject newObject)
	{
		if (!playerInArea) {return;}
		int id;
		if (oldObject == null) {
			id = newObject.getId();
		} else {
			id = oldObject.getId();
		}

		switch (id)
		{
			case RUNESTONE_SOUTH_ID:
				runestoneSouth = newObject;
				break;
			case RUNESTONE_NORTH_ID:
				runestoneNorth = newObject;
				break;
			case ObjectID.DARK_ALTAR:
				altarDark = newObject;
				break;
			case ObjectID.BLOOD_ALTAR:
				altarBlood = newObject;
				break;
			case ObjectID.SOUL_ALTAR:
				altarSoul = newObject;
				break;
			case ObjectID.ROCKS_27984:
				shortcut73 = newObject;
				break;
			case ObjectID.ROCKS_34741:
				shortcut69 = newObject;
				break;
			case ObjectID.ROCKS_27987:
				shortcut52Outer = newObject;
				break;
			case ObjectID.ROCKS_27988:
				shortcut52Inner = newObject;
				break;
			case ObjectID.BOULDER_27990:
				shortcut49 = newObject;
				break;
		}
	}



	@Subscribe
	public void onClientTick(ClientTick tick) {
		if (!playerInArea) {return;}
		Player localPlayer = client.getLocalPlayer();
		int idlePose = localPlayer.getIdlePoseAnimation();
		int pose = localPlayer.getPoseAnimation();
		int animation = localPlayer.getAnimation();

		if (!(animation == IDLE && pose == idlePose)) {
			lastActive = Instant.now();
			idle = false;
			return;
		}

		final Duration alarmDelay = Duration.ofMillis(300);

		if (Instant.now().compareTo(lastActive.plus(alarmDelay)) >= 0) {
			idle = true;
		} else {
			idle = false; // TODO needed?
		}
	}
	@Subscribe
	public void onGameTick(GameTick event)
	{
		if (checkPlayerInArea()) {
			disableTintForLevel = (client.getWidget(WidgetInfo.LEVEL_UP_LEVEL) != null);
			findNextAction();
		}
	}

	private boolean checkPlayerInArea() {
		playerInArea = chunks.contains(client.getLocalPlayer().getWorldLocation().getRegionID());
		return playerInArea;
	}

	private void findNextAction() {
		nextItemClick = -2;
		nextOtherClick = 0;
		nextActionClick = null;

		if (!idle) {
			return;
		}

		WorldPoint ploc = client.getLocalPlayer().getWorldLocation();

		boolean runEnabled = client.getVarpValue(173)==1;
		if (!runEnabled) {
			if (config.suggestRun()) {
				if (client.getEnergy() > 50*100) {
					if (client.getWidget(WidgetInfo.MINIMAP_TOGGLE_RUN_ORB) != null) {
						nextOtherClick = 3;
						return;
					}
				}
			}
		}

		if (heldOnlyInactiveBloodEssence) {
			nextItemClick = ItemID.BLOOD_ESSENCE;
			return;
		}

		if (inventoryFull) {
			if (heldEssenceDense) {
				nextDarkAltar(ploc);
			} else if (heldEssenceDark) {
				if (heldEssenceFragments) {
					nextRuneAltar(ploc);
				} else {
					nextChiseling();
				}
			}
		} else {
			if (heldEssenceDark) {
				if (heldMaxEssenceFragments) {
					nextRuneAltar(ploc);
				} else {
					nextChiseling();
				}
			} else {
				if (heldEssenceFragments && playerNearbyTo(ploc, altarChosen(), 15)) {
					nextActionClick = altarChosen();
				} else {
					nextMining(ploc);
				}
			}
		}
	}

	private TileObject altarChosen() {
		if (config.whichRunes().isBlood()) {
			return altarBlood;
		}
		return altarSoul;
	}

	private boolean playerNearbyTo(WorldPoint ploc, TileObject object, int maxDistance) {
		if (object == null) {return false;}
		return ploc.distanceTo(object.getWorldLocation()) <= maxDistance;
	}

	private void nextChiseling() {
		Widget selected = client.getSelectedWidget();
		int id = (selected == null) ? 0 : selected.getItemId();
		switch (id) {
			case ItemID.CHISEL:
				nextItemClick = ItemID.DARK_ESSENCE_BLOCK;
				break;
			case ItemID.DARK_ESSENCE_BLOCK:
			default:
				nextItemClick = ItemID.CHISEL;
				break;
		}
	}

	private void nextDarkAltar(WorldPoint ploc) {
		if ((ploc.getX()<1753 && ploc.getY()>3867) || (ploc.getX()<1770 && ploc.getY()>3872)) {
			nextActionClick = altarDark;
		} else {
			if (client.getBoostedSkillLevel(Skill.AGILITY)>=69) {
				nextActionClick = shortcut69;
			} else if (client.getBoostedSkillLevel(Skill.AGILITY)>=52 && ploc.getX()<1770) {
				nextActionClick = shortcut52Inner;
			} else {
				nextOtherClick = 2;
			}
		}
	}

	private void nextRuneAltar(WorldPoint ploc) {
		// TODO setup shortcuts and souls?
		if (config.whichRunes().isBlood()) {
			if ((ploc.getX()<1720 || ploc.getY()>3867) && !(ploc.getY()<3836)) {
				nextOtherClick = 1;
			} else {
				nextActionClick = altarBlood;
			}
		} else {
			nextActionClick = altarSoul;
		}
	}

	private void nextMining(WorldPoint ploc) {
		if (ploc.getX()<1743 && ploc.getY()<3860) {
			//returning from blood altar
			if (client.getBoostedSkillLevel(Skill.AGILITY)>=73) {
				nextActionClick = shortcut73;
			} else {
				nextOtherClick = 2;
			}
		} else if (ploc.getX()>1789 || (ploc.getY()>3883)) {
			// returning from soul altar
			nextActionClick = shortcut49; // TODO handle those who can't use these shortcuts.
		} else if ((ploc.getX()<1753 && ploc.getY()>3867) || (ploc.getX()<1768 && ploc.getY()>3872)) {
			//returning from dark altar
			if (ploc.getX()<1740) {
				nextOtherClick = 2;
			} else {
				if (client.getBoostedSkillLevel(Skill.AGILITY)>=69) {
					nextActionClick = shortcut69;
				} else if (client.getBoostedSkillLevel(Skill.AGILITY)>=52) {
					// TODO stopping halfway along this path will highlight the runestones before taking the shortcut.
					nextActionClick = shortcut52Outer;
				} else {
					chooseRunestone(ploc);
				}
			}
		} else {
			chooseRunestone(ploc);
		}
	}

	private void chooseRunestone(WorldPoint ploc) {
		boolean runestoneNorthDense = client.getVarbitValue(4927) == 0;
		boolean runestoneSouthDense = client.getVarbitValue(4928) == 0;
		if (runestoneNorthDense && runestoneSouthDense) {
			if (playerNearbyTo(ploc, runestoneNorth, 4)) {
				nextActionClick = runestoneNorth;
			} else {
				nextActionClick = runestoneSouth;
			}
		} else if (runestoneSouthDense) {
			nextActionClick = runestoneSouth;
		} else if (runestoneNorthDense) {
			nextActionClick = runestoneNorth;
		}
	}

	public Color currentHighlightColor() {
		if (config.actionFlash()) {
			int ms_in_cycle = (int) (System.currentTimeMillis() % (config.actionColor1Time() + config.actionColor2Time()));
			if (ms_in_cycle >= config.actionColor1Time()) {
				return config.actionColor2();
			}
		}
		return config.actionColor();
	}

	public Color currentidleTintColor() {
		if (config.idleTintFlash()) {
			int ms_in_cycle = (int) (System.currentTimeMillis() % (config.idleTintColor1Time() + config.idleTintColor2Time()));
			if (ms_in_cycle >= config.idleTintColor1Time()) {
				return config.idleTintColor2();
			}
		}
		return config.idleTintColor();
	}
}
