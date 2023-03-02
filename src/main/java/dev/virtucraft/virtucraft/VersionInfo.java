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

public class VersionInfo {

    public static final boolean DEFAULT_DEBUG = true;

    private final String baseVersion = "1.0-snapshot";
    private final String buildVersion = "#build";
    private final String author = "VirtuCraftTEAM";

    private final String branchName;
    private final String commitId;
    private boolean debug;

    public VersionInfo(String branchName, String commitId, boolean debug) {
        this.branchName = branchName;
        this.commitId = commitId;
        this.debug = debug;
    }

    public static VersionInfo unknown() {
        return new VersionInfo("unknown", "unknown", DEFAULT_DEBUG);
    }

    public String baseVersion() {
        return this.baseVersion;
    }

    public String buildVersion() {
        return this.buildVersion;
    }

    public String author() {
        return this.author;
    }

    public String branchName() {
        return this.branchName;
    }

    public String commitId() {
        return this.commitId;
    }

    //TODO: Debug

    public boolean debug() {
        return this.debug;
    }
}
