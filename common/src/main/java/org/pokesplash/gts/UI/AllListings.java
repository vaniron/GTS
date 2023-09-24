package org.pokesplash.gts.UI;

import ca.landonjw.gooeylibs2.api.UIManager;
import ca.landonjw.gooeylibs2.api.button.Button;
import ca.landonjw.gooeylibs2.api.button.FlagType;
import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import ca.landonjw.gooeylibs2.api.button.PlaceholderButton;
import ca.landonjw.gooeylibs2.api.button.linked.LinkType;
import ca.landonjw.gooeylibs2.api.button.linked.LinkedPageButton;
import ca.landonjw.gooeylibs2.api.helpers.PaginationHelper;
import ca.landonjw.gooeylibs2.api.page.LinkedPage;
import ca.landonjw.gooeylibs2.api.page.Page;
import ca.landonjw.gooeylibs2.api.template.types.ChestTemplate;
import com.cobblemon.mod.common.CobblemonItems;
import com.cobblemon.mod.common.item.PokemonItem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.pokesplash.gts.Gts;
import org.pokesplash.gts.Listing.ItemListing;
import org.pokesplash.gts.Listing.PokemonListing;
import org.pokesplash.gts.UI.module.PokemonInfo;
import org.pokesplash.gts.util.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * UI of the Pokemon Listings page.
 */
public class AllListings {

	/**
	 * Method that returns the page.
	 * @return Pokemon Listings page.
	 */
	public Page getPage() {

		Button seeItemListings = GooeyButton.builder()
				.display(new ItemStack(CobblemonItems.ASSAULT_VEST.get()))
				.title("§9See Item Listings")
				.onClick((action) -> {
					ServerPlayer sender = action.getPlayer();
					Page page = new ItemListings().getPage(ItemListings.SORT.NONE);
					UIManager.openUIForcefully(sender, page);
				})
				.build();

		Button seePokemonListings = GooeyButton.builder()
				.display(new ItemStack(CobblemonItems.POKE_BALL.get()))
				.hideFlags(FlagType.All)
				.title("§9See Pokemon Listings")
				.onClick((action) -> {
					ServerPlayer sender = action.getPlayer();
					Page page = new PokemonListings().getPage(PokemonListings.SORT.NONE);
					UIManager.openUIForcefully(sender, page);
				})
				.build();

		Button manageListings = GooeyButton.builder()
				.display(new ItemStack(CobblemonItems.SACHET.get()))
				.title("§dManage Listings")
				.onClick((action) -> {
					ServerPlayer sender = action.getPlayer();
					Page page = new ManageListings().getPage(action.getPlayer().getUUID());
					UIManager.openUIForcefully(sender, page);
				})
				.build();

		LinkedPageButton nextPage = LinkedPageButton.builder()
				.display(new ItemStack(Items.ARROW))
				.title("§7Next Page")
				.linkType(LinkType.Next)
				.build();

		LinkedPageButton previousPage = LinkedPageButton.builder()
				.display(new ItemStack(CobblemonItems.POISON_BARB.get()))
				.title("§7Previous Page")
				.linkType(LinkType.Previous)
				.build();


		PlaceholderButton placeholder = new PlaceholderButton();

		List<Button> pokemonButtons = new ArrayList<>();
		for (PokemonListing listing : Gts.listings.getPokemonListings()) {
			Collection<String> lore = new ArrayList<>();

			lore.add("§9Seller: §b" + listing.getSellerName());
			lore.add("§9Price: §b" + listing.getPrice());
			lore.add("§9Time Remaining: §b" + Utils.parseLongDate(listing.getEndTime() - new Date().getTime()));
			lore.addAll(PokemonInfo.parse(listing));

			Button button = GooeyButton.builder()
					.display(PokemonItem.from(listing.getPokemon(), 1))
					.title("§3" + Utils.capitaliseFirst(listing.getPokemon().getSpecies().toString()))
					.lore(lore)
					.onClick((action) -> {
						ServerPlayer sender = action.getPlayer();
						Page page = new SinglePokemonListing().getPage(sender, listing);
						UIManager.openUIForcefully(sender, page);
					})
					.build();
			pokemonButtons.add(button);
		}

		List<Button> itemButtons = new ArrayList<>();
		for (ItemListing listing : Gts.listings.getItemListings()) {
			Collection<String> lore = new ArrayList<>();

			lore.add("§9Seller: §b" + listing.getSellerName());
			lore.add("§9Price: §b" + listing.getPrice());
			lore.add("§9Time Remaining: §b" + Utils.parseLongDate(listing.getEndTime() - new Date().getTime()));

			Button button = GooeyButton.builder()
					.display(listing.getItem())
					.title("§3" + Utils.capitaliseFirst(listing.getItem().getDisplayName().getString()))
					.lore(lore)
					.onClick((action) -> {
						ServerPlayer sender = action.getPlayer();
						Page page = new SingleItemListing().getPage(sender, listing);
						UIManager.openUIForcefully(sender, page);
					})
					.build();
			itemButtons.add(button);
		}

		Button filler = GooeyButton.builder()
				.display(new ItemStack(Items.WHITE_STAINED_GLASS_PANE))
				.build();

		ChestTemplate template = ChestTemplate.builder(6)
				.rectangle(0, 0, 5, 9, placeholder)
				.fill(filler)
				.set(48, seePokemonListings)
				.set(49, manageListings)
				.set(50, seeItemListings)
				.set(53, nextPage)
				.set(45, previousPage)
				.build();

		pokemonButtons.addAll(itemButtons);

		LinkedPage page = PaginationHelper.createPagesFromPlaceholders(template, pokemonButtons, null);
		page.setTitle("§3" + Gts.language.getTitle());

		setPageTitle(page);

		return page;
	}

	private void setPageTitle(LinkedPage page) {
		LinkedPage next = page.getNext();
		if (next != null) {
			next.setTitle("§3" + Gts.language.getTitle());
			setPageTitle(next);
		}
	}
}
