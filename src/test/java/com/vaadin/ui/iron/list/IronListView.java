/*
 * Copyright 2000-2017 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.ui.iron.list;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.github.javafaker.Faker;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.Query;
import com.vaadin.flow.demo.DemoView;
import com.vaadin.function.ValueProvider;
import com.vaadin.router.Route;
import com.vaadin.router.RouteAlias;
import com.vaadin.ui.html.Label;
import com.vaadin.ui.iron.list.model.Person;
import com.vaadin.ui.renderers.TemplateRenderer;

@Route("")
@RouteAlias("iron-list")
public class IronListView extends DemoView {

    private static final List<String> LIST_OF_BOOKS;
    static {
        Faker faker = Faker.instance(new Random(42));
        LIST_OF_BOOKS = createListOfStrings(1000, () -> faker.book().title());
    }

    @Override
    protected void initView() {
        createStringList();
        createStringListWithDataProvider();
        createPeopleListWithDataProvider();
        createChuckNorrisFacts();
    }

    private void createStringList() {
        // begin-source-example
        // source-example-heading: List of strings
        IronList<String> list = new IronList<>();
        list.setHeight("100px");
        list.getStyle().set("border", "1px solid lightgray");

        List<String> items = Arrays.asList("Item 1", "Item 2", "Item 3");
        list.setItems(items);
        // end-source-example

        addCard("List of strings", list);
    }

    private void createStringListWithDataProvider() {
        // begin-source-example
        // source-example-heading: List of strings with DataProvider
        IronList<String> list = new IronList<>();
        list.setHeight("200px");
        list.getStyle().set("border", "1px solid lightgray");

        DataProvider<String, ?> dataProvider = DataProvider.fromCallbacks(
                query -> queryStringsFromDatabase(query),
                query -> countStringsFromDatabase(query));

        list.setDataProvider(dataProvider);
        // end-source-example

        addCard("List of strings with DataProvider",
                new Label("List of books lazy loaded from the database"), list);
    }

    private void createChuckNorrisFacts() {
        // begin-source-example
        // source-example-heading: List of random Chuck Norris facts
        IronList<String> list = new IronList<>();
        list.setHeight("400px");

        DataProvider<String, ?> dataProvider = DataProvider.fromCallbacks(
                query -> createFacts(query.getLimit()), query -> 1000);

        list.setDataProvider(dataProvider);
        list.setRenderer(TemplateRenderer.<String> of(
                "<div style='font-size:20px; margin:10px; padding:10px; "
                        + "border:1px solid lightgray; border-radius:5px;'>"
                        + "#[[index]]. [[item.fact]]</div>")
                .withProperty("fact", ValueProvider.identity()));
        // end-source-example

        addCard("Using templates", "List of random Chuck Norris facts", list);
    }

    private void createPeopleListWithDataProvider() {
        //@formatter:off
        // begin-source-example
        // source-example-heading: List of people with DataProvider
        IronList<Person> list = new IronList<>();
        list.setHeight("400px");
        list.getStyle().set("border", "1px solid lightgray");

        DataProvider<Person, ?> dataProvider = DataProvider
                .ofCollection(createListOfPeople());

        list.setDataProvider(dataProvider);
        list.setGridLayout(true);
        list.setRenderer(TemplateRenderer
                .<Person> of("<div style='padding:10px; display:flex; min-width:250px'>"
                                + "<div style='margin-right:10px; width:40px; height:40px'>"
                                    + "<img src='[[item.picture]]' style='border-radius:50%; width:40px; height:40px'/>"
                                + "</div>"
                                + "<div>"
                                    + "[[item.firstName]] [[item.lastName]]"
                                    + "<br><small>[[item.email]]</small>"
                                + "</div>"
                        + "</div>")
                .withProperty("firstName", Person::getFirstName)
                .withProperty("lastName", Person::getLastName)
                .withProperty("email", Person::getEmail)
                .withProperty("picture", Person::getPicture));
        // end-source-example
        //@formatter:on

        addCard("Using templates", "List of people with DataProvider",
                new Label("List of people with grid layout"), list);
    }

    private Stream<String> queryStringsFromDatabase(Query<String, Void> query) {
        return LIST_OF_BOOKS
                .subList(Math.min(query.getOffset(), LIST_OF_BOOKS.size() - 1),
                        Math.min(query.getOffset() + query.getLimit(),
                                LIST_OF_BOOKS.size()))
                .stream();
    }

    private int countStringsFromDatabase(Query<String, Void> query) {
        return LIST_OF_BOOKS.size();
    }

    private static Stream<String> createFacts(int number) {
        number = Math.min(number, 1000);
        List<String> list = new ArrayList<>(number);
        Faker faker = Faker.instance();
        for (int i = 0; i < number; i++) {
            list.add(faker.chuckNorris().fact());
        }
        return list.stream();
    }

    private static List<String> createListOfStrings(int number,
            Supplier<String> supplier) {
        List<String> list = new ArrayList<>(number);
        for (int i = 0; i < number; i++) {
            list.add(supplier.get());
        }
        return list;
    }

    private List<Person> createListOfPeople() {
        List<Person> people = new ArrayList<>(50);
        Faker faker = Faker.instance(new Random(42));
        for (int i = 0; i < 50; i++) {
            Person person = new Person();
            person.setFirstName(faker.name().firstName());
            person.setLastName(faker.name().lastName());
            person.setEmail(faker.internet().safeEmailAddress());
            person.setPicture(faker.internet().avatar());
            people.add(person);
        }

        return people;
    }
}