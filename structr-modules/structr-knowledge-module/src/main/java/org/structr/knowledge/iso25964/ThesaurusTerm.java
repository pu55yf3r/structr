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
package org.structr.knowledge.iso25964;

import java.net.URI;
import java.util.Locale;
import org.structr.common.PropertyView;
import org.structr.core.entity.Relation.Cardinality;
import org.structr.core.graph.NodeInterface;
import org.structr.schema.SchemaService;
import org.structr.schema.json.JsonObjectType;
import org.structr.schema.json.JsonSchema;

/**
 * Class as defined in ISO 25964 data model
 */

public interface ThesaurusTerm extends NodeInterface {

	static class Impl { static {

		final JsonSchema schema   = SchemaService.getDynamicSchema();
		
		final JsonObjectType type                = schema.addType("ThesaurusTerm");
		final JsonObjectType customTermAttribute = schema.addType("CustomTermAttribute");
		final JsonObjectType historyNote         = schema.addType("HistoryNote");
		final JsonObjectType definition          = schema.addType("Definition");
		final JsonObjectType editorialNote       = schema.addType("EditorialNote");

		type.setImplements(URI.create("https://structr.org/v1.1/definitions/ThesaurusTerm"));

		type.addStringProperty("lexicalValue", PropertyView.All, PropertyView.Ui).setIndexed(true).setRequired(true);
		type.addStringArrayProperty("identifier", PropertyView.All, PropertyView.Ui).setIndexed(true).setRequired(true);
		type.addDateProperty("created", PropertyView.All, PropertyView.Ui).setIndexed(true);
		type.addDateProperty("modified", PropertyView.All, PropertyView.Ui).setIndexed(true);
		type.addStringProperty("source", PropertyView.All, PropertyView.Ui).setIndexed(true);
		type.addStringProperty("status", PropertyView.All, PropertyView.Ui).setIndexed(true);
		type.addEnumProperty("lang", PropertyView.All, PropertyView.Ui).setEnums(Locale.getISOLanguages());
		
		type.relate(customTermAttribute, "hasCustomTermAttribute", Cardinality.OneToMany, "customTermAttributes", "term");
		type.relate(historyNote,         "hasHistoryNote",         Cardinality.OneToMany, "historyNotes",         "term");
		type.relate(definition,          "hasDefiniton",           Cardinality.OneToMany, "definitions",          "term");
		type.relate(editorialNote,       "hasEditorialNote",       Cardinality.OneToMany, "editorialNotes",       "term");
	}}
}
