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

package dev.virtucraft.virtucraft.command;

import lombok.Getter;

/**
 * Base class for server commands
 */
@Getter
public abstract class Command {

    /**
     * The name of the command
     */
    private final String name;

    /**
     * The command settings assigned to it
     */
    private final CommandSettings settings;

    public Command(String name) {
        this(name, CommandSettings.empty());
    }

    public Command(String name, CommandSettings settings) {
        this.name = name;
        this.settings = settings;
    }

    public abstract boolean onExecute(CommandSender sender, String alias, String[] args);

    public String getDescription() {
        return this.settings.getDescription();
    }

    public String getUsageMessage() {
        return this.settings.getUsageMessage();
    }

    public String getPermissionMessage() {
        return this.settings.getPermissionMessage();
    }

    public String getPermission() {
        return this.settings.getPermission();
    }

    public String[] getAliases() {
        return this.settings.getAliases();
    }
}