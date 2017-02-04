package net.mohron.skyclaims.command.argument;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.config.type.PermissionConfig;
import net.mohron.skyclaims.permissions.Permissions;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nullable;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SchematicArgument extends CommandElement {
	private static final SkyClaims PLUGIN = SkyClaims.getInstance();
	private static PermissionConfig config = PLUGIN.getConfig().getPermissionConfig();

	public static final Map<String, String> SCHEMATICS = Maps.newHashMap();

	static {
		load();
	}

	public SchematicArgument(@Nullable Text key) {
		super(key);
	}

	@Nullable
	@Override
	protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
		String schem = args.next().toLowerCase();
		if (SCHEMATICS.isEmpty())
			throw new ArgumentParseException(Text.of(TextColors.RED, "There are no valid schematics available!"), schem, 0);
		if (SCHEMATICS.containsKey(schem)) {
			if (!hasPermission(source, schem))
				throw new ArgumentParseException(Text.of(TextColors.RED, "You do not have permission to use the supplied schematic!"), schem, 0);
			return SCHEMATICS.get(schem);
		}
		throw new ArgumentParseException(Text.of(TextColors.RED, "Invalid Schematic!"), schem, 0);
	}

	@Override
	public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
		try {
			String name = args.peek().toLowerCase();
			return SCHEMATICS.keySet().stream()
					.filter(s -> s.startsWith(name))
					.filter(s -> hasPermission(src, s))
					.collect(Collectors.toList());
		} catch (ArgumentParseException e) {
			return Lists.newArrayList();
		}
	}

	private boolean hasPermission(CommandSource src, String name) {
		return !config.isSeparateSchematicPerms() || src.hasPermission(Permissions.COMMAND_ARGUMENTS_SCHEMATICS + "." + name.toLowerCase());
	}

	@SuppressWarnings("ConstantConditions")
	public static void load() {
		SchematicArgument.SCHEMATICS.clear();
		File schemDir = new File(PLUGIN.getConfigDir() + File.separator + "schematics");
		try {
			PLUGIN.getLogger().info("Attempting to retrieve all schematics!");
			for (File file : schemDir.listFiles()) {
				PLUGIN.getLogger().info("Found File: " + file);
				String schem = file.getName();
				if (schem.endsWith(".schematic")) {
					SchematicArgument.SCHEMATICS.put(schem.replace(".schematic", "").toLowerCase(), schem.replace(".schematic", ""));
					PLUGIN.getLogger().info("Added Schematic: " + schem);
				}
			}
		} catch (NullPointerException e) {
			PLUGIN.getLogger().error("Failed to read schematics directory!");
		}
	}
} 