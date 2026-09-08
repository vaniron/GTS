package org.pokesplash.gts.UI.module;

import com.cobblemon.mod.common.api.moves.Move;
import com.cobblemon.mod.common.api.pokemon.stats.Stat;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.api.types.ElementalType;
import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.pokesplash.gts.Gts;
import org.pokesplash.gts.Listing.PokemonListing;
import org.pokesplash.gts.util.ColorUtil;
import org.pokesplash.gts.util.Utils;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Class that creates Pokemon specific UI lore.
 */
public abstract class PokemonInfo {

	private static Component createStatLine(Pokemon pokemon, String translationKey, Style style, Stat stat) {
		// Safely grab IVs and EVs, defaulting to 0
		Integer rawIv = pokemon.getIvs().get(stat);
		int iv = (rawIv == null) ? 0 : rawIv;

		Integer rawEv = pokemon.getEvs().get(stat);
		int ev = (rawEv == null) ? 0 : rawEv;

		// Check for HyperTrained IVs. If null, return an empty string.
		// If present, format it with parentheses and a leading space.
		Integer rawHt = pokemon.getIvs().getHyperTrainedIVs().get(stat);
		String htDisplay = (rawHt == null) ? "" : " (" + rawHt + ")";

		// Construct and return the final formatted Component
		return Component.translatable(translationKey).setStyle(style)
				.append(" §8- §3IV: §a" + iv + htDisplay + " §cEV: §a" + ev);
	}

    /**
	 * Create UI component lore for a give Pokemon (Gender, IVs, Nature, etc).
	 * @param pokemon The Pokemon to create lore for.
	 * @return The list of lore created.
	 */
	public static Collection<Component> parse(Pokemon pokemon) {

		Style style = Style.EMPTY.withItalic(false);

		Collection<Component> lore = new ArrayList<>();
		Style dark_aqua = style.withColor(TextColor.parseColor("dark_aqua").getOrThrow());
		Style dark_green = style.withColor(TextColor.parseColor("dark_green").getOrThrow());
		Style dark_purple = style.withColor(TextColor.parseColor("dark_purple").getOrThrow());
		Style gold = style.withColor(TextColor.parseColor("gold").getOrThrow());
		Style gray = style.withColor(TextColor.parseColor("gray").getOrThrow());
		Style green = style.withColor(TextColor.parseColor("green").getOrThrow());
		Style red = style.withColor(TextColor.parseColor("red").getOrThrow());
		Style light_purple = style.withColor(TextColor.parseColor("light_purple").getOrThrow());
		Style yellow = style.withColor(TextColor.parseColor("yellow").getOrThrow());
		Style white = style.withColor(TextColor.parseColor("white").getOrThrow());

		Item ball = pokemon.getCaughtBall().item();

		lore.add(Component.empty().setStyle(style).append(ColorUtil.parse(Gts.language.getPokemonBall()))
				.append(Component.translatable(ball.getName(new ItemStack(ball)).getString()).setStyle(green)));

		lore.add(Component.translatable("cobblemon.ui.info.species").setStyle(dark_green).append(": ")
				.append(pokemon.getSpecies().getTranslatedName().setStyle(green)));

		MutableComponent types = Component.empty().setStyle(green);
		for (ElementalType type : pokemon.getSpecies().getTypes()) {
			types.append(" ").append(type.getDisplayName());
		}
		lore.add(Component.translatable("cobblemon.ui.info.type").setStyle(dark_green).append(":").append(types));

		MutableComponent natureLine = Component.translatable("cobblemon.ui.info.nature").setStyle(dark_green).append(": ")
				.append(Component.translatable(pokemon.getNature().getDisplayName()).setStyle(green));
		if (Utils.hasMintedNature(pokemon)) {
			natureLine.append(Component.literal(" (").setStyle(dark_green))
					.append(Component.translatable(pokemon.getMintedNature().getDisplayName()).setStyle(yellow))
					.append(Component.literal(")").setStyle(dark_green));
		}
		lore.add(natureLine);

		MutableComponent ability = Component.translatable("cobblemon.ui.info.ability").setStyle(dark_green)
				.append(": ")
				.append(Component.translatable(pokemon.getAbility().getDisplayName()).setStyle(green));
		if (Utils.isHA(pokemon)) {
			ability.append(Component.literal(" §b(HA)").setStyle(gold));
		}
		lore.add(ability);

		if (!pokemon.getPersistentData().getString("size").isEmpty()) {
			lore.add(Component.literal("Size").setStyle(dark_green)
					.append(": ")
					.append(Component.literal(Utils.capitaliseFirst(pokemon.getPersistentData().getString("size")))
							.setStyle(green))
			);
		}

		if (pokemon.getPersistentData().getBoolean("pokerus")) {
			lore.add(Component.literal("Pokerus").setStyle(dark_green)
					.append(": ")
					.append(Component.translatable("cobblemon.ui.generic.yes")
							.setStyle(green))
			);
		}

		lore.add(Component.translatable("cobblemon.ui.stats").setStyle(gray).append(": "));

        lore.add(createStatLine(pokemon, "cobblemon.ui.stats.hp", light_purple, Stats.HP));
        lore.add(createStatLine(pokemon, "cobblemon.ui.stats.atk", red, Stats.ATTACK));
        lore.add(createStatLine(pokemon, "cobblemon.ui.stats.def", gold, Stats.DEFENCE));
        lore.add(createStatLine(pokemon, "cobblemon.ui.stats.sp_atk", dark_purple, Stats.SPECIAL_ATTACK));
        lore.add(createStatLine(pokemon, "cobblemon.ui.stats.sp_def", yellow, Stats.SPECIAL_DEFENCE));
        lore.add(createStatLine(pokemon, "cobblemon.ui.stats.speed", dark_aqua, Stats.SPEED));

		lore.add(Component.translatable("cobblemon.ui.stats.friendship").setStyle(dark_green)
				.append(": §a" + pokemon.getFriendship()));

		lore.add(Component.translatable("cobblemon.ui.moves").setStyle(gold).append(": "));
		for (Move move : pokemon.getMoveSet().getMoves()) {
			lore.add(Component.translatable(move.getTemplate().getDisplayName().getString()).setStyle(white));
		}

		if (Gts.config.isShowBreedable()) {

			if (pokemon.getPersistentData().contains("breedable") &&
					!pokemon.getPersistentData().getBoolean("breedable")) {
				lore.add(Component.literal("§cUnbreedable"));
			} else {
				lore.add(Component.literal("§bBreedable"));
			}
		}
		return lore;
	}

	public static Collection<Component> parse(PokemonListing listing) {
		Pokemon pokemon = listing.getListing();
		return parse(pokemon);
	}


}
