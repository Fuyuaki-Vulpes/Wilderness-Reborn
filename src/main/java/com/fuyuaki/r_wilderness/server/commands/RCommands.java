package com.fuyuaki.r_wilderness.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.logging.LogUtils;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.execution.ExecutionContext;
import net.minecraft.server.permissions.PermissionCheck;
import net.minecraft.server.permissions.Permissions;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class RCommands {

    public static void init(Commands.CommandSelection selection, CommandBuildContext context, CommandDispatcher<CommandSourceStack> dispatcher) {
        RegionLocateCommand.register(dispatcher,context);
    }
}
