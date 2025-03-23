module io.ccid.covercard {
	exports ch.framsteg.io.ccid.covercard;
	requires org.apache.logging.log4j;
	requires org.apache.logging.log4j.core;
	requires transitive java.smartcardio;
	requires java.desktop;
}