package com.example.searchengine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;


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
		String template = Files.readString(Path.of("./src/main/resources/static/index.html"));

		if(!urls.isEmpty()) {
			// Create a StringBuilder to build the search results as HTML
			StringBuilder resultHtml = new StringBuilder("<div class=\"result-container\"><ul>");
			for (String url : urls) {
				// Format each search result as a list item
				resultHtml.append("<li><a href=\"").append(url).append("\">").append(url).append("</a></li>");
			}

			// Close the unordered list
			resultHtml.append("</ul></div>");

			// Replace the "${results}" placeholder with the dynamically generated search results
			return template.replace("${results}", resultHtml.toString());
		}else {
			return template.replace("${results}", "<div class=\"result-container\">No results Found</div>");
		}
    }
	@GetMapping("/lucky")
	public ResponseEntity<?> lucky(@RequestParam String keyword, @RequestHeader("Accept") String acceptHeader) {
		if (keyword == null || keyword.isEmpty()) {
			// If the input is invalid, return a 400 Bad Request response
			return ResponseEntity.badRequest().body("Missing the query string parameter.");
		}

		List<String> urls = searcher.search(keyword, flippedIndexFileName);
		if (!urls.isEmpty()) {
			if (acceptHeader.contains("application/json")) {
				return ResponseEntity.ok(urls.get(0));
			} else {
				HttpHeaders headers = new HttpHeaders();

				headers.setLocation(URI.create(urls.get(0)));
				return new ResponseEntity<>(headers, HttpStatus.FOUND);
			}
		} else {
			return ResponseEntity.notFound().build();
		}
	}
	@GetMapping("/no-results")
	public String noresult() throws IOException {
		String template = Files.readString(Path.of("./src/main/resources/static/index.html"));

		// Replace the "${results}" placeholder with the dynamically generated search results
		return template.replace("${results}", "<div class=\"result-container\">No results Found</div>");
	}
}
