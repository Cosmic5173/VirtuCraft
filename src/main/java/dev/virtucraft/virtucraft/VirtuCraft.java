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
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class VirtuCraft {

    public static String DATA_PATH = System.getProperty("user.dir") + "/";
    public final static String PLUGIN_PATH = DATA_PATH + "plugins";
    private static final VersionInfo versionInfo = loadVersion();

    public static void main(String[] args) {
        Thread.currentThread().setName("VirtuCraft-main");
        System.out.println("Starting VirtuCraft...");
        System.setProperty("log4j.skipJansi", "false");

        var logger = MainLogger.getLogger();
        logger.info("Starting VirtuCraft server software!");
        logger.info("Software Version: " + versionInfo.baseVersion());
        logger.info("Software Build: " + versionInfo.buildVersion());
        logger.info("Development Build: " + versionInfo.debug());
        logger.info("Software Authors: " + versionInfo.author());

        var javaVersion = getJavaVersion();
        if (javaVersion < 17) {
            logger.error("VirtuCraft requires Java 17 or higher to run!");
            logger.error("Please update your Java version.");
            return;
        }

        if (versionInfo.buildVersion().equals("#build") || versionInfo.branchName().equals("unknown")) {
            logger.warning("Custom build? Unofficial builds should be not run in production!");
        } else {
            logger.info("Discovered branch " + versionInfo.branchName() + " commitId " + versionInfo.commitId());
        }

        try {
            new Server(logger, DATA_PATH, PLUGIN_PATH);
        } catch (Exception e) {
            logger.logException(e);
            onShutdown();
        }
    }

    public static void onShutdown() {
        LogManager.shutdown();
    }

    private static VersionInfo loadVersion() {
        InputStream inputStream = VirtuCraft.class.getClassLoader().getResourceAsStream("git.properties");
        if (inputStream == null) {
            return VersionInfo.unknown();
        }

        Properties properties = new Properties();
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            return VersionInfo.unknown();
        }

        String branchName = properties.getProperty("git.branch", "unknown");
        String commitId = properties.getProperty("git.commit.id.abbrev", "unknown");
        boolean debug = !branchName.equals("release") && VersionInfo.DEFAULT_DEBUG;
        return new VersionInfo(branchName, commitId, debug);
    }

    public static void setLoggerLevel(Level level) {
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        Configuration log4jConfig = context.getConfiguration();
        LoggerConfig loggerConfig = log4jConfig.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);
        loggerConfig.setLevel(level);
        context.updateLoggers();
    }

    public static VersionInfo version() {
        return versionInfo;
    }

    private static int getJavaVersion() {
        String version = System.getProperty("java.version");
        if (version.startsWith("1.")) {
            return Integer.parseInt(version.substring(2, 3));
        }

        int index = version.indexOf(".");
        if (index != -1) {
            version = version.substring(0, index);
        }
        return Integer.parseInt(version);
    }
}
