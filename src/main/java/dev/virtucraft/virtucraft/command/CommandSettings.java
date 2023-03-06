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

import dev.virtucraft.virtucraft.utils.text.TranslationContainer;
import lombok.Builder;
import lombok.Getter;

/**
 * A container holding base information of each command
 */
@Builder @Getter
public class CommandSettings {

    private static final CommandSettings EMPTY_SETTINGS = CommandSettings.builder().build();

    private final String usageMessage;
    private final String description;
    private final boolean quoteAware;

    private final String permission;
    private final String permissionMessage;

    private final String[] aliases;

    private CommandSettings(String usageMessage, String description, String permission, String[] aliases, String permissionMessage, boolean quoteAware) {
        this.usageMessage = new TranslationContainer(usageMessage).getTranslated();
        this.description = new TranslationContainer(description).getTranslated();
        this.permission = new TranslationContainer(permission).getTranslated();
        this.permissionMessage = new TranslationContainer(permissionMessage).getTranslated();
        this.quoteAware = quoteAware;
        this.aliases = aliases;
    }

    private CommandSettings(String usageMessage, String description, String permission, String[] aliases, String permissionMessage){
        this(usageMessage, description, permission, aliases, permissionMessage, false);
    }

    public static CommandSettings empty() {
        return EMPTY_SETTINGS;
    }

    @lombok.Builder
    public static class Builder {
        private String usageMessage;
        private String description;
        private String permission;
        private String permissionMessage;
        private String[] aliases;
        private boolean quoteAware;
    }
}
