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

package dev.virtucraft.virtucraft.utils.config;

import dev.virtucraft.virtucraft.Server;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.cubespace.Yamler.Config.InvalidConfigurationException;

import java.io.File;
import java.util.Arrays;

@Getter
public final class ConfigurationManager {

    private final Server server;
    private ServerConfig serverConfig;

    public ConfigurationManager(Server server) {
        this.server = server;
    }

    public void loadServerConfig() throws InvalidConfigurationException {
        var configFile = new File(this.server.getDataPath().toString() + "/config.yml");
        var config = new ServerConfig(configFile);
        config.init();
        this.serverConfig = config;
    }

    @AllArgsConstructor
    public enum Type {
        JSON(1),
        YAML(2),
        UNKNOWN(-1);

        @Getter
        private final int id;

        public static Type getTypeById(int id) {
            return Arrays.stream(Type.values()).filter(type -> type.getId() == id).findFirst().orElse(Type.UNKNOWN);
        }
    }
}
