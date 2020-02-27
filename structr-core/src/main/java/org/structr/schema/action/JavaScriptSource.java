/**
 * Copyright (C) 2010-2020 Structr GmbH
 *
 * This file is part of Structr <http://structr.org>.
 *
 * Structr is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * Structr is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Structr.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.structr.schema.action;

import java.net.URI;
import org.structr.common.PropertyView;
import org.structr.core.graph.NodeInterface;
import org.structr.schema.SchemaService;
import org.structr.schema.json.JsonObjectType;
import org.structr.schema.json.JsonSchema;

/**
 *
 *
 */
public interface JavaScriptSource extends NodeInterface {

	static class Impl { static {

		final JsonSchema schema   = SchemaService.getDynamicSchema();
		final JsonObjectType type = schema.addType("JavaScriptSource");

		type.setIsInterface();
		type.setImplements(URI.create("https://structr.org/v1.1/definitions/JavaScriptSource"));

		type.addBooleanProperty("useAsJavascriptLibrary", PropertyView.Ui).setIndexed(true);
	}}

	/*
	public static final Property<Boolean> useAsJavascriptLibrary = new BooleanProperty("useAsJavascriptLibrary").indexed();
	public static final View uiView = new View(JavaScriptSource.class, PropertyView.Ui, useAsJavascriptLibrary);
	*/

	public String getJavascriptLibraryCode();
	public String getContentType();
}
