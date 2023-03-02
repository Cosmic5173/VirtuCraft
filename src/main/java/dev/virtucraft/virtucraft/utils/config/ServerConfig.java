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

import net.cubespace.Yamler.Config.SerializeOptions;
import net.cubespace.Yamler.Config.YamlConfig;

import java.io.File;

@SerializeOptions(skipFailedObjects = true)
public class ServerConfig extends YamlConfig {

    public ServerConfig(File file) {
        this.CONFIG_HEADER = new String[]{"VirtuCraft Server Configuration file", "Configure your desired settings here."};
        this.CONFIG_FILE = file;
    }
}
