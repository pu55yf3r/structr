/*
 *  Copyright (C) 2011 Axel Morgner
 * 
 *  This file is part of structr <http://structr.org>.
 * 
 *  structr is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  structr is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with structr.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.structr.rest.constraint;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import org.neo4j.graphdb.Direction;
import org.structr.core.GraphObject;
import org.structr.core.entity.AbstractNode;
import org.structr.rest.exception.IllegalPathException;
import org.structr.rest.exception.PathException;
import org.structr.rest.wrapper.PropertySet;

/**
 *
 * @author Christian Morgner
 */
public class RelationshipConstraint extends WrappingConstraint {

	private static final Logger logger = Logger.getLogger(RelationshipConstraint.class.getName());
	private Direction direction = null;

	@Override
	public boolean checkAndConfigure(String part, HttpServletRequest request) {

		if("in".equals(part.toLowerCase())) {

			direction = Direction.INCOMING;
			return true;

		} else if("out".equals(part.toLowerCase())) {

			direction = Direction.OUTGOING;
			return true;

		}

		return false;
	}

	@Override
	public List<GraphObject> doGet() throws PathException {

		List<GraphObject> results = wrappedConstraint.doGet();
		if(results != null) {

			try {
				List<GraphObject> resultList = new LinkedList<GraphObject>();
				for(GraphObject obj : results) {

					if(obj instanceof AbstractNode) {

						List relationships = obj.getRelationships(null, direction);
						if(relationships != null) {

							resultList.addAll(relationships);
						}
					}
				}

				return resultList;

			} catch(Throwable t) {

				logger.log(Level.WARNING, "Exception while fetching relationships", t);
			}

		} else {

			logger.log(Level.INFO, "No results from parent..");

		}

		throw new IllegalPathException();
	}
	@Override
	public void doDelete() throws PathException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void doPost(PropertySet propertySet) throws Throwable {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void doPut(PropertySet propertySet) throws PathException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void doHead() throws PathException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void doOptions() throws PathException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public ResourceConstraint tryCombineWith(ResourceConstraint next) {
		return null;
	}
}
