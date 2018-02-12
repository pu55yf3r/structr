/**
 * Copyright (C) 2010-2018 Structr GmbH
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
package org.structr.rest.test;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.filter.log.ResponseLoggingFilter;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.structr.common.error.FrameworkException;
import org.structr.core.app.StructrApp;
import org.structr.core.entity.AbstractNode;
import org.structr.core.entity.SchemaNode;
import org.structr.core.graph.NodeAttribute;
import org.structr.core.graph.Tx;
import org.structr.core.notion.TypeAndPropertySetDeserializationStrategy;
import org.structr.core.property.PropertyKey;
import org.structr.core.property.StringProperty;
import org.structr.rest.common.IndexingTest;
import org.structr.rest.common.TestEnum;
import org.structr.rest.entity.TestNine;
import org.structr.rest.entity.TestThree;
import org.structr.rest.entity.TestTwo;
import org.structr.rest.entity.TestUser;

/**
 *
 *
 */
public class AdvancedSearchTest extends IndexingTest {

	private static final Logger logger = LoggerFactory.getLogger(AdvancedSearchTest.class.getName());

	@Test
	public void testGraphBasedIndexingSearchOnNotionProperties() {

		String test01 = createEntity("/test_sixs", "{ name: test01, aString: string01, anInt: 1 }");
		String test02 = createEntity("/test_sixs", "{ name: test02, aString: string02, anInt: 2 }");
		String test03 = createEntity("/test_sixs", "{ name: test03, aString: string03, anInt: 3 }");
		String test04 = createEntity("/test_sixs", "{ name: test04, aString: string04, anInt: 4 }");
		String test05 = createEntity("/test_sixs", "{ name: test05, aString: string05, anInt: 5 }");
		String test06 = createEntity("/test_sixs", "{ name: test06, aString: string06, anInt: 6 }");
		String test07 = createEntity("/test_sixs", "{ name: test07, aString: string07, anInt: 7 }");
		String test08 = createEntity("/test_sixs", "{ name: test08, aString: string08, anInt: 8 }");

		String test09 = createEntity("/test_sevens", "{ name: test09, testSixIds: [", test01, ",", test02, "], aString: string09, anInt: 9 }");
		String test10 = createEntity("/test_sevens", "{ name: test10, testSixIds: [", test03, ",", test04, "], aString: string10, anInt: 10 }");
		String test11 = createEntity("/test_sevens", "{ name: test11, testSixIds: [", test05, ",", test06, "], aString: string11, anInt: 11 }");
		String test12 = createEntity("/test_sevens", "{ name: test12, testSixIds: [", test07, ",", test08, "], aString: string12, anInt: 12 }");

		String test13 = createEntity("/test_eights", "{ name: test13, testSixIds: [", test01, ",", test02, "], aString: string13, anInt: 13 }");
		String test14 = createEntity("/test_eights", "{ name: test14, testSixIds: [", test02, ",", test03, "], aString: string14, anInt: 14 }");
		String test15 = createEntity("/test_eights", "{ name: test15, testSixIds: [", test03, ",", test04, "], aString: string15, anInt: 15 }");
		String test16 = createEntity("/test_eights", "{ name: test16, testSixIds: [", test04, ",", test05, "], aString: string16, anInt: 16 }");
		String test17 = createEntity("/test_eights", "{ name: test17, testSixIds: [", test05, ",", test06, "], aString: string17, anInt: 17 }");
		String test18 = createEntity("/test_eights", "{ name: test18, testSixIds: [", test06, ",", test07, "], aString: string18, anInt: 18 }");
		String test19 = createEntity("/test_eights", "{ name: test19, testSixIds: [", test07, ",", test08, "], aString: string19, anInt: 19 }");
		String test20 = createEntity("/test_eights", "{ name: test20, testSixIds: [", test08, ",", test01, "], aString: string20, anInt: 20 }");

		String test21 = createEntity("/test_sixs", "{ name: test21, aString: string21, anInt: 21 }");
		String test22 = createEntity("/test_sixs", "{ name: test22, aString: string22, anInt: 22 }");

		String test23 = createEntity("/test_eights", "{ name: test23, testSixIds: [", test21, ",", test22, "], aString: string23, anInt: 23 }");

		// test simple related search with one object,
		// expected result is a list of two elements:
		// test05 and test07
		RestAssured

			.given()
				.contentType("application/json; charset=UTF-8")

			.expect()
				.statusCode(200)

				.body("result",	      hasSize(2))
				.body("result_count", equalTo(2))
				.body("result[0].id", equalTo(test01))
				.body("result[1].id", equalTo(test02))

			.when()
				.get(concat("/test_sixs?testSevenName=test09"));

		// test inexact related search with one object,
		// expected result is a list of four elements:
		// test01, test02, test03 and test04, because
		// test09 and test10 contain a "0" in their
		// name and are associated with those four
		RestAssured

			.given()
				.contentType("application/json; charset=UTF-8")

			.expect()
				.statusCode(200)

				.body("result",	      hasSize(4))
				.body("result_count", equalTo(4))
				.body("result[0].id", equalTo(test01))
				.body("result[1].id", equalTo(test02))
				.body("result[2].id", equalTo(test03))
				.body("result[3].id", equalTo(test04))

			.when()
				.get(concat("/test_sixs?testSevenName=0&loose=1"));


		// test simple related search with two objects, AND,
		// expected result is empty
		RestAssured

			.given()
				.contentType("application/json; charset=UTF-8")

			.expect()
				.statusCode(200)

				.body("result",	      hasSize(0))
				.body("result_count", equalTo(0))

			.when()
				.get(concat("/test_sevens?testSixIds=", test01, ",", test06));

		// test simple related search with two objects, OR
		// expected result is a list of two elements:
		// test09 and test11
		RestAssured

			.given()
				.contentType("application/json; charset=UTF-8")

			.expect()
				.statusCode(200)

				.body("result",	      hasSize(2))
				.body("result_count", equalTo(2))
				.body("result[0].id", equalTo(test09))
				.body("result[1].id", equalTo(test11))

			.when()
				.get(concat("/test_sevens?testSixIds=", test01, ";", test06));

		// test simple related search with one object,
		// expected result is a list of two elements
		RestAssured

			.given()
				.contentType("application/json; charset=UTF-8")

			.expect()
				.statusCode(200)

				.body("result",	      hasSize(2))
				.body("result_count", equalTo(2))
				.body("result[0].id", equalTo(test13))
				.body("result[1].id", equalTo(test20))

			.when()
				.get(concat("/test_eights?testSixIds=", test01));

		// test simple related search with two objects, OR
		// expected result is a list of two elements:
		// test09 and test11
		RestAssured

			.given()
				.contentType("application/json; charset=UTF-8")

			.expect()
				.statusCode(200)

				.body("result",	      hasSize(4))
				.body("result_count", equalTo(4))
				.body("result[0].id", equalTo(test13))
				.body("result[1].id", equalTo(test17))
				.body("result[2].id", equalTo(test18))
				.body("result[3].id", equalTo(test20))

			.when()
				.get(concat("/test_eights?testSixIds=", test01, ";", test06));

		// test related search with two related properties,
		// expected result is a single object:
		// test01
		RestAssured

			.given()
				.contentType("application/json; charset=UTF-8")

			.expect()
				.statusCode(200)

				.body("result",	      hasSize(1))
				.body("result_count", equalTo(1))
				.body("result[0].id", equalTo(test01))

			.when()
				.get(concat("/test_sixs?testSevenName=test09&testEightStrings=string20"));


		// test related search with a single related property and two
		// indexed properties that should filter the result set.
		// expected result is a single object:
		// test01
		RestAssured

			.given()
				.contentType("application/json; charset=UTF-8")

			.expect()
				.statusCode(200)

				.body("result",	      hasSize(1))
				.body("result_count", equalTo(1))
				.body("result[0].id", equalTo(test01))

			.when()
				.get(concat("/test_sixs?testSevenName=test09&aString=string01&anInt=1"));

		// test related search with two related properties and one
		// indexed property that should filter the result set.
		// expected result is a single object:
		// test07
		RestAssured

			.given()
				.contentType("application/json; charset=UTF-8")

			.expect()
				.statusCode(200)

				.body("result",	      hasSize(1))
				.body("result_count", equalTo(1))
				.body("result[0].id", equalTo(test07))

			.when()
				.get(concat("/test_sixs?testEightStrings=string19&testSevenName=test12&anInt=7"));

		// test inexact related search with collection property
		// expected result is a list of four objects:
		// test01, test08, test21, test22
		RestAssured

			.given()
				.contentType("application/json; charset=UTF-8")

			.expect()
				.statusCode(200)

				.body("result",	      hasSize(4))
				.body("result_count", equalTo(4))
				.body("result[0].id", equalTo(test01))
				.body("result[1].id", equalTo(test08))
				.body("result[2].id", equalTo(test21))
				.body("result[3].id", equalTo(test22))

			.when()
				.get(concat("/test_sixs?testEightStrings=2&loose=1"));

		// test inexact related search with two collection properties
		// expected result is a list of four objects:
		// test01, test02, test03, test04
		RestAssured

			.given()
				.contentType("application/json; charset=UTF-8")
				.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(200))

			.expect()
				.statusCode(200)

				.body("result",	      hasSize(4))
				.body("result_count", equalTo(4))
				.body("result[0].id", equalTo(test01))
				.body("result[1].id", equalTo(test02))
				.body("result[2].id", equalTo(test03))
				.body("result[3].id", equalTo(test04))

			.when()
				.get(concat("/test_sixs?testEightStrings=1&testSevenName=0&loose=1"));

		// test inexact related search with collection property and
		// two filter properties,
		// expected result is a single object:
		// test08
		RestAssured

			.given()
				.contentType("application/json; charset=UTF-8")

			.expect()
				.statusCode(200)

				.body("result",	      hasSize(1))
				.body("result_count", equalTo(1))
				.body("result[0].id", equalTo(test08))

			.when()
				.get(concat("/test_sixs?testEightStrings=2&testSevenName=test12&anInt=8&loose=1"));

		// test related search with one empty related property,
		// expected result is a list of two objects:
		// test21 and test22
		RestAssured

			.given()
				.contentType("application/json; charset=UTF-8")

			.expect()
				.statusCode(200)

				.body("result",	      hasSize(2))
				.body("result_count", equalTo(2))
				.body("result[0].id", equalTo(test21))
				.body("result[1].id", equalTo(test22))

			.when()
				.get(concat("/test_sixs?testSevenName="));


		// test related search with one related property and one
		// empty related property.
		// expected result is a list of two objects:
		// test21 and test22
		RestAssured

			.given()
				.contentType("application/json; charset=UTF-8")

			.expect()
				.statusCode(200)

				.body("result",	      hasSize(2))
				.body("result_count", equalTo(2))
				.body("result[0].id", equalTo(test21))
				.body("result[1].id", equalTo(test22))

			.when()
				.get(concat("/test_sixs?testSevenName=&testEightStrings=string23"));

		// test related search with one related property, one empty
		// related property and two indexed properties
		// expected result is a single object:
		// test21
		RestAssured

			.given()
				.contentType("application/json; charset=UTF-8")

			.expect()
				.statusCode(200)

				.body("result",	      hasSize(1))
				.body("result_count", equalTo(1))
				.body("result[0].id", equalTo(test21))

			.when()
				.get(concat("/test_sixs?testSevenName=&testEightStrings=string23&aString=string21&anInt=21"));

	}

	@Test
	public void testGraphBasedIndexingSearchOnRelatedNodeProperties() {

		String test01 = createEntity("/test_sixs", "{ name: test01, aString: string01, anInt: 1 }");
		String test02 = createEntity("/test_sixs", "{ name: test02, aString: string02, anInt: 2 }");
		String test03 = createEntity("/test_sixs", "{ name: test03, aString: string03, anInt: 3 }");
		String test04 = createEntity("/test_sixs", "{ name: test04, aString: string04, anInt: 4 }");
		String test05 = createEntity("/test_sixs", "{ name: test05, aString: string05, anInt: 5 }");
		String test06 = createEntity("/test_sixs", "{ name: test06, aString: string06, anInt: 6 }");
		String test07 = createEntity("/test_sixs", "{ name: test07, aString: string07, anInt: 7 }");
		String test08 = createEntity("/test_sixs", "{ name: test08, aString: string08, anInt: 8 }");

		String test09 = createEntity("/test_sevens", "{ name: test09, testSixIds: [", test01, ",", test02, "], aString: string09, anInt: 9 }");
		String test10 = createEntity("/test_sevens", "{ name: test10, testSixIds: [", test03, ",", test04, "], aString: string10, anInt: 10 }");
		String test11 = createEntity("/test_sevens", "{ name: test11, testSixIds: [", test05, ",", test06, "], aString: string11, anInt: 11 }");
		String test12 = createEntity("/test_sevens", "{ name: test12, testSixIds: [", test07, ",", test08, "], aString: string12, anInt: 12 }");

		String test13 = createEntity("/test_eights", "{ name: test13, testSixIds: [", test01, ",", test02, "], aString: string13, anInt: 13 }");
		String test14 = createEntity("/test_eights", "{ name: test14, testSixIds: [", test02, ",", test03, "], aString: string14, anInt: 14 }");
		String test15 = createEntity("/test_eights", "{ name: test15, testSixIds: [", test03, ",", test04, "], aString: string15, anInt: 15 }");
		String test16 = createEntity("/test_eights", "{ name: test16, testSixIds: [", test04, ",", test05, "], aString: string16, anInt: 16 }");
		String test17 = createEntity("/test_eights", "{ name: test17, testSixIds: [", test05, ",", test06, "], aString: string17, anInt: 17 }");
		String test18 = createEntity("/test_eights", "{ name: test18, testSixIds: [", test06, ",", test07, "], aString: string18, anInt: 18 }");
		String test19 = createEntity("/test_eights", "{ name: test19, testSixIds: [", test07, ",", test08, "], aString: string19, anInt: 19 }");
		String test20 = createEntity("/test_eights", "{ name: test20, testSixIds: [", test08, ",", test01, "], aString: string20, anInt: 20 }");

		String test21 = createEntity("/test_sixs", "{ name: test21, aString: string21, anInt: 21 }");
		String test22 = createEntity("/test_sixs", "{ name: test22, aString: string22, anInt: 22 }");

		String test23 = createEntity("/test_eights", "{ name: test23, testSixIds: [", test21, ",", test22, "], aString: string23, anInt: 23 }");

		// test simple related search with two objects, AND,
		// expected result is empty
		RestAssured

			.given()
				.contentType("application/json; charset=UTF-8")

			.expect()
				.statusCode(200)

				.body("result",	      hasSize(0))
				.body("result_count", equalTo(0))

			.when()
				.get(concat("/test_sevens?testSixs=", test01, ",", test06));

		// test simple related search with two objects, AND,
		// expected result is exactly one element
		RestAssured

			.given()
				.contentType("application/json; charset=UTF-8")

			.expect()
				.statusCode(200)

				.body("result",	      hasSize(1))
				.body("result_count", equalTo(1))

				.body("result[0].id", equalTo(test09))

			.when()
				.get(concat("/test_sevens?testSixs=", test01, ",", test02));

		// test simple related search with two objects, OR
		// expected result is a list of two elements:
		// test09 and test11
		RestAssured

			.given()
				.contentType("application/json; charset=UTF-8")

			.expect()
				.statusCode(200)

				.body("result",	      hasSize(2))
				.body("result_count", equalTo(2))
				.body("result[0].id", equalTo(test09))
				.body("result[1].id", equalTo(test11))

			.when()
				.get(concat("/test_sevens?testSixs=", test01, ";", test06));

		// test simple related search with one object,
		// expected result is a list of two elements
		RestAssured

			.given()
				.contentType("application/json; charset=UTF-8")

			.expect()
				.statusCode(200)

				.body("result",	      hasSize(2))
				.body("result_count", equalTo(2))
				.body("result[0].id", equalTo(test13))
				.body("result[1].id", equalTo(test20))

			.when()
				.get(concat("/test_eights?testSixs=", test01));

		// test simple related search with two objects, OR
		// expected result is a list of four elements:
		RestAssured

			.given()
				.contentType("application/json; charset=UTF-8")

			.expect()
				.statusCode(200)

				.body("result",	      hasSize(4))
				.body("result_count", equalTo(4))
				.body("result[0].id", equalTo(test13))
				.body("result[1].id", equalTo(test17))
				.body("result[2].id", equalTo(test18))
				.body("result[3].id", equalTo(test20))

			.when()
				.get(concat("/test_eights?testSixs=", test01, ";", test06));

		// test simple related search with two objects, OR
		// expected result is a list of two elements:
		// test09 and test11
		RestAssured

			.given()
				.contentType("application/json; charset=UTF-8")

			.expect()
				.statusCode(200)

				.body("result",	      hasSize(1))
				.body("result_count", equalTo(1))
				.body("result[0].id", equalTo(test13))

			.when()
				.get(concat("/test_eights?testSixs=", test01, ",", test02));

	}

	@Test
	public void testGraphBasedIndexingSearchCombinedWithGeocoding() {

		String test01 = createEntity("/test_eights", "{ name: test01, aString: string01, anInt: 1 }");
		String test02 = createEntity("/test_eights", "{ name: test02, aString: string02, anInt: 2 }");
		String test03 = createEntity("/test_eights", "{ name: test03, aString: string03, anInt: 3 }");
		String test04 = createEntity("/test_eights", "{ name: test04, aString: string04, anInt: 4 }");

		String test05 = createEntity("/test_nines", "{ name: test05, city: Dortmund, street: Strobelallee, testEightIds: [ ", test01, ",", test02, "] }");
		String test06 = createEntity("/test_nines", "{ name: test06, city: 'Fehlerstadt', street: 'Unbekanntstraße', testEightIds: [ ", test03, ",", test04, "] }");
		String test07 = createEntity("/test_nines", "{ name: test07, city: München, street: Maximiliansplatz }");

		RestAssured.given().filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(200)).get("/TestNine/" + test05);
		RestAssured.given().filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(200)).get("/TestNine/" + test06);
		RestAssured.given().filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(200)).get("/TestNine/" + test07);

		// only test spatial search if test05 and test07 have been geocoded successfully
		try (final Tx tx = app.tx()) {

			final TestNine test05Object = app.get(TestNine.class, test05);
			final TestNine test06Object = app.get(TestNine.class, test06);

			assertNotNull(test05Object);
			assertNotNull(test06Object);

			final Double latitude1  = test05Object.getProperty(TestNine.latitude);
			final Double longitude1 = test05Object.getProperty(TestNine.longitude);

			final Double latitude2  = test06Object.getProperty(TestNine.latitude);
			final Double longitude2 = test06Object.getProperty(TestNine.longitude);


			if (latitude1 == null || longitude1 == null || latitude2 == null || longitude2 == null) {

				System.out.println("############################ geocoding failed, skipping assertions");
				return;
			}

			tx.success();

		} catch (FrameworkException fex) {
			fex.printStackTrace();
		}




		// test geocoding, expected result is a list of 3 objects
		// test05, test06 and test07
		RestAssured

			.given()
				.contentType("application/json; charset=UTF-8")

			.expect()
				.statusCode(200)

				.body("result",	              hasSize(3))
				.body("result_count",         equalTo(3))
				.body("result[0].id",         equalTo(test05))
				.body("result[0].postalCode", equalTo("44139"))
				.body("result[0].latitude",   notNullValue())
				.body("result[0].longitude",  notNullValue())

			.when()
				.get(concat("/test_nines"));

		// test geocoding, expected result is a list of 2 objects
		// test01 and test02
		RestAssured

			.given()
				.contentType("application/json; charset=UTF-8")

			.expect()
				.statusCode(200)

				.body("result",	              hasSize(2))
				.body("result_count",         equalTo(2))
				.body("result[0].id",         equalTo(test01))
				.body("result[1].id",         equalTo(test02))

			.when()
				.get(concat("/test_eights?testNinePostalCodes=44139"));

		// test geocoding, expected result is a list of 2 objects
		// test03 and test04.

		// NOTE: This test assumes that the geocoding will NOT find a postal
		// code for the address "Köln, Heumarkt", so this test will fail if
		// the  geocoding information of the given provider changes!
		RestAssured

			.given()
				.contentType("application/json; charset=UTF-8")

			.expect()
				.statusCode(200)

				.body("result",	              hasSize(2))
				.body("result_count",         equalTo(2))
				.body("result[0].id",         equalTo(test03))
				.body("result[1].id",         equalTo(test04))

			.when()
				.get(concat("/test_eights?testNinePostalCodes="));

		// test geocoding, expected result is a list of 2 objects
		// test03 and test04.

		// NOTE: This test is exactly the same as above, but with a list of
		// the "empty" values for testNinePostalCodes
		RestAssured

			.given()
				.contentType("application/json; charset=UTF-8")

			.expect()
				.statusCode(200)

				.body("result",	              hasSize(2))
				.body("result_count",         equalTo(2))
				.body("result[0].id",         equalTo(test03))
				.body("result[1].id",         equalTo(test04))

			.when()
				.get(concat("/test_eights?testNinePostalCodes=,,,"));

		// test spatial search, expected result is a single object:
		// test05
		RestAssured

			.given()
				.contentType("application/json; charset=UTF-8")

			.expect()
				.statusCode(200)

				.body("result",	              hasSize(1))
				.body("result_count",         equalTo(1))
				.body("result[0].id",         equalTo(test05))

			.when()
				.get(concat("/test_nines?distance=2&location=Poststraße,Dortmund"));






		// test spatial search with large radius,
		// expected result is a list of two objects:
		// test05, test06 has no coordinates and should be ignored!
		RestAssured

			.given()
				.contentType("application/json; charset=UTF-8")

			.expect()
				.statusCode(200)

				.body("result",	              hasSize(1))
				.body("result_count",         equalTo(1))
				.body("result[0].id",         equalTo(test05))

			.when()
				.get(concat("/test_nines?distance=100&location=Bahnhofstraße,Wuppertal"));

		// test spatial search in combination with graph-based related node search,
		// expected result is a single result:
		// test05
		RestAssured

			.given()
				.contentType("application/json; charset=UTF-8")

			.expect()
				.statusCode(200)

				.body("result",	              hasSize(1))
				.body("result_count",         equalTo(1))
				.body("result[0].id",         equalTo(test05))

			.when()
				.get(concat("/test_nines?distance=100&location=Bahnhofstraße,Wuppertal&testEightIds=", test01));

		// test spatial search in combination with an empty related node property,
		// expected result is a single result:
		// test06

		// NOTE: This test assumes that the geocoding will NOT find a postal
		// code for the address "Köln, Heumarkt", so this test will fail if
		// the  geocoding information of the given provider changes!
		RestAssured

			.given()
				.contentType("application/json; charset=UTF-8")
				.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(200))

			.expect()
				.statusCode(200)

				.body("result",	              hasSize(1))
				.body("result_count",         equalTo(1))
				//.body("result[0].id",         equalTo(test06))

			.when()
				.get(concat("/test_nines?distance=100&location=Bahnhofstraße,Wuppertal"));
	}

	@Test
	public void testMultiValueSearch() {

		String test01 = createEntity("/test_sixs", "{ aString: string01 }");
		String test02 = createEntity("/test_sixs", "{ aString: string02 }");
		String test03 = createEntity("/test_sixs", "{ aString: string03 }");
		String test04 = createEntity("/test_sixs", "{ aString: string04 }");
		String test05 = createEntity("/test_sixs", "{ aString: string05 }");
		String test06 = createEntity("/test_sixs", "{ aString: string06 }");
		String test07 = createEntity("/test_sixs", "{ aString: string07 }");

		RestAssured

			.given()
				.contentType("application/json; charset=UTF-8")
				.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(200))
				.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(500))

			.expect()
				.statusCode(200)

				.body("result",	              hasSize(3))
				.body("result_count",         equalTo(3))
				.body("result[0].id",         equalTo(test02))
				.body("result[1].id",         equalTo(test04))
				.body("result[2].id",         equalTo(test06))

			.when()
				.get(concat("/test_sixs?aString=string02;string04;string06"));


	}

	@Test
	public void testSearchDynamicNodes() {

		/**
		 * This is actually a core test but has to go here because some
		 * of the includes for dynamic types are only available in rest
		 */

		try {

			SchemaNode node = null;

			// setup
			try (final Tx tx = app.tx()) {

				node = app.create(SchemaNode.class, new NodeAttribute(SchemaNode.name, "TestType"));
				node.setProperty(new StringProperty("_test"), "Integer");

				tx.success();
			}

			// fetch dynamic type info
			final Class dynamicType   = StructrApp.getConfiguration().getNodeEntityClass("TestType");
			final PropertyKey testKey = StructrApp.getConfiguration().getPropertyKeyForJSONName(dynamicType, "test");

			// modify schema node but keep reference to "old" type
			try (final Tx tx = app.tx()) {

				node.setProperty(new StringProperty("_test2"), "String");

				tx.success();
			}

			// create test nodes
			try (final Tx tx = app.tx()) {

				app.create(dynamicType, new NodeAttribute(testKey, 10));
				app.create(dynamicType, new NodeAttribute(testKey, 11));
				app.create(dynamicType, new NodeAttribute(testKey, 12));

				tx.success();
			}

			// query test nodes
			try (final Tx tx = app.tx()) {

				/*
				 * If this test fails, the method "allSubtypes" in SearchCommand was not able to identify
				 * a dynamic type as being assignable to itself. This can happen when the reference to an
				 * existing dynamic type is used after the type has been modified, because the class
				 * instances produced by the dynamic schema ClassLoader are not equal even if they have
				 * the same name and package etc.
				 */

				assertEquals("Query for dynamic node should return exactly one result: ", 1, app.nodeQuery(dynamicType).and(testKey, 10).getAsList().size());

				tx.success();
			}




		} catch (FrameworkException ex) {

			logger.warn("", ex);
			fail("Unexpected exception");

		}
	}

	@Test
	public void testSearchWithOwnerAndEnum() {

		try {

			final List<TestUser> users       = createTestNodes(TestUser.class, 3);
			final List<TestThree> testThrees = new LinkedList<>();
			final Random random              = new Random();
			String uuid                      = null;
			int count                        = 0;

			try (final Tx tx = app.tx()) {

				for (final TestUser user : users) {

					// create 20 entities for every user
					for (int i=0; i<20; i++) {

						testThrees.add(app.create(TestThree.class,
							new NodeAttribute(AbstractNode.name, "test" + count++),
							new NodeAttribute(AbstractNode.owner, user),
							new NodeAttribute(TestThree.enumProperty, TestEnum.values()[random.nextInt(TestEnum.values().length)])
						));
					}
				}

				uuid = users.get(0).getUuid();

				tx.success();
			}

			// test with core API
			try (final Tx tx = app.tx()) {

				for (final TestUser user : users) {

					for (final TestThree test : app.nodeQuery(TestThree.class).and(AbstractNode.owner, user).and(TestThree.enumProperty, TestEnum.Status1).getAsList()) {
						assertEquals("Invalid enum query result", TestEnum.Status1, test.getProperty(TestThree.enumProperty));
					}
				}

				tx.success();
			}

			// test via REST
			RestAssured

				.given()
					.contentType("application/json; charset=UTF-8")
					.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(500))

				.expect()
					.statusCode(200)

				.when()
					.get(concat("/test_threes?sort=createdDate&owner=" + uuid  + "&enumProperty=" + TestEnum.Status1));



		} catch (FrameworkException ex) {

			logger.warn("", ex);
			fail("Unexpected exception");

		}
	}

	@Test
	public void testSearchWithOwnerAndEnumOnDynamicNodes() {

		try {

			// create 3 users
			final String user1 = createEntity("/test_users", "{ name: user1 }");
			final String user2 = createEntity("/test_users", "{ name: user2 }");
			final String user3 = createEntity("/test_users", "{ name: user3 }");

			SchemaNode node = null;

			// setup schema
			try (final Tx tx = app.tx()) {

				node = app.create(SchemaNode.class, new NodeAttribute(SchemaNode.name, "TestType"));
				node.setProperty(new StringProperty("_status"), "Enum(one, two, three)");
				node.setProperty(new StringProperty("___ui"), "status");

				tx.success();
			}

			// create 9 test entities
			final String test1 = createEntity("/test_types", "{ name: test1, owner: " + user1 + ", status: one }");
			final String test2 = createEntity("/test_types", "{ name: test2, owner: " + user2 + ", status: two }");
			final String test3 = createEntity("/test_types", "{ name: test3, owner: " + user3 + ", status: one }");
			final String test4 = createEntity("/test_types", "{ name: test4, owner: " + user1 + ", status: two }");
			final String test5 = createEntity("/test_types", "{ name: test5, owner: " + user2 + ", status: three }");
			final String test6 = createEntity("/test_types", "{ name: test6, owner: " + user3 + ", status: one }");
			final String test7 = createEntity("/test_types", "{ name: test7, owner: " + user1 + ", status: two }");
			final String test8 = createEntity("/test_types", "{ name: test8, owner: " + user2 + ", status: three }");
			final String test9 = createEntity("/test_types", "{ name: test9, owner: " + user3 + ", status: one }");

			// check that all entities are there
			RestAssured

				.given()
					.contentType("application/json; charset=UTF-8")
					.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(201))
					.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(400))
					.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(401))
					.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(403))
					.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(404))
					.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(500))

				.expect()
					.statusCode(200)
					.body("result",	              hasSize(9))
					.body("result_count",         equalTo(9))
					.body("result[0].id",         equalTo(test1))
					.body("result[1].id",         equalTo(test2))
					.body("result[2].id",         equalTo(test3))
					.body("result[3].id",         equalTo(test4))
					.body("result[4].id",         equalTo(test5))
					.body("result[5].id",         equalTo(test6))
					.body("result[6].id",         equalTo(test7))
					.body("result[7].id",         equalTo(test8))
					.body("result[8].id",         equalTo(test9))

				.when()
					.get("/test_types/ui?sort=name");



			// check entities of user1 are there
			RestAssured

				.given()
					.contentType("application/json; charset=UTF-8")
					.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(201))
					.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(400))
					.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(401))
					.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(403))
					.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(404))
					.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(500))

				.expect()
					.statusCode(200)
					.body("result",	              hasSize(3))
					.body("result_count",         equalTo(3))
					.body("result[0].id",         equalTo(test1))
					.body("result[1].id",         equalTo(test4))
					.body("result[2].id",         equalTo(test7))

				.when()
					.get("/test_types/ui?sort=createdDate&owner=" + user1);



			// check entities of user2 are there
			RestAssured

				.given()
					.contentType("application/json; charset=UTF-8")
					.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(201))
					.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(400))
					.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(401))
					.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(403))
					.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(404))
					.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(500))

				.expect()
					.statusCode(200)
					.body("result",	              hasSize(3))
					.body("result_count",         equalTo(3))
					.body("result[0].id",         equalTo(test2))
					.body("result[1].id",         equalTo(test5))
					.body("result[2].id",         equalTo(test8))

				.when()
					.get("/test_types/ui?sort=createdDate&owner=" + user2);



			// check entities of user3 are there
			RestAssured

				.given()
					.contentType("application/json; charset=UTF-8")
					.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(201))
					.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(400))
					.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(401))
					.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(403))
					.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(404))
					.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(500))

				.expect()
					.statusCode(200)
					.body("result",	              hasSize(3))
					.body("result_count",         equalTo(3))
					.body("result[0].id",         equalTo(test3))
					.body("result[1].id",         equalTo(test6))
					.body("result[2].id",         equalTo(test9))

				.when()
					.get("/test_types/ui?sort=createdDate&owner=" + user3);


			// check entities of user1 with a given enum are there
			RestAssured

				.given()
					.contentType("application/json; charset=UTF-8")
					.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(201))
					.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(400))
					.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(401))
					.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(403))
					.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(404))
					.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(500))

				.expect()
					.statusCode(200)
					.body("result",	              hasSize(1))
					.body("result_count",         equalTo(1))
					.body("result[0].id",         equalTo(test1))

				.when()
					.get("/test_types/ui?sort=createdDate&owner=" + user1 + "&status=one");


			// check entities of user1 with a given enum are there
			RestAssured

				.given()
					.contentType("application/json; charset=UTF-8")
					.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(201))
					.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(400))
					.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(401))
					.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(403))
					.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(404))
					.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(500))

				.expect()
					.statusCode(200)
					.body("result",	              hasSize(2))
					.body("result_count",         equalTo(2))
					.body("result[0].id",         equalTo(test4))
					.body("result[1].id",         equalTo(test7))

				.when()
					.get("/test_types/ui?sort=createdDate&owner=" + user1 + "&status=two");


			// check entities of user1 with a given enum are there
			RestAssured

				.given()
					.contentType("application/json; charset=UTF-8")
					.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(201))
					.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(400))
					.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(401))
					.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(403))
					.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(404))
					.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(500))

				.expect()
					.statusCode(200)
					.body("result",	              hasSize(2))
					.body("result_count",         equalTo(2))
					.body("result[0].id",         equalTo(test5))
					.body("result[1].id",         equalTo(test8))

				.when()
					.get("/test_types/ui?sort=createdDate&owner=" + user2 + "&status=three");





		} catch (FrameworkException ex) {

			logger.warn("", ex);
			fail("Unexpected exception");

		}
	}

	@Test
	public void testStaticRelationshipResourceFilter() {

		String test01 = createEntity("/test_sixs", "{ name: test01, aString: string01, anInt: 1 }");
		String test02 = createEntity("/test_sixs", "{ name: test02, aString: string02, anInt: 2 }");
		String test03 = createEntity("/test_sixs", "{ name: test03, aString: string03, anInt: 3 }");
		String test04 = createEntity("/test_sixs", "{ name: test04, aString: string04, anInt: 4 }");
		String test05 = createEntity("/test_sixs", "{ name: test05, aString: string05, anInt: 5 }");
		String test06 = createEntity("/test_sixs", "{ name: test06, aString: string06, anInt: 6 }");
		String test07 = createEntity("/test_sixs", "{ name: test07, aString: string07, anInt: 7 }");
		String test08 = createEntity("/test_sixs", "{ name: test08, aString: string08, anInt: 8 }");

		String test09 = createEntity("/test_sevens", "{ name: test09, testSixIds: [", test01, ",", test02, "], aString: string09, anInt: 9 }");
		String test10 = createEntity("/test_sevens", "{ name: test10, testSixIds: [", test03, ",", test04, "], aString: string10, anInt: 10 }");
		String test11 = createEntity("/test_sevens", "{ name: test11, testSixIds: [", test05, ",", test06, "], aString: string11, anInt: 11 }");
		String test12 = createEntity("/test_sevens", "{ name: test12, testSixIds: [", test07, ",", test08, "], aString: string12, anInt: 12 }");

		String test13 = createEntity("/test_eights", "{ name: test13, testSixIds: [", test01, ",", test02, "], aString: string13, anInt: 13 }");
		String test14 = createEntity("/test_eights", "{ name: test14, testSixIds: [", test02, ",", test03, "], aString: string14, anInt: 14 }");
		String test15 = createEntity("/test_eights", "{ name: test15, testSixIds: [", test03, ",", test04, "], aString: string15, anInt: 15 }");
		String test16 = createEntity("/test_eights", "{ name: test16, testSixIds: [", test04, ",", test05, "], aString: string16, anInt: 16 }");
		String test17 = createEntity("/test_eights", "{ name: test17, testSixIds: [", test05, ",", test06, "], aString: string17, anInt: 17 }");
		String test18 = createEntity("/test_eights", "{ name: test18, testSixIds: [", test06, ",", test07, "], aString: string18, anInt: 18 }");
		String test19 = createEntity("/test_eights", "{ name: test19, testSixIds: [", test07, ",", test08, "], aString: string19, anInt: 19 }");
		String test20 = createEntity("/test_eights", "{ name: test20, testSixIds: [", test08, ",", test01, "], aString: string20, anInt: 20 }");

		// test simple related node search
		RestAssured

			.given()
				.contentType("application/json; charset=UTF-8")
				.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(200))
				.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(201))
				.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(400))
				.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(404))
				.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(422))
				.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(500))

			.expect()
				.statusCode(200)

				.body("result",	      hasSize(1))
				.body("result_count", equalTo(1))
				.body("result[0].id", equalTo(test13))

			.when()
				.get(concat("/test_sixs/", test01, "/testEights?aString=string13&anInt=13"));

		// test simple related node search with range query
		RestAssured

			.given()
				.contentType("application/json; charset=UTF-8")
				.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(200))
				.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(201))
				.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(400))
				.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(404))
				.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(422))
				.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(500))

			.expect()
				.statusCode(200)

				.body("result",	      hasSize(2))
				.body("result_count", equalTo(2))
				.body("result[0].id", equalTo(test14))
				.body("result[1].id", equalTo(test15))

			.when()
				.get(concat("/test_sixs/", test03, "/testEights?anInt=[14 TO 18]"));
	}

	/**
	 * Tests {@link TypeAndPropertySetDeserializationStrategy} for ambiguity avoidance.
	 *
	 * Before fixing a bug in {@link TypeAndPropertySetDeserializationStrategy}, the creation
	 * of test03 was not possible because the a search was conducted internally with the values
	 * given in the 'testSeven' object, and the result was ambiguous.
	 */
	@Test
	public void testAmbiguity() {

		// Create a TestTen with a TestSeven on the fly with {'aString':'test'}
		createEntity("/test_tens", "{ 'name': 'test01', 'testSeven':{ 'aString' : 'test' } }");

		// Create another TestSeven with {'aString':'test'}
		createEntity("/test_sevens", "{ 'aString': 'test' }");

		// Create another TestTen with another TestSeven on the fly
		createEntity("/test_tens", "{ 'name': 'test02', 'testSeven':{ 'aString' : 'test' } }");
	}

	@Test
	public void testPropertyViewsAndResultSetLayout() {

		String resource = "/test_twos";

		// create entity
		final String uuid = getUuidFromLocation(RestAssured

			.given()
				.contentType("application/json; charset=UTF-8")
				.header("Accept", "application/json; charset=UTF-8")
				.body(" { 'name' : 'TestTwo-0', 'anInt' : 0, 'aLong' : 0, 'aDate' : '2012-09-18T00:33:12+0200' } ")

			.expect()
				.statusCode(201)

			.when()
				.post(resource).getHeader("Location")
		);

		// test default view with properties in it
		RestAssured

			.given()
				.contentType("application/json; charset=UTF-8")
				.header("Accept", "application/json; charset=UTF-8")
				.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(200))

			.expect()
				.statusCode(200)
				.body("query_time",                 notNullValue())
				.body("serialization_time",         notNullValue())
				.body("result_count",               equalTo(1))
				.body("result",                     hasSize(1))

				.body("result[0]",                  isEntity(TestTwo.class))

				.body("result[0].id",               equalTo(uuid))
				.body("result[0].type",	            equalTo(TestTwo.class.getSimpleName()))
				.body("result[0].name",             equalTo("TestTwo-0"))
				.body("result[0].anInt",            equalTo(0))
				.body("result[0].aLong",            equalTo(0))
				.body("result[0].aDate",            equalTo("2012-09-17T22:33:12+0000"))

			.when()
				.get(resource);


		// test all view with properties in it
		RestAssured

			.given()
				.contentType("application/json; charset=UTF-8")
				.header("Accept", "application/json; charset=UTF-8")
				.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(200))

			.expect()
				.statusCode(200)
				.body("query_time",                            notNullValue())
				.body("serialization_time",                    notNullValue())
				.body("result_count",                          equalTo(1))
				.body("result",                                hasSize(1))

				.body("result[0]",                             isEntity(TestTwo.class))

				.body("result[0].id",                          equalTo(uuid))
				.body("result[0].type",	                       equalTo(TestTwo.class.getSimpleName()))
				.body("result[0].name",                        equalTo("TestTwo-0"))
				.body("result[0].anInt",                       equalTo(0))
				.body("result[0].aLong",                       equalTo(0))
				.body("result[0].aDate",                       equalTo("2012-09-17T22:33:12+0000"))
				.body("result[0].test_ones",                   notNullValue())
				.body("result[0].base",                        nullValue())
				.body("result[0].createdDate",                 notNullValue())
				.body("result[0].lastModifiedDate",            notNullValue())
				.body("result[0].visibleToPublicUsers",        equalTo(false))
				.body("result[0].visibleToAuthenticatedUsers", equalTo(false))
				.body("result[0].visibilityStartDate",         nullValue())
				.body("result[0].visibilityEndDate",           nullValue())
				.body("result[0].createdBy",                   nullValue())
				.body("result[0].deleted",                     equalTo(false))
				.body("result[0].hidden",                      equalTo(false))
				.body("result[0].owner",                       nullValue())
				.body("result[0].ownerId",                     nullValue())

			.when()
				.get(concat(resource, "/all"));
	}

	@Test
	public void testOutputNestingDepth() {

		String test01 = createEntity("/test_ones", "{ name: test01, anInt: 1 }");
		String test02 = createEntity("/test_ones", "{ name: test02, anInt: 2 }");
		String test03 = createEntity("/test_ones", "{ name: test03, anInt: 3 }");
		String test04 = createEntity("/test_ones", "{ name: test04, anInt: 4 }");
		String test05 = createEntity("/test_ones", "{ name: test05, anInt: 5 }");
		String test06 = createEntity("/test_ones", "{ name: test06, anInt: 6 }");
		String test07 = createEntity("/test_ones", "{ name: test07, anInt: 7 }");
		String test08 = createEntity("/test_ones", "{ name: test08, anInt: 8 }");

		String test09 = createEntity("/test_twos", "{ name: test09, testOnes: [", test01, ",", test02, "], anInt: 9 }");
		String test10 = createEntity("/test_twos", "{ name: test10, testOnes: [", test03, ",", test04, "], anInt: 10 }");
		String test11 = createEntity("/test_twos", "{ name: test11, testOnes: [", test05, ",", test06, "], anInt: 11 }");
		String test12 = createEntity("/test_twos", "{ name: test12, testOnes: [", test07, ",", test08, "], anInt: 12 }");

		String test13 = createEntity("/test_elevens", "{ name: test13, testTwos: [", test09, ",", test10, "]}");
		String test14 = createEntity("/test_elevens", "{ name: test14, testTwos: [", test11, ",", test12, "]}");

		// test depth 0 wich should result only in the 2 TestEleven objects
		String url = "/test_elevens?outputNestingDepth=0";
		RestAssured

				.given()
				.contentType("application/json; charset=UTF-8")
				.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(200))
				.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(201))
				.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(400))
				.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(404))
				.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(422))
				.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(500))

				.expect()
				.statusCode(200)

				.body("output_nesting_depth", equalTo(0))
				.body("result", hasSize(2))
				.body("result_count", equalTo(2))
				.body("result[0].testTwos", hasSize(0))

				.when()
				.get(url);

		// test depth 1 wich should result in a list with 2 TestEleven objects with each two TestTwo objects
		url = "/test_elevens?outputNestingDepth=1";
		RestAssured

				.given()
				.contentType("application/json; charset=UTF-8")
				.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(200))
				.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(201))
				.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(400))
				.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(404))
				.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(422))
				.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(500))

				.expect()
				.statusCode(200)

				.body("output_nesting_depth", equalTo(1))
				.body("result", hasSize(2))
				.body("result_count", equalTo(2))
				.body("result[0].testTwos", hasSize(2))
				.body("result[0].testTwos[0].testOnes", hasSize(0))

				.when()
				.get(url);

		// test depth 2 wich should result in a list with 2 TestEleven objects with each two TestTwo objects with each two TestOne objects
		url = "/test_elevens?outputNestingDepth=2";
		RestAssured

				.given()
				.contentType("application/json; charset=UTF-8")
				.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(200))
				.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(201))
				.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(400))
				.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(404))
				.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(422))
				.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(500))

				.expect()
				.statusCode(200)

				.body("output_nesting_depth", equalTo(2))
				.body("result", hasSize(2))
				.body("result_count", equalTo(2))
				.body("result[0].testTwos", hasSize(2))
				.body("result[0].testTwos[0].testOnes", hasSize(2))

				.when()
				.get(url);

		// test default depth value
		url = "/test_elevens";
		RestAssured

				.given()
				.contentType("application/json; charset=UTF-8")
				.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(200))
				.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(201))
				.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(400))
				.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(404))
				.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(422))
				.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(500))

				.expect()
				.statusCode(200)

				.body("output_nesting_depth", nullValue())
				.body("result", hasSize(2))
				.body("result_count", equalTo(2))

				.when()
				.get(url);


	}

	@Test
	public void testSpatialSearchWithoutGeocoding() {

		// center of Germany is 51.163375; 10.447683
		// test: 2.38km north: 51.183727, 10.460942

		createEntity("/TestNine", "{ name: 'Mittelpunktstein', latitude: 51.163375, longitude: 10.447683 }");

		// test distance of 1km => no result
		RestAssured.given().contentType("application/json; charset=UTF-8").expect()
			.statusCode(200)
			.body("result",	              hasSize(0))
			.body("result_count",         equalTo(0))
			.when().get(concat("/TestNine?distance=1&latlon=51.183727,10.460942"));

		// test distance of 2km => no result
		RestAssured.given().contentType("application/json; charset=UTF-8").expect()
			.statusCode(200)
			.body("result",	              hasSize(0))
			.body("result_count",         equalTo(0))
			.when().get(concat("/TestNine?distance=2&latlon=51.183727,10.460942"));

		// test distance of 3km => 1 result
		RestAssured.given().contentType("application/json; charset=UTF-8").expect()
			.statusCode(200)
			.body("result",	              hasSize(1))
			.body("result_count",         equalTo(1))
			.when().get(concat("/TestNine?distance=3&latlon=51.183727,10.460942"));


	}
}
