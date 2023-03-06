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

package dev.virtucraft.virtucraft;

import com.google.common.base.Preconditions;
import dev.virtucraft.virtucraft.command.Command;
import dev.virtucraft.virtucraft.command.CommandSender;
import dev.virtucraft.virtucraft.command.ConsoleCommandSender;
import dev.virtucraft.virtucraft.command.map.CommandMap;
import dev.virtucraft.virtucraft.command.map.SimpleCommandMap;
import dev.virtucraft.virtucraft.command.utils.CommandUtils;
import dev.virtucraft.virtucraft.logger.MainLogger;
import dev.virtucraft.virtucraft.utils.config.ConfigurationManager;
import dev.virtucraft.virtucraft.utils.config.LangConfig;
import dev.virtucraft.virtucraft.utils.config.ServerConfig;
import dev.virtucraft.virtucraft.utils.text.TextContainer;
import lombok.Getter;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public final class Server {

    @Getter
    private static Server instance;

    @Getter
    private final Path dataPath;
    @Getter
    private final Path pluginPath;

    @Getter
    private final MainLogger logger;

    @Getter
    private final ConfigurationManager configurationManager;

    @Getter
    private CommandMap commandMap;
    @Getter
    private final ConsoleCommandSender consoleCommandSender;

    private boolean shutdown = false;

    public Server(MainLogger logger, String filePath, String pluginPath) throws Exception {
        instance = this;
        this.logger = logger;
        this.dataPath = Paths.get(filePath);
        this.pluginPath = Paths.get(pluginPath);

        this.configurationManager = new ConfigurationManager(this);
        this.configurationManager.loadServerConfig();
        this.configurationManager.loadLanguageConfig();

        this.consoleCommandSender = new ConsoleCommandSender(this);
        this.commandMap = new SimpleCommandMap(this, SimpleCommandMap.DEFAULT_PREFIX);
    }

    public void shutdown() {
        if (this.shutdown) {
            return;
        }
        this.shutdown = true;

        try {
            this.shutdown0();
        } catch (Exception e) {
            this.logger.error("Unable to shutdown server gracefully", e);
        } finally {
            VirtuCraft.onShutdown();
        }

    }

    private void shutdown0() throws Exception {

    }

    public String translate(TextContainer textContainer) {
        return this.getLanguageConfig().translateContainer(textContainer);
    }

    public boolean dispatchCommand(CommandSender sender, String message) {
        if (message.trim().isEmpty()) {
            return false;
        }

        String[] args = message.split(" ");
        if (args.length < 1) {
            return false;
        }

        Command command = this.getCommandMap().getCommand(args[0]);
        if (command == null)  {
            return false;
        }

        String[] shiftedArgs;
        if (command.getSettings().isQuoteAware()) { // Quote aware parsing
            var arguments = CommandUtils.parseArguments(message);
            arguments.remove(0);
            shiftedArgs = arguments.toArray(String[]::new);
        } else {
            shiftedArgs = args.length > 1 ? Arrays.copyOfRange(args, 1, args.length) : new String[0];
        }

        // TODO: Event system: call command event
        return this.commandMap.handleCommand(sender, args[0], shiftedArgs);
    }

    public ServerConfig getConfiguration() {
        return this.configurationManager.getServerConfig();
    }

    public LangConfig getLanguageConfig() {
        return this.configurationManager.getLangConfig();
    }

    public void setCommandMap(CommandMap commandMap) {
        Preconditions.checkNotNull(commandMap, "Command Map cannot be null");
        this.commandMap = commandMap;
    }

    public boolean isRunning() {
        return !shutdown;
    }
}
