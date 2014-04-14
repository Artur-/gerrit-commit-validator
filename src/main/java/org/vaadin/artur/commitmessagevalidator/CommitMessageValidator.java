package org.vaadin.artur.commitmessagevalidator;

import java.util.Collections;
import java.util.List;

import com.google.gerrit.extensions.annotations.Listen;
import com.google.gerrit.server.events.CommitReceivedEvent;
import com.google.gerrit.server.git.validators.CommitValidationException;
import com.google.gerrit.server.git.validators.CommitValidationListener;
import com.google.gerrit.server.git.validators.CommitValidationMessage;
import com.google.inject.Singleton;

@Listen
@Singleton
public class CommitMessageValidator implements CommitValidationListener {
	private final String COMMIT_MSG_INFO = "The commit message must be of type:\n"
			+ "Sort DOM elements for better WAI-ARIA support (#13334)\n"
			+ "\n"
			+ "<Possibly more information>\n"
			+ "\n"
			+ "The first row must be <= 72 characters and acts like a subject (the rest can be omitted at times).\n"
			+ "";

	@Override
	public List<CommitValidationMessage> onCommitReceived(
			CommitReceivedEvent receiveEvent) throws CommitValidationException {
		if (!subjectLengthOk(receiveEvent.commit.getShortMessage())) {
			throw new CommitValidationException(
					"The first line of the commit message is too long.\n\n"
							+ COMMIT_MSG_INFO);
		}
		if (!subjectTicketReference(receiveEvent.commit.getShortMessage())) {
			throw new CommitValidationException(
					"The first line of the commit message must end in (#ticket)"
							+ COMMIT_MSG_INFO);
		}

		return Collections.<CommitValidationMessage> emptyList();
	}

	private static boolean subjectLengthOk(String shortMessage) {
		return (shortMessage.length() <= 72);
	}

	private static boolean subjectTicketReference(String shortMessage) {
		if (!shortMessage.matches(".*\\(#(\\d+)\\)$")) {
			return false;
		}

		return true;
	}

	public static void main(String[] args) {
		System.out.println(subjectLengthOk("Short subject (#1234)"));
		System.out
				.println(subjectLengthOk("A very very long subject which really makes no sense to actually use (#1234)"));
		System.out.println(subjectTicketReference("Short subject (#1234)"));
		System.out
				.println(subjectTicketReference("A very very long subject which really makes no sense to actually use (#1234)"));
		System.out
				.println(subjectTicketReference("A subject ending in dot (#1234)."));
		System.out.println(subjectTicketReference("A subject without ticket"));
		System.out
				.println(subjectTicketReference("A subject with ticket (#111)"));
	}
}
