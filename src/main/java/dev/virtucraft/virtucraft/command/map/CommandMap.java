/*
 * Copyright 2023 VirtuCraftTEAM
 * Licensed under the GNU General Public License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.virtucraft.virtucraft.command.map;

import dev.virtucraft.virtucraft.command.Command;
import dev.virtucraft.virtucraft.command.CommandSender;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;

/**
 * Base interface for a CommandMap. provides required methods for all command maps that should exist
 */
public interface CommandMap {

    /**
     * Registers a command with the respective name in it
     *
     * @param command the command which should be registered
     * @return true if this command could be registered under the respective name
     */
    boolean registerCommand(Command command);

    boolean registerAlias(String name, Command command);

    boolean unregisterCommand(String name);

    /**
     * @param name the name of the command or alias, case-insensitive
     * @return The Command instance of the registered Command, or null if no command or alias with this name could be found.
     */
    Command getCommand(String name);

    boolean isRegistered(String name);

    /**
     * @return if command can be handled by this command map
     */
    boolean handleMessage(CommandSender sender, String message);

    /**
     * WARNING: Will return true even if command was handled but thrown exception!
     *
     * @return true if command was handled.
     */
    boolean handleCommand(CommandSender sender, String command, String[] args);

    String getCommandPrefix();

    Object2ObjectMap<String, Command> getCommands();
}
