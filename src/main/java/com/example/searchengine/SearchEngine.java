package com.example.searchengine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@RestController
public class SearchEngine {

	public final String indexFileName = "./src/main/resources/index.csv";

	public final String flippedIndexFileName = "./src/main/resources/index_flipped.csv";

	public final String startUrl = "https://api.interactions.ics.unisg.ch/hypermedia-environment/cc2247b79ac48af0";

	@Autowired
	Searcher searcher;

	@Autowired
	IndexFlipper indexFlipper;

	@Autowired
	SearchEngineProperties properties;

	Crawler crawler;

	@PostConstruct
	public void initialize(){
		if (properties.getCrawler().equals("multithread")){
			this.crawler = new MultithreadCrawler(indexFileName);
		} else {
			this.crawler = new SimpleCrawler(indexFileName);
		}
		if (properties.getCrawl()) {
			crawler.crawl(startUrl);
			indexFlipper.flipIndex(indexFileName, flippedIndexFileName);
		}
	}
	@GetMapping("/")
	public String index() {
		try {
			String html = Files.readString(Path.of("./src/main/resources/static/index.html"));
			return html.replace("${results}", "");
		} catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
	@GetMapping("/search")
	public String search(@RequestParam String keyword) throws IOException {
		List<String> urls = searcher.search(keyword, flippedIndexFileName);

		// Create a StringBuilder to build the search results as HTML
		StringBuilder resultHtml = new StringBuilder("<div class=\"result-container\"><ul>");

		for (String url : urls) {
			// Format each search result as a list item
			resultHtml.append("<li><a href=\"").append(url).append("\">").append(url).append("</a></li>");
		}

		// Close the unordered list
		resultHtml.append("</ul></div>");


		// Read the HTML template
		String template = Files.readString(Path.of("./src/main/resources/static/index.html"));

		// Replace the "${results}" placeholder with the dynamically generated search results
        return template.replace("${results}", resultHtml.toString());

    }
	@GetMapping("/lucky")
	public String lucky(@RequestParam String keyword) {
			List<String> urls = searcher.search(keyword, flippedIndexFileName);
			if (!urls.isEmpty()) {
				return urls.get(0);
			} else {
				return "No results found";
			}
	}


}
