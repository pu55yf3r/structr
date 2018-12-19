/**
 * Copyright (C) 2010-2018 Structr GmbH
 *
 * This file is part of Structr <http://structr.org>.
 *
 * Structr is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * Structr is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Structr.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.structr.websocket.command;

import java.util.regex.Pattern;
import org.eclipse.jetty.websocket.api.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.structr.common.SecurityContext;
import org.structr.common.error.FrameworkException;
import org.structr.core.GraphObject;
import org.structr.core.app.App;
import org.structr.core.app.StructrApp;
import org.structr.core.entity.AbstractNode;
import org.structr.core.entity.AbstractRelationship;
import org.structr.core.graph.Tx;
import org.structr.core.property.PropertyMap;
import org.structr.web.entity.Widget;
import org.structr.web.entity.dom.DOMNode;
import org.structr.web.entity.dom.Page;
import org.structr.web.entity.dom.ShadowDocument;
import org.structr.websocket.StructrWebSocket;
import org.structr.websocket.message.WebSocketMessage;

/**
 * Base class for all WebSocket commands in Structr.
 */
public abstract class AbstractCommand {

	public static final Pattern UUID_PATTERN = Pattern.compile("[a-fA-F0-9]{32}");
	public static final String COMMAND_KEY   = "command";
	public static final String ID_KEY        = "id";
	private static final Logger logger       = LoggerFactory.getLogger(AbstractCommand.class.getName());

	protected Session session                = null;
	protected StructrWebSocket webSocket     = null;
	protected String callback                = null;

	public abstract void processMessage(final WebSocketMessage webSocketData) throws FrameworkException;

	public abstract String getCommand();

	public Session getSession() {
		return session;
	}

	public StructrWebSocket getWebSocket() {
		return webSocket;
	}

	public Page getPage(final String id) {

		final AbstractNode node = getNode(id);

		if (node != null && node instanceof Page) {

			return (Page) node;
		}

		return null;
	}

	public DOMNode getDOMNode(final String id) {

		final AbstractNode node = getNode(id);

		if (node != null && node instanceof DOMNode) {

			return (DOMNode) node;
		}

		return null;
	}

	public Widget getWidget(final String id) {

		final AbstractNode node = getNode(id);

		if (node != null && node instanceof Widget) {

			return (Widget) node;
		}

		return null;
	}

	/**
	 * Returns the graph object with the given id.
	 *
	 * @param id
	 * @return the graph object
	 */
	public GraphObject getGraphObject(final String id) {

		return getGraphObject(id, null);
	}

	/**
	 * Returns the graph object with the given id.
	 *
	 * If no node with the given id is found and nodeId is not null,
	 * this method will search for a relationship in the list of relationships
	 * of the node with the given nodeId.
	 *
	 * @param id
	 * @param nodeId
	 * @return the graph object
	 */
	public GraphObject getGraphObject(final String id, final String nodeId) {

		if (isValidUuid(id)) {

			final AbstractNode node = getNode(id);
			if (node != null) {

				return node;

			} else {

				if (nodeId == null) {
					logger.warn("Relationship access by UUID is deprecated and not supported by Neo4j, this can take a very long time.");
				}

				final AbstractRelationship rel = getRelationship(id, nodeId);
				if (rel != null) {

					return rel;
				}
			}
			
		} else {

			logger.warn("Invalid UUID used for getGraphObject: {} is not a valid UUID.", id);
		}

		return null;
	}

	/**
	 * Returns the node with the given id.
	 *
	 * @param id
	 * @return the node
	 */
	public AbstractNode getNode(final String id) {

		final SecurityContext securityContext = getWebSocket().getSecurityContext();
		final App app = StructrApp.getInstance(securityContext);

		try (final Tx tx = app.tx()) {

			final AbstractNode node = (AbstractNode) app.getNodeById(id);

			tx.success();

			return node;

		} catch (FrameworkException fex) {
			logger.warn("Unable to get node", fex);
		}

		return null;
	}

	/**
	 * Returns the relationship with the given id by looking up a node
	 * with the given nodeId and filtering the relationships.
	 *
	 * This avoids the performance issues of getRelationshipById due to missing index support.
	 *
	 * If nodeId is null, the method falls back to {@link getRelationship(id)}.
	 *
	 *
	 * @param id
	 * @param nodeId
	 * @return the node
	 */
	public AbstractRelationship getRelationship(final String id, final String nodeId) {

		if (id == null) {
			return null;
		}

		if (nodeId == null) {
			return getRelationship(id);
		}

		final SecurityContext securityContext = getWebSocket().getSecurityContext();
		final App app = StructrApp.getInstance(securityContext);

		try (final Tx tx = app.tx()) {

			final AbstractNode node = (AbstractNode) app.getNodeById(nodeId);

			for (final AbstractRelationship rel : node.getRelationships()) {

				if (rel.getUuid().equals(id)) {
					return rel;
				}
			}

			tx.success();

		} catch (FrameworkException fex) {
			logger.warn("Unable to get relationship", fex);
		}

		return null;

	}

	/**
	 * Returns the relationship to which the uuid parameter
	 * of this command refers to.
	 *
	 * @param id
	 * @return the node
	 */
	public AbstractRelationship getRelationship(final String id) {

		if (id == null) {
			return null;
		}

		final SecurityContext securityContext = getWebSocket().getSecurityContext();
		final App app = StructrApp.getInstance(securityContext);

		try (final Tx tx = app.tx()) {

			final AbstractRelationship rel = (AbstractRelationship) app.getRelationshipById(id);

			tx.success();

			return rel;

		} catch (FrameworkException fex) {
			logger.warn("Unable to get relationship", fex);
		}

		return null;
	}

	/**
	 * Override this method if the websocket command will create its own
	 * transaction context
	 *
	 * @return a boolean
	 */
	public boolean requiresEnclosingTransaction() {
		return true;
	}

	/**
	 * Make child nodes of the source nodes child nodes of the target node.
	 *
	 * @param sourceNode
	 * @param targetNode
	 */
	protected void moveChildNodes(final DOMNode sourceNode, final DOMNode targetNode) {

		DOMNode child = (DOMNode) sourceNode.getFirstChild();

		while (child != null) {

			DOMNode next = (DOMNode) child.getNextSibling();

			targetNode.appendChild(child);

			child = next;

		}

	}
	/**
	 * Search for a hidden page named __ShadowDocument__ of type.
	 *
	 * If found, return it, if not, create it.
	 * The shadow page is the DOM document all reusable components are connected to.
	 * It is necessary to comply with DOM standards.
	 *
	 * @return shadow document
	 * @throws FrameworkException
	 */
	public static ShadowDocument getOrCreateHiddenDocument() throws FrameworkException {

		final App app = StructrApp.getInstance();

		try (final Tx tx = app.tx()) {

			ShadowDocument doc = app.nodeQuery(ShadowDocument.class).includeHidden().getFirst();
			if (doc == null) {

				final PropertyMap properties = new PropertyMap();
				properties.put(AbstractNode.type, ShadowDocument.class.getSimpleName());
				properties.put(AbstractNode.name, "__ShadowDocument__");
				properties.put(AbstractNode.hidden, true);
				properties.put(AbstractNode.visibleToAuthenticatedUsers, true);

				doc = app.create(ShadowDocument.class, properties);
			}

			tx.success();

			return doc;

		} catch (FrameworkException fex) {
			logger.warn("Unable to create container for shared components: {}", fex.getMessage());
		}

		return null;
	}

	public void setSession(final Session session) {
		this.session = session;
	}

	public void setWebSocket(final StructrWebSocket webSocket) {
		this.webSocket = webSocket;
	}

	public void setCallback(final String callback) {
		this.callback = callback;
	}

	public void setDoTransactionNotifications(final boolean notify) {

		final SecurityContext securityContext = getWebSocket().getSecurityContext();

		if (securityContext != null) {
			securityContext.setDoTransactionNotifications(notify);
		}

	}

	// ----- private methods -----
	private boolean isValidUuid(final String id) {

		if (id.length() != 32) {
			return false;
		}

		return UUID_PATTERN.matcher(id).matches();
	}
}
