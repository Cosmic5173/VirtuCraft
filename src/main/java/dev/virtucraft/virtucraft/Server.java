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
import java.util.concurrent.atomic.AtomicBoolean;

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

    private int tickCounter;
    private long nextTick;
    private final float[] tickAverage = {20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20};
    private final float[] useAverage = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private float maxTick = 20;
    private float maxUse = 0;
    private final AtomicBoolean isRunning = new AtomicBoolean(true);

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

        this.start();
    }

    private void start() {
        Preconditions.checkState(this.isRunning.get(), "Server is already running");
        this.tickCounter = 0;

        this.tickProcessor();

        try {
            this.shutdown0();
        } catch (Exception e) {
            this.logger.error("Error gracefully shutting down server", e);
        }
    }

    private void tickProcessor() {
        this.nextTick = System.currentTimeMillis();
        try {
            while (this.isRunning.get()) {
                try {
                    this.tick();

                    var next = this.nextTick;
                    var current = System.currentTimeMillis();

                    if (next - 0.1 > current) {
                        var allocated = next - current - 1;
                        if (allocated > 0) {
                            Thread.sleep(allocated, 900000);
                        }
                    }
                } catch (RuntimeException e) {
                    this.logger.logException(e);
                }
            }
        } catch (Throwable e) {
            this.logger.error("Unexpected error while ticking server", e);
        }
    }

    private void tick() {
        var tickTime = System.currentTimeMillis();

        var time = tickTime - this.nextTick;
        if (time < -25) {
            try {
                Thread.sleep(Math.max(5, -time - 25));
            } catch (InterruptedException e) {
               this.logger.logException(e);
            }
        }

        var tickTimeNano = System.nanoTime();
        if ((tickTime - this.nextTick) < -25) {
            return;
        }

        ++this.tickCounter;

        if ((this.tickCounter & 0b1111) == 0) {
            this.titleTick();
            this.maxTick = 20;
            this.maxUse = 0;
        }

        long nowNano = System.nanoTime();
        float tick = (float) Math.min(20, 1000000000 / Math.max(1000000, ((double) nowNano - tickTimeNano)));
        float use = (float) Math.min(1, ((double) (nowNano - tickTimeNano)) / 50000000);

        if (this.maxTick > tick) {
            this.maxTick = tick;
        }

        if (this.maxUse < use) {
            this.maxUse = use;
        }

        System.arraycopy(this.tickAverage, 1, this.tickAverage, 0, this.tickAverage.length - 1);
        this.tickAverage[this.tickAverage.length - 1] = tick;

        System.arraycopy(this.useAverage, 1, this.useAverage, 0, this.useAverage.length - 1);
        this.useAverage[this.useAverage.length - 1] = use;

        if ((this.nextTick - tickTime) < -1000) {
            this.nextTick = tickTime;
        } else {
            this.nextTick += 50;
        }
    }

    private void titleTick() {
        // TODO: Implement title tick
        // This appears on the console window. We should show info on tick rate, memory usage, network, players, etc.
    }

    public void shutdown() {
        this.isRunning.getAndSet(false);
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
        return this.isRunning.get();
    }
}
