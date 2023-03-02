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

import dev.virtucraft.virtucraft.logger.MainLogger;
import lombok.Getter;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class Server {

    @Getter
    private static Server instance;

    private final Path dataPath;
    private final Path pluginPath;

    private final MainLogger logger;

    private boolean shutdown = false;

    public Server(MainLogger logger, String filePath, String pluginPath) {
        instance = this;
        this.logger = logger;
        this.dataPath = Paths.get(filePath);
        this.pluginPath = Paths.get(pluginPath);
    }

    public boolean isRunning() {
        return !shutdown;
    }
}
