package com.fuyuaki.r_wilderness.server.commands.arguments;

import com.fuyuaki.r_wilderness.world.generation.region.reference.RegionLayerTypes;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.neoforged.neoforge.server.command.CommandUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RegionLayerArgument implements ArgumentType<RegionLayerTypes> {
    private static final Dynamic2CommandExceptionType INVALID = new Dynamic2CommandExceptionType(
            (found, constants) -> CommandUtils.makeTranslatableWithFallback("commands.r_wilderness.arguments.region_layer.invalid", constants, found));

    public static RegionLayerArgument argument() {
        return new RegionLayerArgument();
    }

    public static RegionLayerTypes getType(CommandContext<CommandSourceStack> context, String name) {
        return context.getArgument(name, RegionLayerTypes.class);
    }
    @Override
    public RegionLayerTypes parse(StringReader reader) throws CommandSyntaxException {
        String key = reader.readUnquotedString();
        try {
            return RegionLayerTypes.fromCommandKey(key);
        } catch (IllegalArgumentException e) {
            throw INVALID.createWithContext(reader, key, Arrays.toString(Arrays.stream(RegionLayerTypes.values()).map(RegionLayerTypes::getSerializedName).toArray()));
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggest(Stream.of(RegionLayerTypes.values()).map(RegionLayerTypes::getSerializedName), builder);
    }

    @Override
    public Collection<String> getExamples() {
        return Stream.of(RegionLayerTypes.values()).map(RegionLayerTypes::getSerializedName).collect(Collectors.toList());
    }

}
