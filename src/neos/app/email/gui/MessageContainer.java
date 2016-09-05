package neos.app.email.gui;

import org.apache.james.mime4j.message.Message;

public interface MessageContainer {
	void addMessage(int id, Message msg);
}
