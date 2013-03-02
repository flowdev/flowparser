package org.flowdev.flowparser.mustache;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

public class MustacheTest {

    public List<Item> items;

    @Before
    public void setUp() throws Exception {
	items = Arrays.asList(new Item(), new Item());
	Item it = items.get(0);
	it.name = "Item 1";
	it.price = "$19.99";
	it.features = Arrays.asList(new Feature(), new Feature());
	Feature feat = it.features.get(0);
	feat.description = "New!";
	feat = it.features.get(1);
	feat.description = "Awesome!";
	it = items.get(1);
	it.name = "Item 2";
	it.price = "$29.99";
	it.features = Arrays.asList(new Feature(), new Feature());
	feat = it.features.get(0);
	feat.description = "Old.";
	feat = it.features.get(1);
	feat.description = "Ugly.";
    }

    @Test
    @Ignore
    public void test() throws IOException {
	MustacheFactory mf = new DefaultMustacheFactory(
		"org/flowdev/flowparser/mustache");
	Mustache mustache = mf.compile("template.mustache");
	mustache.execute(new PrintWriter(System.out), this).flush();
	fail("Not yet implemented");
    }

    @SuppressWarnings("unused")
    private static class Item {
	public String name, price;
	public List<Feature> features;
    }

    @SuppressWarnings("unused")
    private static class Feature {
	public String description;
    }

}
