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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TargetArgument extends CommandElement {
	private static final SkyClaims PLUGIN = SkyClaims.getInstance();
	private static PermissionConfig config = PLUGIN.getConfig().getPermissionConfig();

	public static final Map<String, Target> TARGETS = Maps.newHashMap();

	static {
		TARGETS.put("island", Target.ISLAND);
		TARGETS.put("i", Target.ISLAND);
		TARGETS.put("chunk", Target.CHUNK);
		TARGETS.put("c", Target.CHUNK);
		TARGETS.put("block", Target.BLOCK);
		TARGETS.put("b", Target.BLOCK);
	}

	public enum Target {
		BLOCK, CHUNK, ISLAND;
	}

	public TargetArgument(@Nullable Text key) {
		super(key);
	}

	@Nullable
	@Override
	protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
		String target = args.next().toLowerCase();
		if (TARGETS.containsKey(target)) {
			if (!hasPermission(source, TARGETS.get(target)))
				throw new ArgumentParseException(Text.of(TextColors.RED, "You do not have permission to use the supplied target!"), target, 0);
			return TARGETS.get(target);
		}
		throw new ArgumentParseException(Text.of(TextColors.RED, "Invalid target!"), target, 0);
	}

	@Override
	public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
		try {
			String name = args.peek().toLowerCase();
			return TARGETS.entrySet().stream()
					.filter(s -> s.getKey().length() > 1)
					.filter(s -> s.getKey().startsWith(name))
					.filter(s -> hasPermission(src, s.getValue()))
					.map(Map.Entry::getKey)
					.collect(Collectors.toList());
		} catch (ArgumentParseException e) {
			return Lists.newArrayList();
		}
	}

	private boolean hasPermission(CommandSource src, Target target) {
		if (!config.isSeparateTargetPerms()) return true;
		switch (target) {
			case BLOCK:
				return src.hasPermission(Permissions.COMMAND_ARGUMENTS_BLOCK);
			case CHUNK:
				return src.hasPermission(Permissions.COMMAND_ARGUMENTS_CHUNK);
			case ISLAND:
				return true;
			default:
				return false;
		}
	}
}