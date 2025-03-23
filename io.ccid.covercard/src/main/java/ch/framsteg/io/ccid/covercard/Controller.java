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

import java.awt.AWTException;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.List;
import java.util.Properties;

import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Controller {
	private static final Logger logger = LogManager.getLogger(Controller.class);
	private final static String CODE = "Code: ";
	private final static String COUNTRY = "Country Code: ";
	private final static String INSURANCE = "Insurance: ";
	private final static String BAG_NR = "BAG Nr: ";
	private final static String CARD_NR = "Card Nr: ";
	private final static String VALID_UNTIL = "Valid until: ";

	public static void run(Properties properties, CardTerminal cardTerminal) {
		// Starting service
		while (true) {
			try {
				// Let's sleep for a second
				cardTerminal.waitForCardPresent(1000);
				if (cardTerminal.isCardPresent()) {
					logger.info("Card inserted");
					// "T=1"
					Card card = cardTerminal.connect(properties.getProperty("protocol"));
					CardChannel channel = card.getBasicChannel();
					// Get the hex data
					// APDU 00B0870000
					ResponseAPDU response = channel
							.transmit(new CommandAPDU(HexFormat.of().parseHex(properties.getProperty("apdu"))));
					logger.debug(response.toString());
					logger.debug(Arrays.toString(response.getData()));
					// Build the char data
					StringBuilder charData = new StringBuilder();
					byte[] rawData = response.getBytes();
					logger.debug("Response size: " + rawData.length);
					// Cut the necessary data off the string
					if (rawData.length > 96) {
						for (int i = 0; i < rawData.length; i++) {
							// Normal characters are being taking over
							if (rawData[i] >= 33 && rawData[i] <= 126) {
								charData.append((char) rawData[i]);
								// Control characters are being sort out
							} else if (rawData[i] < 0) {
								charData.append(":");
							}
						}
						List<String> charDataSplitted = Arrays.asList(charData.toString().split(":"));
						logger.debug(CODE + charDataSplitted.get(0));
						logger.debug(COUNTRY + charDataSplitted.get(1));
						logger.debug(INSURANCE + charDataSplitted.get(2));
						logger.debug(BAG_NR + charDataSplitted.get(3));
						logger.info(CARD_NR + charDataSplitted.get(4));
						logger.debug(VALID_UNTIL + charDataSplitted.get(5));
						Transmitter.transmitNumeric(charDataSplitted.get(4));
						charDataSplitted = null;
						charData = null;
					} else {
						logger.info("Wrong card type");
					}
					card.disconnect(true);
					logger.debug("Awaiting the card being removed");
					while (cardTerminal.isCardPresent()) {
					}
					logger.info("Card removed");
				}
			} catch (CardException e) {
				e.printStackTrace();

			} catch (AWTException e) {
				e.printStackTrace();
			}
		}
	}
}
