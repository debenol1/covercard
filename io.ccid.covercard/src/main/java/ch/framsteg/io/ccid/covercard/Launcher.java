/*******************************************************************************
 * Copyright 2024 Framsteg GmbH / olivier.debenath@framsteg.ch
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package ch.framsteg.io.ccid.covercard;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CardTerminals;
import javax.smartcardio.TerminalFactory;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

public class Launcher {
	private static final Logger logger = LogManager.getLogger(Launcher.class);
	private static Properties properties = new Properties();

	public static void main(String[] args) {
		try {
			setVerbosity(args);
			loadProperties();
			Controller.run(properties,checkTerminal());
		} catch (IOException e) {
			logger.error("Properties not loeaded");
			e.printStackTrace();
		} catch (CardException e) {
			logger.error("Terminal not present");
			e.printStackTrace();
		}
	}

	private static void loadProperties() throws IOException {
		InputStream inputStream = Launcher.class.getClassLoader().getResourceAsStream("application.properties");
		properties.load(inputStream);
	}

	private static void setVerbosity(String[] params) {
		if (Arrays.stream(params).anyMatch("-v"::equals)) {
			Configurator.setAllLevels(LogManager.getRootLogger().getName(), Level.ALL);
		} else {
			Configurator.setAllLevels(LogManager.getRootLogger().getName(), Level.INFO);
		}
	}

	private static CardTerminal checkTerminal() throws CardException {
		TerminalFactory factory = TerminalFactory.getDefault();
		logger.debug(factory.toString());
		CardTerminals terminals=factory.terminals();
		logger.debug("["+terminals.list().size()+"] terminals found");
		logger.debug(terminals.list().get(0).getName());
		return terminals.list().get(0);
	}

	public static Properties getProperties() {
		return properties;
	}

	public static void setProperties(Properties properties) {
		Launcher.properties = properties;
	}

}
